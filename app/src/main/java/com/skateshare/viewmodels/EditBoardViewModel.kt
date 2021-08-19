package com.skateshare.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.R
import com.skateshare.misc.*
import com.skateshare.modelUtils.toBoard
import com.skateshare.models.Board
import com.skateshare.repostitories.FirestoreBoards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class EditBoardViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid!!
    private val _board = MutableLiveData<Board>()
    val board: LiveData<Board> get() = _board
    private val _response = MutableLiveData<EventResponse>()
    val response: LiveData<EventResponse> get() = _response

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _board.postValue(FirestoreBoards.getBoard(uid).toBoard())
        }
    }

    fun editBoard(boardArgs: HashMap<String, String>, units: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parsedData = parseBoardData(boardArgs, units, uri) ?: throw Exception()
                FirestoreBoards.editBoard(uid, uri, parsedData)
                _response.postValue(EventResponse(R.string.board_saved_successfully, true))
            } catch (e: Exception) {
                _response.postValue(EventResponse(R.string.error_saving_board, false))
            }
        }
    }

    private fun parseBoardData(rawData: HashMap<String, String>, units: String,
                               uri: Uri?): HashMap<String, Any?>? {
        val parsedData = hashMapOf<String, Any?>()

        if (!dataIsValid(rawData, uri)
            || !setAmpHours(rawData, parsedData)
            || !setSpeed(rawData, parsedData, units))
            return null

        // Copy remaining data to hashmap
        for (key in listOf("batteryConfig", "motorConfig", "escConfig", "description"))
            parsedData[key] = rawData[key]
        parsedData["postedBy"] = uid
        if (board.value?.imageUrl != null)
            parsedData["imageUrl"] = board.value?.imageUrl

        return parsedData
    }

    private fun setAmpHours(data: HashMap<String, String>, parsedData: HashMap<String, Any?>) : Boolean {
        var ampHours = 0.0
        try {
            ampHours = data["ampHours"]!!.toDouble()
        } catch (e: NumberFormatException) {
            _response.postValue(EventResponse(R.string.error_saving_board, false))
            return false
        }
        parsedData["ampHours"] = ampHours
        return true
    }

    private fun setSpeed(data: HashMap<String, String>,
                         parsedData: HashMap<String, Any?>, unit: String) : Boolean {
        var speed = 0.0
        try {
            speed = data["speed"]!!.toDouble()
        } catch (e: NumberFormatException) {
            _response.postValue(EventResponse(R.string.error_saving_board, false))
            return false
        }
        when (unit) {
            UNIT_MILES -> {
                parsedData["topSpeedMph"] = speed
                parsedData["topSpeedKph"] = speed * MI_TO_KM
            }
            UNIT_KILOMETERS -> {
                parsedData["topSpeedKph"] = speed
                parsedData["topSpeedMph"] = speed / MI_TO_KM
            }
        }
        return true
    }

    private fun dataIsValid(data: HashMap<String, String>, uri: Uri?) : Boolean {
        for (key in data.keys) {
            if (data[key]!!.trim{it<=' '}.isEmpty()) {
                _response.postValue(EventResponse(R.string.fill_required_fields, false))
                return false
            }
        }
        return true
    }

    private fun sendErrorSavingBoardMessage() {
        _response.postValue(EventResponse(R.string.error_saving_board, false))
    }

    fun resetResponse() {
        _response.postValue(EventResponse(-1, false))
    }
}