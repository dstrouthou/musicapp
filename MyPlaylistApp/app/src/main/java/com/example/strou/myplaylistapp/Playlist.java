package com.example.strou.myplaylistapp;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**Class for displaying the List with the songs from the sdcard when a user choose
 * the Show Playlist button in the Main activity. A Service is estamblished for handling
 * background operations of the music player.
 */
public class Playlist extends AppCompatActivity {

    public static ArrayList<Songs> SongsList; // declaration of the Array with the Songs
    private ListView MusicList; // declaration of the List View
    MusicService mService = new MusicService(); // declaration of the Music Service class which handles apps services
    Intent intent; // declaration of the Intent
    Bundle bundle; // declaration of the Bundle

    /* On create method for creating and initialising all the variables and methods.
    * The MusicList and SongsList are being used for mapping the list and the array in the activity.
    * A song adapter is created for compining the songs in the array also. Finally an on Click Method
    * is added in the list to handle users clicks on the playlist
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        MusicList = (ListView) findViewById(R.id.musiclist); // Mapping the list in the activity
        SongsList = new ArrayList<Songs>(); // Mapping the array with the songs
        final SongAdapter ma = new SongAdapter(this, SongsList);
        RetrieveSongs(); // call of the RetriveSongs Method

        /* Method for sorting alphabetically the songs in the playlist by the name.
         */
        Collections.sort(SongsList, new Comparator<Songs>() {
            public int compare(Songs a, Songs b) {
                return a.getName().compareTo(b.getName());
            }
        });
        MusicList.setAdapter(ma); // Setting the adapter in the playlist

        /* Setting an OnItemClickListener for handling each users clicks on the Playlist.
        * When a user clicks on a song in the List view then a new activity will appear. In
        * order to achieve this the Service class is called using the intent method.
        * We start by getting each songs position using the Service class. Then the clicked song
        * is start playing and a new intent is created with a bundle. We used the Parcelable to
        * get the song's name and place it on the top in the next activity. The duration of the song
        * is also shown in the next activity and we can send it using the bundle. At the end the intent
        * method is called for adding the bundles that we collect and show the next activity
        */
        MusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mService.setSong(position);
                mService.playSong();
                Intent i = (new Intent(getApplicationContext(), SongButtons.class));
                bundle = new Bundle();
                bundle.putParcelable("Songlist", ma.getItem(position));
                bundle.putInt("pos", position);
                bundle.putInt("Duration", ma.getItem(position).getSongDuration());
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    /* Default method from Android Studio for retrieving data from a content resolver.
    * Uri is used in this method for identifying the songs in the sdcard and with a
    * cursor is also used for checking each song. The we get the id, the title and the duration
    * of each song and we add them in the list with the songs.
     */
    public void RetrieveSongs()
    {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            Log.e("Query failed..", "Cursor cant find anything!!");
        } else if (!cursor.moveToFirst()) {
            Log.e("Query failed..", "No any media in the device!!");
        } else {
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            //int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                //String thisArtist = cursor.getString(artistColumn);
                int thisDuration = cursor.getInt(durationColumn);
                SongsList.add(new Songs(thisId,thisTitle,thisDuration));
            } while (cursor.moveToNext());
        }
    }

    /* The following method is the default method for setting the connection of the List with the Services.
     * It implements two override methods where it checks if there is a connection with the service or not.
     */
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder=(MusicService.MusicBinder) service;
            mService=binder.getService();
            mService.setList(SongsList);
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
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
    }
}
