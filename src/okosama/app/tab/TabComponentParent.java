package okosama.app.tab;

import java.util.ArrayList;
import java.util.HashMap;
// import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.behavior.IBehavior;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * タブ上の項目の親となることができるクラスの抽象クラス
 * @author 25689
 *
 */
public abstract class TabComponentParent implements ITabComponent {

	Activity activity;	
	public TabComponentParent()
	{
	}
	public TabComponentParent( Activity activity )
	{
		this.activity = activity;		
	}
	protected String name;
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	protected boolean active = false;
	public boolean isActive() {
		return active;
	}
	public void setActiveFlg( boolean b ) {
		active = b;
	}

	ViewGroup tabBaseLayout;
	
	// 子項目のリスト
	// 本当は最初にaddする時に作った方が無駄がないはずだが、こちらの方が安全ではある
	protected HashMap<Integer,ITabComponent> children 
		= new HashMap<Integer,ITabComponent>();
	
	// ここにコンテナを持つのは、作りが雑かもしれない。
	// 結果的にこのクラスはタブとタブページにしか継承されていないが、
	// 子でこれを使わないクラスもいつかできるのかも
	// 設計としてはよくない
	protected LinearLayout pageContainer;
	protected ViewGroup componentContainer;

	protected Tab parent;
	protected SparseArray< IViewAction > actionMap;
	public void setActionMap(SparseArray< IViewAction > actionMap) {
		this.actionMap = actionMap;
	}
	
	/**
	 * Viewの取得
	 * @return view
	 */
	public View getView() {
		return null;
	}
	/**
	 * Activeかどうかを設定。子の同関数もコールする
	 * @param b
	 */	
	public void setActivate( boolean b )
	{
		active = b;
        for( ITabComponent c : children.values() ) {
        	c.setActivate( b );
        }
	}
	/**
	 * Visibleかどうかを設定。子の同関数もコールする
	 * @param b
	 */	
	public void setVisible( boolean b )
	{
        for( ITabComponent c : children.values() ) {
        	c.setVisible( b );
        }	
	}
	/**
	 * 表示の更新。子の同関数もコールする
	 */
	public void updateDisplay() {
        for( ITabComponent c : children.values() ) {
        	c.updateDisplay();
        }
	}

	/**
	 * 子項目の追加
	 * @param child
	 */
	public void addChild( int tabId, ITabComponent child ) {
		children.put(tabId,child);
	}

	/**
	 * 外部オブジェクトに設定を委譲する
	 * 子のオブジェクトには適用しない
	 * @param conf
	 */
	public void acceptConfigurator( ITabComponentConfigurator conf )
	{
		conf.configure(this);
	}
	/**
	 * @param pageContainer the pageContainer to set
	 */
	public void setPageContainer(LinearLayout pageContainer) {
		this.pageContainer = pageContainer;
	}	
	public void setComponentContainer(RelativeLayout componentContainer) {
		this.componentContainer = componentContainer;
	}
	/**
	 * 設定されたアクションの内容を、コンポーネントに設定する
	 */
	public void configureAction()
	{
	}
	
	public IBehavior getBehavior()
	{
		return null;
	}
	/**
	 * 子項目のクリア
	 */
//	public void clearChild() {
//		children.clear();
//	}	
}
