/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
 * This class is a wrapper for {@link ObjectEntryFolder}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntryFolder
 * @generated
 */
public class ObjectEntryFolderWrapper
	extends BaseModelWrapper<ObjectEntryFolder>
	implements ModelWrapper<ObjectEntryFolder>, ObjectEntryFolder {

	public ObjectEntryFolderWrapper(ObjectEntryFolder objectEntryFolder) {
		super(objectEntryFolder);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("objectEntryFolderId", getObjectEntryFolderId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"parentObjectEntryFolderId", getParentObjectEntryFolderId());
		attributes.put("description", getDescription());
		attributes.put("label", getLabel());
		attributes.put("name", getName());
		attributes.put("treePath", getTreePath());
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

		Long objectEntryFolderId = (Long)attributes.get("objectEntryFolderId");

		if (objectEntryFolderId != null) {
			setObjectEntryFolderId(objectEntryFolderId);
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

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long parentObjectEntryFolderId = (Long)attributes.get(
			"parentObjectEntryFolderId");

		if (parentObjectEntryFolderId != null) {
			setParentObjectEntryFolderId(parentObjectEntryFolderId);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String label = (String)attributes.get("label");

		if (label != null) {
			setLabel(label);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String treePath = (String)attributes.get("treePath");

		if (treePath != null) {
			setTreePath(treePath);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public String buildTreePath()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.buildTreePath();
	}

	@Override
	public ObjectEntryFolder cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public java.util.List<Long> getAncestorObjectEntryFolderIds()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAncestorObjectEntryFolderIds();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this object entry folder.
	 *
	 * @return the company ID of this object entry folder
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the container model ID of this object entry folder.
	 *
	 * @return the container model ID of this object entry folder
	 */
	@Override
	public long getContainerModelId() {
		return model.getContainerModelId();
	}

	/**
	 * Returns the container name of this object entry folder.
	 *
	 * @return the container name of this object entry folder
	 */
	@Override
	public String getContainerModelName() {
		return model.getContainerModelName();
	}

	/**
	 * Returns the create date of this object entry folder.
	 *
	 * @return the create date of this object entry folder
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
	 * Returns the description of this object entry folder.
	 *
	 * @return the description of this object entry folder
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the external reference code of this object entry folder.
	 *
	 * @return the external reference code of this object entry folder
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this object entry folder.
	 *
	 * @return the group ID of this object entry folder
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the label of this object entry folder.
	 *
	 * @return the label of this object entry folder
	 */
	@Override
	public String getLabel() {
		return model.getLabel();
	}

	/**
	 * Returns the localized label of this object entry folder in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized label of this object entry folder
	 */
	@Override
	public String getLabel(java.util.Locale locale) {
		return model.getLabel(locale);
	}

	/**
	 * Returns the localized label of this object entry folder in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized label of this object entry folder. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getLabel(java.util.Locale locale, boolean useDefault) {
		return model.getLabel(locale, useDefault);
	}

	/**
	 * Returns the localized label of this object entry folder in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized label of this object entry folder
	 */
	@Override
	public String getLabel(String languageId) {
		return model.getLabel(languageId);
	}

	/**
	 * Returns the localized label of this object entry folder in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized label of this object entry folder
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
	 * Returns a map of the locales and localized labels of this object entry folder.
	 *
	 * @return the locales and localized labels of this object entry folder
	 */
	@Override
	public Map<java.util.Locale, String> getLabelMap() {
		return model.getLabelMap();
	}

	/**
	 * Returns the modified date of this object entry folder.
	 *
	 * @return the modified date of this object entry folder
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object entry folder.
	 *
	 * @return the mvcc version of this object entry folder
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this object entry folder.
	 *
	 * @return the name of this object entry folder
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the object entry folder ID of this object entry folder.
	 *
	 * @return the object entry folder ID of this object entry folder
	 */
	@Override
	public long getObjectEntryFolderId() {
		return model.getObjectEntryFolderId();
	}

	/**
	 * Returns the parent container model ID of this object entry folder.
	 *
	 * @return the parent container model ID of this object entry folder
	 */
	@Override
	public long getParentContainerModelId() {
		return model.getParentContainerModelId();
	}

	/**
	 * Returns the parent object entry folder ID of this object entry folder.
	 *
	 * @return the parent object entry folder ID of this object entry folder
	 */
	@Override
	public long getParentObjectEntryFolderId() {
		return model.getParentObjectEntryFolderId();
	}

	/**
	 * Returns the primary key of this object entry folder.
	 *
	 * @return the primary key of this object entry folder
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the status of this object entry folder.
	 *
	 * @return the status of this object entry folder
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the tree path of this object entry folder.
	 *
	 * @return the tree path of this object entry folder
	 */
	@Override
	public String getTreePath() {
		return model.getTreePath();
	}

	/**
	 * Returns the user ID of this object entry folder.
	 *
	 * @return the user ID of this object entry folder
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object entry folder.
	 *
	 * @return the user name of this object entry folder
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object entry folder.
	 *
	 * @return the user uuid of this object entry folder
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object entry folder.
	 *
	 * @return the uuid of this object entry folder
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public boolean isRoot() {
		return model.isRoot();
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
	 * Sets the company ID of this object entry folder.
	 *
	 * @param companyId the company ID of this object entry folder
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the container model ID of this object entry folder.
	 *
	 * @param containerModelId the container model ID of this object entry folder
	 */
	@Override
	public void setContainerModelId(long containerModelId) {
		model.setContainerModelId(containerModelId);
	}

	/**
	 * Sets the create date of this object entry folder.
	 *
	 * @param createDate the create date of this object entry folder
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this object entry folder.
	 *
	 * @param description the description of this object entry folder
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the external reference code of this object entry folder.
	 *
	 * @param externalReferenceCode the external reference code of this object entry folder
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this object entry folder.
	 *
	 * @param groupId the group ID of this object entry folder
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the label of this object entry folder.
	 *
	 * @param label the label of this object entry folder
	 */
	@Override
	public void setLabel(String label) {
		model.setLabel(label);
	}

	/**
	 * Sets the localized label of this object entry folder in the language.
	 *
	 * @param label the localized label of this object entry folder
	 * @param locale the locale of the language
	 */
	@Override
	public void setLabel(String label, java.util.Locale locale) {
		model.setLabel(label, locale);
	}

	/**
	 * Sets the localized label of this object entry folder in the language, and sets the default locale.
	 *
	 * @param label the localized label of this object entry folder
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
	 * Sets the localized labels of this object entry folder from the map of locales and localized labels.
	 *
	 * @param labelMap the locales and localized labels of this object entry folder
	 */
	@Override
	public void setLabelMap(Map<java.util.Locale, String> labelMap) {
		model.setLabelMap(labelMap);
	}

	/**
	 * Sets the localized labels of this object entry folder from the map of locales and localized labels, and sets the default locale.
	 *
	 * @param labelMap the locales and localized labels of this object entry folder
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setLabelMap(
		Map<java.util.Locale, String> labelMap,
		java.util.Locale defaultLocale) {

		model.setLabelMap(labelMap, defaultLocale);
	}

	/**
	 * Sets the modified date of this object entry folder.
	 *
	 * @param modifiedDate the modified date of this object entry folder
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object entry folder.
	 *
	 * @param mvccVersion the mvcc version of this object entry folder
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this object entry folder.
	 *
	 * @param name the name of this object entry folder
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the object entry folder ID of this object entry folder.
	 *
	 * @param objectEntryFolderId the object entry folder ID of this object entry folder
	 */
	@Override
	public void setObjectEntryFolderId(long objectEntryFolderId) {
		model.setObjectEntryFolderId(objectEntryFolderId);
	}

	/**
	 * Sets the parent container model ID of this object entry folder.
	 *
	 * @param parentContainerModelId the parent container model ID of this object entry folder
	 */
	@Override
	public void setParentContainerModelId(long parentContainerModelId) {
		model.setParentContainerModelId(parentContainerModelId);
	}

	/**
	 * Sets the parent object entry folder ID of this object entry folder.
	 *
	 * @param parentObjectEntryFolderId the parent object entry folder ID of this object entry folder
	 */
	@Override
	public void setParentObjectEntryFolderId(long parentObjectEntryFolderId) {
		model.setParentObjectEntryFolderId(parentObjectEntryFolderId);
	}

	/**
	 * Sets the primary key of this object entry folder.
	 *
	 * @param primaryKey the primary key of this object entry folder
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the status of this object entry folder.
	 *
	 * @param status the status of this object entry folder
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the tree path of this object entry folder.
	 *
	 * @param treePath the tree path of this object entry folder
	 */
	@Override
	public void setTreePath(String treePath) {
		model.setTreePath(treePath);
	}

	/**
	 * Sets the user ID of this object entry folder.
	 *
	 * @param userId the user ID of this object entry folder
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object entry folder.
	 *
	 * @param userName the user name of this object entry folder
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object entry folder.
	 *
	 * @param userUuid the user uuid of this object entry folder
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object entry folder.
	 *
	 * @param uuid the uuid of this object entry folder
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
	public void updateTreePath(String treePath) {
		model.updateTreePath(treePath);
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected ObjectEntryFolderWrapper wrap(
		ObjectEntryFolder objectEntryFolder) {

		return new ObjectEntryFolderWrapper(objectEntryFolder);
	}

}