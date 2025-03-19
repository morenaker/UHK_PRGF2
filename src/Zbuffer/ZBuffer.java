package Zbuffer;
import raster.DepthBuffer;
import raster.ImageBuffer;
import raster.Raster;
import transforms.Col;

public class ZBuffer {
    private final Raster<Double> depthBuffer;
    private final Raster<Col> imageBuffer;

    public ZBuffer(Raster<Col> imageBuffer){
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        double val = depthBuffer.getValue(x,y);
        if(z < val){
            imageBuffer.setValue(x,y,color);
            depthBuffer.setValue(x,y,z);
        }
    }
    public void clear()
    {
        depthBuffer.clear();
    }
    public ImageBuffer getImageBuffer(){
        return (ImageBuffer) imageBuffer;
    }
}
