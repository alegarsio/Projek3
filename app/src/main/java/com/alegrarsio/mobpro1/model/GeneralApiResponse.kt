package com.alegrarsio.mobpro1.model

data class GeneralApiResponse(
    val status: String,
    val message: String,
    val id: Int? = null,
    val imageUrl: String? = null,
    val imagePath: String? = null,
    val nama: String? = null,
    val deskripsi: String? = null
)