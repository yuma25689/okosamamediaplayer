package okosama.app.state;

import okosama.app.AppStatus;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.view.Menu;
import android.view.MenuItem;

public class DisplayStatePlay extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		tab.setCurrentTab(TabPage.TABPAGE_ID_PLAY, true);

		// �T�u��ʂ�����̂ŁA2��ԋp����
		return 2;
	}
	@Override
	public int registerReceivers(int status) {
		// ���̃^�u�́A�ΏۊO
		return 2;
	}
	@Override
	public long updateDisplay() {
		// TODO Auto-generated method stub
		return AppStatus.NO_REFRESH;
	}
	@Override
	public int onCreateOptionsMenu(Menu menu)
	{
		return MENU_PLAY_STATE;		
	}
	@Override
	public int onPrepareOptionsMenu(Menu menu)
	{
		return MENU_PLAY_STATE;		
	}
	@Override
	public int onOptionsItemSelected(MenuItem menu)
	{
		return MENU_PLAY_STATE;
	}
	
}
