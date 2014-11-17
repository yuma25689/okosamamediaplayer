package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.PlayControlPanel;
import okosama.app.tab.TabPage;
// import android.R;
// import android.view.View;

/**
 * ���f�B�A���w�莞�ԃV�[�N����A�N�V����
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
		
		OkosamaMediaPlayerActivity act 
		= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		TabPage page = (TabPage) act.getCurrentTabPage();
		if( null != page )
		{
			if( PlayControlPanel.getInstance().getView() != null )
			{
				if(PlayControlPanel.getInstance().getView().getParent() == null 
				|| page.getTabBaseLayout() != PlayControlPanel.getInstance().getView().getParent()	
				)
				{
					PlayControlPanel.insertToLayout(page.getTabBaseLayout());
					page.setCtrlPanelShowFlg(true);
				}
				else
				{
					OkosamaMediaPlayerActivity.removeFromParent(PlayControlPanel.getInstance().getView());
					page.setCtrlPanelShowFlg(false);					
				}
			}
			// TabPage page = (TabPage) act.getTabStocker().getTab(iTabId).getChild(iCurrentTabPageId);
			// page.updateControlPanel();
		}
		
		
		return 0;
	}

}
