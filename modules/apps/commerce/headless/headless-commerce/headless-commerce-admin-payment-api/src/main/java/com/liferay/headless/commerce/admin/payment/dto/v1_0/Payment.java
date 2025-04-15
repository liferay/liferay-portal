/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("Payment")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Payment")
public class Payment implements Serializable {

	public static Payment toDTO(String json) {
		return ObjectMapperUtil.readValue(Payment.class, json);
	}

	public static Payment unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Payment.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "101")
	@Valid
	public BigDecimal getAmount() {
		if (_amountSupplier != null) {
			amount = _amountSupplier.get();

			_amountSupplier = null;
		}

		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;

		_amountSupplier = null;
	}

	@JsonIgnore
	public void setAmount(
		UnsafeSupplier<BigDecimal, Exception> amountUnsafeSupplier) {

		_amountSupplier = () -> {
			try {
				return amountUnsafeSupplier.get();
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
	protected BigDecimal amount;

	@JsonIgnore
	private Supplier<BigDecimal> _amountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "$ 101.00")
	public String getAmountFormatted() {
		if (_amountFormattedSupplier != null) {
			amountFormatted = _amountFormattedSupplier.get();

			_amountFormattedSupplier = null;
		}

		return amountFormatted;
	}

	public void setAmountFormatted(String amountFormatted) {
		this.amountFormatted = amountFormatted;

		_amountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setAmountFormatted(
		UnsafeSupplier<String, Exception> amountFormattedUnsafeSupplier) {

		_amountFormattedSupplier = () -> {
			try {
				return amountFormattedUnsafeSupplier.get();
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
	protected String amountFormatted;

	@JsonIgnore
	private Supplier<String> _amountFormattedSupplier;

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
	public String getCallbackURL() {
		if (_callbackURLSupplier != null) {
			callbackURL = _callbackURLSupplier.get();

			_callbackURLSupplier = null;
		}

		return callbackURL;
	}

	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;

		_callbackURLSupplier = null;
	}

	@JsonIgnore
	public void setCallbackURL(
		UnsafeSupplier<String, Exception> callbackURLUnsafeSupplier) {

		_callbackURLSupplier = () -> {
			try {
				return callbackURLUnsafeSupplier.get();
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
	protected String callbackURL;

	@JsonIgnore
	private Supplier<String> _callbackURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCancelURL() {
		if (_cancelURLSupplier != null) {
			cancelURL = _cancelURLSupplier.get();

			_cancelURLSupplier = null;
		}

		return cancelURL;
	}

	public void setCancelURL(String cancelURL) {
		this.cancelURL = cancelURL;

		_cancelURLSupplier = null;
	}

	@JsonIgnore
	public void setCancelURL(
		UnsafeSupplier<String, Exception> cancelURLUnsafeSupplier) {

		_cancelURLSupplier = () -> {
			try {
				return cancelURLUnsafeSupplier.get();
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
	protected String cancelURL;

	@JsonIgnore
	private Supplier<String> _cancelURLSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long channelId;

	@JsonIgnore
	private Supplier<Long> _channelIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getComment() {
		if (_commentSupplier != null) {
			comment = _commentSupplier.get();

			_commentSupplier = null;
		}

		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

		_commentSupplier = null;
	}

	@JsonIgnore
	public void setComment(
		UnsafeSupplier<String, Exception> commentUnsafeSupplier) {

		_commentSupplier = () -> {
			try {
				return commentUnsafeSupplier.get();
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
	protected String comment;

	@JsonIgnore
	private Supplier<String> _commentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2023-12-01")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "USD")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String currencyCode;

	@JsonIgnore
	private Supplier<String> _currencyCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getCurrencyExternalReferenceCode() {
		if (_currencyExternalReferenceCodeSupplier != null) {
			currencyExternalReferenceCode =
				_currencyExternalReferenceCodeSupplier.get();

			_currencyExternalReferenceCodeSupplier = null;
		}

		return currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		String currencyExternalReferenceCode) {

		this.currencyExternalReferenceCode = currencyExternalReferenceCode;

		_currencyExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			currencyExternalReferenceCodeUnsafeSupplier) {

		_currencyExternalReferenceCodeSupplier = () -> {
			try {
				return currencyExternalReferenceCodeUnsafeSupplier.get();
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
	protected String currencyExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _currencyExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getCurrencyId() {
		if (_currencyIdSupplier != null) {
			currencyId = _currencyIdSupplier.get();

			_currencyIdSupplier = null;
		}

		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;

		_currencyIdSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyId(
		UnsafeSupplier<Long, Exception> currencyIdUnsafeSupplier) {

		_currencyIdSupplier = () -> {
			try {
				return currencyIdUnsafeSupplier.get();
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
	protected Long currencyId;

	@JsonIgnore
	private Supplier<Long> _currencyIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getErrorMessages() {
		if (_errorMessagesSupplier != null) {
			errorMessages = _errorMessagesSupplier.get();

			_errorMessagesSupplier = null;
		}

		return errorMessages;
	}

	public void setErrorMessages(String errorMessages) {
		this.errorMessages = errorMessages;

		_errorMessagesSupplier = null;
	}

	@JsonIgnore
	public void setErrorMessages(
		UnsafeSupplier<String, Exception> errorMessagesUnsafeSupplier) {

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String errorMessages;

	@JsonIgnore
	private Supplier<String> _errorMessagesSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "en_US")
	public String getLanguageId() {
		if (_languageIdSupplier != null) {
			languageId = _languageIdSupplier.get();

			_languageIdSupplier = null;
		}

		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;

		_languageIdSupplier = null;
	}

	@JsonIgnore
	public void setLanguageId(
		UnsafeSupplier<String, Exception> languageIdUnsafeSupplier) {

		_languageIdSupplier = () -> {
			try {
				return languageIdUnsafeSupplier.get();
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
	protected String languageId;

	@JsonIgnore
	private Supplier<String> _languageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPayload() {
		if (_payloadSupplier != null) {
			payload = _payloadSupplier.get();

			_payloadSupplier = null;
		}

		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;

		_payloadSupplier = null;
	}

	@JsonIgnore
	public void setPayload(
		UnsafeSupplier<String, Exception> payloadUnsafeSupplier) {

		_payloadSupplier = () -> {
			try {
				return payloadUnsafeSupplier.get();
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
	protected String payload;

	@JsonIgnore
	private Supplier<String> _payloadSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "money-order")
	public String getPaymentIntegrationKey() {
		if (_paymentIntegrationKeySupplier != null) {
			paymentIntegrationKey = _paymentIntegrationKeySupplier.get();

			_paymentIntegrationKeySupplier = null;
		}

		return paymentIntegrationKey;
	}

	public void setPaymentIntegrationKey(String paymentIntegrationKey) {
		this.paymentIntegrationKey = paymentIntegrationKey;

		_paymentIntegrationKeySupplier = null;
	}

	@JsonIgnore
	public void setPaymentIntegrationKey(
		UnsafeSupplier<String, Exception> paymentIntegrationKeyUnsafeSupplier) {

		_paymentIntegrationKeySupplier = () -> {
			try {
				return paymentIntegrationKeyUnsafeSupplier.get();
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
	protected String paymentIntegrationKey;

	@JsonIgnore
	private Supplier<String> _paymentIntegrationKeySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	public Integer getPaymentIntegrationType() {
		if (_paymentIntegrationTypeSupplier != null) {
			paymentIntegrationType = _paymentIntegrationTypeSupplier.get();

			_paymentIntegrationTypeSupplier = null;
		}

		return paymentIntegrationType;
	}

	public void setPaymentIntegrationType(Integer paymentIntegrationType) {
		this.paymentIntegrationType = paymentIntegrationType;

		_paymentIntegrationTypeSupplier = null;
	}

	@JsonIgnore
	public void setPaymentIntegrationType(
		UnsafeSupplier<Integer, Exception>
			paymentIntegrationTypeUnsafeSupplier) {

		_paymentIntegrationTypeSupplier = () -> {
			try {
				return paymentIntegrationTypeUnsafeSupplier.get();
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
	protected Integer paymentIntegrationType;

	@JsonIgnore
	private Supplier<Integer> _paymentIntegrationTypeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer paymentStatus;

	@JsonIgnore
	private Supplier<Integer> _paymentStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getPaymentStatusStatus() {
		if (_paymentStatusStatusSupplier != null) {
			paymentStatusStatus = _paymentStatusStatusSupplier.get();

			_paymentStatusStatusSupplier = null;
		}

		return paymentStatusStatus;
	}

	public void setPaymentStatusStatus(Status paymentStatusStatus) {
		this.paymentStatusStatus = paymentStatusStatus;

		_paymentStatusStatusSupplier = null;
	}

	@JsonIgnore
	public void setPaymentStatusStatus(
		UnsafeSupplier<Status, Exception> paymentStatusStatusUnsafeSupplier) {

		_paymentStatusStatusSupplier = () -> {
			try {
				return paymentStatusStatusUnsafeSupplier.get();
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
	protected Status paymentStatusStatus;

	@JsonIgnore
	private Supplier<Status> _paymentStatusStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "product-defect")
	public String getReasonKey() {
		if (_reasonKeySupplier != null) {
			reasonKey = _reasonKeySupplier.get();

			_reasonKeySupplier = null;
		}

		return reasonKey;
	}

	public void setReasonKey(String reasonKey) {
		this.reasonKey = reasonKey;

		_reasonKeySupplier = null;
	}

	@JsonIgnore
	public void setReasonKey(
		UnsafeSupplier<String, Exception> reasonKeyUnsafeSupplier) {

		_reasonKeySupplier = () -> {
			try {
				return reasonKeyUnsafeSupplier.get();
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
	protected String reasonKey;

	@JsonIgnore
	private Supplier<String> _reasonKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Product Defect, hr_HR=Product Defect HR, hu_HU=Product Defect HU}"
	)
	@Valid
	public Map<String, String> getReasonName() {
		if (_reasonNameSupplier != null) {
			reasonName = _reasonNameSupplier.get();

			_reasonNameSupplier = null;
		}

		return reasonName;
	}

	public void setReasonName(Map<String, String> reasonName) {
		this.reasonName = reasonName;

		_reasonNameSupplier = null;
	}

	@JsonIgnore
	public void setReasonName(
		UnsafeSupplier<Map<String, String>, Exception>
			reasonNameUnsafeSupplier) {

		_reasonNameSupplier = () -> {
			try {
				return reasonNameUnsafeSupplier.get();
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
	protected Map<String, String> reasonName;

	@JsonIgnore
	private Supplier<Map<String, String>> _reasonNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRedirectURL() {
		if (_redirectURLSupplier != null) {
			redirectURL = _redirectURLSupplier.get();

			_redirectURLSupplier = null;
		}

		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;

		_redirectURLSupplier = null;
	}

	@JsonIgnore
	public void setRedirectURL(
		UnsafeSupplier<String, Exception> redirectURLUnsafeSupplier) {

		_redirectURLSupplier = () -> {
			try {
				return redirectURLUnsafeSupplier.get();
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
	protected String redirectURL;

	@JsonIgnore
	private Supplier<String> _redirectURLSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getRelatedItemId() {
		if (_relatedItemIdSupplier != null) {
			relatedItemId = _relatedItemIdSupplier.get();

			_relatedItemIdSupplier = null;
		}

		return relatedItemId;
	}

	public void setRelatedItemId(Long relatedItemId) {
		this.relatedItemId = relatedItemId;

		_relatedItemIdSupplier = null;
	}

	@JsonIgnore
	public void setRelatedItemId(
		UnsafeSupplier<Long, Exception> relatedItemIdUnsafeSupplier) {

		_relatedItemIdSupplier = () -> {
			try {
				return relatedItemIdUnsafeSupplier.get();
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
	protected Long relatedItemId;

	@JsonIgnore
	private Supplier<Long> _relatedItemIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "com.liferay.commerce.model.CommerceOrder"
	)
	public String getRelatedItemName() {
		if (_relatedItemNameSupplier != null) {
			relatedItemName = _relatedItemNameSupplier.get();

			_relatedItemNameSupplier = null;
		}

		return relatedItemName;
	}

	public void setRelatedItemName(String relatedItemName) {
		this.relatedItemName = relatedItemName;

		_relatedItemNameSupplier = null;
	}

	@JsonIgnore
	public void setRelatedItemName(
		UnsafeSupplier<String, Exception> relatedItemNameUnsafeSupplier) {

		_relatedItemNameSupplier = () -> {
			try {
				return relatedItemNameUnsafeSupplier.get();
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
	protected String relatedItemName;

	@JsonIgnore
	private Supplier<String> _relatedItemNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Order")
	public String getRelatedItemNameLabel() {
		if (_relatedItemNameLabelSupplier != null) {
			relatedItemNameLabel = _relatedItemNameLabelSupplier.get();

			_relatedItemNameLabelSupplier = null;
		}

		return relatedItemNameLabel;
	}

	public void setRelatedItemNameLabel(String relatedItemNameLabel) {
		this.relatedItemNameLabel = relatedItemNameLabel;

		_relatedItemNameLabelSupplier = null;
	}

	@JsonIgnore
	public void setRelatedItemNameLabel(
		UnsafeSupplier<String, Exception> relatedItemNameLabelUnsafeSupplier) {

		_relatedItemNameLabelSupplier = () -> {
			try {
				return relatedItemNameLabelUnsafeSupplier.get();
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
	protected String relatedItemNameLabel;

	@JsonIgnore
	private Supplier<String> _relatedItemNameLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTransactionCode() {
		if (_transactionCodeSupplier != null) {
			transactionCode = _transactionCodeSupplier.get();

			_transactionCodeSupplier = null;
		}

		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;

		_transactionCodeSupplier = null;
	}

	@JsonIgnore
	public void setTransactionCode(
		UnsafeSupplier<String, Exception> transactionCodeUnsafeSupplier) {

		_transactionCodeSupplier = () -> {
			try {
				return transactionCodeUnsafeSupplier.get();
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
	protected String transactionCode;

	@JsonIgnore
	private Supplier<String> _transactionCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	public Integer getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(Integer type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Integer, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
	protected Integer type;

	@JsonIgnore
	private Supplier<Integer> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Refund")
	public String getTypeLabel() {
		if (_typeLabelSupplier != null) {
			typeLabel = _typeLabelSupplier.get();

			_typeLabelSupplier = null;
		}

		return typeLabel;
	}

	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;

		_typeLabelSupplier = null;
	}

	@JsonIgnore
	public void setTypeLabel(
		UnsafeSupplier<String, Exception> typeLabelUnsafeSupplier) {

		_typeLabelSupplier = () -> {
			try {
				return typeLabelUnsafeSupplier.get();
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
	protected String typeLabel;

	@JsonIgnore
	private Supplier<String> _typeLabelSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Payment)) {
			return false;
		}

		Payment payment = (Payment)object;

		return Objects.equals(toString(), payment.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		BigDecimal amount = getAmount();

		if (amount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amount\": ");

			sb.append(amount);
		}

		String amountFormatted = getAmountFormatted();

		if (amountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(amountFormatted));

			sb.append("\"");
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

		String callbackURL = getCallbackURL();

		if (callbackURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"callbackURL\": ");

			sb.append("\"");

			sb.append(_escape(callbackURL));

			sb.append("\"");
		}

		String cancelURL = getCancelURL();

		if (cancelURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cancelURL\": ");

			sb.append("\"");

			sb.append(_escape(cancelURL));

			sb.append("\"");
		}

		Long channelId = getChannelId();

		if (channelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(channelId);
		}

		String comment = getComment();

		if (comment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(comment));

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

		String currencyExternalReferenceCode =
			getCurrencyExternalReferenceCode();

		if (currencyExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(currencyExternalReferenceCode));

			sb.append("\"");
		}

		Long currencyId = getCurrencyId();

		if (currencyId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(currencyId);
		}

		String errorMessages = getErrorMessages();

		if (errorMessages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("\"");

			sb.append(_escape(errorMessages));

			sb.append("\"");
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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String languageId = getLanguageId();

		if (languageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageId\": ");

			sb.append("\"");

			sb.append(_escape(languageId));

			sb.append("\"");
		}

		String payload = getPayload();

		if (payload != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"payload\": ");

			sb.append("\"");

			sb.append(_escape(payload));

			sb.append("\"");
		}

		String paymentIntegrationKey = getPaymentIntegrationKey();

		if (paymentIntegrationKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentIntegrationKey\": ");

			sb.append("\"");

			sb.append(_escape(paymentIntegrationKey));

			sb.append("\"");
		}

		Integer paymentIntegrationType = getPaymentIntegrationType();

		if (paymentIntegrationType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentIntegrationType\": ");

			sb.append(paymentIntegrationType);
		}

		Integer paymentStatus = getPaymentStatus();

		if (paymentStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatus\": ");

			sb.append(paymentStatus);
		}

		Status paymentStatusStatus = getPaymentStatusStatus();

		if (paymentStatusStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusStatus\": ");

			sb.append(String.valueOf(paymentStatusStatus));
		}

		String reasonKey = getReasonKey();

		if (reasonKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reasonKey\": ");

			sb.append("\"");

			sb.append(_escape(reasonKey));

			sb.append("\"");
		}

		Map<String, String> reasonName = getReasonName();

		if (reasonName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reasonName\": ");

			sb.append(_toJSON(reasonName));
		}

		String redirectURL = getRedirectURL();

		if (redirectURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"redirectURL\": ");

			sb.append("\"");

			sb.append(_escape(redirectURL));

			sb.append("\"");
		}

		Long relatedItemId = getRelatedItemId();

		if (relatedItemId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedItemId\": ");

			sb.append(relatedItemId);
		}

		String relatedItemName = getRelatedItemName();

		if (relatedItemName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedItemName\": ");

			sb.append("\"");

			sb.append(_escape(relatedItemName));

			sb.append("\"");
		}

		String relatedItemNameLabel = getRelatedItemNameLabel();

		if (relatedItemNameLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedItemNameLabel\": ");

			sb.append("\"");

			sb.append(_escape(relatedItemNameLabel));

			sb.append("\"");
		}

		String transactionCode = getTransactionCode();

		if (transactionCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transactionCode\": ");

			sb.append("\"");

			sb.append(_escape(transactionCode));

			sb.append("\"");
		}

		Integer type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(type);
		}

		String typeLabel = getTypeLabel();

		if (typeLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeLabel\": ");

			sb.append("\"");

			sb.append(_escape(typeLabel));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment",
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