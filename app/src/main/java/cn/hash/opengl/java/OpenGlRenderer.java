package cn.hash.opengl.java;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.hash.opengl.java.objects.Mallet;
import cn.hash.opengl.java.objects.Puck;
import cn.hash.opengl.java.objects.Table;
import cn.hash.opengl.java.program.ColorShaderProgram;
import cn.hash.opengl.java.program.TextureShaderProgram;
import cn.hash.opengl.java.util.MatrixHelper;
import cn.hash.opengl.java.util.TextureHelper;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by Hash on 2020-04-08.
 */


public class OpenGlRenderer implements GLSurfaceView.Renderer {

    //投影矩阵
    private final float[] projectMatrix = new float[16];

    //视图矩阵
    private final float[] viewMatrix = new float[16];

    //存储的是视图矩阵和投影矩阵的组合之后的结果
    private final float[] viewProjectMatrix = new float[16];

    //模型矩阵
    private final float[] modelMatrix = new float[16];

    //存储的是模型矩阵与视图矩阵投影矩阵组合的组合结果。 因此最后我们操作的是 modelMatrix*(viewMatrix*projectMatrix) 这个就是我们最后展示的空间示意图。
    private final float[] modelViewMatrix = new float[16];

    private Table table;

    private Puck puck;

    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;


    private Context context;


    public OpenGlRenderer(Context context) {
        this.context = context;

    }

    //when surface create invoke this function, maybe device wakeup or
    // switch back form another activity this function also be invoked.
    // so this function can invoke at least once
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        glClearColor(0.0f, 0f, 0f, 0f);
        table = new Table();

        puck = new Puck(0.06f, 0.02f, 32);

        mallet = new Mallet(0.08f, 0.15f, 32);

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);


    }

    // when surface change size , vertical or horizontal switch surface size
    // change so this function invoke
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //set the opengl viewport to fill the entire surface
        Log.i("Hash", "width: " + width + " height:" + height);
        glViewport(0, 0, width, height);


        MatrixHelper.perspectiveM(projectMatrix, 45,
                (float) width / (float) height,
                1f, 10f);

        /**
         * float[] rm : 目标数组，这个矩阵的长度应该至少容纳16个元素，以便能存储视图矩阵
         * int rmOffset: setLookAtM 会把结果从rm的这个
         * float eyeX,eyeY,eyeZ:眼睛所在位置，场景中的所有东西都看起来像从这个点观察他们
         * float centerX,centerY,centerZ:眼睛正在看的地方，场景中心
         * float upX,upY,upZ:这是你头顶上的位置，upY为1 说明你的位置笔直指向头的上方，
         *
         *
         * eye（0，1.2,2.2）:意味着眼睛的位置在x-z平面上方1.2个单位，并且向后2.2个单位，
         * center（0,0,0): 说明中心点在你眼睛的下方，你需要朝下1.2个单位看你前面的原点
         * up(0,1,0): 说明你的头笔直指向上面，这个场景不会旋转到任何一边（可以这么理解，你的头没有歪着。）
         *
         */
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f,
                0f, 0f, 0f, 0f, 1f, 0f);

        // TODO: 2020-04-10 将这些操作全部转化为投影矩阵 视图矩阵 以及 模型矩阵 组合形式来展示。 
//        setIdentityM(modelMatrix, 0);
//        //沿着z轴方向平移-2
//        translateM(modelMatrix, 0, 0f, 0f, -2f);
//
//
//        rotateM(modelMatrix, 0, -60f, 1, 0, 0);
//        //需要将投影矩阵与平移矩阵相乘，这样坐标最终才会沿着z轴方向移动2个单位。
//        final float[] temp = new float[16];
//        //把投影矩阵和模型矩阵相乘，结果存储在temp中
//        multiplyMM(temp, 0, projectMatrix, 0, modelMatrix, 0);
//        //利用System.arrayCopy()把结果存回projectMatrix 包含了模型矩阵与投影矩阵的组合效应
//        System.arraycopy(temp, 0, projectMatrix, 0, temp.length);


    }

    //when draw per frame this function invoke,
    //also we should draw some thing, even if clear screen
    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);

        //将投影矩阵和视图矩阵的结果缓存到viewProjectMatrix
        multiplyMM(viewProjectMatrix, 0, projectMatrix, 0, viewMatrix, 0);


        //Draw the table
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(modelViewMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        //Draw the mallet
        positionObjectInScene(0f, mallet.height / 2, -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(modelViewMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2, 0.4f);
        colorShaderProgram.setUniform(modelViewMatrix, 0f, 0f, 1f);
        mallet.draw();

        //Draw the puck
        positionObjectInScene(0f, puck.height / 2, 0f);
        colorShaderProgram.setUniform(modelViewMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorShaderProgram);
        puck.draw();


    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewMatrix, 0, viewProjectMatrix, 0, modelMatrix, 0);

    }

    private void positionTableInScene() {

        //桌子原来是x和y坐标定义的，如果我们想看到立体的效果，也就是一开始，这个平面是相当于是我们的手机平面，
        // 但是立体的话 好比桌子是垂直我们手机屏幕的，所以要把坐标绕x轴向后旋转90
        setIdentityM(modelMatrix, 0);
        /**
         * x 分量 为1 说明旋转轴为x
         * -90 说明向后旋转 角度为90
         */
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);

        multiplyMM(modelViewMatrix, 0, viewProjectMatrix, 0, modelMatrix, 0);


    }

    public void handleTouchPress(float normalizedX, float normalizedY) {

    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

    }
}
