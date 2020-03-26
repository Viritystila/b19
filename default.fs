out vec4 op;
void main(void) {
  vec2 uv = (gl_FragCoord.xy / iResolution.xy);
  vec2 uv2=uv;
  uv.y=-2.0-uv.y;
  vec4 iChannel1_texture=texture2D(iChannel1, uv2);
  vec4 iChannel2_texture=texture2D(iChannel2, uv);
  vec4 text=texture2D(iText, uv);
  op =mix(iChannel1_texture, iChannel2_texture+text*100, 0.5);
}
