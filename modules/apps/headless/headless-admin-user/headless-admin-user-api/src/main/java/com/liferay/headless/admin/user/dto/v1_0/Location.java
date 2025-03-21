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

import jakarta.validation.Valid;

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
	description = "The organization's postal information (country and region).",
	value = "Location"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Location")
public class Location implements Serializable {

	public static Location toDTO(String json) {
		return ObjectMapperUtil.readValue(Location.class, json);
	}

	public static Location unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Location.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's country. This follows the [`addressCountry`](https://schema.org/addressCountry) specification."
	)
	public String getAddressCountry() {
		if (_addressCountrySupplier != null) {
			addressCountry = _addressCountrySupplier.get();

			_addressCountrySupplier = null;
		}

		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;

		_addressCountrySupplier = null;
	}

	@JsonIgnore
	public void setAddressCountry(
		UnsafeSupplier<String, Exception> addressCountryUnsafeSupplier) {

		_addressCountrySupplier = () -> {
			try {
				return addressCountryUnsafeSupplier.get();
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
		description = "The organization's country. This follows the [`addressCountry`](https://schema.org/addressCountry) specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressCountry;

	@JsonIgnore
	private Supplier<String> _addressCountrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's country isocode."
	)
	public String getAddressCountryCode() {
		if (_addressCountryCodeSupplier != null) {
			addressCountryCode = _addressCountryCodeSupplier.get();

			_addressCountryCodeSupplier = null;
		}

		return addressCountryCode;
	}

	public void setAddressCountryCode(String addressCountryCode) {
		this.addressCountryCode = addressCountryCode;

		_addressCountryCodeSupplier = null;
	}

	@JsonIgnore
	public void setAddressCountryCode(
		UnsafeSupplier<String, Exception> addressCountryCodeUnsafeSupplier) {

		_addressCountryCodeSupplier = () -> {
			try {
				return addressCountryCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The organization's country isocode.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String addressCountryCode;

	@JsonIgnore
	private Supplier<String> _addressCountryCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getAddressCountry_i18n() {
		if (_addressCountry_i18nSupplier != null) {
			addressCountry_i18n = _addressCountry_i18nSupplier.get();

			_addressCountry_i18nSupplier = null;
		}

		return addressCountry_i18n;
	}

	public void setAddressCountry_i18n(
		Map<String, String> addressCountry_i18n) {

		this.addressCountry_i18n = addressCountry_i18n;

		_addressCountry_i18nSupplier = null;
	}

	@JsonIgnore
	public void setAddressCountry_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			addressCountry_i18nUnsafeSupplier) {

		_addressCountry_i18nSupplier = () -> {
			try {
				return addressCountry_i18nUnsafeSupplier.get();
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
	protected Map<String, String> addressCountry_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _addressCountry_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's region. This follows the [`addressRegion`](https://schema.org/addressRegion) specification."
	)
	public String getAddressRegion() {
		if (_addressRegionSupplier != null) {
			addressRegion = _addressRegionSupplier.get();

			_addressRegionSupplier = null;
		}

		return addressRegion;
	}

	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;

		_addressRegionSupplier = null;
	}

	@JsonIgnore
	public void setAddressRegion(
		UnsafeSupplier<String, Exception> addressRegionUnsafeSupplier) {

		_addressRegionSupplier = () -> {
			try {
				return addressRegionUnsafeSupplier.get();
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
		description = "The organization's region. This follows the [`addressRegion`](https://schema.org/addressRegion) specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressRegion;

	@JsonIgnore
	private Supplier<String> _addressRegionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's region code."
	)
	public String getAddressRegionCode() {
		if (_addressRegionCodeSupplier != null) {
			addressRegionCode = _addressRegionCodeSupplier.get();

			_addressRegionCodeSupplier = null;
		}

		return addressRegionCode;
	}

	public void setAddressRegionCode(String addressRegionCode) {
		this.addressRegionCode = addressRegionCode;

		_addressRegionCodeSupplier = null;
	}

	@JsonIgnore
	public void setAddressRegionCode(
		UnsafeSupplier<String, Exception> addressRegionCodeUnsafeSupplier) {

		_addressRegionCodeSupplier = () -> {
			try {
				return addressRegionCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The organization's region code.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String addressRegionCode;

	@JsonIgnore
	private Supplier<String> _addressRegionCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The location's ID."
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

	@GraphQLField(description = "The location's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Location)) {
			return false;
		}

		Location location = (Location)object;

		return Objects.equals(toString(), location.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String addressCountry = getAddressCountry();

		if (addressCountry != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry\": ");

			sb.append("\"");

			sb.append(_escape(addressCountry));

			sb.append("\"");
		}

		String addressCountryCode = getAddressCountryCode();

		if (addressCountryCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountryCode\": ");

			sb.append("\"");

			sb.append(_escape(addressCountryCode));

			sb.append("\"");
		}

		Map<String, String> addressCountry_i18n = getAddressCountry_i18n();

		if (addressCountry_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry_i18n\": ");

			sb.append(_toJSON(addressCountry_i18n));
		}

		String addressRegion = getAddressRegion();

		if (addressRegion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressRegion\": ");

			sb.append("\"");

			sb.append(_escape(addressRegion));

			sb.append("\"");
		}

		String addressRegionCode = getAddressRegionCode();

		if (addressRegionCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressRegionCode\": ");

			sb.append("\"");

			sb.append(_escape(addressRegionCode));

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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.Location",
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