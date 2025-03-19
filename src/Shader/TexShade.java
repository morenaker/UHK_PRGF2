package Shader;

import Solid.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class TexShade implements Shader {
    private static TextureLoader texLoader = new TextureLoader();
    @Override
    public Col shade(Vertex v){
        int x = (int) (v.getTexCoords().getX()); //width *
        int y = (int) (v.getTexCoords().getY()); //height *
        return texLoader.getCol(x,y);
    }
}
