package com.example.parkee.ui

import com.example.parkee.core.common.AppError
import com.example.parkee.core.common.DataResult
import com.example.parkee.domain.model.Movie
import com.example.parkee.ui.home.HomeViewModel
import com.example.parkee.ui.home.MovieSectionType
import com.example.parkee.ui.home.SectionState
import com.example.parkee.util.FakeMovieRepository
import com.example.parkee.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository
    private val dummyMovies = listOf(
        Movie(1, "Dune", "desc", null, null, "22 Okt 2021", 8.0)
    )

    @Before
    fun setup() {
        repository = FakeMovieRepository()
    }

    @Test
    fun `all sections succeed when all requests succeed`() = runTest {
        repository.popularResult = DataResult.Success(dummyMovies)
        repository.topRatedResult = DataResult.Success(dummyMovies)
        repository.nowPlayingResult = DataResult.Success(dummyMovies)

        val viewModel = HomeViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state.popular is SectionState.Success)
        assertTrue(state.topRated is SectionState.Success)
        assertTrue(state.nowPlaying is SectionState.Success)

    }

    @Test
    fun `one section failing does not affect other sections`() = runTest {
        repository.popularResult = DataResult.Success(dummyMovies)
        repository.topRatedResult = DataResult.Failure(AppError.NoConnection)
        repository.nowPlayingResult = DataResult.Success(dummyMovies)

        val viewModel = HomeViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state.popular is SectionState.Success)
        assertTrue(state.topRated is SectionState.Error)
        assertTrue(state.nowPlaying is SectionState.Success)
    }

    @Test
    fun `retry only reloads the requested section`() = runTest {
        repository.topRatedResult = DataResult.Failure(AppError.NoConnection)
        val viewModel = HomeViewModel(repository)

        assertEquals(1, repository.popularCallCount)
        assertEquals(1, repository.topRatedCallCount)
        assertEquals(1, repository.nowPlayingCallCount)

        viewModel.retrySection(MovieSectionType.TOP_RATED)

        assertEquals(1, repository.popularCallCount)
        assertEquals(2, repository.topRatedCallCount)
        assertEquals(1, repository.nowPlayingCallCount)
    }

    @Test
    fun `retry changes section from error to success`() = runTest {
        repository.topRatedResult = DataResult.Failure(AppError.NoConnection)
        val viewModel = HomeViewModel(repository)
        assertTrue(viewModel.uiState.value.topRated is SectionState.Error)

        repository.topRatedResult = DataResult.Success(dummyMovies)
        viewModel.retrySection(MovieSectionType.TOP_RATED)

        assertTrue(viewModel.uiState.value.topRated is SectionState.Success)
    }
}