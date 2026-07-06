/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
@GraphQLName("Chatbot")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Chatbot")
public class Chatbot implements Serializable {

	public static Chatbot toDTO(String json) {
		return ObjectMapperUtil.readValue(Chatbot.class, json);
	}

	public static Chatbot unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Chatbot.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, ?> getAvatar() {
		if (_avatarSupplier != null) {
			avatar = _avatarSupplier.get();

			_avatarSupplier = null;
		}

		return avatar;
	}

	public void setAvatar(Map<String, ?> avatar) {
		this.avatar = avatar;

		_avatarSupplier = null;
	}

	@JsonIgnore
	public void setAvatar(
		UnsafeSupplier<Map<String, ?>, Exception> avatarUnsafeSupplier) {

		_avatarSupplier = () -> {
			try {
				return avatarUnsafeSupplier.get();
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
	protected Map<String, ?> avatar;

	@JsonIgnore
	private Supplier<Map<String, ?>> _avatarSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDisclaimerMessage() {
		if (_disclaimerMessageSupplier != null) {
			disclaimerMessage = _disclaimerMessageSupplier.get();

			_disclaimerMessageSupplier = null;
		}

		return disclaimerMessage;
	}

	public void setDisclaimerMessage(String disclaimerMessage) {
		this.disclaimerMessage = disclaimerMessage;

		_disclaimerMessageSupplier = null;
	}

	@JsonIgnore
	public void setDisclaimerMessage(
		UnsafeSupplier<String, Exception> disclaimerMessageUnsafeSupplier) {

		_disclaimerMessageSupplier = () -> {
			try {
				return disclaimerMessageUnsafeSupplier.get();
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
	protected String disclaimerMessage;

	@JsonIgnore
	private Supplier<String> _disclaimerMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@io.swagger.v3.oas.annotations.media.Schema
	public String getIntroMessage() {
		if (_introMessageSupplier != null) {
			introMessage = _introMessageSupplier.get();

			_introMessageSupplier = null;
		}

		return introMessage;
	}

	public void setIntroMessage(String introMessage) {
		this.introMessage = introMessage;

		_introMessageSupplier = null;
	}

	@JsonIgnore
	public void setIntroMessage(
		UnsafeSupplier<String, Exception> introMessageUnsafeSupplier) {

		_introMessageSupplier = () -> {
			try {
				return introMessageUnsafeSupplier.get();
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
	protected String introMessage;

	@JsonIgnore
	private Supplier<String> _introMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getNotificationMessage() {
		if (_notificationMessageSupplier != null) {
			notificationMessage = _notificationMessageSupplier.get();

			_notificationMessageSupplier = null;
		}

		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;

		_notificationMessageSupplier = null;
	}

	@JsonIgnore
	public void setNotificationMessage(
		UnsafeSupplier<String, Exception> notificationMessageUnsafeSupplier) {

		_notificationMessageSupplier = () -> {
			try {
				return notificationMessageUnsafeSupplier.get();
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
	protected String notificationMessage;

	@JsonIgnore
	private Supplier<String> _notificationMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPlaceholderMessage() {
		if (_placeholderMessageSupplier != null) {
			placeholderMessage = _placeholderMessageSupplier.get();

			_placeholderMessageSupplier = null;
		}

		return placeholderMessage;
	}

	public void setPlaceholderMessage(String placeholderMessage) {
		this.placeholderMessage = placeholderMessage;

		_placeholderMessageSupplier = null;
	}

	@JsonIgnore
	public void setPlaceholderMessage(
		UnsafeSupplier<String, Exception> placeholderMessageUnsafeSupplier) {

		_placeholderMessageSupplier = () -> {
			try {
				return placeholderMessageUnsafeSupplier.get();
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
	protected String placeholderMessage;

	@JsonIgnore
	private Supplier<String> _placeholderMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
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
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Chatbot)) {
			return false;
		}

		Chatbot chatbot = (Chatbot)object;

		return Objects.equals(toString(), chatbot.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Map<String, ?> avatar = getAvatar();

		if (avatar != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"avatar\": ");

			sb.append(_toJSON(avatar));
		}

		String disclaimerMessage = getDisclaimerMessage();

		if (disclaimerMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disclaimerMessage\": ");

			sb.append("\"");

			sb.append(_escape(disclaimerMessage));

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

		String introMessage = getIntroMessage();

		if (introMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"introMessage\": ");

			sb.append("\"");

			sb.append(_escape(introMessage));

			sb.append("\"");
		}

		String notificationMessage = getNotificationMessage();

		if (notificationMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notificationMessage\": ");

			sb.append("\"");

			sb.append(_escape(notificationMessage));

			sb.append("\"");
		}

		String placeholderMessage = getPlaceholderMessage();

		if (placeholderMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placeholderMessage\": ");

			sb.append("\"");

			sb.append(_escape(placeholderMessage));

			sb.append("\"");
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.rest.dto.v1_0.Chatbot",
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
// LIFERAY-REST-BUILDER-HASH:2078847285