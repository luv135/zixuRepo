package com.zigsun.luo.voicetest;

import android.app.Activity;
import android.media.AudioRecord;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.core.MsgMgr;
import com.zigsun.util.log.BaseLog;

@Deprecated
public class VoceTransActivity extends Activity {

    private TextView logTextView;
    private AudioRecord mAudioRecord;
    private boolean mRecordThreadExitFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voce_trans);
        logTextView = (TextView) findViewById(R.id.textView);
        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordThreadExitFlag = true;
            }
        });
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    /*
     * 音频采集线程
	 */
    class RecordAudioThread extends Thread {
        @Override
        public void run() {
            if (mAudioRecord == null)
                return;
            try {
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            } catch (Exception e) {
                BaseLog.print("Set record thread priority failed: "
                        + e.getMessage());
            }
            mAudioRecord.startRecording();
            BaseLog.print("audio record....");
            byte[] recordbuf = new byte[640];
            while (!mRecordThreadExitFlag) {
                try {
                    int ret = mAudioRecord.read(recordbuf, 0, recordbuf.length);
                    if (ret == AudioRecord.ERROR_INVALID_OPERATION
                            || ret == AudioRecord.ERROR_BAD_VALUE)
                        break;
                    // 通过外部音频输入接口将音频采样数据传入内核
                    MsgMgr.MTAVAudioCaptureNotifyFrame(
                            recordbuf, recordbuf.length);
                } catch (Exception e) {
                    break;
                }
            }
            BaseLog.print("audio record stop....");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voce_trans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.abc_action_add_contact) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
