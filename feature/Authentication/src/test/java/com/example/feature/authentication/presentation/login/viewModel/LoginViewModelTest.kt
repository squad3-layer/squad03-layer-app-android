package com.example.feature.authentication.presentation.login.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.example.feature.authentication.domain.login.useCase.LoginUseCase
import com.example.services.analytics.AnalyticsTags
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val loginUseCase: LoginUseCase = mockk()
    private val analyticsHelper: AnalyticsTags = mockk(relaxed = true)
    private val renderScreenUseCase: RenderScreenUseCase = mockk()
    private val remoteConfig: FirebaseRemoteConfig = mockk(relaxed = true)

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics

        viewModel = LoginViewModel(loginUseCase, analyticsHelper, renderScreenUseCase, remoteConfig)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(com.google.firebase.crashlytics.FirebaseCrashlytics::class)
    }

    @Test
    fun `login should update loginState with success when useCase returns success`() = runTest {

        val email = "test@example.com"
        val password = "password123"
        coEvery { loginUseCase(email, password) } returns Result.success(Unit)

        val observer: Observer<Result<Unit>> = mockk(relaxed = true)
        viewModel.loginState.observeForever(observer)

        viewModel.login(email, password)

        verify { observer.onChanged(match { it.isSuccess }) }
        verify { analyticsHelper.logEvent("login_success") }
    }

    @Test
    fun `login should update loginState with failure when useCase returns failure`() = runTest {

        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Login failed")
        coEvery { loginUseCase(email, password) } returns Result.failure(exception)

        val observer: Observer<Result<Unit>> = mockk(relaxed = true)
        viewModel.loginState.observeForever(observer)

        viewModel.login(email, password)

        verify { observer.onChanged(match { it.isFailure }) }
        verify { analyticsHelper.logEvent("login_error", any()) }
    }

    @Test
    fun `validateEmail should return true for valid email`() {
        val result = viewModel.validateEmail("test@example.com")
        assert(result)
        assert(viewModel.emailError.value == null)
    }

    @Test
    fun `validateEmail should return false for invalid email`() {
        val result = viewModel.validateEmail("invalid-email")
        assert(!result)
        assert(viewModel.emailError.value != null)
    }

    @Test
    fun `validatePassword should return true for valid password`() {
        val result = viewModel.validatePassword("123456")
        assert(result)
        assert(viewModel.passwordError.value == null)
    }

    @Test
    fun `validatePassword should return false for short password`() {
        val result = viewModel.validatePassword("12345")
        assert(!result)
        assert(viewModel.passwordError.value != null)
    }
    @Test
    fun `login should toggle loading state`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.success(Unit)
        val loadingObserver: Observer<Boolean> = mockk(relaxed = true)
        viewModel.isLoading.observeForever(loadingObserver)

        viewModel.login("test@test.com", "123456")

        verifyOrder {
            loadingObserver.onChanged(true)
            loadingObserver.onChanged(false)
        }
    }

    @Test
    fun `onInputChanged should enable button only when email and password are valid`() {
        val observer: Observer<Boolean> = mockk(relaxed = true)
        viewModel.isButtonEnabled.observeForever(observer)

        viewModel.onInputChanged("email-errado", "123456")
        verify { observer.onChanged(false) }

        viewModel.onInputChanged("leo@teste.com", "123456")
        verify { observer.onChanged(true) }
    }
}
