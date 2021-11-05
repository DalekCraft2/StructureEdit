#version 460

// Adapted from http://www.cs.toronto.edu/~jacobson/phong-demo/

uniform sampler2D texture;

uniform float Ka;// Ambient reflection coefficient
uniform float Kd;// Diffuse reflection coefficient
uniform float Ks;// Specular reflection coefficient
uniform float shininess;// Shininess
// Material color
uniform vec3 ambientColor;
uniform vec3 diffuseColor;
uniform vec3 specularColor;

varying Data {
    vec3 position;
    vec4 color;
    vec3 normal;
    vec2 texCoord;
    vec3 lightPosition;
} Input;

void main() {
    // Ambient
    vec3 ambient = Ka * ambientColor;

    // Diffuse
    vec3 normal = normalize(Input.normal);
    vec3 lightDirection = normalize(Input.lightPosition - Input.position);
    // Lambert's cosine law
    float lambertian = max(dot(normal, lightDirection), 0.0);
    vec3 diffuse = Kd * lambertian * diffuseColor;

    // Specular
    vec3 viewDirection = normalize(-Input.position);// Vector to viewer
    vec3 reflectDirection = reflect(-lightDirection, normal);// Reflected light vector
    float specAngle = max(dot(viewDirection, reflectDirection), 0.0);
    float spec = pow(specAngle, shininess);
    vec3 specular = Ks * spec * specularColor;

    vec4 lightColor = vec4(ambient + diffuse + specular, 1.0);
    vec4 textureColor = texture(texture, Input.texCoord);
    vec4 finalColor = lightColor * Input.color * textureColor;

    if (finalColor[3] > 0.0) {
        gl_FragColor = finalColor;
    } else {
        discard;
    }
}
