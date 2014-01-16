package okosama.app.behavior;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import okosama.app.DeleteItems;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.TrackData;
import okosama.app.tab.TabPage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TrackListBehavior extends IListBehavior implements Database.Defs {

    private static final int REMOVE = CHILD_MENU_BASE + 5;
    private static final int SEARCH = CHILD_MENU_BASE + 6;

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		
		if (act.getAdapter(TabPage.TABPAGE_ID_SONG).getMainItemCount() == 0) {
            return;
        }
		MediaInfo[] list = act.getTrackAdp().getCurrentAllMediaInfo();		
        MediaPlayerUtil.playAll(
        		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
        		list, position, false);
	}

    private int mSelectedPosition;
    private long mSelectedId;
    private String mCurrentTrackName;
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		// TODO Auto-generated method stub
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		menu.add(0, PLAY_SELECTION, 0, R.string.play_selection);
		SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
		Database.makePlaylistMenu(activity, sub);
		// menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
		menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
		menu.add(0, SHOW_ITEM_INFORMATION, 0, R.string.show_item_information);
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfoIn;
		mSelectedPosition =  mi.position;
		// Cursor cursor = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).getCursor(Database.SongCursorName);
		// cursor.moveToPosition(mSelectedPosition);
		TrackData data = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().getItem(mSelectedPosition);
		//try {
		    //int id_idx = cursor.getColumnIndexOrThrow(
		    //        MediaStore.Audio.Playlists.Members.AUDIO_ID);
		    mSelectedId = data.getTrackAudioId();//cursor.getLong(id_idx);
//		} catch (IllegalArgumentException ex) {
//		    mSelectedId = mi.id;
//		}
		// only add the 'search' menu if the selected item is music
		if (data.isMusic()) {// MediaPlayerUtil.isMusic(cursor)) {
		    menu.add(0, SEARCH, 0, R.string.search_title);
		}
//		mCurrentAlbumName = cursor.getString(cursor.getColumnIndexOrThrow(
//		        MediaStore.Audio.Media.ALBUM));
//		mCurrentArtistNameForAlbum = cursor.getString(cursor.getColumnIndexOrThrow(
//		        MediaStore.Audio.Media.ARTIST));
		mCurrentTrackName = data.getTrackTitle(); //cursor.getString(cursor.getColumnIndexOrThrow(
		        //MediaColumns.TITLE));
		menu.setHeaderTitle(mCurrentTrackName);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// Cursor cursor = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).getCursor(Database.SongCursorName);
		MediaInfo[] listMedia = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().getCurrentAllMediaInfo();
		switch (item.getItemId()) {
		case PLAY_SELECTION: {
			// play the track
			int position = mSelectedPosition;
			MediaPlayerUtil.playAll(activity, listMedia, position);
			return true;
		}
		case SHOW_ITEM_INFORMATION:
		{
			TrackData data = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().getItem(mSelectedPosition);
			//Log.d("track_filename",data.getTrackData());
			File file = new File(data.getTrackData());
			AudioFile f;
			try {
				f = AudioFileIO.read(file);
				Tag tag = f.getTag();
				int cnt = tag.getFieldCount();
			} catch (CannotReadException e) {
				Log.e("file_info_get_error",e.getMessage());
				//e.printStackTrace();
			} catch (IOException e) {
				Log.e("file_info_get_error",e.getMessage());
				//e.printStackTrace();
			} catch (TagException e) {
				Log.e("file_info_get_error",e.getMessage());
				//e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				Log.e("file_info_get_error",e.getMessage());				
				//e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				Log.e("file_info_get_error",e.getMessage());				
				// e.printStackTrace();
			}
			// AudioHeader = f.getAudioHeader();			
		}
		break;
	   case QUEUE: {
		   MediaInfo [] list = new MediaInfo[] { 
				   new MediaInfo(mSelectedId,MediaInfo.MEDIA_TYPE_AUDIO) };
	       MediaPlayerUtil.addToCurrentPlaylist(activity, list);
	       return true;
	   }
	
	   // TODO: Œã‚ÅŽÀ‘•
//	   case NEW_PLAYLIST: {
//	       Intent intent = new Intent();
//	       intent.setClass(this, CreatePlaylist.class);
//	       startActivityForResult(intent, NEW_PLAYLIST);
//	       return true;
//	   }
	
	   case PLAYLIST_SELECTED: {
		   MediaInfo [] list = new MediaInfo[] { new MediaInfo(mSelectedId,MediaInfo.MEDIA_TYPE_AUDIO) };
	       long playlist = item.getIntent().getLongExtra("playlist", 0);
	       Database.addToPlaylist(activity, list, playlist);
	       return true;
	   }
	
	   // TODO:Œã‚ÅŽÀ‘•
//	   case USE_AS_RINGTONE:
//	       // Set the system setting to make this the current ringtone
//	       Database.setRingtone(activity, mSelectedId);
//	       return true;
	
	   case DELETE_ITEM: {
	       long [] list = new long[1];
	       list[0] = (int) mSelectedId;
	       int[] listType = new int[] { MediaInfo.MEDIA_TYPE_AUDIO };		
	       Bundle b = new Bundle();
	       String f = activity.getString(R.string.delete_song_desc); 
	       String desc = String.format(f, mCurrentTrackName);
	       b.putString(DeleteItems.TITLE_KEY, desc);
	       b.putLongArray(DeleteItems.ITEMID_KEY, list);
	       b.putIntArray(DeleteItems.TYPEID_KEY, listType);
	       Intent intent = new Intent();
	       intent.setClass(activity, okosama.app.DeleteItems.class);
	       intent.putExtras(b);
	       activity.startActivityForResult(intent, DeleteItems.DELETE_REQUEST_CODE);
	       return true;
	   }
	
	   // TODO: Œã‚ÅŽÀ‘•
//	   case REMOVE:
//	       removePlaylistItem(mSelectedPosition);
//	       return true;
//	       
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

	@Override
	public MediaInfo[] getCurrentMediaList() {
		MediaInfo [] list = new MediaInfo[] { new MediaInfo(mSelectedId,MediaInfo.MEDIA_TYPE_AUDIO) };
        return list;
	}

}
