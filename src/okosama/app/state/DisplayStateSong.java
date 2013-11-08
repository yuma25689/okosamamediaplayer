package okosama.app.state;

import android.view.MenuItem;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateSong extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// ã»ëIëâÊñ Ç÷ÇÃêÿÇËë÷Ç¶
		tab.setCurrentTab(TabPage.TABPAGE_ID_SONG, true);		
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_SONG );
//		action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(ControlIDs.TAB_ID_MEDIA, false);
		return ret;
	}
	@Override
	public int onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		case MENU_UPDATE:
			// AlbumÇçƒìxí[ññÇ©ÇÁì«Ç›íºÇ∑
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(
				TabPage.TABPAGE_ID_SONG
			);
			break;
		}
		return 0;
	}
	@Override
	public int updateStatus() {
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().updateStatus();
		return 0;
	}
	
//	@Override
//	public int registerReceivers(int status) {
//		// TODO Auto-generated method stub
//		
//		return 0;
//	}
}
