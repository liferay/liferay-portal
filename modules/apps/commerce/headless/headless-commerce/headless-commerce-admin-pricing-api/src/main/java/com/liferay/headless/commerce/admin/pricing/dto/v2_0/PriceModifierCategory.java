/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.dto.v2_0;

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
@GraphQLName("PriceModifierCategory")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"categoryId", "priceModifierId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceModifierCategory")
public class PriceModifierCategory implements Serializable {

	public static PriceModifierCategory toDTO(String json) {
		return ObjectMapperUtil.readValue(PriceModifierCategory.class, json);
	}

	public static PriceModifierCategory unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PriceModifierCategory.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Category getCategory() {
		if (_categorySupplier != null) {
			category = _categorySupplier.get();

			_categorySupplier = null;
		}

		return category;
	}

	public void setCategory(Category category) {
		this.category = category;

		_categorySupplier = null;
	}

	@JsonIgnore
	public void setCategory(
		UnsafeSupplier<Category, Exception> categoryUnsafeSupplier) {

		_categorySupplier = () -> {
			try {
				return categoryUnsafeSupplier.get();
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
	protected Category category;

	@JsonIgnore
	private Supplier<Category> _categorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getCategoryExternalReferenceCode() {
		if (_categoryExternalReferenceCodeSupplier != null) {
			categoryExternalReferenceCode =
				_categoryExternalReferenceCodeSupplier.get();

			_categoryExternalReferenceCodeSupplier = null;
		}

		return categoryExternalReferenceCode;
	}

	public void setCategoryExternalReferenceCode(
		String categoryExternalReferenceCode) {

		this.categoryExternalReferenceCode = categoryExternalReferenceCode;

		_categoryExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCategoryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			categoryExternalReferenceCodeUnsafeSupplier) {

		_categoryExternalReferenceCodeSupplier = () -> {
			try {
				return categoryExternalReferenceCodeUnsafeSupplier.get();
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
	protected String categoryExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _categoryExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getCategoryId() {
		if (_categoryIdSupplier != null) {
			categoryId = _categoryIdSupplier.get();

			_categoryIdSupplier = null;
		}

		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;

		_categoryIdSupplier = null;
	}

	@JsonIgnore
	public void setCategoryId(
		UnsafeSupplier<Long, Exception> categoryIdUnsafeSupplier) {

		_categoryIdSupplier = () -> {
			try {
				return categoryIdUnsafeSupplier.get();
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
	protected Long categoryId;

	@JsonIgnore
	private Supplier<Long> _categoryIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getPriceModifierCategoryId() {
		if (_priceModifierCategoryIdSupplier != null) {
			priceModifierCategoryId = _priceModifierCategoryIdSupplier.get();

			_priceModifierCategoryIdSupplier = null;
		}

		return priceModifierCategoryId;
	}

	public void setPriceModifierCategoryId(Long priceModifierCategoryId) {
		this.priceModifierCategoryId = priceModifierCategoryId;

		_priceModifierCategoryIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierCategoryId(
		UnsafeSupplier<Long, Exception> priceModifierCategoryIdUnsafeSupplier) {

		_priceModifierCategoryIdSupplier = () -> {
			try {
				return priceModifierCategoryIdUnsafeSupplier.get();
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
	protected Long priceModifierCategoryId;

	@JsonIgnore
	private Supplier<Long> _priceModifierCategoryIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getPriceModifierExternalReferenceCode() {
		if (_priceModifierExternalReferenceCodeSupplier != null) {
			priceModifierExternalReferenceCode =
				_priceModifierExternalReferenceCodeSupplier.get();

			_priceModifierExternalReferenceCodeSupplier = null;
		}

		return priceModifierExternalReferenceCode;
	}

	public void setPriceModifierExternalReferenceCode(
		String priceModifierExternalReferenceCode) {

		this.priceModifierExternalReferenceCode =
			priceModifierExternalReferenceCode;

		_priceModifierExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			priceModifierExternalReferenceCodeUnsafeSupplier) {

		_priceModifierExternalReferenceCodeSupplier = () -> {
			try {
				return priceModifierExternalReferenceCodeUnsafeSupplier.get();
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
	protected String priceModifierExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _priceModifierExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getPriceModifierId() {
		if (_priceModifierIdSupplier != null) {
			priceModifierId = _priceModifierIdSupplier.get();

			_priceModifierIdSupplier = null;
		}

		return priceModifierId;
	}

	public void setPriceModifierId(Long priceModifierId) {
		this.priceModifierId = priceModifierId;

		_priceModifierIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierId(
		UnsafeSupplier<Long, Exception> priceModifierIdUnsafeSupplier) {

		_priceModifierIdSupplier = () -> {
			try {
				return priceModifierIdUnsafeSupplier.get();
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
	protected Long priceModifierId;

	@JsonIgnore
	private Supplier<Long> _priceModifierIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceModifierCategory)) {
			return false;
		}

		PriceModifierCategory priceModifierCategory =
			(PriceModifierCategory)object;

		return Objects.equals(toString(), priceModifierCategory.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Category category = getCategory();

		if (category != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"category\": ");

			sb.append(String.valueOf(category));
		}

		String categoryExternalReferenceCode =
			getCategoryExternalReferenceCode();

		if (categoryExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(categoryExternalReferenceCode));

			sb.append("\"");
		}

		Long categoryId = getCategoryId();

		if (categoryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryId\": ");

			sb.append(categoryId);
		}

		Long priceModifierCategoryId = getPriceModifierCategoryId();

		if (priceModifierCategoryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierCategoryId\": ");

			sb.append(priceModifierCategoryId);
		}

		String priceModifierExternalReferenceCode =
			getPriceModifierExternalReferenceCode();

		if (priceModifierExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceModifierExternalReferenceCode));

			sb.append("\"");
		}

		Long priceModifierId = getPriceModifierId();

		if (priceModifierId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierId\": ");

			sb.append(priceModifierId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierCategory",
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