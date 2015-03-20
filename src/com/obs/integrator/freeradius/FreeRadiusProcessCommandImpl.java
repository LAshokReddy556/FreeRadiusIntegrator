package com.obs.integrator.freeradius;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.json.JSONException;

public class FreeRadiusProcessCommandImpl {

	public Timer timer;
	public int timePeriod;
	static Logger logger = Logger.getLogger("");
	private SendingDataToFreeRadiusServer sendingDataToFreeRadiusServer;

	public FreeRadiusProcessCommandImpl(String mysqlDatabase, String mysqlhost, String mysqlUsername, 
			String mysqlPassword, String mikrotik_RouterType, String mikrotik_HostName, 
			String mikrotik_userName, String mikrotik_password, String mikrotik_port,String userName, int timePeriod) {
		
		this.timePeriod = timePeriod;
		Reminder(timePeriod);
		sendingDataToFreeRadiusServer = new SendingDataToFreeRadiusServer(mysqlDatabase, mysqlhost, mysqlUsername,
				mysqlPassword, mikrotik_RouterType, mikrotik_HostName,
				mikrotik_userName, mikrotik_password, mikrotik_port, userName);
	}

	public void processRequest(FreeRadiusProcessRequestData processRequestData) {
		
		try {
				
				if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_CREATE_NAS)) {
				
					String output = sendingDataToFreeRadiusServer.createNasCommandProcessing(processRequestData);
				
					if(!output.isEmpty()){
					
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());		
					}
				
				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_CREATE_NAS_SERVICE)) {
				
					String output = sendingDataToFreeRadiusServer.createNasServiceCommandProcessing(processRequestData);
				
					if(!output.isEmpty()){
					
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());		
					}
				
				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_ACTIVATION) ||
						processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_RECONNECTION) ||
						processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_RENEWAL_AE)) {
					
					String output = sendingDataToFreeRadiusServer.activationCommandProcessing(processRequestData);
					
					if(null != output && !output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}
				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_CHANGE_CREDENTIALS)) {
					
					String output = sendingDataToFreeRadiusServer.changeCredentialsCommandProcessing(processRequestData);
					
					if(null != output && !output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}
				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_DISCONNECTION)) {
					
					String output = sendingDataToFreeRadiusServer.disConnectionCommandProcessing(processRequestData);
					
					if(!output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}
				/*} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_CHANGE_PLAN)) {
					
					String output = sendingDataToBeeniusServer.changePlanCommandProcessing(processRequestData);
					
					if(!output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}
					
				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_DEVICE_SWAP) ) {
					
					String output = sendingDataToBeeniusServer.deviceSwapCommandProcessing(processRequestData);
					
					if(!output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}

				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_RELEASE_DEVICE) ) {
					
					String output = sendingDataToBeeniusServer.releaseDeviceCommandProcessing(processRequestData);
					
					if(!output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}

				} else if (processRequestData.getRequestType().equalsIgnoreCase(FreeRadiusConstants.REQ_MESSAGE) ) {
					
					String output = sendingDataToBeeniusServer.beeniusOSDMessageCommandProcessing(processRequestData);
					
					if(!output.isEmpty()){
						
						FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
						
					}

				*/} else  {
					
					logger.error("Request Type Not Found : "+ processRequestData.getRequestType());
					String output = FreeRadiusConstants.FAILURE + "Invalid Request Type";
					FreeRadiusProcessCommandImpl.process(output, processRequestData.getId(),processRequestData.getPrdetailsId(), processRequestData.getClientId(), processRequestData.getRequestType());	
					
				}
			
		} catch (NullPointerException e) {
			logger.error("Exception : " + e.getMessage());
		} catch (JSONException e) {
			logger.error("JsonException : JsonObject Creation failed, " + e.getMessage());
		} catch (IOException e) {
			logger.error("IOException : Data Sending to FreeRadius Server failed, " + e.getMessage());
		} catch (SQLException e) {
			logger.error("SQLException : Data Insertion in FreeRadius Server failed, " + e.getMessage());
		} 

	}


	
	
	public static void process(String value, Long id, Long prdetailsId, Long clientId, String requestType){	
		try{		
			logger.info("output from CAS Server is :" +value);
			if(value==null){
				throw new NullPointerException();
			}else{		
				FreeRadiusConsumer.sendResponse(value,id,prdetailsId, clientId, null, requestType);
			}		
		} catch(NullPointerException e){
			logger.error("NullPointerException : Output from the Oss System Server is : " + value);
		} catch (Exception e) {
		    logger.error("Exception : " + e.getMessage());
	    }
		
	}
	
	public void Reminder(int seconds) {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds);
	}

	class RemindTask extends TimerTask {
		public void run() {
			connectionHolding();
			Reminder(timePeriod);
		}
	
	}

	public void connectionHolding() {
		
		try {
			sendingDataToFreeRadiusServer.heartBeatCommandProcessing();
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		} 
	}
	
	
}

	
