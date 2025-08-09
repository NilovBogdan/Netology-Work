package bogdan.nilov.netologywork.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import bogdan.nilov.netologywork.dto.Event
import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.Job
import bogdan.nilov.netologywork.dto.Post
import bogdan.nilov.netologywork.dto.UserResponse
import bogdan.nilov.netologywork.model.AttachmentModel
import bogdan.nilov.netologywork.model.AuthModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    val dataAuth: StateFlow<AuthModel>
    val dataPost: Flow<PagingData<FeedItem>>
    val dataEvent: Flow<PagingData<FeedItem>>
    val dataUsers: Flow<PagingData<FeedItem>>
    val dataJob: LiveData<List<Job>>
    suspend fun register(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    )

    suspend fun login(login: String, pass: String)
    fun logout()

    suspend fun getUser(id: Long): UserResponse
    suspend fun like(post: Post)
    suspend fun savePost(post: Post)
    suspend fun savePostWithAttachment(post: Post, attachmentModel: AttachmentModel)
    suspend fun deletePost(id: Long)

    suspend fun saveEvent(event: Event)
    suspend fun saveEventWithAttachment(event: Event, attachmentModel: AttachmentModel)
    suspend fun deleteEvent(id: Long)
    suspend fun likeEvent(event: Event)

    suspend fun getMyJobs()
    suspend fun getJobs(userId: Long)
    suspend fun saveJob(job: Job)
    suspend fun deleteJob(id: Long)
}