/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.custom.field;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Carlos Correa
 */
@GraphQLName(
	description = "Represents the value of each custom field. Fields can contain different information types (e.g., geolocation, strings, etc.).",
	value = "CustomField"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CustomField")
public class CustomField implements Serializable {

	public static CustomField toDTO(String json) {
		return ObjectMapperUtil.readValue(CustomField.class, json);
	}

	public static CustomField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(CustomField.class, json);
	}

	@Schema(description = "The field's attribute type.")
	public AttributeType getAttributeType() {
		if (_attributeTypeSupplier != null) {
			attributeType = _attributeTypeSupplier.get();

			_attributeTypeSupplier = null;
		}

		return attributeType;
	}

	@JsonIgnore
	public String getAttributeTypeAsString() {
		AttributeType attributeType = getAttributeType();

		if (attributeType == null) {
			return null;
		}

		return attributeType.toString();
	}

	@Schema(description = "The field's value.")
	@Valid
	public CustomValue getCustomValue() {
		if (_customValueSupplier != null) {
			customValue = _customValueSupplier.get();

			_customValueSupplier = null;
		}

		return customValue;
	}

	@Schema(description = "The field's type (e.g., image, text, etc.).")
	public String getDataType() {
		if (_dataTypeSupplier != null) {
			dataType = _dataTypeSupplier.get();

			_dataTypeSupplier = null;
		}

		return dataType;
	}

	@Schema(
		description = "The field's internal name. This is valid for comparisons and unique in the structured content."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;

		_attributeTypeSupplier = null;
	}

	@JsonIgnore
	public void setAttributeType(
		UnsafeSupplier<AttributeType, Exception> attributeTypeUnsafeSupplier) {

		_attributeTypeSupplier = () -> {
			try {
				return attributeTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	public void setCustomValue(CustomValue customValue) {
		this.customValue = customValue;

		_customValueSupplier = null;
	}

	@JsonIgnore
	public void setCustomValue(
		UnsafeSupplier<CustomValue, Exception> customValueUnsafeSupplier) {

		_customValueSupplier = () -> {
			try {
				return customValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;

		_dataTypeSupplier = null;
	}

	@JsonIgnore
	public void setDataType(
		UnsafeSupplier<String, Exception> dataTypeUnsafeSupplier) {

		_dataTypeSupplier = () -> {
			try {
				return dataTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
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

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.vulcan.custom.field.CustomField",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("AttributeType")
	public static enum AttributeType {

		BOOLEAN("BOOLEAN"), BOOLEAN_ARRAY("BOOLEAN_ARRAY"), DATE("DATE"),
		DATE_ARRAY("DATE_ARRAY"), DOUBLE("DOUBLE"),
		DOUBLE_ARRAY("DOUBLE_ARRAY"), FLOAT("FLOAT"),
		FLOAT_ARRAY("FLOAT_ARRAY"), GEOLOCATION("GEOLOCATION"),
		INTEGER("INTEGER"), INTEGER_ARRAY("INTEGER_ARRAY"), LONG("LONG"),
		LONG_ARRAY("LONG_ARRAY"), NUMBER("NUMBER"),
		NUMBER_ARRAY("NUMBER_ARRAY"), SHORT("SHORT"),
		SHORT_ARRAY("SHORT_ARRAY"), STRING("STRING"),
		STRING_ARRAY("STRING_ARRAY"),
		STRING_ARRAY_LOCALIZED("STRING_ARRAY_LOCALIZED"),
		STRING_LOCALIZED("STRING_LOCALIZED");

		@JsonCreator
		public static AttributeType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (AttributeType attributeType : values()) {
				if (Objects.equals(attributeType.getValue(), value)) {
					return attributeType;
				}
			}

			throw new IllegalArgumentException("Invalid value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private AttributeType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLField(description = "The field's attribute type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AttributeType attributeType;

	@GraphQLField(description = "The field's value.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CustomValue customValue;

	@GraphQLField(description = "The field's type (e.g., image, text, etc.).")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String dataType;

	@GraphQLField(
		description = "The field's internal name. This is valid for comparisons and unique in the structured content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<AttributeType> _attributeTypeSupplier;

	@JsonIgnore
	private Supplier<CustomValue> _customValueSupplier;

	@JsonIgnore
	private Supplier<String> _dataTypeSupplier;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

}