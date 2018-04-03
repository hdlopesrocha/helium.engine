#version 330 core

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TextureCoord;


uniform vec3 cameraPosition;
uniform mat4 viewProjectionMatrix;
uniform mat4 modelMatrix;
uniform vec3 ambientColor;
uniform int lightEnabled;
uniform float ftime;

out vec3 vPosition;
out vec3 vNormal;
out vec2 vTexCoord;

void main(void) {
	mat3 modelMatrix3 = mat3(modelMatrix);
	vPosition = modelMatrix3*in_Position;
	if(lightEnabled == 1){
	    vNormal = modelMatrix3*in_Normal;
	}
	vTexCoord = in_TextureCoord;


	mat4 modelViewProjection = viewProjectionMatrix * modelMatrix;
	gl_Position = modelViewProjection * vec4(in_Position,1.0);
}
