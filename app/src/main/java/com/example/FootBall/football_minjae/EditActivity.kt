package com.example.FootBall.football_minjae

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.R
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MainTeamList
import com.example.FootBall.MainViewActivity
import com.example.FootBall.MyApplication
import com.example.FootBall.MyUser
import com.yalantis.ucrop.UCrop
import java.io.File

class EditActivity : AppCompatActivity() {

    private var croppedImageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private val teamList = MainTeamList().getMainTeamList()
    //lateinit var dbHelper: GameDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val app = application as MyApplication
        val user = app.currentUser

        // UI 요소 초기화
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val infoEditText: EditText = findViewById(R.id.infoEditText)
        val teamEditText: EditText = findViewById(R.id.teamEditText)
        profileImageView = findViewById(R.id.profileImageView)

        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val saveButton: Button = findViewById(R.id.saveButton)

        // 글자수 제한 설정
        nameEditText.filters = arrayOf(InputFilter.LengthFilter(10))
        infoEditText.filters = arrayOf(InputFilter.LengthFilter(20))

        // 기존 데이터 세팅
        nameEditText.setText(user?.name)
        infoEditText.setText(user?.info)
        teamEditText.setText(user?.team)
        user?.profile?.let { FireStorageConnection.bindImageByPath(this, it, profileImageView) }

        // 팀 목록 설정
        val teams = arrayOf("없음") + teamList.map { it.name }

        // 팀 선택 다이얼로그
        teamEditText.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("팀 선택")
            builder.setItems(teams) { _, which ->
                teamEditText.setText(teams[which])
            }
            builder.create().show()
        }

        // 입력 제한 및 경고 메시지 설정
        nameEditText.addTextChangedListener(createTextWatcher(nameEditText, 10, "최대 10자까지 입력 가능합니다"))
        infoEditText.addTextChangedListener(createTextWatcher(infoEditText, 20, "최대 20자까지 입력 가능합니다"))

        // 이미지 선택 버튼
        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        // 저장 버튼
        saveButton.setOnClickListener {
            if (user != null) {
                user.name = nameEditText.text.toString()
                user.info = infoEditText.text.toString()
                user.team = teamEditText.text.toString()

                if (croppedImageUri != null) {
                    FireStorageConnection.deleteDirectory("users/${user.email}")
                    FireStorageConnection.addFile("users/${user.email}", croppedImageUri) { success, filePath ->
                        if (success) {
                            user.profile = filePath
                            saveUser(user, app)
                        } else {
                            Toast.makeText(this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    saveUser(user, app)
                }
            }
            // 일단 폐기
            /*
            // TODO: 여기에 팀 경기 일정을 DB에 저장하는 곳
            dbHelper = GameDBHelper(this)
            val db = dbHelper.readableDatabase

            val cursor: Cursor = db.rawQuery("SELECT ${teamEditText.text.toString()} FROM teamDataTBL ORDER BY date DESC;", null)
            // 저장된 팀이 저장되지 않았을 때, 새로 저장함
            if(cursor.count == 0){
                //TODO: 이곳에서 크롤링해서 DB에 저장함
                dbHelper.deleteTeamCalTable() // 삭제

            }
            cursor.close()

             */
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                startCropActivity(uri)
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            UCrop.getOutput(data!!)?.let { uri ->
                croppedImageUri = uri
                profileImageView.setImageURI(uri)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            croppedImageUri = null
            val cropError = UCrop.getError(data!!)
            Toast.makeText(this, "이미지 크롭 실패: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "croppedImage.jpg"))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .start(this)
    }

    private fun createTextWatcher(editText: EditText, maxLength: Int, warningMessage: String): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editText.removeTextChangedListener(this)
                if (s?.length ?: 0 > maxLength) {
                    Toast.makeText(this@EditActivity, warningMessage, Toast.LENGTH_SHORT).show()
                    editText.setText(s?.substring(0, maxLength))
                    editText.setSelection(maxLength)
                }
                editText.addTextChangedListener(this)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun saveUser(user: MyUser, app: MyApplication) {
        FireStoreConnection.setDocument("users/${user.email}", user) { success, _ ->
            if (success) {
                app.currentUser = user
                val intent = Intent(this, MainViewActivity::class.java)
                intent.putExtra("INITIAL_PAGE", 4) // 예: 2번째 페이지로 설정
                startActivity(intent)

                finish()
            } else {
                Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("회원정보 수정")
            setMessage("수정 정보를 폐기하시겠습니까?")

            setPositiveButton("네") { _, _ ->
                // 컨텍스트를 명시적으로 지정
                val intent = Intent(this@EditActivity, MainViewActivity::class.java)
                intent.putExtra("INITIAL_PAGE", 4) // 초기 페이지 전달
                startActivity(intent)
                finish() // 현재 Activity 종료
            }
            setNegativeButton("아니요") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }
            show()
        }
    }

    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 1001
    }

}
