package com.quico.tech.utils

import android.media.MediaRecorder
import android.util.Log
import java.io.IOException

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private fun initMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(
            MediaRecorder.AudioSource.MIC
        )
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
       // mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder!!.setAudioEncodingBitRate(384000)
        mediaRecorder!!.setAudioSamplingRate(44100)
    }

    @Throws(IOException::class)
    fun start(filePath: String?) {
        /*if (mediaRecorder == null) {
            initMediaRecorder()
        }*/
        initMediaRecorder()

        try {
            mediaRecorder!!.setOutputFile(filePath)
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
        } catch (e: IllegalStateException) {
           Log.d("RecordView","IllegalStateException " + e.message.toString())
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("RecordView","IOException " + e.message.toString())
        }
    }

    fun stop() {
        try {
            mediaRecorder?.let {
                mediaRecorder!!.stop()
                destroyMediaRecorder()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun destroyMediaRecorder() {
        try {
            mediaRecorder?.let { media ->
                media.release()
                media.reset()
                mediaRecorder=null
            }
        }catch (e:java.lang.Exception){}

    }
}
