package pink.galah.scratches
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable fun MaterialBench(specimen:Specimen,intensity:Float,onIntensity:(Float)->Unit,speed:Float,onSpeed:(Float)->Unit,scale:Float,onScale:(Float)->Unit,time:Float,touchX:Float,touchY:Float){
 Column(Modifier.fillMaxWidth().background(Color.Black.copy(.30f),RoundedCornerShape(28.dp)).padding(14.dp)){
  Row(verticalAlignment=Alignment.CenterVertically){Text(specimen.glyph+" "+specimen.name.uppercase(),fontSize=11.sp);Spacer(Modifier.weight(1f));Text("MATERIAL BENCH",fontSize=9.sp,color=Color.White.copy(.45f))}
  LivingRail("INTENSITY",intensity,0f,2f,onIntensity,time)
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){LivingRail("TIME",speed,.1f,3f,onSpeed,time,Modifier.weight(1f));LivingRail("ZOOM",scale,.35f,3f,onScale,time+2f,Modifier.weight(1f))}
  NormalGlassField(time,intensity,touchX,touchY);WarpedInput(time,intensity);MutantInputs(time,intensity);CreatureControls(time)
 }
}
@Composable private fun LivingRail(label:String,value:Float,min:Float,max:Float,onValue:(Float)->Unit,time:Float,modifier:Modifier=Modifier){
 Column(modifier.padding(vertical=4.dp)){Text(label+"  "+"%.2f".format(value),fontSize=9.sp,color=Color.White.copy(.65f));Canvas(Modifier.fillMaxWidth().height(34.dp).pointerInput(label){detectDragGestures(onDragStart={p->onValue((min+(p.x/size.width)*(max-min)).coerceIn(min,max))},onDrag={change,_->onValue((min+(change.position.x/size.width)*(max-min)).coerceIn(min,max))})}){val t=((value-min)/(max-min)).coerceIn(0f,1f);val y=size.height/2;val active=size.width*t;for(i in 0..24){val x=size.width*i/24f;val wob=kotlin.math.sin(time*2f+i*.7f)*3f;drawCircle(Color.White.copy(if(x<=active).38f else .10f),5f+(if(x<=active)2f else 0f),Offset(x,y+wob))};drawCircle(Color.White.copy(.85f),10f,Offset(active,y+kotlin.math.sin(time*2f+t*12f)*3f),style=Stroke(3f))}}
}

@Composable private fun CreatureControls(time:Float){
 var on by androidx.compose.runtime.remember{androidx.compose.runtime.mutableStateOf(true)}
 var text by androidx.compose.runtime.remember{androidx.compose.runtime.mutableStateOf("still an interface")}
 var poke by androidx.compose.runtime.remember{androidx.compose.runtime.mutableFloatStateOf(0f)}
 Row(Modifier.fillMaxWidth().height(62.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)){
  Canvas(Modifier.size(76.dp,48.dp).pointerInput(Unit){detectTapGestures{on=!on}}){val left=Offset(size.width*.28f,size.height*.5f);val right=Offset(size.width*.72f,size.height*.5f);val pulse=(kotlin.math.sin(time*3f)+1f)*.5f;drawCircle(Color.White.copy(if(on).18f else .07f),19f,left);drawCircle(Color.White.copy(if(on).48f else .15f),19f,right);if(on){val mid=Offset(size.width*.5f,size.height*.5f);drawCircle(Color.White.copy(.22f+.10f*pulse),22f,mid)}}
  androidx.compose.foundation.text.BasicTextField(value=text,onValueChange={text=it},singleLine=true,textStyle=androidx.compose.ui.text.TextStyle(color=Color.White,fontSize=14.sp),modifier=Modifier.weight(1f).background(Color.White.copy(.08f),RoundedCornerShape(50)).padding(13.dp))
  Canvas(Modifier.size(82.dp,52.dp).pointerInput(Unit){detectTapGestures(onPress={poke=1f;tryAwaitRelease();poke=0f})}){val squash=1f-poke*.22f;drawOval(Color.White.copy(.30f+poke*.22f),topLeft=Offset(3f,size.height*(1f-squash)/2),size=androidx.compose.ui.geometry.Size(size.width-6f,size.height*squash));drawCircle(Color.White.copy(.18f),8f+poke*10f,Offset(size.width*.5f,size.height*.5f))}}
 }
}
