package okosama.app.action;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
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
public final class ControllerShowHideAction implements IViewAction {

	// long seekVal_ms;
	public ControllerShowHideAction() {
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
		int iTabId = act.getTabStocker().getCurrentTabId();
		int iCurrentTabPageId = act.getCurrentTabPageId();
		if( null != act.getTabStocker().getTab(iTabId) )
		{
			TabPage page = (TabPage) act.getTabStocker().getTab(iTabId).getChild(iCurrentTabPageId);
			page.setCtrlPanelShowFlg(!(page.getCtrlPanelShowFlg()));
			page.updateControlPanel();
		}
		
		
		return 0;
	}

}
