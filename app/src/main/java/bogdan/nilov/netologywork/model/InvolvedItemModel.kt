package bogdan.nilov.netologywork.model

import bogdan.nilov.netologywork.dto.UserResponse


data class InvolvedItemModel(
    val speakers: List<UserResponse> = emptyList(),
    val likers: List<UserResponse> = emptyList(),
    val participants: List<UserResponse> = emptyList(),
    val mentioned: List<UserResponse> = emptyList()
)

enum class InvolvedItemType {
    SPEAKERS,
    LIKERS,
    PARTICIPANT,
    MENTIONED
}
