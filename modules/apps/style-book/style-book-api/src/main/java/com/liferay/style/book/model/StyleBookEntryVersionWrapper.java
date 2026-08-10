/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link StyleBookEntryVersion}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryVersion
 * @generated
 */
public class StyleBookEntryVersionWrapper
	extends BaseModelWrapper<StyleBookEntryVersion>
	implements ModelWrapper<StyleBookEntryVersion>, StyleBookEntryVersion {

	public StyleBookEntryVersionWrapper(
		StyleBookEntryVersion styleBookEntryVersion) {

		super(styleBookEntryVersion);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("styleBookEntryVersionId", getStyleBookEntryVersionId());
		attributes.put("version", getVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("styleBookEntryId", getStyleBookEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("defaultStyleBookEntry", isDefaultStyleBookEntry());
		attributes.put("frontendTokenDefinition", getFrontendTokenDefinition());
		attributes.put("frontendTokensValues", getFrontendTokensValues());
		attributes.put("name", getName());
		attributes.put("previewFileEntryId", getPreviewFileEntryId());
		attributes.put("styleBookEntryKey", getStyleBookEntryKey());
		attributes.put("themeId", getThemeId());

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

		Long styleBookEntryVersionId = (Long)attributes.get(
			"styleBookEntryVersionId");

		if (styleBookEntryVersionId != null) {
			setStyleBookEntryVersionId(styleBookEntryVersionId);
		}

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
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

		Long styleBookEntryId = (Long)attributes.get("styleBookEntryId");

		if (styleBookEntryId != null) {
			setStyleBookEntryId(styleBookEntryId);
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

		Boolean defaultStyleBookEntry = (Boolean)attributes.get(
			"defaultStyleBookEntry");

		if (defaultStyleBookEntry != null) {
			setDefaultStyleBookEntry(defaultStyleBookEntry);
		}

		String frontendTokenDefinition = (String)attributes.get(
			"frontendTokenDefinition");

		if (frontendTokenDefinition != null) {
			setFrontendTokenDefinition(frontendTokenDefinition);
		}

		String frontendTokensValues = (String)attributes.get(
			"frontendTokensValues");

		if (frontendTokensValues != null) {
			setFrontendTokensValues(frontendTokensValues);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Long previewFileEntryId = (Long)attributes.get("previewFileEntryId");

		if (previewFileEntryId != null) {
			setPreviewFileEntryId(previewFileEntryId);
		}

		String styleBookEntryKey = (String)attributes.get("styleBookEntryKey");

		if (styleBookEntryKey != null) {
			setStyleBookEntryKey(styleBookEntryKey);
		}

		String themeId = (String)attributes.get("themeId");

		if (themeId != null) {
			setThemeId(themeId);
		}
	}

	@Override
	public StyleBookEntryVersion cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this style book entry version.
	 *
	 * @return the company ID of this style book entry version
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this style book entry version.
	 *
	 * @return the create date of this style book entry version
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this style book entry version.
	 *
	 * @return the ct collection ID of this style book entry version
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the default style book entry of this style book entry version.
	 *
	 * @return the default style book entry of this style book entry version
	 */
	@Override
	public boolean getDefaultStyleBookEntry() {
		return model.getDefaultStyleBookEntry();
	}

	/**
	 * Returns the external reference code of this style book entry version.
	 *
	 * @return the external reference code of this style book entry version
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the frontend token definition of this style book entry version.
	 *
	 * @return the frontend token definition of this style book entry version
	 */
	@Override
	public String getFrontendTokenDefinition() {
		return model.getFrontendTokenDefinition();
	}

	/**
	 * Returns the frontend tokens values of this style book entry version.
	 *
	 * @return the frontend tokens values of this style book entry version
	 */
	@Override
	public String getFrontendTokensValues() {
		return model.getFrontendTokensValues();
	}

	/**
	 * Returns the group ID of this style book entry version.
	 *
	 * @return the group ID of this style book entry version
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the modified date of this style book entry version.
	 *
	 * @return the modified date of this style book entry version
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this style book entry version.
	 *
	 * @return the mvcc version of this style book entry version
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this style book entry version.
	 *
	 * @return the name of this style book entry version
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the preview file entry ID of this style book entry version.
	 *
	 * @return the preview file entry ID of this style book entry version
	 */
	@Override
	public long getPreviewFileEntryId() {
		return model.getPreviewFileEntryId();
	}

	/**
	 * Returns the primary key of this style book entry version.
	 *
	 * @return the primary key of this style book entry version
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the style book entry ID of this style book entry version.
	 *
	 * @return the style book entry ID of this style book entry version
	 */
	@Override
	public long getStyleBookEntryId() {
		return model.getStyleBookEntryId();
	}

	/**
	 * Returns the style book entry key of this style book entry version.
	 *
	 * @return the style book entry key of this style book entry version
	 */
	@Override
	public String getStyleBookEntryKey() {
		return model.getStyleBookEntryKey();
	}

	/**
	 * Returns the style book entry version ID of this style book entry version.
	 *
	 * @return the style book entry version ID of this style book entry version
	 */
	@Override
	public long getStyleBookEntryVersionId() {
		return model.getStyleBookEntryVersionId();
	}

	/**
	 * Returns the theme ID of this style book entry version.
	 *
	 * @return the theme ID of this style book entry version
	 */
	@Override
	public String getThemeId() {
		return model.getThemeId();
	}

	/**
	 * Returns the user ID of this style book entry version.
	 *
	 * @return the user ID of this style book entry version
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this style book entry version.
	 *
	 * @return the user name of this style book entry version
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this style book entry version.
	 *
	 * @return the user uuid of this style book entry version
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this style book entry version.
	 *
	 * @return the uuid of this style book entry version
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this style book entry version.
	 *
	 * @return the version of this style book entry version
	 */
	@Override
	public int getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns <code>true</code> if this style book entry version is default style book entry.
	 *
	 * @return <code>true</code> if this style book entry version is default style book entry; <code>false</code> otherwise
	 */
	@Override
	public boolean isDefaultStyleBookEntry() {
		return model.isDefaultStyleBookEntry();
	}

	/**
	 * Sets the company ID of this style book entry version.
	 *
	 * @param companyId the company ID of this style book entry version
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this style book entry version.
	 *
	 * @param createDate the create date of this style book entry version
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this style book entry version.
	 *
	 * @param ctCollectionId the ct collection ID of this style book entry version
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets whether this style book entry version is default style book entry.
	 *
	 * @param defaultStyleBookEntry the default style book entry of this style book entry version
	 */
	@Override
	public void setDefaultStyleBookEntry(boolean defaultStyleBookEntry) {
		model.setDefaultStyleBookEntry(defaultStyleBookEntry);
	}

	/**
	 * Sets the external reference code of this style book entry version.
	 *
	 * @param externalReferenceCode the external reference code of this style book entry version
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the frontend token definition of this style book entry version.
	 *
	 * @param frontendTokenDefinition the frontend token definition of this style book entry version
	 */
	@Override
	public void setFrontendTokenDefinition(String frontendTokenDefinition) {
		model.setFrontendTokenDefinition(frontendTokenDefinition);
	}

	/**
	 * Sets the frontend tokens values of this style book entry version.
	 *
	 * @param frontendTokensValues the frontend tokens values of this style book entry version
	 */
	@Override
	public void setFrontendTokensValues(String frontendTokensValues) {
		model.setFrontendTokensValues(frontendTokensValues);
	}

	/**
	 * Sets the group ID of this style book entry version.
	 *
	 * @param groupId the group ID of this style book entry version
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the modified date of this style book entry version.
	 *
	 * @param modifiedDate the modified date of this style book entry version
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this style book entry version.
	 *
	 * @param mvccVersion the mvcc version of this style book entry version
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this style book entry version.
	 *
	 * @param name the name of this style book entry version
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the preview file entry ID of this style book entry version.
	 *
	 * @param previewFileEntryId the preview file entry ID of this style book entry version
	 */
	@Override
	public void setPreviewFileEntryId(long previewFileEntryId) {
		model.setPreviewFileEntryId(previewFileEntryId);
	}

	/**
	 * Sets the primary key of this style book entry version.
	 *
	 * @param primaryKey the primary key of this style book entry version
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the style book entry ID of this style book entry version.
	 *
	 * @param styleBookEntryId the style book entry ID of this style book entry version
	 */
	@Override
	public void setStyleBookEntryId(long styleBookEntryId) {
		model.setStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Sets the style book entry key of this style book entry version.
	 *
	 * @param styleBookEntryKey the style book entry key of this style book entry version
	 */
	@Override
	public void setStyleBookEntryKey(String styleBookEntryKey) {
		model.setStyleBookEntryKey(styleBookEntryKey);
	}

	/**
	 * Sets the style book entry version ID of this style book entry version.
	 *
	 * @param styleBookEntryVersionId the style book entry version ID of this style book entry version
	 */
	@Override
	public void setStyleBookEntryVersionId(long styleBookEntryVersionId) {
		model.setStyleBookEntryVersionId(styleBookEntryVersionId);
	}

	/**
	 * Sets the theme ID of this style book entry version.
	 *
	 * @param themeId the theme ID of this style book entry version
	 */
	@Override
	public void setThemeId(String themeId) {
		model.setThemeId(themeId);
	}

	/**
	 * Sets the user ID of this style book entry version.
	 *
	 * @param userId the user ID of this style book entry version
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this style book entry version.
	 *
	 * @param userName the user name of this style book entry version
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this style book entry version.
	 *
	 * @param userUuid the user uuid of this style book entry version
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this style book entry version.
	 *
	 * @param uuid the uuid of this style book entry version
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this style book entry version.
	 *
	 * @param version the version of this style book entry version
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
	public Map<String, Function<StyleBookEntryVersion, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<StyleBookEntryVersion, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public long getVersionedModelId() {
		return model.getVersionedModelId();
	}

	@Override
	public void setVersionedModelId(long id) {
		model.setVersionedModelId(id);
	}

	@Override
	public void populateVersionedModel(StyleBookEntry styleBookEntry) {
		model.populateVersionedModel(styleBookEntry);
	}

	@Override
	public StyleBookEntry toVersionedModel() {
		return model.toVersionedModel();
	}

	@Override
	protected StyleBookEntryVersionWrapper wrap(
		StyleBookEntryVersion styleBookEntryVersion) {

		return new StyleBookEntryVersionWrapper(styleBookEntryVersion);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:633940839