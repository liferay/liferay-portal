/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
	description = "The settings of a link to page page.",
	value = "LinkToPagePageSettings"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The settings of a link to page page."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "LinkToPagePageSettings")
public class LinkToPagePageSettings
	extends PageSettings implements Serializable {

	public static LinkToPagePageSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(LinkToPagePageSettings.class, json);
	}

	public static LinkToPagePageSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			LinkToPagePageSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference code of the link to page within the same site to redirect to."
	)
	public String getLinkToPageExternalReferenceCode() {
		if (_linkToPageExternalReferenceCodeSupplier != null) {
			linkToPageExternalReferenceCode =
				_linkToPageExternalReferenceCodeSupplier.get();

			_linkToPageExternalReferenceCodeSupplier = null;
		}

		return linkToPageExternalReferenceCode;
	}

	public void setLinkToPageExternalReferenceCode(
		String linkToPageExternalReferenceCode) {

		this.linkToPageExternalReferenceCode = linkToPageExternalReferenceCode;

		_linkToPageExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setLinkToPageExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			linkToPageExternalReferenceCodeUnsafeSupplier) {

		_linkToPageExternalReferenceCodeSupplier = () -> {
			try {
				return linkToPageExternalReferenceCodeUnsafeSupplier.get();
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
		description = "The external reference code of the link to page within the same site to redirect to."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String linkToPageExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _linkToPageExternalReferenceCodeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LinkToPagePageSettings)) {
			return false;
		}

		LinkToPagePageSettings linkToPagePageSettings =
			(LinkToPagePageSettings)object;

		return Objects.equals(toString(), linkToPagePageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String linkToPageExternalReferenceCode =
			getLinkToPageExternalReferenceCode();

		if (linkToPageExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkToPageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(linkToPageExternalReferenceCode));

			sb.append("\"");
		}

		Boolean hiddenFromNavigation = getHiddenFromNavigation();

		if (hiddenFromNavigation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(hiddenFromNavigation);
		}

		SitePageNavigationSettings navigationSettings = getNavigationSettings();

		if (navigationSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationSettings\": ");

			sb.append(String.valueOf(navigationSettings));
		}

		Integer priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.LinkToPagePageSettings",
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
// LIFERAY-REST-BUILDER-HASH:1905768063