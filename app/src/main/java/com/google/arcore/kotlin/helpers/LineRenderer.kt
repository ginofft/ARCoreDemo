package com.google.arcore.kotlin.helpers

import com.google.ar.core.Pose
import com.google.arcore.java.common.samplerender.Mesh
import com.google.arcore.java.common.samplerender.SampleRender
import com.google.arcore.java.common.samplerender.Shader
import com.google.arcore.java.common.samplerender.VertexBuffer
import com.google.arcore.kotlin.helpers.TextRenderer.Companion.NDC_QUAD_COORDS_BUFFER
import com.google.arcore.kotlin.helpers.TextRenderer.Companion.SQUARE_TEX_COORDS_BUFFER
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class LineRenderer {
    companion object
    {
        val TAG = "LineRenderer"
        val COORDS_BUFFER_SIZE = 1 * 6 * 4 // 2 coordinates x 2 elements x 4 bytes (float)

        val NDC_QUAD_COORDS_BUFFER =
            ByteBuffer.allocateDirect(COORDS_BUFFER_SIZE).order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer().apply {
                put(
                    floatArrayOf(
                        0.0f, 0.0f, -1.5f, 1.5f, 1.5f, -1.5f
                    )
                )
            }
    }

    lateinit var mesh:Mesh
    lateinit var shader: Shader
    lateinit var vertexBuffer: VertexBuffer
    fun onSurfaceCreated(render: SampleRender){
        shader = Shader.createFromAssets(render,
            "shaders/line.vert",
            "shaders/line.frag",
            null
            )
            .setVec4("u_Color", floatArrayOf(255.0f, 255.0f, 0.0f, 1.0f))
        vertexBuffer = VertexBuffer(render, 3, NDC_QUAD_COORDS_BUFFER)
        var vertexBuffers = arrayOf(vertexBuffer)
        mesh = Mesh(render, Mesh.PrimitiveMode.LINES, null, vertexBuffers)
    }

    fun draw(
        render:SampleRender,
        ModelViewProjectionMatrix: FloatArray,
        firstCoordinate: FloatArray,
        secondCoordinate: FloatArray
    ){
        val FloatBuffer = floatArrayToBuffer(firstCoordinate.plus(secondCoordinate))
        vertexBuffer.set(FloatBuffer)
        //val vertexBuffers = arrayOf(vertexBuffer)
        //mesh = Mesh(render, Mesh.PrimitiveMode.LINES, null, vertexBuffers)
        shader
            .setMat4("u_ModelViewProjection", ModelViewProjectionMatrix)
        render.draw(mesh, shader)
    }
    private fun floatArrayToBuffer(array: FloatArray): FloatBuffer {
        // Convert an kotlin float array to a float buffer for better performance
        var floatBuffer = ByteBuffer.allocateDirect(COORDS_BUFFER_SIZE).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer().apply{put(array)}
        return floatBuffer
    }
}
