package okosama.app.tab;

import android.app.Activity;
import android.util.SparseArray;
import okosama.app.action.HideTabComponentAction;
import okosama.app.action.IViewAction;
import okosama.app.action.NoAction;
import okosama.app.action.ShowTabComponentAction;
import okosama.app.behavior.IBehavior;

public abstract class TabLeaf implements ITabComponent {
	// public static final int TAGKEY_LISTNAME = 1205;

	protected Activity activity;
	
	protected Integer internalID;
	protected IBehavior behavior = null;
//	protected HashMap<Integer,ITabComponent> children 
//	= new HashMap<Integer,ITabComponent>();
	protected SparseArray<ITabComponent> children
	= new SparseArray<ITabComponent>();
	/**
	 * 子項目の追加
	 * @param child
	 */
	public void addChild( int ID, ITabComponent child ) {
		children.put(ID,child);
	}	
	public void setInternalID(Integer internalID)
	{
		this.internalID = internalID;
	}
	public Integer getInternalID()
	{
		return this.internalID;
	}	
	protected SparseArray< IViewAction > actionMap;
	public void addAction( int id, IViewAction action )
	{
		actionMap.put( id, action );
	}
	
	public void setActionMap(SparseArray< IViewAction > actionMap) {
		this.actionMap = actionMap;
	}
	public boolean isActionExists(int id)
	{
		if( null == actionMap.get(id, null) )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	public IViewAction getAction(int id)
	{
		if( null == actionMap.get(id, null) )
		{
			return new NoAction();
		}
		else
		{
			return actionMap.get( id );
		}		
	}

	public TabLeaf( Activity activity )
	{
		this.activity = activity;
	}
	
	/**
	 * Activeかどうかを設定
	 * @param b
	 */
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			// アクティブ化された時
			ShowTabComponentAction.getInstance().doAction( getView() );			
		}
		else
		{
			// アクティブではなくなった時
			HideTabComponentAction.getInstance().doAction( getView() );
		}
	}
	/**
	 * Visibleかどうかを設定
	 * @param b
	 */
	@Override
	public void setVisible( boolean b )
	{
		if( b )
		{
			// 表示
			ShowTabComponentAction.getInstance().doAction( getView() );			
		}
		else
		{
			// 非表示
			HideTabComponentAction.getInstance().doAction( getView() );
		}
	}
	public void updateDisplay() {
	}

	public void addChild( ITabComponent child) {
	}

	/**
	 * 設定されたアクションの内容を、コンポーネントに設定する
	 */
	public void configureAction()
	{
	}
	public void setBehavior( IBehavior behavior )
	{
		this.behavior = behavior;
	}
	public IBehavior getBehavior()
	{
		return behavior;
	}	
}
