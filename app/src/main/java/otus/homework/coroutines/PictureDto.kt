package otus.homework.coroutines

import com.google.gson.annotations.SerializedName

data class PictureDto(
    @field:SerializedName("url")
    val url: String
)
