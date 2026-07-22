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
 * This class is a wrapper for {@link CPSpecificationOption}.
 * </p>
 *
 * @author Marco Leo
 * @see CPSpecificationOption
 * @generated
 */
public class CPSpecificationOptionWrapper
	extends BaseModelWrapper<CPSpecificationOption>
	implements CPSpecificationOption, ModelWrapper<CPSpecificationOption> {

	public CPSpecificationOptionWrapper(
		CPSpecificationOption cpSpecificationOption) {

		super(cpSpecificationOption);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("CPSpecificationOptionId", getCPSpecificationOptionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("CPOptionCategoryId", getCPOptionCategoryId());
		attributes.put("title", getTitle());
		attributes.put("description", getDescription());
		attributes.put("facetable", isFacetable());
		attributes.put("key", getKey());
		attributes.put("priority", getPriority());
		attributes.put("visible", isVisible());
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

		Long CPSpecificationOptionId = (Long)attributes.get(
			"CPSpecificationOptionId");

		if (CPSpecificationOptionId != null) {
			setCPSpecificationOptionId(CPSpecificationOptionId);
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

		Long CPOptionCategoryId = (Long)attributes.get("CPOptionCategoryId");

		if (CPOptionCategoryId != null) {
			setCPOptionCategoryId(CPOptionCategoryId);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Boolean facetable = (Boolean)attributes.get("facetable");

		if (facetable != null) {
			setFacetable(facetable);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		Boolean visible = (Boolean)attributes.get("visible");

		if (visible != null) {
			setVisible(visible);
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
	public CPSpecificationOption cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this cp specification option.
	 *
	 * @return the company ID of this cp specification option
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public CPOptionCategory getCPOptionCategory()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPOptionCategory();
	}

	/**
	 * Returns the cp option category ID of this cp specification option.
	 *
	 * @return the cp option category ID of this cp specification option
	 */
	@Override
	public long getCPOptionCategoryId() {
		return model.getCPOptionCategoryId();
	}

	/**
	 * Returns the cp specification option ID of this cp specification option.
	 *
	 * @return the cp specification option ID of this cp specification option
	 */
	@Override
	public long getCPSpecificationOptionId() {
		return model.getCPSpecificationOptionId();
	}

	/**
	 * Returns the create date of this cp specification option.
	 *
	 * @return the create date of this cp specification option
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp specification option.
	 *
	 * @return the ct collection ID of this cp specification option
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
	 * Returns the description of this cp specification option.
	 *
	 * @return the description of this cp specification option
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this cp specification option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this cp specification option
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this cp specification option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this cp specification option. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this cp specification option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this cp specification option
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this cp specification option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this cp specification option
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
	 * Returns a map of the locales and localized descriptions of this cp specification option.
	 *
	 * @return the locales and localized descriptions of this cp specification option
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the external reference code of this cp specification option.
	 *
	 * @return the external reference code of this cp specification option
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the facetable of this cp specification option.
	 *
	 * @return the facetable of this cp specification option
	 */
	@Override
	public boolean getFacetable() {
		return model.getFacetable();
	}

	/**
	 * Returns the key of this cp specification option.
	 *
	 * @return the key of this cp specification option
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the last publish date of this cp specification option.
	 *
	 * @return the last publish date of this cp specification option
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeDefinition>
			getListTypeDefinitions()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getListTypeDefinitions();
	}

	@Override
	public long getListTypeDefinitionsCount()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getListTypeDefinitionsCount();
	}

	/**
	 * Returns the modified date of this cp specification option.
	 *
	 * @return the modified date of this cp specification option
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cp specification option.
	 *
	 * @return the mvcc version of this cp specification option
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this cp specification option.
	 *
	 * @return the primary key of this cp specification option
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this cp specification option.
	 *
	 * @return the priority of this cp specification option
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the status of this cp specification option.
	 *
	 * @return the status of this cp specification option
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the title of this cp specification option.
	 *
	 * @return the title of this cp specification option
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this cp specification option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this cp specification option
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this cp specification option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this cp specification option. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this cp specification option in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this cp specification option
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this cp specification option in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this cp specification option
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
	 * Returns a map of the locales and localized titles of this cp specification option.
	 *
	 * @return the locales and localized titles of this cp specification option
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the user ID of this cp specification option.
	 *
	 * @return the user ID of this cp specification option
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp specification option.
	 *
	 * @return the user name of this cp specification option
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp specification option.
	 *
	 * @return the user uuid of this cp specification option
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp specification option.
	 *
	 * @return the uuid of this cp specification option
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the visible of this cp specification option.
	 *
	 * @return the visible of this cp specification option
	 */
	@Override
	public boolean getVisible() {
		return model.getVisible();
	}

	/**
	 * Returns <code>true</code> if this cp specification option is facetable.
	 *
	 * @return <code>true</code> if this cp specification option is facetable; <code>false</code> otherwise
	 */
	@Override
	public boolean isFacetable() {
		return model.isFacetable();
	}

	/**
	 * Returns <code>true</code> if this cp specification option is visible.
	 *
	 * @return <code>true</code> if this cp specification option is visible; <code>false</code> otherwise
	 */
	@Override
	public boolean isVisible() {
		return model.isVisible();
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
	 * Sets the company ID of this cp specification option.
	 *
	 * @param companyId the company ID of this cp specification option
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp option category ID of this cp specification option.
	 *
	 * @param CPOptionCategoryId the cp option category ID of this cp specification option
	 */
	@Override
	public void setCPOptionCategoryId(long CPOptionCategoryId) {
		model.setCPOptionCategoryId(CPOptionCategoryId);
	}

	/**
	 * Sets the cp specification option ID of this cp specification option.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID of this cp specification option
	 */
	@Override
	public void setCPSpecificationOptionId(long CPSpecificationOptionId) {
		model.setCPSpecificationOptionId(CPSpecificationOptionId);
	}

	/**
	 * Sets the create date of this cp specification option.
	 *
	 * @param createDate the create date of this cp specification option
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp specification option.
	 *
	 * @param ctCollectionId the ct collection ID of this cp specification option
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this cp specification option.
	 *
	 * @param description the description of this cp specification option
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this cp specification option in the language.
	 *
	 * @param description the localized description of this cp specification option
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this cp specification option in the language, and sets the default locale.
	 *
	 * @param description the localized description of this cp specification option
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
	 * Sets the localized descriptions of this cp specification option from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this cp specification option
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this cp specification option from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this cp specification option
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this cp specification option.
	 *
	 * @param externalReferenceCode the external reference code of this cp specification option
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets whether this cp specification option is facetable.
	 *
	 * @param facetable the facetable of this cp specification option
	 */
	@Override
	public void setFacetable(boolean facetable) {
		model.setFacetable(facetable);
	}

	/**
	 * Sets the key of this cp specification option.
	 *
	 * @param key the key of this cp specification option
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the last publish date of this cp specification option.
	 *
	 * @param lastPublishDate the last publish date of this cp specification option
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this cp specification option.
	 *
	 * @param modifiedDate the modified date of this cp specification option
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cp specification option.
	 *
	 * @param mvccVersion the mvcc version of this cp specification option
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this cp specification option.
	 *
	 * @param primaryKey the primary key of this cp specification option
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this cp specification option.
	 *
	 * @param priority the priority of this cp specification option
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the status of this cp specification option.
	 *
	 * @param status the status of this cp specification option
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the title of this cp specification option.
	 *
	 * @param title the title of this cp specification option
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this cp specification option in the language.
	 *
	 * @param title the localized title of this cp specification option
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this cp specification option in the language, and sets the default locale.
	 *
	 * @param title the localized title of this cp specification option
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
	 * Sets the localized titles of this cp specification option from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this cp specification option
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this cp specification option from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this cp specification option
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the user ID of this cp specification option.
	 *
	 * @param userId the user ID of this cp specification option
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp specification option.
	 *
	 * @param userName the user name of this cp specification option
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp specification option.
	 *
	 * @param userUuid the user uuid of this cp specification option
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp specification option.
	 *
	 * @param uuid the uuid of this cp specification option
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets whether this cp specification option is visible.
	 *
	 * @param visible the visible of this cp specification option
	 */
	@Override
	public void setVisible(boolean visible) {
		model.setVisible(visible);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CPSpecificationOption, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPSpecificationOption, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPSpecificationOptionWrapper wrap(
		CPSpecificationOption cpSpecificationOption) {

		return new CPSpecificationOptionWrapper(cpSpecificationOption);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-215186867