package bogdan.nilov.netologywork.extension

import android.widget.ImageView
import bogdan.nilov.netologywork.R
import com.bumptech.glide.Glide


fun ImageView.loadAvatar(url: String?) {
    Glide.with(this)
        .load(url)
        .error(R.drawable.no_avatar2)
        .placeholder(R.drawable.no_avatar2)
        .timeout(10_000)
        .circleCrop()
        .into(this)
}

fun ImageView.loadAttachment(url: String?) {
    if (url == null) {
        return
    }
    Glide.with(this)
        .load(url)
        .error(R.drawable.no_avatar2)
        .timeout(10_000)
        .into(this)
}