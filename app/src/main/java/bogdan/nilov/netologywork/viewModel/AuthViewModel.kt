package bogdan.nilov.netologywork.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bogdan.nilov.netologywork.dto.AttachmentType
import bogdan.nilov.netologywork.model.AttachmentModel
import bogdan.nilov.netologywork.model.AuthModel
import bogdan.nilov.netologywork.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val dataAuth: LiveData<AuthModel> = repository.dataAuth.asLiveData(Dispatchers.Default)

    private val _photoData: MutableLiveData<AttachmentModel?> = MutableLiveData(null)
    val photoData: LiveData<AttachmentModel?>
        get() = _photoData

    fun register(login: String, name: String, pass: String) {
        viewModelScope.launch {
            val photo = _photoData.value
            repository.register(login, name, pass, photo)
        }
    }

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            repository.login(login, pass)
        }
    }

    fun setPhoto(uri: Uri, file: File) {
        _photoData.value = AttachmentModel(AttachmentType.IMAGE, uri, file)
    }

    fun logout() {
        repository.logout()
    }
}