package okosama.app.storage;

import java.io.Serializable;

public class PlaylistData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6339969296530048661L;
	long playlistId;
	String playlistName;
	/**
	 * @return the playlistId
	 */
	public long getPlaylistId() {
		return playlistId;
	}
	/**
	 * @param playlistId the playlistId to set
	 */
	public void setPlaylistId(long playlistId) {
		this.playlistId = playlistId;
	}
	/**
	 * @return the playlistName
	 */
	public String getPlaylistName() {
		return playlistName;
	}
	/**
	 * @param playlistName the playlistName to set
	 */
	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}
	
}
