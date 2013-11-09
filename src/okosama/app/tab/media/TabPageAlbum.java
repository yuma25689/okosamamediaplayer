package okosama.app.tab.media;


//import okosama.app.AppStatus;
import okosama.app.ControlDefs;
import okosama.app.ControlIDs;
//import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.AlbumListBehavior;
import okosama.app.behavior.TrackListBehavior;
import okosama.app.factory.DroidWidgetKit;
import okosama.app.tab.Tab;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.tab.TabComponentPropertySetter.ComponentType;
import okosama.app.tab.TabPage;
import okosama.app.widget.Button;
import okosama.app.widget.List;
import okosama.app.widget.absWidget;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 音楽再生タブ
 * @author 25689
 *
 */
public class TabPageAlbum extends TabPage {

	public TabPageAlbum( Tab parent, LinearLayout ll, ViewGroup rl ) {
		super();
		this.parent = parent;
		this.pageContainer = ll;
		this.componentContainer = rl;
		// コンストラクタでこのタブのタブIDを設定
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
		View v = tabBaseLayout.findViewById(R.id.top_info_bar);
//		TextView line1 = (TextView) v.findViewById(R.id.line1);
//		TextView line2 = (TextView) v.findViewById(R.id.line2);
//	    ImageView play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
	    ImageView icon = (ImageView) v.findViewById(R.id.icon);
	    BitmapDrawable albumIcon 
	    =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
	    		android.R.drawable.divider_horizontal_dark );
	    		// R.drawable.albumart_mp_unknown_list);
	    albumIcon.setFilterBitmap(false);
	    albumIcon.setDither(false);
	    icon.setBackgroundDrawable(albumIcon);
		updateProgressPanel = (ViewGroup)tabBaseLayout.findViewById(R.id.TabCommonProgressPanel );
		RelativeLayout.LayoutParams lpList 
		= new RelativeLayout.LayoutParams(//OkosamaMediaPlayerActivity.createLayoutParamForAbsolutePosOnBk( 
        		//0, 0, 
        		RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
        );
		lpList.addRule(RelativeLayout.BELOW,R.id.top_info_bar);
		//////////////////// list //////////////////////////
		TabComponentPropertySetter creationData[] = {
			// ------------- TAB
			new TabComponentPropertySetter(
				List.LISTID_ALBUM, ComponentType.LIST_ALBUM, 
				//0, 260, 480, 599
				lpList
//				0, 0,//150 + 2 // + 90
//				RelativeLayout.LayoutParams.FILL_PARENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT
				//, 480, ControlDefs.LIST_HEIGHT_1//637 + 70//- 90 //599
				, null, null,//R.drawable.tab_1_list_bk, 
				"", ScaleType.FIT_XY
			),
		};
		List lst = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getList(List.LISTID_ALBUM);
		if( lst == null )
		{
			lst = DroidWidgetKit.getInstance().MakeList( new AlbumListBehavior() );
			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setList(List.LISTID_ALBUM,lst);
		
		// okosama.app.widget.ProgressBar prog = DroidWidgetKit.getInstance().MakeProgressBar();
//		absWidget widgets[] = {
//			lst
			//prog
//			,DroidWidgetKit.getInstance().MakeButton()
//		};
			widgets.add(lst);
		}
		
		// ---- action
		// ボタンを作成、位置を合わせ、アクションを設定し、レイアウトに配置
		creationData[0].setColorBack(Color.WHITE);
		int i=0;
		for( absWidget widget : widgets )
		{
			widget.acceptConfigurator(creationData[i]);
			
			// このタブの子項目として追加
			tabBaseLayout.addView( widget.getView() );

			i++;
		}
		// lst.getView().setVisibility(View.GONE);//setVisible(false);
		
		//////////////////////// image /////////////////////
		return 0;
	}
}
