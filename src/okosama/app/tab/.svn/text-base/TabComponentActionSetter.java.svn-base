/**
 * 
 */
package okosama.app.tab;

import java.util.HashMap;

import okosama.app.action.IViewAction;

/**
 * @author 25689
 *
 */
public class TabComponentActionSetter implements ITabComponentConfigurator {

	public TabComponentActionSetter(HashMap< Integer, IViewAction > actionMap)
	{
		this.actionMap = actionMap;
	}
	protected HashMap< Integer, IViewAction > actionMap;

	public HashMap<Integer, IViewAction> getActionMap() {
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
