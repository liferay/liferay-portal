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
 * This class is a wrapper for {@link CPMeasurementUnit}.
 * </p>
 *
 * @author Marco Leo
 * @see CPMeasurementUnit
 * @generated
 */
public class CPMeasurementUnitWrapper
	extends BaseModelWrapper<CPMeasurementUnit>
	implements CPMeasurementUnit, ModelWrapper<CPMeasurementUnit> {

	public CPMeasurementUnitWrapper(CPMeasurementUnit cpMeasurementUnit) {
		super(cpMeasurementUnit);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("CPMeasurementUnitId", getCPMeasurementUnitId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("key", getKey());
		attributes.put("rate", getRate());
		attributes.put("primary", isPrimary());
		attributes.put("priority", getPriority());
		attributes.put("type", getType());
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

		Long CPMeasurementUnitId = (Long)attributes.get("CPMeasurementUnitId");

		if (CPMeasurementUnitId != null) {
			setCPMeasurementUnitId(CPMeasurementUnitId);
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

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double rate = (Double)attributes.get("rate");

		if (rate != null) {
			setRate(rate);
		}

		Boolean primary = (Boolean)attributes.get("primary");

		if (primary != null) {
			setPrimary(primary);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
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
	public CPMeasurementUnit cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this cp measurement unit.
	 *
	 * @return the company ID of this cp measurement unit
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the cp measurement unit ID of this cp measurement unit.
	 *
	 * @return the cp measurement unit ID of this cp measurement unit
	 */
	@Override
	public long getCPMeasurementUnitId() {
		return model.getCPMeasurementUnitId();
	}

	/**
	 * Returns the create date of this cp measurement unit.
	 *
	 * @return the create date of this cp measurement unit
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp measurement unit.
	 *
	 * @return the ct collection ID of this cp measurement unit
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
	 * Returns the external reference code of this cp measurement unit.
	 *
	 * @return the external reference code of this cp measurement unit
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this cp measurement unit.
	 *
	 * @return the group ID of this cp measurement unit
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the key of this cp measurement unit.
	 *
	 * @return the key of this cp measurement unit
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the last publish date of this cp measurement unit.
	 *
	 * @return the last publish date of this cp measurement unit
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this cp measurement unit.
	 *
	 * @return the modified date of this cp measurement unit
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cp measurement unit.
	 *
	 * @return the mvcc version of this cp measurement unit
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cp measurement unit.
	 *
	 * @return the name of this cp measurement unit
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this cp measurement unit in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this cp measurement unit
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this cp measurement unit in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp measurement unit. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this cp measurement unit in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this cp measurement unit
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this cp measurement unit in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp measurement unit
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
	 * Returns a map of the locales and localized names of this cp measurement unit.
	 *
	 * @return the locales and localized names of this cp measurement unit
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary of this cp measurement unit.
	 *
	 * @return the primary of this cp measurement unit
	 */
	@Override
	public boolean getPrimary() {
		return model.getPrimary();
	}

	/**
	 * Returns the primary key of this cp measurement unit.
	 *
	 * @return the primary key of this cp measurement unit
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this cp measurement unit.
	 *
	 * @return the priority of this cp measurement unit
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the rate of this cp measurement unit.
	 *
	 * @return the rate of this cp measurement unit
	 */
	@Override
	public double getRate() {
		return model.getRate();
	}

	/**
	 * Returns the status of this cp measurement unit.
	 *
	 * @return the status of this cp measurement unit
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the type of this cp measurement unit.
	 *
	 * @return the type of this cp measurement unit
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this cp measurement unit.
	 *
	 * @return the user ID of this cp measurement unit
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp measurement unit.
	 *
	 * @return the user name of this cp measurement unit
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp measurement unit.
	 *
	 * @return the user uuid of this cp measurement unit
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp measurement unit.
	 *
	 * @return the uuid of this cp measurement unit
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this cp measurement unit is primary.
	 *
	 * @return <code>true</code> if this cp measurement unit is primary; <code>false</code> otherwise
	 */
	@Override
	public boolean isPrimary() {
		return model.isPrimary();
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
	 * Sets the company ID of this cp measurement unit.
	 *
	 * @param companyId the company ID of this cp measurement unit
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp measurement unit ID of this cp measurement unit.
	 *
	 * @param CPMeasurementUnitId the cp measurement unit ID of this cp measurement unit
	 */
	@Override
	public void setCPMeasurementUnitId(long CPMeasurementUnitId) {
		model.setCPMeasurementUnitId(CPMeasurementUnitId);
	}

	/**
	 * Sets the create date of this cp measurement unit.
	 *
	 * @param createDate the create date of this cp measurement unit
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp measurement unit.
	 *
	 * @param ctCollectionId the ct collection ID of this cp measurement unit
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the external reference code of this cp measurement unit.
	 *
	 * @param externalReferenceCode the external reference code of this cp measurement unit
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this cp measurement unit.
	 *
	 * @param groupId the group ID of this cp measurement unit
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the key of this cp measurement unit.
	 *
	 * @param key the key of this cp measurement unit
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the last publish date of this cp measurement unit.
	 *
	 * @param lastPublishDate the last publish date of this cp measurement unit
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this cp measurement unit.
	 *
	 * @param modifiedDate the modified date of this cp measurement unit
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cp measurement unit.
	 *
	 * @param mvccVersion the mvcc version of this cp measurement unit
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cp measurement unit.
	 *
	 * @param name the name of this cp measurement unit
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this cp measurement unit in the language.
	 *
	 * @param name the localized name of this cp measurement unit
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this cp measurement unit in the language, and sets the default locale.
	 *
	 * @param name the localized name of this cp measurement unit
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
	 * Sets the localized names of this cp measurement unit from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this cp measurement unit
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this cp measurement unit from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this cp measurement unit
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets whether this cp measurement unit is primary.
	 *
	 * @param primary the primary of this cp measurement unit
	 */
	@Override
	public void setPrimary(boolean primary) {
		model.setPrimary(primary);
	}

	/**
	 * Sets the primary key of this cp measurement unit.
	 *
	 * @param primaryKey the primary key of this cp measurement unit
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this cp measurement unit.
	 *
	 * @param priority the priority of this cp measurement unit
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the rate of this cp measurement unit.
	 *
	 * @param rate the rate of this cp measurement unit
	 */
	@Override
	public void setRate(double rate) {
		model.setRate(rate);
	}

	/**
	 * Sets the status of this cp measurement unit.
	 *
	 * @param status the status of this cp measurement unit
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the type of this cp measurement unit.
	 *
	 * @param type the type of this cp measurement unit
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this cp measurement unit.
	 *
	 * @param userId the user ID of this cp measurement unit
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp measurement unit.
	 *
	 * @param userName the user name of this cp measurement unit
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp measurement unit.
	 *
	 * @param userUuid the user uuid of this cp measurement unit
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp measurement unit.
	 *
	 * @param uuid the uuid of this cp measurement unit
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
	public Map<String, Function<CPMeasurementUnit, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPMeasurementUnit, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPMeasurementUnitWrapper wrap(
		CPMeasurementUnit cpMeasurementUnit) {

		return new CPMeasurementUnitWrapper(cpMeasurementUnit);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1733371317