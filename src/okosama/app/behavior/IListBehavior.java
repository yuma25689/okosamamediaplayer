package okosama.app.behavior;

import android.view.View;
import android.widget.ExpandableListView;

/**
 * ���Ӗ��ɂȂ��Ă��܂��� TODO:�������ǂ����l��
 * @author 25689
 *
 */
abstract public class IListBehavior implements IBehavior {
	@Override
	public void onItemClick(ExpandableListView parent, View v, int grouppos, int childpos, long id)
	{
	}

//	void onItemClick(int iItemType);
//	void onCreateOptionsMenu();
//	void onPrepareOptionsMenu();
//	void onOptionsItemSelected();
//	void onCreateContextMenu(int iListItem);
//	void onContextItemSelected(int iListItem);
//	void doSearch();
}
