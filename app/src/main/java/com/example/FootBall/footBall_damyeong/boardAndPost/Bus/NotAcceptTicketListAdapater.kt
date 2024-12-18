package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.FootBall.FireStoreConnection

import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.google.firebase.firestore.DocumentSnapshot

class NotAcceptTicketListAdapater
    (context: Context,
     private val resource: Int,
     private val TicketList: List<DocumentSnapshot>,)
    : ArrayAdapter<DocumentSnapshot>(context, resource, TicketList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val userBankName:TextView=view.findViewById(R.id.itemBusTicketOnManagerPage_userBankName)
        val price:TextView=view.findViewById(R.id.itemBusTicketOnManagerPage_userEmail)
        val userEmail:TextView=view.findViewById(R.id.itemBusTicketOnManagerPage_money)
        val button:Button=view.findViewById(R.id.itemBusTicketOnManagerPage_Button)
        //Item 객체 받아오기
        val item = TicketList[position].toObject(Ticket::class.java)
        if(item==null){
            view.visibility=View.GONE
            Log.e("NotAcceptTicketListAdapater","item empty error")
            return view
        }
        userBankName.text="입금자명 :"+item.bankName
        price.text="입금받아야 할 돈 :"+item.price+"원"
        userEmail.text="이메일 :"+ item.userEmail

        //입금수락버튼을 누르면 아이템을 /notAcceptedTicket 애서 /acceptedBusTicket 로 옮김.
        button.setOnClickListener{
            FireStoreConnection.addDocument("publicBoards/"+BoardActivity.user.team+"/acceptedBusTicket",item)
            {
                success, docPath ->
                if(success){
                    view.visibility=View.GONE
                    FireStoreConnection.documentDelete(TicketList[position].reference.path)
                    {
                        success2 ->
                        if(success2){
                            Toast.makeText(context,"입금수락완료",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context,"입금수락실패2",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(context,"입금수락실패",Toast.LENGTH_SHORT).show()
                }
            }
        }
        //점세개 버튼을 누를시에
        return view
    }

}