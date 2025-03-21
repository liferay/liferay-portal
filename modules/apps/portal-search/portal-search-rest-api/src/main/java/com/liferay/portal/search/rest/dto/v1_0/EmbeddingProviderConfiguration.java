/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.dto.v1_0;

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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("EmbeddingProviderConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "EmbeddingProviderConfiguration")
public class EmbeddingProviderConfiguration implements Serializable {

	public static EmbeddingProviderConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(
			EmbeddingProviderConfiguration.class, json);
	}

	public static EmbeddingProviderConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			EmbeddingProviderConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getAttributes() {
		if (_attributesSupplier != null) {
			attributes = _attributesSupplier.get();

			_attributesSupplier = null;
		}

		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;

		_attributesSupplier = null;
	}

	@JsonIgnore
	public void setAttributes(
		UnsafeSupplier<Object, Exception> attributesUnsafeSupplier) {

		_attributesSupplier = () -> {
			try {
				return attributesUnsafeSupplier.get();
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
	protected Object attributes;

	@JsonIgnore
	private Supplier<Object> _attributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getEmbeddingVectorDimensions() {
		if (_embeddingVectorDimensionsSupplier != null) {
			embeddingVectorDimensions =
				_embeddingVectorDimensionsSupplier.get();

			_embeddingVectorDimensionsSupplier = null;
		}

		return embeddingVectorDimensions;
	}

	public void setEmbeddingVectorDimensions(
		Integer embeddingVectorDimensions) {

		this.embeddingVectorDimensions = embeddingVectorDimensions;

		_embeddingVectorDimensionsSupplier = null;
	}

	@JsonIgnore
	public void setEmbeddingVectorDimensions(
		UnsafeSupplier<Integer, Exception>
			embeddingVectorDimensionsUnsafeSupplier) {

		_embeddingVectorDimensionsSupplier = () -> {
			try {
				return embeddingVectorDimensionsUnsafeSupplier.get();
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
	protected Integer embeddingVectorDimensions;

	@JsonIgnore
	private Supplier<Integer> _embeddingVectorDimensionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getLanguageIds() {
		if (_languageIdsSupplier != null) {
			languageIds = _languageIdsSupplier.get();

			_languageIdsSupplier = null;
		}

		return languageIds;
	}

	public void setLanguageIds(String[] languageIds) {
		this.languageIds = languageIds;

		_languageIdsSupplier = null;
	}

	@JsonIgnore
	public void setLanguageIds(
		UnsafeSupplier<String[], Exception> languageIdsUnsafeSupplier) {

		_languageIdsSupplier = () -> {
			try {
				return languageIdsUnsafeSupplier.get();
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
	protected String[] languageIds;

	@JsonIgnore
	private Supplier<String[]> _languageIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getModelClassNames() {
		if (_modelClassNamesSupplier != null) {
			modelClassNames = _modelClassNamesSupplier.get();

			_modelClassNamesSupplier = null;
		}

		return modelClassNames;
	}

	public void setModelClassNames(String[] modelClassNames) {
		this.modelClassNames = modelClassNames;

		_modelClassNamesSupplier = null;
	}

	@JsonIgnore
	public void setModelClassNames(
		UnsafeSupplier<String[], Exception> modelClassNamesUnsafeSupplier) {

		_modelClassNamesSupplier = () -> {
			try {
				return modelClassNamesUnsafeSupplier.get();
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
	protected String[] modelClassNames;

	@JsonIgnore
	private Supplier<String[]> _modelClassNamesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getProviderName() {
		if (_providerNameSupplier != null) {
			providerName = _providerNameSupplier.get();

			_providerNameSupplier = null;
		}

		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;

		_providerNameSupplier = null;
	}

	@JsonIgnore
	public void setProviderName(
		UnsafeSupplier<String, Exception> providerNameUnsafeSupplier) {

		_providerNameSupplier = () -> {
			try {
				return providerNameUnsafeSupplier.get();
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
	protected String providerName;

	@JsonIgnore
	private Supplier<String> _providerNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EmbeddingProviderConfiguration)) {
			return false;
		}

		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			(EmbeddingProviderConfiguration)object;

		return Objects.equals(
			toString(), embeddingProviderConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object attributes = getAttributes();

		if (attributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			if (attributes instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)attributes));
			}
			else if (attributes instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)attributes));
				sb.append("\"");
			}
			else {
				sb.append(attributes);
			}
		}

		Integer embeddingVectorDimensions = getEmbeddingVectorDimensions();

		if (embeddingVectorDimensions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embeddingVectorDimensions\": ");

			sb.append(embeddingVectorDimensions);
		}

		String[] languageIds = getLanguageIds();

		if (languageIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageIds\": ");

			sb.append("[");

			for (int i = 0; i < languageIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(languageIds[i]));

				sb.append("\"");

				if ((i + 1) < languageIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] modelClassNames = getModelClassNames();

		if (modelClassNames != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelClassNames\": ");

			sb.append("[");

			for (int i = 0; i < modelClassNames.length; i++) {
				sb.append("\"");

				sb.append(_escape(modelClassNames[i]));

				sb.append("\"");

				if ((i + 1) < modelClassNames.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String providerName = getProviderName();

		if (providerName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(_escape(providerName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration",
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