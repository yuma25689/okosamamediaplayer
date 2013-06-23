package okosama.app.state;

import okosama.app.tab.Tab;

public interface IDisplayState {

	// Bundle mBundle;
	
	/**
	 * ‚±‚Ì‰æ–Êó‘Ô‚ÉŠî‚Ã‚¢‚Ä‰æ–Ê‚ğØ‚è‘Ö‚¦‚é
	 * @return 0:³í ƒ}ƒCƒiƒX:ˆÙí ‰æ–ÊID:ƒTƒu‰æ–Ê‚ ‚èi•Ô‚é‚Ì‚Íe‰æ–Ê‚ÌIDj
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
	 * ‚±‚Ì‰æ–Êó‘Ô‚ÉŠî‚Ã‚¢‚ÄABloadcastReceiver‚ğ“o˜^‚·‚é
	 * @return “o˜^Œ‹‰Ê 0:“o˜^OK 1:“o˜^‘ÎÛ‚Å‚Í‚È‚¢ -1:“o˜^¸”s
	 */
	public int registerReceivers(int status);
	
	/**
	 * ‚±‚Ì‰æ–Êó‘Ô‚Ì(?)BloadcastReceiver‚ğ‰ğœ‚·‚é
	 */
	public void unregisterReceivers(int status);
}
