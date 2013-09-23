package okosama.app.tab;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.action.IViewAction;
import okosama.app.action.MediaPlayPauseAction;
import okosama.app.action.NextAction;
import okosama.app.action.PrevAction;
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

	public Tab( String name, LinearLayout ll, ViewGroup rl )
	{
		this.name = name;
		pageContainer = ll;
		componentContainer = rl;
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
		
		// �^�u�̒ǉ�
		addChild( TabPage.TABPAGE_ID_PLAY, new TabPagePlay( this, pageContainer, tabBaseLayout ) );
		addChild( TabPage.TABPAGE_ID_MEDIA, new TabPageMedia( this, pageContainer, tabBaseLayout ); );
		addChild( TabPage.TABPAGE_ID_PLAY, new TabPageNowPlaylist( this, pageContainer, tabBaseLayout ));
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
        	if( c instanceof TabPage ) {
        		// ��x�S�Ẵ^�u�̑I��������
        		c.setActivate( false );
        	}
        	// �w��̃^�uID�̃^�u�����A�s���|�C���g�őI������
        	if( c instanceof TabPage ) { // �ł�����g�������Ȃ������E�E�E�B
        		if( ((TabPage) c).IsEqualTabId(tabId) == true )
        		{
        			c.setActivate( true );
        		}
        	}
        }
        for( ITabComponent c : children ) {
        	if( c instanceof TabPage ) { // �ł�����g�������Ȃ������E�E�E�B
        		((TabPage) c).setTabButtonToFront();
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
