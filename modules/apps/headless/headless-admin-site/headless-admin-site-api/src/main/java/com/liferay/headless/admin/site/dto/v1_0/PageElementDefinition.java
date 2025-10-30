/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The page element definition.",
	value = "PageElementDefinition"
)
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(
			name = "CollectionDisplay",
			value = CollectionDisplayPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "CollectionItem",
			value = CollectionItemPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "Container", value = ContainerPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "DropZone", value = DropZonePageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "Form", value = FormPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "FormStep", value = FormStepPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "FormStepContainer",
			value = FormStepContainerPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "Fragment",
			value = FragmentInstancePageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "FragmentComposition",
			value = FragmentCompositionInstancePageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "FragmentDropZone",
			value = FragmentDropZonePageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "Grid", value = GridPageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "Module", value = ModulePageElementDefinition.class
		),
		@JsonSubTypes.Type(
			name = "Widget", value = WidgetInstancePageElementDefinition.class
		)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.PROPERTY, property = "type",
	use = JsonTypeInfo.Id.NAME, visible = true
)
@XmlRootElement(name = "PageElementDefinition")
public abstract class PageElementDefinition implements Serializable {

	public static PageElementDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(PageElementDefinition.class, json);
	}

	public static PageElementDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageElementDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page element definition's type (collection display, collection item, container, drop zone, form, form step, form step container, fragment, fragment composition, fragment drop zone, grid, module or widget)."
	)
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
		description = "The page element definition's type (collection display, collection item, container, drop zone, form, form step, form step container, fragment, fragment composition, fragment drop zone, grid, module or widget)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageElementDefinition)) {
			return false;
		}

		PageElementDefinition pageElementDefinition =
			(PageElementDefinition)object;

		return Objects.equals(toString(), pageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		COLLECTION_DISPLAY("CollectionDisplay"),
		COLLECTION_ITEM("CollectionItem"), CONTAINER("Container"),
		DROP_ZONE("DropZone"), FORM("Form"), FORM_STEP("FormStep"),
		FORM_STEP_CONTAINER("FormStepContainer"), FRAGMENT("Fragment"),
		FRAGMENT_COMPOSITION("FragmentComposition"),
		FRAGMENT_DROP_ZONE("FragmentDropZone"), GRID("Grid"), MODULE("Module"),
		WIDGET("Widget");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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