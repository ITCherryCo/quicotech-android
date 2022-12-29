package com.quico.tech.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.quico.tech.R
import com.quico.tech.data.Constant
import com.quico.tech.data.Constant.CHANGE_PASSWORD
import com.quico.tech.data.Constant.EMAIL
import com.quico.tech.data.Constant.PASSWORD
import com.quico.tech.data.Constant.PHONE_NUMBER
import com.quico.tech.databinding.ActivityEditProfileBinding
import com.quico.tech.model.UpdateUserBodyParameters
import com.quico.tech.model.UpdateUserParams
import com.quico.tech.utils.Common
import com.quico.tech.viewmodel.SharedViewModel
import java.lang.Byte.decode
import java.net.URLDecoder.decode
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: SharedViewModel by viewModels()
    private var imageUri: Uri? = null
    private var imageEncoded: String = ""
    private var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpText()
        binding.apply {
            loadImage()

            imageChoose.setOnClickListener {
                checkGalleryPermissions()
                //selectImage()
            }
            profileImage.setOnClickListener {
                checkGalleryPermissions()
                //selectImage()
            }
            birthdayField.setOnClickListener {
                chooseDate()
            }
            backArrow.setOnClickListener {
                onBackPressed()
            }

            changeMobileBtn.setOnClickListener {
                startActivity(
                    Intent(this@EditProfileActivity, EditCredentialsActivity::class.java)
                        .putExtra(Constant.PROFILE_EDIT_TYPE, Constant.CHANGE_PHONE_NUMBER)
                )
            }

            changeEmailBtn.setOnClickListener {
                startActivity(
                    Intent(this@EditProfileActivity, EditCredentialsActivity::class.java)
                        .putExtra(Constant.PROFILE_EDIT_TYPE, Constant.CHANGE_EMAIL)
                )
            }

            changePasswordBtn.setOnClickListener {
                startActivity(
                    Intent(this@EditProfileActivity, EditCredentialsActivity::class.java)
                        .putExtra(Constant.PROFILE_EDIT_TYPE, CHANGE_PASSWORD)
                )
            }
        }
    }

    private fun loadImage() {
        try {

            binding.apply {
                if (viewModel.user?.image.isNullOrEmpty()) {
                    profileImage.setImageResource(R.drawable.profile_user)
                } else {
                    val imageBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Base64.getDecoder().decode(viewModel.user?.image)

                    } else {
                        android.util.Base64.decode(
                            viewModel.user?.image,
                            android.util.Base64.DEFAULT
                        )
                    }

                    var decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    profileImage.setImageBitmap(decodedImage)

                    //                        binding.profileImage.setImageURI(imageUri)
                }
            }
        }catch (e:Exception){}
    }

    fun chooseDate() {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(
            this,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            mDateSetListener,
            year, month, day
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    var mDateSetListener =
        OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month = month + 1
            val date = "$year-$month-$day"
            binding.birthdayField.setText(date)
        }


    private fun setUpText() {
        binding.apply {
            title.text = viewModel.getLangResources().getString(R.string.profile)
            fullNameText.text = viewModel.getLangResources().getString(R.string.full_name)
            nameField.hint = viewModel.getLangResources().getString(R.string.full_name)
            birthdateText.text = viewModel.getLangResources().getString(R.string.date_of_birth)
            birthdayField.hint = viewModel.getLangResources().getString(R.string.birthday_example)
            saveBtn.text = viewModel.getLangResources().getString(R.string.save)
            changePasswordBtn.text =
                viewModel.getLangResources().getString(R.string.change_password)
            changeMobileBtn.text =
                viewModel.getLangResources().getString(R.string.change_mobile_number)
            changeEmailBtn.text =
                viewModel.getLangResources().getString(R.string.change_email_address)

            if (viewModel.getLanguage().equals(Constant.AR))
                backArrow.scaleX = -1f

            saveBtn.setOnClickListener {
                checkFields()
            }

            viewModel.user?.let { user ->
                if (user.name.isNullOrEmpty())
                    nameField.hint = viewModel.getLangResources().getString(R.string.full_name)
                else
                    nameField.setText(user.name)
            }
        }
    }

    var launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
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

    private fun requestPermissions() {

        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS,
            PackageManager.PERMISSION_GRANTED
        )
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        imageLauncher.launch(gallery)
        //  val intent = Intent()
        // intent.type = "image/*"
        // intent.action = Intent.ACTION_GET_CONTENT
    }

    var imageLauncher =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (result.resultCode == RESULT_OK) {
                    if (result.data != null && result.data!!.data != null) {
                        imageUri = result.data!!.data
                        val bytes = contentResolver.openInputStream(imageUri!!)?.readBytes()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            imageEncoded= Base64.getEncoder().encodeToString(bytes)
                        }

                        binding.profileImage.setImageURI(imageUri)
                    }
                }
            }
        )

    private fun checkFields() {
        binding.apply {
            if (nameField.text.toString().isEmpty()) {
                nameField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            } else if (birthdayField.text.toString().isEmpty()) {
                birthdayField.error =
                    viewModel.getLangResources().getString(R.string.required_field)
            } else {
                updateUserInfo(nameField.text.toString(), birthdayField.text.toString(), imageEncoded)
            }
        }
    }

    private fun updateUserInfo(name:String, dob:String, image:String) {
        binding.apply {
            Common.setUpProgressDialog(this@EditProfileActivity)

            var updateUserBodyParameters = UpdateUserBodyParameters(
                UpdateUserParams(name,dob,image)
            )

            viewModel.updateUserInfo(
                updateUserBodyParameters,
                object : SharedViewModel.ResponseStandard {
                    override fun onSuccess(
                        success: Boolean,
                        resultTitle: String,
                        message: String
                    ) {
                        Common.cancelProgressDialog()
                        Toast.makeText(this@EditProfileActivity,message,Toast.LENGTH_LONG).show()

                    }

                    override fun onFailure(
                        success: Boolean,
                        resultTitle: String,
                        message: String
                    ) {
                        Common.cancelProgressDialog()

                        Common.setUpAlert(
                            this@EditProfileActivity, false,
                            viewModel.getLangResources().getString(R.string.error),
                            message,
                            viewModel.getLangResources().getString(R.string.ok),
                            null
                        )
                    }
                })
        }
    }
}
