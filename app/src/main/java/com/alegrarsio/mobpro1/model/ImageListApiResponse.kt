package com.alegrarsio.mobpro1.model

data class ImageListApiResponse(
    val status: String,
    val data: List<ImageItem>?,
    val message: String?

)
