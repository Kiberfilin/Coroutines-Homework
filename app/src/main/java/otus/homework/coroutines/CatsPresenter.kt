package otus.homework.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null
    private var job: Job? = null

    fun onInitComplete() {
        job = CoroutineScope(Dispatchers.Main + CoroutineName("CatsCoroutine"))
            .launch {
                val response: Response<Fact> = try {
                    catsService.getCatFact()
                } catch (e: Throwable) {
                    when (e) {
                        is java.net.SocketTimeoutException ->
                            _catsView?.showToast("Не удалось получить ответ от сервером")

                        else -> {
                            CrashMonitor.trackWarning()
                            _catsView?.showToast(e.message.toString())
                        }
                    }
                    return@launch
                }
                if (response.isSuccessful && response.body() != null) {
                    _catsView?.populate(response.body()!!)
                } else {
                    CrashMonitor.trackWarning()
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