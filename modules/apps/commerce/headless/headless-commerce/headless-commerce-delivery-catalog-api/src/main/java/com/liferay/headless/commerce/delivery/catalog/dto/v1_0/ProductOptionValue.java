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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("ProductOptionValue")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductOptionValue")
public class ProductOptionValue implements Serializable {

	public static ProductOptionValue toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductOptionValue.class, json);
	}

	public static ProductOptionValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ProductOptionValue.class, json);
	}

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
	public String getInfoMessage() {
		if (_infoMessageSupplier != null) {
			infoMessage = _infoMessageSupplier.get();

			_infoMessageSupplier = null;
		}

		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;

		_infoMessageSupplier = null;
	}

	@JsonIgnore
	public void setInfoMessage(
		UnsafeSupplier<String, Exception> infoMessageUnsafeSupplier) {

		_infoMessageSupplier = () -> {
			try {
				return infoMessageUnsafeSupplier.get();
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
	protected String infoMessage;

	@JsonIgnore
	private Supplier<String> _infoMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "black")
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
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
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getPreselected() {
		if (_preselectedSupplier != null) {
			preselected = _preselectedSupplier.get();

			_preselectedSupplier = null;
		}

		return preselected;
	}

	public void setPreselected(Boolean preselected) {
		this.preselected = preselected;

		_preselectedSupplier = null;
	}

	@JsonIgnore
	public void setPreselected(
		UnsafeSupplier<Boolean, Exception> preselectedUnsafeSupplier) {

		_preselectedSupplier = () -> {
			try {
				return preselectedUnsafeSupplier.get();
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
	protected Boolean preselected;

	@JsonIgnore
	private Supplier<Boolean> _preselectedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPrice() {
		if (_priceSupplier != null) {
			price = _priceSupplier.get();

			_priceSupplier = null;
		}

		return price;
	}

	public void setPrice(String price) {
		this.price = price;

		_priceSupplier = null;
	}

	@JsonIgnore
	public void setPrice(
		UnsafeSupplier<String, Exception> priceUnsafeSupplier) {

		_priceSupplier = () -> {
			try {
				return priceUnsafeSupplier.get();
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
	protected String price;

	@JsonIgnore
	private Supplier<String> _priceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "static")
	public String getPriceType() {
		if (_priceTypeSupplier != null) {
			priceType = _priceTypeSupplier.get();

			_priceTypeSupplier = null;
		}

		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;

		_priceTypeSupplier = null;
	}

	@JsonIgnore
	public void setPriceType(
		UnsafeSupplier<String, Exception> priceTypeUnsafeSupplier) {

		_priceTypeSupplier = () -> {
			try {
				return priceTypeUnsafeSupplier.get();
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
	protected String priceType;

	@JsonIgnore
	private Supplier<String> _priceTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "1.2")
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
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
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getProductOptionId() {
		if (_productOptionIdSupplier != null) {
			productOptionId = _productOptionIdSupplier.get();

			_productOptionIdSupplier = null;
		}

		return productOptionId;
	}

	public void setProductOptionId(Long productOptionId) {
		this.productOptionId = productOptionId;

		_productOptionIdSupplier = null;
	}

	@JsonIgnore
	public void setProductOptionId(
		UnsafeSupplier<Long, Exception> productOptionIdUnsafeSupplier) {

		_productOptionIdSupplier = () -> {
			try {
				return productOptionIdUnsafeSupplier.get();
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
	protected Long productOptionId;

	@JsonIgnore
	private Supplier<Long> _productOptionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getQuantity() {
		if (_quantitySupplier != null) {
			quantity = _quantitySupplier.get();

			_quantitySupplier = null;
		}

		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;

		_quantitySupplier = null;
	}

	@JsonIgnore
	public void setQuantity(
		UnsafeSupplier<String, Exception> quantityUnsafeSupplier) {

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String quantity;

	@JsonIgnore
	private Supplier<String> _quantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRelativePriceFormatted() {
		if (_relativePriceFormattedSupplier != null) {
			relativePriceFormatted = _relativePriceFormattedSupplier.get();

			_relativePriceFormattedSupplier = null;
		}

		return relativePriceFormatted;
	}

	public void setRelativePriceFormatted(String relativePriceFormatted) {
		this.relativePriceFormatted = relativePriceFormatted;

		_relativePriceFormattedSupplier = null;
	}

	@JsonIgnore
	public void setRelativePriceFormatted(
		UnsafeSupplier<String, Exception>
			relativePriceFormattedUnsafeSupplier) {

		_relativePriceFormattedSupplier = () -> {
			try {
				return relativePriceFormattedUnsafeSupplier.get();
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
	protected String relativePriceFormatted;

	@JsonIgnore
	private Supplier<String> _relativePriceFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getSelectable() {
		if (_selectableSupplier != null) {
			selectable = _selectableSupplier.get();

			_selectableSupplier = null;
		}

		return selectable;
	}

	public void setSelectable(Boolean selectable) {
		this.selectable = selectable;

		_selectableSupplier = null;
	}

	@JsonIgnore
	public void setSelectable(
		UnsafeSupplier<Boolean, Exception> selectableUnsafeSupplier) {

		_selectableSupplier = () -> {
			try {
				return selectableUnsafeSupplier.get();
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
	protected Boolean selectable;

	@JsonIgnore
	private Supplier<Boolean> _selectableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getSkuId() {
		if (_skuIdSupplier != null) {
			skuId = _skuIdSupplier.get();

			_skuIdSupplier = null;
		}

		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;

		_skuIdSupplier = null;
	}

	@JsonIgnore
	public void setSkuId(UnsafeSupplier<Long, Exception> skuIdUnsafeSupplier) {
		_skuIdSupplier = () -> {
			try {
				return skuIdUnsafeSupplier.get();
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
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalPrice() {
		if (_totalPriceSupplier != null) {
			totalPrice = _totalPriceSupplier.get();

			_totalPriceSupplier = null;
		}

		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;

		_totalPriceSupplier = null;
	}

	@JsonIgnore
	public void setTotalPrice(
		UnsafeSupplier<String, Exception> totalPriceUnsafeSupplier) {

		_totalPriceSupplier = () -> {
			try {
				return totalPriceUnsafeSupplier.get();
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
	protected String totalPrice;

	@JsonIgnore
	private Supplier<String> _totalPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String unitOfMeasureKey;

	@JsonIgnore
	private Supplier<String> _unitOfMeasureKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getVisible() {
		if (_visibleSupplier != null) {
			visible = _visibleSupplier.get();

			_visibleSupplier = null;
		}

		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;

		_visibleSupplier = null;
	}

	@JsonIgnore
	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		_visibleSupplier = () -> {
			try {
				return visibleUnsafeSupplier.get();
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
	protected Boolean visible;

	@JsonIgnore
	private Supplier<Boolean> _visibleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductOptionValue)) {
			return false;
		}

		ProductOptionValue productOptionValue = (ProductOptionValue)object;

		return Objects.equals(toString(), productOptionValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String infoMessage = getInfoMessage();

		if (infoMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"infoMessage\": ");

			sb.append("\"");

			sb.append(_escape(infoMessage));

			sb.append("\"");
		}

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

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

		Boolean preselected = getPreselected();

		if (preselected != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"preselected\": ");

			sb.append(preselected);
		}

		String price = getPrice();

		if (price != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append("\"");

			sb.append(_escape(price));

			sb.append("\"");
		}

		String priceType = getPriceType();

		if (priceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceType\": ");

			sb.append("\"");

			sb.append(_escape(priceType));

			sb.append("\"");
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		Long productOptionId = getProductOptionId();

		if (productOptionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptionId\": ");

			sb.append(productOptionId);
		}

		String quantity = getQuantity();

		if (quantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append("\"");

			sb.append(_escape(quantity));

			sb.append("\"");
		}

		String relativePriceFormatted = getRelativePriceFormatted();

		if (relativePriceFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relativePriceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(relativePriceFormatted));

			sb.append("\"");
		}

		Boolean selectable = getSelectable();

		if (selectable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectable\": ");

			sb.append(selectable);
		}

		Long skuId = getSkuId();

		if (skuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(skuId);
		}

		String totalPrice = getTotalPrice();

		if (totalPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalPrice\": ");

			sb.append("\"");

			sb.append(_escape(totalPrice));

			sb.append("\"");
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

		Boolean visible = getVisible();

		if (visible != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(visible);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOptionValue",
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