package com.example.strou.myplaylistapp;

import android.os.Parcel;
import android.os.Parcelable;

/** This class is responsible for modeling data for each song. We will get the title, the id,
 * and the duration of each song. It implements also a Parcelable for adding the List View
 * in a parcel in order to help us retrieving the data that we want more easily.
 */
public class Songs implements Parcelable {
    private String name; // Variable for the name of each song
    private long songid; // Variable for the id of each song
    private int songDuration; // Variable for the duration of each song

    /* The following is a constructor for the class where is initialise the id, title and the
    * duration of the each song.
    */
    public Songs(long songID, String songName,  int sDuration) {
        songid = songID;
        name = songName;
        songDuration = sDuration;
    }

    /* The following is the constructor of the implementation of the Parcelabel.
    * It has only one parameter and it creates a parcel  with the id, title and duration
    * of each song.
    */
    protected Songs(Parcel in) {
        name = in.readString();
        songid = in.readInt();
        songDuration = in.readInt();
    }

    /* Getter method for returning songs id whenever is called.
     */
    public long getID() {
        return songid;
    }

    /* Getter method for returning songs name as title whenever is called.
     */
    public String getName(){
        return name;
    }

    /* Getter method for returning songs duration whenever is called.
     */
    public int getSongDuration() {
        return songDuration;
    }

    /* Default method of the implementation of the Parcelable with another two
    * overrided methods. The first is returning the parcel the it was created above
    * and the second is returning the size of the array.
    */
    public static final Creator<Songs> CREATOR = new Creator<Songs>() {
        @Override
        public Songs createFromParcel(Parcel in) {
            return new Songs(in);
        }

        @Override
        public Songs[] newArray(int size) {
            return new Songs[size];
        }
    };

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
        dest.writeString(name);
        dest.writeLong(songid);
        dest.writeInt(songDuration);
    }
}
