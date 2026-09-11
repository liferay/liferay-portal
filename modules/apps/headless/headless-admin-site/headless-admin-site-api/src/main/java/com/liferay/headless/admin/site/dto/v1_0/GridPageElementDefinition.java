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
	description = "The page element definition of a row.",
	value = "GridPageElementDefinition"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The page element definition of a row."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "GridPageElementDefinition")
public class GridPageElementDefinition
	extends PageElementDefinition implements Serializable {

	public static GridPageElementDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			GridPageElementDefinition.class, json);
	}

	public static GridPageElementDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			GridPageElementDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The background image."
	)
	@Valid
	public BackgroundImageValue getBackgroundImageValue() {
		if (_backgroundImageValueSupplier != null) {
			backgroundImageValue = _backgroundImageValueSupplier.get();

			_backgroundImageValueSupplier = null;
		}

		return backgroundImageValue;
	}

	public void setBackgroundImageValue(
		BackgroundImageValue backgroundImageValue) {

		this.backgroundImageValue = backgroundImageValue;

		_backgroundImageValueSupplier = null;
	}

	@JsonIgnore
	public void setBackgroundImageValue(
		UnsafeSupplier<BackgroundImageValue, Exception>
			backgroundImageValueUnsafeSupplier) {

		_backgroundImageValueSupplier = () -> {
			try {
				return backgroundImageValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The background image.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BackgroundImageValue backgroundImageValue;

	@JsonIgnore
	private Supplier<BackgroundImageValue> _backgroundImageValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of CSS classes that are applied to the row page element."
	)
	public String[] getCssClasses() {
		if (_cssClassesSupplier != null) {
			cssClasses = _cssClassesSupplier.get();

			_cssClassesSupplier = null;
		}

		return cssClasses;
	}

	public void setCssClasses(String[] cssClasses) {
		this.cssClasses = cssClasses;

		_cssClassesSupplier = null;
	}

	@JsonIgnore
	public void setCssClasses(
		UnsafeSupplier<String[], Exception> cssClassesUnsafeSupplier) {

		_cssClassesSupplier = () -> {
			try {
				return cssClassesUnsafeSupplier.get();
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
		description = "A list of CSS classes that are applied to the row page element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] cssClasses;

	@JsonIgnore
	private Supplier<String[]> _cssClassesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of viewports configuration of the grid page element."
	)
	@Valid
	public GridViewport[] getGridViewports() {
		if (_gridViewportsSupplier != null) {
			gridViewports = _gridViewportsSupplier.get();

			_gridViewportsSupplier = null;
		}

		return gridViewports;
	}

	public void setGridViewports(GridViewport[] gridViewports) {
		this.gridViewports = gridViewports;

		_gridViewportsSupplier = null;
	}

	@JsonIgnore
	public void setGridViewports(
		UnsafeSupplier<GridViewport[], Exception> gridViewportsUnsafeSupplier) {

		_gridViewportsSupplier = () -> {
			try {
				return gridViewportsUnsafeSupplier.get();
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
		description = "A list of viewports configuration of the grid page element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected GridViewport[] gridViewports;

	@JsonIgnore
	private Supplier<GridViewport[]> _gridViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the row page element has gutters."
	)
	public Boolean getGutters() {
		if (_guttersSupplier != null) {
			gutters = _guttersSupplier.get();

			_guttersSupplier = null;
		}

		return gutters;
	}

	public void setGutters(Boolean gutters) {
		this.gutters = gutters;

		_guttersSupplier = null;
	}

	@JsonIgnore
	public void setGutters(
		UnsafeSupplier<Boolean, Exception> guttersUnsafeSupplier) {

		_guttersSupplier = () -> {
			try {
				return guttersUnsafeSupplier.get();
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
		description = "A flag that indicates whether the row page element has gutters."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean gutters;

	@JsonIgnore
	private Supplier<Boolean> _guttersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the row page element is indexed or not."
	)
	public Boolean getIndexed() {
		if (_indexedSupplier != null) {
			indexed = _indexedSupplier.get();

			_indexedSupplier = null;
		}

		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;

		_indexedSupplier = null;
	}

	@JsonIgnore
	public void setIndexed(
		UnsafeSupplier<Boolean, Exception> indexedUnsafeSupplier) {

		_indexedSupplier = () -> {
			try {
				return indexedUnsafeSupplier.get();
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
		description = "A flag that indicates whether the row page element is indexed or not."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean indexed;

	@JsonIgnore
	private Supplier<Boolean> _indexedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a row page element."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The custom name of a row page element.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The row page element's number of modules."
	)
	public Integer getNumberOfModules() {
		if (_numberOfModulesSupplier != null) {
			numberOfModules = _numberOfModulesSupplier.get();

			_numberOfModulesSupplier = null;
		}

		return numberOfModules;
	}

	public void setNumberOfModules(Integer numberOfModules) {
		this.numberOfModules = numberOfModules;

		_numberOfModulesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfModules(
		UnsafeSupplier<Integer, Exception> numberOfModulesUnsafeSupplier) {

		_numberOfModulesSupplier = () -> {
			try {
				return numberOfModulesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The row page element's number of modules.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfModules;

	@JsonIgnore
	private Supplier<Integer> _numberOfModulesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the row page element has reverse order."
	)
	public Boolean getReverseOrder() {
		if (_reverseOrderSupplier != null) {
			reverseOrder = _reverseOrderSupplier.get();

			_reverseOrderSupplier = null;
		}

		return reverseOrder;
	}

	public void setReverseOrder(Boolean reverseOrder) {
		this.reverseOrder = reverseOrder;

		_reverseOrderSupplier = null;
	}

	@JsonIgnore
	public void setReverseOrder(
		UnsafeSupplier<Boolean, Exception> reverseOrderUnsafeSupplier) {

		_reverseOrderSupplier = () -> {
			try {
				return reverseOrderUnsafeSupplier.get();
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
		description = "A flag that indicates whether the row page element has reverse order."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean reverseOrder;

	@JsonIgnore
	private Supplier<Boolean> _reverseOrderSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GridPageElementDefinition)) {
			return false;
		}

		GridPageElementDefinition gridPageElementDefinition =
			(GridPageElementDefinition)object;

		return Objects.equals(toString(), gridPageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		BackgroundImageValue backgroundImageValue = getBackgroundImageValue();

		if (backgroundImageValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundImageValue\": ");

			sb.append(String.valueOf(backgroundImageValue));
		}

		String[] cssClasses = getCssClasses();

		if (cssClasses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0; i < cssClasses.length; i++) {
				sb.append("\"");

				sb.append(_escape(cssClasses[i]));

				sb.append("\"");

				if ((i + 1) < cssClasses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		GridViewport[] gridViewports = getGridViewports();

		if (gridViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gridViewports\": ");

			sb.append("[");

			for (int i = 0; i < gridViewports.length; i++) {
				sb.append(String.valueOf(gridViewports[i]));

				if ((i + 1) < gridViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean gutters = getGutters();

		if (gutters != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gutters\": ");

			sb.append(gutters);
		}

		Boolean indexed = getIndexed();

		if (indexed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(indexed);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Integer numberOfModules = getNumberOfModules();

		if (numberOfModules != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfModules\": ");

			sb.append(numberOfModules);
		}

		Boolean reverseOrder = getReverseOrder();

		if (reverseOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverseOrder\": ");

			sb.append(reverseOrder);
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.GridPageElementDefinition",
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
// LIFERAY-REST-BUILDER-HASH:869222215