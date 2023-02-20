package com.pec_acm.moviedroid.mainpage.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pec_acm.moviedroid.firebase.User
import kotlinx.coroutines.launch


class ProfileViewModel: ViewModel() {
    private var databaseReference = Firebase.database.reference
    private var userReference = databaseReference.child("Users")
    val user : MutableLiveData<User> = MutableLiveData()

    val overallRating: MutableLiveData<String> = MutableLiveData("Rate something to view your average rating!")
    val movieRating: MutableLiveData<String> = MutableLiveData("")
    val tvRating: MutableLiveData<String> = MutableLiveData("")

    fun setUserRatingValues(uid : String)
    {
        viewModelScope.launch {
            userReference.child(uid).get().addOnCompleteListener {
                val user = it.result.getValue(User::class.java)
                var movieRate = 0
                var tvRate = 0
                var movieCount = 0
                var tvCount = 0

                for (item in user!!.userList)
                {
                    if (item.personalScore != 0)
                    {
                        if (item.category == "tv")
                        {
                            tvCount += 1
                            tvRate += item.personalScore
                        }
                        else
                        {
                            movieCount += 1
                            movieRate += item.personalScore
                        }
                    }
                }

                if (movieCount + tvCount != 0)
                {
                    overallRating.value = buildString {
                        append("Overall Rating:\n")
                        append((movieRate + tvRate) / (movieCount + tvCount))
                        append("/10")
                    }
                }
                if (tvCount != 0)
                {
                    tvRating.value = buildString {
                        append("TV Shows Rating:\n")
                        append((tvRate / tvCount))
                        append("/10")
                    }
                }
                if (movieCount != 0)
                {
                    movieRating.value = buildString {
                        append("Movie Rating:\n")
                        append(movieRate / movieCount)
                        append("/10")
                    }
                }
            }
        }
    }

}