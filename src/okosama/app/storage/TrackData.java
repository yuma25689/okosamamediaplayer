package okosama.app.storage;

import java.io.Serializable;

public class TrackData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8254196509016146397L;


	@Override
	public String toString()
	{
		// filter typeを設定し、それによって返却文字列を変更してみる？
		// filter用
		return trackTitle;//trackArtistId + trackAlbumId;//trackGenre + trackAlbum + trackArtist; 
	}
	
	long trackId;
	String trackTitle;
	String trackData;
	String trackGenre;
	String trackAlbum;
	String trackAlbumId;
	String trackArtist;
	String trackArtistId;
	// ms
	long trackDuration;
	String trackAlbumArt;
	

	/**
	 * @return the trackAlbumArt
	 */
	public String getTrackAlbumArt() {
		return trackAlbumArt;
	}
	/**
	 * @param trackAlbumArt the trackAlbumArt to set
	 */
	public void setTrackAlbumArt(String trackAlbumArt) {
		this.trackAlbumArt = trackAlbumArt;
	}

	// below: playlistmemberのもの？
	long trackPlayOrder;
	long trackAudioId;
	boolean isMusic;
	/**
	 * @return the trackId
	 */
	public long getTrackId() {
		return trackId;
	}
	/**
	 * @param trackId the trackId to set
	 */
	public void setTrackId(long trackId) {
		this.trackId = trackId;
	}
	/**
	 * @return the trackTitle
	 */
	public String getTrackTitle() {
		return trackTitle;
	}
	/**
	 * @param trackTitle the trackTitle to set
	 */
	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}
	/**
	 * @return the trackGenre
	 */
	public String getTrackGenre() {
		return trackGenre;
	}
	/**
	 * @param trackGenre the trackGenre to set
	 */
	public void setTrackGenre(String trackGenre) {
		this.trackGenre = trackGenre;
	}
	/**
	 * @return the trackAlbum
	 */
	public String getTrackAlbum() {
		return trackAlbum;
	}
	/**
	 * @param trackAlbum the trackAlbum to set
	 */
	public void setTrackAlbum(String trackAlbum) {
		this.trackAlbum = trackAlbum;
	}
	/**
	 * @return the trackArtist
	 */
	public String getTrackArtist() {
		return trackArtist;
	}
	/**
	 * @param trackArtist the trackArtist to set
	 */
	public void setTrackArtist(String trackArtist) {
		this.trackArtist = trackArtist;
	}
	/**
	 * @return the trackArtistId
	 */
	public String getTrackArtistId() {
		return trackArtistId;
	}
	/**
	 * @param trackArtistId the trackArtistId to set
	 */
	public void setTrackArtistId(String trackArtistId) {
		this.trackArtistId = trackArtistId;
	}
	/**
	 * @return the trackDuration
	 */
	public long getTrackDuration() {
		return trackDuration;
	}
	/**
	 * @param trackDuration the trackDuration to set
	 */
	public void setTrackDuration(long trackDuration) {
		this.trackDuration = trackDuration;
	}
	/**
	 * @return the trackPlayOrder
	 */
	public long getTrackPlayOrder() {
		return trackPlayOrder;
	}
	/**
	 * @param trackPlayOrder the trackPlayOrder to set
	 */
	public void setTrackPlayOrder(long trackPlayOrder) {
		this.trackPlayOrder = trackPlayOrder;
	}
	/**
	 * @return the trackAudioId
	 */
	public long getTrackAudioId() {
		return trackAudioId;
	}
	/**
	 * @param trackAudioId the trackAudioId to set
	 */
	public void setTrackAudioId(long trackAudioId) {
		this.trackAudioId = trackAudioId;
	}
	/**
	 * @return the isMusic
	 */
	public boolean isMusic() {
		return isMusic;
	}
	/**
	 * @param isMusic the isMusic to set
	 */
	public void setMusic(boolean isMusic) {
		this.isMusic = isMusic;
	}
	/**
	 * @return the trackAlbumId
	 */
	public String getTrackAlbumId() {
		return trackAlbumId;
	}
	/**
	 * @param trackAlbumId the trackAlbumId to set
	 */
	public void setTrackAlbumId(String trackAlbumId) {
		this.trackAlbumId = trackAlbumId;
	}
	/**
	 * @return the trackData
	 */
	public String getTrackData() {
		return trackData;
	}
	/**
	 * @param trackData the trackData to set
	 */
	public void setTrackData(String trackData) {
		this.trackData = trackData;
	}
	
}
