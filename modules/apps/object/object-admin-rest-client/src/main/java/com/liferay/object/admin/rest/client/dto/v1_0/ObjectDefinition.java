/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectDefinitionSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectDefinition implements Cloneable, Serializable {

	public static ObjectDefinition toDTO(String json) {
		return ObjectDefinitionSerDes.toDTO(json);
	}

	public Boolean getAccountEntryRestricted() {
		return accountEntryRestricted;
	}

	public void setAccountEntryRestricted(Boolean accountEntryRestricted) {
		this.accountEntryRestricted = accountEntryRestricted;
	}

	public void setAccountEntryRestricted(
		UnsafeSupplier<Boolean, Exception>
			accountEntryRestrictedUnsafeSupplier) {

		try {
			accountEntryRestricted = accountEntryRestrictedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean accountEntryRestricted;

	public String getAccountEntryRestrictedObjectFieldName() {
		return accountEntryRestrictedObjectFieldName;
	}

	public void setAccountEntryRestrictedObjectFieldName(
		String accountEntryRestrictedObjectFieldName) {

		this.accountEntryRestrictedObjectFieldName =
			accountEntryRestrictedObjectFieldName;
	}

	public void setAccountEntryRestrictedObjectFieldName(
		UnsafeSupplier<String, Exception>
			accountEntryRestrictedObjectFieldNameUnsafeSupplier) {

		try {
			accountEntryRestrictedObjectFieldName =
				accountEntryRestrictedObjectFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountEntryRestrictedObjectFieldName;

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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		try {
			className = classNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String className;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getDefaultLanguageId() {
		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;
	}

	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		try {
			defaultLanguageId = defaultLanguageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultLanguageId;

	public Boolean getEnableCategorization() {
		return enableCategorization;
	}

	public void setEnableCategorization(Boolean enableCategorization) {
		this.enableCategorization = enableCategorization;
	}

	public void setEnableCategorization(
		UnsafeSupplier<Boolean, Exception> enableCategorizationUnsafeSupplier) {

		try {
			enableCategorization = enableCategorizationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableCategorization;

	public Boolean getEnableComments() {
		return enableComments;
	}

	public void setEnableComments(Boolean enableComments) {
		this.enableComments = enableComments;
	}

	public void setEnableComments(
		UnsafeSupplier<Boolean, Exception> enableCommentsUnsafeSupplier) {

		try {
			enableComments = enableCommentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableComments;

	public Boolean getEnableFriendlyURLCustomization() {
		return enableFriendlyURLCustomization;
	}

	public void setEnableFriendlyURLCustomization(
		Boolean enableFriendlyURLCustomization) {

		this.enableFriendlyURLCustomization = enableFriendlyURLCustomization;
	}

	public void setEnableFriendlyURLCustomization(
		UnsafeSupplier<Boolean, Exception>
			enableFriendlyURLCustomizationUnsafeSupplier) {

		try {
			enableFriendlyURLCustomization =
				enableFriendlyURLCustomizationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableFriendlyURLCustomization;

	public Boolean getEnableIndexSearch() {
		return enableIndexSearch;
	}

	public void setEnableIndexSearch(Boolean enableIndexSearch) {
		this.enableIndexSearch = enableIndexSearch;
	}

	public void setEnableIndexSearch(
		UnsafeSupplier<Boolean, Exception> enableIndexSearchUnsafeSupplier) {

		try {
			enableIndexSearch = enableIndexSearchUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableIndexSearch;

	public Boolean getEnableLocalization() {
		return enableLocalization;
	}

	public void setEnableLocalization(Boolean enableLocalization) {
		this.enableLocalization = enableLocalization;
	}

	public void setEnableLocalization(
		UnsafeSupplier<Boolean, Exception> enableLocalizationUnsafeSupplier) {

		try {
			enableLocalization = enableLocalizationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableLocalization;

	public Boolean getEnableObjectEntryDraft() {
		return enableObjectEntryDraft;
	}

	public void setEnableObjectEntryDraft(Boolean enableObjectEntryDraft) {
		this.enableObjectEntryDraft = enableObjectEntryDraft;
	}

	public void setEnableObjectEntryDraft(
		UnsafeSupplier<Boolean, Exception>
			enableObjectEntryDraftUnsafeSupplier) {

		try {
			enableObjectEntryDraft = enableObjectEntryDraftUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableObjectEntryDraft;

	public Boolean getEnableObjectEntryHistory() {
		return enableObjectEntryHistory;
	}

	public void setEnableObjectEntryHistory(Boolean enableObjectEntryHistory) {
		this.enableObjectEntryHistory = enableObjectEntryHistory;
	}

	public void setEnableObjectEntryHistory(
		UnsafeSupplier<Boolean, Exception>
			enableObjectEntryHistoryUnsafeSupplier) {

		try {
			enableObjectEntryHistory =
				enableObjectEntryHistoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableObjectEntryHistory;

	public Boolean getEnableObjectEntryVersioning() {
		return enableObjectEntryVersioning;
	}

	public void setEnableObjectEntryVersioning(
		Boolean enableObjectEntryVersioning) {

		this.enableObjectEntryVersioning = enableObjectEntryVersioning;
	}

	public void setEnableObjectEntryVersioning(
		UnsafeSupplier<Boolean, Exception>
			enableObjectEntryVersioningUnsafeSupplier) {

		try {
			enableObjectEntryVersioning =
				enableObjectEntryVersioningUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableObjectEntryVersioning;

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

	public Boolean getModifiable() {
		return modifiable;
	}

	public void setModifiable(Boolean modifiable) {
		this.modifiable = modifiable;
	}

	public void setModifiable(
		UnsafeSupplier<Boolean, Exception> modifiableUnsafeSupplier) {

		try {
			modifiable = modifiableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean modifiable;

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

	public ObjectAction[] getObjectActions() {
		return objectActions;
	}

	public void setObjectActions(ObjectAction[] objectActions) {
		this.objectActions = objectActions;
	}

	public void setObjectActions(
		UnsafeSupplier<ObjectAction[], Exception> objectActionsUnsafeSupplier) {

		try {
			objectActions = objectActionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectAction[] objectActions;

	public ObjectDefinitionSetting[] getObjectDefinitionSettings() {
		return objectDefinitionSettings;
	}

	public void setObjectDefinitionSettings(
		ObjectDefinitionSetting[] objectDefinitionSettings) {

		this.objectDefinitionSettings = objectDefinitionSettings;
	}

	public void setObjectDefinitionSettings(
		UnsafeSupplier<ObjectDefinitionSetting[], Exception>
			objectDefinitionSettingsUnsafeSupplier) {

		try {
			objectDefinitionSettings =
				objectDefinitionSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectDefinitionSetting[] objectDefinitionSettings;

	public ObjectField[] getObjectFields() {
		return objectFields;
	}

	public void setObjectFields(ObjectField[] objectFields) {
		this.objectFields = objectFields;
	}

	public void setObjectFields(
		UnsafeSupplier<ObjectField[], Exception> objectFieldsUnsafeSupplier) {

		try {
			objectFields = objectFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectField[] objectFields;

	public String getObjectFolderExternalReferenceCode() {
		return objectFolderExternalReferenceCode;
	}

	public void setObjectFolderExternalReferenceCode(
		String objectFolderExternalReferenceCode) {

		this.objectFolderExternalReferenceCode =
			objectFolderExternalReferenceCode;
	}

	public void setObjectFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectFolderExternalReferenceCodeUnsafeSupplier) {

		try {
			objectFolderExternalReferenceCode =
				objectFolderExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectFolderExternalReferenceCode;

	public ObjectLayout[] getObjectLayouts() {
		return objectLayouts;
	}

	public void setObjectLayouts(ObjectLayout[] objectLayouts) {
		this.objectLayouts = objectLayouts;
	}

	public void setObjectLayouts(
		UnsafeSupplier<ObjectLayout[], Exception> objectLayoutsUnsafeSupplier) {

		try {
			objectLayouts = objectLayoutsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectLayout[] objectLayouts;

	public ObjectRelationship[] getObjectRelationships() {
		return objectRelationships;
	}

	public void setObjectRelationships(
		ObjectRelationship[] objectRelationships) {

		this.objectRelationships = objectRelationships;
	}

	public void setObjectRelationships(
		UnsafeSupplier<ObjectRelationship[], Exception>
			objectRelationshipsUnsafeSupplier) {

		try {
			objectRelationships = objectRelationshipsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectRelationship[] objectRelationships;

	public ObjectValidationRule[] getObjectValidationRules() {
		return objectValidationRules;
	}

	public void setObjectValidationRules(
		ObjectValidationRule[] objectValidationRules) {

		this.objectValidationRules = objectValidationRules;
	}

	public void setObjectValidationRules(
		UnsafeSupplier<ObjectValidationRule[], Exception>
			objectValidationRulesUnsafeSupplier) {

		try {
			objectValidationRules = objectValidationRulesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectValidationRule[] objectValidationRules;

	public ObjectView[] getObjectViews() {
		return objectViews;
	}

	public void setObjectViews(ObjectView[] objectViews) {
		this.objectViews = objectViews;
	}

	public void setObjectViews(
		UnsafeSupplier<ObjectView[], Exception> objectViewsUnsafeSupplier) {

		try {
			objectViews = objectViewsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectView[] objectViews;

	public String getPanelAppOrder() {
		return panelAppOrder;
	}

	public void setPanelAppOrder(String panelAppOrder) {
		this.panelAppOrder = panelAppOrder;
	}

	public void setPanelAppOrder(
		UnsafeSupplier<String, Exception> panelAppOrderUnsafeSupplier) {

		try {
			panelAppOrder = panelAppOrderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String panelAppOrder;

	public String getPanelCategoryKey() {
		return panelCategoryKey;
	}

	public void setPanelCategoryKey(String panelCategoryKey) {
		this.panelCategoryKey = panelCategoryKey;
	}

	public void setPanelCategoryKey(
		UnsafeSupplier<String, Exception> panelCategoryKeyUnsafeSupplier) {

		try {
			panelCategoryKey = panelCategoryKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String panelCategoryKey;

	public Boolean getParameterRequired() {
		return parameterRequired;
	}

	public void setParameterRequired(Boolean parameterRequired) {
		this.parameterRequired = parameterRequired;
	}

	public void setParameterRequired(
		UnsafeSupplier<Boolean, Exception> parameterRequiredUnsafeSupplier) {

		try {
			parameterRequired = parameterRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean parameterRequired;

	public Map<String, String> getPluralLabel() {
		return pluralLabel;
	}

	public void setPluralLabel(Map<String, String> pluralLabel) {
		this.pluralLabel = pluralLabel;
	}

	public void setPluralLabel(
		UnsafeSupplier<Map<String, String>, Exception>
			pluralLabelUnsafeSupplier) {

		try {
			pluralLabel = pluralLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> pluralLabel;

	public Boolean getPortlet() {
		return portlet;
	}

	public void setPortlet(Boolean portlet) {
		this.portlet = portlet;
	}

	public void setPortlet(
		UnsafeSupplier<Boolean, Exception> portletUnsafeSupplier) {

		try {
			portlet = portletUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean portlet;

	public String getRestContextPath() {
		return restContextPath;
	}

	public void setRestContextPath(String restContextPath) {
		this.restContextPath = restContextPath;
	}

	public void setRestContextPath(
		UnsafeSupplier<String, Exception> restContextPathUnsafeSupplier) {

		try {
			restContextPath = restContextPathUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String restContextPath;

	public String getRootObjectDefinitionExternalReferenceCode() {
		return rootObjectDefinitionExternalReferenceCode;
	}

	public void setRootObjectDefinitionExternalReferenceCode(
		String rootObjectDefinitionExternalReferenceCode) {

		this.rootObjectDefinitionExternalReferenceCode =
			rootObjectDefinitionExternalReferenceCode;
	}

	public void setRootObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			rootObjectDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			rootObjectDefinitionExternalReferenceCode =
				rootObjectDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String rootObjectDefinitionExternalReferenceCode;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setScope(
		UnsafeSupplier<String, Exception> scopeUnsafeSupplier) {

		try {
			scope = scopeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String scope;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status status;

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public void setStorageType(
		UnsafeSupplier<String, Exception> storageTypeUnsafeSupplier) {

		try {
			storageType = storageTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String storageType;

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

	public String getTitleObjectFieldName() {
		return titleObjectFieldName;
	}

	public void setTitleObjectFieldName(String titleObjectFieldName) {
		this.titleObjectFieldName = titleObjectFieldName;
	}

	public void setTitleObjectFieldName(
		UnsafeSupplier<String, Exception> titleObjectFieldNameUnsafeSupplier) {

		try {
			titleObjectFieldName = titleObjectFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String titleObjectFieldName;

	@Override
	public ObjectDefinition clone() throws CloneNotSupportedException {
		return (ObjectDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinition)) {
			return false;
		}

		ObjectDefinition objectDefinition = (ObjectDefinition)object;

		return Objects.equals(toString(), objectDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectDefinitionSerDes.toJSON(this);
	}

}