/**
 * 
 */
package okosama.app;


/**
 * アプリケーションの状態を保持するクラス
 * アプリケーション全体から参照可能にする
 * 読み書きは、プリファレンスから行うようにする
 * @author 25689
 *
 */
public final class AppStatus {
	// リフレッシュ用メッセージID
    public static final int REFRESH = 1001;
    public static final int NO_REFRESH = -10;
    public static final int DEFAULT_REFRESH_MS = 500;

    public static final int RESTART = 999;
    
	public void clearSrchCondition()
	{
		artistID = null;
		albumID = null;
		genre = null;
		playlistID = null;
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
	private String playlistID = null;

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
	public String getPlaylistID() {
		return playlistID;
	}

	/**
	 * @param playlistName the playlistName to set
	 */
	public void setPlaylistID(String playlistID) {
		this.playlistID = playlistID;
	}
	
}
