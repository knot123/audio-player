package com.audioStreaming;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import wseemann.media.FFmpegMediaMetadataRetriever;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ReactNativeAudioStreamingModule extends ReactContextBaseJavaModule {
    private static final String PARAM_DISPLAY_NOTIFICATION = "showInAndroidNotifications";

    private static final String BUFFERING = "BUFFERING";
    private static final String PAUSED = "PAUSED";
    private static final String PLAYING = "PLAYING";
    private static final String STOPPED = "STOPPED";

    private ReactApplicationContext mReactApplicationContext;
    private MediaPlayer mMediaPlayer;

    private boolean mShouldDisplayNotification = false;
    private int mDuration = 0;
    private int mProgress = 0;
    private String mState = STOPPED;

    public ReactNativeAudioStreamingModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.mReactApplicationContext = reactContext;
        this.mMediaPlayer = new MediaPlayer();
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        this.mReactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private String getState() {
        if (this.mMediaPlayer.isPlaying()) {
            return PLAYING;
        } else {
            return this.mState;
        }
    }

    private MediaPlayer createMediaPlayer(Context context, Uri uri, MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mState = BUFFERING;

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (onPreparedListener != null) {
                mediaPlayer.setOnPreparedListener(onPreparedListener);
                mediaPlayer.prepareAsync();
            } else {
                mediaPlayer.prepare();
            }

            return mediaPlayer;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public String getName() {
        return "ReactNativeAudioStreaming";
    }

    @ReactMethod
    public void play(String streamingURL, ReadableMap options) {
        this.mShouldDisplayNotification = options.getBoolean(PARAM_DISPLAY_NOTIFICATION);

        Uri streamingURI = Uri.parse(streamingURL);
        FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(streamingURL);

        try {
            this.mDuration = Integer.valueOf(mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mediaMetadataRetriever.release();

        this.stop();

        this.mMediaPlayer = this.createMediaPlayer(this.mReactApplicationContext, streamingURI, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                ReactNativeAudioStreamingModule.this.resume();
            }
        });
    }

    @ReactMethod
    public void goBack(Float seconds) {
        try {
            int milliseconds = Math.round(seconds) * 1000;
            int newPosition = this.mMediaPlayer.getCurrentPosition() - milliseconds;

            if (newPosition < 0) {
                newPosition = 0;
            }

            this.seekTo(newPosition);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ReactMethod
    public void goForward(Float seconds) {
        try {
            int milliseconds = Math.round(seconds) * 1000;
            int newPosition = this.mMediaPlayer.getCurrentPosition() + milliseconds;

            if (newPosition > this.mDuration) {
                newPosition = this.mDuration;
            }

            this.seekTo(newPosition);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ReactMethod
    public void seekTo(int newPosition) {
//        try {
//            if (this.mMediaPlayer != null) {
//                this.mMediaPlayer.seekTo(newPosition);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            this.mState = BUFFERING;
//        }
    }

    @ReactMethod
    public void pause() {
        try {
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.pause();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.mState = PAUSED;
        }
    }

    @ReactMethod
    public void resume() {
        try {
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.mState = BUFFERING;
        }
    }

    @ReactMethod
    public void stop() {
        try {
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.stop();
                this.mMediaPlayer.release();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.mState = STOPPED;
        }
    }

    @ReactMethod
    public void destroyNotification() {
    }

    @ReactMethod
    public void getStatus(Callback callback) {
        WritableMap state = Arguments.createMap();
        state.putString("status", this.getState());
        state.putInt("duration", this.mDuration / 1000);
        state.putInt("progress", this.mMediaPlayer.getCurrentPosition() / 1000);
        callback.invoke(null, state);
    }
}
