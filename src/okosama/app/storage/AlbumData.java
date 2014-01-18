package okosama.app.storage;

import java.io.Serializable;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.provider.MediaStore;

public class AlbumData implements Serializable, ISimpleData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8832540249308517413L;
	long albumId;
	String albumName;
	String albumArtist;
	String albumArt;
	/**
	 * @return the albumId
	 */
	public long getDataId() {
		return albumId;
	}
	/**
	 * @param albumId the albumId to set
	 */
	public void setDataId(long albumId) {
		this.albumId = albumId;
	}
	/**
	 * @return the albumName
	 */
	public String getName() {
		return albumName;
	}
	/**
	 * @param albumName the albumName to set
	 */
	public void setName(String albumName) {
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

	
	@Override
	public String toString()
	{
		String name = this.albumName;//getArtistName();
		String displayname = name;
		boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING);
		if (unknown) {
			displayname = OkosamaMediaPlayerActivity.getResourceAccessor().getString(R.string.unknown_album_name);		
		}
		
		return displayname;//this.artistName;
		
		//return this.albumName;
	}
}
