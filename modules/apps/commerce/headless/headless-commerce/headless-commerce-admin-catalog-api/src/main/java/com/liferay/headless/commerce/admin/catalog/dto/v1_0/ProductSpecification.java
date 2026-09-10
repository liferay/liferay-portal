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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A specification attribute value assigned to a specific product; it binds a specification template to a product and carries the localized value, an optional override of the category, a per-product priority, and a visibility flag.",
	value = "ProductSpecification"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A specification attribute value assigned to a specific product; it binds a specification template to a product and carries the localized value, an optional override of the category, a per-product priority, and a visibility flag.",
	requiredProperties = {"value"}
)
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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per product specification within the company.",
		example = "AB-34098-789-N"
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

	@GraphQLField(
		description = "Idempotency key for create and update; must be unique per product specification within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Primary key; assigned by the service on create.",
		example = "31130"
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

	@GraphQLField(
		description = "Primary key; assigned by the service on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Machine identifier on the product specification row; the service generates it from the primary key on create.",
		example = "material"
	)
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
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
		description = "Machine identifier on the product specification row; the service generates it from the primary key on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized label of the underlying specification, returned as a convenience; map keys are locale codes and values are the translated strings; read-only on this resource.",
		example = "{en_US=Hand Saw, hr_HR=Product Name HR, hu_HU=Product Name HU}"
	)
	@Valid
	public Map<String, String> getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
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
		description = "Localized label of the underlying specification, returned as a convenience; map keys are locale codes and values are the translated strings; read-only on this resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> label;

	@JsonIgnore
	private Supplier<Map<String, String>> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the linked option category; used as an alternative to `optionCategoryId` when overriding the specification's default category for this product.",
		example = "AB-34098-789-N"
	)
	public String getOptionCategoryExternalReferenceCode() {
		if (_optionCategoryExternalReferenceCodeSupplier != null) {
			optionCategoryExternalReferenceCode =
				_optionCategoryExternalReferenceCodeSupplier.get();

			_optionCategoryExternalReferenceCodeSupplier = null;
		}

		return optionCategoryExternalReferenceCode;
	}

	public void setOptionCategoryExternalReferenceCode(
		String optionCategoryExternalReferenceCode) {

		this.optionCategoryExternalReferenceCode =
			optionCategoryExternalReferenceCode;

		_optionCategoryExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOptionCategoryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			optionCategoryExternalReferenceCodeUnsafeSupplier) {

		_optionCategoryExternalReferenceCodeSupplier = () -> {
			try {
				return optionCategoryExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the linked option category; used as an alternative to `optionCategoryId` when overriding the specification's default category for this product."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String optionCategoryExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _optionCategoryExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to an option category that overrides, for this product, the category inherited from the underlying specification.",
		example = "30129"
	)
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

	@GraphQLField(
		description = "Reference to an option category that overrides, for this product, the category inherited from the underlying specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long optionCategoryId;

	@JsonIgnore
	private Supplier<Long> _optionCategoryIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display order of this specification within the product; lower values appear first.",
		example = "1.2"
	)
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

	@GraphQLField(
		description = "Display order of this specification within the product; lower values appear first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the product that owns this specification value.",
		example = "30129"
	)
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

	@GraphQLField(
		description = "Reference to the product that owns this specification value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the underlying specification; used as an alternative to `specificationId` on writes.",
		example = "AB-34098-789-N"
	)
	public String getSpecificationExternalReferenceCode() {
		if (_specificationExternalReferenceCodeSupplier != null) {
			specificationExternalReferenceCode =
				_specificationExternalReferenceCodeSupplier.get();

			_specificationExternalReferenceCodeSupplier = null;
		}

		return specificationExternalReferenceCode;
	}

	public void setSpecificationExternalReferenceCode(
		String specificationExternalReferenceCode) {

		this.specificationExternalReferenceCode =
			specificationExternalReferenceCode;

		_specificationExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setSpecificationExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			specificationExternalReferenceCodeUnsafeSupplier) {

		_specificationExternalReferenceCodeSupplier = () -> {
			try {
				return specificationExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the underlying specification; used as an alternative to `specificationId` on writes."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String specificationExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _specificationExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the specification template this value is for.",
		example = "30129"
	)
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

	@GraphQLField(
		description = "Reference to the specification template this value is for."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long specificationId;

	@JsonIgnore
	private Supplier<Long> _specificationIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Machine key of the underlying specification; returned as a convenience.",
		example = "specification-key"
	)
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

	@GraphQLField(
		description = "Machine key of the underlying specification; returned as a convenience."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String specificationKey;

	@JsonIgnore
	private Supplier<String> _specificationKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display order configured on the underlying specification template; returned as a convenience and not editable on this resource.",
		example = "1.1"
	)
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

	@GraphQLField(
		description = "Display order configured on the underlying specification template; returned as a convenience and not editable on this resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double specificationPriority;

	@JsonIgnore
	private Supplier<Double> _specificationPrioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized value for this specification on this product; map keys are locale codes and values are the translated strings.",
		example = "{en_US=Steel, hr_HR=Celik, hu_HU=Acel}"
	)
	@Valid
	public Map<String, String> getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(Map<String, String> value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<Map<String, String>, Exception> valueUnsafeSupplier) {

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

	@GraphQLField(
		description = "Localized value for this specification on this product; map keys are locale codes and values are the translated strings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Map<String, String> value;

	@JsonIgnore
	private Supplier<Map<String, String>> _valueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the specification value is shown to shoppers on the storefront; values where `visible` is false are filtered out of public product detail views.",
		example = "true"
	)
	public Boolean getVisible() {
		if (_visibleSupplier != null) {
			visible = _visibleSupplier.get();

			_visibleSupplier = null;
		}

		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;

		_visibleSupplier = null;
	}

	@JsonIgnore
	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		_visibleSupplier = () -> {
			try {
				return visibleUnsafeSupplier.get();
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
		description = "Whether the specification value is shown to shoppers on the storefront; values where `visible` is false are filtered out of public product detail views."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean visible;

	@JsonIgnore
	private Supplier<Boolean> _visibleSupplier;

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

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		Map<String, String> label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(label));
		}

		String optionCategoryExternalReferenceCode =
			getOptionCategoryExternalReferenceCode();

		if (optionCategoryExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionCategoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(optionCategoryExternalReferenceCode));

			sb.append("\"");
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

		String specificationExternalReferenceCode =
			getSpecificationExternalReferenceCode();

		if (specificationExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(specificationExternalReferenceCode));

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

		Map<String, String> value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(_toJSON(value));
		}

		Boolean visible = getVisible();

		if (visible != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(visible);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSpecification",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
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
// LIFERAY-REST-BUILDER-HASH:528776058