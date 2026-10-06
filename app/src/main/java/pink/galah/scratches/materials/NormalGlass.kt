package pink.galah.scratches

import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val NORMAL_GLASS = """uniform shader content;
uniform float2 resolution;
uniform float time;
uniform float amount;
uniform float2 touch;

float heightField(float2 uv) {
    float2 m = touch / resolution;
    float d = distance(uv, m);
    float waves = sin(uv.x * 19.0 + sin(uv.y * 11.0) + time * .45) * .45;
    waves += sin(uv.y * 27.0 - uv.x * 7.0 - time * .31) * .25;
    waves += exp(-d*d*55.0) * sin(d*70.0-time*3.0) * .55;
    return waves;
}
half4 main(float2 p) {
    float2 uv = p / resolution;
    float e = .0025;
    float hx = heightField(uv + float2(e,0)) - heightField(uv - float2(e,0));
    float hy = heightField(uv + float2(0,e)) - heightField(uv - float2(0,e));
    float2 normal = float2(hx,hy) / (2.0*e);
    float2 refractOffset = normal * (2.2 + 5.5*amount);
    half4 glass = content.eval(p + refractOffset);
    half red = content.eval(p + refractOffset*1.28 + float2(1.8*amount,0)).r;
    half blue = content.eval(p + refractOffset*.78 - float2(1.8*amount,0)).b;
    float fresnel = pow(clamp(length(normal)*.07,0.0,1.0),1.4);
    return half4(half3(red,glass.g,blue) + half3(.18,.28,.34)*half(fresnel), glass.a);
}"""

@Composable
fun NormalGlassField(time: Float, amount: Float, touchX: Float, touchY: Float) {
    var text by remember { mutableStateOf("NORMAL-MAPPED GLASS") }
    val shader = remember { RuntimeShader(NORMAL_GLASS) }
    shader.setFloatUniform("resolution", 900f, 150f)
    shader.setFloatUniform("time", time)
    shader.setFloatUniform("amount", amount)
    shader.setFloatUniform("touch", touchX, touchY)
    val effect = remember(shader) {
        AndroidRenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
    }
    Box(
        Modifier.fillMaxWidth().height(58.dp).padding(vertical = 5.dp)
            .graphicsLayer { renderEffect = effect }
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(.13f)),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
        )
        TextLabel()
    }
}

@Composable
private fun TextLabel() {
    androidx.compose.material3.Text(
        "NORMAL FIELD GLASS",
        fontSize = 8.sp,
        color = Color.White.copy(.55f),
        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.End
    )
}
