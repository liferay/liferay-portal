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
@GraphQLName("SkuOption")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SkuOption")
public class SkuOption implements Serializable {

	public static SkuOption toDTO(String json) {
		return ObjectMapperUtil.readValue(SkuOption.class, json);
	}

	public static SkuOption unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SkuOption.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
	public Long getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(Long key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<Long, Exception> keyUnsafeSupplier) {
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long key;

	@JsonIgnore
	private Supplier<Long> _keySupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String priceType;

	@JsonIgnore
	private Supplier<String> _priceTypeSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String quantity;

	@JsonIgnore
	private Supplier<String> _quantitySupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long skuId;

	@JsonIgnore
	private Supplier<Long> _skuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getSkuOptionId() {
		if (_skuOptionIdSupplier != null) {
			skuOptionId = _skuOptionIdSupplier.get();

			_skuOptionIdSupplier = null;
		}

		return skuOptionId;
	}

	public void setSkuOptionId(Long skuOptionId) {
		this.skuOptionId = skuOptionId;

		_skuOptionIdSupplier = null;
	}

	@JsonIgnore
	public void setSkuOptionId(
		UnsafeSupplier<Long, Exception> skuOptionIdUnsafeSupplier) {

		_skuOptionIdSupplier = () -> {
			try {
				return skuOptionIdUnsafeSupplier.get();
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
	protected Long skuOptionId;

	@JsonIgnore
	private Supplier<Long> _skuOptionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Sku Option Key")
	public String getSkuOptionKey() {
		if (_skuOptionKeySupplier != null) {
			skuOptionKey = _skuOptionKeySupplier.get();

			_skuOptionKeySupplier = null;
		}

		return skuOptionKey;
	}

	public void setSkuOptionKey(String skuOptionKey) {
		this.skuOptionKey = skuOptionKey;

		_skuOptionKeySupplier = null;
	}

	@JsonIgnore
	public void setSkuOptionKey(
		UnsafeSupplier<String, Exception> skuOptionKeyUnsafeSupplier) {

		_skuOptionKeySupplier = () -> {
			try {
				return skuOptionKeyUnsafeSupplier.get();
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
	protected String skuOptionKey;

	@JsonIgnore
	private Supplier<String> _skuOptionKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Sku Option Name")
	public String getSkuOptionName() {
		if (_skuOptionNameSupplier != null) {
			skuOptionName = _skuOptionNameSupplier.get();

			_skuOptionNameSupplier = null;
		}

		return skuOptionName;
	}

	public void setSkuOptionName(String skuOptionName) {
		this.skuOptionName = skuOptionName;

		_skuOptionNameSupplier = null;
	}

	@JsonIgnore
	public void setSkuOptionName(
		UnsafeSupplier<String, Exception> skuOptionNameUnsafeSupplier) {

		_skuOptionNameSupplier = () -> {
			try {
				return skuOptionNameUnsafeSupplier.get();
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
	protected String skuOptionName;

	@JsonIgnore
	private Supplier<String> _skuOptionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getSkuOptionValueId() {
		if (_skuOptionValueIdSupplier != null) {
			skuOptionValueId = _skuOptionValueIdSupplier.get();

			_skuOptionValueIdSupplier = null;
		}

		return skuOptionValueId;
	}

	public void setSkuOptionValueId(Long skuOptionValueId) {
		this.skuOptionValueId = skuOptionValueId;

		_skuOptionValueIdSupplier = null;
	}

	@JsonIgnore
	public void setSkuOptionValueId(
		UnsafeSupplier<Long, Exception> skuOptionValueIdUnsafeSupplier) {

		_skuOptionValueIdSupplier = () -> {
			try {
				return skuOptionValueIdUnsafeSupplier.get();
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
	protected Long skuOptionValueId;

	@JsonIgnore
	private Supplier<Long> _skuOptionValueIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Sku Option Value Key"
	)
	public String getSkuOptionValueKey() {
		if (_skuOptionValueKeySupplier != null) {
			skuOptionValueKey = _skuOptionValueKeySupplier.get();

			_skuOptionValueKeySupplier = null;
		}

		return skuOptionValueKey;
	}

	public void setSkuOptionValueKey(String skuOptionValueKey) {
		this.skuOptionValueKey = skuOptionValueKey;

		_skuOptionValueKeySupplier = null;
	}

	@JsonIgnore
	public void setSkuOptionValueKey(
		UnsafeSupplier<String, Exception> skuOptionValueKeyUnsafeSupplier) {

		_skuOptionValueKeySupplier = () -> {
			try {
				return skuOptionValueKeyUnsafeSupplier.get();
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
	protected String skuOptionValueKey;

	@JsonIgnore
	private Supplier<String> _skuOptionValueKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSkuOptionValueNames() {
		if (_skuOptionValueNamesSupplier != null) {
			skuOptionValueNames = _skuOptionValueNamesSupplier.get();

			_skuOptionValueNamesSupplier = null;
		}

		return skuOptionValueNames;
	}

	public void setSkuOptionValueNames(String[] skuOptionValueNames) {
		this.skuOptionValueNames = skuOptionValueNames;

		_skuOptionValueNamesSupplier = null;
	}

	@JsonIgnore
	public void setSkuOptionValueNames(
		UnsafeSupplier<String[], Exception> skuOptionValueNamesUnsafeSupplier) {

		_skuOptionValueNamesSupplier = () -> {
			try {
				return skuOptionValueNamesUnsafeSupplier.get();
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
	protected String[] skuOptionValueNames;

	@JsonIgnore
	private Supplier<String[]> _skuOptionValueNamesSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
	public Long getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(Long value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(UnsafeSupplier<Long, Exception> valueUnsafeSupplier) {
		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
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
	protected Long value;

	@JsonIgnore
	private Supplier<Long> _valueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SkuOption)) {
			return false;
		}

		SkuOption skuOption = (SkuOption)object;

		return Objects.equals(toString(), skuOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append(key);
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

		Long skuId = getSkuId();

		if (skuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(skuId);
		}

		Long skuOptionId = getSkuOptionId();

		if (skuOptionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptionId\": ");

			sb.append(skuOptionId);
		}

		String skuOptionKey = getSkuOptionKey();

		if (skuOptionKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptionKey\": ");

			sb.append("\"");

			sb.append(_escape(skuOptionKey));

			sb.append("\"");
		}

		String skuOptionName = getSkuOptionName();

		if (skuOptionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptionName\": ");

			sb.append("\"");

			sb.append(_escape(skuOptionName));

			sb.append("\"");
		}

		Long skuOptionValueId = getSkuOptionValueId();

		if (skuOptionValueId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptionValueId\": ");

			sb.append(skuOptionValueId);
		}

		String skuOptionValueKey = getSkuOptionValueKey();

		if (skuOptionValueKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptionValueKey\": ");

			sb.append("\"");

			sb.append(_escape(skuOptionValueKey));

			sb.append("\"");
		}

		String[] skuOptionValueNames = getSkuOptionValueNames();

		if (skuOptionValueNames != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptionValueNames\": ");

			sb.append("[");

			for (int i = 0; i < skuOptionValueNames.length; i++) {
				sb.append("\"");

				sb.append(_escape(skuOptionValueNames[i]));

				sb.append("\"");

				if ((i + 1) < skuOptionValueNames.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(value);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuOption",
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