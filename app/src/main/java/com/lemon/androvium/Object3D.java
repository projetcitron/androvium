package com.lemon.androvium;

import android.opengl.GLES20;

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
    protected FloatBuffer vertexBuffer_, indexBuffer_;
    private int nbPoints_, stride_;
    final int buffers[] = new int[1];
    int bufferIndex_ = 0;

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

    protected void initVBO(float[] vertexData,float[] indexData){
        // Initialize the buffers.
        vertexBuffer_ = ByteBuffer.allocateDirect(vertexData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer_.put(vertexData).position(0);

        indexBuffer_ = ByteBuffer.allocateDirect(indexData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        indexBuffer_.put(indexData).position(0);
        nbPoints_ = vertexData.length/12;
        stride_ = 12 * 4;


        GLES20.glGenBuffers(1, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer_.capacity() * 4, vertexBuffer_, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        bufferIndex_ = buffers[0];

        vertexBuffer_.limit(0);
        vertexBuffer_ = null;

    }

    public FloatBuffer getPositions(){ return positions_;}
    public FloatBuffer getColors(){ return colors_;}
    public FloatBuffer getNormals(){ return normals_;}
    public FloatBuffer getTextureCoordinates(){ return textureCoordinates_;}

    public FloatBuffer getVertexBuffer(){return vertexBuffer_;}
    public FloatBuffer getIndexBuffer(){return indexBuffer_;}

    public int getBufferIndex(){return bufferIndex_;}
    public int getNbPoints(){return nbPoints_;}
    public int getStride()  {return stride_;  }
}
