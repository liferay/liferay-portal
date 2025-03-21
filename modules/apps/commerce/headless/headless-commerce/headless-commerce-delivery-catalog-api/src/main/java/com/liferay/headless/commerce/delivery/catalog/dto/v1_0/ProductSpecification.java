/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.dto.v1_0;

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

import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("ProductSpecification")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductSpecification")
public class ProductSpecification implements Serializable {

	public static ProductSpecification toDTO(String json) {
		return ObjectMapperUtil.readValue(ProductSpecification.class, json);
	}

	public static ProductSpecification unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductSpecification.class, json);
	}

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

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30129")
	public Long getOptionCategoryId() {
		if (_optionCategoryIdSupplier != null) {
			optionCategoryId = _optionCategoryIdSupplier.get();

			_optionCategoryIdSupplier = null;
		}

		return optionCategoryId;
	}

	public void setOptionCategoryId(Long optionCategoryId) {
		this.optionCategoryId = optionCategoryId;

		_optionCategoryIdSupplier = null;
	}

	@JsonIgnore
	public void setOptionCategoryId(
		UnsafeSupplier<Long, Exception> optionCategoryIdUnsafeSupplier) {

		_optionCategoryIdSupplier = () -> {
			try {
				return optionCategoryIdUnsafeSupplier.get();
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
	protected Long optionCategoryId;

	@JsonIgnore
	private Supplier<Long> _optionCategoryIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1.2")
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
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
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30129")
	public Long getProductId() {
		if (_productIdSupplier != null) {
			productId = _productIdSupplier.get();

			_productIdSupplier = null;
		}

		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;

		_productIdSupplier = null;
	}

	@JsonIgnore
	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		_productIdSupplier = () -> {
			try {
				return productIdUnsafeSupplier.get();
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
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSpecificationGroupKey() {
		if (_specificationGroupKeySupplier != null) {
			specificationGroupKey = _specificationGroupKeySupplier.get();

			_specificationGroupKeySupplier = null;
		}

		return specificationGroupKey;
	}

	public void setSpecificationGroupKey(String specificationGroupKey) {
		this.specificationGroupKey = specificationGroupKey;

		_specificationGroupKeySupplier = null;
	}

	@JsonIgnore
	public void setSpecificationGroupKey(
		UnsafeSupplier<String, Exception> specificationGroupKeyUnsafeSupplier) {

		_specificationGroupKeySupplier = () -> {
			try {
				return specificationGroupKeyUnsafeSupplier.get();
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
	protected String specificationGroupKey;

	@JsonIgnore
	private Supplier<String> _specificationGroupKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSpecificationGroupTitle() {
		if (_specificationGroupTitleSupplier != null) {
			specificationGroupTitle = _specificationGroupTitleSupplier.get();

			_specificationGroupTitleSupplier = null;
		}

		return specificationGroupTitle;
	}

	public void setSpecificationGroupTitle(String specificationGroupTitle) {
		this.specificationGroupTitle = specificationGroupTitle;

		_specificationGroupTitleSupplier = null;
	}

	@JsonIgnore
	public void setSpecificationGroupTitle(
		UnsafeSupplier<String, Exception>
			specificationGroupTitleUnsafeSupplier) {

		_specificationGroupTitleSupplier = () -> {
			try {
				return specificationGroupTitleUnsafeSupplier.get();
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
	protected String specificationGroupTitle;

	@JsonIgnore
	private Supplier<String> _specificationGroupTitleSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30129")
	public Long getSpecificationId() {
		if (_specificationIdSupplier != null) {
			specificationId = _specificationIdSupplier.get();

			_specificationIdSupplier = null;
		}

		return specificationId;
	}

	public void setSpecificationId(Long specificationId) {
		this.specificationId = specificationId;

		_specificationIdSupplier = null;
	}

	@JsonIgnore
	public void setSpecificationId(
		UnsafeSupplier<Long, Exception> specificationIdUnsafeSupplier) {

		_specificationIdSupplier = () -> {
			try {
				return specificationIdUnsafeSupplier.get();
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
	protected Long specificationId;

	@JsonIgnore
	private Supplier<Long> _specificationIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "specification-key")
	public String getSpecificationKey() {
		if (_specificationKeySupplier != null) {
			specificationKey = _specificationKeySupplier.get();

			_specificationKeySupplier = null;
		}

		return specificationKey;
	}

	public void setSpecificationKey(String specificationKey) {
		this.specificationKey = specificationKey;

		_specificationKeySupplier = null;
	}

	@JsonIgnore
	public void setSpecificationKey(
		UnsafeSupplier<String, Exception> specificationKeyUnsafeSupplier) {

		_specificationKeySupplier = () -> {
			try {
				return specificationKeyUnsafeSupplier.get();
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
	protected String specificationKey;

	@JsonIgnore
	private Supplier<String> _specificationKeySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1.1")
	public Double getSpecificationPriority() {
		if (_specificationPrioritySupplier != null) {
			specificationPriority = _specificationPrioritySupplier.get();

			_specificationPrioritySupplier = null;
		}

		return specificationPriority;
	}

	public void setSpecificationPriority(Double specificationPriority) {
		this.specificationPriority = specificationPriority;

		_specificationPrioritySupplier = null;
	}

	@JsonIgnore
	public void setSpecificationPriority(
		UnsafeSupplier<Double, Exception> specificationPriorityUnsafeSupplier) {

		_specificationPrioritySupplier = () -> {
			try {
				return specificationPriorityUnsafeSupplier.get();
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
	protected Double specificationPriority;

	@JsonIgnore
	private Supplier<Double> _specificationPrioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSpecificationTitle() {
		if (_specificationTitleSupplier != null) {
			specificationTitle = _specificationTitleSupplier.get();

			_specificationTitleSupplier = null;
		}

		return specificationTitle;
	}

	public void setSpecificationTitle(String specificationTitle) {
		this.specificationTitle = specificationTitle;

		_specificationTitleSupplier = null;
	}

	@JsonIgnore
	public void setSpecificationTitle(
		UnsafeSupplier<String, Exception> specificationTitleUnsafeSupplier) {

		_specificationTitleSupplier = () -> {
			try {
				return specificationTitleUnsafeSupplier.get();
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
	protected String specificationTitle;

	@JsonIgnore
	private Supplier<String> _specificationTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(String value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
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
	protected String value;

	@JsonIgnore
	private Supplier<String> _valueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductSpecification)) {
			return false;
		}

		ProductSpecification productSpecification =
			(ProductSpecification)object;

		return Objects.equals(toString(), productSpecification.toString());
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

		Long optionCategoryId = getOptionCategoryId();

		if (optionCategoryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionCategoryId\": ");

			sb.append(optionCategoryId);
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		String specificationGroupKey = getSpecificationGroupKey();

		if (specificationGroupKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationGroupKey\": ");

			sb.append("\"");

			sb.append(_escape(specificationGroupKey));

			sb.append("\"");
		}

		String specificationGroupTitle = getSpecificationGroupTitle();

		if (specificationGroupTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationGroupTitle\": ");

			sb.append("\"");

			sb.append(_escape(specificationGroupTitle));

			sb.append("\"");
		}

		Long specificationId = getSpecificationId();

		if (specificationId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationId\": ");

			sb.append(specificationId);
		}

		String specificationKey = getSpecificationKey();

		if (specificationKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationKey\": ");

			sb.append("\"");

			sb.append(_escape(specificationKey));

			sb.append("\"");
		}

		Double specificationPriority = getSpecificationPriority();

		if (specificationPriority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationPriority\": ");

			sb.append(specificationPriority);
		}

		String specificationTitle = getSpecificationTitle();

		if (specificationTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationTitle\": ");

			sb.append("\"");

			sb.append(_escape(specificationTitle));

			sb.append("\"");
		}

		String value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(value));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductSpecification",
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