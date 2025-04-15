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
	description = "Represents a mailing address. This follows the [`PostalAddress`](https://www.schema.org/PostalAddress) specification.",
	value = "PostalAddress"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PostalAddress")
public class PostalAddress implements Serializable {

	public static PostalAddress toDTO(String json) {
		return ObjectMapperUtil.readValue(PostalAddress.class, json);
	}

	public static PostalAddress unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PostalAddress.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's country (e.g., USA)."
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

	@GraphQLField(description = "The address's country (e.g., USA).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressCountry;

	@JsonIgnore
	private Supplier<String> _addressCountrySupplier;

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
		description = "The address's locality (e.g., city)."
	)
	public String getAddressLocality() {
		if (_addressLocalitySupplier != null) {
			addressLocality = _addressLocalitySupplier.get();

			_addressLocalitySupplier = null;
		}

		return addressLocality;
	}

	public void setAddressLocality(String addressLocality) {
		this.addressLocality = addressLocality;

		_addressLocalitySupplier = null;
	}

	@JsonIgnore
	public void setAddressLocality(
		UnsafeSupplier<String, Exception> addressLocalityUnsafeSupplier) {

		_addressLocalitySupplier = () -> {
			try {
				return addressLocalityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The address's locality (e.g., city).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressLocality;

	@JsonIgnore
	private Supplier<String> _addressLocalitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's region (e.g., state)."
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

	@GraphQLField(description = "The address's region (e.g., state).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressRegion;

	@JsonIgnore
	private Supplier<String> _addressRegionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's subtype."
	)
	public String getAddressSubtype() {
		if (_addressSubtypeSupplier != null) {
			addressSubtype = _addressSubtypeSupplier.get();

			_addressSubtypeSupplier = null;
		}

		return addressSubtype;
	}

	public void setAddressSubtype(String addressSubtype) {
		this.addressSubtype = addressSubtype;

		_addressSubtypeSupplier = null;
	}

	@JsonIgnore
	public void setAddressSubtype(
		UnsafeSupplier<String, Exception> addressSubtypeUnsafeSupplier) {

		_addressSubtypeSupplier = () -> {
			try {
				return addressSubtypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The address's subtype.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressSubtype;

	@JsonIgnore
	private Supplier<String> _addressSubtypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's type."
	)
	public String getAddressType() {
		if (_addressTypeSupplier != null) {
			addressType = _addressTypeSupplier.get();

			_addressTypeSupplier = null;
		}

		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;

		_addressTypeSupplier = null;
	}

	@JsonIgnore
	public void setAddressType(
		UnsafeSupplier<String, Exception> addressTypeUnsafeSupplier) {

		_addressTypeSupplier = () -> {
			try {
				return addressTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The address's type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String addressType;

	@JsonIgnore
	private Supplier<String> _addressTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The optional external key of this address."
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

	@GraphQLField(description = "The optional external key of this address.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's ID."
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

	@GraphQLField(description = "The address's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's name."
	)
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

	@GraphQLField(description = "The address's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The phone number."
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

	@GraphQLField(description = "The phone number.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String phoneNumber;

	@JsonIgnore
	private Supplier<String> _phoneNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The address's postal code (e.g., zip code)."
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

	@GraphQLField(description = "The address's postal code (e.g., zip code).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String postalCode;

	@JsonIgnore
	private Supplier<String> _postalCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that identifies whether this is the main address of the user/organization."
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
		description = "A flag that identifies whether this is the main address of the user/organization."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean primary;

	@JsonIgnore
	private Supplier<Boolean> _primarySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The street address's first line (e.g., 1600 Amphitheatre Pkwy.)."
	)
	public String getStreetAddressLine1() {
		if (_streetAddressLine1Supplier != null) {
			streetAddressLine1 = _streetAddressLine1Supplier.get();

			_streetAddressLine1Supplier = null;
		}

		return streetAddressLine1;
	}

	public void setStreetAddressLine1(String streetAddressLine1) {
		this.streetAddressLine1 = streetAddressLine1;

		_streetAddressLine1Supplier = null;
	}

	@JsonIgnore
	public void setStreetAddressLine1(
		UnsafeSupplier<String, Exception> streetAddressLine1UnsafeSupplier) {

		_streetAddressLine1Supplier = () -> {
			try {
				return streetAddressLine1UnsafeSupplier.get();
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
		description = "The street address's first line (e.g., 1600 Amphitheatre Pkwy.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String streetAddressLine1;

	@JsonIgnore
	private Supplier<String> _streetAddressLine1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The street address's second line."
	)
	public String getStreetAddressLine2() {
		if (_streetAddressLine2Supplier != null) {
			streetAddressLine2 = _streetAddressLine2Supplier.get();

			_streetAddressLine2Supplier = null;
		}

		return streetAddressLine2;
	}

	public void setStreetAddressLine2(String streetAddressLine2) {
		this.streetAddressLine2 = streetAddressLine2;

		_streetAddressLine2Supplier = null;
	}

	@JsonIgnore
	public void setStreetAddressLine2(
		UnsafeSupplier<String, Exception> streetAddressLine2UnsafeSupplier) {

		_streetAddressLine2Supplier = () -> {
			try {
				return streetAddressLine2UnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The street address's second line.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String streetAddressLine2;

	@JsonIgnore
	private Supplier<String> _streetAddressLine2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The street address's third line."
	)
	public String getStreetAddressLine3() {
		if (_streetAddressLine3Supplier != null) {
			streetAddressLine3 = _streetAddressLine3Supplier.get();

			_streetAddressLine3Supplier = null;
		}

		return streetAddressLine3;
	}

	public void setStreetAddressLine3(String streetAddressLine3) {
		this.streetAddressLine3 = streetAddressLine3;

		_streetAddressLine3Supplier = null;
	}

	@JsonIgnore
	public void setStreetAddressLine3(
		UnsafeSupplier<String, Exception> streetAddressLine3UnsafeSupplier) {

		_streetAddressLine3Supplier = () -> {
			try {
				return streetAddressLine3UnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The street address's third line.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String streetAddressLine3;

	@JsonIgnore
	private Supplier<String> _streetAddressLine3Supplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PostalAddress)) {
			return false;
		}

		PostalAddress postalAddress = (PostalAddress)object;

		return Objects.equals(toString(), postalAddress.toString());
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

		Map<String, String> addressCountry_i18n = getAddressCountry_i18n();

		if (addressCountry_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry_i18n\": ");

			sb.append(_toJSON(addressCountry_i18n));
		}

		String addressLocality = getAddressLocality();

		if (addressLocality != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressLocality\": ");

			sb.append("\"");

			sb.append(_escape(addressLocality));

			sb.append("\"");
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

		String addressSubtype = getAddressSubtype();

		if (addressSubtype != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressSubtype\": ");

			sb.append("\"");

			sb.append(_escape(addressSubtype));

			sb.append("\"");
		}

		String addressType = getAddressType();

		if (addressType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressType\": ");

			sb.append("\"");

			sb.append(_escape(addressType));

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

		String streetAddressLine1 = getStreetAddressLine1();

		if (streetAddressLine1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddressLine1\": ");

			sb.append("\"");

			sb.append(_escape(streetAddressLine1));

			sb.append("\"");
		}

		String streetAddressLine2 = getStreetAddressLine2();

		if (streetAddressLine2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddressLine2\": ");

			sb.append("\"");

			sb.append(_escape(streetAddressLine2));

			sb.append("\"");
		}

		String streetAddressLine3 = getStreetAddressLine3();

		if (streetAddressLine3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddressLine3\": ");

			sb.append("\"");

			sb.append(_escape(streetAddressLine3));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.PostalAddress",
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