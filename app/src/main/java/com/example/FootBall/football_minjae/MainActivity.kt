package com.example.FootBall.football_minjae

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.R
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MainViewActivity
import com.example.FootBall.MyApplication
import com.example.FootBall.MyUser
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvSignUp: TextView = findViewById(R.id.tvSignUp)
        val tvForgotPassword: TextView = findViewById(R.id.tvForgotPassword)
        val cbAutoLogin: CheckBox = findViewById(R.id.cbAutoLogin) // 자동 로그인 체크박스

        // 자동 로그인 확인
        val savedEmail = sharedPreferences.getString("EMAIL", null)
        val savedPassword = sharedPreferences.getString("PASSWORD", null)
        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            performLogin(savedEmail, savedPassword)
        }

        // 로그인 버튼 클릭 시
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // 자동 로그인 활성화 상태 확인
                        if (cbAutoLogin.isChecked) {
                            // 자동 로그인 활성화 시 계정 정보 저장
                            sharedPreferences.edit().apply {
                                putString("EMAIL", email)
                                putString("PASSWORD", password) // 비밀번호 저장
                                apply()
                            }
                        } else {
                            // 자동 로그인 비활성화 시 저장된 정보 삭제
                            sharedPreferences.edit().clear().apply()
                        }

                        // Firestore에서 사용자 데이터 가져오기
                        fetchUserData(email)
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> "잘못된 이메일 또는 비밀번호입니다."
                            is FirebaseAuthInvalidUserException -> "해당 계정이 존재하지 않습니다."
                            else -> "로그인 실패: ${task.exception?.localizedMessage}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // 회원가입 화면으로 이동
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // 비밀번호 찾기 버튼 클릭 이벤트
        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "비밀번호 재설정 이메일이 전송되었습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "에러 : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Firestore에서 사용자 데이터 가져오기
    protected fun fetchUserData(email: String) {
        FireStoreConnection.onGetDocument("users/$email") { success, document ->
            if (!success || document == null) {
                Toast.makeText(this, "회원 데이터를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
                return@onGetDocument
            }

            val player = document.toObject(MyUser::class.java)
            if (player != null) {

                val app = application as MyApplication
                app.currentUser = MyUser(
                    email = player.email,
                    name = player.name,
                    info = player.info,
                    profile = player.profile,
                    team = player.team,
                    admin = player.admin
                )

                //싱글톤
                BoardActivity.user = app.currentUser!!

                val intent = Intent(this, MainViewActivity::class.java)
                intent.putExtra("INITIAL_PAGE", 2) // 예: 2번째 페이지로 설정
                startActivity(intent)

                finish() // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "유효하지 않은 사용자 데이터입니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 자동 로그인 수행
    private fun performLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserData(email)
                } else {
                    Toast.makeText(this, "자동 로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
