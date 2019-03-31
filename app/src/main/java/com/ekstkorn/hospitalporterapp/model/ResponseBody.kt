package com.ekstkorn.hospitalporterapp.model

data class Building(val buildingId: String, val buildingName: String)

data class AuthResponse(val userId: String, val username: String)

data class UserProfileResponse(val userId: String,
                               val firstName: String,
                               val lastName: String,
                               val mobile: String,
                               val urlProfile: String,
                               val departmentId: String,
                               val departmentName: String,
                               val roleId: String,
                               val roleName: String,
                               val buildingId: String,
                               val buildingName: String,
                               val username: String,
                               val password: String,
                               val isOnline: String)

data class JobStatusResponse(
    val endDateTime: String,
    val firstName: String,
    val isAvailable: String,
    val isOnline: String,
    val jobBuildingId: String,
    val jobBuildingName: String,
    val jobDate: String,
    val jobId: String,
    val lastName: String,
    val mobile: String,
    val patientName: String,
    val remainingTime: String,
    val startDateTime: String,
    val status: String,
    val totalJobComplete: Int,
    val userId: String
)

//data class JobListResponse(val jobList: List<JobResponse>)

data class JobListResponse(
    val completeDateTime: String,
    val endDateTime: String,
    val jobBuildingId: String,
    val jobBuildingName: String,
    val jobDate: String,
    val jobId: String,
    val patientName: String,
    val startDateTime: String,
    val status: String,
    val userId: String
)
