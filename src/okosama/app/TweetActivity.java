package okosama.app;

import okosama.app.service.MediaPlayerUtil;
import okosama.app.service.TwitterUtils;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TweetActivity extends Activity {

    private EditText mInputText;
    private Twitter mTwitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_tweet);
	    // twitterの設定
	    mTwitter = TwitterUtils.getTwitterInstance(this);
        //mCallbackURL = getString(R.string.twitter_callback_url);
 
		mInputText = (EditText) findViewById(R.id.input_text);
		String nowplayingtag = getString(R.string.nowplaying_tag);
		String artist, album, song;
		try {
	        artist = MediaPlayerUtil.sService.getArtistName();
	        album = MediaPlayerUtil.sService.getAlbumName();
	        song = MediaPlayerUtil.sService.getTrackName();
		} catch( RemoteException e ) {
			artist = getString(R.string.unknown_artist_name);
			album = getString(R.string.unknown_album_name);
			song = getString(R.string.unknown_song_name);
		}
		if( artist == null )
			artist = getString(R.string.unknown_artist_name);
		if( album == null )
			album = getString(R.string.unknown_album_name);
		if( song == null )
			song = getString(R.string.unknown_song_name);
		
		String crlf = System.getProperty("line.separator");
		String tweetText = getString(R.string.artist_label) + artist + crlf +
				getString(R.string.album_label) + album + crlf +
				getString(R.string.song_label) + song + crlf +
				nowplayingtag;
		mInputText.setText(tweetText);
		findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        tweet();
		    }
		});
	}
    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                	//RateLimitStatus sts = e.getRateLimitStatus();
                	//sts.get
                    //e.printStackTrace();
                	Log.e("tweet失敗", e.getErrorCode() + " " + e.getMessage() + " " 
                    + e.getExceptionCode() + " " + e.getStatusCode() + e.getLocalizedMessage() + e.toString() );
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    showToast("ツイートが完了しました！");
                    finish();
                } else {
                    showToast("ツイートに失敗しました。。。");
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
