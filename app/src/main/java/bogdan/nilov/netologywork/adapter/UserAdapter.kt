package bogdan.nilov.netologywork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import bogdan.nilov.netologywork.databinding.CardUserBinding
import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.UserResponse
import bogdan.nilov.netologywork.extension.loadAvatar

class UserAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val selectUser: Boolean,
    private val selectedUsers: List<Long>? = null
) : PagingDataAdapter<FeedItem, UserViewHolder>(FeedItemCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener, selectUser)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position) as UserResponse
        holder.bind(if (selectedUsers?.firstOrNull { it == item.id } == null) item else item.copy(
            selected = true
        ))
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onInteractionListener: OnInteractionListener,
    private val selectUser: Boolean
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(userResponse: UserResponse) {
        with(binding) {
            authorName.text = userResponse.name
            authorLogin.text = userResponse.login
            authorAvatar.loadAvatar(userResponse.avatar)
            checkBox.isVisible = selectUser
            checkBox.isChecked = userResponse.selected

            checkBox.setOnClickListener {
                onInteractionListener.selectUser(userResponse)
            }

            cardUser.setOnClickListener {
                onInteractionListener.openCard(userResponse)
            }
        }
    }
}