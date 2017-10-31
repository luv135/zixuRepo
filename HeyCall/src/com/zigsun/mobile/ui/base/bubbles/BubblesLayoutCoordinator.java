/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.zigsun.mobile.ui.base.bubbles;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

final class BubblesLayoutCoordinator {
    private static final String TAG = BubblesLayoutCoordinator.class.getSimpleName();
    private static BubblesLayoutCoordinator INSTANCE;
//    private BubbleTrashLayout trashView;
    private List<BubbleTrashLayout> trashViews;
    private WindowManager windowManager;

    private static BubblesLayoutCoordinator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BubblesLayoutCoordinator();
        }
        return INSTANCE;
    }

    private BubblesLayoutCoordinator() {
        trashViews = new ArrayList<>();}

    public void notifyBubblePositionChanged(BubbleLayout bubble, int x, int y) {
        for(BubbleTrashLayout trashView: trashViews) {

                trashView.setVisibility(View.VISIBLE);
                if (checkIfBubbleIsOverTrash(trashView, bubble)) {
                    trashView.applyMagnetism();
                    applyTrashMagnetismToBubble(trashView,bubble);
                } else {
                    trashView.releaseMagnetism();
                }
            }
    }

//    private boolean checkIfBubbleIsOverTrash(BubbleTrashLayout trashView, BubbleLayout bubble) {
//        return false;
//    }

    private void applyTrashMagnetismToBubble(BubbleTrashLayout trashContentView, BubbleLayout bubble) {
//        View trashContentView = getTrashContent();
        int trashCenterX = (trashContentView.getLeft() + (trashContentView.getMeasuredWidth() / 2));
        int trashCenterY = (trashContentView.getTop() + (trashContentView.getMeasuredHeight() / 2));
//        int x = (trashCenterX - (bubble.getMeasuredWidth() / 2));
//        int y = (trashCenterY - (bubble.getMeasuredHeight() / 2));

//        int x = (trashCenterX - (bubble.getMeasuredWidth() / 2));
//        int y = (trashCenterY - (bubble.getMeasuredHeight() / 2));

        int tx = trashContentView.getLeft()-bubble.getLeft();
        int ty = trashContentView.getTop()-bubble.getTop();
        bubble.setTranslationX(tx);
        bubble.setTranslationY(ty);



//        bubble.setLeft(x);

//        bubble.setTranslationX(x);
//        bubble.setTop(y);
//        bubble.setTranslationY(y);

        Log.d(TAG, "applyTrashMagnetismToBubble");
//        windowManager.updateViewLayout(bubble, bubble.getViewParams());
    }

    private boolean checkIfBubbleIsOverTrash(BubbleTrashLayout trashContentView, BubbleLayout bubble) {
        boolean result = false;
        if (trashContentView.getVisibility() == View.VISIBLE) {




            int trashWidth = trashContentView.getMeasuredWidth();
            int trashHeight = trashContentView.getMeasuredHeight();
            int trashLeft = (trashContentView.getLeft() - (trashWidth / 2));
            int trashRight = (trashContentView.getLeft() + trashWidth + (trashWidth / 2));
            int trashTop = (trashContentView.getTop() - (trashHeight / 2));
            int trashBottom = (trashContentView.getTop() + trashHeight + (trashHeight / 2));
          

            int bubbleWidth = bubble.getMeasuredWidth();
            int bubbleHeight = bubble.getMeasuredHeight();
            int bubbleLeft = bubble.getLeft();
            int bubbleRight = bubbleLeft + bubbleWidth;
            int bubbleTop = bubble.getTop();
            int bubbleBottom = bubbleTop + bubbleHeight;



            bubbleLeft += bubble.getTranslationX();
            bubbleRight += bubble.getTranslationX();
            bubbleTop += bubble.getTranslationY();
            bubbleBottom += bubble.getTranslationY();


            if (bubbleLeft >= trashLeft && bubbleRight <= trashRight) {
                if (bubbleTop >= trashTop && bubbleBottom <= trashBottom) {
                    result = true;
                }
            }
        }
        return result;
    }

    public boolean notifyBubbleRelease(BubbleLayout bubble) {
        for(BubbleTrashLayout trashView: trashViews) {
            if (trashView != null) {
                if (checkIfBubbleIsOverTrash(trashView,bubble)) {
                    if(trashView.hasOnClickListeners()) {
                        trashView.performClick();
                        return true;
                    }
//                bubblesService.removeBubble(bubble);
                }
                trashView.setVisibility(View.INVISIBLE);
            }
        }
        return false;
    }

    public static class Builder {
        private BubblesLayoutCoordinator layoutCoordinator;

        public Builder(/*BubblesService service*/) {
            layoutCoordinator = getInstance();
//            layoutCoordinator.bubblesService = service;
        }

        public Builder setTrashView(BubbleTrashLayout trashView) {
//            layoutCoordinator.trashView = trashView;
            return this;
        }
        public Builder setTrashViews(List<BubbleTrashLayout> trashViews) {
            layoutCoordinator.trashViews = trashViews;
            return this;
        }

        public Builder setWindowManager(WindowManager windowManager) {
            layoutCoordinator.windowManager = windowManager;
            return this;
        }

        public BubblesLayoutCoordinator build() {
            return layoutCoordinator;
        }
    }

//    private View getTrashContent() {
//        return trashView;
//    }
}
