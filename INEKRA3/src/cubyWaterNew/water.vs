#version 330

in vec3 pos;
in vec3 norm;

out vec3 normal;
out vec4 clipSpace;
out vec3 toCameraVector;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform float time;

void main(){
	
	vec4 worldPos = vec4(pos.x, pos.y, pos.z, 1.0);
	//if(norm.y == 1){
	//	worldPos.y += 0.1*sin(time+pos.x*2+pos.y*5+pos.z*3);
	//}
	gl_Position = projectionMatrix * viewMatrix * worldPos;
	normal = norm;
	clipSpace = gl_Position;
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPos.xyz;
	
}
