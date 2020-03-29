layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 1) in vec3 colors_modelspace;


float ux = floor((gl_VertexID*1.0) / 8.0*iFloat1/10) + mod((gl_VertexID*1.0),0.2);
float vy = mod(floor((gl_VertexID*1.0) / 2.0) + floor ((gl_VertexID*1.0) /3.0), 1.0*0);

vec2 uv=vec2(ux,vy);
vec4 ic1t=texture2D(iChannel1, uv);

out vec2 texCoordV;
void main(void) {
  texCoordV=uv;
  gl_Position = vertexPosition_modelspace;
}
