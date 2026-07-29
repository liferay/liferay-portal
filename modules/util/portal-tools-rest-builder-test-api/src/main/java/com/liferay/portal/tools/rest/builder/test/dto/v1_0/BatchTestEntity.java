/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.dto.v1_0;

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
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("BatchTestEntity")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BatchTestEntity")
public class BatchTestEntity implements Serializable {

	public static BatchTestEntity toDTO(String json) {
		return ObjectMapperUtil.readValue(BatchTestEntity.class, json);
	}

	public static BatchTestEntity unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BatchTestEntity.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAcceptAllLanguages() {
		if (_acceptAllLanguagesSupplier != null) {
			acceptAllLanguages = _acceptAllLanguagesSupplier.get();

			_acceptAllLanguagesSupplier = null;
		}

		return acceptAllLanguages;
	}

	public void setAcceptAllLanguages(Boolean acceptAllLanguages) {
		this.acceptAllLanguages = acceptAllLanguages;

		_acceptAllLanguagesSupplier = null;
	}

	@JsonIgnore
	public void setAcceptAllLanguages(
		UnsafeSupplier<Boolean, Exception> acceptAllLanguagesUnsafeSupplier) {

		_acceptAllLanguagesSupplier = () -> {
			try {
				return acceptAllLanguagesUnsafeSupplier.get();
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
	protected Boolean acceptAllLanguages;

	@JsonIgnore
	private Supplier<Boolean> _acceptAllLanguagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
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
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getEmbeddedNestedField() {
		if (_embeddedNestedFieldSupplier != null) {
			embeddedNestedField = _embeddedNestedFieldSupplier.get();

			_embeddedNestedFieldSupplier = null;
		}

		return embeddedNestedField;
	}

	public void setEmbeddedNestedField(Object embeddedNestedField) {
		this.embeddedNestedField = embeddedNestedField;

		_embeddedNestedFieldSupplier = null;
	}

	@JsonIgnore
	public void setEmbeddedNestedField(
		UnsafeSupplier<Object, Exception> embeddedNestedFieldUnsafeSupplier) {

		_embeddedNestedFieldSupplier = () -> {
			try {
				return embeddedNestedFieldUnsafeSupplier.get();
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
	protected Object embeddedNestedField;

	@JsonIgnore
	private Supplier<Object> _embeddedNestedFieldSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

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
	public String getNestedField1() {
		if (_nestedField1Supplier != null) {
			nestedField1 = _nestedField1Supplier.get();

			_nestedField1Supplier = null;
		}

		return nestedField1;
	}

	public void setNestedField1(String nestedField1) {
		this.nestedField1 = nestedField1;

		_nestedField1Supplier = null;
	}

	@JsonIgnore
	public void setNestedField1(
		UnsafeSupplier<String, Exception> nestedField1UnsafeSupplier) {

		_nestedField1Supplier = () -> {
			try {
				return nestedField1UnsafeSupplier.get();
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
	protected String nestedField1;

	@JsonIgnore
	private Supplier<String> _nestedField1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getNestedField2() {
		if (_nestedField2Supplier != null) {
			nestedField2 = _nestedField2Supplier.get();

			_nestedField2Supplier = null;
		}

		return nestedField2;
	}

	public void setNestedField2(String nestedField2) {
		this.nestedField2 = nestedField2;

		_nestedField2Supplier = null;
	}

	@JsonIgnore
	public void setNestedField2(
		UnsafeSupplier<String, Exception> nestedField2UnsafeSupplier) {

		_nestedField2Supplier = () -> {
			try {
				return nestedField2UnsafeSupplier.get();
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
	protected String nestedField2;

	@JsonIgnore
	private Supplier<String> _nestedField2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CompanyTestEntity getRelatedCompanyTestEntity() {
		if (_relatedCompanyTestEntitySupplier != null) {
			relatedCompanyTestEntity = _relatedCompanyTestEntitySupplier.get();

			_relatedCompanyTestEntitySupplier = null;
		}

		return relatedCompanyTestEntity;
	}

	public void setRelatedCompanyTestEntity(
		CompanyTestEntity relatedCompanyTestEntity) {

		this.relatedCompanyTestEntity = relatedCompanyTestEntity;

		_relatedCompanyTestEntitySupplier = null;
	}

	@JsonIgnore
	public void setRelatedCompanyTestEntity(
		UnsafeSupplier<CompanyTestEntity, Exception>
			relatedCompanyTestEntityUnsafeSupplier) {

		_relatedCompanyTestEntitySupplier = () -> {
			try {
				return relatedCompanyTestEntityUnsafeSupplier.get();
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
	protected CompanyTestEntity relatedCompanyTestEntity;

	@JsonIgnore
	private Supplier<CompanyTestEntity> _relatedCompanyTestEntitySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BatchTestEntity)) {
			return false;
		}

		BatchTestEntity batchTestEntity = (BatchTestEntity)object;

		return Objects.equals(toString(), batchTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean acceptAllLanguages = getAcceptAllLanguages();

		if (acceptAllLanguages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"acceptAllLanguages\": ");

			sb.append(acceptAllLanguages);
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Object embeddedNestedField = getEmbeddedNestedField();

		if (embeddedNestedField != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embeddedNestedField\": ");

			if (embeddedNestedField instanceof Collection) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						(Collection<?>)embeddedNestedField));
			}
			else if (embeddedNestedField instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)embeddedNestedField));
			}
			else if (embeddedNestedField instanceof Object[]) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						Arrays.asList((Object[])embeddedNestedField)));
			}
			else if (embeddedNestedField instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)embeddedNestedField));
				sb.append("\"");
			}
			else {
				sb.append(embeddedNestedField);
			}
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		String nestedField1 = getNestedField1();

		if (nestedField1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedField1\": ");

			sb.append("\"");

			sb.append(_escape(nestedField1));

			sb.append("\"");
		}

		String nestedField2 = getNestedField2();

		if (nestedField2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedField2\": ");

			sb.append("\"");

			sb.append(_escape(nestedField2));

			sb.append("\"");
		}

		CompanyTestEntity relatedCompanyTestEntity =
			getRelatedCompanyTestEntity();

		if (relatedCompanyTestEntity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedCompanyTestEntity\": ");

			sb.append(String.valueOf(relatedCompanyTestEntity));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity",
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
// LIFERAY-REST-BUILDER-HASH:-1419626643