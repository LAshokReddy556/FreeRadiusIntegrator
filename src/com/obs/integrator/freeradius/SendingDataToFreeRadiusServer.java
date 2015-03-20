package com.obs.integrator.freeradius;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


public class SendingDataToFreeRadiusServer {
	
	static Logger logger = Logger.getLogger("");
	
	private String mikrotik_RouterType;
	private String mikrotik_HostName;
	private String mikrotik_userName;
	private String mikrotik_password;
	private String mikrotik_port;
	private String mysqlDatabase;
	private String mysqlhost;
	private String mysqlUsername;
	private String mysqlPassword;
	@SuppressWarnings("unused")
	private String username;
	
	public SendingDataToFreeRadiusServer(String mysqlDatabase, String mysqlhost, String mysqlUsername, 
			String mysqlPassword, String mikrotik_RouterType, String mikrotik_HostName,
			String mikrotik_userName, String mikrotik_password, String mikrotik_port, String username) {
		
		this.mysqlDatabase = mysqlDatabase;
		this.mysqlhost = mysqlhost;
		this.mysqlUsername = mysqlUsername;
		this.mysqlPassword = mysqlPassword;
		this.mikrotik_RouterType = mikrotik_RouterType;
		this.mikrotik_HostName = mikrotik_HostName;
		this.mikrotik_userName = mikrotik_userName;
		this.mikrotik_password = mikrotik_password;
		this.mikrotik_port = mikrotik_port;
		this.username = username;
	}
	
