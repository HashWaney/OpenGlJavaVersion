package cn.hash.opengl.java.program;

import android.content.Context;

import cn.hash.opengl.java.util.ShaderHelper;
import cn.hash.opengl.java.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by Hash on 2020-04-10.
 */


public class ShaderProgram {

    //Uniform constants
    protected static final String U_MATRIX = "u_Matrix";

    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    //attribute constants
    protected static final String A_POSITION = "a_Position";

    protected static final String A_COLOR = "a_Color";

    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    //Shader program
    protected final int program;

    protected ShaderProgram(Context context) {
        this(context, -1, -1);
    }


    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {

        //compile the shader and link the program
        program = ShaderHelper.buildProgram(TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
        );

    }

    public void useProgram() {
        //set the current opengl shader program to this program;
        glUseProgram(program);
    }

}
