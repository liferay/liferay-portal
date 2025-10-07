/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ObjectDefinition}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectDefinition
 * @generated
 */
public class ObjectDefinitionWrapper
	extends BaseModelWrapper<ObjectDefinition>
	implements ModelWrapper<ObjectDefinition>, ObjectDefinition {

	public ObjectDefinitionWrapper(ObjectDefinition objectDefinition) {
		super(objectDefinition);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"accountEntryRestrictedObjectFieldId",
			getAccountEntryRestrictedObjectFieldId());
		attributes.put(
			"descriptionObjectFieldId", getDescriptionObjectFieldId());
		attributes.put("objectFolderId", getObjectFolderId());
		attributes.put("titleObjectFieldId", getTitleObjectFieldId());
		attributes.put("accountEntryRestricted", isAccountEntryRestricted());
		attributes.put("active", isActive());
		attributes.put("className", getClassName());
		attributes.put("dbTableName", getDBTableName());
		attributes.put("enableCategorization", isEnableCategorization());
		attributes.put("enableComments", isEnableComments());
		attributes.put("enableFormContainer", isEnableFormContainer());
		attributes.put(
			"enableFriendlyURLCustomization",
			isEnableFriendlyURLCustomization());
		attributes.put("enableIndexSearch", isEnableIndexSearch());
		attributes.put("enableLocalization", isEnableLocalization());
		attributes.put("enableObjectEntryDraft", isEnableObjectEntryDraft());
		attributes.put(
			"enableObjectEntryHistory", isEnableObjectEntryHistory());
		attributes.put(
			"enableObjectEntrySchedule", isEnableObjectEntrySchedule());
		attributes.put(
			"enableObjectEntrySubscription", isEnableObjectEntrySubscription());
		attributes.put(
			"enableObjectEntryVersioning", isEnableObjectEntryVersioning());
		attributes.put("friendlyURLSeparator", getFriendlyURLSeparator());
		attributes.put("label", getLabel());
		attributes.put("modifiable", isModifiable());
		attributes.put("name", getName());
		attributes.put("panelAppOrder", getPanelAppOrder());
		attributes.put("panelCategoryKey", getPanelCategoryKey());
		attributes.put(
			"pkObjectFieldDBColumnName", getPKObjectFieldDBColumnName());
		attributes.put("pkObjectFieldName", getPKObjectFieldName());
		attributes.put("pluralLabel", getPluralLabel());
		attributes.put("portlet", isPortlet());
		attributes.put("scope", getScope());
		attributes.put("storageType", getStorageType());
		attributes.put("system", isSystem());
		attributes.put("version", getVersion());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long objectDefinitionId = (Long)attributes.get("objectDefinitionId");

		if (objectDefinitionId != null) {
			setObjectDefinitionId(objectDefinitionId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long accountEntryRestrictedObjectFieldId = (Long)attributes.get(
			"accountEntryRestrictedObjectFieldId");

		if (accountEntryRestrictedObjectFieldId != null) {
			setAccountEntryRestrictedObjectFieldId(
				accountEntryRestrictedObjectFieldId);
		}

		Long descriptionObjectFieldId = (Long)attributes.get(
			"descriptionObjectFieldId");

		if (descriptionObjectFieldId != null) {
			setDescriptionObjectFieldId(descriptionObjectFieldId);
		}

		Long objectFolderId = (Long)attributes.get("objectFolderId");

		if (objectFolderId != null) {
			setObjectFolderId(objectFolderId);
		}

		Long titleObjectFieldId = (Long)attributes.get("titleObjectFieldId");

		if (titleObjectFieldId != null) {
			setTitleObjectFieldId(titleObjectFieldId);
		}

		Boolean accountEntryRestricted = (Boolean)attributes.get(
			"accountEntryRestricted");

		if (accountEntryRestricted != null) {
			setAccountEntryRestricted(accountEntryRestricted);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}

		String className = (String)attributes.get("className");

		if (className != null) {
			setClassName(className);
		}

		String dbTableName = (String)attributes.get("dbTableName");

		if (dbTableName != null) {
			setDBTableName(dbTableName);
		}

		Boolean enableCategorization = (Boolean)attributes.get(
			"enableCategorization");

		if (enableCategorization != null) {
			setEnableCategorization(enableCategorization);
		}

		Boolean enableComments = (Boolean)attributes.get("enableComments");

		if (enableComments != null) {
			setEnableComments(enableComments);
		}

		Boolean enableFormContainer = (Boolean)attributes.get(
			"enableFormContainer");

		if (enableFormContainer != null) {
			setEnableFormContainer(enableFormContainer);
		}

		Boolean enableFriendlyURLCustomization = (Boolean)attributes.get(
			"enableFriendlyURLCustomization");

		if (enableFriendlyURLCustomization != null) {
			setEnableFriendlyURLCustomization(enableFriendlyURLCustomization);
		}

		Boolean enableIndexSearch = (Boolean)attributes.get(
			"enableIndexSearch");

		if (enableIndexSearch != null) {
			setEnableIndexSearch(enableIndexSearch);
		}

		Boolean enableLocalization = (Boolean)attributes.get(
			"enableLocalization");

		if (enableLocalization != null) {
			setEnableLocalization(enableLocalization);
		}

		Boolean enableObjectEntryDraft = (Boolean)attributes.get(
			"enableObjectEntryDraft");

		if (enableObjectEntryDraft != null) {
			setEnableObjectEntryDraft(enableObjectEntryDraft);
		}

		Boolean enableObjectEntryHistory = (Boolean)attributes.get(
			"enableObjectEntryHistory");

		if (enableObjectEntryHistory != null) {
			setEnableObjectEntryHistory(enableObjectEntryHistory);
		}

		Boolean enableObjectEntrySchedule = (Boolean)attributes.get(
			"enableObjectEntrySchedule");

		if (enableObjectEntrySchedule != null) {
			setEnableObjectEntrySchedule(enableObjectEntrySchedule);
		}

		Boolean enableObjectEntrySubscription = (Boolean)attributes.get(
			"enableObjectEntrySubscription");

		if (enableObjectEntrySubscription != null) {
			setEnableObjectEntrySubscription(enableObjectEntrySubscription);
		}

		Boolean enableObjectEntryVersioning = (Boolean)attributes.get(
			"enableObjectEntryVersioning");

		if (enableObjectEntryVersioning != null) {
			setEnableObjectEntryVersioning(enableObjectEntryVersioning);
		}

		String friendlyURLSeparator = (String)attributes.get(
			"friendlyURLSeparator");

		if (friendlyURLSeparator != null) {
			setFriendlyURLSeparator(friendlyURLSeparator);
		}

		String label = (String)attributes.get("label");

		if (label != null) {
			setLabel(label);
		}

		Boolean modifiable = (Boolean)attributes.get("modifiable");

		if (modifiable != null) {
			setModifiable(modifiable);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String panelAppOrder = (String)attributes.get("panelAppOrder");

		if (panelAppOrder != null) {
			setPanelAppOrder(panelAppOrder);
		}

		String panelCategoryKey = (String)attributes.get("panelCategoryKey");

		if (panelCategoryKey != null) {
			setPanelCategoryKey(panelCategoryKey);
		}

		String pkObjectFieldDBColumnName = (String)attributes.get(
			"pkObjectFieldDBColumnName");

		if (pkObjectFieldDBColumnName != null) {
			setPKObjectFieldDBColumnName(pkObjectFieldDBColumnName);
		}

		String pkObjectFieldName = (String)attributes.get("pkObjectFieldName");

		if (pkObjectFieldName != null) {
			setPKObjectFieldName(pkObjectFieldName);
		}

		String pluralLabel = (String)attributes.get("pluralLabel");

		if (pluralLabel != null) {
			setPluralLabel(pluralLabel);
		}

		Boolean portlet = (Boolean)attributes.get("portlet");

		if (portlet != null) {
			setPortlet(portlet);
		}

		String scope = (String)attributes.get("scope");

		if (scope != null) {
			setScope(scope);
		}

		String storageType = (String)attributes.get("storageType");

		if (storageType != null) {
			setStorageType(storageType);
		}

		Boolean system = (Boolean)attributes.get("system");

		if (system != null) {
			setSystem(system);
		}

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public ObjectDefinition cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account entry restricted of this object definition.
	 *
	 * @return the account entry restricted of this object definition
	 */
	@Override
	public boolean getAccountEntryRestricted() {
		return model.getAccountEntryRestricted();
	}

	/**
	 * Returns the account entry restricted object field ID of this object definition.
	 *
	 * @return the account entry restricted object field ID of this object definition
	 */
	@Override
	public long getAccountEntryRestrictedObjectFieldId() {
		return model.getAccountEntryRestrictedObjectFieldId();
	}

	/**
	 * Returns the active of this object definition.
	 *
	 * @return the active of this object definition
	 */
	@Override
	public boolean getActive() {
		return model.getActive();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the class name of this object definition.
	 *
	 * @return the class name of this object definition
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the company ID of this object definition.
	 *
	 * @return the company ID of this object definition
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this object definition.
	 *
	 * @return the create date of this object definition
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the db table name of this object definition.
	 *
	 * @return the db table name of this object definition
	 */
	@Override
	public String getDBTableName() {
		return model.getDBTableName();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	@Override
	public java.util.Locale getDefaultLocale() {
		return model.getDefaultLocale();
	}

	/**
	 * Returns the description object field ID of this object definition.
	 *
	 * @return the description object field ID of this object definition
	 */
	@Override
	public long getDescriptionObjectFieldId() {
		return model.getDescriptionObjectFieldId();
	}

	@Override
	public String getDestinationName() {
		return model.getDestinationName();
	}

	/**
	 * Returns the enable categorization of this object definition.
	 *
	 * @return the enable categorization of this object definition
	 */
	@Override
	public boolean getEnableCategorization() {
		return model.getEnableCategorization();
	}

	/**
	 * Returns the enable comments of this object definition.
	 *
	 * @return the enable comments of this object definition
	 */
	@Override
	public boolean getEnableComments() {
		return model.getEnableComments();
	}

	/**
	 * Returns the enable form container of this object definition.
	 *
	 * @return the enable form container of this object definition
	 */
	@Override
	public boolean getEnableFormContainer() {
		return model.getEnableFormContainer();
	}

	/**
	 * Returns the enable friendly url customization of this object definition.
	 *
	 * @return the enable friendly url customization of this object definition
	 */
	@Override
	public boolean getEnableFriendlyURLCustomization() {
		return model.getEnableFriendlyURLCustomization();
	}

	/**
	 * Returns the enable index search of this object definition.
	 *
	 * @return the enable index search of this object definition
	 */
	@Override
	public boolean getEnableIndexSearch() {
		return model.getEnableIndexSearch();
	}

	/**
	 * Returns the enable localization of this object definition.
	 *
	 * @return the enable localization of this object definition
	 */
	@Override
	public boolean getEnableLocalization() {
		return model.getEnableLocalization();
	}

	/**
	 * Returns the enable object entry draft of this object definition.
	 *
	 * @return the enable object entry draft of this object definition
	 */
	@Override
	public boolean getEnableObjectEntryDraft() {
		return model.getEnableObjectEntryDraft();
	}

	/**
	 * Returns the enable object entry history of this object definition.
	 *
	 * @return the enable object entry history of this object definition
	 */
	@Override
	public boolean getEnableObjectEntryHistory() {
		return model.getEnableObjectEntryHistory();
	}

	/**
	 * Returns the enable object entry schedule of this object definition.
	 *
	 * @return the enable object entry schedule of this object definition
	 */
	@Override
	public boolean getEnableObjectEntrySchedule() {
		return model.getEnableObjectEntrySchedule();
	}

	/**
	 * Returns the enable object entry subscription of this object definition.
	 *
	 * @return the enable object entry subscription of this object definition
	 */
	@Override
	public boolean getEnableObjectEntrySubscription() {
		return model.getEnableObjectEntrySubscription();
	}

	/**
	 * Returns the enable object entry versioning of this object definition.
	 *
	 * @return the enable object entry versioning of this object definition
	 */
	@Override
	public boolean getEnableObjectEntryVersioning() {
		return model.getEnableObjectEntryVersioning();
	}

	@Override
	public String getExtensionDBTableName() {
		return model.getExtensionDBTableName();
	}

	/**
	 * Returns the external reference code of this object definition.
	 *
	 * @return the external reference code of this object definition
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the friendly url separator of this object definition.
	 *
	 * @return the friendly url separator of this object definition
	 */
	@Override
	public String getFriendlyURLSeparator() {
		return model.getFriendlyURLSeparator();
	}

	/**
	 * Returns the label of this object definition.
	 *
	 * @return the label of this object definition
	 */
	@Override
	public String getLabel() {
		return model.getLabel();
	}

	/**
	 * Returns the localized label of this object definition in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized label of this object definition
	 */
	@Override
	public String getLabel(java.util.Locale locale) {
		return model.getLabel(locale);
	}

	/**
	 * Returns the localized label of this object definition in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized label of this object definition. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getLabel(java.util.Locale locale, boolean useDefault) {
		return model.getLabel(locale, useDefault);
	}

	/**
	 * Returns the localized label of this object definition in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized label of this object definition
	 */
	@Override
	public String getLabel(String languageId) {
		return model.getLabel(languageId);
	}

	/**
	 * Returns the localized label of this object definition in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized label of this object definition
	 */
	@Override
	public String getLabel(String languageId, boolean useDefault) {
		return model.getLabel(languageId, useDefault);
	}

	@Override
	public String getLabelCurrentLanguageId() {
		return model.getLabelCurrentLanguageId();
	}

	@Override
	public String getLabelCurrentValue() {
		return model.getLabelCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized labels of this object definition.
	 *
	 * @return the locales and localized labels of this object definition
	 */
	@Override
	public Map<java.util.Locale, String> getLabelMap() {
		return model.getLabelMap();
	}

	@Override
	public String getLocalizationDBTableName() {
		return model.getLocalizationDBTableName();
	}

	/**
	 * Returns the modifiable of this object definition.
	 *
	 * @return the modifiable of this object definition
	 */
	@Override
	public boolean getModifiable() {
		return model.getModifiable();
	}

	/**
	 * Returns the modified date of this object definition.
	 *
	 * @return the modified date of this object definition
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object definition.
	 *
	 * @return the mvcc version of this object definition
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this object definition.
	 *
	 * @return the name of this object definition
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the object definition ID of this object definition.
	 *
	 * @return the object definition ID of this object definition
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	@Override
	public java.util.List<ObjectDefinitionSetting>
		getObjectDefinitionSettings() {

		return model.getObjectDefinitionSettings();
	}

	@Override
	public String getObjectFolderExternalReferenceCode() {
		return model.getObjectFolderExternalReferenceCode();
	}

	/**
	 * Returns the object folder ID of this object definition.
	 *
	 * @return the object folder ID of this object definition
	 */
	@Override
	public long getObjectFolderId() {
		return model.getObjectFolderId();
	}

	@Override
	public String getOSGiJaxRsName() {
		return model.getOSGiJaxRsName();
	}

	@Override
	public String getOSGiJaxRsName(String className) {
		return model.getOSGiJaxRsName(className);
	}

	/**
	 * Returns the panel app order of this object definition.
	 *
	 * @return the panel app order of this object definition
	 */
	@Override
	public String getPanelAppOrder() {
		return model.getPanelAppOrder();
	}

	/**
	 * Returns the panel category key of this object definition.
	 *
	 * @return the panel category key of this object definition
	 */
	@Override
	public String getPanelCategoryKey() {
		return model.getPanelCategoryKey();
	}

	/**
	 * Returns the pk object field db column name of this object definition.
	 *
	 * @return the pk object field db column name of this object definition
	 */
	@Override
	public String getPKObjectFieldDBColumnName() {
		return model.getPKObjectFieldDBColumnName();
	}

	/**
	 * Returns the pk object field name of this object definition.
	 *
	 * @return the pk object field name of this object definition
	 */
	@Override
	public String getPKObjectFieldName() {
		return model.getPKObjectFieldName();
	}

	/**
	 * Returns the plural label of this object definition.
	 *
	 * @return the plural label of this object definition
	 */
	@Override
	public String getPluralLabel() {
		return model.getPluralLabel();
	}

	/**
	 * Returns the localized plural label of this object definition in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized plural label of this object definition
	 */
	@Override
	public String getPluralLabel(java.util.Locale locale) {
		return model.getPluralLabel(locale);
	}

	/**
	 * Returns the localized plural label of this object definition in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized plural label of this object definition. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getPluralLabel(java.util.Locale locale, boolean useDefault) {
		return model.getPluralLabel(locale, useDefault);
	}

	/**
	 * Returns the localized plural label of this object definition in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized plural label of this object definition
	 */
	@Override
	public String getPluralLabel(String languageId) {
		return model.getPluralLabel(languageId);
	}

	/**
	 * Returns the localized plural label of this object definition in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized plural label of this object definition
	 */
	@Override
	public String getPluralLabel(String languageId, boolean useDefault) {
		return model.getPluralLabel(languageId, useDefault);
	}

	@Override
	public String getPluralLabelCurrentLanguageId() {
		return model.getPluralLabelCurrentLanguageId();
	}

	@Override
	public String getPluralLabelCurrentValue() {
		return model.getPluralLabelCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized plural labels of this object definition.
	 *
	 * @return the locales and localized plural labels of this object definition
	 */
	@Override
	public Map<java.util.Locale, String> getPluralLabelMap() {
		return model.getPluralLabelMap();
	}

	/**
	 * Returns the portlet of this object definition.
	 *
	 * @return the portlet of this object definition
	 */
	@Override
	public boolean getPortlet() {
		return model.getPortlet();
	}

	@Override
	public String getPortletId() {
		return model.getPortletId();
	}

	/**
	 * Returns the primary key of this object definition.
	 *
	 * @return the primary key of this object definition
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public String getResourceName() {
		return model.getResourceName();
	}

	@Override
	public String getRESTContextPath() {
		return model.getRESTContextPath();
	}

	@Override
	public String getRootObjectDefinitionExternalReferenceCode() {
		return model.getRootObjectDefinitionExternalReferenceCode();
	}

	@Override
	public long getRootObjectDefinitionId() {
		return model.getRootObjectDefinitionId();
	}

	@Override
	public long[] getRootObjectDefinitionIds() {
		return model.getRootObjectDefinitionIds();
	}

	/**
	 * Returns the scope of this object definition.
	 *
	 * @return the scope of this object definition
	 */
	@Override
	public String getScope() {
		return model.getScope();
	}

	@Override
	public String getShortName() {
		return model.getShortName();
	}

	/**
	 * Returns the status of this object definition.
	 *
	 * @return the status of this object definition
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the storage type of this object definition.
	 *
	 * @return the storage type of this object definition
	 */
	@Override
	public String getStorageType() {
		return model.getStorageType();
	}

	/**
	 * Returns the system of this object definition.
	 *
	 * @return the system of this object definition
	 */
	@Override
	public boolean getSystem() {
		return model.getSystem();
	}

	/**
	 * Returns the title object field ID of this object definition.
	 *
	 * @return the title object field ID of this object definition
	 */
	@Override
	public long getTitleObjectFieldId() {
		return model.getTitleObjectFieldId();
	}

	/**
	 * Returns the user ID of this object definition.
	 *
	 * @return the user ID of this object definition
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object definition.
	 *
	 * @return the user name of this object definition
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object definition.
	 *
	 * @return the user uuid of this object definition
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object definition.
	 *
	 * @return the uuid of this object definition
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this object definition.
	 *
	 * @return the version of this object definition
	 */
	@Override
	public int getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns <code>true</code> if this object definition is account entry restricted.
	 *
	 * @return <code>true</code> if this object definition is account entry restricted; <code>false</code> otherwise
	 */
	@Override
	public boolean isAccountEntryRestricted() {
		return model.isAccountEntryRestricted();
	}

	/**
	 * Returns <code>true</code> if this object definition is active.
	 *
	 * @return <code>true</code> if this object definition is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	@Override
	public boolean isDefaultStorageType() {
		return model.isDefaultStorageType();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable categorization.
	 *
	 * @return <code>true</code> if this object definition is enable categorization; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableCategorization() {
		return model.isEnableCategorization();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable comments.
	 *
	 * @return <code>true</code> if this object definition is enable comments; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableComments() {
		return model.isEnableComments();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable form container.
	 *
	 * @return <code>true</code> if this object definition is enable form container; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableFormContainer() {
		return model.isEnableFormContainer();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable friendly url customization.
	 *
	 * @return <code>true</code> if this object definition is enable friendly url customization; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableFriendlyURLCustomization() {
		return model.isEnableFriendlyURLCustomization();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable index search.
	 *
	 * @return <code>true</code> if this object definition is enable index search; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableIndexSearch() {
		return model.isEnableIndexSearch();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable localization.
	 *
	 * @return <code>true</code> if this object definition is enable localization; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableLocalization() {
		return model.isEnableLocalization();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable object entry draft.
	 *
	 * @return <code>true</code> if this object definition is enable object entry draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableObjectEntryDraft() {
		return model.isEnableObjectEntryDraft();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable object entry history.
	 *
	 * @return <code>true</code> if this object definition is enable object entry history; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableObjectEntryHistory() {
		return model.isEnableObjectEntryHistory();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable object entry schedule.
	 *
	 * @return <code>true</code> if this object definition is enable object entry schedule; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableObjectEntrySchedule() {
		return model.isEnableObjectEntrySchedule();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable object entry subscription.
	 *
	 * @return <code>true</code> if this object definition is enable object entry subscription; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableObjectEntrySubscription() {
		return model.isEnableObjectEntrySubscription();
	}

	/**
	 * Returns <code>true</code> if this object definition is enable object entry versioning.
	 *
	 * @return <code>true</code> if this object definition is enable object entry versioning; <code>false</code> otherwise
	 */
	@Override
	public boolean isEnableObjectEntryVersioning() {
		return model.isEnableObjectEntryVersioning();
	}

	@Override
	public boolean isLinkedToObjectFolder(long objectFolderId) {
		return model.isLinkedToObjectFolder(objectFolderId);
	}

	/**
	 * Returns <code>true</code> if this object definition is modifiable.
	 *
	 * @return <code>true</code> if this object definition is modifiable; <code>false</code> otherwise
	 */
	@Override
	public boolean isModifiable() {
		return model.isModifiable();
	}

	@Override
	public boolean isModifiableAndSystem() {
		return model.isModifiableAndSystem();
	}

	/**
	 * Returns <code>true</code> if this object definition is portlet.
	 *
	 * @return <code>true</code> if this object definition is portlet; <code>false</code> otherwise
	 */
	@Override
	public boolean isPortlet() {
		return model.isPortlet();
	}

	@Override
	public boolean isRootDescendantNode() {
		return model.isRootDescendantNode();
	}

	@Override
	public boolean isRootDescendantNode(long rootObjectDefinitionId) {
		return model.isRootDescendantNode(rootObjectDefinitionId);
	}

	@Override
	public boolean isRootNode() {
		return model.isRootNode();
	}

	/**
	 * Returns <code>true</code> if this object definition is system.
	 *
	 * @return <code>true</code> if this object definition is system; <code>false</code> otherwise
	 */
	@Override
	public boolean isSystem() {
		return model.isSystem();
	}

	@Override
	public boolean isUnmodifiableSystemObject() {
		return model.isUnmodifiableSystemObject();
	}

	@Override
	public void persist() {
		model.persist();
	}

	@Override
	public void prepareLocalizedFieldsForImport()
		throws com.liferay.portal.kernel.exception.LocaleException {

		model.prepareLocalizedFieldsForImport();
	}

	@Override
	public void prepareLocalizedFieldsForImport(
			java.util.Locale defaultImportLocale)
		throws com.liferay.portal.kernel.exception.LocaleException {

		model.prepareLocalizedFieldsForImport(defaultImportLocale);
	}

	/**
	 * Sets whether this object definition is account entry restricted.
	 *
	 * @param accountEntryRestricted the account entry restricted of this object definition
	 */
	@Override
	public void setAccountEntryRestricted(boolean accountEntryRestricted) {
		model.setAccountEntryRestricted(accountEntryRestricted);
	}

	/**
	 * Sets the account entry restricted object field ID of this object definition.
	 *
	 * @param accountEntryRestrictedObjectFieldId the account entry restricted object field ID of this object definition
	 */
	@Override
	public void setAccountEntryRestrictedObjectFieldId(
		long accountEntryRestrictedObjectFieldId) {

		model.setAccountEntryRestrictedObjectFieldId(
			accountEntryRestrictedObjectFieldId);
	}

	/**
	 * Sets whether this object definition is active.
	 *
	 * @param active the active of this object definition
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the class name of this object definition.
	 *
	 * @param className the class name of this object definition
	 */
	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the company ID of this object definition.
	 *
	 * @param companyId the company ID of this object definition
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this object definition.
	 *
	 * @param createDate the create date of this object definition
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the db table name of this object definition.
	 *
	 * @param dbTableName the db table name of this object definition
	 */
	@Override
	public void setDBTableName(String dbTableName) {
		model.setDBTableName(dbTableName);
	}

	/**
	 * Sets the description object field ID of this object definition.
	 *
	 * @param descriptionObjectFieldId the description object field ID of this object definition
	 */
	@Override
	public void setDescriptionObjectFieldId(long descriptionObjectFieldId) {
		model.setDescriptionObjectFieldId(descriptionObjectFieldId);
	}

	/**
	 * Sets whether this object definition is enable categorization.
	 *
	 * @param enableCategorization the enable categorization of this object definition
	 */
	@Override
	public void setEnableCategorization(boolean enableCategorization) {
		model.setEnableCategorization(enableCategorization);
	}

	/**
	 * Sets whether this object definition is enable comments.
	 *
	 * @param enableComments the enable comments of this object definition
	 */
	@Override
	public void setEnableComments(boolean enableComments) {
		model.setEnableComments(enableComments);
	}

	/**
	 * Sets whether this object definition is enable form container.
	 *
	 * @param enableFormContainer the enable form container of this object definition
	 */
	@Override
	public void setEnableFormContainer(boolean enableFormContainer) {
		model.setEnableFormContainer(enableFormContainer);
	}

	/**
	 * Sets whether this object definition is enable friendly url customization.
	 *
	 * @param enableFriendlyURLCustomization the enable friendly url customization of this object definition
	 */
	@Override
	public void setEnableFriendlyURLCustomization(
		boolean enableFriendlyURLCustomization) {

		model.setEnableFriendlyURLCustomization(enableFriendlyURLCustomization);
	}

	/**
	 * Sets whether this object definition is enable index search.
	 *
	 * @param enableIndexSearch the enable index search of this object definition
	 */
	@Override
	public void setEnableIndexSearch(boolean enableIndexSearch) {
		model.setEnableIndexSearch(enableIndexSearch);
	}

	/**
	 * Sets whether this object definition is enable localization.
	 *
	 * @param enableLocalization the enable localization of this object definition
	 */
	@Override
	public void setEnableLocalization(boolean enableLocalization) {
		model.setEnableLocalization(enableLocalization);
	}

	/**
	 * Sets whether this object definition is enable object entry draft.
	 *
	 * @param enableObjectEntryDraft the enable object entry draft of this object definition
	 */
	@Override
	public void setEnableObjectEntryDraft(boolean enableObjectEntryDraft) {
		model.setEnableObjectEntryDraft(enableObjectEntryDraft);
	}

	/**
	 * Sets whether this object definition is enable object entry history.
	 *
	 * @param enableObjectEntryHistory the enable object entry history of this object definition
	 */
	@Override
	public void setEnableObjectEntryHistory(boolean enableObjectEntryHistory) {
		model.setEnableObjectEntryHistory(enableObjectEntryHistory);
	}

	/**
	 * Sets whether this object definition is enable object entry schedule.
	 *
	 * @param enableObjectEntrySchedule the enable object entry schedule of this object definition
	 */
	@Override
	public void setEnableObjectEntrySchedule(
		boolean enableObjectEntrySchedule) {

		model.setEnableObjectEntrySchedule(enableObjectEntrySchedule);
	}

	/**
	 * Sets whether this object definition is enable object entry subscription.
	 *
	 * @param enableObjectEntrySubscription the enable object entry subscription of this object definition
	 */
	@Override
	public void setEnableObjectEntrySubscription(
		boolean enableObjectEntrySubscription) {

		model.setEnableObjectEntrySubscription(enableObjectEntrySubscription);
	}

	/**
	 * Sets whether this object definition is enable object entry versioning.
	 *
	 * @param enableObjectEntryVersioning the enable object entry versioning of this object definition
	 */
	@Override
	public void setEnableObjectEntryVersioning(
		boolean enableObjectEntryVersioning) {

		model.setEnableObjectEntryVersioning(enableObjectEntryVersioning);
	}

	/**
	 * Sets the external reference code of this object definition.
	 *
	 * @param externalReferenceCode the external reference code of this object definition
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the friendly url separator of this object definition.
	 *
	 * @param friendlyURLSeparator the friendly url separator of this object definition
	 */
	@Override
	public void setFriendlyURLSeparator(String friendlyURLSeparator) {
		model.setFriendlyURLSeparator(friendlyURLSeparator);
	}

	/**
	 * Sets the label of this object definition.
	 *
	 * @param label the label of this object definition
	 */
	@Override
	public void setLabel(String label) {
		model.setLabel(label);
	}

	/**
	 * Sets the localized label of this object definition in the language.
	 *
	 * @param label the localized label of this object definition
	 * @param locale the locale of the language
	 */
	@Override
	public void setLabel(String label, java.util.Locale locale) {
		model.setLabel(label, locale);
	}

	/**
	 * Sets the localized label of this object definition in the language, and sets the default locale.
	 *
	 * @param label the localized label of this object definition
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setLabel(
		String label, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setLabel(label, locale, defaultLocale);
	}

	@Override
	public void setLabelCurrentLanguageId(String languageId) {
		model.setLabelCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized labels of this object definition from the map of locales and localized labels.
	 *
	 * @param labelMap the locales and localized labels of this object definition
	 */
	@Override
	public void setLabelMap(Map<java.util.Locale, String> labelMap) {
		model.setLabelMap(labelMap);
	}

	/**
	 * Sets the localized labels of this object definition from the map of locales and localized labels, and sets the default locale.
	 *
	 * @param labelMap the locales and localized labels of this object definition
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setLabelMap(
		Map<java.util.Locale, String> labelMap,
		java.util.Locale defaultLocale) {

		model.setLabelMap(labelMap, defaultLocale);
	}

	/**
	 * Sets whether this object definition is modifiable.
	 *
	 * @param modifiable the modifiable of this object definition
	 */
	@Override
	public void setModifiable(boolean modifiable) {
		model.setModifiable(modifiable);
	}

	/**
	 * Sets the modified date of this object definition.
	 *
	 * @param modifiedDate the modified date of this object definition
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object definition.
	 *
	 * @param mvccVersion the mvcc version of this object definition
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this object definition.
	 *
	 * @param name the name of this object definition
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the object definition ID of this object definition.
	 *
	 * @param objectDefinitionId the object definition ID of this object definition
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	@Override
	public void setObjectDefinitionSettings(
		java.util.List<ObjectDefinitionSetting> objectDefinitionSettings) {

		model.setObjectDefinitionSettings(objectDefinitionSettings);
	}

	/**
	 * Sets the object folder ID of this object definition.
	 *
	 * @param objectFolderId the object folder ID of this object definition
	 */
	@Override
	public void setObjectFolderId(long objectFolderId) {
		model.setObjectFolderId(objectFolderId);
	}

	/**
	 * Sets the panel app order of this object definition.
	 *
	 * @param panelAppOrder the panel app order of this object definition
	 */
	@Override
	public void setPanelAppOrder(String panelAppOrder) {
		model.setPanelAppOrder(panelAppOrder);
	}

	/**
	 * Sets the panel category key of this object definition.
	 *
	 * @param panelCategoryKey the panel category key of this object definition
	 */
	@Override
	public void setPanelCategoryKey(String panelCategoryKey) {
		model.setPanelCategoryKey(panelCategoryKey);
	}

	/**
	 * Sets the pk object field db column name of this object definition.
	 *
	 * @param pkObjectFieldDBColumnName the pk object field db column name of this object definition
	 */
	@Override
	public void setPKObjectFieldDBColumnName(String pkObjectFieldDBColumnName) {
		model.setPKObjectFieldDBColumnName(pkObjectFieldDBColumnName);
	}

	/**
	 * Sets the pk object field name of this object definition.
	 *
	 * @param pkObjectFieldName the pk object field name of this object definition
	 */
	@Override
	public void setPKObjectFieldName(String pkObjectFieldName) {
		model.setPKObjectFieldName(pkObjectFieldName);
	}

	/**
	 * Sets the plural label of this object definition.
	 *
	 * @param pluralLabel the plural label of this object definition
	 */
	@Override
	public void setPluralLabel(String pluralLabel) {
		model.setPluralLabel(pluralLabel);
	}

	/**
	 * Sets the localized plural label of this object definition in the language.
	 *
	 * @param pluralLabel the localized plural label of this object definition
	 * @param locale the locale of the language
	 */
	@Override
	public void setPluralLabel(String pluralLabel, java.util.Locale locale) {
		model.setPluralLabel(pluralLabel, locale);
	}

	/**
	 * Sets the localized plural label of this object definition in the language, and sets the default locale.
	 *
	 * @param pluralLabel the localized plural label of this object definition
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setPluralLabel(
		String pluralLabel, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setPluralLabel(pluralLabel, locale, defaultLocale);
	}

	@Override
	public void setPluralLabelCurrentLanguageId(String languageId) {
		model.setPluralLabelCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized plural labels of this object definition from the map of locales and localized plural labels.
	 *
	 * @param pluralLabelMap the locales and localized plural labels of this object definition
	 */
	@Override
	public void setPluralLabelMap(
		Map<java.util.Locale, String> pluralLabelMap) {

		model.setPluralLabelMap(pluralLabelMap);
	}

	/**
	 * Sets the localized plural labels of this object definition from the map of locales and localized plural labels, and sets the default locale.
	 *
	 * @param pluralLabelMap the locales and localized plural labels of this object definition
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setPluralLabelMap(
		Map<java.util.Locale, String> pluralLabelMap,
		java.util.Locale defaultLocale) {

		model.setPluralLabelMap(pluralLabelMap, defaultLocale);
	}

	/**
	 * Sets whether this object definition is portlet.
	 *
	 * @param portlet the portlet of this object definition
	 */
	@Override
	public void setPortlet(boolean portlet) {
		model.setPortlet(portlet);
	}

	/**
	 * Sets the primary key of this object definition.
	 *
	 * @param primaryKey the primary key of this object definition
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the scope of this object definition.
	 *
	 * @param scope the scope of this object definition
	 */
	@Override
	public void setScope(String scope) {
		model.setScope(scope);
	}

	/**
	 * Sets the status of this object definition.
	 *
	 * @param status the status of this object definition
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the storage type of this object definition.
	 *
	 * @param storageType the storage type of this object definition
	 */
	@Override
	public void setStorageType(String storageType) {
		model.setStorageType(storageType);
	}

	/**
	 * Sets whether this object definition is system.
	 *
	 * @param system the system of this object definition
	 */
	@Override
	public void setSystem(boolean system) {
		model.setSystem(system);
	}

	/**
	 * Sets the title object field ID of this object definition.
	 *
	 * @param titleObjectFieldId the title object field ID of this object definition
	 */
	@Override
	public void setTitleObjectFieldId(long titleObjectFieldId) {
		model.setTitleObjectFieldId(titleObjectFieldId);
	}

	/**
	 * Sets the user ID of this object definition.
	 *
	 * @param userId the user ID of this object definition
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object definition.
	 *
	 * @param userName the user name of this object definition
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object definition.
	 *
	 * @param userUuid the user uuid of this object definition
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object definition.
	 *
	 * @param uuid the uuid of this object definition
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this object definition.
	 *
	 * @param version the version of this object definition
	 */
	@Override
	public void setVersion(int version) {
		model.setVersion(version);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected ObjectDefinitionWrapper wrap(ObjectDefinition objectDefinition) {
		return new ObjectDefinitionWrapper(objectDefinition);
	}

}