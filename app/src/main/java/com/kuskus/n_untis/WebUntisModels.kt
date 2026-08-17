package com.kuskus.n_untis

data class JsonRpcRequest(
    val id: String,
    val method: String,
    val params: Any,
    val jsonrpc: String = "2.0"
)

data class AuthenticateParams(
    val user: String,
    val password: String,
    val client: String = "MyUntisClient"
)

data class AuthenticateResponse(
    val jsonrpc: String?,
    val id: String?,
    val result: SessionInfo?,
    val error: ApiError?
)

data class SessionInfo(
    val sessionId: String,
    val personType: Int,
    val personId: Int,
    val klasseId: Int?
)

data class ApiError(
    val code: Int?,
    val message: String?
)

data class TimetableParams(
    val options: TimetableOptions
)

data class TimetableOptions(
    val id: Int,
    val element: TimetableElement,
    val startDate: String,
    val endDate: String,
    val showLsText: Boolean = true,
    val showStudentgroup: Boolean = true,
    val showLsNumber: Boolean = true,
    val showSubstText: Boolean = true,
    val showInfo: Boolean = true,
    val showBooking: Boolean = true,
    val klasseFields: List<String> = listOf(
        "id",
        "name",
        "longname",
        "externalkey"
    ),
    val roomFields: List<String> = listOf(
        "id",
        "name",
        "longname",
        "externalkey"
    ),
    val subjectFields: List<String> = listOf(
        "id",
        "name",
        "longname",
        "externalkey"
    ),
    val teacherFields: List<String> = listOf(
        "id",
        "name",
        "longname",
        "externalkey"
    )
)

data class TimetableElement(
    val id: Int,
    val type: Int,
    val keyType: String = "id"
)

data class TimetableResponse(
    val jsonrpc: String?,
    val id: String?,
    val result: List<Lesson>?,
    val error: ApiError?
)

data class Lesson(
    val id: Int?,
    val date: String?,
    val startTime: Int?,
    val endTime: Int?,
    val su: List<Subject>?,
    val te: List<Teacher>?,
    val ro: List<Room>?,
    val kl: List<ClassInfo>?,
    val lstext: String?,
    val substText: String?,
    val info: String?,
    val code: String?,
    val activityType: String?
)

data class Subject(
    val id: Int?,
    val name: String?,
    val longname: String?
)

data class Teacher(
    val id: Int?,
    val name: String?,
    val longname: String?
)

data class Room(
    val id: Int?,
    val name: String?,
    val longname: String?
)

data class ClassInfo(
    val id: Int?,
    val name: String?,
    val longname: String?
)