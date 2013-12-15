package okosama.app.tab.media;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.AlbumListBehavior;
import okosama.app.factory.DroidWidgetKit;
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

		// ���C�A�E�g���N���A
		resetPanelViews(panelLayoutID);
		// �p�l���̈ʒu��ݒ�
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		
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
		RelativeLayout.LayoutParams lpList 
		= new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
        );
		lpList.addRule(RelativeLayout.BELOW,R.id.top_info_bar);
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
		
		return 0;
	}
}
