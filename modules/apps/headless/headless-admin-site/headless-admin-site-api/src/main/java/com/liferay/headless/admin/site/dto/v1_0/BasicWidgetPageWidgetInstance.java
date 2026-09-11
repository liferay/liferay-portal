/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A basic (non nested applications) widget instance in a widget page.",
	value = "BasicWidgetPageWidgetInstance"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A basic (non nested applications) widget instance in a widget page."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BasicWidgetPageWidgetInstance")
public class BasicWidgetPageWidgetInstance
	extends WidgetPageWidgetInstance implements Serializable {

	public static BasicWidgetPageWidgetInstance toDTO(String json) {
		return ObjectMapperUtil.readValue(
			BasicWidgetPageWidgetInstance.class, json);
	}

	public static BasicWidgetPageWidgetInstance unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			BasicWidgetPageWidgetInstance.class, json);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BasicWidgetPageWidgetInstance)) {
			return false;
		}

		BasicWidgetPageWidgetInstance basicWidgetPageWidgetInstance =
			(BasicWidgetPageWidgetInstance)object;

		return Objects.equals(
			toString(), basicWidgetPageWidgetInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		String parentSectionId = getParentSectionId();

		if (parentSectionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSectionId\": ");

			sb.append("\"");

			sb.append(_escape(parentSectionId));

			sb.append("\"");
		}

		String parentWidgetInstanceExternalReferenceCode =
			getParentWidgetInstanceExternalReferenceCode();

		if (parentWidgetInstanceExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentWidgetInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentWidgetInstanceExternalReferenceCode));

			sb.append("\"");
		}

		Integer position = getPosition();

		if (position != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(position);
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

		Map<String, Object> widgetConfig = getWidgetConfig();

		if (widgetConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetConfig\": ");

			sb.append(_toJSON(widgetConfig));
		}

		String widgetInstanceId = getWidgetInstanceId();

		if (widgetInstanceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstanceId\": ");

			sb.append("\"");

			sb.append(_escape(widgetInstanceId));

			sb.append("\"");
		}

		WidgetLookAndFeelConfig widgetLookAndFeelConfig =
			getWidgetLookAndFeelConfig();

		if (widgetLookAndFeelConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetLookAndFeelConfig\": ");

			sb.append(String.valueOf(widgetLookAndFeelConfig));
		}

		String widgetName = getWidgetName();

		if (widgetName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetName\": ");

			sb.append("\"");

			sb.append(_escape(widgetName));

			sb.append("\"");
		}

		WidgetPermission[] widgetPermissions = getWidgetPermissions();

		if (widgetPermissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPermissions\": ");

			sb.append("[");

			for (int i = 0; i < widgetPermissions.length; i++) {
				sb.append(String.valueOf(widgetPermissions[i]));

				if ((i + 1) < widgetPermissions.length) {
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.BasicWidgetPageWidgetInstance",
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
// LIFERAY-REST-BUILDER-HASH:79125448