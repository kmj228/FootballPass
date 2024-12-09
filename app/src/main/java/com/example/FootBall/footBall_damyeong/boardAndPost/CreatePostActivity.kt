package com.example.FootBall.footBall_damyeong.boardAndPost

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.databinding.ActivityCreatePostBinding
import com.example.FootBall.MyApplication
import java.util.Date

class CreatePostActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    //private lateinit var authorEditText: EditText
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var uploadButton: Button
    private var imageUri: Uri? = null

    private var boardPath:String?=""
    private var boardName:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titleEditText = binding.titleEditText
        contentEditText = binding.contentEditText
        //authorEditText = binding.authorEditText
        imageView = binding.postCreateImageView
        selectImageButton = binding.selectImageButton
        uploadButton = binding.uploadButton

        //보드경로 불러오기
        boardPath=intent.getStringExtra("boardPath")
        boardPath=intent.getStringExtra("boardPath")
        if(boardPath ==null){
            finish()
        }
        //이미지 선택시 콜백작성
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    imageUri=it
                    binding.postCreateImageView.setImageURI(it)
                }
            }
        //getContent.launch("image/*")로 이미지만 선택가능한 미디어선택작업
        selectImageButton.setOnClickListener {
            getContent.launch("image/*")
            //openImageChooser()
        }

        uploadButton.setOnClickListener {
            uploadPost()
        }
    }

    private fun uploadPost()
    {
        val app = application as MyApplication
        val user = app.currentUser

        val post= Post(
            title = titleEditText.text.toString() ?:"",
            author = user!!.name,
            email =user!!.email,
            content = contentEditText.text.toString() ?:"",
            timestamp = Date().time,//얘는 로컬시간이라 나중에 서버시간으로 바꿔야함.
            imagePath=null
        )
        if(imageUri != null){
            FireStorageConnection.addFile(boardPath!!, imageUri) {

                    success, filePath ->
                //파일을 스토리지에 올린 뒤 스토리지상의 경로를 post에 추가
                if (success) {//파일 올리기에 성공했다면
                    Log.d("FireStorageConnection", filePath)
                    post.imagePath = filePath //post에 추가

                    //파이어스토어에 올리기
                    uploadPost2(post)
                } else {//파일 올리기에 실패했다면
                    Toast.makeText(this, "이미지 업로드 오류", Toast.LENGTH_SHORT).show()
                    return@addFile
                }
            }
        }
        else //이미지를 안넣었으면 그냥 파이어베이스에 올리기
        {
            uploadPost2(post)
        }


    }
    private fun uploadPost2(post: Post)
    {
        //post를 파이어스토어에 올림.
        FireStoreConnection.addDocument(boardPath+"/posts", post) { success, docPath ->
            if (success) {
                finish()//게시가 완료되었으면 다시 돌아감.
            }
            else{
                Toast.makeText(this, "게시글 작성 실패", Toast.LENGTH_SHORT).show()
                //게시글 올리기를 실패했으면 앞서 스토리지에 올린 이미지를 삭제함
                if(post.imagePath=="" ||post.imagePath==null){
                    return@addDocument
                }
                FireStorageConnection.deleteFile(post.imagePath!!){}
            }
        }
    }
}





/*
var post=Post("ㅇ너루겨ㅜㅗ녛","ㅑㄱㅎ갸혁ㅎㅎㄱ");
FireStoreConnection.addDocument("board1/reference/itemInfos",post)
{
    success ->
    if(success){
        Toast.makeText(this,"잘 저장됨.",Toast.LENGTH_SHORT).show()
    }
    else{
        Toast.makeText(this,"안 저장됨.",Toast.LENGTH_SHORT).show()
    }
}


 */

/*
private fun openImageChooser() {
    // 이미지 선택을 위한 인텐트 시작
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*" // 이미지 파일만 선택 가능하도록 설정
    startActivityForResult(intent, PICK_IMAGE_REQUEST)// 이미지를 선택한 후 onActivityResult로 결과 처리

}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
        imageUri = data.data
        imageView.setImageURI(imageUri) // 선택한 이미지를 ImageView에 표시
    }
}

private fun uploadPost() {
    val title = titleEditText.text.toString()
    val content = contentEditText.text.toString()
    val author = authorEditText.text.toString()

    if (title.isEmpty() || content.isEmpty() || author.isEmpty()) {
        Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
        return
    }

    // 이미지가 선택되었으면 Firebase Storage에 업로드
    var imageUrl: String? = null
    if (imageUri != null) {
        val imageRef = storage.reference.child("posts_images/${UUID.randomUUID()}")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                // 이미지 업로드가 성공하면, 해당 이미지의 URL을 가져와서 Firestore에 저장
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    savePostToFirestore(title, content, author, imageUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    } else {
        // 이미지가 없으면, Firestore에 저장할 때 null을 저장
        savePostToFirestore(title, content, author, imageUrl)
    }
}

private fun savePostToFirestore(post:Post) {

    FireStoreConnection.addDocument("board1",post){
        success ->
        if(success){
            Toast.makeText(this, "게시글이 성공적으로 업로드되었습니다", Toast.LENGTH_SHORT).show()
            finish() // 게시글 업로드 후 이전 화면으로 돌아감
        }
        else{
            Toast.makeText(this, "게시글 업로드 실패", Toast.LENGTH_SHORT).show()
        }
    }
}

 */

 */
