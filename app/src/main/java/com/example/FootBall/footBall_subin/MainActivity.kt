package com.example.FootBall.footBall_subin

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.media.ExifInterface
import android.graphics.Matrix
import com.example.FootBall.R

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button
    private lateinit var cropButton: Button
    private lateinit var cropArea: View
    private var imageUri: Uri? = null
    private var capturedBitmap: Bitmap? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            try {
                // Bitmap 로드
                capturedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                // EXIF로부터 회전 정보 읽기
                val rotationDegrees = getRotationFromUri(imageUri!!)
                capturedBitmap = rotateBitmap(capturedBitmap!!, rotationDegrees)

                // ImageView에 표시
                imageView.setImageBitmap(capturedBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "사진 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        imageView = findViewById(R.id.imageView)
        captureButton = findViewById(R.id.captureButton)
        cropButton = findViewById(R.id.cropButton)
        cropArea = findViewById(R.id.cropArea)

        captureButton.setOnClickListener {
            if (checkCameraPermission()) {
                takePicture()
            } else {
                requestCameraPermission()
            }
        }


        cropButton.setOnClickListener {
            if (capturedBitmap != null) {
                // Crop 영역 계산 및 크롭 비트맵 생성
                val cropRect = calculateCropRect(cropArea, capturedBitmap!!)
                val croppedBitmap = cropBitmap(capturedBitmap!!, cropRect)

                if (croppedBitmap != null) {
                    // ImageView에 크롭된 비트맵 표시
                    imageView.setImageBitmap(croppedBitmap)
                    capturedBitmap = croppedBitmap // Update capturedBitmap

                    // 갤러리에 크롭된 비트맵 저장
                    val savedUri = saveImageToGallery(croppedBitmap, "cropped_image_${System.currentTimeMillis()}")
                    if (savedUri != null) {
                        Toast.makeText(this, "갤러리에 저장되었습니다: $savedUri", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "갤러리 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "크롭에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "먼저 사진을 촬영하세요.", Toast.LENGTH_SHORT).show()
            }
        }







        // 드래그 및 크기 조정 리스너 추가
        cropArea.setOnTouchListener { view, motionEvent ->
            handleTouch(view, motionEvent)
            true
        }
    }

    private fun calculateCropRect(cropArea: View, bitmap: Bitmap): Rect {
        // Get cropArea's position on the screen
        val cropAreaLocation = IntArray(2)
        cropArea.getLocationOnScreen(cropAreaLocation)

        // Get ImageView's position on the screen
        val imageViewLocation = IntArray(2)
        imageView.getLocationOnScreen(imageViewLocation)

        // Calculate scale factors between the ImageView and the Bitmap
        val drawable = imageView.drawable ?: return Rect(0, 0, 0, 0)
        val imageScaleX = bitmap.width.toFloat() / imageView.width
        val imageScaleY = bitmap.height.toFloat() / imageView.height

        // Calculate the offset for the ImageView padding
        val imagePaddingX = ((imageView.width - bitmap.width / imageScaleX) / 2).toInt()
        val imagePaddingY = ((imageView.height - bitmap.height / imageScaleY) / 2).toInt()

        // Translate the cropArea's coordinates to the Bitmap's coordinate system
        val left = ((cropAreaLocation[0] - imageViewLocation[0] - imagePaddingX) * imageScaleX).toInt()
        val top = ((cropAreaLocation[1] - imageViewLocation[1] - imagePaddingY) * imageScaleY).toInt()
        val right = ((cropAreaLocation[0] - imageViewLocation[0] + cropArea.width - imagePaddingX) * imageScaleX).toInt()
        val bottom = ((cropAreaLocation[1] - imageViewLocation[1] + cropArea.height - imagePaddingY) * imageScaleY).toInt()

        // Ensure the crop rectangle stays within the Bitmap bounds
        return Rect(
            left.coerceIn(0, bitmap.width),
            top.coerceIn(0, bitmap.height),
            right.coerceIn(0, bitmap.width),
            bottom.coerceIn(0, bitmap.height)
        )
    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            // Create a file in the app's external storage directory
            val storageDir = getExternalFilesDir(null)
            val imageFile = File(storageDir, "$fileName.jpg")

            // Write the bitmap to the file
            val outputStream = imageFile.outputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Return the URI of the saved file
            FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg") // 저장할 파일 이름
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // 파일 형식
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp") // 저장 위치
        }

        return try {
            // MediaStore에 파일 정보를 삽입하고 Uri 생성
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                // OutputStream을 열어 Bitmap 저장
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    } else {
                        throw Exception("OutputStream을 열 수 없습니다.")
                    }
                }
            }
            uri // 성공적으로 저장된 URI 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null // 오류 발생 시 null 반환
        }
    }




    private fun loadSavedImage(uri: Uri) {
        try {
            // Load the image as a Bitmap
            val inputStream = contentResolver.openInputStream(uri)
            val loadedBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Set the Bitmap to the ImageView
            imageView.setImageBitmap(loadedBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }





    private fun getRotationFromUri(uri: Uri): Int {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap // 회전 필요 없음

        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }




    private fun checkCameraPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            imageUri = createImageUri()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            takePictureLauncher.launch(imageUri!!)
        }
    }

    private fun createImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
    }

    private fun cropBitmap(source: Bitmap, cropArea: Rect): Bitmap? {
        return try {
            // 크롭할 영역이 비트맵의 범위를 초과하지 않도록 조정
            val x = cropArea.left.coerceIn(0, source.width)
            val y = cropArea.top.coerceIn(0, source.height)
            val width = (cropArea.right - cropArea.left).coerceIn(0, source.width - x)
            val height = (cropArea.bottom - cropArea.top).coerceIn(0, source.height - y)

            // 비트맵 크롭
            Bitmap.createBitmap(source, x, y, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            null // 크롭 실패 시 null 반환
        }
    }



    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f
    private var initialWidth: Int = 0
    private var initialHeight: Int = 0
    private var isResizing: Boolean = false

    private fun handleTouch(view: View, motionEvent: MotionEvent) {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = motionEvent.rawX
                initialTouchY = motionEvent.rawY
                initialWidth = view.width
                initialHeight = view.height
                isResizing = isTouchingCorner(view, motionEvent)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isResizing) {
                    resizeView(view, motionEvent)
                } else {
                    moveView(view, motionEvent)
                }
            }
        }
    }

    private fun isTouchingCorner(view: View, motionEvent: MotionEvent): Boolean {
        val cornerThreshold = 50 // 모서리 감지 범위
        val x = motionEvent.x
        val y = motionEvent.y

        return (x < cornerThreshold && y < cornerThreshold) || // 왼쪽 위
                (x > view.width - cornerThreshold && y < cornerThreshold) || // 오른쪽 위
                (x < cornerThreshold && y > view.height - cornerThreshold) || // 왼쪽 아래
                (x > view.width - cornerThreshold && y > view.height - cornerThreshold) // 오른쪽 아래
    }

    private fun resizeView(view: View, motionEvent: MotionEvent) {
        val deltaX = motionEvent.rawX - initialTouchX
        val deltaY = motionEvent.rawY - initialTouchY

        // 크기 조정
        val newWidth = (initialWidth + deltaX).toInt()
        val newHeight = (initialHeight + deltaY).toInt()

        // 최소 크기 제한
        if (newWidth > 100 && newHeight > 100) {
            view.layoutParams.width = newWidth
            view.layoutParams.height = newHeight
            view.requestLayout()
        }
    }

    private fun moveView(view: View, motionEvent: MotionEvent) {
        val deltaX = motionEvent.rawX - initialTouchX
        val deltaY = motionEvent.rawY - initialTouchY

        // 위치 조정
        view.x += deltaX
        view.y += deltaY
        initialTouchX = motionEvent.rawX
        initialTouchY = motionEvent.rawY
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}