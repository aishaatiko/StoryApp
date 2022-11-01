package com.alicea.storyappsubmission.view.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.alicea.storyappsubmission.*
import com.alicea.storyappsubmission.customview.ButtonAdd
import com.alicea.storyappsubmission.databinding.ActivityAddStoryBinding
import com.alicea.storyappsubmission.model.UserPreference
import com.alicea.storyappsubmission.view.ViewModelFactory
import com.alicea.storyappsubmission.view.camera.CameraActivity
import com.alicea.storyappsubmission.view.main.MainActivity
import com.alicea.storyappsubmission.view.welcome.WelcomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addViewModel: AddViewModel
    private lateinit var token: String

    private lateinit var buttonAdd: ButtonAdd


    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()
        setupAction()
    }

    private fun setupAction() {
        buttonAdd = binding.buttonAdd

        setButtonAddEnable()

        binding.edAddDescription.addTextChangedListener(onTextChanged = {_, _, _, _ ->
            setButtonAddEnable()
        })

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener {
            if (getFile != null) {
                val file = getFile as File

                val description = binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                addViewModel.uploadImage(imageMultipart, description, token)

            } else {
                Toast.makeText(this@AddStoryActivity, getString(R.string.input_image_file_first), Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun setupViewModel() {
        addViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddViewModel::class.java]

        addViewModel.getToken().observe(this) { session ->
            if (session.isLogin) {
                this.token = session.token
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        addViewModel.loading.observe(this) {
            showLoading(it)
        }

        addViewModel.errorMessage.observe(this) {
            when (it) {
                "Story created successfully" -> {
                    Toast.makeText(this@AddStoryActivity, getString(R.string.story_created), Toast.LENGTH_SHORT).show()
                }
                "onFailure" -> {
                    Toast.makeText(this@AddStoryActivity, getString(R.string.on_failure_message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@AddStoryActivity, getString(R.string.fail_to_img), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_pic))
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            result.compress(Bitmap.CompressFormat.JPEG, compressQuality(myFile), FileOutputStream(getFile))

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
            result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = reduceRotateFileImage(myFile)

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        }
    }

    private fun setButtonAddEnable() {
        val result = binding.edAddDescription.text
        buttonAdd.isEnabled = result != null && result.toString().isNotEmpty()
    }
}