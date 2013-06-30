package okosama.app.tab;

import java.util.HashMap;

import android.app.Activity;
import okosama.app.action.HideTabComponentAction;
import okosama.app.action.IViewAction;
import okosama.app.action.NoAction;
import okosama.app.action.ShowTabComponentAction;
import okosama.app.behavior.IBehavior;

public abstract class TabLeaf implements ITabComponent {
	// public static final int TAGKEY_LISTNAME = 1205;

	protected Activity activity;
	
	protected String name;
	protected IBehavior behavior = null;
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}	
	protected HashMap< Integer, IViewAction > actionMap;
	public void addAction( int id, IViewAction action )
	{
		actionMap.put( id, action );
	}
	
	public void setActionMap(HashMap<Integer, IViewAction> actionMap) {
		this.actionMap = actionMap;
	}
	public boolean isActionExists(int id)
	{
		return actionMap.containsKey(id);
	}
	public IViewAction getAction(int id)
	{
		if( actionMap.containsKey(id))
			return actionMap.get( id );
		else
			return new NoAction();
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
