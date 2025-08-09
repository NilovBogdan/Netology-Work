package bogdan.nilov.netologywork.adapter

import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.UserResponse


interface OnInteractionListener {
    fun like(feedItem: FeedItem)
    fun delete(feedItem: FeedItem)
    fun edit(feedItem: FeedItem)
    fun selectUser(userResponse: UserResponse)
    fun openCard(feedItem: FeedItem)
    fun share(content: String)
}