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
 * This class is a wrapper for {@link SXPBlueprint}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SXPBlueprint
 * @generated
 */
public class SXPBlueprintWrapper
	extends BaseModelWrapper<SXPBlueprint>
	implements ModelWrapper<SXPBlueprint>, SXPBlueprint {

	public SXPBlueprintWrapper(SXPBlueprint sxpBlueprint) {
		super(sxpBlueprint);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("sxpBlueprintId", getSXPBlueprintId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("collectionProvider", isCollectionProvider());
		attributes.put("compatibility", getCompatibility());
		attributes.put("configurationJSON", getConfigurationJSON());
		attributes.put("description", getDescription());
		attributes.put("elementInstancesJSON", getElementInstancesJSON());
		attributes.put("fallbackDescription", getFallbackDescription());
		attributes.put("fallbackTitle", getFallbackTitle());
		attributes.put("schemaVersion", getSchemaVersion());
		attributes.put("title", getTitle());
		attributes.put("version", getVersion());
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

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long sxpBlueprintId = (Long)attributes.get("sxpBlueprintId");

		if (sxpBlueprintId != null) {
			setSXPBlueprintId(sxpBlueprintId);
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

		Boolean collectionProvider = (Boolean)attributes.get(
			"collectionProvider");

		if (collectionProvider != null) {
			setCollectionProvider(collectionProvider);
		}

		Integer compatibility = (Integer)attributes.get("compatibility");

		if (compatibility != null) {
			setCompatibility(compatibility);
		}

		String configurationJSON = (String)attributes.get("configurationJSON");

		if (configurationJSON != null) {
			setConfigurationJSON(configurationJSON);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String elementInstancesJSON = (String)attributes.get(
			"elementInstancesJSON");

		if (elementInstancesJSON != null) {
			setElementInstancesJSON(elementInstancesJSON);
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

		String schemaVersion = (String)attributes.get("schemaVersion");

		if (schemaVersion != null) {
			setSchemaVersion(schemaVersion);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		String version = (String)attributes.get("version");

		if (version != null) {
			setVersion(version);
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
	public SXPBlueprint cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the collection provider of this sxp blueprint.
	 *
	 * @return the collection provider of this sxp blueprint
	 */
	@Override
	public boolean getCollectionProvider() {
		return model.getCollectionProvider();
	}

	/**
	 * Returns the company ID of this sxp blueprint.
	 *
	 * @return the company ID of this sxp blueprint
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the compatibility of this sxp blueprint.
	 *
	 * @return the compatibility of this sxp blueprint
	 */
	@Override
	public int getCompatibility() {
		return model.getCompatibility();
	}

	/**
	 * Returns the configuration json of this sxp blueprint.
	 *
	 * @return the configuration json of this sxp blueprint
	 */
	@Override
	public String getConfigurationJSON() {
		return model.getConfigurationJSON();
	}

	/**
	 * Returns the create date of this sxp blueprint.
	 *
	 * @return the create date of this sxp blueprint
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
	 * Returns the description of this sxp blueprint.
	 *
	 * @return the description of this sxp blueprint
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this sxp blueprint in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this sxp blueprint
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this sxp blueprint in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this sxp blueprint. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this sxp blueprint in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this sxp blueprint
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this sxp blueprint in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this sxp blueprint
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
	 * Returns a map of the locales and localized descriptions of this sxp blueprint.
	 *
	 * @return the locales and localized descriptions of this sxp blueprint
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	/**
	 * Returns the element instances json of this sxp blueprint.
	 *
	 * @return the element instances json of this sxp blueprint
	 */
	@Override
	public String getElementInstancesJSON() {
		return model.getElementInstancesJSON();
	}

	/**
	 * Returns the external reference code of this sxp blueprint.
	 *
	 * @return the external reference code of this sxp blueprint
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the fallback description of this sxp blueprint.
	 *
	 * @return the fallback description of this sxp blueprint
	 */
	@Override
	public String getFallbackDescription() {
		return model.getFallbackDescription();
	}

	/**
	 * Returns the fallback title of this sxp blueprint.
	 *
	 * @return the fallback title of this sxp blueprint
	 */
	@Override
	public String getFallbackTitle() {
		return model.getFallbackTitle();
	}

	/**
	 * Returns the modified date of this sxp blueprint.
	 *
	 * @return the modified date of this sxp blueprint
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this sxp blueprint.
	 *
	 * @return the mvcc version of this sxp blueprint
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this sxp blueprint.
	 *
	 * @return the primary key of this sxp blueprint
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the schema version of this sxp blueprint.
	 *
	 * @return the schema version of this sxp blueprint
	 */
	@Override
	public String getSchemaVersion() {
		return model.getSchemaVersion();
	}

	/**
	 * Returns the status of this sxp blueprint.
	 *
	 * @return the status of this sxp blueprint
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this sxp blueprint.
	 *
	 * @return the status by user ID of this sxp blueprint
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this sxp blueprint.
	 *
	 * @return the status by user name of this sxp blueprint
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this sxp blueprint.
	 *
	 * @return the status by user uuid of this sxp blueprint
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this sxp blueprint.
	 *
	 * @return the status date of this sxp blueprint
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the sxp blueprint ID of this sxp blueprint.
	 *
	 * @return the sxp blueprint ID of this sxp blueprint
	 */
	@Override
	public long getSXPBlueprintId() {
		return model.getSXPBlueprintId();
	}

	/**
	 * Returns the title of this sxp blueprint.
	 *
	 * @return the title of this sxp blueprint
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this sxp blueprint in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this sxp blueprint
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this sxp blueprint in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this sxp blueprint. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this sxp blueprint in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this sxp blueprint
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this sxp blueprint in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this sxp blueprint
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
	 * Returns a map of the locales and localized titles of this sxp blueprint.
	 *
	 * @return the locales and localized titles of this sxp blueprint
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the user ID of this sxp blueprint.
	 *
	 * @return the user ID of this sxp blueprint
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this sxp blueprint.
	 *
	 * @return the user name of this sxp blueprint
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this sxp blueprint.
	 *
	 * @return the user uuid of this sxp blueprint
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this sxp blueprint.
	 *
	 * @return the uuid of this sxp blueprint
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this sxp blueprint.
	 *
	 * @return the version of this sxp blueprint
	 */
	@Override
	public String getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is approved.
	 *
	 * @return <code>true</code> if this sxp blueprint is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is collection provider.
	 *
	 * @return <code>true</code> if this sxp blueprint is collection provider; <code>false</code> otherwise
	 */
	@Override
	public boolean isCollectionProvider() {
		return model.isCollectionProvider();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is denied.
	 *
	 * @return <code>true</code> if this sxp blueprint is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is a draft.
	 *
	 * @return <code>true</code> if this sxp blueprint is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is expired.
	 *
	 * @return <code>true</code> if this sxp blueprint is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is inactive.
	 *
	 * @return <code>true</code> if this sxp blueprint is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is incomplete.
	 *
	 * @return <code>true</code> if this sxp blueprint is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is pending.
	 *
	 * @return <code>true</code> if this sxp blueprint is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this sxp blueprint is scheduled.
	 *
	 * @return <code>true</code> if this sxp blueprint is scheduled; <code>false</code> otherwise
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
	 * Sets whether this sxp blueprint is collection provider.
	 *
	 * @param collectionProvider the collection provider of this sxp blueprint
	 */
	@Override
	public void setCollectionProvider(boolean collectionProvider) {
		model.setCollectionProvider(collectionProvider);
	}

	/**
	 * Sets the company ID of this sxp blueprint.
	 *
	 * @param companyId the company ID of this sxp blueprint
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the compatibility of this sxp blueprint.
	 *
	 * @param compatibility the compatibility of this sxp blueprint
	 */
	@Override
	public void setCompatibility(int compatibility) {
		model.setCompatibility(compatibility);
	}

	/**
	 * Sets the configuration json of this sxp blueprint.
	 *
	 * @param configurationJSON the configuration json of this sxp blueprint
	 */
	@Override
	public void setConfigurationJSON(String configurationJSON) {
		model.setConfigurationJSON(configurationJSON);
	}

	/**
	 * Sets the create date of this sxp blueprint.
	 *
	 * @param createDate the create date of this sxp blueprint
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this sxp blueprint.
	 *
	 * @param description the description of this sxp blueprint
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this sxp blueprint in the language.
	 *
	 * @param description the localized description of this sxp blueprint
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this sxp blueprint in the language, and sets the default locale.
	 *
	 * @param description the localized description of this sxp blueprint
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
	 * Sets the localized descriptions of this sxp blueprint from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this sxp blueprint
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this sxp blueprint from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this sxp blueprint
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the element instances json of this sxp blueprint.
	 *
	 * @param elementInstancesJSON the element instances json of this sxp blueprint
	 */
	@Override
	public void setElementInstancesJSON(String elementInstancesJSON) {
		model.setElementInstancesJSON(elementInstancesJSON);
	}

	/**
	 * Sets the external reference code of this sxp blueprint.
	 *
	 * @param externalReferenceCode the external reference code of this sxp blueprint
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the fallback description of this sxp blueprint.
	 *
	 * @param fallbackDescription the fallback description of this sxp blueprint
	 */
	@Override
	public void setFallbackDescription(String fallbackDescription) {
		model.setFallbackDescription(fallbackDescription);
	}

	/**
	 * Sets the fallback title of this sxp blueprint.
	 *
	 * @param fallbackTitle the fallback title of this sxp blueprint
	 */
	@Override
	public void setFallbackTitle(String fallbackTitle) {
		model.setFallbackTitle(fallbackTitle);
	}

	/**
	 * Sets the modified date of this sxp blueprint.
	 *
	 * @param modifiedDate the modified date of this sxp blueprint
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this sxp blueprint.
	 *
	 * @param mvccVersion the mvcc version of this sxp blueprint
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this sxp blueprint.
	 *
	 * @param primaryKey the primary key of this sxp blueprint
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the schema version of this sxp blueprint.
	 *
	 * @param schemaVersion the schema version of this sxp blueprint
	 */
	@Override
	public void setSchemaVersion(String schemaVersion) {
		model.setSchemaVersion(schemaVersion);
	}

	/**
	 * Sets the status of this sxp blueprint.
	 *
	 * @param status the status of this sxp blueprint
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this sxp blueprint.
	 *
	 * @param statusByUserId the status by user ID of this sxp blueprint
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this sxp blueprint.
	 *
	 * @param statusByUserName the status by user name of this sxp blueprint
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this sxp blueprint.
	 *
	 * @param statusByUserUuid the status by user uuid of this sxp blueprint
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this sxp blueprint.
	 *
	 * @param statusDate the status date of this sxp blueprint
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the sxp blueprint ID of this sxp blueprint.
	 *
	 * @param sxpBlueprintId the sxp blueprint ID of this sxp blueprint
	 */
	@Override
	public void setSXPBlueprintId(long sxpBlueprintId) {
		model.setSXPBlueprintId(sxpBlueprintId);
	}

	/**
	 * Sets the title of this sxp blueprint.
	 *
	 * @param title the title of this sxp blueprint
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this sxp blueprint in the language.
	 *
	 * @param title the localized title of this sxp blueprint
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this sxp blueprint in the language, and sets the default locale.
	 *
	 * @param title the localized title of this sxp blueprint
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
	 * Sets the localized titles of this sxp blueprint from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this sxp blueprint
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this sxp blueprint from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this sxp blueprint
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the user ID of this sxp blueprint.
	 *
	 * @param userId the user ID of this sxp blueprint
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this sxp blueprint.
	 *
	 * @param userName the user name of this sxp blueprint
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this sxp blueprint.
	 *
	 * @param userUuid the user uuid of this sxp blueprint
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this sxp blueprint.
	 *
	 * @param uuid the uuid of this sxp blueprint
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this sxp blueprint.
	 *
	 * @param version the version of this sxp blueprint
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
	protected SXPBlueprintWrapper wrap(SXPBlueprint sxpBlueprint) {
		return new SXPBlueprintWrapper(sxpBlueprint);
	}

}