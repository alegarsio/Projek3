package com.alegrarsio.ui.screen


import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alegrarsio.mobpro1.model.ImageItem
import com.alegrarsio.mobpro1.network.ApiStatus
import com.alegrarsio.mobpro1.network.ImageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class ImageViewModel : ViewModel() {

    var imageList = mutableStateOf(emptyList<ImageItem>())
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var apiStatus = MutableStateFlow(ApiStatus.LOADING)
        private set

    fun clearMessage() {
        errorMessage.value = null
    }

    fun retrieveData(userEmail: String) {

        viewModelScope.launch(Dispatchers.IO) {
            apiStatus.value = ApiStatus.LOADING
            errorMessage.value = null
            try {

                val response = ImageApi.service.getAllImages(userEmail)

                if (response.status.equals("success", ignoreCase = true)) {

                    imageList.value = response.data ?: emptyList()
                    apiStatus.value = ApiStatus.SUCCESS
                } else {
                    errorMessage.value = response.message ?: "Gagal mengambil daftar gambar."
                    apiStatus.value = ApiStatus.FAILED
                }
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Failure retrieveData: ${e.message}", e)

                apiStatus.value = ApiStatus.FAILED
            }
        }
    }


    fun uploadImage(userEmail: String,nama: String?, deskripsi: String?, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            apiStatus.value = ApiStatus.LOADING
            errorMessage.value = null
            try {
                val namaRequestBody = nama?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiRequestBody = deskripsi?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = bitmap.toMultipartBody()

                val result = ImageApi.service.uploadImage(
                    userEmail = userEmail,
                    nama = namaRequestBody,
                    deskripsi = deskripsiRequestBody,
                    gambar = imagePart
                )

                if (result.status.equals("success", ignoreCase = true)) {
                    errorMessage.value = result.message
                    retrieveData(userEmail)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Failure uploadImage: ${e.message}", e)
                errorMessage.value = "Gagal mengupload gambar: ${e.message}"
                apiStatus.value = ApiStatus.FAILED
            }
        }
    }

    fun deleteImage(userEmail: String,imageId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            errorMessage.value = null
            try {
                val result = ImageApi.service.deleteImage(userEmail,imageId)
                if (result.status.equals("success", ignoreCase = true)) {
                    errorMessage.value = result.message
                    retrieveData(userEmail)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Failure deleteImage: ${e.message}", e)
                errorMessage.value = "Gagal menghapus gambar: ${e.message}"
            }
        }
    }

    fun updateImage(
        userEmail: String,
        imageId: Int,
        newName: String?,
        newDescription: String?,
        newBitmap: Bitmap?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            errorMessage.value = null
            try {
                val namaBody = newName?.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiBody = newDescription?.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = newBitmap?.toMultipartBody()

                val result = ImageApi.service.updateImage(
                    userEmail = userEmail,
                    imageId = imageId,
                    nama = namaBody,
                    deskripsi = deskripsiBody,
                    gambar = imagePart
                )

                if (result.status.equals("success", ignoreCase = true) || result.status.equals("info", ignoreCase = true)) {
                    errorMessage.value = result.message
                    retrieveData(userEmail)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Failure updateImage: ${e.message}", e)
                errorMessage.value = "Gagal mengupdate gambar: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(fileName: String = "image.jpg"): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpeg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData("gambar", fileName, requestBody)
    }
}