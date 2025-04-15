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
import jakarta.validation.constraints.DecimalMin;

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
	description = "A widget instance in a widget page.",
	value = "WidgetPageWidgetInstance"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetPageWidgetInstance")
public class WidgetPageWidgetInstance implements Serializable {

	public static WidgetPageWidgetInstance toDTO(String json) {
		return ObjectMapperUtil.readValue(WidgetPageWidgetInstance.class, json);
	}

	public static WidgetPageWidgetInstance unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WidgetPageWidgetInstance.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference code of the widget instance."
	)
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

	@GraphQLField(
		description = "The external reference code of the widget instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The section's ID of the widget page or the nested application widget instance this widget belongs to."
	)
	public String getParentSectionId() {
		if (_parentSectionIdSupplier != null) {
			parentSectionId = _parentSectionIdSupplier.get();

			_parentSectionIdSupplier = null;
		}

		return parentSectionId;
	}

	public void setParentSectionId(String parentSectionId) {
		this.parentSectionId = parentSectionId;

		_parentSectionIdSupplier = null;
	}

	@JsonIgnore
	public void setParentSectionId(
		UnsafeSupplier<String, Exception> parentSectionIdUnsafeSupplier) {

		_parentSectionIdSupplier = () -> {
			try {
				return parentSectionIdUnsafeSupplier.get();
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
		description = "The section's ID of the widget page or the nested application widget instance this widget belongs to."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentSectionId;

	@JsonIgnore
	private Supplier<String> _parentSectionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference code of the parent widget instance. Only available if this widget instance is within a nested applications widget instance."
	)
	public String getParentWidgetInstanceExternalReferenceCode() {
		if (_parentWidgetInstanceExternalReferenceCodeSupplier != null) {
			parentWidgetInstanceExternalReferenceCode =
				_parentWidgetInstanceExternalReferenceCodeSupplier.get();

			_parentWidgetInstanceExternalReferenceCodeSupplier = null;
		}

		return parentWidgetInstanceExternalReferenceCode;
	}

	public void setParentWidgetInstanceExternalReferenceCode(
		String parentWidgetInstanceExternalReferenceCode) {

		this.parentWidgetInstanceExternalReferenceCode =
			parentWidgetInstanceExternalReferenceCode;

		_parentWidgetInstanceExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentWidgetInstanceExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentWidgetInstanceExternalReferenceCodeUnsafeSupplier) {

		_parentWidgetInstanceExternalReferenceCodeSupplier = () -> {
			try {
				return parentWidgetInstanceExternalReferenceCodeUnsafeSupplier.
					get();
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
		description = "The external reference code of the parent widget instance. Only available if this widget instance is within a nested applications widget instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentWidgetInstanceExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _parentWidgetInstanceExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The 0-based position this widget instance occupies with respect to its siblings (0 for first child, 1 for second child, ...). If not specified when creating a widget instance the widget instance will be added at the last valid position."
	)
	public Integer getPosition() {
		if (_positionSupplier != null) {
			position = _positionSupplier.get();

			_positionSupplier = null;
		}

		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;

		_positionSupplier = null;
	}

	@JsonIgnore
	public void setPosition(
		UnsafeSupplier<Integer, Exception> positionUnsafeSupplier) {

		_positionSupplier = () -> {
			try {
				return positionUnsafeSupplier.get();
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
		description = "The 0-based position this widget instance occupies with respect to its siblings (0 for first child, 1 for second child, ...). If not specified when creating a widget instance the widget instance will be added at the last valid position."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer position;

	@JsonIgnore
	private Supplier<Integer> _positionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The configuration keys and values of the widget instance."
	)
	@Valid
	public Map<String, Object> getWidgetConfig() {
		if (_widgetConfigSupplier != null) {
			widgetConfig = _widgetConfigSupplier.get();

			_widgetConfigSupplier = null;
		}

		return widgetConfig;
	}

	public void setWidgetConfig(Map<String, Object> widgetConfig) {
		this.widgetConfig = widgetConfig;

		_widgetConfigSupplier = null;
	}

	@JsonIgnore
	public void setWidgetConfig(
		UnsafeSupplier<Map<String, Object>, Exception>
			widgetConfigUnsafeSupplier) {

		_widgetConfigSupplier = () -> {
			try {
				return widgetConfigUnsafeSupplier.get();
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
		description = "The configuration keys and values of the widget instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Object> widgetConfig;

	@JsonIgnore
	private Supplier<Map<String, Object>> _widgetConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The widget instance's ID."
	)
	public String getWidgetInstanceId() {
		if (_widgetInstanceIdSupplier != null) {
			widgetInstanceId = _widgetInstanceIdSupplier.get();

			_widgetInstanceIdSupplier = null;
		}

		return widgetInstanceId;
	}

	public void setWidgetInstanceId(String widgetInstanceId) {
		this.widgetInstanceId = widgetInstanceId;

		_widgetInstanceIdSupplier = null;
	}

	@JsonIgnore
	public void setWidgetInstanceId(
		UnsafeSupplier<String, Exception> widgetInstanceIdUnsafeSupplier) {

		_widgetInstanceIdSupplier = () -> {
			try {
				return widgetInstanceIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The widget instance's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String widgetInstanceId;

	@JsonIgnore
	private Supplier<String> _widgetInstanceIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The widget instance's look and feel configuration."
	)
	@Valid
	public WidgetLookAndFeelConfig[] getWidgetLookAndFeelConfig() {
		if (_widgetLookAndFeelConfigSupplier != null) {
			widgetLookAndFeelConfig = _widgetLookAndFeelConfigSupplier.get();

			_widgetLookAndFeelConfigSupplier = null;
		}

		return widgetLookAndFeelConfig;
	}

	public void setWidgetLookAndFeelConfig(
		WidgetLookAndFeelConfig[] widgetLookAndFeelConfig) {

		this.widgetLookAndFeelConfig = widgetLookAndFeelConfig;

		_widgetLookAndFeelConfigSupplier = null;
	}

	@JsonIgnore
	public void setWidgetLookAndFeelConfig(
		UnsafeSupplier<WidgetLookAndFeelConfig[], Exception>
			widgetLookAndFeelConfigUnsafeSupplier) {

		_widgetLookAndFeelConfigSupplier = () -> {
			try {
				return widgetLookAndFeelConfigUnsafeSupplier.get();
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
		description = "The widget instance's look and feel configuration."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WidgetLookAndFeelConfig[] widgetLookAndFeelConfig;

	@JsonIgnore
	private Supplier<WidgetLookAndFeelConfig[]>
		_widgetLookAndFeelConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The widget instance's name."
	)
	public String getWidgetName() {
		if (_widgetNameSupplier != null) {
			widgetName = _widgetNameSupplier.get();

			_widgetNameSupplier = null;
		}

		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;

		_widgetNameSupplier = null;
	}

	@JsonIgnore
	public void setWidgetName(
		UnsafeSupplier<String, Exception> widgetNameUnsafeSupplier) {

		_widgetNameSupplier = () -> {
			try {
				return widgetNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The widget instance's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String widgetName;

	@JsonIgnore
	private Supplier<String> _widgetNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The widget instance's permissions."
	)
	@Valid
	public WidgetPermission[] getWidgetPermissions() {
		if (_widgetPermissionsSupplier != null) {
			widgetPermissions = _widgetPermissionsSupplier.get();

			_widgetPermissionsSupplier = null;
		}

		return widgetPermissions;
	}

	public void setWidgetPermissions(WidgetPermission[] widgetPermissions) {
		this.widgetPermissions = widgetPermissions;

		_widgetPermissionsSupplier = null;
	}

	@JsonIgnore
	public void setWidgetPermissions(
		UnsafeSupplier<WidgetPermission[], Exception>
			widgetPermissionsUnsafeSupplier) {

		_widgetPermissionsSupplier = () -> {
			try {
				return widgetPermissionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The widget instance's permissions.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WidgetPermission[] widgetPermissions;

	@JsonIgnore
	private Supplier<WidgetPermission[]> _widgetPermissionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageWidgetInstance)) {
			return false;
		}

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			(WidgetPageWidgetInstance)object;

		return Objects.equals(toString(), widgetPageWidgetInstance.toString());
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

		WidgetLookAndFeelConfig[] widgetLookAndFeelConfig =
			getWidgetLookAndFeelConfig();

		if (widgetLookAndFeelConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetLookAndFeelConfig\": ");

			sb.append("[");

			for (int i = 0; i < widgetLookAndFeelConfig.length; i++) {
				sb.append(String.valueOf(widgetLookAndFeelConfig[i]));

				if ((i + 1) < widgetLookAndFeelConfig.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance",
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