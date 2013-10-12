package okosama.app.behavior;

import android.content.Intent;
import android.database.Cursor;
//import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.adapter.ArtistAlbumListAdapter;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.tab.TabPage;

public class ArtistListBehavior extends IExpListBehavior implements Database.Defs {

	public static final int SEARCH = CHILD_MENU_BASE;

	public void onItemClick(ExpandableListView parent, View v, int grouppos, int childpos, long id)
	{
		// これがchildclickとして使われる
		mCurrentAlbumId = Long.valueOf(id).toString();
		
		Cursor c = (Cursor) parent.getExpandableListAdapter().getChild(grouppos, childpos);
		String album = c.getString(c.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
		if (album == null || album.equals(MediaStore.UNKNOWN_STRING)) {
		    // unknown album, so we should include the artist ID to limit the songs to songs only by that artist
			Cursor cursor = Database.getInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()).getCursor(Database.ArtistCursorName);
			cursor.moveToPosition(grouppos);
			mCurrentArtistId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
			OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setArtistID(mCurrentArtistId);
		}
		// OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setAlbumID(mCurrentAlbumId);
		// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setArtistID(
		IViewAction action = new TabSelectAction( 
				ControlIDs.TAB_ID_MEDIA, //act.getMediaTab().getTabContent(),
				TabPage.TABPAGE_ID_SONG );
		action.doAction(v);		
	}	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
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
    private String mCurrentArtistId;
    private String mCurrentArtistName;
    private String mCurrentAlbumId;
    private String mCurrentAlbumName;
    private String mCurrentArtistNameForAlbum;
    boolean mIsUnknownArtist;
    boolean mIsUnknownAlbum;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// TODO: ここにExpandableListViewが入っているかどうかは美容。要確認。
		ExpandableListView v = (ExpandableListView) view;
        Cursor artistCursor = Database.getInstance(activity).getCursor(Database.ArtistCursorName);
        if( artistCursor == null )
        {
        	Log.w("onCreateContextMenu - Artist","cursor is null");
        	return;
        }
		ArtistAlbumListAdapter adapter = activity.getArtistAdp();
		if( adapter == null )
		{
        	Log.w("onCreateContextMenu - Artist","adapter is null");
			return;
		}
        
		// 選択したものを再生
        menu.add(0, PLAY_SELECTION, 0, R.string.play_selection);
        // プレイリストに追加
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        Database.makePlaylistMenu(activity, sub);
        // 削除
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        
        // 選択された項目の位置を取得
        ExpandableListContextMenuInfo mi = (ExpandableListContextMenuInfo) menuInfoIn;
        
        int itemtype = ExpandableListView.getPackedPositionType(mi.packedPosition);
        int gpos = ExpandableListView.getPackedPositionGroup(mi.packedPosition);
        int cpos = ExpandableListView.getPackedPositionChild(mi.packedPosition);
        if (itemtype == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
        	// グループが選択された
            if (gpos == -1) {
                // this shouldn't happen
                Log.d("Artist/Album", "no group");
                return;
            }
            
            gpos = gpos - v.getHeaderViewsCount();
            artistCursor.moveToPosition(gpos);
            mCurrentArtistId = artistCursor.getString(artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
            mCurrentArtistName = artistCursor.getString(artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
            mCurrentAlbumId = null;
            mIsUnknownArtist = mCurrentArtistName == null ||
                    mCurrentArtistName.equals(MediaStore.UNKNOWN_STRING);
            mIsUnknownAlbum = true;
            if (mIsUnknownArtist) {
                menu.setHeaderTitle(activity.getString(R.string.unknown_artist_name));
            } else {
                menu.setHeaderTitle(mCurrentArtistName);
                menu.add(0, SEARCH, 0, R.string.search_title);
            }
            return;
        } else if (itemtype == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            if (cpos == -1) {
                // this shouldn't happen
                Log.d("Artist/Album", "no child");
                return;
            }
            Cursor c = (Cursor) adapter.getChild(gpos, cpos);
            c.moveToPosition(cpos);
            mCurrentArtistId = null;
            mCurrentAlbumId = Long.valueOf(mi.id).toString();
            mCurrentAlbumName = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
            gpos = gpos - v.getHeaderViewsCount();
            artistCursor.moveToPosition(gpos);
            mCurrentArtistNameForAlbum = artistCursor.getString(
            		artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
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
                menu.add(0, SEARCH, 0, R.string.search_title);
            }
        }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
        switch (item.getItemId()) {
	        case PLAY_SELECTION: {
	            // play everything by the selected artist
	            long [] list =
	                mCurrentArtistId != null ?
	                Database.getSongListForArtist(activity, Long.parseLong(mCurrentArtistId))
	                : Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
	                    
	            MediaPlayerUtil.playAll(activity, list, 0);
	            return true;
	        }
	
	        case QUEUE: {
	            long [] list =
	                mCurrentArtistId != null ?
	                Database.getSongListForArtist(activity, Long.parseLong(mCurrentArtistId))
	                : Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
	            MediaPlayerUtil.addToCurrentPlaylist(activity, list);
	            return true;
	        }
	
	        case NEW_PLAYLIST: {
	        	// TODO: 実装
//	            Intent intent = new Intent();
//	            intent.setClass(this, CreatePlaylist.class);
//	            startActivityForResult(intent, NEW_PLAYLIST);
	            return true;
	        }
	
	        case PLAYLIST_SELECTED: {
	            long [] list =
	                mCurrentArtistId != null ?
	                Database.getSongListForArtist(activity, Long.parseLong(mCurrentArtistId))
	                : Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
	            long playlist = item.getIntent().getLongExtra("playlist", 0);
	            Database.addToPlaylist(activity, list, playlist);
	            return true;
	        }
	        
	        case DELETE_ITEM: {
	            long [] list;
	            String desc;
	            if (mCurrentArtistId != null) {
	                list = Database.getSongListForArtist(activity, Long.parseLong(mCurrentArtistId));
	                String f = activity.getString(R.string.delete_artist_desc);
	                desc = String.format(f, mCurrentArtistName);
	            } else {
	                list = Database.getSongListForAlbum(activity, Long.parseLong(mCurrentAlbumId));
	                String f = activity.getString(R.string.delete_album_desc); 
	                desc = String.format(f, mCurrentAlbumName);
	            }
	            Bundle b = new Bundle();
	            b.putString("description", desc);
	            b.putLongArray("items", list);
	            Intent intent = new Intent();
	            intent.setClass(activity, okosama.app.DeleteItems.class);
	            intent.putExtras(b);
	            // TODO: 受け取れるようにできていない
	            activity.startActivityForResult(intent, -1);
	            return true;
	        }
	        
	        case SEARCH:
	            doSearch();
	            return true;
	    }
		return true;
	}

	@Override
	public void doSearch() {
		// TODO Auto-generated method stub

	}

}
