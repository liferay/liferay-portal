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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("OrderRuleOrderType")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"orderRuleId", "orderTypeId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OrderRuleOrderType")
public class OrderRuleOrderType implements Serializable {

	public static OrderRuleOrderType toDTO(String json) {
		return ObjectMapperUtil.readValue(OrderRuleOrderType.class, json);
	}

	public static OrderRuleOrderType unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(OrderRuleOrderType.class, json);
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getOrderRuleExternalReferenceCode() {
		if (_orderRuleExternalReferenceCodeSupplier != null) {
			orderRuleExternalReferenceCode =
				_orderRuleExternalReferenceCodeSupplier.get();

			_orderRuleExternalReferenceCodeSupplier = null;
		}

		return orderRuleExternalReferenceCode;
	}

	public void setOrderRuleExternalReferenceCode(
		String orderRuleExternalReferenceCode) {

		this.orderRuleExternalReferenceCode = orderRuleExternalReferenceCode;

		_orderRuleExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderRuleExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderRuleExternalReferenceCodeUnsafeSupplier) {

		_orderRuleExternalReferenceCodeSupplier = () -> {
			try {
				return orderRuleExternalReferenceCodeUnsafeSupplier.get();
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
	protected String orderRuleExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderRuleExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getOrderRuleId() {
		if (_orderRuleIdSupplier != null) {
			orderRuleId = _orderRuleIdSupplier.get();

			_orderRuleIdSupplier = null;
		}

		return orderRuleId;
	}

	public void setOrderRuleId(Long orderRuleId) {
		this.orderRuleId = orderRuleId;

		_orderRuleIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderRuleId(
		UnsafeSupplier<Long, Exception> orderRuleIdUnsafeSupplier) {

		_orderRuleIdSupplier = () -> {
			try {
				return orderRuleIdUnsafeSupplier.get();
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
	protected Long orderRuleId;

	@JsonIgnore
	private Supplier<Long> _orderRuleIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getOrderRuleOrderTypeId() {
		if (_orderRuleOrderTypeIdSupplier != null) {
			orderRuleOrderTypeId = _orderRuleOrderTypeIdSupplier.get();

			_orderRuleOrderTypeIdSupplier = null;
		}

		return orderRuleOrderTypeId;
	}

	public void setOrderRuleOrderTypeId(Long orderRuleOrderTypeId) {
		this.orderRuleOrderTypeId = orderRuleOrderTypeId;

		_orderRuleOrderTypeIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderRuleOrderTypeId(
		UnsafeSupplier<Long, Exception> orderRuleOrderTypeIdUnsafeSupplier) {

		_orderRuleOrderTypeIdSupplier = () -> {
			try {
				return orderRuleOrderTypeIdUnsafeSupplier.get();
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
	protected Long orderRuleOrderTypeId;

	@JsonIgnore
	private Supplier<Long> _orderRuleOrderTypeIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public OrderType getOrderType() {
		if (_orderTypeSupplier != null) {
			orderType = _orderTypeSupplier.get();

			_orderTypeSupplier = null;
		}

		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;

		_orderTypeSupplier = null;
	}

	@JsonIgnore
	public void setOrderType(
		UnsafeSupplier<OrderType, Exception> orderTypeUnsafeSupplier) {

		_orderTypeSupplier = () -> {
			try {
				return orderTypeUnsafeSupplier.get();
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
	protected OrderType orderType;

	@JsonIgnore
	private Supplier<OrderType> _orderTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getOrderTypeExternalReferenceCode() {
		if (_orderTypeExternalReferenceCodeSupplier != null) {
			orderTypeExternalReferenceCode =
				_orderTypeExternalReferenceCodeSupplier.get();

			_orderTypeExternalReferenceCodeSupplier = null;
		}

		return orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		String orderTypeExternalReferenceCode) {

		this.orderTypeExternalReferenceCode = orderTypeExternalReferenceCode;

		_orderTypeExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setOrderTypeExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderTypeExternalReferenceCodeUnsafeSupplier) {

		_orderTypeExternalReferenceCodeSupplier = () -> {
			try {
				return orderTypeExternalReferenceCodeUnsafeSupplier.get();
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
	protected String orderTypeExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _orderTypeExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getOrderTypeId() {
		if (_orderTypeIdSupplier != null) {
			orderTypeId = _orderTypeIdSupplier.get();

			_orderTypeIdSupplier = null;
		}

		return orderTypeId;
	}

	public void setOrderTypeId(Long orderTypeId) {
		this.orderTypeId = orderTypeId;

		_orderTypeIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderTypeId(
		UnsafeSupplier<Long, Exception> orderTypeIdUnsafeSupplier) {

		_orderTypeIdSupplier = () -> {
			try {
				return orderTypeIdUnsafeSupplier.get();
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
	protected Long orderTypeId;

	@JsonIgnore
	private Supplier<Long> _orderTypeIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderRuleOrderType)) {
			return false;
		}

		OrderRuleOrderType orderRuleOrderType = (OrderRuleOrderType)object;

		return Objects.equals(toString(), orderRuleOrderType.toString());
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

		String orderRuleExternalReferenceCode =
			getOrderRuleExternalReferenceCode();

		if (orderRuleExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderRuleExternalReferenceCode));

			sb.append("\"");
		}

		Long orderRuleId = getOrderRuleId();

		if (orderRuleId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleId\": ");

			sb.append(orderRuleId);
		}

		Long orderRuleOrderTypeId = getOrderRuleOrderTypeId();

		if (orderRuleOrderTypeId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleOrderTypeId\": ");

			sb.append(orderRuleOrderTypeId);
		}

		OrderType orderType = getOrderType();

		if (orderType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(String.valueOf(orderType));
		}

		String orderTypeExternalReferenceCode =
			getOrderTypeExternalReferenceCode();

		if (orderTypeExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderTypeExternalReferenceCode));

			sb.append("\"");
		}

		Long orderTypeId = getOrderTypeId();

		if (orderTypeId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(orderTypeId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleOrderType",
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