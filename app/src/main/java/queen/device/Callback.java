package queen.device;

public class Callback {
    private Long functionAddress;
    private Long callbackAddress;

    public Callback(Long functionAddress, Long callbackAddress) {
        this.functionAddress = functionAddress;
        this.callbackAddress = callbackAddress;
    }

    public Long getFunctionAddress() {
        return this.functionAddress;
    }

    public void setFunctionAddress(Long functionAddress) {
        this.functionAddress = functionAddress;
    }

    public Long getCallbackAddress() {
        return this.callbackAddress;
    }

    public void setCallbackAddress(Long callbackAddress) {
        this.callbackAddress = callbackAddress;
    }
}
