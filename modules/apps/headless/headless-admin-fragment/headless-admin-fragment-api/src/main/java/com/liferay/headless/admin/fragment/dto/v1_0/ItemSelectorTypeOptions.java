/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

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

import java.util.Arrays;
import java.util.Collection;
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
	description = "The type-specific options of an `itemSelector` fragment configuration field.",
	value = "ItemSelectorTypeOptions"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The type-specific options of an `itemSelector` fragment configuration field."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ItemSelectorTypeOptions")
public class ItemSelectorTypeOptions implements Serializable {

	public static ItemSelectorTypeOptions toDTO(String json) {
		return ObjectMapperUtil.readValue(ItemSelectorTypeOptions.class, json);
	}

	public static ItemSelectorTypeOptions unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ItemSelectorTypeOptions.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The class name of the items that can be selected."
	)
	public String getClassName() {
		if (_classNameSupplier != null) {
			className = _classNameSupplier.get();

			_classNameSupplier = null;
		}

		return className;
	}

	public void setClassName(String className) {
		this.className = className;

		_classNameSupplier = null;
	}

	@JsonIgnore
	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		_classNameSupplier = () -> {
			try {
				return classNameUnsafeSupplier.get();
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
		description = "The class name of the items that can be selected."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String className;

	@JsonIgnore
	private Supplier<String> _classNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's visibility conditions, keyed by the name of the field each condition applies to."
	)
	@Valid
	public Map<String, Dependency> getDependency() {
		if (_dependencySupplier != null) {
			dependency = _dependencySupplier.get();

			_dependencySupplier = null;
		}

		return dependency;
	}

	public void setDependency(Map<String, Dependency> dependency) {
		this.dependency = dependency;

		_dependencySupplier = null;
	}

	@JsonIgnore
	public void setDependency(
		UnsafeSupplier<Map<String, Dependency>, Exception>
			dependencyUnsafeSupplier) {

		_dependencySupplier = () -> {
			try {
				return dependencyUnsafeSupplier.get();
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
		description = "The field's visibility conditions, keyed by the name of the field each condition applies to."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Dependency> dependency;

	@JsonIgnore
	private Supplier<Map<String, Dependency>> _dependencySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether a template can be selected for the item."
	)
	public Boolean getEnableSelectTemplate() {
		if (_enableSelectTemplateSupplier != null) {
			enableSelectTemplate = _enableSelectTemplateSupplier.get();

			_enableSelectTemplateSupplier = null;
		}

		return enableSelectTemplate;
	}

	public void setEnableSelectTemplate(Boolean enableSelectTemplate) {
		this.enableSelectTemplate = enableSelectTemplate;

		_enableSelectTemplateSupplier = null;
	}

	@JsonIgnore
	public void setEnableSelectTemplate(
		UnsafeSupplier<Boolean, Exception> enableSelectTemplateUnsafeSupplier) {

		_enableSelectTemplateSupplier = () -> {
			try {
				return enableSelectTemplateUnsafeSupplier.get();
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
		description = "A flag that indicates whether a template can be selected for the item."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean enableSelectTemplate;

	@JsonIgnore
	private Supplier<Boolean> _enableSelectTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The subtype of the items that can be selected."
	)
	public String getItemSubtype() {
		if (_itemSubtypeSupplier != null) {
			itemSubtype = _itemSubtypeSupplier.get();

			_itemSubtypeSupplier = null;
		}

		return itemSubtype;
	}

	public void setItemSubtype(String itemSubtype) {
		this.itemSubtype = itemSubtype;

		_itemSubtypeSupplier = null;
	}

	@JsonIgnore
	public void setItemSubtype(
		UnsafeSupplier<String, Exception> itemSubtypeUnsafeSupplier) {

		_itemSubtypeSupplier = () -> {
			try {
				return itemSubtypeUnsafeSupplier.get();
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
		description = "The subtype of the items that can be selected."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String itemSubtype;

	@JsonIgnore
	private Supplier<String> _itemSubtypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of the items that can be selected."
	)
	public String getItemType() {
		if (_itemTypeSupplier != null) {
			itemType = _itemTypeSupplier.get();

			_itemTypeSupplier = null;
		}

		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;

		_itemTypeSupplier = null;
	}

	@JsonIgnore
	public void setItemType(
		UnsafeSupplier<String, Exception> itemTypeUnsafeSupplier) {

		_itemTypeSupplier = () -> {
			try {
				return itemTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The type of the items that can be selected.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String itemType;

	@JsonIgnore
	private Supplier<String> _itemTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The MIME types of the files that can be selected."
	)
	public String[] getMimeTypes() {
		if (_mimeTypesSupplier != null) {
			mimeTypes = _mimeTypesSupplier.get();

			_mimeTypesSupplier = null;
		}

		return mimeTypes;
	}

	public void setMimeTypes(String[] mimeTypes) {
		this.mimeTypes = mimeTypes;

		_mimeTypesSupplier = null;
	}

	@JsonIgnore
	public void setMimeTypes(
		UnsafeSupplier<String[], Exception> mimeTypesUnsafeSupplier) {

		_mimeTypesSupplier = () -> {
			try {
				return mimeTypesUnsafeSupplier.get();
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
		description = "The MIME types of the files that can be selected."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] mimeTypes;

	@JsonIgnore
	private Supplier<String[]> _mimeTypesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ItemSelectorTypeOptions)) {
			return false;
		}

		ItemSelectorTypeOptions itemSelectorTypeOptions =
			(ItemSelectorTypeOptions)object;

		return Objects.equals(toString(), itemSelectorTypeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String className = getClassName();

		if (className != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(className));

			sb.append("\"");
		}

		Map<String, Dependency> dependency = getDependency();

		if (dependency != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(_toJSON(dependency));
		}

		Boolean enableSelectTemplate = getEnableSelectTemplate();

		if (enableSelectTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableSelectTemplate\": ");

			sb.append(enableSelectTemplate);
		}

		String itemSubtype = getItemSubtype();

		if (itemSubtype != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemSubtype\": ");

			sb.append("\"");

			sb.append(_escape(itemSubtype));

			sb.append("\"");
		}

		String itemType = getItemType();

		if (itemType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemType\": ");

			sb.append("\"");

			sb.append(_escape(itemType));

			sb.append("\"");
		}

		String[] mimeTypes = getMimeTypes();

		if (mimeTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mimeTypes\": ");

			sb.append("[");

			for (int i = 0; i < mimeTypes.length; i++) {
				sb.append("\"");

				sb.append(_escape(mimeTypes[i]));

				sb.append("\"");

				if ((i + 1) < mimeTypes.length) {
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
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.ItemSelectorTypeOptions",
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
// LIFERAY-REST-BUILDER-HASH:-955994626