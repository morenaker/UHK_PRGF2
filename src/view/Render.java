package view;

import Solid.*;
import Zbuffer.ZBuffer;
import raster.LineRasterizer;
import raster.TriangleRasterizer;
import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.IntStream;

public class Render {
    private ZBuffer bf;

    public void setFilled(boolean filled) {
        triangleRasterizer.setFilled(filled);
    }
    public void setTextured(boolean textured){triangleRasterizer.setTextured(textured);}

    private TriangleRasterizer triangleRasterizer;
    private LineRasterizer lineRasterizer;

    public void setModel(Mat4 model) {
        this.model = model;
    }

    private Mat4 model, view, projecMat4;

    public Render(ZBuffer bf, Mat4 modelMat, Mat4 view, Mat4 projecMat4) {
        this.bf = bf;
        triangleRasterizer = new TriangleRasterizer(bf);
        lineRasterizer = new LineRasterizer(bf);
        this.view = view;
        this.projecMat4 = projecMat4;
    }

    private void SortAxisLine(Vertex a, Vertex b){
        a = new Vertex(a.getPosition().mul(view).mul(projecMat4), a.getColor());
        b = new Vertex(b.getPosition().mul(view).mul(projecMat4), b.getColor());
        if(checkAxisOutOfBounds(a,b))
            return;
        if(a.getPosition().getZ() <b.getPosition().getZ()){
            Vertex temp = a;
            a = b;
            b = temp;
        }
        boolean vertexBehindNearPlane = a.getPosition().getZ() < 0 || b.getPosition().getZ() < 0;
        if(vertexBehindNearPlane){
            boolean aBehind = a.getPosition().getZ()<0;
            if(aBehind){
                return;
            }else{
                double t1 = (0 - a.getPosition().getZ()) / (b.getPosition().getZ() - a.getPosition().getZ());
                Vertex ab = a.mul(1 - t1).add(b.mul(t1));
                lineRasterizer.rasterize(a,ab);
            }
        }
        else{
            lineRasterizer.rasterize(a,b);
        }
    }

    private void SortLines(Vertex a, Vertex b){
        Mat4 transMat = new Mat4Identity().mul(model).mul(view).mul(projecMat4);

        a = new Vertex(a.getPosition().mul(transMat), a.getColor());
        b = new Vertex(b.getPosition().mul(transMat), b.getColor());
        if(checkAxisOutOfBounds(a,b))
            return;
        if(a.getPosition().getZ() <b.getPosition().getZ()){
            Vertex temp = a;
            a = b;
            b = temp;
        }
        boolean vertexBehindNearPlane = a.getPosition().getZ() < 0 || b.getPosition().getZ() < 0;
        if(vertexBehindNearPlane){
            boolean aBehind = a.getPosition().getZ()<0;
            if(aBehind){
                return;
            }else{
                double t1 = (0 - a.getPosition().getZ()) / (b.getPosition().getZ() - a.getPosition().getZ());
                Vertex ab = a.mul(1 - t1).add(b.mul(t1));
                lineRasterizer.rasterize(a,ab);
            }
        }
        else{
            lineRasterizer.rasterize(a,b);
        }
    }

    private void SortTriangle(Vertex a, Vertex b, Vertex c){
        Mat4 transMat = new Mat4Identity().mul(model).mul(view).mul(projecMat4);
        Vertex aVert = new Vertex(a.getPosition().mul(transMat), a.getColor(), a.getTexCoords());
        Vertex bVert = new Vertex(b.getPosition().mul(transMat), b.getColor(), b.getTexCoords());
        Vertex cVert = new Vertex(c.getPosition().mul(transMat), c.getColor(), c.getTexCoords());
        if(checkTriangleOutOfBounds(aVert,bVert,cVert))
            return;

        //je potřeba saeřadit strany podle z.
        if (aVert.getPosition().getZ() < bVert.getPosition().getZ()) {
            Vertex temp = aVert;
            aVert = bVert;
            bVert = temp;
        }
        if (bVert.getPosition().getZ() < cVert.getPosition().getZ()) {
            Vertex temp = bVert;
            bVert = cVert;
            cVert = temp;
        }
        if (aVert.getPosition().getZ() < bVert.getPosition().getZ()) {
            Vertex temp = aVert;
            aVert = bVert;
            bVert = temp;
        }
        boolean vertexBehindNearPlane = aVert.getPosition().getZ() < 0 || bVert.getPosition().getZ() < 0 || cVert.getPosition().getZ() < 0;

            if (vertexBehindNearPlane) {
                boolean aBehind = aVert.getPosition().getZ() < 0;
                boolean bBehind = bVert.getPosition().getZ() < 0;
                boolean cBehind = cVert.getPosition().getZ() < 0;

                if (bBehind) {
                    // jsou videt b + c
                    double t1 = (0 - aVert.getPosition().getZ()) / (bVert.getPosition().getZ() - aVert.getPosition().getZ());
                    Vertex tAB = aVert.mul(1 - t1).add(bVert.mul(t1));

                    double t2 = -aVert.getPosition().getZ() / (cVert.getPosition().getZ() - aVert.getPosition().getZ());
                    Vertex tAC = aVert.mul(1 - t2).add(cVert.mul(t2));
                    System.out.println("Rasterizing b +c");
                    triangleRasterizer.rasterize(aVert, tAB, tAC);
                } else if (cBehind) {
                    // jsou videt a + c
                    double t1 = -aVert.getPosition().getZ() / (cVert.getPosition().getZ() - aVert.getPosition().getZ());
                    Vertex tAC = aVert.mul(1 - t1).add(cVert.mul(t1));

                    double t2 = -bVert.getPosition().getZ() / (cVert.getPosition().getZ() - bVert.getPosition().getZ());
                    Vertex tBC = bVert.mul(1 - t2).add(cVert.mul(t2));
                    System.out.println("Rasterizing a+c");
                    triangleRasterizer.rasterize(aVert, bVert, tBC);
                    triangleRasterizer.rasterize(aVert, tAC, tBC);
                } else if (aBehind) {
                    System.out.println("unable to render");
                    //nemuzeme rendrovat jelikoz 2 hrany nejsou viditelne
                }
            } else {
                //ve je videt
                System.out.println("Rasterizing everything");
                triangleRasterizer.rasterize(aVert, bVert, cVert);
            }
    }

