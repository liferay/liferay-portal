/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A fragment field with an action.",
	value = "FragmentFieldAction"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentFieldAction")
public class FragmentFieldAction implements Serializable {

	public static FragmentFieldAction toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentFieldAction.class, json);
	}

	public static FragmentFieldAction unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FragmentFieldAction.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment field's action. Must be mapped to an external value."
	)
	@Valid
	public Object getAction() {
		if (_actionSupplier != null) {
			action = _actionSupplier.get();

			_actionSupplier = null;
		}

		return action;
	}

	public void setAction(Object action) {
		this.action = action;

		_actionSupplier = null;
	}

	@JsonIgnore
	public void setAction(
		UnsafeSupplier<Object, Exception> actionUnsafeSupplier) {

		_actionSupplier = () -> {
			try {
				return actionUnsafeSupplier.get();
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
		description = "The fragment field's action. Must be mapped to an external value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object action;

	@JsonIgnore
	private Supplier<Object> _actionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The action execution result in case the action fails."
	)
	@Valid
	public ActionExecutionResult getOnError() {
		if (_onErrorSupplier != null) {
			onError = _onErrorSupplier.get();

			_onErrorSupplier = null;
		}

		return onError;
	}

	public void setOnError(ActionExecutionResult onError) {
		this.onError = onError;

		_onErrorSupplier = null;
	}

	@JsonIgnore
	public void setOnError(
		UnsafeSupplier<ActionExecutionResult, Exception>
			onErrorUnsafeSupplier) {

		_onErrorSupplier = () -> {
			try {
				return onErrorUnsafeSupplier.get();
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
		description = "The action execution result in case the action fails."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ActionExecutionResult onError;

	@JsonIgnore
	private Supplier<ActionExecutionResult> _onErrorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The action execution result in case the action succeeds."
	)
	@Valid
	public ActionExecutionResult getOnSuccess() {
		if (_onSuccessSupplier != null) {
			onSuccess = _onSuccessSupplier.get();

			_onSuccessSupplier = null;
		}

		return onSuccess;
	}

	public void setOnSuccess(ActionExecutionResult onSuccess) {
		this.onSuccess = onSuccess;

		_onSuccessSupplier = null;
	}

	@JsonIgnore
	public void setOnSuccess(
		UnsafeSupplier<ActionExecutionResult, Exception>
			onSuccessUnsafeSupplier) {

		_onSuccessSupplier = () -> {
			try {
				return onSuccessUnsafeSupplier.get();
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
		description = "The action execution result in case the action succeeds."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ActionExecutionResult onSuccess;

	@JsonIgnore
	private Supplier<ActionExecutionResult> _onSuccessSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment field's text."
	)
	@Valid
	public Object getText() {
		if (_textSupplier != null) {
			text = _textSupplier.get();

			_textSupplier = null;
		}

		return text;
	}

	public void setText(Object text) {
		this.text = text;

		_textSupplier = null;
	}

	@JsonIgnore
	public void setText(UnsafeSupplier<Object, Exception> textUnsafeSupplier) {
		_textSupplier = () -> {
			try {
				return textUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment field's text.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object text;

	@JsonIgnore
	private Supplier<Object> _textSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentFieldAction)) {
			return false;
		}

		FragmentFieldAction fragmentFieldAction = (FragmentFieldAction)object;

		return Objects.equals(toString(), fragmentFieldAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object action = getAction();

		if (action != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"action\": ");

			if (action instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)action));
			}
			else if (action instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)action));
				sb.append("\"");
			}
			else {
				sb.append(action);
			}
		}

		ActionExecutionResult onError = getOnError();

		if (onError != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onError\": ");

			sb.append(String.valueOf(onError));
		}

		ActionExecutionResult onSuccess = getOnSuccess();

		if (onSuccess != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onSuccess\": ");

			sb.append(String.valueOf(onSuccess));
		}

		Object text = getText();

		if (text != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text\": ");

			if (text instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)text));
			}
			else if (text instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)text));
				sb.append("\"");
			}
			else {
				sb.append(text);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentFieldAction",
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