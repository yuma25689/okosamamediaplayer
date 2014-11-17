package okosama.app.tab.media;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.AlbumListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.panel.MoveTabInfo;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * �A���o�����C�u�����^�u
 * @author 25689
 *
 */
public class TabPageAlbum extends TabPage {

	/**
	 * �R���X�g���N�^
	 * @param parent �e
	 * @param ll �e�̑匳�̃��C�A�E�g
	 * @param rl �e�̃��C�A�E�g
	 */
	public TabPageAlbum( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_ALBUM;
		// �v���O���X�t���̃��C�A�E�g
		create(R.layout.tab_layout_content_generic_progress);
	}
	@Override
	public int create(int panelLayoutID) {
		// �t���b�N���͑Ή�
		
		ArrayList<MoveTabInfo> arrMti = new ArrayList<MoveTabInfo>();
		// ���t���b�N���̐ݒ�
		MoveTabInfo mti = new MoveTabInfo();
		mti.setImageVertialAlign( MoveTabInfo.VERTIAL_TOP );
		mti.setTabInfoIndex( MoveTabInfo.LEFT_1 );
		mti.setTabId(ControlIDs.TAB_ID_MAIN);
		mti.setTabPageId(TabPage.TABPAGE_ID_NOW_PLAYLIST);
		mti.setPanelId(R.id.left_move_panel);
		mti.setImageViewId(R.id.left_move_image);
		mti.setTabImageResId(R.drawable.brat_main_normal);
		arrMti.add(mti);
		// �E�t���b�N���̐ݒ�
		MoveTabInfo mtiR = new MoveTabInfo();
		mtiR.setTabInfoIndex( MoveTabInfo.RIGHT_1 );
		mtiR.setTabId(ControlIDs.TAB_ID_MEDIA);
		mtiR.setTabPageId(TabPage.TABPAGE_ID_ARTIST);
		mtiR.setPanelId(R.id.right_move_panel);
		mtiR.setImageViewId(R.id.right_move_image);
		mtiR.setTabImageResId(R.drawable.artisttabbtn_normal);
		arrMti.add(mtiR);

		// ���C�A�E�g���N���A
		resetPanelViews(panelLayoutID,arrMti);
		// �p�l���̈ʒu��ݒ�
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        	0, 0// , ControlDefs.APP_BASE_WIDTH, ControlDefs.APP_BASE_HEIGHT
        );
		tabBaseLayout.setLayoutParams(lp);
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel ); 
		
		// TODO:������Ă邩���� ???
//		View v = tabBaseLayout.findViewById(R.id.top_info_bar);
//	    ImageView icon = (ImageView) v.findViewById(R.id.icon);
//	    BitmapDrawable albumIcon 
//	    =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
//	    		android.R.drawable.divider_horizontal_dark );
//	    albumIcon.setFilterBitmap(false);
//	    albumIcon.setDither(false);
//	    icon.setBackgroundDrawable(albumIcon);
		
		// �v���O���X�o�[�̐ݒ�
//		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel );
//		ProgressBar prog = (ProgressBar) updateProgressPanel.findViewById(R.id.progress_common);
//		prog.setBackgroundResource(R.drawable.empty);
		
		// �p�l���ɂ̂��郊�X�g�̈ʒu�̐ݒ�
		RelativeLayout.LayoutParams lpList = OkosamaMediaPlayerActivity.dispInfo.createLayoutParamsForTabContent();
		
		// ���X�g�̍쐬
		TabComponentPropertySetter creationData[] = {
			// ���X�g�̐�������ݒ�
			new TabComponentPropertySetter(
				List.LISTID_ALBUM, this, ComponentType.LIST_ALBUM, 
				lpList
				, null, null, 
				"", ScaleType.FIT_XY
			),
		};
		// �쐬�ς݂̏ꍇ�A���C����ʂɊ��Ɋi�[���Ă��郊�X�g���擾
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getList(List.LISTID_ALBUM);
		if( lst == null )
		{
			// �܂��쐬����Ă��Ȃ��ꍇ�A���X�g���쐬���A���C����ʂɊi�[����
			lst = DroidWidgetKit.getInstance().MakeList( new AlbumListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setList(
					List.LISTID_ALBUM,lst);
			widgets.add(lst);
		}
		else
		{
			if( -1 == widgets.indexOf(lst) )
			{
				widgets.add(lst);
				OkosamaMediaPlayerActivity.removeFromParent(lst.getView());
			}			
		}
		
		// ���̃p�l���ɂ̂����S�Ă�widget�̐�����ݒ�
		creationData[0].setColorBack(Color.WHITE);
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			
			// ���̃^�u�̎q���ڂƂ��Ēǉ�
			tabBaseLayout.addView( widget.getView() );
			i++;
		}
		// lst.getView().setOnTouchListener(new TabListViewTouchListener(0,0));
		rightPanel = new TabMoveRightInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel = new TabMoveLeftInfoPanel(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		leftPanel.createInstance(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity());
		rightPanel.insertToLayout(tabBaseLayout);
		leftPanel.insertToLayout(tabBaseLayout);
		// LogWrapper.e("album flick setting","ok");
		
		return 0;
	}
}
