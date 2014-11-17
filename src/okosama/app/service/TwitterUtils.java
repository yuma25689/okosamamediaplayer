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
     * OAuth�C���X�^���X���擾���܂��B
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
        //Twitetr4j�̐ݒ��ǂݍ���
        //Configuration conf = ConfigurationContext.getInstance();
 
        //Oauth�F�؃I�u�W�F�N�g�쐬
        _oauth = new OAuthAuthorization(conf.build());
        //Oauth�F�؃I�u�W�F�N�g��consumerKey��consumerSecret��ݒ�
        _oauth.setOAuthConsumer(consumerKey, consumerSecret);
        _oauth.setOAuthAccessToken(null);
        return _oauth;
    }

    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(TOKEN, accessToken.getToken());
        editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }


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


    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }
}
