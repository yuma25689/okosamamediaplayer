package okosama.app.tab.media;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.panel.MoveTabInfo;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * �ȃ��C�u�����^�u
 * @author 25689
 *
 */
public class TabPageSong extends TabPage {

	/**
	 * �R���X�g���N�^
	 * @param parent �e�E�B���h�E
	 * @param ll
	 * @param rl
	 */
	public TabPageSong( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		this.tabId = TABPAGE_ID_SONG;
		
		create(R.layout.tab_layout_content_generic_progress);
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		// �t���b�N���͑Ή�
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// ���t���b�N���̐ݒ�
		MoveTabInfo mti = new MoveTabInfo();
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_MEDIA);
		mti.setTabPageId(TabPage.TABPAGE_ID_ARTIST);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.artisttabbtn_normal);
		arrMti.add(mti);
		// �E�t���b�N���̐ݒ�
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MEDIA);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_PLAYLIST);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.playlisttabbtn_normal);
		arrMti.add(mtiR);
				
		// �p�l���̍쐬
		resetPanelViews( panelLayoutID, arrMti );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		// �X�e�[�^�X�o�[�̎擾�A�A�C�R���̐ݒ�
		View v = tabBaseLayout.findViewById(R.id.top_info_bar);
	    ImageView icon = (ImageView) v.findViewById(R.id.icon);
	    BitmapDrawable albumIcon 
	    =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
	    		R.drawable.filter_normal );
	    albumIcon.setFilterBitmap(false);
	    albumIcon.setDither(false);
	    icon.setBackgroundDrawable(albumIcon);
		tabBaseLayout.setLayoutParams(lp);
		
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel ); 
		//////////////////// list //////////////////////////
		// �p�l���ɂ̂��郊�X�g�̈ʒu�̐ݒ�
		RelativeLayout.LayoutParams lpList 
		= new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
        );
		lpList.addRule(RelativeLayout.BELOW,R.id.top_info_bar);
		
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTID_SONG, this, ComponentType.LIST_SONG, 
				lpList
				, null, null//R.drawable.tab_3_list_bk
				, "", ScaleType.FIT_XY
			)
		};
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getList(List.LISTID_SONG);
		if( lst == null )
		{
			lst = DroidWidgetKit.getInstance().MakeList( new TrackListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setList(List.LISTID_SONG,lst);
		
			widgets.add(lst);
		}
		
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			
			
			widget.getView().setBackgroundColor(Color.BLUE);
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			// addChild( creationData[i].getInternalID(), lst );
			tabBaseLayout.addView( widget.getView() );
			
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

		return 0;
	}
	
	@Override
	public void setActivate( boolean bActivate )
	{
		// �����A�����Ŗ߂��Ă͂����Ȃ�
		// OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTrackAdp().setFilterType(TrackListRawAdapter.FILTER_NORMAL);
		super.setActivate(bActivate);
	}
}
