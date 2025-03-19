package Shader;

import Solid.Vertex;
import transforms.Col;

@FunctionalInterface
public interface Shader {
   Col shade(Vertex v);
}
