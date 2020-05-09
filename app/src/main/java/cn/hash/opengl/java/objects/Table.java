package cn.hash.opengl.java.objects;

import cn.hash.opengl.java.Constants;
import cn.hash.opengl.java.data.VertexArray;
import cn.hash.opengl.java.program.TextureShaderProgram;

import static android.opengl.GLES20.glDrawArrays;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;

/**
 * Created by Hash on 2020-04-10.
 */


public class Table {
    //x.y 两个点为一组
    private static final int POSITION_COMPONENT_COUNT = 2;
    //s.t 分量，纹理 方向y 向下增加 注意 此处使用的是纹理，没有使用RGB分量颜色来渲染顶点
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    //跨度 也就是隔几个数据 访问下一组数据
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT)
            * Constants.BYTES_PER_FLOAT;

    private VertexArray vertexArray;


    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }


    /**
     * 注意纹理的T分量默认是y方向向下增加，这就与坐标y方向相反，y默认向上是增加，
     * 因此注意了，如果坐标y方向是负数，说明向下，那么T分量就增大，如果坐标y方向为正数，说明T分量减小
     */
    private static final float[] VERTEX_DATA = {

            //order of coordinates :x , y ,s,t

            //triangle fan
            0f, 0f, 0.5f, 0.5f,

            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f
    };

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttributePointer(0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinatesPosition(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );

    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


}
