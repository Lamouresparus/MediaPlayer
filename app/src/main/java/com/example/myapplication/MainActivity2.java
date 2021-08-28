package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    private MediaPlayerViewModel viewModel;
    private SeekBar seek_bar;
    private ProgressBar progressBar;
    private TextView playerText;
    private ImageView pause;
    private ImageView play;
    private ImageView stop;
    private TextView seekBarHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        seek_bar = findViewById(R.id.seek_bar);
        progressBar = findViewById(R.id.progress_bar);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        playerText = findViewById(R.id.listen_to_podcast_tv);
        seekBarHint = findViewById(R.id.seekBarHint);
        viewModel = new MediaPlayerViewModel();
        viewModel.initialiseMediaPlayer();
        setOnClickListeners();
        initializeSeekBar();
        observeData();
        observeUI();


    }

    private void setOnClickListeners() {
        pause.setOnClickListener(view -> viewModel.pauseMedia());

        play.setOnClickListener(view -> viewModel.playMedia());

        stop.setOnClickListener(view -> viewModel.stopMediaPlayer());
    }

    private void observeData() {
        viewModel.observeUIStates().observe(this, uiStates -> {
            switch (uiStates) {

                case Loading: {
                    Log.v("music", "Uistate is" + uiStates);

                    Log.v("music", "loading here");
                    progressBar.setVisibility(View.VISIBLE);
                    playerText.setVisibility(View.INVISIBLE);
                    break;
                }
                case Playing: {
                    Log.v("music", "Uistate is" + uiStates);

                    Log.v("music", "playing here");
                    progressBar.setVisibility(View.INVISIBLE);
                    playerText.setText("Playing...");
                    playerText.setVisibility(View.VISIBLE);
                    break;

                }

                case Stopped: {
                    progressBar.setVisibility(View.INVISIBLE);
                    playerText.setText("Stopped");
                    playerText.setVisibility(View.VISIBLE);
                    break;

                }

                case Paused: {
                    Log.v("music", "Paused here");
                    progressBar.setVisibility(View.INVISIBLE);
                    playerText.setText("Paused");
                    playerText.setVisibility(View.VISIBLE);
                    break;

                }
                case Finished: {
                    progressBar.setVisibility(View.INVISIBLE);
                    playerText.setText("Finished");
                    playerText.setVisibility(View.VISIBLE);
                    break;

                }

            }
        });

    }

    private void initializeSeekBar() {

        seek_bar.setMax(viewModel.getMediaDuration());

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(i * 1000f);
                Date date = new Date(x);

                if (x == 0) {
                    viewModel.stopMediaPlayer();
                }
                seekBarHint.setText(date.getMinutes() + ":" + date.getSeconds());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destroyMediaPlayer();
    }

    public void observeUI() {


        Handler mHandler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                int currentProgress = viewModel.getPlayerProgress();

                Date date = new Date(currentProgress * 1000L);

                seek_bar.setProgress(currentProgress);
                seekBarHint.setText(date.getMinutes() + ":" + date.getSeconds());
                if (viewModel.mediaPlaying()) {
                    mHandler.postDelayed(this, 1000);
                }
            }
        };

        mHandler.post(runnable);
    }

}