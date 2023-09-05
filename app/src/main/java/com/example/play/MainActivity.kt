package com.example.play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.play.ga.BatchTimeTable
import com.example.play.ui.theme.PlayTheme
import com.example.play.viewModel.MyViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = MyViewModel()
        vm.setInputParser(applicationContext)

        setContent {
            PlayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    CustomSlider(vm)
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    teacherName: String = "",
    color: Color = MaterialTheme.colorScheme.secondary,
    width: Float = 150F,
    dayLabel: Boolean = false
) {
    Column(modifier = Modifier.width(width.dp)) {
        Text(
            text = text,
            Modifier
                .drawBehind {
                    val strokeWidth = 1F
                    drawLine(
                        color = Color.Gray,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height * 2F),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(8.dp)
                .fillMaxWidth(),
            color = color,
            fontWeight = (if (dayLabel) FontWeight.ExtraBold else FontWeight.Normal),
            fontSize = (if (dayLabel) 25.sp else 19.sp),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = teacherName,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.inversePrimary
        )
    }
}

@Composable
fun TableScreen(data: BatchTimeTable) {
    val scrollState = rememberScrollState()
    val timeLabelStart = 9 // 09 AM
    val periodLength = 1 // 1 Hour

    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = data.batchName!!,
            fontSize = 35.sp,
            lineHeight = 35.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Row(
            Modifier
                .horizontalScroll(scrollState, true)
                .fillMaxSize()
        ) {
            LazyColumn(Modifier.padding(16.dp)) {
                item { ->
                    Row(
                        Modifier.background(MaterialTheme.colorScheme.onPrimary)
                    ) {
                        TableCell("")

                        data.rows?.get(0)!!.forEachIndexed { it, _ ->
                            val delta1 = (timeLabelStart + it)
                            if (delta1 <= 12) {
                                val delta2 = (timeLabelStart + it + periodLength)
                                val timeLabel =
                                    "${(delta1 % 13)}AM - ${(if (delta2 > 12) (delta2 % 13) + 1 else (delta2 % 13))}${if (delta2 > 12) "PM" else "AM"}"
                                TableCell(text = timeLabel)
                            } else {
                                val delta2 = (timeLabelStart + it + periodLength)
                                val timeLabel = "${(delta1 % 12)}PM - ${(delta2 % 12)}PM"
                                TableCell(text = timeLabel)
                            }
                        }
                    }
                }

                itemsIndexed(data.rows!!) { idx, item ->
                    Row(
                        Modifier.background(
                            if (idx % 2 == 0) Color.White else Color(200, 200, 200)
                        )
                    ) {
                        // For Day labels
                        TableCell(text = "Day ${idx + 1}", color = Color.Black, dayLabel = true)

                        // actual data
                        item.forEachIndexed { idx, it ->
                            if (idx == 3) {
                                TableCell(
                                    text = "BREAK",
                                    teacherName = "",
                                    color = Color.DarkGray
                                )
                            } else {
                                TableCell(
                                    text = it.subjectName,
                                    teacherName = if (it.teacherName.equals(
                                            "Free",
                                            true
                                        )
                                    ) "" else it.teacherName,
                                    color = Color.Black
                                )
                            }
                        }

                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(vm: MyViewModel) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val vmData = vm.getTables()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 5 }

    Scaffold(bottomBar = {
        NavigationBar(
            modifier = Modifier.height(56.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            NavigationBarItem(selected = false, onClick = {
                scope.launch {
                    if (pagerState.currentPage > 0 && vm.processed.value) {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            }, icon = {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            })

            NavigationBarItem(selected = false, onClick = {
                if (vm.processed.value) vm.reset()
            }, icon = {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            })

            NavigationBarItem(selected = false, onClick = {
                scope.launch {
                    if (pagerState.currentPage < vmData.size && vm.processed.value) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },

                icon = {
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                })
        }
    },
        content = { padding ->
            NavHost(navController = navController, startDestination = Screens.Home) {
                composable(route = Screens.Splash) {
                    //                SplashScreen(navController)
                }

                composable(route = Screens.Home) {
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 0.dp,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        val pageOffset =
                            (pagerState.currentPage - it) + pagerState.currentPageOffsetFraction

                        val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)

                        Card(shape = RoundedCornerShape(
                            0.dp
                        ), modifier = Modifier
                            .graphicsLayer {
                                scaleX = scaleFactor
                                scaleY = scaleFactor
                            }
                            .fillMaxWidth()
                            .padding(padding)
                        ) {
                            if (vm.processing.value) {
                                Box(
                                    Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                        )

                                        Text(
                                            text = "Processing",
                                            fontSize = 40.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            text = "Generation: ${vm.generation.intValue}",
                                            fontSize = 25.sp, textAlign = TextAlign.Center
                                        )

                                        Text(
                                            text = "Fitness: ${
                                                String.format(
                                                    "%.2f",
                                                    vm.fitness.doubleValue
                                                )
                                            }",
                                            fontSize = 19.sp,
                                            textAlign = TextAlign.Center
                                        )

                                        if (vm.best.value) {
                                            Text(
                                                text = "Found Best",
                                                fontSize = 19.sp,
                                                color = Color.Green,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                if (vm.processed.value) {
                                    TableScreen(data = vmData[it])
                                } else {
                                    Box(
                                        Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(onClick = {
                                            vm.generateSolution()
                                        }) {
                                            Text(
                                                text = "Generate",
                                                fontSize = 40.sp,
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
