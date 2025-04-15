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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

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

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("ShipmentItem")
@io.swagger.v3.oas.annotations.media.Schema(requiredProperties = {"quantity"})
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ShipmentItem")
public class ShipmentItem implements Serializable {

	public static ShipmentItem toDTO(String json) {
		return ObjectMapperUtil.readValue(ShipmentItem.class, json);
	}

	public static ShipmentItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ShipmentItem.class, json);
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

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOrderItemExternalReferenceCode() {
		if (_orderItemExternalReferenceCodeSupplier != null) {
			orderItemExternalReferenceCode =
				_orderItemExternalReferenceCodeSupplier.get();

			_orderItemExternalReferenceCodeSupplier = null;
		}

		return orderItemExternalReferenceCode;
	}

	public void setOrderItemExternalReferenceCode(
		String orderItemExternalReferenceCode) {

		this.orderItemExternalReferenceCode = orderItemExternalReferenceCode;

		_orderItemExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderItemExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderItemExternalReferenceCodeUnsafeSupplier) {

		_orderItemExternalReferenceCodeSupplier = () -> {
			try {
				return orderItemExternalReferenceCodeUnsafeSupplier.get();
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
	protected String orderItemExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderItemExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getOrderItemId() {
		if (_orderItemIdSupplier != null) {
			orderItemId = _orderItemIdSupplier.get();

			_orderItemIdSupplier = null;
		}

		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;

		_orderItemIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderItemId(
		UnsafeSupplier<Long, Exception> orderItemIdUnsafeSupplier) {

		_orderItemIdSupplier = () -> {
			try {
				return orderItemIdUnsafeSupplier.get();
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
	protected Long orderItemId;

	@JsonIgnore
	private Supplier<Long> _orderItemIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getQuantity() {
		if (_quantitySupplier != null) {
			quantity = _quantitySupplier.get();

			_quantitySupplier = null;
		}

		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;

		_quantitySupplier = null;
	}

	@JsonIgnore
	public void setQuantity(
		UnsafeSupplier<BigDecimal, Exception> quantityUnsafeSupplier) {

		_quantitySupplier = () -> {
			try {
				return quantityUnsafeSupplier.get();
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
	protected BigDecimal quantity;

	@JsonIgnore
	private Supplier<BigDecimal> _quantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShipmentExternalReferenceCode() {
		if (_shipmentExternalReferenceCodeSupplier != null) {
			shipmentExternalReferenceCode =
				_shipmentExternalReferenceCodeSupplier.get();

			_shipmentExternalReferenceCodeSupplier = null;
		}

		return shipmentExternalReferenceCode;
	}

	public void setShipmentExternalReferenceCode(
		String shipmentExternalReferenceCode) {

		this.shipmentExternalReferenceCode = shipmentExternalReferenceCode;

		_shipmentExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setShipmentExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			shipmentExternalReferenceCodeUnsafeSupplier) {

		_shipmentExternalReferenceCodeSupplier = () -> {
			try {
				return shipmentExternalReferenceCodeUnsafeSupplier.get();
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
	protected String shipmentExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _shipmentExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getShipmentId() {
		if (_shipmentIdSupplier != null) {
			shipmentId = _shipmentIdSupplier.get();

			_shipmentIdSupplier = null;
		}

		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;

		_shipmentIdSupplier = null;
	}

	@JsonIgnore
	public void setShipmentId(
		UnsafeSupplier<Long, Exception> shipmentIdUnsafeSupplier) {

		_shipmentIdSupplier = () -> {
			try {
				return shipmentIdUnsafeSupplier.get();
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
	protected Long shipmentId;

	@JsonIgnore
	private Supplier<Long> _shipmentIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "s")
	public String getUnitOfMeasureKey() {
		if (_unitOfMeasureKeySupplier != null) {
			unitOfMeasureKey = _unitOfMeasureKeySupplier.get();

			_unitOfMeasureKeySupplier = null;
		}

		return unitOfMeasureKey;
	}

	public void setUnitOfMeasureKey(String unitOfMeasureKey) {
		this.unitOfMeasureKey = unitOfMeasureKey;

		_unitOfMeasureKeySupplier = null;
	}

	@JsonIgnore
	public void setUnitOfMeasureKey(
		UnsafeSupplier<String, Exception> unitOfMeasureKeyUnsafeSupplier) {

		_unitOfMeasureKeySupplier = () -> {
			try {
				return unitOfMeasureKeyUnsafeSupplier.get();
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
	protected String unitOfMeasureKey;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "John")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String userName;

	@JsonIgnore
	private Supplier<String> _userNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getValidateInventory() {
		if (_validateInventorySupplier != null) {
			validateInventory = _validateInventorySupplier.get();

			_validateInventorySupplier = null;
		}

		return validateInventory;
	}

	public void setValidateInventory(Boolean validateInventory) {
		this.validateInventory = validateInventory;

		_validateInventorySupplier = null;
	}

	@JsonIgnore
	public void setValidateInventory(
		UnsafeSupplier<Boolean, Exception> validateInventoryUnsafeSupplier) {

		_validateInventorySupplier = () -> {
			try {
				return validateInventoryUnsafeSupplier.get();
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
	protected Boolean validateInventory;

	@JsonIgnore
	private Supplier<Boolean> _validateInventorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getWarehouseExternalReferenceCode() {
		if (_warehouseExternalReferenceCodeSupplier != null) {
			warehouseExternalReferenceCode =
				_warehouseExternalReferenceCodeSupplier.get();

			_warehouseExternalReferenceCodeSupplier = null;
		}

		return warehouseExternalReferenceCode;
	}

	public void setWarehouseExternalReferenceCode(
		String warehouseExternalReferenceCode) {

		this.warehouseExternalReferenceCode = warehouseExternalReferenceCode;

		_warehouseExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setWarehouseExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			warehouseExternalReferenceCodeUnsafeSupplier) {

		_warehouseExternalReferenceCodeSupplier = () -> {
			try {
				return warehouseExternalReferenceCodeUnsafeSupplier.get();
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
	protected String warehouseExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _warehouseExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getWarehouseId() {
		if (_warehouseIdSupplier != null) {
			warehouseId = _warehouseIdSupplier.get();

			_warehouseIdSupplier = null;
		}

		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;

		_warehouseIdSupplier = null;
	}

	@JsonIgnore
	public void setWarehouseId(
		UnsafeSupplier<Long, Exception> warehouseIdUnsafeSupplier) {

		_warehouseIdSupplier = () -> {
			try {
				return warehouseIdUnsafeSupplier.get();
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
	protected Long warehouseId;

	@JsonIgnore
	private Supplier<Long> _warehouseIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ShipmentItem)) {
			return false;
		}

		ShipmentItem shipmentItem = (ShipmentItem)object;

		return Objects.equals(toString(), shipmentItem.toString());
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

		String orderItemExternalReferenceCode =
			getOrderItemExternalReferenceCode();

		if (orderItemExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderItemExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderItemExternalReferenceCode));

			sb.append("\"");
		}

		Long orderItemId = getOrderItemId();

		if (orderItemId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderItemId\": ");

			sb.append(orderItemId);
		}

		BigDecimal quantity = getQuantity();

		if (quantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(quantity);
		}

		String shipmentExternalReferenceCode =
			getShipmentExternalReferenceCode();

		if (shipmentExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shipmentExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(shipmentExternalReferenceCode));

			sb.append("\"");
		}

		Long shipmentId = getShipmentId();

		if (shipmentId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shipmentId\": ");

			sb.append(shipmentId);
		}

		String unitOfMeasureKey = getUnitOfMeasureKey();

		if (unitOfMeasureKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureKey\": ");

			sb.append("\"");

			sb.append(_escape(unitOfMeasureKey));

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

		Boolean validateInventory = getValidateInventory();

		if (validateInventory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"validateInventory\": ");

			sb.append(validateInventory);
		}

		String warehouseExternalReferenceCode =
			getWarehouseExternalReferenceCode();

		if (warehouseExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(warehouseExternalReferenceCode));

			sb.append("\"");
		}

		Long warehouseId = getWarehouseId();

		if (warehouseId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(warehouseId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShipmentItem",
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