package okosama.app.storage;

import java.io.Serializable;

public class VideoData implements Serializable {
	
	/**
	 * @return the videoId
	 */
	public long getVideoId() {
		return videoId;
	}
	/**
	 * @param videoId the videoId to set
	 */
	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}
	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8254196509016146397L;


	@Override
	public String toString()
	{
		// filter typeを設定し、それによって返却文字列を変更してみる？
		// filter用
		return title;//videoId + title;//trackGenre + trackAlbum + trackArtist; 
	}
	
	long videoId;
	String title;
	String artist;
	String type;
	long duration;//ms


	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

}
