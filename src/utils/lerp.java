package utils;

import Solid.Vectorizable;
import Solid.Vertex;

public class lerp<T extends Vectorizable<T>> {
    public T lerp(T v1, T v2, double t){
        return v1.mul(1 - t).add((Vertex) v2.mul(t));
    }
}
