package okosama.app.state;

import android.view.MenuItem;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateNowPlaylist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// 曲選択画面への切り替え
		// ここがタブ選択処理の末端？相当変な作りだが・・・
		tab.setCurrentTab(TabPage.TABPAGE_ID_NOW_PLAYLIST, true);
		//IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_NOW_PLAYLIST );
		//action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(ControlIDs.TAB_ID_MAIN, false);
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
				TabPage.TABPAGE_ID_NOW_PLAYLIST
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
	
	
}
