/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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
	description = "The collection display's template list style.",
	value = "TemplateListStyle"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TemplateListStyle")
public class TemplateListStyle
	extends CollectionDisplayListStyle implements Serializable {

	public static TemplateListStyle toDTO(String json) {
		return ObjectMapperUtil.readValue(TemplateListStyle.class, json);
	}

	public static TemplateListStyle unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TemplateListStyle.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The style className of a list of items in the collection display page element."
	)
	public String getListItemStyleClassName() {
		if (_listItemStyleClassNameSupplier != null) {
			listItemStyleClassName = _listItemStyleClassNameSupplier.get();

			_listItemStyleClassNameSupplier = null;
		}

		return listItemStyleClassName;
	}

	public void setListItemStyleClassName(String listItemStyleClassName) {
		this.listItemStyleClassName = listItemStyleClassName;

		_listItemStyleClassNameSupplier = null;
	}

	@JsonIgnore
	public void setListItemStyleClassName(
		UnsafeSupplier<String, Exception>
			listItemStyleClassNameUnsafeSupplier) {

		_listItemStyleClassNameSupplier = () -> {
			try {
				return listItemStyleClassNameUnsafeSupplier.get();
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
		description = "The style className of a list of items in the collection display page element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String listItemStyleClassName;

	@JsonIgnore
	private Supplier<String> _listItemStyleClassNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The style className of a list in the collection display page element."
	)
	public String getListStyleClassName() {
		if (_listStyleClassNameSupplier != null) {
			listStyleClassName = _listStyleClassNameSupplier.get();

			_listStyleClassNameSupplier = null;
		}

		return listStyleClassName;
	}

	public void setListStyleClassName(String listStyleClassName) {
		this.listStyleClassName = listStyleClassName;

		_listStyleClassNameSupplier = null;
	}

	@JsonIgnore
	public void setListStyleClassName(
		UnsafeSupplier<String, Exception> listStyleClassNameUnsafeSupplier) {

		_listStyleClassNameSupplier = () -> {
			try {
				return listStyleClassNameUnsafeSupplier.get();
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
		description = "The style className of a list in the collection display page element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String listStyleClassName;

	@JsonIgnore
	private Supplier<String> _listStyleClassNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The collection display page element's template key."
	)
	public String getTemplateKey() {
		if (_templateKeySupplier != null) {
			templateKey = _templateKeySupplier.get();

			_templateKeySupplier = null;
		}

		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;

		_templateKeySupplier = null;
	}

	@JsonIgnore
	public void setTemplateKey(
		UnsafeSupplier<String, Exception> templateKeyUnsafeSupplier) {

		_templateKeySupplier = () -> {
			try {
				return templateKeyUnsafeSupplier.get();
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
		description = "The collection display page element's template key."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String templateKey;

	@JsonIgnore
	private Supplier<String> _templateKeySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TemplateListStyle)) {
			return false;
		}

		TemplateListStyle templateListStyle = (TemplateListStyle)object;

		return Objects.equals(toString(), templateListStyle.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String listItemStyleClassName = getListItemStyleClassName();

		if (listItemStyleClassName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listItemStyleClassName\": ");

			sb.append("\"");

			sb.append(_escape(listItemStyleClassName));

			sb.append("\"");
		}

		String listStyleClassName = getListStyleClassName();

		if (listStyleClassName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyleClassName\": ");

			sb.append("\"");

			sb.append(_escape(listStyleClassName));

			sb.append("\"");
		}

		String templateKey = getTemplateKey();

		if (templateKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(templateKey));

			sb.append("\"");
		}

		CollectionDisplayListStyleType collectionDisplayListStyleType =
			getCollectionDisplayListStyleType();

		if (collectionDisplayListStyleType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayListStyleType\": ");

			sb.append("\"");
			sb.append(collectionDisplayListStyleType);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.TemplateListStyle",
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