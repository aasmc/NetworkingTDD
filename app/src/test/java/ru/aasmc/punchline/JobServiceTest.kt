package ru.aasmc.punchline

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

private const val id = "6"
private const val joke = "This is a test joke"

class JobServiceTestUsingMockWebServer {

    @get:Rule
    val mockWebServer = MockWebServer()

    private val testJson = """{ "id": $id, "joke": "$joke" }"""

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val jokeService by lazy {
        retrofit.create(JokeService::class.java)
    }

    @Test
    fun getRandomJokeEmitsJoke() {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(testJson)
                .setResponseCode(200)
        )

        val testObserver = jokeService.getRandomJoke().test()

        testObserver.assertValue(Joke(id, joke))
    }

    @Test
    fun getRandomJokeGetsRandomJokeJson() {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(testJson)
                .setResponseCode(200)
        )

        val testObserver = jokeService.getRandomJoke().test()
        testObserver.assertNoErrors()
        assertEquals("/random_joke.json", mockWebServer.takeRequest().path)
    }

}

class JobServiceTestMockingService {
    private val jokeService: JokeService = mock()
    private val repository = RepositoryImpl(jokeService)

    @Test
    fun getRandomJokeEmitsJoke() {
        val joke = Joke(id, joke)
        whenever(jokeService.getRandomJoke())
            .thenReturn(Single.just(joke))

        val testObserver = repository.getJoke().test()
        testObserver.assertValue(joke)
    }
}






















