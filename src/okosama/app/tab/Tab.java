package okosama.app.tab;

import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPlayAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.widget.Button;
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

	TabPageMedia tabPageMedia = null;
	public TabPageMedia getTabPageMedia()
	{
		return tabPageMedia;
	}
	
	public Tab( String name, LinearLayout ll, RelativeLayout rl )
	{
		this.name = name;
		pageContainer = ll;
		componentContainer = rl;
	}
	
	/**
	 * �^�u�S�̂̍쐬
	 * @return 0:���� 0�ȊO:�ُ�
	 */
	public int create() {
		int errCode = 0;
		TabComponentPropertySetter creationData[] = {
			// --------------------- PLAY
			new TabComponentPropertySetter(
				"playbutton", ComponentType.BUTTON, 
				140, 155 + 2
				, 90, 90
				, null, R.drawable.play_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- NEXT
			new TabComponentPropertySetter(
				"nextbutton", ComponentType.BUTTON, 
				270, 155 + 2, 90, 90
				, null, R.drawable.next_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- PREV
			new TabComponentPropertySetter(
				"prevbutton", ComponentType.BUTTON, 
				10, 155 + 2, 90, 90
				, null, R.drawable.back_button_image, "", ScaleType.FIT_XY
			),
			// --------------------- SHUFFLE
			new TabComponentPropertySetter(
				"shufflebutton", ComponentType.BUTTON, 
				20, 700, 100, 100
				, null, R.drawable.btn_no_repeat_image, "", ScaleType.FIT_XY
			),
			// --------------------- REPEAT
			new TabComponentPropertySetter(
				"repeatbutton", ComponentType.BUTTON, 
				200, 690, 100, 100
				, null, R.drawable.btn_no_shuffle_image, "", ScaleType.FIT_XY
			)
		};
				
		// TODO:�����炭�A�N���X�Ɏ�������������
		// �摜�����������K�v�ɂȂ邩������Ȃ�
		Button btns[] = {
			DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
			,DroidWidgetKit.getInstance().MakeButton()
		};
		
		// Play�{�^��
		HashMap< Integer, IViewAction > actMapPlay 
		= new HashMap< Integer, IViewAction >();
		actMapPlay.put( IViewAction.ACTION_ID_ONCLICK, new MediaPlayAction() );
		// next�{�^��
		HashMap< Integer, IViewAction > actMapNext
			= new HashMap< Integer, IViewAction >();
		actMapNext.put( IViewAction.ACTION_ID_ONCLICK, new NextAction() );
		// back�{�^��
		HashMap< Integer, IViewAction > actMapBack
			= new HashMap< Integer, IViewAction >();
		actMapBack.put( IViewAction.ACTION_ID_ONCLICK, new PrevAction() );

		TabComponentActionSetter actionSetterCont[] = {
			new TabComponentActionSetter( actMapPlay )
			,new TabComponentActionSetter( actMapNext )
			,new TabComponentActionSetter( actMapBack )
		};
		
		int i=0;
		for( Button btn : btns )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns.add(btn);
			btn.acceptConfigurator(creationData[i]);
			// �{�^���̃A�N�V������ݒ�
			if( actionSetterCont[i] != null )
			{
				btn.acceptConfigurator(actionSetterCont[i]);
			}
			// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
			addChild( btn );
			// �{�^����z�u
			componentContainer.addView( btn.getView() );
			i++;
		}
		
		// �^�u�̒ǉ�
		addChild( new TabPagePlay( this, pageContainer, componentContainer ) );
		tabPageMedia = new TabPageMedia( this, pageContainer, componentContainer );
		addChild( tabPageMedia );
		addChild( new TabPageNowPlaylist( this, pageContainer, componentContainer ));
		// �^�u�y�[�W�́AsetCurrentTab��ǂ񂾎��A�A�N�e�B�u�Ȃ��̂����������B
		// �Ȃ����^�u�y�[�W��create�͌Ă�ł͂����Ȃ����ƂɂȂ��Ă��܂����B
		// �܂��Acreate���̃^�uID�͕s���Ȃ̂ŁAsetCurrentTab�͂����ł͌Ă΂��A��ʂɌĂ΂���B
		
		return errCode;
	}
	
	/**
	 * ���݂̃^�u��ݒ肷��
	 * @param tabId
	 */
	public void setCurrentTab(int tabId,boolean save)
	{
        for( ITabComponent c : children ) {
        	if( c instanceof TabPage ) { // �ł�����g�������Ȃ������E�E�E�B
        		c.setActivate( ((TabPage) c).IsEqualTabId(tabId) );
        	}
        }
		// �A�v���P�[�V�����ɑI�����ꂽ�^�u�̉��ID��ݒ肷��
		// ���̏ꏊ�����ł������ǂ����͕s��
        if( save == true )
        {
        	OkosamaMediaPlayerActivity.setCurrentDisplayId(this.name,tabId);
        }
	}

}
