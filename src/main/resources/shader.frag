#version 460

uniform sampler2D texture;

layout(location = 3) uniform mat3 normalMatrix;
layout(location = 4) uniform mat4 textureMatrix;
uniform float Ka;// Ambient reflection coefficient
uniform float Kd;// Diffuse reflection coefficient
uniform float Ks;// Specular reflection coefficient
uniform float shininess;// Shininess
// Material color
uniform vec3 ambientColor;
uniform vec3 diffuseColor;
uniform vec3 specularColor;
uniform vec3 lightPosition;// Light position

varying Data {
    vec3 position;
    vec4 color;
    vec3 normal;
    vec2 texCoord;
} Input;

void main() {
    vec3 normalInterp = normalMatrix * Input.normal;
    vec3 N = normalize(normalInterp);
    vec3 L = normalize(lightPosition - Input.position);
    // Lambert's cosine law
    float lambertian = max(dot(N, L), 0.0);
    float specular = 0.0;
    if (lambertian > 0.0) {
        vec3 R = reflect(-L, N);// Reflected light vector
        vec3 V = normalize(-Input.position);// Vector to viewer
        // Compute the specular term
        float specAngle = max(dot(R, V), 0.0);
        specular = pow(specAngle, shininess);
    }
    vec4 lightingColor = vec4(Ka * ambientColor + Kd * lambertian * diffuseColor + Ks * specular * specularColor, 1.0) * Input.color;

    vec2 texCoordTranslated = (textureMatrix * vec4(Input.texCoord, 0.0, 1.0)).st;
    vec4 textureColor = texture(texture, texCoordTranslated);
    vec4 finalColor = textureColor * lightingColor;

    if (finalColor[3] > 0.0) {
        gl_FragColor = finalColor;
    } else {
        discard;
    }
}
