/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("ProductTaxConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductTaxConfiguration")
public class ProductTaxConfiguration implements Serializable {

	public static ProductTaxConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductTaxConfiguration.class, json);
	}

	public static ProductTaxConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductTaxConfiguration.class, json);
	}

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

	@io.swagger.v3.oas.annotations.media.Schema(example = "taxCategoryName")
	public String getTaxCategory() {
		if (_taxCategorySupplier != null) {
			taxCategory = _taxCategorySupplier.get();

			_taxCategorySupplier = null;
		}

		return taxCategory;
	}

	public void setTaxCategory(String taxCategory) {
		this.taxCategory = taxCategory;

		_taxCategorySupplier = null;
	}

	@JsonIgnore
	public void setTaxCategory(
		UnsafeSupplier<String, Exception> taxCategoryUnsafeSupplier) {

		_taxCategorySupplier = () -> {
			try {
				return taxCategoryUnsafeSupplier.get();
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
	protected String taxCategory;

	@JsonIgnore
	private Supplier<String> _taxCategorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getTaxable() {
		if (_taxableSupplier != null) {
			taxable = _taxableSupplier.get();

			_taxableSupplier = null;
		}

		return taxable;
	}

	public void setTaxable(Boolean taxable) {
		this.taxable = taxable;

		_taxableSupplier = null;
	}

	@JsonIgnore
	public void setTaxable(
		UnsafeSupplier<Boolean, Exception> taxableUnsafeSupplier) {

		_taxableSupplier = () -> {
			try {
				return taxableUnsafeSupplier.get();
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
	protected Boolean taxable;

	@JsonIgnore
	private Supplier<Boolean> _taxableSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductTaxConfiguration)) {
			return false;
		}

		ProductTaxConfiguration productTaxConfiguration =
			(ProductTaxConfiguration)object;

		return Objects.equals(toString(), productTaxConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String taxCategory = getTaxCategory();

		if (taxCategory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxCategory\": ");

			sb.append("\"");

			sb.append(_escape(taxCategory));

			sb.append("\"");
		}

		Boolean taxable = getTaxable();

		if (taxable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxable\": ");

			sb.append(taxable);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration",
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