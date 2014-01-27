package okosama.app;

import okosama.app.service.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TwitterAuthActivity extends Activity {
    //private Twitter mTwitter;
	private OAuthAuthorization mOAuth;
	public static String mCallbackURL;
    public static RequestToken mRequestToken;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.tweetAuthTitle);
        setContentView(R.layout.twitter_auth);
        mOAuth = TwitterUtils.getOAuthInstance(this);
        mCallbackURL = getString(R.string.twitter_callback_url);
//    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//    	StrictMode.setThreadPolicy(policy);                	
                
        findViewById(R.id.action_start_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthorize();
            }
        });
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
                	mOAuth.setOAuthAccessToken(null);
                    mRequestToken = mOAuth.getOAuthRequestToken(mCallbackURL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                	Log.e("auth fail",e.getMessage() + " " + e.getErrorCode() + " " + e.getErrorMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
            	Log.i("TweetAuth - onPostExe","url=" + url);
                if (url != null) {
                	// なぜかsslの設定なのにhttp:で取得できるので、https:に変換する・・・(TT)
                	if( url.startsWith("http:") )
                	{
                		url = url.replaceFirst("http", "https");
                    	Log.i("TweetAuth - onPostExe","url(converted)=" + url);
                	}
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
                    return mOAuth.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功！
                	Toast.makeText(TwitterAuthActivity.this, R.string.auth_success, Toast.LENGTH_SHORT).show();
                    successOAuth(accessToken);
                } else {
                    // 認証失敗。。。
                	Toast.makeText(TwitterAuthActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, OkosamaMediaPlayerActivity.class);
        startActivity(intent);        
        finish();
    }
    
}
