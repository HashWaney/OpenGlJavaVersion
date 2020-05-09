package cn.hash.opengl.java;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.hash.opengl.java.util.MatrixHelper;
import cn.hash.opengl.java.util.ShaderHelper;
import cn.hash.opengl.java.util.TextResourceReader;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import android.opengl.Matrix;

/**
 * Created by Hash on 2020-04-08.
 */


public class OpenGlRenderer implements GLSurfaceView.Renderer {


    //点的组合（a,b)
    private static final int POSITION_COMPONENT_COUNT = 2;
    //每个点的占用字节数
    private static final int BYTES_PER_FLOAT = 4;

    //颜色的组合（rgb）
    private static final int COLOR_COMPONENT_COUNT = 3;

    //跨度 就是隔多少个点访问下一个点
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    // color 使用attribute 形式进行颜色属性的数据更新， 而不是采用unifrom形式只能单一的绘制一种确定的颜色。
    private static final String V_COLOR = "a_Color";
    private int vColor;


    private static final String V_POSITION = "v_Position";
    private int vPosition;

    //定义一个矩阵，该矩阵会把虚拟坐标空间变换回归一化设备坐标
    private static final String U_MATRIX = "u_Matrix";
    //定义顶底数组存储矩阵(投影矩阵)
    private final float[] projectMatrix = new float[16];
    private int uMatrixLocation;

    //模型矩阵
    private final float[] modelMatrix = new float[16];


    private Context context;
    //分配native层的内存空间，将Java层的顶点数据传递给OpenGL floatBuffer
    private FloatBuffer vertexData;


    //TODO 只能看到桌子的一个角 为什么呢，OpenGL的坐标范围只在[-1,1] 区间 ，需要将定义的坐标映射到屏幕上的实际物理坐标

    float[] tableVerticesWithTriangle = {
            //Triangle1  X,Y,R,G,B
            0f, 0f, 1f, 1f, 1f,
            0.7f, 0.7f, 0.7f, 0.7f, 0.7f,
            -0.7f, 0.7f, 0.7f, 0.7f, 0.7f,

            //Triangle2
            -0.7f, -0.7f, 0.7f, 0.7f, 0.7f,
            0.7f, -0.7f, 0.7f, 0.7f, 0.7f,
            0.7f, 0.7f, 0.7f, 0.7f, 0.7f,


            //line1
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0, 0f, 1f, 0f,

            //mallets
            0f, -0.25f, 0f, 0f, 1f,
            0, 0.25f, 1f, 0f, 0f


    };


    //TODO 展示的是一个立体效果，效果为站在桌子的一侧水平观察对面，立体感，
    // 加入了Z分量和W分量，Z分量设置为0f，W分量远的设置大一些，近的设置小点，呈现一种立体效果。
    // OpenGL会自动使用我们的w值做透视除法。 但是这是一种硬编码方式，如果想让这些物体更动态，
    // 就需要用到矩阵来生成这些值， 比如改变桌子的角度，缩放，
//    float[] tableVerticesWithTriangle = {
//            //Triangle1  X,Y,Z,W,R,G,B
//            0f, 0f, 0f, 1.5f,        1f, 1f, 1f,
//            0.7f, 0.7f, 0f, 2f,      0.7f, 0.7f, 0.7f,
//            -0.7f, 0.7f, 0f, 2f,     0.7f, 0.7f, 0.7f,
//
//            -0.7f, -0.7f, 0f, 1f,    0.7f, 0.7f, 0.7f,
//            0.7f, -0.7f, 0f, 1f,     0.7f, 0.7f, 0.7f,
//            0.7f, 0.7f, 0f, 2f,      0.7f, 0.7f, 0.7f,
//
//
//            //line1
//            -0.5f, 0f,0f,1.5f,       1f, 0f, 0f,
//            0.5f, 0f, 0f,1.5f,       0f, 1f, 0f,
//
//            //mallets
//            0f, -0.25f,0f,1.25f,     0f, 0f, 1f,
//            0, 0.25f,0f,1.25f,       1f, 0f, 0f
//
//
//    };

