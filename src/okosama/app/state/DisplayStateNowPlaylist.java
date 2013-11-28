package okosama.app.state;

import android.view.MenuItem;
import okosama.app.AppStatus;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateNowPlaylist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// �ȑI����ʂւ̐؂�ւ�
		// �������^�u�I�������̖��[�H�����ςȍ�肾���E�E�E
		tab.setCurrentTab(TabPage.TABPAGE_ID_NOW_PLAYLIST, true);
		//IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_NOW_PLAYLIST );
		//action.doAction(null);
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  AppStatus.NO_REFRESH;
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(ControlIDs.TAB_ID_PLAY, false);
		return ret;
	}
	@Override
	public int onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		case MENU_UPDATE:
			// NowPlaylist���ēx�[������ǂݒ���
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
				ControlIDs.TAB_ID_PLAY,
				true
			);
			return 0;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public int updateStatus() {
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().updateStatus();
		return 0;
	}
	
	
}
