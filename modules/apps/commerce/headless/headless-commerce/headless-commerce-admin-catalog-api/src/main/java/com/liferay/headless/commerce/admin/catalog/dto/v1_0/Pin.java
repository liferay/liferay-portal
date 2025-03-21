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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

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
@GraphQLName("Pin")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Pin")
public class Pin implements Serializable {

	public static Pin toDTO(String json) {
		return ObjectMapperUtil.readValue(Pin.class, json);
	}

	public static Pin unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Pin.class, json);
	}

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MappedProduct getMappedProduct() {
		if (_mappedProductSupplier != null) {
			mappedProduct = _mappedProductSupplier.get();

			_mappedProductSupplier = null;
		}

		return mappedProduct;
	}

	public void setMappedProduct(MappedProduct mappedProduct) {
		this.mappedProduct = mappedProduct;

		_mappedProductSupplier = null;
	}

	@JsonIgnore
	public void setMappedProduct(
		UnsafeSupplier<MappedProduct, Exception> mappedProductUnsafeSupplier) {

		_mappedProductSupplier = () -> {
			try {
				return mappedProductUnsafeSupplier.get();
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
	protected MappedProduct mappedProduct;

	@JsonIgnore
	private Supplier<MappedProduct> _mappedProductSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "33.54")
	public Double getPositionX() {
		if (_positionXSupplier != null) {
			positionX = _positionXSupplier.get();

			_positionXSupplier = null;
		}

		return positionX;
	}

	public void setPositionX(Double positionX) {
		this.positionX = positionX;

		_positionXSupplier = null;
	}

	@JsonIgnore
	public void setPositionX(
		UnsafeSupplier<Double, Exception> positionXUnsafeSupplier) {

		_positionXSupplier = () -> {
			try {
				return positionXUnsafeSupplier.get();
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
	protected Double positionX;

	@JsonIgnore
	private Supplier<Double> _positionXSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "33.54")
	public Double getPositionY() {
		if (_positionYSupplier != null) {
			positionY = _positionYSupplier.get();

			_positionYSupplier = null;
		}

		return positionY;
	}

	public void setPositionY(Double positionY) {
		this.positionY = positionY;

		_positionYSupplier = null;
	}

	@JsonIgnore
	public void setPositionY(
		UnsafeSupplier<Double, Exception> positionYUnsafeSupplier) {

		_positionYSupplier = () -> {
			try {
				return positionYUnsafeSupplier.get();
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
	protected Double positionY;

	@JsonIgnore
	private Supplier<Double> _positionYSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "1")
	public String getSequence() {
		if (_sequenceSupplier != null) {
			sequence = _sequenceSupplier.get();

			_sequenceSupplier = null;
		}

		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;

		_sequenceSupplier = null;
	}

	@JsonIgnore
	public void setSequence(
		UnsafeSupplier<String, Exception> sequenceUnsafeSupplier) {

		_sequenceSupplier = () -> {
			try {
				return sequenceUnsafeSupplier.get();
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
	protected String sequence;

	@JsonIgnore
	private Supplier<String> _sequenceSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Pin)) {
			return false;
		}

		Pin pin = (Pin)object;

		return Objects.equals(toString(), pin.toString());
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

		MappedProduct mappedProduct = getMappedProduct();

		if (mappedProduct != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappedProduct\": ");

			sb.append(String.valueOf(mappedProduct));
		}

		Double positionX = getPositionX();

		if (positionX != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"positionX\": ");

			sb.append(positionX);
		}

		Double positionY = getPositionY();

		if (positionY != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"positionY\": ");

			sb.append(positionY);
		}

		String sequence = getSequence();

		if (sequence != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sequence\": ");

			sb.append("\"");

			sb.append(_escape(sequence));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Pin",
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