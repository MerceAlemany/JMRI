package jmri.jmrix.easydcc;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017	
 */
public class EasyDccThrottleManagerTest {

    @Test
    public void testCTor() {
        // infrastructure objects
        EasyDccTrafficControlScaffold tc = new EasyDccTrafficControlScaffold();
        EasyDccSystemConnectionMemo memo = new EasyDccSystemConnectionMemo(tc);
        EasyDccThrottleManager t = new EasyDccThrottleManager(memo);
        Assert.assertNotNull("exists",t);
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(EasyDccThrottleManagerTest.class.getName());

}
