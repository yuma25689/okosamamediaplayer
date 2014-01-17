package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.VideoData;
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
 * VideoList�̃A�_�v�^
 * @author 25689
 *
 */
public class VideoSpinnerAdapter extends ArrayAdapter<VideoData> { 
//implements IAdapterUpdate, SectionIndexer {

	private LayoutInflater inflater;
	// private ArrayList<VideoData> items;
	private int iLayoutId;
	private ArrayList<VideoData> items = new ArrayList<VideoData>();
	private ArrayList<Integer> unknownVideoIds = new ArrayList<Integer>();
    // private OkosamaMediaPlayerActivity mActivity;
	private final String mUnknownVideo;

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
    public VideoSpinnerAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<VideoData> items) {
        super(currentactivity, layout, items );

        this.iLayoutId = layout;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUnknownVideo = currentactivity.getString(R.string.unknown_album_name);
        
        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        // mActivity = currentactivity;
        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);
        setItems(items);

    }

    /**
     * �V�����r���[�̍쐬�H
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
     * �r���[�ƃf�[�^��R����
     */
    //@Override
    public void bindView(View view, int pos) {
        
       	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();
        // position����f�[�^���擾
        VideoData data = getItem(pos);
    	
    	if( data == null )
    	{
    		// �f�[�^���Ȃ��Ƃ����̂́A���S�ɂ���������Ԃ����E�E
    		 vh.line1.setText("");
    		 return;
    	}
 
        // �A���o�������擾�A�r���[�ɐݒ�
        String name = data.getTitle();
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownVideo;
        }
        vh.line1.setText(displayname);
        
    }
    
    /**
     * �f�[�^�̕ύX�H
     */
    

	/**
	 * @return the items
	 */
	public ArrayList<VideoData> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<VideoData> items) {
		unknownVideoIds.clear();
//		ArrayList<Integer> arrRemove = new ArrayList<Integer>();
//        int index = 0;
//		for( VideoData data : items )
//		{
//	        String name = data.getVideoName();
//	        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
//			if( unknown )
//			{
//		        unknownVideoIds.add(data.getVideoId());
//		        if( 1 < unknownVideoIds.size())
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
		this.items = items;
	}	
 }
