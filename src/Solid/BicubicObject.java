package Solid;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class BicubicObject extends Solid{
    public List<Point3D> points;

    public BicubicObject(final Mat4 baseMat) {
        points = new ArrayList<>();

        points.add(new Point3D(-2, 3, -2));
        points.add(new Point3D(3, 2.7, -7));
        points.add(new Point3D(8, 6, -7));
        points.add(new Point3D(0.5, 3, -6));

        points.add(new Point3D(4, 3.5, -3));
        points.add(new Point3D(4.5, 5, -5));
        points.add(new Point3D(7, 7, -3.5));
        points.add(new Point3D(3, 6, -4.8));

        points.add(new Point3D(-5, 4, -4));
        points.add(new Point3D(6, 8, -7));
        points.add(new Point3D(9, 7, -5.5));
        points.add(new Point3D(8, 8, -6));

        points.add(new Point3D(-6, 9, -5));
        points.add(new Point3D(5, 9, -7));
        points.add(new Point3D(11, 8, -8));
        points.add(new Point3D(15, 17, -5));



        transforms.Bicubic bicubic = new transforms.Bicubic(baseMat, points.toArray(new Point3D[0]), 0);

        double step = 0.1;
        int numSteps = (int) (1 / step);

        for (int j = 0; j < numSteps; j++) {
            double y = j * step;
            for (int i = 0; i < numSteps; i++) {
                double x = i * step;
                Point3D vertex = bicubic.compute(x, y);
                vertexBuffer.add(new Vertex(vertex, new Col(60 * (x + 1), 0, 60 * (y + 1))));
            }
        }

        for (int j = 0; j < numSteps; j++) {
            for (int i = 0; i < numSteps - 1; i++) {
                int currentIndex = j * numSteps + i;
                indexBuffer.add(currentIndex);
                indexBuffer.add(currentIndex + 1);
            }
        }
        for (int i = 0; i < numSteps; i++) {
            for (int j = 0; j < numSteps - 1; j++) {
                int currentIndex = j * numSteps + i;
                indexBuffer.add(currentIndex);
                indexBuffer.add(currentIndex + numSteps);
            }
        }

        partBuffer.add(new Part(0, indexBuffer.size() / 2, TopologyType.LINES));
    }


}
