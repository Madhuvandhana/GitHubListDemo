package com.example.githubtoprepos.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.githubtoprepos.R
import com.example.githubtoprepos.data.remote.dto.Contributor
import com.example.githubtoprepos.domain.model.GithubRepos
import com.example.githubtoprepos.domain.model.util.Resource
import com.example.githubtoprepos.presentation.ui.sampleGithubRepos
import com.example.githubtoprepos.presentation.ui.theme.AppTheme
import com.example.githubtoprepos.presentation.ui.topContributor
import com.example.githubtoprepos.presentation.util.formatStarCount
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun GithubRepoScreen(viewModel: GithubRepoViewModel = hiltViewModel()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val contributorState by viewModel.contributorsState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onAction(GithubRepoViewAction.FetchRepositories)
    }
    GithubRepoScreenComponent(
        viewState = viewState,
        contributorState = contributorState,
        onAction = viewModel::onAction
    )
}

@Composable
fun GithubRepoScreenComponent(
    viewState: GithubRepoViewState,
    contributorState: ContributorViewState,
    onAction: (GithubRepoViewAction) -> Unit
) {
    when (val resource = viewState.resource) {
        is Resource.Loading -> {
            CircularProgressIndicator()
        }
        is Resource.Success ->  {
            RepositoryList(
                repositories = resource.data ?: emptyList(),
                contributorState = contributorState,
                onAction = onAction
            )
        }
        is Resource.Error -> {
            ErrorText(value = resource.message ?: stringResource(R.string.unknown_error))
        }
    }
}

@Composable
fun ErrorText(
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = value,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
    )
}

@Composable
fun RepositoryList(
    repositories: List<GithubRepos>,
    modifier: Modifier = Modifier,
    contributorState: ContributorViewState,
    onAction: (GithubRepoViewAction) -> Unit
) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                visibleItems.map { repositories[it.index] }
            }
            .distinctUntilChanged()
            .collect { visibleRepos ->
                onAction(GithubRepoViewAction.UpdateVisibleKeys(visibleRepos))
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = scrollState
    ) {
        items(
            items = repositories,
            key = { repo -> "${repo.owner.login}/${repo.name}"},
        ) { repo ->
            val key = "${repo.owner.login}/${repo.name}"
//            val topContributor = remember(contributorState.topContributors) {
//                contributorState.topContributors[key]
//            }
            val topContributor = contributorState.topContributors[key]
            RepositoryItem(repository = repo, topContributor)
            HorizontalDivider()
        }
    }
}

@Composable
fun RepositoryItem(
    repository: GithubRepos,
    topContributor: Contributor?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small))
    ) {

        Image(
            rememberAsyncImagePainter(model = repository.owner.avatarUrl),
            contentDescription = null,
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_size))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = repository.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            repository.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TopContributorComponent(topContributor)
        }
        Text(
            text = stringResource(id = R.string.star_count_format, formatStarCount(repository.stargazersCount)),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
fun TopContributorComponent(topContributor: Contributor?) {
    if (topContributor != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                rememberAsyncImagePainter(model = topContributor.avatar_url),
                contentDescription = "Contributor Avatar",
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_size_small))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
            Text(
                text = stringResource(R.string.top_contributor, topContributor.login),
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        Text(
            text = stringResource(R.string.contributors_data_unavailable),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GithubRepoScreenPreview() {
    AppTheme {
        GithubRepoScreenComponent(
            viewState = GithubRepoViewState(
                Resource.Success(
                    data = sampleGithubRepos
                )
            ),
            contributorState = ContributorViewState(
                topContributors = mutableStateMapOf(
                    "freeCodeCamp/freeCodeCamp" to topContributor
                )
            ),
            onAction = {}
        )
    }
}
