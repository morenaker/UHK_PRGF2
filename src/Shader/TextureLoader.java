package Shader;

import transforms.Col;
import transforms.Vec2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureLoader {
    private BufferedImage image;
    public TextureLoader(){
        try {
            image = ImageIO.read(new File("./src/texture.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Col getCol(int x,int y) {
      x = x * image.getWidth();
      y = y * image.getHeight();
      return new Col(image.getRGB(x,y));
    }
}
