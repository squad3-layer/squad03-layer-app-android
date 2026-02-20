package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.feature.news.domain.model.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun toggleFavorite(article: Article) {
        val userId = auth.currentUser?.uid ?: return
        val newState = !_isFavorite.value
        _isFavorite.value = newState

        val documentId = article.url.hashCode().toString()
        val docRef = firestore.collection("users").document(userId)
            .collection("favorites").document(documentId)

        if (newState) {
            docRef.set(article)
                .addOnFailureListener { _isFavorite.value = false }
        } else {
            docRef.delete()
                .addOnFailureListener { _isFavorite.value = true }
        }
    }

    fun checkIfIsFavorite(articleUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val documentId = articleUrl.hashCode().toString()

        firestore.collection("users").document(userId)
            .collection("favorites").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                _isFavorite.value = document.exists()
            }
    }
}