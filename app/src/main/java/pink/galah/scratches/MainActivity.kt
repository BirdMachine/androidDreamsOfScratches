package pink.galah.scratches
import android.graphics.RuntimeShader
import android.graphics.RenderEffect as AndroidRenderEffect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{ScratchApp()}}}
data class Specimen(val name:String,val glyph:String,val source:String)
private const val WARP="""uniform shader content; uniform float2 resolution; uniform float time; uniform float amount;
half4 main(float2 p){float2 uv=p/resolution;float2 q=uv-.5;float r=length(q);float a=atan(q.y,q.x);float wob=sin(a*7.0+time*2.1)*.012*amount+sin(r*32.0-time*3.0)*.007*amount;float2 d=normalize(q+float2(.0001))*wob*resolution;float ca=.003*amount*resolution.x;half4 g=content.eval(p+d);half rr=content.eval(p+d+float2(ca,0)).r;half bb=content.eval(p+d-float2(ca,0)).b;float edge=smoothstep(.46,.34,r);return half4(rr,g.g,bb,g.a)*half(.82+.18*edge);}"""
private const val H="""uniform float2 resolution; uniform float time; uniform float2 touch; uniform float intensity; uniform float2 nodeA; uniform float2 nodeB; uniform float2 nodeC;
float hash(float2 p){return fract(sin(dot(p,float2(127.1,311.7)))*43758.5453);}
float noise(float2 p){float2 i=floor(p),f=fract(p);f=f*f*(3.0-2.0*f);return mix(mix(hash(i),hash(i+float2(1,0)),f.x),mix(hash(i+float2(0,1)),hash(i+float2(1,1)),f.x),f.y);}
half3 pal(float t){return half3(.52+.48*cos(6.283*(t+0.00)),.52+.48*cos(6.283*(t+.33)),.52+.48*cos(6.283*(t+.67)));}"""
private val specimens=listOf(
Specimen("Glass","◫",H+"""half4 main(float2 p){float2 uv=p/resolution;float2 m=touch/resolution;float d=distance(uv,m);float n=noise(uv*7.0+time*.05);float lens=exp(-d*d*22.0)*intensity;float2 warp=float2(sin((uv.y+n)*18.0+time),cos((uv.x-n)*16.0-time))*.018*lens;float bands=.5+.5*sin((uv.x+warp.x)*18.0+(uv.y+warp.y)*11.0);half3 c=pal(bands*.16+n*.22+time*.018);float rim=exp(-abs(d-.22)*75.0);return half4(c*half(.42+.45*lens)+half3(rim*.8),1);}"""),
Specimen("Metaball UI","⬡",H+"""half4 main(float2 p){float2 uv=p/resolution;float2 m=touch/resolution;float2 a=nodeA/resolution,b=nodeB/resolution,c=nodeC/resolution;float f=.014/max(distance(uv,a),.008)+.014/max(distance(uv,b),.008)+.018/max(distance(uv,c),.008)+.012/max(distance(uv,m),.008);float body=smoothstep(.48,.63,f),rim=smoothstep(.39,.50,f)-smoothstep(.62,.72,f);half3 ink=mix(half3(.012,.01,.035),pal(f*.23+time*.018),half(body));ink+=half3(.8,.9,1.0)*half(rim*.65);return half4(ink,1);}"""),
Specimen("Liquid","◉",H+"""half4 main(float2 p){float2 uv=(p-.5*resolution)/resolution.y;float2 m=(touch-.5*resolution)/resolution.y;float n=noise(uv*3.0+float2(time*.12,-time*.09));uv+=.22*intensity*float2(sin(n*6.28+time),cos(n*5.7-time*.7));float d=length(uv-m);float v=n+.16*sin(8.0*uv.x+time)+.12/(1.0+14.0*d);return half4(pal(v*.55+time*.025),1);}"""),
Specimen("Goo","●",H+"""half4 main(float2 p){float2 uv=p/resolution;float2 m=touch/resolution;float a=time*.7;float2 q1=float2(.5+.22*sin(a),.48+.18*cos(a*1.3));float2 q2=float2(.5+.24*cos(a*.8),.52+.20*sin(a*1.1));float f=.020/max(length(uv-q1),.01)+.020/max(length(uv-q2),.01)+.026/max(length(uv-m),.01);float edge=smoothstep(.78,.95,f);half3 c=mix(half3(.025,.02,.06),pal(f*.35+time*.02),half(edge));return half4(c,1);}"""),
Specimen("Plasma","⌁",H+"""half4 main(float2 p){float2 uv=p/resolution*6.0;float v=sin(uv.x+time)+sin(uv.y*1.3-time*.8)+sin((uv.x+uv.y)*.72+time*.55);v+=sin(length(uv-float2(3.0))*2.2-time);return half4(pal(v*.11+time*.035),1);}"""),
Specimen("Aurora","≋",H+"""half4 main(float2 p){float2 uv=p/resolution;float n=0.0;float2 q=uv*3.0;n+=.55*noise(q+time*.08);n+=.28*noise(q*2.1-time*.06);n+=.14*noise(q*4.2+time*.04);float band=exp(-pow((uv.y-.52)-.17*sin(uv.x*6.0+n*4.0+time*.35),2.0)*35.0);half3 c=half3(.015,.02,.055)+pal(n*.5+.34)*half(band*.9*intensity);return half4(c,1);}"""),
Specimen("Ripple","◎",H+"""half4 main(float2 p){float2 uv=p/resolution;float2 m=touch/resolution;float d=distance(uv,m);float wave=sin(d*65.0-time*7.0)*exp(-d*5.0)*intensity;float base=.45+.2*sin(uv.y*8.0+wave*2.0)+.18*noise(uv*5.0+time*.04);half3 water=mix(half3(.015,.07,.10),half3(.18,.72,.78),half(base+wave*.15));return half4(water,1);}"""),
Specimen("Lens","◌",H+"""half4 main(float2 p){float2 uv=p/resolution;float2 m=touch/resolution;float d=distance(uv,m);float lens=exp(-d*d*45.0)*intensity;float grid=step(.94,fract((uv.x+lens*.04)*14.0))+step(.94,fract((uv.y-lens*.03)*22.0));half3 c=pal(uv.x*.35+uv.y*.22+time*.02+lens*.15);c=mix(c,half3(1),half(min(grid,1.0)*.22));return half4(c*(.55+.45*lens),1);}"""),
Specimen("Reaction","✣",H+"""half4 main(float2 p){float2 uv=p/resolution*4.0;float n=noise(uv*1.7+float2(time*.025,-time*.018));float r=sin((n*6.0+sin(uv.x*2.3)+cos(uv.y*2.0))*3.1);float cells=smoothstep(-.12,.2,r);half3 a=half3(.025,.015,.06),b=pal(n*.45+time*.01);return half4(mix(a,b,half(cells*.85)),1);}"""),
Specimen("SDF","◇",H+"""half4 main(float2 p){float2 uv=(p-.5*resolution)/resolution.y;float a=time*.45;float2 q=float2(cos(a)*uv.x-sin(a)*uv.y,sin(a)*uv.x+cos(a)*uv.y);float circle=length(q)-.27;float box=length(max(abs(q)-float2(.22,.22),0.0))-.055;float k=.5+.5*sin(time*.55);float d=mix(circle,box,k);float glow=exp(-abs(d)*28.0);half3 c=half3(.02,.015,.055)+pal(k*.5+time*.02)*half(glow*intensity);return half4(c,1);}"""))
@Composable fun ScratchApp(){
 var selected by remember{mutableStateOf(specimens.first())};var intensity by remember{mutableFloatStateOf(1f)};var controls by remember{mutableStateOf(true)}
 var tx by remember{mutableFloatStateOf(540f)};var ty by remember{mutableFloatStateOf(900f)};var nodeA by remember{mutableStateOf(Offset(260f,700f))};var nodeB by remember{mutableStateOf(Offset(760f,850f))};var nodeC by remember{mutableStateOf(Offset(520f,1150f))};var time by remember{mutableFloatStateOf(0f)};var running by remember{mutableStateOf(true)};var speed by remember{mutableFloatStateOf(1f)};var scale by remember{mutableFloatStateOf(1f)};var labMode by remember{mutableStateOf(true)}
 LaunchedEffect(running){var start=0L;while(running){withFrameNanos{n->if(start==0L)start=n;time=(n-start)/1_000_000_000f*speed}}}
 MaterialTheme(colorScheme=darkColorScheme()){Box(Modifier.fillMaxSize().background(Color(0xFF090812))){
  ShaderField(selected,time,intensity,tx,ty,scale,nodeA,nodeB,nodeC,Modifier.fillMaxSize().pointerInput(selected){detectDragGestures(onDragStart={tx=it.x;ty=it.y},onDrag={c,_->tx=c.position.x;ty=c.position.y})}.pointerInput(selected){detectTapGestures{tx=it.x;ty=it.y}}.pointerInput(selected){detectTransformGestures{centroid,_,zoom,_->tx=centroid.x;ty=centroid.y;scale=(scale*zoom).coerceIn(.35f,3f)}})
  ExperimentalChrome(selected,labMode,{labMode=!labMode},nodeA,nodeB,nodeC,{nodeA+=it},{nodeB+=it},{nodeC+=it})
  Column(Modifier.fillMaxSize().systemBarsPadding().padding(14.dp),verticalArrangement=Arrangement.SpaceBetween){
   Column{Row(verticalAlignment=Alignment.CenterVertically){Text("SCRATCH",fontSize=25.sp);Spacer(Modifier.width(9.dp));Text(if(labMode)"THE UI IS THE SPECIMEN" else "UI MATERIAL LAB",fontSize=11.sp,color=Color.White.copy(.62f));Spacer(Modifier.weight(1f));TextButton(onClick={running=!running}){Text(if(running)"PAUSE" else "PLAY")}}
    LazyRow(horizontalArrangement=Arrangement.spacedBy(7.dp)){items(specimens){s->FilterChip(selected=s==selected,onClick={selected=s},label={Text(s.glyph+" "+s.name)})}}}
   Column{AnimatedVisibility(controls){Surface(color=Color.Black.copy(.50f),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(16.dp)){Text(selected.glyph+" "+selected.name.uppercase()+" / UNIFORM BENCH",fontSize=12.sp);Spacer(Modifier.height(8.dp));Text("INTENSITY  "+"%.2f".format(intensity),fontSize=11.sp,color=Color.White.copy(.7f));Slider(value=intensity,onValueChange={intensity=it},valueRange=0f..2f);Row(verticalAlignment=Alignment.CenterVertically){Text("TIME ×"+"%.1f".format(speed),fontSize=10.sp,modifier=Modifier.width(72.dp));Slider(value=speed,onValueChange={speed=it},valueRange=.1f..3f,modifier=Modifier.weight(1f));Text("ZOOM ×"+"%.1f".format(scale),fontSize=10.sp,modifier=Modifier.padding(start=8.dp))};WarpedInput(time,intensity);TortureTest()}}}
    TextButton(onClick={controls=!controls},modifier=Modifier.align(Alignment.End)){Text(if(controls)"HIDE BENCH ↓" else "SHOW BENCH ↑")}}
  }
 }}
}
@Composable private fun ShaderField(s:Specimen,time:Float,intensity:Float,tx:Float,ty:Float,scale:Float,nodeA:Offset,nodeB:Offset,nodeC:Offset,modifier:Modifier){
 val shader=remember(s){RuntimeShader(s.source)};Canvas(modifier){shader.setFloatUniform("resolution",size.width,size.height);shader.setFloatUniform("time",time);shader.setFloatUniform("touch",tx.coerceIn(0f,size.width),ty.coerceIn(0f,size.height));shader.setFloatUniform("nodeA",nodeA.x,nodeA.y);shader.setFloatUniform("nodeB",nodeB.x,nodeB.y);shader.setFloatUniform("nodeC",nodeC.x,nodeC.y);shader.setFloatUniform("intensity",intensity*scale);drawRect(ShaderBrush(shader))}
}
@Composable private fun TortureTest(){var text by remember{mutableStateOf("still an interface")};var on by remember{mutableStateOf(true)};Row(verticalAlignment=Alignment.CenterVertically){Switch(checked=on,onCheckedChange={on=it});Spacer(Modifier.width(10.dp));BasicTextField(value=text,onValueChange={text=it},singleLine=true,textStyle=TextStyle(color=Color.White,fontSize=14.sp),modifier=Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.10f)).padding(12.dp));Spacer(Modifier.width(8.dp));Button(onClick={text=if(text=="saved ✓")"again?!" else "saved ✓"}){Text("POKE")}}}

