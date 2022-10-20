#version 300 es

precision mediump float;

uniform mat4 u_ModelViewProjection;
uniform float u_LineSize;

layout(location = 0) in vec3 pos;
void main(){
    gl_Position = u_ModelViewProjection*vec4(pos, 1.0f);
}