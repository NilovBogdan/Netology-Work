package bogdan.nilov.netologywork.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import bogdan.nilov.netologywork.dto.UserResponse

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
) {
    fun toDto() = UserResponse(
        id,
        login,
        name,
        avatar
    )

    companion object {
        fun fromDto(userResponse: UserResponse) = UserEntity(
            userResponse.id,
            userResponse.login,
            userResponse.name,
            userResponse.avatar
        )
    }
}

fun List<UserEntity>.toDto(): List<UserResponse> = map(UserEntity::toDto)
fun List<UserResponse>.toEntity(): List<UserEntity> = map(UserEntity.Companion::fromDto)