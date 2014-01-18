package okosama.app.storage;

import java.io.Serializable;

public class PlaylistData implements Serializable,ISimpleData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6339969296530048661L;
	long playlistId;
	String playlistName;
	String playlistCount;
	/**
	 * @return the playlistCount
	 */
	public String getPlaylistCount() {
		return playlistCount;
	}
	/**
	 * @param playlistCount the playlistCount to set
	 */
	public void setPlaylistCount(String playlistCount) {
		this.playlistCount = playlistCount;
	}
	/**
	 * @return the playlistId
	 */
	public long getDataId() {
		return playlistId;
	}
	/**
	 * @param playlistId the playlistId to set
	 */
	public void setDataId(long playlistId) {
		this.playlistId = playlistId;
	}
	/**
	 * @return the playlistName
	 */
	public String getName() {
		return playlistName;
	}
	/**
	 * @param playlistName the playlistName to set
	 */
	public void setName(String playlistName) {
		this.playlistName = playlistName;
	}
	@Override
	public String toString()
	{
		return this.playlistName;
	}
	
}
