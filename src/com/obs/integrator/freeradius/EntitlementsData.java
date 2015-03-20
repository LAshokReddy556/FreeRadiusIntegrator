package com.obs.integrator.freeradius;

public class EntitlementsData {

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
	
	
	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPrdetailsId() {
		return prdetailsId;
	}

	public void setPrdetailsId(Long prdetailsId) {
		this.prdetailsId = prdetailsId;
	}

	public String getProvisioingSystem() {
		return provisioingSystem;
	}

	public void setProvisioingSystem(String provisioingSystem) {
		this.provisioingSystem = provisioingSystem;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSelfcareUsername() {
		return selfcareUsername;
	}

	public void setSelfcareUsername(String selfcareUsername) {
		this.selfcareUsername = selfcareUsername;
	}

	public String getSelfcarePassword() {
		return selfcarePassword;
	}

	public void setSelfcarePassword(String selfcarePassword) {
		this.selfcarePassword = selfcarePassword;
	}
	
}
