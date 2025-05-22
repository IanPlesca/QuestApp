package com.example.questapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class QuestState(
    val arrived: Boolean = false,
    val answered: Boolean = false,
    val points: Int = 0 // Adăugăm proprietatea points
)

class GameProgressViewModel : ViewModel() {

    // Starea quest-urilor
    private val _questProgress = MutableStateFlow(
        mutableMapOf(
            "quest11" to QuestState(),
            "quest21" to QuestState(),
            "quest31" to QuestState(),
            "quest41" to QuestState(),
            "quest51" to QuestState(),
            "quest61" to QuestState(),
            "quest71" to QuestState(),
            "quest81" to QuestState(),
            "quest91" to QuestState()
        )
    )
    val questProgress: StateFlow<Map<String, QuestState>> = _questProgress.asStateFlow()

    // Punctaj total
    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    // Checkpoint curent (0 = quest11, 1 = quest21, etc.)
    private val _currentCheckpoint = MutableStateFlow(0)
    val currentCheckpoint: StateFlow<Int> = _currentCheckpoint.asStateFlow()

    // Răspunsuri salvate pentru fiecare quest
    private val _selectedAnswers = MutableStateFlow(mutableMapOf<String, MutableMap<Int, String>>())
    val selectedAnswers: StateFlow<Map<String, MutableMap<Int, String>>> = _selectedAnswers.asStateFlow()

    // Salvează un răspuns pentru un quest
    fun saveAnswer(questId: String, questionIndex: Int, answer: String) {
        val answersForQuest = _selectedAnswers.value.getOrPut(questId) { mutableMapOf() }
        answersForQuest[questionIndex] = answer
        _selectedAnswers.value = _selectedAnswers.value.toMutableMap().apply {
            this[questId] = answersForQuest
        }
    }

    // Obține răspunsurile pentru un quest
    fun getAnswersForQuest(questId: String): Map<Int, String> {
        return _selectedAnswers.value[questId] ?: emptyMap()
    }

    // Marchează că utilizatorul a ajuns la un quest
    fun markArrived(questId: String) {
        val state = _questProgress.value[questId] ?: QuestState()
        _questProgress.value = _questProgress.value.toMutableMap().apply {
            this[questId] = state.copy(arrived = true)
        }
    }

    // Marchează că utilizatorul a finalizat un quest și actualizează punctajul
    fun markAnswered(questId: String, gainedScore: Int) {
        val state = _questProgress.value[questId] ?: QuestState()
        if (!state.answered) { // Adaugăm punctele doar dacă quest-ul nu a fost completat anterior
            _totalScore.value += gainedScore
        }
        _questProgress.value = _questProgress.value.toMutableMap().apply {
            this[questId] = state.copy(answered = true, points = gainedScore) // Salvăm punctele
        }

        _currentCheckpoint.value = when (questId) {
            "quest11" -> 1
            "quest21" -> 2
            "quest31" -> 3
            "quest41" -> 4
            "quest51" -> 5
            "quest61" -> 6
            "quest71" -> 7
            "quest81" -> 8
            "quest91" -> 9
            else -> _currentCheckpoint.value
        }

        setLastCompletedQuest(questId)
    }

    private val _lastCompletedQuest = MutableStateFlow("quest11")
    val lastCompletedQuest: StateFlow<String> = _lastCompletedQuest.asStateFlow()

    fun setLastCompletedQuest(questId: String) {
        _lastCompletedQuest.value = questId
    }

    // Obține starea unui quest
    fun getQuestState(questId: String): QuestState {
        return _questProgress.value[questId] ?: QuestState()
    }

    // Obține ruta quest-ului curent
    fun getCurrentQuestRoute(): String {
        Log.d("GameProgressViewModel", "Getting quest route, checkpoint: ${_currentCheckpoint.value}")
        return when (_currentCheckpoint.value) {
            0 -> "quest11"
            1 -> "quest21"
            2 -> "quest31"
            3 -> "quest41"
            4 -> "quest51"
            5 -> "quest61"
            6 -> "quest71"
            7 -> "quest81"
            8 -> "quest91"
            else -> "map" // Dacă toate quest-urile sunt finalizate, întoarce-te la hartă
        }
    }
}