    private boolean checkTriangleOutOfBounds(Vertex a, Vertex b, Vertex c){
        boolean triangleOutOfRightBounds = a.getPosition().getX() > a.getPosition().getW() && b.getPosition().getX() > b.getPosition().getW() && c.getPosition().getX() > c.getPosition().getW();
        boolean triangleOutOfLeftBounds = a.getPosition().getX() < -a.getPosition().getW() && b.getPosition().getX() < -b.getPosition().getW() && c.getPosition().getX() < -c.getPosition().getW();
        boolean triangleOutOfTopBounds = a.getPosition().getY() > a.getPosition().getW() && b.getPosition().getY() > b.getPosition().getW() && c.getPosition().getY() > c.getPosition().getW();
        boolean triangleOutOfBottomBounds = a.getPosition().getY() < -a.getPosition().getW() && b.getPosition().getY() < -b.getPosition().getW() && c.getPosition().getY() < -c.getPosition().getW();
        boolean triangleOutOfFrontBounds = a.getPosition().getZ() > a.getPosition().getW() && b.getPosition().getZ() > b.getPosition().getW() && c.getPosition().getZ() > c.getPosition().getW();
        boolean triangleOutOfBackBounds = a.getPosition().getZ() < 0 && b.getPosition().getZ() < 0 && c.getPosition().getZ() < 0;

        if (triangleOutOfRightBounds || triangleOutOfLeftBounds || triangleOutOfTopBounds ||
                triangleOutOfBottomBounds || triangleOutOfFrontBounds || triangleOutOfBackBounds) {
            return true;
        }
        else{
            return false;
        }
    }

    private boolean checkAxisOutOfBounds(Vertex a, Vertex b){
        boolean AxisOutOfRightBounds = a.getPosition().getX() > a.getPosition().getW() && b.getPosition().getX() > b.getPosition().getW();
        boolean AxisOutOfLeftBounds = a.getPosition().getX() < -a.getPosition().getW() && b.getPosition().getX() < -b.getPosition().getW();
        boolean AxisOutOfTopBounds = a.getPosition().getY() > a.getPosition().getW() && b.getPosition().getY() > b.getPosition().getW();
        boolean AxisOutOfDownBounds = a.getPosition().getY() < -a.getPosition().getW() && b.getPosition().getY() < -b.getPosition().getW();
        boolean AxisOutOfFrontBounds = a.getPosition().getZ() > a.getPosition().getW() && b.getPosition().getZ() > b.getPosition().getW();
        boolean AxisOutOfBackBounds = a.getPosition().getZ() < 0 && b.getPosition().getZ() < 0;

        if(AxisOutOfRightBounds || AxisOutOfLeftBounds || AxisOutOfTopBounds || AxisOutOfDownBounds || AxisOutOfFrontBounds || AxisOutOfBackBounds){
            return true;
        }
        else
        {
            return false;
        }
    }

    public void draw(ArrayList<Solid> solids){
        for(Solid s : solids){
            this.setModel(s.getModel());
            for(Part p : s.getPartBuffer()){
                TopologyType topT = p.getType();
                switch (topT) {
                    case TRIANGLES:
                        // Procházíme všechny trojúhelníky v části
                        IntStream.range(0, p.getCount())
                                .mapToObj(i -> Arrays.asList(3 * i, 3 * i + 1, 3 * i + 2))
                                .forEach(indices -> {
                                    //je potřeba určit jestli se má trojuhelnik vykreslit nebo ne.
                                    SortTriangle(s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + indices.get(0))),
                                                 s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + indices.get(1))),
                                                 s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + indices.get(2))));
                                });
                    case AXIS:
                        IntStream.range(0, p.getCount())
                                .mapToObj(i -> Arrays.asList(2 * i, 2 * i + 1))
                                .forEach(indices -> {
                                    SortAxisLine(s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + indices.get(0))),
                                            s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + indices.get(1))));
                                });
                    case LINES:
                        IntStream.range(0, p.getCount()).mapToObj(i -> Arrays.asList(2 * i, 2 * i + 1)).forEach(integers ->  {
                            SortLines(s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + integers.get(0))),
                                    s.getVertexBuffer().get(s.getIndexBuffer().get(p.getStart() + integers.get(1))));
                        });
                }
            }
        }
    }

    public void clear(){
        bf.clear();
        bf.getImageBuffer().clear();
    }
}
