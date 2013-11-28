package okosama.app.state;

import android.util.Log;
import android.view.MenuItem;
import okosama.app.AppStatus;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateArtist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		Log.w("ChangeDisplayBasedOnThisState", "come");
		// アーティスト別選択画面への切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_ARTIST, true);
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_ARTIST );
//		action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  AppStatus.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(ControlIDs.TAB_ID_MEDIA, false);
		return ret;
	}
	@Override
	public int onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		case MENU_UPDATE:
			// Albumを再度端末から読み直す
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
				ControlIDs.TAB_ID_MEDIA,
				true
			);
			return 0;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public int updateStatus() {
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp().updateStatus();
		return 0;
	}
	
}
