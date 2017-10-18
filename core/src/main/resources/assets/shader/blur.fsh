varying vec4 vColor;
varying vec2 vTexCoord;
uniform sampler2D u_texture;
uniform vec2 resolution;
uniform float darkness;

void sampl(int d, vec2 uv, inout vec4 fragColor, int totalPasses) {
    vec2 step1 = (vec2(d) + 0.5) / resolution.xy;
   	fragColor += texture2D(u_texture, uv + step1) / float(totalPasses*4);
    fragColor += texture2D(u_texture,  uv - step1) / float(totalPasses*4);
  	vec2 step2 = step1;
    step2.x = -step2.x;
    fragColor += texture2D(u_texture, uv + step2) / float(totalPasses*4);
    fragColor += texture2D(u_texture,  uv - step2) / float(totalPasses*4);
}

void main() {
	gl_FragColor = vec4(0);
	vec2 uv = gl_FragCoord.xy / resolution.xy;
    sampl(0, uv, gl_FragColor, 5);
    sampl(1, uv, gl_FragColor, 5);
    sampl(2, uv, gl_FragColor, 5);
    sampl(3, uv, gl_FragColor, 5);
    sampl(4, uv, gl_FragColor, 5);
    gl_FragColor = gl_FragColor / darkness;
}

