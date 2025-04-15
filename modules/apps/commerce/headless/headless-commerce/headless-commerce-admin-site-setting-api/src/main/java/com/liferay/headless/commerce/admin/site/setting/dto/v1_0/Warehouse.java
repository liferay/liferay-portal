/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.dto.v1_0;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("Warehouse")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"commerceCountryId", "name"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Warehouse")
public class Warehouse implements Serializable {

	public static Warehouse toDTO(String json) {
		return ObjectMapperUtil.readValue(Warehouse.class, json);
	}

	public static Warehouse unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Warehouse.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

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
	protected String city;

	@JsonIgnore
	private Supplier<String> _citySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getCommerceCountryId() {
		if (_commerceCountryIdSupplier != null) {
			commerceCountryId = _commerceCountryIdSupplier.get();

			_commerceCountryIdSupplier = null;
		}

		return commerceCountryId;
	}

	public void setCommerceCountryId(Long commerceCountryId) {
		this.commerceCountryId = commerceCountryId;

		_commerceCountryIdSupplier = null;
	}

	@JsonIgnore
	public void setCommerceCountryId(
		UnsafeSupplier<Long, Exception> commerceCountryIdUnsafeSupplier) {

		_commerceCountryIdSupplier = () -> {
			try {
				return commerceCountryIdUnsafeSupplier.get();
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
	@NotNull
	protected Long commerceCountryId;

	@JsonIgnore
	private Supplier<Long> _commerceCountryIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30234")
	public Long getCommerceRegionId() {
		if (_commerceRegionIdSupplier != null) {
			commerceRegionId = _commerceRegionIdSupplier.get();

			_commerceRegionIdSupplier = null;
		}

		return commerceRegionId;
	}

	public void setCommerceRegionId(Long commerceRegionId) {
		this.commerceRegionId = commerceRegionId;

		_commerceRegionIdSupplier = null;
	}

	@JsonIgnore
	public void setCommerceRegionId(
		UnsafeSupplier<Long, Exception> commerceRegionIdUnsafeSupplier) {

		_commerceRegionIdSupplier = () -> {
			try {
				return commerceRegionIdUnsafeSupplier.get();
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
	protected Long commerceRegionId;

	@JsonIgnore
	private Supplier<Long> _commerceRegionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Warehouse Description US, hr_HR=Warehouse Description HR, hu_HU=Warehouse Description HU}"
	)
	@Valid
	public Map<String, String> getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

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
	protected Map<String, String> description;

	@JsonIgnore
	private Supplier<Map<String, String>> _descriptionSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "23130")
	public Long getGroupId() {
		if (_groupIdSupplier != null) {
			groupId = _groupIdSupplier.get();

			_groupIdSupplier = null;
		}

		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;

		_groupIdSupplier = null;
	}

	@JsonIgnore
	public void setGroupId(
		UnsafeSupplier<Long, Exception> groupIdUnsafeSupplier) {

		_groupIdSupplier = () -> {
			try {
				return groupIdUnsafeSupplier.get();
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
	protected Long groupId;

	@JsonIgnore
	private Supplier<Long> _groupIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "0")
	@Valid
	public Number getMvccVersion() {
		if (_mvccVersionSupplier != null) {
			mvccVersion = _mvccVersionSupplier.get();

			_mvccVersionSupplier = null;
		}

		return mvccVersion;
	}

	public void setMvccVersion(Number mvccVersion) {
		this.mvccVersion = mvccVersion;

		_mvccVersionSupplier = null;
	}

	@JsonIgnore
	public void setMvccVersion(
		UnsafeSupplier<Number, Exception> mvccVersionUnsafeSupplier) {

		_mvccVersionSupplier = () -> {
			try {
				return mvccVersionUnsafeSupplier.get();
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
	protected Number mvccVersion;

	@JsonIgnore
	private Supplier<Number> _mvccVersionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Warehouse Name US, hr_HR=Warehouse Name HR, hu_HU=Warehouse Name HU}"
	)
	@Valid
	public Map<String, String> getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

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
	@NotNull
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean primary;

	@JsonIgnore
	private Supplier<Boolean> _primarySupplier;

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

		if (!(object instanceof Warehouse)) {
			return false;
		}

		Warehouse warehouse = (Warehouse)object;

		return Objects.equals(toString(), warehouse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

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

		Long commerceCountryId = getCommerceCountryId();

		if (commerceCountryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commerceCountryId\": ");

			sb.append(commerceCountryId);
		}

		Long commerceRegionId = getCommerceRegionId();

		if (commerceRegionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commerceRegionId\": ");

			sb.append(commerceRegionId);
		}

		Map<String, String> description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(description));
		}

		Long groupId = getGroupId();

		if (groupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupId\": ");

			sb.append(groupId);
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

		Number mvccVersion = getMvccVersion();

		if (mvccVersion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mvccVersion\": ");

			sb.append(mvccVersion);
		}

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
		}

		Boolean primary = getPrimary();

		if (primary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(primary);
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
		defaultValue = "com.liferay.headless.commerce.admin.site.setting.dto.v1_0.Warehouse",
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