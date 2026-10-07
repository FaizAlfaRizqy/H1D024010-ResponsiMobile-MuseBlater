package com.pemmob.museblater.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pemmob.museblater.R
import com.pemmob.museblater.data.model.AnimeUiModel
import com.pemmob.museblater.data.model.Genre
import com.pemmob.museblater.ui.components.AnimeCardSkeleton
import com.pemmob.museblater.ui.components.EmptyState
import com.pemmob.museblater.ui.components.ErrorState
import com.pemmob.museblater.ui.theme.CardBorder
import com.pemmob.museblater.ui.theme.OrangePale
import com.pemmob.museblater.ui.theme.OrangePrimary
import com.pemmob.museblater.ui.theme.OrangeSoft
import com.pemmob.museblater.ui.theme.SurfaceWarmWhite
import com.pemmob.museblater.ui.theme.TextPrimary
import com.pemmob.museblater.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Top bar (full width) ─────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                HomeTopBar()
            }

            // ── Search bar (full width) ──────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // ── Genre chips (full width) ─────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                GenreChipsRow(
                    genres = uiState.genres,
                    selectedGenreId = uiState.selectedGenreId,
                    onGenreSelected = viewModel::onGenreSelected
                )
            }

            // ── Results header (full width) ──────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                ResultsHeader(
                    count = uiState.animeList.size,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            when {
                // Loading skeletons
                uiState.isLoading -> {
                    items(count = 6, span = { GridItemSpan(1) }) {
                        AnimeCardSkeleton(
                            modifier = Modifier.padding(
                                start = if (it % 2 == 0) 16.dp else 0.dp,
                                end = if (it % 2 == 1) 16.dp else 0.dp
                            )
                        )
                    }
                }

                // Error state
                uiState.errorMessage != null -> {
                    item(span = { GridItemSpan(2) }) {
                        ErrorState(
                            message = uiState.errorMessage!!,
                            onRetry = viewModel::retry
                        )
                    }
                }

                // Empty state
                uiState.isEmpty -> {
                    item(span = { GridItemSpan(2) }) {
                        EmptyState(
                            title = if (uiState.searchQuery.isNotBlank())
                                "No results for \"${uiState.searchQuery}\""
                            else
                                "No anime found",
                            message = if (uiState.searchQuery.isNotBlank())
                                "Try a different search term."
                            else
                                "Try selecting a different genre."
                        )
                    }
                }

                // Results grid
                else -> {
                    items(
                        items = uiState.animeList,
                        span = { GridItemSpan(1) },
                        key = { it.malId }
                    ) { anime ->
                        val isLeft = uiState.animeList.indexOf(anime) % 2 == 0
                        AnimeCard(
                            anime = anime,
                            onClick = { onAnimeClick(anime.malId) },
                            modifier = Modifier.padding(
                                start = if (isLeft) 16.dp else 0.dp,
                                end = if (!isLeft) 16.dp else 0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ── Top bar ──────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo mark
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(OrangePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo_mark),
                contentDescription = "Muse Blater logo",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Muse Blater",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Discover your next favorite anime.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }
        // Sparkle icon
        Icon(
            painter = painterResource(R.drawable.ic_sparkle),
            contentDescription = null,
            tint = OrangePrimary,
            modifier = Modifier.size(28.dp)
        )
    }
}

// ── Search bar ───────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search anime...",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search",
                tint = OrangePrimary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clear),
                        contentDescription = "Clear search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = OrangePrimary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
    )
}

// ── Genre chips ───────────────────────────────────────────────────────────────

@Composable
private fun GenreChipsRow(
    genres: List<Genre>,
    selectedGenreId: Int?,
    onGenreSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        item {
            GenreChip(
                label = "All",
                isSelected = selectedGenreId == null,
                onClick = { onGenreSelected(null) }
            )
        }
        items(items = genres, key = { it.malId }) { genre ->
            GenreChip(
                label = genre.name,
                isSelected = selectedGenreId == genre.malId,
                onClick = { onGenreSelected(genre.malId) }
            )
        }
    }
}

@Composable
private fun GenreChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) OrangePrimary else Color.White,
        animationSpec = tween(200),
        label = "chip_color"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextPrimary,
        animationSpec = tween(200),
        label = "chip_text_color"
    )

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                )
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = OrangePrimary,
            containerColor = Color.White,
            selectedLabelColor = Color.White,
            labelColor = TextPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            selectedBorderColor = OrangePrimary,
            borderColor = CardBorder,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        )
    )
}

// ── Results header ────────────────────────────────────────────────────────────

@Composable
private fun ResultsHeader(
    count: Int,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Anime Results",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Worth watching",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!isLoading && count > 0) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = OrangePale
            ) {
                Text(
                    text = "$count results",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ── Anime card ────────────────────────────────────────────────────────────────

@Composable
fun AnimeCard(
    anime: AnimeUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Poster image with overlay badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            ) {
                AsyncImage(
                    model = anime.imageUrl,
                    contentDescription = anime.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.ic_image_placeholder),
                    error = painterResource(R.drawable.ic_image_placeholder)
                )

                // Bottom scrim gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x80000000))
                            )
                        )
                )

                // Type badge (top-left)
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xCC000000)
                ) {
                    Text(
                        text = anime.type,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Rating badge (top-right)
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xE6FFFFFF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = anime.score,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Info section below poster
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = anime.genres.firstOrNull() ?: anime.type,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OrangePrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
