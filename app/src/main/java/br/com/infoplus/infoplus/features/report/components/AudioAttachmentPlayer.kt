package br.com.infoplus.infoplus.features.report.components

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AudioAttachmentPlayer(uriString: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isPrepared by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val player = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { player.stop() }
            runCatching { player.reset() }
            runCatching { player.release() }
        }
    }

    fun prepareAndPlay() {
        error = null
        isPrepared = false
        isPlaying = false

        runCatching {
            player.reset()
            val uri = Uri.parse(uriString)

            // ✅ Para content:// (FileProvider e pickers)
            // 🔁 Fallback: se for file://, usa path
            if (uri.scheme == "file") {
                player.setDataSource(uri.path)
            } else {
                player.setDataSource(context, uri)
            }

            player.setOnPreparedListener {
                isPrepared = true
                it.start()
                isPlaying = true
            }
            player.setOnCompletionListener { isPlaying = false }

            player.prepareAsync()
        }.onFailure {
            error = it.message ?: "Falha ao reproduzir áudio."
            isPrepared = false
            isPlaying = false
        }
    }

    fun pause() {
        runCatching { if (player.isPlaying) player.pause() }
        isPlaying = false
    }

    fun stop() {
        runCatching { player.stop() }
        isPrepared = false
        isPlaying = false
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(
                onClick = {
                    if (!isPrepared) prepareAndPlay()
                    else if (isPlaying) pause()
                    else runCatching { player.start() }.onSuccess { isPlaying = true }
                }
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isPlaying) "Pausar" else "Play")
            }

            OutlinedButton(
                onClick = { stop() },
                enabled = isPrepared || isPlaying
            ) {
                Icon(Icons.Filled.Stop, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Parar")
            }
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
