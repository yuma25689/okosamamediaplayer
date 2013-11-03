package okosama.app.storage;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.adapter.IAdapterUpdate;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

public class QueryHandler extends AsyncQueryHandler {
	
	//Å@OkosamaMediaPlayerActivity activity;
	IAdapterUpdate adapter;
	
    public QueryHandler(ContentResolver res, IAdapterUpdate adapter) {
        super(res);
        this.adapter = adapter;
    }
    
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        //Log.i("@@@", "query complete");
    	// TODO:ï Ç…tokenÇ…ÇµÇ»Ç≠ÇƒÇ‡ÅAÇ±Ç±Ç≈ê›íËÇ∑ÇÍÇŒÇ¢Ç¢ÇÊÇ§Ç»
    	adapter.insertAllDataFromCursor(cursor);
    }
}
