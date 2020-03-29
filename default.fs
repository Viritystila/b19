out vec4 op;
in vec2 texCoordV;

void main(void) {
  vec2 uv = (gl_FragCoord.xy / iResolution.xy);
  vec2 uv2=uv;
  //uv=mix(uv,texCoordV, iFloat1/300);
  uv.y=-2.0-uv.y;
  vec4 iChannel1_texture=texture2D(iChannel1, uv2);
  vec4 iChannel2_texture=texture2D(iChannel2, uv);
  vec4 iChannel3_texture=texture2D(iChannel3, uv);
  vec4 text=texture2D(iText, uv);
  op =mix(iChannel1_texture-iChannel3_texture, iChannel2_texture+text*100, 0.9+iFloat1*iFloat1/5000);
}
