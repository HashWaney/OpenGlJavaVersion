package cn.hash.opengl.java.program;

import android.content.Context;

import cn.hash.opengl.java.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Hash on 2020-04-10.
 */


public class TextureShaderProgram extends ShaderProgram {

    //Uniform locations;
    private final int uMatrixLocation;

    private final int uTextureUnitLocation;

    //attribute position
    private final int aPosition;
    private final int aTextureCoordinatesPosition;


    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPosition = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesPosition = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }


    public void setUniforms(float[] matrix, int textureId) {
        //传递矩阵给uniform
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        /**
         * 当在OpenGl里使用纹理进行绘制的时候，不需要直接给着色器传递纹理，
         * 而是使用纹理单元保存一个纹理，是因为-个GPU只能同时绘制有限数量的纹理，使用这些纹理单元表示当前正在被绘制的活动的纹理，
         *
         */
        glActiveTexture(GL_TEXTURE0);

        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureId);

        //把选定的纹理单元传递给片段着色器的u_TextureUnit 也就是通过varying声明的变量，片段着色器就可以根据数据进行着色
        glUniform1i(uTextureUnitLocation, 0);


    }

    public int getPositionAttributeLocation() {
        return aPosition;
    }

    public int getTextureCoordinatesPosition() {
        return aTextureCoordinatesPosition;
    }
}
