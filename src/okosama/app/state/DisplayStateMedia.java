package okosama.app.state;

import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStateMedia extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// ���f�B�A�I����ʂւ̐؂�ւ�
		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_MEDIA );
		action.doAction(null);

		// �T�u��ʂ�����̂ŁA1��ԋp����
		return TabPage.TABPAGE_ID_MEDIA;
	}
	@Override
	public int registerReceivers(int status) {
		// ���̃^�u�́A�ΏۊO
		return 1;
	}
}
