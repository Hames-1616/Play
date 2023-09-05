package com.example.play.ga

import java.io.*
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
class TimeTable(inputParser: InputParser) {
    private var subjectNo = 0
    private var hourCount = 1
    private val days = inputParser.daysPerWeek
    private val hours = inputParser.hoursPerDay
    private val studentGroups = inputParser.numStudentGroups
    private val stGroups = inputParser.studentGroup

    init {
        slot.clear()
    }

    fun generateSlots(): List<Slot?> {
        for (i in 0..<studentGroups) {
            subjectNo = 0

            for (j in 0..<hours * days) {
                val studentGroup = stGroups[i]

                if (subjectNo >= studentGroup.numSubjects) {
                    slot.add(null)
                } else {
                    val sl = Slot(
                        studentGroup,
                        studentGroup.teacherID!![subjectNo],
                        studentGroup.subjects[subjectNo]
                    )

                    slot.add(sl)

                    if (hourCount < studentGroup.hours[subjectNo]) {
                        hourCount++
                    } else {
                        hourCount = 1
                        subjectNo++
                    }
                }
            }
        }

        return slot
    }

    companion object {
        val slot: ArrayList<Slot?> = ArrayList()
    }
}

@OptIn(ExperimentalStdlibApi::class)
class Gene(i: Int, inputParser: InputParser) : Serializable {
    val slotNo: ArrayList<Int> = ArrayList()
    private val days = inputParser.daysPerWeek
    private val hours = inputParser.hoursPerDay
    private val random = Random()
    private val added = ArrayList<Int>()

    init {
        for (j in 0..<days * hours) {
            var rnd: Int = random.nextInt(days * hours)
            while (added.contains(rnd)) {
                rnd = random.nextInt(days * hours)
            }
            added.add(rnd)
            slotNo.add(i * days * hours + rnd)
        }
    }

    fun deepClone(): Gene {
        val bos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(bos)
        oos.writeObject(this)

        val bis = ByteArrayInputStream(bos.toByteArray())
        val ois = ObjectInputStream(bis)
        return ois.readObject() as Gene
    }
}

@OptIn(ExperimentalStdlibApi::class)
class Chromosome(private val inputParser: InputParser) : Serializable {
    private val hours = inputParser.hoursPerDay
    private val days = inputParser.daysPerWeek
    private val numStudentGroups = inputParser.numStudentGroups
    var fitness = 0.0
    private var point = 0
    val gene: ArrayList<Gene> = ArrayList()

    init {
        for (i in 0..<numStudentGroups) {
            gene.add(Gene(i, inputParser))
        }

        fitness = calcFitness()
    }

    fun calcFitness(): Double {
        point = 0

        for (i in 0..<hours * days) {
            val teacherList = ArrayList<Int>()

            for (j in 0..<numStudentGroups) {
                var slot: Slot?
                val tt = TimeTable(inputParser).generateSlots()

                slot = (tt[gene[j].slotNo[i]])

                if (slot != null) {
                    if (teacherList.contains(slot.teacherId)) {
                        point++
                    } else {
                        teacherList.add(slot.teacherId)
                    }
                }
            }
        }

        val newFitness = 1 - (point / ((numStudentGroups - 1.0) * hours * days))
        point = 0
        this.fitness = newFitness
        return newFitness
    }

    fun deepClone(): Chromosome {
        val bos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(bos)
        oos.writeObject(this)
        val bis = ByteArrayInputStream(bos.toByteArray())
        val ois = ObjectInputStream(bis)
        return ois.readObject() as Chromosome
    }

    companion object {
        fun printTimeTable(
            chromo: Chromosome,
            inputParser: InputParser
        ): ArrayList<BatchTimeTable> {
            val timeTables = ArrayList<BatchTimeTable>()

            for (i in 0..<chromo.numStudentGroups) {
                var status = false
                var l = 0
                val batchTable = BatchTimeTable("", rows = ArrayList())

                while (!status) {
                    if (TimeTable.slot[chromo.gene[i].slotNo[l]] != null) {
                        //println("Batch " + TimeTable.slot[gene[i].slotNo[l]]!!.studentsGroup.name + " Timetable-")
                        batchTable.batchName =
                            "Batch " + TimeTable.slot[chromo.gene[i].slotNo[l]]!!.studentsGroup.name
                        status = true
                    }
                    l++
                }

                for (j in 0..<chromo.days) {
                    val batchTableCols = ArrayList<TeacherSubject>()
                    for (k in 0..<chromo.hours) {
                        if (TimeTable.slot[chromo.gene[i].slotNo[k + j * chromo.hours]] != null) {
                            //print(TimeTable.slot[gene[i].slotNo[k + (j * hours)]]!!.subject + "\t ")
                            val teacherName =
                                inputParser.teacherGroup[TimeTable.slot[chromo.gene[i].slotNo[k + j * chromo.hours]]!!.teacherId].teacherName
                            batchTableCols.add(
                                TeacherSubject(
                                    subjectName = TimeTable.slot[chromo.gene[i].slotNo[k + j * chromo.hours]]!!.subject,
                                    teacherName = teacherName
                                )
                            )
                        } else {
                            batchTableCols.add(TeacherSubject("Free", "Free"))
                        }
                    }

                    //println()
                    batchTable.rows!!.add(batchTableCols)
                }

                timeTables.add(batchTable)
            }

            return timeTables
        }
    }
}

