package com.google.arcore.kotlin.helpers

import com.google.ar.core.Pose
import com.google.arcore.java.common.samplerender.Mesh
import com.google.arcore.java.common.samplerender.SampleRender
import com.google.arcore.java.common.samplerender.Shader
import com.google.arcore.java.common.samplerender.VertexBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class TextRenderer {
    companion object {
        private const val TAG = "TextRenderer"
        val COORDS_BUFFFER_SIZE = 2*4*4

        val NDC_QUAD_COORDS_BUFFER =
            ByteBuffer.allocateDirect(COORDS_BUFFFER_SIZE).order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer().apply {
                put(
                    floatArrayOf(
                        -1.5f, -1.5f,
                        1.5f, -1.5f,
                        -1.5f, 1.5f,
                        1.5f, 1.5f,
                    )
                )
            }
        val SQUARE_TEX_COORDS_BUFFER =
            ByteBuffer.allocateDirect(COORDS_BUFFFER_SIZE).order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer().apply {
                put(
                    floatArrayOf(
                        0f, 0f,
                        1f, 0f,
                        0f, 1f,
                        1f, 1f,
                    )
                )
            }
    }

    val cache = TextTextureCache()
    lateinit var mesh: Mesh
    lateinit var shader: Shader

    fun onSurfaceCreated(render: SampleRender){
        shader = Shader.createFromAssets(render,
            "shaders/text.vert",
            "shaders/text.frag",
            null)
            .setBlend(Shader.BlendFactor.ONE, Shader.BlendFactor.ONE_MINUS_SRC_ALPHA)
            .setDepthTest(false)
            .setDepthWrite(false)
        val vertexBuffer = arrayOf(
            VertexBuffer(render, 2, NDC_QUAD_COORDS_BUFFER),
            VertexBuffer(render, 2, SQUARE_TEX_COORDS_BUFFER),
        )
        mesh = Mesh(render, Mesh.PrimitiveMode.TRIANGLE_STRIP, null, vertexBuffer)
    }

    val textOrigin = FloatArray(3)
    fun draw(
        render: SampleRender,
        viewProjectionMatrix: FloatArray,
        pose: Pose,
        cameraPose: Pose,
        text: String
    ){
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