package com.chat.zipchat.clone.Model.GifStickers;

import java.util.ArrayList;

public class GifResponse {

    ArrayList<GifData> data;

    public ArrayList<GifData> getData() {
        return data;
    }

    public class GifData {

        private Images images;

        public Images getImages() {
            return images;
        }
    }

    public class Images {

        private Original original;

        public Original getOriginal() {
            return original;
        }
    }

    public class Original {

        private String url;

        public String getUrl() {
            return url;
        }
    }
}
