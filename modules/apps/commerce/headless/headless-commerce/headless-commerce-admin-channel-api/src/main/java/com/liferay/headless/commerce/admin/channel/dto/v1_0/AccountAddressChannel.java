/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.dto.v1_0;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("AccountAddressChannel")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"addressChannelId", "addressId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AccountAddressChannel")
public class AccountAddressChannel implements Serializable {

	public static AccountAddressChannel toDTO(String json) {
		return ObjectMapperUtil.readValue(AccountAddressChannel.class, json);
	}

	public static AccountAddressChannel unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AccountAddressChannel.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getAccountAddressChannelId() {
		if (_accountAddressChannelIdSupplier != null) {
			accountAddressChannelId = _accountAddressChannelIdSupplier.get();

			_accountAddressChannelIdSupplier = null;
		}

		return accountAddressChannelId;
	}

	public void setAccountAddressChannelId(Long accountAddressChannelId) {
		this.accountAddressChannelId = accountAddressChannelId;

		_accountAddressChannelIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountAddressChannelId(
		UnsafeSupplier<Long, Exception> accountAddressChannelIdUnsafeSupplier) {

		_accountAddressChannelIdSupplier = () -> {
			try {
				return accountAddressChannelIdUnsafeSupplier.get();
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
	protected Long accountAddressChannelId;

	@JsonIgnore
	private Supplier<Long> _accountAddressChannelIdSupplier;

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
	public String getAddressChannelExternalReferenceCode() {
		if (_addressChannelExternalReferenceCodeSupplier != null) {
			addressChannelExternalReferenceCode =
				_addressChannelExternalReferenceCodeSupplier.get();

			_addressChannelExternalReferenceCodeSupplier = null;
		}

		return addressChannelExternalReferenceCode;
	}

	public void setAddressChannelExternalReferenceCode(
		String addressChannelExternalReferenceCode) {

		this.addressChannelExternalReferenceCode =
			addressChannelExternalReferenceCode;

		_addressChannelExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAddressChannelExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			addressChannelExternalReferenceCodeUnsafeSupplier) {

		_addressChannelExternalReferenceCodeSupplier = () -> {
			try {
				return addressChannelExternalReferenceCodeUnsafeSupplier.get();
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
	protected String addressChannelExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _addressChannelExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getAddressChannelId() {
		if (_addressChannelIdSupplier != null) {
			addressChannelId = _addressChannelIdSupplier.get();

			_addressChannelIdSupplier = null;
		}

		return addressChannelId;
	}

	public void setAddressChannelId(Long addressChannelId) {
		this.addressChannelId = addressChannelId;

		_addressChannelIdSupplier = null;
	}

	@JsonIgnore
	public void setAddressChannelId(
		UnsafeSupplier<Long, Exception> addressChannelIdUnsafeSupplier) {

		_addressChannelIdSupplier = () -> {
			try {
				return addressChannelIdUnsafeSupplier.get();
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
	protected Long addressChannelId;

	@JsonIgnore
	private Supplier<Long> _addressChannelIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getAddressExternalReferenceCode() {
		if (_addressExternalReferenceCodeSupplier != null) {
			addressExternalReferenceCode =
				_addressExternalReferenceCodeSupplier.get();

			_addressExternalReferenceCodeSupplier = null;
		}

		return addressExternalReferenceCode;
	}

	public void setAddressExternalReferenceCode(
		String addressExternalReferenceCode) {

		this.addressExternalReferenceCode = addressExternalReferenceCode;

		_addressExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			addressExternalReferenceCodeUnsafeSupplier) {

		_addressExternalReferenceCodeSupplier = () -> {
			try {
				return addressExternalReferenceCodeUnsafeSupplier.get();
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
	protected String addressExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _addressExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getAddressId() {
		if (_addressIdSupplier != null) {
			addressId = _addressIdSupplier.get();

			_addressIdSupplier = null;
		}

		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;

		_addressIdSupplier = null;
	}

	@JsonIgnore
	public void setAddressId(
		UnsafeSupplier<Long, Exception> addressIdUnsafeSupplier) {

		_addressIdSupplier = () -> {
			try {
				return addressIdUnsafeSupplier.get();
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
	protected Long addressId;

	@JsonIgnore
	private Supplier<Long> _addressIdSupplier;

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

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountAddressChannel)) {
			return false;
		}

		AccountAddressChannel accountAddressChannel =
			(AccountAddressChannel)object;

		return Objects.equals(toString(), accountAddressChannel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long accountAddressChannelId = getAccountAddressChannelId();

		if (accountAddressChannelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountAddressChannelId\": ");

			sb.append(accountAddressChannelId);
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String addressChannelExternalReferenceCode =
			getAddressChannelExternalReferenceCode();

		if (addressChannelExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressChannelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(addressChannelExternalReferenceCode));

			sb.append("\"");
		}

		Long addressChannelId = getAddressChannelId();

		if (addressChannelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressChannelId\": ");

			sb.append(addressChannelId);
		}

		String addressExternalReferenceCode = getAddressExternalReferenceCode();

		if (addressExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(addressExternalReferenceCode));

			sb.append("\"");
		}

		Long addressId = getAddressId();

		if (addressId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressId\": ");

			sb.append(addressId);
		}

		Channel channel = getChannel();

		if (channel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(channel));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.channel.dto.v1_0.AccountAddressChannel",
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