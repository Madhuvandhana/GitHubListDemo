package com.example.githubtoprepos.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubtoprepos.data.remote.dto.Contributor
import com.example.githubtoprepos.domain.model.util.Resource
import com.example.githubtoprepos.di.IoDispatcher
import com.example.githubtoprepos.domain.model.GithubRepos
import com.example.githubtoprepos.domain.repository.GitHubContributorsRepository
import com.example.githubtoprepos.presentation.util.NetworkStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import retrofit2.HttpException
import javax.inject.Inject

private const val NUMBER_OF_PROCESS = 5
private const val ONE_SECOND_DELAY_MS = 1000L
private const val DEFAULT_WAIT_TIME = 60000L

private const val NUM_CONCURRENT_API_CALLS = 3

@HiltViewModel
class GithubRepoViewModel @Inject constructor(
    private val repository: GitHubContributorsRepository,
    private val networkStateProvider: NetworkStateProvider,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState = MutableStateFlow(GithubRepoViewState())
    val viewState: StateFlow<GithubRepoViewState> get() = _viewState

    private val _contributorsState = MutableStateFlow<ContributorViewState>(ContributorViewState())
    val contributorsState: StateFlow<ContributorViewState> = _contributorsState

    // Job to keep track of ongoing fetch operations
    private var fetchJob: Job? = null
    private var nextAvailableRequestTime: Long = 0L

    private val semaphore = Semaphore(NUM_CONCURRENT_API_CALLS) // Limit concurrent API calls to 3

    fun onAction(action: GithubRepoViewAction) {
        when (action) {
            is GithubRepoViewAction.FetchRepositories -> fetchRepositories()
            is GithubRepoViewAction.UpdateVisibleKeys -> {
                Log.d("GithubRepoViewModel", "Visible items: ${action.visibleItems}")
                updateVisibleKeys(action.visibleItems)
            }
        }
    }

    private fun updateVisibleKeys(visibleItems: List<GithubRepos>) {
        fetchTopContributors(visibleItems)
    }

    fun fetchTopContributors(repositories: List<GithubRepos>) {
        // Cancel previous fetch job if it exists
        fetchJob?.cancel()
        val currentTime = System.currentTimeMillis()
        if (currentTime < nextAvailableRequestTime) {
            Log.w(
                "GithubRepoViewModel",
                "Rate limit active. Waiting for ${(nextAvailableRequestTime - currentTime) / 1000}s before retrying."
            )
            return
        }
        callTopContributorsApiInBatch(currentTime, repositories)
    }

    private fun callTopContributorsApiInBatch(currentTime: Long, repositories: List<GithubRepos>) {
        // Start a new job for fetching contributors
        fetchJob = viewModelScope.launch(dispatcher) {
            val contributorQueue = repositories.map { repo ->
                "${repo.owner.login}/${repo.name}" to repo
            }.toMutableList()

            while (contributorQueue.isNotEmpty()) {
                // Avoid secondary rate limits by usings a queue system to handle requests
                val batch = contributorQueue.take(NUMBER_OF_PROCESS) // Process in batches of 5
                contributorQueue.removeAll(batch)

                batch.map { (key, repo) ->
                    async {
                        Log.d("GithubRepoViewModel","contributor key:"+contributorsState.value.topContributors[key])
                        if (contributorsState.value.topContributors[key] == null && currentTime > nextAvailableRequestTime) {
                            semaphore.withPermit {
                                try {
                                    repository.getTopContributor(repo.owner.login, repo.name)
                                        .collect { topContributor ->

                                            updateContributorState(key, topContributor)
                                        }
                                } catch (e: Exception) {
                                    // Handle error silently and store null
                                    updateContributorState(key, null)
                                    // As per the guidelines when there is a rate limit error,
                                    // we should stop making requests temporarily
                                    if(e is HttpException && isRateLimitException(e)) {
                                        // Cancel the parent coroutine to stop all API calls
                                        handleRateLimitException(e)
                                        this@launch.cancel()
                                    }
                                }
                            }
                        }
                    }
                }.awaitAll()
                // To prevent hitting the rate limit too quickly, add delay after each batch
                // wait at least one second between each request. This will help avoid secondary rate limits
                delay(ONE_SECOND_DELAY_MS)
            }
        }
    }

    /**
     * Handles exceptions caused by rate limits(specifically to handle secondary rate limit)
     * from the API and calculates the next available request time.
     * This function specifically checks for `HttpException` and parses the headers to determine when the next
     * request can be made. It uses the "retry-after" or "x-ratelimit-reset" headers if available. If neither
     * header is present, it defaults to a fixed wait time.
     *
     * @param e The exception to handle, typically an `HttpException` thrown when the rate limit is exceeded.
     *
     * ### Behavior:
     * - If the "retry-after" header is present, calculates the wait time based on its value.
     * - If the "x-ratelimit-reset" header is present, calculates the reset time, ensuring it is not earlier
     *   than the current time.
     * - Logs a warning and applies a default wait time if no relevant headers are found.
     */

    private fun handleRateLimitException(e: Exception) {
        if (e is HttpException) {
            val headers = e.response()?.headers()
            val retryAfter = headers?.get("retry-after")?.toLongOrNull()
            if (retryAfter != null) {
                nextAvailableRequestTime = System.currentTimeMillis() + (retryAfter * ONE_SECOND_DELAY_MS)
                return
            }

            val resetTime = headers?.get("x-ratelimit-reset")?.toLongOrNull()
            if (resetTime != null) {
                nextAvailableRequestTime =
                    (resetTime * ONE_SECOND_DELAY_MS).coerceAtLeast(System.currentTimeMillis())
                return
            }
        }
        Log.w("GithubRepoViewModel", "Rate limit reached. Applying default wait time of 60 seconds.")
        nextAvailableRequestTime = System.currentTimeMillis() + DEFAULT_WAIT_TIME
    }

        private fun isRateLimitException(e: HttpException):Boolean {
            val headers = e.response()?.headers()
//            val retryAfter = headers?.get("retry-after")?.toLongOrNull()
//            if (retryAfter != null) return retryAfter * 1000

            val remaining = headers?.get("x-ratelimit-remaining")?.toLongOrNull()

            val resetTime = headers?.get("x-ratelimit-reset")?.toLongOrNull()
            if (remaining != null && remaining == 0L && resetTime != null) {
                Log.w(
                    "GithubRepoViewModel",
                    "Rate limit reached. Stopping processing. Reset at $resetTime."
                )
                return true
            }
            return false
        }

    private fun updateContributorState(key: String, topContributor: Contributor?) {
        _contributorsState.value.topContributors[key] = topContributor
    }

    private fun fetchRepositories() {
        if (!networkStateProvider.isInternetAvailable()) {
            _viewState.value = _viewState.value.copy(
                resource = Resource.Error(
                    message = "Internet connection is unavailable",
                    data = null
                )
            )
            return
        }
        _viewState.value = _viewState.value.copy(resource = Resource.Loading())
        viewModelScope.launch(dispatcher) {
            repository.getTopStarredRepositories().collect { resource ->
                _viewState.update { currentState ->
                    currentState.copy(resource = resource)
                }
            }
        }
    }

    // Cancel ongoing coroutines fetch contributors operations when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}