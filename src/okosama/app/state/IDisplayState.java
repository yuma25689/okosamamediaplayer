package okosama.app.state;

import okosama.app.tab.Tab;

public interface IDisplayState {

	// Bundle mBundle;
	
	/**
	 * この画面状態に基づいて画面を切り替える
	 * @return 0:正常 マイナス:異常 画面ID:サブ画面あり（返るのは親画面のID）
	 */
	public int ChangeDisplayBasedOnThisState(Tab tab);
	
	public static String LSNER_NAME_TRACK = "TRACK_LSN";
	public static String LSNER_NAME_SCAN = "SCAN_LSN";
	public static String HDLER_NAME_RESCAN = "RESCAN_HDL";
	
	
	public static int STATUS_ON_CREATE = 1;
	public static int STATUS_ON_RESUME = 2;
	public static int STATUS_ON_DESTROY = 3;
	public static int STATUS_ON_PAUSE = 4;
	/**
	 * この画面状態に基づいて、BloadcastReceiverを登録する
	 * @return 登録結果 0:登録OK 1:登録対象ではない -1:登録失敗
	 */
	public int registerReceivers(int status);
	
	/**
	 * この画面状態の(?)BloadcastReceiverを解除する
	 */
	public void unregisterReceivers(int status);
	
	/**
	 * 現在の状態で、表示を更新すべきときにコールする
	 * @return 次の更新へのカウントダウン(ms) NO_REFRESHならば、継続的には更新しない
	 */
	public long updateDisplay();
	
}
