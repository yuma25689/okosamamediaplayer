package okosama.app.tab;

import java.util.HashMap;

import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.List;
import android.graphics.Color;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class TabPageNowPlaylist extends TabPage {

	Tab tabContent;
	TabPageNowPlaylist( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_NOW_PLAYLIST;
		create();
		componentContainer.addView(tabButton.getView());
	}
	@Override
	public int create() {
		// �^�u�̃{�^�������͂����ō��H
		tabButton = DroidWidgetKit.getInstance().MakeButton();
		OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns.add(tabButton);
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData
		= new TabComponentPropertySetter(
			"playlistTabBtn", ComponentType.BUTTON,
			230, 40, 100, 100,
			null, R.drawable.now_playlist_button_image,
			"", ScaleType.FIT_XY
		);
		tabButton.acceptConfigurator(tabBtnCreationData);
		// NowPlaylistTab�{�^���̃A�N�V����
		SparseArray< IViewAction > actMapTemp
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );	
		tabButton.acceptConfigurator(actionSetter);

		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTNAME_NOW_PLAYLIST, ComponentType.LIST_NOWPLAYLIST, 
				// 0, 260, 480, 599
				0, 0,//150 + 2
				480, AppStatus.LIST_HEIGHT_1//637 //599
				, null, null//R.drawable.tab_3_list_bk
				, "", ScaleType.FIT_XY
			)
		};
		List lsts[] = {
			DroidWidgetKit.getInstance().MakeList( new TrackListBehavior() )
		};
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( List lst : lsts )
		{
			lst.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			
			
			lst.getView().setBackgroundColor(Color.DKGRAY);
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			addChild( lst );
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}		
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
			// =���f�B�A�^�u���I�����ꂽ�ꍇ�H
			// �^�u�{�^�����u���v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( false );
			// pageContainer.setBackgroundColor(Color.rgb(100, 120, 140));

			// TODO:�w�i�C���[�W��ݒ肷��
			// pageContainer.setBackgroundDrawable(null);
			// �^�u�y�[�W��������
			// tabContent.setActiveFlg( true );	// setActivate��setActiveFlg���ł��Ă��܂����͕̂s�{�ӂ����d���Ȃ�
		}
		else
		{
			// �^�u���A�N�e�B�u�ł͂Ȃ��Ȃ����ꍇ
			// �^�u�{�^�����u�L�v���Ȏ��̕\���ɂ���
			tabButton.setEnabled( true );
			// �w�i�C���[�W������
			// �K�v�Ȃ��H
			// pageContainer.setBackgroundDrawable(null);
			// �^�u�y�[�W��������
			// tabContent.setActiveFlg( false );	// setActivate��setActiveFlg���ł��Ă��܂����͕̂s�{�ӂ����d���Ȃ�
		}
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
	}

}
