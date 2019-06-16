package com.chat.zipchat.clone;

import java.io.Serializable;

public class ChatAssert implements Serializable {

    private int audio_call;
    private int video_call;
    private int send;
    private int add;
    private int payments;
    private int photos;
    private int documents;
    private int location;
    private int gif;
    private int record_audio;
    private int record_video;
    private int cancel;


    public int getAudio_call() {
        return audio_call;
    }

    public void setAudio_call(int audio_call) {
        this.audio_call = audio_call;
    }

    public int getVideo_call() {
        return video_call;
    }

    public void setVideo_call(int video_call) {
        this.video_call = video_call;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public int getPhotos() {
        return photos;
    }

    public void setPhotos(int photos) {
        this.photos = photos;
    }

    public int getDocuments() {
        return documents;
    }

    public void setDocuments(int documents) {
        this.documents = documents;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getGif() {
        return gif;
    }

    public void setGif(int gif) {
        this.gif = gif;
    }

    public int getRecord_video() {
        return record_video;
    }

    public void setRecord_video(int record_video) {
        this.record_video = record_video;
    }

    public int getRecord_audio() {
        return record_audio;
    }

    public void setRecord_audio(int record_audio) {
        this.record_audio = record_audio;
    }

    public int getCancel() {
        return cancel;
    }

    public void setCancel(int cancel) {
        this.cancel = cancel;
    }
}
