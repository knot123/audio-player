package br.com.arauk.reactnative.audioplayer;

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
public class ReactNativeAudioPlayerModule extends ReactContextBaseJavaModule {
    private static final String PARAM_DISPLAY_NOTIFICATION = "showInAndroidNotifications";

    private static final String BUFFERING = "BUFFERING";
    private static final String COMPLETED = "COMPLETED";
    private static final String PAUSED = "PAUSED";
    private static final String PLAYING = "PLAYING";
    private static final String STOPPED = "STOPPED";

    private ReactApplicationContext mReactApplicationContext;
    private MediaPlayerStateWrapper mMediaPlayer;

    private int mDuration = 0;
    private int mProgress = 0;
    private String mState = STOPPED;

    public ReactNativeAudioPlayerModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.mReactApplicationContext = reactContext;
        this.mMediaPlayer = new MediaPlayerStateWrapper();
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

    private MediaPlayerStateWrapper createMediaPlayer(Context context, Uri uri, MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mState = BUFFERING;

        try {
            MediaPlayerStateWrapper mediaPlayer = new MediaPlayerStateWrapper();
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
        return "ReactNativeAudioPlayer";
    }

    @ReactMethod
    public void play(String streamingURL, ReadableMap options, Callback callback) {
        try {
            Uri streamingURI = Uri.parse(streamingURL);

            FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(streamingURL);
            this.mDuration = Integer.valueOf(mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
            mediaMetadataRetriever.release();

            this.stop();

            this.mMediaPlayer = this.createMediaPlayer(this.mReactApplicationContext, streamingURI, new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    ReactNativeAudioPlayerModule.this.resume();
                }
            });
            this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ReactNativeAudioPlayerModule.this.mState = COMPLETED;
                }
            });

            callback.invoke("OK");
        } catch (Exception ex) {
            ex.printStackTrace();
            callback.invoke("ERROR");
        }
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
        try {
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.seekTo(newPosition);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.mState = BUFFERING;
        }
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
        state.putInt("duration", this.mMediaPlayer.getDuration() / 1000);
        state.putInt("progress", this.mMediaPlayer.getCurrentPosition() / 1000);
        callback.invoke(null, state);
    }
}
