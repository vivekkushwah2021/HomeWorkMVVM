package com.imaginato.homeworkmvvm

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.imaginato.homeworkmvvm.data.local.login.User
import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.remote.login.LoginApi
import com.imaginato.homeworkmvvm.data.remote.login.UserDataRepository
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.ApiResponse
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.domain.apiModules
import com.imaginato.homeworkmvvm.domain.databaseModule
import com.imaginato.homeworkmvvm.domain.loginApiModules
import com.imaginato.homeworkmvvm.domain.netModules
import com.imaginato.homeworkmvvm.domain.repositoryModules
import com.imaginato.homeworkmvvm.domain.viewModelModules

import com.imaginato.homeworkmvvm.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@OptIn(KoinApiExtension::class)
class LoginViewModelUnitTest {

    private lateinit var viewModel: LoginViewModel
    private var loginApi: LoginApi = mock {}
    private lateinit var repository: UserDataRepository
    private var loginResponse: Response<ApiResponse<LoginResponse>> = mock {}

    val dispatcher = StandardTestDispatcher()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setInit() {
        Dispatchers.setMain(dispatcher)
        repository = UserDataRepository(loginApi)
        viewModel = LoginViewModel()
        startKoin {
            androidLogger(Level.ERROR)
            modules(
                databaseModule,
                netModules,
                apiModules,
                repositoryModules,
                viewModelModules,
                loginApiModules
            )
        }
    }


    @Test
    fun usernameIsEmpty() {
        viewModel.isDataValid("", "")
        assert(viewModel.uiMessage.awaitValue() == R.string.add_username)
    }

    @Test
    fun userNameIsNotEmpty() {
        viewModel.isDataValid("123", "")
        assert(viewModel.uiMessage.awaitValue() != R.string.add_username)
    }

    @Test
    fun passwordIsEmpty() {
        viewModel.isDataValid("1234", "")
        assert(viewModel.uiMessage.awaitValue() == R.string.add_password)
    }

    @Test
    fun passwordIsNotEmpty() {
        viewModel.isDataValid("1234", "123")
        assert(viewModel.uiMessage.awaitValue() != R.string.add_password)
    }

    @Test
    fun isPasswordLengthLessThen6() {
        viewModel.isDataValid("1234", "123")
        assert(viewModel.uiMessage.awaitValue() == R.string.invalid_password)
    }

    @Test
    fun isPasswordLengthNotLessThen6() {
        viewModel.isDataValid("1234", "123574")
        assert(viewModel.uiMessage.awaitValue() != R.string.invalid_password)
    }

    @Test
    fun `login with valid username should set loginResult as success`() = runTest(dispatcher) {

        val url = "https://private-222d3-homework5.apiary-mock.com/api/login"
        val request: LoginRequest = LoginRequest("jnfknfklsfs", "jhjkjkjkjkjk")
        whenever(
            loginApi.doLogin(
                any(),
                any()
            )
        ).doReturn(Response.success(ApiResponse(data = LoginResponse("455"))))
        viewModel.login(request)

//        coEvery { loginApi.doLogin(url,request) } returns loginResponse

        assert(viewModel.apiResponse.getOrAwaitValue() != null)


    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        try {
            afterObserve.invoke()
            // Don't wait indefinitely if the LiveData is not set.    if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        } finally {
            this.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

}

fun <T> LiveData<T>.awaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@awaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}