package otus.homework.coroutines

import retrofit2.http.GET
import retrofit2.Response

interface CatsServicePicture {

    @GET("v1/images/search")
    suspend fun getCatPicture(): Response<List<PictureDto>>
}