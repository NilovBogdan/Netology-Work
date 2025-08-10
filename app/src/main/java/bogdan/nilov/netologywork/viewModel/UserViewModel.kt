package bogdan.nilov.netologywork.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.UserResponse
import bogdan.nilov.netologywork.error.NetworkError
import bogdan.nilov.netologywork.error.UnknownError
import bogdan.nilov.netologywork.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val dataUsers: Flow<PagingData<FeedItem>> =
        repository.dataUsers.map {
            it.map { feedItem ->
                feedItem
            }
        }.flowOn(Dispatchers.Default)

    private val _dataUser = MutableLiveData<UserResponse>(null)
    val dataUser: LiveData<UserResponse> = _dataUser

    fun getUser(userId: Long) = viewModelScope.launch {
        try {
            _dataUser.value = repository.getUser(userId)
        } catch (_: IOException) {
            throw NetworkError
        }catch(_: Exception) {
            throw UnknownError
        }
    }

}