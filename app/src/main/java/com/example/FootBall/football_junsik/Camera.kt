package com.example.FootBall.football_junsik

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class Camera(private val activity: AppCompatActivity){
    private val REQUEST_IMAGE_CAPTURE: Int = 101
    private val CAMERA_PERMISSION_CODE: Int = 102

    var imageView: ImageView? = null
    var button: Button? = null
    var textView: TextView? = null

    private lateinit var mImageCaptureUri: Uri
    fun startCamera(){
        checkCameraPermission()
    }

    // 카메라 권한 확인 및 요청
    private fun checkCameraPermission() {
        // 카메라 권한이 부여되었는지 확인
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없다면 권한 요청
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // 권한이 이미 허용되었다면 사진 촬영 진행
            dispatchTakePictureIntent()
        }
    }

    // 카메라로 사진 찍기
    private fun dispatchTakePictureIntent() {
        // 카메라 앱을 실행하기 위한 Intent 생성
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    // 권한 요청 결과 처리
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        // 부모 클래스의 메서드를 호출
        /*
        왜 super 호출이 필요한가?
            프레임워크와의 호환성 유지

            부모 클래스(AppCompatActivity 등)에서 추가적으로 실행해야 하는 권한 처리 코드가 있을 수 있습니다.
            이를 생략하면 일부 기능이 정상적으로 동작하지 않을 수 있습니다.
            Fragment와의 연동

            권한 요청이 Fragment에서 이루어진 경우, 부모 클래스의 onRequestPermissionsResult가 호출되지 않으면 Fragment에서 결과를 받을 수 없습니다.
            라이브러리 및 다른 의존성 관리

            사용 중인 라이브러리 또는 의존성이 내부적으로 onRequestPermissionsResult를 활용하는 경우, super 호출이 없으면 예상치 못한 동작을 유발할 수 있습니다.
         */
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(activity, "권한이 있어야 실행 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 찍은 사진 결과 표시
//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            // 사진 찍기 완료 후 결과 이미지를 ImageView에 설정
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            //imageView?.setImageBitmap(imageBitmap)
//            imageToText(imageBitmap)
//        }
//    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            startCropActivity(imageBitmap)
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == AppCompatActivity.RESULT_OK) {
            UCrop.getOutput(data!!)?.let { uri ->
                val croppedBitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                imageToText(croppedBitmap)  // 이미지에서 텍스트 인식
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(activity, "이미지 크롭 실패: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCropActivity(bitmap: Bitmap) {
        val tempUri = saveImageToCache(bitmap)
        val destinationUri = Uri.fromFile(File(activity.cacheDir, "croppedImage.jpg"))

        UCrop.of(tempUri, destinationUri)
            .withAspectRatio(0.5f, 0.2f)
            .withMaxResultSize(500, 500)
            .start(activity)
    }

    private fun saveImageToCache(bitmap: Bitmap): Uri {
        val file = File(activity.cacheDir, "tempImage.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return Uri.fromFile(file)
    }

    private fun imageToText(img: Bitmap){
        Toast.makeText(activity, "시작", Toast.LENGTH_SHORT).show()
        try {
            val image = InputImage.fromBitmap(img, 0) // 비트맵 이미지를 input에 맞게
            //val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build()) // 객체 생성
            recognizer.process(image)
                .addOnSuccessListener { // 성공시
                    Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show() // Toast로 결과 표시

                }
                .addOnFailureListener { e->// 실패시
                    Toast.makeText(activity, "인식 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(activity, "끝", Toast.LENGTH_SHORT).show()
    }

    private fun doTakePhotoAction() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val url = "tmp_" + System.currentTimeMillis().toString() + ".jpg"
        mImageCaptureUri = Uri.fromFile(File(Environment.getExternalStorageDirectory(), url))
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        //startActivityForResult(intent, PICK_FROM_CAMERA)
    }
}