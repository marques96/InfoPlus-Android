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
