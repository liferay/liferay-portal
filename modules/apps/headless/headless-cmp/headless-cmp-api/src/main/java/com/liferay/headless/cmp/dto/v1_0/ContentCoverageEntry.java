/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.dto.v1_0;

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
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
@GraphQLName("ContentCoverageEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentCoverageEntry")
public class ContentCoverageEntry implements Serializable {

	public static ContentCoverageEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentCoverageEntry.class, json);
	}

	public static ContentCoverageEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ContentCoverageEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAssetCount() {
		if (_assetCountSupplier != null) {
			assetCount = _assetCountSupplier.get();

			_assetCountSupplier = null;
		}

		return assetCount;
	}

	public void setAssetCount(Long assetCount) {
		this.assetCount = assetCount;

		_assetCountSupplier = null;
	}

	@JsonIgnore
	public void setAssetCount(
		UnsafeSupplier<Long, Exception> assetCountUnsafeSupplier) {

		_assetCountSupplier = () -> {
			try {
				return assetCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long assetCount;

	@JsonIgnore
	private Supplier<Long> _assetCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getFunnelStageId() {
		if (_funnelStageIdSupplier != null) {
			funnelStageId = _funnelStageIdSupplier.get();

			_funnelStageIdSupplier = null;
		}

		return funnelStageId;
	}

	public void setFunnelStageId(Long funnelStageId) {
		this.funnelStageId = funnelStageId;

		_funnelStageIdSupplier = null;
	}

	@JsonIgnore
	public void setFunnelStageId(
		UnsafeSupplier<Long, Exception> funnelStageIdUnsafeSupplier) {

		_funnelStageIdSupplier = () -> {
			try {
				return funnelStageIdUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long funnelStageId;

	@JsonIgnore
	private Supplier<Long> _funnelStageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getPersonaId() {
		if (_personaIdSupplier != null) {
			personaId = _personaIdSupplier.get();

			_personaIdSupplier = null;
		}

		return personaId;
	}

	public void setPersonaId(Long personaId) {
		this.personaId = personaId;

		_personaIdSupplier = null;
	}

	@JsonIgnore
	public void setPersonaId(
		UnsafeSupplier<Long, Exception> personaIdUnsafeSupplier) {

		_personaIdSupplier = () -> {
			try {
				return personaIdUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long personaId;

	@JsonIgnore
	private Supplier<Long> _personaIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentCoverageEntry)) {
			return false;
		}

		ContentCoverageEntry contentCoverageEntry =
			(ContentCoverageEntry)object;

		return Objects.equals(toString(), contentCoverageEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long assetCount = getAssetCount();

		if (assetCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetCount\": ");

			sb.append(assetCount);
		}

		Long funnelStageId = getFunnelStageId();

		if (funnelStageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"funnelStageId\": ");

			sb.append(funnelStageId);
		}

		Long personaId = getPersonaId();

		if (personaId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"personaId\": ");

			sb.append(personaId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.cmp.dto.v1_0.ContentCoverageEntry",
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
// LIFERAY-REST-BUILDER-HASH:-261095960