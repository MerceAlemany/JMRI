package jmri.jmrix.loconet;

import jmri.util.JUnitUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;

public class LocoNetSlotTest extends TestCase {

    public LocoNetSlotTest(String s) {
        super(s);
    }

    public void testGetSlotSend() {
        SlotManager slotmanager = new SlotManager(lnis);
        SlotListener p2 = new SlotListener() {
            @Override
            public void notifyChangedSlot(LocoNetSlot l) {
            }
        };
        slotmanager.slotFromLocoAddress(21, p2);
        Assert.assertEquals("slot request message",
                "BF 00 15 00",
                lnis.outbound.elementAt(lnis.outbound.size() - 1).toString());
    }

    LocoNetInterfaceScaffold lnis;

    // Main entry point
    static public void main(String[] args) {
        String[] testCaseName = {LocoNetSlotTest.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    // test suite from all defined tests
    public static Test suite() {
        TestSuite suite = new TestSuite(LocoNetSlotTest.class);
        return suite;
    }

    // The minimal setup for log4J
    @Override
    protected void setUp() {
        // prepare an interface
        lnis = new LocoNetInterfaceScaffold();

        apps.tests.Log4JFixture.setUp();
    }

    @Override
    protected void tearDown() {
        JUnitUtil.tearDown();
    }

}
