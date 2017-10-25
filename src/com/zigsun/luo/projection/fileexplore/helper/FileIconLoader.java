package com.zigsun.luo.projection.fileexplore.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.zigsun.luo.projection.utils.Util;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Luo on 2015/5/13.
 */
public class FileIconLoader implements Handler.Callback {

    /**
     * A soft cache for image thumbnails. the key is file path
     */
    private final static ConcurrentHashMap<String, ImageHolder> mImageCache = new ConcurrentHashMap<String, ImageHolder>();
    /**
     * A map from ImageView to the corresponding photo ID. Please note that this
     * photo ID may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<ImageView, FileId> mPendingRequests = new ConcurrentHashMap<ImageView, FileId>();


    public static class FileId {
        public String mPath;


        public FileCategoryHelper.FileCategory mCategory;

        public FileId(String path, FileCategoryHelper.FileCategory cate) {
            mPath = path;
            mCategory = cate;
        }
    }

    private final Context mContext;
    private final IconLoadFinishListener iconLoadListener;
    /**
     * Flag indicating if the image loading is paused.
     */
    private boolean mPaused;

    /**
     * Constructor.
     *
     * @param context content context
     */
    public FileIconLoader(Context context, IconLoadFinishListener l) {
        mContext = context;
        iconLoadListener = l;
    }

    public interface IconLoadFinishListener {
        void onIconLoadFinished(ImageView view);
    }

    private static abstract class ImageHolder {
        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;

        public static ImageHolder create(FileCategoryHelper.FileCategory cate) {
            switch (cate) {
                case Apk:
                    return new DrawableHolder();
                case Picture:
                case Video:
                    return new BitmapHolder();
            }

            return null;
        }

        ;

        public abstract boolean setImageView(ImageView v);

        public abstract boolean isNull();

        public abstract void setImage(Object image);
    }

    private static class BitmapHolder extends ImageHolder {
        SoftReference<Bitmap> bitmapRef;

        @Override
        public boolean setImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            v.setImageBitmap(bitmapRef.get());
            return true;
        }

        @Override
        public boolean isNull() {
            return bitmapRef == null;
        }

