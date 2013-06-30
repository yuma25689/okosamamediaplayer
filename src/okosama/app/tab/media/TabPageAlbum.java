package okosama.app.tab.media;

import java.util.HashMap;

import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.behavior.AlbumListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.storage.Database;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import android.graphics.Color;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPageAlbum extends TabPage {

	public TabPageAlbum( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_ALBUM;
		
		create();
		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create() {
		// �^�u�̃{�^�������͂����ō��H
		tabButton = DroidWidgetKit.getInstance().MakeButton();
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			"albumTabBtn", ComponentType.BUTTON, 
			//90 + 5, 859 - 150 + 2, 90, 70,
			0, 859 - 70, 90, 70,
			R.drawable.music_select_album_image,
			null, // R.drawable.tab1_btn_not_select_no_shadow2, 
			"", ScaleType.FIT_XY 
		);
		tabButton.acceptConfigurator(tabBtnCreationData);

		// ---- action
		HashMap< Integer, IViewAction > actMapTemp 
			= new HashMap< Integer, IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTNAME_ALBUM, ComponentType.LIST_ALBUM, 
				//0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, AppStatus.LIST_HEIGHT_1//637 + 70//- 90 //599
				, null, null,//R.drawable.tab_1_list_bk, 
				"", ScaleType.FIT_XY
			)
		};
		List lsts[] = {
			DroidWidgetKit.getInstance().MakeList( new AlbumListBehavior() )
//			,DroidWidgetKit.getInstance().MakeButton()
		};
		// ---- action
//		HashMap< Integer, IViewAction > actMapList
//			= new HashMap< Integer, IViewAction >();
//		// TODO: Action�̐ݒ�
//		actMapList.put( IViewAction.ACTION_ID_ONCLICK, new NoAction() );
//		TabComponentActionSetter actionLstClick = new TabComponentActionSetter( actMapList );			
//		lsts[0].acceptConfigurator(actionLstClick);
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( List lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:�A�N�V������ݒ�
			
			lst.getView().setBackgroundColor(Color.YELLOW);
			
			// ���X�g�����̃^�u�q���ڂƂ��Ēǉ�
			addChild( lst );
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////

		return 0;
	}
	/**
	 * Active���ǂ�����ݒ�B
	 * @param bActivate
	 */
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{			
			// �^�u���A�N�e�B�u�����ꂽ�ꍇ
			// �^�u�{�^�����u���v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( false );
			
			// �w�i�C���[�W�������H
//			if( pageContainer.getBackground() != null )
//			{
//				pageContainer.getBackground().setCallback(null);
//			}			
//			// �w�i�C���[�W��ݒ肷��
//			pageContainer.setBackgroundDrawable(
//				OkosamaMediaPlayerActivity.res.getResourceDrawable(
//					R.drawable.tab_2_select_2
//				)
//				// getResources().getDrawable(R.drawable.background_2)
//			);
			
			// �J�[�\�����Đݒ肷��
			// �J�[�\���̍쐬
			OkosamaMediaPlayerActivity activity = (OkosamaMediaPlayerActivity) ResourceAccessor.getInstance().getActivity();
			//if( null == Database.getInstance(activity).getCursor(Database.AlbumCursorName) )
			//{
			if( activity != null)
			{
				// TODO: �����������A���C������擾
				Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createAlbumCursor(activity.getAlbumAdp().getQueryHandler(), null ); //, null);
			}
			//}
		}
		else
		{
			// �^�u���A�N�e�B�u�ł͂Ȃ��Ȃ����ꍇ
			// �^�u�{�^�����u�L�v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( true );
			// �w�i�C���[�W������
			// �K�v�Ȃ��H
			// pageContainer.setBackgroundDrawable(null);
		}
		// �e�^�u�������Ȃ�΁A�\��������
		// ���̃^�u�̏ꍇ�AActive�Ƃ̈Ⴂ���K�v
		// TODO:�����ł�邩�ǂ����͔���
		tabButton.setVisible(parent.isActive());
		
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
	}
}
