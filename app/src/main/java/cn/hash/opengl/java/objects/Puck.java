package cn.hash.opengl.java.objects;

import java.util.List;

import cn.hash.opengl.java.data.VertexArray;
import cn.hash.opengl.java.program.ColorShaderProgram;
import cn.hash.opengl.java.util.Geometry;

/**
 * Created by Hash on 2020-04-10.
 *
 * 当一个冰球被创建的时候，会生成那个物体的数据，用VertexArray 把顶点存储在一个本地缓冲区中，
 * 并把绘制命令列表存储在drawList中。
 */


public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;


    private final VertexArray vertexArray;

    private final List<ObjectBuilder.DrawCommand> drawCommandList;


    public Puck(float radius, float height, int numPoints) {
        ObjectBuilder.GenerateData generateData = ObjectBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius,
                height), numPoints);

        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generateData.vertexData);
        drawCommandList = generateData.drawCommandList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttributePointer(0,
                colorShaderProgram.getPositionAttributeLocation()
                , POSITION_COMPONENT_COUNT, 0);

    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawCommandList) {
            drawCommand.draw();
        }
    }
}
