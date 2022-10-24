package com.google.arcore.kotlin.helpers

import com.google.ar.core.Pose
import com.google.arcore.java.common.samplerender.Mesh
import com.google.arcore.java.common.samplerender.SampleRender
import com.google.arcore.java.common.samplerender.Shader
import com.google.arcore.java.common.samplerender.VertexBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class LineRenderer {
    companion object
    {
        private const val TAG = "LineRenderer"
        private const val COORDS_PER_VERTEX = 3; //x,y,z
        private const val VERTICES_PER_BUFFER = 2;
        private const val BYTES_PER_FLOAT = Float.SIZE_BYTES
        private const val COORDS_BUFFER_SIZE = VERTICES_PER_BUFFER * COORDS_PER_VERTEX * BYTES_PER_FLOAT // 2 vertices x 3 coordinates x 4 bytes per float

        private val NDC_QUAD_COORDS_BUFFER =  //Template buffer
            ByteBuffer.allocateDirect(COORDS_BUFFER_SIZE).order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer().apply {
                put(
                    floatArrayOf(
                        -1.5f, -1.5f, 0.0f, 1.5f, 1.5f, -1.5f
                    )
                )
            }
    }
    lateinit var vertexBuffer: VertexBuffer
    lateinit var mesh: Mesh
    var shader: Shader
    constructor(render:SampleRender)
    {
        shader = Shader.createFromAssets(
            render,
            "shaders/line.vert",
            "shaders/line.frag",
            null)
            .setVec4("u_Color", floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f))
    }

    fun draw(
        render:SampleRender,
        ModelViewProjectionMatrix: FloatArray,
        firstCoordinate: FloatArray,
        secondCoordinate: FloatArray
    ){
        vertexBuffer = VertexBuffer(render,3, floatArrayToBuffer(firstCoordinate.plus(secondCoordinate)))
        mesh = Mesh(render, Mesh.PrimitiveMode.LINES, null, arrayOf(vertexBuffer))
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
