package com.zigsun.luo.projection.fileexplore.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PatternMatcher;

import java.util.ArrayList;
import java.util.List;


public class MediaMountReceiver extends BroadcastReceiver {
    public MediaMountReceiver() {
    }

    public static class Helper {
        public static MediaMountReceiver receiver = new MediaMountReceiver();

        public static void register(Activity activity) {
            if (receiver.isRegistered) return;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addDataScheme("file");
            filter.addDataPath("mnt/", PatternMatcher.PATTERN_LITERAL);
            activity.registerReceiver(receiver, filter);
            receiver.isRegistered = true;
        }

        public static void register(Fragment fragment) {
            register(fragment.getActivity());
            try {
                receiver.iMediaState.add((IMediaState) fragment);
            } catch (Exception e) {
                throw new ClassCastException("must implements IMediaState Interface");
            }
        }

        public static void register(android.support.v4.app.Fragment fragment) {
            register(fragment.getActivity());
            try {
                receiver.iMediaState.add((IMediaState) fragment);
            } catch (Exception e) {
                throw new ClassCastException("must implements IMediaState Interface");
            }
        }

        public static void unregister(Activity activity) {
            if (!receiver.isRegistered) return;
            activity.unregisterReceiver(receiver);
            receiver.iMediaState.clear();
            receiver.isRegistered = false;
        }

        public static void unregister(Fragment fragment) {
            unregister(fragment.getActivity());
        }

        public static void unregister(android.support.v4.app.Fragment fragment) {
            unregister(fragment.getActivity());
        }
    }

    private List<IMediaState> iMediaState = new ArrayList<>();


    public interface IMediaState {
        void unMount();

        void mount();
    }

    private boolean isRegistered;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_MEDIA_MOUNTED:
                for (IMediaState state : iMediaState)
                    state.mount();
                break;
            case Intent.ACTION_MEDIA_BAD_REMOVAL:
            case Intent.ACTION_MEDIA_UNMOUNTED:
            case Intent.ACTION_MEDIA_REMOVED:
                for (IMediaState state : iMediaState)
                    state.unMount();
                break;
        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }

}
