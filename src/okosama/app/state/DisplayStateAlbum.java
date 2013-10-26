package okosama.app.state;



import android.view.Menu;
import android.view.MenuItem;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateAlbum extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// �A���o���ʑI����ʂւ̐؂�ւ�
		tab.setCurrentTab(TabPage.TABPAGE_ID_ALBUM, true);

//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_ALBUM );
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
	public int onCreateOptionsMenu(Menu menu)
	{
		MenuItem item = menu.add(Menu.NONE, MENU_UPDATE, Menu.NONE, R.string.update_menu);
		// TODO: �A�C�R�����X�V�� ic_menu_refresh���{���͂���͂��H
		item.setIcon(android.R.drawable.ic_popup_sync );
		
		return 0;
	}
//	public int onPrepareOptionsMenu(Menu menu);
	@Override
	public int onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		case MENU_UPDATE:
			// Album���ēx�[������ǂݒ���
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMedia(
				TabPage.TABPAGE_ID_ALBUM
			);
			break;
		}
		return 0;
	}

}
