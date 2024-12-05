package com.example.FootBall

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

open class FireStoreConnection {
    companion object {
        open fun onGetCollection(collection: String,callBack:(documents:List<DocumentSnapshot>)->Unit) {
            val db=FirebaseFirestore.getInstance()
            db.collection(collection)
                .get()
                .addOnSuccessListener { result ->
                    callBack(result.documents)
                }
        }
        open fun onGetDocument(documentPath:String,callBack:(document:DocumentSnapshot)->Unit)
        {
            Log.d("onGetDocument",documentPath)
            val db=FirebaseFirestore.getInstance()
            db.document(documentPath)
                .get()
                .addOnSuccessListener {
                    document->
                    if(document.exists())//만약 문서가 존재하면
                    {
                        callBack(document)
                    }
                    else
                    {
                        Log.d("onGetDocument","failure")
                    }
                }
        }
        //이 친구는 콜백에 성공여부까지 담아서 보내줌
        open fun onGetDocument(documentPath:String,callBack:(success:Boolean,document:DocumentSnapshot?)->Unit)
        {
            Log.d("onGetDocument",documentPath)
            val db=FirebaseFirestore.getInstance()
            db.document(documentPath)
                .get()
                .addOnSuccessListener {
                        document->
                    if(document.exists())//만약 문서가 존재하면
                    {
                        callBack(true,document)
                    }
                    else
                    {
                        callBack(false,null)
                    }
                }
                .addOnFailureListener{
                    callBack(false,null)
                }
        }
        open fun onGetCount(collection: String,callBack:(numberOfDocument:Long)->Unit)
        {
            val db=FirebaseFirestore.getInstance()
            db.collection(collection)
                .count()
                .get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Count fetched successfully
                    val snapshot = task.result
                    callBack(snapshot.count)
                }
                else{
                    Log.d("onGetCount","failure")
                }
            }
        }

        //문서삭제하기 ( 삭제여부를 담아 콜백호출)
        open fun documentDelete(documentPath:String,callBack:(success:Boolean)->Unit)
        {
            val db=FirebaseFirestore.getInstance()
            db.document(documentPath)
                .delete()
                .addOnSuccessListener {
                    callBack(true)
                }
                .addOnFailureListener{
                    callBack(false)
                }
        }

        //성공여부를 담아보내주는 콜백이 매개변수로 있음.
        open fun addDocument(collection: String,post:Any,
                             isCompleted:(success:Boolean,docPath:String)->Unit)
        {
            val db=FirebaseFirestore.getInstance()
            db.collection(collection)
                .add(post)
                .addOnSuccessListener {
                    documentReference->

                    isCompleted(true,documentReference.path)
                }
                .addOnFailureListener {
                    isCompleted(false,"")
                }
        }
        //이 친구는 추가로 Document의 id까지 지정할 수 있게하는 함수
        open fun setDocument(documentPath: String,post:Any,
                             isCompleted:(success:Boolean,docPath:String)->Unit)
        {
            val db=FirebaseFirestore.getInstance()
            db.document(documentPath)
                .set(post)
                .addOnSuccessListener {
                    isCompleted(true,documentPath)
                }
                .addOnFailureListener {
                    isCompleted(false,"")
                }
        }

        //이 함수는 키워드와 필드데이터가 100% 일치해야 검색됨. 부분문자열을 찾는 건 안됨.
        //문서검색하기(컬렉션경로,검색할필드명,키워드,콜백(성공여부,문서들)
        open fun selectDocuments(collection: String,field:String,keyword:String
                                ,callBack:(success: Boolean,documents:List<DocumentSnapshot>?)->Unit)
        {
            val db=FirebaseFirestore.getInstance()
            db.collection(collection)
                .whereGreaterThanOrEqualTo(field, keyword)
                .whereLessThan(field, keyword + "\uF8FF")
                .get()
                .addOnSuccessListener { result ->
                        callBack(true,result.documents)
                }
                .addOnFailureListener {
                    callBack(false,null)
                }
        }

    }
}