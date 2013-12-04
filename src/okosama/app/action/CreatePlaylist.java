package okosama.app.action;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.IBehavior;
import okosama.app.service.MediaInfo;
import okosama.app.storage.Database;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;

public class CreatePlaylist extends Activity
{
    private EditText mPlaylist;
    private TextView mPrompt;
    private Button mSaveButton;
    private String defaultname;
 
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_playlist);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.WRAP_CONTENT);
        // インテントを取得
        Intent intent = getIntent();
        // インテントに保存されたデータを取得
        defaultname = intent.getStringExtra("defaultname");
        
        mPrompt = (TextView)findViewById(R.id.prompt);
        mPlaylist = (EditText)findViewById(R.id.playlist);
        mPlaylist.setVisibility(View.VISIBLE);        
        mSaveButton = (Button) findViewById(R.id.create);
        mSaveButton.setOnClickListener(mOpenClicked);

        ((Button)findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        
        // String defaultname = icicle != null ? icicle.getString("defaultname") : makePlaylistName();
        if (defaultname == null) {
            finish();
            return;
        }
        String promptformat = getString(R.string.create_playlist_create_text_prompt);
        String prompt = String.format(promptformat, defaultname);
        mPrompt.setText(prompt);
        mPlaylist.setText(defaultname);
        mPlaylist.setSelection(defaultname.length());
        mPlaylist.addTextChangedListener(mTextWatcher);
    }
    
    TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // don't care about this one
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newText = mPlaylist.getText().toString();
            if (newText.trim().length() == 0) {
                mSaveButton.setEnabled(false);
            } else {
                mSaveButton.setEnabled(true);
                // check if playlist with current name exists already, and warn the user if so.
                if (idForplaylist(newText) >= 0) {
                    mSaveButton.setText(R.string.create_playlist_overwrite_text);
                } else {
                    mSaveButton.setText(R.string.create_playlist_create_text);
                }
            }
        };
        public void afterTextChanged(Editable s) {
            // don't care about this one
        }
    };
    
    private int idForplaylist(String name) {
        Cursor c = Database.query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Playlists._ID },
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[] { name },
                MediaStore.Audio.Playlists.NAME);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
            }
            c.close();
        }
        return id;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outcicle) {
        outcicle.putString("defaultname", mPlaylist.getText().toString());
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    private String makePlaylistName() {

        String template = getString(R.string.new_playlist_name_template);
        if( defaultname != null && 0 < defaultname.length() )
        {
        	template = defaultname;
        }
        int num = 1;

        String[] cols = new String[] {
                MediaStore.Audio.Playlists.NAME
        };
        ContentResolver resolver = getContentResolver();
        String whereclause = MediaStore.Audio.Playlists.NAME + " != ''";
        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
            cols, whereclause, null,
            MediaStore.Audio.Playlists.NAME);

        if (c == null) {
            return null;
        }
        
        String suggestedname;
        suggestedname = String.format(template, num++);
        
        // Need to loop until we've made 1 full pass through without finding a match.
        // Looping more than once shouldn't happen very often, but will happen if
        // you have playlists named "New Playlist 1"/10/2/3/4/5/6/7/8/9, where
        // making only one pass would result in "New Playlist 10" being erroneously
        // picked for the new name.
        boolean done = false;
        while (!done) {
            done = true;
            c.moveToFirst();
            while (! c.isAfterLast()) {
                String playlistname = c.getString(0);
                if (playlistname.compareToIgnoreCase(suggestedname) == 0) {
                    suggestedname = String.format(template, num++);
                    done = false;
                }
                c.moveToNext();
            }
        }
        c.close();
        return suggestedname;
    }
    
    private View.OnClickListener mOpenClicked = new View.OnClickListener() {
        public void onClick(View v) {
            String name = mPlaylist.getText().toString();
            if (name != null && name.length() > 0) {
                ContentResolver resolver = getContentResolver();
                int id = idForplaylist(name);
                Uri uri;
                if (id >= 0) {
                    uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
                    Database.clearPlaylist(CreatePlaylist.this, id);
                } else {
                    ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Audio.Playlists.NAME, name);
                    uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                }
                // setResult(RESULT_OK, (new Intent()).setData(uri));
	            OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

        		if (uri != null) {
                	Log.d("■new playlist","tabid=" + act.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA));
                	absWidget lst = act.getListFromTabID(act.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA));
                	if( lst != null )
                	{
                		IBehavior behavior = lst.getBehavior();
                		if( behavior != null )
                		{
	                		MediaInfo [] list = behavior.getCurrentMediaList(); //Database.getSongListForAlbum(this, Long.parseLong(mCurrentAlbumId));
	                		Database.addToPlaylist(act, list, Long.parseLong(uri.getLastPathSegment()));
                		}
                    	else
                    	{
                    		Log.e("new playlist","behavior null");
                    	}
                	}
                	else
                	{
                		Log.e("new playlist","list null");
                	}
                }
                
                finish();
            }
        }
    };
}
