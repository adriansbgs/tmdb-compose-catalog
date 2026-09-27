package com.example.parkee.data.mapper

import com.example.parkee.data.remote.dto.MovieDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class MovieMapperTest {

    private fun dto(
        id: Int = 0,
        title: String = "",
        posterPath: String? = null,
        overview: String = "",
        releaseDate: String = ""
    ) = MovieDto(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = null,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = 8.0
    )

    @Test
    fun `null poster path results in null posterUrl`() {
        val result = dto(posterPath = null).toDomain()
        assertNull(result.posterUrl)
    }

    @Test
    fun `blank overview is replaced with default text`() {
        val result = dto(overview = "").toDomain()
        assertEquals("No overview", result.overview)
    }

    @Test
    fun `posterPath present results in full url`() {
        val result = dto(posterPath = "/abc.jpg").toDomain()
        assertEquals("https://image.tmdb.org/t/p/w342/abc.jpg", result.posterUrl)
    }

    @Test
    fun `blank release date becomes dash`() {
        val result = dto(releaseDate = "").toDomain()
        assertEquals("-", result.releaseDate)
    }

    @Test
    fun `valid release date is formatted`() {
        val result = dto(releaseDate = "2022-01-01").toDomain()
        assertEquals("1 Jan 2022", result.releaseDate)
    }

    @Test
    fun `unrecognized releaseDate format is returned as is`() {
        val result = dto(releaseDate = "bukan tanggal").toDomain()
        assertEquals("bukan tanggal", result.releaseDate)
    }
}