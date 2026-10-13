/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "One recipient on a signature request.",
	value = "SignatureRequestRecipient"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "One recipient on a signature request."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SignatureRequestRecipient")
public class SignatureRequestRecipient implements Serializable {

	public static SignatureRequestRecipient toDTO(String json) {
		return ObjectMapperUtil.readValue(
			SignatureRequestRecipient.class, json);
	}

	public static SignatureRequestRecipient unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			SignatureRequestRecipient.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Email address of the recipient. Read-only; taken from the user."
	)
	public String getEmailAddress() {
		if (_emailAddressSupplier != null) {
			emailAddress = _emailAddressSupplier.get();

			_emailAddressSupplier = null;
		}

		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;

		_emailAddressSupplier = null;
	}

	@JsonIgnore
	public void setEmailAddress(
		UnsafeSupplier<String, Exception> emailAddressUnsafeSupplier) {

		_emailAddressSupplier = () -> {
			try {
				return emailAddressUnsafeSupplier.get();
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
		description = "Email address of the recipient. Read-only; taken from the user."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String emailAddress;

	@JsonIgnore
	private Supplier<String> _emailAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the recipient. Read-only; taken from the user."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
		description = "Name of the recipient. Read-only; taken from the user."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the recipient was notified. Read-only."
	)
	public Date getSentDate() {
		if (_sentDateSupplier != null) {
			sentDate = _sentDateSupplier.get();

			_sentDateSupplier = null;
		}

		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;

		_sentDateSupplier = null;
	}

	@JsonIgnore
	public void setSentDate(
		UnsafeSupplier<Date, Exception> sentDateUnsafeSupplier) {

		_sentDateSupplier = () -> {
			try {
				return sentDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the recipient was notified. Read-only.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date sentDate;

	@JsonIgnore
	private Supplier<Date> _sentDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Status of this recipient. Read-only."
	)
	public String getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Status of this recipient. Read-only.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When this recipient's status last changed. Read-only."
	)
	public Date getStatusDate() {
		if (_statusDateSupplier != null) {
			statusDate = _statusDateSupplier.get();

			_statusDateSupplier = null;
		}

		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;

		_statusDateSupplier = null;
	}

	@JsonIgnore
	public void setStatusDate(
		UnsafeSupplier<Date, Exception> statusDateUnsafeSupplier) {

		_statusDateSupplier = () -> {
			try {
				return statusDateUnsafeSupplier.get();
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
		description = "When this recipient's status last changed. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date statusDate;

	@JsonIgnore
	private Supplier<Date> _statusDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Liferay user who must sign. Required when creating a signature request."
	)
	public Long getUserId() {
		if (_userIdSupplier != null) {
			userId = _userIdSupplier.get();

			_userIdSupplier = null;
		}

		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;

		_userIdSupplier = null;
	}

	@JsonIgnore
	public void setUserId(
		UnsafeSupplier<Long, Exception> userIdUnsafeSupplier) {

		_userIdSupplier = () -> {
			try {
				return userIdUnsafeSupplier.get();
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
		description = "Liferay user who must sign. Required when creating a signature request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long userId;

	@JsonIgnore
	private Supplier<Long> _userIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SignatureRequestRecipient)) {
			return false;
		}

		SignatureRequestRecipient signatureRequestRecipient =
			(SignatureRequestRecipient)object;

		return Objects.equals(toString(), signatureRequestRecipient.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String emailAddress = getEmailAddress();

		if (emailAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(emailAddress));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Date sentDate = getSentDate();

		if (sentDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sentDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(sentDate));

			sb.append("\"");
		}

		String status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(status));

			sb.append("\"");
		}

		Date statusDate = getStatusDate();

		if (statusDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(statusDate));

			sb.append("\"");
		}

		Long userId = getUserId();

		if (userId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(userId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.digital.signature.rest.dto.v1_0.SignatureRequestRecipient",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:429761350