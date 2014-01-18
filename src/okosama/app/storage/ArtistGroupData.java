package okosama.app.storage;

import java.io.Serializable;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;

import android.provider.MediaStore;

public class ArtistGroupData implements Serializable, ISimpleData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1551599762543511030L;
	long groupId;
	long artistId;
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
	public long getDataId() {
		return artistId;
	}
	/**
	 * @param artistId the artistId to set
	 */
	public void setDataId(long artistId) {
		this.artistId = artistId;
	}
	/**
	 * @return the artistName
	 */
	public String getName() {
		return artistName;
	}
	/**
	 * @param artistName the artistName to set
	 */
	public void setName(String artistName) {
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

	@Override
	public String toString()
	{
		String name = this.artistName;//getArtistName();
		String displayname = name;
		boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING);
		if (unknown) {
			displayname = OkosamaMediaPlayerActivity.getResourceAccessor().getString(R.string.unknown_artist_name);		
		}
		return displayname;//this.artistName;
	}
	
}
