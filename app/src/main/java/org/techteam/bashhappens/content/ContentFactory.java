package org.techteam.bashhappens.content;

import android.os.Parcel;
import android.os.Parcelable;

import org.techteam.bashhappens.content.bashorg.newest.BashOrgNewest;

public class ContentFactory implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(locale);
        parcel.writeString(currentSection.toString());
        parcel.writeParcelable(currentSource, i);
    }

    public static final Parcelable.Creator<ContentFactory> CREATOR
            = new Parcelable.Creator<ContentFactory>() {
        public ContentFactory createFromParcel(Parcel in) {
            return new ContentFactory(in);
        }

        public ContentFactory[] newArray(int size) {
            return new ContentFactory[size];
        }
    };

    private ContentFactory(Parcel in) {
        locale = in.readString();
        currentSection = Enum.valueOf(ContentSection.class, in.readString());
        currentSource = in.readParcelable(ContentSource.class.getClassLoader());
    }

    public enum ContentSection {
        BASH_ORG_NEWEST,
        IT_HAPPENS_NEWEST
    }

    private String locale;
    private ContentSection currentSection = null;
    private ContentSource currentSource = null;

    public ContentFactory(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public ContentSource buildContent(ContentSection section) {
        return buildContent(section, false);
    }

    public ContentSource buildContent(ContentSection section, boolean forceRecreate) {
        if (currentSection != section || forceRecreate) {
            currentSection = section;

            switch (currentSection) {
                case BASH_ORG_NEWEST:
                    currentSource = new BashOrgNewest(locale);
                case IT_HAPPENS_NEWEST:
                    break;
            }
        }
        return currentSource;
    }
}