	public String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}

	public Connection connectionSetUp(){
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		
			String url = "jdbc:mysql://" + mysqlhost + "/" + mysqlDatabase;
			
			return DriverManager.getConnection(url, mysqlUsername, mysqlPassword);
			
		} catch (InstantiationException e) {
			logger.error("InstantiationException in Connection Establish...");
			return null;
		
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException in Connection Establish...");
			return null;
		
		} catch (ClassNotFoundException e) {
			
			logger.error("ClassNotFoundException in Connection Establish..."+e.getStackTrace());
			return null;
		
		} catch (SQLException e) {
			logger.error("SQLException in Connection Establish...");
			return null;
		}	
	}

	public String activationCommandProcessing(FreeRadiusProcessRequestData processRequestData) throws JSONException, IOException, SQLException {
	
		JSONObject jsonObject = new JSONObject(processRequestData.getProduct());
		String planIdentification = jsonObject.getString("planIdentification");
		
		if (planIdentification == null || planIdentification.isEmpty() || planIdentification.equalsIgnoreCase("")) {
		
			logger.info("Plan Identification should Not Mapped Properly, Plan Identification=" + planIdentification + " \r\n");
			return FreeRadiusConstants.FAILURE + "Plan Identification "
					+ " should Not Mapped Properly and Plan Identification=" + planIdentification;
		} 
		
		String selfcarePassword = processRequestData.getSelfcarePassword().trim();
		
		String password = MD5(selfcarePassword);
		Calendar cal = Calendar.getInstance();
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String createdOn = sdf1.format(cal.getTime());
		
		cal.add( Calendar.YEAR, 1 );
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String expiration = sdf.format(cal.getTime());
		
		int acctype = 0;
		
		
		if(processRequestData.getSelfcareUsername().contains(":")){
			acctype = 1;
			selfcarePassword = "";
		}
		
		//Get the data from rm_services.
		String selectQuery = " select combquota,limitcomb from rm_services where srvid=" + planIdentification;
		
		Long downlimit = 0L, comblimit = 0L, combquota = 0L;
		
		processSelectQueryForUser(selectQuery,processRequestData);
		
		combquota = processRequestData.getCombquota();	
		Boolean limitcomb = processRequestData.getLimitcomb();
		
		if (limitcomb) comblimit=combquota;
		else downlimit = combquota;
		
		String query =" insert into `rm_users`(`username`,`password`,`groupid`,`enableuser`,`uplimit`,`downlimit`,`comblimit`,`firstname`,`lastname`," +
			   " `company`,`phone`,`mobile`,`address`,`city`,`zip`,`country`,`state`,`comment`,`gpslat`,`gpslong`,`mac`,`usemacauth`,`expiration`, " +
			   " `uptimelimit`,`srvid`,`staticipcm`,`staticipcpe`,`ipmodecm`,`ipmodecpe`,`poolidcm`,`poolidcpe`,`createdon`,`acctype`,`credits`, " +
			   " `cardfails`,`createdby`,`owner`,`taxid`,`email`,`maccm`,`custattr`,`warningsent`,`verifycode`,`verified`,`selfreg`,`verifyfails`," +
			   " `verifysentnum`,`verifymobile`,`contractid`,`contractvalid`,`actcode`,`pswactsmsnum`,`alertemail`,`alertsms`,`lang`,`lastlogoff`)" +
			   "  values ('"+processRequestData.getSelfcareUsername().trim()+"','" + password +"',1,1,0,"+downlimit+","+ comblimit +",'" + processRequestData.getFirstName() + "','" + processRequestData.getLastName() +"'," +
			   " 'R&amp;C company','158182817','','West road 1343.','Tampa','32434','California','','',0.0,0.0,'mac',0,'"+expiration+"'," +
			   " 0,'-1','','',0,0,0,0,'"+createdOn+"',"+ acctype +",0," +
			   " 0,'admin','admin','','"+processRequestData.getEmail()+"','','',0,'',0,0,0," +
			   " 0,'','AE1323-12','0000-00-00','',1,0,1,'English','"+createdOn+"');";
		
		
		int outputMessage = processPostQuery(query);
		
		if(outputMessage>=0){
			
			String createUserQuery = "INSERT INTO radcheck (username, attribute, op, value) VALUES ('" + processRequestData.getSelfcareUsername().trim() +
					"','Simultaneous-Use',':=','1'),('" +  processRequestData.getSelfcareUsername().trim() +"','Cleartext-Password',':='," +
					"'"+ selfcarePassword +"');";
			
			int outputMessage1 = processPostQuery(createUserQuery);
			
			if(outputMessage1 >=0) {
				
				String updateUserQuery = "update `rm_users` set srvid = '"+ planIdentification +"' where username='"+ processRequestData.getSelfcareUsername().trim() +"'";
				
				int updateOutput = processPostQuery(updateUserQuery);
				
				if (updateOutput >=0) return FreeRadiusConstants.SUCCESS;
				
				else  return FreeRadiusConstants.FAILURE + " Plan Updation Failed in Client Creation Operation";
				
			}
			
			else return FreeRadiusConstants.FAILURE + " Client Selfcare Data insertion Failed in Client Creation Operation.";
	
		} else{
			return FreeRadiusConstants.FAILURE + " Insert Query Failed in Client Creation Operation. ";
		}
		
	}
	
	private void processSelectQueryForUser(String query, FreeRadiusProcessRequestData processRequestData) {

		Connection connection = null;
		PreparedStatement ps = null;
		try{
			connection = connectionSetUp();
			ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			//logger.info(rs);
			while (rs.next()) {
				Long combquota = rs.getLong("combquota");
				Boolean limitcomb = rs.getBoolean("limitcomb");
				processRequestData.setCombquota(combquota);
				processRequestData.setLimitcomb(limitcomb);
			}
			
		} catch (SQLException e) {
			logger.info("SQLException while executing the command..."+e.getMessage());
		
		} catch(NullPointerException npe){
			logger.info("NullPointerException while Using connection/statements.."+npe.getMessage());
		} finally{
			if(null != ps)
				try {
					ps.close();
				} catch (SQLException e) {
					logger.info("SQLException while closing the connection");
				}
			if(null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					logger.info("SQLException while closing the connection");
				}
		}
		
	}

	private int processPostQuery(String query){
		Connection connection = null;
		PreparedStatement ps = null;
		try{
			connection = connectionSetUp();
			ps = connection.prepareStatement(query);
			int rs = ps.executeUpdate();
			//logger.info(rs);
			return rs;
			
		} catch (SQLException e) {
			logger.info("SQLException while executing the command..."+e.getMessage());
			return -1;
		
		} catch(NullPointerException npe){
			logger.info("NullPointerException while Using connection/statements.."+npe.getMessage());
			return -1;
		} finally{
			if(null != ps)
				try {
					ps.close();
				} catch (SQLException e) {
					logger.info("SQLException while closing the connection");
				}
			if(null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					logger.info("SQLException while closing the connection");
				}
		}
		
		
	}

	public String disConnectionCommandProcessing(FreeRadiusProcessRequestData processRequestData) throws ClientProtocolException, IOException {
		
		String userName = processRequestData.getSelfcareUsername().trim();
	
		String query = "DELETE FROM  rm_users , radcheck  using rm_users,radcheck where rm_users.username = radcheck.username and " +
				" rm_users.username ='" + userName + "'";
		
		int outputMessage = processPostQuery(query);
		
		if(outputMessage >=0){
		
			processRadiusSessionOperation(userName, FreeRadiusConstants.REQ_DISCONNECTION, null);
			
			String updateQuery = "Update rm_users set enableuser=0 where username='"+userName+"'";
		
			int output = processPostQuery(updateQuery);
			
			if(output >=0){
				return FreeRadiusConstants.SUCCESS;
			}
			else return FreeRadiusConstants.FAILURE + " Query Execution Failed";
			
		}
		
		else return FreeRadiusConstants.FAILURE + " Query Execution Failed";
	}
	

	private void processRadiusSessionOperation(String userName, String requestType, String value) {
		
		try{
			if(null != userName){
				String prefixCommand = null;
				
				ApiConnection con = ApiConnection.connect(mikrotik_HostName,Integer.parseInt(mikrotik_port));
				con.login(mikrotik_userName,mikrotik_password); 
				
				if(mikrotik_RouterType != null && mikrotik_RouterType.equalsIgnoreCase(FreeRadiusConstants.RADIUS_HOTSPOT))prefixCommand = "/ip/hotspot/";
				
				if(mikrotik_RouterType != null && mikrotik_RouterType.equalsIgnoreCase(FreeRadiusConstants.RADIUS_PPPOE)) prefixCommand = "/ppp/";
				
				if(prefixCommand !=null && requestType != null && userName != null && !userName.isEmpty()){
					
					if(requestType.equalsIgnoreCase(FreeRadiusConstants.REQ_DISCONNECTION)){
						List<Map<String, String>> res = con.execute(prefixCommand + "active/print where name=" + userName);
				        for (Map<String, String> attr : res) {
				            String id = attr.get(".id");
				            con.execute(prefixCommand + "active/remove .id=" + id);
				            System.out.println("Session Deleted For "+ userName);   
				        }
					}else if(requestType.equalsIgnoreCase(FreeRadiusConstants.REQ_CHANGE_PLAN)){
						if(null != value){
							String name = "<"+ mikrotik_RouterType + "-" + userName + ">";
							String printCommand = "/queue/simple/print where name='"+name+"'";
							System.out.println("Specific user command : " + printCommand);
							List<Map<String, String>> res = con.execute(printCommand);
							
							for (Map<String, String> attr : res) {
							    String id = attr.get(".id");
							    String command = "/queue/simple/set max-limit=" + value + " limit-at=" + value + " .id="+id;
							    System.out.println("Executing command : " + command);
							    con.execute(command);
							    System.out.println("plan changed Successfully. bandwidth="+value);
							}
						}	
					}

				} else{
					System.out.println("Please Configure the Mikrotik Data Properly");
				
				}
				
			}	
			
		} catch (MikrotikApiException e) {
			logger.error("Mikrotik Api Exception:" + e.getCause().getMessage());
		} catch (InterruptedException e) {
			logger.error("Interrupted Exception:"+ e.getCause().getMessage());
		}
	}

	public String createNasCommandProcessing(FreeRadiusProcessRequestData processRequestData) throws JSONException {
		
		JSONObject jsonObject = new JSONObject(processRequestData.getProduct());
		
		String nasName = jsonObject.getString("nasname");
		String shortName = jsonObject.getString("shortname");
		String secret = jsonObject.getString("secret");
		String description = jsonObject.getString("description");
		
		String query = "insert into nas(nasname, shortname, type, secret, description,starospassword,ciscobwmode," +
				" apiusername,apipassword,enableapi) values('" + nasName + "','" + shortName + "',0,'" + secret + "','" + description +
				"','',0,'','',0);";
		
		int outputMessage = processPostQuery(query);
		
		if(outputMessage >=0){
			
			return FreeRadiusConstants.SUCCESS;
		}
		
		else return FreeRadiusConstants.FAILURE + " Insert Query Execution Failed in Nas Creation";
	}

	public String createNasServiceCommandProcessing(FreeRadiusProcessRequestData processRequestData) throws JSONException {
		
		int limitul=0,renew=0,limitdl=0, islimitcomb=0;
		
		JSONObject jsonObject = new JSONObject(processRequestData.getProduct());
		
		// selectQuery = "select Max(srvid)+1 as id from rm_services";
		//logger.info("executing query:"+selectQuery);
		
		//int id = processSelectQuery(selectQuery);
		
		/*if(jsonObject.has("srvid")){
			id = jsonObject.getInt("srvid");
		}*/
		
		int id = jsonObject.getInt("srvid");
		String srvName = jsonObject.getString("srvname");
		Long downrate = jsonObject.getLong("downrate");
		Long uprate = jsonObject.getLong("uprate");
		Long nextsrvid = jsonObject.getLong("nextsrvid");
		Long trafficunitdl = jsonObject.getLong("trafficunitdl");
		Boolean renewB = jsonObject.getBoolean("renew");
		Boolean limitulB = jsonObject.getBoolean("limitul");
		Boolean limitdlB = jsonObject.getBoolean("limitdl");	
		Boolean islimitcombB = jsonObject.getBoolean("islimitcomb");	
		
		
		if(limitulB){
			limitul = 1;
		}
		if(limitdlB){
			limitdl = 1;
		}
		if(renewB){
			renew = 1;
		}
		if(islimitcombB){
			islimitcomb = 1;
		}
		
		if(id >=0){
			
			String query = " INSERT INTO rm_services(srvid, srvname, descr, downrate, uprate, limitdl, limitul, limitcomb, " +
					"limitexpiration, limituptime, poolname, unitprice, unitpriceadd, timebaseexp, timebaseonline, timeunitexp," +
					"timeunitonline, trafficunitdl, trafficunitul, trafficunitcomb, inittimeexp, inittimeonline, initdl," +
					"initul, inittotal, srvtype, timeaddmodeexp, timeaddmodeonline, trafficaddmode, monthly, enaddcredits," +
					"minamount, minamountadd, resetcounters, pricecalcdownload, pricecalcupload, pricecalcuptime, unitpricetax," +
					"unitpriceaddtax, enableburst, dlburstlimit, ulburstlimit, dlburstthreshold, ulburstthreshold, dlbursttime," +
					"ulbursttime, enableservice, dlquota, ulquota, combquota, timequota, priority, nextsrvid, dailynextsrvid, " +
					"disnextsrvid, availucp, renew, policymapdl, policymapul, custattr, carryover, gentftp, cmcfg," +
					"advcmcfg, addamount, ignstatip) VALUES (" + id + ",'" + srvName +
					"',''," + downrate +"," + uprate +"," + limitdl + "," + limitul + ","+ islimitcomb +",1,0,'',0,0,2,0,0,0," +
					"0,0,0,0,0,'0',0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0," +
					"0,0,0,0,0,0,0,1,0,0,'"+ trafficunitdl +"',0,8,'" + nextsrvid +"'" 
					+",'-1','-1',0," + renew + ",'','','',0,0,'',0,1,0);";
			
			int outputMessage = processPostQuery(query);
			
			if(outputMessage >=0){
				
				String allowednasQuery = "Insert into rm_allowednases  Select (select max(srvid) from rm_services),id from nas";
				
				int allowesNasOutput = processPostQuery(allowednasQuery);
				
				if(allowesNasOutput >=0) return FreeRadiusConstants.SUCCESS;
				
				else return FreeRadiusConstants.FAILURE + " insert Query Execution Failed in allowednases Operation.";
				
			}
			
			else return FreeRadiusConstants.FAILURE + " Insert Query Execution Failed in Create Service Operation.";
		}
		
		else return FreeRadiusConstants.FAILURE + " Query Execution Failed in Select Statement/ Srvid Value Error.";
		
	}

	private int processSelectQuery(String selectQuery) {
		Connection connection = null;
		PreparedStatement ps = null;
		try{
			connection = connectionSetUp();
			ps = connection.prepareStatement(selectQuery);
			ResultSet rs = ps.executeQuery();
		     rs.next();
			logger.info(rs.getInt("id"));
			return rs.getInt("id");
			
		} catch (SQLException e) {
			logger.info("SQLException while executing the command..."+e.getMessage());
			return -1;
		
		} catch(NullPointerException npe){
			logger.info("NullPointerException while Using connection/statements.."+npe.getMessage());
			return -1;
		} finally{
			if(null != ps)
				try {
					ps.close();
				} catch (SQLException e) {
					logger.info("SQLException while closing the connection");
				}
			if(null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					logger.info("SQLException while closing the connection");
				}
		}
	}

	public String changeCredentialsCommandProcessing(FreeRadiusProcessRequestData processRequestData) throws JSONException {	
		
		JSONObject jsonObject = new JSONObject(processRequestData.getProduct());
		
		String newUserName = jsonObject.getString("newUserName");
		String existingUserName = jsonObject.getString("existingUserName");
		String newPassword = jsonObject.getString("newPassword").trim();
		String existingPassword = jsonObject.getString("existingPassword");	
			
		
		// wrte execute command
		if(newUserName.equalsIgnoreCase(existingUserName) && newPassword.equalsIgnoreCase(existingPassword) ){
			logger.info("Logn credentials are not changed...");
			return FreeRadiusConstants.SUCCESS;	
		}else{
			
			String password = MD5(newPassword);
			
			int acctype = 0;
			
			if(newUserName.contains(":")){
				acctype = 1;
				newPassword = "";
			}
			
			String query = "update rm_users SET username='" + newUserName.trim() + "', password='" + password + "', " +
					"acctype=" + acctype + " WHERE username ='" + existingUserName.trim() +"'";
			
			int outputMessage = processPostQuery(query);
					
			if(outputMessage >=0){	
				
				String rcChangePasswordQuery = "update radcheck SET username='"+newUserName.trim()+"', value='"+newPassword+"' " +
						" WHERE username ='" + existingUserName.trim() +"' and attribute='Cleartext-Password'";
				
				String rcSimultaniousquery = "update radcheck SET username='"+newUserName.trim()+"'" +
						" WHERE username ='" + existingUserName.trim() + "' and attribute='Simultaneous-Use'";
				
				int outputMessage1 = processPostQuery(rcChangePasswordQuery);
				int outputMessage2 = processPostQuery(rcSimultaniousquery);
				
				if(outputMessage1 >=0){	
					
					if (outputMessage2 >=0) {
						
						return FreeRadiusConstants.SUCCESS;
						
					}else return FreeRadiusConstants.FAILURE + " Update radcheck 'Simultaneous-Use' attribute value Query Execution Failed in change Credential Operation";
				
				} else return FreeRadiusConstants.FAILURE + " Update radcheck Query Execution Failed in change Credential Operation";
					
			}
					
			else return FreeRadiusConstants.FAILURE + " Update Query Execution Failed in change Credential Operation";
		}
		
	}

	public void heartBeatCommandProcessing() {
		
		logger.info("HeartBeatCalling");
		
		String query = "update radacct set AcctstopTime = AcctStartTime + Interval AcctSessionTime SECOND " +
				"Where AcctStopTime is Null and _acctTime < Now() - Interval '10' MINUTE;";
		
		int outputMessage = processPostQuery(query);
		
		if(outputMessage < 0){		
			logger.error("Heart Beat Command Execution Failed...");
		}
	}
	
}