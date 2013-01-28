package org.thoughtcrime.redphone.ui;

import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.CompoundButton;
import org.thoughtcrime.redphone.R;
import org.thoughtcrime.redphone.Release;

/**
 * Manages the audio button displayed on the in-call screen
 *
 * The behavior of this button depends on the availability of headset audio, and changes from being a regular
 * toggle button (enabling speakerphone) to bringing up a model dialog that includes speakerphone, bluetooth,
 * and regular audio options.
 *
 * Based on com.android.phone.InCallTouchUI
 *
 * @author Stuart O. Anderson
 */
public class InCallAudioButton {
  private static final String TAG = InCallAudioButton.class.getName();
  private final CompoundButton mAudioButton;

  public InCallAudioButton(CompoundButton audioButton) {
    mAudioButton = audioButton;
  }


  public void updateAudioButton(InCallControlState inCallControlState) {
    if (Release.DEBUG) log("updateAudioButton()...");

    // The various layers of artwork for this button come from
    // btn_compound_audio.xml.  Keep track of which layers we want to be
    // visible:
    //
    // - This selector shows the blue bar below the button icon when
    //   this button is a toggle *and* it's currently "checked".
    boolean showToggleStateIndication = false;
    //
    // - This is visible if the popup menu is enabled:
    boolean showMoreIndicator = false;
    //
    // - Foreground icons for the button.  Exactly one of these is enabled:
    boolean showSpeakerOnIcon = false;
    boolean showSpeakerOffIcon = false;
    boolean showHandsetIcon = false;
    boolean showBluetoothIcon = false;

    if (inCallControlState.bluetoothEnabled) {
      if (Release.DEBUG) log("- updateAudioButton: 'popup menu action button' mode...");

      mAudioButton.setEnabled(true);

      // The audio button is NOT a toggle in this state.  (And its
      // setChecked() state is irrelevant since we completely hide the
      // btn_compound_background layer anyway.)

      // Update desired layers:
      showMoreIndicator = true;
      if (inCallControlState.bluetoothIndicatorOn) {
        showBluetoothIcon = true;
      } else if (inCallControlState.speakerOn) {
        showSpeakerOnIcon = true;
      } else {
        showHandsetIcon = true;
        // TODO: if a wired headset is plugged in, that takes precedence
        // over the handset earpiece.  If so, maybe we should show some
        // sort of "wired headset" icon here instead of the "handset
        // earpiece" icon.  (Still need an asset for that, though.)
      }
    } else if (inCallControlState.speakerEnabled) {
      if (Release.DEBUG) log("- updateAudioButton: 'speaker toggle' mode...");

      mAudioButton.setEnabled(true);

      // The audio button *is* a toggle in this state, and indicates the
      // current state of the speakerphone.
      mAudioButton.setChecked(inCallControlState.speakerOn);

      // Update desired layers:
      showToggleStateIndication = true;

      showSpeakerOnIcon = inCallControlState.speakerOn;
      showSpeakerOffIcon = !inCallControlState.speakerOn;
    } else {
      if (Release.DEBUG) log("- updateAudioButton: disabled...");

      // The audio button is a toggle in this state, but that's mostly
      // irrelevant since it's always disabled and unchecked.
      mAudioButton.setEnabled(false);
      mAudioButton.setChecked(false);

      // Update desired layers:
      showToggleStateIndication = true;
      showSpeakerOffIcon = true;
    }

    // Finally, update the drawable layers (see btn_compound_audio.xml).

    // Constants used below with Drawable.setAlpha():
    final int HIDDEN = 0;
    final int VISIBLE = 255;

    LayerDrawable layers = (LayerDrawable) mAudioButton.getBackground();
    if (Release.DEBUG) log("- 'layers' drawable: " + layers);

    layers.findDrawableByLayerId(R.id.compoundBackgroundItem)
      .setAlpha(showToggleStateIndication ? VISIBLE : HIDDEN);

    layers.findDrawableByLayerId(R.id.moreIndicatorItem)
      .setAlpha(showMoreIndicator ? VISIBLE : HIDDEN);

    layers.findDrawableByLayerId(R.id.bluetoothItem)
      .setAlpha(showBluetoothIcon ? VISIBLE : HIDDEN);

    layers.findDrawableByLayerId(R.id.handsetItem)
      .setAlpha(showHandsetIcon ? VISIBLE : HIDDEN);

    layers.findDrawableByLayerId(R.id.speakerphoneOnItem)
      .setAlpha(showSpeakerOnIcon ? VISIBLE : HIDDEN);

    layers.findDrawableByLayerId(R.id.speakerphoneOffItem)
      .setAlpha(showSpeakerOffIcon ? VISIBLE : HIDDEN);
  }

  private void log(String msg) {
    Log.d(TAG, msg);
  }

  public static enum AudioMode {
    DEFAULT,
    HEADSET,
    SPEAKER,
  }

  public static interface AudioButtonListener {
    public void onAudioChange(AudioMode mode);
  }

  public void setListener(final AudioButtonListener listener) {
    mAudioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //TODO: Stuart O. Anderson - Add bluetooth support here.
        listener.onAudioChange(b ? AudioMode.SPEAKER : AudioMode.DEFAULT);
      }
    });
  }

}
