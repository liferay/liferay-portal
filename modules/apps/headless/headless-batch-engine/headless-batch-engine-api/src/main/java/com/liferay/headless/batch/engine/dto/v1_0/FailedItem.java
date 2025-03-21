/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
@GraphQLName("FailedItem")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FailedItem")
public class FailedItem implements Serializable {

	public static FailedItem toDTO(String json) {
		return ObjectMapperUtil.readValue(FailedItem.class, json);
	}

	public static FailedItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FailedItem.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The item which failed to be imported."
	)
	public String getItem() {
		if (_itemSupplier != null) {
			item = _itemSupplier.get();

			_itemSupplier = null;
		}

		return item;
	}

	public void setItem(String item) {
		this.item = item;

		_itemSupplier = null;
	}

	@JsonIgnore
	public void setItem(UnsafeSupplier<String, Exception> itemUnsafeSupplier) {
		_itemSupplier = () -> {
			try {
				return itemUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The item which failed to be imported.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String item;

	@JsonIgnore
	private Supplier<String> _itemSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Position of the item in the import file. For CSV file it will represent a line number, for JSON file it will represent an array index etc."
	)
	public Integer getItemIndex() {
		if (_itemIndexSupplier != null) {
			itemIndex = _itemIndexSupplier.get();

			_itemIndexSupplier = null;
		}

		return itemIndex;
	}

	public void setItemIndex(Integer itemIndex) {
		this.itemIndex = itemIndex;

		_itemIndexSupplier = null;
	}

	@JsonIgnore
	public void setItemIndex(
		UnsafeSupplier<Integer, Exception> itemIndexUnsafeSupplier) {

		_itemIndexSupplier = () -> {
			try {
				return itemIndexUnsafeSupplier.get();
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
		description = "Position of the item in the import file. For CSV file it will represent a line number, for JSON file it will represent an array index etc."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer itemIndex;

	@JsonIgnore
	private Supplier<Integer> _itemIndexSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Message describing the reason of import failure."
	)
	public String getMessage() {
		if (_messageSupplier != null) {
			message = _messageSupplier.get();

			_messageSupplier = null;
		}

		return message;
	}

	public void setMessage(String message) {
		this.message = message;

		_messageSupplier = null;
	}

	@JsonIgnore
	public void setMessage(
		UnsafeSupplier<String, Exception> messageUnsafeSupplier) {

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

	@GraphQLField(
		description = "Message describing the reason of import failure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String message;

	@JsonIgnore
	private Supplier<String> _messageSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FailedItem)) {
			return false;
		}

		FailedItem failedItem = (FailedItem)object;

		return Objects.equals(toString(), failedItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String item = getItem();

		if (item != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"item\": ");

			sb.append("\"");

			sb.append(_escape(item));

			sb.append("\"");
		}

		Integer itemIndex = getItemIndex();

		if (itemIndex != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemIndex\": ");

			sb.append(itemIndex);
		}

		String message = getMessage();

		if (message != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append("\"");

			sb.append(_escape(message));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.batch.engine.dto.v1_0.FailedItem",
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