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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    // -------------------------
    // Picker: Foto/Vídeo
    // -------------------------
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val mime = context.contentResolver.getType(it).orEmpty()
            val type = if (mime.startsWith("video/")) AttachmentType.VIDEO else AttachmentType.IMAGE
            onAddAttachment(it.toString(), type)
        }
    }

    // -------------------------
    // Picker: Áudio (arquivo)
    // -------------------------
    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAddAttachment(it.toString(), AttachmentType.AUDIO) }
    }

    // -------------------------
    // Gravação de áudio (MediaRecorder)
    // -------------------------
    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordPath by remember { mutableStateOf<String?>(null) }

    var pendingStartRecord by remember { mutableStateOf(false) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (pendingStartRecord) {
            pendingStartRecord = false
            if (granted) {
                startRecording(
                    context = context,
                    onStarted = { rec, path ->
                        recorder = rec
                        recordPath = path
                        isRecording = true
                    },
                    onError = onError
                )
            } else {
                onError("Permissão de microfone necessária para gravar áudio.")
            }
        }
    }

    fun stopRecordingAndAttach() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
            // MVP: ignora erro se parar muito rápido
        } finally {
            recorder = null
            isRecording = false
        }

        val path = recordPath
        if (path != null) {
            val uri = Uri.fromFile(File(path)).toString()
            onAddAttachment(uri, AttachmentType.AUDIO)
        }
        recordPath = null
    }

    // -------------------------
    // UI
    // -------------------------
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = canAddMore,
                onClick = {
                    mediaPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
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
                        if (!canAddMore) return@Button
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
    }

        if (attachments.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(attachments) { a ->
                    AssistChip(
                        onClick = { /* depois: preview */ },
                        label = { Text(labelFor(a.type)) },
                        trailingIcon = {
                            IconButton(onClick = { onRemoveAttachment(a.uri) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remover")
                            }
                        }
                    )
                }
            }
        } else {
            Text(
                "Você pode anexar foto, vídeo ou áudio (até $maxAttachments).",
                style = MaterialTheme.typography.bodySmall
            )

            Text("Anexos atuais: ${attachments.size}/$maxAttachments", style = MaterialTheme.typography.bodySmall)

        }
    }

private fun labelFor(type: AttachmentType): String =
    when (type) {
        AttachmentType.IMAGE -> "Foto"
        AttachmentType.VIDEO -> "Vídeo"
        AttachmentType.AUDIO -> "Áudio"
    }

/**
 * MVP estável: grava no cacheDir e retorna o path do arquivo.
 */
private fun startRecording(
    context: Context,
    onStarted: (MediaRecorder, String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val name = "infoplus_audio_${sdf.format(Date())}.m4a"
        val path = File(context.cacheDir, name).absolutePath

        val rec = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(path)
            prepare()
            start()
        }

        onStarted(rec, path)
    } catch (e: Exception) {
        onError("Falha ao iniciar gravação: ${e.message ?: "erro desconhecido"}")
    }
}
