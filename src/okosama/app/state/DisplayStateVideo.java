package okosama.app.state;

import android.view.MenuItem;
import okosama.app.AppStatus;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateVideo extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		tab.setCurrentTab(TabPage.TABPAGE_ID_VIDEO, true);		
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  AppStatus.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
				ControlIDs.TAB_ID_MEDIA, false);
		return ret;
	}
	@Override
	public int onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		case MENU_UPDATE:
			// �ēx�[������ǂݒ���
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
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoAdp().updateStatus();
		return 0;
	}
	
}
