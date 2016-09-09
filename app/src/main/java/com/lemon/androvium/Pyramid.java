package com.lemon.androvium;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by marc on 12/08/2016.
 */
public class Pyramid extends Object3D{

    public Pyramid(float x,float y, float z) {
        super();
        //     position           color                normals      texturesPosition
        final float[] vertexData = {
                0, 0, z,    1.0f, 0.0f, 0.0f, 0.5f,    0, -1, 0,    0.0f, 0.0f, //A  0
                0, 0, 0,    0.0f, 1.0f, 0.0f, 0.5f,    0, -1, 0,    0.0f, 1f,   //B  1
                x, 0, 0,    0.0f, 0.0f, 1.0f, 0.5f,    0, -1, 0,    1f, 1f,     //C  2

                0, 0, z,    1.0f, 0.0f, 0.0f, 0.5f,    0, -1, 0,    0.0f, 0.0f, //A  3
                0, y, 0,    1.0f, 1.0f, 0.0f, 0.5f,    0, -1, 0,    1f, 0f,     //D  4
                0, 0, 0,    0.0f, 1.0f, 0.0f, 0.5f,    0, -1, 0,    0.0f, 1f,   //B  5

                0, y, 0,    1.0f, 1.0f, 0.0f, 0.5f,    0, -1, 0,    1f, 0f,     //D  6
                x, 0, 0,    0.0f, 0.0f, 1.0f, 0.5f,    0, -1, 0,    1f, 1f,     //C  7
                0, 0, 0,    0.0f, 1.0f, 0.0f, 0.5f,    0, -1, 0,    0.0f, 1f,   //B  8

                0, y, 0,    1.0f, 1.0f, 0.0f, 0.5f,    0, -1, 0,    1f, 0f,     //D  9
                0, 0, z,    1.0f, 0.0f, 0.0f, 0.5f,    0, -1, 0,    0.0f, 0.0f, //A  10
                x, 0, 0,    0.0f, 0.0f, 1.0f, 0.5f,    0, -1, 0,    1f, 1f     //C  11
        };
        final float[] indexData = {
                0, 1, 2, //bottom
                0, 3, 1, //left
                3, 2, 1, //back
                3, 0, 2  //cutted
        };
        initVBO(vertexData, indexData);
    }
}
