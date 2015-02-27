/*
SoundPlayer.java
Copyright (C) 2015 Lassi Marttala, Maxpower Inc (http://maxp.fi)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package uk.co.onecallcaspian.custom.filesharing;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.onecallcaspian.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @author fizzl
 *
 */
public class SoundPlayer extends LinearLayout {
	private void init(Context context) {
		this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sound_player, this);
    }
	public SoundPlayer(Context context) {
		super(context);
		init(context);
	}
	public SoundPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public SoundPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	@TargetApi(Build.VERSION_CODES.L) 
	public SoundPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		findViews();
		prepareViews();
	}
	
	private void prepareViews() {
		mPlay.setOnClickListener(onPlayClick);
		mPause.setOnClickListener(onPauseClick);
		mStop.setOnClickListener(onStopClick);
		mSeek.setOnSeekBarChangeListener(onSeek);
	}

	private void findViews() {
		mPlay = (ImageView) findViewById(R.id.soundplayer_play);
		mPause = (ImageView) findViewById(R.id.soundplayer_pause);
		mStop = (ImageView) findViewById(R.id.soundplayer_stop);
		mSeek = (SeekBar) findViewById(R.id.soundplayer_seek);
	}
	public void setSound(File file) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		reset();
		mSoundFile = file;
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setDataSource(file.getAbsolutePath());
		mMediaPlayer.setOnCompletionListener(onAudioComplete);
		mMediaPlayer.prepare();
		mMediaPlayer.setOnPreparedListener(onMediaPrepared);
	}
	
	public void reset() {
		if(mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mPlay.setVisibility(View.GONE);
		mPause.setVisibility(View.GONE);
		mStop.setVisibility(View.GONE);
	}

	private OnCompletionListener onAudioComplete = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			reset();
			try {
				setSound(mSoundFile);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	OnPreparedListener onMediaPrepared = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			mSeek.setMax(mp.getDuration());
			mSeek.setProgress(mp.getCurrentPosition());
			mPlay.setVisibility(View.VISIBLE);
			mPause.setVisibility(View.GONE);
			mStop.setVisibility(View.VISIBLE);
		}
	};

	OnClickListener onPlayClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mMediaPlayer.start();
			mPlay.setVisibility(View.GONE);
			mPause.setVisibility(View.VISIBLE);
			mSeekTimer = new Timer();
			mSeekTimer.schedule(new SeekUpdateTimerTask(), 200);
		}
	};
	OnClickListener onPauseClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mMediaPlayer.pause();
			mPlay.setVisibility(View.VISIBLE);
			mPause.setVisibility(View.GONE);
		}
	};
	OnClickListener onStopClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				setSound(mSoundFile);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	};

	OnSeekBarChangeListener onSeek = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(fromUser) {
				mMediaPlayer.seekTo(progress);
			}
		}
	};

	private class SeekUpdateTimerTask extends TimerTask {
		@Override
		public void run() {
			if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				mSeek.setProgress(mMediaPlayer.getCurrentPosition());
				mSeekTimer = new Timer();
				mSeekTimer.schedule(new SeekUpdateTimerTask(), 200);
			}
		}
	};
	
	private Timer mSeekTimer;
	private MediaPlayer mMediaPlayer;
	private ImageView mPlay;
	private ImageView mPause;
	private ImageView mStop;
	private SeekBar mSeek;
	private File mSoundFile;
	private Context context;
}
