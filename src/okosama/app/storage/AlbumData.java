package okosama.app.storage;

import java.io.Serializable;

public class AlbumData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8832540249308517413L;
	int albumId;
	String albumName;
	String albumArtist;
	String albumArt;
	/**
	 * @return the albumId
	 */
	public int getAlbumId() {
		return albumId;
	}
	/**
	 * @param albumId the albumId to set
	 */
	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	/**
	 * @return the albumName
	 */
	public String getAlbumName() {
		return albumName;
	}
	/**
	 * @param albumName the albumName to set
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	/**
	 * @return the albumArtist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}
	/**
	 * @param albumArtist the albumArtist to set
	 */
	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
	/**
	 * @return the albumArt
	 */
	public String getAlbumArt() {
		return albumArt;
	}
	/**
	 * @param albumArt the albumArt to set
	 */
	public void setAlbumArt(String albumArt) {
		this.albumArt = albumArt;
	}
	
}
