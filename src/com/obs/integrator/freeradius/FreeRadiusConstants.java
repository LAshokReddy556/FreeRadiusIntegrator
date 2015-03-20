package com.obs.integrator.freeradius;

public class FreeRadiusConstants {
	
	public static final String PLANMAPPING = "planIdentification";
	public static final String SERVICES = "services";
	public static final String SERVICEIDENTIFICATION = "serviceIdentification";
	public static final String OLDSERVICES = "oldServices";
	public static final String OLDSERVICEIDENTIFICATION = "oldServiceIdentification";
	public static final String OSD_MESSAGE_TYPE = "genericmessage";
	
	
	//For request Type
	public static final String REQ_ACTIVATION = "ACTIVATION";
	public static final String REQ_DISCONNECTION = "DISCONNECTION";
	public static final String REQ_RECONNECTION = "RECONNECTION";
    public static final String REQ_MESSAGE = "MESSAGE";  //OSDMESSAGE
    public static final String REQ_RENEWAL_AE = "RENEWAL_AE";
    public static final String REQ_CHANGE_PLAN = "CHANGE_PLAN";
    public static final String REQ_TERMINATE = "TERMINATE";
    public static final String REQ_DEVICE_SWAP = "DEVICE_SWAP";
    public static final String REQ_RELEASE_DEVICE = "RELEASE DEVICE";
    public static final String REQ_CREATE_NAS = "Create Nas";
    public static final String REQ_CREATE_NAS_SERVICE = "CREATE RADSERVICE";
    public static final String REQ_CHANGE_CREDENTIALS = "Change Credentials";
    
    //gve nas request 
    //output type
    public static final String SUCCESS = "Success";
    public static final String FAILURE = "failure : ";
    
    public static final String RADIUS_HOTSPOT = "hotspot";
	public static final String RADIUS_PPPOE = "pppoe";
    
    public static final String RADCHECK_V2_CREATE_OUTPUT = "Radcheck is created"; 
    public static final String RADIUS_V2_DELETE_OUTPUT = "Record is deleted";
}
