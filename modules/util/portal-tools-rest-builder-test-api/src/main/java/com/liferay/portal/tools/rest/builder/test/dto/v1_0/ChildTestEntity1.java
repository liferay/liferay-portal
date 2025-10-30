/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.rest.builder.test.constant.v1_0.StringTestEntity;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("ChildTestEntity1")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ChildTestEntity1")
public class ChildTestEntity1 extends TestEntity implements Serializable {

	public static ChildTestEntity1 toDTO(String json) {
		return ObjectMapperUtil.readValue(ChildTestEntity1.class, json);
	}

	public static ChildTestEntity1 unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ChildTestEntity1.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getProperty1() {
		if (_property1Supplier != null) {
			property1 = _property1Supplier.get();

			_property1Supplier = null;
		}

		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;

		_property1Supplier = null;
	}

	@JsonIgnore
	public void setProperty1(
		UnsafeSupplier<String, Exception> property1UnsafeSupplier) {

		_property1Supplier = () -> {
			try {
				return property1UnsafeSupplier.get();
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
	protected String property1;

	@JsonIgnore
	private Supplier<String> _property1Supplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ChildTestEntity1)) {
			return false;
		}

		ChildTestEntity1 childTestEntity1 = (ChildTestEntity1)object;

		return Objects.equals(toString(), childTestEntity1.toString());
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

		String property1 = getProperty1();

		if (property1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"property1\": ");

			sb.append("\"");

			sb.append(_escape(property1));

			sb.append("\"");
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		Long documentId = getDocumentId();

		if (documentId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentId\": ");

			sb.append(documentId);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String jsonProperty = getJsonProperty();

		if (jsonProperty != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jsonProperty\": ");

			sb.append("\"");

			sb.append(_escape(jsonProperty));

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

		NestedTestEntity nestedTestEntity = getNestedTestEntity();

		if (nestedTestEntity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedTestEntity\": ");

			sb.append(String.valueOf(nestedTestEntity));
		}

		String self = getSelf();

		if (self != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"self\": ");

			sb.append("\"");

			sb.append(_escape(self));

			sb.append("\"");
		}

		StringTestEntity[] stringTestEntities = getStringTestEntities();

		if (stringTestEntities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntities\": ");

			sb.append("[");

			for (int i = 0; i < stringTestEntities.length; i++) {
				sb.append(stringTestEntities[i]);

				if ((i + 1) < stringTestEntities.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		StringTestEntity stringTestEntity = getStringTestEntity();

		if (stringTestEntity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntity\": ");

			sb.append(stringTestEntity);
		}

		TestEntity testEntities = getTestEntities();

		if (testEntities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testEntities\": ");

			sb.append(String.valueOf(testEntities));
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.tools.rest.builder.test.dto.v1_0.ChildTestEntity1",
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