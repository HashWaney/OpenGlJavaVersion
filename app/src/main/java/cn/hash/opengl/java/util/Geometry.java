package cn.hash.opengl.java.util;

/**
 * Created by Hash on 2020-04-10.
 */


public class Geometry {


    //顶点
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        //沿着y轴平移
        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }
    }

    //圆形：一个中心点，一个半径
    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    //圆柱体: 一个中心点，一个半径，一个高度
    public static class Cylinder {
        public final Point point;
        public final float radius;
        public final float height;


        public Cylinder(Point center, float radius, float height) {
            this.radius = radius;
            this.height = height;
            this.point = center;
        }
    }
}
