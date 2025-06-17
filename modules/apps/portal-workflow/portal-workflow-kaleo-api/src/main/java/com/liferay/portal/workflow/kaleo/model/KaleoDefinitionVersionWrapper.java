/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link KaleoDefinitionVersion}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionVersion
 * @generated
 */
public class KaleoDefinitionVersionWrapper
	extends BaseModelWrapper<KaleoDefinitionVersion>
	implements KaleoDefinitionVersion, ModelWrapper<KaleoDefinitionVersion> {

	public KaleoDefinitionVersionWrapper(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		super(kaleoDefinitionVersion);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put(
			"kaleoDefinitionVersionId", getKaleoDefinitionVersionId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("kaleoDefinitionId", getKaleoDefinitionId());
		attributes.put("name", getName());
		attributes.put("title", getTitle());
		attributes.put("description", getDescription());
		attributes.put("content", getContent());
		attributes.put("version", getVersion());
		attributes.put("startKaleoNodeId", getStartKaleoNodeId());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

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

		Long kaleoDefinitionVersionId = (Long)attributes.get(
			"kaleoDefinitionVersionId");

		if (kaleoDefinitionVersionId != null) {
			setKaleoDefinitionVersionId(kaleoDefinitionVersionId);
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

		Long kaleoDefinitionId = (Long)attributes.get("kaleoDefinitionId");

		if (kaleoDefinitionId != null) {
			setKaleoDefinitionId(kaleoDefinitionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String content = (String)attributes.get("content");

		if (content != null) {
			setContent(content);
		}

		String version = (String)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		Long startKaleoNodeId = (Long)attributes.get("startKaleoNodeId");

		if (startKaleoNodeId != null) {
			setStartKaleoNodeId(startKaleoNodeId);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public KaleoDefinitionVersion cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this kaleo definition version.
	 *
	 * @return the company ID of this kaleo definition version
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the content of this kaleo definition version.
	 *
	 * @return the content of this kaleo definition version
	 */
	@Override
	public String getContent() {
		return model.getContent();
	}

	@Override
	public String getContentAsXML() {
		return model.getContentAsXML();
	}

	/**
	 * Returns the create date of this kaleo definition version.
	 *
	 * @return the create date of this kaleo definition version
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this kaleo definition version.
	 *
	 * @return the ct collection ID of this kaleo definition version
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this kaleo definition version.
	 *
	 * @return the description of this kaleo definition version
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the group ID of this kaleo definition version.
	 *
	 * @return the group ID of this kaleo definition version
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	@Override
	public KaleoDefinition getKaleoDefinition()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getKaleoDefinition();
	}

	/**
	 * Returns the kaleo definition ID of this kaleo definition version.
	 *
	 * @return the kaleo definition ID of this kaleo definition version
	 */
	@Override
	public long getKaleoDefinitionId() {
		return model.getKaleoDefinitionId();
	}

	/**
	 * Returns the kaleo definition version ID of this kaleo definition version.
	 *
	 * @return the kaleo definition version ID of this kaleo definition version
	 */
	@Override
	public long getKaleoDefinitionVersionId() {
		return model.getKaleoDefinitionVersionId();
	}

	@Override
	public KaleoNode getKaleoStartNode()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getKaleoStartNode();
	}

	/**
	 * Returns the modified date of this kaleo definition version.
	 *
	 * @return the modified date of this kaleo definition version
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this kaleo definition version.
	 *
	 * @return the mvcc version of this kaleo definition version
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this kaleo definition version.
	 *
	 * @return the name of this kaleo definition version
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this kaleo definition version.
	 *
	 * @return the primary key of this kaleo definition version
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the start kaleo node ID of this kaleo definition version.
	 *
	 * @return the start kaleo node ID of this kaleo definition version
	 */
	@Override
	public long getStartKaleoNodeId() {
		return model.getStartKaleoNodeId();
	}

	/**
	 * Returns the status of this kaleo definition version.
	 *
	 * @return the status of this kaleo definition version
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this kaleo definition version.
	 *
	 * @return the status by user ID of this kaleo definition version
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this kaleo definition version.
	 *
	 * @return the status by user name of this kaleo definition version
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this kaleo definition version.
	 *
	 * @return the status by user uuid of this kaleo definition version
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this kaleo definition version.
	 *
	 * @return the status date of this kaleo definition version
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the title of this kaleo definition version.
	 *
	 * @return the title of this kaleo definition version
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this kaleo definition version in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this kaleo definition version
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this kaleo definition version in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this kaleo definition version. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this kaleo definition version in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this kaleo definition version
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this kaleo definition version in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this kaleo definition version
	 */
	@Override
	public String getTitle(String languageId, boolean useDefault) {
		return model.getTitle(languageId, useDefault);
	}

	@Override
	public String getTitleCurrentLanguageId() {
		return model.getTitleCurrentLanguageId();
	}

	@Override
	public String getTitleCurrentValue() {
		return model.getTitleCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized titles of this kaleo definition version.
	 *
	 * @return the locales and localized titles of this kaleo definition version
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the user ID of this kaleo definition version.
	 *
	 * @return the user ID of this kaleo definition version
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this kaleo definition version.
	 *
	 * @return the user name of this kaleo definition version
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this kaleo definition version.
	 *
	 * @return the user uuid of this kaleo definition version
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the version of this kaleo definition version.
	 *
	 * @return the version of this kaleo definition version
	 */
	@Override
	public String getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is approved.
	 *
	 * @return <code>true</code> if this kaleo definition version is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is denied.
	 *
	 * @return <code>true</code> if this kaleo definition version is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is a draft.
	 *
	 * @return <code>true</code> if this kaleo definition version is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is expired.
	 *
	 * @return <code>true</code> if this kaleo definition version is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is inactive.
	 *
	 * @return <code>true</code> if this kaleo definition version is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is incomplete.
	 *
	 * @return <code>true</code> if this kaleo definition version is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is pending.
	 *
	 * @return <code>true</code> if this kaleo definition version is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this kaleo definition version is scheduled.
	 *
	 * @return <code>true</code> if this kaleo definition version is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
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
	 * Sets the company ID of this kaleo definition version.
	 *
	 * @param companyId the company ID of this kaleo definition version
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the content of this kaleo definition version.
	 *
	 * @param content the content of this kaleo definition version
	 */
	@Override
	public void setContent(String content) {
		model.setContent(content);
	}

	@Override
	public void setContentAsXML(String contentAsXML) {
		model.setContentAsXML(contentAsXML);
	}

	/**
	 * Sets the create date of this kaleo definition version.
	 *
	 * @param createDate the create date of this kaleo definition version
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this kaleo definition version.
	 *
	 * @param ctCollectionId the ct collection ID of this kaleo definition version
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this kaleo definition version.
	 *
	 * @param description the description of this kaleo definition version
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the group ID of this kaleo definition version.
	 *
	 * @param groupId the group ID of this kaleo definition version
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the kaleo definition ID of this kaleo definition version.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID of this kaleo definition version
	 */
	@Override
	public void setKaleoDefinitionId(long kaleoDefinitionId) {
		model.setKaleoDefinitionId(kaleoDefinitionId);
	}

	/**
	 * Sets the kaleo definition version ID of this kaleo definition version.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID of this kaleo definition version
	 */
	@Override
	public void setKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		model.setKaleoDefinitionVersionId(kaleoDefinitionVersionId);
	}

	/**
	 * Sets the modified date of this kaleo definition version.
	 *
	 * @param modifiedDate the modified date of this kaleo definition version
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this kaleo definition version.
	 *
	 * @param mvccVersion the mvcc version of this kaleo definition version
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this kaleo definition version.
	 *
	 * @param name the name of this kaleo definition version
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this kaleo definition version.
	 *
	 * @param primaryKey the primary key of this kaleo definition version
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the start kaleo node ID of this kaleo definition version.
	 *
	 * @param startKaleoNodeId the start kaleo node ID of this kaleo definition version
	 */
	@Override
	public void setStartKaleoNodeId(long startKaleoNodeId) {
		model.setStartKaleoNodeId(startKaleoNodeId);
	}

	/**
	 * Sets the status of this kaleo definition version.
	 *
	 * @param status the status of this kaleo definition version
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this kaleo definition version.
	 *
	 * @param statusByUserId the status by user ID of this kaleo definition version
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this kaleo definition version.
	 *
	 * @param statusByUserName the status by user name of this kaleo definition version
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this kaleo definition version.
	 *
	 * @param statusByUserUuid the status by user uuid of this kaleo definition version
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this kaleo definition version.
	 *
	 * @param statusDate the status date of this kaleo definition version
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the title of this kaleo definition version.
	 *
	 * @param title the title of this kaleo definition version
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this kaleo definition version in the language.
	 *
	 * @param title the localized title of this kaleo definition version
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this kaleo definition version in the language, and sets the default locale.
	 *
	 * @param title the localized title of this kaleo definition version
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitle(
		String title, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setTitle(title, locale, defaultLocale);
	}

	@Override
	public void setTitleCurrentLanguageId(String languageId) {
		model.setTitleCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized titles of this kaleo definition version from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this kaleo definition version
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this kaleo definition version from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this kaleo definition version
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the user ID of this kaleo definition version.
	 *
	 * @param userId the user ID of this kaleo definition version
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this kaleo definition version.
	 *
	 * @param userName the user name of this kaleo definition version
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this kaleo definition version.
	 *
	 * @param userUuid the user uuid of this kaleo definition version
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the version of this kaleo definition version.
	 *
	 * @param version the version of this kaleo definition version
	 */
	@Override
	public void setVersion(String version) {
		model.setVersion(version);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<KaleoDefinitionVersion, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<KaleoDefinitionVersion, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected KaleoDefinitionVersionWrapper wrap(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		return new KaleoDefinitionVersionWrapper(kaleoDefinitionVersion);
	}

}