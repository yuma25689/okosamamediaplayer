package okosama.app.tab;

// import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.behavior.IBehavior;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
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
	protected abstract int create( int panelLayoutId );
	public TabComponentParent()
	{
	}
	public TabComponentParent( Activity activity )
	{
		this.activity = activity;		
	}
	protected Integer internalID;
	public void setInternalID(Integer internalID)
	{
		this.internalID = internalID;
	}
	public Integer getInternalID()
	{
		return this.internalID;
	}
	protected boolean active = false;
	public boolean isActive() {
		return active;
	}
	public void setActiveFlg( boolean b ) {
		active = b;
	}

	protected ViewGroup tabBaseLayout;

	protected void resetPanelViews(int iPanelLayoutId)
	{
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		LayoutInflater inflator = act.getLayoutInflater();
		tabBaseLayout = (ViewGroup)inflator.inflate(iPanelLayoutId, null, false);	
		tabBaseLayout.removeAllViews();
	}

	// 子項目のリスト
	// 本当は最初にaddする時に作った方が無駄がないはずだが、こちらの方が安全ではある
	protected SparseArray<ITabComponent> children 
		= new SparseArray<ITabComponent>();
	
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
        for( int i=0; i < children.size(); i++ ) {
        	children.valueAt(i).setActivate( b );
        }
	}
	/**
	 * Visibleかどうかを設定。子の同関数もコールする
	 * @param b
	 */	
	public void setVisible( boolean b )
	{
		for( int i=0; i < children.size(); i++ ) {
			children.valueAt(i).setVisible( b );
        }	
	}
	/**
	 * 表示の更新。子の同関数もコールする
	 */
	public void updateDisplay() {
		for( int i=0; i < children.size(); i++ ) {
			children.valueAt(i).updateDisplay();
        }
	}

	/**
	 * 子項目の追加
	 * @param child
	 */
	public void addChild( int ID, ITabComponent child ) {
		children.put(ID,child);
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
