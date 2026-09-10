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
import com.fasterxml.jackson.annotation.JsonValue;

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
@GraphQLName("SitePagesNavigationMenuValue")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SitePagesNavigationMenuValue")
public class SitePagesNavigationMenuValue
	extends NavigationMenuValue implements Serializable {

	public static SitePagesNavigationMenuValue toDTO(String json) {
		return ObjectMapperUtil.readValue(
			SitePagesNavigationMenuValue.class, json);
	}

	public static SitePagesNavigationMenuValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			SitePagesNavigationMenuValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("pageSetType")
	@Valid
	public PageSetType getPageSetType() {
		if (_pageSetTypeSupplier != null) {
			pageSetType = _pageSetTypeSupplier.get();

			_pageSetTypeSupplier = null;
		}

		return pageSetType;
	}

	@JsonIgnore
	public String getPageSetTypeAsString() {
		PageSetType pageSetType = getPageSetType();

		if (pageSetType == null) {
			return null;
		}

		return pageSetType.toString();
	}

	public void setPageSetType(PageSetType pageSetType) {
		this.pageSetType = pageSetType;

		_pageSetTypeSupplier = null;
	}

	@JsonIgnore
	public void setPageSetType(
		UnsafeSupplier<PageSetType, Exception> pageSetTypeUnsafeSupplier) {

		_pageSetTypeSupplier = () -> {
			try {
				return pageSetTypeUnsafeSupplier.get();
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
	protected PageSetType pageSetType;

	@JsonIgnore
	private Supplier<PageSetType> _pageSetTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getParentSitePageExternalReferenceCode() {
		if (_parentSitePageExternalReferenceCodeSupplier != null) {
			parentSitePageExternalReferenceCode =
				_parentSitePageExternalReferenceCodeSupplier.get();

			_parentSitePageExternalReferenceCodeSupplier = null;
		}

		return parentSitePageExternalReferenceCode;
	}

	public void setParentSitePageExternalReferenceCode(
		String parentSitePageExternalReferenceCode) {

		this.parentSitePageExternalReferenceCode =
			parentSitePageExternalReferenceCode;

		_parentSitePageExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentSitePageExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentSitePageExternalReferenceCodeUnsafeSupplier) {

		_parentSitePageExternalReferenceCodeSupplier = () -> {
			try {
				return parentSitePageExternalReferenceCodeUnsafeSupplier.get();
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
	protected String parentSitePageExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _parentSitePageExternalReferenceCodeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePagesNavigationMenuValue)) {
			return false;
		}

		SitePagesNavigationMenuValue sitePagesNavigationMenuValue =
			(SitePagesNavigationMenuValue)object;

		return Objects.equals(
			toString(), sitePagesNavigationMenuValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		PageSetType pageSetType = getPageSetType();

		if (pageSetType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSetType\": ");

			sb.append("\"");
			sb.append(pageSetType);
			sb.append("\"");
		}

		String parentSitePageExternalReferenceCode =
			getParentSitePageExternalReferenceCode();

		if (parentSitePageExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSitePageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentSitePageExternalReferenceCode));

			sb.append("\"");
		}

		NavigationMenuType navigationMenuType = getNavigationMenuType();

		if (navigationMenuType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuType\": ");

			sb.append("\"");
			sb.append(navigationMenuType);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.SitePagesNavigationMenuValue",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("PageSetType")
	public static enum PageSetType {

		PRIVATE_PAGES("PrivatePages"), PUBLIC_PAGES("PublicPages");

		@JsonCreator
		public static PageSetType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (PageSetType pageSetType : values()) {
				if (Objects.equals(pageSetType.getValue(), value)) {
					return pageSetType;
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

		private PageSetType(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-149532967