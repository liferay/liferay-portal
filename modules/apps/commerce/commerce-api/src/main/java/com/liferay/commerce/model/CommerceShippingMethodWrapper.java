/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CommerceShippingMethod}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingMethod
 * @generated
 */
public class CommerceShippingMethodWrapper
	extends BaseModelWrapper<CommerceShippingMethod>
	implements CommerceShippingMethod, ModelWrapper<CommerceShippingMethod> {

	public CommerceShippingMethodWrapper(
		CommerceShippingMethod commerceShippingMethod) {

		super(commerceShippingMethod);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"commerceShippingMethodId", getCommerceShippingMethodId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("active", isActive());
		attributes.put("engineKey", getEngineKey());
		attributes.put("imageId", getImageId());
		attributes.put("priority", getPriority());
		attributes.put("trackingURL", getTrackingURL());
		attributes.put("typeSettings", getTypeSettings());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long commerceShippingMethodId = (Long)attributes.get(
			"commerceShippingMethodId");

		if (commerceShippingMethodId != null) {
			setCommerceShippingMethodId(commerceShippingMethodId);
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

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}

		String engineKey = (String)attributes.get("engineKey");

		if (engineKey != null) {
			setEngineKey(engineKey);
		}

		Long imageId = (Long)attributes.get("imageId");

		if (imageId != null) {
			setImageId(imageId);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		String trackingURL = (String)attributes.get("trackingURL");

		if (trackingURL != null) {
			setTrackingURL(trackingURL);
		}

		String typeSettings = (String)attributes.get("typeSettings");

		if (typeSettings != null) {
			setTypeSettings(typeSettings);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public CommerceShippingMethod cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the active of this commerce shipping method.
	 *
	 * @return the active of this commerce shipping method
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
	 * Returns the commerce shipping method ID of this commerce shipping method.
	 *
	 * @return the commerce shipping method ID of this commerce shipping method
	 */
	@Override
	public long getCommerceShippingMethodId() {
		return model.getCommerceShippingMethodId();
	}

	/**
	 * Returns the company ID of this commerce shipping method.
	 *
	 * @return the company ID of this commerce shipping method
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this commerce shipping method.
	 *
	 * @return the create date of this commerce shipping method
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
	 * Returns the description of this commerce shipping method.
	 *
	 * @return the description of this commerce shipping method
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this commerce shipping method in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this commerce shipping method
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this commerce shipping method in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this commerce shipping method. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this commerce shipping method in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this commerce shipping method
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this commerce shipping method in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this commerce shipping method
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
	 * Returns a map of the locales and localized descriptions of this commerce shipping method.
	 *
	 * @return the locales and localized descriptions of this commerce shipping method
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the engine key of this commerce shipping method.
	 *
	 * @return the engine key of this commerce shipping method
	 */
	@Override
	public String getEngineKey() {
		return model.getEngineKey();
	}

	/**
	 * Returns the external reference code of this commerce shipping method.
	 *
	 * @return the external reference code of this commerce shipping method
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this commerce shipping method.
	 *
	 * @return the group ID of this commerce shipping method
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the image ID of this commerce shipping method.
	 *
	 * @return the image ID of this commerce shipping method
	 */
	@Override
	public long getImageId() {
		return model.getImageId();
	}

	@Override
	public String getImageURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getImageURL(themeDisplay);
	}

	/**
	 * Returns the modified date of this commerce shipping method.
	 *
	 * @return the modified date of this commerce shipping method
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce shipping method.
	 *
	 * @return the mvcc version of this commerce shipping method
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce shipping method.
	 *
	 * @return the name of this commerce shipping method
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this commerce shipping method in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this commerce shipping method
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this commerce shipping method in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce shipping method. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this commerce shipping method in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this commerce shipping method
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this commerce shipping method in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce shipping method
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
	 * Returns a map of the locales and localized names of this commerce shipping method.
	 *
	 * @return the locales and localized names of this commerce shipping method
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary key of this commerce shipping method.
	 *
	 * @return the primary key of this commerce shipping method
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this commerce shipping method.
	 *
	 * @return the priority of this commerce shipping method
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the status of this commerce shipping method.
	 *
	 * @return the status of this commerce shipping method
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the tracking url of this commerce shipping method.
	 *
	 * @return the tracking url of this commerce shipping method
	 */
	@Override
	public String getTrackingURL() {
		return model.getTrackingURL();
	}

	/**
	 * Returns the type settings of this commerce shipping method.
	 *
	 * @return the type settings of this commerce shipping method
	 */
	@Override
	public String getTypeSettings() {
		return model.getTypeSettings();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getTypeSettingsUnicodeProperties() {

		return model.getTypeSettingsUnicodeProperties();
	}

	/**
	 * Returns the user ID of this commerce shipping method.
	 *
	 * @return the user ID of this commerce shipping method
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce shipping method.
	 *
	 * @return the user name of this commerce shipping method
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce shipping method.
	 *
	 * @return the user uuid of this commerce shipping method
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this commerce shipping method is active.
	 *
	 * @return <code>true</code> if this commerce shipping method is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
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
	 * Sets whether this commerce shipping method is active.
	 *
	 * @param active the active of this commerce shipping method
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the commerce shipping method ID of this commerce shipping method.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID of this commerce shipping method
	 */
	@Override
	public void setCommerceShippingMethodId(long commerceShippingMethodId) {
		model.setCommerceShippingMethodId(commerceShippingMethodId);
	}

	/**
	 * Sets the company ID of this commerce shipping method.
	 *
	 * @param companyId the company ID of this commerce shipping method
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this commerce shipping method.
	 *
	 * @param createDate the create date of this commerce shipping method
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this commerce shipping method.
	 *
	 * @param description the description of this commerce shipping method
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this commerce shipping method in the language.
	 *
	 * @param description the localized description of this commerce shipping method
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this commerce shipping method in the language, and sets the default locale.
	 *
	 * @param description the localized description of this commerce shipping method
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
	 * Sets the localized descriptions of this commerce shipping method from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this commerce shipping method
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this commerce shipping method from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this commerce shipping method
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the engine key of this commerce shipping method.
	 *
	 * @param engineKey the engine key of this commerce shipping method
	 */
	@Override
	public void setEngineKey(String engineKey) {
		model.setEngineKey(engineKey);
	}

	/**
	 * Sets the external reference code of this commerce shipping method.
	 *
	 * @param externalReferenceCode the external reference code of this commerce shipping method
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this commerce shipping method.
	 *
	 * @param groupId the group ID of this commerce shipping method
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the image ID of this commerce shipping method.
	 *
	 * @param imageId the image ID of this commerce shipping method
	 */
	@Override
	public void setImageId(long imageId) {
		model.setImageId(imageId);
	}

	/**
	 * Sets the modified date of this commerce shipping method.
	 *
	 * @param modifiedDate the modified date of this commerce shipping method
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce shipping method.
	 *
	 * @param mvccVersion the mvcc version of this commerce shipping method
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce shipping method.
	 *
	 * @param name the name of this commerce shipping method
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this commerce shipping method in the language.
	 *
	 * @param name the localized name of this commerce shipping method
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this commerce shipping method in the language, and sets the default locale.
	 *
	 * @param name the localized name of this commerce shipping method
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
	 * Sets the localized names of this commerce shipping method from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this commerce shipping method
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this commerce shipping method from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this commerce shipping method
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the primary key of this commerce shipping method.
	 *
	 * @param primaryKey the primary key of this commerce shipping method
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this commerce shipping method.
	 *
	 * @param priority the priority of this commerce shipping method
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the status of this commerce shipping method.
	 *
	 * @param status the status of this commerce shipping method
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the tracking url of this commerce shipping method.
	 *
	 * @param trackingURL the tracking url of this commerce shipping method
	 */
	@Override
	public void setTrackingURL(String trackingURL) {
		model.setTrackingURL(trackingURL);
	}

	/**
	 * Sets the type settings of this commerce shipping method.
	 *
	 * @param typeSettings the type settings of this commerce shipping method
	 */
	@Override
	public void setTypeSettings(String typeSettings) {
		model.setTypeSettings(typeSettings);
	}

	@Override
	public void setTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			typeSettingsUnicodeProperties) {

		model.setTypeSettingsUnicodeProperties(typeSettingsUnicodeProperties);
	}

	/**
	 * Sets the user ID of this commerce shipping method.
	 *
	 * @param userId the user ID of this commerce shipping method
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce shipping method.
	 *
	 * @param userName the user name of this commerce shipping method
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce shipping method.
	 *
	 * @param userUuid the user uuid of this commerce shipping method
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected CommerceShippingMethodWrapper wrap(
		CommerceShippingMethod commerceShippingMethod) {

		return new CommerceShippingMethodWrapper(commerceShippingMethod);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1681284770