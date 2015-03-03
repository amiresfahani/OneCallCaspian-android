/*
ChatMessageFileState.java
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

/**
 * @author fizzl
 *
 */
public class ChatMessageFileState {
	public static final int STATE_STOP = 1;
	public static final int STATE_PLAY = 2;
	public static final int STATE_PAUSE = 3;

	public int getPlaying() {
		return playing;
	}
	public void setPlaying(int playing) {
		this.playing = playing;
	}
	public int getSeekPosition() {
		return seekPosition;
	}
	public void setSeekPosition(int seekPosition) {
		this.seekPosition = seekPosition;
	}

	private int playing = STATE_STOP;
	private int seekPosition = 0;
}
