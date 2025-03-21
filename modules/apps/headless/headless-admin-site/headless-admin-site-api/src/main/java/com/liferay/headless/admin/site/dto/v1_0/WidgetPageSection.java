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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A widget page section.", value = "WidgetPageSection"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetPageSection")
public class WidgetPageSection implements Serializable {

	public static WidgetPageSection toDTO(String json) {
		return ObjectMapperUtil.readValue(WidgetPageSection.class, json);
	}

	public static WidgetPageSection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(WidgetPageSection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the widget page section is customizable or not."
	)
	public Boolean getCustomizable() {
		if (_customizableSupplier != null) {
			customizable = _customizableSupplier.get();

			_customizableSupplier = null;
		}

		return customizable;
	}

	public void setCustomizable(Boolean customizable) {
		this.customizable = customizable;

		_customizableSupplier = null;
	}

	@JsonIgnore
	public void setCustomizable(
		UnsafeSupplier<Boolean, Exception> customizableUnsafeSupplier) {

		_customizableSupplier = () -> {
			try {
				return customizableUnsafeSupplier.get();
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
		description = "A flag that indicates whether the widget page section is customizable or not."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean customizable;

	@JsonIgnore
	private Supplier<Boolean> _customizableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The widget page section's id."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The widget page section's id.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the widget instances within this page section."
	)
	@Valid
	public WidgetPageWidgetInstance[] getWidgetPageWidgetInstances() {
		if (_widgetPageWidgetInstancesSupplier != null) {
			widgetPageWidgetInstances =
				_widgetPageWidgetInstancesSupplier.get();

			_widgetPageWidgetInstancesSupplier = null;
		}

		return widgetPageWidgetInstances;
	}

	public void setWidgetPageWidgetInstances(
		WidgetPageWidgetInstance[] widgetPageWidgetInstances) {

		this.widgetPageWidgetInstances = widgetPageWidgetInstances;

		_widgetPageWidgetInstancesSupplier = null;
	}

	@JsonIgnore
	public void setWidgetPageWidgetInstances(
		UnsafeSupplier<WidgetPageWidgetInstance[], Exception>
			widgetPageWidgetInstancesUnsafeSupplier) {

		_widgetPageWidgetInstancesSupplier = () -> {
			try {
				return widgetPageWidgetInstancesUnsafeSupplier.get();
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
		description = "A list of the widget instances within this page section."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WidgetPageWidgetInstance[] widgetPageWidgetInstances;

	@JsonIgnore
	private Supplier<WidgetPageWidgetInstance[]>
		_widgetPageWidgetInstancesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageSection)) {
			return false;
		}

		WidgetPageSection widgetPageSection = (WidgetPageSection)object;

		return Objects.equals(toString(), widgetPageSection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean customizable = getCustomizable();

		if (customizable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customizable\": ");

			sb.append(customizable);
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		WidgetPageWidgetInstance[] widgetPageWidgetInstances =
			getWidgetPageWidgetInstances();

		if (widgetPageWidgetInstances != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPageWidgetInstances\": ");

			sb.append("[");

			for (int i = 0; i < widgetPageWidgetInstances.length; i++) {
				sb.append(String.valueOf(widgetPageWidgetInstances[i]));

				if ((i + 1) < widgetPageWidgetInstances.length) {
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.WidgetPageSection",
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