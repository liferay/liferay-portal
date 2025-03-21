/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("ProductSubscriptionConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductSubscriptionConfiguration")
public class ProductSubscriptionConfiguration implements Serializable {

	public static ProductSubscriptionConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ProductSubscriptionConfiguration.class, json);
	}

	public static ProductSubscriptionConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductSubscriptionConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getDeliverySubscriptionEnable() {
		if (_deliverySubscriptionEnableSupplier != null) {
			deliverySubscriptionEnable =
				_deliverySubscriptionEnableSupplier.get();

			_deliverySubscriptionEnableSupplier = null;
		}

		return deliverySubscriptionEnable;
	}

	public void setDeliverySubscriptionEnable(
		Boolean deliverySubscriptionEnable) {

		this.deliverySubscriptionEnable = deliverySubscriptionEnable;

		_deliverySubscriptionEnableSupplier = null;
	}

	@JsonIgnore
	public void setDeliverySubscriptionEnable(
		UnsafeSupplier<Boolean, Exception>
			deliverySubscriptionEnableUnsafeSupplier) {

		_deliverySubscriptionEnableSupplier = () -> {
			try {
				return deliverySubscriptionEnableUnsafeSupplier.get();
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
	protected Boolean deliverySubscriptionEnable;

	@JsonIgnore
	private Supplier<Boolean> _deliverySubscriptionEnableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2")
	public Integer getDeliverySubscriptionLength() {
		if (_deliverySubscriptionLengthSupplier != null) {
			deliverySubscriptionLength =
				_deliverySubscriptionLengthSupplier.get();

			_deliverySubscriptionLengthSupplier = null;
		}

		return deliverySubscriptionLength;
	}

	public void setDeliverySubscriptionLength(
		Integer deliverySubscriptionLength) {

		this.deliverySubscriptionLength = deliverySubscriptionLength;

		_deliverySubscriptionLengthSupplier = null;
	}

	@JsonIgnore
	public void setDeliverySubscriptionLength(
		UnsafeSupplier<Integer, Exception>
			deliverySubscriptionLengthUnsafeSupplier) {

		_deliverySubscriptionLengthSupplier = () -> {
			try {
				return deliverySubscriptionLengthUnsafeSupplier.get();
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
	protected Integer deliverySubscriptionLength;

	@JsonIgnore
	private Supplier<Integer> _deliverySubscriptionLengthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	public Long getDeliverySubscriptionNumberOfLength() {
		if (_deliverySubscriptionNumberOfLengthSupplier != null) {
			deliverySubscriptionNumberOfLength =
				_deliverySubscriptionNumberOfLengthSupplier.get();

			_deliverySubscriptionNumberOfLengthSupplier = null;
		}

		return deliverySubscriptionNumberOfLength;
	}

	public void setDeliverySubscriptionNumberOfLength(
		Long deliverySubscriptionNumberOfLength) {

		this.deliverySubscriptionNumberOfLength =
			deliverySubscriptionNumberOfLength;

		_deliverySubscriptionNumberOfLengthSupplier = null;
	}

	@JsonIgnore
	public void setDeliverySubscriptionNumberOfLength(
		UnsafeSupplier<Long, Exception>
			deliverySubscriptionNumberOfLengthUnsafeSupplier) {

		_deliverySubscriptionNumberOfLengthSupplier = () -> {
			try {
				return deliverySubscriptionNumberOfLengthUnsafeSupplier.get();
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
	protected Long deliverySubscriptionNumberOfLength;

	@JsonIgnore
	private Supplier<Long> _deliverySubscriptionNumberOfLengthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "monthly")
	@JsonGetter("deliverySubscriptionType")
	@Valid
	public DeliverySubscriptionType getDeliverySubscriptionType() {
		if (_deliverySubscriptionTypeSupplier != null) {
			deliverySubscriptionType = _deliverySubscriptionTypeSupplier.get();

			_deliverySubscriptionTypeSupplier = null;
		}

		return deliverySubscriptionType;
	}

	@JsonIgnore
	public String getDeliverySubscriptionTypeAsString() {
		DeliverySubscriptionType deliverySubscriptionType =
			getDeliverySubscriptionType();

		if (deliverySubscriptionType == null) {
			return null;
		}

		return deliverySubscriptionType.toString();
	}

	public void setDeliverySubscriptionType(
		DeliverySubscriptionType deliverySubscriptionType) {

		this.deliverySubscriptionType = deliverySubscriptionType;

		_deliverySubscriptionTypeSupplier = null;
	}

	@JsonIgnore
	public void setDeliverySubscriptionType(
		UnsafeSupplier<DeliverySubscriptionType, Exception>
			deliverySubscriptionTypeUnsafeSupplier) {

		_deliverySubscriptionTypeSupplier = () -> {
			try {
				return deliverySubscriptionTypeUnsafeSupplier.get();
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
	protected DeliverySubscriptionType deliverySubscriptionType;

	@JsonIgnore
	private Supplier<DeliverySubscriptionType>
		_deliverySubscriptionTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{deliveryMonthDay=1, deliveryMonthlyMode=0}"
	)
	@Valid
	public Map<String, String> getDeliverySubscriptionTypeSettings() {
		if (_deliverySubscriptionTypeSettingsSupplier != null) {
			deliverySubscriptionTypeSettings =
				_deliverySubscriptionTypeSettingsSupplier.get();

			_deliverySubscriptionTypeSettingsSupplier = null;
		}

		return deliverySubscriptionTypeSettings;
	}

	public void setDeliverySubscriptionTypeSettings(
		Map<String, String> deliverySubscriptionTypeSettings) {

		this.deliverySubscriptionTypeSettings =
			deliverySubscriptionTypeSettings;

		_deliverySubscriptionTypeSettingsSupplier = null;
	}

	@JsonIgnore
	public void setDeliverySubscriptionTypeSettings(
		UnsafeSupplier<Map<String, String>, Exception>
			deliverySubscriptionTypeSettingsUnsafeSupplier) {

		_deliverySubscriptionTypeSettingsSupplier = () -> {
			try {
				return deliverySubscriptionTypeSettingsUnsafeSupplier.get();
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
	protected Map<String, String> deliverySubscriptionTypeSettings;

	@JsonIgnore
	private Supplier<Map<String, String>>
		_deliverySubscriptionTypeSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getEnable() {
		if (_enableSupplier != null) {
			enable = _enableSupplier.get();

			_enableSupplier = null;
		}

		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;

		_enableSupplier = null;
	}

	@JsonIgnore
	public void setEnable(
		UnsafeSupplier<Boolean, Exception> enableUnsafeSupplier) {

		_enableSupplier = () -> {
			try {
				return enableUnsafeSupplier.get();
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
	protected Boolean enable;

	@JsonIgnore
	private Supplier<Boolean> _enableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2")
	public Integer getLength() {
		if (_lengthSupplier != null) {
			length = _lengthSupplier.get();

			_lengthSupplier = null;
		}

		return length;
	}

	public void setLength(Integer length) {
		this.length = length;

		_lengthSupplier = null;
	}

	@JsonIgnore
	public void setLength(
		UnsafeSupplier<Integer, Exception> lengthUnsafeSupplier) {

		_lengthSupplier = () -> {
			try {
				return lengthUnsafeSupplier.get();
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
	protected Integer length;

	@JsonIgnore
	private Supplier<Integer> _lengthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "12")
	public Long getNumberOfLength() {
		if (_numberOfLengthSupplier != null) {
			numberOfLength = _numberOfLengthSupplier.get();

			_numberOfLengthSupplier = null;
		}

		return numberOfLength;
	}

	public void setNumberOfLength(Long numberOfLength) {
		this.numberOfLength = numberOfLength;

		_numberOfLengthSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfLength(
		UnsafeSupplier<Long, Exception> numberOfLengthUnsafeSupplier) {

		_numberOfLengthSupplier = () -> {
			try {
				return numberOfLengthUnsafeSupplier.get();
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
	protected Long numberOfLength;

	@JsonIgnore
	private Supplier<Long> _numberOfLengthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "monthly")
	@JsonGetter("subscriptionType")
	@Valid
	public SubscriptionType getSubscriptionType() {
		if (_subscriptionTypeSupplier != null) {
			subscriptionType = _subscriptionTypeSupplier.get();

			_subscriptionTypeSupplier = null;
		}

		return subscriptionType;
	}

	@JsonIgnore
	public String getSubscriptionTypeAsString() {
		SubscriptionType subscriptionType = getSubscriptionType();

		if (subscriptionType == null) {
			return null;
		}

		return subscriptionType.toString();
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;

		_subscriptionTypeSupplier = null;
	}

	@JsonIgnore
	public void setSubscriptionType(
		UnsafeSupplier<SubscriptionType, Exception>
			subscriptionTypeUnsafeSupplier) {

		_subscriptionTypeSupplier = () -> {
			try {
				return subscriptionTypeUnsafeSupplier.get();
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
	protected SubscriptionType subscriptionType;

	@JsonIgnore
	private Supplier<SubscriptionType> _subscriptionTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{monthDay=1, monthlyMode=0}"
	)
	@Valid
	public Map<String, String> getSubscriptionTypeSettings() {
		if (_subscriptionTypeSettingsSupplier != null) {
			subscriptionTypeSettings = _subscriptionTypeSettingsSupplier.get();

			_subscriptionTypeSettingsSupplier = null;
		}

		return subscriptionTypeSettings;
	}

	public void setSubscriptionTypeSettings(
		Map<String, String> subscriptionTypeSettings) {

		this.subscriptionTypeSettings = subscriptionTypeSettings;

		_subscriptionTypeSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSubscriptionTypeSettings(
		UnsafeSupplier<Map<String, String>, Exception>
			subscriptionTypeSettingsUnsafeSupplier) {

		_subscriptionTypeSettingsSupplier = () -> {
			try {
				return subscriptionTypeSettingsUnsafeSupplier.get();
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
	protected Map<String, String> subscriptionTypeSettings;

	@JsonIgnore
	private Supplier<Map<String, String>> _subscriptionTypeSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductSubscriptionConfiguration)) {
			return false;
		}

		ProductSubscriptionConfiguration productSubscriptionConfiguration =
			(ProductSubscriptionConfiguration)object;

		return Objects.equals(
			toString(), productSubscriptionConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean deliverySubscriptionEnable = getDeliverySubscriptionEnable();

		if (deliverySubscriptionEnable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliverySubscriptionEnable\": ");

			sb.append(deliverySubscriptionEnable);
		}

		Integer deliverySubscriptionLength = getDeliverySubscriptionLength();

		if (deliverySubscriptionLength != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliverySubscriptionLength\": ");

			sb.append(deliverySubscriptionLength);
		}

		Long deliverySubscriptionNumberOfLength =
			getDeliverySubscriptionNumberOfLength();

		if (deliverySubscriptionNumberOfLength != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliverySubscriptionNumberOfLength\": ");

			sb.append(deliverySubscriptionNumberOfLength);
		}

		DeliverySubscriptionType deliverySubscriptionType =
			getDeliverySubscriptionType();

		if (deliverySubscriptionType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliverySubscriptionType\": ");

			sb.append("\"");

			sb.append(deliverySubscriptionType);

			sb.append("\"");
		}

		Map<String, String> deliverySubscriptionTypeSettings =
			getDeliverySubscriptionTypeSettings();

		if (deliverySubscriptionTypeSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliverySubscriptionTypeSettings\": ");

			sb.append(_toJSON(deliverySubscriptionTypeSettings));
		}

		Boolean enable = getEnable();

		if (enable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enable\": ");

			sb.append(enable);
		}

		Integer length = getLength();

		if (length != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"length\": ");

			sb.append(length);
		}

		Long numberOfLength = getNumberOfLength();

		if (numberOfLength != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfLength\": ");

			sb.append(numberOfLength);
		}

		SubscriptionType subscriptionType = getSubscriptionType();

		if (subscriptionType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscriptionType\": ");

			sb.append("\"");

			sb.append(subscriptionType);

			sb.append("\"");
		}

		Map<String, String> subscriptionTypeSettings =
			getSubscriptionTypeSettings();

		if (subscriptionTypeSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscriptionTypeSettings\": ");

			sb.append(_toJSON(subscriptionTypeSettings));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSubscriptionConfiguration",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("DeliverySubscriptionType")
	public static enum DeliverySubscriptionType {

		DAILY("daily"), MONTHLY("monthly"), WEEKLY("weekly"), YEARLY("yearly");

		@JsonCreator
		public static DeliverySubscriptionType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DeliverySubscriptionType deliverySubscriptionType : values()) {
				if (Objects.equals(
						deliverySubscriptionType.getValue(), value)) {

					return deliverySubscriptionType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private DeliverySubscriptionType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("SubscriptionType")
	public static enum SubscriptionType {

		DAILY("daily"), MONTHLY("monthly"), WEEKLY("weekly"), YEARLY("yearly");

		@JsonCreator
		public static SubscriptionType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (SubscriptionType subscriptionType : values()) {
				if (Objects.equals(subscriptionType.getValue(), value)) {
					return subscriptionType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private SubscriptionType(String value) {
			_value = value;
		}

		private final String _value;

	}

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