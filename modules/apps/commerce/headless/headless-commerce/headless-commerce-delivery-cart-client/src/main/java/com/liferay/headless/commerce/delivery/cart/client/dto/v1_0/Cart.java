/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0.CartSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Cart implements Cloneable, Serializable {

	public static Cart toDTO(String json) {
		return CartSerDes.toDTO(json);
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setAccount(
		UnsafeSupplier<String, Exception> accountUnsafeSupplier) {

		try {
			account = accountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String account;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		try {
			accountId = accountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountId;

	public Attachment[] getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;
	}

	public void setAttachments(
		UnsafeSupplier<Attachment[], Exception> attachmentsUnsafeSupplier) {

		try {
			attachments = attachmentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Attachment[] attachments;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		try {
			author = authorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String author;

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public void setBillingAddress(
		UnsafeSupplier<Address, Exception> billingAddressUnsafeSupplier) {

		try {
			billingAddress = billingAddressUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Address billingAddress;

	public String getBillingAddressExternalReferenceCode() {
		return billingAddressExternalReferenceCode;
	}

	public void setBillingAddressExternalReferenceCode(
		String billingAddressExternalReferenceCode) {

		this.billingAddressExternalReferenceCode =
			billingAddressExternalReferenceCode;
	}

	public void setBillingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			billingAddressExternalReferenceCodeUnsafeSupplier) {

		try {
			billingAddressExternalReferenceCode =
				billingAddressExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String billingAddressExternalReferenceCode;

	public Long getBillingAddressId() {
		return billingAddressId;
	}

	public void setBillingAddressId(Long billingAddressId) {
		this.billingAddressId = billingAddressId;
	}

	public void setBillingAddressId(
		UnsafeSupplier<Long, Exception> billingAddressIdUnsafeSupplier) {

		try {
			billingAddressId = billingAddressIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long billingAddressId;

	public CartItem[] getCartItems() {
		return cartItems;
	}

	public void setCartItems(CartItem[] cartItems) {
		this.cartItems = cartItems;
	}

	public void setCartItems(
		UnsafeSupplier<CartItem[], Exception> cartItemsUnsafeSupplier) {

		try {
			cartItems = cartItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CartItem[] cartItems;

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public void setChannelId(
		UnsafeSupplier<Long, Exception> channelIdUnsafeSupplier) {

		try {
			channelId = channelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long channelId;

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public void setCouponCode(
		UnsafeSupplier<String, Exception> couponCodeUnsafeSupplier) {

		try {
			couponCode = couponCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String couponCode;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public void setCurrencyCode(
		UnsafeSupplier<String, Exception> currencyCodeUnsafeSupplier) {

		try {
			currencyCode = currencyCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String currencyCode;

	public String getCurrencyExternalReferenceCode() {
		return currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		String currencyExternalReferenceCode) {

		this.currencyExternalReferenceCode = currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			currencyExternalReferenceCodeUnsafeSupplier) {

		try {
			currencyExternalReferenceCode =
				currencyExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String currencyExternalReferenceCode;

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public void setCurrencyId(
		UnsafeSupplier<Long, Exception> currencyIdUnsafeSupplier) {

		try {
			currencyId = currencyIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long currencyId;

	public Map<String, ?> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, ?> customFields) {
		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier<Map<String, ?>, Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> customFields;

	public Long getDeliveryTermId() {
		return deliveryTermId;
	}

	public void setDeliveryTermId(Long deliveryTermId) {
		this.deliveryTermId = deliveryTermId;
	}

	public void setDeliveryTermId(
		UnsafeSupplier<Long, Exception> deliveryTermIdUnsafeSupplier) {

		try {
			deliveryTermId = deliveryTermIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long deliveryTermId;

	public String getDeliveryTermLabel() {
		return deliveryTermLabel;
	}

	public void setDeliveryTermLabel(String deliveryTermLabel) {
		this.deliveryTermLabel = deliveryTermLabel;
	}

	public void setDeliveryTermLabel(
		UnsafeSupplier<String, Exception> deliveryTermLabelUnsafeSupplier) {

		try {
			deliveryTermLabel = deliveryTermLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String deliveryTermLabel;

	public String[] getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(String[] errorMessages) {
		this.errorMessages = errorMessages;
	}

	public void setErrorMessages(
		UnsafeSupplier<String[], Exception> errorMessagesUnsafeSupplier) {

		try {
			errorMessages = errorMessagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] errorMessages;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public String getFriendlyURLSeparator() {
		return friendlyURLSeparator;
	}

	public void setFriendlyURLSeparator(String friendlyURLSeparator) {
		this.friendlyURLSeparator = friendlyURLSeparator;
	}

	public void setFriendlyURLSeparator(
		UnsafeSupplier<String, Exception> friendlyURLSeparatorUnsafeSupplier) {

		try {
			friendlyURLSeparator = friendlyURLSeparatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String friendlyURLSeparator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Date getLastPriceUpdateDate() {
		return lastPriceUpdateDate;
	}

	public void setLastPriceUpdateDate(Date lastPriceUpdateDate) {
		this.lastPriceUpdateDate = lastPriceUpdateDate;
	}

	public void setLastPriceUpdateDate(
		UnsafeSupplier<Date, Exception> lastPriceUpdateDateUnsafeSupplier) {

		try {
			lastPriceUpdateDate = lastPriceUpdateDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastPriceUpdateDate;

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		try {
			modifiedDate = modifiedDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date modifiedDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public CartComment[] getNotes() {
		return notes;
	}

	public void setNotes(CartComment[] notes) {
		this.notes = notes;
	}

	public void setNotes(
		UnsafeSupplier<CartComment[], Exception> notesUnsafeSupplier) {

		try {
			notes = notesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CartComment[] notes;

	public Status getOrderStatusInfo() {
		return orderStatusInfo;
	}

	public void setOrderStatusInfo(Status orderStatusInfo) {
		this.orderStatusInfo = orderStatusInfo;
	}

	public void setOrderStatusInfo(
		UnsafeSupplier<Status, Exception> orderStatusInfoUnsafeSupplier) {

		try {
			orderStatusInfo = orderStatusInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status orderStatusInfo;

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setOrderType(
		UnsafeSupplier<String, Exception> orderTypeUnsafeSupplier) {

		try {
			orderType = orderTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderType;

	public String getOrderTypeExternalReferenceCode() {
		return orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		String orderTypeExternalReferenceCode) {

		this.orderTypeExternalReferenceCode = orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderTypeExternalReferenceCodeUnsafeSupplier) {

		try {
			orderTypeExternalReferenceCode =
				orderTypeExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderTypeExternalReferenceCode;

	public Long getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(Long orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public void setOrderTypeId(
		UnsafeSupplier<Long, Exception> orderTypeIdUnsafeSupplier) {

		try {
			orderTypeId = orderTypeIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderTypeId;

	public String getOrderUUID() {
		return orderUUID;
	}

	public void setOrderUUID(String orderUUID) {
		this.orderUUID = orderUUID;
	}

	public void setOrderUUID(
		UnsafeSupplier<String, Exception> orderUUIDUnsafeSupplier) {

		try {
			orderUUID = orderUUIDUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderUUID;

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setPaymentMethod(
		UnsafeSupplier<String, Exception> paymentMethodUnsafeSupplier) {

		try {
			paymentMethod = paymentMethodUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paymentMethod;

	public String getPaymentMethodLabel() {
		return paymentMethodLabel;
	}

	public void setPaymentMethodLabel(String paymentMethodLabel) {
		this.paymentMethodLabel = paymentMethodLabel;
	}

	public void setPaymentMethodLabel(
		UnsafeSupplier<String, Exception> paymentMethodLabelUnsafeSupplier) {

		try {
			paymentMethodLabel = paymentMethodLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paymentMethodLabel;

	public Integer getPaymentMethodType() {
		return paymentMethodType;
	}

	public void setPaymentMethodType(Integer paymentMethodType) {
		this.paymentMethodType = paymentMethodType;
	}

	public void setPaymentMethodType(
		UnsafeSupplier<Integer, Exception> paymentMethodTypeUnsafeSupplier) {

		try {
			paymentMethodType = paymentMethodTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paymentMethodType;

	public Integer getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public void setPaymentStatus(
		UnsafeSupplier<Integer, Exception> paymentStatusUnsafeSupplier) {

		try {
			paymentStatus = paymentStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paymentStatus;

	public Status getPaymentStatusInfo() {
		return paymentStatusInfo;
	}

	public void setPaymentStatusInfo(Status paymentStatusInfo) {
		this.paymentStatusInfo = paymentStatusInfo;
	}

	public void setPaymentStatusInfo(
		UnsafeSupplier<Status, Exception> paymentStatusInfoUnsafeSupplier) {

		try {
			paymentStatusInfo = paymentStatusInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status paymentStatusInfo;

	public String getPaymentStatusLabel() {
		return paymentStatusLabel;
	}

	public void setPaymentStatusLabel(String paymentStatusLabel) {
		this.paymentStatusLabel = paymentStatusLabel;
	}

	public void setPaymentStatusLabel(
		UnsafeSupplier<String, Exception> paymentStatusLabelUnsafeSupplier) {

		try {
			paymentStatusLabel = paymentStatusLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paymentStatusLabel;

	public Long getPaymentTermId() {
		return paymentTermId;
	}

	public void setPaymentTermId(Long paymentTermId) {
		this.paymentTermId = paymentTermId;
	}

	public void setPaymentTermId(
		UnsafeSupplier<Long, Exception> paymentTermIdUnsafeSupplier) {

		try {
			paymentTermId = paymentTermIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long paymentTermId;

	public String getPaymentTermLabel() {
		return paymentTermLabel;
	}

	public void setPaymentTermLabel(String paymentTermLabel) {
		this.paymentTermLabel = paymentTermLabel;
	}

	public void setPaymentTermLabel(
		UnsafeSupplier<String, Exception> paymentTermLabelUnsafeSupplier) {

		try {
			paymentTermLabel = paymentTermLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paymentTermLabel;

	public String getPrintedNote() {
		return printedNote;
	}

	public void setPrintedNote(String printedNote) {
		this.printedNote = printedNote;
	}

	public void setPrintedNote(
		UnsafeSupplier<String, Exception> printedNoteUnsafeSupplier) {

		try {
			printedNote = printedNoteUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String printedNote;

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(
		UnsafeSupplier<String, Exception> purchaseOrderNumberUnsafeSupplier) {

		try {
			purchaseOrderNumber = purchaseOrderNumberUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String purchaseOrderNumber;

	public Date getRequestedDeliveryDate() {
		return requestedDeliveryDate;
	}

	public void setRequestedDeliveryDate(Date requestedDeliveryDate) {
		this.requestedDeliveryDate = requestedDeliveryDate;
	}

	public void setRequestedDeliveryDate(
		UnsafeSupplier<Date, Exception> requestedDeliveryDateUnsafeSupplier) {

		try {
			requestedDeliveryDate = requestedDeliveryDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date requestedDeliveryDate;

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public void setShippingAddress(
		UnsafeSupplier<Address, Exception> shippingAddressUnsafeSupplier) {

		try {
			shippingAddress = shippingAddressUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Address shippingAddress;

	public String getShippingAddressExternalReferenceCode() {
		return shippingAddressExternalReferenceCode;
	}

	public void setShippingAddressExternalReferenceCode(
		String shippingAddressExternalReferenceCode) {

		this.shippingAddressExternalReferenceCode =
			shippingAddressExternalReferenceCode;
	}

	public void setShippingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			shippingAddressExternalReferenceCodeUnsafeSupplier) {

		try {
			shippingAddressExternalReferenceCode =
				shippingAddressExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shippingAddressExternalReferenceCode;

	public Long getShippingAddressId() {
		return shippingAddressId;
	}

	public void setShippingAddressId(Long shippingAddressId) {
		this.shippingAddressId = shippingAddressId;
	}

	public void setShippingAddressId(
		UnsafeSupplier<Long, Exception> shippingAddressIdUnsafeSupplier) {

		try {
			shippingAddressId = shippingAddressIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long shippingAddressId;

	public String getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public void setShippingMethod(
		UnsafeSupplier<String, Exception> shippingMethodUnsafeSupplier) {

		try {
			shippingMethod = shippingMethodUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shippingMethod;

	public String getShippingOption() {
		return shippingOption;
	}

	public void setShippingOption(String shippingOption) {
		this.shippingOption = shippingOption;
	}

	public void setShippingOption(
		UnsafeSupplier<String, Exception> shippingOptionUnsafeSupplier) {

		try {
			shippingOption = shippingOptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shippingOption;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String status;

	public Step[] getSteps() {
		return steps;
	}

	public void setSteps(Step[] steps) {
		this.steps = steps;
	}

	public void setSteps(
		UnsafeSupplier<Step[], Exception> stepsUnsafeSupplier) {

		try {
			steps = stepsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Step[] steps;

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public void setSummary(
		UnsafeSupplier<Summary, Exception> summaryUnsafeSupplier) {

		try {
			summary = summaryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Summary summary;

	public Boolean getUseAsBilling() {
		return useAsBilling;
	}

	public void setUseAsBilling(Boolean useAsBilling) {
		this.useAsBilling = useAsBilling;
	}

	public void setUseAsBilling(
		UnsafeSupplier<Boolean, Exception> useAsBillingUnsafeSupplier) {

		try {
			useAsBilling = useAsBillingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean useAsBilling;

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public void setValid(
		UnsafeSupplier<Boolean, Exception> validUnsafeSupplier) {

		try {
			valid = validUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean valid;

	public Status getWorkflowStatusInfo() {
		return workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(Status workflowStatusInfo) {
		this.workflowStatusInfo = workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(
		UnsafeSupplier<Status, Exception> workflowStatusInfoUnsafeSupplier) {

		try {
			workflowStatusInfo = workflowStatusInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status workflowStatusInfo;

	@Override
	public Cart clone() throws CloneNotSupportedException {
		return (Cart)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Cart)) {
			return false;
		}

		Cart cart = (Cart)object;

		return Objects.equals(toString(), cart.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CartSerDes.toJSON(this);
	}

}