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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
@GraphQLName("TaxonomyCategoryBrief")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {
		"parentTaxonomyVocabulary", "taxonomyCategoryExternalReferenceCode"
	}
)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected ParentTaxonomyVocabulary parentTaxonomyVocabulary;

	@JsonIgnore
	private Supplier<ParentTaxonomyVocabulary>
		_parentTaxonomyVocabularySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.scope.Scope getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(com.liferay.portal.vulcan.scope.Scope scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(
		UnsafeSupplier<com.liferay.portal.vulcan.scope.Scope, Exception>
			scopeUnsafeSupplier) {

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
	protected com.liferay.portal.vulcan.scope.Scope scope;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.scope.Scope> _scopeSupplier;

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
	@NotEmpty
	protected String taxonomyCategoryExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _taxonomyCategoryExternalReferenceCodeSupplier;

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

		com.liferay.portal.vulcan.scope.Scope scope = getScope();

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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.TaxonomyCategoryBrief",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:1286441114