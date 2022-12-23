package com.quico.tech.activity

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.RequestPhotoRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.SERVICE_ID
import com.quico.tech.databinding.ActivityRequestBinding
import com.quico.tech.utils.AudioRecorder
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import java.io.File
import java.util.concurrent.TimeUnit

class RequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestBinding
    private val viewModel: SharedViewModel by viewModels()
    private val max_pics = 4
    private var pics_count = 0
    private var total_pics_count = 0
    private lateinit var requestPhotoRecyclerViewAdapter: RequestPhotoRecyclerViewAdapter
    private var photos = ArrayList<PhotoService>()
    var iteration_nb = max_pics
    var pics_left = 0
    private var audioRecorder: AudioRecorder? = null
    private var recordFile: File? = null
    var audioPath = ""
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var current_photo: PhotoService
    private  var current_photo_position: Int=-1

    class PhotoService(val id: Int, var img: Uri?)

    private var id_generator = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaRecorder = MediaRecorder()
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpText()
        setUpPhotosAdapter()

    }

    private fun setUpText() {
        binding.apply {

            //title.text = viewModel.getLangResources().getString(R.string.all)
            problemDescription.text =
                viewModel.getLangResources().getString(R.string.problem_description)
            uploadImagesText.text = viewModel.getLangResources().getString(R.string.upload_images)
            nextBtn.text = viewModel.getLangResources().getString(R.string.next)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            nextBtn.setOnClickListener {
                startActivity(
                    Intent(this@RequestActivity, RequestDeliveryActivity::class.java).putExtra(
                        SERVICE_ID,
                        1
                    )
                )
            }



//            add1.setOnClickListener {
//                // if (total_pics_count < 4)
//                checkGalleryPermissions()
//            }
//
//            add2.setOnClickListener {
//                checkGalleryPermissions()
//            }
//
//            add3.setOnClickListener {
//                checkGalleryPermissions()
//            }
//
//            add4.setOnClickListener {
//                checkGalleryPermissions()
//            }

            setUpVoiceRecorder()
        }
    }

    private fun checkGalleryPermissions() {
        if (Common.checkGalleryPermissions(this)) {
            // open gallery
            openGallery()
        } else
        // requestPermissions()
            Common.requestPermissions(this)
    }

    private fun openGallery() {
        /*  val intent = Intent()
          intent.type = "image/*"
          intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
          intent.action = Intent.ACTION_GET_CONTENT
          imageLauncher.launch(intent) */
           this is for selecting multiple image
         */

        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        imageLauncher.launch(gallery)

    }

    var imageLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (result.resultCode == RESULT_OK) {
                    if (result.data != null && result.data!!.data != null) {

                        var imageUri = result.data!!.data
                       // photos.set(current_photo_position,PhotoService(current_photo.id,imageUri))
                        photos.get(current_photo_position).img=imageUri
                        Log.d("CURRENT_PHOTO",current_photo_position.toString() + " "  +current_photo.id.toString())
                        requestPhotoRecyclerViewAdapter.differ.submitList(photos)

//                        if (photos.size < 4) {
//                            photos.add(PhotoService(id_generator, imageUri!!))
//                            id_generator++
//                            requestPhotoRecyclerViewAdapter.differ.submitList(photos)
//                        }
//                        when (photos.size) {
//                            1 -> binding.add4.visibility = View.INVISIBLE
//                            2 -> binding.add3.visibility = View.INVISIBLE
//                            3 -> binding.add2.visibility = View.INVISIBLE
//                            4 -> binding.add1.visibility = View.INVISIBLE
//                        }

                        // binding.profileImage.setImageURI(imageUri)
//                        pics_count = result.data!!.clipData!!.itemCount
//                        total_pics_count = photos.size
//                        pics_left = max_pics - total_pics_count
//
//                        if (pics_count > 0) {
//                            if (pics_count >= max_pics)
//                                iteration_nb = max_pics
//                            else if (pics_count < max_pics) {
//                                if (pics_count < pics_left)
//                                    iteration_nb = pics_count
//                                else if (pics_count >= pics_left)
//                                    iteration_nb = pics_left
//                            }
//
//                            for (i in 0 until iteration_nb) {
//                                photos.add(PhotoService(id_generator,result.data!!.clipData!!.getItemAt(i).uri))
//                                id_generator++
//                            }
//                            binding.uploadImage.visibility = View.GONE
//                            binding.recyclerView.visibility = View.VISIBLE
//                            requestPhotoRecyclerViewAdapter.differ.submitList(photos)
//
//                        } else {
//                            binding.uploadImage.visibility = View.VISIBLE
//                            binding.recyclerView.visibility = View.GONE
//                        }
                    } else {
                        Toast.makeText(
                            this,
                            null,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

    fun setUpPhotosAdapter() {
        binding.apply {

            photos.add(PhotoService(1,null))
            photos.add(PhotoService(2,null))
            photos.add(PhotoService(3,null))
            photos.add(PhotoService(4,null))

            requestPhotoRecyclerViewAdapter = RequestPhotoRecyclerViewAdapter(object :
                RequestPhotoRecyclerViewAdapter.OnAddPhoto {
                override fun onAddPhoto(position: Int,currentPhoto:PhotoService) {

                    if (currentPhoto.img==null){
                        current_photo = currentPhoto
                        current_photo_position = position
                        checkGalleryPermissions()
                    }

                    //requestPhotoRecyclerViewAdapter.differ.submitList(photos)
                    //  total_pics_count = photos.size
                    //  pics_left = max_pics - total_pics_count

                  /*  when (photos.size) {
                        1 -> {
                            binding.add1.visibility = View.VISIBLE
                            binding.add2.visibility = View.VISIBLE
                            binding.add3.visibility = View.VISIBLE
                            binding.add4.visibility = View.INVISIBLE
                        }
                        2 -> {
                            binding.add1.visibility = View.VISIBLE
                            binding.add2.visibility = View.VISIBLE
                            binding.add3.visibility = View.INVISIBLE
                            binding.add4.visibility = View.INVISIBLE
                        }

                        3 -> {
                            binding.add1.visibility = View.VISIBLE
                            binding.add2.visibility = View.INVISIBLE
                            binding.add3.visibility = View.INVISIBLE
                            binding.add4.visibility = View.INVISIBLE
                        }

                        4 -> {
                            binding.add1.visibility = View.INVISIBLE
                            binding.add2.visibility = View.INVISIBLE
                            binding.add3.visibility = View.INVISIBLE
                            binding.add4.visibility = View.INVISIBLE
                        }
                    }

                    if (photos.isEmpty()) {
                        add1.visibility = View.VISIBLE
                        add2.visibility = View.VISIBLE
                        add3.visibility = View.VISIBLE
                        add4.visibility = View.VISIBLE
                    }*/
                }
            })


            recyclerView.layoutManager = GridLayoutManager(this@RequestActivity, 2)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(requestPhotoRecyclerViewAdapter)
            requestPhotoRecyclerViewAdapter.differ.submitList(photos)
        }
    }


    private fun setUpVoiceRecorder() {
        binding.apply {
/*
            recordBtn.setRecordView(recordView)
            recordView.setCancelBounds(8f)
            recordView.setLessThanSecondAllowed(false)
            recordView.setSlideToCancelText("Slide To Cancel")
            recordView.setCustomSounds(0, 0, 0)
            Log.d("RecordView", "setup")

            recordView.setOnRecordListener(object : OnRecordListener {
                var date: String = ""
                override fun onStart() {
                   // deleteAllFiles()
                    audioRecorder = AudioRecorder()
                    mediaRecorder = MediaRecorder()
                    recordFile = File(
                        Environment.getExternalStorageDirectory(),
                        APP_MEDIA_PATH
                    )
                    try {
                        if (!recordFile!!.mkdirs()) recordFile!!.mkdirs()
                        date = System.currentTimeMillis().toString()
                        audioPath = recordFile?.getAbsolutePath() + File.separator + date + ".3gp"
                        audioRecorder!!.start(audioPath,mediaRecorder)
                        Log.d("RecordView", audioPath)
                    } catch (e: IOException) {
                        // recordView.setBackgroundColor(resources.getColor(android.R.color.transparent))
                        e.printStackTrace()
                        Log.d("RecordView", "EXCEPTION ${e.message}")
                    }
                   // Toast.makeText(this@RequestActivity,"start",Toast.LENGTH_LONG).show()
                    Log.d("RecordView", "onStart")
                }

                override fun onCancel() {
                    stopRecording(true)
                    //Toast.makeText(this@RequestActivity,"cancel",Toast.LENGTH_LONG).show()

                    Log.d("RecordView", "onCancel")
                }

                override fun onFinish(recordTime: Long, limitReached: Boolean) {
                    Log.d("RECORD_TIME", recordTime.toString())
                    try {

                        stopRecording(false)
                        val time: String = getHumanTimeText(recordTime)!!
                        Log.d("RecordView", "onFinish Limit Reached? $limitReached")
                        Log.d("RecordTime", time)

                        val file: File = File(audioPath)
                        val file_size = (file.length() / 1024).toString().toInt()
                        if (file_size > 0) {
                            setVoice(audioPath)
                            // Toast.makeText(this@RequestActivity,"set voice",Toast.LENGTH_LONG).show()
                            //Toast.makeText(this@RequestActivity,"finish",Toast.LENGTH_LONG).show()
                        } else {
                            Log.d("RecordView", "failed")
                            //Toast.makeText(this@RequestActivity,"failed",Toast.LENGTH_LONG).show()
                            stopRecording(true)
                        }
                    }catch (e:java.lang.Exception){

                    }
                    Log.d("RecordView", "FINISH RECORD")
                }

                override fun onLessThanSecond() {
                    try {
                        stopRecording(true)
                    }catch (e:java.lang.Exception){

                    }
                    Log.d("RecordView", "onLessThanSecond")
                   // Toast.makeText(this@RequestActivity,"less than 1",Toast.LENGTH_LONG).show()
                }
            })

            recordView.setRecordPermissionHandler(RecordPermissionHandler {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return@RecordPermissionHandler true
                }
                val recordPermissionAvailable = ContextCompat.checkSelfPermission(
                    this@RequestActivity, Manifest.permission.RECORD_AUDIO
                ) == PermissionChecker.PERMISSION_GRANTED
                if (recordPermissionAvailable) {
                    return@RecordPermissionHandler true
                }
                ActivityCompat.requestPermissions(
                    this@RequestActivity, arrayOf(Manifest.permission.RECORD_AUDIO),
                    0
                )
                false
            })

            recordView.setOnBasketAnimationEndListener {
                Log.d(
                    "RecordView",
                    "Basket Animation Finished"
                )
            }*/
        }
    }

    private fun setVoice(audioPath: String) {
        //if (recordFile.exists() && file.canRead() && audioPath.toUri() != null) {

        binding.apply {
            audioPath?.toUri().let { uri ->
                mediaPlayer = MediaPlayer.create(this@RequestActivity, audioPath?.toUri())
                val seconds = (mediaPlayer.duration / 1000) as Int % 60
                val minutes = (mediaPlayer.duration / (1000 * 60) % 60)
                /* duration.visibility = View.VISIBLE
                 duration.text = String.format(Locale.ENGLISH, "%2d:%02d", minutes, seconds)*/

                voiceContainer.visibility = View.VISIBLE
                voicePlayerView.setAudio(audioPath)
                deleteImage.setOnClickListener {
                    hideCurrentVoice()
                    // recordView.setEnabled(true)
                }
            }
        }
    }

    private fun hideCurrentVoice() {
        binding.voiceContainer.visibility = View.GONE
        deleteAllFiles()
    }

    override fun onStop() {
        super.onStop()
        // here  must stop playing audio
        try {
            stopRecording(false)
            //   binding.recordView.cancelRecord()
        } catch (e: Exception) {
        }
    }

    private fun stopRecording(deleteFile: Boolean) {
        try {
            audioRecorder?.stop(mediaRecorder)
            if (recordFile != null && deleteFile) {
                deleteAllFiles()
                Log.d(
                    "RecordView",
                    " DELETE "
                )
            } else
                Log.d(
                    "RecordView",
                    "CANT DELETE FILE"
                )
        } catch (e: java.lang.Exception) {

        }
    }

    private fun deleteAllFiles() {
        try {
            recordFile?.let {
                if (recordFile!!.isDirectory) {
                    val children: Array<String> = recordFile!!.list()
                    for (i in children.indices) {
                        File(recordFile, children[i]).delete()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun getHumanTimeText(milliseconds: Long): String? {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )
    }


}