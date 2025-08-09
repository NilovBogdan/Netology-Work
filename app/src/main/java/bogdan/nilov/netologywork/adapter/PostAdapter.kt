package bogdan.nilov.netologywork.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import bogdan.nilov.netologywork.R
import bogdan.nilov.netologywork.databinding.CardPostBinding
import bogdan.nilov.netologywork.dto.AttachmentType
import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.Post
import bogdan.nilov.netologywork.extension.loadAttachment
import bogdan.nilov.netologywork.extension.loadAvatar
import com.airbnb.lottie.LottieDrawable
import java.time.format.DateTimeFormatter

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, PostViewHolder>(FeedItemCallBack()) {

    override fun onViewRecycled(holder: PostViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, parent.context)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position) as Post
        holder.bind(item)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var player: ExoPlayer? = null

    fun bind(post: Post) {
        with(binding) {
            avatar.loadAvatar(post.authorAvatar)
            authorName.text = post.author
            datePublication.text =
                post.published.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            content.text = post.content
            buttonLike.text = post.likeOwnerIds.size.toString()
            buttonLike.isChecked = post.likedByMe

            fun setAttachmentVisibility(
                imageContentVisible: Boolean = false,
                videoContentVisible: Boolean = false,
                audioContentVisible: Boolean = false,
            ) {
                imageContent.isVisible = imageContentVisible
                videoContent.isVisible = videoContentVisible
                audioContent.isVisible = audioContentVisible
            }

            when (post.attachment?.type) {
                AttachmentType.IMAGE -> {
                    imageContent.loadAttachment(post.attachment.url)
                    setAttachmentVisibility(imageContentVisible = true)
                }

                AttachmentType.VIDEO -> {
                    player = ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(post.attachment.url))
                    }
                    videoContent.player = player
                    setAttachmentVisibility(videoContentVisible = true)
                }

                AttachmentType.AUDIO -> {
                    player = ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(post.attachment.url))
                    }
                    setAttachmentVisibility(audioContentVisible = true)
                }

                null -> {
                    releasePlayer()
                    setAttachmentVisibility()
                }
            }

            playPauseAudio.setOnClickListener {
                if (player?.isPlaying == true) {
                    player!!.playWhenReady = !player!!.playWhenReady
                } else {
                    player?.apply {
                        prepare()
                        play()
                    }
                }
            }

            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying){
                        binding.playPauseAudio.setIconResource(R.drawable.ic_pause_circle_24)
                        binding.mediaVisualizer.apply {
                            setMinProgress(0.0f)
                            setMaxProgress(1.0f)
                            repeatMode = LottieDrawable.REVERSE
                            repeatCount = LottieDrawable.INFINITE
                            playAnimation()
                        }
                    }else{
                        binding.playPauseAudio.setIconResource(R.drawable.ic_play_circle_24)
                        binding.mediaVisualizer.pauseAnimation()
                    }
                }

            })

            buttonLike.setOnClickListener {
                val scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1F, 1.25F, 1F)
                val scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1F, 1.25F, 1F)
                ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
                    duration = 1000
                    interpolator = BounceInterpolator()
                }.start()
                onInteractionListener.like(post)
            }
            buttonShare.setOnClickListener {
                onInteractionListener.share(post.content)
            }

            buttonOption.isVisible = post.ownedByMe
            buttonOption.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.delete(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                    gravity = Gravity.END
                }
                    .show()
            }

            binding.cardPost.setOnClickListener {
                onInteractionListener.openCard(post)
            }


        }
    }

    fun releasePlayer() {
        player?.apply {
            stop()
            release()
        }
    }

    fun stopPlayer() {
        player?.stop()
    }

}