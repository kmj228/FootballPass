package com.example.FootBall.football_minjae

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.R
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MyUser
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // XML의 뷰 참조
        val nicknameEditText = findViewById<EditText>(R.id.nicknameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val signupButton = findViewById<Button>(R.id.signupButton)

        // 회원가입 버튼 클릭 이벤트
        signupButton.setOnClickListener {
            val thisnickname = nicknameEditText.text.toString().trim()
            val thisemail = emailEditText.text.toString().trim()
            val thispassword = passwordEditText.text.toString().trim()
            val thisconfirmPassword = confirmPasswordEditText.text.toString().trim()

            // 입력값 검증
            if (thisnickname.isEmpty() || thisemail.isEmpty() || thispassword.isEmpty() || thisconfirmPassword.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (thispassword != thisconfirmPassword) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (thispassword.length < 6) {
                Toast.makeText(this, "비밀번호는 6자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase를 이용한 회원가입
            auth.createUserWithEmailAndPassword(thisemail, thispassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 회원가입 성공
                        sendEmailVerification() // 이메일 인증 메일 보내기
                        Toast.makeText(this, "회원가입 성공! 이메일 인증을 완료해주세요", Toast.LENGTH_LONG).show()

                        val user = MyUser(
                            email = thisemail,
                            name = thisnickname,
                            info = "자기소개 없음",
                            profile = "user/IMG_1393.jpeg",
                            team = "팀 없음",
                            admin = false
                        )

                        FireStoreConnection.setDocument("users/${user.email}", user) { success, docPath ->
                            if (success) {
                                println("유저 저장 성공")
                            } else {
                                println("유저 저장 실패")
                            }
                        }

                    } else {
                        // 회원가입 실패
                        Toast.makeText(this, "회원가입 실패 : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // 이메일 인증 메일 전송
    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "인증 이메일이 전송되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "이메일 인증 전송 실패 : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        // 뒤로가기 동작 수행
        super.onBackPressed() // 기본 동작을 포함하여 오류 해결

        // 추가 동작 수행
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 기존 스택을 모두 비우고 새로 시작
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}
