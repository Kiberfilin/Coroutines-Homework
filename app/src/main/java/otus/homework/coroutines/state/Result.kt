package otus.homework.coroutines.state


sealed class Result {
    data class Success<T>(val data: T) : Result()
    data class  Error(val errorMsg: String) : Result()
}
