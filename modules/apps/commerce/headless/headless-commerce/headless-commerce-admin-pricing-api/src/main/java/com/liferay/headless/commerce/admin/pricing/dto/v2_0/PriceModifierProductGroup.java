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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
@GraphQLName("PriceModifierProductGroup")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"priceModifierId", "productGroupId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceModifierProductGroup")
public class PriceModifierProductGroup implements Serializable {

	public static PriceModifierProductGroup toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PriceModifierProductGroup.class, json);
	}

	public static PriceModifierProductGroup unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PriceModifierProductGroup.class, json);
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

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getPriceModifierProductGroupId() {
		if (_priceModifierProductGroupIdSupplier != null) {
			priceModifierProductGroupId =
				_priceModifierProductGroupIdSupplier.get();

			_priceModifierProductGroupIdSupplier = null;
		}

		return priceModifierProductGroupId;
	}

	public void setPriceModifierProductGroupId(
		Long priceModifierProductGroupId) {

		this.priceModifierProductGroupId = priceModifierProductGroupId;

		_priceModifierProductGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierProductGroupId(
		UnsafeSupplier<Long, Exception>
			priceModifierProductGroupIdUnsafeSupplier) {

		_priceModifierProductGroupIdSupplier = () -> {
			try {
				return priceModifierProductGroupIdUnsafeSupplier.get();
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
	protected Long priceModifierProductGroupId;

	@JsonIgnore
	private Supplier<Long> _priceModifierProductGroupIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductGroup getProductGroup() {
		if (_productGroupSupplier != null) {
			productGroup = _productGroupSupplier.get();

			_productGroupSupplier = null;
		}

		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;

		_productGroupSupplier = null;
	}

	@JsonIgnore
	public void setProductGroup(
		UnsafeSupplier<ProductGroup, Exception> productGroupUnsafeSupplier) {

		_productGroupSupplier = () -> {
			try {
				return productGroupUnsafeSupplier.get();
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
	protected ProductGroup productGroup;

	@JsonIgnore
	private Supplier<ProductGroup> _productGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getProductGroupExternalReferenceCode() {
		if (_productGroupExternalReferenceCodeSupplier != null) {
			productGroupExternalReferenceCode =
				_productGroupExternalReferenceCodeSupplier.get();

			_productGroupExternalReferenceCodeSupplier = null;
		}

		return productGroupExternalReferenceCode;
	}

	public void setProductGroupExternalReferenceCode(
		String productGroupExternalReferenceCode) {

		this.productGroupExternalReferenceCode =
			productGroupExternalReferenceCode;

		_productGroupExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productGroupExternalReferenceCodeUnsafeSupplier) {

		_productGroupExternalReferenceCodeSupplier = () -> {
			try {
				return productGroupExternalReferenceCodeUnsafeSupplier.get();
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
	protected String productGroupExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _productGroupExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getProductGroupId() {
		if (_productGroupIdSupplier != null) {
			productGroupId = _productGroupIdSupplier.get();

			_productGroupIdSupplier = null;
		}

		return productGroupId;
	}

	public void setProductGroupId(Long productGroupId) {
		this.productGroupId = productGroupId;

		_productGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setProductGroupId(
		UnsafeSupplier<Long, Exception> productGroupIdUnsafeSupplier) {

		_productGroupIdSupplier = () -> {
			try {
				return productGroupIdUnsafeSupplier.get();
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
	protected Long productGroupId;

	@JsonIgnore
	private Supplier<Long> _productGroupIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceModifierProductGroup)) {
			return false;
		}

		PriceModifierProductGroup priceModifierProductGroup =
			(PriceModifierProductGroup)object;

		return Objects.equals(toString(), priceModifierProductGroup.toString());
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

		Long priceModifierProductGroupId = getPriceModifierProductGroupId();

		if (priceModifierProductGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProductGroupId\": ");

			sb.append(priceModifierProductGroupId);
		}

		ProductGroup productGroup = getProductGroup();

		if (productGroup != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroup\": ");

			sb.append(String.valueOf(productGroup));
		}

		String productGroupExternalReferenceCode =
			getProductGroupExternalReferenceCode();

		if (productGroupExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productGroupExternalReferenceCode));

			sb.append("\"");
		}

		Long productGroupId = getProductGroupId();

		if (productGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroupId\": ");

			sb.append(productGroupId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProductGroup",
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