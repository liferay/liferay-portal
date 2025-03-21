/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectField implements Cloneable, Serializable {

	public static ObjectField toDTO(String json) {
		return ObjectFieldSerDes.toDTO(json);
	}

	public DBType getDBType() {
		return DBType;
	}

	public String getDBTypeAsString() {
		if (DBType == null) {
			return null;
		}

		return DBType.toString();
	}

	public void setDBType(DBType DBType) {
		this.DBType = DBType;
	}

	public void setDBType(
		UnsafeSupplier<DBType, Exception> DBTypeUnsafeSupplier) {

		try {
			DBType = DBTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DBType DBType;

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public BusinessType getBusinessType() {
		return businessType;
	}

	public String getBusinessTypeAsString() {
		if (businessType == null) {
			return null;
		}

		return businessType.toString();
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public void setBusinessType(
		UnsafeSupplier<BusinessType, Exception> businessTypeUnsafeSupplier) {

		try {
			businessType = businessTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BusinessType businessType;

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier<String, Exception> defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultValue;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public void setIndexed(
		UnsafeSupplier<Boolean, Exception> indexedUnsafeSupplier) {

		try {
			indexed = indexedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean indexed;

	public Boolean getIndexedAsKeyword() {
		return indexedAsKeyword;
	}

	public void setIndexedAsKeyword(Boolean indexedAsKeyword) {
		this.indexedAsKeyword = indexedAsKeyword;
	}

	public void setIndexedAsKeyword(
		UnsafeSupplier<Boolean, Exception> indexedAsKeywordUnsafeSupplier) {

		try {
			indexedAsKeyword = indexedAsKeywordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean indexedAsKeyword;

	public String getIndexedLanguageId() {
		return indexedLanguageId;
	}

	public void setIndexedLanguageId(String indexedLanguageId) {
		this.indexedLanguageId = indexedLanguageId;
	}

	public void setIndexedLanguageId(
		UnsafeSupplier<String, Exception> indexedLanguageIdUnsafeSupplier) {

		try {
			indexedLanguageId = indexedLanguageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String indexedLanguageId;

	public Map<String, String> getLabel() {
		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> label;

	public String getListTypeDefinitionExternalReferenceCode() {
		return listTypeDefinitionExternalReferenceCode;
	}

	public void setListTypeDefinitionExternalReferenceCode(
		String listTypeDefinitionExternalReferenceCode) {

		this.listTypeDefinitionExternalReferenceCode =
			listTypeDefinitionExternalReferenceCode;
	}

	public void setListTypeDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			listTypeDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			listTypeDefinitionExternalReferenceCode =
				listTypeDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String listTypeDefinitionExternalReferenceCode;

	public Long getListTypeDefinitionId() {
		return listTypeDefinitionId;
	}

	public void setListTypeDefinitionId(Long listTypeDefinitionId) {
		this.listTypeDefinitionId = listTypeDefinitionId;
	}

	public void setListTypeDefinitionId(
		UnsafeSupplier<Long, Exception> listTypeDefinitionIdUnsafeSupplier) {

		try {
			listTypeDefinitionId = listTypeDefinitionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long listTypeDefinitionId;

	public Boolean getLocalized() {
		return localized;
	}

	public void setLocalized(Boolean localized) {
		this.localized = localized;
	}

	public void setLocalized(
		UnsafeSupplier<Boolean, Exception> localizedUnsafeSupplier) {

		try {
			localized = localizedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean localized;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public String getObjectDefinitionExternalReferenceCode1() {
		return objectDefinitionExternalReferenceCode1;
	}

	public void setObjectDefinitionExternalReferenceCode1(
		String objectDefinitionExternalReferenceCode1) {

		this.objectDefinitionExternalReferenceCode1 =
			objectDefinitionExternalReferenceCode1;
	}

	public void setObjectDefinitionExternalReferenceCode1(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCode1UnsafeSupplier) {

		try {
			objectDefinitionExternalReferenceCode1 =
				objectDefinitionExternalReferenceCode1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionExternalReferenceCode1;

	public ObjectFieldSetting[] getObjectFieldSettings() {
		return objectFieldSettings;
	}

	public void setObjectFieldSettings(
		ObjectFieldSetting[] objectFieldSettings) {

		this.objectFieldSettings = objectFieldSettings;
	}

	public void setObjectFieldSettings(
		UnsafeSupplier<ObjectFieldSetting[], Exception>
			objectFieldSettingsUnsafeSupplier) {

		try {
			objectFieldSettings = objectFieldSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectFieldSetting[] objectFieldSettings;

	public String getObjectRelationshipExternalReferenceCode() {
		return objectRelationshipExternalReferenceCode;
	}

	public void setObjectRelationshipExternalReferenceCode(
		String objectRelationshipExternalReferenceCode) {

		this.objectRelationshipExternalReferenceCode =
			objectRelationshipExternalReferenceCode;
	}

	public void setObjectRelationshipExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectRelationshipExternalReferenceCodeUnsafeSupplier) {

		try {
			objectRelationshipExternalReferenceCode =
				objectRelationshipExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectRelationshipExternalReferenceCode;

	public ReadOnly getReadOnly() {
		return readOnly;
	}

	public String getReadOnlyAsString() {
		if (readOnly == null) {
			return null;
		}

		return readOnly.toString();
	}

	public void setReadOnly(ReadOnly readOnly) {
		this.readOnly = readOnly;
	}

	public void setReadOnly(
		UnsafeSupplier<ReadOnly, Exception> readOnlyUnsafeSupplier) {

		try {
			readOnly = readOnlyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ReadOnly readOnly;

	public String getReadOnlyConditionExpression() {
		return readOnlyConditionExpression;
	}

	public void setReadOnlyConditionExpression(
		String readOnlyConditionExpression) {

		this.readOnlyConditionExpression = readOnlyConditionExpression;
	}

	public void setReadOnlyConditionExpression(
		UnsafeSupplier<String, Exception>
			readOnlyConditionExpressionUnsafeSupplier) {

		try {
			readOnlyConditionExpression =
				readOnlyConditionExpressionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String readOnlyConditionExpression;

	public RelationshipType getRelationshipType() {
		return relationshipType;
	}

	public String getRelationshipTypeAsString() {
		if (relationshipType == null) {
			return null;
		}

		return relationshipType.toString();
	}

	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	public void setRelationshipType(
		UnsafeSupplier<RelationshipType, Exception>
			relationshipTypeUnsafeSupplier) {

		try {
			relationshipType = relationshipTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RelationshipType relationshipType;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		try {
			required = requiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean required;

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public void setState(
		UnsafeSupplier<Boolean, Exception> stateUnsafeSupplier) {

		try {
			state = stateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean state;

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		try {
			system = systemUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean system;

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public void setUnique(
		UnsafeSupplier<Boolean, Exception> uniqueUnsafeSupplier) {

		try {
			unique = uniqueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean unique;

	@Override
	public ObjectField clone() throws CloneNotSupportedException {
		return (ObjectField)super.clone();
	}

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
		return ObjectFieldSerDes.toJSON(this);
	}

	public static enum BusinessType {

		AGGREGATION("Aggregation"), ATTACHMENT("Attachment"),
		AUTO_INCREMENT("AutoIncrement"), BOOLEAN("Boolean"), DATE("Date"),
		DATE_TIME("DateTime"), DECIMAL("Decimal"), ENCRYPTED("Encrypted"),
		FORMULA("Formula"), INTEGER("Integer"), LONG_INTEGER("LongInteger"),
		LONG_TEXT("LongText"), MULTISELECT_PICKLIST("MultiselectPicklist"),
		PICKLIST("Picklist"), PRECISION_DECIMAL("PrecisionDecimal"),
		RELATIONSHIP("Relationship"), RICH_TEXT("RichText"), TEXT("Text");

		public static BusinessType create(String value) {
			for (BusinessType businessType : values()) {
				if (Objects.equals(businessType.getValue(), value) ||
					Objects.equals(businessType.name(), value)) {

					return businessType;
				}
			}

			return null;
		}

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

	public static enum DBType {

		BIG_DECIMAL("BigDecimal"), BOOLEAN("Boolean"), CLOB("Clob"),
		DATE("Date"), DATE_TIME("DateTime"), DOUBLE("Double"),
		INTEGER("Integer"), LONG("Long"), STRING("String");

		public static DBType create(String value) {
			for (DBType dbType : values()) {
				if (Objects.equals(dbType.getValue(), value) ||
					Objects.equals(dbType.name(), value)) {

					return dbType;
				}
			}

			return null;
		}

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

	public static enum ReadOnly {

		CONDITIONAL("conditional"), FALSE("false"), TRUE("true");

		public static ReadOnly create(String value) {
			for (ReadOnly readOnly : values()) {
				if (Objects.equals(readOnly.getValue(), value) ||
					Objects.equals(readOnly.name(), value)) {

					return readOnly;
				}
			}

			return null;
		}

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

	public static enum RelationshipType {

		ONE_TO_MANY("oneToMany"), ONE_TO_ONE("oneToOne");

		public static RelationshipType create(String value) {
			for (RelationshipType relationshipType : values()) {
				if (Objects.equals(relationshipType.getValue(), value) ||
					Objects.equals(relationshipType.name(), value)) {

					return relationshipType;
				}
			}

			return null;
		}

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

	public static enum Type {

		BIG_DECIMAL("BigDecimal"), BOOLEAN("Boolean"), CLOB("Clob"),
		DATE("Date"), DATE_TIME("DateTime"), DOUBLE("Double"),
		INTEGER("Integer"), LONG("Long"), STRING("String");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
				}
			}

			return null;
		}

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

}