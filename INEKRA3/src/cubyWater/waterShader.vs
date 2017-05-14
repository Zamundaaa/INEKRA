#version 400 core

in vec3 position;
in vec2 texCoords;
in vec3 translation;

out vec2 passTexCoords;
out float colorfact;
out vec4 coloradd;
out float visibility;
out vec3 norm;

out vec3 toCameraVector;
out float verz;

out vec4 clipSpace;
//out vec2 textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transmat;
uniform vec2 facts;
uniform float RANDOM;
uniform float waveheight;
uniform float holePlattn;
uniform int funnyColors;
uniform vec3 sunDir;

uniform float density;// = 0.03
uniform float gradient;// = 5.0

uniform float TIME;
uniform int MODE;
uniform float DIST;

void main(void){
	vec4 worldPosition;
	if(holePlattn <= 0.5){
		worldPosition = vec4(position + translation, 1.0);
	}else{
		worldPosition = vec4(translation, 1.0);
	}
	//float shift = sin(RANDOM)*(facts.x*worldPosition.x+facts.y*worldPosition.z));
	float shift = sin(RANDOM+(facts.x*worldPosition.x+facts.y*worldPosition.z));
	worldPosition.y += shift*waveheight;
	
	if(holePlattn > 0.5){
		worldPosition.x += position.x;
		worldPosition.y += position.y;
		worldPosition.z += position.z;
		colorfact = 1;
		norm = vec3(0, 1, 0);
	}else{
		//float s = waveheight*sin(RANDOM)*cos((facts.x*translation.x+facts.y*translation.z));
		//norm = cross(vec3(facts.x, 0, facts.y), vec3(position.x, s, position.z));
		float sv = waveheight*cos(RANDOM+facts.x*translation.x+facts.y*translation.z);
		float ablx = sv*facts.x;
		float ablz = sv*facts.y;
		norm = cross(vec3(1, ablx, 0), vec3(0, ablz, 1));
		norm = vec3(ablx, 1, ablz);
		norm = normalize(norm);
		coloradd = vec4(0.5*(shift+1), 0, 0, 1.0);
		colorfact = 0.5*(shift+1);
		//norm = -sunDir;
	}
	
	//vec4 worldPosition = transmat * vec4(position, 1.0);
	//worldPosition.xyz += translation;
	//float shift = 0;
	
	vec4 posRelatToCam = viewMatrix * worldPosition;
	float distance = length(posRelatToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float teilDings = min((distance-DIST), 1);
	if(MODE != 0 && teilDings > 0){
		float dx = teilDings*sin(TIME+worldPosition.y+worldPosition.z);
		float dy = teilDings*sin(TIME+worldPosition.x+worldPosition.z);
		float dz = teilDings*cos(TIME+worldPosition.x+worldPosition.y);
		if(MODE == 1){
			worldPosition.x += dx;
			worldPosition.y += dy;
			worldPosition.z += dz;
		}
		verz = dx*dx+dy*dy+dz*dz;
		
		if(verz >= 1){
			verz = 1;
			if(MODE == 2){
				worldPosition = vec4(dx+translation.x, dy+translation.y, dz+translation.z, 1.0);
				worldPosition.y -= 50+posRelatToCam.y;	
			}
		}else if(MODE == 2){
			vec4 newPos = vec4(dx+translation.x, dy+translation.y, dz+translation.z, 1.0);
			newPos.y = -50-posRelatToCam.y;
			worldPosition = (newPos + worldPosition)*0.5;
		}
	}else{
		verz = 0;
	}
	
	coloradd = vec4(0, 0, 0, 0);
	switch(funnyColors){
		case 1:
			colorfact = 0.05*(1-abs(shift)) + 0.95;
			break;
		case 2:
			colorfact = 0.5*(shift+1);
			break;
		case 3:
			colorfact = 0.125*(shift+1)+0.75;
			break;
		case 4:
			colorfact = 0;
			if(round(shift*100) == 100){
				coloradd = vec4(1, 0, 0, 1);
			}else if(round(shift*100) == -100){
				coloradd = vec4(0, 0, 1, 1);
			}
			break;
		default:
			colorfact = 1;
	}
		
	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	passTexCoords = texCoords;
	
	//vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	clipSpace = gl_Position;
	
	//gl_Position = clipSpace;
	//textureCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * 1;
	
	//normalize(unitVectorToCamera);
	
}
