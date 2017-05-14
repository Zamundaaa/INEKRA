#version 400 core

//in float randomMult;
in vec2 passTexCoords;
in float colorfact;
in float visibility;
in vec4 coloradd;
in vec3 norm;
in vec3 toCameraVector;
in float verz;

//in vec2 textureCoords;
in vec4 clipSpace;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_BrightColor;

uniform sampler2D tex;
//uniform sampler2D blink;
uniform float random;
uniform vec3 sunlight;
uniform vec3 skyColor;
uniform vec3 sunDir;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D refractDepth;

const float shineDamper = 100;
const float reflectivity = 0.75;

uniform int MODE;

uniform float reflective;

//uniform sampler2D skybox //use for semigood reflections that make the waves more visible!
// RENDER IN SKYBOXRENDERER TO EXTRA SKYBOX-TEXTURE!!! --> thinmatrix tut

const vec4 mixColor = vec4(0.5, 0.5, 0.8, 1.0);
const float mixFact = 0.05;
const float PI = 3.1415;

void main(void){//GGGGGGGGGLLLLLLÄÄÄÄÄÄÄNNNNNZZZZZEEEEENNNNNNNNNNNNN!!!!!!!!!!!!!!
	
	vec4 texColor = texture(tex, passTexCoords);
	if(reflective < 0.5){
		out_Color *= colorfact;
		out_Color += coloradd;
		
		// just lighting calcs
		float nDot1 = dot(norm, sunDir);
		float brightness = max(nDot1, 1);
		vec3 diffuse = brightness * sunlight;
		
		vec3 unitVectorToCam = normalize(toCameraVector);
		vec3 lightDirection = -normalize(sunDir);
		vec3 reflectedLightDirection = reflect(lightDirection, norm);
		
		float specularFactor = dot(reflectedLightDirection , unitVectorToCam);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		vec3 finalSpecular = dampedFactor * reflectivity * sunlight;
		
		out_Color = vec4(diffuse * texColor.xyz + finalSpecular, texColor.a);
		out_BrightColor = vec4(finalSpecular, texColor.a);
		
		//out_BrightColor = vec4(0.0);
		//lc end
	}else{
		
		// just lighting calcs
		float nDot1 = dot(norm, sunDir);
		float brightness = max(nDot1, 1);
		vec3 diffuse = brightness * sunlight;
		
		vec3 unitVectorToCam = normalize(toCameraVector);
		vec3 calcNorm;
		if(unitVectorToCam.y < 0){
			calcNorm = -norm;
		}else{
			calcNorm = norm;
		}
		float fact = 1;
		vec3 lightDirection = -normalize(sunDir);
		if(lightDirection.y > 0){
			lightDirection = -lightDirection;
			fact = 0.2;
		}
		vec3 reflectedLightDirection;
		if(unitVectorToCam.y > 0){
			reflectedLightDirection = reflect(lightDirection, calcNorm);
		}else{
			reflectedLightDirection = lightDirection;
		}
		
		float specularFactor = dot(reflectedLightDirection , unitVectorToCam);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		vec3 finalSpecular = dampedFactor * reflectivity * sunlight;
		out_BrightColor = vec4(finalSpecular*fact, 1.0);
		
			
			
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
			if(unitVectorToCam.y >= 0){
				refractivity = 0.25;
			}else{
				refractivity = 1;
			}
			
			refractiveFactor = clamp(pow(refractiveFactor, 0.75), 0, 1);
			
			out_Color = mix(reflectColour, refractColour, refractiveFactor);
			out_Color = mix(out_Color, mixColor, mixFact);
			
			//out_Color.a = clamp(waterDepth, 0, 1);
			
			//if(waterDepth > 100 && waterDepth < 1000){
			//	out_Color = mix(out_Color, vec4(0.1, 0.1, 0.6, 1.0), clamp(waterDepth/400, 0, 1));
			//}
			//out_Color = vec4(vec3(waterDepth/10), 1.0);
			
	}
	
	if(MODE == 0){
		out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
	}else{
		out_Color = mix(out_Color, vec4(0.7, 0.7, 0.7, 1-verz), verz);
	}
	
}