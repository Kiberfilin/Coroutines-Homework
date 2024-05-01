package otus.homework.coroutines

import retrofit2.http.GET
import retrofit2.Response

interface CatsServiceFact {

    @GET("fact")
    suspend fun getCatFact(): Response<Fact>
}