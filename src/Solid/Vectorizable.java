package Solid;

import transforms.Col;
import transforms.Point3D;

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col color;

    //souřadnice do textury

    public Vertex(Point3D pos, Col color){
        this.position = pos;
        this.color =color;
    }

    public Point3D getPosition(){
        return position;
    }

    public Col getColor(){
        return color;
    }

    @Override
    public Vertex mul(double k) {
        //TODO: implementovat, vynásobím vše skalárem
        return null;
    }

    @Override
    public Vertex add(Vertex v) {
        //TODO: přidám vše ke všemu
        return null;
    }
}