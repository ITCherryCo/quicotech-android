package com.quico.tech.utils

import android.media.MediaRecorder
import android.util.Log
import java.io.IOException

class AudioRecorder {
    //private var mediaRecorder: MediaRecorder? = null
    private fun initMediaRecorder(mediaRecorder:MediaRecorder?) {
        //mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(
            MediaRecorder.AudioSource.MIC
        )
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder!!.setAudioEncodingBitRate(384000)
        mediaRecorder!!.setAudioSamplingRate(44100)
    }

    @Throws(IOException::class)
    fun start(filePath: String?, mediaRecorder: MediaRecorder?) {
//        if (mediaRecorder == null) {
//            initMediaRecorder()
//        }
        initMediaRecorder(mediaRecorder!!)
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

    fun stop(mediaRecorder:MediaRecorder?) {
        try {
            mediaRecorder!!.stop()
            destroyMediaRecorder(mediaRecorder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun destroyMediaRecorder(mediaRecorder:MediaRecorder?) {
        try {

            mediaRecorder?.let { media ->
                media.release()
                media.reset()
            }
        }catch (e:java.lang.Exception){}

        // mediaRecorder = null
    }
}