    public OpenGlRenderer(Context context) {
        this.context = context;
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangle.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangle);
    }

    //when surface create invoke this function, maybe device wakeup or
    // switch back form another activity this function also be invoked.
    // so this function can invoke at least once
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        glClearColor(0.0f, 0f, 0f, 0f);

        String fragment_shader_source_code = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragmet_shader);
        String vertex_shader_source_code = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);

        int vertexShaderId = ShaderHelper.compileVertexShader(vertex_shader_source_code);
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragment_shader_source_code);

        int program = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);
        Log.w(ShaderHelper.TAG, "program Id:" + program);
        if (!ShaderHelper.validateProgram(program)) {
            Log.w(ShaderHelper.TAG, "TODO");
            return;
        }
        glUseProgram(program);

        //获取一个uniform的位置：用来告诉GPU在绘制的时候设置颜色
//        uColorLocation = glGetUniformLocation(program, U_COLOR);
        //获取属性的位置：用来告诉GPU分配位置
        vPosition = glGetAttribLocation(program, V_POSITION);
        vColor = glGetAttribLocation(program, V_COLOR);

        //正交矩阵
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);


        //将指针移动到缓冲区的第一个元素
        vertexData.position(0);
        /**
         *         int indx,属性位置
         *         int size,属性的数据的计数，比如数组中以两个点作为一个坐标点，因此二维坐标 传2 2个点组合就 代表一个坐标点
         *         int type,该点是数据类型，因为定义的是float 因此占4个字节
         *         boolean normalized,是否进行归一化
         *         int stride, 是指数组存储了多于一个属性比如我们定义了v_Position 可能还有a_Position
         *         java.nio.Buffer ptr；告诉OpenGL去哪读取数据，我们定义了FloatBuffer 开辟了一块缓冲区，数据就会被put到这里，因此opengl直接操作本地内存，读取数据
         */

        //告诉OpenGL去哪读取数据，读的数据包括大小，多少个为一组。
        glVertexAttribPointer(vPosition, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        //使得OpenGL去vPosition去寻找数据
        glEnableVertexAttribArray(vPosition);

        //将指针指向颜色属性的位置
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(vColor, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(vColor);


    }

    // when surface change size , vertical or horizontal switch surface size
    // change so this function invoke
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //set the opengl viewport to fill the entire surface
        Log.i("Hash", "width: " + width + " height:" + height);
        glViewport(0, 0, width, height);

        // TODO: 2020-04-09 旋转屏幕导致宽高比变化，因此需要做适配
//        final float aspectRatio = width > height ?
//                (float) width / (float) height :
//                (float) height / (float) width;
//
//        if (width > height) { //横屏情况下，扩展宽度的坐标范围，范围由[-1,1] -->
//            // [-aspectRatio,aspectRatio] 高度保持[-1,1]
//            Matrix.orthoM(projectMatrix, 0, -aspectRatio, aspectRatio,
//                    -1f, 1f, -1f, 1f);
//
//        } else {
//            Matrix.orthoM(projectMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio,
//                    -1f, 1f);
//
//        }

        //TODO 桌子不见了，没有给桌子指定z的位置，默认情况下处于z为0 的位置，因为视锥体是从z值为-1的位置开始
        //除非把它移动到那个距离内。
        //因此在使用投影矩阵进行投影之前，使用一个平移矩阵把桌子移出来，
        MatrixHelper.perspectiveM(projectMatrix, 45,
                (float) width / (float) height,
                1f, 10f);

        setIdentityM(modelMatrix, 0);
        //沿着z轴方向平移-2
        translateM(modelMatrix, 0, 0f, 0f, -2f);
        rotateM(modelMatrix,0,-60f,1,0,0);
        //需要将投影矩阵与平移矩阵相乘，这样坐标最终才会沿着z轴方向移动2个单位。
        final float[] temp = new float[16];
        //把投影矩阵和模型矩阵相乘，结果存储在temp中
        multiplyMM(temp, 0, projectMatrix, 0, modelMatrix, 0);
        //利用System.arrayCopy()把结果存回projectMatrix 包含了模型矩阵与投影矩阵的组合效应
        System.arraycopy(temp, 0, projectMatrix, 0, temp.length);




    }

    //when draw per frame this function invoke,
    //also we should draw some thing, even if clear screen
    @Override
    public void onDrawFrame(GL10 gl) {
        //clear the  rendering surface  and invoke the glClearColor to fill the screen
        glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //TODO 传递矩阵给着色器，
        glUniformMatrix4fv(uMatrixLocation, 1,
                false, projectMatrix, 0);

        //绘制矩形
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        //绘制线
        glDrawArrays(GL_LINES, 6, 2);
        //绘制点
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);


    }
}
