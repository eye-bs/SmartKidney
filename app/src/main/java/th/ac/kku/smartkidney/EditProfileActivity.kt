package th.ac.kku.smartkidney

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : AppCompatActivity() {

    lateinit var mDatabaseHelper: DatabaseHelper
    lateinit var birthDate: String
    lateinit var imageUri: Uri
    private lateinit var cropIntent: Intent
    val userObject = ApiObject.instant.user
    val formatter = SimpleDateFormat("dd MMMM", Locale.getDefault())
    val birthDateApi = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mDatabaseHelper = DatabaseHelper(this)
        setData()

        editProfileBt.setOnClickListener {
            when{
                TextUtils.isEmpty(nameEditText.text) -> nameEditText.error = this.getString(R.string.checkFill)
                TextUtils.isEmpty(birthDateEditText.text) -> birthDateEditText.error = this.getString(R.string.checkFill)
                TextUtils.isEmpty(hospitalDateEditText.text) -> hospitalDateEditText.error = this.getString(R.string.checkFill)
                TextUtils.isEmpty(weightDateEditText.text) -> weightDateEditText.error = this.getString(R.string.checkFill)
                TextUtils.isEmpty(heightDateEditText.text) -> heightDateEditText.error = this.getString(R.string.checkFill)
                else -> onSaveClick()
            }
        }
        profileEditImage.setOnClickListener { openGallery() }
        editBirthDateView.setOnClickListener { selectBirthDate() }
        editProfileBackBt.setOnClickListener{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        logOutBt.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            signOut()
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener { }
    }

    private fun onSaveClick(){
        val name = nameEditText.text.toString()
        val birthDate = birthDate
        val hospital = hospitalDateEditText.text.toString()
        val weight = weightDateEditText.text.toString()
        val height = heightDateEditText.text.toString()
        val intent = Intent(this, HomeActivity::class.java)
        val apiHandler = ApiHandler(this, editProfileProgressBar, intent)

        apiHandler.editUserInfo(
            userObject!!.id,
            null,
            name,
            birthDate,
            null,
            hospital,
            weight.toInt(),
            height.toInt()
        )

        val bitmap = (profileEditImage.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imgByteArr = stream.toByteArray()
        mDatabaseHelper.deleteName(Constant.NAME_ATT)
        mDatabaseHelper.addData(Constant.NAME_ATT,imgByteArr)

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setData() {


        val data = mDatabaseHelper.getImgData(Constant.NAME_ATT)
        var imgV: ByteArray? = null
        while (data!!.moveToNext()) {
            imgV = data.getBlob(0)
        }
        val bitmap = BitmapFactory.decodeByteArray(imgV, 0, imgV!!.size)
        profileEditImage.setImageBitmap(bitmap)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val bDate = userObject!!.birthDate
        val paserBDate = dateFormat.parse(bDate)
        val formattedDate = formatter.format(paserBDate)
        birthDate = birthDateApi.format(paserBDate)
        c.time = paserBDate
        var setYear = c.get(Calendar.YEAR)
        if (Locale.getDefault().displayCountry == "ไทย") {
            setYear += 543
        }
        nameEditText.setText(userObject.name)
        birthDateEditText.setText("$formattedDate $setYear")
        hospitalDateEditText.setText(userObject.hospital)
        weightDateEditText.setText(userObject.weight.toString())
        heightDateEditText.setText(userObject.height.toString())

    }

    @SuppressLint("SimpleDateFormat")
    fun selectBirthDate() {

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                val getDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T09:55:00"
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val toDate = parser.parse(getDate)
                val formattedDate = formatter.format(toDate)
                birthDate = birthDateApi.format(toDate)
                var setYear = year
                if (Locale.getDefault().displayCountry == "ไทย") {
                    setYear += 543
                }
                birthDateEditText.setText("$formattedDate $setYear")
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun openGallery() {
        val callbackId = 42
        checkPermissions(
            callbackId,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, Constant.PICK_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 0) {
            ImageCropFunction()

        } else if (requestCode == Constant.PICK_IMAGE) {
            if (data != null) {
                imageUri = data.data
                ImageCropFunction()
            }

        } else if (requestCode == 1) {
            if (data != null) {
                val bundle = data.extras
                val bitmap12 = bundle!!.getParcelable<Bitmap>("data")
                profileEditImage.setImageBitmap(bitmap12)
            }
        }
    }

    fun ImageCropFunction() {
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(imageUri, "image/*")
            cropIntent.putExtra("crop", true)
            cropIntent.putExtra("outputX", 200)
            cropIntent.putExtra("outputY", 200)
            cropIntent.putExtra("aspectX", 6)
            cropIntent.putExtra("aspectY", 6)
            cropIntent.putExtra("scaleUpIfneeded", true)
            cropIntent.putExtra("return-data", true)

            startActivityForResult(cropIntent, 1)

        } catch (e: ActivityNotFoundException) {

        }

    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(
                this,
                p
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId)
    }

    override fun onBackPressed() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}
