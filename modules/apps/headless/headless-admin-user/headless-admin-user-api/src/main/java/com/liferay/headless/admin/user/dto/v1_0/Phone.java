/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a phone number. This follows the [telephone](https://schema.org/telephone) specification.",
	value = "Phone"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Phone")
public class Phone implements Serializable {

	public static Phone toDTO(String json) {
		return ObjectMapperUtil.readValue(Phone.class, json);
	}

	public static Phone unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Phone.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The phone number's extension."
	)
	public String getExtension() {
		if (_extensionSupplier != null) {
			extension = _extensionSupplier.get();

			_extensionSupplier = null;
		}

		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;

		_extensionSupplier = null;
	}

	@JsonIgnore
	public void setExtension(
		UnsafeSupplier<String, Exception> extensionUnsafeSupplier) {

		_extensionSupplier = () -> {
			try {
				return extensionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The phone number's extension.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String extension;

	@JsonIgnore
	private Supplier<String> _extensionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The phone number's external reference code."
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

	@GraphQLField(description = "The phone number's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The phone number's ID."
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

	@GraphQLField(description = "The phone number's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The phone number without its extension."
	)
	public String getPhoneNumber() {
		if (_phoneNumberSupplier != null) {
			phoneNumber = _phoneNumberSupplier.get();

			_phoneNumberSupplier = null;
		}

		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;

		_phoneNumberSupplier = null;
	}

	@JsonIgnore
	public void setPhoneNumber(
		UnsafeSupplier<String, Exception> phoneNumberUnsafeSupplier) {

		_phoneNumberSupplier = () -> {
			try {
				return phoneNumberUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The phone number without its extension.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String phoneNumber;

	@JsonIgnore
	private Supplier<String> _phoneNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The phone number's type."
	)
	public String getPhoneType() {
		if (_phoneTypeSupplier != null) {
			phoneType = _phoneTypeSupplier.get();

			_phoneTypeSupplier = null;
		}

		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;

		_phoneTypeSupplier = null;
	}

	@JsonIgnore
	public void setPhoneType(
		UnsafeSupplier<String, Exception> phoneTypeUnsafeSupplier) {

		_phoneTypeSupplier = () -> {
			try {
				return phoneTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The phone number's type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String phoneType;

	@JsonIgnore
	private Supplier<String> _phoneTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that identifies whether this is the main phone number of the user/organization."
	)
	public Boolean getPrimary() {
		if (_primarySupplier != null) {
			primary = _primarySupplier.get();

			_primarySupplier = null;
		}

		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;

		_primarySupplier = null;
	}

	@JsonIgnore
	public void setPrimary(
		UnsafeSupplier<Boolean, Exception> primaryUnsafeSupplier) {

		_primarySupplier = () -> {
			try {
				return primaryUnsafeSupplier.get();
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
		description = "A flag that identifies whether this is the main phone number of the user/organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean primary;

	@JsonIgnore
	private Supplier<Boolean> _primarySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Phone)) {
			return false;
		}

		Phone phone = (Phone)object;

		return Objects.equals(toString(), phone.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String extension = getExtension();

		if (extension != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"extension\": ");

			sb.append("\"");

			sb.append(_escape(extension));

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

		String phoneNumber = getPhoneNumber();

		if (phoneNumber != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(phoneNumber));

			sb.append("\"");
		}

		String phoneType = getPhoneType();

		if (phoneType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneType\": ");

			sb.append("\"");

			sb.append(_escape(phoneType));

			sb.append("\"");
		}

		Boolean primary = getPrimary();

		if (primary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(primary);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.Phone",
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