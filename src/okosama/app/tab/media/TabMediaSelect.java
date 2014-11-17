package okosama.app.tab.media;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentActionSetter;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * �^�u��͕킵���N���X�B���̃N���X�͕\���������Ȃ��B
 * �^�u�Ƃ������́AMediator�ɋ߂��B
 * �^�u�̍쐬����сA�^�u��̃R���|�[�l���g�̗L��/�����A�\��/��\���݂̂𐧌䂷��
 * @author 25689
 *
 */
public class TabMediaSelect extends Tab {
	// ���̃^�u�̃{�^���̑傫��
	public static final int BUTTON_WIDTH = 90;
	public static final int BUTTON_HEIGHT = 90;
	// ���̃^�u�̃t�b�^�̑傫��
	//static final int HOOTER_SIZE = BUTTON_HEIGHT + Tab.HDR_SIZE;
	
	/**
	 * �R���X�g���N�^ �e�Ƃقړ��l �^�uID�ɁATAB_ID_MEDIA�𗘗p����
	 * @param ID
	 * @param ll
	 * @param rl
	 */
	public TabMediaSelect(int ID, LinearLayout ll, ViewGroup rl) {
		super(ControlIDs.TAB_ID_MEDIA, ll, rl);
	}

	/**
	 * �^�u�S�̂̍쐬
	 * @return 0:���� 0�ȊO:�ُ�
	 */
	@Override
	public int create(int panelLayoutId) {
		int errCode = 0;
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();

		// �^�u�̃p�l�����쐬
		LayoutInflater inflator = act.getLayoutInflater();
		
		if( false == OkosamaMediaPlayerActivity.getResourceAccessor().isSdCanRead() )
		{
			// SD�J�[�h���ǂ߂Ȃ���Ԃ̏ꍇ�A�����\������r���[������Ă����܂�
			OkosamaMediaPlayerActivity.getResourceAccessor().setReadSDCardSuccess(false);
			tabBaseLayout = (ViewGroup)inflator.inflate(R.layout.tab_layout_sdcard_cant_read, null, false);
		}
		else
		{
			// �t���OON�̃^�C�~���O���������A���̍ۂ���ł���
			OkosamaMediaPlayerActivity.getResourceAccessor().setReadSDCardSuccess(true);
			tabBaseLayout = (ViewGroup)inflator.inflate(panelLayoutId, null, false);
			// �p�l���ʒu�̐ݒ�(FILL_PARENT)
			RelativeLayout.LayoutParams lp 
			= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
	        		0, 0 
	        );
			tabBaseLayout.setLayoutParams(lp);
			// �^�u�{�^����u���w�b�_�ƂȂ郌�C�A�E�g
			RelativeLayout rlHooter = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_hooter);
			// �A���o���^�u�{�^��
			
			mapBtn.put( TabPage.TABPAGE_ID_ALBUM, DroidWidgetKit.getInstance().MakeButton() );
			TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.ALBUM_TAB_BUTTON, null, 
				ComponentType.BUTTON, 
				0, 0, // DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE,
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.selector_music_select_album_image,
				R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_ALBUM).acceptConfigurator(tabBtnCreationData);
			SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_ALBUM ) );
			TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
			mapBtn.get(TabPage.TABPAGE_ID_ALBUM).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_ALBUM).getView());
	
			// �A�[�e�B�X�g�^�u�{�^��
			mapBtn.put( TabPage.TABPAGE_ID_ARTIST, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.ARTIST_TAB_BUTTON, null, ComponentType.BUTTON, 
				BUTTON_WIDTH + 5, 0, //DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.selector_music_select_artist_image,
				R.drawable.no_image,//R.drawable.tab4_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_ARTIST).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_ARTIST ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );			
			mapBtn.get(TabPage.TABPAGE_ID_ARTIST).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_ARTIST).getView());
	
			// �\���O�^�u
			mapBtn.put( TabPage.TABPAGE_ID_SONG, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.SONG_TAB_BUTTON, null, ComponentType.BUTTON, 
				( BUTTON_WIDTH + 5 ) * 2, 0, //DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.selector_music_select_song_image,
				R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_SONG).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_SONG ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );		
			mapBtn.get(TabPage.TABPAGE_ID_SONG).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_SONG).getView());
			
			// �v���C���X�g�^�u
			mapBtn.put( TabPage.TABPAGE_ID_PLAYLIST, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.PLAYLIST_TAB_BUTTON, null, ComponentType.BUTTON, 
				( BUTTON_WIDTH + 5 ) * 3, 0,//DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.selector_music_select_playlist_image,
				R.drawable.no_image,//R.drawable.tab4_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_PLAYLIST ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );			
			mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).getView());
	
			// video�^�u
			mapBtn.put( TabPage.TABPAGE_ID_VIDEO, DroidWidgetKit.getInstance().MakeButton() );
			tabBtnCreationData = new TabComponentPropertySetter(
				ControlIDs.VIDEO_TAB_BUTTON, null, ComponentType.BUTTON, 
				( BUTTON_WIDTH + 5 ) * 4, 0, 
				BUTTON_WIDTH, BUTTON_HEIGHT,
				R.drawable.selector_video_select_image,
				R.drawable.no_image, // R.drawable.tab3_btn_not_select_no_shadow2, 
				"", ScaleType.FIT_XY 
			);
			mapBtn.get(TabPage.TABPAGE_ID_VIDEO).acceptConfigurator(tabBtnCreationData);
			actMapTemp = new SparseArray< IViewAction >();
			actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_VIDEO ) );
			actionSetter = new TabComponentActionSetter( actMapTemp );		
			mapBtn.get(TabPage.TABPAGE_ID_VIDEO).acceptConfigurator(actionSetter);
			rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_VIDEO).getView());
		
			
			RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
			// �^�u�̒ǉ�
			addChild( TabPage.TABPAGE_ID_ALBUM, new TabPageAlbum( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_ARTIST, new TabPageArtist( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_SONG, new TabPageSong( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_PLAYLIST, new TabPagePlayList( this, pageContainer, rlCont ) );
			addChild( TabPage.TABPAGE_ID_VIDEO, new TabPageVideo( this, pageContainer, rlCont ) );
			//rlHooter.setBackgroundResource(R.color.gradiant_test4);
			rlCont.setBackgroundResource(R.color.gradiant_tab_base);
		}
		
		// �^�u�̃p�l����e����^����ꂽ���C�A�E�g�ɒǉ�
		componentContainer.addView(tabBaseLayout);
				
		return errCode;
	}

}
