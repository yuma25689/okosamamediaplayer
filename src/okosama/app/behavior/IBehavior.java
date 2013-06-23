package okosama.app.behavior;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public interface IBehavior {
	void onItemClick(int iItemType);
	void onCreateOptionsMenu();
	void onPrepareOptionsMenu();
	void onOptionsItemSelected();
	void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn);
	boolean onContextItemSelected(MenuItem item);
	void doSearch();
}
