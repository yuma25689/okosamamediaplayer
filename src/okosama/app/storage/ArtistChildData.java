package okosama.app.storage;

public class ArtistChildData {
	long childId;
	String albumId;
	/**
	 * @return the albumId
	 */
	public String getAlbumId() {
		return albumId;
	}
	/**
	 * @param albumId the albumId to set
	 */
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	String artistId;
	String artistName;
	String albumName;
	int numOfSongs;
	int numOfSongsForArtist;
	String albumArt;
	/**
	 * @return the childId
	 */
	public long getChildId() {
		return childId;
	}
	/**
	 * @param childId the childId to set
	 */
	public void setChildId(long childId) {
		this.childId = childId;
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
	 * @return the numOfSongs
	 */
	public int getNumOfSongs() {
		return numOfSongs;
	}
	/**
	 * @param numOfSongs the numOfSongs to set
	 */
	public void setNumOfSongs(int numOfSongs) {
		this.numOfSongs = numOfSongs;
	}
	/**
	 * @return the numOfSongsForArtist
	 */
	public int getNumOfSongsForArtist() {
		return numOfSongsForArtist;
	}
	/**
	 * @param numOfSongsForArtist the numOfSongsForArtist to set
	 */
	public void setNumOfSongsForArtist(int numOfSongsForArtist) {
		this.numOfSongsForArtist = numOfSongsForArtist;
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
