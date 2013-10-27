package okosama.app.tab.media;


//import okosama.app.AppStatus;
import okosama.app.ControlDefs;
//import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.AlbumListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * ���y�Đ��^�u
 * @author 25689
 *
 */
public class TabPageAlbum extends TabPage {

	public TabPageAlbum( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// �R���X�g���N�^�ł��̃^�u�̃^�uID��ݒ�
		this.tabId = TABPAGE_ID_ALBUM;
		
		create(R.layout.tab_layout_content_generic_progress);
		// componentContainer.addView(tabButton.getView());
	}
	/* (non-Javadoc)
	 * @see okosama.app.container.ITabComponent#create()
	 */
	@Override
	public int create(int panelLayoutID) {

		resetPanelViews(panelLayoutID);
		RelativeLayout.LayoutParams lp 
		= OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		0, 0
        );
		tabBaseLayout.setLayoutParams(lp);
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel ); 
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTID_ALBUM, ComponentType.LIST_ALBUM, 
				//0, 260, 480, 599
				0, 0//150 + 2 // + 90
				, 480, ControlDefs.LIST_HEIGHT_1//637 + 70//- 90 //599
				, null, null,//R.drawable.tab_1_list_bk, 
				"", ScaleType.FIT_XY
			),
			// --------------------- PROGRESS
			// ���܂������Ȃ��̂Ń��C�A�E�gxml�Œǉ�
//			new TabComponentPropertySetter(
//				ControlIDs.COMMON_PROGRESS, ComponentType.PROGRESS,
//				RelativeLayout.CENTER_HORIZONTAL, //ControlDefs.PROGRESS_INVERSE_SIZE, 
//				RelativeLayout.CENTER_VERTICAL, //ControlDefs.PROGRESS_INVERSE_SIZE,
////				( 480 - ControlDefs.PROGRESS_INVERSE_SIZE ) / 2,
////				( AppStatus.LIST_HEIGHT_1 - ControlDefs.PROGRESS_INVERSE_SIZE ) / 2, 
//				//ControlDefs.PROGRESS_INVERSE_SIZE, 
//				//ControlDefs.PROGRESS_INVERSE_SIZE,
//				//0, 0, 480, AppStatus.LIST_HEIGHT_1
//				null, null, "", ScaleType.FIT_XY
//			)
		};
		List lst = DroidWidgetKit.getInstance().MakeList( new AlbumListBehavior() );
		// okosama.app.widget.ProgressBar prog = DroidWidgetKit.getInstance().MakeProgressBar();
//		absWidget widgets[] = {
//			lst
			//prog
//			,DroidWidgetKit.getInstance().MakeButton()
//		};
		widgets.add(lst);		
		// ---- action
		// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
		creationData[0].setColorBack(Color.WHITE);
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			
			// ���̃^�u�̎q���ڂƂ��Ēǉ�
			tabBaseLayout.addView( widget.getView() );

			i++;
		}
		// lst.getView().setVisibility(View.GONE);//setVisible(false);
		
		//////////////////////// image /////////////////////
		return 0;
	}
}
