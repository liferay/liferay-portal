/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CommerceAvailabilityEstimate}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimate
 * @generated
 */
public class CommerceAvailabilityEstimateWrapper
	extends BaseModelWrapper<CommerceAvailabilityEstimate>
	implements CommerceAvailabilityEstimate,
			   ModelWrapper<CommerceAvailabilityEstimate> {

	public CommerceAvailabilityEstimateWrapper(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		super(commerceAvailabilityEstimate);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"commerceAvailabilityEstimateId",
			getCommerceAvailabilityEstimateId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("title", getTitle());
		attributes.put("priority", getPriority());
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

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long commerceAvailabilityEstimateId = (Long)attributes.get(
			"commerceAvailabilityEstimateId");

		if (commerceAvailabilityEstimateId != null) {
			setCommerceAvailabilityEstimateId(commerceAvailabilityEstimateId);
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

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
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
	public CommerceAvailabilityEstimate cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the commerce availability estimate ID of this commerce availability estimate.
	 *
	 * @return the commerce availability estimate ID of this commerce availability estimate
	 */
	@Override
	public long getCommerceAvailabilityEstimateId() {
		return model.getCommerceAvailabilityEstimateId();
	}

	/**
	 * Returns the company ID of this commerce availability estimate.
	 *
	 * @return the company ID of this commerce availability estimate
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this commerce availability estimate.
	 *
	 * @return the create date of this commerce availability estimate
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
	 * Returns the external reference code of this commerce availability estimate.
	 *
	 * @return the external reference code of this commerce availability estimate
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the last publish date of this commerce availability estimate.
	 *
	 * @return the last publish date of this commerce availability estimate
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this commerce availability estimate.
	 *
	 * @return the modified date of this commerce availability estimate
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce availability estimate.
	 *
	 * @return the mvcc version of this commerce availability estimate
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this commerce availability estimate.
	 *
	 * @return the primary key of this commerce availability estimate
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this commerce availability estimate.
	 *
	 * @return the priority of this commerce availability estimate
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the status of this commerce availability estimate.
	 *
	 * @return the status of this commerce availability estimate
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the title of this commerce availability estimate.
	 *
	 * @return the title of this commerce availability estimate
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this commerce availability estimate in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this commerce availability estimate
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this commerce availability estimate in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this commerce availability estimate. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this commerce availability estimate in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this commerce availability estimate
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this commerce availability estimate in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this commerce availability estimate
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
	 * Returns a map of the locales and localized titles of this commerce availability estimate.
	 *
	 * @return the locales and localized titles of this commerce availability estimate
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the user ID of this commerce availability estimate.
	 *
	 * @return the user ID of this commerce availability estimate
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce availability estimate.
	 *
	 * @return the user name of this commerce availability estimate
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce availability estimate.
	 *
	 * @return the user uuid of this commerce availability estimate
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this commerce availability estimate.
	 *
	 * @return the uuid of this commerce availability estimate
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
	 * Sets the commerce availability estimate ID of this commerce availability estimate.
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID of this commerce availability estimate
	 */
	@Override
	public void setCommerceAvailabilityEstimateId(
		long commerceAvailabilityEstimateId) {

		model.setCommerceAvailabilityEstimateId(commerceAvailabilityEstimateId);
	}

	/**
	 * Sets the company ID of this commerce availability estimate.
	 *
	 * @param companyId the company ID of this commerce availability estimate
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this commerce availability estimate.
	 *
	 * @param createDate the create date of this commerce availability estimate
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this commerce availability estimate.
	 *
	 * @param externalReferenceCode the external reference code of this commerce availability estimate
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the last publish date of this commerce availability estimate.
	 *
	 * @param lastPublishDate the last publish date of this commerce availability estimate
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this commerce availability estimate.
	 *
	 * @param modifiedDate the modified date of this commerce availability estimate
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce availability estimate.
	 *
	 * @param mvccVersion the mvcc version of this commerce availability estimate
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this commerce availability estimate.
	 *
	 * @param primaryKey the primary key of this commerce availability estimate
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this commerce availability estimate.
	 *
	 * @param priority the priority of this commerce availability estimate
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the status of this commerce availability estimate.
	 *
	 * @param status the status of this commerce availability estimate
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the title of this commerce availability estimate.
	 *
	 * @param title the title of this commerce availability estimate
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this commerce availability estimate in the language.
	 *
	 * @param title the localized title of this commerce availability estimate
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this commerce availability estimate in the language, and sets the default locale.
	 *
	 * @param title the localized title of this commerce availability estimate
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
	 * Sets the localized titles of this commerce availability estimate from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this commerce availability estimate
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this commerce availability estimate from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this commerce availability estimate
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the user ID of this commerce availability estimate.
	 *
	 * @param userId the user ID of this commerce availability estimate
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce availability estimate.
	 *
	 * @param userName the user name of this commerce availability estimate
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce availability estimate.
	 *
	 * @param userUuid the user uuid of this commerce availability estimate
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this commerce availability estimate.
	 *
	 * @param uuid the uuid of this commerce availability estimate
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
	protected CommerceAvailabilityEstimateWrapper wrap(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		return new CommerceAvailabilityEstimateWrapper(
			commerceAvailabilityEstimate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1775784551