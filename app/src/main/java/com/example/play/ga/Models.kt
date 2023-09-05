package com.example.play.ga

import java.io.Serializable

// Constants
const val POP_SIZE = 500
const val MAX_GENS = 25

// Data Models
class StudentsGroup(
    val id: Int,
    val name: String,
    var numSubjects: Int,
    val teacherID: ArrayList<Int>?,
    val subjects: ArrayList<String>,
    val hours: ArrayList<Int>
) : Serializable

class Teacher(
    val teacherId: Int,
    val teacherName: String,
    var subjectName: String,
    var batchesAssigned: Int = 0
) : Serializable

data class Slot(
    val studentsGroup: StudentsGroup, val teacherId: Int, val subject: String
)

data class TeacherSubject(val subjectName: String, val teacherName: String)

data class BatchTimeTable(
    var batchName: String?,
    val rows: ArrayList<ArrayList<TeacherSubject>>?
)
