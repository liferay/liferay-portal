/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("ProductConfigurationListChannel")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"channelId", "productConfigurationListId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductConfigurationListChannel")
public class ProductConfigurationListChannel implements Serializable {

	public static ProductConfigurationListChannel toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ProductConfigurationListChannel.class, json);
	}

	public static ProductConfigurationListChannel unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductConfigurationListChannel.class, json);
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "1")
	public Integer getOrder() {
		if (_orderSupplier != null) {
			order = _orderSupplier.get();

			_orderSupplier = null;
		}

		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;

		_orderSupplier = null;
	}

	@JsonIgnore
	public void setOrder(
		UnsafeSupplier<Integer, Exception> orderUnsafeSupplier) {

		_orderSupplier = () -> {
			try {
				return orderUnsafeSupplier.get();
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
	protected Integer order;

	@JsonIgnore
	private Supplier<Integer> _orderSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getProductConfigurationListChannelId() {
		if (_productConfigurationListChannelIdSupplier != null) {
			productConfigurationListChannelId =
				_productConfigurationListChannelIdSupplier.get();

			_productConfigurationListChannelIdSupplier = null;
		}

		return productConfigurationListChannelId;
	}

	public void setProductConfigurationListChannelId(
		Long productConfigurationListChannelId) {

		this.productConfigurationListChannelId =
			productConfigurationListChannelId;

		_productConfigurationListChannelIdSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListChannelId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListChannelIdUnsafeSupplier) {

		_productConfigurationListChannelIdSupplier = () -> {
			try {
				return productConfigurationListChannelIdUnsafeSupplier.get();
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
	protected Long productConfigurationListChannelId;

	@JsonIgnore
	private Supplier<Long> _productConfigurationListChannelIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getProductConfigurationListExternalReferenceCode() {
		if (_productConfigurationListExternalReferenceCodeSupplier != null) {
			productConfigurationListExternalReferenceCode =
				_productConfigurationListExternalReferenceCodeSupplier.get();

			_productConfigurationListExternalReferenceCodeSupplier = null;
		}

		return productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		String productConfigurationListExternalReferenceCode) {

		this.productConfigurationListExternalReferenceCode =
			productConfigurationListExternalReferenceCode;

		_productConfigurationListExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productConfigurationListExternalReferenceCodeUnsafeSupplier) {

		_productConfigurationListExternalReferenceCodeSupplier = () -> {
			try {
				return productConfigurationListExternalReferenceCodeUnsafeSupplier.
					get();
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
	protected String productConfigurationListExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_productConfigurationListExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getProductConfigurationListId() {
		if (_productConfigurationListIdSupplier != null) {
			productConfigurationListId =
				_productConfigurationListIdSupplier.get();

			_productConfigurationListIdSupplier = null;
		}

		return productConfigurationListId;
	}

	public void setProductConfigurationListId(Long productConfigurationListId) {
		this.productConfigurationListId = productConfigurationListId;

		_productConfigurationListIdSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListIdUnsafeSupplier) {

		_productConfigurationListIdSupplier = () -> {
			try {
				return productConfigurationListIdUnsafeSupplier.get();
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
	protected Long productConfigurationListId;

	@JsonIgnore
	private Supplier<Long> _productConfigurationListIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfigurationListChannel)) {
			return false;
		}

		ProductConfigurationListChannel productConfigurationListChannel =
			(ProductConfigurationListChannel)object;

		return Objects.equals(
			toString(), productConfigurationListChannel.toString());
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

		Integer order = getOrder();

		if (order != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"order\": ");

			sb.append(order);
		}

		Long productConfigurationListChannelId =
			getProductConfigurationListChannelId();

		if (productConfigurationListChannelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListChannelId\": ");

			sb.append(productConfigurationListChannelId);
		}

		String productConfigurationListExternalReferenceCode =
			getProductConfigurationListExternalReferenceCode();

		if (productConfigurationListExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productConfigurationListExternalReferenceCode));

			sb.append("\"");
		}

		Long productConfigurationListId = getProductConfigurationListId();

		if (productConfigurationListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(productConfigurationListId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListChannel",
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