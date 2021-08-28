package com.example.myapplication;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

public class MediaPlayerViewModel extends ViewModel {
    private MediaPlayer mediaPlayer;

    private final MutableLiveData<UIStates> emitter = new MutableLiveData<>();

    LiveData<UIStates> observeUIStates() {
        return emitter;
    }

    public void initialiseMediaPlayer() {
        emitter.postValue(UIStates.Loading);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            String url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3";
            mediaPlayer.setDataSource(url);
            preparePlayer();
            playMedia();
            setOnCompletionListener();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("music", "error" + e);

        }


    }

    private void setOnCompletionListener() {
        mediaPlayer.setOnCompletionListener(mediaPlayer -> emitter.postValue(UIStates.Finished));
    }


    public void playMedia() {
        if (emitter.getValue() == UIStates.Paused) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
        }
        if (emitter.getValue() == UIStates.Finished) {
            preparePlayer();
        }
        mediaPlayer.start();
        emitter.postValue(UIStates.Playing);


    }

    private void preparePlayer() {

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getPlayerProgress() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();

        if (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            return mediaPlayer.getCurrentPosition() / 1000;
        }
        return 0;
    }

    public void pauseMedia() {

        if (emitter.getValue() == UIStates.Playing) {
            mediaPlayer.pause();
        }
        emitter.postValue(UIStates.Paused);
    }

    public void stopMediaPlayer() {
        mediaPlayer.stop();
        emitter.postValue(UIStates.Stopped);
    }

    public int getMediaDuration() {

        return mediaPlayer.getDuration() / 1000;

    }

    public void destroyMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(progress * 1000);
        }
    }

    public Boolean mediaPlaying() {
        return mediaPlayer.isPlaying();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        destroyMediaPlayer();

    }
}

enum UIStates {
    Loading,
    Playing,
    Paused,
    Stopped,
    Finished
}

