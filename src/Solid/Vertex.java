package Solid;

import transforms.*;

public class Vertex implements Vectorizable<Vertex> {
    private Point3D position;
    private Col color;
    public Vec2D texCoords;

    public Vertex(Point3D pos, Col color, Vec2D texCoords){
        this.position = pos;
        this.color =color;
        this.texCoords = texCoords;
    }

    public Vertex(Point3D pos, Col color){
        this.position = pos;
        this.color = color;
        this.texCoords = new Vec2D(color.getR(),color.getB());
    }

    public Point3D getPosition(){
        return position;
    }
    public Col getColor(){
        return color;
    }
    public void setColor(Col color){
        this.color = color;
    }
    public boolean areTexCoordsPresent(){
        if(texCoords != null)
            return true;
        else
            return false;
    }
    public Vec2D getTexCoords(){
        return texCoords;
    }

    public Vertex mul(double t) {
        // Násobení pozice a barvy vrcholu skalárem
        Point3D newPosition = position.mul(t);
        Col newColor = color.mul(t);
        Vec2D newTex = texCoords.mul(t);
        return new Vertex(newPosition,newColor,newTex);
    }

    @Override
    public Vertex add(Vertex v) {
        // Sčítání pozice a barvy vrcholů
        Point3D newPosition = position.add(v.getPosition());
        Col newColor = color.add(v.getColor());
        Vec2D newTex = texCoords.add(v.getTexCoords());
        return new Vertex(newPosition, newColor, newTex);
    }

    @Override
    public String toString(){
        System.out.println("X: " + this.getPosition().getX()+
                            "Y: " + this.getPosition().getY()+
                            "Z: "+ this.getPosition().getZ());
        return null;
    }
}
