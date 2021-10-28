#version 460

attribute vec4 position;
attribute vec4 color;
uniform vec3 rotation;
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

varying vec4 varyingColor;

void main() {
    varyingColor = color;
    gl_Position = projectionMatrix * modelViewMatrix * position;
}
