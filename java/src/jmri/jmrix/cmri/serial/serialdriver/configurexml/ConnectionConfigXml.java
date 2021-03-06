package jmri.jmrix.cmri.serial.serialdriver.configurexml;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.StringTokenizer;
import jmri.jmrix.cmri.CMRISystemConnectionMemo;
import jmri.jmrix.cmri.serial.SerialNode;
import jmri.jmrix.cmri.serial.SerialTrafficController;
import jmri.jmrix.cmri.serial.serialdriver.ConnectionConfig;
import jmri.jmrix.cmri.serial.serialdriver.SerialDriverAdapter;
import jmri.jmrix.configurexml.AbstractSerialConnectionConfigXml;
import org.jdom2.Element;

/**
 * Handle XML persistance of layout connections by persisting the
 * SerialDriverAdapter (and connections). Note this is named as the XML version
 * of a ConnectionConfig object, but it's actually persisting the
 * SerialDriverAdapter.
 * <P>
 * This class is invoked from jmrix.JmrixConfigPaneXml on write, as that class
 * is the one actually registered. Reads are brought here directly via the class
 * attribute in the XML.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 
 * @author Chuck Catania Copyright: Copyright (c) 2014, 2015, 2016, 2017

 */
public class ConnectionConfigXml extends AbstractSerialConnectionConfigXml {

    public ConnectionConfigXml() {
        super();
    }

