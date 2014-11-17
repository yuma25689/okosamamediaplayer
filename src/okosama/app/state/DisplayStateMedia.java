package okosama.app.state;

import okosama.app.AppStatus;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.view.Menu;
import android.view.MenuItem;

public class DisplayStateMedia extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// ���f�B�A�I����ʂւ̐؂�ւ�
		tab.setCurrentTab(TabPage.TABPAGE_ID_MEDIA, true);
//		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_MEDIA );
//		action.doAction(null);

		// �T�u��ʂ�����̂ŁA1��ԋp����
		return TabPage.TABPAGE_ID_MEDIA;
	}
	@Override
	public int registerReceivers(int status) {
		// ���̃^�u�́A�ΏۊO
		return 1;
	}
	@Override
	public long updateDisplay() {
		// TODO Auto-generated method stub
		return AppStatus.NO_REFRESH;
	}
	@Override
	public int onCreateOptionsMenu(Menu menu)
	{
		return MENU_MEDIA_STATE;		
	}
	@Override
	public int onPrepareOptionsMenu(Menu menu)
	{
		return MENU_MEDIA_STATE;		
	}
	@Override
	public int onOptionsItemSelected(MenuItem menu)
	{
		return MENU_MEDIA_STATE;
	}
	
}
