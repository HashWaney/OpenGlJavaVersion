package cn.hash.opengl.java;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.hash.opengl.java.objects.Mallet;
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
import static android.opengl.Matrix.translateM;

/**
 * Created by Hash on 2020-04-08.
 */


public class OpenGlRenderer implements GLSurfaceView.Renderer {

    private final float[] projectMatrix = new float[16];

    //模型矩阵
    private final float[] modelMatrix = new float[16];

    private Table table;

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
        mallet = new Mallet();

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

        for (int i = 0; i < projectMatrix.length; i++) {
            System.err.println(String.format("before : i =%d , values %f ",i,projectMatrix[i]));
            System.err.println("\n");

        }
        setIdentityM(modelMatrix, 0);
        //沿着z轴方向平移-2
        translateM(modelMatrix, 0, 0f, 0f, -2f);

        for (int i=0;i<modelMatrix.length;i++)
        {
            System.err.println(String.format("i=%d , values =%f",i,modelMatrix[i]));
            System.err.println("\n");
        }


        rotateM(modelMatrix, 0, -60f, 1, 0, 0);
        //需要将投影矩阵与平移矩阵相乘，这样坐标最终才会沿着z轴方向移动2个单位。
        final float[] temp = new float[16];
        //把投影矩阵和模型矩阵相乘，结果存储在temp中
        multiplyMM(temp, 0, projectMatrix, 0, modelMatrix, 0);
        //利用System.arrayCopy()把结果存回projectMatrix 包含了模型矩阵与投影矩阵的组合效应
        System.arraycopy(temp, 0, projectMatrix, 0, temp.length);

        for (int i = 0; i < projectMatrix.length; i++) {
            System.err.println(String.format("end i = %d ,value =%f ",i,projectMatrix[i]));

        }


    }

    //when draw per frame this function invoke,
    //also we should draw some thing, even if clear screen
    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);

        //Draw the table
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectMatrix,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        //Draw the mallet
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(projectMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();


    }
}
