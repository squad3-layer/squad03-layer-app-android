package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.feature.authentication.domain.login.model.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val analyticsHelper: AnalyticsHelper
) : ViewModel()
