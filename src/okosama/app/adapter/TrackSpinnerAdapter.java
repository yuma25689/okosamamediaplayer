package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.TrackData;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
// import okosama.app.storage.QueryHandler;
// import android.content.AsyncQueryHandler;

/**
 * AlbumList�̃A�_�v�^
 * @author 25689
 *
 */
public class TrackSpinnerAdapter extends ArrayAdapter<TrackData> { 
//implements IAdapterUpdate, SectionIndexer {

	private LayoutInflater inflater;
	// private ArrayList<AlbumData> items;
	private int iLayoutId;
	private ArrayList<TrackData> items = new ArrayList<TrackData>();
	private ArrayList<Integer> unknownAlbumIds = new ArrayList<Integer>();
    // private OkosamaMediaPlayerActivity mActivity;
	private final String mUnknownAlbum;
	private final String mNoSelection;

    // View�̃z���_�H
    static class ViewHolder {
        TextView line1;
    }

    /**
     * �A�_�v�^�̃R���X�g���N�^
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public TrackSpinnerAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<TrackData> items) {
        super(currentactivity, layout, items );

        this.iLayoutId = layout;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUnknownAlbum = currentactivity.getString(R.string.unknown_album_name);
        mNoSelection = currentactivity.getString(R.string.no_selection);
        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        // mActivity = currentactivity;
        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);
        setItems(items);
    }

    /**
     * �V�����r���[�̍쐬�H
     */
//    @Override
//    public View getView(int pos, View convertView, ViewGroup parent) {
//    	View v = convertView;  
//    	if (v == null) {
//    	   ViewHolder vh = new ViewHolder();
//	       v = inflater.inflate(iLayoutId, null); 
//	       vh.line1 = (TextView) v.findViewById(R.id.line1);
//	       v.setTag(vh);
//    	}
//	    bindView(v,pos);
//    	return v;
//    }
//
//    /**
//     * �r���[�ƃf�[�^��R����
//     */
//    //@Override
//    public void bindView(View view, int pos) {
//        
//       	// �^�O����r���[�z���_�[���擾
//        ViewHolder vh = (ViewHolder) view.getTag();
//        // position����f�[�^���擾
//        TrackData data = getItem(pos);
//    	
//    	if( data == null )
//    	{
//    		// �f�[�^���Ȃ��Ƃ����̂́A���S�ɂ���������Ԃ����E�E
//    		 vh.line1.setText("");
//    		 return;
//    	}
// 
//        // �A���o�������擾�A�r���[�ɐݒ�
//        String name = data.getTrackTitle();
//        String displayname = name;
//        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
//        if (unknown) {
//            displayname = mUnknownAlbum;
//        }
//        vh.line1.setText(displayname);
//        
//    }
    
    /**
     * �f�[�^�̕ύX�H
     */
    

	/**
	 * @return the items
	 */
	public ArrayList<TrackData> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<TrackData> items) {
		unknownAlbumIds.clear();
//		ArrayList<Integer> arrRemove = new ArrayList<Integer>();
//        int index = 0;
//		for( TrackData data : items )
//		{
//	        String name = data.getTrackName();
//	        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
//			if( unknown )
//			{
//		        unknownAlbumIds.add(data.getAlbumId());
//		        if( 1 < unknownAlbumIds.size())
//		        {
//		        	arrRemove.add(index);
//		        	//this.items.remove(index);
//		        }
//			}
//			index++;
//		}
//		for(int i=arrRemove.size();0<i;i--)
//		{
//			items.remove(arrRemove.get(i-1));
//		}
        ArrayList<TrackData> itemsTmp = new ArrayList<TrackData>(items.size());
        itemsTmp.addAll(items);
        TrackData dataNoSelect = new TrackData();
        dataNoSelect.setDataId(-1);
        dataNoSelect.setName(mNoSelection);
        itemsTmp.add(0,dataNoSelect);        
		this.items = itemsTmp;
	}	
 }
