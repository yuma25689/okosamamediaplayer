package okosama.app.state;

import okosama.app.AppStatus;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.SearchPanelShowHideAction;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.view.Menu;
import android.view.MenuItem;

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
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
				ControlIDs.TAB_ID_MAIN, false);
		return ret;
	}
	@Override
	public int onPrepareOptionsMenu(Menu menu)
	{
		int iRet = super.onPrepareOptionsMenu(menu);
		MenuItem item = null;
		item = menu.add(Menu.NONE, MENU_SEARCH, Menu.NONE, R.string.search_title);
		item.setIcon(android.R.drawable.ic_menu_search );
		
		return iRet;
	}
	
	@Override
	public int onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		case MENU_UPDATE:
			// NowPlaylist���ēx�[������ǂݒ���
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
				ControlIDs.TAB_ID_MAIN,
				true
			);
			return 0;
		case MENU_SEARCH:
			SearchPanelShowHideAction action = new SearchPanelShowHideAction();
			action.doAction(null);
			return 0;			
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public int updateStatus() {
		if( MediaPlayerUtil.isNowPlayingVideos() )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoAdp().updateStatus();
		}
		else
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().updateStatus();
		}
		return 0;
	}
	
	
}
