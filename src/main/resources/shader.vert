#version 460

layout(location = 0) attribute vec3 position;
layout(location = 1) attribute vec4 color;
layout(location = 2) attribute vec3 normal;
layout(location = 3) attribute vec2 texCoord;

layout(location = 0) uniform mat4 projectionMatrix;
layout(location = 1) uniform mat4 viewMatrix;
layout(location = 2) uniform mat4 modelMatrix;

varying Data {
    vec3 position;
    vec4 color;
    vec3 normal;
    vec2 texCoord;
} Output;

void main() {
    vec4 vertPos4 = viewMatrix * modelMatrix * vec4(position, 1.0);
    vec3 vertPos = vec3(vertPos4) / vertPos4.w;
    gl_Position = projectionMatrix * vertPos4;
    Output.position = vertPos;
    Output.color = color;
    Output.normal = normal;
    Output.texCoord = texCoord;
}
