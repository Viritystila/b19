mat4 rotationMatrix(vec3 axis, float angle) {
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;

    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
                0.0,                                0.0,                                0.0,                                1.0);
}

#define PI 3.14159
out vec2 texCoordV;
out vec3 Normal;
out VertexData {
    vec4 vertexPosition_modelspace;
    vec3 colors_modelspace;
    vec3 index_modelspace;
    vec2 uv_modelspace;
    vec3 normals_modelspace;
    vec4 modelScale;
    vec4 modelPosition;
    mat4 modelRotation;
} VertexOut;



void main(void) {
  VertexOut.vertexPosition_modelspace=vertexPosition_modelspace;
  VertexOut.colors_modelspace=colors_modelspace;
  VertexOut.index_modelspace=index_modelspace;
  VertexOut.uv_modelspace=uv_modelspace;
  VertexOut.normals_modelspace=normals_modelspace;
  VertexOut.modelScale=modelScale;
  VertexOut.modelPosition=modelPosition;
  VertexOut.modelRotation=modelRotation;

  float rad=texture2D(iChannel2, uv_modelspace).b;

  texCoordV=uv_modelspace;
  texCoordV.y=texCoordV.y;
  texCoordV.x=1-texCoordV.x;
  VertexOut.uv_modelspace=texCoordV;
  vec4 iChannel1_texture=texture2D(iChannel1, texCoordV);
  Normal=normals_modelspace;
  float time = iGlobalTime + 20.0;
  vec4 scales[4]=vec4[4](vec4(21,21,0,1),
                         vec4(1,1,1,2),
                         vec4(1,1,1,1),
                          vec4(1,1,1,1));
  vec4 posits[4]=vec4[4](vec4(0,0,1,20),
                         vec4(0,0,-2,0),
                         vec4(-1,-1,-1,4),
                         vec4(0,1,-3,3));
  mat4 rotmas[4]=mat4[4](rotationMatrix(vec3(0.0,1, 1.0), 0),
                         rotationMatrix(vec3(1.0+time,1+time, 1.0+time), time),
                         rotationMatrix(vec3(1.0+1,1, 1.0), time),
                         rotationMatrix(vec3(0.0,1, 1.0), 0));
  vec4 vertexPoss[4]=vec4[4](vertexPosition_modelspace,
                             vertexPosition_modelspace+rad*vec4(normals_modelspace, 0),
                             vertexPosition_modelspace,
                             vertexPosition_modelspace
                             );
  vec4 scale=scales[iMeshID];
  vec4 posit=posits[iMeshID];
  mat4 rotma=rotmas[iMeshID];
  vec4 vertexPos=vertexPoss[iMeshID];
  gl_Position =vertexPos*scale*rotma+posit;
}
