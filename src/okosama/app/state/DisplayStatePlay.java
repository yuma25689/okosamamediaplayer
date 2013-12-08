package okosama.app.state;

import android.view.Menu;
import android.view.MenuItem;
import okosama.app.AppStatus;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlay extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		tab.setCurrentTab(TabPage.TABPAGE_ID_PLAY, true);

		// サブ画面があるので、2を返却する
		return 2;
	}
	@Override
	public int registerReceivers(int status) {
		// このタブは、対象外
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
