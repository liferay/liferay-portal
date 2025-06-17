/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link DDMFormInstance}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstance
 * @generated
 */
public class DDMFormInstanceWrapper
	extends BaseModelWrapper<DDMFormInstance>
	implements DDMFormInstance, ModelWrapper<DDMFormInstance> {

	public DDMFormInstanceWrapper(DDMFormInstance ddmFormInstance) {
		super(ddmFormInstance);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("formInstanceId", getFormInstanceId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("versionUserId", getVersionUserId());
		attributes.put("versionUserName", getVersionUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("structureId", getStructureId());
		attributes.put("version", getVersion());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("settings", getSettings());
		attributes.put("lastPublishDate", getLastPublishDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long formInstanceId = (Long)attributes.get("formInstanceId");

		if (formInstanceId != null) {
			setFormInstanceId(formInstanceId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
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

		Long versionUserId = (Long)attributes.get("versionUserId");

		if (versionUserId != null) {
			setVersionUserId(versionUserId);
		}

		String versionUserName = (String)attributes.get("versionUserName");

		if (versionUserName != null) {
			setVersionUserName(versionUserName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long structureId = (Long)attributes.get("structureId");

		if (structureId != null) {
			setStructureId(structureId);
		}

		String version = (String)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String settings = (String)attributes.get("settings");

		if (settings != null) {
			setSettings(settings);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}
	}

	@Override
	public DDMFormInstance cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this ddm form instance.
	 *
	 * @return the company ID of this ddm form instance
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this ddm form instance.
	 *
	 * @return the create date of this ddm form instance
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this ddm form instance.
	 *
	 * @return the ct collection ID of this ddm form instance
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public DDMForm getDDMForm()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDDMForm();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this ddm form instance.
	 *
	 * @return the description of this ddm form instance
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this ddm form instance in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this ddm form instance
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this ddm form instance in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this ddm form instance. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this ddm form instance in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this ddm form instance
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this ddm form instance in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this ddm form instance
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
	 * Returns a map of the locales and localized descriptions of this ddm form instance.
	 *
	 * @return the locales and localized descriptions of this ddm form instance
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the form instance ID of this ddm form instance.
	 *
	 * @return the form instance ID of this ddm form instance
	 */
	@Override
	public long getFormInstanceId() {
		return model.getFormInstanceId();
	}

	@Override
	public java.util.List<DDMFormInstanceRecord> getFormInstanceRecords() {
		return model.getFormInstanceRecords();
	}

	@Override
	public DDMFormInstanceVersion getFormInstanceVersion(String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFormInstanceVersion(version);
	}

	/**
	 * Returns the group ID of this ddm form instance.
	 *
	 * @return the group ID of this ddm form instance
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this ddm form instance.
	 *
	 * @return the last publish date of this ddm form instance
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this ddm form instance.
	 *
	 * @return the modified date of this ddm form instance
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this ddm form instance.
	 *
	 * @return the mvcc version of this ddm form instance
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this ddm form instance.
	 *
	 * @return the name of this ddm form instance
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this ddm form instance in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this ddm form instance
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this ddm form instance in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this ddm form instance. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this ddm form instance in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this ddm form instance
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this ddm form instance in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this ddm form instance
	 */
	@Override
	public String getName(String languageId, boolean useDefault) {
		return model.getName(languageId, useDefault);
	}

	@Override
	public String getNameCurrentLanguageId() {
		return model.getNameCurrentLanguageId();
	}

	@Override
	public String getNameCurrentValue() {
		return model.getNameCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized names of this ddm form instance.
	 *
	 * @return the locales and localized names of this ddm form instance
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	@Override
	public long getObjectDefinitionId()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getObjectDefinitionId();
	}

	/**
	 * Returns the primary key of this ddm form instance.
	 *
	 * @return the primary key of this ddm form instance
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the settings of this ddm form instance.
	 *
	 * @return the settings of this ddm form instance
	 */
	@Override
	public String getSettings() {
		return model.getSettings();
	}

	@Override
	public com.liferay.dynamic.data.mapping.storage.DDMFormValues
		getSettingsDDMFormValues() {

		return model.getSettingsDDMFormValues();
	}

	@Override
	public DDMFormInstanceSettings getSettingsModel()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getSettingsModel();
	}

	@Override
	public String getStorageType()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getStorageType();
	}

	@Override
	public DDMStructure getStructure()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getStructure();
	}

	/**
	 * Returns the structure ID of this ddm form instance.
	 *
	 * @return the structure ID of this ddm form instance
	 */
	@Override
	public long getStructureId() {
		return model.getStructureId();
	}

	/**
	 * Returns the user ID of this ddm form instance.
	 *
	 * @return the user ID of this ddm form instance
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this ddm form instance.
	 *
	 * @return the user name of this ddm form instance
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this ddm form instance.
	 *
	 * @return the user uuid of this ddm form instance
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this ddm form instance.
	 *
	 * @return the uuid of this ddm form instance
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this ddm form instance.
	 *
	 * @return the version of this ddm form instance
	 */
	@Override
	public String getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns the version user ID of this ddm form instance.
	 *
	 * @return the version user ID of this ddm form instance
	 */
	@Override
	public long getVersionUserId() {
		return model.getVersionUserId();
	}

	/**
	 * Returns the version user name of this ddm form instance.
	 *
	 * @return the version user name of this ddm form instance
	 */
	@Override
	public String getVersionUserName() {
		return model.getVersionUserName();
	}

	/**
	 * Returns the version user uuid of this ddm form instance.
	 *
	 * @return the version user uuid of this ddm form instance
	 */
	@Override
	public String getVersionUserUuid() {
		return model.getVersionUserUuid();
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
	 * Sets the company ID of this ddm form instance.
	 *
	 * @param companyId the company ID of this ddm form instance
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this ddm form instance.
	 *
	 * @param createDate the create date of this ddm form instance
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this ddm form instance.
	 *
	 * @param ctCollectionId the ct collection ID of this ddm form instance
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this ddm form instance.
	 *
	 * @param description the description of this ddm form instance
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this ddm form instance in the language.
	 *
	 * @param description the localized description of this ddm form instance
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this ddm form instance in the language, and sets the default locale.
	 *
	 * @param description the localized description of this ddm form instance
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
	 * Sets the localized descriptions of this ddm form instance from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this ddm form instance
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this ddm form instance from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this ddm form instance
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the form instance ID of this ddm form instance.
	 *
	 * @param formInstanceId the form instance ID of this ddm form instance
	 */
	@Override
	public void setFormInstanceId(long formInstanceId) {
		model.setFormInstanceId(formInstanceId);
	}

	/**
	 * Sets the group ID of this ddm form instance.
	 *
	 * @param groupId the group ID of this ddm form instance
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this ddm form instance.
	 *
	 * @param lastPublishDate the last publish date of this ddm form instance
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this ddm form instance.
	 *
	 * @param modifiedDate the modified date of this ddm form instance
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this ddm form instance.
	 *
	 * @param mvccVersion the mvcc version of this ddm form instance
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this ddm form instance.
	 *
	 * @param name the name of this ddm form instance
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this ddm form instance in the language.
	 *
	 * @param name the localized name of this ddm form instance
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this ddm form instance in the language, and sets the default locale.
	 *
	 * @param name the localized name of this ddm form instance
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setName(
		String name, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setName(name, locale, defaultLocale);
	}

	@Override
	public void setNameCurrentLanguageId(String languageId) {
		model.setNameCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized names of this ddm form instance from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this ddm form instance
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this ddm form instance from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this ddm form instance
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the primary key of this ddm form instance.
	 *
	 * @param primaryKey the primary key of this ddm form instance
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the settings of this ddm form instance.
	 *
	 * @param settings the settings of this ddm form instance
	 */
	@Override
	public void setSettings(String settings) {
		model.setSettings(settings);
	}

	@Override
	public void setSettingsDDMFormValues(
		com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues) {

		model.setSettingsDDMFormValues(ddmFormValues);
	}

	/**
	 * Sets the structure ID of this ddm form instance.
	 *
	 * @param structureId the structure ID of this ddm form instance
	 */
	@Override
	public void setStructureId(long structureId) {
		model.setStructureId(structureId);
	}

	/**
	 * Sets the user ID of this ddm form instance.
	 *
	 * @param userId the user ID of this ddm form instance
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this ddm form instance.
	 *
	 * @param userName the user name of this ddm form instance
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this ddm form instance.
	 *
	 * @param userUuid the user uuid of this ddm form instance
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this ddm form instance.
	 *
	 * @param uuid the uuid of this ddm form instance
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this ddm form instance.
	 *
	 * @param version the version of this ddm form instance
	 */
	@Override
	public void setVersion(String version) {
		model.setVersion(version);
	}

	/**
	 * Sets the version user ID of this ddm form instance.
	 *
	 * @param versionUserId the version user ID of this ddm form instance
	 */
	@Override
	public void setVersionUserId(long versionUserId) {
		model.setVersionUserId(versionUserId);
	}

	/**
	 * Sets the version user name of this ddm form instance.
	 *
	 * @param versionUserName the version user name of this ddm form instance
	 */
	@Override
	public void setVersionUserName(String versionUserName) {
		model.setVersionUserName(versionUserName);
	}

	/**
	 * Sets the version user uuid of this ddm form instance.
	 *
	 * @param versionUserUuid the version user uuid of this ddm form instance
	 */
	@Override
	public void setVersionUserUuid(String versionUserUuid) {
		model.setVersionUserUuid(versionUserUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<DDMFormInstance, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<DDMFormInstance, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected DDMFormInstanceWrapper wrap(DDMFormInstance ddmFormInstance) {
		return new DDMFormInstanceWrapper(ddmFormInstance);
	}

}