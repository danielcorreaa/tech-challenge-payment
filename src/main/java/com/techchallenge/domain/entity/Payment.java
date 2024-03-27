package com.techchallenge.domain.entity;

import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.domain.valueobject.Validation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Payment {

	private String externalReference;
	private String title;
	private String description;
	private String notificationUrl;
	private String orderStatus;
	private List<Item> items;
	private LocalDateTime createTime;
	private Boolean sent;
	private String cpfCustomer;
	private LocalDateTime expirationDate;

	public Payment(String externalReference, String title, String description, String notificationUrl,
				   List<Item> items,
				   String cpfCustomer) {
		this.externalReference =  Validation.validateExternalReference(externalReference);
		this.title = Validation.validateTitle(title);
		this.description = Validation.validateDescription(description);
		this.notificationUrl = notificationUrl;
		this.items = Validation.validateItems(items);
		this.sent = Boolean.FALSE;
		this.createTime = LocalDateTime.now();
		this.cpfCustomer = cpfCustomer;
	}

	private Payment(String externalReference, String title, String description, String notificationUrl,
					String orderStatus, List<Item> items, LocalDateTime createTime, Boolean sent,
					LocalDateTime expirationDate, String cpfCustomer) {
		this.externalReference = externalReference;
		this.title = title;
		this.description = description;
		this.notificationUrl = notificationUrl;
		this.orderStatus = orderStatus;
		this.items = items;
		this.createTime = createTime;
		this.sent = sent;
		this.expirationDate = expirationDate;
		this.cpfCustomer = cpfCustomer;
	}



	public String getExternalReference() {
		return externalReference;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getNotificationUrl() {
		if(Optional.ofNullable(notificationUrl).orElse("").isEmpty()){
			notificationUrl = "https://eo5swulfz2hoccg.m.pipedream.net";
		}
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	public BigDecimal getTotalAmount() {
		return Optional.ofNullable(items).stream().flatMap(Collection::stream).map(Item::getUnitPrice)
				.reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	public List<Item> getItems() {
		return items;
	}

	public String getOrderStatus() {
		return orderStatus;
	}


	public Boolean isPaid() {
		return Optional.ofNullable(orderStatus).orElse("").equals("paid");
	}

	public Payment toSend(){
		if(Boolean.TRUE.equals(isPaid())) {
			this.sent = Boolean.TRUE;
		}
		return this;
	}

	public Payment toSendExpiration(){
		if(Boolean.FALSE.equals(isPaid())) {
			this.sent = Boolean.TRUE;
			this.changeStatus("expired");
		}
		return this;
	}

	public Payment changeStatus(String status){
		orderStatus = status;
		return this;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public Boolean getSent() {
		return sent;
	}

	public Optional<String> getCpfCustomer() {
		return Optional.ofNullable(cpfCustomer);
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public static Payment toPayment(String externalReference, String title, String description, String notificationUrl,
									String orderStatus, List<Item> items, LocalDateTime createTime,
									Boolean sent,LocalDateTime expirationDate, String cpfCustomer){
		return new Payment(externalReference, title, description, notificationUrl,
				 orderStatus,items, createTime, sent, expirationDate, cpfCustomer);
	}

	@Override
	public String toString() {
		return super.toString();
	}


}
