/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.dto.v1_0;

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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("Order")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"channelId", "currencyCode"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Order")
public class Order implements Serializable {

	public static Order toDTO(String json) {
		return ObjectMapperUtil.readValue(Order.class, json);
	}

	public static Order unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Order.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Account getAccount() {
		if (_accountSupplier != null) {
			account = _accountSupplier.get();

			_accountSupplier = null;
		}

		return account;
	}

	public void setAccount(Account account) {
		this.account = account;

		_accountSupplier = null;
	}

	@JsonIgnore
	public void setAccount(
		UnsafeSupplier<Account, Exception> accountUnsafeSupplier) {

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
	protected Account account;

	@JsonIgnore
	private Supplier<Account> _accountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getAccountExternalReferenceCode() {
		if (_accountExternalReferenceCodeSupplier != null) {
			accountExternalReferenceCode =
				_accountExternalReferenceCodeSupplier.get();

			_accountExternalReferenceCodeSupplier = null;
		}

		return accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		String accountExternalReferenceCode) {

		this.accountExternalReferenceCode = accountExternalReferenceCode;

		_accountExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountExternalReferenceCodeUnsafeSupplier) {

		_accountExternalReferenceCodeSupplier = () -> {
			try {
				return accountExternalReferenceCodeUnsafeSupplier.get();
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
	protected String accountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(example = "trasmitted")
	public String getAdvanceStatus() {
		if (_advanceStatusSupplier != null) {
			advanceStatus = _advanceStatusSupplier.get();

			_advanceStatusSupplier = null;
		}

		return advanceStatus;
	}

	public void setAdvanceStatus(String advanceStatus) {
		this.advanceStatus = advanceStatus;

		_advanceStatusSupplier = null;
	}

	@JsonIgnore
	public void setAdvanceStatus(
		UnsafeSupplier<String, Exception> advanceStatusUnsafeSupplier) {

		_advanceStatusSupplier = () -> {
			try {
				return advanceStatusUnsafeSupplier.get();
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
	protected String advanceStatus;

	@JsonIgnore
	private Supplier<String> _advanceStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BillingAddress getBillingAddress() {
		if (_billingAddressSupplier != null) {
			billingAddress = _billingAddressSupplier.get();

			_billingAddressSupplier = null;
		}

		return billingAddress;
	}

	public void setBillingAddress(BillingAddress billingAddress) {
		this.billingAddress = billingAddress;

		_billingAddressSupplier = null;
	}

	@JsonIgnore
	public void setBillingAddress(
		UnsafeSupplier<BillingAddress, Exception>
			billingAddressUnsafeSupplier) {

		_billingAddressSupplier = () -> {
			try {
				return billingAddressUnsafeSupplier.get();
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
	protected BillingAddress billingAddress;

	@JsonIgnore
	private Supplier<BillingAddress> _billingAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getBillingAddressExternalReferenceCode() {
		if (_billingAddressExternalReferenceCodeSupplier != null) {
			billingAddressExternalReferenceCode =
				_billingAddressExternalReferenceCodeSupplier.get();

			_billingAddressExternalReferenceCodeSupplier = null;
		}

		return billingAddressExternalReferenceCode;
	}

	public void setBillingAddressExternalReferenceCode(
		String billingAddressExternalReferenceCode) {

		this.billingAddressExternalReferenceCode =
			billingAddressExternalReferenceCode;

		_billingAddressExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setBillingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			billingAddressExternalReferenceCodeUnsafeSupplier) {

		_billingAddressExternalReferenceCodeSupplier = () -> {
			try {
				return billingAddressExternalReferenceCodeUnsafeSupplier.get();
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
	protected String billingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _billingAddressExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
	public Long getBillingAddressId() {
		if (_billingAddressIdSupplier != null) {
			billingAddressId = _billingAddressIdSupplier.get();

			_billingAddressIdSupplier = null;
		}

		return billingAddressId;
	}

	public void setBillingAddressId(Long billingAddressId) {
		this.billingAddressId = billingAddressId;

		_billingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setBillingAddressId(
		UnsafeSupplier<Long, Exception> billingAddressIdUnsafeSupplier) {

		_billingAddressIdSupplier = () -> {
			try {
				return billingAddressIdUnsafeSupplier.get();
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
	protected Long billingAddressId;

	@JsonIgnore
	private Supplier<Long> _billingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Channel getChannel() {
		if (_channelSupplier != null) {
			channel = _channelSupplier.get();

			_channelSupplier = null;
		}

		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;

		_channelSupplier = null;
	}

	@JsonIgnore
	public void setChannel(
		UnsafeSupplier<Channel, Exception> channelUnsafeSupplier) {

		_channelSupplier = () -> {
			try {
				return channelUnsafeSupplier.get();
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
	protected Channel channel;

	@JsonIgnore
	private Supplier<Channel> _channelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getChannelExternalReferenceCode() {
		if (_channelExternalReferenceCodeSupplier != null) {
			channelExternalReferenceCode =
				_channelExternalReferenceCodeSupplier.get();

			_channelExternalReferenceCodeSupplier = null;
		}

		return channelExternalReferenceCode;
	}

	public void setChannelExternalReferenceCode(
		String channelExternalReferenceCode) {

		this.channelExternalReferenceCode = channelExternalReferenceCode;

		_channelExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setChannelExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			channelExternalReferenceCodeUnsafeSupplier) {

		_channelExternalReferenceCodeSupplier = () -> {
			try {
				return channelExternalReferenceCodeUnsafeSupplier.get();
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
	protected String channelExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _channelExternalReferenceCodeSupplier;

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
	@NotNull
	protected Long channelId;

	@JsonIgnore
	private Supplier<Long> _channelIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "save20")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String couponCode;

	@JsonIgnore
	private Supplier<String> _couponCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCreatorEmailAddress() {
		if (_creatorEmailAddressSupplier != null) {
			creatorEmailAddress = _creatorEmailAddressSupplier.get();

			_creatorEmailAddressSupplier = null;
		}

		return creatorEmailAddress;
	}

	public void setCreatorEmailAddress(String creatorEmailAddress) {
		this.creatorEmailAddress = creatorEmailAddress;

		_creatorEmailAddressSupplier = null;
	}

	@JsonIgnore
	public void setCreatorEmailAddress(
		UnsafeSupplier<String, Exception> creatorEmailAddressUnsafeSupplier) {

		_creatorEmailAddressSupplier = () -> {
			try {
				return creatorEmailAddressUnsafeSupplier.get();
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
	protected String creatorEmailAddress;

	@JsonIgnore
	private Supplier<String> _creatorEmailAddressSupplier;

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
	@NotEmpty
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

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Orders delivery terms description"
	)
	public String getDeliveryTermDescription() {
		if (_deliveryTermDescriptionSupplier != null) {
			deliveryTermDescription = _deliveryTermDescriptionSupplier.get();

			_deliveryTermDescriptionSupplier = null;
		}

		return deliveryTermDescription;
	}

	public void setDeliveryTermDescription(String deliveryTermDescription) {
		this.deliveryTermDescription = deliveryTermDescription;

		_deliveryTermDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setDeliveryTermDescription(
		UnsafeSupplier<String, Exception>
			deliveryTermDescriptionUnsafeSupplier) {

		_deliveryTermDescriptionSupplier = () -> {
			try {
				return deliveryTermDescriptionUnsafeSupplier.get();
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
	protected String deliveryTermDescription;

	@JsonIgnore
	private Supplier<String> _deliveryTermDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getDeliveryTermExternalReferenceCode() {
		if (_deliveryTermExternalReferenceCodeSupplier != null) {
			deliveryTermExternalReferenceCode =
				_deliveryTermExternalReferenceCodeSupplier.get();

			_deliveryTermExternalReferenceCodeSupplier = null;
		}

		return deliveryTermExternalReferenceCode;
	}

	public void setDeliveryTermExternalReferenceCode(
		String deliveryTermExternalReferenceCode) {

		this.deliveryTermExternalReferenceCode =
			deliveryTermExternalReferenceCode;

		_deliveryTermExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setDeliveryTermExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			deliveryTermExternalReferenceCodeUnsafeSupplier) {

		_deliveryTermExternalReferenceCodeSupplier = () -> {
			try {
				return deliveryTermExternalReferenceCodeUnsafeSupplier.get();
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
	protected String deliveryTermExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _deliveryTermExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getDeliveryTermId() {
		if (_deliveryTermIdSupplier != null) {
			deliveryTermId = _deliveryTermIdSupplier.get();

			_deliveryTermIdSupplier = null;
		}

		return deliveryTermId;
	}

	public void setDeliveryTermId(Long deliveryTermId) {
		this.deliveryTermId = deliveryTermId;

		_deliveryTermIdSupplier = null;
	}

	@JsonIgnore
	public void setDeliveryTermId(
		UnsafeSupplier<Long, Exception> deliveryTermIdUnsafeSupplier) {

		_deliveryTermIdSupplier = () -> {
			try {
				return deliveryTermIdUnsafeSupplier.get();
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
	protected Long deliveryTermId;

	@JsonIgnore
	private Supplier<Long> _deliveryTermIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Orders delivery terms name"
	)
	public String getDeliveryTermName() {
		if (_deliveryTermNameSupplier != null) {
			deliveryTermName = _deliveryTermNameSupplier.get();

			_deliveryTermNameSupplier = null;
		}

		return deliveryTermName;
	}

	public void setDeliveryTermName(String deliveryTermName) {
		this.deliveryTermName = deliveryTermName;

		_deliveryTermNameSupplier = null;
	}

	@JsonIgnore
	public void setDeliveryTermName(
		UnsafeSupplier<String, Exception> deliveryTermNameUnsafeSupplier) {

		_deliveryTermNameSupplier = () -> {
			try {
				return deliveryTermNameUnsafeSupplier.get();
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
	protected String deliveryTermName;

	@JsonIgnore
	private Supplier<String> _deliveryTermNameSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date lastPriceUpdateDate;

	@JsonIgnore
	private Supplier<Date> _lastPriceUpdateDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-08-21")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
	public Date getOrderDate() {
		if (_orderDateSupplier != null) {
			orderDate = _orderDateSupplier.get();

			_orderDateSupplier = null;
		}

		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;

		_orderDateSupplier = null;
	}

	@JsonIgnore
	public void setOrderDate(
		UnsafeSupplier<Date, Exception> orderDateUnsafeSupplier) {

		_orderDateSupplier = () -> {
			try {
				return orderDateUnsafeSupplier.get();
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
	protected Date orderDate;

	@JsonIgnore
	private Supplier<Date> _orderDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public OrderItem[] getOrderItems() {
		if (_orderItemsSupplier != null) {
			orderItems = _orderItemsSupplier.get();

			_orderItemsSupplier = null;
		}

		return orderItems;
	}

	public void setOrderItems(OrderItem[] orderItems) {
		this.orderItems = orderItems;

		_orderItemsSupplier = null;
	}

	@JsonIgnore
	public void setOrderItems(
		UnsafeSupplier<OrderItem[], Exception> orderItemsUnsafeSupplier) {

		_orderItemsSupplier = () -> {
			try {
				return orderItemsUnsafeSupplier.get();
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
	protected OrderItem[] orderItems;

	@JsonIgnore
	private Supplier<OrderItem[]> _orderItemsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	public Integer getOrderStatus() {
		if (_orderStatusSupplier != null) {
			orderStatus = _orderStatusSupplier.get();

			_orderStatusSupplier = null;
		}

		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;

		_orderStatusSupplier = null;
	}

	@JsonIgnore
	public void setOrderStatus(
		UnsafeSupplier<Integer, Exception> orderStatusUnsafeSupplier) {

		_orderStatusSupplier = () -> {
			try {
				return orderStatusUnsafeSupplier.get();
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
	protected Integer orderStatus;

	@JsonIgnore
	private Supplier<Integer> _orderStatusSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String orderTypeExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderTypeExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long orderTypeId;

	@JsonIgnore
	private Supplier<Long> _orderTypeIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "paypal")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String paymentMethod;

	@JsonIgnore
	private Supplier<String> _paymentMethodSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Orders payment terms description"
	)
	public String getPaymentTermDescription() {
		if (_paymentTermDescriptionSupplier != null) {
			paymentTermDescription = _paymentTermDescriptionSupplier.get();

			_paymentTermDescriptionSupplier = null;
		}

		return paymentTermDescription;
	}

	public void setPaymentTermDescription(String paymentTermDescription) {
		this.paymentTermDescription = paymentTermDescription;

		_paymentTermDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setPaymentTermDescription(
		UnsafeSupplier<String, Exception>
			paymentTermDescriptionUnsafeSupplier) {

		_paymentTermDescriptionSupplier = () -> {
			try {
				return paymentTermDescriptionUnsafeSupplier.get();
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
	protected String paymentTermDescription;

	@JsonIgnore
	private Supplier<String> _paymentTermDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getPaymentTermExternalReferenceCode() {
		if (_paymentTermExternalReferenceCodeSupplier != null) {
			paymentTermExternalReferenceCode =
				_paymentTermExternalReferenceCodeSupplier.get();

			_paymentTermExternalReferenceCodeSupplier = null;
		}

		return paymentTermExternalReferenceCode;
	}

	public void setPaymentTermExternalReferenceCode(
		String paymentTermExternalReferenceCode) {

		this.paymentTermExternalReferenceCode =
			paymentTermExternalReferenceCode;

		_paymentTermExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setPaymentTermExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			paymentTermExternalReferenceCodeUnsafeSupplier) {

		_paymentTermExternalReferenceCodeSupplier = () -> {
			try {
				return paymentTermExternalReferenceCodeUnsafeSupplier.get();
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
	protected String paymentTermExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _paymentTermExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getPaymentTermId() {
		if (_paymentTermIdSupplier != null) {
			paymentTermId = _paymentTermIdSupplier.get();

			_paymentTermIdSupplier = null;
		}

		return paymentTermId;
	}

	public void setPaymentTermId(Long paymentTermId) {
		this.paymentTermId = paymentTermId;

		_paymentTermIdSupplier = null;
	}

	@JsonIgnore
	public void setPaymentTermId(
		UnsafeSupplier<Long, Exception> paymentTermIdUnsafeSupplier) {

		_paymentTermIdSupplier = () -> {
			try {
				return paymentTermIdUnsafeSupplier.get();
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
	protected Long paymentTermId;

	@JsonIgnore
	private Supplier<Long> _paymentTermIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Orders payment terms name"
	)
	public String getPaymentTermName() {
		if (_paymentTermNameSupplier != null) {
			paymentTermName = _paymentTermNameSupplier.get();

			_paymentTermNameSupplier = null;
		}

		return paymentTermName;
	}

	public void setPaymentTermName(String paymentTermName) {
		this.paymentTermName = paymentTermName;

		_paymentTermNameSupplier = null;
	}

	@JsonIgnore
	public void setPaymentTermName(
		UnsafeSupplier<String, Exception> paymentTermNameUnsafeSupplier) {

		_paymentTermNameSupplier = () -> {
			try {
				return paymentTermNameUnsafeSupplier.get();
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
	protected String paymentTermName;

	@JsonIgnore
	private Supplier<String> _paymentTermNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Order printed note")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "Abcd1234")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date requestedDeliveryDate;

	@JsonIgnore
	private Supplier<Date> _requestedDeliveryDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getShippable() {
		if (_shippableSupplier != null) {
			shippable = _shippableSupplier.get();

			_shippableSupplier = null;
		}

		return shippable;
	}

	public void setShippable(Boolean shippable) {
		this.shippable = shippable;

		_shippableSupplier = null;
	}

	@JsonIgnore
	public void setShippable(
		UnsafeSupplier<Boolean, Exception> shippableUnsafeSupplier) {

		_shippableSupplier = () -> {
			try {
				return shippableUnsafeSupplier.get();
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
	protected Boolean shippable;

	@JsonIgnore
	private Supplier<Boolean> _shippableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ShippingAddress getShippingAddress() {
		if (_shippingAddressSupplier != null) {
			shippingAddress = _shippingAddressSupplier.get();

			_shippingAddressSupplier = null;
		}

		return shippingAddress;
	}

	public void setShippingAddress(ShippingAddress shippingAddress) {
		this.shippingAddress = shippingAddress;

		_shippingAddressSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddress(
		UnsafeSupplier<ShippingAddress, Exception>
			shippingAddressUnsafeSupplier) {

		_shippingAddressSupplier = () -> {
			try {
				return shippingAddressUnsafeSupplier.get();
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
	protected ShippingAddress shippingAddress;

	@JsonIgnore
	private Supplier<ShippingAddress> _shippingAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AAB-34098-789-N")
	public String getShippingAddressExternalReferenceCode() {
		if (_shippingAddressExternalReferenceCodeSupplier != null) {
			shippingAddressExternalReferenceCode =
				_shippingAddressExternalReferenceCodeSupplier.get();

			_shippingAddressExternalReferenceCodeSupplier = null;
		}

		return shippingAddressExternalReferenceCode;
	}

	public void setShippingAddressExternalReferenceCode(
		String shippingAddressExternalReferenceCode) {

		this.shippingAddressExternalReferenceCode =
			shippingAddressExternalReferenceCode;

		_shippingAddressExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			shippingAddressExternalReferenceCodeUnsafeSupplier) {

		_shippingAddressExternalReferenceCodeSupplier = () -> {
			try {
				return shippingAddressExternalReferenceCodeUnsafeSupplier.get();
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
	protected String shippingAddressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _shippingAddressExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
	public Long getShippingAddressId() {
		if (_shippingAddressIdSupplier != null) {
			shippingAddressId = _shippingAddressIdSupplier.get();

			_shippingAddressIdSupplier = null;
		}

		return shippingAddressId;
	}

	public void setShippingAddressId(Long shippingAddressId) {
		this.shippingAddressId = shippingAddressId;

		_shippingAddressIdSupplier = null;
	}

	@JsonIgnore
	public void setShippingAddressId(
		UnsafeSupplier<Long, Exception> shippingAddressIdUnsafeSupplier) {

		_shippingAddressIdSupplier = () -> {
			try {
				return shippingAddressIdUnsafeSupplier.get();
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
	protected Long shippingAddressId;

	@JsonIgnore
	private Supplier<Long> _shippingAddressIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	@Valid
	public BigDecimal getShippingAmount() {
		if (_shippingAmountSupplier != null) {
			shippingAmount = _shippingAmountSupplier.get();

			_shippingAmountSupplier = null;
		}

		return shippingAmount;
	}

	public void setShippingAmount(BigDecimal shippingAmount) {
		this.shippingAmount = shippingAmount;

		_shippingAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingAmount(
		UnsafeSupplier<BigDecimal, Exception> shippingAmountUnsafeSupplier) {

		_shippingAmountSupplier = () -> {
			try {
				return shippingAmountUnsafeSupplier.get();
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
	protected BigDecimal shippingAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingAmountFormatted() {
		if (_shippingAmountFormattedSupplier != null) {
			shippingAmountFormatted = _shippingAmountFormattedSupplier.get();

			_shippingAmountFormattedSupplier = null;
		}

		return shippingAmountFormatted;
	}

	public void setShippingAmountFormatted(String shippingAmountFormatted) {
		this.shippingAmountFormatted = shippingAmountFormatted;

		_shippingAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingAmountFormatted(
		UnsafeSupplier<String, Exception>
			shippingAmountFormattedUnsafeSupplier) {

		_shippingAmountFormattedSupplier = () -> {
			try {
				return shippingAmountFormattedUnsafeSupplier.get();
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
	protected String shippingAmountFormatted;

	@JsonIgnore
	private Supplier<String> _shippingAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	public Double getShippingAmountValue() {
		if (_shippingAmountValueSupplier != null) {
			shippingAmountValue = _shippingAmountValueSupplier.get();

			_shippingAmountValueSupplier = null;
		}

		return shippingAmountValue;
	}

	public void setShippingAmountValue(Double shippingAmountValue) {
		this.shippingAmountValue = shippingAmountValue;

		_shippingAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setShippingAmountValue(
		UnsafeSupplier<Double, Exception> shippingAmountValueUnsafeSupplier) {

		_shippingAmountValueSupplier = () -> {
			try {
				return shippingAmountValueUnsafeSupplier.get();
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
	protected Double shippingAmountValue;

	@JsonIgnore
	private Supplier<Double> _shippingAmountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal getShippingDiscountAmount() {
		if (_shippingDiscountAmountSupplier != null) {
			shippingDiscountAmount = _shippingDiscountAmountSupplier.get();

			_shippingDiscountAmountSupplier = null;
		}

		return shippingDiscountAmount;
	}

	public void setShippingDiscountAmount(BigDecimal shippingDiscountAmount) {
		this.shippingDiscountAmount = shippingDiscountAmount;

		_shippingDiscountAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountAmountUnsafeSupplier) {

		_shippingDiscountAmountSupplier = () -> {
			try {
				return shippingDiscountAmountUnsafeSupplier.get();
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
	protected BigDecimal shippingDiscountAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingDiscountAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingDiscountAmountFormatted() {
		if (_shippingDiscountAmountFormattedSupplier != null) {
			shippingDiscountAmountFormatted =
				_shippingDiscountAmountFormattedSupplier.get();

			_shippingDiscountAmountFormattedSupplier = null;
		}

		return shippingDiscountAmountFormatted;
	}

	public void setShippingDiscountAmountFormatted(
		String shippingDiscountAmountFormatted) {

		this.shippingDiscountAmountFormatted = shippingDiscountAmountFormatted;

		_shippingDiscountAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountAmountFormatted(
		UnsafeSupplier<String, Exception>
			shippingDiscountAmountFormattedUnsafeSupplier) {

		_shippingDiscountAmountFormattedSupplier = () -> {
			try {
				return shippingDiscountAmountFormattedUnsafeSupplier.get();
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
	protected String shippingDiscountAmountFormatted;

	@JsonIgnore
	private Supplier<String> _shippingDiscountAmountFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getShippingDiscountAmountValue() {
		if (_shippingDiscountAmountValueSupplier != null) {
			shippingDiscountAmountValue =
				_shippingDiscountAmountValueSupplier.get();

			_shippingDiscountAmountValueSupplier = null;
		}

		return shippingDiscountAmountValue;
	}

	public void setShippingDiscountAmountValue(
		Double shippingDiscountAmountValue) {

		this.shippingDiscountAmountValue = shippingDiscountAmountValue;

		_shippingDiscountAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountAmountValue(
		UnsafeSupplier<Double, Exception>
			shippingDiscountAmountValueUnsafeSupplier) {

		_shippingDiscountAmountValueSupplier = () -> {
			try {
				return shippingDiscountAmountValueUnsafeSupplier.get();
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
	protected Double shippingDiscountAmountValue;

	@JsonIgnore
	private Supplier<Double> _shippingDiscountAmountValueSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel1() {
		if (_shippingDiscountPercentageLevel1Supplier != null) {
			shippingDiscountPercentageLevel1 =
				_shippingDiscountPercentageLevel1Supplier.get();

			_shippingDiscountPercentageLevel1Supplier = null;
		}

		return shippingDiscountPercentageLevel1;
	}

	public void setShippingDiscountPercentageLevel1(
		BigDecimal shippingDiscountPercentageLevel1) {

		this.shippingDiscountPercentageLevel1 =
			shippingDiscountPercentageLevel1;

		_shippingDiscountPercentageLevel1Supplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel1(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel1UnsafeSupplier) {

		_shippingDiscountPercentageLevel1Supplier = () -> {
			try {
				return shippingDiscountPercentageLevel1UnsafeSupplier.get();
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
	protected BigDecimal shippingDiscountPercentageLevel1;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingDiscountPercentageLevel1Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel1WithTaxAmount() {
		if (_shippingDiscountPercentageLevel1WithTaxAmountSupplier != null) {
			shippingDiscountPercentageLevel1WithTaxAmount =
				_shippingDiscountPercentageLevel1WithTaxAmountSupplier.get();

			_shippingDiscountPercentageLevel1WithTaxAmountSupplier = null;
		}

		return shippingDiscountPercentageLevel1WithTaxAmount;
	}

	public void setShippingDiscountPercentageLevel1WithTaxAmount(
		BigDecimal shippingDiscountPercentageLevel1WithTaxAmount) {

		this.shippingDiscountPercentageLevel1WithTaxAmount =
			shippingDiscountPercentageLevel1WithTaxAmount;

		_shippingDiscountPercentageLevel1WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel1WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel1WithTaxAmountUnsafeSupplier) {

		_shippingDiscountPercentageLevel1WithTaxAmountSupplier = () -> {
			try {
				return shippingDiscountPercentageLevel1WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal shippingDiscountPercentageLevel1WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_shippingDiscountPercentageLevel1WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel2() {
		if (_shippingDiscountPercentageLevel2Supplier != null) {
			shippingDiscountPercentageLevel2 =
				_shippingDiscountPercentageLevel2Supplier.get();

			_shippingDiscountPercentageLevel2Supplier = null;
		}

		return shippingDiscountPercentageLevel2;
	}

	public void setShippingDiscountPercentageLevel2(
		BigDecimal shippingDiscountPercentageLevel2) {

		this.shippingDiscountPercentageLevel2 =
			shippingDiscountPercentageLevel2;

		_shippingDiscountPercentageLevel2Supplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel2(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel2UnsafeSupplier) {

		_shippingDiscountPercentageLevel2Supplier = () -> {
			try {
				return shippingDiscountPercentageLevel2UnsafeSupplier.get();
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
	protected BigDecimal shippingDiscountPercentageLevel2;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingDiscountPercentageLevel2Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel2WithTaxAmount() {
		if (_shippingDiscountPercentageLevel2WithTaxAmountSupplier != null) {
			shippingDiscountPercentageLevel2WithTaxAmount =
				_shippingDiscountPercentageLevel2WithTaxAmountSupplier.get();

			_shippingDiscountPercentageLevel2WithTaxAmountSupplier = null;
		}

		return shippingDiscountPercentageLevel2WithTaxAmount;
	}

	public void setShippingDiscountPercentageLevel2WithTaxAmount(
		BigDecimal shippingDiscountPercentageLevel2WithTaxAmount) {

		this.shippingDiscountPercentageLevel2WithTaxAmount =
			shippingDiscountPercentageLevel2WithTaxAmount;

		_shippingDiscountPercentageLevel2WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel2WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel2WithTaxAmountUnsafeSupplier) {

		_shippingDiscountPercentageLevel2WithTaxAmountSupplier = () -> {
			try {
				return shippingDiscountPercentageLevel2WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal shippingDiscountPercentageLevel2WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_shippingDiscountPercentageLevel2WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel3() {
		if (_shippingDiscountPercentageLevel3Supplier != null) {
			shippingDiscountPercentageLevel3 =
				_shippingDiscountPercentageLevel3Supplier.get();

			_shippingDiscountPercentageLevel3Supplier = null;
		}

		return shippingDiscountPercentageLevel3;
	}

	public void setShippingDiscountPercentageLevel3(
		BigDecimal shippingDiscountPercentageLevel3) {

		this.shippingDiscountPercentageLevel3 =
			shippingDiscountPercentageLevel3;

		_shippingDiscountPercentageLevel3Supplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel3(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel3UnsafeSupplier) {

		_shippingDiscountPercentageLevel3Supplier = () -> {
			try {
				return shippingDiscountPercentageLevel3UnsafeSupplier.get();
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
	protected BigDecimal shippingDiscountPercentageLevel3;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingDiscountPercentageLevel3Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel3WithTaxAmount() {
		if (_shippingDiscountPercentageLevel3WithTaxAmountSupplier != null) {
			shippingDiscountPercentageLevel3WithTaxAmount =
				_shippingDiscountPercentageLevel3WithTaxAmountSupplier.get();

			_shippingDiscountPercentageLevel3WithTaxAmountSupplier = null;
		}

		return shippingDiscountPercentageLevel3WithTaxAmount;
	}

	public void setShippingDiscountPercentageLevel3WithTaxAmount(
		BigDecimal shippingDiscountPercentageLevel3WithTaxAmount) {

		this.shippingDiscountPercentageLevel3WithTaxAmount =
			shippingDiscountPercentageLevel3WithTaxAmount;

		_shippingDiscountPercentageLevel3WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel3WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel3WithTaxAmountUnsafeSupplier) {

		_shippingDiscountPercentageLevel3WithTaxAmountSupplier = () -> {
			try {
				return shippingDiscountPercentageLevel3WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal shippingDiscountPercentageLevel3WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_shippingDiscountPercentageLevel3WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel4() {
		if (_shippingDiscountPercentageLevel4Supplier != null) {
			shippingDiscountPercentageLevel4 =
				_shippingDiscountPercentageLevel4Supplier.get();

			_shippingDiscountPercentageLevel4Supplier = null;
		}

		return shippingDiscountPercentageLevel4;
	}

	public void setShippingDiscountPercentageLevel4(
		BigDecimal shippingDiscountPercentageLevel4) {

		this.shippingDiscountPercentageLevel4 =
			shippingDiscountPercentageLevel4;

		_shippingDiscountPercentageLevel4Supplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel4(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel4UnsafeSupplier) {

		_shippingDiscountPercentageLevel4Supplier = () -> {
			try {
				return shippingDiscountPercentageLevel4UnsafeSupplier.get();
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
	protected BigDecimal shippingDiscountPercentageLevel4;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingDiscountPercentageLevel4Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getShippingDiscountPercentageLevel4WithTaxAmount() {
		if (_shippingDiscountPercentageLevel4WithTaxAmountSupplier != null) {
			shippingDiscountPercentageLevel4WithTaxAmount =
				_shippingDiscountPercentageLevel4WithTaxAmountSupplier.get();

			_shippingDiscountPercentageLevel4WithTaxAmountSupplier = null;
		}

		return shippingDiscountPercentageLevel4WithTaxAmount;
	}

	public void setShippingDiscountPercentageLevel4WithTaxAmount(
		BigDecimal shippingDiscountPercentageLevel4WithTaxAmount) {

		this.shippingDiscountPercentageLevel4WithTaxAmount =
			shippingDiscountPercentageLevel4WithTaxAmount;

		_shippingDiscountPercentageLevel4WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentageLevel4WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountPercentageLevel4WithTaxAmountUnsafeSupplier) {

		_shippingDiscountPercentageLevel4WithTaxAmountSupplier = () -> {
			try {
				return shippingDiscountPercentageLevel4WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal shippingDiscountPercentageLevel4WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_shippingDiscountPercentageLevel4WithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal getShippingDiscountWithTaxAmount() {
		if (_shippingDiscountWithTaxAmountSupplier != null) {
			shippingDiscountWithTaxAmount =
				_shippingDiscountWithTaxAmountSupplier.get();

			_shippingDiscountWithTaxAmountSupplier = null;
		}

		return shippingDiscountWithTaxAmount;
	}

	public void setShippingDiscountWithTaxAmount(
		BigDecimal shippingDiscountWithTaxAmount) {

		this.shippingDiscountWithTaxAmount = shippingDiscountWithTaxAmount;

		_shippingDiscountWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingDiscountWithTaxAmountUnsafeSupplier) {

		_shippingDiscountWithTaxAmountSupplier = () -> {
			try {
				return shippingDiscountWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal shippingDiscountWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingDiscountWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingDiscountWithTaxAmountFormatted() {
		if (_shippingDiscountWithTaxAmountFormattedSupplier != null) {
			shippingDiscountWithTaxAmountFormatted =
				_shippingDiscountWithTaxAmountFormattedSupplier.get();

			_shippingDiscountWithTaxAmountFormattedSupplier = null;
		}

		return shippingDiscountWithTaxAmountFormatted;
	}

	public void setShippingDiscountWithTaxAmountFormatted(
		String shippingDiscountWithTaxAmountFormatted) {

		this.shippingDiscountWithTaxAmountFormatted =
			shippingDiscountWithTaxAmountFormatted;

		_shippingDiscountWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			shippingDiscountWithTaxAmountFormattedUnsafeSupplier) {

		_shippingDiscountWithTaxAmountFormattedSupplier = () -> {
			try {
				return shippingDiscountWithTaxAmountFormattedUnsafeSupplier.
					get();
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
	protected String shippingDiscountWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _shippingDiscountWithTaxAmountFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "fixed")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String shippingMethod;

	@JsonIgnore
	private Supplier<String> _shippingMethodSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "by Air")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String shippingOption;

	@JsonIgnore
	private Supplier<String> _shippingOptionSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	@Valid
	public BigDecimal getShippingWithTaxAmount() {
		if (_shippingWithTaxAmountSupplier != null) {
			shippingWithTaxAmount = _shippingWithTaxAmountSupplier.get();

			_shippingWithTaxAmountSupplier = null;
		}

		return shippingWithTaxAmount;
	}

	public void setShippingWithTaxAmount(BigDecimal shippingWithTaxAmount) {
		this.shippingWithTaxAmount = shippingWithTaxAmount;

		_shippingWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			shippingWithTaxAmountUnsafeSupplier) {

		_shippingWithTaxAmountSupplier = () -> {
			try {
				return shippingWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal shippingWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingWithTaxAmountFormatted() {
		if (_shippingWithTaxAmountFormattedSupplier != null) {
			shippingWithTaxAmountFormatted =
				_shippingWithTaxAmountFormattedSupplier.get();

			_shippingWithTaxAmountFormattedSupplier = null;
		}

		return shippingWithTaxAmountFormatted;
	}

	public void setShippingWithTaxAmountFormatted(
		String shippingWithTaxAmountFormatted) {

		this.shippingWithTaxAmountFormatted = shippingWithTaxAmountFormatted;

		_shippingWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			shippingWithTaxAmountFormattedUnsafeSupplier) {

		_shippingWithTaxAmountFormattedSupplier = () -> {
			try {
				return shippingWithTaxAmountFormattedUnsafeSupplier.get();
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
	protected String shippingWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _shippingWithTaxAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	public Double getShippingWithTaxAmountValue() {
		if (_shippingWithTaxAmountValueSupplier != null) {
			shippingWithTaxAmountValue =
				_shippingWithTaxAmountValueSupplier.get();

			_shippingWithTaxAmountValueSupplier = null;
		}

		return shippingWithTaxAmountValue;
	}

	public void setShippingWithTaxAmountValue(
		Double shippingWithTaxAmountValue) {

		this.shippingWithTaxAmountValue = shippingWithTaxAmountValue;

		_shippingWithTaxAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setShippingWithTaxAmountValue(
		UnsafeSupplier<Double, Exception>
			shippingWithTaxAmountValueUnsafeSupplier) {

		_shippingWithTaxAmountValueSupplier = () -> {
			try {
				return shippingWithTaxAmountValueUnsafeSupplier.get();
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
	protected Double shippingWithTaxAmountValue;

	@JsonIgnore
	private Supplier<Double> _shippingWithTaxAmountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal getSubtotal() {
		if (_subtotalSupplier != null) {
			subtotal = _subtotalSupplier.get();

			_subtotalSupplier = null;
		}

		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;

		_subtotalSupplier = null;
	}

	@JsonIgnore
	public void setSubtotal(
		UnsafeSupplier<BigDecimal, Exception> subtotalUnsafeSupplier) {

		_subtotalSupplier = () -> {
			try {
				return subtotalUnsafeSupplier.get();
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
	protected BigDecimal subtotal;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getSubtotalAmount() {
		if (_subtotalAmountSupplier != null) {
			subtotalAmount = _subtotalAmountSupplier.get();

			_subtotalAmountSupplier = null;
		}

		return subtotalAmount;
	}

	public void setSubtotalAmount(Double subtotalAmount) {
		this.subtotalAmount = subtotalAmount;

		_subtotalAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalAmount(
		UnsafeSupplier<Double, Exception> subtotalAmountUnsafeSupplier) {

		_subtotalAmountSupplier = () -> {
			try {
				return subtotalAmountUnsafeSupplier.get();
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
	protected Double subtotalAmount;

	@JsonIgnore
	private Supplier<Double> _subtotalAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal getSubtotalDiscountAmount() {
		if (_subtotalDiscountAmountSupplier != null) {
			subtotalDiscountAmount = _subtotalDiscountAmountSupplier.get();

			_subtotalDiscountAmountSupplier = null;
		}

		return subtotalDiscountAmount;
	}

	public void setSubtotalDiscountAmount(BigDecimal subtotalDiscountAmount) {
		this.subtotalDiscountAmount = subtotalDiscountAmount;

		_subtotalDiscountAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountAmountUnsafeSupplier) {

		_subtotalDiscountAmountSupplier = () -> {
			try {
				return subtotalDiscountAmountUnsafeSupplier.get();
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
	protected BigDecimal subtotalDiscountAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalDiscountAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubtotalDiscountAmountFormatted() {
		if (_subtotalDiscountAmountFormattedSupplier != null) {
			subtotalDiscountAmountFormatted =
				_subtotalDiscountAmountFormattedSupplier.get();

			_subtotalDiscountAmountFormattedSupplier = null;
		}

		return subtotalDiscountAmountFormatted;
	}

	public void setSubtotalDiscountAmountFormatted(
		String subtotalDiscountAmountFormatted) {

		this.subtotalDiscountAmountFormatted = subtotalDiscountAmountFormatted;

		_subtotalDiscountAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountAmountFormatted(
		UnsafeSupplier<String, Exception>
			subtotalDiscountAmountFormattedUnsafeSupplier) {

		_subtotalDiscountAmountFormattedSupplier = () -> {
			try {
				return subtotalDiscountAmountFormattedUnsafeSupplier.get();
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
	protected String subtotalDiscountAmountFormatted;

	@JsonIgnore
	private Supplier<String> _subtotalDiscountAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel1() {
		if (_subtotalDiscountPercentageLevel1Supplier != null) {
			subtotalDiscountPercentageLevel1 =
				_subtotalDiscountPercentageLevel1Supplier.get();

			_subtotalDiscountPercentageLevel1Supplier = null;
		}

		return subtotalDiscountPercentageLevel1;
	}

	public void setSubtotalDiscountPercentageLevel1(
		BigDecimal subtotalDiscountPercentageLevel1) {

		this.subtotalDiscountPercentageLevel1 =
			subtotalDiscountPercentageLevel1;

		_subtotalDiscountPercentageLevel1Supplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel1(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel1UnsafeSupplier) {

		_subtotalDiscountPercentageLevel1Supplier = () -> {
			try {
				return subtotalDiscountPercentageLevel1UnsafeSupplier.get();
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
	protected BigDecimal subtotalDiscountPercentageLevel1;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalDiscountPercentageLevel1Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel1WithTaxAmount() {
		if (_subtotalDiscountPercentageLevel1WithTaxAmountSupplier != null) {
			subtotalDiscountPercentageLevel1WithTaxAmount =
				_subtotalDiscountPercentageLevel1WithTaxAmountSupplier.get();

			_subtotalDiscountPercentageLevel1WithTaxAmountSupplier = null;
		}

		return subtotalDiscountPercentageLevel1WithTaxAmount;
	}

	public void setSubtotalDiscountPercentageLevel1WithTaxAmount(
		BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount) {

		this.subtotalDiscountPercentageLevel1WithTaxAmount =
			subtotalDiscountPercentageLevel1WithTaxAmount;

		_subtotalDiscountPercentageLevel1WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel1WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel1WithTaxAmountUnsafeSupplier) {

		_subtotalDiscountPercentageLevel1WithTaxAmountSupplier = () -> {
			try {
				return subtotalDiscountPercentageLevel1WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_subtotalDiscountPercentageLevel1WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel2() {
		if (_subtotalDiscountPercentageLevel2Supplier != null) {
			subtotalDiscountPercentageLevel2 =
				_subtotalDiscountPercentageLevel2Supplier.get();

			_subtotalDiscountPercentageLevel2Supplier = null;
		}

		return subtotalDiscountPercentageLevel2;
	}

	public void setSubtotalDiscountPercentageLevel2(
		BigDecimal subtotalDiscountPercentageLevel2) {

		this.subtotalDiscountPercentageLevel2 =
			subtotalDiscountPercentageLevel2;

		_subtotalDiscountPercentageLevel2Supplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel2(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel2UnsafeSupplier) {

		_subtotalDiscountPercentageLevel2Supplier = () -> {
			try {
				return subtotalDiscountPercentageLevel2UnsafeSupplier.get();
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
	protected BigDecimal subtotalDiscountPercentageLevel2;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalDiscountPercentageLevel2Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel2WithTaxAmount() {
		if (_subtotalDiscountPercentageLevel2WithTaxAmountSupplier != null) {
			subtotalDiscountPercentageLevel2WithTaxAmount =
				_subtotalDiscountPercentageLevel2WithTaxAmountSupplier.get();

			_subtotalDiscountPercentageLevel2WithTaxAmountSupplier = null;
		}

		return subtotalDiscountPercentageLevel2WithTaxAmount;
	}

	public void setSubtotalDiscountPercentageLevel2WithTaxAmount(
		BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount) {

		this.subtotalDiscountPercentageLevel2WithTaxAmount =
			subtotalDiscountPercentageLevel2WithTaxAmount;

		_subtotalDiscountPercentageLevel2WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel2WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel2WithTaxAmountUnsafeSupplier) {

		_subtotalDiscountPercentageLevel2WithTaxAmountSupplier = () -> {
			try {
				return subtotalDiscountPercentageLevel2WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_subtotalDiscountPercentageLevel2WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel3() {
		if (_subtotalDiscountPercentageLevel3Supplier != null) {
			subtotalDiscountPercentageLevel3 =
				_subtotalDiscountPercentageLevel3Supplier.get();

			_subtotalDiscountPercentageLevel3Supplier = null;
		}

		return subtotalDiscountPercentageLevel3;
	}

	public void setSubtotalDiscountPercentageLevel3(
		BigDecimal subtotalDiscountPercentageLevel3) {

		this.subtotalDiscountPercentageLevel3 =
			subtotalDiscountPercentageLevel3;

		_subtotalDiscountPercentageLevel3Supplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel3(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel3UnsafeSupplier) {

		_subtotalDiscountPercentageLevel3Supplier = () -> {
			try {
				return subtotalDiscountPercentageLevel3UnsafeSupplier.get();
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
	protected BigDecimal subtotalDiscountPercentageLevel3;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalDiscountPercentageLevel3Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel3WithTaxAmount() {
		if (_subtotalDiscountPercentageLevel3WithTaxAmountSupplier != null) {
			subtotalDiscountPercentageLevel3WithTaxAmount =
				_subtotalDiscountPercentageLevel3WithTaxAmountSupplier.get();

			_subtotalDiscountPercentageLevel3WithTaxAmountSupplier = null;
		}

		return subtotalDiscountPercentageLevel3WithTaxAmount;
	}

	public void setSubtotalDiscountPercentageLevel3WithTaxAmount(
		BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount) {

		this.subtotalDiscountPercentageLevel3WithTaxAmount =
			subtotalDiscountPercentageLevel3WithTaxAmount;

		_subtotalDiscountPercentageLevel3WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel3WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel3WithTaxAmountUnsafeSupplier) {

		_subtotalDiscountPercentageLevel3WithTaxAmountSupplier = () -> {
			try {
				return subtotalDiscountPercentageLevel3WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_subtotalDiscountPercentageLevel3WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel4() {
		if (_subtotalDiscountPercentageLevel4Supplier != null) {
			subtotalDiscountPercentageLevel4 =
				_subtotalDiscountPercentageLevel4Supplier.get();

			_subtotalDiscountPercentageLevel4Supplier = null;
		}

		return subtotalDiscountPercentageLevel4;
	}

	public void setSubtotalDiscountPercentageLevel4(
		BigDecimal subtotalDiscountPercentageLevel4) {

		this.subtotalDiscountPercentageLevel4 =
			subtotalDiscountPercentageLevel4;

		_subtotalDiscountPercentageLevel4Supplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel4(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel4UnsafeSupplier) {

		_subtotalDiscountPercentageLevel4Supplier = () -> {
			try {
				return subtotalDiscountPercentageLevel4UnsafeSupplier.get();
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
	protected BigDecimal subtotalDiscountPercentageLevel4;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalDiscountPercentageLevel4Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getSubtotalDiscountPercentageLevel4WithTaxAmount() {
		if (_subtotalDiscountPercentageLevel4WithTaxAmountSupplier != null) {
			subtotalDiscountPercentageLevel4WithTaxAmount =
				_subtotalDiscountPercentageLevel4WithTaxAmountSupplier.get();

			_subtotalDiscountPercentageLevel4WithTaxAmountSupplier = null;
		}

		return subtotalDiscountPercentageLevel4WithTaxAmount;
	}

	public void setSubtotalDiscountPercentageLevel4WithTaxAmount(
		BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount) {

		this.subtotalDiscountPercentageLevel4WithTaxAmount =
			subtotalDiscountPercentageLevel4WithTaxAmount;

		_subtotalDiscountPercentageLevel4WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentageLevel4WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountPercentageLevel4WithTaxAmountUnsafeSupplier) {

		_subtotalDiscountPercentageLevel4WithTaxAmountSupplier = () -> {
			try {
				return subtotalDiscountPercentageLevel4WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_subtotalDiscountPercentageLevel4WithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal getSubtotalDiscountWithTaxAmount() {
		if (_subtotalDiscountWithTaxAmountSupplier != null) {
			subtotalDiscountWithTaxAmount =
				_subtotalDiscountWithTaxAmountSupplier.get();

			_subtotalDiscountWithTaxAmountSupplier = null;
		}

		return subtotalDiscountWithTaxAmount;
	}

	public void setSubtotalDiscountWithTaxAmount(
		BigDecimal subtotalDiscountWithTaxAmount) {

		this.subtotalDiscountWithTaxAmount = subtotalDiscountWithTaxAmount;

		_subtotalDiscountWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalDiscountWithTaxAmountUnsafeSupplier) {

		_subtotalDiscountWithTaxAmountSupplier = () -> {
			try {
				return subtotalDiscountWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal subtotalDiscountWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalDiscountWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubtotalDiscountWithTaxAmountFormatted() {
		if (_subtotalDiscountWithTaxAmountFormattedSupplier != null) {
			subtotalDiscountWithTaxAmountFormatted =
				_subtotalDiscountWithTaxAmountFormattedSupplier.get();

			_subtotalDiscountWithTaxAmountFormattedSupplier = null;
		}

		return subtotalDiscountWithTaxAmountFormatted;
	}

	public void setSubtotalDiscountWithTaxAmountFormatted(
		String subtotalDiscountWithTaxAmountFormatted) {

		this.subtotalDiscountWithTaxAmountFormatted =
			subtotalDiscountWithTaxAmountFormatted;

		_subtotalDiscountWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			subtotalDiscountWithTaxAmountFormattedUnsafeSupplier) {

		_subtotalDiscountWithTaxAmountFormattedSupplier = () -> {
			try {
				return subtotalDiscountWithTaxAmountFormattedUnsafeSupplier.
					get();
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
	protected String subtotalDiscountWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _subtotalDiscountWithTaxAmountFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubtotalFormatted() {
		if (_subtotalFormattedSupplier != null) {
			subtotalFormatted = _subtotalFormattedSupplier.get();

			_subtotalFormattedSupplier = null;
		}

		return subtotalFormatted;
	}

	public void setSubtotalFormatted(String subtotalFormatted) {
		this.subtotalFormatted = subtotalFormatted;

		_subtotalFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalFormatted(
		UnsafeSupplier<String, Exception> subtotalFormattedUnsafeSupplier) {

		_subtotalFormattedSupplier = () -> {
			try {
				return subtotalFormattedUnsafeSupplier.get();
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
	protected String subtotalFormatted;

	@JsonIgnore
	private Supplier<String> _subtotalFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal getSubtotalWithTaxAmount() {
		if (_subtotalWithTaxAmountSupplier != null) {
			subtotalWithTaxAmount = _subtotalWithTaxAmountSupplier.get();

			_subtotalWithTaxAmountSupplier = null;
		}

		return subtotalWithTaxAmount;
	}

	public void setSubtotalWithTaxAmount(BigDecimal subtotalWithTaxAmount) {
		this.subtotalWithTaxAmount = subtotalWithTaxAmount;

		_subtotalWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			subtotalWithTaxAmountUnsafeSupplier) {

		_subtotalWithTaxAmountSupplier = () -> {
			try {
				return subtotalWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal subtotalWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _subtotalWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubtotalWithTaxAmountFormatted() {
		if (_subtotalWithTaxAmountFormattedSupplier != null) {
			subtotalWithTaxAmountFormatted =
				_subtotalWithTaxAmountFormattedSupplier.get();

			_subtotalWithTaxAmountFormattedSupplier = null;
		}

		return subtotalWithTaxAmountFormatted;
	}

	public void setSubtotalWithTaxAmountFormatted(
		String subtotalWithTaxAmountFormatted) {

		this.subtotalWithTaxAmountFormatted = subtotalWithTaxAmountFormatted;

		_subtotalWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			subtotalWithTaxAmountFormattedUnsafeSupplier) {

		_subtotalWithTaxAmountFormattedSupplier = () -> {
			try {
				return subtotalWithTaxAmountFormattedUnsafeSupplier.get();
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
	protected String subtotalWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _subtotalWithTaxAmountFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getSubtotalWithTaxAmountValue() {
		if (_subtotalWithTaxAmountValueSupplier != null) {
			subtotalWithTaxAmountValue =
				_subtotalWithTaxAmountValueSupplier.get();

			_subtotalWithTaxAmountValueSupplier = null;
		}

		return subtotalWithTaxAmountValue;
	}

	public void setSubtotalWithTaxAmountValue(
		Double subtotalWithTaxAmountValue) {

		this.subtotalWithTaxAmountValue = subtotalWithTaxAmountValue;

		_subtotalWithTaxAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalWithTaxAmountValue(
		UnsafeSupplier<Double, Exception>
			subtotalWithTaxAmountValueUnsafeSupplier) {

		_subtotalWithTaxAmountValueSupplier = () -> {
			try {
				return subtotalWithTaxAmountValueUnsafeSupplier.get();
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
	protected Double subtotalWithTaxAmountValue;

	@JsonIgnore
	private Supplier<Double> _subtotalWithTaxAmountValueSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11")
	@Valid
	public BigDecimal getTaxAmount() {
		if (_taxAmountSupplier != null) {
			taxAmount = _taxAmountSupplier.get();

			_taxAmountSupplier = null;
		}

		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;

		_taxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTaxAmount(
		UnsafeSupplier<BigDecimal, Exception> taxAmountUnsafeSupplier) {

		_taxAmountSupplier = () -> {
			try {
				return taxAmountUnsafeSupplier.get();
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
	protected BigDecimal taxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _taxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTaxAmountFormatted() {
		if (_taxAmountFormattedSupplier != null) {
			taxAmountFormatted = _taxAmountFormattedSupplier.get();

			_taxAmountFormattedSupplier = null;
		}

		return taxAmountFormatted;
	}

	public void setTaxAmountFormatted(String taxAmountFormatted) {
		this.taxAmountFormatted = taxAmountFormatted;

		_taxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTaxAmountFormatted(
		UnsafeSupplier<String, Exception> taxAmountFormattedUnsafeSupplier) {

		_taxAmountFormattedSupplier = () -> {
			try {
				return taxAmountFormattedUnsafeSupplier.get();
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
	protected String taxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _taxAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	public Double getTaxAmountValue() {
		if (_taxAmountValueSupplier != null) {
			taxAmountValue = _taxAmountValueSupplier.get();

			_taxAmountValueSupplier = null;
		}

		return taxAmountValue;
	}

	public void setTaxAmountValue(Double taxAmountValue) {
		this.taxAmountValue = taxAmountValue;

		_taxAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setTaxAmountValue(
		UnsafeSupplier<Double, Exception> taxAmountValueUnsafeSupplier) {

		_taxAmountValueSupplier = () -> {
			try {
				return taxAmountValueUnsafeSupplier.get();
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
	protected Double taxAmountValue;

	@JsonIgnore
	private Supplier<Double> _taxAmountValueSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "113")
	@Valid
	public BigDecimal getTotal() {
		if (_totalSupplier != null) {
			total = _totalSupplier.get();

			_totalSupplier = null;
		}

		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;

		_totalSupplier = null;
	}

	@JsonIgnore
	public void setTotal(
		UnsafeSupplier<BigDecimal, Exception> totalUnsafeSupplier) {

		_totalSupplier = () -> {
			try {
				return totalUnsafeSupplier.get();
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
	protected BigDecimal total;

	@JsonIgnore
	private Supplier<BigDecimal> _totalSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "113")
	public Double getTotalAmount() {
		if (_totalAmountSupplier != null) {
			totalAmount = _totalAmountSupplier.get();

			_totalAmountSupplier = null;
		}

		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;

		_totalAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalAmount(
		UnsafeSupplier<Double, Exception> totalAmountUnsafeSupplier) {

		_totalAmountSupplier = () -> {
			try {
				return totalAmountUnsafeSupplier.get();
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
	protected Double totalAmount;

	@JsonIgnore
	private Supplier<Double> _totalAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11")
	@Valid
	public BigDecimal getTotalDiscountAmount() {
		if (_totalDiscountAmountSupplier != null) {
			totalDiscountAmount = _totalDiscountAmountSupplier.get();

			_totalDiscountAmountSupplier = null;
		}

		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;

		_totalDiscountAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountAmountUnsafeSupplier) {

		_totalDiscountAmountSupplier = () -> {
			try {
				return totalDiscountAmountUnsafeSupplier.get();
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
	protected BigDecimal totalDiscountAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _totalDiscountAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalDiscountAmountFormatted() {
		if (_totalDiscountAmountFormattedSupplier != null) {
			totalDiscountAmountFormatted =
				_totalDiscountAmountFormattedSupplier.get();

			_totalDiscountAmountFormattedSupplier = null;
		}

		return totalDiscountAmountFormatted;
	}

	public void setTotalDiscountAmountFormatted(
		String totalDiscountAmountFormatted) {

		this.totalDiscountAmountFormatted = totalDiscountAmountFormatted;

		_totalDiscountAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountAmountFormatted(
		UnsafeSupplier<String, Exception>
			totalDiscountAmountFormattedUnsafeSupplier) {

		_totalDiscountAmountFormattedSupplier = () -> {
			try {
				return totalDiscountAmountFormattedUnsafeSupplier.get();
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
	protected String totalDiscountAmountFormatted;

	@JsonIgnore
	private Supplier<String> _totalDiscountAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11")
	public Double getTotalDiscountAmountValue() {
		if (_totalDiscountAmountValueSupplier != null) {
			totalDiscountAmountValue = _totalDiscountAmountValueSupplier.get();

			_totalDiscountAmountValueSupplier = null;
		}

		return totalDiscountAmountValue;
	}

	public void setTotalDiscountAmountValue(Double totalDiscountAmountValue) {
		this.totalDiscountAmountValue = totalDiscountAmountValue;

		_totalDiscountAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountAmountValue(
		UnsafeSupplier<Double, Exception>
			totalDiscountAmountValueUnsafeSupplier) {

		_totalDiscountAmountValueSupplier = () -> {
			try {
				return totalDiscountAmountValueUnsafeSupplier.get();
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
	protected Double totalDiscountAmountValue;

	@JsonIgnore
	private Supplier<Double> _totalDiscountAmountValueSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel1() {
		if (_totalDiscountPercentageLevel1Supplier != null) {
			totalDiscountPercentageLevel1 =
				_totalDiscountPercentageLevel1Supplier.get();

			_totalDiscountPercentageLevel1Supplier = null;
		}

		return totalDiscountPercentageLevel1;
	}

	public void setTotalDiscountPercentageLevel1(
		BigDecimal totalDiscountPercentageLevel1) {

		this.totalDiscountPercentageLevel1 = totalDiscountPercentageLevel1;

		_totalDiscountPercentageLevel1Supplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel1(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel1UnsafeSupplier) {

		_totalDiscountPercentageLevel1Supplier = () -> {
			try {
				return totalDiscountPercentageLevel1UnsafeSupplier.get();
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
	protected BigDecimal totalDiscountPercentageLevel1;

	@JsonIgnore
	private Supplier<BigDecimal> _totalDiscountPercentageLevel1Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel1WithTaxAmount() {
		if (_totalDiscountPercentageLevel1WithTaxAmountSupplier != null) {
			totalDiscountPercentageLevel1WithTaxAmount =
				_totalDiscountPercentageLevel1WithTaxAmountSupplier.get();

			_totalDiscountPercentageLevel1WithTaxAmountSupplier = null;
		}

		return totalDiscountPercentageLevel1WithTaxAmount;
	}

	public void setTotalDiscountPercentageLevel1WithTaxAmount(
		BigDecimal totalDiscountPercentageLevel1WithTaxAmount) {

		this.totalDiscountPercentageLevel1WithTaxAmount =
			totalDiscountPercentageLevel1WithTaxAmount;

		_totalDiscountPercentageLevel1WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel1WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel1WithTaxAmountUnsafeSupplier) {

		_totalDiscountPercentageLevel1WithTaxAmountSupplier = () -> {
			try {
				return totalDiscountPercentageLevel1WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal totalDiscountPercentageLevel1WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_totalDiscountPercentageLevel1WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel2() {
		if (_totalDiscountPercentageLevel2Supplier != null) {
			totalDiscountPercentageLevel2 =
				_totalDiscountPercentageLevel2Supplier.get();

			_totalDiscountPercentageLevel2Supplier = null;
		}

		return totalDiscountPercentageLevel2;
	}

	public void setTotalDiscountPercentageLevel2(
		BigDecimal totalDiscountPercentageLevel2) {

		this.totalDiscountPercentageLevel2 = totalDiscountPercentageLevel2;

		_totalDiscountPercentageLevel2Supplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel2(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel2UnsafeSupplier) {

		_totalDiscountPercentageLevel2Supplier = () -> {
			try {
				return totalDiscountPercentageLevel2UnsafeSupplier.get();
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
	protected BigDecimal totalDiscountPercentageLevel2;

	@JsonIgnore
	private Supplier<BigDecimal> _totalDiscountPercentageLevel2Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel2WithTaxAmount() {
		if (_totalDiscountPercentageLevel2WithTaxAmountSupplier != null) {
			totalDiscountPercentageLevel2WithTaxAmount =
				_totalDiscountPercentageLevel2WithTaxAmountSupplier.get();

			_totalDiscountPercentageLevel2WithTaxAmountSupplier = null;
		}

		return totalDiscountPercentageLevel2WithTaxAmount;
	}

	public void setTotalDiscountPercentageLevel2WithTaxAmount(
		BigDecimal totalDiscountPercentageLevel2WithTaxAmount) {

		this.totalDiscountPercentageLevel2WithTaxAmount =
			totalDiscountPercentageLevel2WithTaxAmount;

		_totalDiscountPercentageLevel2WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel2WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel2WithTaxAmountUnsafeSupplier) {

		_totalDiscountPercentageLevel2WithTaxAmountSupplier = () -> {
			try {
				return totalDiscountPercentageLevel2WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal totalDiscountPercentageLevel2WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_totalDiscountPercentageLevel2WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel3() {
		if (_totalDiscountPercentageLevel3Supplier != null) {
			totalDiscountPercentageLevel3 =
				_totalDiscountPercentageLevel3Supplier.get();

			_totalDiscountPercentageLevel3Supplier = null;
		}

		return totalDiscountPercentageLevel3;
	}

	public void setTotalDiscountPercentageLevel3(
		BigDecimal totalDiscountPercentageLevel3) {

		this.totalDiscountPercentageLevel3 = totalDiscountPercentageLevel3;

		_totalDiscountPercentageLevel3Supplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel3(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel3UnsafeSupplier) {

		_totalDiscountPercentageLevel3Supplier = () -> {
			try {
				return totalDiscountPercentageLevel3UnsafeSupplier.get();
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
	protected BigDecimal totalDiscountPercentageLevel3;

	@JsonIgnore
	private Supplier<BigDecimal> _totalDiscountPercentageLevel3Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel3WithTaxAmount() {
		if (_totalDiscountPercentageLevel3WithTaxAmountSupplier != null) {
			totalDiscountPercentageLevel3WithTaxAmount =
				_totalDiscountPercentageLevel3WithTaxAmountSupplier.get();

			_totalDiscountPercentageLevel3WithTaxAmountSupplier = null;
		}

		return totalDiscountPercentageLevel3WithTaxAmount;
	}

	public void setTotalDiscountPercentageLevel3WithTaxAmount(
		BigDecimal totalDiscountPercentageLevel3WithTaxAmount) {

		this.totalDiscountPercentageLevel3WithTaxAmount =
			totalDiscountPercentageLevel3WithTaxAmount;

		_totalDiscountPercentageLevel3WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel3WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel3WithTaxAmountUnsafeSupplier) {

		_totalDiscountPercentageLevel3WithTaxAmountSupplier = () -> {
			try {
				return totalDiscountPercentageLevel3WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal totalDiscountPercentageLevel3WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_totalDiscountPercentageLevel3WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel4() {
		if (_totalDiscountPercentageLevel4Supplier != null) {
			totalDiscountPercentageLevel4 =
				_totalDiscountPercentageLevel4Supplier.get();

			_totalDiscountPercentageLevel4Supplier = null;
		}

		return totalDiscountPercentageLevel4;
	}

	public void setTotalDiscountPercentageLevel4(
		BigDecimal totalDiscountPercentageLevel4) {

		this.totalDiscountPercentageLevel4 = totalDiscountPercentageLevel4;

		_totalDiscountPercentageLevel4Supplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel4(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel4UnsafeSupplier) {

		_totalDiscountPercentageLevel4Supplier = () -> {
			try {
				return totalDiscountPercentageLevel4UnsafeSupplier.get();
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
	protected BigDecimal totalDiscountPercentageLevel4;

	@JsonIgnore
	private Supplier<BigDecimal> _totalDiscountPercentageLevel4Supplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public BigDecimal getTotalDiscountPercentageLevel4WithTaxAmount() {
		if (_totalDiscountPercentageLevel4WithTaxAmountSupplier != null) {
			totalDiscountPercentageLevel4WithTaxAmount =
				_totalDiscountPercentageLevel4WithTaxAmountSupplier.get();

			_totalDiscountPercentageLevel4WithTaxAmountSupplier = null;
		}

		return totalDiscountPercentageLevel4WithTaxAmount;
	}

	public void setTotalDiscountPercentageLevel4WithTaxAmount(
		BigDecimal totalDiscountPercentageLevel4WithTaxAmount) {

		this.totalDiscountPercentageLevel4WithTaxAmount =
			totalDiscountPercentageLevel4WithTaxAmount;

		_totalDiscountPercentageLevel4WithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentageLevel4WithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountPercentageLevel4WithTaxAmountUnsafeSupplier) {

		_totalDiscountPercentageLevel4WithTaxAmountSupplier = () -> {
			try {
				return totalDiscountPercentageLevel4WithTaxAmountUnsafeSupplier.
					get();
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
	protected BigDecimal totalDiscountPercentageLevel4WithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal>
		_totalDiscountPercentageLevel4WithTaxAmountSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11")
	@Valid
	public BigDecimal getTotalDiscountWithTaxAmount() {
		if (_totalDiscountWithTaxAmountSupplier != null) {
			totalDiscountWithTaxAmount =
				_totalDiscountWithTaxAmountSupplier.get();

			_totalDiscountWithTaxAmountSupplier = null;
		}

		return totalDiscountWithTaxAmount;
	}

	public void setTotalDiscountWithTaxAmount(
		BigDecimal totalDiscountWithTaxAmount) {

		this.totalDiscountWithTaxAmount = totalDiscountWithTaxAmount;

		_totalDiscountWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalDiscountWithTaxAmountUnsafeSupplier) {

		_totalDiscountWithTaxAmountSupplier = () -> {
			try {
				return totalDiscountWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal totalDiscountWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _totalDiscountWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalDiscountWithTaxAmountFormatted() {
		if (_totalDiscountWithTaxAmountFormattedSupplier != null) {
			totalDiscountWithTaxAmountFormatted =
				_totalDiscountWithTaxAmountFormattedSupplier.get();

			_totalDiscountWithTaxAmountFormattedSupplier = null;
		}

		return totalDiscountWithTaxAmountFormatted;
	}

	public void setTotalDiscountWithTaxAmountFormatted(
		String totalDiscountWithTaxAmountFormatted) {

		this.totalDiscountWithTaxAmountFormatted =
			totalDiscountWithTaxAmountFormatted;

		_totalDiscountWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			totalDiscountWithTaxAmountFormattedUnsafeSupplier) {

		_totalDiscountWithTaxAmountFormattedSupplier = () -> {
			try {
				return totalDiscountWithTaxAmountFormattedUnsafeSupplier.get();
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
	protected String totalDiscountWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _totalDiscountWithTaxAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11")
	public Double getTotalDiscountWithTaxAmountValue() {
		if (_totalDiscountWithTaxAmountValueSupplier != null) {
			totalDiscountWithTaxAmountValue =
				_totalDiscountWithTaxAmountValueSupplier.get();

			_totalDiscountWithTaxAmountValueSupplier = null;
		}

		return totalDiscountWithTaxAmountValue;
	}

	public void setTotalDiscountWithTaxAmountValue(
		Double totalDiscountWithTaxAmountValue) {

		this.totalDiscountWithTaxAmountValue = totalDiscountWithTaxAmountValue;

		_totalDiscountWithTaxAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountWithTaxAmountValue(
		UnsafeSupplier<Double, Exception>
			totalDiscountWithTaxAmountValueUnsafeSupplier) {

		_totalDiscountWithTaxAmountValueSupplier = () -> {
			try {
				return totalDiscountWithTaxAmountValueUnsafeSupplier.get();
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
	protected Double totalDiscountWithTaxAmountValue;

	@JsonIgnore
	private Supplier<Double> _totalDiscountWithTaxAmountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalFormatted() {
		if (_totalFormattedSupplier != null) {
			totalFormatted = _totalFormattedSupplier.get();

			_totalFormattedSupplier = null;
		}

		return totalFormatted;
	}

	public void setTotalFormatted(String totalFormatted) {
		this.totalFormatted = totalFormatted;

		_totalFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTotalFormatted(
		UnsafeSupplier<String, Exception> totalFormattedUnsafeSupplier) {

		_totalFormattedSupplier = () -> {
			try {
				return totalFormattedUnsafeSupplier.get();
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
	protected String totalFormatted;

	@JsonIgnore
	private Supplier<String> _totalFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "113")
	@Valid
	public BigDecimal getTotalWithTaxAmount() {
		if (_totalWithTaxAmountSupplier != null) {
			totalWithTaxAmount = _totalWithTaxAmountSupplier.get();

			_totalWithTaxAmountSupplier = null;
		}

		return totalWithTaxAmount;
	}

	public void setTotalWithTaxAmount(BigDecimal totalWithTaxAmount) {
		this.totalWithTaxAmount = totalWithTaxAmount;

		_totalWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setTotalWithTaxAmount(
		UnsafeSupplier<BigDecimal, Exception>
			totalWithTaxAmountUnsafeSupplier) {

		_totalWithTaxAmountSupplier = () -> {
			try {
				return totalWithTaxAmountUnsafeSupplier.get();
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
	protected BigDecimal totalWithTaxAmount;

	@JsonIgnore
	private Supplier<BigDecimal> _totalWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalWithTaxAmountFormatted() {
		if (_totalWithTaxAmountFormattedSupplier != null) {
			totalWithTaxAmountFormatted =
				_totalWithTaxAmountFormattedSupplier.get();

			_totalWithTaxAmountFormattedSupplier = null;
		}

		return totalWithTaxAmountFormatted;
	}

	public void setTotalWithTaxAmountFormatted(
		String totalWithTaxAmountFormatted) {

		this.totalWithTaxAmountFormatted = totalWithTaxAmountFormatted;

		_totalWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTotalWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			totalWithTaxAmountFormattedUnsafeSupplier) {

		_totalWithTaxAmountFormattedSupplier = () -> {
			try {
				return totalWithTaxAmountFormattedUnsafeSupplier.get();
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
	protected String totalWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _totalWithTaxAmountFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "113")
	public Double getTotalWithTaxAmountValue() {
		if (_totalWithTaxAmountValueSupplier != null) {
			totalWithTaxAmountValue = _totalWithTaxAmountValueSupplier.get();

			_totalWithTaxAmountValueSupplier = null;
		}

		return totalWithTaxAmountValue;
	}

	public void setTotalWithTaxAmountValue(Double totalWithTaxAmountValue) {
		this.totalWithTaxAmountValue = totalWithTaxAmountValue;

		_totalWithTaxAmountValueSupplier = null;
	}

	@JsonIgnore
	public void setTotalWithTaxAmountValue(
		UnsafeSupplier<Double, Exception>
			totalWithTaxAmountValueUnsafeSupplier) {

		_totalWithTaxAmountValueSupplier = () -> {
			try {
				return totalWithTaxAmountValueUnsafeSupplier.get();
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
	protected Double totalWithTaxAmountValue;

	@JsonIgnore
	private Supplier<Double> _totalWithTaxAmountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTransactionId() {
		if (_transactionIdSupplier != null) {
			transactionId = _transactionIdSupplier.get();

			_transactionIdSupplier = null;
		}

		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;

		_transactionIdSupplier = null;
	}

	@JsonIgnore
	public void setTransactionId(
		UnsafeSupplier<String, Exception> transactionIdUnsafeSupplier) {

		_transactionIdSupplier = () -> {
			try {
				return transactionIdUnsafeSupplier.get();
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
	protected String transactionId;

	@JsonIgnore
	private Supplier<String> _transactionIdSupplier;

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

		if (!(object instanceof Order)) {
			return false;
		}

		Order order = (Order)object;

		return Objects.equals(toString(), order.toString());
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

		Account account = getAccount();

		if (account != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(String.valueOf(account));
		}

		String accountExternalReferenceCode = getAccountExternalReferenceCode();

		if (accountExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountExternalReferenceCode));

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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String advanceStatus = getAdvanceStatus();

		if (advanceStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"advanceStatus\": ");

			sb.append("\"");

			sb.append(_escape(advanceStatus));

			sb.append("\"");
		}

		BillingAddress billingAddress = getBillingAddress();

		if (billingAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAddress\": ");

			sb.append(String.valueOf(billingAddress));
		}

		String billingAddressExternalReferenceCode =
			getBillingAddressExternalReferenceCode();

		if (billingAddressExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(billingAddressExternalReferenceCode));

			sb.append("\"");
		}

		Long billingAddressId = getBillingAddressId();

		if (billingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAddressId\": ");

			sb.append(billingAddressId);
		}

		Channel channel = getChannel();

		if (channel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(channel));
		}

		String channelExternalReferenceCode = getChannelExternalReferenceCode();

		if (channelExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(channelExternalReferenceCode));

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

		String creatorEmailAddress = getCreatorEmailAddress();

		if (creatorEmailAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorEmailAddress\": ");

			sb.append("\"");

			sb.append(_escape(creatorEmailAddress));

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

		Map<String, ?> customFields = getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(customFields));
		}

		String deliveryTermDescription = getDeliveryTermDescription();

		if (deliveryTermDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermDescription\": ");

			sb.append("\"");

			sb.append(_escape(deliveryTermDescription));

			sb.append("\"");
		}

		String deliveryTermExternalReferenceCode =
			getDeliveryTermExternalReferenceCode();

		if (deliveryTermExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(deliveryTermExternalReferenceCode));

			sb.append("\"");
		}

		Long deliveryTermId = getDeliveryTermId();

		if (deliveryTermId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermId\": ");

			sb.append(deliveryTermId);
		}

		String deliveryTermName = getDeliveryTermName();

		if (deliveryTermName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermName\": ");

			sb.append("\"");

			sb.append(_escape(deliveryTermName));

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

		Date orderDate = getOrderDate();

		if (orderDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(orderDate));

			sb.append("\"");
		}

		OrderItem[] orderItems = getOrderItems();

		if (orderItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderItems\": ");

			sb.append("[");

			for (int i = 0; i < orderItems.length; i++) {
				sb.append(String.valueOf(orderItems[i]));

				if ((i + 1) < orderItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer orderStatus = getOrderStatus();

		if (orderStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatus\": ");

			sb.append(orderStatus);
		}

		Status orderStatusInfo = getOrderStatusInfo();

		if (orderStatusInfo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatusInfo\": ");

			sb.append(String.valueOf(orderStatusInfo));
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

		String paymentTermDescription = getPaymentTermDescription();

		if (paymentTermDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermDescription\": ");

			sb.append("\"");

			sb.append(_escape(paymentTermDescription));

			sb.append("\"");
		}

		String paymentTermExternalReferenceCode =
			getPaymentTermExternalReferenceCode();

		if (paymentTermExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(paymentTermExternalReferenceCode));

			sb.append("\"");
		}

		Long paymentTermId = getPaymentTermId();

		if (paymentTermId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermId\": ");

			sb.append(paymentTermId);
		}

		String paymentTermName = getPaymentTermName();

		if (paymentTermName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermName\": ");

			sb.append("\"");

			sb.append(_escape(paymentTermName));

			sb.append("\"");
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

		Boolean shippable = getShippable();

		if (shippable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippable\": ");

			sb.append(shippable);
		}

		ShippingAddress shippingAddress = getShippingAddress();

		if (shippingAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(shippingAddress));
		}

		String shippingAddressExternalReferenceCode =
			getShippingAddressExternalReferenceCode();

		if (shippingAddressExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(shippingAddressExternalReferenceCode));

			sb.append("\"");
		}

		Long shippingAddressId = getShippingAddressId();

		if (shippingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(shippingAddressId);
		}

		BigDecimal shippingAmount = getShippingAmount();

		if (shippingAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAmount\": ");

			sb.append(shippingAmount);
		}

		String shippingAmountFormatted = getShippingAmountFormatted();

		if (shippingAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingAmountFormatted));

			sb.append("\"");
		}

		Double shippingAmountValue = getShippingAmountValue();

		if (shippingAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAmountValue\": ");

			sb.append(shippingAmountValue);
		}

		BigDecimal shippingDiscountAmount = getShippingDiscountAmount();

		if (shippingDiscountAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountAmount\": ");

			sb.append(shippingDiscountAmount);
		}

		String shippingDiscountAmountFormatted =
			getShippingDiscountAmountFormatted();

		if (shippingDiscountAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingDiscountAmountFormatted));

			sb.append("\"");
		}

		Double shippingDiscountAmountValue = getShippingDiscountAmountValue();

		if (shippingDiscountAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountAmountValue\": ");

			sb.append(shippingDiscountAmountValue);
		}

		BigDecimal shippingDiscountPercentageLevel1 =
			getShippingDiscountPercentageLevel1();

		if (shippingDiscountPercentageLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel1\": ");

			sb.append(shippingDiscountPercentageLevel1);
		}

		BigDecimal shippingDiscountPercentageLevel1WithTaxAmount =
			getShippingDiscountPercentageLevel1WithTaxAmount();

		if (shippingDiscountPercentageLevel1WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel1WithTaxAmount\": ");

			sb.append(shippingDiscountPercentageLevel1WithTaxAmount);
		}

		BigDecimal shippingDiscountPercentageLevel2 =
			getShippingDiscountPercentageLevel2();

		if (shippingDiscountPercentageLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel2\": ");

			sb.append(shippingDiscountPercentageLevel2);
		}

		BigDecimal shippingDiscountPercentageLevel2WithTaxAmount =
			getShippingDiscountPercentageLevel2WithTaxAmount();

		if (shippingDiscountPercentageLevel2WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel2WithTaxAmount\": ");

			sb.append(shippingDiscountPercentageLevel2WithTaxAmount);
		}

		BigDecimal shippingDiscountPercentageLevel3 =
			getShippingDiscountPercentageLevel3();

		if (shippingDiscountPercentageLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel3\": ");

			sb.append(shippingDiscountPercentageLevel3);
		}

		BigDecimal shippingDiscountPercentageLevel3WithTaxAmount =
			getShippingDiscountPercentageLevel3WithTaxAmount();

		if (shippingDiscountPercentageLevel3WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel3WithTaxAmount\": ");

			sb.append(shippingDiscountPercentageLevel3WithTaxAmount);
		}

		BigDecimal shippingDiscountPercentageLevel4 =
			getShippingDiscountPercentageLevel4();

		if (shippingDiscountPercentageLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel4\": ");

			sb.append(shippingDiscountPercentageLevel4);
		}

		BigDecimal shippingDiscountPercentageLevel4WithTaxAmount =
			getShippingDiscountPercentageLevel4WithTaxAmount();

		if (shippingDiscountPercentageLevel4WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel4WithTaxAmount\": ");

			sb.append(shippingDiscountPercentageLevel4WithTaxAmount);
		}

		BigDecimal shippingDiscountWithTaxAmount =
			getShippingDiscountWithTaxAmount();

		if (shippingDiscountWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountWithTaxAmount\": ");

			sb.append(shippingDiscountWithTaxAmount);
		}

		String shippingDiscountWithTaxAmountFormatted =
			getShippingDiscountWithTaxAmountFormatted();

		if (shippingDiscountWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingDiscountWithTaxAmountFormatted));

			sb.append("\"");
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

		BigDecimal shippingWithTaxAmount = getShippingWithTaxAmount();

		if (shippingWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingWithTaxAmount\": ");

			sb.append(shippingWithTaxAmount);
		}

		String shippingWithTaxAmountFormatted =
			getShippingWithTaxAmountFormatted();

		if (shippingWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingWithTaxAmountFormatted));

			sb.append("\"");
		}

		Double shippingWithTaxAmountValue = getShippingWithTaxAmountValue();

		if (shippingWithTaxAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingWithTaxAmountValue\": ");

			sb.append(shippingWithTaxAmountValue);
		}

		BigDecimal subtotal = getSubtotal();

		if (subtotal != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotal\": ");

			sb.append(subtotal);
		}

		Double subtotalAmount = getSubtotalAmount();

		if (subtotalAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalAmount\": ");

			sb.append(subtotalAmount);
		}

		BigDecimal subtotalDiscountAmount = getSubtotalDiscountAmount();

		if (subtotalDiscountAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountAmount\": ");

			sb.append(subtotalDiscountAmount);
		}

		String subtotalDiscountAmountFormatted =
			getSubtotalDiscountAmountFormatted();

		if (subtotalDiscountAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(subtotalDiscountAmountFormatted));

			sb.append("\"");
		}

		BigDecimal subtotalDiscountPercentageLevel1 =
			getSubtotalDiscountPercentageLevel1();

		if (subtotalDiscountPercentageLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel1\": ");

			sb.append(subtotalDiscountPercentageLevel1);
		}

		BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount =
			getSubtotalDiscountPercentageLevel1WithTaxAmount();

		if (subtotalDiscountPercentageLevel1WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel1WithTaxAmount\": ");

			sb.append(subtotalDiscountPercentageLevel1WithTaxAmount);
		}

		BigDecimal subtotalDiscountPercentageLevel2 =
			getSubtotalDiscountPercentageLevel2();

		if (subtotalDiscountPercentageLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel2\": ");

			sb.append(subtotalDiscountPercentageLevel2);
		}

		BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount =
			getSubtotalDiscountPercentageLevel2WithTaxAmount();

		if (subtotalDiscountPercentageLevel2WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel2WithTaxAmount\": ");

			sb.append(subtotalDiscountPercentageLevel2WithTaxAmount);
		}

		BigDecimal subtotalDiscountPercentageLevel3 =
			getSubtotalDiscountPercentageLevel3();

		if (subtotalDiscountPercentageLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel3\": ");

			sb.append(subtotalDiscountPercentageLevel3);
		}

		BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount =
			getSubtotalDiscountPercentageLevel3WithTaxAmount();

		if (subtotalDiscountPercentageLevel3WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel3WithTaxAmount\": ");

			sb.append(subtotalDiscountPercentageLevel3WithTaxAmount);
		}

		BigDecimal subtotalDiscountPercentageLevel4 =
			getSubtotalDiscountPercentageLevel4();

		if (subtotalDiscountPercentageLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel4\": ");

			sb.append(subtotalDiscountPercentageLevel4);
		}

		BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount =
			getSubtotalDiscountPercentageLevel4WithTaxAmount();

		if (subtotalDiscountPercentageLevel4WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel4WithTaxAmount\": ");

			sb.append(subtotalDiscountPercentageLevel4WithTaxAmount);
		}

		BigDecimal subtotalDiscountWithTaxAmount =
			getSubtotalDiscountWithTaxAmount();

		if (subtotalDiscountWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountWithTaxAmount\": ");

			sb.append(subtotalDiscountWithTaxAmount);
		}

		String subtotalDiscountWithTaxAmountFormatted =
			getSubtotalDiscountWithTaxAmountFormatted();

		if (subtotalDiscountWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(subtotalDiscountWithTaxAmountFormatted));

			sb.append("\"");
		}

		String subtotalFormatted = getSubtotalFormatted();

		if (subtotalFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(subtotalFormatted));

			sb.append("\"");
		}

		BigDecimal subtotalWithTaxAmount = getSubtotalWithTaxAmount();

		if (subtotalWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalWithTaxAmount\": ");

			sb.append(subtotalWithTaxAmount);
		}

		String subtotalWithTaxAmountFormatted =
			getSubtotalWithTaxAmountFormatted();

		if (subtotalWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(subtotalWithTaxAmountFormatted));

			sb.append("\"");
		}

		Double subtotalWithTaxAmountValue = getSubtotalWithTaxAmountValue();

		if (subtotalWithTaxAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalWithTaxAmountValue\": ");

			sb.append(subtotalWithTaxAmountValue);
		}

		BigDecimal taxAmount = getTaxAmount();

		if (taxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxAmount\": ");

			sb.append(taxAmount);
		}

		String taxAmountFormatted = getTaxAmountFormatted();

		if (taxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(taxAmountFormatted));

			sb.append("\"");
		}

		Double taxAmountValue = getTaxAmountValue();

		if (taxAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxAmountValue\": ");

			sb.append(taxAmountValue);
		}

		BigDecimal total = getTotal();

		if (total != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(total);
		}

		Double totalAmount = getTotalAmount();

		if (totalAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalAmount\": ");

			sb.append(totalAmount);
		}

		BigDecimal totalDiscountAmount = getTotalDiscountAmount();

		if (totalDiscountAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountAmount\": ");

			sb.append(totalDiscountAmount);
		}

		String totalDiscountAmountFormatted = getTotalDiscountAmountFormatted();

		if (totalDiscountAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(totalDiscountAmountFormatted));

			sb.append("\"");
		}

		Double totalDiscountAmountValue = getTotalDiscountAmountValue();

		if (totalDiscountAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountAmountValue\": ");

			sb.append(totalDiscountAmountValue);
		}

		BigDecimal totalDiscountPercentageLevel1 =
			getTotalDiscountPercentageLevel1();

		if (totalDiscountPercentageLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel1\": ");

			sb.append(totalDiscountPercentageLevel1);
		}

		BigDecimal totalDiscountPercentageLevel1WithTaxAmount =
			getTotalDiscountPercentageLevel1WithTaxAmount();

		if (totalDiscountPercentageLevel1WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel1WithTaxAmount\": ");

			sb.append(totalDiscountPercentageLevel1WithTaxAmount);
		}

		BigDecimal totalDiscountPercentageLevel2 =
			getTotalDiscountPercentageLevel2();

		if (totalDiscountPercentageLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel2\": ");

			sb.append(totalDiscountPercentageLevel2);
		}

		BigDecimal totalDiscountPercentageLevel2WithTaxAmount =
			getTotalDiscountPercentageLevel2WithTaxAmount();

		if (totalDiscountPercentageLevel2WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel2WithTaxAmount\": ");

			sb.append(totalDiscountPercentageLevel2WithTaxAmount);
		}

		BigDecimal totalDiscountPercentageLevel3 =
			getTotalDiscountPercentageLevel3();

		if (totalDiscountPercentageLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel3\": ");

			sb.append(totalDiscountPercentageLevel3);
		}

		BigDecimal totalDiscountPercentageLevel3WithTaxAmount =
			getTotalDiscountPercentageLevel3WithTaxAmount();

		if (totalDiscountPercentageLevel3WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel3WithTaxAmount\": ");

			sb.append(totalDiscountPercentageLevel3WithTaxAmount);
		}

		BigDecimal totalDiscountPercentageLevel4 =
			getTotalDiscountPercentageLevel4();

		if (totalDiscountPercentageLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel4\": ");

			sb.append(totalDiscountPercentageLevel4);
		}

		BigDecimal totalDiscountPercentageLevel4WithTaxAmount =
			getTotalDiscountPercentageLevel4WithTaxAmount();

		if (totalDiscountPercentageLevel4WithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel4WithTaxAmount\": ");

			sb.append(totalDiscountPercentageLevel4WithTaxAmount);
		}

		BigDecimal totalDiscountWithTaxAmount = getTotalDiscountWithTaxAmount();

		if (totalDiscountWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountWithTaxAmount\": ");

			sb.append(totalDiscountWithTaxAmount);
		}

		String totalDiscountWithTaxAmountFormatted =
			getTotalDiscountWithTaxAmountFormatted();

		if (totalDiscountWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(totalDiscountWithTaxAmountFormatted));

			sb.append("\"");
		}

		Double totalDiscountWithTaxAmountValue =
			getTotalDiscountWithTaxAmountValue();

		if (totalDiscountWithTaxAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountWithTaxAmountValue\": ");

			sb.append(totalDiscountWithTaxAmountValue);
		}

		String totalFormatted = getTotalFormatted();

		if (totalFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(totalFormatted));

			sb.append("\"");
		}

		BigDecimal totalWithTaxAmount = getTotalWithTaxAmount();

		if (totalWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalWithTaxAmount\": ");

			sb.append(totalWithTaxAmount);
		}

		String totalWithTaxAmountFormatted = getTotalWithTaxAmountFormatted();

		if (totalWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(totalWithTaxAmountFormatted));

			sb.append("\"");
		}

		Double totalWithTaxAmountValue = getTotalWithTaxAmountValue();

		if (totalWithTaxAmountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalWithTaxAmountValue\": ");

			sb.append(totalWithTaxAmountValue);
		}

		String transactionId = getTransactionId();

		if (transactionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transactionId\": ");

			sb.append("\"");

			sb.append(_escape(transactionId));

			sb.append("\"");
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
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.Order",
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