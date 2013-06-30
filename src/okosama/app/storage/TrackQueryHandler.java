package okosama.app.storage;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.TabPage;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * �g���b�N�p�̃N�G���[�n���h���H
 * @author 25689
 *
 */
public class TrackQueryHandler extends AsyncQueryHandler {

	// ����
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
     * �N�G�����s�H
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
        	// �񓯊��H
            // Get 100 results first, which is enough to allow the user to start scrolling,
            // while still being very fast.
            Uri limituri = uri.buildUpon().appendQueryParameter("limit", "100").build();
            QueryArgs args = new QueryArgs();
            args.uri = uri;
            args.projection = projection;
            args.selection = selection;
            args.selectionArgs = selectionArgs;
            args.orderBy = orderBy;

            // TODO:cookie,project���Ӗ��������ĂȂ��̂ŕK�����ׂ邱��
            startQuery(TabPage.TABPAGE_ID_SONG, args, limituri, projection, selection, selectionArgs, orderBy);
            return null;
        }
        // �����H
        return Database.query(ctx,
                uri, projection, selection, selectionArgs, orderBy);
    }

    /**
     * �N�G�������H
     */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        //Log.i("@@@", "query complete: " + cursor.getCount() + "   " + mActivity);
        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().initAdapter(TabPage.TABPAGE_ID_SONG, cursor, cookie != null);
        if (token == TabPage.TABPAGE_ID_SONG && cookie != null && cursor != null && cursor.getCount() >= 100) {
        	// ������100���ȏ�̎��H���낤���H
        	// token��100�ɂ��čĔ��s���H
            QueryArgs args = (QueryArgs) cookie;
            startQuery(100, null, args.uri, args.projection, args.selection,
                    args.selectionArgs, args.orderBy);
        }
    }
}
