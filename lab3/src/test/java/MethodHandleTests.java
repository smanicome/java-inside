import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class MethodHandleTests {
    @Test @Tag("Q2")
    void findStaticTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        assertEquals(MethodType.methodType(int.class, String.class), methodHandle.type());
    }

    @Test @Tag("Q3")
    void findVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));
        assertEquals(MethodType.methodType(String.class, String.class), methodHandle.type());
    }

    @Test @Tag("Q4")
    void invokeExactStaticTest() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        assertEquals(1, (int) methodHandle.invokeExact("1"));
    }

    @Test @Tag("Q4")
    void invokeExactStaticWrongArgumentTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        assertThrows(WrongMethodTypeException.class, methodHandle::invokeExact);
    }

    @Test @Tag("Q5")
    void invokeExactVirtualTest() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));
        assertEquals("A", (String) methodHandle.invokeExact("a"));
    }

    @Test @Tag("Q5")
    void invokeExactVirtualWrongArgumentTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));
        assertThrows(WrongMethodTypeException.class, methodHandle::invokeExact);
    }

    @Test @Tag("Q6")
    void invokeStaticTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        assertAll(
            () -> assertEquals(1, (Integer) methodHandle.invoke("1")),
            () -> assertThrows(WrongMethodTypeException.class, () -> {
                var a = (String) methodHandle.invoke("1");
            })
        );
    }

    @Test @Tag("Q6")
    void invokeVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));
        assertAll(
            () -> assertEquals("A", (Object) methodHandle.invoke("a")),
            () -> assertThrows(ClassCastException.class, () -> {
                var a = (Double) methodHandle.invoke("a");
            })
        );
    }

    @Test @Tag("Q7")
    void insertAndInvokeStaticTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));

        assertAll(
            () -> assertEquals(123, (int) MethodHandles.insertArguments(methodHandle, 0, "123").invokeExact()),
            () -> assertThrows(ClassCastException.class, () -> MethodHandles.insertArguments(methodHandle, 0, 123).invokeExact())
        );
    }

    @Test @Tag("Q7")
    void binToAndInvokeVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));

        assertAll(
            () -> assertEquals("ABC", (String) methodHandle.bindTo("abc").invokeExact()),
            () -> assertThrows(ClassCastException.class, () -> methodHandle.bindTo(1).invokeExact())
        );
    }

    @Test @Tag("Q8")
    void dropAndInvokeStaticTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        MethodHandle methodHandleBis = MethodHandles.dropArguments(methodHandle, 0, int.class);

        assertAll(
                () -> assertEquals(123, (int) methodHandleBis.invokeExact(123, "123")),
                () -> assertThrows(WrongMethodTypeException.class,
                        () -> methodHandleBis.invokeExact(123))
        );
    }

    @Test @Tag("Q8")
    void dropAndInvokeVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));
        MethodHandle methodHandleBis = MethodHandles.dropArguments(methodHandle, 0, String.class);

        assertAll(
            () -> assertEquals("ABC", (String) methodHandleBis.invokeExact("efg", "abc")),
            () -> assertThrows(WrongMethodTypeException.class,
                () -> methodHandleBis.invokeExact("abc"))
        );
    }

    @Test @Tag("Q9")
    void asTypeAndInvokeExactStaticTest() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        assertAll(
            () -> assertEquals(1, (Integer) methodHandle.asType(MethodType.methodType(Integer.class, String.class)).invokeExact("1")),
            () -> assertThrows(WrongMethodTypeException.class, () -> {
                var a = (String) methodHandle.asType(MethodType.methodType(String.class, String.class)).invokeExact("1");
            })
        );
    }

    @Test @Tag("Q10")
    void invokeExactConstantTest() throws Throwable {
        assertEquals(42, (int) MethodHandles.constant(int.class, 42).invoke());
    }

    private static MethodHandle match(String text) throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var equals = lookup.findVirtual(String.class, "equals", MethodType.methodType(boolean.class, Object.class));
        var test = MethodHandles.insertArguments(equals, 1, text);
        var target = MethodHandles.dropArguments(MethodHandles.constant(int.class, 1), 0, String.class);
        var fallback = MethodHandles.dropArguments(MethodHandles.constant(int.class, -1), 0, String.class);

        return MethodHandles.guardWithTest(test, target, fallback);
    }

    private static MethodHandle matchAll(List<String> textes) throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var equals = lookup.findVirtual(String.class, "equals", MethodType.methodType(boolean.class, Object.class));
        var target = MethodHandles.dropArguments(MethodHandles.constant(int.class, -1), 0, String.class);

        int index = 0;
        for(var text : textes) {
            var test = MethodHandles.insertArguments(equals, 1, text);
            var ok =  MethodHandles.dropArguments(MethodHandles.constant(int.class, index), 0, String.class);
            target = MethodHandles.guardWithTest(test, ok, target);
            index++;
        }

        return target;
    }

    @Test @Tag("Q11")
    void matchTest() throws Throwable {
        MethodHandle methodHandle = match("toto");
        assertAll(
            () -> assertEquals(1, methodHandle.invoke("toto")),
            () -> assertEquals(-1, methodHandle.invoke("titi"))
        );
    }

    @Test @Tag("Q12")
    void matchAllTest() throws Throwable {
        MethodHandle methodHandle = matchAll(List.of("toto", "titi", "tata"));
        assertAll(
            () -> assertNotEquals(-1, methodHandle.invoke("toto")),
            () -> assertNotEquals(-1, methodHandle.invoke("titi")),
            () -> assertNotEquals(-1, methodHandle.invoke("tata"))
        );
    }
}
