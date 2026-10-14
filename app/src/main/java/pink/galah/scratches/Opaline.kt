package pink.galah.scratches

val opalineSpecimen = Specimen("Opaline", "✧", """
uniform float2 resolution;
uniform float time;
uniform float2 touch;
uniform float intensity;
uniform float2 nodeA;
uniform float2 nodeB;
uniform float2 nodeC;
half4 main(float2 p) {
 float2 uv=p/resolution;
 float2 q=uv-touch/resolution;
 float d=length(q);
 float h=sin(uv.x*12.0+time*.35)*sin(uv.y*9.0-time*.25)*.13;
 h+=exp(-d*d*38.0)*.8;
 float2 n=float2(cos(uv.x*12.0+time*.35)*sin(uv.y*9.0-time*.25)*1.56,
 sin(uv.x*12.0+time*.35)*cos(uv.y*9.0-time*.25)*1.17);
 n+=-q*exp(-d*d*38.0)*76.0;
 float edge=clamp(length(n)*.08,0.0,1.0);
 float sheen=pow(clamp(dot(normalize(float3(-n*.11,1.0)),normalize(float3(-.4,-.5,1.0))),0.0,1.0),16.0);
 float r=clamp(.965+edge*.05+sin(h*12.0+n.x*.9)*edge*.08,0.0,1.0);
 float g=clamp(.958+edge*.04+sin(h*12.0+n.y*.9+2.1)*edge*.065,0.0,1.0);
 float b=clamp(.985+edge*.012+sin(h*12.0+n.x*.9+4.2)*edge*.05,0.0,1.0);
 half3 color=half3(r,g,b)-half3(.035,.045,.065)*half(edge*.65)+half3(sheen*.12);
 return half4(color,1.0);
}
""")
