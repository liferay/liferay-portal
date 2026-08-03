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
 * This class is a wrapper for {@link ObjectView}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectView
 * @generated
 */
public class ObjectViewWrapper
	extends BaseModelWrapper<ObjectView>
	implements ModelWrapper<ObjectView>, ObjectView {

	public ObjectViewWrapper(ObjectView objectView) {
		super(objectView);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("objectViewId", getObjectViewId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("defaultObjectView", isDefaultObjectView());
		attributes.put("name", getName());

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

		Long objectViewId = (Long)attributes.get("objectViewId");

		if (objectViewId != null) {
			setObjectViewId(objectViewId);
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

		Boolean defaultObjectView = (Boolean)attributes.get(
			"defaultObjectView");

		if (defaultObjectView != null) {
			setDefaultObjectView(defaultObjectView);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public ObjectView cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this object view.
	 *
	 * @return the company ID of this object view
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this object view.
	 *
	 * @return the create date of this object view
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
	 * Returns the default object view of this object view.
	 *
	 * @return the default object view of this object view
	 */
	@Override
	public boolean getDefaultObjectView() {
		return model.getDefaultObjectView();
	}

	/**
	 * Returns the external reference code of this object view.
	 *
	 * @return the external reference code of this object view
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this object view.
	 *
	 * @return the modified date of this object view
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object view.
	 *
	 * @return the mvcc version of this object view
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this object view.
	 *
	 * @return the name of this object view
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this object view in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this object view
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this object view in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this object view. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this object view in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this object view
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this object view in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this object view
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
	 * Returns a map of the locales and localized names of this object view.
	 *
	 * @return the locales and localized names of this object view
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the object definition ID of this object view.
	 *
	 * @return the object definition ID of this object view
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	@Override
	public java.util.List<ObjectViewColumn> getObjectViewColumns() {
		return model.getObjectViewColumns();
	}

	@Override
	public java.util.List<ObjectViewFilterColumn> getObjectViewFilterColumns() {
		return model.getObjectViewFilterColumns();
	}

	/**
	 * Returns the object view ID of this object view.
	 *
	 * @return the object view ID of this object view
	 */
	@Override
	public long getObjectViewId() {
		return model.getObjectViewId();
	}

	@Override
	public java.util.List<ObjectViewSortColumn> getObjectViewSortColumns() {
		return model.getObjectViewSortColumns();
	}

	/**
	 * Returns the primary key of this object view.
	 *
	 * @return the primary key of this object view
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this object view.
	 *
	 * @return the user ID of this object view
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object view.
	 *
	 * @return the user name of this object view
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object view.
	 *
	 * @return the user uuid of this object view
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this object view.
	 *
	 * @return the uuid of this object view
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this object view is default object view.
	 *
	 * @return <code>true</code> if this object view is default object view; <code>false</code> otherwise
	 */
	@Override
	public boolean isDefaultObjectView() {
		return model.isDefaultObjectView();
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
	 * Sets the company ID of this object view.
	 *
	 * @param companyId the company ID of this object view
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this object view.
	 *
	 * @param createDate the create date of this object view
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets whether this object view is default object view.
	 *
	 * @param defaultObjectView the default object view of this object view
	 */
	@Override
	public void setDefaultObjectView(boolean defaultObjectView) {
		model.setDefaultObjectView(defaultObjectView);
	}

	/**
	 * Sets the external reference code of this object view.
	 *
	 * @param externalReferenceCode the external reference code of this object view
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this object view.
	 *
	 * @param modifiedDate the modified date of this object view
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object view.
	 *
	 * @param mvccVersion the mvcc version of this object view
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this object view.
	 *
	 * @param name the name of this object view
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this object view in the language.
	 *
	 * @param name the localized name of this object view
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this object view in the language, and sets the default locale.
	 *
	 * @param name the localized name of this object view
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
	 * Sets the localized names of this object view from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this object view
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this object view from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this object view
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the object definition ID of this object view.
	 *
	 * @param objectDefinitionId the object definition ID of this object view
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	@Override
	public void setObjectViewColumns(
		java.util.List<ObjectViewColumn> objectViewColumns) {

		model.setObjectViewColumns(objectViewColumns);
	}

	@Override
	public void setObjectViewFilterColumns(
		java.util.List<ObjectViewFilterColumn> objectViewFilterColumns) {

		model.setObjectViewFilterColumns(objectViewFilterColumns);
	}

	/**
	 * Sets the object view ID of this object view.
	 *
	 * @param objectViewId the object view ID of this object view
	 */
	@Override
	public void setObjectViewId(long objectViewId) {
		model.setObjectViewId(objectViewId);
	}

	@Override
	public void setObjectViewSortColumns(
		java.util.List<ObjectViewSortColumn> objectViewSortColumns) {

		model.setObjectViewSortColumns(objectViewSortColumns);
	}

	/**
	 * Sets the primary key of this object view.
	 *
	 * @param primaryKey the primary key of this object view
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this object view.
	 *
	 * @param userId the user ID of this object view
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object view.
	 *
	 * @param userName the user name of this object view
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object view.
	 *
	 * @param userUuid the user uuid of this object view
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this object view.
	 *
	 * @param uuid the uuid of this object view
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
	protected ObjectViewWrapper wrap(ObjectView objectView) {
		return new ObjectViewWrapper(objectView);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-844534279