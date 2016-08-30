package com.lemon.androvium;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//NON-INSTANCIABLE CLASS OBJECT3D
public class Object3D {
    public static final int PYRAMID = 0;
    public static final int DECORS = 1;
    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;
    protected FloatBuffer positions_, colors_, normals_, textureCoordinates_;
    private int nbPoints_;

    Object3D(){}
    protected void init(float[] positionData,float[] colorData,float[] normalData,float[] textureCoordinateData){
        // Initialize the buffers.
        positions_ = ByteBuffer.allocateDirect(positionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        positions_.put(positionData).position(0);

        colors_ = ByteBuffer.allocateDirect(colorData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colors_.put(colorData).position(0);

        normals_ = ByteBuffer.allocateDirect(normalData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normals_.put(normalData).position(0);

        textureCoordinates_ = ByteBuffer.allocateDirect(textureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinates_.put(textureCoordinateData).position(0);
        nbPoints_ = positionData.length/3;
    }

    public FloatBuffer getPositions(){ return positions_;}
    public FloatBuffer getColors(){ return colors_;}
    public FloatBuffer getNormals(){ return normals_;}
    public FloatBuffer getTextureCoordinates(){ return textureCoordinates_;}
    public int getNbPoints(){ return nbPoints_;}
}
