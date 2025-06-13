package com.alegrarsio.mobpro1.network

import com.alegrarsio.mobpro1.model.GeneralApiResponse
import com.alegrarsio.mobpro1.model.ImageListApiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query


private const val BASE_URL = "https://aleapi.my.id/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ImageApiService {
    @GET("api.php")
    suspend fun getAllImages(
        @Header("Authorization") userEmail: String
    ): ImageListApiResponse

    @Multipart
    @POST("api.php")
    suspend fun uploadImage(
        @Header("Authorization") userEmail: String,
        @Part("nama") nama: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part gambar: MultipartBody.Part
    ): GeneralApiResponse

    @Multipart
    @POST("api.php")
    suspend fun updateImage(
        @Header("Authorization") userEmail: String,
        @Query("id") imageId: Int,
        @Part("nama") nama: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part gambar: MultipartBody.Part?
    ): GeneralApiResponse

    @DELETE("api.php")
    suspend fun deleteImage(
        @Header("Authorization") userEmail: String,
        @Query("id") imageId: Int
    ): GeneralApiResponse
}

object ImageApi {
    val service: ImageApiService by lazy {
        retrofit.create(ImageApiService::class.java)
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }
