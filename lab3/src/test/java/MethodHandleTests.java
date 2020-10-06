import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.junit.jupiter.api.Assertions.*;

public class MethodHandleTests {
    @Test
    void findStaticTest() {
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        MethodHandle methodHandle = null;
        try {
            methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            fail(e.getCause());
        }
        assertEquals(methodHandle.type(), MethodType.methodType(int.class, String.class));
    }
}
