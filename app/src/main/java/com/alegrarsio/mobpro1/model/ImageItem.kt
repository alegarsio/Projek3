package com.alegrarsio.mobpro1.model

data class ImageItem(
    val id: Int,
    val imagepath: String,
    val nama: String?,
    val deskripsi: String?,
    val mimetype: String?,
    val upload_date: String,
    val imageUrl: String? = null
)