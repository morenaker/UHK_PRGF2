package raster;

import transforms.Point3D;
import transforms.Vec3D;

public interface Rasterizer {
    public Vec3D transformToWindow(Point3D pos);
}
