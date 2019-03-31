package com.ekstkorn.hospitalporterapp.view.model

import com.ekstkorn.hospitalporterapp.JobStatus

data class UserProfile(
        var imageProfileUrl: String? = null,
        var profileName: String = "",
        var mobile: String = "",
        var userId: String = ""
)

data class JobStatusView(
        var status: JobStatus,
        var time: String,
        var textColor: Int
)

data class JobListView(val list: List<JobView>)

data class JobView(val time: String, val name: String, val building: String, val jobStatus: String)