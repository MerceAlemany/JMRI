package jmri.jmrix.direct.serial.configurexml;

import jmri.jmrix.configurexml.AbstractSerialConnectionConfigXml;
import jmri.jmrix.direct.serial.ConnectionConfig;
import jmri.jmrix.direct.serial.SerialDriverAdapter;

/**
 * Handle XML persistance of layout connections by persistening the
 * SerialDriverAdapter (and connections). Note this is named as the XML version
 * of a ConnectionConfig object, but it's actually persisting the
 * SerialDriverAdapter.
 * <P>
 * This class is invoked from jmrix.JmrixConfigPaneXml on write, as that class
 * is the one actually registered. Reads are brought here directly via the class
 * attribute in the XML.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2003, 2004
 */
public class ConnectionConfigXml extends AbstractSerialConnectionConfigXml {

    public ConnectionConfigXml() {
        super();
    }

    @Override
    protected void getInstance() {
        adapter = SerialDriverAdapter.instance();
    }

    @Override
    protected void register() {
        this.register(new ConnectionConfig(adapter));
    }

}
