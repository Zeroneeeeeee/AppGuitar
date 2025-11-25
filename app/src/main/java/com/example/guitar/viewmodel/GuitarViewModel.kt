package com.example.guitar.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitar.repository.impl.NoteEventImpl
import com.example.guitar.repository.impl.RecordingImpl
import com.example.guitar.dtmodels.NoteEventVM
import com.example.guitar.dtmodels.RecordingVM
import com.example.guitar.utils.SoundManager
import com.example.guitar.utils.toNoteEvent
import com.example.guitar.utils.toNoteEventVM
import com.example.guitar.utils.toRecording
import com.example.guitar.utils.toRecordingVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow

class GuitarViewModel(application: Application) : AndroidViewModel(application) {
    val noteEventRepo = NoteEventImpl(application)
    val recordingRepo = RecordingImpl(application)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _recordings = MutableStateFlow<List<RecordingVM>>(emptyList())
    val recordings: StateFlow<List<RecordingVM>> = _recordings.asStateFlow()

    val recordedSequence = mutableStateListOf<NoteEventVM>()
    private var recordingStartTime: Long = 0
    private var playbackJob: Job? = null

    private lateinit var soundManager: SoundManager
    var isSoundManagerInitialized = false

    fun fetchRecordings() {
        viewModelScope.launch {
            _recordings.value = emptyList()
            _recordings.value = recordingRepo.getAllRecordings().map { recording ->
                recording.toRecordingVM(
                    sequence = noteEventRepo.getNoteEventByRecordId(recording.id)
                        .map { note -> note.toNoteEventVM() })
            }
            Log.d("GuitarViewModel", "Recordings: ${_recordings.value}")
        }
        //_recordings.value = DemoMusic.demoRecordings
    }

    fun initialize(context: Context) {
        if (!isSoundManagerInitialized) {
            soundManager = SoundManager(context)
            isSoundManagerInitialized = true
        }
    }

    fun playHandSlap() {
        if (isSoundManagerInitialized) {
            soundManager.playHandSlap()
        }
    }

    // --- Logic Ghi Âm ---
    fun startRecording() {
        if (_isPlaying.value) return
        recordedSequence.clear()
        recordingStartTime = System.currentTimeMillis()
        _isRecording.value = true
    }

    fun stopRecording( getDuration:(Long)->Unit = {}) {
        if (!_isRecording.value) return
        val duration = System.currentTimeMillis() - recordingStartTime
        _isRecording.value = false
        getDuration(duration)
    }

    fun saveRecording(name: String, duration: Long) {
        viewModelScope.launch {
            val sortedSequence = recordedSequence.toList().sortedBy { it.timestamp }

            val newRecording = RecordingVM(
                name = name,
                durationMs = duration,
                sequence = sortedSequence,
                id = System.currentTimeMillis()
            )

            val recordingConvert = newRecording.toRecording()

            val id = recordingRepo.insertRecording(recordingConvert)
            newRecording.sequence.forEach { noteEvent ->
                Log.d("Recording", "Insert recording: ${noteEvent.toNoteEvent(id)}")
                noteEventRepo.addNoteEvent(noteEvent.toNoteEvent(id))
            }
        }
    }

    fun onGuitarFretClicked(baseNote: String, fret: Int) {
        if (!isSoundManagerInitialized) return

        val rate = 2f.pow(fret / 12f)

        soundManager.playNote(baseNote, rate)

        if (_isRecording.value) {
            val relativeTimestamp = System.currentTimeMillis() - recordingStartTime
            val newEvent = NoteEventVM(
                baseNote = baseNote,
                fret = fret,
                timestamp = relativeTimestamp
            )
            recordedSequence.add(newEvent)
        }
    }

    fun startPlayback(recordingSequence: List<NoteEventVM>? = null) {
        if (_isRecording.value || !isSoundManagerInitialized) return

        val sequenceToPlay = recordingSequence ?: recordedSequence.toList()
        if (sequenceToPlay.isEmpty()) return

        stopPlayback()
        _isPlaying.value = true

        playbackJob = viewModelScope.launch(Dispatchers.Default) {
            val sortedSequence = sequenceToPlay.sortedBy { it.timestamp }
            var noteIndex = 0
            val playbackStartTime = System.currentTimeMillis()

            while (isActive && noteIndex < sortedSequence.size) {
                val currentNoteEvent = sortedSequence[noteIndex]
                val expectedTime = currentNoteEvent.timestamp

                val elapsedTime = System.currentTimeMillis() - playbackStartTime
                val delayTime = expectedTime - elapsedTime

                if (delayTime > 0) {
                    delay(delayTime)
                }

                if (isActive) {
                    val rate = 2f.pow(currentNoteEvent.fret / 12f)
                    withContext(Dispatchers.Main) {
                        soundManager.playNote(currentNoteEvent.baseNote, rate)
                    }
                    noteIndex++
                }
            }
            withContext(Dispatchers.Main) { _isPlaying.value = false }
        }
    }

    fun renameRecord(newName: String, timestamp: Long) {
        viewModelScope.launch {
            recordingRepo.updateRecording(newName, timestamp)
            fetchRecordings()
        }
    }

    fun deleteRecord(timestamp: Long) {
        viewModelScope.launch {
            recordingRepo.deleteRecording(timestamp)
            fetchRecordings()
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        _isPlaying.value = false
    }

    fun renderToWavBuffer(recording: RecordingVM): ShortArray {
        return ShortArray(0)
    }

    fun exportRecording(recording: RecordingVM) {
        viewModelScope.launch(Dispatchers.IO) {
            renderToWavBuffer(recording)
            Log.i("AudioExport", "Quá trình kết xuất cho ${recording.name} hoàn tất.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isSoundManagerInitialized) {
            soundManager.release()
        }
        playbackJob?.cancel()
    }
}