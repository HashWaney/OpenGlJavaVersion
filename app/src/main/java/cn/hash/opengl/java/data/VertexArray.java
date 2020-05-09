package cn.hash.opengl.java.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import cn.hash.opengl.java.Constants;

/**
 * Created by Hash on 2020-04-10.
 */


public class VertexArray {


    //本地代码存储顶点矩阵数据，并float数组写入缓冲区
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length
                        * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }


    public void setVertexAttributePointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {

        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation,
                componentCount, GL_FLOAT,
                false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);


    }
}
