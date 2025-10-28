/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.object.dto.v1_0.Scope;
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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("TaxonomyCategoryBrief")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaxonomyCategoryBrief")
public class TaxonomyCategoryBrief implements Serializable {

	public static TaxonomyCategoryBrief toDTO(String json) {
		return ObjectMapperUtil.readValue(TaxonomyCategoryBrief.class, json);
	}

	public static TaxonomyCategoryBrief unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			TaxonomyCategoryBrief.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the embedded taxonomy category, can be embedded with nestedFields"
	)
	@Valid
	public Object getEmbeddedTaxonomyCategory() {
		if (_embeddedTaxonomyCategorySupplier != null) {
			embeddedTaxonomyCategory = _embeddedTaxonomyCategorySupplier.get();

			_embeddedTaxonomyCategorySupplier = null;
		}

		return embeddedTaxonomyCategory;
	}

	public void setEmbeddedTaxonomyCategory(Object embeddedTaxonomyCategory) {
		this.embeddedTaxonomyCategory = embeddedTaxonomyCategory;

		_embeddedTaxonomyCategorySupplier = null;
	}

	@JsonIgnore
	public void setEmbeddedTaxonomyCategory(
		UnsafeSupplier<Object, Exception>
			embeddedTaxonomyCategoryUnsafeSupplier) {

		_embeddedTaxonomyCategorySupplier = () -> {
			try {
				return embeddedTaxonomyCategoryUnsafeSupplier.get();
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
		description = "Optional field with the embedded taxonomy category, can be embedded with nestedFields"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Object embeddedTaxonomyCategory;

	@JsonIgnore
	private Supplier<Object> _embeddedTaxonomyCategorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's parent category."
	)
	@Valid
	public ParentTaxonomyCategory getParentTaxonomyCategory() {
		if (_parentTaxonomyCategorySupplier != null) {
			parentTaxonomyCategory = _parentTaxonomyCategorySupplier.get();

			_parentTaxonomyCategorySupplier = null;
		}

		return parentTaxonomyCategory;
	}

	public void setParentTaxonomyCategory(
		ParentTaxonomyCategory parentTaxonomyCategory) {

		this.parentTaxonomyCategory = parentTaxonomyCategory;

		_parentTaxonomyCategorySupplier = null;
	}

	@JsonIgnore
	public void setParentTaxonomyCategory(
		UnsafeSupplier<ParentTaxonomyCategory, Exception>
			parentTaxonomyCategoryUnsafeSupplier) {

		_parentTaxonomyCategorySupplier = () -> {
			try {
				return parentTaxonomyCategoryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The category's parent category.")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected ParentTaxonomyCategory parentTaxonomyCategory;

	@JsonIgnore
	private Supplier<ParentTaxonomyCategory> _parentTaxonomyCategorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent category's `TaxonomyVocabulary`."
	)
	@Valid
	public ParentTaxonomyVocabulary getParentTaxonomyVocabulary() {
		if (_parentTaxonomyVocabularySupplier != null) {
			parentTaxonomyVocabulary = _parentTaxonomyVocabularySupplier.get();

			_parentTaxonomyVocabularySupplier = null;
		}

		return parentTaxonomyVocabulary;
	}

	public void setParentTaxonomyVocabulary(
		ParentTaxonomyVocabulary parentTaxonomyVocabulary) {

		this.parentTaxonomyVocabulary = parentTaxonomyVocabulary;

		_parentTaxonomyVocabularySupplier = null;
	}

	@JsonIgnore
	public void setParentTaxonomyVocabulary(
		UnsafeSupplier<ParentTaxonomyVocabulary, Exception>
			parentTaxonomyVocabularyUnsafeSupplier) {

		_parentTaxonomyVocabularySupplier = () -> {
			try {
				return parentTaxonomyVocabularyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The parent category's `TaxonomyVocabulary`.")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected ParentTaxonomyVocabulary parentTaxonomyVocabulary;

	@JsonIgnore
	private Supplier<ParentTaxonomyVocabulary>
		_parentTaxonomyVocabularySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Scope getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(UnsafeSupplier<Scope, Exception> scopeUnsafeSupplier) {
		_scopeSupplier = () -> {
			try {
				return scopeUnsafeSupplier.get();
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
	protected Scope scope;

	@JsonIgnore
	private Supplier<Scope> _scopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's external reference code."
	)
	public String getTaxonomyCategoryExternalReferenceCode() {
		if (_taxonomyCategoryExternalReferenceCodeSupplier != null) {
			taxonomyCategoryExternalReferenceCode =
				_taxonomyCategoryExternalReferenceCodeSupplier.get();

			_taxonomyCategoryExternalReferenceCodeSupplier = null;
		}

		return taxonomyCategoryExternalReferenceCode;
	}

	public void setTaxonomyCategoryExternalReferenceCode(
		String taxonomyCategoryExternalReferenceCode) {

		this.taxonomyCategoryExternalReferenceCode =
			taxonomyCategoryExternalReferenceCode;

		_taxonomyCategoryExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			taxonomyCategoryExternalReferenceCodeUnsafeSupplier) {

		_taxonomyCategoryExternalReferenceCodeSupplier = () -> {
			try {
				return taxonomyCategoryExternalReferenceCodeUnsafeSupplier.
					get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The category's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String taxonomyCategoryExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _taxonomyCategoryExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's ID. This can be used to retrieve more information in the `TaxonomyCategory` API."
	)
	public Long getTaxonomyCategoryId() {
		if (_taxonomyCategoryIdSupplier != null) {
			taxonomyCategoryId = _taxonomyCategoryIdSupplier.get();

			_taxonomyCategoryIdSupplier = null;
		}

		return taxonomyCategoryId;
	}

	public void setTaxonomyCategoryId(Long taxonomyCategoryId) {
		this.taxonomyCategoryId = taxonomyCategoryId;

		_taxonomyCategoryIdSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryId(
		UnsafeSupplier<Long, Exception> taxonomyCategoryIdUnsafeSupplier) {

		_taxonomyCategoryIdSupplier = () -> {
			try {
				return taxonomyCategoryIdUnsafeSupplier.get();
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
		description = "The category's ID. This can be used to retrieve more information in the `TaxonomyCategory` API."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long taxonomyCategoryId;

	@JsonIgnore
	private Supplier<Long> _taxonomyCategoryIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's name."
	)
	public String getTaxonomyCategoryName() {
		if (_taxonomyCategoryNameSupplier != null) {
			taxonomyCategoryName = _taxonomyCategoryNameSupplier.get();

			_taxonomyCategoryNameSupplier = null;
		}

		return taxonomyCategoryName;
	}

	public void setTaxonomyCategoryName(String taxonomyCategoryName) {
		this.taxonomyCategoryName = taxonomyCategoryName;

		_taxonomyCategoryNameSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryName(
		UnsafeSupplier<String, Exception> taxonomyCategoryNameUnsafeSupplier) {

		_taxonomyCategoryNameSupplier = () -> {
			try {
				return taxonomyCategoryNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The category's name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String taxonomyCategoryName;

	@JsonIgnore
	private Supplier<String> _taxonomyCategoryNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized category's names."
	)
	@Valid
	public Map<String, String> getTaxonomyCategoryName_i18n() {
		if (_taxonomyCategoryName_i18nSupplier != null) {
			taxonomyCategoryName_i18n =
				_taxonomyCategoryName_i18nSupplier.get();

			_taxonomyCategoryName_i18nSupplier = null;
		}

		return taxonomyCategoryName_i18n;
	}

	public void setTaxonomyCategoryName_i18n(
		Map<String, String> taxonomyCategoryName_i18n) {

		this.taxonomyCategoryName_i18n = taxonomyCategoryName_i18n;

		_taxonomyCategoryName_i18nSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			taxonomyCategoryName_i18nUnsafeSupplier) {

		_taxonomyCategoryName_i18nSupplier = () -> {
			try {
				return taxonomyCategoryName_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized category's names.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> taxonomyCategoryName_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _taxonomyCategoryName_i18nSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyCategoryBrief)) {
			return false;
		}

		TaxonomyCategoryBrief taxonomyCategoryBrief =
			(TaxonomyCategoryBrief)object;

		return Objects.equals(toString(), taxonomyCategoryBrief.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object embeddedTaxonomyCategory = getEmbeddedTaxonomyCategory();

		if (embeddedTaxonomyCategory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embeddedTaxonomyCategory\": ");

			if (embeddedTaxonomyCategory instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)embeddedTaxonomyCategory));
			}
			else if (embeddedTaxonomyCategory instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)embeddedTaxonomyCategory));
				sb.append("\"");
			}
			else {
				sb.append(embeddedTaxonomyCategory);
			}
		}

		ParentTaxonomyCategory parentTaxonomyCategory =
			getParentTaxonomyCategory();

		if (parentTaxonomyCategory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyCategory\": ");

			sb.append(String.valueOf(parentTaxonomyCategory));
		}

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			getParentTaxonomyVocabulary();

		if (parentTaxonomyVocabulary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyVocabulary\": ");

			sb.append(String.valueOf(parentTaxonomyVocabulary));
		}

		Scope scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(scope);
		}

		String taxonomyCategoryExternalReferenceCode =
			getTaxonomyCategoryExternalReferenceCode();

		if (taxonomyCategoryExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryExternalReferenceCode));

			sb.append("\"");
		}

		Long taxonomyCategoryId = getTaxonomyCategoryId();

		if (taxonomyCategoryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryId\": ");

			sb.append(taxonomyCategoryId);
		}

		String taxonomyCategoryName = getTaxonomyCategoryName();

		if (taxonomyCategoryName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryName\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryName));

			sb.append("\"");
		}

		Map<String, String> taxonomyCategoryName_i18n =
			getTaxonomyCategoryName_i18n();

		if (taxonomyCategoryName_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryName_i18n\": ");

			sb.append(_toJSON(taxonomyCategoryName_i18n));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.rest.dto.v1_0.TaxonomyCategoryBrief",
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