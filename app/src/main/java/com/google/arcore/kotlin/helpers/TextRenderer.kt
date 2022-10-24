package com.google.arcore.kotlin.helpers

import com.google.ar.core.Pose
import com.google.arcore.java.common.samplerender.Mesh
import com.google.arcore.java.common.samplerender.SampleRender
import com.google.arcore.java.common.samplerender.Shader
import com.google.arcore.java.common.samplerender.VertexBuffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ByteOrder


class TextRenderer {
    companion object {
        private const val TAG = "TextRenderer"
        private const val COORDS_PER_VERTEX = 2; //x, y
        private const val VERTICES_PER_BUFFER = 4;

        private const val BYTES_PER_FLOAT = Float.SIZE_BYTES
        private const val COORDS_BUFFER_SIZE = COORDS_PER_VERTEX * VERTICES_PER_BUFFER * BYTES_PER_FLOAT

        private val NDC_QUAD_COORDS_BUFFER =
            ByteBuffer.allocateDirect(COORDS_BUFFER_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply{
                    put(
                        floatArrayOf(
                            -1.5f, -1.5f,
                            1.5f, -1.5f,
                            -1.5f, 1.5f,
                            1.5f, 1.5f
                        )
                    )
                }
        val SQUARE_TEX_COORDS_BUFFER =
            ByteBuffer.allocateDirect(COORDS_BUFFER_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply{
                    put(
                        floatArrayOf(
                            0f, 0f,
                            1f, 0f,
                            0f, 1f,
                            1f, 1f
                        )
                    )
                }
    }
    val cache = TextTextureCache()
    lateinit var mesh: Mesh
    lateinit var shader: Shader

    constructor(render: SampleRender)
    {
        shader = Shader.createFromAssets(render,
            "shaders/text.vert",
            "shaders/text.frag",
            null)
            .setBlend(Shader.BlendFactor.ONE, Shader.BlendFactor.ONE_MINUS_SRC_ALPHA)
            .setDepthTest(false)
            .setDepthWrite(false)
        val vertexBuffers = arrayOf(
            VertexBuffer(render, 2, NDC_QUAD_COORDS_BUFFER),
            VertexBuffer(render, 2, SQUARE_TEX_COORDS_BUFFER)
        )
        mesh = Mesh(render, Mesh.PrimitiveMode.TRIANGLE_STRIP, null, vertexBuffers)
    }
    fun draw(
        render: SampleRender,
        viewProjectionMatrix: FloatArray,
        pose: Pose,
        cameraPose: Pose,
        text: String
    ){
        val textOrigin = FloatArray(3);
        textOrigin[0] = pose.tx()
        textOrigin[1] = pose.ty()
        textOrigin[2] = pose.tz()
        shader
            .setMat4("u_ViewProjection", viewProjectionMatrix)
            .setVec3("u_TextOrigin", textOrigin)
            .setVec3("u_CameraPose", cameraPose.translation)
            .setTexture("u_Texture", cache.get(render, text))
        render.draw(mesh, shader)
    }
}