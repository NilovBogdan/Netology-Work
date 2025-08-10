package bogdan.nilov.netologywork.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogdan.nilov.netologywork.dto.Job
import bogdan.nilov.netologywork.error.NetworkError
import bogdan.nilov.netologywork.error.UnknownError
import bogdan.nilov.netologywork.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    val data: LiveData<List<Job>> = repository.dataJob


    fun getJobs(userId: Long?) = viewModelScope.launch {
        try {
            if (userId == null) {
                repository.getMyJobs()
            } else {
                repository.getJobs(userId)
            }
        } catch (_: IOException) {
            throw NetworkError
        }catch(_: Exception) {
            throw UnknownError
        }
    }

    fun saveJob(
        name: String,
        position: String,
        link: String?,
        startWork: OffsetDateTime,
        finishWork: OffsetDateTime
    ) = viewModelScope.launch {
        try {
            repository.saveJob(
                Job(
                    id = 0,
                    name = name,
                    position = position,
                    link = link,
                    start = startWork,
                    finish = finishWork,
                )
            )
        } catch (_: IOException) {
            throw NetworkError
        }catch(_: Exception) {
            throw UnknownError
        }
    }

    fun deleteJob(id: Long) = viewModelScope.launch {
        try {
            repository.deleteJob(id)
        } catch (_: IOException) {
            throw NetworkError
        }catch(_: Exception) {
            throw UnknownError
        }
    }


}