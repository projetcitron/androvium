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

import java.nio.FloatBuffer;

public class MyGLRenderer implements GLSurfaceView.Renderer {
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
    /** Size of the color data in elements. */
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
    float eyeY = 2f;
    float eyeZ = 5f;

    float deltaAngleX = 0.0f, initialDeltaAngleX = 0.0f;
    float deltaAngleY = 0.0f, initialDeltaAngleY = 0.0f;
    float deltaAngleZ = 0.0f, initialDeltaAngleZ = 0.0f;
    float angleX = 0.0f;
    float angleY = 0.0f;
    float angleZ = 0.0f;
    float forward = 0.0f, initialForward = 0.0f;
    float lx, ly,lz;

    // We are looking toward the distance
    float lookX = 0.0f;
    float lookY = 0.0f;
    float lookZ = 0.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;



    /**
     * Initialize the model data.
     */
    public MyGLRenderer(final MainActivity mActivity) {
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
        initialDeltaAngleX =  mActivity_.mOrientationAngles[2];
        initialDeltaAngleY =  mActivity_.mOrientationAngles[0];
        initialDeltaAngleZ =  mActivity_.mOrientationAngles[1];
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
        final float far = 75.0f;

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
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        Matrix.setIdentityM(mModelMatrix, 0);
        drawObject(new Decors(2,2,2));
        //Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        GLES20.glUseProgram(mBlendProgramHandle);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mBlendProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mBlendProgramHandle, "u_MVMatrix");
        //mLightPosHandle = GLES20.glGetUniformLocation(mBlendProgramHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mBlendProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mBlendProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mBlendProgramHandle, "a_Normal");
        drawObject(new Decors(10,4,10));
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 2.0f, 0.5f);

        // No culling of back faces
        //GLES20.glDisable(GLES20.GL_CULL_FACE);
        // No depth testing
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        // Enable blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        drawObject(new Pyramid());
        GLES20.glDisable(GLES20.GL_BLEND);

        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.rotateM(mLightModelMatrix, 0, -angleInDegrees, 0.0f, 1.0f, 1.0f) ;
        Matrix.translateM(mLightModelMatrix, 0, 1.5f, 0.0f, 1.0f);
        //Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        // Draw a point to indicate the light.
        GLES20.glUseProgram(mPointProgramHandle);
        drawLight();


        mActivity_.updateOrientationAngles();
        angleX += 0.01 * (mActivity_.mOrientationAngles[2] - initialDeltaAngleX);
        if(angleX >= 6.400006)
            angleX = 0;
        forward = -0.05f * (mActivity_.mOrientationAngles[1] - initialDeltaAngleZ);

        //deltaAngleZ += 0.1 * mActivity_.mOrientationAngles[1] + 0.2;
        lx = (float)Math.sin(angleX);
        lz = (float)-Math.cos(angleX);
        eyeX -= forward * lx / (float)(Math.sqrt(lx*lx + lz*lz));
        eyeZ -= forward * lz / (float)(Math.sqrt(lx*lx + lz*lz));

        lookX = eyeX + lx;
        lookY = eyeY;
        lookZ = eyeZ + lz;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX , eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    /**
     * Draws an object
     */
    private void drawObject(Object3D object) {
        // Pass in the position information
        object.getPositions().position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, object.getPositions());
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Pass in the color information
        object.getColors().position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, 0, object.getColors());
        GLES20.glEnableVertexAttribArray(mColorHandle);
        // Pass in the normal information
        object.getNormals().position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 0, object.getNormals());
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        // Pass in the texture coordinate information
        object.getTextureCoordinates().position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, object.getTextureCoordinates());
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
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
}