        @Override
        public void setImage(Object image) {
            bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap) image);
        }
    }

    private static class DrawableHolder extends ImageHolder {
        SoftReference<Drawable> drawableRef;

        @Override
        public boolean setImageView(ImageView v) {
            if (drawableRef.get() == null)
                return false;

            v.setImageDrawable(drawableRef.get());
            return true;
        }

        @Override
        public boolean isNull() {
            return drawableRef == null;
        }

        @Override
        public void setImage(Object image) {
            drawableRef = image == null ? null : new SoftReference<Drawable>((Drawable) image);
        }
    }

    /**
     * Load photo into the supplied image view. If the photo is already cached,
     * it is displayed immediately. Otherwise a request is sent to load the
     * photo from the database.
     *
     */
    public boolean loadIcon(ImageView view, String path, FileCategoryHelper.FileCategory cate) {
        boolean loaded = loadCachedIcon(view, path, cate);
        if (loaded) {
            mPendingRequests.remove(view);
        } else {
            FileId p = new FileId(path, cate);
            mPendingRequests.put(view, p);
            if (!mPaused) {
                // Send a request to start loading photos
                requestLoading();
            }
        }
        return loaded;
    }

    /**
     * Handler for messages sent to the UI thread.
     */
    private final Handler mMainThreadHandler = new Handler(this);

    /**
     * Type of message sent by the UI thread to itself to indicate that some
     * photos need to be loaded.
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;

    /**
     * Type of message sent by the loader thread to indicate that some photos
     * have been loaded.
     */
    private static final int MESSAGE_ICON_LOADED = 2;
    /**
     * Thread responsible for loading photos from the database. Created upon the
     * first request.
     */
    private LoaderThread mLoaderThread;
    private static final String LOADER_THREAD_NAME = "FileIconLoader";


    /**
     * The thread that performs loading of photos from the database.
     */
    private class LoaderThread extends HandlerThread implements Handler.Callback {
        private Handler mLoaderThreadHandler;

        public LoaderThread() {
            super(LOADER_THREAD_NAME);
        }

        /**
         * Sends a message to this thread to load requested photos.
         */
        public void requestLoading() {
            if (mLoaderThreadHandler == null) {
                mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            mLoaderThreadHandler.sendEmptyMessage(0);
        }


        /**
         * Receives the above message, loads photos and then sends a message to
         * the main thread to process them.
         */
        @Override
        public boolean handleMessage(Message msg) {
            Iterator<FileId> iterator = mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                FileId id = iterator.next();
                ImageHolder holder = mImageCache.get(id.mPath);
                if (holder != null && holder.state == ImageHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = ImageHolder.LOADING;
                    switch (id.mCategory) {
                        case Apk:
                            Drawable icon = Util.getApkIcon(mContext, id.mPath);
                            holder.setImage(icon);
                            break;
                        case Picture:
                        case Video:
                            boolean isVideo = id.mCategory == FileCategoryHelper.FileCategory.Video;
                            long mId =getDbId(id.mPath, isVideo);
                            holder.setImage(isVideo ? getVideoThumbnail(mId, null) : getImageThumbnail(mId, id.mPath));
                            break;
                    }

                    holder.state = BitmapHolder.LOADED;
                    mImageCache.put(id.mPath, holder);
                }
            }

            mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
            return true;
        }
        private static final int MICRO_KIND = 3;
        private Bitmap getImageThumbnail(long id, String filePath) {

            if(id==0) {
                Log.e("FileIconLoader", "create image thumbail by self "+filePath);
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
               return ThumbnailUtils.extractThumbnail(bitmap, 96, 96, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            }
            return MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MICRO_KIND, null);
        }

        private Bitmap getVideoThumbnail(long id, String filePath) {
            if(id==0) {
                Log.e("FileIconLoader", "create video thumbail by self "+filePath);
                return ThumbnailUtils.createVideoThumbnail(filePath, MICRO_KIND);
            }
            return MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MICRO_KIND, null);
        }
    }

    public long getDbId(String path, boolean isVideo) {
        String volumeName = "external";
        Uri uri = isVideo ? MediaStore.Video.Media.getContentUri(volumeName) : MediaStore.Images.Media.getContentUri(volumeName);
        String selection = MediaStore.Files.FileColumns.DATA + "=?";

        String[] selectionArgs = new String[] {
                path
        };

        String[] columns = new String[] {
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA
        };

        Cursor c = mContext.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        if (c.moveToNext()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }
    /**
     * Processes requests on the main thread.
     */
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_REQUEST_LOADING: {
                mLoadingRequested = false;
                if (!mPaused) {
                    if (mLoaderThread == null) {
                        mLoaderThread = new LoaderThread();
                        mLoaderThread.start();
                    }

                    mLoaderThread.requestLoading();
                }
                return true;
            }

            case MESSAGE_ICON_LOADED: {
                if (!mPaused) {
                    processLoadedIcons();
                }
                return true;
            }
        }
        return false;
    }
    /**
     * Goes over pending loading requests and displays loaded photos. If some of
     * the photos still haven't been loaded, sends another request for image
     * loading.
     */
    private void processLoadedIcons() {
        Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            ImageView view = iterator.next();
            FileId fileId = mPendingRequests.get(view);
            boolean loaded = loadCachedIcon(view, fileId.mPath, fileId.mCategory);
            if (loaded) {
                iterator.remove();
                iconLoadListener.onIconLoadFinished(view);
            }
        }

        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    /**
     * A gate to make sure we only send one instance of MESSAGE_PHOTOS_NEEDED at
     * a time.
     */
    private boolean mLoadingRequested;

    /**
     * Sends a message to this thread itself to start loading images. If the
     * current view contains multiple image views, all of those image views will
     * get a chance to request their respective photos before any of those
     * requests are executed. This allows us to load images in bulk.
     */
    private void requestLoading() {
        if (!mLoadingRequested) {
            mLoadingRequested = true;
            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
        }
    }

    /**
     * Checks if the photo is present in cache. If so, sets the photo on the
     * view, otherwise sets the state of the photo to
     * {@link BitmapHolder#NEEDED}
     */
    private boolean loadCachedIcon(ImageView view, String path, FileCategoryHelper.FileCategory cate) {
        ImageHolder holder = mImageCache.get(path);

        if (holder == null) {
            holder = ImageHolder.create(cate);
            if (holder == null)
                return false;

            mImageCache.put(path, holder);
        } else if (holder.state == ImageHolder.LOADED) {
            if (holder.isNull()) {
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.setImageView(view)) {
                return true;
            }
        }

        holder.state = ImageHolder.NEEDED;
        return false;
    }
}
