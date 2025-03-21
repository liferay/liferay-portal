/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectField")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectField")
public class ObjectField implements Serializable {

	public static ObjectField toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectField.class, json);
	}

	public static ObjectField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectField.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("DBType")
	@Valid
	public DBType getDBType() {
		if (_DBTypeSupplier != null) {
			DBType = _DBTypeSupplier.get();

			_DBTypeSupplier = null;
		}

		return DBType;
	}

	@JsonIgnore
	public String getDBTypeAsString() {
		DBType DBType = getDBType();

		if (DBType == null) {
			return null;
		}

		return DBType.toString();
	}

	public void setDBType(DBType DBType) {
		this.DBType = DBType;

		_DBTypeSupplier = null;
	}

	@JsonIgnore
	public void setDBType(
		UnsafeSupplier<DBType, Exception> DBTypeUnsafeSupplier) {

		_DBTypeSupplier = () -> {
			try {
				return DBTypeUnsafeSupplier.get();
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
	protected DBType DBType;

	@JsonIgnore
	private Supplier<DBType> _DBTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("businessType")
	@Valid
	public BusinessType getBusinessType() {
		if (_businessTypeSupplier != null) {
			businessType = _businessTypeSupplier.get();

			_businessTypeSupplier = null;
		}

		return businessType;
	}

	@JsonIgnore
	public String getBusinessTypeAsString() {
		BusinessType businessType = getBusinessType();

		if (businessType == null) {
			return null;
		}

		return businessType.toString();
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;

		_businessTypeSupplier = null;
	}

	@JsonIgnore
	public void setBusinessType(
		UnsafeSupplier<BusinessType, Exception> businessTypeUnsafeSupplier) {

		_businessTypeSupplier = () -> {
			try {
				return businessTypeUnsafeSupplier.get();
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
	protected BusinessType businessType;

	@JsonIgnore
	private Supplier<BusinessType> _businessTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public String getDefaultValue() {
		if (_defaultValueSupplier != null) {
			defaultValue = _defaultValueSupplier.get();

			_defaultValueSupplier = null;
		}

		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;

		_defaultValueSupplier = null;
	}

	@JsonIgnore
	public void setDefaultValue(
		UnsafeSupplier<String, Exception> defaultValueUnsafeSupplier) {

		_defaultValueSupplier = () -> {
			try {
				return defaultValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String defaultValue;

	@JsonIgnore
	private Supplier<String> _defaultValueSupplier;

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
	public Boolean getIndexed() {
		if (_indexedSupplier != null) {
			indexed = _indexedSupplier.get();

			_indexedSupplier = null;
		}

		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;

		_indexedSupplier = null;
	}

	@JsonIgnore
	public void setIndexed(
		UnsafeSupplier<Boolean, Exception> indexedUnsafeSupplier) {

		_indexedSupplier = () -> {
			try {
				return indexedUnsafeSupplier.get();
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
	protected Boolean indexed;

	@JsonIgnore
	private Supplier<Boolean> _indexedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getIndexedAsKeyword() {
		if (_indexedAsKeywordSupplier != null) {
			indexedAsKeyword = _indexedAsKeywordSupplier.get();

			_indexedAsKeywordSupplier = null;
		}

		return indexedAsKeyword;
	}

	public void setIndexedAsKeyword(Boolean indexedAsKeyword) {
		this.indexedAsKeyword = indexedAsKeyword;

		_indexedAsKeywordSupplier = null;
	}

	@JsonIgnore
	public void setIndexedAsKeyword(
		UnsafeSupplier<Boolean, Exception> indexedAsKeywordUnsafeSupplier) {

		_indexedAsKeywordSupplier = () -> {
			try {
				return indexedAsKeywordUnsafeSupplier.get();
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
	protected Boolean indexedAsKeyword;

	@JsonIgnore
	private Supplier<Boolean> _indexedAsKeywordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getIndexedLanguageId() {
		if (_indexedLanguageIdSupplier != null) {
			indexedLanguageId = _indexedLanguageIdSupplier.get();

			_indexedLanguageIdSupplier = null;
		}

		return indexedLanguageId;
	}

	public void setIndexedLanguageId(String indexedLanguageId) {
		this.indexedLanguageId = indexedLanguageId;

		_indexedLanguageIdSupplier = null;
	}

	@JsonIgnore
	public void setIndexedLanguageId(
		UnsafeSupplier<String, Exception> indexedLanguageIdUnsafeSupplier) {

		_indexedLanguageIdSupplier = () -> {
			try {
				return indexedLanguageIdUnsafeSupplier.get();
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
	protected String indexedLanguageId;

	@JsonIgnore
	private Supplier<String> _indexedLanguageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
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
	protected Map<String, String> label;

	@JsonIgnore
	private Supplier<Map<String, String>> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getListTypeDefinitionExternalReferenceCode() {
		if (_listTypeDefinitionExternalReferenceCodeSupplier != null) {
			listTypeDefinitionExternalReferenceCode =
				_listTypeDefinitionExternalReferenceCodeSupplier.get();

			_listTypeDefinitionExternalReferenceCodeSupplier = null;
		}

		return listTypeDefinitionExternalReferenceCode;
	}

	public void setListTypeDefinitionExternalReferenceCode(
		String listTypeDefinitionExternalReferenceCode) {

		this.listTypeDefinitionExternalReferenceCode =
			listTypeDefinitionExternalReferenceCode;

		_listTypeDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setListTypeDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			listTypeDefinitionExternalReferenceCodeUnsafeSupplier) {

		_listTypeDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return listTypeDefinitionExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String listTypeDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _listTypeDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getListTypeDefinitionId() {
		if (_listTypeDefinitionIdSupplier != null) {
			listTypeDefinitionId = _listTypeDefinitionIdSupplier.get();

			_listTypeDefinitionIdSupplier = null;
		}

		return listTypeDefinitionId;
	}

	public void setListTypeDefinitionId(Long listTypeDefinitionId) {
		this.listTypeDefinitionId = listTypeDefinitionId;

		_listTypeDefinitionIdSupplier = null;
	}

	@JsonIgnore
	public void setListTypeDefinitionId(
		UnsafeSupplier<Long, Exception> listTypeDefinitionIdUnsafeSupplier) {

		_listTypeDefinitionIdSupplier = () -> {
			try {
				return listTypeDefinitionIdUnsafeSupplier.get();
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
	protected Long listTypeDefinitionId;

	@JsonIgnore
	private Supplier<Long> _listTypeDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLocalized() {
		if (_localizedSupplier != null) {
			localized = _localizedSupplier.get();

			_localizedSupplier = null;
		}

		return localized;
	}

	public void setLocalized(Boolean localized) {
		this.localized = localized;

		_localizedSupplier = null;
	}

	@JsonIgnore
	public void setLocalized(
		UnsafeSupplier<Boolean, Exception> localizedUnsafeSupplier) {

		_localizedSupplier = () -> {
			try {
				return localizedUnsafeSupplier.get();
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
	protected Boolean localized;

	@JsonIgnore
	private Supplier<Boolean> _localizedSupplier;

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
	public String getObjectDefinitionExternalReferenceCode1() {
		if (_objectDefinitionExternalReferenceCode1Supplier != null) {
			objectDefinitionExternalReferenceCode1 =
				_objectDefinitionExternalReferenceCode1Supplier.get();

			_objectDefinitionExternalReferenceCode1Supplier = null;
		}

		return objectDefinitionExternalReferenceCode1;
	}

	public void setObjectDefinitionExternalReferenceCode1(
		String objectDefinitionExternalReferenceCode1) {

		this.objectDefinitionExternalReferenceCode1 =
			objectDefinitionExternalReferenceCode1;

		_objectDefinitionExternalReferenceCode1Supplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode1(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCode1UnsafeSupplier) {

		_objectDefinitionExternalReferenceCode1Supplier = () -> {
			try {
				return objectDefinitionExternalReferenceCode1UnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String objectDefinitionExternalReferenceCode1;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCode1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectFieldSetting[] getObjectFieldSettings() {
		if (_objectFieldSettingsSupplier != null) {
			objectFieldSettings = _objectFieldSettingsSupplier.get();

			_objectFieldSettingsSupplier = null;
		}

		return objectFieldSettings;
	}

	public void setObjectFieldSettings(
		ObjectFieldSetting[] objectFieldSettings) {

		this.objectFieldSettings = objectFieldSettings;

		_objectFieldSettingsSupplier = null;
	}

	@JsonIgnore
	public void setObjectFieldSettings(
		UnsafeSupplier<ObjectFieldSetting[], Exception>
			objectFieldSettingsUnsafeSupplier) {

		_objectFieldSettingsSupplier = () -> {
			try {
				return objectFieldSettingsUnsafeSupplier.get();
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
	protected ObjectFieldSetting[] objectFieldSettings;

	@JsonIgnore
	private Supplier<ObjectFieldSetting[]> _objectFieldSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectRelationshipExternalReferenceCode() {
		if (_objectRelationshipExternalReferenceCodeSupplier != null) {
			objectRelationshipExternalReferenceCode =
				_objectRelationshipExternalReferenceCodeSupplier.get();

			_objectRelationshipExternalReferenceCodeSupplier = null;
		}

		return objectRelationshipExternalReferenceCode;
	}

	public void setObjectRelationshipExternalReferenceCode(
		String objectRelationshipExternalReferenceCode) {

		this.objectRelationshipExternalReferenceCode =
			objectRelationshipExternalReferenceCode;

		_objectRelationshipExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectRelationshipExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectRelationshipExternalReferenceCodeUnsafeSupplier) {

		_objectRelationshipExternalReferenceCodeSupplier = () -> {
			try {
				return objectRelationshipExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String objectRelationshipExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectRelationshipExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("readOnly")
	@Valid
	public ReadOnly getReadOnly() {
		if (_readOnlySupplier != null) {
			readOnly = _readOnlySupplier.get();

			_readOnlySupplier = null;
		}

		return readOnly;
	}

	@JsonIgnore
	public String getReadOnlyAsString() {
		ReadOnly readOnly = getReadOnly();

		if (readOnly == null) {
			return null;
		}

		return readOnly.toString();
	}

	public void setReadOnly(ReadOnly readOnly) {
		this.readOnly = readOnly;

		_readOnlySupplier = null;
	}

	@JsonIgnore
	public void setReadOnly(
		UnsafeSupplier<ReadOnly, Exception> readOnlyUnsafeSupplier) {

		_readOnlySupplier = () -> {
			try {
				return readOnlyUnsafeSupplier.get();
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
	protected ReadOnly readOnly;

	@JsonIgnore
	private Supplier<ReadOnly> _readOnlySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getReadOnlyConditionExpression() {
		if (_readOnlyConditionExpressionSupplier != null) {
			readOnlyConditionExpression =
				_readOnlyConditionExpressionSupplier.get();

			_readOnlyConditionExpressionSupplier = null;
		}

		return readOnlyConditionExpression;
	}

	public void setReadOnlyConditionExpression(
		String readOnlyConditionExpression) {

		this.readOnlyConditionExpression = readOnlyConditionExpression;

		_readOnlyConditionExpressionSupplier = null;
	}

	@JsonIgnore
	public void setReadOnlyConditionExpression(
		UnsafeSupplier<String, Exception>
			readOnlyConditionExpressionUnsafeSupplier) {

		_readOnlyConditionExpressionSupplier = () -> {
			try {
				return readOnlyConditionExpressionUnsafeSupplier.get();
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
	protected String readOnlyConditionExpression;

	@JsonIgnore
	private Supplier<String> _readOnlyConditionExpressionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("relationshipType")
	@Valid
	public RelationshipType getRelationshipType() {
		if (_relationshipTypeSupplier != null) {
			relationshipType = _relationshipTypeSupplier.get();

			_relationshipTypeSupplier = null;
		}

		return relationshipType;
	}

	@JsonIgnore
	public String getRelationshipTypeAsString() {
		RelationshipType relationshipType = getRelationshipType();

		if (relationshipType == null) {
			return null;
		}

		return relationshipType.toString();
	}

	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType = relationshipType;

		_relationshipTypeSupplier = null;
	}

	@JsonIgnore
	public void setRelationshipType(
		UnsafeSupplier<RelationshipType, Exception>
			relationshipTypeUnsafeSupplier) {

		_relationshipTypeSupplier = () -> {
			try {
				return relationshipTypeUnsafeSupplier.get();
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
	protected RelationshipType relationshipType;

	@JsonIgnore
	private Supplier<RelationshipType> _relationshipTypeSupplier;

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
	public Boolean getState() {
		if (_stateSupplier != null) {
			state = _stateSupplier.get();

			_stateSupplier = null;
		}

		return state;
	}

	public void setState(Boolean state) {
		this.state = state;

		_stateSupplier = null;
	}

	@JsonIgnore
	public void setState(
		UnsafeSupplier<Boolean, Exception> stateUnsafeSupplier) {

		_stateSupplier = () -> {
			try {
				return stateUnsafeSupplier.get();
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
	protected Boolean state;

	@JsonIgnore
	private Supplier<Boolean> _stateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSystem() {
		if (_systemSupplier != null) {
			system = _systemSupplier.get();

			_systemSupplier = null;
		}

		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;

		_systemSupplier = null;
	}

	@JsonIgnore
	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		_systemSupplier = () -> {
			try {
				return systemUnsafeSupplier.get();
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
	protected Boolean system;

	@JsonIgnore
	private Supplier<Boolean> _systemSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getUnique() {
		if (_uniqueSupplier != null) {
			unique = _uniqueSupplier.get();

			_uniqueSupplier = null;
		}

		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;

		_uniqueSupplier = null;
	}

	@JsonIgnore
	public void setUnique(
		UnsafeSupplier<Boolean, Exception> uniqueUnsafeSupplier) {

		_uniqueSupplier = () -> {
			try {
				return uniqueUnsafeSupplier.get();
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
	protected Boolean unique;

	@JsonIgnore
	private Supplier<Boolean> _uniqueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectField)) {
			return false;
		}

		ObjectField objectField = (ObjectField)object;

		return Objects.equals(toString(), objectField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DBType DBType = getDBType();

		if (DBType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"DBType\": ");

			sb.append("\"");

			sb.append(DBType);

			sb.append("\"");
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		BusinessType businessType = getBusinessType();

		if (businessType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"businessType\": ");

			sb.append("\"");

			sb.append(businessType);

			sb.append("\"");
		}

		String defaultValue = getDefaultValue();

		if (defaultValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append("\"");

			sb.append(_escape(defaultValue));

			sb.append("\"");
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

		Boolean indexed = getIndexed();

		if (indexed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(indexed);
		}

		Boolean indexedAsKeyword = getIndexedAsKeyword();

		if (indexedAsKeyword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexedAsKeyword\": ");

			sb.append(indexedAsKeyword);
		}

		String indexedLanguageId = getIndexedLanguageId();

		if (indexedLanguageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexedLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(indexedLanguageId));

			sb.append("\"");
		}

		Map<String, String> label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(label));
		}

		String listTypeDefinitionExternalReferenceCode =
			getListTypeDefinitionExternalReferenceCode();

		if (listTypeDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(listTypeDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		Long listTypeDefinitionId = getListTypeDefinitionId();

		if (listTypeDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionId\": ");

			sb.append(listTypeDefinitionId);
		}

		Boolean localized = getLocalized();

		if (localized != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localized\": ");

			sb.append(localized);
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

		String objectDefinitionExternalReferenceCode1 =
			getObjectDefinitionExternalReferenceCode1();

		if (objectDefinitionExternalReferenceCode1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode1\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode1));

			sb.append("\"");
		}

		ObjectFieldSetting[] objectFieldSettings = getObjectFieldSettings();

		if (objectFieldSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldSettings\": ");

			sb.append("[");

			for (int i = 0; i < objectFieldSettings.length; i++) {
				sb.append(String.valueOf(objectFieldSettings[i]));

				if ((i + 1) < objectFieldSettings.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String objectRelationshipExternalReferenceCode =
			getObjectRelationshipExternalReferenceCode();

		if (objectRelationshipExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationshipExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectRelationshipExternalReferenceCode));

			sb.append("\"");
		}

		ReadOnly readOnly = getReadOnly();

		if (readOnly != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append("\"");

			sb.append(readOnly);

			sb.append("\"");
		}

		String readOnlyConditionExpression = getReadOnlyConditionExpression();

		if (readOnlyConditionExpression != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnlyConditionExpression\": ");

			sb.append("\"");

			sb.append(_escape(readOnlyConditionExpression));

			sb.append("\"");
		}

		RelationshipType relationshipType = getRelationshipType();

		if (relationshipType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relationshipType\": ");

			sb.append("\"");

			sb.append(relationshipType);

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

		Boolean state = getState();

		if (state != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"state\": ");

			sb.append(state);
		}

		Boolean system = getSystem();

		if (system != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(system);
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

		Boolean unique = getUnique();

		if (unique != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unique\": ");

			sb.append(unique);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectField",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("BusinessType")
	public static enum BusinessType {

		AGGREGATION("Aggregation"), ATTACHMENT("Attachment"),
		AUTO_INCREMENT("AutoIncrement"), BOOLEAN("Boolean"), DATE("Date"),
		DATE_TIME("DateTime"), DECIMAL("Decimal"), ENCRYPTED("Encrypted"),
		FORMULA("Formula"), INTEGER("Integer"), LONG_INTEGER("LongInteger"),
		LONG_TEXT("LongText"), MULTISELECT_PICKLIST("MultiselectPicklist"),
		PICKLIST("Picklist"), PRECISION_DECIMAL("PrecisionDecimal"),
		RELATIONSHIP("Relationship"), RICH_TEXT("RichText"), TEXT("Text");

		@JsonCreator
		public static BusinessType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (BusinessType businessType : values()) {
				if (Objects.equals(businessType.getValue(), value)) {
					return businessType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private BusinessType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("DBType")
	public static enum DBType {

		BIG_DECIMAL("BigDecimal"), BOOLEAN("Boolean"), CLOB("Clob"),
		DATE("Date"), DATE_TIME("DateTime"), DOUBLE("Double"),
		INTEGER("Integer"), LONG("Long"), STRING("String");

		@JsonCreator
		public static DBType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DBType dbType : values()) {
				if (Objects.equals(dbType.getValue(), value)) {
					return dbType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private DBType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("ReadOnly")
	public static enum ReadOnly {

		CONDITIONAL("conditional"), FALSE("false"), TRUE("true");

		@JsonCreator
		public static ReadOnly create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ReadOnly readOnly : values()) {
				if (Objects.equals(readOnly.getValue(), value)) {
					return readOnly;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ReadOnly(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("RelationshipType")
	public static enum RelationshipType {

		ONE_TO_MANY("oneToMany"), ONE_TO_ONE("oneToOne");

		@JsonCreator
		public static RelationshipType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (RelationshipType relationshipType : values()) {
				if (Objects.equals(relationshipType.getValue(), value)) {
					return relationshipType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private RelationshipType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Type")
	public static enum Type {

		BIG_DECIMAL("BigDecimal"), BOOLEAN("Boolean"), CLOB("Clob"),
		DATE("Date"), DATE_TIME("DateTime"), DOUBLE("Double"),
		INTEGER("Integer"), LONG("Long"), STRING("String");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

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