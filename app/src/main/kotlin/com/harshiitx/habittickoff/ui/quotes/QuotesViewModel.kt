package com.harshiitx.habittickoff.ui.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.repository.SettingsRepository
import com.harshiitx.habittickoff.domain.quotes.QUOTES
import com.harshiitx.habittickoff.domain.quotes.pickNextQuoteIndex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuotesViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _quoteIndex = MutableStateFlow(0)
    val quoteIndex: StateFlow<Int> = _quoteIndex.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.load()
            shuffle()
        }
    }

    fun shuffle() {
        viewModelScope.launch {
            val next = pickNextQuoteIndex(size = QUOTES.size, excludingIndex = _quoteIndex.value)
            _quoteIndex.value = next
            settingsRepository.update { it.copy(lastQuoteIndex = next) }
        }
    }
}
