package queen.dfu;

import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class EventHandlingThread extends Thread {
    private volatile boolean abort;

    public void abort() {
        this.abort = true;
    }

    @Override
    public void run()
    {
        while (!this.abort)
        {
            int result = LibUsb.handleEventsTimeout(null, 250000);
            if (result != LibUsb.SUCCESS)
                throw new LibUsbException("Unable to handle events", result);
        }
    }
}
