package otus.homework.coroutines

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class CatsPresenter(
    private val catsServiceFact: CatsServiceFact,
    private val catsServicePicture: CatsServicePicture
) {

    private var _catsView: ICatsView? = null
    private var job: Job? = null

    fun onInitComplete() {
        job = Job()
        CoroutineScope(
            Dispatchers.Main + CoroutineName("CatsCoroutine") + job as CompletableJob
        ).launch {
            val factDeferred: Deferred<Response<Fact>?> = async {
                try {
                    catsServiceFact.getCatFact()
                } catch (e: Throwable) {
                    handleThrowable(e)
                    null
                }
            }
            val pictureDtoDeferred: Deferred<Response<List<PictureDto>>?> = async {
                try {
                    catsServicePicture.getCatPicture()
                } catch (e: Throwable) {
                    handleThrowable(e)
                    null
                }
            }
            val factResponse: Response<Fact>? = factDeferred.await()
            val pictureDtoResponse: Response<List<PictureDto>>? = pictureDtoDeferred.await()

            if (isResponseCorrect(factResponse) && isResponseCorrect(pictureDtoResponse)) {
                _catsView?.populate(
                    DataDto(factResponse!!.body()!!, pictureDtoResponse!!.body()!!.first())
                )
            } else {
                CrashMonitor.trackWarning()
            }
        }
    }

    private fun <T : Any> isResponseCorrect(response: Response<T>?): Boolean =
        response != null && response.isSuccessful && response.body() != null

    private fun handleThrowable(e: Throwable) {
        when (e) {
            is java.net.SocketTimeoutException ->
                _catsView?.showToast("Не удалось получить ответ от сервером")

            else -> {
                CrashMonitor.trackWarning()
                _catsView?.showToast(e.message.toString())
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun cancelJob() {
        job?.cancel()
    }

    fun detachView() {
        _catsView = null
    }
}