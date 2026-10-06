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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable fun MaterialBench(specimen:Specimen,intensity:Float,onIntensity:(Float)->Unit,speed:Float,onSpeed:(Float)->Unit,scale:Float,onScale:(Float)->Unit,time:Float,touchX:Float,touchY:Float){
 Column(Modifier.fillMaxWidth().background(Color.Black.copy(.30f),RoundedCornerShape(28.dp)).padding(14.dp)){
  Row(verticalAlignment=Alignment.CenterVertically){Text(specimen.glyph+" "+specimen.name.uppercase(),fontSize=11.sp);Spacer(Modifier.weight(1f));Text("MATERIAL BENCH",fontSize=9.sp,color=Color.White.copy(.45f))}
  LivingRail("INTENSITY",intensity,0f,2f,onIntensity,time)
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){LivingRail("TIME",speed,.1f,3f,onSpeed,time,Modifier.weight(1f));LivingRail("ZOOM",scale,.35f,3f,onScale,time+2f,Modifier.weight(1f))}
  NormalGlassField(time,intensity,touchX,touchY);WarpedInput(time,intensity);MutantInputs(time,intensity);TortureTest()
 }
}
@Composable private fun LivingRail(label:String,value:Float,min:Float,max:Float,onValue:(Float)->Unit,time:Float,modifier:Modifier=Modifier){
 Column(modifier.padding(vertical=4.dp)){Text(label+"  "+"%.2f".format(value),fontSize=9.sp,color=Color.White.copy(.65f));Canvas(Modifier.fillMaxWidth().height(34.dp).pointerInput(label){detectDragGestures(onDragStart={p->onValue((min+(p.x/size.width)*(max-min)).coerceIn(min,max))},onDrag={change,_->onValue((min+(change.position.x/size.width)*(max-min)).coerceIn(min,max))})}){val t=((value-min)/(max-min)).coerceIn(0f,1f);val y=size.height/2;val active=size.width*t;for(i in 0..24){val x=size.width*i/24f;val wob=kotlin.math.sin(time*2f+i*.7f)*3f;drawCircle(Color.White.copy(if(x<=active).38f else .10f),5f+(if(x<=active)2f else 0f),Offset(x,y+wob))};drawCircle(Color.White.copy(.85f),10f,Offset(active,y+kotlin.math.sin(time*2f+t*12f)*3f),style=Stroke(3f))}}
}
