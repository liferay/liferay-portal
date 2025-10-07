/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

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
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A physical mailing address for this user.", value = "Address"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Address")
public class Address implements Serializable {

	public static Address toDTO(String json) {
		return ObjectMapperUtil.readValue(Address.class, json);
	}

	public static Address unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Address.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The country name component."
	)
	public String getCountry() {
		if (_countrySupplier != null) {
			country = _countrySupplier.get();

			_countrySupplier = null;
		}

		return country;
	}

	public void setCountry(String country) {
		this.country = country;

		_countrySupplier = null;
	}

	@JsonIgnore
	public void setCountry(
		UnsafeSupplier<String, Exception> countryUnsafeSupplier) {

		_countrySupplier = () -> {
			try {
				return countryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The country name component.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String country;

	@JsonIgnore
	private Supplier<String> _countrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The full mailing address, formatted for display or use with a mailing label."
	)
	public String getFormatted() {
		if (_formattedSupplier != null) {
			formatted = _formattedSupplier.get();

			_formattedSupplier = null;
		}

		return formatted;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;

		_formattedSupplier = null;
	}

	@JsonIgnore
	public void setFormatted(
		UnsafeSupplier<String, Exception> formattedUnsafeSupplier) {

		_formattedSupplier = () -> {
			try {
				return formattedUnsafeSupplier.get();
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
		description = "The full mailing address, formatted for display or use with a mailing label."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String formatted;

	@JsonIgnore
	private Supplier<String> _formattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The city or locality component."
	)
	public String getLocality() {
		if (_localitySupplier != null) {
			locality = _localitySupplier.get();

			_localitySupplier = null;
		}

		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;

		_localitySupplier = null;
	}

	@JsonIgnore
	public void setLocality(
		UnsafeSupplier<String, Exception> localityUnsafeSupplier) {

		_localitySupplier = () -> {
			try {
				return localityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The city or locality component.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String locality;

	@JsonIgnore
	private Supplier<String> _localitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The zip code or postal code component."
	)
	public String getPostalCode() {
		if (_postalCodeSupplier != null) {
			postalCode = _postalCodeSupplier.get();

			_postalCodeSupplier = null;
		}

		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;

		_postalCodeSupplier = null;
	}

	@JsonIgnore
	public void setPostalCode(
		UnsafeSupplier<String, Exception> postalCodeUnsafeSupplier) {

		_postalCodeSupplier = () -> {
			try {
				return postalCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The zip code or postal code component.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String postalCode;

	@JsonIgnore
	private Supplier<String> _postalCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The preferred mailing address."
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

	@GraphQLField(description = "The preferred mailing address.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean primary;

	@JsonIgnore
	private Supplier<Boolean> _primarySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The state or region component."
	)
	public String getRegion() {
		if (_regionSupplier != null) {
			region = _regionSupplier.get();

			_regionSupplier = null;
		}

		return region;
	}

	public void setRegion(String region) {
		this.region = region;

		_regionSupplier = null;
	}

	@JsonIgnore
	public void setRegion(
		UnsafeSupplier<String, Exception> regionUnsafeSupplier) {

		_regionSupplier = () -> {
			try {
				return regionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The state or region component.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String region;

	@JsonIgnore
	private Supplier<String> _regionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The full street address component, which may include house number, street name, P.O. box, and multi-line extended street address information."
	)
	public String getStreetAddress() {
		if (_streetAddressSupplier != null) {
			streetAddress = _streetAddressSupplier.get();

			_streetAddressSupplier = null;
		}

		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;

		_streetAddressSupplier = null;
	}

	@JsonIgnore
	public void setStreetAddress(
		UnsafeSupplier<String, Exception> streetAddressUnsafeSupplier) {

		_streetAddressSupplier = () -> {
			try {
				return streetAddressUnsafeSupplier.get();
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
		description = "The full street address component, which may include house number, street name, P.O. box, and multi-line extended street address information."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String streetAddress;

	@JsonIgnore
	private Supplier<String> _streetAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of address, e.g., 'business', 'other', or 'personal'."
	)
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
		description = "The type of address, e.g., 'business', 'other', or 'personal'."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Address)) {
			return false;
		}

		Address address = (Address)object;

		return Objects.equals(toString(), address.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String country = getCountry();

		if (country != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"country\": ");

			sb.append("\"");

			sb.append(_escape(country));

			sb.append("\"");
		}

		String formatted = getFormatted();

		if (formatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formatted\": ");

			sb.append("\"");

			sb.append(_escape(formatted));

			sb.append("\"");
		}

		String locality = getLocality();

		if (locality != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locality\": ");

			sb.append("\"");

			sb.append(_escape(locality));

			sb.append("\"");
		}

		String postalCode = getPostalCode();

		if (postalCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalCode\": ");

			sb.append("\"");

			sb.append(_escape(postalCode));

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

		String region = getRegion();

		if (region != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"region\": ");

			sb.append("\"");

			sb.append(_escape(region));

			sb.append("\"");
		}

		String streetAddress = getStreetAddress();

		if (streetAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddress\": ");

			sb.append("\"");

			sb.append(_escape(streetAddress));

			sb.append("\"");
		}

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.Address",
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