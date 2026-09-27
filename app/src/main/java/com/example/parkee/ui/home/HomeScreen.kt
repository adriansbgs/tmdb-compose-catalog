package com.example.parkee.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkee.R
import com.example.parkee.core.common.AppError
import com.example.parkee.core.designsystem.component.EmptyState
import com.example.parkee.core.designsystem.component.FeaturedMovieCard
import com.example.parkee.core.designsystem.component.InlineErrorState
import com.example.parkee.core.designsystem.component.MovieCard
import com.example.parkee.core.designsystem.component.MovieCarouselPlaceholder
import com.example.parkee.core.designsystem.component.PlaceholderBox
import com.example.parkee.core.designsystem.theme.ParkeeTheme
import com.example.parkee.domain.model.Movie

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onMovieClick: (Int) -> Unit,
    onFavoriteListClick: () -> Unit,
    onRetrySection: (MovieSectionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { HomeTopBar(onFavoriteListClick = onFavoriteListClick) },
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FeaturedMovieSection(
                    title = stringResource(R.string.home_section_popular),
                    state = uiState.popular,
                    onMovieClick = onMovieClick,
                    onRetry = { onRetrySection(MovieSectionType.POPULAR) },
                )
            }

            item {
                MovieCarouselSection(
                    title = stringResource(R.string.home_section_top_rated),
                    state = uiState.topRated,
                    onMovieClick = onMovieClick,
                    onRetry = { onRetrySection(MovieSectionType.TOP_RATED) },
                )
            }

            item {
                MovieCarouselSection(
                    title = stringResource(R.string.home_section_now_playing),
                    state = uiState.nowPlaying,
                    onMovieClick = onMovieClick,
                    onRetry = { onRetrySection(MovieSectionType.NOW_PLAYING) },
                )
            }
        }
    }
}

@Composable
private fun SectionContainer(
    title: String,
    state: SectionState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit,
    successContent: @Composable (List<Movie>) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        when (state) {
            SectionState.Loading -> loadingContent()
            is SectionState.Error -> InlineErrorState(state.error, onRetry)
            is SectionState.Success ->
                if (state.movies.isEmpty()) {
                    EmptyState(stringResource(R.string.home_section_empty))
                } else {
                    successContent(state.movies)
                }
        }
    }
}

@Composable
fun MovieCarouselSection(
    title: String,
    state: SectionState,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) = SectionContainer(
    title = title,
    state = state,
    onRetry = onRetry,
    modifier = modifier,
    loadingContent = {
        MovieCarouselPlaceholder()
    },
    successContent = { movies ->
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
)


private val dummyMovies = listOf(
    Movie(1, "Dune", "", null, null, "2021-10-22", 8.0),
    Movie(2, "Oppenheimer", "", null, null, "2023-07-21", 8.1),
    Movie(
        3,
        "The Lord of the Rings: The Fellowship of the Ring",
        "",
        null,
        null,
        "2001-12-19",
        8.4
    ),
)

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    ParkeeTheme {
        HomeScreen(
            uiState = HomeUiState(
                popular = SectionState.Success(dummyMovies),
                topRated = SectionState.Success(dummyMovies),
                nowPlaying = SectionState.Success(dummyMovies),
            ),
            onFavoriteListClick = {},
            onMovieClick = {},
            onRetrySection = {},
        )
    }
}

@Composable
private fun FeaturedMovieSection(
    title: String,
    state: SectionState,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) = SectionContainer(
    title = title,
    state = state,
    onRetry = onRetry,
    modifier = modifier,
    loadingContent = {
        PlaceholderBox(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(200.dp)
        )
    },
    successContent = { movies ->
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(movies, key = { it.id }) { movie ->
                FeaturedMovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    },
)


@Composable
fun HomeRoute(
    onMovieClick: (Int) -> Unit,
    onFavoriteListClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onMovieClick = onMovieClick,
        onFavoriteListClick = onFavoriteListClick,
        onRetrySection = {type -> viewModel.retrySection(type)},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onFavoriteListClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = onFavoriteListClick) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.action_favorite_list)
                )
            }
        }
    )
}


@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPartialErrorPreview() {
    ParkeeTheme {
        HomeScreen(
            uiState = HomeUiState(
                popular = SectionState.Success(dummyMovies),
                topRated = SectionState.Error(AppError.NoConnection),
                nowPlaying = SectionState.Loading,
            ),
            onFavoriteListClick = {},
            onMovieClick = {},
            onRetrySection = {},
        )
    }
}