/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.dto.v1_0;

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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("SuggestionsContributorResults")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SuggestionsContributorResults")
public class SuggestionsContributorResults implements Serializable {

	public static SuggestionsContributorResults toDTO(String json) {
		return ObjectMapperUtil.readValue(
			SuggestionsContributorResults.class, json);
	}

	public static SuggestionsContributorResults unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			SuggestionsContributorResults.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getAttributes() {
		if (_attributesSupplier != null) {
			attributes = _attributesSupplier.get();

			_attributesSupplier = null;
		}

		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;

		_attributesSupplier = null;
	}

	@JsonIgnore
	public void setAttributes(
		UnsafeSupplier<Object, Exception> attributesUnsafeSupplier) {

		_attributesSupplier = () -> {
			try {
				return attributesUnsafeSupplier.get();
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
	protected Object attributes;

	@JsonIgnore
	private Supplier<Object> _attributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDisplayGroupName() {
		if (_displayGroupNameSupplier != null) {
			displayGroupName = _displayGroupNameSupplier.get();

			_displayGroupNameSupplier = null;
		}

		return displayGroupName;
	}

	public void setDisplayGroupName(String displayGroupName) {
		this.displayGroupName = displayGroupName;

		_displayGroupNameSupplier = null;
	}

	@JsonIgnore
	public void setDisplayGroupName(
		UnsafeSupplier<String, Exception> displayGroupNameUnsafeSupplier) {

		_displayGroupNameSupplier = () -> {
			try {
				return displayGroupNameUnsafeSupplier.get();
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
	protected String displayGroupName;

	@JsonIgnore
	private Supplier<String> _displayGroupNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Suggestion[] getSuggestions() {
		if (_suggestionsSupplier != null) {
			suggestions = _suggestionsSupplier.get();

			_suggestionsSupplier = null;
		}

		return suggestions;
	}

	public void setSuggestions(Suggestion[] suggestions) {
		this.suggestions = suggestions;

		_suggestionsSupplier = null;
	}

	@JsonIgnore
	public void setSuggestions(
		UnsafeSupplier<Suggestion[], Exception> suggestionsUnsafeSupplier) {

		_suggestionsSupplier = () -> {
			try {
				return suggestionsUnsafeSupplier.get();
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
	protected Suggestion[] suggestions;

	@JsonIgnore
	private Supplier<Suggestion[]> _suggestionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SuggestionsContributorResults)) {
			return false;
		}

		SuggestionsContributorResults suggestionsContributorResults =
			(SuggestionsContributorResults)object;

		return Objects.equals(
			toString(), suggestionsContributorResults.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object attributes = getAttributes();

		if (attributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			if (attributes instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)attributes));
			}
			else if (attributes instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)attributes));
				sb.append("\"");
			}
			else {
				sb.append(attributes);
			}
		}

		String displayGroupName = getDisplayGroupName();

		if (displayGroupName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayGroupName\": ");

			sb.append("\"");

			sb.append(_escape(displayGroupName));

			sb.append("\"");
		}

		Suggestion[] suggestions = getSuggestions();

		if (suggestions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"suggestions\": ");

			sb.append("[");

			for (int i = 0; i < suggestions.length; i++) {
				sb.append(String.valueOf(suggestions[i]));

				if ((i + 1) < suggestions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorResults",
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