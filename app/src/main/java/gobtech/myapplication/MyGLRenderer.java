package gobtech.myapplication;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Gabe K on 7/15/2016.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    int mProgram;
    float width, height;
    long startTime;

    FloatBuffer vertexBuffer;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
        mProgram = getProgram();

        startTime = System.currentTimeMillis();



        float [] verticesData = new float [] {-1,1,1,1,-1,-1,1,-1};
        //4 bytes per float
        vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(verticesData).position(0);




//        int programHandle = getProgram();
//        int mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
//        GLES20.glUseProgram(programHandle);
//
//

//        final int vertexBufferIdx;
//        final int buffers[] = new int[1];
//        GLES20.glGenBuffers(1, buffers, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//
//        vertexBufferIdx = buffers[0];
//
//
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferIdx);
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPos");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 2,
                GLES20.GL_FLOAT, false,
                2 * 4, vertexBuffer);



        int mResHandle = GLES20.glGetUniformLocation(mProgram, "uRes");

        GLES20.glUniform2fv(mResHandle, 1, new float[] {width, height}, 0);

        int mTimeHandle = GLES20.glGetUniformLocation(mProgram, "uTime");

        GLES20.glUniform1f(mTimeHandle, (float) (System.currentTimeMillis() - startTime)/1000);
        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 6);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public int getProgram() {

        final String vertexShader =
                "attribute vec2 aPos;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    gl_Position = vec4(aPos, 0.0, 1.0);\n" +
                        "}";

        final String fragmentShader =
                "uniform vec2 uRes;\n" +
                        "uniform float uTime;\n" +
                        "\n" +
                        "float map(vec3 p) {\n" +
                        "\tvec3 q = fract(p)  * 2.0 - 1.0;\n" +
                        "\t\n" +
                        "    return length(q) - .25;\n" +
                        "}\n" +
                        "\n" +
                        "float trace(vec3 o, vec3 r) {\n" +
                        "    float t = 0.0;\n" +
                        "    for (int i =0; i < 32; ++i) {\n" +
                        "        vec3 p = o + r * t;\n" +
                        "\t\tfloat d = map(p);\n" +
                        "\t\tt += d * .5;\n" +
                        "\t}\n" +
                        "\treturn t;\n" +
                        "}\n" +
                        "\n" +
                        "void main() {\n" +
                        "\tvec2 uv = gl_FragCoord.xy / uRes.xy;\n" +
                        "\tuv = uv * 2.0 - 1.0;\n" +
                        "\t\n" +
                        "\t\n" +
                        "\tuv.x *= uRes.x / uRes.y;\n" +
                        "\t\n" +
                        "\tvec3 r = normalize(vec3(uv, 1.0));\n" +
                        "\t\n" +
                        "\t//float the = uTime * .5;\n" +
                        "\t//r.xz *= mat2(cos(the), -sin(the), sin(the), cos(the));\n" +
                        "\tvec3 o = vec3(-cos(uTime), sin(uTime), uTime * uTime * .1);\n" +
                        "\t//vec3 o = vec3(0.0, 0.0, -1.0);\n" +
                        "\tfloat t = trace(o, r);\n" +
                        "\t\n" +
                        "\tfloat fog = 1.0 / (1.0 + t * t * .1);\n" +
                        "\tvec3 fc = vec3(fog);\n" +
                        "\tgl_FragColor = vec4(fc.x, fc.x, fc.y, 1.0);\n" +
                        "}";



        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }


        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0) {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "aPos");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;

    }
}
