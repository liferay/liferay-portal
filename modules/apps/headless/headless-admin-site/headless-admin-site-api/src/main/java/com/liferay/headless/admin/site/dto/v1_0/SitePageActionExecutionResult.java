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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The definition of an action execution of type page.",
	value = "SitePageActionExecutionResult"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SitePageActionExecutionResult")
public class SitePageActionExecutionResult implements Serializable {

	public static SitePageActionExecutionResult toDTO(String json) {
		return ObjectMapperUtil.readValue(
			SitePageActionExecutionResult.class, json);
	}

	public static SitePageActionExecutionResult unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			SitePageActionExecutionResult.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The reference to a page."
	)
	@Valid
	public ItemExternalReference getItemReference() {
		if (_itemReferenceSupplier != null) {
			itemReference = _itemReferenceSupplier.get();

			_itemReferenceSupplier = null;
		}

		return itemReference;
	}

	public void setItemReference(ItemExternalReference itemReference) {
		this.itemReference = itemReference;

		_itemReferenceSupplier = null;
	}

	@JsonIgnore
	public void setItemReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			itemReferenceUnsafeSupplier) {

		_itemReferenceSupplier = () -> {
			try {
				return itemReferenceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The reference to a page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ItemExternalReference itemReference;

	@JsonIgnore
	private Supplier<ItemExternalReference> _itemReferenceSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePageActionExecutionResult)) {
			return false;
		}

		SitePageActionExecutionResult sitePageActionExecutionResult =
			(SitePageActionExecutionResult)object;

		return Objects.equals(
			toString(), sitePageActionExecutionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ItemExternalReference itemReference = getItemReference();

		if (itemReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemReference\": ");

			sb.append(String.valueOf(itemReference));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.SitePageActionExecutionResult",
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