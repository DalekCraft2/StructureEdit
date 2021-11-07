#version 460

layout(location = 0) attribute vec3 position;
layout(location = 1) attribute vec4 color;

layout(location = 0) uniform mat4 projectionMatrix;
layout(location = 1) uniform mat4 viewMatrix;
layout(location = 2) uniform mat4 modelMatrix;

varying Data {
    vec4 color;
} Output;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    Output.color = color;
}
