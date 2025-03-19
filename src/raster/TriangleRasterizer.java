package raster;

import Shader.*;
import Solid.Vertex;
import Zbuffer.ZBuffer;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;
import utils.lerp;

import java.util.Optional;

public class TriangleRasterizer implements Rasterizer {
    private final ZBuffer zb;
    private final InterShade shader = new InterShade();
    private final TexShade texShader = new TexShade();
    private final lerp LERP;

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    public void setTextured(boolean textured) {this.textured = textured;}

    private boolean filled = true;
    private boolean textured = false;

    public TriangleRasterizer(ZBuffer zb){
        this.zb = zb;
        this.LERP = new lerp<Vertex>();
    }

    public void rasterize(Vertex a, Vertex b, Vertex c){
        if(filled) {
            if(textured) {
                rasterizeWithTextures(a,b,c);
            }
            else{
               rasterizeWithColors(a,b,c);
            }
        }
        else{
            rasterizeOnlyLines(a,b, c);
        }
    }

    @Override
    public Vec3D transformToWindow(Point3D pos) {
        return new Vec3D(pos)
                .mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(zb.getImageBuffer().getWidth() / 2f, zb.getImageBuffer().getHeight() / 2f, 1));
    }

    public void rasterizeWithTextures(Vertex a, Vertex b, Vertex c){
        Optional<Vec3D> dA = a.getPosition().dehomog();
        Optional<Vec3D> dB = b.getPosition().dehomog();
        Optional<Vec3D> dC = c.getPosition().dehomog();
        a = new Vertex(new Point3D(transformToWindow(new Point3D(dA.get()))), a.getColor(), a.getTexCoords());
        b = new Vertex(new Point3D(transformToWindow(new Point3D(dB.get()))), b.getColor(), b.getTexCoords());
        c = new Vertex(new Point3D(transformToWindow(new Point3D(dC.get()))), c.getColor(), c.getTexCoords());

        if (b.getPosition().getY() < a.getPosition().getY()) //pokud B je menší jak A
        {
            Vertex temp = b;
            b = a;
            a = temp;
        }
        if (c.getPosition().getY() < a.getPosition().getY()) //pokud C je menší jak A
        {
            Vertex temp = c;
            c = a;
            a = temp;
        }
        if (c.getPosition().getY() < b.getPosition().getY()) //pokud C je menší jak B
        {
            Vertex temp = c;
            c = b;
            b = temp;
        }

        int yStart = Math.max(0, (int) a.getPosition().getY() + 1);
        double yEnd = Math.min(zb.getImageBuffer().getHeight() - 1, b.getPosition().getY());
        for (int y = yStart; y <= yEnd; y++) {
            //hrana AB
            double tAB = (y - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
            Vertex vAB = a.mul(1 - tAB).add(b.mul(tAB));

            //hrana AC
            double tAC = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            Vertex vAC = a.mul(1 - tAC).add(c.mul(tAC));

            if (Math.round(vAB.getPosition().getX()) > Math.round(vAC.getPosition().getX())) {
                Vertex temp = vAB;
                vAB = vAC;
                vAC = temp;
            }
            for (int x = (int) vAB.getPosition().getX(); x <= vAC.getPosition().getX(); x++) {
                double tZ = (x - vAB.getPosition().getX()) / (double) (vAC.getPosition().getX() - vAB.getPosition().getX());
                Vertex z = vAB.mul(1 - tZ).add(vAC.mul(tZ));

                zb.setPixelWithZTest(x, y, z.getPosition().getZ(), texShader.shade(z));
            }
        }

        int xStart = Math.max(0, (int) b.getPosition().getY() + 1);
        double xEnd = Math.min(zb.getImageBuffer().getWidth() - 1, c.getPosition().getY());
        for (int y = xStart; y <= xEnd; y++) {
            //hrana BC
            double tBC = (y - b.getPosition().getY()) / (c.getPosition().getY() - b.getPosition().getY());
            Vertex vBC = b.mul(1 - tBC).add(c.mul(tBC));

            //hrana AC
            double tAC2 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            Vertex vAC = a.mul(1 - tAC2).add(c.mul(tAC2));
            if (vBC.getPosition().getX() > vAC.getPosition().getX()) {
                Vertex temp = vBC;
                vBC = vAC;
                vAC = temp;
            }

            for (int x = (int) vBC.getPosition().getX(); x <= vAC.getPosition().getX(); x++) {
                double tZ = (x - vBC.getPosition().getX()) / (vAC.getPosition().getX() - vBC.getPosition().getX());
                Vertex z = vBC.mul(1 - tZ).add(vAC.mul(tZ));
                zb.setPixelWithZTest(x, y, z.getPosition().getZ(), texShader.shade(z));
            }
        }
    }

    public void rasterizeWithColors(Vertex a, Vertex b, Vertex c){
        Optional<Vec3D> dA = a.getPosition().dehomog();
        Optional<Vec3D> dB = b.getPosition().dehomog();
        Optional<Vec3D> dC = c.getPosition().dehomog();
        a = new Vertex(new Point3D(transformToWindow(new Point3D(dA.get()))), a.getColor(), a.getTexCoords());
        b = new Vertex(new Point3D(transformToWindow(new Point3D(dB.get()))), b.getColor(), b.getTexCoords());
        c = new Vertex(new Point3D(transformToWindow(new Point3D(dC.get()))), c.getColor(), c.getTexCoords());

        if (b.getPosition().getY() < a.getPosition().getY()) //pokud B je menší jak A
        {
            Vertex temp = b;
            b = a;
            a = temp;
        }
        if (c.getPosition().getY() < a.getPosition().getY()) //pokud C je menší jak A
        {
            Vertex temp = c;
            c = a;
            a = temp;
        }
        if (c.getPosition().getY() < b.getPosition().getY()) //pokud C je menší jak B
        {
            Vertex temp = c;
            c = b;
            b = temp;
        }

        int yStart = Math.max(0, (int) a.getPosition().getY() + 1);
        double yEnd = Math.min(zb.getImageBuffer().getHeight() - 1, b.getPosition().getY());
        for (int y = yStart; y <= yEnd; y++) {
            //hrana AB
            double tAB = (y - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
            Vertex vAB = a.mul(1 - tAB).add(b.mul(tAB));

            //hrana AC
            double tAC = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            Vertex vAC = a.mul(1 - tAC).add(c.mul(tAC));

            if (Math.round(vAB.getPosition().getX()) > Math.round(vAC.getPosition().getX())) {
                Vertex temp = vAB;
                vAB = vAC;
                vAC = temp;
            }
            for (int x = (int) vAB.getPosition().getX(); x <= vAC.getPosition().getX(); x++) {
                double tZ = (x - vAB.getPosition().getX()) / (double) (vAC.getPosition().getX() - vAB.getPosition().getX());
                Vertex z = vAB.mul(1 - tZ).add(vAC.mul(tZ));

                zb.setPixelWithZTest(x, y, z.getPosition().getZ(), shader.shade(z));
            }
        }

        int xStart = Math.max(0, (int) b.getPosition().getY() + 1);
        double xEnd = Math.min(zb.getImageBuffer().getWidth() - 1, c.getPosition().getY());
        for (int y = xStart; y <= xEnd; y++) {
            //hrana BC
            double tBC = (y - b.getPosition().getY()) / (c.getPosition().getY() - b.getPosition().getY());
            Vertex vBC = b.mul(1 - tBC).add(c.mul(tBC));

            //hrana AC
            double tAC2 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            Vertex vAC = a.mul(1 - tAC2).add(c.mul(tAC2));
            if (vBC.getPosition().getX() > vAC.getPosition().getX()) {
                Vertex temp = vBC;
                vBC = vAC;
                vAC = temp;
            }

            for (int x = (int) vBC.getPosition().getX(); x <= vAC.getPosition().getX(); x++) {
                double tZ = (x - vBC.getPosition().getX()) / (vAC.getPosition().getX() - vBC.getPosition().getX());
                Vertex z = vBC.mul(1 - tZ).add(vAC.mul(tZ));
                zb.setPixelWithZTest(x, y, z.getPosition().getZ(), shader.shade(z));
            }
        }
    }

    public void rasterizeOnlyLines(Vertex a, Vertex b, Vertex c){
        LineRasterizer lr = new LineRasterizer(zb);
        lr.rasterize(a,b);
        lr.rasterize(b,c);
        lr.rasterize(a,c);
    }
}
