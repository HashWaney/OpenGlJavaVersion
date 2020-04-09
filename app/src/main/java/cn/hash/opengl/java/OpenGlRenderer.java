package cn.hash.opengl.java;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import cn.hash.opengl.java.util.ShaderHelper;
import cn.hash.opengl.java.util.TextResourceReader;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Hash on 2020-04-08.
 */


public class OpenGlRenderer implements GLSurfaceView.Renderer {


    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int POINT_SIZE = 4;

    //to use the fragment_shade define varying named u_Color
    private static final String U_COLOR = "u_Color";
    //define varying to accept the value
    private int uColorLocation;

    private static final String V_POSITION = "v_Position";
    private int vPosition;


    private Context context;
    //分配native层的内存空间，将Java层的顶点数据传递给OpenGL floatBuffer
    private FloatBuffer vertexData;


    //TODO 只能看到桌子的一个角 为什么呢，OpenGL的坐标范围只在[-1,1] 区间 ，需要将定义的坐标映射到屏幕上的实际物理坐标
//    float[] tableVerticesWithTriangle = {
//            //triangle1
//            0f, 0f,
//            9f, 14f,
//            0f, 14f,
//
//            //triangle2
//            0f, 0f,
//            9f, 0f,
//            9f, 14f,
//
//            //line1
//            0f, 7f,
//            9f, 7f,
//
//            //mallets
//            4.5f, 2f,
//            4.5f, 12f
//
//
//    };


    float[] tableVerticesWithTriangle = {
            //Triangle1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,

            //Triangle2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,


            //line1
            -0.5f, 0f,
            0.5f, 0,

            //mallets
            0f, -0.25f,
            0, 0.24f


    };


    public OpenGlRenderer(Context context) {
        this.context = context;
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangle.length * POINT_SIZE)
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
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        //获取属性的位置：用来告诉GPU分配位置
        vPosition = glGetAttribLocation(program, V_POSITION);
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
        glVertexAttribPointer(vPosition, POSITION_COMPONENT_COUNT, GL_FLOAT, true, 0, vertexData);

        //使得OpenGL去vPosition去寻找数据
        glEnableVertexAttribArray(vPosition);


    }

    // when surface change size , vertical or horizontal switch surface size
    // change so this function invoke
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //set the opengl viewport to fill the entire surface
        glViewport(0, 0, width, height);
        Log.i("Hash", "width: " + width + " height:" + height);


    }

    //when draw per frame this function invoke,
    //also we should draw some thing, even if clear screen
    @Override
    public void onDrawFrame(GL10 gl) {
        //clear the  rendering surface  and invoke the glClearColor to fill the screen
        glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //更新着色器u_Color的值
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        //告诉OpenGl要绘制一个矩形，0代表第一个位置，6表示6个绘制的坐标点，绘制属性为三角形，两个三角形拼接为矩形
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 0.0f);
        glDrawArrays(GL_POINTS, 8, 1);


        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);


    }
}
