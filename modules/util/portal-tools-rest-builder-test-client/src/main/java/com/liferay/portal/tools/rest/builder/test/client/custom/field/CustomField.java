/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.custom.field;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class CustomField {

	public static CustomField toDTO(String json) {
		CustomFieldJSONParser customFieldJSONParser =
			new CustomFieldJSONParser();

		return customFieldJSONParser.parseToDTO(json);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CustomField)) {
			return false;
		}

		CustomField customField = (CustomField)object;

		return Objects.equals(toString(), customField.toString());
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public CustomValue getCustomValue() {
		return customValue;
	}

	public String getDataType() {
		return dataType;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public void setAttributeType(
		UnsafeSupplier<AttributeType, Exception> attributeTypeUnsafeSupplier) {

		try {
			attributeType = attributeTypeUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setCustomValue(CustomValue customValue) {
		this.customValue = customValue;
	}

	public void setCustomValue(
		UnsafeSupplier<CustomValue, Exception> customValueUnsafeSupplier) {

		try {
			customValue = customValueUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setDataType(
		UnsafeSupplier<String, Exception> dataTypeUnsafeSupplier) {

		try {
			dataType = dataTypeUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public String toString() {
		return CustomFieldJSONParser.toJSON(this);
	}

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

		public static AttributeType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (AttributeType attributeType : values()) {
				if (Objects.equals(attributeType.getValue(), value)) {
					return attributeType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

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

	protected AttributeType attributeType;
	protected CustomValue customValue;
	protected String dataType;
	protected String name;

	private static class CustomFieldJSONParser
		extends BaseJSONParser<CustomField> {

		public static String toJSON(CustomField customField) {
			if (customField == null) {
				return "null";
			}

			StringBuilder sb = new StringBuilder();

			sb.append("{");

			if (customField.getAttributeType() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"attributeType\": ");

				sb.append("\"");

				sb.append(customField.getAttributeType());

				sb.append("\"");
			}

			if (customField.getCustomValue() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"customValue\": ");

				sb.append(String.valueOf(customField.getCustomValue()));
			}

			if (customField.getDataType() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"dataType\": ");

				sb.append("\"");

				sb.append(_escape(customField.getDataType()));

				sb.append("\"");
			}

			if (customField.getName() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"name\": ");

				sb.append("\"");

				sb.append(_escape(customField.getName()));

				sb.append("\"");
			}

			sb.append("}");

			return sb.toString();
		}

		@Override
		protected CustomField createDTO() {
			return new CustomField();
		}

		@Override
		protected CustomField[] createDTOArray(int size) {
			return new CustomField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributeType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CustomField customField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributeType")) {
				if (jsonParserFieldValue != null) {
					customField.setAttributeType(
						CustomField.AttributeType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customValue")) {
				if (jsonParserFieldValue != null) {
					customField.setCustomValue(
						CustomValue.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					customField.setDataType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					customField.setName((String)jsonParserFieldValue);
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

		private static String _escape(Object object) {
			String string = String.valueOf(object);

			for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
				string = string.replace(strings[0], strings[1]);
			}

			return string;
		}

	}

}