package com.harshiitx.habittickoff.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.model.JournalEntry
import com.harshiitx.habittickoff.data.repository.JournalRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _selectedEpochDay = MutableStateFlow(LocalDate.now().toEpochDay())
    val selectedEpochDay: StateFlow<Long> = _selectedEpochDay.asStateFlow()

    val entries: StateFlow<List<JournalEntry>> = repository.entries

    init {
        viewModelScope.launch { repository.load() }
    }

    fun selectDay(epochDay: Long) {
        _selectedEpochDay.value = epochDay
    }

    fun updateText(text: String) {
        viewModelScope.launch { repository.saveEntry(_selectedEpochDay.value, text) }
    }
}
