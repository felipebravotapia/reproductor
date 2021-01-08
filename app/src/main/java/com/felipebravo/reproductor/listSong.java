package com.felipebravo.reproductor;

public class listSong {
    private String image;
    private String music;
    private String trackName;
    private String artistName;
    private String releaseDate;
    private Integer trackId;

    public listSong() {
    }

    public listSong(String image, String music, String trackName, String artistName, String releaseDate, Integer trackId) {
        this.image = image;
        this.music = music;
        this.trackName = trackName;
        this.artistName = artistName;
        this.releaseDate = releaseDate;
        this.trackId = trackId;
    }

    public String getImage() {
        return image;
    }

    public String getMusic() {
        return music;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }
}
