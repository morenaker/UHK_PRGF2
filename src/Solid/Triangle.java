package Solid;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class Triangle extends Solid {
    public Triangle(){
        vertexBuffer.add(new Vertex(new Point3D(8, 6, 5), new Col(0, 125, 0), new Vec2D(0,0.5d)));
        vertexBuffer.add(new Vertex(new Point3D(3, 8, 6), new Col(255, 125, 200), new Vec2D(1,1)));
        vertexBuffer.add(new Vertex(new Point3D(4, 5, -6), new Col(0, 125, 200), new Vec2D(1,0)));
        indexBuffer.add(0);
        indexBuffer.add(1);
        indexBuffer.add(2);
        partBuffer.add(new Part(0,1,TopologyType.TRIANGLES));
    }
}
