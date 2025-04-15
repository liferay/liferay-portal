/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.dto.v1_0;

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
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("BillingAddress")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"city", "countryISOCode", "name", "street1"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BillingAddress")
public class BillingAddress implements Serializable {

	public static BillingAddress toDTO(String json) {
		return ObjectMapperUtil.readValue(BillingAddress.class, json);
	}

	public static BillingAddress unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BillingAddress.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "Diamond Bar")
	public String getCity() {
		if (_citySupplier != null) {
			city = _citySupplier.get();

			_citySupplier = null;
		}

		return city;
	}

	public void setCity(String city) {
		this.city = city;

		_citySupplier = null;
	}

	@JsonIgnore
	public void setCity(UnsafeSupplier<String, Exception> cityUnsafeSupplier) {
		_citySupplier = () -> {
			try {
				return cityUnsafeSupplier.get();
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
	@NotEmpty
	protected String city;

	@JsonIgnore
	private Supplier<String> _citySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "US")
	public String getCountryISOCode() {
		if (_countryISOCodeSupplier != null) {
			countryISOCode = _countryISOCodeSupplier.get();

			_countryISOCodeSupplier = null;
		}

		return countryISOCode;
	}

	public void setCountryISOCode(String countryISOCode) {
		this.countryISOCode = countryISOCode;

		_countryISOCodeSupplier = null;
	}

	@JsonIgnore
	public void setCountryISOCode(
		UnsafeSupplier<String, Exception> countryISOCodeUnsafeSupplier) {

		_countryISOCodeSupplier = () -> {
			try {
				return countryISOCodeUnsafeSupplier.get();
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
	@NotEmpty
	protected String countryISOCode;

	@JsonIgnore
	private Supplier<String> _countryISOCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "right stairs, first room on the left"
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "31130")
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "33.9976884")
	public Double getLatitude() {
		if (_latitudeSupplier != null) {
			latitude = _latitudeSupplier.get();

			_latitudeSupplier = null;
		}

		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;

		_latitudeSupplier = null;
	}

	@JsonIgnore
	public void setLatitude(
		UnsafeSupplier<Double, Exception> latitudeUnsafeSupplier) {

		_latitudeSupplier = () -> {
			try {
				return latitudeUnsafeSupplier.get();
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
	protected Double latitude;

	@JsonIgnore
	private Supplier<Double> _latitudeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "-117.8144595")
	public Double getLongitude() {
		if (_longitudeSupplier != null) {
			longitude = _longitudeSupplier.get();

			_longitudeSupplier = null;
		}

		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;

		_longitudeSupplier = null;
	}

	@JsonIgnore
	public void setLongitude(
		UnsafeSupplier<Double, Exception> longitudeUnsafeSupplier) {

		_longitudeSupplier = () -> {
			try {
				return longitudeUnsafeSupplier.get();
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
	protected Double longitude;

	@JsonIgnore
	private Supplier<Double> _longitudeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Alessio Antonio Rendina"
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "(123) 456 7890")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String phoneNumber;

	@JsonIgnore
	private Supplier<String> _phoneNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "CA")
	public String getRegionISOCode() {
		if (_regionISOCodeSupplier != null) {
			regionISOCode = _regionISOCodeSupplier.get();

			_regionISOCodeSupplier = null;
		}

		return regionISOCode;
	}

	public void setRegionISOCode(String regionISOCode) {
		this.regionISOCode = regionISOCode;

		_regionISOCodeSupplier = null;
	}

	@JsonIgnore
	public void setRegionISOCode(
		UnsafeSupplier<String, Exception> regionISOCodeUnsafeSupplier) {

		_regionISOCodeSupplier = () -> {
			try {
				return regionISOCodeUnsafeSupplier.get();
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
	protected String regionISOCode;

	@JsonIgnore
	private Supplier<String> _regionISOCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "1400 Montefino Ave")
	public String getStreet1() {
		if (_street1Supplier != null) {
			street1 = _street1Supplier.get();

			_street1Supplier = null;
		}

		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;

		_street1Supplier = null;
	}

	@JsonIgnore
	public void setStreet1(
		UnsafeSupplier<String, Exception> street1UnsafeSupplier) {

		_street1Supplier = () -> {
			try {
				return street1UnsafeSupplier.get();
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
	@NotEmpty
	protected String street1;

	@JsonIgnore
	private Supplier<String> _street1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "1st floor")
	public String getStreet2() {
		if (_street2Supplier != null) {
			street2 = _street2Supplier.get();

			_street2Supplier = null;
		}

		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;

		_street2Supplier = null;
	}

	@JsonIgnore
	public void setStreet2(
		UnsafeSupplier<String, Exception> street2UnsafeSupplier) {

		_street2Supplier = () -> {
			try {
				return street2UnsafeSupplier.get();
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
	protected String street2;

	@JsonIgnore
	private Supplier<String> _street2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "suite 200")
	public String getStreet3() {
		if (_street3Supplier != null) {
			street3 = _street3Supplier.get();

			_street3Supplier = null;
		}

		return street3;
	}

	public void setStreet3(String street3) {
		this.street3 = street3;

		_street3Supplier = null;
	}

	@JsonIgnore
	public void setStreet3(
		UnsafeSupplier<String, Exception> street3UnsafeSupplier) {

		_street3Supplier = () -> {
			try {
				return street3UnsafeSupplier.get();
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
	protected String street3;

	@JsonIgnore
	private Supplier<String> _street3Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "box")
	public String getSubtype() {
		if (_subtypeSupplier != null) {
			subtype = _subtypeSupplier.get();

			_subtypeSupplier = null;
		}

		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;

		_subtypeSupplier = null;
	}

	@JsonIgnore
	public void setSubtype(
		UnsafeSupplier<String, Exception> subtypeUnsafeSupplier) {

		_subtypeSupplier = () -> {
			try {
				return subtypeUnsafeSupplier.get();
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
	protected String subtype;

	@JsonIgnore
	private Supplier<String> _subtypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "353246836565")
	public String getVatNumber() {
		if (_vatNumberSupplier != null) {
			vatNumber = _vatNumberSupplier.get();

			_vatNumberSupplier = null;
		}

		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;

		_vatNumberSupplier = null;
	}

	@JsonIgnore
	public void setVatNumber(
		UnsafeSupplier<String, Exception> vatNumberUnsafeSupplier) {

		_vatNumberSupplier = () -> {
			try {
				return vatNumberUnsafeSupplier.get();
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
	protected String vatNumber;

	@JsonIgnore
	private Supplier<String> _vatNumberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "91765")
	public String getZip() {
		if (_zipSupplier != null) {
			zip = _zipSupplier.get();

			_zipSupplier = null;
		}

		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;

		_zipSupplier = null;
	}

	@JsonIgnore
	public void setZip(UnsafeSupplier<String, Exception> zipUnsafeSupplier) {
		_zipSupplier = () -> {
			try {
				return zipUnsafeSupplier.get();
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
	protected String zip;

	@JsonIgnore
	private Supplier<String> _zipSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BillingAddress)) {
			return false;
		}

		BillingAddress billingAddress = (BillingAddress)object;

		return Objects.equals(toString(), billingAddress.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String city = getCity();

		if (city != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"city\": ");

			sb.append("\"");

			sb.append(_escape(city));

			sb.append("\"");
		}

		String countryISOCode = getCountryISOCode();

		if (countryISOCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryISOCode\": ");

			sb.append("\"");

			sb.append(_escape(countryISOCode));

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

		Double latitude = getLatitude();

		if (latitude != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"latitude\": ");

			sb.append(latitude);
		}

		Double longitude = getLongitude();

		if (longitude != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"longitude\": ");

			sb.append(longitude);
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

		String regionISOCode = getRegionISOCode();

		if (regionISOCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regionISOCode\": ");

			sb.append("\"");

			sb.append(_escape(regionISOCode));

			sb.append("\"");
		}

		String street1 = getStreet1();

		if (street1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street1\": ");

			sb.append("\"");

			sb.append(_escape(street1));

			sb.append("\"");
		}

		String street2 = getStreet2();

		if (street2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street2\": ");

			sb.append("\"");

			sb.append(_escape(street2));

			sb.append("\"");
		}

		String street3 = getStreet3();

		if (street3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street3\": ");

			sb.append("\"");

			sb.append(_escape(street3));

			sb.append("\"");
		}

		String subtype = getSubtype();

		if (subtype != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtype\": ");

			sb.append("\"");

			sb.append(_escape(subtype));

			sb.append("\"");
		}

		String vatNumber = getVatNumber();

		if (vatNumber != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"vatNumber\": ");

			sb.append("\"");

			sb.append(_escape(vatNumber));

			sb.append("\"");
		}

		String zip = getZip();

		if (zip != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zip\": ");

			sb.append("\"");

			sb.append(_escape(zip));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.BillingAddress",
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