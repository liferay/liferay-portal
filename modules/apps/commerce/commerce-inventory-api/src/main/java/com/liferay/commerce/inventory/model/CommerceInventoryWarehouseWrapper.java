/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CommerceInventoryWarehouse}.
 * </p>
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouse
 * @generated
 */
public class CommerceInventoryWarehouseWrapper
	extends BaseModelWrapper<CommerceInventoryWarehouse>
	implements CommerceInventoryWarehouse,
			   ModelWrapper<CommerceInventoryWarehouse> {

	public CommerceInventoryWarehouseWrapper(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		super(commerceInventoryWarehouse);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"commerceInventoryWarehouseId", getCommerceInventoryWarehouseId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("active", isActive());
		attributes.put("street1", getStreet1());
		attributes.put("street2", getStreet2());
		attributes.put("street3", getStreet3());
		attributes.put("city", getCity());
		attributes.put("zip", getZip());
		attributes.put("commerceRegionCode", getCommerceRegionCode());
		attributes.put(
			"countryTwoLettersISOCode", getCountryTwoLettersISOCode());
		attributes.put("latitude", getLatitude());
		attributes.put("longitude", getLongitude());
		attributes.put("type", getType());
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

		Long commerceInventoryWarehouseId = (Long)attributes.get(
			"commerceInventoryWarehouseId");

		if (commerceInventoryWarehouseId != null) {
			setCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
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

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}

		String street1 = (String)attributes.get("street1");

		if (street1 != null) {
			setStreet1(street1);
		}

		String street2 = (String)attributes.get("street2");

		if (street2 != null) {
			setStreet2(street2);
		}

		String street3 = (String)attributes.get("street3");

		if (street3 != null) {
			setStreet3(street3);
		}

		String city = (String)attributes.get("city");

		if (city != null) {
			setCity(city);
		}

		String zip = (String)attributes.get("zip");

		if (zip != null) {
			setZip(zip);
		}

		String commerceRegionCode = (String)attributes.get(
			"commerceRegionCode");

		if (commerceRegionCode != null) {
			setCommerceRegionCode(commerceRegionCode);
		}

		String countryTwoLettersISOCode = (String)attributes.get(
			"countryTwoLettersISOCode");

		if (countryTwoLettersISOCode != null) {
			setCountryTwoLettersISOCode(countryTwoLettersISOCode);
		}

		Double latitude = (Double)attributes.get("latitude");

		if (latitude != null) {
			setLatitude(latitude);
		}

		Double longitude = (Double)attributes.get("longitude");

		if (longitude != null) {
			setLongitude(longitude);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public CommerceInventoryWarehouse cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the active of this commerce inventory warehouse.
	 *
	 * @return the active of this commerce inventory warehouse
	 */
	@Override
	public boolean getActive() {
		return model.getActive();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the city of this commerce inventory warehouse.
	 *
	 * @return the city of this commerce inventory warehouse
	 */
	@Override
	public String getCity() {
		return model.getCity();
	}

	/**
	 * Returns the commerce inventory warehouse ID of this commerce inventory warehouse.
	 *
	 * @return the commerce inventory warehouse ID of this commerce inventory warehouse
	 */
	@Override
	public long getCommerceInventoryWarehouseId() {
		return model.getCommerceInventoryWarehouseId();
	}

	@Override
	public java.util.List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItems() {

		return model.getCommerceInventoryWarehouseItems();
	}

	/**
	 * Returns the commerce region code of this commerce inventory warehouse.
	 *
	 * @return the commerce region code of this commerce inventory warehouse
	 */
	@Override
	public String getCommerceRegionCode() {
		return model.getCommerceRegionCode();
	}

	/**
	 * Returns the company ID of this commerce inventory warehouse.
	 *
	 * @return the company ID of this commerce inventory warehouse
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the country two letters iso code of this commerce inventory warehouse.
	 *
	 * @return the country two letters iso code of this commerce inventory warehouse
	 */
	@Override
	public String getCountryTwoLettersISOCode() {
		return model.getCountryTwoLettersISOCode();
	}

	/**
	 * Returns the create date of this commerce inventory warehouse.
	 *
	 * @return the create date of this commerce inventory warehouse
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
	 * Returns the description of this commerce inventory warehouse.
	 *
	 * @return the description of this commerce inventory warehouse
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this commerce inventory warehouse in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this commerce inventory warehouse
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this commerce inventory warehouse in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this commerce inventory warehouse. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this commerce inventory warehouse in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this commerce inventory warehouse
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this commerce inventory warehouse in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this commerce inventory warehouse
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
	 * Returns a map of the locales and localized descriptions of this commerce inventory warehouse.
	 *
	 * @return the locales and localized descriptions of this commerce inventory warehouse
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the external reference code of this commerce inventory warehouse.
	 *
	 * @return the external reference code of this commerce inventory warehouse
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the latitude of this commerce inventory warehouse.
	 *
	 * @return the latitude of this commerce inventory warehouse
	 */
	@Override
	public double getLatitude() {
		return model.getLatitude();
	}

	/**
	 * Returns the longitude of this commerce inventory warehouse.
	 *
	 * @return the longitude of this commerce inventory warehouse
	 */
	@Override
	public double getLongitude() {
		return model.getLongitude();
	}

	/**
	 * Returns the modified date of this commerce inventory warehouse.
	 *
	 * @return the modified date of this commerce inventory warehouse
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce inventory warehouse.
	 *
	 * @return the mvcc version of this commerce inventory warehouse
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce inventory warehouse.
	 *
	 * @return the name of this commerce inventory warehouse
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this commerce inventory warehouse in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this commerce inventory warehouse
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this commerce inventory warehouse in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce inventory warehouse. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this commerce inventory warehouse in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this commerce inventory warehouse
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this commerce inventory warehouse in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce inventory warehouse
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
	 * Returns a map of the locales and localized names of this commerce inventory warehouse.
	 *
	 * @return the locales and localized names of this commerce inventory warehouse
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary key of this commerce inventory warehouse.
	 *
	 * @return the primary key of this commerce inventory warehouse
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the status of this commerce inventory warehouse.
	 *
	 * @return the status of this commerce inventory warehouse
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the street1 of this commerce inventory warehouse.
	 *
	 * @return the street1 of this commerce inventory warehouse
	 */
	@Override
	public String getStreet1() {
		return model.getStreet1();
	}

	/**
	 * Returns the street2 of this commerce inventory warehouse.
	 *
	 * @return the street2 of this commerce inventory warehouse
	 */
	@Override
	public String getStreet2() {
		return model.getStreet2();
	}

	/**
	 * Returns the street3 of this commerce inventory warehouse.
	 *
	 * @return the street3 of this commerce inventory warehouse
	 */
	@Override
	public String getStreet3() {
		return model.getStreet3();
	}

	/**
	 * Returns the type of this commerce inventory warehouse.
	 *
	 * @return the type of this commerce inventory warehouse
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this commerce inventory warehouse.
	 *
	 * @return the user ID of this commerce inventory warehouse
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce inventory warehouse.
	 *
	 * @return the user name of this commerce inventory warehouse
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce inventory warehouse.
	 *
	 * @return the user uuid of this commerce inventory warehouse
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this commerce inventory warehouse.
	 *
	 * @return the uuid of this commerce inventory warehouse
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the zip of this commerce inventory warehouse.
	 *
	 * @return the zip of this commerce inventory warehouse
	 */
	@Override
	public String getZip() {
		return model.getZip();
	}

	/**
	 * Returns <code>true</code> if this commerce inventory warehouse is active.
	 *
	 * @return <code>true</code> if this commerce inventory warehouse is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	@Override
	public boolean isGeolocated() {
		return model.isGeolocated();
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
	 * Sets whether this commerce inventory warehouse is active.
	 *
	 * @param active the active of this commerce inventory warehouse
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the city of this commerce inventory warehouse.
	 *
	 * @param city the city of this commerce inventory warehouse
	 */
	@Override
	public void setCity(String city) {
		model.setCity(city);
	}

	/**
	 * Sets the commerce inventory warehouse ID of this commerce inventory warehouse.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID of this commerce inventory warehouse
	 */
	@Override
	public void setCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		model.setCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
	}

	/**
	 * Sets the commerce region code of this commerce inventory warehouse.
	 *
	 * @param commerceRegionCode the commerce region code of this commerce inventory warehouse
	 */
	@Override
	public void setCommerceRegionCode(String commerceRegionCode) {
		model.setCommerceRegionCode(commerceRegionCode);
	}

	/**
	 * Sets the company ID of this commerce inventory warehouse.
	 *
	 * @param companyId the company ID of this commerce inventory warehouse
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the country two letters iso code of this commerce inventory warehouse.
	 *
	 * @param countryTwoLettersISOCode the country two letters iso code of this commerce inventory warehouse
	 */
	@Override
	public void setCountryTwoLettersISOCode(String countryTwoLettersISOCode) {
		model.setCountryTwoLettersISOCode(countryTwoLettersISOCode);
	}

	/**
	 * Sets the create date of this commerce inventory warehouse.
	 *
	 * @param createDate the create date of this commerce inventory warehouse
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this commerce inventory warehouse.
	 *
	 * @param description the description of this commerce inventory warehouse
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this commerce inventory warehouse in the language.
	 *
	 * @param description the localized description of this commerce inventory warehouse
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this commerce inventory warehouse in the language, and sets the default locale.
	 *
	 * @param description the localized description of this commerce inventory warehouse
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
	 * Sets the localized descriptions of this commerce inventory warehouse from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this commerce inventory warehouse
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this commerce inventory warehouse from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this commerce inventory warehouse
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this commerce inventory warehouse.
	 *
	 * @param externalReferenceCode the external reference code of this commerce inventory warehouse
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the latitude of this commerce inventory warehouse.
	 *
	 * @param latitude the latitude of this commerce inventory warehouse
	 */
	@Override
	public void setLatitude(double latitude) {
		model.setLatitude(latitude);
	}

	/**
	 * Sets the longitude of this commerce inventory warehouse.
	 *
	 * @param longitude the longitude of this commerce inventory warehouse
	 */
	@Override
	public void setLongitude(double longitude) {
		model.setLongitude(longitude);
	}

	/**
	 * Sets the modified date of this commerce inventory warehouse.
	 *
	 * @param modifiedDate the modified date of this commerce inventory warehouse
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce inventory warehouse.
	 *
	 * @param mvccVersion the mvcc version of this commerce inventory warehouse
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce inventory warehouse.
	 *
	 * @param name the name of this commerce inventory warehouse
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this commerce inventory warehouse in the language.
	 *
	 * @param name the localized name of this commerce inventory warehouse
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this commerce inventory warehouse in the language, and sets the default locale.
	 *
	 * @param name the localized name of this commerce inventory warehouse
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
	 * Sets the localized names of this commerce inventory warehouse from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this commerce inventory warehouse
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this commerce inventory warehouse from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this commerce inventory warehouse
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the primary key of this commerce inventory warehouse.
	 *
	 * @param primaryKey the primary key of this commerce inventory warehouse
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the status of this commerce inventory warehouse.
	 *
	 * @param status the status of this commerce inventory warehouse
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the street1 of this commerce inventory warehouse.
	 *
	 * @param street1 the street1 of this commerce inventory warehouse
	 */
	@Override
	public void setStreet1(String street1) {
		model.setStreet1(street1);
	}

	/**
	 * Sets the street2 of this commerce inventory warehouse.
	 *
	 * @param street2 the street2 of this commerce inventory warehouse
	 */
	@Override
	public void setStreet2(String street2) {
		model.setStreet2(street2);
	}

	/**
	 * Sets the street3 of this commerce inventory warehouse.
	 *
	 * @param street3 the street3 of this commerce inventory warehouse
	 */
	@Override
	public void setStreet3(String street3) {
		model.setStreet3(street3);
	}

	/**
	 * Sets the type of this commerce inventory warehouse.
	 *
	 * @param type the type of this commerce inventory warehouse
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this commerce inventory warehouse.
	 *
	 * @param userId the user ID of this commerce inventory warehouse
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce inventory warehouse.
	 *
	 * @param userName the user name of this commerce inventory warehouse
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce inventory warehouse.
	 *
	 * @param userUuid the user uuid of this commerce inventory warehouse
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this commerce inventory warehouse.
	 *
	 * @param uuid the uuid of this commerce inventory warehouse
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the zip of this commerce inventory warehouse.
	 *
	 * @param zip the zip of this commerce inventory warehouse
	 */
	@Override
	public void setZip(String zip) {
		model.setZip(zip);
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
	protected CommerceInventoryWarehouseWrapper wrap(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		return new CommerceInventoryWarehouseWrapper(
			commerceInventoryWarehouse);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-929497066