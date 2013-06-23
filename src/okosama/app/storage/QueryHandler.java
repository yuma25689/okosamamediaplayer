package okosama.app.storage;

import okosama.app.OkosamaMediaPlayerActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

public class QueryHandler extends AsyncQueryHandler {
	
	OkosamaMediaPlayerActivity activity;
	
    public QueryHandler(ContentResolver res, OkosamaMediaPlayerActivity act) {
        super(res);
        activity = act;
    }
    
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        //Log.i("@@@", "query complete");
    	// TODO:別にtokenにしなくても、ここで設定すればいいような
    	activity.initAdapter(token,cursor);
    }
}
