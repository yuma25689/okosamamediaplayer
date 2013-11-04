package okosama.app;

import okosama.app.adapter.AlbumListRawAdapter;
import okosama.app.adapter.ArtistAlbumListRawAdapter;
import okosama.app.adapter.PlaylistListRawAdapter;
import okosama.app.adapter.TrackListRawAdapter;

public class TempBackupData {
	private AlbumListRawAdapter albumAdp;
	private ArtistAlbumListRawAdapter artistAdp;
    private PlaylistListRawAdapter playlistAdp;
    private TrackListRawAdapter tracklistAdp;
	/**
	 * @return the albumAdp
	 */
	public AlbumListRawAdapter getAlbumAdp() {
		return albumAdp;
	}
	/**
	 * @param albumAdp the albumAdp to set
	 */
	public void setAlbumAdp(AlbumListRawAdapter albumAdp) {
		this.albumAdp = albumAdp;
	}
	/**
	 * @return the artistAdp
	 */
	public ArtistAlbumListRawAdapter getArtistAdp() {
		return artistAdp;
	}
	/**
	 * @param artistAdp the artistAdp to set
	 */
	public void setArtistAdp(ArtistAlbumListRawAdapter artistAdp) {
		this.artistAdp = artistAdp;
	}
	/**
	 * @return the playlistAdp
	 */
	public PlaylistListRawAdapter getPlaylistAdp() {
		return playlistAdp;
	}
	/**
	 * @param playlistAdp the playlistAdp to set
	 */
	public void setPlaylistAdp(PlaylistListRawAdapter playlistAdp) {
		this.playlistAdp = playlistAdp;
	}
	/**
	 * @return the tracklistAdp
	 */
	public TrackListRawAdapter getTracklistAdp() {
		return tracklistAdp;
	}
	/**
	 * @param tracklistAdp the tracklistAdp to set
	 */
	public void setTracklistAdp(TrackListRawAdapter tracklistAdp) {
		this.tracklistAdp = tracklistAdp;
	}
}
