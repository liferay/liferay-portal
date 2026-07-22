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
 * This class is a wrapper for {@link CPOption}.
 * </p>
 *
 * @author Marco Leo
 * @see CPOption
 * @generated
 */
public class CPOptionWrapper
	extends BaseModelWrapper<CPOption>
	implements CPOption, ModelWrapper<CPOption> {

	public CPOptionWrapper(CPOption cpOption) {
		super(cpOption);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("CPOptionId", getCPOptionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("commerceOptionTypeKey", getCommerceOptionTypeKey());
		attributes.put("facetable", isFacetable());
		attributes.put("required", isRequired());
		attributes.put("skuContributor", isSkuContributor());
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

		Long CPOptionId = (Long)attributes.get("CPOptionId");

		if (CPOptionId != null) {
			setCPOptionId(CPOptionId);
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

		String commerceOptionTypeKey = (String)attributes.get(
			"commerceOptionTypeKey");

		if (commerceOptionTypeKey != null) {
			setCommerceOptionTypeKey(commerceOptionTypeKey);
		}

		Boolean facetable = (Boolean)attributes.get("facetable");

		if (facetable != null) {
			setFacetable(facetable);
		}

		Boolean required = (Boolean)attributes.get("required");

		if (required != null) {
			setRequired(required);
		}

		Boolean skuContributor = (Boolean)attributes.get("skuContributor");

		if (skuContributor != null) {
			setSkuContributor(skuContributor);
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
	public CPOption cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the commerce option type key of this cp option.
	 *
	 * @return the commerce option type key of this cp option
	 */
	@Override
	public String getCommerceOptionTypeKey() {
		return model.getCommerceOptionTypeKey();
	}

	/**
	 * Returns the company ID of this cp option.
	 *
	 * @return the company ID of this cp option
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the cp option ID of this cp option.
	 *
	 * @return the cp option ID of this cp option
	 */
	@Override
	public long getCPOptionId() {
		return model.getCPOptionId();
	}

	@Override
	public java.util.List<CPOptionValue> getCPOptionValues() {
		return model.getCPOptionValues();
	}

	/**
	 * Returns the create date of this cp option.
	 *
	 * @return the create date of this cp option
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp option.
	 *
	 * @return the ct collection ID of this cp option
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
	 * Returns the description of this cp option.
	 *
	 * @return the description of this cp option
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this cp option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this cp option
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this cp option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this cp option. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this cp option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this cp option
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this cp option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this cp option
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
	 * Returns a map of the locales and localized descriptions of this cp option.
	 *
	 * @return the locales and localized descriptions of this cp option
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the external reference code of this cp option.
	 *
	 * @return the external reference code of this cp option
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the facetable of this cp option.
	 *
	 * @return the facetable of this cp option
	 */
	@Override
	public boolean getFacetable() {
		return model.getFacetable();
	}

	/**
	 * Returns the key of this cp option.
	 *
	 * @return the key of this cp option
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the last publish date of this cp option.
	 *
	 * @return the last publish date of this cp option
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this cp option.
	 *
	 * @return the modified date of this cp option
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cp option.
	 *
	 * @return the mvcc version of this cp option
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cp option.
	 *
	 * @return the name of this cp option
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this cp option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this cp option
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this cp option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp option. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this cp option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this cp option
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this cp option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp option
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
	 * Returns a map of the locales and localized names of this cp option.
	 *
	 * @return the locales and localized names of this cp option
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary key of this cp option.
	 *
	 * @return the primary key of this cp option
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the required of this cp option.
	 *
	 * @return the required of this cp option
	 */
	@Override
	public boolean getRequired() {
		return model.getRequired();
	}

	/**
	 * Returns the sku contributor of this cp option.
	 *
	 * @return the sku contributor of this cp option
	 */
	@Override
	public boolean getSkuContributor() {
		return model.getSkuContributor();
	}

	/**
	 * Returns the status of this cp option.
	 *
	 * @return the status of this cp option
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the user ID of this cp option.
	 *
	 * @return the user ID of this cp option
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp option.
	 *
	 * @return the user name of this cp option
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp option.
	 *
	 * @return the user uuid of this cp option
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp option.
	 *
	 * @return the uuid of this cp option
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this cp option is facetable.
	 *
	 * @return <code>true</code> if this cp option is facetable; <code>false</code> otherwise
	 */
	@Override
	public boolean isFacetable() {
		return model.isFacetable();
	}

	/**
	 * Returns <code>true</code> if this cp option is required.
	 *
	 * @return <code>true</code> if this cp option is required; <code>false</code> otherwise
	 */
	@Override
	public boolean isRequired() {
		return model.isRequired();
	}

	/**
	 * Returns <code>true</code> if this cp option is sku contributor.
	 *
	 * @return <code>true</code> if this cp option is sku contributor; <code>false</code> otherwise
	 */
	@Override
	public boolean isSkuContributor() {
		return model.isSkuContributor();
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
	 * Sets the commerce option type key of this cp option.
	 *
	 * @param commerceOptionTypeKey the commerce option type key of this cp option
	 */
	@Override
	public void setCommerceOptionTypeKey(String commerceOptionTypeKey) {
		model.setCommerceOptionTypeKey(commerceOptionTypeKey);
	}

	/**
	 * Sets the company ID of this cp option.
	 *
	 * @param companyId the company ID of this cp option
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp option ID of this cp option.
	 *
	 * @param CPOptionId the cp option ID of this cp option
	 */
	@Override
	public void setCPOptionId(long CPOptionId) {
		model.setCPOptionId(CPOptionId);
	}

	/**
	 * Sets the create date of this cp option.
	 *
	 * @param createDate the create date of this cp option
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp option.
	 *
	 * @param ctCollectionId the ct collection ID of this cp option
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this cp option.
	 *
	 * @param description the description of this cp option
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this cp option in the language.
	 *
	 * @param description the localized description of this cp option
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this cp option in the language, and sets the default locale.
	 *
	 * @param description the localized description of this cp option
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
	 * Sets the localized descriptions of this cp option from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this cp option
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this cp option from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this cp option
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this cp option.
	 *
	 * @param externalReferenceCode the external reference code of this cp option
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets whether this cp option is facetable.
	 *
	 * @param facetable the facetable of this cp option
	 */
	@Override
	public void setFacetable(boolean facetable) {
		model.setFacetable(facetable);
	}

	/**
	 * Sets the key of this cp option.
	 *
	 * @param key the key of this cp option
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the last publish date of this cp option.
	 *
	 * @param lastPublishDate the last publish date of this cp option
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this cp option.
	 *
	 * @param modifiedDate the modified date of this cp option
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cp option.
	 *
	 * @param mvccVersion the mvcc version of this cp option
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cp option.
	 *
	 * @param name the name of this cp option
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this cp option in the language.
	 *
	 * @param name the localized name of this cp option
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this cp option in the language, and sets the default locale.
	 *
	 * @param name the localized name of this cp option
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
	 * Sets the localized names of this cp option from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this cp option
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this cp option from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this cp option
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the primary key of this cp option.
	 *
	 * @param primaryKey the primary key of this cp option
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this cp option is required.
	 *
	 * @param required the required of this cp option
	 */
	@Override
	public void setRequired(boolean required) {
		model.setRequired(required);
	}

	/**
	 * Sets whether this cp option is sku contributor.
	 *
	 * @param skuContributor the sku contributor of this cp option
	 */
	@Override
	public void setSkuContributor(boolean skuContributor) {
		model.setSkuContributor(skuContributor);
	}

	/**
	 * Sets the status of this cp option.
	 *
	 * @param status the status of this cp option
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the user ID of this cp option.
	 *
	 * @param userId the user ID of this cp option
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp option.
	 *
	 * @param userName the user name of this cp option
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp option.
	 *
	 * @param userUuid the user uuid of this cp option
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp option.
	 *
	 * @param uuid the uuid of this cp option
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
	public Map<String, Function<CPOption, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPOption, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPOptionWrapper wrap(CPOption cpOption) {
		return new CPOptionWrapper(cpOption);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:888338024