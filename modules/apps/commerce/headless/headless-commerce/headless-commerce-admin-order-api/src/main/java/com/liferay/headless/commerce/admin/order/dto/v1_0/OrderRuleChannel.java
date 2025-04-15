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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("OrderRuleChannel")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"channelId", "orderRuleId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OrderRuleChannel")
public class OrderRuleChannel implements Serializable {

	public static OrderRuleChannel toDTO(String json) {
		return ObjectMapperUtil.readValue(OrderRuleChannel.class, json);
	}

	public static OrderRuleChannel unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(OrderRuleChannel.class, json);
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
	public Channel getChannel() {
		if (_channelSupplier != null) {
			channel = _channelSupplier.get();

			_channelSupplier = null;
		}

		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;

		_channelSupplier = null;
	}

	@JsonIgnore
	public void setChannel(
		UnsafeSupplier<Channel, Exception> channelUnsafeSupplier) {

		_channelSupplier = () -> {
			try {
				return channelUnsafeSupplier.get();
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
	protected Channel channel;

	@JsonIgnore
	private Supplier<Channel> _channelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getChannelExternalReferenceCode() {
		if (_channelExternalReferenceCodeSupplier != null) {
			channelExternalReferenceCode =
				_channelExternalReferenceCodeSupplier.get();

			_channelExternalReferenceCodeSupplier = null;
		}

		return channelExternalReferenceCode;
	}

	public void setChannelExternalReferenceCode(
		String channelExternalReferenceCode) {

		this.channelExternalReferenceCode = channelExternalReferenceCode;

		_channelExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setChannelExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			channelExternalReferenceCodeUnsafeSupplier) {

		_channelExternalReferenceCodeSupplier = () -> {
			try {
				return channelExternalReferenceCodeUnsafeSupplier.get();
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
	protected String channelExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _channelExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getChannelId() {
		if (_channelIdSupplier != null) {
			channelId = _channelIdSupplier.get();

			_channelIdSupplier = null;
		}

		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;

		_channelIdSupplier = null;
	}

	@JsonIgnore
	public void setChannelId(
		UnsafeSupplier<Long, Exception> channelIdUnsafeSupplier) {

		_channelIdSupplier = () -> {
			try {
				return channelIdUnsafeSupplier.get();
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
	protected Long channelId;

	@JsonIgnore
	private Supplier<Long> _channelIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getOrderRuleChannelId() {
		if (_orderRuleChannelIdSupplier != null) {
			orderRuleChannelId = _orderRuleChannelIdSupplier.get();

			_orderRuleChannelIdSupplier = null;
		}

		return orderRuleChannelId;
	}

	public void setOrderRuleChannelId(Long orderRuleChannelId) {
		this.orderRuleChannelId = orderRuleChannelId;

		_orderRuleChannelIdSupplier = null;
	}

	@JsonIgnore
	public void setOrderRuleChannelId(
		UnsafeSupplier<Long, Exception> orderRuleChannelIdUnsafeSupplier) {

		_orderRuleChannelIdSupplier = () -> {
			try {
				return orderRuleChannelIdUnsafeSupplier.get();
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
	protected Long orderRuleChannelId;

	@JsonIgnore
	private Supplier<Long> _orderRuleChannelIdSupplier;

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

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderRuleChannel)) {
			return false;
		}

		OrderRuleChannel orderRuleChannel = (OrderRuleChannel)object;

		return Objects.equals(toString(), orderRuleChannel.toString());
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

		Channel channel = getChannel();

		if (channel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(channel));
		}

		String channelExternalReferenceCode = getChannelExternalReferenceCode();

		if (channelExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(channelExternalReferenceCode));

			sb.append("\"");
		}

		Long channelId = getChannelId();

		if (channelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(channelId);
		}

		Long orderRuleChannelId = getOrderRuleChannelId();

		if (orderRuleChannelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleChannelId\": ");

			sb.append(orderRuleChannelId);
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleChannel",
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