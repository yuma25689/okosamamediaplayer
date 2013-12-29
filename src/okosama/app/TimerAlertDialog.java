package okosama.app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;

public class TimerAlertDialog extends AlertDialog {
	
	protected TimerAlertDialog(Context context) {
		super(context);
	}
	
    public static class Builder extends AlertDialog.Builder {
        private Handler mHandler = new Handler();   // �n���h���[
        private int mShowTime = 3000;
        private int mCount = 3;
        private Runnable mRunDismiss;
        private Runnable mRunCountdown;
        private CharSequence mMessage;
        private AlertDialog mDlg;
        
    	
//        // ////////////////////////////////////////////////////////////
//        // �J�E���g�_�E�����������Ԃ�ݒ肷��
//        public void setShowTime(int time) {
//            this.mShowTime = time;
//            this.mCount = time / 1000;
//        }

        public Builder(Context context) {
            super(context);
        }
        @Override
        public AlertDialog.Builder setMessage(CharSequence message)
        {
        	mMessage = message;
        	return super.setMessage(message);
        }
        // ////////////////////////////////////////////////////////////
        // �J�E���g�_�E�����X�V����
        public void updateCountdown(CharSequence messageFmt) {
            this.mCount--;
            if( messageFmt != null && mDlg != null)
            {
            	String sMessage = new String(messageFmt.toString());
            	mDlg.setMessage(String.format(sMessage, mCount));
            }
        }
        
        // ////////////////////////////////////////////////////////////
        // �_�C�A���O�̕\���J�n
        @Override
        public AlertDialog show() {
            final TimerAlertDialog.Builder dlgBlde = this;
        	String sMessage = new String(mMessage.toString());
            mDlg = super.show();
            mDlg.setMessage(String.format(sMessage, mCount));
            
            // �_�C�A���O���폜���邽�߂̃��m
            this.mRunDismiss = new Runnable() {
                public void run() {
                	mDlg.dismiss();
                }
            };
             
            // �����Ԃ��Ƃɕ\�������^�C�}�[���X�V���邽�߂̃��m
            this.mRunCountdown = new Runnable() {
                public void run() {
                	dlgBlde.updateCountdown(mMessage);
                    // �����Ԃ��ƂɃJ�E���g�_�E��
                    dlgBlde.mHandler.postDelayed(dlgBlde.mRunCountdown, 1000);
                }
            };
            // ���΂炭�܂��Ă�����s
            this.mHandler.postDelayed(this.mRunDismiss, this.mShowTime + 1000);
            this.mHandler.postDelayed(this.mRunCountdown, 1000);
            
            return mDlg;
        }
  
    }     
 
}