@Suppress("KotlinConstantConditions")
@OptIn(ExperimentalStdlibApi::class)
class GA(private val inputParser: InputParser) {
    // population...
    private val population: ArrayList<Chromosome> = ArrayList()
    private var popFitness = 0.0
    private var numGeneration = 0
    private var bestFound = false
    private lateinit var parentA: Chromosome
    private lateinit var parentB: Chromosome
    private lateinit var child: Chromosome

    fun reset() {
        population.clear()
        popFitness = 0.0
        numGeneration = 0
        bestFound = false
    }

    // Initializing the population of "Gene's" ...
    fun initPopulation() {
        for (i in 0..<POP_SIZE) {
            val chromo = Chromosome(inputParser)
            popFitness += chromo.fitness
            population.add(chromo)
        }

        population.sortBy { -it.fitness }
    }

    fun createNextGeneration(callback: (numGen: Int, fitness: Double, best: Boolean) -> Unit): Chromosome? {
        var mostFit: Chromosome? = null

        for (x in 0..<MAX_GENS) {
            val newGen: ArrayList<Chromosome> = ArrayList()
            var newGenFitness = 0.0
            var looped = 0

            for (i in 0..<POP_SIZE / 10) {
                newGen.add(population[i].deepClone())
                newGenFitness += population[i].calcFitness()
                looped = i
            }

            while (looped < POP_SIZE) {
                parentA = selectParentRoulette()
                parentB = selectParentRoulette()

                // Crossover
                child = if (Random().nextDouble() < inputParser.crossOverRate) {
                    crossOver(parentA, parentB)
                } else {
                    parentA
                }

                // Mutation
//                customMutation(child)
                swapMutation(child)

                if (child.fitness >= 1.0) bestFound = true

                if (child.fitness >= 1.0 && numGeneration >= 5) {
                    mostFit = child.deepClone()
                    return mostFit
                }

                newGen.add(child)
                newGenFitness += child.calcFitness()
                looped++
            }

            if (looped >= POP_SIZE) mostFit = child.deepClone()

            population.clear()
            population.addAll(newGen)
            population.sortBy { -it.fitness }
            callback(numGeneration + 1, newGenFitness, bestFound)
            numGeneration++
        }

        return mostFit
    }

    //probabilistic algo
    //roulette wheel selection
    private fun selectParentRoulette(): Chromosome {
        popFitness /= 10.0
        val rand = Random().nextDouble() * popFitness
        var currentSum = 0.0
        var i = 0

        while (currentSum <= rand) {
            currentSum += population[i++].calcFitness()
        }

        return population[--i].deepClone()
        //returns the population with max fitness
    }

    private fun crossOver(parentA: Chromosome, parentB: Chromosome): Chromosome {
        val randInt = Random().nextInt(inputParser.numStudentGroups)
        val temp: Gene = parentA.gene[randInt].deepClone()

        //swap the genes => crossover to increase diversity - increased features
        parentA.gene[randInt] = parentB.gene[randInt].deepClone()
        parentB.gene[randInt] = temp

        return if (parentA.calcFitness() > parentB.calcFitness()) parentA else parentB
    }

    private fun customMutation(chromo: Chromosome) {
        var newFitness = 0.0
        val oldFitness = chromo.calcFitness()
        var i = 0
        val geneNo = Random().nextInt(inputParser.numStudentGroups)

        while (newFitness < oldFitness) {
            chromo.gene[geneNo] = Gene(geneNo, inputParser)
            newFitness = chromo.calcFitness()
            if (i++ >= 200) break
        }
    }

    // Alternative mutation algorithms
    private fun swapMutation(c: Chromosome) {
        val geneNo = Random().nextInt(inputParser.numStudentGroups)
        val slot1 = Random().nextInt(inputParser.hoursPerDay * inputParser.daysPerWeek)
        val slot2 = Random().nextInt(inputParser.hoursPerDay * inputParser.daysPerWeek)

        val temp = c.gene[geneNo].slotNo[slot1]
        c.gene[geneNo].slotNo[slot1] = c.gene[geneNo].slotNo[slot2]
        c.gene[geneNo].slotNo[slot2] = temp
    }

}
