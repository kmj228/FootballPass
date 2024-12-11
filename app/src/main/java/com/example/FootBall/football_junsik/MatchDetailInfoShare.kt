package com.example.FootBall.football_junsik

// SharedViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray

// 이 클래스는 ViewModel로 데이터를 실시간으로 공유하는데 사용됨
// New에서 만들어진 데이터들을 프래그먼트 화면에 뿌려줌
class MatchDetailInfoShare : ViewModel() {
    private val _status = MutableLiveData<JSONArray>()
    val matchStatus: LiveData<JSONArray> get() = _status

    private val _homeScorer = MutableLiveData<String>()
    val matchHomeScorer: LiveData<String> get() = _homeScorer

    private val _awayScorer = MutableLiveData<String>()
    val matchAwayScorer: LiveData<String> get() = _awayScorer

    private val _homeLineUp = MutableLiveData<JSONArray>()
    val homeLineUp: LiveData<JSONArray> get() = _homeLineUp

    private val _awayLineUp = MutableLiveData<JSONArray>()
    val awayLineUp: LiveData<JSONArray> get() = _awayLineUp

    fun setStatus(status: JSONArray) {
        _status.value = status
    }

    fun setLineUp(homeLineUp: JSONArray, awayLineUp: JSONArray){
        _homeLineUp.value = homeLineUp
        _awayLineUp.value = awayLineUp
    }
}
