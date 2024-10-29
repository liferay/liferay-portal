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
 * This class is a wrapper for {@link DDMStructure}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructure
 * @generated
 */
public class DDMStructureWrapper
	extends BaseModelWrapper<DDMStructure>
	implements DDMStructure, ModelWrapper<DDMStructure> {

	public DDMStructureWrapper(DDMStructure ddmStructure) {
		super(ddmStructure);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("structureId", getStructureId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("versionUserId", getVersionUserId());
		attributes.put("versionUserName", getVersionUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("parentStructureId", getParentStructureId());
		attributes.put("classNameId", getClassNameId());
		attributes.put("structureKey", getStructureKey());
		attributes.put("version", getVersion());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("definition", getDefinition());
		attributes.put("storageType", getStorageType());
		attributes.put("type", getType());
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

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long structureId = (Long)attributes.get("structureId");

		if (structureId != null) {
			setStructureId(structureId);
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

		Long parentStructureId = (Long)attributes.get("parentStructureId");

		if (parentStructureId != null) {
			setParentStructureId(parentStructureId);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		String structureKey = (String)attributes.get("structureKey");

		if (structureKey != null) {
			setStructureKey(structureKey);
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

		String definition = (String)attributes.get("definition");

		if (definition != null) {
			setDefinition(definition);
		}

		String storageType = (String)attributes.get("storageType");

		if (storageType != null) {
			setStorageType(storageType);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}
	}

	@Override
	public DDMStructure cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public DDMForm createFullHierarchyDDMForm()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.createFullHierarchyDDMForm();
	}

	@Override
	public DDMStructureLayout fetchDDMStructureLayout() {
		return model.fetchDDMStructureLayout();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	@Override
	public java.util.List<String> getChildrenFieldNames(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getChildrenFieldNames(fieldName);
	}

	/**
	 * Returns the fully qualified class name of this ddm structure.
	 *
	 * @return the fully qualified class name of this ddm structure
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this ddm structure.
	 *
	 * @return the class name ID of this ddm structure
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the company ID of this ddm structure.
	 *
	 * @return the company ID of this ddm structure
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this ddm structure.
	 *
	 * @return the create date of this ddm structure
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this ddm structure.
	 *
	 * @return the ct collection ID of this ddm structure
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public DDMForm getDDMForm() {
		return model.getDDMForm();
	}

	@Override
	public DDMFormField getDDMFormField(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDDMFormField(fieldName);
	}

	@Override
	public DDMFormField getDDMFormFieldByFieldReference(String fieldReference)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDDMFormFieldByFieldReference(fieldReference);
	}

	@Override
	public java.util.List<DDMFormField> getDDMFormFields(
		boolean includeTransientFields) {

		return model.getDDMFormFields(includeTransientFields);
	}

	@Override
	public DDMFormLayout getDDMFormLayout()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDDMFormLayout();
	}

	@Override
	public long getDefaultDDMStructureLayoutId() {
		return model.getDefaultDDMStructureLayoutId();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the definition of this ddm structure.
	 *
	 * @return the definition of this ddm structure
	 */
	@Override
	public String getDefinition() {
		return model.getDefinition();
	}

	/**
	 * Returns the description of this ddm structure.
	 *
	 * @return the description of this ddm structure
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this ddm structure in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this ddm structure
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this ddm structure in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this ddm structure. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this ddm structure in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this ddm structure
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this ddm structure in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this ddm structure
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
	 * Returns a map of the locales and localized descriptions of this ddm structure.
	 *
	 * @return the locales and localized descriptions of this ddm structure
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the external reference code of this ddm structure.
	 *
	 * @return the external reference code of this ddm structure
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	@Override
	public String getFieldDataType(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldDataType(fieldName);
	}

	@Override
	public String getFieldLabel(String fieldName, java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldLabel(fieldName, locale);
	}

	@Override
	public String getFieldLabel(String fieldName, String locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldLabel(fieldName, locale);
	}

	@Override
	public java.util.Set<String> getFieldNames() {
		return model.getFieldNames();
	}

	@Override
	public String getFieldProperty(String fieldName, String property)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldProperty(fieldName, property);
	}

	@Override
	public String getFieldPropertyByFieldReference(
			String fieldReference, String property)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldPropertyByFieldReference(fieldReference, property);
	}

	@Override
	public boolean getFieldRepeatable(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldRepeatable(fieldName);
	}

	@Override
	public boolean getFieldRequired(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldRequired(fieldName);
	}

	@Override
	public String getFieldTip(String fieldName, java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldTip(fieldName, locale);
	}

	@Override
	public String getFieldTip(String fieldName, String locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldTip(fieldName, locale);
	}

	@Override
	public String getFieldType(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getFieldType(fieldName);
	}

	@Override
	public DDMForm getFullHierarchyDDMForm() {
		return model.getFullHierarchyDDMForm();
	}

	@Override
	public Map<String, DDMFormField> getFullHierarchyDDMFormFieldsMap(
		boolean includeNestedDDMFormFields) {

		return model.getFullHierarchyDDMFormFieldsMap(
			includeNestedDDMFormFields);
	}

	/**
	 * Returns the group ID of this ddm structure.
	 *
	 * @return the group ID of this ddm structure
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this ddm structure.
	 *
	 * @return the last publish date of this ddm structure
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	@Override
	public DDMStructureVersion getLatestStructureVersion()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLatestStructureVersion();
	}

	/**
	 * Returns the modified date of this ddm structure.
	 *
	 * @return the modified date of this ddm structure
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this ddm structure.
	 *
	 * @return the mvcc version of this ddm structure
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this ddm structure.
	 *
	 * @return the name of this ddm structure
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this ddm structure in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this ddm structure
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this ddm structure in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this ddm structure. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this ddm structure in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this ddm structure
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this ddm structure in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this ddm structure
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
	 * Returns a map of the locales and localized names of this ddm structure.
	 *
	 * @return the locales and localized names of this ddm structure
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the parent structure ID of this ddm structure.
	 *
	 * @return the parent structure ID of this ddm structure
	 */
	@Override
	public long getParentStructureId() {
		return model.getParentStructureId();
	}

	/**
	 * Returns the primary key of this ddm structure.
	 *
	 * @return the primary key of this ddm structure
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public java.util.List<String> getRootFieldNames() {
		return model.getRootFieldNames();
	}

	/**
	 * Returns the storage type of this ddm structure.
	 *
	 * @return the storage type of this ddm structure
	 */
	@Override
	public String getStorageType() {
		return model.getStorageType();
	}

	/**
	 * Returns the structure ID of this ddm structure.
	 *
	 * @return the structure ID of this ddm structure
	 */
	@Override
	public long getStructureId() {
		return model.getStructureId();
	}

	/**
	 * Returns the structure key of this ddm structure.
	 *
	 * @return the structure key of this ddm structure
	 */
	@Override
	public String getStructureKey() {
		return model.getStructureKey();
	}

	@Override
	public DDMStructureVersion getStructureVersion()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getStructureVersion();
	}

	@Override
	public java.util.List<DDMTemplate> getTemplates() {
		return model.getTemplates();
	}

	/**
	 * Returns the type of this ddm structure.
	 *
	 * @return the type of this ddm structure
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	@Override
	public String getUnambiguousName(
			java.util.List<DDMStructure> structures, long groupId,
			java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getUnambiguousName(structures, groupId, locale);
	}

	/**
	 * Returns the user ID of this ddm structure.
	 *
	 * @return the user ID of this ddm structure
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this ddm structure.
	 *
	 * @return the user name of this ddm structure
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this ddm structure.
	 *
	 * @return the user uuid of this ddm structure
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this ddm structure.
	 *
	 * @return the uuid of this ddm structure
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this ddm structure.
	 *
	 * @return the version of this ddm structure
	 */
	@Override
	public String getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns the version user ID of this ddm structure.
	 *
	 * @return the version user ID of this ddm structure
	 */
	@Override
	public long getVersionUserId() {
		return model.getVersionUserId();
	}

	/**
	 * Returns the version user name of this ddm structure.
	 *
	 * @return the version user name of this ddm structure
	 */
	@Override
	public String getVersionUserName() {
		return model.getVersionUserName();
	}

	/**
	 * Returns the version user uuid of this ddm structure.
	 *
	 * @return the version user uuid of this ddm structure
	 */
	@Override
	public String getVersionUserUuid() {
		return model.getVersionUserUuid();
	}

	/**
	 * Returns the WebDAV URL to access the structure.
	 *
	 * @param themeDisplay the theme display needed to build the URL. It can
	 set HTTPS access, the server name, the server port, the path
	 context, and the scope group.
	 * @param webDAVToken the WebDAV token for the URL
	 * @return the WebDAV URL
	 */
	@Override
	public String getWebDavURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		String webDAVToken) {

		return model.getWebDavURL(themeDisplay, webDAVToken);
	}

	@Override
	public boolean hasField(String fieldName) {
		return model.hasField(fieldName);
	}

	@Override
	public boolean hasFieldByFieldReference(String fieldReference) {
		return model.hasFieldByFieldReference(fieldReference);
	}

	@Override
	public boolean isFieldRepeatable(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isFieldRepeatable(fieldName);
	}

	@Override
	public boolean isFieldTransient(String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isFieldTransient(fieldName);
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

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this ddm structure.
	 *
	 * @param classNameId the class name ID of this ddm structure
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the company ID of this ddm structure.
	 *
	 * @param companyId the company ID of this ddm structure
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this ddm structure.
	 *
	 * @param createDate the create date of this ddm structure
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this ddm structure.
	 *
	 * @param ctCollectionId the ct collection ID of this ddm structure
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	@Override
	public void setDDMForm(DDMForm ddmForm) {
		model.setDDMForm(ddmForm);
	}

	/**
	 * Sets the definition of this ddm structure.
	 *
	 * @param definition the definition of this ddm structure
	 */
	@Override
	public void setDefinition(String definition) {
		model.setDefinition(definition);
	}

	/**
	 * Sets the description of this ddm structure.
	 *
	 * @param description the description of this ddm structure
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this ddm structure in the language.
	 *
	 * @param description the localized description of this ddm structure
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this ddm structure in the language, and sets the default locale.
	 *
	 * @param description the localized description of this ddm structure
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
	 * Sets the localized descriptions of this ddm structure from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this ddm structure
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this ddm structure from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this ddm structure
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this ddm structure.
	 *
	 * @param externalReferenceCode the external reference code of this ddm structure
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this ddm structure.
	 *
	 * @param groupId the group ID of this ddm structure
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this ddm structure.
	 *
	 * @param lastPublishDate the last publish date of this ddm structure
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this ddm structure.
	 *
	 * @param modifiedDate the modified date of this ddm structure
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this ddm structure.
	 *
	 * @param mvccVersion the mvcc version of this ddm structure
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this ddm structure.
	 *
	 * @param name the name of this ddm structure
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this ddm structure in the language.
	 *
	 * @param name the localized name of this ddm structure
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this ddm structure in the language, and sets the default locale.
	 *
	 * @param name the localized name of this ddm structure
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
	 * Sets the localized names of this ddm structure from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this ddm structure
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this ddm structure from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this ddm structure
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the parent structure ID of this ddm structure.
	 *
	 * @param parentStructureId the parent structure ID of this ddm structure
	 */
	@Override
	public void setParentStructureId(long parentStructureId) {
		model.setParentStructureId(parentStructureId);
	}

	/**
	 * Sets the primary key of this ddm structure.
	 *
	 * @param primaryKey the primary key of this ddm structure
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the storage type of this ddm structure.
	 *
	 * @param storageType the storage type of this ddm structure
	 */
	@Override
	public void setStorageType(String storageType) {
		model.setStorageType(storageType);
	}

	/**
	 * Sets the structure ID of this ddm structure.
	 *
	 * @param structureId the structure ID of this ddm structure
	 */
	@Override
	public void setStructureId(long structureId) {
		model.setStructureId(structureId);
	}

	/**
	 * Sets the structure key of this ddm structure.
	 *
	 * @param structureKey the structure key of this ddm structure
	 */
	@Override
	public void setStructureKey(String structureKey) {
		model.setStructureKey(structureKey);
	}

	/**
	 * Sets the type of this ddm structure.
	 *
	 * @param type the type of this ddm structure
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this ddm structure.
	 *
	 * @param userId the user ID of this ddm structure
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this ddm structure.
	 *
	 * @param userName the user name of this ddm structure
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this ddm structure.
	 *
	 * @param userUuid the user uuid of this ddm structure
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this ddm structure.
	 *
	 * @param uuid the uuid of this ddm structure
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this ddm structure.
	 *
	 * @param version the version of this ddm structure
	 */
	@Override
	public void setVersion(String version) {
		model.setVersion(version);
	}

	/**
	 * Sets the version user ID of this ddm structure.
	 *
	 * @param versionUserId the version user ID of this ddm structure
	 */
	@Override
	public void setVersionUserId(long versionUserId) {
		model.setVersionUserId(versionUserId);
	}

	/**
	 * Sets the version user name of this ddm structure.
	 *
	 * @param versionUserName the version user name of this ddm structure
	 */
	@Override
	public void setVersionUserName(String versionUserName) {
		model.setVersionUserName(versionUserName);
	}

	/**
	 * Sets the version user uuid of this ddm structure.
	 *
	 * @param versionUserUuid the version user uuid of this ddm structure
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
	public Map<String, Function<DDMStructure, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<DDMStructure, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected DDMStructureWrapper wrap(DDMStructure ddmStructure) {
		return new DDMStructureWrapper(ddmStructure);
	}

}