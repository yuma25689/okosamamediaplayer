package okosama.app.storage;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.TabPage;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * トラック用のクエリーハンドラ？
 * @author 25689
 *
 */
public class TrackQueryHandler extends AsyncQueryHandler {

	// 引数
    class QueryArgs {
        public Uri uri;
        public String [] projection;
        public String selection;
        public String [] selectionArgs;
        public String orderBy;
    }

    public TrackQueryHandler(ContentResolver res) {
        super(res);
        // ctx = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
    }
    
    /**
     * クエリ実行？
     * @param ctx
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param orderBy
     * @param async
     * @return
     */
    public Cursor doQuery(OkosamaMediaPlayerActivity ctx, Uri uri, String[] projection,
            String selection, String[] selectionArgs,
            String orderBy, boolean async) {
        if (async) {
        	// 非同期？
            // Get 100 results first, which is enough to allow the user to start scrolling,
            // while still being very fast.
            Uri limituri = uri.buildUpon().appendQueryParameter("limit", "100").build();
            QueryArgs args = new QueryArgs();
            args.uri = uri;
            args.projection = projection;
            args.selection = selection;
            args.selectionArgs = selectionArgs;
            args.orderBy = orderBy;

            // TODO:cookie,project等意味分かってないので必ず調べること
            startQuery(TabPage.TABPAGE_ID_SONG, args, limituri, projection, selection, selectionArgs, orderBy);
            return null;
        }
        // 同期？
        return Database.query(ctx,
                uri, projection, selection, selectionArgs, orderBy);
    }

    /**
     * クエリ完了？
     */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        //Log.i("@@@", "query complete: " + cursor.getCount() + "   " + mActivity);
        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().initAdapter(TabPage.TABPAGE_ID_SONG, cursor, cookie != null);
        if (token == TabPage.TABPAGE_ID_SONG && cookie != null && cursor != null && cursor.getCount() >= 100) {
        	// 件数が100件以上の時？だろうか？
        	// tokenを100にして再発行か？
            QueryArgs args = (QueryArgs) cookie;
            startQuery(100, null, args.uri, args.projection, args.selection,
                    args.selectionArgs, args.orderBy);
        }
    }
}
