package com.example.lauzhack

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CameraViewModel"

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val _isCapturing = MutableStateFlow(false)
    val isCapturing = _isCapturing.asStateFlow()

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage = _capturedImage.asStateFlow()

    private var captureJob: Job? = null
    private var cameraManager: CameraManager? = null

    fun initializeCameraManager(manager: CameraManager) {
        this.cameraManager = manager
    }

    fun toggleCapture() {
        if (_isCapturing.value) {
            stopCapture()
        } else {
            startCapture()
        }
    }

    private fun startCapture() {
        if (captureJob?.isActive == true) return
        _isCapturing.value = true
        Log.i(TAG, "Picture capture loop STARTED (every 5s).")

        captureJob = viewModelScope.launch {
            while (_isCapturing.value) {
                cameraManager?.takePicture()
                delay(5000L) // Capture every 5 seconds
            }
        }
    }

    private fun stopCapture() {
        captureJob?.cancel()
        captureJob = null
        _isCapturing.value = false
        Log.i(TAG, "Picture capture loop STOPPED.")
    }

    fun onImageCaptured(jpegData: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        _capturedImage.value = bitmap
        Log.i(TAG, "ViewModel updated with new captured image bitmap.")
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager?.shutdown()
        stopCapture()
        Log.d(TAG, "ViewModel cleared and camera resources released.")
    }
}
