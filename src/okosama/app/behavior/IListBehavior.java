package okosama.app.behavior;

import android.view.View;
import android.widget.ExpandableListView;

/**
 * 無意味になってしまった TODO:消すかどうか考慮
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
