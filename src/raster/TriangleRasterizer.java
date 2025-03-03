package raster;

import Solid.Vertex;
import Zbuffer.ZBuffer;
import transforms.Col;
import transforms.Point3D;
@@ -11,34 +12,107 @@ public TriangleRasterizer(ZBuffer zb){
    this.zb = zb;
}

public void rasterize(Point3D a, Point3D b, Point3D c, Col color){
    public void rasterize(Vertex a, Vertex b, Vertex c, Col color){
        int aX = (int) Math.round(a.getPosition().getX());
        int aY = (int) Math.round(a.getPosition().getY());
        double aZ = a.getPosition().getZ();

        int bX = (int) Math.round(b.getPosition().getX());
        int bY = (int) Math.round(b.getPosition().getY());
        double bZ = b.getPosition().getZ();

        int aX = (int) Math.round(a.getX());
        int aY = (int) Math.round(a.getY());

        int bX = (int) Math.round(b.getX());
        int bY = (int) Math.round(b.getY());

        int cX = (int) Math.round(c.getX());
        int cY = (int) Math.round(c.getY());
        int cX = (int) Math.round(c.getPosition().getX());
        int cY = (int) Math.round(c.getPosition().getY());
        double cZ = b.getPosition().getZ();

        zb.getImageBuffer().getImg().getGraphics().drawLine(aX,aY,bX,bY);
        zb.getImageBuffer().getImg().getGraphics().drawLine(bX,bY,cX,cY);
        zb.getImageBuffer().getImg().getGraphics().drawLine(aX,aY,cX,cY);

        // seřadit body podle y
        //
        if(bY < aY) //pokud B je menší jak A
        {
            int tempX = bX;
            int tempY = bY;
            double tempZ = bZ;
            bY = aY;
            bX = aX;
            bZ = aZ;
            aX = tempX;
            aY = tempY;
            aZ = tempZ;
        }
        if(cY < aY) //pokud C je menší jak A
        {
            int tempX = cX;
            int tempY = cY;
            double tempZ = cZ;
            cY = aY;
            cX = aX;
            cZ = aZ;
            aX = tempX;
            aY = tempY;
            aZ = tempZ;
        }
        if(cY < bY) //pokud C je menší jak B
        {
            int tempX = cX;
            int tempY = cY;
            double tempZ = cZ;
            cY = bY;
            cX = bX;
            cZ = bZ;
            bX = tempX;
            bY = tempY;
            bZ = tempZ;
        }

        //prvni cast
        for(int y = aY; y <=bY; y++){
            //hrana AB
            double tAB = (y - aY) / (double) (bY - aY);
            int x1 =(int)Math.round ((1 - tAB) * aX + tAB * bX);
            double t2 = (y-aY)/(double)(cY-aY);
            int x2 = (int)Math.round ((1 - t2) * aX + t2 * cX);
            double z1 = (1-tAB) * aZ + tAB * bZ;

            Vertex vAB = a.mul(1-tAB).add(b.mul(tAB));

            //hrana AC
            double tAC = (y-aY)/(double)(cY-aY);

            int x2 = (int)Math.round ((1 - tAC) * aX + tAC * cX);
            double z2 = (1-tAC) * aZ + tAC * cZ;

            Vertex vAC = a.mul(1-tAC).add(c.mul(tAC));
            //Triangle vs Triangle-strip
            for(int x = x1; x <= x2; x++){
                zb.setPixelWithZTest(x,y,0.5d, color);
                double tZ = (x - x1) / (double) (x2-x1);
                double z = (1-tZ) * z1 + tZ * z2;
                zb.setPixelWithZTest(x,y,z, color);
            }
        }
        //zobrazovaci retezec, souradnice, kamery, Java, Rasterizace trojuhelniku, interpolace, zbuffer, Vertex, part buffer.

        //druha cast
        for(int y = bY; y <= cY; y++) {
            //hrana BC
            double tBC = (y - bY) / (double) (cY - bY);
            int x1 = (int) Math.round((1 - tBC) * bX + tBC * cX);
            double z1 = (1-tBC) * bZ + tBC * cZ;
            //hrana AC
            double tAC = (y - aY) / (double) (cY - aY);
            int x2 = (int) Math.round((1 - tAC) * aX + tAC * cX);
            double z2 = (1-tAC) * aZ + tAC * cZ;

            if (x1 > x2) {
                int tempX = x1;
                x1 = x2;
                x2 = tempX;
            }

            for (int x = x1; x <= x2; x++) {
                double tZ = (x - x1) / (double) (x2-x1);
                double z = (1-tZ) * z1 + tZ * z2;
                zb.setPixelWithZTest(x, y, z, color);
            }
        }
    }
}