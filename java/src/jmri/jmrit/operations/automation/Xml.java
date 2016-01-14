package jmri.jmrit.operations.automation;

/**
 * A convenient place to access operations xml element and attribute names.
 *
 * @author Daniel Boudreau Copyright (C) 2016
 * @version $Revision: 22156 $
 *
 */
public class Xml {

    // Common to operation xml files
    static final String ID = "id"; // NOI18N
    static final String NAME = "name"; // NOI18N
    static final String COMMENT = "comment"; // NOI18N
    static final String TRUE = "true"; // NOI18N
    static final String FALSE = "false"; // NOI18N
    
    static final String AUTOMATION = "automation"; // NOI18N

    static final String AUTOMATIONS = "automations"; // NOI18N
    
    // Automation.java
    static final String CURRENT_ITEM = "currentItem"; // NOI18N

    // AutomationItem.java
    static final String ITEM = "item"; // NOI18N
    static final String SEQUENCE_ID = "sequenceId"; // NOI18N
    static final String ACTION_CODE = "actionCode"; // NOI18N
    static final String TRAIN_ID = "trainId"; // NOI18N
    static final String ROUTE_LOCATION_ID = "routeLocationId"; // NOI18N
    static final String AUTOMATION_ID = "automationId"; // NOI18N
    static final String MESSAGE = "message"; // NOI18N

}
