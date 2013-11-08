package okosama.app.state;

import android.view.MenuItem;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlaylist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// プレイ選択画面への切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_PLAYLIST, true);		
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_PLAYLIST );
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
			// Albumを再度端末から読み直す
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(
				TabPage.TABPAGE_ID_PLAYLIST
			);
			break;
		}
		return 0;
	}
	
	@Override
	public int updateStatus() {
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getPlaylistAdp().updateStatus();
		return 0;
	}
//	@Override
//	public int registerReceivers(int status) {
//		// TODO Auto-generated method stub
//	
//		return 0;
//	}
}
