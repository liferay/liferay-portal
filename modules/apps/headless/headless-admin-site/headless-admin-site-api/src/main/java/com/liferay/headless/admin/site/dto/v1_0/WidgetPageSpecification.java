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
	description = "A page specification of a widget page. A widget page contains always 1 page specification in published status.",
	value = "WidgetPageSpecification"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetPageSpecification")
public class WidgetPageSpecification
	extends PageSpecification implements Serializable {

	public static WidgetPageSpecification toDTO(String json) {
		return ObjectMapperUtil.readValue(WidgetPageSpecification.class, json);
	}

	public static WidgetPageSpecification unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WidgetPageSpecification.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The sections of a widget page."
	)
	@Valid
	public WidgetPageSection[] getWidgetPageSections() {
		if (_widgetPageSectionsSupplier != null) {
			widgetPageSections = _widgetPageSectionsSupplier.get();

			_widgetPageSectionsSupplier = null;
		}

		return widgetPageSections;
	}

	public void setWidgetPageSections(WidgetPageSection[] widgetPageSections) {
		this.widgetPageSections = widgetPageSections;

		_widgetPageSectionsSupplier = null;
	}

	@JsonIgnore
	public void setWidgetPageSections(
		UnsafeSupplier<WidgetPageSection[], Exception>
			widgetPageSectionsUnsafeSupplier) {

		_widgetPageSectionsSupplier = () -> {
			try {
				return widgetPageSectionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The sections of a widget page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WidgetPageSection[] widgetPageSections;

	@JsonIgnore
	private Supplier<WidgetPageSection[]> _widgetPageSectionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageSpecification)) {
			return false;
		}

		WidgetPageSpecification widgetPageSpecification =
			(WidgetPageSpecification)object;

		return Objects.equals(toString(), widgetPageSpecification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		WidgetPageSection[] widgetPageSections = getWidgetPageSections();

		if (widgetPageSections != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPageSections\": ");

			sb.append("[");

			for (int i = 0; i < widgetPageSections.length; i++) {
				sb.append(String.valueOf(widgetPageSections[i]));

				if ((i + 1) < widgetPageSections.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Settings settings = getSettings();

		if (settings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(settings));
		}

		String siteTemplatePageSpecificationExternalReferenceCode =
			getSiteTemplatePageSpecificationExternalReferenceCode();

		if (siteTemplatePageSpecificationExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"siteTemplatePageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(siteTemplatePageSpecificationExternalReferenceCode));

			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(status);

			sb.append("\"");
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.WidgetPageSpecification",
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