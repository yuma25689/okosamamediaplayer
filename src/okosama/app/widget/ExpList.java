package okosama.app.widget;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.IExpListBehavior;
import android.app.Activity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class ExpList extends absWidget {
	
	public static int LISTID_ARTIST = 300;
	
	/**
	 * �����N���X
	 */
	private ExpListImpl impl;

	public ExpList( Activity activity, IExpListBehavior behavior )
	{
		super( activity );
		create();
		setBehavior( behavior );
	}

	@Override
	public int create() {
		impl = new ExpListImpl(activity);
		impl.setDivider(OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.list_divider_tesuri));
		impl.setFastScrollEnabled(true);
		
		impl.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// �N���b�N���̏���
				behavior.onItemClick( parent, v, groupPosition, childPosition, id);
				return false;
			}
		});
		// TODO: ����Axml�̐؂�ւ��̉摜�͒ʏ탍�[�h�����A�ł����玩�O���\�b�h�ŉ掿���������ă��[�h������
		impl.setGroupIndicator(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getResources().getDrawable(R.drawable.selector_exp_ind));
				//OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
				//		R.drawable.exp_ind));
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
