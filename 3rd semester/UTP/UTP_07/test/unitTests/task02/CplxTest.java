package unitTests.task02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public
    class CplxTest {

    // Step 1: the field was non-static:
    //     private Cplx cplx = new Cplx(2, 3);
    // -> all tests passed.
    //
    // Step 2: the field was changed to static:
    //     private static Cplx cplx = new Cplx(2, 3);
    // -> 2 of 4 tests failed (JUnit ran them in the order: testToString,
    //    testAddAss, compareCplxArr, testSubAss):
    //      compareCplxArr: expected [2.0, 3.0] but was [3.0, 4.0]
    //      testSubAss:     expected 1.0 + 2.0i but was 2.0 + 3.0i
    //
    // WHY: JUnit 5 creates a NEW instance of the test class for every test
    // method (Lifecycle.PER_METHOD). A non-static field is initialized again
    // for each test, so every test gets a fresh Cplx(2, 3).
    // A static field belongs to the class, not to the instance, so it is
    // initialized only ONCE and all tests share the same object.
    // addAss and subAss modify "this" (they don't create a new object), so
    // after testAddAss the shared cplx is (3, 4) instead of (2, 3) and the
    // next tests get wrong values. The result depends on the order in which
    // JUnit runs the methods - tests are not independent anymore.
    //
    // Step 3: setup() with @BeforeEach - YES, it solves the problem. It runs
    // before every single test and assigns a new Cplx(2, 3) to the field, so
    // each test starts from the same state again, even though the field is
    // still static.
    private static Cplx cplx = new Cplx(2, 3);

    @BeforeEach
    public void setup() {
        cplx = new Cplx(2, 3);
    }

    @Test
    public void compareCplxArr() {
        double[] expected = {2.0, 3.0};
        assertArrayEquals(expected, cplx.getCplxArr());
    }

    @Test
    public void testToString() {
        assertEquals("2.0 + 3.0i", cplx.toString());
    }

    @Test
    public void testAddAss() {
        Cplx result = cplx.addAss(new Cplx(1, 1));
        assertEquals(new Cplx(3, 4), result);
        assertSame(cplx, result); // addAss returns the same object
    }

    @Test
    public void testSubAss() {
        Cplx result = cplx.subAss(new Cplx(1, 1));
        assertEquals(new Cplx(1, 2), result);
        assertSame(cplx, result); // subAss returns the same object
    }
}
