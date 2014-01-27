package okosama.app.service;

import okosama.app.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class TwitterUtils {
    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "token_secret";
    private static final String PREF_NAME = "twitter_access_token";

    /**
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     * 
     * @param context
     * @return
     */
    public static Twitter getTwitterInstance(Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        Log.d("comsumerKey",consumerKey);
        Log.d("consumerSecret",consumerSecret);
        ConfigurationBuilder conf = new ConfigurationBuilder().setUseSSL(true);        
        TwitterFactory factory = new TwitterFactory(conf.build());
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        else
        {
        	twitter.setOAuthAccessToken(null);	
        }
        return twitter;
    }
    /**
     * OAuthインスタンスを取得します。
     * 
     * @param context
     * @return
     */
    public static OAuthAuthorization getOAuthInstance(Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        Log.d("comsumerKey",consumerKey);
        Log.d("consumerSecret",consumerSecret);
        ConfigurationBuilder conf = new ConfigurationBuilder().setUseSSL(true);
        OAuthAuthorization _oauth = null;
        //Twitetr4jの設定を読み込む
        //Configuration conf = ConfigurationContext.getInstance();
 
        //Oauth認証オブジェクト作成
        _oauth = new OAuthAuthorization(conf.build());
        //Oauth認証オブジェクトにconsumerKeyとconsumerSecretを設定
        _oauth.setOAuthConsumer(consumerKey, consumerSecret);
        _oauth.setOAuthAccessToken(null);
        return _oauth;
    }

    /**
     * アクセストークンをプリファレンスに保存します。
     * 
     * @param context
     * @param accessToken
     */
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(TOKEN, accessToken.getToken());
        editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    /**
     * アクセストークンをプリファレンスから読み込みます。
     * 
     * @param context
     * @return
     */
    public static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN, null);
        String tokenSecret = preferences.getString(TOKEN_SECRET, null);
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    /**
     * アクセストークンが存在する場合はtrueを返します。
     * 
     * @return
     */
    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }
}
