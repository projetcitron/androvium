package com.lemon.androvium;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by marc on 12/08/2016.
 */
public class Pyramid extends Object3D{
    final float[] positionData = {
            //pyramid
            //  X  Y  Z
            //bottom - red
            0, 0, 1, //A
            0, 0, 0,  //B
            1, 0, 0,  //C
            //left - yellow
            0, 0, 1, //A
            0, 1, 0,  //D
            0, 0, 0,  //B
            //back - green
            0, 1, 0,  //D
            1, 0, 0,  //C
            0, 0, 0, //B
            //cutted -blue
            0, 1, 0,  //D
            0, 0, 1,  //A
            1, 0, 0  //C
    };

    // R, G, B, A
    final float[] colorData = {
            //bottom - red
            1.0f, 0.0f, 0.0f, 0.5f,//A
            0.0f, 1.0f, 0.0f, 0.5f,//B
            0.0f, 0.0f, 1.0f, 0.5f,//C
            //left - yellow
            1.0f, 0.0f, 0.0f, 0.5f,//A
            1.0f, 1.0f, 0.0f, 0.5f,//D
            0.0f, 1.0f, 0.0f, 0.5f,//B
            //back - green
            1.0f, 1.0f, 0.0f, 0.5f,//D
            0.0f, 0.0f, 1.0f, 0.5f,//C
            0.0f, 1.0f, 0.0f, 0.5f,//B
            //cutted - blue
            1.0f, 1.0f, 0.0f, 0.5f, //D
            1.0f, 0.0f, 0.0f, 0.5f, //A
            0.0f, 0.0f, 1.0f, 0.5f  //C
    };

    // X, Y, Z
    // The normal is used in light calculations and is a vector which points
    // orthogonal to the plane of the surface. For a cube model, the normals
    // should be orthogonal to the points of each face.
    final float[] normalData = {
            //bottom - red
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            //left - yellow
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            //back - green
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            //cutted - blue
            1, 1, 1,
            1, 1, 1,
            1, 1, 1
    };

    final float[] textureCoordinateData = {
            // Bottom face
            0.0f, 0.0f,
            0.0f, 1f,
            0.5f, 1.0f,
            // left
            0.0f, 0.0f,
            0.0f, 1f,
            0.5f, 1f,
            // back
            0.0f, 1f,
            0.5f, 0.0f,
            0.0f, 0.0f,
            // cutted
            0.25f, 1f,
            0.0f, 0.0f,
            0.5f, 0.0f,
    };

    public Pyramid() {
        super();
        init(positionData, colorData, normalData, textureCoordinateData);
    }

    public FloatBuffer getPositions(){ return positions_;}
    public FloatBuffer getColors(){ return colors_;}
    public FloatBuffer getNormals(){ return normals_;}
    public FloatBuffer getTextureCoordinates(){ return textureCoordinates_;}

}
