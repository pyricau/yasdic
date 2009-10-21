package info.piwai.yasdic;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class info.piwai.yasdic.UselessTest \
 * info.piwai.yasdic.tests/android.test.InstrumentationTestRunner
 */
public class UselessTest extends ActivityInstrumentationTestCase<Useless> {

    public UselessTest() {
        super("info.piwai.yasdic", Useless.class);
    }

}
