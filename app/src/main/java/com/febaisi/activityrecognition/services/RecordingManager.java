package com.febaisi.activityrecognition.services;

/**
 * Created by BaisFe01 on 10/9/2016.
 */

public interface RecordingManager {
    boolean isRecording();
    void stopRecording();
    long getCurrentRecordingTime();
    void registerListener(RecordingListener recordingListener);
}
