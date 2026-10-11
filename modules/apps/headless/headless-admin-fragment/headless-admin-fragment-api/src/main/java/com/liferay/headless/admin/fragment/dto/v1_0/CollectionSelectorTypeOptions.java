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
import jakarta.validation.constraints.DecimalMin;

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
	description = "The type-specific options of a `collectionSelector` fragment configuration field.",
	value = "CollectionSelectorTypeOptions"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The type-specific options of a `collectionSelector` fragment configuration field."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CollectionSelectorTypeOptions")
public class CollectionSelectorTypeOptions implements Serializable {

	public static CollectionSelectorTypeOptions toDTO(String json) {
		return ObjectMapperUtil.readValue(
			CollectionSelectorTypeOptions.class, json);
	}

	public static CollectionSelectorTypeOptions unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			CollectionSelectorTypeOptions.class, json);
	}

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
		description = "The subtype of the items the selectable collections contain."
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
		description = "The subtype of the items the selectable collections contain."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String itemSubtype;

	@JsonIgnore
	private Supplier<String> _itemSubtypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of the items the selectable collections contain."
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

	@GraphQLField(
		description = "The type of the items the selectable collections contain."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String itemType;

	@JsonIgnore
	private Supplier<String> _itemTypeSupplier;

	@DecimalMin("1")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of collection items made available to the fragment."
	)
	public Integer getNumberOfItems() {
		if (_numberOfItemsSupplier != null) {
			numberOfItems = _numberOfItemsSupplier.get();

			_numberOfItemsSupplier = null;
		}

		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;

		_numberOfItemsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfItems(
		UnsafeSupplier<Integer, Exception> numberOfItemsUnsafeSupplier) {

		_numberOfItemsSupplier = () -> {
			try {
				return numberOfItemsUnsafeSupplier.get();
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
		description = "The number of collection items made available to the fragment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfItems;

	@JsonIgnore
	private Supplier<Integer> _numberOfItemsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionSelectorTypeOptions)) {
			return false;
		}

		CollectionSelectorTypeOptions collectionSelectorTypeOptions =
			(CollectionSelectorTypeOptions)object;

		return Objects.equals(
			toString(), collectionSelectorTypeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Dependency> dependency = getDependency();

		if (dependency != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(_toJSON(dependency));
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

		Integer numberOfItems = getNumberOfItems();

		if (numberOfItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(numberOfItems);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.CollectionSelectorTypeOptions",
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
// LIFERAY-REST-BUILDER-HASH:1042941723