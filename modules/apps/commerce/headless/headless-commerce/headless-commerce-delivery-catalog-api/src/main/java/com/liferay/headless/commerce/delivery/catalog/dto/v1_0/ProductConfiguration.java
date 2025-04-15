/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.dto.v1_0;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("ProductConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductConfiguration")
public class ProductConfiguration implements Serializable {

	public static ProductConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductConfiguration.class, json);
	}

	public static ProductConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getAllowBackOrder() {
		if (_allowBackOrderSupplier != null) {
			allowBackOrder = _allowBackOrderSupplier.get();

			_allowBackOrderSupplier = null;
		}

		return allowBackOrder;
	}

	public void setAllowBackOrder(Boolean allowBackOrder) {
		this.allowBackOrder = allowBackOrder;

		_allowBackOrderSupplier = null;
	}

	@JsonIgnore
	public void setAllowBackOrder(
		UnsafeSupplier<Boolean, Exception> allowBackOrderUnsafeSupplier) {

		_allowBackOrderSupplier = () -> {
			try {
				return allowBackOrderUnsafeSupplier.get();
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
	protected Boolean allowBackOrder;

	@JsonIgnore
	private Supplier<Boolean> _allowBackOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "[10, 20, 30, 40]")
	@Valid
	public BigDecimal[] getAllowedOrderQuantities() {
		if (_allowedOrderQuantitiesSupplier != null) {
			allowedOrderQuantities = _allowedOrderQuantitiesSupplier.get();

			_allowedOrderQuantitiesSupplier = null;
		}

		return allowedOrderQuantities;
	}

	public void setAllowedOrderQuantities(BigDecimal[] allowedOrderQuantities) {
		this.allowedOrderQuantities = allowedOrderQuantities;

		_allowedOrderQuantitiesSupplier = null;
	}

	@JsonIgnore
	public void setAllowedOrderQuantities(
		UnsafeSupplier<BigDecimal[], Exception>
			allowedOrderQuantitiesUnsafeSupplier) {

		_allowedOrderQuantitiesSupplier = () -> {
			try {
				return allowedOrderQuantitiesUnsafeSupplier.get();
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
	protected BigDecimal[] allowedOrderQuantities;

	@JsonIgnore
	private Supplier<BigDecimal[]> _allowedOrderQuantitiesSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
	public Long getAvailabilityEstimateId() {
		if (_availabilityEstimateIdSupplier != null) {
			availabilityEstimateId = _availabilityEstimateIdSupplier.get();

			_availabilityEstimateIdSupplier = null;
		}

		return availabilityEstimateId;
	}

	public void setAvailabilityEstimateId(Long availabilityEstimateId) {
		this.availabilityEstimateId = availabilityEstimateId;

		_availabilityEstimateIdSupplier = null;
	}

	@JsonIgnore
	public void setAvailabilityEstimateId(
		UnsafeSupplier<Long, Exception> availabilityEstimateIdUnsafeSupplier) {

		_availabilityEstimateIdSupplier = () -> {
			try {
				return availabilityEstimateIdUnsafeSupplier.get();
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
	protected Long availabilityEstimateId;

	@JsonIgnore
	private Supplier<Long> _availabilityEstimateIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAvailabilityEstimateName() {
		if (_availabilityEstimateNameSupplier != null) {
			availabilityEstimateName = _availabilityEstimateNameSupplier.get();

			_availabilityEstimateNameSupplier = null;
		}

		return availabilityEstimateName;
	}

	public void setAvailabilityEstimateName(String availabilityEstimateName) {
		this.availabilityEstimateName = availabilityEstimateName;

		_availabilityEstimateNameSupplier = null;
	}

	@JsonIgnore
	public void setAvailabilityEstimateName(
		UnsafeSupplier<String, Exception>
			availabilityEstimateNameUnsafeSupplier) {

		_availabilityEstimateNameSupplier = () -> {
			try {
				return availabilityEstimateNameUnsafeSupplier.get();
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
	protected String availabilityEstimateName;

	@JsonIgnore
	private Supplier<String> _availabilityEstimateNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getDisplayAvailability() {
		if (_displayAvailabilitySupplier != null) {
			displayAvailability = _displayAvailabilitySupplier.get();

			_displayAvailabilitySupplier = null;
		}

		return displayAvailability;
	}

	public void setDisplayAvailability(Boolean displayAvailability) {
		this.displayAvailability = displayAvailability;

		_displayAvailabilitySupplier = null;
	}

	@JsonIgnore
	public void setDisplayAvailability(
		UnsafeSupplier<Boolean, Exception> displayAvailabilityUnsafeSupplier) {

		_displayAvailabilitySupplier = () -> {
			try {
				return displayAvailabilityUnsafeSupplier.get();
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
	protected Boolean displayAvailability;

	@JsonIgnore
	private Supplier<Boolean> _displayAvailabilitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getDisplayStockQuantity() {
		if (_displayStockQuantitySupplier != null) {
			displayStockQuantity = _displayStockQuantitySupplier.get();

			_displayStockQuantitySupplier = null;
		}

		return displayStockQuantity;
	}

	public void setDisplayStockQuantity(Boolean displayStockQuantity) {
		this.displayStockQuantity = displayStockQuantity;

		_displayStockQuantitySupplier = null;
	}

	@JsonIgnore
	public void setDisplayStockQuantity(
		UnsafeSupplier<Boolean, Exception> displayStockQuantityUnsafeSupplier) {

		_displayStockQuantitySupplier = () -> {
			try {
				return displayStockQuantityUnsafeSupplier.get();
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
	protected Boolean displayStockQuantity;

	@JsonIgnore
	private Supplier<Boolean> _displayStockQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The inventory engine that will be used to manage the product inventory"
	)
	public String getInventoryEngine() {
		if (_inventoryEngineSupplier != null) {
			inventoryEngine = _inventoryEngineSupplier.get();

			_inventoryEngineSupplier = null;
		}

		return inventoryEngine;
	}

	public void setInventoryEngine(String inventoryEngine) {
		this.inventoryEngine = inventoryEngine;

		_inventoryEngineSupplier = null;
	}

	@JsonIgnore
	public void setInventoryEngine(
		UnsafeSupplier<String, Exception> inventoryEngineUnsafeSupplier) {

		_inventoryEngineSupplier = () -> {
			try {
				return inventoryEngineUnsafeSupplier.get();
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
		description = "The inventory engine that will be used to manage the product inventory"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String inventoryEngine;

	@JsonIgnore
	private Supplier<String> _inventoryEngineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The low stock action that will be performed when a product is out of stock"
	)
	public String getLowStockAction() {
		if (_lowStockActionSupplier != null) {
			lowStockAction = _lowStockActionSupplier.get();

			_lowStockActionSupplier = null;
		}

		return lowStockAction;
	}

	public void setLowStockAction(String lowStockAction) {
		this.lowStockAction = lowStockAction;

		_lowStockActionSupplier = null;
	}

	@JsonIgnore
	public void setLowStockAction(
		UnsafeSupplier<String, Exception> lowStockActionUnsafeSupplier) {

		_lowStockActionSupplier = () -> {
			try {
				return lowStockActionUnsafeSupplier.get();
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
		description = "The low stock action that will be performed when a product is out of stock"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String lowStockAction;

	@JsonIgnore
	private Supplier<String> _lowStockActionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMaxOrderQuantity() {
		if (_maxOrderQuantitySupplier != null) {
			maxOrderQuantity = _maxOrderQuantitySupplier.get();

			_maxOrderQuantitySupplier = null;
		}

		return maxOrderQuantity;
	}

	public void setMaxOrderQuantity(BigDecimal maxOrderQuantity) {
		this.maxOrderQuantity = maxOrderQuantity;

		_maxOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMaxOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception> maxOrderQuantityUnsafeSupplier) {

		_maxOrderQuantitySupplier = () -> {
			try {
				return maxOrderQuantityUnsafeSupplier.get();
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
	protected BigDecimal maxOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _maxOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMinOrderQuantity() {
		if (_minOrderQuantitySupplier != null) {
			minOrderQuantity = _minOrderQuantitySupplier.get();

			_minOrderQuantitySupplier = null;
		}

		return minOrderQuantity;
	}

	public void setMinOrderQuantity(BigDecimal minOrderQuantity) {
		this.minOrderQuantity = minOrderQuantity;

		_minOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMinOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception> minOrderQuantityUnsafeSupplier) {

		_minOrderQuantitySupplier = () -> {
			try {
				return minOrderQuantityUnsafeSupplier.get();
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
	protected BigDecimal minOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _minOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMinStockQuantity() {
		if (_minStockQuantitySupplier != null) {
			minStockQuantity = _minStockQuantitySupplier.get();

			_minStockQuantitySupplier = null;
		}

		return minStockQuantity;
	}

	public void setMinStockQuantity(BigDecimal minStockQuantity) {
		this.minStockQuantity = minStockQuantity;

		_minStockQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMinStockQuantity(
		UnsafeSupplier<BigDecimal, Exception> minStockQuantityUnsafeSupplier) {

		_minStockQuantitySupplier = () -> {
			try {
				return minStockQuantityUnsafeSupplier.get();
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
	protected BigDecimal minStockQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _minStockQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMultipleOrderQuantity() {
		if (_multipleOrderQuantitySupplier != null) {
			multipleOrderQuantity = _multipleOrderQuantitySupplier.get();

			_multipleOrderQuantitySupplier = null;
		}

		return multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(BigDecimal multipleOrderQuantity) {
		this.multipleOrderQuantity = multipleOrderQuantity;

		_multipleOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMultipleOrderQuantity(
		UnsafeSupplier<BigDecimal, Exception>
			multipleOrderQuantityUnsafeSupplier) {

		_multipleOrderQuantitySupplier = () -> {
			try {
				return multipleOrderQuantityUnsafeSupplier.get();
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
	protected BigDecimal multipleOrderQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _multipleOrderQuantitySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfiguration)) {
			return false;
		}

		ProductConfiguration productConfiguration =
			(ProductConfiguration)object;

		return Objects.equals(toString(), productConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean allowBackOrder = getAllowBackOrder();

		if (allowBackOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowBackOrder\": ");

			sb.append(allowBackOrder);
		}

		BigDecimal[] allowedOrderQuantities = getAllowedOrderQuantities();

		if (allowedOrderQuantities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedOrderQuantities\": ");

			sb.append("[");

			for (int i = 0; i < allowedOrderQuantities.length; i++) {
				sb.append(allowedOrderQuantities[i]);

				if ((i + 1) < allowedOrderQuantities.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long availabilityEstimateId = getAvailabilityEstimateId();

		if (availabilityEstimateId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availabilityEstimateId\": ");

			sb.append(availabilityEstimateId);
		}

		String availabilityEstimateName = getAvailabilityEstimateName();

		if (availabilityEstimateName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availabilityEstimateName\": ");

			sb.append("\"");

			sb.append(_escape(availabilityEstimateName));

			sb.append("\"");
		}

		Boolean displayAvailability = getDisplayAvailability();

		if (displayAvailability != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAvailability\": ");

			sb.append(displayAvailability);
		}

		Boolean displayStockQuantity = getDisplayStockQuantity();

		if (displayStockQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayStockQuantity\": ");

			sb.append(displayStockQuantity);
		}

		String inventoryEngine = getInventoryEngine();

		if (inventoryEngine != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inventoryEngine\": ");

			sb.append("\"");

			sb.append(_escape(inventoryEngine));

			sb.append("\"");
		}

		String lowStockAction = getLowStockAction();

		if (lowStockAction != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lowStockAction\": ");

			sb.append("\"");

			sb.append(_escape(lowStockAction));

			sb.append("\"");
		}

		BigDecimal maxOrderQuantity = getMaxOrderQuantity();

		if (maxOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxOrderQuantity\": ");

			sb.append(maxOrderQuantity);
		}

		BigDecimal minOrderQuantity = getMinOrderQuantity();

		if (minOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minOrderQuantity\": ");

			sb.append(minOrderQuantity);
		}

		BigDecimal minStockQuantity = getMinStockQuantity();

		if (minStockQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minStockQuantity\": ");

			sb.append(minStockQuantity);
		}

		BigDecimal multipleOrderQuantity = getMultipleOrderQuantity();

		if (multipleOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multipleOrderQuantity\": ");

			sb.append(multipleOrderQuantity);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductConfiguration",
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