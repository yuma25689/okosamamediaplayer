package okosama.app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;

public class TimerAlertDialog extends AlertDialog {
	
	protected TimerAlertDialog(Context context) {
		super(context);
	}
	
    public static class Builder extends AlertDialog.Builder {
        private Handler mHandler = new Handler();   // ハンドラー
        private int mShowTime = 3000;
        private int mCount = 3;
        private Runnable mRunDismiss;
        private Runnable mRunCountdown;
        private CharSequence mMessage;
        private AlertDialog mDlg;
        
    	
//        // ////////////////////////////////////////////////////////////
//        // カウントダウンしたい期間を設定する
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
        // カウントダウンを更新する
        public void updateCountdown(CharSequence messageFmt) {
            this.mCount--;
            if( messageFmt != null && mDlg != null)
            {
            	String sMessage = new String(messageFmt.toString());
            	mDlg.setMessage(String.format(sMessage, mCount));
            }
        }
        
        // ////////////////////////////////////////////////////////////
        // ダイアログの表示開始
        @Override
        public AlertDialog show() {
            final TimerAlertDialog.Builder dlgBlde = this;
        	String sMessage = new String(mMessage.toString());
            mDlg = super.show();
            mDlg.setMessage(String.format(sMessage, mCount));
            
            // ダイアログを削除するためのモノ
            this.mRunDismiss = new Runnable() {
                public void run() {
                	mDlg.dismiss();
                }
            };
             
            // 一定期間ごとに表示されるタイマーを更新するためのモノ
            this.mRunCountdown = new Runnable() {
                public void run() {
                	dlgBlde.updateCountdown(mMessage);
                    // 一定期間ごとにカウントダウン
                    dlgBlde.mHandler.postDelayed(dlgBlde.mRunCountdown, 1000);
                }
            };
            // しばらくまってから実行
            this.mHandler.postDelayed(this.mRunDismiss, this.mShowTime + 1000);
            this.mHandler.postDelayed(this.mRunCountdown, 1000);
            
            return mDlg;
        }
  
    }     
 
}
