package okosama.app.tab;

import okosama.app.action.IViewAction;
import okosama.app.behavior.IBehavior;
import android.util.SparseArray;
import android.view.View;

/**
 * 
 * @author 25689
 *
 */
public interface ITabComponent {

	/**
	 * 自らの作成
	 * @return 0:正常 0以外:異常
	 */
	public int create(int panelLayoutId);

	/**
	 * 名前の設定
	 * @param name
	 */
	public void setName(String name);
	/**
	 * 名前の取得
	 * @return
	 */
	public String getName();
	/**
	 * Viewの取得
	 * @return view
	 */
	public View getView();
	
	/**
	 * Activeかどうかを設定
	 * @param b
	 */
	public void setActivate( boolean b );

	/**
	 * Visibleかどうかを設定
	 * @param b
	 */
	public void setVisible( boolean b );

	/**
	 * 表示の更新
	 */
	public void updateDisplay();
 
	/**
	 * 子項目の追加
	 * @param child
	 */
	public void addChild( int ID, ITabComponent child );

	/**
	 * 外部オブジェクトに設定を委譲する
	 * @param conf
	 */
	public void acceptConfigurator( ITabComponentConfigurator conf );
	
	/**
	 * 設定されたActionMapの内容を、コンポーネントに設定する
	 */
	public void configureAction();
	
	/**
	 * ActionMapを内部に設定する
	 * @param actionMap
	 */
	public void setActionMap(SparseArray< IViewAction > actionMap);
	
	public IBehavior getBehavior();
	/**
	 * 子項目のクリア
	 */
	// public void clearChild();
}
