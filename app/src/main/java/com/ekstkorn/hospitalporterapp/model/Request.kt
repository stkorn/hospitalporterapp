package com.ekstkorn.hospitalporterapp.model

data class AuthenRequest(var userName: String, var password: String)

data class CreateJobRequest(
    val buildingId: String,
    val jobstatus: String,
    val patientName: String,
    val userId: String
)

data class CompleteJobRequest(
    val buildingId: String,
    val jobId: String,
    val jobstatus: String,
    val patientName: String,
    val userId: String
)

data class LogoutRequest(val username: String)
