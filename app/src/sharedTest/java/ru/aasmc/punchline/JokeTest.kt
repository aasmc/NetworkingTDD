package ru.aasmc.punchline

import com.github.javafaker.Faker
import kotlin.test.Test

class JokeTest {

    private val faker = Faker()

    @Test
    fun jokeReturnsJoke() {
        val title = faker.book().title()
        val joke = Joke(faker.code().isbn10(), title)

        assert(title == joke.joke)
    }

}