    /**
     * Write out the SerialNode objects too
     *
     * @param e Element being extended
     */
    @SuppressFBWarnings(value = "SBSC_USE_STRINGBUFFER_CONCATENATION")
    // Only used occasionally, so inefficient String processing not really a problem
    // though it would be good to fix it if you're working in this area
    @Override
    protected void extendElement(Element e) {
        // Create a polling list from the configured nodes  c2
        String polllist = ""; //c2
        SerialTrafficController tcPL = ((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController();
        SerialNode plNode = (SerialNode) tcPL.getNode(0);
        int index = 1;
        while(plNode != null)
        {
          if (index != 1) polllist = polllist+",";
          polllist = polllist + Integer.toString(plNode.getNodeAddress());
          plNode = (SerialNode) tcPL.getNode(index);
          index ++;
        }
        
        Element l = new Element("polllist");
        l.setAttribute("pollseq",polllist);
        e.addContent(l);
        
        SerialTrafficController tc = ((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController();
        SerialNode node = (SerialNode) tc.getNode(0);
        index = 1;
        
        while (node != null) {
            // add node as an element
            Element n = new Element("node");
            n.setAttribute("name", "" + node.getNodeAddress());
            e.addContent(n);
            // add parameters to the node as needed
            n.addContent(makeParameter("nodetype", "" + node.getNodeType()));
            n.addContent(makeParameter("bitspercard", "" + node.getNumBitsPerCard()));
            n.addContent(makeParameter("transmissiondelay", "" + node.getTransmissionDelay()));
            n.addContent(makeParameter("num2lsearchlights", "" + node.getNum2LSearchLights()));
            n.addContent(makeParameter("pulsewidth", "" + node.getPulseWidth()));
            
            String value = "";
            for (int i = 0; i < node.getLocSearchLightBits().length; i++) {
                value = value + Integer.toHexString(node.getLocSearchLightBits()[i] & 0xF);
            }
            n.addContent(makeParameter("locsearchlightbits", "" + value));
            value = "";
            for (int i = 0; i < node.getCardTypeLocation().length; i++) {
                value = value + Integer.toHexString(node.getCardTypeLocation()[i] & 0xF);
            }
            n.addContent(makeParameter("cardtypelocation", "" + value));
//            log.info("Node "+node.nodeAddress+" Card Type Written = "+value);

            // CMRInet Options  c2
            //-----------------
            value = "";
            for (int i=0; i<SerialNode.NUMCMRINETOPTS; i++) {
                    value = value + Integer.toHexString((node.getCMRInetOpts(i)&0xF));
            }     
                n.addContent(makeParameter("cmrinetoptions",""+value.toUpperCase()));
//            log.info("Node "+node.nodeAddress+" NET Options Written = "+value);
               
            // cpNode Options  Classic CMRI nodes do not have options
            //-------------------------------------------------------
            if (node.getNodeType()==SerialNode.CPNODE || node.getNodeType()==SerialNode.CPMEGA)  //c2
            {
                value = "";
                for (int i=0; i<SerialNode.NUMCPNODEOPTS; i++) {
                    value = value + Integer.toHexString((node.getcpnodeOpts(i)&0xF));
                }           
                n.addContent(makeParameter("cpnodeoptions",""+value.toUpperCase()));
//                log.info("Node "+node.nodeAddress+" NODE Options Written = "+value);
            }
            
            // node description c2
            n.addContent(makeParameter("cmrinodedesc",""+node.getcmriNodeDesc()));
            
            // look for the next node
            node = (SerialNode) tc.getNode(index);
            index++;
        }
    }

    protected Element makeParameter(String name, String value) {
        Element p = new Element("parameter");
        p.setAttribute("name", name);
        p.addContent(value);
        return p;
    }

    @Override
    protected void getInstance() {
        if(adapter == null) {
           adapter = new SerialDriverAdapter();
        } 
    }

    @Override
    protected void getInstance(Object object) {
        adapter = ((ConnectionConfig) object).getAdapter();
    }

    @Override
    protected void unpackElement(Element shared, Element perNode) {
        // --------------------------------------
        // Load the poll list sequence if present
        // --------------------------------------
        SerialNode plNode;
        List<Element> pl = shared.getChildren("polllist");
        if(pl.size()!=0)
        {
            Element ps = pl.get(0);
            if(ps != null)
            {
                String pseq = ps.getAttributeValue("pollseq");
                if(pseq!=null)
                 {
                    StringTokenizer nodes = new StringTokenizer(pseq," ,");
                    SerialTrafficController tcPL = ((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController();
                    plNode = (SerialNode) tcPL.getNode(0);
                    while(nodes.hasMoreTokens())
                    {
                      tcPL.cmriNetPollList.add(Integer.parseInt(nodes.nextToken()));
                    }
//                    log.info("Poll List = "+tcPL.cmriNetPollList);
                 }
            }
        } 
        
        // Load the node specific parameters
        // ---------------------------------
//        int pollListSize = SerialTrafficController.instance().cmriNetPollList.size();
        int pollListSize = ((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController().cmriNetPollList.size();
        int nextPollPos = pollListSize+1;        
        
        List<Element> l = shared.getChildren("node");
        for (int i = 0; i < l.size(); i++) {
            Element n = l.get(i);
            int addr = Integer.parseInt(n.getAttributeValue("name"));
            int type = Integer.parseInt(findParmValue(n, "nodetype"));
            int bpc = Integer.parseInt(findParmValue(n, "bitspercard"));
            int delay = Integer.parseInt(findParmValue(n, "transmissiondelay"));
            int num2l = Integer.parseInt(findParmValue(n, "num2lsearchlights"));
            int pulseWidth = 500;
            if ((findParmValue(n, "pulsewidth")) != null) {
                pulseWidth = Integer.parseInt(findParmValue(n, "pulsewidth"));
            }

            String slb = findParmValue(n, "locsearchlightbits");
            String ctl = findParmValue(n, "cardtypelocation");
            String opts = "";  //c2
            
            // create node (they register themselves)
            SerialNode node = new SerialNode(addr, type,((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController());
            node.setNumBitsPerCard(bpc);
            node.setTransmissionDelay(delay);
            node.setNum2LSearchLights(num2l);
            node.setPulseWidth(pulseWidth);

            // --------------------------------------------------------------------
            // From the loaded poll list, assign the poll list position to the node
            // --------------------------------------------------------------------
            int pls = 0;
            boolean assigned = false;
            if(pollListSize > 0)
            {
                for (pls=0; pls<pollListSize; pls++)
                {
                 if (((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController().cmriNetPollList.get(pls) == node.getNodeAddress())
                 {
                  node.setPollListPosition(pls+1);
                  assigned = true;
                 }
                }
                if (!assigned)
                 node.setPollListPosition(nextPollPos++); 
           }

           // CMRInet Options   //c2
           //----------------
           if (findParmValue(n,"cmrinetoptions") != null)
           {
               opts = findParmValue(n,"cmrinetoptions"); 
           // Convert and load the  value into the node options array
               for  (int j = 0; j<SerialNode.NUMCMRINETOPTS; j++)
               {
                node.setCMRInetOpts(j, (opts.charAt(j)-'0') );
               }
 //              log.info("Node "+node.nodeAddress+" NET Options Read = "+opts);

            }
           else
            // This must be the first time the nodes were loaded into a cpNode
            // supported version.  Set the Auto Poll option to avoid confusion
            // with installed configurations.
           {
               for  (int j = 0; j<SerialNode.NUMCMRINETOPTS; j++)
               {
                node.setCMRInetOpts(j,0);
               }
               node.setOptNet_AUTOPOLL(1);
 //              log.info("Node "+node.nodeAddress+" AUTO POLL Set ");
              
           }
            
            
            for (int j = 0; j < slb.length(); j++) {
                node.setLocSearchLightBits(j, (slb.charAt(j) - '0'));
            }

            for (int j = 0; j < ctl.length(); j++) {
                node.setCardTypeLocation(j, (ctl.charAt(j) - '0'));
            }
            
            if (type==SerialNode.CPNODE || type==SerialNode.CPMEGA)  //c2
            {   
                // cpNode Options
                //---------------
                if (findParmValue(n,"cpnodeoptions") != null)
                {
                   opts = findParmValue(n,"cpnodeoptions"); 
                // Convert and load the  value into the node options array
                   for  (int j = 0; j<SerialNode.NUMCPNODEOPTS; j++)
                   {
                    node.setcpnodeOpts(j, (opts.charAt(j)-'0') );
                   }
                }
               
 //             log.info("Node "+node.nodeAddress+" NODE Options Read = "+opts);            
           }
            
          if (findParmValue(n,"cmrinodedesc") != null) 
           {node.setcmriNodeDesc(findParmValue(n,"cmrinodedesc"));}
          else
           {
 //              log.info("No Description - Node "+addr);
           }
                          
            // Trigger initialization of this Node to reflect these parameters
            ((CMRISystemConnectionMemo)adapter.getSystemConnectionMemo()).getTrafficController().initializeSerialNode(node);
        }
    }

    /**
     * Service routine to look through "parameter" child elements to find a
     * particular parameter value
     *
     * @param e    Element containing parameters
     * @param name name of desired parameter
     * @return String value
     */
    String findParmValue(Element e, String name) {
        List<Element> l = e.getChildren("parameter");
        for (int i = 0; i < l.size(); i++) {
            Element n = l.get(i);
            if (n.getAttributeValue("name").equals(name)) {
                return n.getTextTrim();
            }
        }
        return null;
    }

    @Override
    protected void register() {
        this.register(new ConnectionConfig(adapter));
    }

}
