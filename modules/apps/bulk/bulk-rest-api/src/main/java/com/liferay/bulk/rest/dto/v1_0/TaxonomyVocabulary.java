/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.dto.v1_0;

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
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("TaxonomyVocabulary")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaxonomyVocabulary")
public class TaxonomyVocabulary implements Serializable {

	public static TaxonomyVocabulary toDTO(String json) {
		return ObjectMapperUtil.readValue(TaxonomyVocabulary.class, json);
	}

	public static TaxonomyVocabulary unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TaxonomyVocabulary.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getMultiValued() {
		if (_multiValuedSupplier != null) {
			multiValued = _multiValuedSupplier.get();

			_multiValuedSupplier = null;
		}

		return multiValued;
	}

	public void setMultiValued(Boolean multiValued) {
		this.multiValued = multiValued;

		_multiValuedSupplier = null;
	}

	@JsonIgnore
	public void setMultiValued(
		UnsafeSupplier<Boolean, Exception> multiValuedUnsafeSupplier) {

		_multiValuedSupplier = () -> {
			try {
				return multiValuedUnsafeSupplier.get();
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
	protected Boolean multiValued;

	@JsonIgnore
	private Supplier<Boolean> _multiValuedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getRequired() {
		if (_requiredSupplier != null) {
			required = _requiredSupplier.get();

			_requiredSupplier = null;
		}

		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;

		_requiredSupplier = null;
	}

	@JsonIgnore
	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		_requiredSupplier = () -> {
			try {
				return requiredUnsafeSupplier.get();
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
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public TaxonomyCategory[] getTaxonomyCategories() {
		if (_taxonomyCategoriesSupplier != null) {
			taxonomyCategories = _taxonomyCategoriesSupplier.get();

			_taxonomyCategoriesSupplier = null;
		}

		return taxonomyCategories;
	}

	public void setTaxonomyCategories(TaxonomyCategory[] taxonomyCategories) {
		this.taxonomyCategories = taxonomyCategories;

		_taxonomyCategoriesSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategories(
		UnsafeSupplier<TaxonomyCategory[], Exception>
			taxonomyCategoriesUnsafeSupplier) {

		_taxonomyCategoriesSupplier = () -> {
			try {
				return taxonomyCategoriesUnsafeSupplier.get();
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
	protected TaxonomyCategory[] taxonomyCategories;

	@JsonIgnore
	private Supplier<TaxonomyCategory[]> _taxonomyCategoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTaxonomyVocabularyId() {
		if (_taxonomyVocabularyIdSupplier != null) {
			taxonomyVocabularyId = _taxonomyVocabularyIdSupplier.get();

			_taxonomyVocabularyIdSupplier = null;
		}

		return taxonomyVocabularyId;
	}

	public void setTaxonomyVocabularyId(Long taxonomyVocabularyId) {
		this.taxonomyVocabularyId = taxonomyVocabularyId;

		_taxonomyVocabularyIdSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyVocabularyId(
		UnsafeSupplier<Long, Exception> taxonomyVocabularyIdUnsafeSupplier) {

		_taxonomyVocabularyIdSupplier = () -> {
			try {
				return taxonomyVocabularyIdUnsafeSupplier.get();
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
	protected Long taxonomyVocabularyId;

	@JsonIgnore
	private Supplier<Long> _taxonomyVocabularyIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyVocabulary)) {
			return false;
		}

		TaxonomyVocabulary taxonomyVocabulary = (TaxonomyVocabulary)object;

		return Objects.equals(toString(), taxonomyVocabulary.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean multiValued = getMultiValued();

		if (multiValued != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiValued\": ");

			sb.append(multiValued);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
		}

		TaxonomyCategory[] taxonomyCategories = getTaxonomyCategories();

		if (taxonomyCategories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategories\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategories.length; i++) {
				sb.append(String.valueOf(taxonomyCategories[i]));

				if ((i + 1) < taxonomyCategories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long taxonomyVocabularyId = getTaxonomyVocabularyId();

		if (taxonomyVocabularyId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyVocabularyId\": ");

			sb.append(taxonomyVocabularyId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.bulk.rest.dto.v1_0.TaxonomyVocabulary",
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