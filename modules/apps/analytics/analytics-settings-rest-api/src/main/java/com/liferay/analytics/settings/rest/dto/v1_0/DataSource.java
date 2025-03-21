/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
@GraphQLName("DataSource")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DataSource")
public class DataSource implements Serializable {

	public static DataSource toDTO(String json) {
		return ObjectMapperUtil.readValue(DataSource.class, json);
	}

	public static DataSource unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(DataSource.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getCommerceChannelIds() {
		if (_commerceChannelIdsSupplier != null) {
			commerceChannelIds = _commerceChannelIdsSupplier.get();

			_commerceChannelIdsSupplier = null;
		}

		return commerceChannelIds;
	}

	public void setCommerceChannelIds(Long[] commerceChannelIds) {
		this.commerceChannelIds = commerceChannelIds;

		_commerceChannelIdsSupplier = null;
	}

	@JsonIgnore
	public void setCommerceChannelIds(
		UnsafeSupplier<Long[], Exception> commerceChannelIdsUnsafeSupplier) {

		_commerceChannelIdsSupplier = () -> {
			try {
				return commerceChannelIdsUnsafeSupplier.get();
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
	protected Long[] commerceChannelIds;

	@JsonIgnore
	private Supplier<Long[]> _commerceChannelIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDataSourceId() {
		if (_dataSourceIdSupplier != null) {
			dataSourceId = _dataSourceIdSupplier.get();

			_dataSourceIdSupplier = null;
		}

		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;

		_dataSourceIdSupplier = null;
	}

	@JsonIgnore
	public void setDataSourceId(
		UnsafeSupplier<String, Exception> dataSourceIdUnsafeSupplier) {

		_dataSourceIdSupplier = () -> {
			try {
				return dataSourceIdUnsafeSupplier.get();
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
	protected String dataSourceId;

	@JsonIgnore
	private Supplier<String> _dataSourceIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getSiteIds() {
		if (_siteIdsSupplier != null) {
			siteIds = _siteIdsSupplier.get();

			_siteIdsSupplier = null;
		}

		return siteIds;
	}

	public void setSiteIds(Long[] siteIds) {
		this.siteIds = siteIds;

		_siteIdsSupplier = null;
	}

	@JsonIgnore
	public void setSiteIds(
		UnsafeSupplier<Long[], Exception> siteIdsUnsafeSupplier) {

		_siteIdsSupplier = () -> {
			try {
				return siteIdsUnsafeSupplier.get();
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
	protected Long[] siteIds;

	@JsonIgnore
	private Supplier<Long[]> _siteIdsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataSource)) {
			return false;
		}

		DataSource dataSource = (DataSource)object;

		return Objects.equals(toString(), dataSource.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long[] commerceChannelIds = getCommerceChannelIds();

		if (commerceChannelIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commerceChannelIds\": ");

			sb.append("[");

			for (int i = 0; i < commerceChannelIds.length; i++) {
				sb.append(commerceChannelIds[i]);

				if ((i + 1) < commerceChannelIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String dataSourceId = getDataSourceId();

		if (dataSourceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(dataSourceId));

			sb.append("\"");
		}

		Long[] siteIds = getSiteIds();

		if (siteIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteIds\": ");

			sb.append("[");

			for (int i = 0; i < siteIds.length; i++) {
				sb.append(siteIds[i]);

				if ((i + 1) < siteIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.settings.rest.dto.v1_0.DataSource",
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