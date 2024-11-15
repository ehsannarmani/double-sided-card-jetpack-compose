package ir.ehsannarmani.todo.cardtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.todo.cardtest.ui.theme.CardTestTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

const val dragThreshold = 3f

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val scope = rememberCoroutineScope()
                    val cardDegree = remember {
                        Animatable(0f)
                    }
                    val dragHolder = remember {
                        mutableStateOf(0f)
                    }
                    val releaseAnimation = remember {
                        mutableStateOf<AnimationSpec<Float>>(spring(
                            dampingRatio = 0.3f,
                            stiffness = Spring.StiffnessMediumLow
                        ))
                    }
                    val turnAnimation = remember {
                        mutableStateOf<AnimationSpec<Float>>(tween(1000))
                    }
                    Column(modifier= Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {

                                },
                                onDragEnd = {
                                    scope.launch {
                                        if (cardDegree.value.absoluteValue > 90f) {
                                            if (cardDegree.value > 0) {
                                                cardDegree.animateTo(
                                                    180f,
                                                    animationSpec = releaseAnimation.value
                                                )
                                            } else {
                                                cardDegree.animateTo(
                                                    -180f,
                                                    animationSpec = releaseAnimation.value
                                                )
                                            }
                                        } else {
                                            cardDegree.animateTo(
                                                0f,
                                                animationSpec = releaseAnimation.value
                                            )
                                        }
                                    }
                                },
                                onHorizontalDrag = { change, amount ->
                                    scope.launch {
                                        (if (change.position.x > dragHolder.value) {
                                            cardDegree.value + dragThreshold
                                        } else {
                                            cardDegree.value - dragThreshold
                                        }).also {
                                            cardDegree.snapTo(it.coerceIn(-180f, 180f))
                                        }
                                        dragHolder.value = change.position.x
                                    }
                                    change.consume()
                                }
                            )
                        }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Drag horizontally")
                        Spacer(modifier = Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(350.dp)
                                .graphicsLayer {
                                    rotationY = cardDegree.value
                                    val newScale = if (cardDegree.value.absoluteValue < 90) {
                                        1f - (cardDegree.value.absoluteValue / 180f * 0.5f)
                                    } else {
                                        .5f + (cardDegree.value.absoluteValue / 180f * 0.5f)
                                    }
                                    val scale = newScale.coerceIn(0.5f, 1f)
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF647EFF))
                            ,
                            contentAlignment = Alignment.Center
                        ){
                            if(cardDegree.value.absoluteValue > 90f){
                                Image(
                                    painter = painterResource(id = R.drawable.img_2),
                                    contentDescription = null,
                                    modifier= Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { rotationY = 180f },
                                    contentScale = ContentScale.Crop
                                )
                            }else{
                                Image(
                                    painter = painterResource(id = R.drawable.img),
                                    contentDescription = null,
                                    modifier=Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = {
                            scope.launch {
                                when(cardDegree.value){
                                    0f->{
                                        cardDegree.animateTo(180f, animationSpec = turnAnimation.value)
                                    }
                                    else->{
                                        cardDegree.animateTo(0f, animationSpec = turnAnimation.value)
                                    }
                                }
                            }
                        }) {
                            Text(text = "Turn")
                        }
                    }
                }
            }
        }
    }
}
operator fun Offset.minus(other:Offset) = Offset(x = x-other.x,y = y-other.y)
