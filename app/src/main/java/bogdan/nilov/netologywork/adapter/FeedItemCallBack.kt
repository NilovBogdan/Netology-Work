package bogdan.nilov.netologywork.adapter

import androidx.recyclerview.widget.DiffUtil
import bogdan.nilov.netologywork.dto.FeedItem

class FeedItemCallBack : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}