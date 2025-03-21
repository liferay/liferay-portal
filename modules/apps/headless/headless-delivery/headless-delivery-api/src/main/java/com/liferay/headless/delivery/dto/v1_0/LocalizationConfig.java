/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
@GraphQLName("LocalizationConfig")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "LocalizationConfig")
public class LocalizationConfig implements Serializable {

	public static LocalizationConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(LocalizationConfig.class, json);
	}

	public static LocalizationConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(LocalizationConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The message displayed in the unlocalized fields."
	)
	@Valid
	public FragmentInlineValue getUnlocalizedFieldsMessage() {
		if (_unlocalizedFieldsMessageSupplier != null) {
			unlocalizedFieldsMessage = _unlocalizedFieldsMessageSupplier.get();

			_unlocalizedFieldsMessageSupplier = null;
		}

		return unlocalizedFieldsMessage;
	}

	public void setUnlocalizedFieldsMessage(
		FragmentInlineValue unlocalizedFieldsMessage) {

		this.unlocalizedFieldsMessage = unlocalizedFieldsMessage;

		_unlocalizedFieldsMessageSupplier = null;
	}

	@JsonIgnore
	public void setUnlocalizedFieldsMessage(
		UnsafeSupplier<FragmentInlineValue, Exception>
			unlocalizedFieldsMessageUnsafeSupplier) {

		_unlocalizedFieldsMessageSupplier = () -> {
			try {
				return unlocalizedFieldsMessageUnsafeSupplier.get();
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
		description = "The message displayed in the unlocalized fields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentInlineValue unlocalizedFieldsMessage;

	@JsonIgnore
	private Supplier<FragmentInlineValue> _unlocalizedFieldsMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The state of the unlocalized fields."
	)
	@JsonGetter("unlocalizedFieldsState")
	@Valid
	public UnlocalizedFieldsState getUnlocalizedFieldsState() {
		if (_unlocalizedFieldsStateSupplier != null) {
			unlocalizedFieldsState = _unlocalizedFieldsStateSupplier.get();

			_unlocalizedFieldsStateSupplier = null;
		}

		return unlocalizedFieldsState;
	}

	@JsonIgnore
	public String getUnlocalizedFieldsStateAsString() {
		UnlocalizedFieldsState unlocalizedFieldsState =
			getUnlocalizedFieldsState();

		if (unlocalizedFieldsState == null) {
			return null;
		}

		return unlocalizedFieldsState.toString();
	}

	public void setUnlocalizedFieldsState(
		UnlocalizedFieldsState unlocalizedFieldsState) {

		this.unlocalizedFieldsState = unlocalizedFieldsState;

		_unlocalizedFieldsStateSupplier = null;
	}

	@JsonIgnore
	public void setUnlocalizedFieldsState(
		UnsafeSupplier<UnlocalizedFieldsState, Exception>
			unlocalizedFieldsStateUnsafeSupplier) {

		_unlocalizedFieldsStateSupplier = () -> {
			try {
				return unlocalizedFieldsStateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The state of the unlocalized fields.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected UnlocalizedFieldsState unlocalizedFieldsState;

	@JsonIgnore
	private Supplier<UnlocalizedFieldsState> _unlocalizedFieldsStateSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LocalizationConfig)) {
			return false;
		}

		LocalizationConfig localizationConfig = (LocalizationConfig)object;

		return Objects.equals(toString(), localizationConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FragmentInlineValue unlocalizedFieldsMessage =
			getUnlocalizedFieldsMessage();

		if (unlocalizedFieldsMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unlocalizedFieldsMessage\": ");

			sb.append(String.valueOf(unlocalizedFieldsMessage));
		}

		UnlocalizedFieldsState unlocalizedFieldsState =
			getUnlocalizedFieldsState();

		if (unlocalizedFieldsState != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unlocalizedFieldsState\": ");

			sb.append("\"");

			sb.append(unlocalizedFieldsState);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.LocalizationConfig",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("UnlocalizedFieldsState")
	public static enum UnlocalizedFieldsState {

		DISABLED("Disabled"), READ_ONLY("ReadOnly");

		@JsonCreator
		public static UnlocalizedFieldsState create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (UnlocalizedFieldsState unlocalizedFieldsState : values()) {
				if (Objects.equals(unlocalizedFieldsState.getValue(), value)) {
					return unlocalizedFieldsState;
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

		private UnlocalizedFieldsState(String value) {
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