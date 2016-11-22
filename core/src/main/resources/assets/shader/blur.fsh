varying vec4 vColor;
varying vec2 vTexCoord;
uniform sampler2D u_texture;
uniform vec2 resolution;
uniform float power;
uniform float darkness;

/*float normpdf(in float x, in float sigma)
{
	return 0.39894*exp(-0.5*x*x/(sigma*sigma))/sigma;
}

vec4 blur(sampler2D iChannel0, vec2 iResolution, vec2 fragCoord) {
 		const int mSize = 11;
 		const int kSize = (mSize-1)/2;
 		float kernel[mSize];
 		vec3 final_colour = vec3(0.0);

 		//create the 1-D kernel
 		float sigma = 8.0;
 		float Z = 0.0;
 		for (int j = 0; j <= kSize; ++j)
 		{
 			kernel[kSize+j] = kernel[kSize-j] = normpdf(float(j), sigma);
 		}

 		//get the normalization factor (as the gaussian has been clamped)
 		for (int j = 0; j < mSize; ++j)
 		{
 			Z += kernel[j];
 		}

 		//read out the texels
 		for (int i=-kSize; i <= kSize; ++i)
 		{
 			for (int j=-kSize; j <= kSize; ++j)
 			{
 				vec2 blurTC = (fragCoord.xy+vec2(float(i),float(j)) * power) / iResolution.xy;
				//blurTC.y = 1.0 - blurTC.y;

				blurTC = clamp(blurTC, vec2(0.0), vec2(1.0));

				vec3 color = kernel[kSize+j]*kernel[kSize+i] * texture2D(iChannel0, blurTC).rgb;

				final_colour += color;

 			}
 		}

 		return vec4(final_colour/(Z*Z), 1.0);
}*/

void sampl(int d, vec2 uv, inout vec4 gl_FragColor, int totalPasses)
{

    vec2 step1 = (vec2(d) + 0.5) / resolution.xy;

   	gl_FragColor += texture2D(u_texture, uv + step1) / float(totalPasses*4);
    gl_FragColor += texture2D(u_texture,  uv - step1) / float(totalPasses*4);
  	vec2 step2 = step1;
    step2.x = -step2.x;
    gl_FragColor += texture2D(u_texture, uv + step2) / float(totalPasses*4);
    gl_FragColor += texture2D(u_texture,  uv - step2) / float(totalPasses*4);

}

void main() {
	//gl_FragColor = (vColor * blur(u_texture, resolution, gl_FragCoord.xy)) / darkness;
	vec2 uv = gl_FragCoord.xy / resolution.xy ;
    	sampl(0, uv, gl_FragColor, 5);
       sampl(1, uv, gl_FragColor, 5);
       sampl(2, uv, gl_FragColor, 5);
       sampl(2, uv, gl_FragColor, 5);
       sampl(3, uv, gl_FragColor, 5);
       gl_FragColor /= darkness;
}

