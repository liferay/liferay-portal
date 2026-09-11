/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
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
@GraphQLName(
	description = "Fulfillment record that ships a single parcel for a source order. Carries the carrier, tracking identifiers, expected and shipping dates, the ship-to address, the picked items, and the workflow status that advances from processing through ready-to-be-shipped and shipped to delivered.",
	value = "Shipment"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Fulfillment record that ships a single parcel for a source order. Carries the carrier, tracking identifiers, expected and shipping dates, the ship-to address, the picked items, and the workflow status that advances from processing through ready-to-be-shipped and shipped to delivered."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Shipment")
public class Shipment implements Serializable {

	public static Shipment toDTO(String json) {
		return ObjectMapperUtil.readValue(Shipment.class, json);
	}

	public static Shipment unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Shipment.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the customer account that owns the source order (FK identifier). Copied from the order at creation time and read-only thereafter.",
		example = "30130"
	)
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

	@GraphQLField(
		description = "Reference to the customer account that owns the source order (FK identifier). Copied from the order at creation time and read-only thereafter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
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

	@GraphQLField(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the shipping carrier handling the parcel. Free-form string supplied by the fulfillment operator; updatable while the shipment is still in flight.",
		example = "FedEx"
	)
	public String getCarrier() {
		if (_carrierSupplier != null) {
			carrier = _carrierSupplier.get();

			_carrierSupplier = null;
		}

		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;

		_carrierSupplier = null;
	}

	@JsonIgnore
	public void setCarrier(
		UnsafeSupplier<String, Exception> carrierUnsafeSupplier) {

		_carrierSupplier = () -> {
			try {
				return carrierUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Name of the shipping carrier handling the parcel. Free-form string supplied by the fulfillment operator; updatable while the shipment is still in flight."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String carrier;

	@JsonIgnore
	private Supplier<String> _carrierSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Creation timestamp in ISO 8601. Read-only; set when the shipment is first persisted.",
		example = "2017-07-21"
	)
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

	@GraphQLField(
		description = "Creation timestamp in ISO 8601. Read-only; set when the shipment is first persisted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom field values attached to the shipment by the platform's expando mechanism. Each entry is a name/value pair declared on the shipment's custom-fields configuration; the field set is installation-specific."
	)
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

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

	@GraphQLField(
		description = "Custom field values attached to the shipment by the platform's expando mechanism. Each entry is a name/value pair declared on the shipment's custom-fields configuration; the field set is installation-specific."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Estimated delivery date and time in ISO 8601. Supplied by the fulfillment operator; updatable until the shipment is delivered.",
		example = "2017-07-21"
	)
	public Date getExpectedDate() {
		if (_expectedDateSupplier != null) {
			expectedDate = _expectedDateSupplier.get();

			_expectedDateSupplier = null;
		}

		return expectedDate;
	}

	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;

		_expectedDateSupplier = null;
	}

	@JsonIgnore
	public void setExpectedDate(
		UnsafeSupplier<Date, Exception> expectedDateUnsafeSupplier) {

		_expectedDateSupplier = () -> {
			try {
				return expectedDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Estimated delivery date and time in ISO 8601. Supplied by the fulfillment operator; updatable until the shipment is delivered."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date expectedDate;

	@JsonIgnore
	private Supplier<Date> _expectedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per shipment within the company. When set, the resource is also addressable through the by-external-reference-code paths.",
		example = "AB-34098-789-N"
	)
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

	@GraphQLField(
		description = "Idempotency key for create and update; must be unique per shipment within the company. When set, the resource is also addressable through the by-external-reference-code paths."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Server-assigned identifier of the shipment. Stable across the shipment's lifetime. Read-only.",
		example = "30130"
	)
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

	@GraphQLField(
		description = "Server-assigned identifier of the shipment. Stable across the shipment's lifetime. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Timestamp of the most recent update in ISO 8601. Read-only; refreshed on every persisted change.",
		example = "2017-07-21"
	)
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

	@GraphQLField(
		description = "Timestamp of the most recent update in ISO 8601. Read-only; refreshed on every persisted change."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date modifiedDate;

	@JsonIgnore
	private Supplier<Date> _modifiedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the source order. Accepted on create as an alternative to orderId; the order is resolved against the current company scope. Write-only -- the response carries the resolved orderId and accountId instead.",
		example = "AB-34098-789-N"
	)
	public String getOrderExternalReferenceCode() {
		if (_orderExternalReferenceCodeSupplier != null) {
			orderExternalReferenceCode =
				_orderExternalReferenceCodeSupplier.get();

			_orderExternalReferenceCodeSupplier = null;
		}

		return orderExternalReferenceCode;
	}

	public void setOrderExternalReferenceCode(
		String orderExternalReferenceCode) {

		this.orderExternalReferenceCode = orderExternalReferenceCode;

		_orderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderExternalReferenceCodeUnsafeSupplier) {

		_orderExternalReferenceCodeSupplier = () -> {
			try {
				return orderExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "External reference code of the source order. Accepted on create as an alternative to orderId; the order is resolved against the current company scope. Write-only -- the response carries the resolved orderId and accountId instead."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String orderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the source order this shipment fulfills (FK identifier). Required on create when orderExternalReferenceCode is omitted; seeds the shipment's group, customer account, ship-to address, shipping method, and shipping-option label.",
		example = "30130"
	)
	public Long getOrderId() {
		if (_orderIdSupplier != null) {
			orderId = _orderIdSupplier.get();

			_orderIdSupplier = null;
		}

		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;

		_orderIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderId(
		UnsafeSupplier<Long, Exception> orderIdUnsafeSupplier) {

		_orderIdSupplier = () -> {
			try {
				return orderIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Reference to the source order this shipment fulfills (FK identifier). Required on create when orderExternalReferenceCode is omitted; seeds the shipment's group, customer account, ship-to address, shipping method, and shipping-option label."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long orderId;

	@JsonIgnore
	private Supplier<Long> _orderIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Nested picked-line rows to write together with the shipment. When supplied on update, every existing line on the parent is removed first and the array is replayed; supply an empty array to clear all lines, or omit the field to leave the lines untouched."
	)
	@Valid
	public ShipmentItem[] getShipmentItems() {
		if (_shipmentItemsSupplier != null) {
			shipmentItems = _shipmentItemsSupplier.get();

			_shipmentItemsSupplier = null;
		}

		return shipmentItems;
	}

	public void setShipmentItems(ShipmentItem[] shipmentItems) {
		this.shipmentItems = shipmentItems;

		_shipmentItemsSupplier = null;
	}

	@JsonIgnore
	public void setShipmentItems(
		UnsafeSupplier<ShipmentItem[], Exception> shipmentItemsUnsafeSupplier) {

		_shipmentItemsSupplier = () -> {
			try {
				return shipmentItemsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Nested picked-line rows to write together with the shipment. When supplied on update, every existing line on the parent is removed first and the array is replayed; supply an empty array to clear all lines, or omit the field to leave the lines untouched."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ShipmentItem[] shipmentItems;

	@JsonIgnore
	private Supplier<ShipmentItem[]> _shipmentItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Nested ship-to address applied to this shipment. When supplied, the existing address row is updated in place (or a new one is created when none is linked yet) and the shipment is rebound to it. Omit to leave the address inherited from the source order."
	)
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

	@GraphQLField(
		description = "Nested ship-to address applied to this shipment. When supplied, the existing address row is updated in place (or a new one is created when none is linked yet) and the shipment is rebound to it. Omit to leave the address inherited from the source order."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ShippingAddress shippingAddress;

	@JsonIgnore
	private Supplier<ShippingAddress> _shippingAddressSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the postal address the parcel ships to (FK identifier). Copied from the source order at creation time; can be retargeted by supplying a nested shippingAddress or by calling the shipping-address endpoint.",
		example = "31130"
	)
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

	@GraphQLField(
		description = "Reference to the postal address the parcel ships to (FK identifier). Copied from the source order at creation time; can be retargeted by supplying a nested shippingAddress or by calling the shipping-address endpoint."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long shippingAddressId;

	@JsonIgnore
	private Supplier<Long> _shippingAddressIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date and time the parcel was handed over to the carrier, in ISO 8601. Typically set when the shipment transitions to shipped.",
		example = "2017-07-21"
	)
	public Date getShippingDate() {
		if (_shippingDateSupplier != null) {
			shippingDate = _shippingDateSupplier.get();

			_shippingDateSupplier = null;
		}

		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;

		_shippingDateSupplier = null;
	}

	@JsonIgnore
	public void setShippingDate(
		UnsafeSupplier<Date, Exception> shippingDateUnsafeSupplier) {

		_shippingDateSupplier = () -> {
			try {
				return shippingDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Date and time the parcel was handed over to the carrier, in ISO 8601. Typically set when the shipment transitions to shipped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date shippingDate;

	@JsonIgnore
	private Supplier<Date> _shippingDateSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the shipping method used by the carrier (FK identifier). Copied from the source order at creation time; updatable while the shipment is still processing or ready to be shipped.",
		example = "30130"
	)
	public Long getShippingMethodId() {
		if (_shippingMethodIdSupplier != null) {
			shippingMethodId = _shippingMethodIdSupplier.get();

			_shippingMethodIdSupplier = null;
		}

		return shippingMethodId;
	}

	public void setShippingMethodId(Long shippingMethodId) {
		this.shippingMethodId = shippingMethodId;

		_shippingMethodIdSupplier = null;
	}

	@JsonIgnore
	public void setShippingMethodId(
		UnsafeSupplier<Long, Exception> shippingMethodIdUnsafeSupplier) {

		_shippingMethodIdSupplier = () -> {
			try {
				return shippingMethodIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Reference to the shipping method used by the carrier (FK identifier). Copied from the source order at creation time; updatable while the shipment is still processing or ready to be shipped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long shippingMethodId;

	@JsonIgnore
	private Supplier<Long> _shippingMethodIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Label of the shipping option selected on the source order. Copied from the order at creation time. Read-only.",
		example = "Standard Delivery"
	)
	public String getShippingOptionName() {
		if (_shippingOptionNameSupplier != null) {
			shippingOptionName = _shippingOptionNameSupplier.get();

			_shippingOptionNameSupplier = null;
		}

		return shippingOptionName;
	}

	public void setShippingOptionName(String shippingOptionName) {
		this.shippingOptionName = shippingOptionName;

		_shippingOptionNameSupplier = null;
	}

	@JsonIgnore
	public void setShippingOptionName(
		UnsafeSupplier<String, Exception> shippingOptionNameUnsafeSupplier) {

		_shippingOptionNameSupplier = () -> {
			try {
				return shippingOptionNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Label of the shipping option selected on the source order. Copied from the order at creation time. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String shippingOptionName;

	@JsonIgnore
	private Supplier<String> _shippingOptionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Workflow envelope with the integer code and the localized label of the shipment. Transitions are driven by the dedicated status-* endpoints and cannot run backwards; reaching shipped or delivered emits an asynchronous shipment-status message. Read-only on this schema."
	)
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

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

	@GraphQLField(
		description = "Workflow envelope with the integer code and the localized label of the shipment. Transitions are driven by the dedicated status-* endpoints and cannot run backwards; reaching shipped or delivered emits an asynchronous shipment-status message. Read-only on this schema."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Carrier-assigned tracking number for the parcel. Free-form string supplied by the fulfillment operator.",
		example = "123AD-asd"
	)
	public String getTrackingNumber() {
		if (_trackingNumberSupplier != null) {
			trackingNumber = _trackingNumberSupplier.get();

			_trackingNumberSupplier = null;
		}

		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;

		_trackingNumberSupplier = null;
	}

	@JsonIgnore
	public void setTrackingNumber(
		UnsafeSupplier<String, Exception> trackingNumberUnsafeSupplier) {

		_trackingNumberSupplier = () -> {
			try {
				return trackingNumberUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Carrier-assigned tracking number for the parcel. Free-form string supplied by the fulfillment operator."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String trackingNumber;

	@JsonIgnore
	private Supplier<String> _trackingNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Carrier tracking page for the parcel. Free-form string; not validated as a URL. Defaults to the tracking URL configured on the selected shipping method when the field is omitted.",
		example = "https://example.com/track?number=123AD-asd"
	)
	public String getTrackingURL() {
		if (_trackingURLSupplier != null) {
			trackingURL = _trackingURLSupplier.get();

			_trackingURLSupplier = null;
		}

		return trackingURL;
	}

	public void setTrackingURL(String trackingURL) {
		this.trackingURL = trackingURL;

		_trackingURLSupplier = null;
	}

	@JsonIgnore
	public void setTrackingURL(
		UnsafeSupplier<String, Exception> trackingURLUnsafeSupplier) {

		_trackingURLSupplier = () -> {
			try {
				return trackingURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Carrier tracking page for the parcel. Free-form string; not validated as a URL. Defaults to the tracking URL configured on the selected shipping method when the field is omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String trackingURL;

	@JsonIgnore
	private Supplier<String> _trackingURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Full name of the user who created the shipment. Read-only.",
		example = "John Doe"
	)
	public String getUserName() {
		if (_userNameSupplier != null) {
			userName = _userNameSupplier.get();

			_userNameSupplier = null;
		}

		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;

		_userNameSupplier = null;
	}

	@JsonIgnore
	public void setUserName(
		UnsafeSupplier<String, Exception> userNameUnsafeSupplier) {

		_userNameSupplier = () -> {
			try {
				return userNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Full name of the user who created the shipment. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String userName;

	@JsonIgnore
	private Supplier<String> _userNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Shipment)) {
			return false;
		}

		Shipment shipment = (Shipment)object;

		return Objects.equals(toString(), shipment.toString());
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

		String carrier = getCarrier();

		if (carrier != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"carrier\": ");

			sb.append("\"");

			sb.append(_escape(carrier));

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

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date expectedDate = getExpectedDate();

		if (expectedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expectedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(expectedDate));

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

		String orderExternalReferenceCode = getOrderExternalReferenceCode();

		if (orderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderExternalReferenceCode));

			sb.append("\"");
		}

		Long orderId = getOrderId();

		if (orderId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderId);
		}

		ShipmentItem[] shipmentItems = getShipmentItems();

		if (shipmentItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shipmentItems\": ");

			sb.append("[");

			for (int i = 0; i < shipmentItems.length; i++) {
				sb.append(String.valueOf(shipmentItems[i]));

				if ((i + 1) < shipmentItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ShippingAddress shippingAddress = getShippingAddress();

		if (shippingAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(shippingAddress));
		}

		Long shippingAddressId = getShippingAddressId();

		if (shippingAddressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(shippingAddressId);
		}

		Date shippingDate = getShippingDate();

		if (shippingDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(shippingDate));

			sb.append("\"");
		}

		Long shippingMethodId = getShippingMethodId();

		if (shippingMethodId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingMethodId\": ");

			sb.append(shippingMethodId);
		}

		String shippingOptionName = getShippingOptionName();

		if (shippingOptionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingOptionName\": ");

			sb.append("\"");

			sb.append(_escape(shippingOptionName));

			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(status));
		}

		String trackingNumber = getTrackingNumber();

		if (trackingNumber != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trackingNumber\": ");

			sb.append("\"");

			sb.append(_escape(trackingNumber));

			sb.append("\"");
		}

		String trackingURL = getTrackingURL();

		if (trackingURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trackingURL\": ");

			sb.append("\"");

			sb.append(_escape(trackingURL));

			sb.append("\"");
		}

		String userName = getUserName();

		if (userName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(userName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.shipment.dto.v1_0.Shipment",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-2094861992