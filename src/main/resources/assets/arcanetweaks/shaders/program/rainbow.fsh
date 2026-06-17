#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

mat3 hueRotate(float angle)
{
    float c = cos(angle);
    float s = sin(angle);

    return mat3(
        0.299 + 0.701*c + 0.168*s,
        0.587 - 0.587*c + 0.330*s,
        0.114 - 0.114*c - 0.497*s,

        0.299 - 0.299*c - 0.328*s,
        0.587 + 0.413*c + 0.035*s,
        0.114 - 0.114*c + 0.292*s,

        0.299 - 0.300*c + 1.250*s,
        0.587 - 0.588*c - 1.050*s,
        0.114 + 0.886*c - 0.203*s
    );
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    float angle = (texCoord.x + Time) * 6.28318530718;
    float s = sin(angle);
    float c = cos(angle);

    mat3 hueRotate = mat3(
        0.299 + 0.701*c + 0.168*s,
        0.587 - 0.587*c + 0.330*s,
        0.114 - 0.114*c - 0.497*s,

        0.299 - 0.299*c - 0.328*s,
        0.587 + 0.413*c + 0.035*s,
        0.114 - 0.114*c + 0.292*s,

        0.299 - 0.300*c + 1.250*s,
        0.587 - 0.588*c - 1.050*s,
        0.114 + 0.886*c - 0.203*s
    );

    fragColor = vec4(hueRotate * color.rgb, 1.0);
}