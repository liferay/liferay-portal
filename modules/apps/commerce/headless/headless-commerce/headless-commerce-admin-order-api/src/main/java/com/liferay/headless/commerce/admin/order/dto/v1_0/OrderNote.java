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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("OrderNote")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OrderNote")
public class OrderNote implements Serializable {

	public static OrderNote toDTO(String json) {
		return ObjectMapperUtil.readValue(OrderNote.class, json);
	}

	public static OrderNote unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(OrderNote.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Alessio Antonio Rendina"
	)
	public String getAuthor() {
		if (_authorSupplier != null) {
			author = _authorSupplier.get();

			_authorSupplier = null;
		}

		return author;
	}

	public void setAuthor(String author) {
		this.author = author;

		_authorSupplier = null;
	}

	@JsonIgnore
	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		_authorSupplier = () -> {
			try {
				return authorUnsafeSupplier.get();
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
	protected String author;

	@JsonIgnore
	private Supplier<String> _authorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "This order will be shipped separately"
	)
	public String getContent() {
		if (_contentSupplier != null) {
			content = _contentSupplier.get();

			_contentSupplier = null;
		}

		return content;
	}

	public void setContent(String content) {
		this.content = content;

		_contentSupplier = null;
	}

	@JsonIgnore
	public void setContent(
		UnsafeSupplier<String, Exception> contentUnsafeSupplier) {

		_contentSupplier = () -> {
			try {
				return contentUnsafeSupplier.get();
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
	protected String content;

	@JsonIgnore
	private Supplier<String> _contentSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(example = "CAB-34098-789-N")
	public String getOrderExternalReferenceCode() {
		if (_orderExternalReferenceCodeSupplier != null) {
			orderExternalReferenceCode =
				_orderExternalReferenceCodeSupplier.get();

			_orderExternalReferenceCodeSupplier = null;
		}

		return orderExternalReferenceCode;
	}

	public void setOrderExternalReferenceCode(
		String orderExternalReferenceCode) {

		this.orderExternalReferenceCode = orderExternalReferenceCode;

		_orderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderExternalReferenceCodeUnsafeSupplier) {

		_orderExternalReferenceCodeSupplier = () -> {
			try {
				return orderExternalReferenceCodeUnsafeSupplier.get();
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
	protected String orderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30128")
	public Long getOrderId() {
		if (_orderIdSupplier != null) {
			orderId = _orderIdSupplier.get();

			_orderIdSupplier = null;
		}

		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;

		_orderIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderId(
		UnsafeSupplier<Long, Exception> orderIdUnsafeSupplier) {

		_orderIdSupplier = () -> {
			try {
				return orderIdUnsafeSupplier.get();
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
	protected Long orderId;

	@JsonIgnore
	private Supplier<Long> _orderIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getRestricted() {
		if (_restrictedSupplier != null) {
			restricted = _restrictedSupplier.get();

			_restrictedSupplier = null;
		}

		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;

		_restrictedSupplier = null;
	}

	@JsonIgnore
	public void setRestricted(
		UnsafeSupplier<Boolean, Exception> restrictedUnsafeSupplier) {

		_restrictedSupplier = () -> {
			try {
				return restrictedUnsafeSupplier.get();
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
	protected Boolean restricted;

	@JsonIgnore
	private Supplier<Boolean> _restrictedSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderNote)) {
			return false;
		}

		OrderNote orderNote = (OrderNote)object;

		return Objects.equals(toString(), orderNote.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String author = getAuthor();

		if (author != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(author));

			sb.append("\"");
		}

		String content = getContent();

		if (content != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"content\": ");

			sb.append("\"");

			sb.append(_escape(content));

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

		String orderExternalReferenceCode = getOrderExternalReferenceCode();

		if (orderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderExternalReferenceCode));

			sb.append("\"");
		}

		Long orderId = getOrderId();

		if (orderId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderId);
		}

		Boolean restricted = getRestricted();

		if (restricted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"restricted\": ");

			sb.append(restricted);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.OrderNote",
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