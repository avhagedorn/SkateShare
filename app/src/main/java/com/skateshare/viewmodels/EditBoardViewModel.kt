package com.skateshare.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.modelUtils.toBoard
import com.skateshare.models.Board
import com.skateshare.repostitories.FirestoreBoards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditBoardViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid!!
    private var _board = MutableLiveData<Board>()
    val board: LiveData<Board> get() = _board

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _board.postValue(FirestoreBoards.getBoard(uid).toBoard())
        }
    }

}