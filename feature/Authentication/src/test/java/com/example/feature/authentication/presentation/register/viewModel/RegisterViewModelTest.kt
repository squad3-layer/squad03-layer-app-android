package com.example.feature.authentication.presentation.register.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.example.feature.authentication.domain.register.useCase.RegisterUseCase
import com.example.services.analytics.AnalyticsTags
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
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val useCase: RegisterUseCase = mockk()
    private val analyticsHelper: AnalyticsTags = mockk(relaxed = true)
    private val renderScreenUseCase: RenderScreenUseCase = mockk()
    private val remoteConfig: FirebaseRemoteConfig = mockk(relaxed = true)

    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(com.google.firebase.crashlytics.FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<com.google.firebase.crashlytics.FirebaseCrashlytics>(relaxed = true)
        every { com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance() } returns mockCrashlytics

        viewModel = RegisterViewModel(useCase, analyticsHelper, renderScreenUseCase, remoteConfig)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(com.google.firebase.crashlytics.FirebaseCrashlytics::class)
    }

    @Test
    fun `register should update registerState with success when useCase returns success`() = runTest {
        // Given
        val request = RegisterRequest("user", "email@test.com", "12345678901", "password")
        coEvery { useCase(request) } returns Result.success(Unit)

        val observer: Observer<Result<Unit>> = mockk(relaxed = true)
        viewModel.registerState.observeForever(observer)

        // When
        viewModel.register(request)

        // Then
        verify { observer.onChanged(match { it.isSuccess }) }
        verify { analyticsHelper.logEvent("register_success") }
    }

    @Test
    fun `validateEmail should return true for valid email`() {
        val result = viewModel.validateEmail("test@example.com")
        assert(result)
        assert(viewModel.emailError.value == null)
    }

    @Test
    fun `validateCpf should return true for valid CPF`() {
        val result = viewModel.validateCpf("12345678901")
        assert(result)
        assert(viewModel.cpfError.value == null)
    }

    @Test
    fun `validateCpf should return false for invalid CPF length`() {
        val result = viewModel.validateCpf("123")
        assert(!result)
        assert(viewModel.cpfError.value != null)
    }

    @Test
    fun `validatePassword should return true for valid password`() {
        val result = viewModel.validatePassword("123456")
        assert(result)
        assert(viewModel.passwordError.value == null)
    }

    @Test
    fun `validateConfirmPassword should return true when passwords match`() {
        val result = viewModel.validateConfirmPassword("password", "password")
        assert(result)
        assert(viewModel.confirmPasswordError.value == null)
    }

    @Test
    fun `validateConfirmPassword should return false when passwords do not match`() {
        val result = viewModel.validateConfirmPassword("password", "wrong")
        assert(!result)
        assert(viewModel.confirmPasswordError.value != null)
    }
    @Test
    fun `onInputChangedRegister should enable button only when ALL fields are valid`() {
        val observer: Observer<Boolean> = mockk(relaxed = true)
        viewModel.isButtonEnabled.observeForever(observer)

        viewModel.onInputChangedRegister(
            "Leo Silva", "12345678901", "leo@test.com", "123456", "654321"
        )
        verify { observer.onChanged(false) }

        viewModel.onInputChangedRegister(
            "Leo Silva", "12345678901", "leo@test.com", "123456", "123456"
        )
        verify { observer.onChanged(true) }
    }
    @Test
    fun `validateCpf should return false for repeating digits`() {
        val result = viewModel.validateCpf("22222222222")
        assert(!result)
        assert(viewModel.cpfError.value == R.string.register_cpf_invalid)
    }
    @Test
    fun `register should update isLoading and log error on failure`() = runTest {
        val request = RegisterRequest("user", "email@test.com", "12345678901", "password")
        val exception = Exception("Network Error")
        coEvery { useCase(request) } returns Result.failure(exception)

        val loadingObserver: Observer<Boolean> = mockk(relaxed = true)
        viewModel.isLoading.observeForever(loadingObserver)

        viewModel.register(request)

        verify { loadingObserver.onChanged(true) }
        verify { loadingObserver.onChanged(false) }
        verify { analyticsHelper.logEvent("register_error", any()) }
    }
}
