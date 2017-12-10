package com.rzico.weex.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by linhuasen on 15/11/22.
 */
public class MessageEntity implements Parcelable{
    private String type;
    private String content;

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.content);
    }

    public MessageEntity() {
    }

    protected MessageEntity(Parcel in) {
        this.type = in.readString();
        this.content = in.readString();
    }

    public static final Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {
        public MessageEntity createFromParcel(Parcel source) {
            return new MessageEntity(source);
        }

        public MessageEntity[] newArray(int size) {
            return new MessageEntity[size];
        }
    };
}
