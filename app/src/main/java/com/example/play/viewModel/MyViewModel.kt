package com.example.play.viewModel

import android.content.Context
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.play.ga.BatchTimeTable
import com.example.play.ga.Chromosome
import com.example.play.ga.GA
import com.example.play.ga.InputParser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {
    private val tables: SnapshotStateList<BatchTimeTable> = mutableStateListOf()
    private lateinit var inputParser: InputParser
    private var ga: GA? = null

    var processing = mutableStateOf(false)
    var processed = mutableStateOf(false)
    var best = mutableStateOf(false)
    var generation = mutableIntStateOf(0)
    var fitness = mutableDoubleStateOf(0.0)

    fun setInputParser(ctx: Context) {
        inputParser = InputParser.getInstance(ctx)
        ga = GA(inputParser)
    }

    fun getTables() = tables.toList()

    fun reset() {
        // Reset View Model
        processed.value = false
        processing.value = false
        generation.intValue = 0
        fitness.doubleValue = 0.0

        // Reset GA
        ga!!.reset()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun generateSolution() {
        GlobalScope.launch(Dispatchers.Default) {
            processing.value = true
            tables.clear()

            ga!!.initPopulation()//intial 500 peeps
            val chromo = ga!!.createNextGeneration { gen, fit, bestFound ->
                generation.intValue = gen
                fitness.doubleValue = fit
                best.value = bestFound
            }!!
            tables.addAll(Chromosome.printTimeTable(chromo, inputParser))

            processed.value = true
            processing.value = false
        }
    }
}
