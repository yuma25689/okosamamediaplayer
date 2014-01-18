package okosama.app.behavior;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;
import okosama.app.action.IViewAction;
import okosama.app.action.RenamePlaylist;
import okosama.app.action.TabSelectAction;
import okosama.app.adapter.TrackListRawAdapter;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.AlbumData;
import okosama.app.storage.Database;
import okosama.app.storage.PlaylistData;
import okosama.app.tab.TabPage;

public class PlaylistListBehavior extends IListBehavior implements Database.Defs {
    private static final int DELETE_PLAYLIST = CHILD_MENU_BASE + 1;
    //private static final int EDIT_PLAYLIST = CHILD_MENU_BASE + 2;
    private static final int RENAME_PLAYLIST = CHILD_MENU_BASE + 3;
    //private static final long RECENTLY_ADDED_PLAYLIST = -1;

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		// TODO: 検索条件を設定後、トラックタブへ移動
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		if( act.getPlaylistAdp() == null
		|| act.getPlaylistAdp().getItem(position) == null )
		{
			return;
		}
		String playlistID = String.valueOf(act.getPlaylistAdp().getItem(position).getDataId());
		Log.d("playlistID set",playlistID);
		OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistID(playlistID);
		act.getTrackAdp().setFilterType(TrackListRawAdapter.FILTER_PLAYLIST);
		act.getTrackAdp().updateList();
		IViewAction action = new TabSelectAction( ControlIDs.TAB_ID_MEDIA,
				TabPage.TABPAGE_ID_SONG );
		action.doAction(v);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		
		menu.add(0, PLAY_SELECTION, 0, R.string.play_selection);

        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfoIn;

        if (mi.id >= 0 /*|| mi.id == PODCASTS_PLAYLIST*/) {
            menu.add(0, DELETE_PLAYLIST, 0, R.string.delete_playlist_menu);
        }

//        if (mi.id == RECENTLY_ADDED_PLAYLIST) {
//            menu.add(0, EDIT_PLAYLIST, 0, R.string.edit_playlist_menu);
//        }

        if (mi.id >= 0) {
            menu.add(0, RENAME_PLAYLIST, 0, R.string.rename_playlist_menu);
        }
//        mPlaylistCursor.moveToPosition(mi.position);
//        menu.setHeaderTitle(mPlaylistCursor.getString(mPlaylistCursor.getColumnIndexOrThrow(
//                MediaStore.Audio.Playlists.NAME)));

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
        PlaylistData data 
        = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getPlaylistAdp().getItem(mi.position);

        switch (item.getItemId()) {
            case PLAY_SELECTION:
            	MediaPlayerUtil.playPlaylist(activity, data.getDataId());
                break;
            case DELETE_PLAYLIST:
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, data.getDataId());
                activity.getContentResolver().delete(uri, null, null);
                Toast.makeText(activity, R.string.playlist_deleted_message, Toast.LENGTH_SHORT).show();
//                if (mPlaylistCursor.getCount() == 0) {
//                    setTitle(R.string.no_playlists_title);
//                }
    			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
    					ControlIDs.TAB_ID_MEDIA,
    					true
    				);
                break;
            case RENAME_PLAYLIST:
                Intent intent = new Intent();
                intent.setClass(activity, RenamePlaylist.class);
                intent.putExtra("rename", data.getDataId());
                activity.startActivityForResult(intent, RENAME_PLAYLIST);
    			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().reScanMediaAndUpdateTabPage(
    					ControlIDs.TAB_ID_MEDIA,
    					true
    				);
                break;
        }
        return true;
	}

	@Override
	public void doSearch() {
		// TODO Auto-generated method stub

	}

	@Override
	public MediaInfo[] getCurrentMediaList() {
        //long [] list = new long[] { mSelectedId };
        return null;//list;
	}
	
}
