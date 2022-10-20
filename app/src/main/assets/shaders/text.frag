#version 300 es

precision mediump float;

uniform sampler2D u_Texture;
in vec2 vTexPos;

layout(location = 0) out vec4 o_FragColor;

void main(void){
    o_FragColor = texture(u_Texture, vec2(vTexPos.x, 1.0 - vTexPos.y));
    //o_FragColor = vec4(1.0f, 0.0f, 0.0f, 0.0f);
}