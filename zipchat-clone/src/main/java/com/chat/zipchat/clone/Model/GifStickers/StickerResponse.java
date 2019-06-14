package com.chat.zipchat.clone.Model.GifStickers;

import java.util.ArrayList;

public class StickerResponse {

    private String status;
    private ArrayList<Result> result;

    public String getStatus() {
        return status;
    }

    public ArrayList<Result> getResult() {
        return result;
    }

    public class Result {

        private String _id;
        private ArrayList<Docs> docs;

        public String get_id() {
            return _id;
        }

        public ArrayList<Docs> getDocs() {
            return docs;
        }

        public class Docs {

            private String stickers;

            public String getStickers() {
                return stickers;
            }
        }

    }
}
