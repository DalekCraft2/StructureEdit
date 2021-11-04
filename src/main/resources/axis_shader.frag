#version 460

varying Data {
    vec4 color;
} Input;

void main() {
    if (Input.color[3] >= 0.0) {
        gl_FragColor = Input.color;
    } else {
        discard;
    }
}
