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
 * This class is a wrapper for {@link ObjectAction}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectAction
 * @generated
 */
public class ObjectActionWrapper
	extends BaseModelWrapper<ObjectAction>
	implements ModelWrapper<ObjectAction>, ObjectAction {

	public ObjectActionWrapper(ObjectAction objectAction) {
		super(objectAction);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("objectActionId", getObjectActionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("active", isActive());
		attributes.put("conditionExpression", getConditionExpression());
		attributes.put("description", getDescription());
		attributes.put("errorMessage", getErrorMessage());
		attributes.put("label", getLabel());
		attributes.put("name", getName());
		attributes.put("objectActionExecutorKey", getObjectActionExecutorKey());
		attributes.put("objectActionTriggerKey", getObjectActionTriggerKey());
		attributes.put("parameters", getParameters());
		attributes.put("system", isSystem());
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

		Long objectActionId = (Long)attributes.get("objectActionId");

		if (objectActionId != null) {
			setObjectActionId(objectActionId);
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

		Long objectDefinitionId = (Long)attributes.get("objectDefinitionId");

		if (objectDefinitionId != null) {
			setObjectDefinitionId(objectDefinitionId);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}

		String conditionExpression = (String)attributes.get(
			"conditionExpression");

		if (conditionExpression != null) {
			setConditionExpression(conditionExpression);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String errorMessage = (String)attributes.get("errorMessage");

		if (errorMessage != null) {
			setErrorMessage(errorMessage);
		}

		String label = (String)attributes.get("label");

		if (label != null) {
			setLabel(label);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String objectActionExecutorKey = (String)attributes.get(
			"objectActionExecutorKey");

		if (objectActionExecutorKey != null) {
			setObjectActionExecutorKey(objectActionExecutorKey);
		}

		String objectActionTriggerKey = (String)attributes.get(
			"objectActionTriggerKey");

		if (objectActionTriggerKey != null) {
			setObjectActionTriggerKey(objectActionTriggerKey);
		}

		String parameters = (String)attributes.get("parameters");

		if (parameters != null) {
			setParameters(parameters);
		}

		Boolean system = (Boolean)attributes.get("system");

		if (system != null) {
			setSystem(system);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public ObjectAction cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the active of this object action.
	 *
	 * @return the active of this object action
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
	 * Returns the company ID of this object action.
	 *
	 * @return the company ID of this object action
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the condition expression of this object action.
	 *
	 * @return the condition expression of this object action
	 */
	@Override
	public String getConditionExpression() {
		return model.getConditionExpression();
	}

	/**
	 * Returns the create date of this object action.
	 *
	 * @return the create date of this object action
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this object action.
	 *
	 * @return the description of this object action
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this object action in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this object action
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this object action in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this object action. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this object action in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this object action
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this object action in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this object action
	 */
	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return model.getDescription(languageId, useDefault);
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return model.getDescriptionCurrentLanguageId();
	}

	@Override
	public String getDescriptionCurrentValue() {
		return model.getDescriptionCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized descriptions of this object action.
	 *
	 * @return the locales and localized descriptions of this object action
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the error message of this object action.
	 *
	 * @return the error message of this object action
	 */
	@Override
	public String getErrorMessage() {
		return model.getErrorMessage();
	}

	/**
	 * Returns the localized error message of this object action in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized error message of this object action
	 */
	@Override
	public String getErrorMessage(java.util.Locale locale) {
		return model.getErrorMessage(locale);
	}

	/**
	 * Returns the localized error message of this object action in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized error message of this object action. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getErrorMessage(java.util.Locale locale, boolean useDefault) {
		return model.getErrorMessage(locale, useDefault);
	}

	/**
	 * Returns the localized error message of this object action in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized error message of this object action
	 */
	@Override
	public String getErrorMessage(String languageId) {
		return model.getErrorMessage(languageId);
	}

	/**
	 * Returns the localized error message of this object action in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized error message of this object action
	 */
	@Override
	public String getErrorMessage(String languageId, boolean useDefault) {
		return model.getErrorMessage(languageId, useDefault);
	}

	@Override
	public String getErrorMessageCurrentLanguageId() {
		return model.getErrorMessageCurrentLanguageId();
	}

	@Override
	public String getErrorMessageCurrentValue() {
		return model.getErrorMessageCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized error messages of this object action.
	 *
	 * @return the locales and localized error messages of this object action
	 */
	@Override
	public Map<java.util.Locale, String> getErrorMessageMap() {
		return model.getErrorMessageMap();
	}

	/**
	 * Returns the external reference code of this object action.
	 *
	 * @return the external reference code of this object action
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the label of this object action.
	 *
	 * @return the label of this object action
	 */
	@Override
	public String getLabel() {
		return model.getLabel();
	}

	/**
	 * Returns the localized label of this object action in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized label of this object action
	 */
	@Override
	public String getLabel(java.util.Locale locale) {
		return model.getLabel(locale);
	}

	/**
	 * Returns the localized label of this object action in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized label of this object action. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getLabel(java.util.Locale locale, boolean useDefault) {
		return model.getLabel(locale, useDefault);
	}

	/**
	 * Returns the localized label of this object action in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized label of this object action
	 */
	@Override
	public String getLabel(String languageId) {
		return model.getLabel(languageId);
	}

	/**
	 * Returns the localized label of this object action in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized label of this object action
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
	 * Returns a map of the locales and localized labels of this object action.
	 *
	 * @return the locales and localized labels of this object action
	 */
	@Override
	public Map<java.util.Locale, String> getLabelMap() {
		return model.getLabelMap();
	}

	/**
	 * Returns the modified date of this object action.
	 *
	 * @return the modified date of this object action
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object action.
	 *
	 * @return the mvcc version of this object action
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this object action.
	 *
	 * @return the name of this object action
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the object action executor key of this object action.
	 *
	 * @return the object action executor key of this object action
	 */
	@Override
	public String getObjectActionExecutorKey() {
		return model.getObjectActionExecutorKey();
	}

	/**
	 * Returns the object action ID of this object action.
	 *
	 * @return the object action ID of this object action
	 */
	@Override
	public long getObjectActionId() {
		return model.getObjectActionId();
	}

	/**
	 * Returns the object action trigger key of this object action.
	 *
	 * @return the object action trigger key of this object action
	 */
	@Override
	public String getObjectActionTriggerKey() {
		return model.getObjectActionTriggerKey();
	}

	/**
	 * Returns the object definition ID of this object action.
	 *
	 * @return the object definition ID of this object action
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	/**
	 * Returns the parameters of this object action.
	 *
	 * @return the parameters of this object action
	 */
	@Override
	public String getParameters() {
		return model.getParameters();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getParametersUnicodeProperties() {

		return model.getParametersUnicodeProperties();
	}

	/**
	 * Returns the primary key of this object action.
	 *
	 * @return the primary key of this object action
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the status of this object action.
	 *
	 * @return the status of this object action
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the system of this object action.
	 *
	 * @return the system of this object action
	 */
	@Override
	public boolean getSystem() {
		return model.getSystem();
	}

	/**
	 * Returns the user ID of this object action.
	 *
	 * @return the user ID of this object action
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object action.
	 *
	 * @return the user name of this object action
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object action.
	 *
	 * @return the user uuid of this object action
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object action.
	 *
	 * @return the uuid of this object action
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this object action is active.
	 *
	 * @return <code>true</code> if this object action is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	/**
	 * Returns <code>true</code> if this object action is system.
	 *
	 * @return <code>true</code> if this object action is system; <code>false</code> otherwise
	 */
	@Override
	public boolean isSystem() {
		return model.isSystem();
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
	 * Sets whether this object action is active.
	 *
	 * @param active the active of this object action
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the company ID of this object action.
	 *
	 * @param companyId the company ID of this object action
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the condition expression of this object action.
	 *
	 * @param conditionExpression the condition expression of this object action
	 */
	@Override
	public void setConditionExpression(String conditionExpression) {
		model.setConditionExpression(conditionExpression);
	}

	/**
	 * Sets the create date of this object action.
	 *
	 * @param createDate the create date of this object action
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this object action.
	 *
	 * @param description the description of this object action
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this object action in the language.
	 *
	 * @param description the localized description of this object action
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this object action in the language, and sets the default locale.
	 *
	 * @param description the localized description of this object action
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescription(
		String description, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setDescription(description, locale, defaultLocale);
	}

	@Override
	public void setDescriptionCurrentLanguageId(String languageId) {
		model.setDescriptionCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized descriptions of this object action from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this object action
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this object action from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this object action
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the error message of this object action.
	 *
	 * @param errorMessage the error message of this object action
	 */
	@Override
	public void setErrorMessage(String errorMessage) {
		model.setErrorMessage(errorMessage);
	}

	/**
	 * Sets the localized error message of this object action in the language.
	 *
	 * @param errorMessage the localized error message of this object action
	 * @param locale the locale of the language
	 */
	@Override
	public void setErrorMessage(String errorMessage, java.util.Locale locale) {
		model.setErrorMessage(errorMessage, locale);
	}

	/**
	 * Sets the localized error message of this object action in the language, and sets the default locale.
	 *
	 * @param errorMessage the localized error message of this object action
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setErrorMessage(
		String errorMessage, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setErrorMessage(errorMessage, locale, defaultLocale);
	}

	@Override
	public void setErrorMessageCurrentLanguageId(String languageId) {
		model.setErrorMessageCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized error messages of this object action from the map of locales and localized error messages.
	 *
	 * @param errorMessageMap the locales and localized error messages of this object action
	 */
	@Override
	public void setErrorMessageMap(
		Map<java.util.Locale, String> errorMessageMap) {

		model.setErrorMessageMap(errorMessageMap);
	}

	/**
	 * Sets the localized error messages of this object action from the map of locales and localized error messages, and sets the default locale.
	 *
	 * @param errorMessageMap the locales and localized error messages of this object action
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setErrorMessageMap(
		Map<java.util.Locale, String> errorMessageMap,
		java.util.Locale defaultLocale) {

		model.setErrorMessageMap(errorMessageMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this object action.
	 *
	 * @param externalReferenceCode the external reference code of this object action
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the label of this object action.
	 *
	 * @param label the label of this object action
	 */
	@Override
	public void setLabel(String label) {
		model.setLabel(label);
	}

	/**
	 * Sets the localized label of this object action in the language.
	 *
	 * @param label the localized label of this object action
	 * @param locale the locale of the language
	 */
	@Override
	public void setLabel(String label, java.util.Locale locale) {
		model.setLabel(label, locale);
	}

	/**
	 * Sets the localized label of this object action in the language, and sets the default locale.
	 *
	 * @param label the localized label of this object action
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
	 * Sets the localized labels of this object action from the map of locales and localized labels.
	 *
	 * @param labelMap the locales and localized labels of this object action
	 */
	@Override
	public void setLabelMap(Map<java.util.Locale, String> labelMap) {
		model.setLabelMap(labelMap);
	}

	/**
	 * Sets the localized labels of this object action from the map of locales and localized labels, and sets the default locale.
	 *
	 * @param labelMap the locales and localized labels of this object action
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setLabelMap(
		Map<java.util.Locale, String> labelMap,
		java.util.Locale defaultLocale) {

		model.setLabelMap(labelMap, defaultLocale);
	}

	/**
	 * Sets the modified date of this object action.
	 *
	 * @param modifiedDate the modified date of this object action
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object action.
	 *
	 * @param mvccVersion the mvcc version of this object action
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this object action.
	 *
	 * @param name the name of this object action
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the object action executor key of this object action.
	 *
	 * @param objectActionExecutorKey the object action executor key of this object action
	 */
	@Override
	public void setObjectActionExecutorKey(String objectActionExecutorKey) {
		model.setObjectActionExecutorKey(objectActionExecutorKey);
	}

	/**
	 * Sets the object action ID of this object action.
	 *
	 * @param objectActionId the object action ID of this object action
	 */
	@Override
	public void setObjectActionId(long objectActionId) {
		model.setObjectActionId(objectActionId);
	}

	/**
	 * Sets the object action trigger key of this object action.
	 *
	 * @param objectActionTriggerKey the object action trigger key of this object action
	 */
	@Override
	public void setObjectActionTriggerKey(String objectActionTriggerKey) {
		model.setObjectActionTriggerKey(objectActionTriggerKey);
	}

	/**
	 * Sets the object definition ID of this object action.
	 *
	 * @param objectDefinitionId the object definition ID of this object action
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Sets the parameters of this object action.
	 *
	 * @param parameters the parameters of this object action
	 */
	@Override
	public void setParameters(String parameters) {
		model.setParameters(parameters);
	}

	/**
	 * Sets the primary key of this object action.
	 *
	 * @param primaryKey the primary key of this object action
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the status of this object action.
	 *
	 * @param status the status of this object action
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets whether this object action is system.
	 *
	 * @param system the system of this object action
	 */
	@Override
	public void setSystem(boolean system) {
		model.setSystem(system);
	}

	/**
	 * Sets the user ID of this object action.
	 *
	 * @param userId the user ID of this object action
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object action.
	 *
	 * @param userName the user name of this object action
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object action.
	 *
	 * @param userUuid the user uuid of this object action
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object action.
	 *
	 * @param uuid the uuid of this object action
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
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
	protected ObjectActionWrapper wrap(ObjectAction objectAction) {
		return new ObjectActionWrapper(objectAction);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2069636381