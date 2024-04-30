/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
class ReadFailException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Read ERROR!";
    public ReadFailException() {
        super(DEFAULT_MESSAGE);
    }
}

class WriteFailException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Write ERROR!";
    public WriteFailException() {
        super(DEFAULT_MESSAGE);
    }
}

public class DeviceDriver {
    public static final byte EMPTY_BYTE_VALUE = (byte) 0xFF;
    public static final int READ_DELAY_TIME = 200;
    private final FlashMemoryDevice hardware;

    public DeviceDriver(FlashMemoryDevice hardware) {
        this.hardware = hardware;
    }

    public byte read(long address) {
        byte firstTimeResult = hardware.read(address);
        for (int i = 0; i < 4; i++) {
            try {
                Thread.sleep(READ_DELAY_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (firstTimeResult != hardware.read(address)) {
                throw new ReadFailException();
            }
        }
        return firstTimeResult;
    }

    public void write(long address, byte data) {
        if (read(address) != EMPTY_BYTE_VALUE) {
            throw new WriteFailException();
        }
        hardware.write(address, data);
    }
}