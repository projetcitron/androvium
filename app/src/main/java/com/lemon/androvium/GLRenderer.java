package com.lemon.androvium;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.nio.FloatBuffer;

public class GLRenderer implements GLSurfaceView.Renderer{
    private static final String TAG = "blending";
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];
    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];
    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];
    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];
    /** Store our model data in a float buffer. */

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;
    /** This will be used to pass in the modelview matrix. */
    private int mMVMatrixHandle;
    /** This will be used to pass in the light position. */
    private int mLightPosHandle;
    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;
    /** This will be used to pass in model position information. */
    private int mPositionHandle;
    /** This will be used to pass in model color information. */
    private int mColorHandle;
    /** This will be used to pass in model normal information. */
    private int mNormalHandle;
    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;
    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;
    /** Size of the color data         drawVBObject(pyramid);
in elements. */
    private final int mColorDataSize = 4;
    /** Size of the normal data in elements. */
    private final int mNormalDataSize = 3;
    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    private final float[] mLightPosInWorldSpace = new float[4];
    /** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
    private final float[] mLightPosInEyeSpace = new float[4];
    /** This is a handle to our per-vertex cube shading program. */
    //private int mPerVertexProgramHandle;
    /** This is a handle to our light point program. */
    private int mPointProgramHandle;
    /** This is a handle to our cube shading program. */
    private int mProgramHandle, mBlendProgramHandle;
    /** This is a handle to our texture data. */
    private int mTextureDataHandle;
    private Context mActivityContext;
    private MainActivity mActivity_;

    float eyeX = 0.0f;
    float eyeY = 0f;
    float eyeZ = 90f;

    float deltaAngleX = 0.0f, initialDeltaAngleX = 0.0f;
    float deltaAngleY = 0.0f, initialDeltaAngleY = 0.0f;
    float deltaAngleZ = 0.0f, initialDeltaAngleZ = 0.0f;
    float angleX = 0.0f;
    float angleY = 0.0f;
    float angleZ = 0.0f;
    float speed_ = 0.0f;
    float lx, ly,lz;

    // We are looking toward the distance
    float lookX = 0.0f;
    float lookY = 0.0f;
    float lookZ = 0.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    float upX = 0.0f;
    float upY = 1.0f;
    float upZ = 0.0f;

    private Object3D pyramid, decors, box;


    /**
     * Initialize the model data.
     */
    public GLRenderer(final MainActivity mActivity) {
        mActivityContext = mActivity;
        mActivity_ = mActivity;
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);// Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);// Enable depth testing

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
        // Enable texture mapping
        // GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        //SHADERS TEXTURES
        int vertexShaderHandle = ShadersTools.compileShader(GLES20.GL_VERTEX_SHADER, Shaders.getVertexShader());
        int fragmentShaderHandle =  ShadersTools.compileShader(GLES20.GL_FRAGMENT_SHADER, Shaders.getFragmentShader());
        mProgramHandle =  ShadersTools.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});
        // Load the texture
        mTextureDataHandle = ShadersTools.loadTexture(mActivityContext, R.drawable.bricks);
        //SHADER BLENDING
        vertexShaderHandle = ShadersTools.compileShader(GLES20.GL_VERTEX_SHADER, Shaders.getBlendVertexShader());
        fragmentShaderHandle =  ShadersTools.compileShader(GLES20.GL_FRAGMENT_SHADER, Shaders.getBlendFragmentShader());
        mBlendProgramHandle =  ShadersTools.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color", "a_Normal"});

        //SHADERS POINT
        final int pointVertexShaderHandle =  ShadersTools.compileShader(GLES20.GL_VERTEX_SHADER, Shaders.getPointVertexShader());
        final int pointFragmentShaderHandle =  ShadersTools.compileShader(GLES20.GL_FRAGMENT_SHADER, Shaders.getPointFragmentShader());
        mPointProgramHandle =  ShadersTools.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle, new String[] {"a_Position"});
        mActivity_.updateOrientationAngles();
        initialDeltaAngleX =  deltaAngleX;
        initialDeltaAngleY =  deltaAngleY;
        initialDeltaAngleZ =  deltaAngleZ;
        pyramid = new Pyramid(10,10,10);
        decors = new Decors(2,2,2);
        box = new Box(100,100,100);
    }



    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 300.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
       GLES20.glUseProgram(mProgramHandle);
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // Set the active texture unit to texture unit 0.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle); // Bind the texture to this unit.
        GLES20.glUniform1i(mTextureUniformHandle, 0); // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.

        Matrix.setIdentityM(mModelMatrix, 0);
        //drawVBObject(decors);
        drawVBObject(box);



        GLES20.glUseProgram(mBlendProgramHandle);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mBlendProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mBlendProgramHandle, "u_MVMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mBlendProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mBlendProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mBlendProgramHandle, "a_Normal");


        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 2.0f, 0.5f);


        // No culling of back faces
        //GLES20.glDisable(GLES20.GL_CULL_FACE);
        // No depth testing
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        // Enable blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.2f, 0.5f, 0.7f);
        drawVBObject(pyramid);

        GLES20.glDisable(GLES20.GL_BLEND);


        Matrix.setIdentityM(mLightModelMatrix, 0);
        //Matrix.rotateM(mLightModelMatrix, 0, -angleInDegrees, 0.0f, 1.0f, 1.0f) ;
        //Matrix.translateM(mLightModelMatrix, 0, 1.5f, 0.0f, 1.0f);
        //Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        // Draw a point to indicate the light.
        GLES20.glUseProgram(mPointProgramHandle);


        for(float i = -30; i<=30; i+=10) {
            for(float j = -30; j<=30; j+=10) {
                for (float k = -30; k <= 30; k += 10) {
                    Matrix.setIdentityM(mLightModelMatrix, 0);
                    Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, k, j, i);
                    Matrix.translateM(mLightModelMatrix, 0, i, j, k);
                    drawLight();
                }
            }
        }
        float angle360 = 6.24f;
        float angle180 = angle360/2;
        float angle90 = angle360/4;

        mActivity_.updateOrientationAngles();
        angleY += 0.01 * (deltaAngleY - initialDeltaAngleY);
        if(angleY >= angle360)
            angleY = 0;

        angleX += 0.05f * (deltaAngleX - initialDeltaAngleX);
        if(angleX >= angle360)
            angleX = 0;

        //speed_ = 0.0f;//0.5f;//-0.01f * (deltaAngleX - initialDeltaAngleX);
        //deltaAngleZ += 0.1 * mActivity_.mOrientationAngles[1] + 0.2;
        lx = (float) (Math.cos(angleX) * Math.sin(angleY) );
        ly = (float) -Math.sin(angleX);
        lz = (float)-(Math.cos(angleX) * Math.cos(angleY) );

        eyeX += speed_ * lx;
        eyeY += speed_ * ly;
        eyeZ += speed_ * lz;


        lookX = eyeX + lx;// - ly;
        lookY = eyeY + ly;
        lookZ = eyeZ + lz;// - ly;

        upX = (float) (Math.cos(angleX - angle90) * Math.sin(angleY) );
        upY = (float) -Math.sin(angleX - angle90);
        upZ = (float) -(Math.cos(angleX - angle90) * Math.cos(angleY) );

        Matrix.setLookAtM(mViewMatrix, 0, eyeX , eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }


    /**
     * Draws an Vertex Buffer Object
     */
    private void drawVBObject(Object3D object) {
        final int stride = object.getStride();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, object.getBufferIndex());
// Pass in the position information
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, stride, 0);

// Pass in the color information
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, stride, mPositionDataSize * 4);

// Pass in the normal information
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, stride,
                (mPositionDataSize + mColorDataSize) * 4);

// Pass in the textures information
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, stride,
                (mPositionDataSize + mColorDataSize + mNormalDataSize) * 4);


        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Pass in the light position in eye space.
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
        // Draw the object
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);     //drawVBObject(decors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, object.getNbPoints());
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight() {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");
        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);
        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }


    public void setOrientation( float[] orientation){
        deltaAngleX = orientation[1];
        deltaAngleY = orientation[2];
        deltaAngleZ = orientation[0];
    }


    public void setSpeed( float speed){
        speed_= speed;
    }
}
