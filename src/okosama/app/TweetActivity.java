package okosama.app;

import okosama.app.service.MediaPlayer;
import okosama.app.service.TwitterUtils;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TweetActivity extends Activity {

	public static String mCallbackURL;
    public static RequestToken mRequestToken;
    private EditText mInputText;
    private Twitter mTwitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_tweet);
	    // twitterの設定
	    mTwitter = TwitterUtils.getTwitterInstance(this);
        mCallbackURL = getString(R.string.twitter_callback_url);
 
		mInputText = (EditText) findViewById(R.id.input_text);
		String nowplayingtag = getString(R.string.nowplaying_tag);
		String artist, album, song;
		try {
	        artist = MediaPlayer.sService.getArtistName();
	        album = MediaPlayer.sService.getAlbumName();
	        song = MediaPlayer.sService.getTrackName();
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
		if( false == TwitterUtils.hasAccessToken(this) )
		{
			startAuthorize();
		}
	}
    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
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
    // twitter認証
    /**
     * OAuth認証（厳密には認可）を開始します。
     * 
     * @param listener
     */
    public void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	// TODO: これで前のアクティビティは消える？要確認
                    startActivity(intent);
                } else {
                    // 失敗。。。
                }
            }
        };
        task.execute();
    }
    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功！
                    showToast("認証成功！");
                    successOAuth(accessToken);
                } else {
                    // 認証失敗。。。
                    showToast("認証失敗。。。");
                }
            }
        };
        task.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(this, accessToken);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
