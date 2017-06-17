#version 330

in vec3 normal;
in vec4 clipSpace;
in vec3 toCameraVector;

out vec4 out_Color;
out vec4 out_BrightColor;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D refractDepth;
uniform float reflections;

uniform vec3 sunLight;

void main(){

	vec3 norm = normal;
	
	vec4 mixColor = vec4(0.2, 0.4, 0.9, 0.3);
	float mixFact = 0.1;
	
	
	out_BrightColor = vec4(0.0);
	
	if(reflections > 0.5 && norm.y == 1){
		vec3 unitVectorToCam = normalize(toCameraVector);
		vec3 calcNorm;
		if(unitVectorToCam.y < 0){
			calcNorm = -norm;
		}else{
			calcNorm = norm;
		}
	
		vec2 ndc = (clipSpace.xy/clipSpace.w)*0.5 + 0.5;
		vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);
		//reflectTexCoords += reflect(norm, vec3(0, 1, 0)).xz;
		
		float near = 0.1;
		float far = 1024;
		float depth = texture(refractDepth, ndc).r;
		float floorDist = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
		
		depth = gl_FragCoord.z;
		float waterDist = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
		float waterDepth = floorDist - waterDist;
		
		vec4 reflectColour = texture(reflectionTexture, reflectTexCoords + (1-dot(norm, vec3(0, 1, 0))) );
		vec4 refractColour = texture(refractionTexture, ndc + (1-dot(norm, vec3(0, 1, 0))) );
		
		float refractiveFactor = dot(unitVectorToCam, calcNorm);
		float refractivity;
		//if(unitVectorToCam.y >= 0){
		//	refractivity = 0.25;
		//}else{
			refractivity = 1;
		//}
		
		refractiveFactor = clamp(pow(refractiveFactor, 0.75), 0, 1);
		
		out_Color = mix(reflectColour, refractColour, refractiveFactor);
		out_Color = mix(out_Color, mixColor*vec4(sunLight, 1.0), mixFact);
		
	}else{
		out_Color = mixColor*vec4(sunLight, 1.0);
	}
	
}
