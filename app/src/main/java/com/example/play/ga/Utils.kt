package com.example.play.ga

import android.content.Context
import com.example.play.R
import java.io.Serializable
import java.util.Scanner
import java.util.StringTokenizer

@OptIn(ExperimentalStdlibApi::class)
class InputParser(ctx: Context) : Serializable {
    val studentGroup = ArrayList<StudentsGroup>()
    val teacherGroup = ArrayList<Teacher>()
    val crossOverRate = 1.0f
    var numStudentGroups = 0
    private var numTeacherGroups = 0
    var hoursPerDay = 7
    var daysPerWeek = 5

    init {
        val file = ctx.resources.openRawResource(R.raw.input)
        val scanner = Scanner(file)

        while (scanner.hasNextLine()) {
            var line = scanner.nextLine()

            if (line.equals("studentgroups", true)) {
                var i = 0
                line = scanner.nextLine()
                do {
                    val tk = StringTokenizer(line, " ")

                    val name = tk.nextToken()
                    print(tk)
                    val numSubject = 0

                    val studentsGroup = StudentsGroup(
                        i, name, numSubject, ArrayList(), ArrayList(), ArrayList()
                    )

                    studentGroup.add(studentsGroup)

                    while (tk.hasMoreTokens()) {
                        studentGroup[i].subjects.add(tk.nextToken())
                        studentGroup[i].hours.add(Integer.parseInt(tk.nextToken()))
                        studentGroup[i].numSubjects += 1
                    }
                    i++
                    // go to next line ...
                    line = scanner.nextLine()
                } while (!line.equals("teachers", true))
                numStudentGroups = i
            }

            if (line.equals("teachers", true)) {
                var i = 0
                line = scanner.nextLine()
                do {
                    val tk = StringTokenizer(line, " ")

                    val teacher = Teacher(
                        i, tk.nextToken(), tk.nextToken(), 0
                    )

                    teacherGroup.add(teacher)
                    i++

                    // go to next line ...
                    line = scanner.nextLine()

                } while (!line.equals("end", true))
                numTeacherGroups = i
            }
        }

        for (i in 0..<numStudentGroups) {
            for (j in 0..<numTeacherGroups) {
                studentGroup[i].teacherID!!.add(-1)
            }
        }

        file.close()
        scanner.close()
        assignTeacher()
    }

    private fun assignTeacher() {
        for (i in 0..<numStudentGroups) {
            for (j in 0..<studentGroup[i].numSubjects) {
                var teacherID = -1
                var assignedMin = -1

                val subject = studentGroup[i].subjects[j]

                for (k in 0..<numTeacherGroups) {

                    if (teacherGroup[k].subjectName.equals(subject, true)) {

                        if (assignedMin == -1) {
                            assignedMin = teacherGroup[k].batchesAssigned
                            teacherID = k
                        } else if (assignedMin > teacherGroup[k].batchesAssigned) {
                            assignedMin = teacherGroup[k].batchesAssigned
                            teacherID = k
                        }
                    }
                }

                teacherGroup[teacherID].batchesAssigned += 1
                studentGroup[i].teacherID?.set(j, teacherID)
            }
        }
    }

    companion object {
        private var instance: InputParser? = null
        fun getInstance(ctx: Context): InputParser {
            if (instance == null) {
                instance = InputParser(ctx)
            }
            return instance as InputParser
        }
    }
}

