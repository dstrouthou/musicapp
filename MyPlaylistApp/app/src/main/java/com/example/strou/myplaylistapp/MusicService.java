package com.example.strou.myplaylistapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/** This is the class which handles all the services. For this purpose the class implements three different
 * listeners; the onPrepared, onError and onCompetion. The first is for preparing the service, the second is
 * for receiving any errors and the last one is for the showing that the service is running normally.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public MediaPlayer mPlayer; // declaring the media player.
    private ArrayList<Songs> SongsList; // declaring the Array with the songs.
    private int pos; // declaring the position as integer
    private String songTitle=""; // declaring the name of each song as empty.
    private static final int NOTIFY_ID=1; // declaring the notifications as integer with value 1.
    private Random random; // declaration of random for helping the Threads.
    private final IBinder musicBind = new MusicBinder(); // declaration of the music binder.
    static final String MY_ACTION = "NextSong"; // setting the action for the receiver

    /* The onCreate is the method for starting the service. It creates the service together with the player.
     * Then it sets the power manager of the player as a Wake Lock and also sets the audio stream as stream music.
     * Afterwards it sets the three implements methods and on the end it creates a new random variable.
     */
    public void onCreate(){
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        random=new Random();
    }

    /* On destroy method for destroying the service.
     */
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    /* Method for mapping the songs from the song Adapter to the list.
     */
    public void setList(ArrayList<Songs> theSongs){
        SongsList = theSongs;
    }

    /* Class for Music Sevrice where it extends binder. Then it creates a getter for the
     * service.
     */
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    /* Helper method for the Music Binder class.
    */
    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    /* Method for stopping the player.
    */
    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    /* Method for getting the title of each song and return it whenever is called.
     */
    public String getTitle() {
        Songs t = SongsList.get(pos);
        return t.getName();
    }

    /* Method for playing each song. First it resets the player and then it get each songs position.
     * Then it finds the title and the id of each song. Afterwards it sets the uri for getting the songs
     * from the sdcard. If the uri find songs in the sdcard the music service will start otherwise it will
     * send an error message.
     */
    public void playSong(){
        mPlayer.reset();
        Songs playSong = SongsList.get(pos);
        songTitle=playSong.getName();
        long currSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                (currSong));
        try
        {
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e) {
            Log.e("Error", "Unable to start service", e);
        }
        mPlayer.prepareAsync();
    }

    /* The following method is the corresponding method of the onCompletion implementation in the service class.
     * It is responsible for starting the next song in the player when a song is finish.
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    /* The following method is the corresponding method of the onError implementation in the service class.
     * It is responsible for checking if there are any errors when a song is playing.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    /* The following method is the corresponding method of the onPrepared implementation in the service class.
     * It is responsible for starting the playback and setting the notification for each song. It creates a builder
     * for this purpose and it adds a picture, a title, and where each element is being placed. Then
     * the NOTIFY_ID variable is used for starting it.
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Intent i = new Intent(this, Playlist.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder b = new Notification.Builder(this);
        b.setContentIntent(pIntent)
                .setSmallIcon(R.drawable.music2)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Now playing")
                .setContentText(songTitle);
        Notification notification = b.build();
        go();
        startForeground(NOTIFY_ID, notification);
    }

    /* Method for setting the position with the songIndex.
     */
    public void setSong(int songIndex){
        pos = songIndex;
    }

    /* Method for getting the Position of the song that is currently playing.
     */
    public int getPosition(){
        return mPlayer.getCurrentPosition();
    }

    /* Method for checking that a the correct song is playing using the position.
     */
    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    /* Method for pausing a song whenever is called.
     */
    public void pausePlayer(){
        mPlayer.pause();
    }

    /* Method for setting the seek bar using the position.
     */
    public void seek(int sposition){
        mPlayer.seekTo(sposition);
    }

    /* Method for start playing a song.
     */
    public void go(){
        mPlayer.start();
    }

    /* Method for stopping a song.
     */
    public void stop(){
        mPlayer.stop();
    }

    /* Method for moving to the previous song whenever is called.
     */
    public void playPrevious(){
        pos--;
        if(pos<0) pos=SongsList.size()-1;
        playSong();
    }

    /* Method for moving to the next song whenever is called. A new Intent method is created where
     * we set an action. This is being used for the broadcast receiver in order to sent the intents.
     */
    public void playNext(){
        pos++;
        if(pos>=SongsList.size()) pos=0;
        setSong(pos);

        Intent intent = new Intent();
        intent.setAction(MY_ACTION);
        intent.putExtra("Song", SongsList.get(pos));
        sendBroadcast(intent);
        playSong();
    }
}

