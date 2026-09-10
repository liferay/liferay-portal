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
@GraphQLName(description = "A fragment image.", value = "FragmentImage")
@io.swagger.v3.oas.annotations.media.Schema(description = "A fragment image.")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentImage")
public class FragmentImage implements Serializable {

	public static FragmentImage toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentImage.class, json);
	}

	public static FragmentImage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FragmentImage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized fragment image's descriptions."
	)
	@Valid
	public Map<String, String> getDescription_i18n() {
		if (_description_i18nSupplier != null) {
			description_i18n = _description_i18nSupplier.get();

			_description_i18nSupplier = null;
		}

		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;

		_description_i18nSupplier = null;
	}

	@JsonIgnore
	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		_description_i18nSupplier = () -> {
			try {
				return description_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized fragment image's descriptions.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _description_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment image's value."
	)
	@Valid
	public FragmentImageValue getFragmentImageValue() {
		if (_fragmentImageValueSupplier != null) {
			fragmentImageValue = _fragmentImageValueSupplier.get();

			_fragmentImageValueSupplier = null;
		}

		return fragmentImageValue;
	}

	public void setFragmentImageValue(FragmentImageValue fragmentImageValue) {
		this.fragmentImageValue = fragmentImageValue;

		_fragmentImageValueSupplier = null;
	}

	@JsonIgnore
	public void setFragmentImageValue(
		UnsafeSupplier<FragmentImageValue, Exception>
			fragmentImageValueUnsafeSupplier) {

		_fragmentImageValueSupplier = () -> {
			try {
				return fragmentImageValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment image's value.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentImageValue fragmentImageValue;

	@JsonIgnore
	private Supplier<FragmentImageValue> _fragmentImageValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of viewports configuration of the fragment image."
	)
	@Valid
	public FragmentImageViewport[] getFragmentImageViewports() {
		if (_fragmentImageViewportsSupplier != null) {
			fragmentImageViewports = _fragmentImageViewportsSupplier.get();

			_fragmentImageViewportsSupplier = null;
		}

		return fragmentImageViewports;
	}

	public void setFragmentImageViewports(
		FragmentImageViewport[] fragmentImageViewports) {

		this.fragmentImageViewports = fragmentImageViewports;

		_fragmentImageViewportsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentImageViewports(
		UnsafeSupplier<FragmentImageViewport[], Exception>
			fragmentImageViewportsUnsafeSupplier) {

		_fragmentImageViewportsSupplier = () -> {
			try {
				return fragmentImageViewportsUnsafeSupplier.get();
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
		description = "A list of viewports configuration of the fragment image."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentImageViewport[] fragmentImageViewports;

	@JsonIgnore
	private Supplier<FragmentImageViewport[]> _fragmentImageViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLazyLoading() {
		if (_lazyLoadingSupplier != null) {
			lazyLoading = _lazyLoadingSupplier.get();

			_lazyLoadingSupplier = null;
		}

		return lazyLoading;
	}

	public void setLazyLoading(Boolean lazyLoading) {
		this.lazyLoading = lazyLoading;

		_lazyLoadingSupplier = null;
	}

	@JsonIgnore
	public void setLazyLoading(
		UnsafeSupplier<Boolean, Exception> lazyLoadingUnsafeSupplier) {

		_lazyLoadingSupplier = () -> {
			try {
				return lazyLoadingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean lazyLoading;

	@JsonIgnore
	private Supplier<Boolean> _lazyLoadingSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentImage)) {
			return false;
		}

		FragmentImage fragmentImage = (FragmentImage)object;

		return Objects.equals(toString(), fragmentImage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, String> description_i18n = getDescription_i18n();

		if (description_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(description_i18n));
		}

		FragmentImageValue fragmentImageValue = getFragmentImageValue();

		if (fragmentImageValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentImageValue\": ");

			sb.append(String.valueOf(fragmentImageValue));
		}

		FragmentImageViewport[] fragmentImageViewports =
			getFragmentImageViewports();

		if (fragmentImageViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentImageViewports\": ");

			sb.append("[");

			for (int i = 0; i < fragmentImageViewports.length; i++) {
				sb.append(String.valueOf(fragmentImageViewports[i]));

				if ((i + 1) < fragmentImageViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean lazyLoading = getLazyLoading();

		if (lazyLoading != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lazyLoading\": ");

			sb.append(lazyLoading);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentImage",
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
// LIFERAY-REST-BUILDER-HASH:1976293311