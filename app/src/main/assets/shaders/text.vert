#version 300 es

layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aTexPos;

out vec2 vTexPos;

uniform mat4 u_ViewProjection;
uniform vec3 u_CameraPose;
uniform vec3 u_TextOrigin;

void main(){
    vTexPos = aTexPos;
    vec3 labelNormal = normalize(u_CameraPose - u_TextOrigin);
    vec3 labelSide = -cross(labelNormal, vec3(0.0, 1.0, 0.0));
    vec3 modelPosition = u_TextOrigin + aTexPos.x*0.3*labelSide + aPosition.y*vec3(0.0, 1.0,0.0)*0.1;
    gl_Position  = u_ViewProjection * vec4(modelPosition, 1.0);
}