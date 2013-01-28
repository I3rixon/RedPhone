package org.thoughtcrime.redphone.ui;

import android.content.Context;
import android.util.Log;
import org.thoughtcrime.redphone.Release;
import org.thoughtcrime.redphone.util.PhoneUtils;

/**
 * Based on com.android.phone.InCallControlState
 *
 * Helper class to keep track of enabledness, visibility, and "on/off"
 * or "checked" state of the various controls available in the in-call
 * UI, based on the current telephony state.
 *
 * This class is independent of the exact UI controls used on any given
 * device.  To avoid cluttering up the "view" code (i.e. InCallTouchUi)
 * with logic about which functions are available right now, we instead
 * have that logic here, and provide simple boolean flags to indicate the
 * state and/or enabledness of all possible in-call user operations.
 *
 * (In other words, this is the "model" that corresponds to the "view"
 * implemented by InCallTouchUi.)
 *
 * @author Stuart O. Anderson
 */

public class InCallControlState {
  private static final String LOG_TAG = "InCallControlState";
  private static final boolean DBG = Release.DEBUG;

  private Context context;

  public boolean canEndCall;
  //
  public boolean bluetoothEnabled;
  public boolean bluetoothIndicatorOn;
  //
  public boolean speakerEnabled;
  public boolean speakerOn;
  //
  public boolean canMute;
  public boolean muteIndicatorOn;

  public InCallControlState(Context cm) {
    if (DBG) log("InCallControlState constructor...");
    context = cm.getApplicationContext();
    update();
  }

  /**
   * Updates all our public boolean flags based on the current state of
   * the Phone.
   */
  public void update() {
    canEndCall = true;

    // "Bluetooth":
    if (PhoneUtils.isBluetoothAvailable()) {
      bluetoothEnabled = true;
      bluetoothIndicatorOn = PhoneUtils.isBluetoothAudioConnectedOrPending();
    } else {
      bluetoothEnabled = false;
      bluetoothIndicatorOn = false;
    }

    // "Speaker": always enabled
    speakerEnabled = true;
    speakerOn = PhoneUtils.isSpeakerOn(context);

    // Mute is also always enabled
    canMute = true;
    muteIndicatorOn = PhoneUtils.getMute(context);
    if (DBG) dumpState();
  }

  public void dumpState() {
    log("InCallControlState:");
    log("  canEndCall: " + canEndCall);
    log("  bluetoothEnabled: " + bluetoothEnabled);
    log("  bluetoothIndicatorOn: " + bluetoothIndicatorOn);
    log("  speakerEnabled: " + speakerEnabled);
    log("  speakerOn: " + speakerOn);
    log("  canMute: " + canMute);
    log("  muteIndicatorOn: " + muteIndicatorOn);
  }

  private void log(String msg) {
    Log.d(LOG_TAG, msg);
  }
}