package com.ekstkorn.hospitalporterapp.module

import com.ekstkorn.hospitalporterapp.model.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface WebServiceApi {
//    @POST("auth/")
//    fun login(@Body request: ): Single<ResponseBody>

    @GET("buildings")
    fun getAllBuilding() : Single<List<Building>>

    @POST("auth")
    fun authenUser(@Body request: AuthenRequest) : Single<AuthResponse>

    @GET("user")
    fun getUserProfile(@Query("userid") userId: String) : Single<UserProfileResponse>

    @GET("job")
    fun getJobStatus(@Query("userid") userId: String) : Single<JobStatusResponse>

    @GET("working-history")
    fun getJobList(@Query("userid") userId: String,
                   @Query("fromDate") fromData: String,
                   @Query("toDate") toDate: String) : Single<JobListResponse>

}

