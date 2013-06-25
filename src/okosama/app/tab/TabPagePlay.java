package okosama.app.tab;

import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPauseAction;
import okosama.app.action.MediaPlayAction;
import okosama.app.action.MediaStopAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
import okosama.app.action.TabSelectAction;
import okosama.app.action.TweetAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
import android.graphics.Color;
import android.widget.ImageView.ScaleType;
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
		HashMap< Integer, IViewAction > actMapTemp 
			= new HashMap< Integer, IViewAction >();
		actMapTemp.put( IViewAction.ACTION_ID_ONCLICK, new TabSelectAction( parent, tabId ) );
		TabComponentActionSetter actionSetter = new TabComponentActionSetter( actMapTemp );			
		tabButton.acceptConfigurator(actionSetter);
		
		//////////////////// button //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- HOUR
			new TabComponentPropertySetter(
				"hour10", ComponentType.BUTTON, 
				22, 400, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num1_1, R.drawable.time_background, "", ScaleType.FIT_XY
			),
			new TabComponentPropertySetter(
				"hour1", ComponentType.BUTTON, 
				92, 400, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num3_1, R.drawable.time_background, "", ScaleType.FIT_XY
			),

			// ------------- MINUTE
			new TabComponentPropertySetter(
				"minute10", ComponentType.BUTTON, 
				167, 390, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num4_1, R.drawable.time_background, "", ScaleType.FIT_XY
			),
			new TabComponentPropertySetter(
				"minute1", ComponentType.BUTTON, 
				237, 375, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num6_1, R.drawable.time_background, "", ScaleType.FIT_XY
			),
			// --------------------- SECOND
			new TabComponentPropertySetter(
				"sec10", ComponentType.BUTTON, 
				303, 390, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num8_1, R.drawable.time_background, "", ScaleType.FIT_XY 
			),
			new TabComponentPropertySetter(
				"sec1", ComponentType.BUTTON, 
				373, 375, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num1_1, R.drawable.time_background, "", ScaleType.FIT_XY
			),			
//			// --------------------- PLAY
//			new TabComponentPropertySetter(
//				"playbutton", ComponentType.BUTTON, 
//				132, 215, 80, 100
//				, null, R.drawable.play_button_image, "", ScaleType.FIT_XY
//			),
			// --------------------- STOP
			new TabComponentPropertySetter(
				"stopbutton", ComponentType.BUTTON, 
				150, 590, 200, 100
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
				, null, null, "", ScaleType.FIT_XY
			),
			// --------------------- REPEAT
			new TabComponentPropertySetter(
				"repeatbutton", ComponentType.BUTTON, 
				200, 690, 100, 100
				, null, null, "", ScaleType.FIT_XY
			),
		};
		
		// �w�i�摜�͂Ȃ���setActivate�̒S���Ȃ̂ŁA�����ł͒ǉ����Ȃ�
		
		// �{�^���̃n���h���N���X���쐬
		// �����炭�A�N���X�Ɏ�������������
		Button btns[] = {
			DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			//,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			//,DroidWidgetKit.getInstance().MakeButton()
			//,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
		};
		
		// ---- action
		// Stop�{�^��
		HashMap< Integer, IViewAction > actMapStop 
			= new HashMap< Integer, IViewAction >();
		actMapStop.put( IViewAction.ACTION_ID_ONCLICK, new MediaStopAction() );
		// twitter�{�^��
		HashMap< Integer, IViewAction > actMapTwitter
			= new HashMap< Integer, IViewAction >();
		actMapTwitter.put( IViewAction.ACTION_ID_ONCLICK, new TweetAction() );
//		// Play�{�^��
//		HashMap< Integer, IViewAction > actMapPlay 
//		= new HashMap< Integer, IViewAction >();
//		actMapPlay.put( IViewAction.ACTION_ID_ONCLICK, new MediaPlayAction() );
//		// next�{�^��
//		HashMap< Integer, IViewAction > actMapNext
//			= new HashMap< Integer, IViewAction >();
//		actMapNext.put( IViewAction.ACTION_ID_ONCLICK, new NextAction() );
//		// back�{�^��
//		HashMap< Integer, IViewAction > actMapBack
//			= new HashMap< Integer, IViewAction >();
//		actMapBack.put( IViewAction.ACTION_ID_ONCLICK, new PrevAction() );
//		// shuffle�{�^��
//		HashMap< Integer, IViewAction > actMapShuffle
//			= new HashMap< Integer, IViewAction >();
//		actMapShuffle.put( IViewAction.ACTION_ID_ONCLICK, new ShuffleAction() );
//		// repeat�{�^��
//		HashMap< Integer, IViewAction > actMapRepeat
//			= new HashMap< Integer, IViewAction >();
//		actMapRepeat.put( IViewAction.ACTION_ID_ONCLICK, new RepeatAction() );
		
		TabComponentActionSetter actionSetterCont[] = {
			null
			,null
			,null
			,null
			,null
			,null
			//,new TabComponentActionSetter( actMapPlay )
			,new TabComponentActionSetter( actMapStop )
			,new TabComponentActionSetter( actMapTwitter )
			//,new TabComponentActionSetter( actMapNext )
			//,new TabComponentActionSetter( actMapBack )
			,null
			,null
//			,new TabComponentActionSetter( actMapShuffle )
//			,new TabComponentActionSetter( actMapRepeat )
		};
		
		
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		int i=0;
		for( Button btn : btns )
		{
			btn.acceptConfigurator(creationData[i]);
			// TODO:�{�^���̃A�N�V������ݒ�
			if( actionSetterCont[i] != null )
			{
				btn.acceptConfigurator(actionSetterCont[i]);
			}
			
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			addChild( btn );
			// �{�^����z�u
			// ����́AsetActivate�ōs��
			// componentContainer.addView( btn.getView() );
			i++;
		}
		
		//////////////////////// image /////////////////////
		TabComponentPropertySetter creationDataImg[] = {
			new TabComponentPropertySetter(
				"sep", ComponentType.IMAGE, 
				157, 380, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num8_1, null, "", ScaleType.FIT_XY // TODO:�Z�p���[�^�̉摜�ɕύX����
			),
			new TabComponentPropertySetter(
				"sep", ComponentType.IMAGE, 
				293, 405, 
				OkosamaMediaPlayerActivity.TIMECHAR_WIDTH, 
				OkosamaMediaPlayerActivity.TIMECHAR_HEIGHT
				, R.drawable.num8_1, null, "", ScaleType.FIT_XY // TODO:�Z�p���[�^�̉摜�ɕύX����
			)
		};
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
			pageContainer.setBackgroundColor(Color.rgb(120, 120, 180));
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
