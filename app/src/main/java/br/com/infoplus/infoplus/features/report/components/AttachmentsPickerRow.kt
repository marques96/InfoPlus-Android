package br.com.infoplus.infoplus.features.report.components

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import br.com.infoplus.infoplus.features.report.model.Attachment
import br.com.infoplus.infoplus.features.report.model.AttachmentType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttachmentsPickerRow(
    attachments: List<Attachment>,
    onAddAttachment: (uri: String, type: AttachmentType) -> Unit,
    onRemoveAttachment: (uri: String) -> Unit,
    maxAttachments: Int = 3,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val canAddMore = attachments.size < maxAttachments

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val mime = context.contentResolver.getType(it).orEmpty()
            val type =
                if (mime.startsWith("video/")) AttachmentType.VIDEO else AttachmentType.IMAGE
            onAddAttachment(it.toString(), type)
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAddAttachment(it.toString(), AttachmentType.AUDIO) }
    }

    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordPath by remember { mutableStateOf<String?>(null) }
    var pendingStartRecord by remember { mutableStateOf(false) }
    var playAudioUri by remember { mutableStateOf<String?>(null) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (pendingStartRecord) {
            pendingStartRecord = false
            if (granted) {
                startRecording(context,
                    onStarted = { rec, path ->
                        recorder = rec
                        recordPath = path
                        isRecording = true
                    },
                    onError = onError
                )
            } else {
                onError("Permissão de microfone necessária.")
            }
        }
    }

    fun stopRecordingAndAttach() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (_: Exception) {
        } finally {
            recorder = null
            isRecording = false
        }

        recordPath?.let { path ->
            val file = File(path)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            onAddAttachment(uri.toString(), AttachmentType.AUDIO)
        }
        recordPath = null
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = canAddMore,
                onClick = {
                    mediaPicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageAndVideo
                        )
                    )
                }
            ) { Text("Foto/Vídeo") }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = canAddMore,
                onClick = { audioPicker.launch("audio/*") }
            ) { Text("Áudio") }

            Button(
                modifier = Modifier.weight(1f),
                enabled = canAddMore || isRecording,
                onClick = {
                    if (isRecording) stopRecordingAndAttach()
                    else {
                        pendingStartRecord = true
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = null
                )
                Spacer(Modifier.width(6.dp))
                Text(if (isRecording) "Parar" else "Gravar")
            }
        }

        if (attachments.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(attachments) { a ->
                    AssistChip(
                        onClick = {
                            if (a.type == AttachmentType.AUDIO) playAudioUri = a.uri
                        },
                        label = { Text(labelFor(a.type)) },
                        trailingIcon = {
                            IconButton(onClick = { onRemoveAttachment(a.uri) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remover")
                            }
                        }
                    )
                }
            }
        }

        if (playAudioUri != null) {
            AlertDialog(
                onDismissRequest = { playAudioUri = null },
                confirmButton = {
                    TextButton(onClick = { playAudioUri = null }) {
                        Text("Fechar")
                    }
                },
                title = { Text("Reproduzir áudio") },
                text = {
                    AudioAttachmentPlayer(uriString = playAudioUri!!)
                }
            )
        }
    }
}

private fun labelFor(type: AttachmentType): String =
    when (type) {
        AttachmentType.IMAGE -> "Foto"
        AttachmentType.VIDEO -> "Vídeo"
        AttachmentType.AUDIO -> "Áudio"
    }

private fun startRecording(
    context: Context,
    onStarted: (MediaRecorder, String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val fileName = "infoplus_audio_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        }.m4a"

        val path = File(context.cacheDir, fileName).absolutePath

        val recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)

            // Container
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            // Codec
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            // 🔥 QUALIDADE
            setAudioEncodingBitRate(192_000)
            setAudioSamplingRate(44_100)
            setAudioChannels(1)

            setOutputFile(path)
            prepare()
            start()
        }

        onStarted(recorder, path)

    } catch (e: Exception) {
        onError("Erro ao gravar áudio: ${e.message}")
    }
}