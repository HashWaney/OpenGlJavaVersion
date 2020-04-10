package cn.hash.opengl.java.objects;

import cn.hash.opengl.java.Constants;
import cn.hash.opengl.java.data.VertexArray;
import cn.hash.opengl.java.program.ColorShaderProgram;


import static android.opengl.GLES20.glDrawArrays;
import static javax.microedition.khronos.opengles.GL10.GL_POINTS;

/**
 * Created by Hash on 2020-04-10.
 */


public class Mallet {
    //x，y坐标
    private static final int POSITION_COMPONENT_COUNT = 2;
    //rgb 分量颜色
    private static final int COLOR_COMPONENT_COUNT = 3;
    //跨度
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //order of coordinates : x,y,r,g,b
            0f, -0.5f, 0f, 0f, 1f,
            0f, 0.5f, 0f, 1f, 0f
    };
    private final VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttributePointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttributePointer(POSITION_COMPONENT_COUNT,
                colorShaderProgram.getColorAttributeLocation()
                , COLOR_COMPONENT_COUNT, STRIDE);

    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, 2);
    }

}
