package Shader;

import Solid.Vertex;
import transforms.Col;

public class InterShade  implements  Shader{
    @Override
    public Col shade(Vertex v){
        return v.getColor();
    }
}
