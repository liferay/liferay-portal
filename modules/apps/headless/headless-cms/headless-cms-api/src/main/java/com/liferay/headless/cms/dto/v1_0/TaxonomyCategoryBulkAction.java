/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
@GraphQLName("TaxonomyCategoryBulkAction")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaxonomyCategoryBulkAction")
public class TaxonomyCategoryBulkAction
	extends BulkAction implements Serializable {

	public static TaxonomyCategoryBulkAction toDTO(String json) {
		return ObjectMapperUtil.readValue(
			TaxonomyCategoryBulkAction.class, json);
	}

	public static TaxonomyCategoryBulkAction unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			TaxonomyCategoryBulkAction.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getTaxonomyCategoryIds() {
		if (_taxonomyCategoryIdsSupplier != null) {
			taxonomyCategoryIds = _taxonomyCategoryIdsSupplier.get();

			_taxonomyCategoryIdsSupplier = null;
		}

		return taxonomyCategoryIds;
	}

	public void setTaxonomyCategoryIds(Long[] taxonomyCategoryIds) {
		this.taxonomyCategoryIds = taxonomyCategoryIds;

		_taxonomyCategoryIdsSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryIds(
		UnsafeSupplier<Long[], Exception> taxonomyCategoryIdsUnsafeSupplier) {

		_taxonomyCategoryIdsSupplier = () -> {
			try {
				return taxonomyCategoryIdsUnsafeSupplier.get();
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
	protected Long[] taxonomyCategoryIds;

	@JsonIgnore
	private Supplier<Long[]> _taxonomyCategoryIdsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyCategoryBulkAction)) {
			return false;
		}

		TaxonomyCategoryBulkAction taxonomyCategoryBulkAction =
			(TaxonomyCategoryBulkAction)object;

		return Objects.equals(
			toString(), taxonomyCategoryBulkAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long[] taxonomyCategoryIds = getTaxonomyCategoryIds();

		if (taxonomyCategoryIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryIds.length; i++) {
				sb.append(taxonomyCategoryIds[i]);

				if ((i + 1) < taxonomyCategoryIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		BulkActionItem[] bulkActionItems = getBulkActionItems();

		if (bulkActionItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0; i < bulkActionItems.length; i++) {
				sb.append(String.valueOf(bulkActionItems[i]));

				if ((i + 1) < bulkActionItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean selectAll = getSelectAll();

		if (selectAll != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(selectAll);
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
		defaultValue = "com.liferay.headless.cms.dto.v1_0.TaxonomyCategoryBulkAction",
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