
package com.obs.integrator.freeradius;


public class FreeRadiusProcessRequestData {
	
	private Long id;
	private Long prdetailsId;
	private String provisioingSystem;

	private Long serviceId;
	private String product;
	private String hardwareId;

	private String requestType;
	private String itemCode;
	private String itemDescription;

	private Long clientId;
	private String accountNo;
	private String firstName;
	private String lastName;
	
	private String selfcareUsername;
	private String selfcarePassword;
	private String email;
	private Long combquota;
	private Boolean limitcomb;

	
	public FreeRadiusProcessRequestData(Long id, Long prdetailsId,String provisioingSystem, Long serviceId, String product,String hardwareId, 
			String requestType, String itemCode,String itemDescription, Long clientId, String accountNo,String firstName, String lastName,
			String selfcareUsername,String selfcarePassword, String email) {
		
		this.id=id;
        this.prdetailsId=prdetailsId;
        this.provisioingSystem=provisioingSystem;
        this.serviceId=serviceId;
        this.product=product; 
        this.hardwareId=hardwareId;
        
        this.requestType=requestType;
        this.itemCode=itemCode;
        this.itemDescription=itemDescription;
        this.clientId=clientId;
        this.accountNo=accountNo;    
        this.firstName=firstName;
        this.lastName=lastName;
        
        this.selfcarePassword = selfcarePassword;
        this.selfcareUsername = selfcareUsername;
        this.email = email;
        this.limitcomb = false;
	}

	public Long getId() {
		return id;
	}

	public Long getPrdetailsId() {
		return prdetailsId;
	}

	public String getProvisioingSystem() {
		return provisioingSystem;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getProduct() {
		return product;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getSelfcareUsername() {
		return selfcareUsername;
	}

	public String getSelfcarePassword() {
		return selfcarePassword;
	}

	public String getEmail() {
		return email;
	}

	public Long getCombquota() {
		return combquota;
	}

	public void setCombquota(Long combquota) {
		this.combquota = combquota;
	}

	public Boolean getLimitcomb() {
		return limitcomb;
	}

	public void setLimitcomb(Boolean limitcomb) {
		this.limitcomb = limitcomb;
	}
	
	
		
}
