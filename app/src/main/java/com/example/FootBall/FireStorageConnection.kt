package com.example.FootBall

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

open class FireStorageConnection{
    companion object {
        open fun addFile(route: String, fileURL: Uri?, isCompleted: (success:Boolean,filePath: String) -> Unit) {

            if(fileURL == null)
                return

            val storage = FirebaseStorage.getInstance()
            val imageRef = storage.reference.child(route + "/" + UUID.randomUUID())
            imageRef.putFile(fileURL)
                .addOnSuccessListener {
                    // 이미지 업로드가 성공하면, 해당 이미지의 경로를 가져와서 반환해줌
                    isCompleted(true,imageRef.path)

                }
                .addOnFailureListener {
                    isCompleted(false,"")
                    Log.d("FireStorageConnection", "imageUproad failure")
                }
        }
        //정적함수. 파이어스토리지 상의 이미지 경로를 가지고 이미지뷰에 이미지표시하는 함수
        open fun bindImageByPath(context: Context, imagePath: String, imageView: ImageView) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child(imagePath)

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Context가 유효한지 확인
                if (context is Activity && (context.isDestroyed || context.isFinishing)) {
                    Log.e("bindImageByPath", "Activity is destroyed, skipping Glide load.")
                    return@addOnSuccessListener
                }

                // Glide를 사용하여 이미지 로드
                Glide.with(context)
                    .load(uri)
                    .into(imageView)
            }.addOnFailureListener { exception ->
                imageView.visibility = ImageView.INVISIBLE
                Log.e("bindImageByPath", "Image loading failed: ${exception.message}")
            }
        }


        open fun deleteFile(filePath: String,callBack:(success: Boolean)->Unit){
            // FirebaseStorage 인스턴스 초기화
            val storage = FirebaseStorage.getInstance()

            // 파일 경로로부터 StorageReference 객체를 생성
            val storageRef= storage.reference.child(filePath)

            // 파일 삭제
            storageRef.delete()
                .addOnSuccessListener {
                    //삭제 성공 시 실행
                    callBack(true)
                }
                .addOnFailureListener { exception ->
                    //삭제 실패 시 실행
                    callBack(false)
                }
        }

        //경로를 삭제하는 함수. (성공여부반환하는 콜백은 만들기 어려워서 뺐음)
        open fun deleteDirectory(directoryPath: String) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            // 주어진 디렉터리 경로에 있는 모든 파일을 나열하기
            val directoryRef = storageRef.child(directoryPath)

            // 디렉터리 내의 모든 파일을 나열
            directoryRef.listAll()
                .addOnSuccessListener { listResult ->
                    // 나열된 파일들 삭제
                    for (item in listResult.items) {
                        // 각 파일 삭제
                        item.delete()
                        /*
                            .addOnSuccessListener {
                                println("파일 ${item.name}이 삭제되었습니다.")
                            }
                            .addOnFailureListener { exception ->
                                println("파일 ${item.name} 삭제 실패: $exception")
                            }

                             */
                    }

                    // 서브폴더가 있다면 재귀적으로 삭제
                    for (prefix in listResult.prefixes) {
                        deleteDirectory(prefix.path)  // 서브 디렉터리 재귀적으로 삭제
                    }
                }
            /*
                .addOnFailureListener { exception ->
                    println("디렉터리 파일 나열 실패: $exception")
                }

                 */

        }
    }
}