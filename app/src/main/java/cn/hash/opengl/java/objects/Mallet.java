package cn.hash.opengl.java.objects;

import java.util.List;
import cn.hash.opengl.java.data.VertexArray;
import cn.hash.opengl.java.program.ColorShaderProgram;
import cn.hash.opengl.java.util.Geometry;


/**
 * Created by Hash on 2020-04-10.
 *
 * @modify 有了物体构建器 就不用在把木槌画成点
 */
public class Mallet {
    //x，y,z 坐标
    private static final int POSITION_COMPONENT_COUNT = 3;

    private final float radius;

    public final float height;


    private final List<ObjectBuilder.DrawCommand> drawCommands;

    private final VertexArray vertexArray;

    public Mallet(float radius, float height, int numPoints) {
        ObjectBuilder.GenerateData generateData = ObjectBuilder.createMallet(new Geometry.Point(0f, 0f, 0f), radius, height, numPoints);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generateData.vertexData);
        drawCommands = generateData.drawCommandList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttributePointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        );
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }

}
