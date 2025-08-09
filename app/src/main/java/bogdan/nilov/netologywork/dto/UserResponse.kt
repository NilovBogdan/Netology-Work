package bogdan.nilov.netologywork.dto

data class UserResponse(
    override val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
    val selected: Boolean = false
): FeedItem