package Solid;

import transforms.Col;
import transforms.Point3D;

public class Axis extends Solid {
    public Axis() {
        vertexBuffer.add(new Vertex(new Point3D(), new Col(255, 255, 255)));
        vertexBuffer.add(new Vertex(new Point3D(10, 0, 0), new Col(255, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(0, 10, 0), new Col(0, 255, 0)));
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 10), new Col(0, 0, 255)));

        indexBuffer.add(0);
        indexBuffer.add(1);

        indexBuffer.add(0);
        indexBuffer.add(2);

        indexBuffer.add(0);
        indexBuffer.add(3);

        partBuffer.add(new Part( 0, 3,TopologyType.AXIS));
    }
}
