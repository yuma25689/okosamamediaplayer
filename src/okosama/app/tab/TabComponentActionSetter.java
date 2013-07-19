/**
 * 
 */
package okosama.app.tab;

import java.util.HashMap;

import android.util.SparseArray;

import okosama.app.action.IViewAction;

/**
 * @author 25689
 *
 */
public class TabComponentActionSetter implements ITabComponentConfigurator {

	public TabComponentActionSetter(SparseArray< IViewAction > actionMap)
	{
		this.actionMap = actionMap;
	}
	protected SparseArray< IViewAction > actionMap;

	public SparseArray< IViewAction > getActionMap() {
		return actionMap;
	}

	/* (non-Javadoc)
	 * @see okosama.app.tab.ITabComponentConfigurator#configure(okosama.app.tab.ITabComponent)
	 */
	@Override
	public int configure(ITabComponent component) {
		component.setActionMap( this.actionMap );
		component.configureAction();
		return 0;
	}

}
