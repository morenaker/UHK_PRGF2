package Solid;

import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.ArrayList;

public abstract class Solid {
    public final ArrayList<Vertex> vertexBuffer = new ArrayList<Vertex>();
    public final ArrayList<Integer> indexBuffer = new ArrayList<>();
    public final ArrayList<Part> partBuffer = new ArrayList<Part>();

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    Mat4 model = new Mat4Identity();

    public ArrayList<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public ArrayList<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public ArrayList<Part> getPartBuffer() {
        return partBuffer;
    }
}
