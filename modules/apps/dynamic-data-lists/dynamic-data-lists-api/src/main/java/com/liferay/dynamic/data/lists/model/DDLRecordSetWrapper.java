/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.model;

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
 * This class is a wrapper for {@link DDLRecordSet}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordSet
 * @generated
 */
public class DDLRecordSetWrapper
	extends BaseModelWrapper<DDLRecordSet>
	implements DDLRecordSet, ModelWrapper<DDLRecordSet> {

	public DDLRecordSetWrapper(DDLRecordSet ddlRecordSet) {
		super(ddlRecordSet);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("recordSetId", getRecordSetId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("versionUserId", getVersionUserId());
		attributes.put("versionUserName", getVersionUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("DDMStructureId", getDDMStructureId());
		attributes.put("recordSetKey", getRecordSetKey());
		attributes.put("version", getVersion());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("minDisplayRows", getMinDisplayRows());
		attributes.put("scope", getScope());
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

		Long recordSetId = (Long)attributes.get("recordSetId");

		if (recordSetId != null) {
			setRecordSetId(recordSetId);
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

		Long DDMStructureId = (Long)attributes.get("DDMStructureId");

		if (DDMStructureId != null) {
			setDDMStructureId(DDMStructureId);
		}

		String recordSetKey = (String)attributes.get("recordSetKey");

		if (recordSetKey != null) {
			setRecordSetKey(recordSetKey);
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

		Integer minDisplayRows = (Integer)attributes.get("minDisplayRows");

		if (minDisplayRows != null) {
			setMinDisplayRows(minDisplayRows);
		}

		Integer scope = (Integer)attributes.get("scope");

		if (scope != null) {
			setScope(scope);
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
	public DDLRecordSet cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this ddl record set.
	 *
	 * @return the company ID of this ddl record set
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this ddl record set.
	 *
	 * @return the create date of this ddl record set
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this ddl record set.
	 *
	 * @return the ct collection ID of this ddl record set
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMStructure getDDMStructure()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDDMStructure();
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMStructure getDDMStructure(
			long formDDMTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDDMStructure(formDDMTemplateId);
	}

	/**
	 * Returns the ddm structure ID of this ddl record set.
	 *
	 * @return the ddm structure ID of this ddl record set
	 */
	@Override
	public long getDDMStructureId() {
		return model.getDDMStructureId();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this ddl record set.
	 *
	 * @return the description of this ddl record set
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this ddl record set in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this ddl record set
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this ddl record set in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this ddl record set. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this ddl record set in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this ddl record set
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this ddl record set in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this ddl record set
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
	 * Returns a map of the locales and localized descriptions of this ddl record set.
	 *
	 * @return the locales and localized descriptions of this ddl record set
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the group ID of this ddl record set.
	 *
	 * @return the group ID of this ddl record set
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this ddl record set.
	 *
	 * @return the last publish date of this ddl record set
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the min display rows of this ddl record set.
	 *
	 * @return the min display rows of this ddl record set
	 */
	@Override
	public int getMinDisplayRows() {
		return model.getMinDisplayRows();
	}

	/**
	 * Returns the modified date of this ddl record set.
	 *
	 * @return the modified date of this ddl record set
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this ddl record set.
	 *
	 * @return the mvcc version of this ddl record set
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this ddl record set.
	 *
	 * @return the name of this ddl record set
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this ddl record set in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this ddl record set
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this ddl record set in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this ddl record set. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this ddl record set in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this ddl record set
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this ddl record set in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this ddl record set
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
	 * Returns a map of the locales and localized names of this ddl record set.
	 *
	 * @return the locales and localized names of this ddl record set
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary key of this ddl record set.
	 *
	 * @return the primary key of this ddl record set
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public java.util.List<DDLRecord> getRecords() {
		return model.getRecords();
	}

	/**
	 * Returns the record set ID of this ddl record set.
	 *
	 * @return the record set ID of this ddl record set
	 */
	@Override
	public long getRecordSetId() {
		return model.getRecordSetId();
	}

	/**
	 * Returns the record set key of this ddl record set.
	 *
	 * @return the record set key of this ddl record set
	 */
	@Override
	public String getRecordSetKey() {
		return model.getRecordSetKey();
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getRecordSetVersion();
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion(String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getRecordSetVersion(version);
	}

	/**
	 * Returns the scope of this ddl record set.
	 *
	 * @return the scope of this ddl record set
	 */
	@Override
	public int getScope() {
		return model.getScope();
	}

	/**
	 * Returns the settings of this ddl record set.
	 *
	 * @return the settings of this ddl record set
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
	public DDLRecordSetSettings getSettingsModel()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getSettingsModel();
	}

	/**
	 * Returns the user ID of this ddl record set.
	 *
	 * @return the user ID of this ddl record set
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this ddl record set.
	 *
	 * @return the user name of this ddl record set
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this ddl record set.
	 *
	 * @return the user uuid of this ddl record set
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this ddl record set.
	 *
	 * @return the uuid of this ddl record set
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this ddl record set.
	 *
	 * @return the version of this ddl record set
	 */
	@Override
	public String getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns the version user ID of this ddl record set.
	 *
	 * @return the version user ID of this ddl record set
	 */
	@Override
	public long getVersionUserId() {
		return model.getVersionUserId();
	}

	/**
	 * Returns the version user name of this ddl record set.
	 *
	 * @return the version user name of this ddl record set
	 */
	@Override
	public String getVersionUserName() {
		return model.getVersionUserName();
	}

	/**
	 * Returns the version user uuid of this ddl record set.
	 *
	 * @return the version user uuid of this ddl record set
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
	 * Sets the company ID of this ddl record set.
	 *
	 * @param companyId the company ID of this ddl record set
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this ddl record set.
	 *
	 * @param createDate the create date of this ddl record set
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this ddl record set.
	 *
	 * @param ctCollectionId the ct collection ID of this ddl record set
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the ddm structure ID of this ddl record set.
	 *
	 * @param DDMStructureId the ddm structure ID of this ddl record set
	 */
	@Override
	public void setDDMStructureId(long DDMStructureId) {
		model.setDDMStructureId(DDMStructureId);
	}

	/**
	 * Sets the description of this ddl record set.
	 *
	 * @param description the description of this ddl record set
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this ddl record set in the language.
	 *
	 * @param description the localized description of this ddl record set
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this ddl record set in the language, and sets the default locale.
	 *
	 * @param description the localized description of this ddl record set
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
	 * Sets the localized descriptions of this ddl record set from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this ddl record set
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this ddl record set from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this ddl record set
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the group ID of this ddl record set.
	 *
	 * @param groupId the group ID of this ddl record set
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this ddl record set.
	 *
	 * @param lastPublishDate the last publish date of this ddl record set
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the min display rows of this ddl record set.
	 *
	 * @param minDisplayRows the min display rows of this ddl record set
	 */
	@Override
	public void setMinDisplayRows(int minDisplayRows) {
		model.setMinDisplayRows(minDisplayRows);
	}

	/**
	 * Sets the modified date of this ddl record set.
	 *
	 * @param modifiedDate the modified date of this ddl record set
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this ddl record set.
	 *
	 * @param mvccVersion the mvcc version of this ddl record set
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this ddl record set.
	 *
	 * @param name the name of this ddl record set
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this ddl record set in the language.
	 *
	 * @param name the localized name of this ddl record set
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this ddl record set in the language, and sets the default locale.
	 *
	 * @param name the localized name of this ddl record set
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
	 * Sets the localized names of this ddl record set from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this ddl record set
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this ddl record set from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this ddl record set
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the primary key of this ddl record set.
	 *
	 * @param primaryKey the primary key of this ddl record set
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the record set ID of this ddl record set.
	 *
	 * @param recordSetId the record set ID of this ddl record set
	 */
	@Override
	public void setRecordSetId(long recordSetId) {
		model.setRecordSetId(recordSetId);
	}

	/**
	 * Sets the record set key of this ddl record set.
	 *
	 * @param recordSetKey the record set key of this ddl record set
	 */
	@Override
	public void setRecordSetKey(String recordSetKey) {
		model.setRecordSetKey(recordSetKey);
	}

	/**
	 * Sets the scope of this ddl record set.
	 *
	 * @param scope the scope of this ddl record set
	 */
	@Override
	public void setScope(int scope) {
		model.setScope(scope);
	}

	/**
	 * Sets the settings of this ddl record set.
	 *
	 * @param settings the settings of this ddl record set
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
	 * Sets the user ID of this ddl record set.
	 *
	 * @param userId the user ID of this ddl record set
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this ddl record set.
	 *
	 * @param userName the user name of this ddl record set
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this ddl record set.
	 *
	 * @param userUuid the user uuid of this ddl record set
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this ddl record set.
	 *
	 * @param uuid the uuid of this ddl record set
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this ddl record set.
	 *
	 * @param version the version of this ddl record set
	 */
	@Override
	public void setVersion(String version) {
		model.setVersion(version);
	}

	/**
	 * Sets the version user ID of this ddl record set.
	 *
	 * @param versionUserId the version user ID of this ddl record set
	 */
	@Override
	public void setVersionUserId(long versionUserId) {
		model.setVersionUserId(versionUserId);
	}

	/**
	 * Sets the version user name of this ddl record set.
	 *
	 * @param versionUserName the version user name of this ddl record set
	 */
	@Override
	public void setVersionUserName(String versionUserName) {
		model.setVersionUserName(versionUserName);
	}

	/**
	 * Sets the version user uuid of this ddl record set.
	 *
	 * @param versionUserUuid the version user uuid of this ddl record set
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
	public Map<String, Function<DDLRecordSet, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<DDLRecordSet, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected DDLRecordSetWrapper wrap(DDLRecordSet ddlRecordSet) {
		return new DDLRecordSetWrapper(ddlRecordSet);
	}

}