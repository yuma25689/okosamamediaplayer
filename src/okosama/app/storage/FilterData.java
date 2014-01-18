package okosama.app.storage;

public class FilterData {
	// ï∂éöóÒÇÊÇËÅAIDÇóDêÊÇ∑ÇÈ
	String artistId;
	String albumId;
	String GenreId;

	String strSong;
	String strArtist;
	String strAlbum;
	String strPlaylist;
	String strVideo;
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
	 * @return the albumId
	 */
	public String getAlbumId() {
		return albumId;
	}
	/**
	 * @param i the albumId to set
	 */
	public void setAlbumId(String i) {
		this.albumId = i;
	}
	/**
	 * @return the genreId
	 */
	public String getGenreId() {
		return GenreId;
	}
	/**
	 * @param Long.l the genreId to set
	 */
	public void setGenreId(long l) {
		GenreId = String.valueOf(l);
	}
	/**
	 * @return the strSong
	 */
	public String getStrSong() {
		return strSong;
	}
	/**
	 * @param strSong the strSong to set
	 */
	public void setStrSong(String strSong) {
		this.strSong = strSong;
	}
	/**
	 * @return the strArtist
	 */
	public String getStrArtist() {
		return strArtist;
	}
	/**
	 * @param strArtist the strArtist to set
	 */
	public void setStrArtist(String strArtist) {
		this.strArtist = strArtist;
	}
	/**
	 * @return the strAlbum
	 */
	public String getStrAlbum() {
		return strAlbum;
	}
	/**
	 * @param strAlbum the strAlbum to set
	 */
	public void setStrAlbum(String strAlbum) {
		this.strAlbum = strAlbum;
	}
	/**
	 * @return the strPlaylist
	 */
	public String getStrPlaylist() {
		return strPlaylist;
	}
	/**
	 * @param strPlaylist the strPlaylist to set
	 */
	public void setStrPlaylist(String strPlaylist) {
		this.strPlaylist = strPlaylist;
	}
	/**
	 * @return the strVideo
	 */
	public String getStrVideo() {
		return strVideo;
	}
	/**
	 * @param strVideo the strVideo to set
	 */
	public void setStrVideo(String strVideo) {
		this.strVideo = strVideo;
	}
	

}
