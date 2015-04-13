package com.example.zach.laststand;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";

    private Map mTriangle;
    public Player   mPlayer;

    //private Map mMap = new Map(0);


    ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    ArrayList<Obstacle> ground = new ArrayList<Obstacle>();

    int mapNum = 0;

    int[][][] levels = {
            {
                    {-2, 0}, {-3, 0}
            }
    };
   /* int[][][][] levels = {
     /*levels  /*level  /*obstacles  /*x,y
               {
                          {
                            /*ground     {-0, 0}, {-3, 0}
                          }
               }
    };*/


    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private float[] mTempMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];



    public static float gravity =  -0.01f;

    long startTime = System.nanoTime();
    private float frames = 0;
    public float fps = 60;
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mPlayer   = new Player(this);

        for(int i = 0; i < levels[mapNum].length; i++){
            obstacles.add(new Obstacle((float) levels[mapNum][i][0],(float) levels[mapNum][i][1]));
           // ground.add(new Obstacle((float) levels[mapNum][0][i][0],(float) levels[mapNum][0][i][1]));
        }

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        //update all objects
        mPlayer.update();



        float[] scratch = new float[16];

        mTempMatrix = mMVPMatrix.clone();
        Matrix.multiplyMM(scratch, 0, mTempMatrix, 0, mModelMatrix, 0);

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, -mPlayer.posX, 0, -2, -mPlayer.posX, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        /** Draw Player **/
        //mModelMatrix = mBaseMatrix.clone();
        Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity matrix
        Matrix.translateM(mModelMatrix, 0, -mPlayer.posX, mPlayer.posY, 0); // translation to the player position

        Matrix.setRotateM(mRotationMatrix, 0, mPlayer.mAngle, 0, 0, 1.0f);

        mTempMatrix = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, mTempMatrix, 0, mRotationMatrix, 0);

        mTempMatrix = mMVPMatrix.clone();
        Matrix.multiplyMM(scratch, 0, mTempMatrix, 0, mModelMatrix, 0);
        // Draw square
        mPlayer.draw(scratch);

       for(int i = 0; i < obstacles.size(); i++) {
           Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity matrix
           Matrix.translateM(mModelMatrix, 0, -mPlayer.posX, mPlayer.posY, 0); // translation to the player position

           mTempMatrix = mMVPMatrix.clone();
           Matrix.multiplyMM(scratch, 0, mTempMatrix, 0, mModelMatrix, 0);

           obstacles.get(i).draw(scratch);
       }
       for(int i = 0; i < ground.size(); i++){
           Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity matrix
           Matrix.translateM(mModelMatrix, 0, -mPlayer.posX, mPlayer.posY, 0); // translation to the player position

           mTempMatrix = mMVPMatrix.clone();
           Matrix.multiplyMM(scratch, 0, mTempMatrix, 0, mModelMatrix, 0);

           ground.get(i).draw(mModelMatrix);
       }




        //fps stuff
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            //Log.d("FPSCounter", "fps: " + frames);
            fps = frames;
            frames = 0;

            startTime = System.nanoTime();
        }

        /*
       // mModelMatrix = mBaseMatrix.clone();
        Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity matrix
        Matrix.translateM(mModelMatrix, 0, 0.5f, 0, 0); // translation to the left

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

        mTempMatrix = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, mTempMatrix, 0, mRotationMatrix, 0);

        mTempMatrix = mMVPMatrix.clone();
        Matrix.multiplyMM(scratch, 0, mTempMatrix, 0, mModelMatrix, 0);
        // Draw square
        mTriangle.draw(scratch);*/
        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

       // Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
      //  Matrix.translateM(mTempTranslationMatrix, 0, mMVPMatrix, 0, -.5f, 0, 0);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //Matrix.multiplyMM(scratch, 0, mTempTranslationMatrix, 0, mRotationMatrix, 0);
       // Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, scratch, 0);

        // Draw triangle
      //  mTriangle.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / (float) height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
   // public float getAngle() {
     //   return mAngle;
   // }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
   // public void setAngle(float angle) {
      //  mAngle = angle;
    //}

}