package okosama.app.tab;

import java.util.ArrayList;
import java.util.HashMap;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPlayPauseAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
import okosama.app.action.TabSelectAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
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
public class Tab extends TabComponentParent {

	protected SparseArray<Button> mapBtn;
	int iCurrentTabPageId;
	
	public Tab( int ID, LinearLayout ll, ViewGroup rl )
	{
		this.internalID = ID;
		pageContainer = ll;
		componentContainer = rl;
		iCurrentTabPageId = TabPage.TABPAGE_ID_NONE;
	}
	
	/**
	 * �^�u�S�̂̍쐬
	 * @return 0:���� 0�ȊO:�ُ�
	 */
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
		RelativeLayout rlHdr = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_header);
		// �^�u�̃{�^�������͂����ō��H
		// �v���C�^�u�{�^��
		mapBtn.put( TabPage.TABPAGE_ID_PLAY, DroidWidgetKit.getInstance().MakeButton() );
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			ControlIDs.PLAY_TAB_BUTTON, ComponentType.BUTTON, 
			10, 10, 100, 100, 
			null, R.drawable.music_tab_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_PLAY).acceptConfigurator(tabBtnCreationData);		
		SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, TabPage.TABPAGE_ID_PLAY ) );
		mapBtn.get(TabPage.TABPAGE_ID_PLAY).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAY).getView());
		// ���f�B�A�^�u�{�^��
		mapBtn.put( TabPage.TABPAGE_ID_MEDIA, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.MEDIA_TAB_BUTTON, ComponentType.BUTTON,
			120, 40, 100, 100,
			null, R.drawable.music_choice_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_MEDIA).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, TabPage.TABPAGE_ID_MEDIA ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );	
		mapBtn.get(TabPage.TABPAGE_ID_MEDIA).acceptConfigurator(actionSetter);
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_MEDIA).getView());
		// �v���C���X�g�^�u�{�^��
		mapBtn.put( TabPage.TABPAGE_ID_NOW_PLAYLIST, DroidWidgetKit.getInstance().MakeButton() );
		tabBtnCreationData
		= new TabComponentPropertySetter(
			ControlIDs.PLAYLIST_TAB_BUTTON, ComponentType.BUTTON,
			230, 40, 100, 100,
			null, R.drawable.now_playlist_button_image,
			"", ScaleType.FIT_XY
		);
		mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(tabBtnCreationData);		
		actMapTemp
		= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, TabPage.TABPAGE_ID_PLAYLIST ) );
		mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).acceptConfigurator(new TabComponentActionSetter( actMapTemp ));
		rlHdr.addView(mapBtn.get(TabPage.TABPAGE_ID_PLAYLIST).getView());				
		
		RelativeLayout rlCont = (RelativeLayout) tabBaseLayout.findViewById(R.id.tab_contents);
		// �^�u�̒ǉ�
		addChild( TabPage.TABPAGE_ID_PLAY, new TabPagePlay( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_MEDIA, new TabPageMedia( this, pageContainer, rlCont ) );
		addChild( TabPage.TABPAGE_ID_PLAY, new TabPageNowPlaylist( this, pageContainer, rlCont ));
		// �^�u�y�[�W�́AsetCurrentTab��ǂ񂾎��A�A�N�e�B�u�Ȃ��̂����������B
		// �Ȃ����^�u�y�[�W��create�͌Ă�ł͂����Ȃ����ƂɂȂ��Ă��܂����B
		// �܂��Acreate���̃^�uID�͕s���Ȃ̂ŁAsetCurrentTab�͂����ł͌Ă΂��A��ʂɌĂ΂���B
		
		// �^�u�̃p�l����e����^����ꂽ���C�A�E�g�ɒǉ�
		componentContainer.addView(tabBaseLayout);
		
		return errCode;
	}
	
	/**
	 * ���݂̃^�u��ݒ肷��
	 * ����ATabSelectAction�ł͌��ǂ��ꂪ�Ă΂��
	 * @param tabId
	 */
	public void setCurrentTab(int tabId,boolean save)
	{
		// TODO: �^�u�y�[�W�̓}�b�v�Ɋi�[��������������������Ȃ�

//        		// ��x�S�Ẵ^�u�̑I��������
//        		c.setActivate( false );
		if( null != children.get(tabId,null) )
		{
			if( null != children.get(iCurrentTabPageId,null) )
			{
	    		// ���ݑI�𒆂̃^�u�̃^�u�y�[�W���N���A����
				children.get(iCurrentTabPageId).setActivate(false);
	   		}
			// ���ݑI�𒆂̃^�u�̃^�u�y�[�W���N���A����
			children.get(tabId).setActivate(true);
			iCurrentTabPageId = tabId;
   		}
//        for( ITabComponent c : children ) {
//        	if( c instanceof TabPage ) { // �ł�����g�������Ȃ������E�E�E�B
//        		((TabPage) c).setTabButtonToFront();
//        	}
//        }
		// �A�v���P�[�V�����ɑI�����ꂽ�^�u�̉��ID��ݒ肷��
		// ���̏ꏊ�����ł������ǂ����͕s��
        if( save == true )
        {
        	OkosamaMediaPlayerActivity.setCurrentDisplayId(this.internalID,tabId);
        }
	}

}
