package com.zigsun.luo.voicetest;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zigsun.mobile.R;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.core.MsgMgr;
import com.zigsun.luo.voicetest.message.AudioCallAcceptRejectMessage;

import de.greenrobot.event.EventBus;

public class VoiceTestActivity extends Activity {

    public static final String USERID = "USERID";
    private static final String TAG = VoiceTestActivity.class.getSimpleName();
    private String logString;
    private AudioRecord mAudioRecord;
    private boolean mAudioRecordReleased;
    private int mMinRecordBufSize;
    private RecordAudioThread mRecordAudioThread;
    private boolean mRecordThreadExitFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_test);
        Intent intent = getIntent();
        final long userId = intent.getLongExtra(USERID, -1);

        if (userId == -1) {
            Toast.makeText(this, "user id error ", Toast.LENGTH_SHORT).show();
            finish();
        }
        ClientSessMgr.CSMPutMsgAudioCall(userId);
        EventBus.getDefault().register(this);
        logWrite("userId=" + userId);
        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAudioRecordReleased = true;
    }

    // 初始化音频采集设备
    public int InitAudioRecorder(int profile) {
        if (mAudioRecord != null)
            return 0;
        Log.d(TAG, "InitAudioRecorder, profile: " + profile);
        int channel, samplerate, samplebit;
        // 根据上层设定的profile来配置参数
        if (profile == 1) {
            samplerate = 16000;
            channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
            samplebit = AudioFormat.ENCODING_PCM_16BIT;
        } else if (profile == 2) {
            samplerate = 44100;
            channel = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
            samplebit = AudioFormat.ENCODING_PCM_16BIT;
        } else {
            return -1;
        }
        try {
            mAudioRecordReleased = false;
            // 获得构建对象的最小缓冲区大小
            mMinRecordBufSize = AudioRecord.getMinBufferSize(samplerate,
                    channel, samplebit);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    samplerate, channel, samplebit, mMinRecordBufSize);

            // 设置AnyChat的外部音频输入参数
            // AnyChatCoreSDK.SetInputAudioFormat(mAudioRecord.getChannelCount(),
            // mAudioRecord.getSampleRate(), 16, 0);

            if (mRecordAudioThread == null) {
                mRecordThreadExitFlag = false;
                mRecordAudioThread = new RecordAudioThread();
                mRecordAudioThread.start();
            }
            Log.d(TAG, "mMinRecordBufSize = " + mMinRecordBufSize);
        } catch (Exception e) {
            return -1;
        }
        return 0;
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
                Log.d(TAG, "Set record thread priority failed: "
                        + e.getMessage());
            }
            mAudioRecord.startRecording();
            Log.d(TAG, "audio record....");
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
            Log.d(TAG, "audio record stop....");
        }

    }

    private void logWrite(String log) {
        logString += "\n" + log;
        ((TextView) findViewById(R.id.logTextView)).setText(logString);
    }

    public void onEvent(AudioCallAcceptRejectMessage call) {
        logWrite("userid: " + call.getUlUserID() + " is accept: " + call.isAccept());
        if (call.isAccept()) {
            InitAudioRecorder(1);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voice_test, menu);
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
