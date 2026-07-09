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

import jakarta.validation.Valid;

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
@GraphQLName("ContentCoverage")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentCoverage")
public class ContentCoverage implements Serializable {

	public static ContentCoverage toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentCoverage.class, json);
	}

	public static ContentCoverage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ContentCoverage.class, json);
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
	@Valid
	public ContentCoverageEntry[] getContentCoverageEntries() {
		if (_contentCoverageEntriesSupplier != null) {
			contentCoverageEntries = _contentCoverageEntriesSupplier.get();

			_contentCoverageEntriesSupplier = null;
		}

		return contentCoverageEntries;
	}

	public void setContentCoverageEntries(
		ContentCoverageEntry[] contentCoverageEntries) {

		this.contentCoverageEntries = contentCoverageEntries;

		_contentCoverageEntriesSupplier = null;
	}

	@JsonIgnore
	public void setContentCoverageEntries(
		UnsafeSupplier<ContentCoverageEntry[], Exception>
			contentCoverageEntriesUnsafeSupplier) {

		_contentCoverageEntriesSupplier = () -> {
			try {
				return contentCoverageEntriesUnsafeSupplier.get();
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
	protected ContentCoverageEntry[] contentCoverageEntries;

	@JsonIgnore
	private Supplier<ContentCoverageEntry[]> _contentCoverageEntriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public FunnelStage[] getFunnelStages() {
		if (_funnelStagesSupplier != null) {
			funnelStages = _funnelStagesSupplier.get();

			_funnelStagesSupplier = null;
		}

		return funnelStages;
	}

	public void setFunnelStages(FunnelStage[] funnelStages) {
		this.funnelStages = funnelStages;

		_funnelStagesSupplier = null;
	}

	@JsonIgnore
	public void setFunnelStages(
		UnsafeSupplier<FunnelStage[], Exception> funnelStagesUnsafeSupplier) {

		_funnelStagesSupplier = () -> {
			try {
				return funnelStagesUnsafeSupplier.get();
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
	protected FunnelStage[] funnelStages;

	@JsonIgnore
	private Supplier<FunnelStage[]> _funnelStagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Persona[] getPersonas() {
		if (_personasSupplier != null) {
			personas = _personasSupplier.get();

			_personasSupplier = null;
		}

		return personas;
	}

	public void setPersonas(Persona[] personas) {
		this.personas = personas;

		_personasSupplier = null;
	}

	@JsonIgnore
	public void setPersonas(
		UnsafeSupplier<Persona[], Exception> personasUnsafeSupplier) {

		_personasSupplier = () -> {
			try {
				return personasUnsafeSupplier.get();
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
	protected Persona[] personas;

	@JsonIgnore
	private Supplier<Persona[]> _personasSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentCoverage)) {
			return false;
		}

		ContentCoverage contentCoverage = (ContentCoverage)object;

		return Objects.equals(toString(), contentCoverage.toString());
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

		ContentCoverageEntry[] contentCoverageEntries =
			getContentCoverageEntries();

		if (contentCoverageEntries != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentCoverageEntries\": ");

			sb.append("[");

			for (int i = 0; i < contentCoverageEntries.length; i++) {
				sb.append(String.valueOf(contentCoverageEntries[i]));

				if ((i + 1) < contentCoverageEntries.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		FunnelStage[] funnelStages = getFunnelStages();

		if (funnelStages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"funnelStages\": ");

			sb.append("[");

			for (int i = 0; i < funnelStages.length; i++) {
				sb.append(String.valueOf(funnelStages[i]));

				if ((i + 1) < funnelStages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Persona[] personas = getPersonas();

		if (personas != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"personas\": ");

			sb.append("[");

			for (int i = 0; i < personas.length; i++) {
				sb.append(String.valueOf(personas[i]));

				if ((i + 1) < personas.length) {
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
		defaultValue = "com.liferay.headless.cmp.dto.v1_0.ContentCoverage",
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
// LIFERAY-REST-BUILDER-HASH:1743746252