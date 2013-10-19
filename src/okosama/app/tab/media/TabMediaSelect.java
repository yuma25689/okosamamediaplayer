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
import okosama.app.tab.TabPage;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

/**
 * �^�u��͕킵���N���X�B���̃N���X�͕\���������Ȃ��B
 * �^�u�Ƃ������́AMediator�ɋ߂��B
 * �^�u�̍쐬����сA�^�u��̃R���|�[�l���g�̗L��/�����A�\��/��\���݂̂𐧌䂷��
 * @author 25689
 *
 */
public class TabMediaSelect extends Tab {
	static final int BUTTON_HEIGHT = 100;
	static final int HOOTER_SIZE = BUTTON_HEIGHT + Tab.HDR_SIZE;
	
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
			ControlIDs.ALBUM_TAB_BUTTON, ComponentType.BUTTON, 
			0, 0, // DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE,
			120, BUTTON_HEIGHT,
			R.drawable.music_select_album_image,
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
			ControlIDs.ARTIST_TAB_BUTTON, ComponentType.BUTTON, 
			120 + 5, 0, //DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
			120, BUTTON_HEIGHT,
			R.drawable.music_select_artist_image,
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
			ControlIDs.SONG_TAB_BUTTON, ComponentType.BUTTON, 
			( 120 + 5 ) * 2, 0, //DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
			120, BUTTON_HEIGHT,
			R.drawable.music_select_song_image,
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
			ControlIDs.PLAYLIST_TAB_BUTTON, ComponentType.BUTTON, 
			( 120 + 5 ) * 3, 0,//DroidDisplayInfo.CURRENT_BASE_DEVICE_HEIGHT - HOOTER_SIZE, 
			120, BUTTON_HEIGHT,
			R.drawable.music_select_playlist_image,
			R.drawable.no_image,//R.drawable.tab4_btn_not_select_no_shadow2, 
			"", ScaleType.FIT_XY 
		);
		mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(tabBtnCreationData);
		actMapTemp = new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( this.getInternalID(), TabPage.TABPAGE_ID_PLAYLIST ) );
		actionSetter = new TabComponentActionSetter( actMapTemp );			
		mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(actionSetter);
		rlHooter.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).getView());
		
		RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
		// �^�u�̒ǉ�
		addChild( TabPage.TABPAGE_ID_ALBUM, new TabPageAlbum( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_ARTIST, new TabPageArtist( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_SONG, new TabPageSong( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_PLAYLIST, new TabPagePlayList( this, pageContainer, rlCont ) );
	
		//rlHooter.setBackgroundResource(R.color.gradiant_test4);
		rlCont.setBackgroundResource(R.color.gradiant_test4);
		
		// �^�u�̃p�l����e����^����ꂽ���C�A�E�g�ɒǉ�
		componentContainer.addView(tabBaseLayout);
				
		return errCode;
	}

}
