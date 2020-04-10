package cn.hash.opengl.java.objects;

import android.util.FloatMath;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.hash.opengl.java.util.Geometry;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Hash on 2020-04-10.
 * <p>
 * 1。 调用者可以决定物体应该有多少个点，点越多，冰球和木槌看上去就越平滑
 * 2。 物体被包含在一个浮点数组中，物体被创建后，调用者将有一个绑定到OpenGL的数组和一个绘制物体的命令
 * 3。 物体将以调用者指定的位置中心，并平放在x-z平面上， 也就是y分量变化不大
 * 对于顶部的圆，y分量不变，对于圆柱体侧面来说，就是一个起始和终点的 以高度为界限，y分量加减1/2高度
 */
public class ObjectBuilder {

    //x,y,z 一个顶点组合
    private static final int FLOAT_PER_VERTEX = 3;

    //存储顶点的数组
    private final float[] vertexData;

    //记录下一个顶点的位置。
    private int offset = 0;

    private List<DrawCommand> drawCommands = new ArrayList<>();

    /**
     * 构造内部初始化了存储这些顶点的数组。
     *
     * @param sizeInVertices 多少个顶点
     */
    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOAT_PER_VERTEX];
        Log.w("ObjectBuilder:", "vertexData length:" + vertexData.length);
    }

    /*
     * 一个圆柱体的顶部是一个三角形扇构造的圆， numPoints
     * 有一个顶点在圆心， 1
     * 围着圆的第一个顶点需要重复两次才能将圆闭合。+1
     *
     */
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    /*
     * 一个圆柱体侧面是一个三角形带构造的，围着顶部圆的每个点都需要其他两个点构成三角形
     * 因此需要2*numPoints， 但是前两个顶点需要重复两次才能是这个管子闭合
     * 因此还有+2
     *
     */
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (1 + numPoints) * 2;
    }
    /*
     * 找出需要多少个顶点表示这个冰球，一个冰球由一个圆柱体的顶部（圆）和一个圆柱体的侧面构成，
     * 因此所有的顶点等于 sizeofCircleInVertices 和 sizeofOpenCylinerInvertices
     *
     * 冰球垂直方向以point.y 为中心，因此在那里放置圆柱体的侧面，圆柱体的顶部需要放在冰球的顶部，把顶部上移动到冰球整体高度的一半。也就是point.y+1/2height
     */

    static GenerateData createPuck(Geometry.Cylinder cylinder, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);
        Geometry.Circle puckTop = new Geometry.Circle(
                cylinder.point.translateY(cylinder.height / 2f),
                cylinder.radius
        );
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCyliner(cylinder, numPoints);
        return builder.build();

    }

    /**
     * 用两个圆柱体构造一个木槌
     * <p>
     * 木槌可以由一个手柄（圆柱体） 垂直放在在一个扁平的圆柱体
     * 高度为3:1
     *
     * @param point
     * @param radius
     * @param height
     * @param numPoints
     * @return
     */
    static GenerateData createMallet(Geometry.Point point, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);

        //1。定义下面的扁平圆柱体
        float bottomHeight = height * 0.25f; //1/4 height

        Geometry.Circle bottomCircle = new Geometry.Circle(point.translateY(-bottomHeight), radius);

        Geometry.Cylinder bottomCylinder = new Geometry.Cylinder(bottomCircle.center.translateY(-bottomHeight / 2),
                radius, bottomHeight);
        builder.appendCircle(bottomCircle, numPoints);
        builder.appendOpenCyliner(bottomCylinder, numPoints);

        //2。定义手柄圆柱体

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

        Geometry.Circle handleCircle = new Geometry.Circle(point.translateY(height * 0.5f),
                handleRadius);

        Geometry.Cylinder handleCyliner = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);

        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCyliner(handleCyliner, numPoints);


        return builder.build();
    }

    private GenerateData build() {
        return new GenerateData(vertexData, drawCommands);
    }

    private void appendOpenCyliner(Geometry.Cylinder cylinder, final int numPoints) {
        final int startVertex = offset / FLOAT_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);

        final float yStart = cylinder.point.y - cylinder.height / 2f;
        final float yEnd = cylinder.point.y + cylinder.height / 2f;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadius = ((float) i / (float) numPoints) * (float) (Math.PI * 2f);

            float xPosition = (float) (cylinder.point.x + (float) cylinder.radius * Math.cos(angleInRadius));
            float zPosition = (float) (cylinder.point.z + cylinder.radius * Math.sin(angleInRadius));
            Log.w("ObjectBuilder", "index:" + offset);
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);

            }
        });


    }

    //添加顶部 圆
    private void appendCircle(Geometry.Circle puckTop, int numPoints) {
        final int startVertex = offset / FLOAT_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        vertexData[offset++] = puckTop.center.x;
        vertexData[offset++] = puckTop.center.y;
        vertexData[offset++] = puckTop.center.z;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadius = ((float) i / numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = (float) (puckTop.center.x + puckTop.radius * Math.cos(angleInRadius));
            vertexData[offset++] = puckTop.center.y;
            vertexData[offset++] = (float) (puckTop.center.z + puckTop.radius * Math.sin(angleInRadius));

        }
        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);

            }
        });

    }


    // 用圆柱体创建球
    static class GenerateData {
        final float[] vertexData;

        final List<DrawCommand> drawCommandList;

        GenerateData(float[] vertexData, List<DrawCommand> drawCommands) {
            this.vertexData = vertexData;
            this.drawCommandList = drawCommands;
        }


    }

    static interface DrawCommand {
        void draw();
    }

}
