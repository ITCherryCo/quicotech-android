package com.quico.tech.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.quico.tech.R
import com.quico.tech.adapter.RequestPhotoRecyclerViewAdapter
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.SERVICE_ID
import com.quico.tech.databinding.ActivityRequestBinding
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel


class RequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestBinding
    private val viewModel: SharedViewModel by viewModels()
    private val max_pics = 7
    private var pics_count = 0
    private lateinit var requestPhotoRecyclerViewAdapter: RequestPhotoRecyclerViewAdapter
    private var photos = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpText()
        setUpServiceAdapter()
    }

    private fun setUpText() {
        binding.apply {

            //title.text = viewModel.getLangResources().getString(R.string.all)
            problemDescription.text = viewModel.getLangResources().getString(R.string.problem_description)
            uploadImagesText.text = viewModel.getLangResources().getString(R.string.upload_images)
            nextBtn.text = viewModel.getLangResources().getString(R.string.next)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            nextBtn.setOnClickListener {
                startActivity(Intent(this@RequestActivity, RequestDeliveryActivity::class.java).putExtra(
                    SERVICE_ID,
                1))
            }

            imageContainer.setOnClickListener {
                checkGalleryPermissions()
            }
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
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imageLauncher.launch(intent)
    }

    var imageLauncher =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (result.resultCode == RESULT_OK) {
                    if (result.data != null && result.data!!.clipData != null) {
                        pics_count = result.data!!.clipData!!.itemCount
                        if (pics_count>0) {
                            for (i in 0 until pics_count) {
                                photos.add(result.data!!.clipData!!.getItemAt(i).uri)
                            }
                            binding.uploadImage.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            requestPhotoRecyclerViewAdapter.differ.submitList(photos)
                        }
                    }
                }
            }
        )

    fun setUpServiceAdapter() {
        binding.apply {

            requestPhotoRecyclerViewAdapter = RequestPhotoRecyclerViewAdapter(object :RequestPhotoRecyclerViewAdapter.OnDeletePhoto{
                override fun onDeletePhoto(position: Int) {
                    photos.removeAt(position)
                    requestPhotoRecyclerViewAdapter.differ.submitList(photos)
                    if (photos.isEmpty())
                        binding.uploadImage.visibility = View.VISIBLE
                }
            })
            recyclerView.layoutManager = GridLayoutManager(this@RequestActivity, 3)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(requestPhotoRecyclerViewAdapter)
            requestPhotoRecyclerViewAdapter.differ.submitList(photos)
        }
    }
}