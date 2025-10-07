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
 * This class is a wrapper for {@link ObjectLayoutTab}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectLayoutTab
 * @generated
 */
public class ObjectLayoutTabWrapper
	extends BaseModelWrapper<ObjectLayoutTab>
	implements ModelWrapper<ObjectLayoutTab>, ObjectLayoutTab {

	public ObjectLayoutTabWrapper(ObjectLayoutTab objectLayoutTab) {
		super(objectLayoutTab);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("objectLayoutTabId", getObjectLayoutTabId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectLayoutId", getObjectLayoutId());
		attributes.put("objectRelationshipId", getObjectRelationshipId());
		attributes.put("name", getName());
		attributes.put("priority", getPriority());

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

		Long objectLayoutTabId = (Long)attributes.get("objectLayoutTabId");

		if (objectLayoutTabId != null) {
			setObjectLayoutTabId(objectLayoutTabId);
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

		Long objectLayoutId = (Long)attributes.get("objectLayoutId");

		if (objectLayoutId != null) {
			setObjectLayoutId(objectLayoutId);
		}

		Long objectRelationshipId = (Long)attributes.get(
			"objectRelationshipId");

		if (objectRelationshipId != null) {
			setObjectRelationshipId(objectRelationshipId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer priority = (Integer)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}
	}

	@Override
	public ObjectLayoutTab cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this object layout tab.
	 *
	 * @return the company ID of this object layout tab
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this object layout tab.
	 *
	 * @return the create date of this object layout tab
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
	 * Returns the modified date of this object layout tab.
	 *
	 * @return the modified date of this object layout tab
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object layout tab.
	 *
	 * @return the mvcc version of this object layout tab
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this object layout tab.
	 *
	 * @return the name of this object layout tab
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this object layout tab in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this object layout tab
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this object layout tab in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this object layout tab. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this object layout tab in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this object layout tab
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this object layout tab in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this object layout tab
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
	 * Returns a map of the locales and localized names of this object layout tab.
	 *
	 * @return the locales and localized names of this object layout tab
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	@Override
	public java.util.List<ObjectLayoutBox> getObjectLayoutBoxes() {
		return model.getObjectLayoutBoxes();
	}

	/**
	 * Returns the object layout ID of this object layout tab.
	 *
	 * @return the object layout ID of this object layout tab
	 */
	@Override
	public long getObjectLayoutId() {
		return model.getObjectLayoutId();
	}

	/**
	 * Returns the object layout tab ID of this object layout tab.
	 *
	 * @return the object layout tab ID of this object layout tab
	 */
	@Override
	public long getObjectLayoutTabId() {
		return model.getObjectLayoutTabId();
	}

	@Override
	public ObjectRelationship getObjectRelationship()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getObjectRelationship();
	}

	/**
	 * Returns the object relationship ID of this object layout tab.
	 *
	 * @return the object relationship ID of this object layout tab
	 */
	@Override
	public long getObjectRelationshipId() {
		return model.getObjectRelationshipId();
	}

	/**
	 * Returns the primary key of this object layout tab.
	 *
	 * @return the primary key of this object layout tab
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this object layout tab.
	 *
	 * @return the priority of this object layout tab
	 */
	@Override
	public int getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the user ID of this object layout tab.
	 *
	 * @return the user ID of this object layout tab
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object layout tab.
	 *
	 * @return the user name of this object layout tab
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object layout tab.
	 *
	 * @return the user uuid of this object layout tab
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object layout tab.
	 *
	 * @return the uuid of this object layout tab
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
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
	 * Sets the company ID of this object layout tab.
	 *
	 * @param companyId the company ID of this object layout tab
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this object layout tab.
	 *
	 * @param createDate the create date of this object layout tab
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this object layout tab.
	 *
	 * @param modifiedDate the modified date of this object layout tab
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object layout tab.
	 *
	 * @param mvccVersion the mvcc version of this object layout tab
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this object layout tab.
	 *
	 * @param name the name of this object layout tab
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this object layout tab in the language.
	 *
	 * @param name the localized name of this object layout tab
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this object layout tab in the language, and sets the default locale.
	 *
	 * @param name the localized name of this object layout tab
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
	 * Sets the localized names of this object layout tab from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this object layout tab
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this object layout tab from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this object layout tab
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	@Override
	public void setObjectLayoutBoxes(
		java.util.List<ObjectLayoutBox> objectLayoutBoxes) {

		model.setObjectLayoutBoxes(objectLayoutBoxes);
	}

	/**
	 * Sets the object layout ID of this object layout tab.
	 *
	 * @param objectLayoutId the object layout ID of this object layout tab
	 */
	@Override
	public void setObjectLayoutId(long objectLayoutId) {
		model.setObjectLayoutId(objectLayoutId);
	}

	/**
	 * Sets the object layout tab ID of this object layout tab.
	 *
	 * @param objectLayoutTabId the object layout tab ID of this object layout tab
	 */
	@Override
	public void setObjectLayoutTabId(long objectLayoutTabId) {
		model.setObjectLayoutTabId(objectLayoutTabId);
	}

	/**
	 * Sets the object relationship ID of this object layout tab.
	 *
	 * @param objectRelationshipId the object relationship ID of this object layout tab
	 */
	@Override
	public void setObjectRelationshipId(long objectRelationshipId) {
		model.setObjectRelationshipId(objectRelationshipId);
	}

	/**
	 * Sets the primary key of this object layout tab.
	 *
	 * @param primaryKey the primary key of this object layout tab
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this object layout tab.
	 *
	 * @param priority the priority of this object layout tab
	 */
	@Override
	public void setPriority(int priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the user ID of this object layout tab.
	 *
	 * @param userId the user ID of this object layout tab
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object layout tab.
	 *
	 * @param userName the user name of this object layout tab
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object layout tab.
	 *
	 * @param userUuid the user uuid of this object layout tab
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object layout tab.
	 *
	 * @param uuid the uuid of this object layout tab
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
	protected ObjectLayoutTabWrapper wrap(ObjectLayoutTab objectLayoutTab) {
		return new ObjectLayoutTabWrapper(objectLayoutTab);
	}

}