package okosama.app.state;

import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateArtist extends absDisplayStateMediaTab {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// �A�[�e�B�X�g�ʑI����ʂւ̐؂�ւ�
		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_ARTIST );
		action.doAction(null);
		return 0;
	}
//	@Override
//	public int registerReceivers(int status) {
//		// TODO Auto-generated method stub
//		
//		
//		return 0;
//	}
}
