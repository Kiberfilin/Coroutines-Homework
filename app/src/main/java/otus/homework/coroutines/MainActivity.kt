package otus.homework.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import otus.homework.coroutines.state.Result

class MainActivity : AppCompatActivity() {

    //lateinit var catsPresenter: CatsPresenter

    //private val diContainer = DiContainer()

    private lateinit var viewModel: CatsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, CatsViewModel.factory)[CatsViewModel::class.java]

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        viewModel.state.observe(this) {
            it?.let { result: Result ->
                when (result) {
                    is Result.Success<*> -> {
                        view.populate(result.data as DataDto)
                    }

                    is Result.Error -> {
                        view.showToast(result.errorMsg)
                    }
                }
            }
        }
        /*catsPresenter = CatsPresenter(
            diContainer.serviceFact,
            diContainer.servicePicture
        )
        view.presenter = catsPresenter
        catsPresenter.attachView(view)
        catsPresenter.onInitComplete()*/
        val onClick: View.OnClickListener = View.OnClickListener {
            viewModel.onInitComplete()
        }
        view.setOnButtonClickListener(onClick)
        viewModel.onInitComplete()
    }

    /*override fun onStop() {
        if (isFinishing) {
            catsPresenter.apply {
                cancelJob()
                detachView()
            }
        }
        super.onStop()
    }*/
}