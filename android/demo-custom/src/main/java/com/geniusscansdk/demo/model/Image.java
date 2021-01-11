package com.geniusscansdk.demo.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by guillaume on 29/09/16.
 */

public class Image implements Parcelable {

    public Image(String name) {
        this.name = name;
    }

    private String name;

    public File getFile(Context context) {
        return new File(context.getExternalFilesDir(null), name);
    }

    public String getAbsolutePath(Context context) {
        return getFile(context).getAbsolutePath();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Image> CREATOR
            = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    private Image(Parcel in) {
        name = in.readString();
    }
}
