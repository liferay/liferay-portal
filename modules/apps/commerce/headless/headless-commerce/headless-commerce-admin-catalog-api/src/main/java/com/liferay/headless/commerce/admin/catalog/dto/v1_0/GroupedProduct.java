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

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("GroupedProduct")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "GroupedProduct")
public class GroupedProduct implements Serializable {

	public static GroupedProduct toDTO(String json) {
		return ObjectMapperUtil.readValue(GroupedProduct.class, json);
	}

	public static GroupedProduct unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(GroupedProduct.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "exampleERC")
	public String getEntryProductExternalReferenceCode() {
		if (_entryProductExternalReferenceCodeSupplier != null) {
			entryProductExternalReferenceCode =
				_entryProductExternalReferenceCodeSupplier.get();

			_entryProductExternalReferenceCodeSupplier = null;
		}

		return entryProductExternalReferenceCode;
	}

	public void setEntryProductExternalReferenceCode(
		String entryProductExternalReferenceCode) {

		this.entryProductExternalReferenceCode =
			entryProductExternalReferenceCode;

		_entryProductExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setEntryProductExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			entryProductExternalReferenceCodeUnsafeSupplier) {

		_entryProductExternalReferenceCodeSupplier = () -> {
			try {
				return entryProductExternalReferenceCodeUnsafeSupplier.get();
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
	protected String entryProductExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _entryProductExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "33131")
	public Long getEntryProductId() {
		if (_entryProductIdSupplier != null) {
			entryProductId = _entryProductIdSupplier.get();

			_entryProductIdSupplier = null;
		}

		return entryProductId;
	}

	public void setEntryProductId(Long entryProductId) {
		this.entryProductId = entryProductId;

		_entryProductIdSupplier = null;
	}

	@JsonIgnore
	public void setEntryProductId(
		UnsafeSupplier<Long, Exception> entryProductIdUnsafeSupplier) {

		_entryProductIdSupplier = () -> {
			try {
				return entryProductIdUnsafeSupplier.get();
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
	protected Long entryProductId;

	@JsonIgnore
	private Supplier<Long> _entryProductIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Hand Saw, hr_HR=Product Name HR, hu_HU=Product Name HU}"
	)
	@Valid
	public Map<String, String> getEntryProductName() {
		if (_entryProductNameSupplier != null) {
			entryProductName = _entryProductNameSupplier.get();

			_entryProductNameSupplier = null;
		}

		return entryProductName;
	}

	public void setEntryProductName(Map<String, String> entryProductName) {
		this.entryProductName = entryProductName;

		_entryProductNameSupplier = null;
	}

	@JsonIgnore
	public void setEntryProductName(
		UnsafeSupplier<Map<String, String>, Exception>
			entryProductNameUnsafeSupplier) {

		_entryProductNameSupplier = () -> {
			try {
				return entryProductNameUnsafeSupplier.get();
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
	protected Map<String, String> entryProductName;

	@JsonIgnore
	private Supplier<Map<String, String>> _entryProductNameSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "33130")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "exampleERC")
	public String getProductExternalReferenceCode() {
		if (_productExternalReferenceCodeSupplier != null) {
			productExternalReferenceCode =
				_productExternalReferenceCodeSupplier.get();

			_productExternalReferenceCodeSupplier = null;
		}

		return productExternalReferenceCode;
	}

	public void setProductExternalReferenceCode(
		String productExternalReferenceCode) {

		this.productExternalReferenceCode = productExternalReferenceCode;

		_productExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productExternalReferenceCodeUnsafeSupplier) {

		_productExternalReferenceCodeSupplier = () -> {
			try {
				return productExternalReferenceCodeUnsafeSupplier.get();
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
	protected String productExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _productExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "33131")
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Hand Saw, hr_HR=Product Name HR, hu_HU=Product Name HU}"
	)
	@Valid
	public Map<String, String> getProductName() {
		if (_productNameSupplier != null) {
			productName = _productNameSupplier.get();

			_productNameSupplier = null;
		}

		return productName;
	}

	public void setProductName(Map<String, String> productName) {
		this.productName = productName;

		_productNameSupplier = null;
	}

	@JsonIgnore
	public void setProductName(
		UnsafeSupplier<Map<String, String>, Exception>
			productNameUnsafeSupplier) {

		_productNameSupplier = () -> {
			try {
				return productNameUnsafeSupplier.get();
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
	protected Map<String, String> productName;

	@JsonIgnore
	private Supplier<Map<String, String>> _productNameSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1")
	public Integer getQuantity() {
		if (_quantitySupplier != null) {
			quantity = _quantitySupplier.get();

			_quantitySupplier = null;
		}

		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;

		_quantitySupplier = null;
	}

	@JsonIgnore
	public void setQuantity(
		UnsafeSupplier<Integer, Exception> quantityUnsafeSupplier) {

		_quantitySupplier = () -> {
			try {
				return quantityUnsafeSupplier.get();
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
	protected Integer quantity;

	@JsonIgnore
	private Supplier<Integer> _quantitySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GroupedProduct)) {
			return false;
		}

		GroupedProduct groupedProduct = (GroupedProduct)object;

		return Objects.equals(toString(), groupedProduct.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String entryProductExternalReferenceCode =
			getEntryProductExternalReferenceCode();

		if (entryProductExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entryProductExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(entryProductExternalReferenceCode));

			sb.append("\"");
		}

		Long entryProductId = getEntryProductId();

		if (entryProductId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entryProductId\": ");

			sb.append(entryProductId);
		}

		Map<String, String> entryProductName = getEntryProductName();

		if (entryProductName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entryProductName\": ");

			sb.append(_toJSON(entryProductName));
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		String productExternalReferenceCode = getProductExternalReferenceCode();

		if (productExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productExternalReferenceCode));

			sb.append("\"");
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		Map<String, String> productName = getProductName();

		if (productName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productName\": ");

			sb.append(_toJSON(productName));
		}

		Integer quantity = getQuantity();

		if (quantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(quantity);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.GroupedProduct",
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