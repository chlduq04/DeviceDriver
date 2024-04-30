import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceDriverTest {
    public static final int DEFAULT_TEST_ADDRESS = 0xFF;
    public static final int DEFAULT_TEST_START_ADDRESS = 0x00;
    public static final int DEFAULT_TEST_END_ADDRESS = 0x04;
    public static final byte DEFAULT_TEST_BYTE = (byte) 0xFE;
    public static final Class READ_FAIL_EXCEPTION_CLASS = ReadFailException.class;
    public static final Class WRITE_FAIL_EXCEPTION_CLASS = WriteFailException.class;

    @Spy
    FlashMemoryDevice hardware;

    DeviceDriver driver;

    @BeforeEach
    void setUp() {
        driver = new DeviceDriver(hardware);
    }

    @Test
    void readAndPrintSuccess() {
        driver.readAndPrint(DEFAULT_TEST_START_ADDRESS, DEFAULT_TEST_END_ADDRESS);

        verify(hardware, times(20)).read(anyLong());
    }

    @Test
    void readAndPrintError() {
        when(hardware.read(DEFAULT_TEST_START_ADDRESS)).thenThrow(new ReadFailException());

        assertThatThrownBy(() -> {
            driver.readAndPrint(DEFAULT_TEST_START_ADDRESS, DEFAULT_TEST_END_ADDRESS);
        }).isInstanceOf(READ_FAIL_EXCEPTION_CLASS).hasMessageContaining("ERROR");
    }

    @Test
    void writeAllSuccess() {
        when(hardware.read(anyLong())).thenReturn(DeviceDriver.EMPTY_BYTE_VALUE);

        driver.writeAll(DEFAULT_TEST_BYTE);

        verify(hardware, times(4)).write(anyLong(), anyByte());
    }

    @Test
    void writeAllError() {
        when(hardware.read(anyLong())).thenReturn(DEFAULT_TEST_BYTE);

        assertThatThrownBy(() -> {
            driver.writeAll(DEFAULT_TEST_BYTE);
        }).isInstanceOf(WRITE_FAIL_EXCEPTION_CLASS).hasMessageContaining("ERROR");
    }

}


