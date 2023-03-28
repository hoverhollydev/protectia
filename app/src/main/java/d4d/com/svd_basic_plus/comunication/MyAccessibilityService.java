package d4d.com.svd_basic_plus.comunication;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final int eventType = accessibilityEvent.getEventType();
        String eventText = null;
        switch(eventType) {
            /*
                You can use catch other events like touch and focus

                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                     eventText = "Clicked: ";
                     break;
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                     eventText = "Focused: ";
                     break;
            */
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventText = "Typed: ";
                break;
        }
        eventText = eventText + accessibilityEvent.getText();

        //print the typed text in the console. Or do anything you want here.
        System.out.println("ACCESSIBILITY SERVICE : "+eventText);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        //handle keyevent for widnows key
        Log.i("event.getKeyCode()", "event.getKeyCode() "+event.getKeyCode());
        if((event.getKeyCode() == KeyEvent.KEYCODE_META_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_META_RIGHT)) {
            //Send broadcast intent to main activity.
            //On the main activity you can take any desired action.
        }

        return super.onKeyEvent(event);
    }

    boolean onKeyDown1(int keyCode, KeyEvent event) {
        //AudibleReadyPlayer abc;
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                // code for fast forward
                return true;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                // code for next
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                // code for play/pause
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                // code for previous
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                // code for rewind
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                // code for stop
                return true;
        }
        return false;
    }
}
