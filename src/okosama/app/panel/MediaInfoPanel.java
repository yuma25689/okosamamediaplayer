package okosama.app.panel;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.tab.TabComponentPropertySetter;
import okosama.app.widget.absWidget;
import android.app.Activity;
import android.view.ViewGroup;

public class MediaInfoPanel extends ControlPanel {
		static MediaInfoPanel instance;
		
		public static void createInstance(Activity activity)
		{
			if( instance == null )
			{
				instance = new MediaInfoPanel(activity);
			}
		}
		public static void deleteInstance()
		{
			removeFromParent();
			instance = null;
			
		}	
		public static MediaInfoPanel getInstance()
		{
			return instance;
		}
		public static void insertToLayout( ViewGroup tabBaseLayout )
		{
			if( instance != null && instance.getView() != null )
			{
				OkosamaMediaPlayerActivity.removeFromParent(instance.getView());

//				if( -1 == tabBaseLayout.indexOfChild(instance.getView()) )
//				{
				tabBaseLayout.addView(instance.getView());
				parent = tabBaseLayout;				
//				}
			}
			else
			{
				LogWrapper.e("error","insert sub control panel");
			}
		}
		public static void removeFromParent() //ViewGroup tabBaseLayout )
		{
			if( instance != null && instance.getView() != null )
			{
				OkosamaMediaPlayerActivity.removeFromParent(instance.getView());

				parent = null;
				
			}
		}

		public MediaInfoPanel(Activity activity) {
			super(activity);
			resetPanelViews(R.layout.panel_layout_scroll);
			TabComponentPropertySetter creationData[] = null;

			if( OkosamaMediaPlayerActivity.dispInfo.isPortrait() )
			{
				//////////////////// button //////////////////////////
//				TabComponentPropertySetter creationDataPort[] = {
//						// --------------------- ARTIST
//						new TabComponentPropertySetter(
//							ControlIDs.TIME_ARTIST_LABEL, null, ComponentType.LABEL, 
//							35, 320, 400, 80
//							, null, drawable.no_image, "", ScaleType.FIT_XY
//						),		
//				};
//				creationData = creationDataPort;
			}
			else
			{
				//////////////////// button //////////////////////////
//				TabComponentPropertySetter creationDataHorz[] = {
//						// --------------------- ARTIST
//						new TabComponentPropertySetter(
//							ControlIDs.TIME_ARTIST_LABEL, null, ComponentType.LABEL, 
//							300, 135, 80, 400
//							, null, drawable.no_image, "", ScaleType.FIT_XY
//						),		
//				};
//				creationData = creationDataHorz;
				
			}
//			absWidget widgets[] = {
//					getNowPlayingArtistLabel()
//					,getNowPlayingAlbumLabel()
//				};
			// ---- action
//			// Time�R���|�[�l���g
//			// shuffle�{�^��
//			SparseArray< IViewAction > actMapShuffle
//				= new SparseArray< IViewAction >();
//			actMapShuffle.put( IViewAction.ACTION_ID_ONCLICK, new ToggleShuffleAction() );
//			// repeat�{�^��
//			SparseArray< IViewAction > actMapRepeat
//				= new SparseArray< IViewAction >();
//			actMapRepeat.put( IViewAction.ACTION_ID_ONCLICK, new CycleRepeatAction() );
	//
//			TabComponentActionSetter actionSetterCont[] = {
//					new TabComponentActionSetter( actMapShuffle )
//					,new TabComponentActionSetter( actMapRepeat )
//				};
			// �{�^�����쐬�A�ʒu�����킹�A�A�N�V������ݒ肵�A���C�A�E�g�ɔz�u
			int i=0;
			for( absWidget widget : widgets )
			{
				widget.acceptConfigurator(creationData[i]);
//				// TODO:�{�^���̃A�N�V������ݒ�
//				if( actionSetterCont[i] != null )
//				{
//					widget.acceptConfigurator(actionSetterCont[i]);
//				}
				
				// �{�^�������̃^�u�q���ڂƂ��Ēǉ�
				addChild( creationData[i].getInternalID(), widget );	
				tabBaseLayout.addView( widget.getView() );
				i++;
			}
			//tabBaseLayout.setBackgroundResource(R.drawable.okosama_app_widget_bg);		
		}

}
