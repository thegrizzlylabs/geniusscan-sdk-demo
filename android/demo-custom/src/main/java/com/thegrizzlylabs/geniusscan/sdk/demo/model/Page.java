package com.thegrizzlylabs.geniusscan.sdk.demo.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.thegrizzlylabs.geniusscan.sdk.core.ImageType;
import com.thegrizzlylabs.geniusscan.sdk.core.Quadrangle;
import com.thegrizzlylabs.geniusscan.sdk.core.ScanContainer;

import java.util.UUID;

/**
 * Created by guillaume on 29/09/16.
 */

public class Page implements ScanContainer, Parcelable {

    private Quadrangle quadrangle;
    private ImageType imageType;
    private boolean distortionCorrectionEnabled = true;

    private Image originalImage;
    private Image enhancedImage;

    public Page() {
        String uuid = UUID.randomUUID().toString();
        originalImage = new Image(uuid + "original.jpg");
        enhancedImage = new Image(uuid + "enhanced.jpg");
    }

    @Override
    public Image getOriginalImage() {
        return originalImage;
    }

    @Override
    public Image getEnhancedImage() {
        return enhancedImage;
    }

    @Override
    public void setQuadrangle(Quadrangle quadrangle) {
        this.quadrangle = quadrangle;
    }

    @Override
    public Quadrangle getQuadrangle() {
        return quadrangle;
    }

    @Override
    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    @Override
    public ImageType getImageType() {
        return imageType;
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
        dest.writeSerializable(imageType);
        dest.writeParcelable(originalImage, 0);
        dest.writeParcelable(enhancedImage, 0);
    }

    private Page(Parcel in) {
        quadrangle = in.readParcelable(Quadrangle.class.getClassLoader());
        imageType = (ImageType) in.readSerializable();
        originalImage = in.readParcelable(Image.class.getClassLoader());
        enhancedImage = in.readParcelable(Image.class.getClassLoader());
    }
}
