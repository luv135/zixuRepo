package com.zigsun.mobile.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zigsun.mobile.R;
import com.zigsun.mobile.ui.base.Activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchFriendActivity extends Activity {

    private static final int QR_REQUEST = 1;
    private static final int SEARCH_FINISH = 2;
    @InjectView(R.id.searchEditText)
    EditText searchEditText;
    @InjectView(R.id.searchButton)
    Button searchButton;
    @InjectView(R.id.scanLayout)
    LinearLayout scanLayout;
    private ADDEvent event;

    private class ADDEvent implements View.OnClickListener, TextView.OnEditorActionListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scanLayout:
                    final Intent intent = new Intent(getApplication(), ScanQRActivity.class);
                    startActivityForResult(intent, QR_REQUEST);
                    break;
                case R.id.searchButton:
                    final String text = searchEditText.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        searchUser(text);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.abc_empty, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                searchButton.performClick();
                return true;
            }
            return false;
        }
    }

    private void searchUser(String text) {

        Intent intent = new Intent(SearchFriendActivity.this, FriendResultActivity.class);
        intent.putExtra(FriendResultActivity.USERNAME, text);
        startActivityForResult(intent, SEARCH_FINISH);
//        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_search_friend);
        ButterKnife.inject(this);
        event = new ADDEvent();
        searchButton.setOnClickListener(event);
        scanLayout.setOnClickListener(event);
        searchEditText
                .setOnEditorActionListener(event);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_contacts, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            final Intent intent = new Intent(this, ScanQRActivity.class);
//            startActivityForResult(intent, QR_REQUEST);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEARCH_FINISH:
                    finish();
                    return;
                case QR_REQUEST:
                    final String action = data.getAction();
                    searchUser(action);
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
