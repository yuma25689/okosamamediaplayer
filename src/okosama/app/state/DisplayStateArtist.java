package okosama.app.state;

import okosama.app.AppStatus;
import okosama.app.ControlIDs;
import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.SearchPanelShowHideAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.view.Menu;
import android.view.MenuItem;

public class DisplayStateArtist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		LogWrapper.w("ChangeDisplayBasedOnThisState", "come");
		// �A�[�e�B�X�g�ʑI����ʂւ̐؂�ւ�
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
			// Artist���ēx�[������ǂݒ���
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
				ControlIDs.TAB_ID_MEDIA,
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
		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp().updateStatus();
		return 0;
	}
	
}
