package otus.homework.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import otus.homework.coroutines.state.Result
import retrofit2.Response

class CatsViewModel(
    private val catsServiceFact: CatsServiceFact,
    private val catsServicePicture: CatsServicePicture
) : ViewModel() {

    private val _state: MutableLiveData<Result?> = MutableLiveData(null)
    val state: LiveData<Result?> = _state

    private val exceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable -> handleThrowable(throwable) }

    fun onInitComplete() {
        viewModelScope.launch(exceptionHandler) {
            val factDeferred: Deferred<Response<Fact>> = async {
                catsServiceFact.getCatFact()
            }
            val pictureDtoDeferred: Deferred<Response<List<PictureDto>>> = async {
                catsServicePicture.getCatPicture()
            }
            val factResponse: Response<Fact> = factDeferred.await()
            val pictureDtoResponse: Response<List<PictureDto>> = pictureDtoDeferred.await()
            if (isResponseCorrect(factResponse) && isResponseCorrect(pictureDtoResponse)) {
                _state.postValue(
                    Result.Success(
                        DataDto(
                            factResponse.body()!!,
                            pictureDtoResponse.body()!!.first()
                        )
                    )
                )
            }
        }
    }

    private fun <T : Any> isResponseCorrect(response: Response<T>?): Boolean =
        response != null && response.isSuccessful && response.body() != null

    private fun handleThrowable(e: Throwable) {
        when (e) {
            is java.net.SocketTimeoutException -> {
                _state.postValue(
                    Result.Error("Не удалось получить ответ от сервером")
                )
            }

            else -> {
                CrashMonitor.trackWarning()
                _state.postValue(Result.Error(e.message.toString()))
            }
        }
    }

    companion object {

        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                val di = (application as App).diContainer
                return CatsViewModel(
                    di.serviceFact,
                    di.servicePicture
                ) as T
            }
        }
    }
}