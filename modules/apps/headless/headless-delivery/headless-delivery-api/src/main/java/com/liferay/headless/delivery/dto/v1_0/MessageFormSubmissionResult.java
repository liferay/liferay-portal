/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a definition of a submission result of type message.",
	value = "MessageFormSubmissionResult"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "MessageFormSubmissionResult")
public class MessageFormSubmissionResult implements Serializable {

	public static MessageFormSubmissionResult toDTO(String json) {
		return ObjectMapperUtil.readValue(
			MessageFormSubmissionResult.class, json);
	}

	public static MessageFormSubmissionResult unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			MessageFormSubmissionResult.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized submission of message type."
	)
	@Valid
	public FragmentInlineValue getMessage() {
		if (_messageSupplier != null) {
			message = _messageSupplier.get();

			_messageSupplier = null;
		}

		return message;
	}

	public void setMessage(FragmentInlineValue message) {
		this.message = message;

		_messageSupplier = null;
	}

	@JsonIgnore
	public void setMessage(
		UnsafeSupplier<FragmentInlineValue, Exception> messageUnsafeSupplier) {

		_messageSupplier = () -> {
			try {
				return messageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized submission of message type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentInlineValue message;

	@JsonIgnore
	private Supplier<FragmentInlineValue> _messageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message form submission type (embedded, none)."
	)
	@JsonGetter("messageType")
	@Valid
	public MessageType getMessageType() {
		if (_messageTypeSupplier != null) {
			messageType = _messageTypeSupplier.get();

			_messageTypeSupplier = null;
		}

		return messageType;
	}

	@JsonIgnore
	public String getMessageTypeAsString() {
		MessageType messageType = getMessageType();

		if (messageType == null) {
			return null;
		}

		return messageType.toString();
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;

		_messageTypeSupplier = null;
	}

	@JsonIgnore
	public void setMessageType(
		UnsafeSupplier<MessageType, Exception> messageTypeUnsafeSupplier) {

		_messageTypeSupplier = () -> {
			try {
				return messageTypeUnsafeSupplier.get();
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
		description = "The message form submission type (embedded, none)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MessageType messageType;

	@JsonIgnore
	private Supplier<MessageType> _messageTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShowNotification() {
		if (_showNotificationSupplier != null) {
			showNotification = _showNotificationSupplier.get();

			_showNotificationSupplier = null;
		}

		return showNotification;
	}

	public void setShowNotification(Boolean showNotification) {
		this.showNotification = showNotification;

		_showNotificationSupplier = null;
	}

	@JsonIgnore
	public void setShowNotification(
		UnsafeSupplier<Boolean, Exception> showNotificationUnsafeSupplier) {

		_showNotificationSupplier = () -> {
			try {
				return showNotificationUnsafeSupplier.get();
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
	protected Boolean showNotification;

	@JsonIgnore
	private Supplier<Boolean> _showNotificationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MessageFormSubmissionResult)) {
			return false;
		}

		MessageFormSubmissionResult messageFormSubmissionResult =
			(MessageFormSubmissionResult)object;

		return Objects.equals(
			toString(), messageFormSubmissionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FragmentInlineValue message = getMessage();

		if (message != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append(String.valueOf(message));
		}

		MessageType messageType = getMessageType();

		if (messageType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageType\": ");

			sb.append("\"");

			sb.append(messageType);

			sb.append("\"");
		}

		Boolean showNotification = getShowNotification();

		if (showNotification != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showNotification\": ");

			sb.append(showNotification);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.MessageFormSubmissionResult",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("MessageType")
	public static enum MessageType {

		EMBEDDED("Embedded"), NONE("None");

		@JsonCreator
		public static MessageType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (MessageType messageType : values()) {
				if (Objects.equals(messageType.getValue(), value)) {
					return messageType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private MessageType(String value) {
			_value = value;
		}

		private final String _value;

	}

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