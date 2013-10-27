/**
 * 
 */
package okosama.app;

//import okosama.app.factory.DroidWidgetKit;
//import okosama.app.widget.Button;

/**
 * アプリケーションの状態を保持するクラス
 * アプリケーション全体から参照可能にする
 * 読み書きは、プリファレンスから行うようにする
 * @author 25689
 *
 */
public final class AppStatus {
	
	public void clearSrchCondition()
	{
		artistID = null;
		albumID = null;
		genre = null;
		playlistName = null;
	}
	public void clearModeFlag()
	{
		shuffleMode = SHUFFLE_NONE;
		// repeatMode = REPEAT_NONE;
	}
	
	// ----------------- 再生用の値
	// シャッフルモード
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    
    // リピートモード
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    
	private static int shuffleMode = SHUFFLE_NONE;
	// private static int repeatMode = REPEAT_NONE;
    
	
	// -------------------- 検索用の値
	// アーティストID
	private String artistID = null;
	
	// アルバムID
	private String albumID = null;
	
	// ジャンル
	private String genre = null;
	
	// プレイリスト名？
	private String playlistName = null;

	/**
	 * @return the shuffleMode
	 */
	public static int getShuffleMode() {
		return shuffleMode;
	}

	/**
	 * @param shuffleMode the shuffleMode to set
	 */
	public static void setShuffleMode(int shuffleMode) {
		AppStatus.shuffleMode = shuffleMode;
	}

	/**
	 * @return the artistID
	 */
	public String getArtistID() {
		return artistID;
	}

	/**
	 * @param artistID the artistID to set
	 */
	public void setArtistID(String artistID) {
		this.artistID = artistID;
	}

	/**
	 * @return the albumID
	 */
	public String getAlbumID() {
		return albumID;
	}

	/**
	 * @param albumID the albumID to set
	 */
	public void setAlbumID(String albumID) {
		this.albumID = albumID;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
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
