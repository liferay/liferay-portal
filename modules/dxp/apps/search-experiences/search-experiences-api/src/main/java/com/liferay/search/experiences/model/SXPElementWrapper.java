/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link SXPElement}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SXPElement
 * @generated
 */
public class SXPElementWrapper
	extends BaseModelWrapper<SXPElement>
	implements ModelWrapper<SXPElement>, SXPElement {

	public SXPElementWrapper(SXPElement sxpElement) {
		super(sxpElement);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("sxpElementId", getSXPElementId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("compatibility", getCompatibility());
		attributes.put("description", getDescription());
		attributes.put("elementDefinitionJSON", getElementDefinitionJSON());
		attributes.put("fallbackDescription", getFallbackDescription());
		attributes.put("fallbackTitle", getFallbackTitle());
		attributes.put("hidden", isHidden());
		attributes.put("readOnly", isReadOnly());
		attributes.put("schemaVersion", getSchemaVersion());
		attributes.put("title", getTitle());
		attributes.put("type", getType());
		attributes.put("version", getVersion());

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

		Long sxpElementId = (Long)attributes.get("sxpElementId");

		if (sxpElementId != null) {
			setSXPElementId(sxpElementId);
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

		Integer compatibility = (Integer)attributes.get("compatibility");

		if (compatibility != null) {
			setCompatibility(compatibility);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String elementDefinitionJSON = (String)attributes.get(
			"elementDefinitionJSON");

		if (elementDefinitionJSON != null) {
			setElementDefinitionJSON(elementDefinitionJSON);
		}

		String fallbackDescription = (String)attributes.get(
			"fallbackDescription");

		if (fallbackDescription != null) {
			setFallbackDescription(fallbackDescription);
		}

		String fallbackTitle = (String)attributes.get("fallbackTitle");

		if (fallbackTitle != null) {
			setFallbackTitle(fallbackTitle);
		}

		Boolean hidden = (Boolean)attributes.get("hidden");

		if (hidden != null) {
			setHidden(hidden);
		}

		Boolean readOnly = (Boolean)attributes.get("readOnly");

		if (readOnly != null) {
			setReadOnly(readOnly);
		}

		String schemaVersion = (String)attributes.get("schemaVersion");

		if (schemaVersion != null) {
			setSchemaVersion(schemaVersion);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String version = (String)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}
	}

	@Override
	public SXPElement cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this sxp element.
	 *
	 * @return the company ID of this sxp element
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the compatibility of this sxp element.
	 *
	 * @return the compatibility of this sxp element
	 */
	@Override
	public int getCompatibility() {
		return model.getCompatibility();
	}

	/**
	 * Returns the create date of this sxp element.
	 *
	 * @return the create date of this sxp element
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
	 * Returns the description of this sxp element.
	 *
	 * @return the description of this sxp element
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this sxp element in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this sxp element
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this sxp element in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this sxp element. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this sxp element in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this sxp element
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this sxp element in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this sxp element
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
	 * Returns a map of the locales and localized descriptions of this sxp element.
	 *
	 * @return the locales and localized descriptions of this sxp element
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the element definition json of this sxp element.
	 *
	 * @return the element definition json of this sxp element
	 */
	@Override
	public String getElementDefinitionJSON() {
		return model.getElementDefinitionJSON();
	}

	/**
	 * Returns the external reference code of this sxp element.
	 *
	 * @return the external reference code of this sxp element
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the fallback description of this sxp element.
	 *
	 * @return the fallback description of this sxp element
	 */
	@Override
	public String getFallbackDescription() {
		return model.getFallbackDescription();
	}

	/**
	 * Returns the fallback title of this sxp element.
	 *
	 * @return the fallback title of this sxp element
	 */
	@Override
	public String getFallbackTitle() {
		return model.getFallbackTitle();
	}

	/**
	 * Returns the hidden of this sxp element.
	 *
	 * @return the hidden of this sxp element
	 */
	@Override
	public boolean getHidden() {
		return model.getHidden();
	}

	/**
	 * Returns the modified date of this sxp element.
	 *
	 * @return the modified date of this sxp element
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this sxp element.
	 *
	 * @return the mvcc version of this sxp element
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this sxp element.
	 *
	 * @return the primary key of this sxp element
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the read only of this sxp element.
	 *
	 * @return the read only of this sxp element
	 */
	@Override
	public boolean getReadOnly() {
		return model.getReadOnly();
	}

	/**
	 * Returns the schema version of this sxp element.
	 *
	 * @return the schema version of this sxp element
	 */
	@Override
	public String getSchemaVersion() {
		return model.getSchemaVersion();
	}

	/**
	 * Returns the sxp element ID of this sxp element.
	 *
	 * @return the sxp element ID of this sxp element
	 */
	@Override
	public long getSXPElementId() {
		return model.getSXPElementId();
	}

	/**
	 * Returns the title of this sxp element.
	 *
	 * @return the title of this sxp element
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this sxp element in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this sxp element
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this sxp element in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this sxp element. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this sxp element in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this sxp element
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this sxp element in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this sxp element
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
	 * Returns a map of the locales and localized titles of this sxp element.
	 *
	 * @return the locales and localized titles of this sxp element
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the type of this sxp element.
	 *
	 * @return the type of this sxp element
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this sxp element.
	 *
	 * @return the user ID of this sxp element
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this sxp element.
	 *
	 * @return the user name of this sxp element
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this sxp element.
	 *
	 * @return the user uuid of this sxp element
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this sxp element.
	 *
	 * @return the uuid of this sxp element
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this sxp element.
	 *
	 * @return the version of this sxp element
	 */
	@Override
	public String getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns <code>true</code> if this sxp element is hidden.
	 *
	 * @return <code>true</code> if this sxp element is hidden; <code>false</code> otherwise
	 */
	@Override
	public boolean isHidden() {
		return model.isHidden();
	}

	/**
	 * Returns <code>true</code> if this sxp element is read only.
	 *
	 * @return <code>true</code> if this sxp element is read only; <code>false</code> otherwise
	 */
	@Override
	public boolean isReadOnly() {
		return model.isReadOnly();
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
	 * Sets the company ID of this sxp element.
	 *
	 * @param companyId the company ID of this sxp element
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the compatibility of this sxp element.
	 *
	 * @param compatibility the compatibility of this sxp element
	 */
	@Override
	public void setCompatibility(int compatibility) {
		model.setCompatibility(compatibility);
	}

	/**
	 * Sets the create date of this sxp element.
	 *
	 * @param createDate the create date of this sxp element
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this sxp element.
	 *
	 * @param description the description of this sxp element
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this sxp element in the language.
	 *
	 * @param description the localized description of this sxp element
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this sxp element in the language, and sets the default locale.
	 *
	 * @param description the localized description of this sxp element
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
	 * Sets the localized descriptions of this sxp element from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this sxp element
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this sxp element from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this sxp element
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the element definition json of this sxp element.
	 *
	 * @param elementDefinitionJSON the element definition json of this sxp element
	 */
	@Override
	public void setElementDefinitionJSON(String elementDefinitionJSON) {
		model.setElementDefinitionJSON(elementDefinitionJSON);
	}

	/**
	 * Sets the external reference code of this sxp element.
	 *
	 * @param externalReferenceCode the external reference code of this sxp element
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the fallback description of this sxp element.
	 *
	 * @param fallbackDescription the fallback description of this sxp element
	 */
	@Override
	public void setFallbackDescription(String fallbackDescription) {
		model.setFallbackDescription(fallbackDescription);
	}

	/**
	 * Sets the fallback title of this sxp element.
	 *
	 * @param fallbackTitle the fallback title of this sxp element
	 */
	@Override
	public void setFallbackTitle(String fallbackTitle) {
		model.setFallbackTitle(fallbackTitle);
	}

	/**
	 * Sets whether this sxp element is hidden.
	 *
	 * @param hidden the hidden of this sxp element
	 */
	@Override
	public void setHidden(boolean hidden) {
		model.setHidden(hidden);
	}

	/**
	 * Sets the modified date of this sxp element.
	 *
	 * @param modifiedDate the modified date of this sxp element
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this sxp element.
	 *
	 * @param mvccVersion the mvcc version of this sxp element
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this sxp element.
	 *
	 * @param primaryKey the primary key of this sxp element
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this sxp element is read only.
	 *
	 * @param readOnly the read only of this sxp element
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		model.setReadOnly(readOnly);
	}

	/**
	 * Sets the schema version of this sxp element.
	 *
	 * @param schemaVersion the schema version of this sxp element
	 */
	@Override
	public void setSchemaVersion(String schemaVersion) {
		model.setSchemaVersion(schemaVersion);
	}

	/**
	 * Sets the sxp element ID of this sxp element.
	 *
	 * @param sxpElementId the sxp element ID of this sxp element
	 */
	@Override
	public void setSXPElementId(long sxpElementId) {
		model.setSXPElementId(sxpElementId);
	}

	/**
	 * Sets the title of this sxp element.
	 *
	 * @param title the title of this sxp element
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this sxp element in the language.
	 *
	 * @param title the localized title of this sxp element
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this sxp element in the language, and sets the default locale.
	 *
	 * @param title the localized title of this sxp element
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
	 * Sets the localized titles of this sxp element from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this sxp element
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this sxp element from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this sxp element
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the type of this sxp element.
	 *
	 * @param type the type of this sxp element
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this sxp element.
	 *
	 * @param userId the user ID of this sxp element
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this sxp element.
	 *
	 * @param userName the user name of this sxp element
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this sxp element.
	 *
	 * @param userUuid the user uuid of this sxp element
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this sxp element.
	 *
	 * @param uuid the uuid of this sxp element
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this sxp element.
	 *
	 * @param version the version of this sxp element
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
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected SXPElementWrapper wrap(SXPElement sxpElement) {
		return new SXPElementWrapper(sxpElement);
	}

}