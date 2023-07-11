package com.example.seccraft_app.screens.kursus

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seccraft_app.collection.User.DataUser
import com.example.seccraft_app.collection.User.DataUserKursus
import com.example.seccraft_app.collection.User.UserLikePortofolio
import com.example.seccraft_app.collection.artikel.DataArtikel
import com.example.seccraft_app.collection.kursus.DataAlatdanBahan
import com.example.seccraft_app.collection.kursus.DataKursus
import com.example.seccraft_app.collection.portofolio.DataPortofolio
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DataKursusModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    val dataKursus = mutableStateListOf<DataKursus>()
    val dataUserKursus = mutableStateListOf<DataKursus>()

    init {
        getDataKursus()
        getDataKursusUser()
    }

    private fun getDataKursus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val querySnapshot =
                    firestore.collection("kursus").orderBy("time", Query.Direction.DESCENDING).get()
                        .await()
                val data = querySnapshot.toObjects(DataKursus::class.java)
                Log.d("dataModel", "KursusScreen: ${data.size}")
                dataKursus.addAll(data)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun getDataKursusUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = Firebase.auth
                val idUser = user.currentUser!!.uid
                val data = mutableListOf<DataKursus>()
                val querySnapshotUser = firestore.collection("users/$idUser/kursus")
                    .orderBy("time", Query.Direction.DESCENDING).get().await()

                querySnapshotUser.toObjects(DataUserKursus::class.java).forEach {
                    val querySnapshotKursus =
                        firestore.document("kursus/${it.idKursus}").get().await()

                    querySnapshotKursus.toObject(DataKursus::class.java)
                        ?.let { it1 -> data.add(it1) }
                }

                dataUserKursus.addAll(data)

            } catch (e: Exception) {
                Log.d("error kenapa", "getDataKursusUser: $e")
            }
        }
    }

}

suspend fun getDataKursusUser(): List<DataKursus> = suspendCoroutine { continuation ->
    val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth
    val idUser = user.currentUser!!.uid
    val data = mutableListOf<DataKursus>()

    db.collection("users/$idUser/kursus")
        .orderBy("time", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { querySnapshotUser ->
            val kursusTasks = mutableListOf<Task<DocumentSnapshot>>()
            querySnapshotUser.toObjects(DataUserKursus::class.java).forEach { userKursus ->
                val kursusTask = db.document("kursus/${userKursus.idKursus}").get()
                kursusTasks.add(kursusTask)
            }

            Tasks.whenAllSuccess<DocumentSnapshot>(kursusTasks)
                .addOnSuccessListener { snapshots ->
                    snapshots.forEach { snapshot ->
                        val kursus = snapshot.toObject(DataKursus::class.java)
                        kursus?.let { data.add(it) }
                    }
                    continuation.resume(data)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
        .addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
}

suspend fun userKursusCheck(idKursus: String): Boolean = suspendCoroutine { continuation ->

    val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth
    val idUser = user.currentUser!!.uid

    try {
        db.document("users/$idUser/kursus/$idKursus").get().addOnSuccessListener { userKursus->
            if (userKursus.exists()){
                continuation.resume(true)
            }
            else {
                continuation.resume(false)
            }
        }

    } catch (e: FirebaseFirestoreException) {
        continuation.resumeWithException(e)
    }

}


suspend fun getKursusWithId(id: String): DataKursus = suspendCoroutine { continuation ->

    val db = FirebaseFirestore.getInstance()

    try {
        db.document("kursus/$id")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val data = snapshot.toObject(DataKursus::class.java)
                    if (data != null) {
                        continuation.resume(data)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Failed to retrieve data"))
                    }
                } else {
                    continuation.resumeWithException(NoSuchElementException("Document not found"))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    } catch (e: FirebaseFirestoreException) {
        continuation.resumeWithException(e)
    }

}

suspend fun getPengikutKursus(id: String): Long = suspendCoroutine { continuation ->

    val db = FirebaseFirestore.getInstance()

    try {
        db.collection("kursus/$id/usersKursus")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val dataList = mutableListOf<DataUserKursus>()
                    snapshot.documents.forEach { document->
                        val data = document.toObject(DataUserKursus::class.java)
                        if (data!=null){
                            dataList.add(data)
                        }
                    }
                    continuation.resume(dataList.size.toLong())
                } else {
                    continuation.resume(0)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    } catch (e: FirebaseFirestoreException) {
        continuation.resumeWithException(e)
    }

}

suspend fun getAlatDanBahan(id: String, type: String): MutableList<DataAlatdanBahan> =
    suspendCoroutine { continuation ->

        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("kursus/$id/$type")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val dataList = mutableListOf<DataAlatdanBahan>()
                        for (document in snapshot.documents) {
                            val data = document.toObject(DataAlatdanBahan::class.java)
                            if (data != null) {
                                dataList.add(data)
                            }
                        }
                        continuation.resume(dataList)
                    } else {
                        continuation.resumeWithException(NoSuchElementException("Document not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: FirebaseFirestoreException) {
            continuation.resumeWithException(e)
        }

    }