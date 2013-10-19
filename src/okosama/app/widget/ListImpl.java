package okosama.app.widget;

import okosama.app.behavior.IListBehavior;

import android.content.Context;

import android.widget.ListView;

/**
 * このアプリケーションで利用するリストの実装
 * Bridgeパターンを適用
 * @author 25689
 *
 */
public class ListImpl extends ListView {

	IListBehavior behavior;
	public ListImpl(Context context) {		
		super(context);
	}

	public void setBehavior( IListBehavior behavior_ )
	{
		behavior = behavior_;
	}
    //public void onItemClick(AdapterView<?>  parent, View v, int position, long id)
    //{
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/track");
//        intent.putExtra("album", Long.valueOf(id).toString());
//        intent.putExtra("artist", mArtistId);
//        startActivity(intent);
    //}
}
