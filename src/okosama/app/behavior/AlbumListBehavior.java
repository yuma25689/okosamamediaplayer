package okosama.app.behavior;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.tab.TabPage;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AlbumListBehavior extends IListBehavior implements Database.Defs {

	public static final int SEARCH = CHILD_MENU_BASE;
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// TODO: 検索条件を設定後、トラックタブへ移動
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/track");
//        intent.putExtra("album", Long.valueOf(id).toString());
//        intent.putExtra("artist", mArtistId);
//        startActivity(intent);
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		if( act.getAlbumAdp() == null
		|| act.getAlbumAdp().getItem(position) == null )
		{
			return;
		}
		OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setAlbumID(String.valueOf(act.getAlbumAdp().getItem(position).getAlbumId() ) );
		// act.getTrackAdp().setAlbumId(String.valueOf(act.getAlbumAdp().getItem(position).getAlbumId() ) );
		act.getTrackAdp().updateList();
		//Long.valueOf(id).toString());
		// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setArtistID(
		IViewAction action = new TabSelectAction( ControlIDs.TAB_ID_MEDIA,
				TabPage.TABPAGE_ID_SONG );
		action.doAction(v);
	}

	@Override
	public void onCreateOptionsMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepareOptionsMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOptionsItemSelected() {
		// TODO Auto-generated method stub

	}
	
    private String mCurrentAlbumId;
    private String mCurrentAlbumName;
    private String mCurrentArtistNameForAlbum;
    boolean mIsUnknownArtist;
    boolean mIsUnknownAlbum;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		// TODO Auto-generated method stub
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
        menu.add(0, PLAY_SELECTION, 0, R.string.play_selection);
        SubMenu sub = menu.addSubMenu(0, Database.Defs.ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        Database.makePlaylistMenu(activity, sub);
//        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);

        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfoIn;
        Cursor albumCursor = Database.getInstance(activity).getCursor(Database.AlbumCursorName);
        albumCursor.moveToPosition(mi.position);
        mCurrentAlbumId = albumCursor.getString(albumCursor.getColumnIndexOrThrow(BaseColumns._ID));
        mCurrentAlbumName = albumCursor.getString(albumCursor.getColumnIndexOrThrow(AlbumColumns.ALBUM));
        mCurrentArtistNameForAlbum = albumCursor.getString(
        		albumCursor.getColumnIndexOrThrow(AlbumColumns.ARTIST));
        mIsUnknownArtist = mCurrentArtistNameForAlbum == null ||
                mCurrentArtistNameForAlbum.equals(MediaStore.UNKNOWN_STRING);
        mIsUnknownAlbum = mCurrentAlbumName == null ||
                mCurrentAlbumName.equals(MediaStore.UNKNOWN_STRING);
        if (mIsUnknownAlbum) {
            menu.setHeaderTitle(activity.getString(R.string.unknown_album_name));
        } else {
            menu.setHeaderTitle(mCurrentAlbumName);
        }
        if (!mIsUnknownAlbum || !mIsUnknownArtist) {
        	// TODO 定数SEARCHを作っても良いが、サンプルでそうなっていない。なぜか調べる
            menu.add(0, SEARCH, 0, R.string.search_title);
        }
	}

	// TODO: とりあえず、新しいプレイリストの作成と項目の削除はコメントにしてある
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case PLAY_SELECTION: {
            // play the selected album
            long [] list = Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
            MediaPlayerUtil.playAll(activity, list, 0);
            return true;
        }

        case QUEUE: {
            long [] list = Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
            MediaPlayerUtil.addToCurrentPlaylist(activity, list);
            return true;
        }

//        case NEW_PLAYLIST: {
//            Intent intent = new Intent();
//            intent.setClass(this, CreatePlaylist.class);
//            startActivityForResult(intent, NEW_PLAYLIST);
//            return true;
//        }

        case PLAYLIST_SELECTED: {
            long [] list = Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
            long playlist = item.getIntent().getLongExtra("playlist", 0);
            Database.addToPlaylist(activity, list, playlist);
            return true;
        }
//        case DELETE_ITEM: {
//            long [] list = Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
//            String f = activity.getString(R.string.delete_album_desc); 
//            String desc = String.format(f, mCurrentAlbumName);
//            Bundle b = new Bundle();
//            b.putString("description", desc);
//            b.putLongArray("items", list);
//            Intent intent = new Intent();
//            intent.setClass(this, DeleteItems.class);
//            intent.putExtras(b);
//            startActivityForResult(intent, -1);
//            return true;
//      }
        case SEARCH: {
            doSearch();
            return true;
        }
        }
        return true;
	}

	@Override
	public void doSearch() {
		// TODO Auto-generated method stub

	}

}
