package okosama.app.behavior;

import okosama.app.service.MediaInfo;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

public interface IBehavior {
	void onItemClick(AdapterView<?> l, View v, int position, long id);
	void onItemClick(ExpandableListView parent, View v, int grouppos, int childpos, long id);
	// -> state�Ɉړ�
//	void onCreateOptionsMenu();
//	void onPrepareOptionsMenu();
//	void onOptionsItemSelected();
	void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn);
	boolean onContextItemSelected(MenuItem item);
	void doSearch();
	MediaInfo[] getCurrentMediaList();	
}
