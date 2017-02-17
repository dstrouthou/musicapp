package com.example.strou.myplaylistapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/* The SongButtons class is the class that demonstates the main activity with the song that is currently on play.
 * It has many different buttons and a seekbar. All of them are helping users to have more functionalities for the
 * played song.
 */
public class SongButtons extends AppCompatActivity implements View.OnClickListener {

    Button bPlay, bPrev, bRew, bFor, bNext; // declaring the buttons
    SeekBar sbar; // declaring the seekbar
    TextView sName, sDuration; // declaring the texts
    private Songs selectsong; // declaring the songs
    Thread updateSeekbar; // decrating a thread in the seekbar
    MusicService mService = new MusicService(); // declaring the Services class
    Intent intent; // declaring the intent
    private MyReceiver myReceiver; // declaring the Receiver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_buttons);

        bPlay = (Button) findViewById(R.id.bplay); // Mapping the variable to the correspond button in the activity
        bPrev = (Button) findViewById(R.id.bprev); // Mapping the variable to the correspond button in the activity
        bRew = (Button) findViewById(R.id.brew); // Mapping the variable to the correspond button in the activity
        bFor = (Button) findViewById(R.id.bff); // Mapping the variable to the correspond button in the activity
        bNext = (Button) findViewById(R.id.bnext); // Mapping the variable to the correspond button in the activity

        sName = (TextView) findViewById(R.id.songTitle); // Mapping the variable to the correspond text in the activity
        sDuration = (TextView) findViewById(R.id.songCurrenDuration2); // Mapping the variable to the correspond button in the activity

        sbar = (SeekBar) findViewById(R.id.songSb); // Mapping the variable to the seek bar in the activity

        Intent i = getIntent(); // declaring an intent
        Bundle b = i.getExtras(); // declaring a bundle
        selectsong = b.getParcelable("Songlist"); // map the sa with the Array that has a Songlist name
        int pos = b.getInt("pos", 0); // mapping the position in the 0
        final int songDuration = b.getInt("Duration", 0); // getting each songs duration
        sName.setText(selectsong.getName()); // setting the name of the song on a text box
        long newsongDur = songDuration; // setting the duration of the song as a new song Dur
        //The following lines are converting songs duration from milliseconds to minutes.
        long minutes = TimeUnit.MILLISECONDS.toMinutes(newsongDur);
        newsongDur -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(newsongDur);
        StringBuilder sbuilder = new StringBuilder(6);
        sbuilder.append(minutes < 10 ? "0" + minutes : minutes);
        sbuilder.append(":");
        sbuilder.append(seconds < 10 ? "0" + seconds : seconds);
        final String newsongDur1 = sbuilder.toString();
        sDuration.setText("0:00"+newsongDur1); // setting the starting duration as 0

        sbar.setMax(songDuration); // setting the duration of the song in the seekbar

            /* The following lines is for initialise the thread in the seekbar and adding a method in it.
             * We setting the total duration of each song and the current position as 0. Then we checking
             * if the current position is less than the total duration. If this is true then the getPosition
             * method from the service class is called and we placing the current position in the seek bar.
             * Otherwise an Exception will occur.
            */
            updateSeekbar = new Thread() {
                @Override
                public void run() {
                    int totalduration = (songDuration);
                    int currentposition = 0;
                    while (currentposition<totalduration) {
                        try {
                            sleep(500);
                            currentposition = mService.getPosition();
                            sbar.setProgress(currentposition);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

        updateSeekbar.start(); // starting the seek bar

        /* In the following lines we setting a click listener for the seek bar where it will change each time
         * a song is playing. Then we convert the milliseconds in minutes and pass them to the text which is
         * above the seekbar.
         */
        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long newsongtotal = mService.getPosition();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(newsongtotal);
                newsongtotal -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(newsongtotal);
                StringBuilder sb = new StringBuilder(6);
                sb.append(minutes < 10 ? "0" + minutes : minutes);
                sb.append(":");
                sb.append(seconds < 10 ? "0" + seconds : seconds);
                sDuration.setText(sb.toString() + "/" + newsongDur1);
            }

            /* Default method for seek bar where it starts a tracking.
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /* Default method for seek bar where it stops the tracking. In this case the Service is
             * called and gets the progress of the seek bar.
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.seek(seekBar.getProgress());
            }
        });

        /* On click listener for handling the clicks for the Play button. If the button is clicked then
         * the service is called and the player start playing the song. If the user press once the button
         * then the song will pause and if user press it again the song will continue playing.
         */
        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService.isPlaying()) {
                    bPlay.setText(">");
                    mService.pausePlayer();
                } else {
                    bPlay.setText("II");
                    mService.go();
                }
            }
        });

        /* On click listener for handling the clicks for the Forward button. If the button is clicked then
         * it forwards the song for 5000 miliseconds.
         */
        bFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.seek(mService.getPosition()+5000);
            }
        });

        /* On click listener for handling the clicks for the Reward button. If the button is clicked then
         * it rewards the song for 5000 miliseconds.
         */
        bRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.seek(mService.getPosition()-5000);
            }
        });

        /* On click listener for handling the clicks for the Next button. If the button is clicked then
         * the player moves to the next song in the list. Also it has the functions to changing the
         * play button if it is in pause mode.
         */
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.stop();
                mService.playNext();
                sName.setText(mService.getTitle());
                if (mService.isPlaying()) {
                    bPlay.setText(">");
                } else {
                    bPlay.setText("II");
                }
            }
        });

        /* On click listener for handling the clicks for the Previous button. If the button is clicked then
         * the player moves to the previous song in the list. Also it has the functions to changing the
         * play button if it is in pause mode.
         */
        bPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.stop();
                mService.playPrevious();
                sName.setText(mService.getTitle());
                if (mService.isPlaying()) {
                    bPlay.setText(">");
                } else {
                    bPlay.setText("II");
                }
            }
        });

    }

    /* The following method is the default method for setting the connection of the List with the Services.
     * As in the service class this method implements two override methods where it checks if there is a
     * connection with the service or not.
     */
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder=(MusicService.MusicBinder) service;
            mService=binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /* Method for starting the connection with the services. The service class is called with the help
     * of the Intent method and a bindService is created. At the end the service is started.
     */
    public void onStart(){
        super.onStart();
        if (intent==null) {
            intent=new Intent(this,MusicService.class);
            bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    /* The following class extends the Broadcast Receiver where it will receive the intents from the Service
     * class and it will make the appropriate actions. In this class it sets the name of the song each time
     * it moves to the next one and also sets the seek bar to start again from the beginning.
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            selectsong = arg1.getParcelableExtra("Song");
            sName.setText(selectsong.getName());
            sbar.setProgress(0);
        }
    }

    /* Default method for the implementation of the onclick events.
     */
    @Override
    public void onClick(View v) {

    }
}
