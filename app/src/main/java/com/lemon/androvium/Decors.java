package com.lemon.androvium;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by marc on 12/08/2016.
 */
public class Decors extends Object3D{


// R, G, B, A
final float[] colorData = {
        //DECORS:
        //bottom
        0.0f, 1.0f, 1.0f, 0.5f,
        0.0f, 1.0f, 1.0f, 0.5f,
        0.0f, 1.0f, 1.0f, 0.5f,
        0.0f, 1.0f, 1.0f, 0.5f,
        0.0f, 1.0f, 1.0f, 0.5f,
        0.0f, 1.0f, 1.0f, 0.5f,
        //left
        1.0f, 1.0f, 1.0f, 0.1f,
        1.0f, 1.0f, 1.0f, 0.1f,
        1.0f, 1.0f, 1.0f, 0.1f,
        1.0f, 1.0f, 1.0f, 0.1f,
        1.0f, 1.0f, 1.0f, 0.1f,
        1.0f, 1.0f, 1.0f, 0.1f,
        //back
        1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 1.0f,
        //right
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f
        };

// X, Y, Z
// The normal is used in light calculations and is a vector which points
// orthogonal to the plane of the surface. For a cube model, the normals
// should be orthogonal to the points of each face.
final float[] normalData = {
        //DECORS:
        //bottom
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,
        //left
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        //back
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        //right
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0
        };

// S, T (or X, Y)
// Texture coordinate data.
// Because images have a Y axis pointing downward (values increase as you move down the image) while
// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
// What's more is that the texture coordinates are the same for every face.
final float[] textureCoordinateData = {
        // Bottom
        0.5f, 0.0f,
        1.0f, 1.0f,
        0.5f, 1.0f,
        0.5f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        // Left
        0.0f, 0.0f,
        0.5f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        0.5f, 0.0f,
        0.5f, 1.0f,
        // Back
        0.0f, 0.0f,
        0.5f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        0.5f, 0.0f,
        0.5f, 1.0f,
        // Right
        0.0f, 0.0f,
        0.5f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        0.5f, 0.0f,
        0.5f, 1.0f
        };

    public Decors(int x, int y, int z) {
        super();
        // X, Y, Z
        final float[] positionData = {
                //bottom
                -x,-y,z,    //A
                x,-y,-z,    //C
                -x,-y,-z,   //B
                -x,-y,z,    //A
                x,-y,z,     //D
                x,-y,-z,    //C
                //left
                -x,-y,z,    //A
                -x,y,-z,    //F
                -x,y,z,     //E
                -x,-y,z,    //A
                -x,-y,-z,   //B
                -x,y,-z,    //F
                //back
                -x,-y,-z,   //B
                x,y,-z,     //G
                -x,y,-z,    //F
                -x,-y,-z,   //B
                x,-y,-z,    //C
                x,y,-z,     //G
                //right
                x,-y,-z,    //C
                x,y,z,      //H
                x,y,-z,     //G
                x,-y,-z,    //C
                x,-y,z,     //D
                x,y,z      //H
        };
        init(positionData, colorData, normalData, textureCoordinateData);
    }

    public FloatBuffer getPositions(){ return positions_;}
    public FloatBuffer getColors(){ return colors_;}
    public FloatBuffer getNormals(){ return normals_;}
    public FloatBuffer getTextureCoordinates(){ return textureCoordinates_;}

}
