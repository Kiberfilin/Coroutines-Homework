package otus.homework.coroutines

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso

class CatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ICatsView {

    //var presenter: CatsPresenter? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun populate(data: DataDto) {
        findViewById<TextView>(R.id.fact_textView).text = data.fact.fact
        Picasso.get().load(data.pictureDto.url)
            .into(findViewById<ImageView>(R.id.imageView))
    }

    override fun showToast(toastText: String) =
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

    override fun setOnButtonClickListener(onClick: OnClickListener) {
        findViewById<Button>(R.id.button).setOnClickListener(onClick)
    }
}

interface ICatsView {

    fun populate(data: DataDto)
    fun showToast(toastText: String)
    fun setOnButtonClickListener(onClick: View.OnClickListener)
}