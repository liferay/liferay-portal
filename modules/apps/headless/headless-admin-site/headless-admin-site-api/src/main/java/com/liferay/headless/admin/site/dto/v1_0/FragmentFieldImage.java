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
	description = "A fragment field with an image.",
	value = "FragmentFieldImage"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentFieldImage")
public class FragmentFieldImage implements Serializable {

	public static FragmentFieldImage toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentFieldImage.class, json);
	}

	public static FragmentFieldImage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FragmentFieldImage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment field's image."
	)
	@Valid
	public FragmentImage getFragmentImage() {
		if (_fragmentImageSupplier != null) {
			fragmentImage = _fragmentImageSupplier.get();

			_fragmentImageSupplier = null;
		}

		return fragmentImage;
	}

	public void setFragmentImage(FragmentImage fragmentImage) {
		this.fragmentImage = fragmentImage;

		_fragmentImageSupplier = null;
	}

	@JsonIgnore
	public void setFragmentImage(
		UnsafeSupplier<FragmentImage, Exception> fragmentImageUnsafeSupplier) {

		_fragmentImageSupplier = () -> {
			try {
				return fragmentImageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment field's image.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentImage fragmentImage;

	@JsonIgnore
	private Supplier<FragmentImage> _fragmentImageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A link to a fragment."
	)
	@Valid
	public FragmentLink getFragmentLink() {
		if (_fragmentLinkSupplier != null) {
			fragmentLink = _fragmentLinkSupplier.get();

			_fragmentLinkSupplier = null;
		}

		return fragmentLink;
	}

	public void setFragmentLink(FragmentLink fragmentLink) {
		this.fragmentLink = fragmentLink;

		_fragmentLinkSupplier = null;
	}

	@JsonIgnore
	public void setFragmentLink(
		UnsafeSupplier<FragmentLink, Exception> fragmentLinkUnsafeSupplier) {

		_fragmentLinkSupplier = () -> {
			try {
				return fragmentLinkUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A link to a fragment.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentLink fragmentLink;

	@JsonIgnore
	private Supplier<FragmentLink> _fragmentLinkSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentFieldImage)) {
			return false;
		}

		FragmentFieldImage fragmentFieldImage = (FragmentFieldImage)object;

		return Objects.equals(toString(), fragmentFieldImage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FragmentImage fragmentImage = getFragmentImage();

		if (fragmentImage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentImage\": ");

			sb.append(String.valueOf(fragmentImage));
		}

		FragmentLink fragmentLink = getFragmentLink();

		if (fragmentLink != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentLink\": ");

			sb.append(String.valueOf(fragmentLink));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentFieldImage",
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