package raster;

import Shader.InterShade;
import Shader.TexShade;
import Solid.Vertex;
import Zbuffer.ZBuffer;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.Optional;

import static java.lang.Math.abs;


public class LineRasterizer implements Rasterizer {
    private final ZBuffer zb;
    private final InterShade shader = new InterShade();
    private final TexShade texShader = new TexShade();
    public LineRasterizer(ZBuffer zb){
        this.zb = zb;
    }

    public void rasterize(Vertex a, Vertex b){
        Optional<Vec3D> dA = a.getPosition().dehomog();
        Optional<Vec3D> dB = b.getPosition().dehomog();
        a = new Vertex(new Point3D(transformToWindow(new Point3D(dA.get()))),a.getColor());
        b = new Vertex(new Point3D(transformToWindow(new Point3D(dB.get()))),b.getColor());

        int aX = (int) Math.round(a.getPosition().getX());
        int aY = (int) Math.round(a.getPosition().getY());

        int bX = (int) Math.round(b.getPosition().getX());
        int bY = (int) Math.round(b.getPosition().getY());

        var dy = bY - aY;
        var dx = bX - aX;

        if(Math.abs(dy) < Math.abs(dx)){
            if (bX < aX) {
                Vertex temp = a;
                a = b;
                b = temp;
            }

            for (int x = Math.max(0, (int) a.getPosition().getX() + 1); x <= Math.min(zb.getImageBuffer().getWidth() - 1, b.getPosition().getX()); x++) {
                double t1 = (x - a.getPosition().getX()) / (b.getPosition().getX() - a.getPosition().getX());
                Vertex d = a.mul(1 - t1).add(b.mul(t1));
                zb.setPixelWithZTest((int) d.getPosition().getX(), (int) d.getPosition().getY(), d.getPosition().getZ(), shader.shade(d));
            }
        }
        else{
            if (bY < aY) {
                Vertex temp = a;
                a = b;
                b = temp;
            }
            for (int y = Math.max(0, (int) a.getPosition().getY() + 1); y <= Math.min(zb.getImageBuffer().getHeight() - 1, b.getPosition().getY()); y++) {
                double t1 = (y - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
                Vertex d = a.mul(1 - t1).add(b.mul(t1));
                zb.setPixelWithZTest((int) d.getPosition().getX(), (int) d.getPosition().getY(), d.getPosition().getZ(), shader.shade(d));
            }
        }

    }

    @Override
    public Vec3D transformToWindow(Point3D pos) {
        return new Vec3D(pos)
                .mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(zb.getImageBuffer().getWidth() / 2f, zb.getImageBuffer().getHeight() / 2f, 1));
    }
}
