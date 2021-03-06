/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package okosama.app;

import okosama.app.storage.Database;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DeleteItems extends Activity
{
	public static final int DELETE_REQUEST_CODE = 444;
	
	public static final int DELETE_NOT_DONE = 0;
	public static final int DELETE_DONE = 1;
	
	public static final String ITEMID_KEY = "items";
	public static final String TYPEID_KEY = "types";
	public static final String TITLE_KEY = "description";
    private TextView mPrompt;
    private Button mButton;
    private long [] mItemList;
    private int [] mTypeList;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_delete);
        getWindow().setLayout(LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT);

        mPrompt = (TextView)findViewById(R.id.prompt);
        mButton = (Button) findViewById(R.id.delete);
        mButton.setOnClickListener(mButtonClicked);

        ((Button)findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	setResult(DELETE_NOT_DONE);
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        String desc = b.getString(TITLE_KEY);
        mItemList = b.getLongArray(ITEMID_KEY);
        mTypeList = b.getIntArray(TYPEID_KEY);
        
        mPrompt.setText(desc);
    }
    
    private View.OnClickListener mButtonClicked = new View.OnClickListener() {
        @Override
		public void onClick(View v) {
            // delete the selected item(s)
            Database.deleteTracks(DeleteItems.this, mItemList, mTypeList);
        	setResult(DELETE_DONE);            
            finish();
        }
    };
}
