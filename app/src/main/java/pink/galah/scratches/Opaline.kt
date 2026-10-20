package pink.galah.scratches

val opalineSpecimen = Specimen("Opaline", "✧", """
uniform float2 resolution;
uniform float time;
uniform float2 touch;
uniform float intensity;
uniform float2 nodeA;
uniform float2 nodeB;
uniform float2 nodeC;
float height(float2 p){
 float2 uv=p/resolution;
 float2 a=nodeA/resolution,b=nodeB/resolution,c=nodeC/resolution;
 float f=0.0;
 f+=.014/(length(uv-a)+.035);
 f+=.014/(length(uv-b)+.035);
 f+=.014/(length(uv-c)+.035);
 f+=.018/(length(uv-touch/resolution)+.045);
 f+=.15*sin(uv.x*14.0+time*.42)*sin(uv.y*11.0-time*.27);
 return f;
}
half4 main(float2 p){
 float2 uv=p/resolution;
 float h=height(p);
 float e=2.0;
 float2 grad=float2(height(p+float2(e,0.0))-height(p-float2(e,0.0)),height(p+float2(0.0,e))-height(p-float2(0.0,e)))*.5/e*resolution.y;
 float relief=length(grad);
 float folds=pow(clamp(relief*.33,0.0,1.0),1.4);
 float seam=exp(-abs(h-.55)*19.0);
 float3 normal=normalize(float3(-grad*.16,1.0));
 float lit=dot(normal,normalize(float3(-.5,-.65,1.0)));
 float facing=clamp(1.0-abs(normal.z),0.0,1.0);\n float illumination=clamp(lit*.65+.35,0.0,1.0);\n float iridescence=pow(facing,1.4)*illumination;\n float ir=sin(h*38.0+relief*1.2+time*.12);
 float ig=sin(h*38.0+relief*1.2+time*.12+2.094);
 float ib=sin(h*38.0+relief*1.2+time*.12+4.188);
 float3 pearl=float3(.94,.94,1.0)+float3(.07,.04,.08)*lit;
 pearl+=float3(ir,ig,ib)*(.015+.36*iridescence);
 pearl-=float3(.08,.09,.13)*folds*.55;
 pearl+=float3(.12,.15,.19)*pow(max(lit,0.0),18.0);
 pearl+=float3(.20,.11,.16)*seam*iridescence;
 return half4(half3(clamp(pearl,0.0,1.0)),1.0);
}
""")
