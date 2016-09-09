package com.lemon.androvium;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by marc on 12/08/2016.
 */
public class Decors extends Object3D{
    public Decors(int x, int y, int z) {
        super();
        //     position           color                normals      texturesPosition
        final float[] vertexData = {
                //bottom
                -x,-y,z,    1.0f, 1.0f, 1.0f, 0.5f,   0, 1, 0,   0.5f, 0.0f,    //A
                x,-y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   0, 1, 0,   1.0f, 1.0f,    //C
                -x,-y,-z,   1.0f, 1.0f, 1.0f, 0.5f,   0, 1, 0,   0.5f, 1.0f,    //B
                -x,-y,z,    1.0f, 1.0f, 1.0f, 0.5f,   0, 1, 0,   0.5f, 0.0f,    //A
                x,-y,z,     1.0f, 1.0f, 1.0f, 0.5f,   0, 1, 0,   1.0f, 0.0f,    //D
                x,-y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   0, 1, 0,   1.0f, 1.0f,    //C
                //left
                -x,-y,z,    1.0f, 1.0f, 1.0f, 0.5f,   1, 0, 0,   0.0f, 0.0f,    //A
                -x,y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   1, 0, 0,   0.5f, 1.0f,    //F
                -x,y,z,     1.0f, 1.0f, 1.0f, 0.5f,   1, 0, 0,   0.0f, 1.0f,    //E
                -x,-y,z,    1.0f, 1.0f, 1.0f, 0.5f,   1, 0, 0,   0.0f, 0.0f,    //A
                -x,-y,-z,   1.0f, 1.0f, 1.0f, 0.5f,   1, 0, 0,   0.5f, 0.0f,    //B
                -x,y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   1, 0, 0,   0.5f, 1.0f,    //F
                //back
                -x,-y,-z,   1.0f, 1.0f, 1.0f, 0.5f,   0, 0, 1,   0.0f, 0.0f,    //B
                x,y,-z,     1.0f, 1.0f, 1.0f, 0.5f,   0, 0, 1,   0.5f, 1.0f,    //G
                -x,y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   0, 0, 1,   0.0f, 1.0f,    //F
                -x,-y,-z,   1.0f, 1.0f, 1.0f, 0.5f,   0, 0, 1,   0.0f, 0.0f,    //B
                x,-y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   0, 0, 1,   0.5f, 0.0f,    //C
                x,y,-z,     1.0f, 1.0f, 1.0f, 0.5f,   0, 0, 1,   0.5f, 1.0f,    //G
                //right
                x,-y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   -1, 0, 0,   0.0f, 0.0f,    //C
                x,y,z,      1.0f, 1.0f, 1.0f, 0.5f,   -1, 0, 0,   0.5f, 1.0f,    //H
                x,y,-z,     1.0f, 1.0f, 1.0f, 0.5f,   -1, 0, 0,   0.0f, 1.0f,    //G
                x,-y,-z,    1.0f, 1.0f, 1.0f, 0.5f,   -1, 0, 0,   0.0f, 0.0f,    //C
                x,-y,z,     1.0f, 1.0f, 1.0f, 0.5f,   -1, 0, 0,   0.5f, 0.0f,    //D
                x,y,z,      1.0f, 1.0f, 1.0f, 0.5f,   -1, 0, 0,   0.5f, 1.0f    //H
        };
        final float[] indexData = {};
        initVBO(vertexData, indexData);
    }
}
