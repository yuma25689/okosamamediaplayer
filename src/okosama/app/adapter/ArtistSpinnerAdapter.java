package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.ArtistGroupData;
// import okosama.app.storage.QueryHandler;
// import android.content.AsyncQueryHandler;
import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ArtistListのアダプタ
 * @author 25689
 *
 */
public class ArtistSpinnerAdapter extends ArrayAdapter<ArtistGroupData> { 
//implements IAdapterUpdate, SectionIndexer {
    
	private LayoutInflater inflater;
	// private ArrayList<ArtistGroupData> items;
	private int iLayoutId;
	private ArrayList<ArtistGroupData> items = new ArrayList<ArtistGroupData>();
	private ArrayList<String> unknownArtistIds = new ArrayList<String>();
    // private OkosamaMediaPlayerActivity mActivity;
	private final String mUnknownArtist;

    // Viewのホルダ？
    static class ViewHolder {
        TextView line1;
    }

    /**
     * アダプタのコンストラクタ
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public ArtistSpinnerAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<ArtistGroupData> items) {
        super(currentactivity, layout, items );

        this.iLayoutId = layout;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUnknownArtist = currentactivity.getString(R.string.unknown_album_name);
        
        // アクティビティの設定
        // クエリハンドラの作成
        // mActivity = currentactivity;
        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);
        setItems(items);

    }

    /**
     * 新しいビューの作成？
     */
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
    	View v = convertView;  
    	if (v == null) {
    	   ViewHolder vh = new ViewHolder();
	       v = inflater.inflate(iLayoutId, null); 
	       vh.line1 = (TextView) v.findViewById(R.id.line1);
	       v.setTag(vh);
    	}
	    bindView(v,pos);
    	return v;
    }

    /**
     * ビューとデータを紐つける
     */
    //@Override
    public void bindView(View view, int pos) {
        
       	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();
        // positionからデータを取得
    	ArtistGroupData data = getItem(pos);
    	
    	if( data == null )
    	{
    		// データがないというのは、完全におかしい状態だが・・
    		 vh.line1.setText("");
    		 return;
    	}
 
        // アルバム名を取得、ビューに設定
        String name = data.getArtistName();
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownArtist;
        }
        vh.line1.setText(displayname);
        
    }
    
    /**
     * データの変更？
     */
    

	/**
	 * @return the items
	 */
	public ArrayList<ArtistGroupData> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<ArtistGroupData> items) {
		unknownArtistIds.clear();
		ArrayList<Integer> arrRemove = new ArrayList<Integer>();
        int index = 0;
		for( ArtistGroupData data : items )
		{
	        String name = data.getArtistName();
	        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
			if( unknown )
			{
		        unknownArtistIds.add(data.getArtistId());
		        if( 1 < unknownArtistIds.size())
		        {
		        	arrRemove.add(index);
		        	//this.items.remove(index);
		        }
			}
			index++;
		}
		for(int i=arrRemove.size();0<i;i--)
		{
			items.remove(arrRemove.get(i-1));
		}
		this.items = items;
	}	
 }
