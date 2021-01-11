package com.geniusscansdk.demo.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.geniusscansdk.core.FilterType;
import com.geniusscansdk.core.Quadrangle;

import java.util.UUID;

/**
 * Created by guillaume on 29/09/16.
 */

public class Page implements Parcelable {

    private Quadrangle quadrangle;
    private FilterType filterType;
    private boolean distortionCorrectionEnabled = true;

    private Image originalImage;
    private Image enhancedImage;

    public Page() {
        String uuid = UUID.randomUUID().toString();
        originalImage = new Image(uuid + "original.jpg");
        enhancedImage = new Image(uuid + "enhanced.jpg");
    }

    public Image getOriginalImage() {
        return originalImage;
    }

    public Image getEnhancedImage() {
        return enhancedImage;
    }

    public void setQuadrangle(Quadrangle quadrangle) {
        this.quadrangle = quadrangle;
    }

    public Quadrangle getQuadrangle() {
        return quadrangle;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setDistortionCorrectionEnabled(boolean distortionCorrectionEnabled) {
        this.distortionCorrectionEnabled = distortionCorrectionEnabled;
    }

    public boolean isDistortionCorrectionEnabled() {
        return distortionCorrectionEnabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Page> CREATOR
            = new Parcelable.Creator<Page>() {
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(quadrangle, 0);
        dest.writeSerializable(filterType);
        dest.writeParcelable(originalImage, 0);
        dest.writeParcelable(enhancedImage, 0);
    }

    private Page(Parcel in) {
        quadrangle = in.readParcelable(Quadrangle.class.getClassLoader());
        filterType = (FilterType) in.readSerializable();
        originalImage = in.readParcelable(Image.class.getClassLoader());
        enhancedImage = in.readParcelable(Image.class.getClassLoader());
    }
}
