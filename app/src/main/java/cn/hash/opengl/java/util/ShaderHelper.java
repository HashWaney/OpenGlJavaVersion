package cn.hash.opengl.java.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by Hash on 2020-04-08.
 */


public class ShaderHelper {
    public static final String TAG = "ShaderHelper";


    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }


    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * Compile the Shader
     *
     * @param type
     * @param shaderCode
     * @return
     */
    private static int compileShader(int type, String shaderCode) {
        // TODO: 2020-04-08
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {//create fail
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader");
            }
            return 0;
        }

        // load shader_code to object
        glShaderSource(shaderObjectId, shaderCode);


        //compile the shader
        glCompileShader(shaderObjectId);

        //get the compile status
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON) {
            Log.v(TAG,
                    "Result of compile source:" + "\n" + shaderCode + "\n" + glGetShaderInfoLog(shaderObjectId));
        }
        if (compileStatus[0] == 0) {
            //if it failed,delete the shader object
            glDeleteShader(shaderObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed");
            }
            return 0;
        }

        return shaderObjectId;

    }

    /**
     * Link shader to Program
     *
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int program = glCreateProgram();
        if (program == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create program");
            }
            return 0;
        }

        glAttachShader(program, vertexShaderId);
        glAttachShader(program, fragmentShaderId);
        glLinkProgram(program);

        final int[] status = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if (LoggerConfig.ON) {
            Log.v(TAG, "Result of linking program :\n" + glGetProgramInfoLog(program));

        }
        if (status[0] == 0) {
            glDeleteProgram(program);
            if (LoggerConfig.ON) {
                Log.v(TAG, "linking program failed");
            }
            return 0;
        }
        Log.w(TAG, "link program programId:" + program);
        return program;
    }

    /**
     * Validate the program
     *
     * @param programId
     * @return
     */
    public static boolean validateProgram(int programId) {

        glValidateProgram(programId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0);

        if (LoggerConfig.ON) {
            Log.w(TAG, "Result of validate program :" + validateStatus[0]
                    + "\n" + " LogInfo :" + glGetProgramInfoLog(programId) + " programId:" + programId


            );
        }


        return validateStatus[0] != 0;
    }


    public static int buildProgram(String vertexShaderSource, String fragmentShaderResource) {
        int program;
        int vertexId = compileVertexShader(vertexShaderSource);
        int fragmentId = compileFragmentShader(fragmentShaderResource);

        program = linkProgram(vertexId, fragmentId);
        if (LoggerConfig.ON) {
            validateProgram(program);
        }


        return program;
    }


}
