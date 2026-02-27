package com.example.feature.authentication.presentation.resetPassword.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.example.services.analytics.AnalyticsTags
import com.example.services.authentication.AuthenticationService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.example.feature.authentication.R
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val authService: AuthenticationService = mockk()
    private val analyticsHelper: AnalyticsTags = mockk(relaxed = true)
    private val renderScreenUseCase: RenderScreenUseCase = mockk()
    private val remoteConfig: FirebaseRemoteConfig = mockk(relaxed = true)

    private lateinit var viewModel: ResetPasswordViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(com.google.firebase.crashlytics.FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<com.google.firebase.crashlytics.FirebaseCrashlytics>(relaxed = true)
        every { com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance() } returns mockCrashlytics

        viewModel = ResetPasswordViewModel(authService, analyticsHelper, renderScreenUseCase, remoteConfig)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(com.google.firebase.crashlytics.FirebaseCrashlytics::class)
    }

    @Test
    fun `sendResetPasswordEmail should update resetResult to true when service returns success`() = runTest {


        val email = "teste@teste.com"
        coEvery { authService.sendPasswordResetEmail(email) } returns Result.success(Unit)

        val observer: Observer<Boolean> = mockk(relaxed = true)
        viewModel.resetResult.observeForever(observer)

        viewModel.sendResetPasswordEmail(email)

        verify { observer.onChanged(true) }
        verify { analyticsHelper.logEvent("reset_password_success") }
    }

    @Test
    fun `sendResetPasswordEmail should update resetResult to false when service returns failure`() = runTest {

        val email = "teste@teste.com"
        coEvery { authService.sendPasswordResetEmail(email) } returns Result.failure(Exception("Error"))

        val observer: Observer<Boolean> = mockk(relaxed = true)
        viewModel.resetResult.observeForever(observer)

        viewModel.sendResetPasswordEmail(email)

        verify { observer.onChanged(false) }
        verify { analyticsHelper.logEvent("reset_password_error", any()) }
    }

    @Test
    fun `validateEmail should return true for valid email`() {
        val result = viewModel.validateEmail("test@teste.com")
        assert(result)
        assert(viewModel.emailError.value == null)
    }

    @Test
    fun `onInputChanged should update isButtonEnabled`() {
        val observer: Observer<Boolean> = mockk(relaxed = true)
        viewModel.isButtonEnabled.observeForever(observer)

        viewModel.onInputChanged("test@teste.com")
        verify { observer.onChanged(true) }

        viewModel.onInputChanged("invalid")
        verify { observer.onChanged(false) }
    }
    @Test
    fun `sendResetPasswordEmail should update errorMessage with network error resource when exception is network error`() = runTest {

        val email = "teste@teste.com"
        val exception = Exception("network error")
        coEvery { authService.sendPasswordResetEmail(email) } returns Result.failure(exception)

        val observer: Observer<Int> = mockk(relaxed = true)
        viewModel.errorMessage.observeForever(observer)

        viewModel.sendResetPasswordEmail(email)

        verify { observer.onChanged(R.string.error_network_error) }
    }
    @Test
    fun `sendResetPasswordEmail should update isLoading during the process`() = runTest {
        coEvery { authService.sendPasswordResetEmail(any()) } returns Result.success(Unit)
        val loadingObserver: Observer<Boolean> = mockk(relaxed = true)
        viewModel.isLoading.observeForever(loadingObserver)

        viewModel.sendResetPasswordEmail("teste@teste.com")

        verifyOrder {
            loadingObserver.onChanged(true)
            loadingObserver.onChanged(false)
        }
    }
}
