package com.yongdd.o_der_re.server;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class BannerUri implements Parcelable {
    Banner banner;
    Uri uri;

    public BannerUri(Banner banner, Uri uri) {
        this.banner = banner;
        this.uri = uri;
    }

    protected BannerUri(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<BannerUri> CREATOR = new Creator<BannerUri>() {

        @Override
        public BannerUri createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public BannerUri[] newArray(int size) {
            return new BannerUri[size];
        }
    };

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
    }
}
