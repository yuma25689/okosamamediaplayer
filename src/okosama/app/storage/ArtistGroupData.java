package okosama.app.storage;

public class ArtistGroupData {
	long groupId;
	String artistId;
	String artistName;
	int numOfAlbums;
	int numOfTracks;
	/**
	 * @return the groupId
	 */
	public long getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the artistId
	 */
	public String getArtistId() {
		return artistId;
	}
	/**
	 * @param artistId the artistId to set
	 */
	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}
	/**
	 * @return the artistName
	 */
	public String getArtistName() {
		return artistName;
	}
	/**
	 * @param artistName the artistName to set
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	/**
	 * @return the numOfAlbums
	 */
	public int getNumOfAlbums() {
		return numOfAlbums;
	}
	/**
	 * @param numOfAlbums the numOfAlbums to set
	 */
	public void setNumOfAlbums(int numOfAlbums) {
		this.numOfAlbums = numOfAlbums;
	}
	/**
	 * @return the numOfTracks
	 */
	public int getNumOfTracks() {
		return numOfTracks;
	}
	/**
	 * @param numOfTracks the numOfTracks to set
	 */
	public void setNumOfTracks(int numOfTracks) {
		this.numOfTracks = numOfTracks;
	}
	
}
