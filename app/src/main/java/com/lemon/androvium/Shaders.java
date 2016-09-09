package com.lemon.androvium;

/**
 * Created by marc on 12/08/2016.
 */
public class Shaders {

    public static String getVertexShader() {
        // Define our per-pixel lighting shader.
        final String perPixelVertexShader =
                "uniform mat4 u_MVPMatrix;          \n"     // A constant representing the combined model/view/projection matrix.
                        + "uniform mat4 u_MVMatrix;         \n"	    // A constant representing the combined model/view matrix.
                        + "attribute vec2 a_TexCoordinate;  \n"     // Per-vertex texture coordinate information we will pass in.
                        + "attribute vec4 a_Position;       \n"		// Per-vertex position information we will pass in.
                        + "attribute vec4 a_Color;          \n"		// Per-vertex color information we will pass in.
                        + "attribute vec3 a_Normal;         \n"		// Per-vertex normal information we will pass in.
                        + "varying vec2 v_TexCoordinate;    \n"     // This will be passed into the fragment shader.
                        + "varying vec3 v_Position;         \n"		// This will be passed into the fragment shader.
                        + "varying vec4 v_Color;            \n"		// This will be passed into the fragment shader.
                        + "varying vec3 v_Normal;           \n"		// This will be passed into the fragment shader.
                        // The entry point for our vertex shader.
                        + "void main(){                                                 \n"
                        // Transform the vertex into eye space.
                        + "   v_Position = vec3(u_MVMatrix * a_Position);               \n"
                        // Pass through the color.
                        + "   v_Color = a_Color;                                        \n"
                        + "   v_TexCoordinate = a_TexCoordinate;                        \n"
                        // Transform the normal's orientation into eye space.
                        + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));        \n"
                        // gl_Position is a special variable used to store the final position.
                        // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
                        + "   gl_Position = u_MVPMatrix * a_Position;                   \n"
                        + "}                                                            \n";
        return perPixelVertexShader;
    }

    public static String getFragmentShader() {
        final String perPixelFragmentShader =
                "precision mediump float;           \n"         // Set the default precision to medium. We don't need as high of a
                        // precision in the fragment shader.
                        + "uniform vec3 u_LightPos;         \n"         // The position of the light in eye space.
                        + "uniform sampler2D u_Texture;     \n"         // The input texture.
                        + "varying vec3 v_Position;		    \n"         // Interpolated position for this fragment.
                        + "varying vec4 v_Color;            \n"         // This is the color from the vertex shader interpolated across the
                        // triangle per fragment.
                        + "varying vec3 v_Normal;           \n"         // Interpolated normal for this fragment.
                        + "varying vec2 v_TexCoordinate;    \n"         // Interpolated texture coordinate per fragment.
                        // The entry point for our fragment shader.
                        + "void main(){                    \n"
                        // Will be used for attenuation.
                        + "   float distance = length(u_LightPos - v_Position);                     \n"
                        // Get a lighting direction vector from the light to the vertex.
                        + "   vec3 lightVector = normalize(u_LightPos - v_Position);                \n"
                        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
                        // pointing in the same direction then it will get max illumination.
                /*+ "   float diffuse = max(dot(v_Normal, lightVector), 0.2);                 \n"*/
                        + "   float diffuse = (dot(v_Normal, lightVector)+0.2)<=1.0 ? (dot(v_Normal, lightVector)+0.2) : 1.0;   \n"
                        // Add attenuation.
                        + "   diffuse = diffuse * (1.0 / (1.0 + (0.00005 * distance * distance)));     \n"
                        //+ "   diffuse = diffuse + 0.3;                                              \n"
                        // Multiply the color by the diffuse illumination level to get final output color.
                        + "   gl_FragColor = /*v_Color **/ diffuse * texture2D(u_Texture, v_TexCoordinate); \n"
                        + "}                                                                     \n";
        return perPixelFragmentShader;
    }

    public static String getPointVertexShader() {
        // Define a simple shader program for our point.
        final String pointVertexShader =
                "uniform mat4 u_MVPMatrix;                              \n"
                        + "attribute vec4 a_Position;                 \n"
                        + "void main(){                                 \n"
                        + "   gl_Position = u_MVPMatrix * a_Position;   \n"
                        + "   gl_PointSize = 2.0;                       \n"
                        + "}                                            \n";
        return pointVertexShader;
    }

    public static String getPointFragmentShader() {
        final String pointFragmentShader =
                "precision mediump float;                               \n"
                        + "void main(){                                 \n"
                        + "   gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);  \n"
                        + "}                                            \n";
        return pointFragmentShader;
    }


    public static String getBlendVertexShader() {
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix.
                        + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                        + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.
                        + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.
                        + "void main(){                   \n"
                        + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                        // It will be interpolated across the triangle.
                        + "   gl_Position = u_MVPMatrix * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                        + "}                              \n";    // normalized screen coordinates.
        return vertexShader;
    }

    public static String getBlendFragmentShader() {
        final String fragmentShader =
                "precision mediump float;       \n"
                        + "varying vec4 v_Color;          \n"
                        + "void main(){                   \n"
                        + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
                        + "}                              \n";
        return fragmentShader;
    }
}
