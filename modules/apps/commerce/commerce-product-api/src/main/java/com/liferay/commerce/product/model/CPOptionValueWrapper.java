/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

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
 * This class is a wrapper for {@link CPOptionValue}.
 * </p>
 *
 * @author Marco Leo
 * @see CPOptionValue
 * @generated
 */
public class CPOptionValueWrapper
	extends BaseModelWrapper<CPOptionValue>
	implements CPOptionValue, ModelWrapper<CPOptionValue> {

	public CPOptionValueWrapper(CPOptionValue cpOptionValue) {
		super(cpOptionValue);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("CPOptionValueId", getCPOptionValueId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("CPOptionId", getCPOptionId());
		attributes.put("name", getName());
		attributes.put("priority", getPriority());
		attributes.put("key", getKey());
		attributes.put("lastPublishDate", getLastPublishDate());
		attributes.put("status", getStatus());

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

		Long CPOptionValueId = (Long)attributes.get("CPOptionValueId");

		if (CPOptionValueId != null) {
			setCPOptionValueId(CPOptionValueId);
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

		Long CPOptionId = (Long)attributes.get("CPOptionId");

		if (CPOptionId != null) {
			setCPOptionId(CPOptionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public CPOptionValue cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this cp option value.
	 *
	 * @return the company ID of this cp option value
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public CPOption getCPOption()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPOption();
	}

	/**
	 * Returns the cp option ID of this cp option value.
	 *
	 * @return the cp option ID of this cp option value
	 */
	@Override
	public long getCPOptionId() {
		return model.getCPOptionId();
	}

	/**
	 * Returns the cp option value ID of this cp option value.
	 *
	 * @return the cp option value ID of this cp option value
	 */
	@Override
	public long getCPOptionValueId() {
		return model.getCPOptionValueId();
	}

	/**
	 * Returns the create date of this cp option value.
	 *
	 * @return the create date of this cp option value
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp option value.
	 *
	 * @return the ct collection ID of this cp option value
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
	 * Returns the external reference code of this cp option value.
	 *
	 * @return the external reference code of this cp option value
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the key of this cp option value.
	 *
	 * @return the key of this cp option value
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the last publish date of this cp option value.
	 *
	 * @return the last publish date of this cp option value
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this cp option value.
	 *
	 * @return the modified date of this cp option value
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cp option value.
	 *
	 * @return the mvcc version of this cp option value
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cp option value.
	 *
	 * @return the name of this cp option value
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this cp option value in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this cp option value
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this cp option value in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp option value. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this cp option value in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this cp option value
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this cp option value in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp option value
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
	 * Returns a map of the locales and localized names of this cp option value.
	 *
	 * @return the locales and localized names of this cp option value
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary key of this cp option value.
	 *
	 * @return the primary key of this cp option value
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this cp option value.
	 *
	 * @return the priority of this cp option value
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the status of this cp option value.
	 *
	 * @return the status of this cp option value
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the user ID of this cp option value.
	 *
	 * @return the user ID of this cp option value
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp option value.
	 *
	 * @return the user name of this cp option value
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp option value.
	 *
	 * @return the user uuid of this cp option value
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp option value.
	 *
	 * @return the uuid of this cp option value
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
	 * Sets the company ID of this cp option value.
	 *
	 * @param companyId the company ID of this cp option value
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp option ID of this cp option value.
	 *
	 * @param CPOptionId the cp option ID of this cp option value
	 */
	@Override
	public void setCPOptionId(long CPOptionId) {
		model.setCPOptionId(CPOptionId);
	}

	/**
	 * Sets the cp option value ID of this cp option value.
	 *
	 * @param CPOptionValueId the cp option value ID of this cp option value
	 */
	@Override
	public void setCPOptionValueId(long CPOptionValueId) {
		model.setCPOptionValueId(CPOptionValueId);
	}

	/**
	 * Sets the create date of this cp option value.
	 *
	 * @param createDate the create date of this cp option value
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp option value.
	 *
	 * @param ctCollectionId the ct collection ID of this cp option value
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the external reference code of this cp option value.
	 *
	 * @param externalReferenceCode the external reference code of this cp option value
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the key of this cp option value.
	 *
	 * @param key the key of this cp option value
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the last publish date of this cp option value.
	 *
	 * @param lastPublishDate the last publish date of this cp option value
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this cp option value.
	 *
	 * @param modifiedDate the modified date of this cp option value
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cp option value.
	 *
	 * @param mvccVersion the mvcc version of this cp option value
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cp option value.
	 *
	 * @param name the name of this cp option value
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this cp option value in the language.
	 *
	 * @param name the localized name of this cp option value
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this cp option value in the language, and sets the default locale.
	 *
	 * @param name the localized name of this cp option value
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
	 * Sets the localized names of this cp option value from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this cp option value
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this cp option value from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this cp option value
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the primary key of this cp option value.
	 *
	 * @param primaryKey the primary key of this cp option value
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this cp option value.
	 *
	 * @param priority the priority of this cp option value
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the status of this cp option value.
	 *
	 * @param status the status of this cp option value
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the user ID of this cp option value.
	 *
	 * @param userId the user ID of this cp option value
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp option value.
	 *
	 * @param userName the user name of this cp option value
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp option value.
	 *
	 * @param userUuid the user uuid of this cp option value
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp option value.
	 *
	 * @param uuid the uuid of this cp option value
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
	public Map<String, Function<CPOptionValue, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPOptionValue, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPOptionValueWrapper wrap(CPOptionValue cpOptionValue) {
		return new CPOptionValueWrapper(cpOptionValue);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-865787065