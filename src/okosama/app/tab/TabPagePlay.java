package okosama.app.tab;

import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.R.drawable;
import okosama.app.action.CycleRepeatAction;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaStopAction;
import okosama.app.action.TabSelectAction;
import okosama.app.action.TimeButtonClickAction;
import okosama.app.action.ToggleShuffleAction;
import okosama.app.action.TweetAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.TimeControlPanel;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.util.SparseArray;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPagePlay extends TabPage {

	public TabPagePlay( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_PLAY;
		
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
		OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns.add(tabButton);
		// TAB_BUTTON
		TabComponentPropertySetter tabBtnCreationData = new TabComponentPropertySetter(
			"playTabBtn", ComponentType.BUTTON, 
			10, 40, 100, 100, 
			null, R.drawable.music_tab_button_image,
			"", ScaleType.FIT_XY
		);
		tabButton.acceptConfigurator(tabBtnCreationData);

		// ---- action
		SparseArray< IViewAction > actMapTemp 
			= new SparseArray< IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- HOUR
//			new TabComponentPropertySetter(
//				"hour10", ComponentType.BUTTON, 
//				22, 400, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
//			),
//			new TabComponentPropertySetter(
//				"hour1", ComponentType.BUTTON, 
//				92, 400, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num3_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
//			),
//
//			// ------------- MINUTE
//			new TabComponentPropertySetter(
//				"minute10", ComponentType.BUTTON, 
//				167, 390, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num4_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
//			),
//			new TabComponentPropertySetter(
//				"minute1", ComponentType.BUTTON, 
//				237, 375, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num6_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
//			),
//			// --------------------- SECOND
//			new TabComponentPropertySetter(
//				"sec10", ComponentType.BUTTON, 
//				303, 390, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num8_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY 
//			),
//			new TabComponentPropertySetter(
//				"sec1", ComponentType.BUTTON, 
//				373, 375, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num1_1, R.drawable.time_bk_shelf, "", ScaleType.FIT_XY
//			),			
//			// --------------------- PLAY
//			new TabComponentPropertySetter(
//				"playbutton", ComponentType.BUTTON, 
//				132, 215, 80, 100
//				, null, R.drawable.play_button_image, "", ScaleType.FIT_XY
//			),
			// --------------------- STOP
			new TabComponentPropertySetter(
				"stopbutton", ComponentType.BUTTON, 
				150, 590, 100, 100
				, null, R.drawable.stop_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- TWITTER
			new TabComponentPropertySetter(
				"tweetbutton", ComponentType.BUTTON, 
				370, 590, 80, 80
				, null, R.drawable.internal_btn_image, "", ScaleType.FIT_XY
			),
//			// --------------------- NEXT
//			new TabComponentPropertySetter(
//				"nextbutton", ComponentType.BUTTON, 
//				380, 420, 100, 100
//				, null, R.drawable.next_button_image, "", ScaleType.FIT_XY
//			),
//			// --------------------- PREV
//			new TabComponentPropertySetter(
//				"prevbutton", ComponentType.BUTTON, 
//				30, 440, 100, 100
//				, null, R.drawable.back_button_image, "", ScaleType.FIT_XY
//			),
			// TODO: �����炭�A�V���b�t���ƃ��s�[�g�̓g�O���ɂ��ׂ��ł���
			// --------------------- SHUFFLE
			new TabComponentPropertySetter(
				"shufflebutton", ComponentType.BUTTON, 
				20, 700, 100, 100
				, null, drawable.no_image, "", ScaleType.FIT_XY
			),
			// --------------------- REPEAT
			new TabComponentPropertySetter(
				"repeatbutton", ComponentType.BUTTON, 
				200, 690, 100, 100
				, null, drawable.no_image, "", ScaleType.FIT_XY
			),
			// --------------------- DURATION
//			new TabComponentPropertySetter(
//				"durationlabel", ComponentType.LABEL, 
//				40, 370, 200, 50
//				, null, drawable.no_image, "", ScaleType.FIT_XY
//			),		
//			// --------------------- SONG
//			new TabComponentPropertySetter(
//				"songlabel", ComponentType.LABEL, 
//				30, 320, 400, 50
//				, null, drawable.no_image, "", ScaleType.FIT_XY
//			),		
//			// --------------------- ARTIST
//			new TabComponentPropertySetter(
//				"artistlabel", ComponentType.LABEL, 
//				35, 480, 400, 50
//				, null, drawable.no_image, "", ScaleType.FIT_XY
//			),		
//			// --------------------- ALBUM
//			new TabComponentPropertySetter(
//				"albumlabel", ComponentType.LABEL, 
//				35, 530, 400, 50
//				, null, drawable.no_image, "", ScaleType.FIT_XY
//			),		
		};
		
		// �w�i�摜�͂Ȃ���setActivate�̒S���Ȃ̂ŁA�����ł͒ǉ����Ȃ�
		
		
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		// �{�^���̃n���h���N���X���쐬
		// �����炭�A�N���X�Ɏ�������������
		absWidget widgets[] = {
//			OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[0]
//			,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[1]
//			,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[2]
//			,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[3]
//			,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[4]
//			,OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getTimesButton()[5]
			DroidWidgetKit.getInstance().MakeButton()
			//,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			//,DroidWidgetKit.getInstance().MakeButton()
			//,DroidWidgetKit.getInstance().MakeButton()
			,activity.getShuffleButton()//DroidWidgetKit.getInstance().MakeButton()
			,activity.getRepeatButton()//DroidWidgetKit.getInstance().MakeButton()
//			,activity.getDurationLabel()
//			,activity.getNowPlayingSongLabel()
//			,activity.getNowPlayingArtistLabel()
//			,activity.getNowPlayingAlbumLabel()
		};
		
		// ---- action
		// Stop�{�^��
//		SparseArray< IViewAction > actMapTimeClick1 = new SparseArray< IViewAction >();
//		SparseArray< IViewAction > actMapTimeClick2 = new SparseArray< IViewAction >();
//		SparseArray< IViewAction > actMapTimeClick3 = new SparseArray< IViewAction >();
//		SparseArray< IViewAction > actMapTimeClick4 = new SparseArray< IViewAction >();
//		SparseArray< IViewAction > actMapTimeClick5 = new SparseArray< IViewAction >();
//		SparseArray< IViewAction > actMapTimeClick6 = new SparseArray< IViewAction >();
//		actMapTimeClick1.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_HOUR_10) );	
//		actMapTimeClick2.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_HOUR_1) );	
//		actMapTimeClick3.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_MINUTE_10) );	
//		actMapTimeClick4.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_MINUTE_1) );	
//		actMapTimeClick5.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_SEC_10) );	
//		actMapTimeClick6.put( IViewAction.ACTION_ID_ONCLICK, new TimeButtonClickAction(TimeButtonClickAction.TIME_ID_SEC_1) );	
		// Stop�{�^��
		SparseArray< IViewAction > actMapStop 
			= new SparseArray< IViewAction >();
		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
		// twitter�{�^��
		SparseArray< IViewAction > actMapTwitter
			= new SparseArray< IViewAction >();
		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
		// shuffle�{�^��
		SparseArray< IViewAction > actMapShuffle
			= new SparseArray< IViewAction >();
		actMapShuffle.put( IViewAction.ACTION_ID_ONCLICK, new ToggleShuffleAction() );
		// repeat�{�^��
		SparseArray< IViewAction > actMapRepeat
			= new SparseArray< IViewAction >();
		actMapRepeat.put( IViewAction.ACTION_ID_ONCLICK, new CycleRepeatAction() );
		
		TabComponentActionSetter actionSetterCont[] = {
//			new TabComponentActionSetter( actMapTimeClick1 )
//			,new TabComponentActionSetter( actMapTimeClick2 )
//			,new TabComponentActionSetter( actMapTimeClick3 )
//			,new TabComponentActionSetter( actMapTimeClick4 )
//			,new TabComponentActionSetter( actMapTimeClick5 )
//			,new TabComponentActionSetter( actMapTimeClick6 )
			//,new TabComponentActionSetter( actMapPlay )
			new TabComponentActionSetter( actMapStop )
			,new TabComponentActionSetter( actMapTwitter )
			//,new TabComponentActionSetter( actMapNext )
			//,new TabComponentActionSetter( actMapBack )
			,new TabComponentActionSetter( actMapShuffle )
			,new TabComponentActionSetter( actMapRepeat )
//			,null
//			,null
//			,null
//			,null
		};
		
		addChild( activity.getTimeCP() );
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			if( actionSetterCont[i] != null )
			{
				widget.acceptConfigurator(actionSetterCont[i]);
			}
			
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			addChild( widget );
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		activity.setShuffleButtonImage();
		activity.setRepeatButtonImage();
		
//		//////////////////////// image /////////////////////
//		TabComponentPropertySetter creationDataImg[] = {
//			new TabComponentPropertySetter(
//				"sep", ComponentType.IMAGE, 
//				157, 380, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num8_1, null, "", ScaleType.FIT_XY // TODO:�Z�p���[�^�̉摜�ɕύX����
//			),
//			new TabComponentPropertySetter(
//				"sep", ComponentType.IMAGE, 
//				293, 405, 
//				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
//				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
//				, R.drawable.num8_1, null, "", ScaleType.FIT_XY // TODO:�Z�p���[�^�̉摜�ɕύX����
//			)
//		};
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
			// �w�i�C���[�W��ݒ肷��
			//pageContainer.setBackgroundColor(Color.rgb(120, 120, 180));
			pageContainer.setBackgroundResource(R.color.gradiant_test);
			// ���R�X�g�̂��ߒ�~��
//			pageContainer.setBackgroundDrawable(
//				OkosamaMediaPlayerActivity.res.getResourceDrawable(
//					R.drawable.background_4
//				)
//				// getResources().getDrawable(R.drawable.background_2)
//			);
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
		// TabComponentParent��setActivate�ŁA�S�Ă̎q�N���X��setActivate�����s�����
        super.setActivate( bActivate );
	}
}