@Composable private fun ExperimentalChrome(selected:Specimen,labMode:Boolean,toggle:()->Unit,a:Offset,b:Offset,c:Offset,dragA:(Offset)->Unit,dragB:(Offset)->Unit,dragC:(Offset)->Unit){
 if(!labMode)return
 Box(Modifier.fillMaxSize()){
  GooNode("DRAG",a,dragA);GooNode("MELT",b,dragB);GooNode(selected.glyph,c,dragC)
  Surface(onClick=toggle,shape=RoundedCornerShape(50),color=Color.White.copy(.12f),modifier=Modifier.align(Alignment.CenterEnd).padding(10.dp)){Text("◫",Modifier.padding(12.dp),fontSize=18.sp)}
 }
}
@Composable private fun GooNode(label:String,pos:Offset,onDrag:(Offset)->Unit){
 Box(Modifier.offset{androidx.compose.ui.unit.IntOffset((pos.x-110).toInt(),(pos.y-110).toInt())}.size(74.dp).clip(RoundedCornerShape(50)).background(Color.White.copy(.13f)).pointerInput(label){detectDragGestures{change,drag->change.consume();onDrag(drag)}},contentAlignment=Alignment.Center){Text(label,fontSize=11.sp,color=Color.White)}
}

@Composable private fun WarpedInput(time:Float,intensity:Float){
 var value by remember{mutableStateOf("TYPE INTO THE WARP")}
 val shader=remember{RuntimeShader(WARP)}
 shader.setFloatUniform("resolution",900f,150f);shader.setFloatUniform("time",time);shader.setFloatUniform("amount",intensity)
 val effect=remember(shader){AndroidRenderEffect.createRuntimeShaderEffect(shader,"content").asComposeRenderEffect()}
 Box(Modifier.fillMaxWidth().padding(vertical=8.dp).height(54.dp).graphicsLayer{renderEffect=effect}.border(1.dp,Color.White.copy(.35f),RoundedCornerShape(18.dp)).clip(RoundedCornerShape(18.dp)).background(Color.White.copy(.09f)),contentAlignment=Alignment.CenterStart){
  BasicTextField(value=value,onValueChange={value=it},singleLine=true,textStyle=TextStyle(color=Color.White,fontSize=16.sp),modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp))
 }
}
