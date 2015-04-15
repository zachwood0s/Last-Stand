/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.zach.laststand;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Player {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_TexCoordinate;"+
            "varying vec2 v_TexCoordinate;"+
            "void main() {" +
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_TexCoordinate = a_TexCoordinate;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D u_Texture;"+
            "varying vec2 v_TexCoordinate;"+
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor * texture2D(u_Texture, v_TexCoordinate);" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private MyGLRenderer game;
    //player controls
    public static float width = .8f;
    public static float height = .8f;
    public float posX = 0;
    public float posY = -14;
    public float mAngle;

    private float velX = 0;
    private float velY = 0;
    private float jumpHeight = 1;
    private float jumpWidth = 1;
    //private boolean jumping = false;
    private float jumpTime = 0;


    private boolean grounded = true;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
   /* static float squareCoords[] = {
            -width/2,  height/2, 0.0f,   // top left
            -width/2, -height/2, 0.0f,   // bottom left
            width/2, -height/2, 0.0f,   // bottom right
            width/2,  height/2, 0.0f }; // top right*/
   static float squareCoords[] = {
           -width/2,  height/2, 0.0f,
           -width/2, -height/2, 0.0f,
           width/2, height/2, 0.0f,
           -width/2,  -height/2, 0.0f,
           width/2, -height/2, 0.0f,
           width/2,  height/2, 0.0f, };
    private final short drawOrder[] = { 0, 1, 2, 3, 4, 5 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 1f, 1f, 1f, 1.0f };


    private final int mBytesPerFloat = 4;

    /** Store our model data in a float buffer. */
    private final FloatBuffer mCubeTextureCoordinates;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Player(MyGLRenderer parent) {
        game = parent;


        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);




        final float[] cubeTextureCoordinateData =
            {
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
            };
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);




        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        mTextureDataHandle = game.loadTexture(game.mActivityContext, R.drawable.ic_launcher);
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */

    public void draw(float[] mvpMatrix) {

       // Matrix.translateM(mTempTranslationMatrix, 0, mMVPMatrix, 0, -.5f, 0, 0);

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);



        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    public void update(){
        velY += game.gravity;



        grounded = false;

        handleCollision();

        //Simple Respawn
        if(posY < game.cameraCenter+game.cameraDist-height){
            posY = game.cameraCenter;
            posX = 0;
            velX = 0;
            velY = 0;
        }
        if(!grounded){
            int multFac = (velX<0)?1:-1;
            mAngle += multFac*(360/jumpTime);
        }
        else{
            velY = 0;
            velX = 0;
            mAngle = 0;
        }


        posX += velX;
        posY += velY;
    }
    public void jump(String direction) {
        if(grounded) {
           // velY = .1f;
            grounded = false;

           // mAngle
            if (direction == "R") {
               // velX = .2f;
                velY = (float) Math.sqrt(-2*game.gravity*jumpHeight*2);

                jumpTime= 2*velY/game.gravity;

                velX = -(jumpWidth*2)/jumpTime;
            } else {
               // velX = -.1f;
                velY = (float) Math.sqrt(-2*game.gravity*jumpHeight);

                jumpTime= 2*velY/game.gravity;

                velX = jumpWidth/jumpTime;
            }
        }
    }
    private String checkCollision(Obstacle obj){
        float vX = (this.posX+ this.velX) - (obj.x);
        float vY = (this.posY+ this.velY)- (obj.y);

        float hWidths = (this.width / 2) + (obj.width / 2);
        float hHeights = (this.height / 2) + (obj.height / 2);

        String colDir = "";


        if (Math.abs(vX) < hWidths && Math.abs(vY) < hHeights){

            float oX = hWidths - Math.abs(vX);
            float oY = hHeights - Math.abs(vY);

            if (oX >= oY) {

                if (vY > 0) {
                    colDir = "t";
                    //a->position.y = b->position.y + b->height / 2 + a->height / 2;
                    //a->position.y += oY;
                }
                else {
                    colDir = "b";
                    //a->position.y = b->position.y - b->height / 2 - a->height / 2;
                    //a->position.y -= oY;
                }
            }
            else {
                if (vX > 0) {
                    colDir = "l";
                    //a->position.x = b->position.x + b->width / 2 + a->width / 2;
                    //a->position.x += oX;
                }
                else {
                    colDir = "r";
                    //a->position.x = b->position.x - b->width / 2 - a->width / 2;
                    //a->position.x -= oX;
                }
            }
        }
        return colDir;
    }
    private void handleCollision(){
        for(int i = 0; i<game.ground.size(); i++){
            String dir = checkCollision(game.ground.get(i));

            if(dir == "t"){
                posY = game.ground.get(i).y + game.ground.get(i).height / 2 + height / 2;
                posX = game.ground.get(i).x;
                grounded = true;
                //jumping = false;
               // Log.d("collisions", "dir: HIT");
            }


        }
    }

}