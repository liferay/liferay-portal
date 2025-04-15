/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
	description = "Represents a definition of a Page form step container.",
	value = "PageFormStepContainerDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageFormStepContainerDefinition")
public class PageFormStepContainerDefinition implements Serializable {

	public static PageFormStepContainerDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PageFormStepContainerDefinition.class, json);
	}

	public static PageFormStepContainerDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageFormStepContainerDefinition.class, json);
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

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageFormStepContainerDefinition)) {
			return false;
		}

		PageFormStepContainerDefinition pageFormStepContainerDefinition =
			(PageFormStepContainerDefinition)object;

		return Objects.equals(
			toString(), pageFormStepContainerDefinition.toString());
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

		Boolean indexed = getIndexed();

		if (indexed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(indexed);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.PageFormStepContainerDefinition",
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