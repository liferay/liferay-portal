/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a definition of a Page row.",
	value = "PageRowDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageRowDefinition")
public class PageRowDefinition implements Serializable {

	public static PageRowDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(PageRowDefinition.class, json);
	}

	public static PageRowDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageRowDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of CSS Classes that are applied to the element."
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
		description = "A list of CSS Classes that are applied to the element."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] cssClasses;

	@JsonIgnore
	private Supplier<String[]> _cssClassesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom CSS that is applied on the fragment."
	)
	public String getCustomCSS() {
		if (_customCSSSupplier != null) {
			customCSS = _customCSSSupplier.get();

			_customCSSSupplier = null;
		}

		return customCSS;
	}

	public void setCustomCSS(String customCSS) {
		this.customCSS = customCSS;

		_customCSSSupplier = null;
	}

	@JsonIgnore
	public void setCustomCSS(
		UnsafeSupplier<String, Exception> customCSSUnsafeSupplier) {

		_customCSSSupplier = () -> {
			try {
				return customCSSUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Custom CSS that is applied on the fragment.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String customCSS;

	@JsonIgnore
	private Supplier<String> _customCSSSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom CSS viewports of the page collection."
	)
	@Valid
	public CustomCSSViewport[] getCustomCSSViewports() {
		if (_customCSSViewportsSupplier != null) {
			customCSSViewports = _customCSSViewportsSupplier.get();

			_customCSSViewportsSupplier = null;
		}

		return customCSSViewports;
	}

	public void setCustomCSSViewports(CustomCSSViewport[] customCSSViewports) {
		this.customCSSViewports = customCSSViewports;

		_customCSSViewportsSupplier = null;
	}

	@JsonIgnore
	public void setCustomCSSViewports(
		UnsafeSupplier<CustomCSSViewport[], Exception>
			customCSSViewportsUnsafeSupplier) {

		_customCSSViewportsSupplier = () -> {
			try {
				return customCSSViewportsUnsafeSupplier.get();
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
		description = "The custom CSS viewports of the page collection."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CustomCSSViewport[] customCSSViewports;

	@JsonIgnore
	private Supplier<CustomCSSViewport[]> _customCSSViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment style of a Page row."
	)
	@Valid
	public FragmentStyle getFragmentStyle() {
		if (_fragmentStyleSupplier != null) {
			fragmentStyle = _fragmentStyleSupplier.get();

			_fragmentStyleSupplier = null;
		}

		return fragmentStyle;
	}

	public void setFragmentStyle(FragmentStyle fragmentStyle) {
		this.fragmentStyle = fragmentStyle;

		_fragmentStyleSupplier = null;
	}

	@JsonIgnore
	public void setFragmentStyle(
		UnsafeSupplier<FragmentStyle, Exception> fragmentStyleUnsafeSupplier) {

		_fragmentStyleSupplier = () -> {
			try {
				return fragmentStyleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment style of a Page row.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentStyle fragmentStyle;

	@JsonIgnore
	private Supplier<FragmentStyle> _fragmentStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of fragment viewports of a Page row."
	)
	@Valid
	public FragmentViewport[] getFragmentViewports() {
		if (_fragmentViewportsSupplier != null) {
			fragmentViewports = _fragmentViewportsSupplier.get();

			_fragmentViewportsSupplier = null;
		}

		return fragmentViewports;
	}

	public void setFragmentViewports(FragmentViewport[] fragmentViewports) {
		this.fragmentViewports = fragmentViewports;

		_fragmentViewportsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentViewports(
		UnsafeSupplier<FragmentViewport[], Exception>
			fragmentViewportsUnsafeSupplier) {

		_fragmentViewportsSupplier = () -> {
			try {
				return fragmentViewportsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of fragment viewports of a Page row.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentViewport[] fragmentViewports;

	@JsonIgnore
	private Supplier<FragmentViewport[]> _fragmentViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the page row has gutters."
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
		description = "A flag that indicates whether the page row has gutters."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean gutters;

	@JsonIgnore
	private Supplier<Boolean> _guttersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the page row is indexed or not."
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
		description = "A flag that indicates whether the page row is indexed or not."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean indexed;

	@JsonIgnore
	private Supplier<Boolean> _indexedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page row's modules per row."
	)
	public Integer getModulesPerRow() {
		if (_modulesPerRowSupplier != null) {
			modulesPerRow = _modulesPerRowSupplier.get();

			_modulesPerRowSupplier = null;
		}

		return modulesPerRow;
	}

	public void setModulesPerRow(Integer modulesPerRow) {
		this.modulesPerRow = modulesPerRow;

		_modulesPerRowSupplier = null;
	}

	@JsonIgnore
	public void setModulesPerRow(
		UnsafeSupplier<Integer, Exception> modulesPerRowUnsafeSupplier) {

		_modulesPerRowSupplier = () -> {
			try {
				return modulesPerRowUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page row's modules per row.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer modulesPerRow;

	@JsonIgnore
	private Supplier<Integer> _modulesPerRowSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a Page row."
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

	@GraphQLField(description = "The custom name of a Page row.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page row's number of columns."
	)
	public Integer getNumberOfColumns() {
		if (_numberOfColumnsSupplier != null) {
			numberOfColumns = _numberOfColumnsSupplier.get();

			_numberOfColumnsSupplier = null;
		}

		return numberOfColumns;
	}

	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;

		_numberOfColumnsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfColumns(
		UnsafeSupplier<Integer, Exception> numberOfColumnsUnsafeSupplier) {

		_numberOfColumnsSupplier = () -> {
			try {
				return numberOfColumnsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page row's number of columns.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfColumns;

	@JsonIgnore
	private Supplier<Integer> _numberOfColumnsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the page row has reverse order."
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
		description = "A flag that indicates whether the page row has reverse order."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean reverseOrder;

	@JsonIgnore
	private Supplier<Boolean> _reverseOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "Deprecated as of Athanasius (7.3.x), replaced by rowViewports"
	)
	@Valid
	public RowViewportConfig getRowViewportConfig() {
		if (_rowViewportConfigSupplier != null) {
			rowViewportConfig = _rowViewportConfigSupplier.get();

			_rowViewportConfigSupplier = null;
		}

		return rowViewportConfig;
	}

	public void setRowViewportConfig(RowViewportConfig rowViewportConfig) {
		this.rowViewportConfig = rowViewportConfig;

		_rowViewportConfigSupplier = null;
	}

	@JsonIgnore
	public void setRowViewportConfig(
		UnsafeSupplier<RowViewportConfig, Exception>
			rowViewportConfigUnsafeSupplier) {

		_rowViewportConfigSupplier = () -> {
			try {
				return rowViewportConfigUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField(
		description = "Deprecated as of Athanasius (7.3.x), replaced by rowViewports"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RowViewportConfig rowViewportConfig;

	@JsonIgnore
	private Supplier<RowViewportConfig> _rowViewportConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of viewports of the page row."
	)
	@Valid
	public RowViewport[] getRowViewports() {
		if (_rowViewportsSupplier != null) {
			rowViewports = _rowViewportsSupplier.get();

			_rowViewportsSupplier = null;
		}

		return rowViewports;
	}

	public void setRowViewports(RowViewport[] rowViewports) {
		this.rowViewports = rowViewports;

		_rowViewportsSupplier = null;
	}

	@JsonIgnore
	public void setRowViewports(
		UnsafeSupplier<RowViewport[], Exception> rowViewportsUnsafeSupplier) {

		_rowViewportsSupplier = () -> {
			try {
				return rowViewportsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of viewports of the page row.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RowViewport[] rowViewports;

	@JsonIgnore
	private Supplier<RowViewport[]> _rowViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The vertical aligment property of the page row."
	)
	public String getVerticalAlignment() {
		if (_verticalAlignmentSupplier != null) {
			verticalAlignment = _verticalAlignmentSupplier.get();

			_verticalAlignmentSupplier = null;
		}

		return verticalAlignment;
	}

	public void setVerticalAlignment(String verticalAlignment) {
		this.verticalAlignment = verticalAlignment;

		_verticalAlignmentSupplier = null;
	}

	@JsonIgnore
	public void setVerticalAlignment(
		UnsafeSupplier<String, Exception> verticalAlignmentUnsafeSupplier) {

		_verticalAlignmentSupplier = () -> {
			try {
				return verticalAlignmentUnsafeSupplier.get();
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
		description = "The vertical aligment property of the page row."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String verticalAlignment;

	@JsonIgnore
	private Supplier<String> _verticalAlignmentSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageRowDefinition)) {
			return false;
		}

		PageRowDefinition pageRowDefinition = (PageRowDefinition)object;

		return Objects.equals(toString(), pageRowDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		String customCSS = getCustomCSS();

		if (customCSS != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(customCSS));

			sb.append("\"");
		}

		CustomCSSViewport[] customCSSViewports = getCustomCSSViewports();

		if (customCSSViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0; i < customCSSViewports.length; i++) {
				sb.append(String.valueOf(customCSSViewports[i]));

				if ((i + 1) < customCSSViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		FragmentStyle fragmentStyle = getFragmentStyle();

		if (fragmentStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(String.valueOf(fragmentStyle));
		}

		FragmentViewport[] fragmentViewports = getFragmentViewports();

		if (fragmentViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0; i < fragmentViewports.length; i++) {
				sb.append(String.valueOf(fragmentViewports[i]));

				if ((i + 1) < fragmentViewports.length) {
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

		Integer modulesPerRow = getModulesPerRow();

		if (modulesPerRow != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modulesPerRow\": ");

			sb.append(modulesPerRow);
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

		Integer numberOfColumns = getNumberOfColumns();

		if (numberOfColumns != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(numberOfColumns);
		}

		Boolean reverseOrder = getReverseOrder();

		if (reverseOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverseOrder\": ");

			sb.append(reverseOrder);
		}

		RowViewportConfig rowViewportConfig = getRowViewportConfig();

		if (rowViewportConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rowViewportConfig\": ");

			sb.append(String.valueOf(rowViewportConfig));
		}

		RowViewport[] rowViewports = getRowViewports();

		if (rowViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rowViewports\": ");

			sb.append("[");

			for (int i = 0; i < rowViewports.length; i++) {
				sb.append(String.valueOf(rowViewports[i]));

				if ((i + 1) < rowViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String verticalAlignment = getVerticalAlignment();

		if (verticalAlignment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");

			sb.append(_escape(verticalAlignment));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.PageRowDefinition",
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