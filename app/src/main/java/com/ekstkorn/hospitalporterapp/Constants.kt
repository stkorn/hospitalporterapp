package com.ekstkorn.hospitalporterapp

const val USERID_EXTRA = "userIdExtra"

enum class JobStatus(val status: String){
    AVAILABLE(""), BUSY(""), WORKING("WORKING"), COMPLETE("COMPLETE")
}