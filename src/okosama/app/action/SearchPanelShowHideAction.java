package okosama.app.action;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.SearchPanel;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.TabPage;
// import android.R;
import android.os.RemoteException;
// import android.view.View;

/**
 * メディアを指定時間シークするアクション
 * @author 25689
 *
 */
public final class SearchPanelShowHideAction implements IViewAction {

	// long seekVal_ms;
	public SearchPanelShowHideAction() {
		super();
		// this.seekVal_ms = val;
	}

	/**
	 * @throws  
	 * 
	 */
	@Override
	public int doAction( Object param ) {
		
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		TabPage page = (TabPage) act.getCurrentTabPage();
		if( null != page )//act.getTabStocker().getTab(iTabId) )
		{
			if( SearchPanel.getInstance().getView() != null )
			{
				if(SearchPanel.getInstance().getView().getParent() == null 
				|| page.getTabBaseLayout() != SearchPanel.getInstance().getView().getParent()	
				)
				{
					SearchPanel.insertToLayout(page.getTabBaseLayout());
				}
				else
				{
					OkosamaMediaPlayerActivity.removeFromParent(SearchPanel.getInstance().getView());
				}
			}
		}
		
		
		return 0;
	}

}
