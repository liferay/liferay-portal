/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.dto.v1_0;

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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Drew Brokke
 * @generated
 */
@Generated("")
@GraphQLName("Country")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"a2", "a3", "name", "number"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Country")
public class Country implements Serializable {

	public static Country toDTO(String json) {
		return ObjectMapperUtil.readValue(Country.class, json);
	}

	public static Country unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Country.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getA2() {
		if (_a2Supplier != null) {
			a2 = _a2Supplier.get();

			_a2Supplier = null;
		}

		return a2;
	}

	public void setA2(String a2) {
		this.a2 = a2;

		_a2Supplier = null;
	}

	@JsonIgnore
	public void setA2(UnsafeSupplier<String, Exception> a2UnsafeSupplier) {
		_a2Supplier = () -> {
			try {
				return a2UnsafeSupplier.get();
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
	protected String a2;

	@JsonIgnore
	private Supplier<String> _a2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getA3() {
		if (_a3Supplier != null) {
			a3 = _a3Supplier.get();

			_a3Supplier = null;
		}

		return a3;
	}

	public void setA3(String a3) {
		this.a3 = a3;

		_a3Supplier = null;
	}

	@JsonIgnore
	public void setA3(UnsafeSupplier<String, Exception> a3UnsafeSupplier) {
		_a3Supplier = () -> {
			try {
				return a3UnsafeSupplier.get();
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
	protected String a3;

	@JsonIgnore
	private Supplier<String> _a3Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getBillingAllowed() {
		if (_billingAllowedSupplier != null) {
			billingAllowed = _billingAllowedSupplier.get();

			_billingAllowedSupplier = null;
		}

		return billingAllowed;
	}

	public void setBillingAllowed(Boolean billingAllowed) {
		this.billingAllowed = billingAllowed;

		_billingAllowedSupplier = null;
	}

	@JsonIgnore
	public void setBillingAllowed(
		UnsafeSupplier<Boolean, Exception> billingAllowedUnsafeSupplier) {

		_billingAllowedSupplier = () -> {
			try {
				return billingAllowedUnsafeSupplier.get();
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
	protected Boolean billingAllowed;

	@JsonIgnore
	private Supplier<Boolean> _billingAllowedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getGroupFilterEnabled() {
		if (_groupFilterEnabledSupplier != null) {
			groupFilterEnabled = _groupFilterEnabledSupplier.get();

			_groupFilterEnabledSupplier = null;
		}

		return groupFilterEnabled;
	}

	public void setGroupFilterEnabled(Boolean groupFilterEnabled) {
		this.groupFilterEnabled = groupFilterEnabled;

		_groupFilterEnabledSupplier = null;
	}

	@JsonIgnore
	public void setGroupFilterEnabled(
		UnsafeSupplier<Boolean, Exception> groupFilterEnabledUnsafeSupplier) {

		_groupFilterEnabledSupplier = () -> {
			try {
				return groupFilterEnabledUnsafeSupplier.get();
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
	protected Boolean groupFilterEnabled;

	@JsonIgnore
	private Supplier<Boolean> _groupFilterEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getIdd() {
		if (_iddSupplier != null) {
			idd = _iddSupplier.get();

			_iddSupplier = null;
		}

		return idd;
	}

	public void setIdd(Integer idd) {
		this.idd = idd;

		_iddSupplier = null;
	}

	@JsonIgnore
	public void setIdd(UnsafeSupplier<Integer, Exception> iddUnsafeSupplier) {
		_iddSupplier = () -> {
			try {
				return iddUnsafeSupplier.get();
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
	protected Integer idd;

	@JsonIgnore
	private Supplier<Integer> _iddSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getNumber() {
		if (_numberSupplier != null) {
			number = _numberSupplier.get();

			_numberSupplier = null;
		}

		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;

		_numberSupplier = null;
	}

	@JsonIgnore
	public void setNumber(
		UnsafeSupplier<Integer, Exception> numberUnsafeSupplier) {

		_numberSupplier = () -> {
			try {
				return numberUnsafeSupplier.get();
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
	protected Integer number;

	@JsonIgnore
	private Supplier<Integer> _numberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPosition() {
		if (_positionSupplier != null) {
			position = _positionSupplier.get();

			_positionSupplier = null;
		}

		return position;
	}

	public void setPosition(Double position) {
		this.position = position;

		_positionSupplier = null;
	}

	@JsonIgnore
	public void setPosition(
		UnsafeSupplier<Double, Exception> positionUnsafeSupplier) {

		_positionSupplier = () -> {
			try {
				return positionUnsafeSupplier.get();
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
	protected Double position;

	@JsonIgnore
	private Supplier<Double> _positionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Region[] getRegions() {
		if (_regionsSupplier != null) {
			regions = _regionsSupplier.get();

			_regionsSupplier = null;
		}

		return regions;
	}

	public void setRegions(Region[] regions) {
		this.regions = regions;

		_regionsSupplier = null;
	}

	@JsonIgnore
	public void setRegions(
		UnsafeSupplier<Region[], Exception> regionsUnsafeSupplier) {

		_regionsSupplier = () -> {
			try {
				return regionsUnsafeSupplier.get();
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
	protected Region[] regions;

	@JsonIgnore
	private Supplier<Region[]> _regionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShippingAllowed() {
		if (_shippingAllowedSupplier != null) {
			shippingAllowed = _shippingAllowedSupplier.get();

			_shippingAllowedSupplier = null;
		}

		return shippingAllowed;
	}

	public void setShippingAllowed(Boolean shippingAllowed) {
		this.shippingAllowed = shippingAllowed;

		_shippingAllowedSupplier = null;
	}

	@JsonIgnore
	public void setShippingAllowed(
		UnsafeSupplier<Boolean, Exception> shippingAllowedUnsafeSupplier) {

		_shippingAllowedSupplier = () -> {
			try {
				return shippingAllowedUnsafeSupplier.get();
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
	protected Boolean shippingAllowed;

	@JsonIgnore
	private Supplier<Boolean> _shippingAllowedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSubjectToVAT() {
		if (_subjectToVATSupplier != null) {
			subjectToVAT = _subjectToVATSupplier.get();

			_subjectToVATSupplier = null;
		}

		return subjectToVAT;
	}

	public void setSubjectToVAT(Boolean subjectToVAT) {
		this.subjectToVAT = subjectToVAT;

		_subjectToVATSupplier = null;
	}

	@JsonIgnore
	public void setSubjectToVAT(
		UnsafeSupplier<Boolean, Exception> subjectToVATUnsafeSupplier) {

		_subjectToVATSupplier = () -> {
			try {
				return subjectToVATUnsafeSupplier.get();
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
	protected Boolean subjectToVAT;

	@JsonIgnore
	private Supplier<Boolean> _subjectToVATSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getTitle_i18n() {
		if (_title_i18nSupplier != null) {
			title_i18n = _title_i18nSupplier.get();

			_title_i18nSupplier = null;
		}

		return title_i18n;
	}

	public void setTitle_i18n(Map<String, String> title_i18n) {
		this.title_i18n = title_i18n;

		_title_i18nSupplier = null;
	}

	@JsonIgnore
	public void setTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			title_i18nUnsafeSupplier) {

		_title_i18nSupplier = () -> {
			try {
				return title_i18nUnsafeSupplier.get();
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
	protected Map<String, String> title_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _title_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getZipRequired() {
		if (_zipRequiredSupplier != null) {
			zipRequired = _zipRequiredSupplier.get();

			_zipRequiredSupplier = null;
		}

		return zipRequired;
	}

	public void setZipRequired(Boolean zipRequired) {
		this.zipRequired = zipRequired;

		_zipRequiredSupplier = null;
	}

	@JsonIgnore
	public void setZipRequired(
		UnsafeSupplier<Boolean, Exception> zipRequiredUnsafeSupplier) {

		_zipRequiredSupplier = () -> {
			try {
				return zipRequiredUnsafeSupplier.get();
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
	protected Boolean zipRequired;

	@JsonIgnore
	private Supplier<Boolean> _zipRequiredSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Country)) {
			return false;
		}

		Country country = (Country)object;

		return Objects.equals(toString(), country.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String a2 = getA2();

		if (a2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"a2\": ");

			sb.append("\"");

			sb.append(_escape(a2));

			sb.append("\"");
		}

		String a3 = getA3();

		if (a3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"a3\": ");

			sb.append("\"");

			sb.append(_escape(a3));

			sb.append("\"");
		}

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Boolean billingAllowed = getBillingAllowed();

		if (billingAllowed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAllowed\": ");

			sb.append(billingAllowed);
		}

		Boolean groupFilterEnabled = getGroupFilterEnabled();

		if (groupFilterEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupFilterEnabled\": ");

			sb.append(groupFilterEnabled);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Integer idd = getIdd();

		if (idd != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idd\": ");

			sb.append(idd);
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

		Integer number = getNumber();

		if (number != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"number\": ");

			sb.append(number);
		}

		Double position = getPosition();

		if (position != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(position);
		}

		Region[] regions = getRegions();

		if (regions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regions\": ");

			sb.append("[");

			for (int i = 0; i < regions.length; i++) {
				sb.append(String.valueOf(regions[i]));

				if ((i + 1) < regions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean shippingAllowed = getShippingAllowed();

		if (shippingAllowed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAllowed\": ");

			sb.append(shippingAllowed);
		}

		Boolean subjectToVAT = getSubjectToVAT();

		if (subjectToVAT != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subjectToVAT\": ");

			sb.append(subjectToVAT);
		}

		Map<String, String> title_i18n = getTitle_i18n();

		if (title_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(title_i18n));
		}

		Boolean zipRequired = getZipRequired();

		if (zipRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zipRequired\": ");

			sb.append(zipRequired);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.address.dto.v1_0.Country",
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