package com.example.strou.myplaylistapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/* This is the Main Activity which consists of a splash screen
* where it welcomes the user in the application and by clicking
* the show playlist it will demonstrate the list with the songs
* in the sd card.
*/
public class MainActivity extends AppCompatActivity {

    Button bPlayList; //declare the button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bPlayList = (Button) findViewById(R.id.bPlaylist); //finding the button in the activity

    }

    /* When the user clicks on the Show Playlist button the the onClickPlaylist
    * method will be called and it will direct the user to the activity with the playlist
    * using the intent action.
    */
    public void onClickPlaylist(View view) {

        Intent intent = new Intent(MainActivity.this, Playlist.class);
        startActivity(intent);

    }






}
