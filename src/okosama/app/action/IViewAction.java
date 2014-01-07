package okosama.app.action;

/**
 * アクションクラスのインタフェース
 * このクラス階層は、割とシステムへの依存が強くなってしまったかも
 * @author 25689
 *
 */
public interface IViewAction {

	public static final int ACTION_ID_NONE = 0;
	public static final int ACTION_ID_ONCLICK = 1;
	public static final int ACTION_ID_ONTOGGLEON = 2;
	public static final int ACTION_ID_ONTOGGLEOFF = 3;
	public static final int ACTION_ID_ONCLICKSEEK = 4;
	public static final int ACTION_ID_ONFLICKUP = 5;
	public static final int ACTION_ID_ONFLICKDOWN = 6;
	public static final int ACTION_ID_ONFLICKLEFT = 7;
	public static final int ACTION_ID_ONFLICKRIGHT = 8;
	
	/**
	 * アクションを実行する
	 * @return エラーコード 0:正常 0以外:異常
	 */
	int doAction(Object param);
}
