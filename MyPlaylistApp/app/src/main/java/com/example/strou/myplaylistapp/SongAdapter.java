package com.example.strou.myplaylistapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/** This class extends a Base Adapter for implementing an Adapter that is being used
 * in the List View. The class also implements a Parcelable where adds the List View
 * in a parcel in order to help us retrieving the data that we want more easily.
 */
public class SongAdapter extends BaseAdapter implements Parcelable {
    private ArrayList<Songs> SongsArray; // declaration of the Array with the songs
    private LayoutInflater SongsLayout; // declaration of the Layout Inflater for the songs

    /* This is the constructor of the class. It has two parameters. The first is the
    * context where is being added to the LayoutInflater and the second is the theSongs
    * where is being used for matching with the songs in the Array.
     */
    public SongAdapter(Context context, ArrayList<Songs> theSongs){
        SongsArray=theSongs;
        SongsLayout=LayoutInflater.from(context);
    }

    /* The following is the default constructor of the implementation of the Parcelabel.
    * It has only one parameter where it creates the parcel with the Array of the
    * songs with a default method.
     */
    protected SongAdapter(Parcel in) {
        SongsArray = in.createTypedArrayList(Songs.CREATOR);
    }

    /* Default method of the implementation of the Parcelable with another two
    * overrided methods. The first is returning the parcel the it was created above
    * and the second is returning the size of the array.
    */
    public static final Creator<SongAdapter> CREATOR = new Creator<SongAdapter>() {
        @Override
        public SongAdapter createFromParcel(Parcel in) {
            return new SongAdapter(in);
        }

        @Override
        public SongAdapter[] newArray(int size) {
            return new SongAdapter[size];
        }
    };

    /* Method for counting and returning the size of the Array with the Songs whenever
    * is called.
     */
    @Override
    public int getCount() {
        return SongsArray.size();
    }

    /* Method for finding the position of each song in the Array. It returns the position
    * whenever is called.
     */
    @Override
    public Songs getItem(int position) {
        return SongsArray.get(position);
    }

    /* Method for finding each songs id from the position. It returns 0 because the first
    * element in an array is always 0.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /* The following method is being used for adding each song from the array in the
    * list view in the activity. A linear layout is being used for mapping the
    * results in the appropriate song layout. Then we initialize the texts that are
    * going to be used for the results and we get the song using the position together
    * with the title of each song. In the end we set the position as a tag and we return
    * the linear layout.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout sLayout = (LinearLayout)SongsLayout.inflate(R.layout.song_layout, parent, false);
        TextView tSong = (TextView)sLayout.findViewById(R.id.songview);
        Songs sCurrent = SongsArray.get(position);
        tSong.setText(sCurrent.getName());
        sLayout.setTag(position);
        return sLayout;
    }

    /* Default method of the implementation of the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /* Default method of the implementation of the Parcelable.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
