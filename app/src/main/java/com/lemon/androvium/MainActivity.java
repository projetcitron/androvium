package com.lemon.androvium;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioRecord;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener, View.OnTouchListener{
    /** Hold a reference to our GLSurfaceView */
    private GLSurfaceView mGLSurfaceView;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final float[] gravity = new float[3];
    private final float[] linear_acceleration = new float[3];


    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    public final float[] mRotationMatrix = new float[9];


    private AudioRecord audioInput = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    protected short sData[] = new short[1024];
    protected double sDataAverage=0;
    protected double fftData[]= new double[1024];
    protected int[] oldSentData=new int[3];

    private GLRenderer glRenderer_;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            glRenderer_ = new GLRenderer(this);
            mGLSurfaceView.setRenderer(glRenderer_);
        }
        else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Tam","down");
                /*time = System.currentTimeMillis();
                if ((time - lastTime) < MAX_DURATION_DOUBLE) {
                    return true;
                }
                lastTime = time;*/
                        glRenderer_.setSpeed(0.5f);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("Tam","up");
                        glRenderer_.setSpeed(0.00f);
                        return true;
                }
                return false;
            }
        });
        setContentView(mGLSurfaceView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        ////////////////////////////////////////////////////////////////////////////////////////////
        //                                  AudioRecord                                           //
        ////////////////////////////////////////////////////////////////////////////////////////////
/*        int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        int RECORDER_SAMPLERATE= 44100;

        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        audioInput = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
        audioInput.startRecording();
        isRecording = true;

        // Fast Fourier Transform from JTransforms
        final DoubleFFT_1D fft = new DoubleFFT_1D(sData.length);

        // Start recording

        recordingThread = new Thread(new Runnable() {
            public void run() {
                while (isRecording) {
                    // Record audio input
                    audioInput.read(sData, 0, sData.length);
                    // Convert and put sData short array into fftData double array to perform FFT
                    for (int j = 0; j < sData.length; j++)
                        fftData[j] = (double) sData[j];

                    // Perform 1D fft-(Math.cos(angleX) *
    //public final float[] mOrientationAngles = new float[3];
                    fft.realForward(fftData);
                    for (int j = 0; j < fftData.length; j++) fftData[j] = Math.abs(fftData[j]);



                    // Update plot //
                    for (int j = 0; j < series1.size(); j++) {
                        series1.removeFirst();
                        series1.addLast(null, fftData[j * powOf2temp] * powOf2temp);
                    }

                    sensBar = (SeekBar) findViewById(R.id.seekBar);
                    int sensBarProgress = sensBar.getProgress();
                    plot.setRangeBoundaries(0, (100 - ((sensBarProgress == 100) ? 99 : sensBarProgress)) * 1000, BoundaryMode.FIXED);
                    plot.redraw();

                    ////////////////

                    final int dataToSend[] = new int[3];
                    sensBarSmooth = (SeekBar) findViewById(R.id.seekBarSmooth);
                    int smoothness = sensBarSmooth.getProgress() + 192;

                    for (int freqDomain = 0; freqDomain < 3; freqDomain++) {
                        sDataAverage = 0;
                        for (int i = freqDomain * 1024 * 2 / 9; i < (freqDomain + 1) * 1024 * 2 / 9; i++)
                            sDataAverage += fftData[i]
    //public final float[] mOrientationAngles = new float[3];;
                        sDataAverage /= 1024 / (3 * (float) sensBarProgress / 500);

                        // Limit the value to 255
                        dataToSend[freqDomain] = (sDataAverage > 255) ? 255 : (int) sDataAverage;
                        // Limit the amplitude fall
                        dataToSend[freqDomain] =
                                (dataToSend[freqDomain] < oldSentData[freqDomain] * (1 - (float) (257 - smoothness) / 255)) ?
                                        (int) (oldSentData[freqDomain] * (1 - (float) (257 - smoothness) / 255)) :
                                        dataToSend[freqDomain];

                        oldSentData[freqDomain] = dat
    //public final float[] mOrientationAngles = new float[3];aToSend[freqDomain];
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = (TextView) findViewById(R.id.textView_debug);
                            tv.setText("Average amplitudes : " + String.valueOf(Arrays.toString(dataToSend)));

                            sendData("R" + dataToSend[0] + "G" + dataToSend[1] + "B" + dataToSend[2]);
                        }
                    });
                }
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        */
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.d("Tam","touch");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*time = System.currentTimeMillis();
                if ((time - lastTime) < MAX_DURATION_DOUBLE) {
                    return true;
                }
                lastTime = time;*/
                glRenderer_.setSpeed(0.05f);
                return true;

            case MotionEvent.ACTION_MOVE:
                return true;

            case MotionEvent.ACTION_UP:
                glRenderer_.setSpeed(0.00f);
                return true;
        }
        return false;
    }

        @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();


        HandlerThread mHandlerThread = new HandlerThread("sensorThread");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
         //       SensorManager.SENSOR_DELAY_FASTEST, handler);


        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, handler);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        // "mRotationMatrix" now has up-to-date information.

        float[] mOrientationAngles = new float[3];
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        glRenderer_.setOrientation( mOrientationAngles);


        // "mOrientationAngles" now has up-to-date information.
        //Log.d("SENSOR", "Rotation: "+mRotationMatrix[0]+"  "+mRotationMatrix[1]+"  "+mRotationMatrix[2]+"  "+mRotationMatrix[3]+"  "
        //        +mRotationMatrix[4]+"  "+mRotationMatrix[5]+"  "+mRotationMatrix[6]+"  "+mRotationMatrix[7]+"  "+mRotationMatrix[8]);
        //Log.d("SENSOR", "Rotation: "+mOrientationAngles[0]+"    "+mOrientationAngles[1]+"     "+mOrientationAngles[2]);
    }






}