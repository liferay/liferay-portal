/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("PlacedOrder")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PlacedOrder")
public class PlacedOrder implements Serializable {

	public static PlacedOrder toDTO(String json) {
		return ObjectMapperUtil.readValue(PlacedOrder.class, json);
	}

	public static PlacedOrder unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PlacedOrder.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAccount() {
		if (_accountSupplier != null) {
			account = _accountSupplier.get();

			_accountSupplier = null;
		}

		return account;
	}

	public void setAccount(String account) {
		this.account = account;

		_accountSupplier = null;
	}

	@JsonIgnore
	public void setAccount(
		UnsafeSupplier<String, Exception> accountUnsafeSupplier) {

		_accountSupplier = () -> {
			try {
				return accountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String account;

	@JsonIgnore
	private Supplier<String> _accountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAccountId() {
		if (_accountIdSupplier != null) {
			accountId = _accountIdSupplier.get();

			_accountIdSupplier = null;
		}

		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;

		_accountIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		_accountIdSupplier = () -> {
			try {
				return accountIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Attachment[] getAttachments() {
		if (_attachmentsSupplier != null) {
			attachments = _attachmentsSupplier.get();

			_attachmentsSupplier = null;
		}

		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;

		_attachmentsSupplier = null;
	}

	@JsonIgnore
	public void setAttachments(
		UnsafeSupplier<Attachment[], Exception> attachmentsUnsafeSupplier) {

		_attachmentsSupplier = () -> {
			try {
				return attachmentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Attachment[] attachments;

	@JsonIgnore
	private Supplier<Attachment[]> _attachmentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAuthor() {
		if (_authorSupplier != null) {
			author = _authorSupplier.get();

			_authorSupplier = null;
		}

		return author;
	}

	public void setAuthor(String author) {
		this.author = author;

		_authorSupplier = null;
	}

	@JsonIgnore
	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		_authorSupplier = () -> {
			try {
				return authorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String author;

	@JsonIgnore
	private Supplier<String> _authorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAuthorId() {
		if (_authorIdSupplier != null) {
			authorId = _authorIdSupplier.get();

			_authorIdSupplier = null;
		}

		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;

		_authorIdSupplier = null;
	}

	@JsonIgnore
	public void setAuthorId(
		UnsafeSupplier<Long, Exception> authorIdUnsafeSupplier) {

		_authorIdSupplier = () -> {
			try {
				return authorIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long authorId;

	@JsonIgnore
	private Supplier<Long> _authorIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getChannelId() {
		if (_channelIdSupplier != null) {
			channelId = _channelIdSupplier.get();

			_channelIdSupplier = null;
		}

		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;

		_channelIdSupplier = null;
	}

	@JsonIgnore
	public void setChannelId(
		UnsafeSupplier<Long, Exception> channelIdUnsafeSupplier) {

		_channelIdSupplier = () -> {
			try {
				return channelIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long channelId;

	@JsonIgnore
	private Supplier<Long> _channelIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCouponCode() {
		if (_couponCodeSupplier != null) {
			couponCode = _couponCodeSupplier.get();

			_couponCodeSupplier = null;
		}

		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;

		_couponCodeSupplier = null;
	}

	@JsonIgnore
	public void setCouponCode(
		UnsafeSupplier<String, Exception> couponCodeUnsafeSupplier) {

		_couponCodeSupplier = () -> {
			try {
				return couponCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String couponCode;

	@JsonIgnore
	private Supplier<String> _couponCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCurrencyCode() {
		if (_currencyCodeSupplier != null) {
			currencyCode = _currencyCodeSupplier.get();

			_currencyCodeSupplier = null;
		}

		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;

		_currencyCodeSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyCode(
		UnsafeSupplier<String, Exception> currencyCodeUnsafeSupplier) {

		_currencyCodeSupplier = () -> {
			try {
				return currencyCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String currencyCode;

	@JsonIgnore
	private Supplier<String> _currencyCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, ?> getCustomFields() {
		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(Map<String, ?> customFields) {
		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier<Map<String, ?>, Exception> customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ?> customFields;

	@JsonIgnore
	private Supplier<Map<String, ?>> _customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getErrorMessages() {
		if (_errorMessagesSupplier != null) {
			errorMessages = _errorMessagesSupplier.get();

			_errorMessagesSupplier = null;
		}

		return errorMessages;
	}

	public void setErrorMessages(String[] errorMessages) {
		this.errorMessages = errorMessages;

		_errorMessagesSupplier = null;
	}

	@JsonIgnore
	public void setErrorMessages(
		UnsafeSupplier<String[], Exception> errorMessagesUnsafeSupplier) {

		_errorMessagesSupplier = () -> {
			try {
				return errorMessagesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] errorMessages;

	@JsonIgnore
	private Supplier<String[]> _errorMessagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFriendlyURLSeparator() {
		if (_friendlyURLSeparatorSupplier != null) {
			friendlyURLSeparator = _friendlyURLSeparatorSupplier.get();

			_friendlyURLSeparatorSupplier = null;
		}

		return friendlyURLSeparator;
	}

	public void setFriendlyURLSeparator(String friendlyURLSeparator) {
		this.friendlyURLSeparator = friendlyURLSeparator;

		_friendlyURLSeparatorSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyURLSeparator(
		UnsafeSupplier<String, Exception> friendlyURLSeparatorUnsafeSupplier) {

		_friendlyURLSeparatorSupplier = () -> {
			try {
				return friendlyURLSeparatorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String friendlyURLSeparator;

	@JsonIgnore
	private Supplier<String> _friendlyURLSeparatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getLastPriceUpdateDate() {
		if (_lastPriceUpdateDateSupplier != null) {
			lastPriceUpdateDate = _lastPriceUpdateDateSupplier.get();

			_lastPriceUpdateDateSupplier = null;
		}

		return lastPriceUpdateDate;
	}

	public void setLastPriceUpdateDate(Date lastPriceUpdateDate) {
		this.lastPriceUpdateDate = lastPriceUpdateDate;

		_lastPriceUpdateDateSupplier = null;
	}

	@JsonIgnore
	public void setLastPriceUpdateDate(
		UnsafeSupplier<Date, Exception> lastPriceUpdateDateUnsafeSupplier) {

		_lastPriceUpdateDateSupplier = () -> {
			try {
				return lastPriceUpdateDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastPriceUpdateDate;

	@JsonIgnore
	private Supplier<Date> _lastPriceUpdateDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getModifiedDate() {
		if (_modifiedDateSupplier != null) {
			modifiedDate = _modifiedDateSupplier.get();

			_modifiedDateSupplier = null;
		}

		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;

		_modifiedDateSupplier = null;
	}

	@JsonIgnore
	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		_modifiedDateSupplier = () -> {
			try {
				return modifiedDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date modifiedDate;

	@JsonIgnore
	private Supplier<Date> _modifiedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Order Name")
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getOrderStatusInfo() {
		if (_orderStatusInfoSupplier != null) {
			orderStatusInfo = _orderStatusInfoSupplier.get();

			_orderStatusInfoSupplier = null;
		}

		return orderStatusInfo;
	}

	public void setOrderStatusInfo(Status orderStatusInfo) {
		this.orderStatusInfo = orderStatusInfo;

		_orderStatusInfoSupplier = null;
	}

	@JsonIgnore
	public void setOrderStatusInfo(
		UnsafeSupplier<Status, Exception> orderStatusInfoUnsafeSupplier) {

		_orderStatusInfoSupplier = () -> {
			try {
				return orderStatusInfoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status orderStatusInfo;

	@JsonIgnore
	private Supplier<Status> _orderStatusInfoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOrderType() {
		if (_orderTypeSupplier != null) {
			orderType = _orderTypeSupplier.get();

			_orderTypeSupplier = null;
		}

		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;

		_orderTypeSupplier = null;
	}

	@JsonIgnore
	public void setOrderType(
		UnsafeSupplier<String, Exception> orderTypeUnsafeSupplier) {

		_orderTypeSupplier = () -> {
			try {
				return orderTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String orderType;

	@JsonIgnore
	private Supplier<String> _orderTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getOrderTypeExternalReferenceCode() {
		if (_orderTypeExternalReferenceCodeSupplier != null) {
			orderTypeExternalReferenceCode =
				_orderTypeExternalReferenceCodeSupplier.get();

			_orderTypeExternalReferenceCodeSupplier = null;
		}

		return orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		String orderTypeExternalReferenceCode) {

		this.orderTypeExternalReferenceCode = orderTypeExternalReferenceCode;

		_orderTypeExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderTypeExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderTypeExternalReferenceCodeUnsafeSupplier) {

		_orderTypeExternalReferenceCodeSupplier = () -> {
			try {
				return orderTypeExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String orderTypeExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderTypeExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOrderTypeId() {
		if (_orderTypeIdSupplier != null) {
			orderTypeId = _orderTypeIdSupplier.get();

			_orderTypeIdSupplier = null;
		}

		return orderTypeId;
	}

	public void setOrderTypeId(Long orderTypeId) {
		this.orderTypeId = orderTypeId;

		_orderTypeIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderTypeId(
		UnsafeSupplier<Long, Exception> orderTypeIdUnsafeSupplier) {

		_orderTypeIdSupplier = () -> {
			try {
				return orderTypeIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long orderTypeId;

	@JsonIgnore
	private Supplier<Long> _orderTypeIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOrderUUID() {
		if (_orderUUIDSupplier != null) {
			orderUUID = _orderUUIDSupplier.get();

			_orderUUIDSupplier = null;
		}

		return orderUUID;
	}

	public void setOrderUUID(String orderUUID) {
		this.orderUUID = orderUUID;

		_orderUUIDSupplier = null;
	}

	@JsonIgnore
	public void setOrderUUID(
		UnsafeSupplier<String, Exception> orderUUIDUnsafeSupplier) {

		_orderUUIDSupplier = () -> {
			try {
				return orderUUIDUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String orderUUID;

	@JsonIgnore
	private Supplier<String> _orderUUIDSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPaymentMethod() {
		if (_paymentMethodSupplier != null) {
			paymentMethod = _paymentMethodSupplier.get();

			_paymentMethodSupplier = null;
		}

		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;

		_paymentMethodSupplier = null;
	}

	@JsonIgnore
	public void setPaymentMethod(
		UnsafeSupplier<String, Exception> paymentMethodUnsafeSupplier) {

		_paymentMethodSupplier = () -> {
			try {
				return paymentMethodUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String paymentMethod;

	@JsonIgnore
	private Supplier<String> _paymentMethodSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPaymentMethodLabel() {
		if (_paymentMethodLabelSupplier != null) {
			paymentMethodLabel = _paymentMethodLabelSupplier.get();

			_paymentMethodLabelSupplier = null;
		}

		return paymentMethodLabel;
	}

	public void setPaymentMethodLabel(String paymentMethodLabel) {
		this.paymentMethodLabel = paymentMethodLabel;

		_paymentMethodLabelSupplier = null;
	}

	@JsonIgnore
	public void setPaymentMethodLabel(
		UnsafeSupplier<String, Exception> paymentMethodLabelUnsafeSupplier) {

		_paymentMethodLabelSupplier = () -> {
			try {
				return paymentMethodLabelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String paymentMethodLabel;

	@JsonIgnore
	private Supplier<String> _paymentMethodLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getPaymentStatus() {
		if (_paymentStatusSupplier != null) {
			paymentStatus = _paymentStatusSupplier.get();

			_paymentStatusSupplier = null;
		}

		return paymentStatus;
	}

	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;

		_paymentStatusSupplier = null;
	}

	@JsonIgnore
	public void setPaymentStatus(
		UnsafeSupplier<Integer, Exception> paymentStatusUnsafeSupplier) {

		_paymentStatusSupplier = () -> {
			try {
				return paymentStatusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer paymentStatus;

	@JsonIgnore
	private Supplier<Integer> _paymentStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getPaymentStatusInfo() {
		if (_paymentStatusInfoSupplier != null) {
			paymentStatusInfo = _paymentStatusInfoSupplier.get();

			_paymentStatusInfoSupplier = null;
		}

		return paymentStatusInfo;
	}

	public void setPaymentStatusInfo(Status paymentStatusInfo) {
		this.paymentStatusInfo = paymentStatusInfo;

		_paymentStatusInfoSupplier = null;
	}

	@JsonIgnore
	public void setPaymentStatusInfo(
		UnsafeSupplier<Status, Exception> paymentStatusInfoUnsafeSupplier) {

		_paymentStatusInfoSupplier = () -> {
			try {
				return paymentStatusInfoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status paymentStatusInfo;

	@JsonIgnore
	private Supplier<Status> _paymentStatusInfoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPaymentStatusLabel() {
		if (_paymentStatusLabelSupplier != null) {
			paymentStatusLabel = _paymentStatusLabelSupplier.get();

			_paymentStatusLabelSupplier = null;
		}

		return paymentStatusLabel;
	}

	public void setPaymentStatusLabel(String paymentStatusLabel) {
		this.paymentStatusLabel = paymentStatusLabel;

		_paymentStatusLabelSupplier = null;
	}

	@JsonIgnore
	public void setPaymentStatusLabel(
		UnsafeSupplier<String, Exception> paymentStatusLabelUnsafeSupplier) {

		_paymentStatusLabelSupplier = () -> {
			try {
				return paymentStatusLabelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String paymentStatusLabel;

	@JsonIgnore
	private Supplier<String> _paymentStatusLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PlacedOrderAddress getPlacedOrderBillingAddress() {
		if (_placedOrderBillingAddressSupplier != null) {
			placedOrderBillingAddress =
				_placedOrderBillingAddressSupplier.get();

			_placedOrderBillingAddressSupplier = null;
		}

		return placedOrderBillingAddress;
	}

	public void setPlacedOrderBillingAddress(
		PlacedOrderAddress placedOrderBillingAddress) {

		this.placedOrderBillingAddress = placedOrderBillingAddress;

		_placedOrderBillingAddressSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderBillingAddress(
		UnsafeSupplier<PlacedOrderAddress, Exception>
			placedOrderBillingAddressUnsafeSupplier) {

		_placedOrderBillingAddressSupplier = () -> {
			try {
				return placedOrderBillingAddressUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PlacedOrderAddress placedOrderBillingAddress;

	@JsonIgnore
	private Supplier<PlacedOrderAddress> _placedOrderBillingAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getPlacedOrderBillingAddressId() {
		if (_placedOrderBillingAddressIdSupplier != null) {
			placedOrderBillingAddressId =
				_placedOrderBillingAddressIdSupplier.get();

			_placedOrderBillingAddressIdSupplier = null;
		}

		return placedOrderBillingAddressId;
	}

	public void setPlacedOrderBillingAddressId(
		Long placedOrderBillingAddressId) {

		this.placedOrderBillingAddressId = placedOrderBillingAddressId;

		_placedOrderBillingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderBillingAddressId(
		UnsafeSupplier<Long, Exception>
			placedOrderBillingAddressIdUnsafeSupplier) {

		_placedOrderBillingAddressIdSupplier = () -> {
			try {
				return placedOrderBillingAddressIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long placedOrderBillingAddressId;

	@JsonIgnore
	private Supplier<Long> _placedOrderBillingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PlacedOrderComment[] getPlacedOrderComments() {
		if (_placedOrderCommentsSupplier != null) {
			placedOrderComments = _placedOrderCommentsSupplier.get();

			_placedOrderCommentsSupplier = null;
		}

		return placedOrderComments;
	}

	public void setPlacedOrderComments(
		PlacedOrderComment[] placedOrderComments) {

		this.placedOrderComments = placedOrderComments;

		_placedOrderCommentsSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderComments(
		UnsafeSupplier<PlacedOrderComment[], Exception>
			placedOrderCommentsUnsafeSupplier) {

		_placedOrderCommentsSupplier = () -> {
			try {
				return placedOrderCommentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PlacedOrderComment[] placedOrderComments;

	@JsonIgnore
	private Supplier<PlacedOrderComment[]> _placedOrderCommentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PlacedOrderItem[] getPlacedOrderItems() {
		if (_placedOrderItemsSupplier != null) {
			placedOrderItems = _placedOrderItemsSupplier.get();

			_placedOrderItemsSupplier = null;
		}

		return placedOrderItems;
	}

	public void setPlacedOrderItems(PlacedOrderItem[] placedOrderItems) {
		this.placedOrderItems = placedOrderItems;

		_placedOrderItemsSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderItems(
		UnsafeSupplier<PlacedOrderItem[], Exception>
			placedOrderItemsUnsafeSupplier) {

		_placedOrderItemsSupplier = () -> {
			try {
				return placedOrderItemsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PlacedOrderItem[] placedOrderItems;

	@JsonIgnore
	private Supplier<PlacedOrderItem[]> _placedOrderItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PlacedOrderAddress getPlacedOrderShippingAddress() {
		if (_placedOrderShippingAddressSupplier != null) {
			placedOrderShippingAddress =
				_placedOrderShippingAddressSupplier.get();

			_placedOrderShippingAddressSupplier = null;
		}

		return placedOrderShippingAddress;
	}

	public void setPlacedOrderShippingAddress(
		PlacedOrderAddress placedOrderShippingAddress) {

		this.placedOrderShippingAddress = placedOrderShippingAddress;

		_placedOrderShippingAddressSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderShippingAddress(
		UnsafeSupplier<PlacedOrderAddress, Exception>
			placedOrderShippingAddressUnsafeSupplier) {

		_placedOrderShippingAddressSupplier = () -> {
			try {
				return placedOrderShippingAddressUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PlacedOrderAddress placedOrderShippingAddress;

	@JsonIgnore
	private Supplier<PlacedOrderAddress> _placedOrderShippingAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getPlacedOrderShippingAddressId() {
		if (_placedOrderShippingAddressIdSupplier != null) {
			placedOrderShippingAddressId =
				_placedOrderShippingAddressIdSupplier.get();

			_placedOrderShippingAddressIdSupplier = null;
		}

		return placedOrderShippingAddressId;
	}

	public void setPlacedOrderShippingAddressId(
		Long placedOrderShippingAddressId) {

		this.placedOrderShippingAddressId = placedOrderShippingAddressId;

		_placedOrderShippingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setPlacedOrderShippingAddressId(
		UnsafeSupplier<Long, Exception>
			placedOrderShippingAddressIdUnsafeSupplier) {

		_placedOrderShippingAddressIdSupplier = () -> {
			try {
				return placedOrderShippingAddressIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long placedOrderShippingAddressId;

	@JsonIgnore
	private Supplier<Long> _placedOrderShippingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPrintedNote() {
		if (_printedNoteSupplier != null) {
			printedNote = _printedNoteSupplier.get();

			_printedNoteSupplier = null;
		}

		return printedNote;
	}

	public void setPrintedNote(String printedNote) {
		this.printedNote = printedNote;

		_printedNoteSupplier = null;
	}

	@JsonIgnore
	public void setPrintedNote(
		UnsafeSupplier<String, Exception> printedNoteUnsafeSupplier) {

		_printedNoteSupplier = () -> {
			try {
				return printedNoteUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String printedNote;

	@JsonIgnore
	private Supplier<String> _printedNoteSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPurchaseOrderNumber() {
		if (_purchaseOrderNumberSupplier != null) {
			purchaseOrderNumber = _purchaseOrderNumberSupplier.get();

			_purchaseOrderNumberSupplier = null;
		}

		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;

		_purchaseOrderNumberSupplier = null;
	}

	@JsonIgnore
	public void setPurchaseOrderNumber(
		UnsafeSupplier<String, Exception> purchaseOrderNumberUnsafeSupplier) {

		_purchaseOrderNumberSupplier = () -> {
			try {
				return purchaseOrderNumberUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String purchaseOrderNumber;

	@JsonIgnore
	private Supplier<String> _purchaseOrderNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getRequestedDeliveryDate() {
		if (_requestedDeliveryDateSupplier != null) {
			requestedDeliveryDate = _requestedDeliveryDateSupplier.get();

			_requestedDeliveryDateSupplier = null;
		}

		return requestedDeliveryDate;
	}

	public void setRequestedDeliveryDate(Date requestedDeliveryDate) {
		this.requestedDeliveryDate = requestedDeliveryDate;

		_requestedDeliveryDateSupplier = null;
	}

	@JsonIgnore
	public void setRequestedDeliveryDate(
		UnsafeSupplier<Date, Exception> requestedDeliveryDateUnsafeSupplier) {

		_requestedDeliveryDateSupplier = () -> {
			try {
				return requestedDeliveryDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date requestedDeliveryDate;

	@JsonIgnore
	private Supplier<Date> _requestedDeliveryDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Shipment getShipments() {
		if (_shipmentsSupplier != null) {
			shipments = _shipmentsSupplier.get();

			_shipmentsSupplier = null;
		}

		return shipments;
	}

	public void setShipments(Shipment shipments) {
		this.shipments = shipments;

		_shipmentsSupplier = null;
	}

	@JsonIgnore
	public void setShipments(
		UnsafeSupplier<Shipment, Exception> shipmentsUnsafeSupplier) {

		_shipmentsSupplier = () -> {
			try {
				return shipmentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Shipment shipments;

	@JsonIgnore
	private Supplier<Shipment> _shipmentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingMethod() {
		if (_shippingMethodSupplier != null) {
			shippingMethod = _shippingMethodSupplier.get();

			_shippingMethodSupplier = null;
		}

		return shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;

		_shippingMethodSupplier = null;
	}

	@JsonIgnore
	public void setShippingMethod(
		UnsafeSupplier<String, Exception> shippingMethodUnsafeSupplier) {

		_shippingMethodSupplier = () -> {
			try {
				return shippingMethodUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String shippingMethod;

	@JsonIgnore
	private Supplier<String> _shippingMethodSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingOption() {
		if (_shippingOptionSupplier != null) {
			shippingOption = _shippingOptionSupplier.get();

			_shippingOptionSupplier = null;
		}

		return shippingOption;
	}

	public void setShippingOption(String shippingOption) {
		this.shippingOption = shippingOption;

		_shippingOptionSupplier = null;
	}

	@JsonIgnore
	public void setShippingOption(
		UnsafeSupplier<String, Exception> shippingOptionUnsafeSupplier) {

		_shippingOptionSupplier = () -> {
			try {
				return shippingOptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String shippingOption;

	@JsonIgnore
	private Supplier<String> _shippingOptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Step[] getSteps() {
		if (_stepsSupplier != null) {
			steps = _stepsSupplier.get();

			_stepsSupplier = null;
		}

		return steps;
	}

	public void setSteps(Step[] steps) {
		this.steps = steps;

		_stepsSupplier = null;
	}

	@JsonIgnore
	public void setSteps(
		UnsafeSupplier<Step[], Exception> stepsUnsafeSupplier) {

		_stepsSupplier = () -> {
			try {
				return stepsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Step[] steps;

	@JsonIgnore
	private Supplier<Step[]> _stepsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Summary getSummary() {
		if (_summarySupplier != null) {
			summary = _summarySupplier.get();

			_summarySupplier = null;
		}

		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;

		_summarySupplier = null;
	}

	@JsonIgnore
	public void setSummary(
		UnsafeSupplier<Summary, Exception> summaryUnsafeSupplier) {

		_summarySupplier = () -> {
			try {
				return summaryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Summary summary;

	@JsonIgnore
	private Supplier<Summary> _summarySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getUseAsBilling() {
		if (_useAsBillingSupplier != null) {
			useAsBilling = _useAsBillingSupplier.get();

			_useAsBillingSupplier = null;
		}

		return useAsBilling;
	}

	public void setUseAsBilling(Boolean useAsBilling) {
		this.useAsBilling = useAsBilling;

		_useAsBillingSupplier = null;
	}

	@JsonIgnore
	public void setUseAsBilling(
		UnsafeSupplier<Boolean, Exception> useAsBillingUnsafeSupplier) {

		_useAsBillingSupplier = () -> {
			try {
				return useAsBillingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean useAsBilling;

	@JsonIgnore
	private Supplier<Boolean> _useAsBillingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getValid() {
		if (_validSupplier != null) {
			valid = _validSupplier.get();

			_validSupplier = null;
		}

		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;

		_validSupplier = null;
	}

	@JsonIgnore
	public void setValid(
		UnsafeSupplier<Boolean, Exception> validUnsafeSupplier) {

		_validSupplier = () -> {
			try {
				return validUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean valid;

	@JsonIgnore
	private Supplier<Boolean> _validSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getWorkflowStatusInfo() {
		if (_workflowStatusInfoSupplier != null) {
			workflowStatusInfo = _workflowStatusInfoSupplier.get();

			_workflowStatusInfoSupplier = null;
		}

		return workflowStatusInfo;
	}

	public void setWorkflowStatusInfo(Status workflowStatusInfo) {
		this.workflowStatusInfo = workflowStatusInfo;

		_workflowStatusInfoSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowStatusInfo(
		UnsafeSupplier<Status, Exception> workflowStatusInfoUnsafeSupplier) {

		_workflowStatusInfoSupplier = () -> {
			try {
				return workflowStatusInfoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status workflowStatusInfo;

	@JsonIgnore
	private Supplier<Status> _workflowStatusInfoSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PlacedOrder)) {
			return false;
		}

		PlacedOrder placedOrder = (PlacedOrder)object;

		return Objects.equals(toString(), placedOrder.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String account = getAccount();

		if (account != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append("\"");

			sb.append(_escape(account));

			sb.append("\"");
		}

		Long accountId = getAccountId();

		if (accountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(accountId);
		}

		Attachment[] attachments = getAttachments();

		if (attachments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachments\": ");

			sb.append("[");

			for (int i = 0; i < attachments.length; i++) {
				sb.append(String.valueOf(attachments[i]));

				if ((i + 1) < attachments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String author = getAuthor();

		if (author != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(author));

			sb.append("\"");
		}

		Long authorId = getAuthorId();

		if (authorId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authorId\": ");

			sb.append(authorId);
		}

		Long channelId = getChannelId();

		if (channelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(channelId);
		}

		String couponCode = getCouponCode();

		if (couponCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"couponCode\": ");

			sb.append("\"");

			sb.append(_escape(couponCode));

			sb.append("\"");
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

			sb.append("\"");
		}

		String currencyCode = getCurrencyCode();

		if (currencyCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(currencyCode));

			sb.append("\"");
		}

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
		}

		String[] errorMessages = getErrorMessages();

		if (errorMessages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("[");

			for (int i = 0; i < errorMessages.length; i++) {
				sb.append("\"");

				sb.append(_escape(errorMessages[i]));

				sb.append("\"");

				if ((i + 1) < errorMessages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String friendlyURLSeparator = getFriendlyURLSeparator();

		if (friendlyURLSeparator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyURLSeparator\": ");

			sb.append("\"");

			sb.append(_escape(friendlyURLSeparator));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Date lastPriceUpdateDate = getLastPriceUpdateDate();

		if (lastPriceUpdateDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastPriceUpdateDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastPriceUpdateDate));

			sb.append("\"");
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(modifiedDate));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Status orderStatusInfo = getOrderStatusInfo();

		if (orderStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatusInfo\": ");

			sb.append(String.valueOf(orderStatusInfo));
		}

		String orderType = getOrderType();

		if (orderType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append("\"");

			sb.append(_escape(orderType));

			sb.append("\"");
		}

		String orderTypeExternalReferenceCode =
			getOrderTypeExternalReferenceCode();

		if (orderTypeExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderTypeExternalReferenceCode));

			sb.append("\"");
		}

		Long orderTypeId = getOrderTypeId();

		if (orderTypeId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(orderTypeId);
		}

		String orderUUID = getOrderUUID();

		if (orderUUID != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderUUID\": ");

			sb.append("\"");

			sb.append(_escape(orderUUID));

			sb.append("\"");
		}

		String paymentMethod = getPaymentMethod();

		if (paymentMethod != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethod\": ");

			sb.append("\"");

			sb.append(_escape(paymentMethod));

			sb.append("\"");
		}

		String paymentMethodLabel = getPaymentMethodLabel();

		if (paymentMethodLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethodLabel\": ");

			sb.append("\"");

			sb.append(_escape(paymentMethodLabel));

			sb.append("\"");
		}

		Integer paymentStatus = getPaymentStatus();

		if (paymentStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatus\": ");

			sb.append(paymentStatus);
		}

		Status paymentStatusInfo = getPaymentStatusInfo();

		if (paymentStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusInfo\": ");

			sb.append(String.valueOf(paymentStatusInfo));
		}

		String paymentStatusLabel = getPaymentStatusLabel();

		if (paymentStatusLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusLabel\": ");

			sb.append("\"");

			sb.append(_escape(paymentStatusLabel));

			sb.append("\"");
		}

		PlacedOrderAddress placedOrderBillingAddress =
			getPlacedOrderBillingAddress();

		if (placedOrderBillingAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderBillingAddress\": ");

			sb.append(String.valueOf(placedOrderBillingAddress));
		}

		Long placedOrderBillingAddressId = getPlacedOrderBillingAddressId();

		if (placedOrderBillingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderBillingAddressId\": ");

			sb.append(placedOrderBillingAddressId);
		}

		PlacedOrderComment[] placedOrderComments = getPlacedOrderComments();

		if (placedOrderComments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderComments\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderComments.length; i++) {
				sb.append(String.valueOf(placedOrderComments[i]));

				if ((i + 1) < placedOrderComments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PlacedOrderItem[] placedOrderItems = getPlacedOrderItems();

		if (placedOrderItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderItems\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItems.length; i++) {
				sb.append(String.valueOf(placedOrderItems[i]));

				if ((i + 1) < placedOrderItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PlacedOrderAddress placedOrderShippingAddress =
			getPlacedOrderShippingAddress();

		if (placedOrderShippingAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderShippingAddress\": ");

			sb.append(String.valueOf(placedOrderShippingAddress));
		}

		Long placedOrderShippingAddressId = getPlacedOrderShippingAddressId();

		if (placedOrderShippingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderShippingAddressId\": ");

			sb.append(placedOrderShippingAddressId);
		}

		String printedNote = getPrintedNote();

		if (printedNote != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"printedNote\": ");

			sb.append("\"");

			sb.append(_escape(printedNote));

			sb.append("\"");
		}

		String purchaseOrderNumber = getPurchaseOrderNumber();

		if (purchaseOrderNumber != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseOrderNumber\": ");

			sb.append("\"");

			sb.append(_escape(purchaseOrderNumber));

			sb.append("\"");
		}

		Date requestedDeliveryDate = getRequestedDeliveryDate();

		if (requestedDeliveryDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(requestedDeliveryDate));

			sb.append("\"");
		}

		Shipment shipments = getShipments();

		if (shipments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shipments\": ");

			sb.append(String.valueOf(shipments));
		}

		String shippingMethod = getShippingMethod();

		if (shippingMethod != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingMethod\": ");

			sb.append("\"");

			sb.append(_escape(shippingMethod));

			sb.append("\"");
		}

		String shippingOption = getShippingOption();

		if (shippingOption != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingOption\": ");

			sb.append("\"");

			sb.append(_escape(shippingOption));

			sb.append("\"");
		}

		String status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(status));

			sb.append("\"");
		}

		Step[] steps = getSteps();

		if (steps != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"steps\": ");

			sb.append("[");

			for (int i = 0; i < steps.length; i++) {
				sb.append(String.valueOf(steps[i]));

				if ((i + 1) < steps.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Summary summary = getSummary();

		if (summary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"summary\": ");

			sb.append(String.valueOf(summary));
		}

		Boolean useAsBilling = getUseAsBilling();

		if (useAsBilling != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useAsBilling\": ");

			sb.append(useAsBilling);
		}

		Boolean valid = getValid();

		if (valid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(valid);
		}

		Status workflowStatusInfo = getWorkflowStatusInfo();

		if (workflowStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(workflowStatusInfo));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrder",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}