package com.yavuzmobile.borsaanalizim.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel: ViewModel() {

    protected suspend fun <T> handleResult(
        action: suspend () -> Flow<Result<T>>,
        onSuccess: suspend (T) -> Unit,
        onLoading: (() -> Unit)? = null,
        onError: suspend ((Result.Error<T>) -> Unit)
    ) {
        action().collect { result ->
            when (result) {
                is Result.Loading -> onLoading?.invoke()
                is Result.Error -> onError.invoke(result)
                is Result.Success -> onSuccess(result.data)
            }
        }
    }

    protected fun <T> updateUiState(
        stateFlow: MutableStateFlow<UiState<T>>,
        isLoading: Boolean = false,
        error: String? = null,
        data: T? = null
    ) {
        stateFlow.update { currentState ->
            currentState.copy(isLoading = isLoading, error = error, data = data)
        }
    }
}