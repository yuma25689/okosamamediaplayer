package okosama.app.tab;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPagePlay extends TabPage {

	public TabPagePlay( Tab parent, LinearLayout ll, RelativeLayout rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_PLAY;
		
		create(R.layout.tab_layout_content_generic_flickable);
//		componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {
		
		resetPanelViews( panelLayoutID );
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		
		// ---- action
//		SparseArray< IViewAction > actMapTemp 
//			= new SparseArray< IViewAction >();
//		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
//		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
//		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// button //////////////////////////
//		TabComponentPropertySetter creationData[] = {
//			// --------------------- STOP
//			new TabComponentPropertySetter(
//				ControlIDs.STOP_BUTTON, this, ComponentType.BUTTON, 
//				150, 500, 100, 100
//				, null, R.drawable.stop_button_image, "", ScaleType.FIT_XY
//			),
//			// --------------------- TWITTER
//			new TabComponentPropertySetter(
//				ControlIDs.TWEET_BUTTON, this, ComponentType.BUTTON, 
//				370, 450, 80, 80
//				, null, R.drawable.internal_btn_image, "", ScaleType.FIT_XY
//			),
//		};
		
		// �w�i�摜�͂Ȃ���setActivate�̒S���Ȃ̂ŁA�����ł͒ǉ����Ȃ�
		
		
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// �{�^���̃n���h���N���X���쐬
		// �����炭�A�N���X�Ɏ�������������
//		absWidget widgets[] = {
//			DroidWidgetKit.getInstance().MakeButton()
//			,DroidWidgetKit.getInstance().MakeButton()
//		};
		
		// ---- action
		// Stop�{�^��
//		SparseArray< IViewAction > actMapStop 
//			= new SparseArray< IViewAction >();
//		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
//		// twitter�{�^��
//		SparseArray< IViewAction > actMapTwitter
//			= new SparseArray< IViewAction >();
//		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
//		
//		TabComponentActionSetter actionSetterCont[] = {
//			new TabComponentActionSetter( actMapStop )
//			,new TabComponentActionSetter( actMapTwitter )
//		};
		
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
//		int i=0;
//		for( absWidget widget : widgets )
//		{
//			widget.acceptConfigurator(creationData[i]);
//			// TODO:�{�^���̃A�N�V������ݒ�
//			if( actionSetterCont[i] != null )
//			{
//				widget.acceptConfigurator(actionSetterCont[i]);
//			}
//			
//			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
//			//addChild( creationData[i].getInternalID(), widget );
//			tabBaseLayout.addView( widget.getView() );
//			
//			// �{�^����z�u
//			// ����́AsetActivate�ōs��
//			// componentContainer.addView( btn.getView() );
//			i++;
//		}
		// ����ɂ��ƁA�Ō�ɉ�����ꂽ���̂�Zorder����ԏ�炵���B
		// �ǂ����ɂ��Ă��ABringToFront�ŕς�����炵����
		SubControlPanel.insertToLayout(tabBaseLayout);
		TimeControlPanel.insertToLayout(tabBaseLayout);
		PlayControlPanel.insertToLayout(tabBaseLayout);
//		tabBaseLayout.addView( activity.getSubCP().getView() );
//		tabBaseLayout.addView( activity.getTimeCP().getView() );
//		tabBaseLayout.addView( activity.getPlayCP().getView() );
		tabBaseLayout.setBackgroundResource(R.color.gradiant_test);
		// tabBaseLayout.setOnTouchListener(new TabViewTouchListener(0,0));
		
		activity.updatePlayStateButtonImage();
		
		return 0;
	}
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			SubControlPanel.insertToLayout(tabBaseLayout);
			NowPlayingControlPanel.insertToLayout(tabBaseLayout);
			TimeControlPanel.insertToLayout(tabBaseLayout);
			PlayControlPanel.insertToLayout(tabBaseLayout);
		}
		super.setActivate(bActivate);
	}
	
}
