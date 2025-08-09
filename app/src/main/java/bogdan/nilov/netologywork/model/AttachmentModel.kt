package bogdan.nilov.netologywork.model

import android.net.Uri
import bogdan.nilov.netologywork.dto.AttachmentType
import java.io.File

data class AttachmentModel(
    val attachmentType: AttachmentType,
    val uri: Uri,
    val file: File,
)