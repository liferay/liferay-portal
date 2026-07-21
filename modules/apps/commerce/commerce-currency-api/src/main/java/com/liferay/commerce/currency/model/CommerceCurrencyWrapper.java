/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CommerceCurrency}.
 * </p>
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrency
 * @generated
 */
public class CommerceCurrencyWrapper
	extends BaseModelWrapper<CommerceCurrency>
	implements CommerceCurrency, ModelWrapper<CommerceCurrency> {

	public CommerceCurrencyWrapper(CommerceCurrency commerceCurrency) {
		super(commerceCurrency);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("commerceCurrencyId", getCommerceCurrencyId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("code", getCode());
		attributes.put("name", getName());
		attributes.put("symbol", getSymbol());
		attributes.put("rate", getRate());
		attributes.put("formatPattern", getFormatPattern());
		attributes.put("maxFractionDigits", getMaxFractionDigits());
		attributes.put("minFractionDigits", getMinFractionDigits());
		attributes.put("roundingMode", getRoundingMode());
		attributes.put("primary", isPrimary());
		attributes.put("priority", getPriority());
		attributes.put("active", isActive());
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

		Long commerceCurrencyId = (Long)attributes.get("commerceCurrencyId");

		if (commerceCurrencyId != null) {
			setCommerceCurrencyId(commerceCurrencyId);
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

		String code = (String)attributes.get("code");

		if (code != null) {
			setCode(code);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String symbol = (String)attributes.get("symbol");

		if (symbol != null) {
			setSymbol(symbol);
		}

		BigDecimal rate = (BigDecimal)attributes.get("rate");

		if (rate != null) {
			setRate(rate);
		}

		String formatPattern = (String)attributes.get("formatPattern");

		if (formatPattern != null) {
			setFormatPattern(formatPattern);
		}

		Integer maxFractionDigits = (Integer)attributes.get(
			"maxFractionDigits");

		if (maxFractionDigits != null) {
			setMaxFractionDigits(maxFractionDigits);
		}

		Integer minFractionDigits = (Integer)attributes.get(
			"minFractionDigits");

		if (minFractionDigits != null) {
			setMinFractionDigits(minFractionDigits);
		}

		String roundingMode = (String)attributes.get("roundingMode");

		if (roundingMode != null) {
			setRoundingMode(roundingMode);
		}

		Boolean primary = (Boolean)attributes.get("primary");

		if (primary != null) {
			setPrimary(primary);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
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
	public CommerceCurrency cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the active of this commerce currency.
	 *
	 * @return the active of this commerce currency
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
	 * Returns the code of this commerce currency.
	 *
	 * @return the code of this commerce currency
	 */
	@Override
	public String getCode() {
		return model.getCode();
	}

	/**
	 * Returns the commerce currency ID of this commerce currency.
	 *
	 * @return the commerce currency ID of this commerce currency
	 */
	@Override
	public long getCommerceCurrencyId() {
		return model.getCommerceCurrencyId();
	}

	/**
	 * Returns the company ID of this commerce currency.
	 *
	 * @return the company ID of this commerce currency
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this commerce currency.
	 *
	 * @return the create date of this commerce currency
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
	 * Returns the external reference code of this commerce currency.
	 *
	 * @return the external reference code of this commerce currency
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the format pattern of this commerce currency.
	 *
	 * @return the format pattern of this commerce currency
	 */
	@Override
	public String getFormatPattern() {
		return model.getFormatPattern();
	}

	/**
	 * Returns the localized format pattern of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized format pattern of this commerce currency
	 */
	@Override
	public String getFormatPattern(java.util.Locale locale) {
		return model.getFormatPattern(locale);
	}

	/**
	 * Returns the localized format pattern of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized format pattern of this commerce currency. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getFormatPattern(
		java.util.Locale locale, boolean useDefault) {

		return model.getFormatPattern(locale, useDefault);
	}

	/**
	 * Returns the localized format pattern of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized format pattern of this commerce currency
	 */
	@Override
	public String getFormatPattern(String languageId) {
		return model.getFormatPattern(languageId);
	}

	/**
	 * Returns the localized format pattern of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized format pattern of this commerce currency
	 */
	@Override
	public String getFormatPattern(String languageId, boolean useDefault) {
		return model.getFormatPattern(languageId, useDefault);
	}

	@Override
	public String getFormatPatternCurrentLanguageId() {
		return model.getFormatPatternCurrentLanguageId();
	}

	@Override
	public String getFormatPatternCurrentValue() {
		return model.getFormatPatternCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized format patterns of this commerce currency.
	 *
	 * @return the locales and localized format patterns of this commerce currency
	 */
	@Override
	public Map<java.util.Locale, String> getFormatPatternMap() {
		return model.getFormatPatternMap();
	}

	/**
	 * Returns the last publish date of this commerce currency.
	 *
	 * @return the last publish date of this commerce currency
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the max fraction digits of this commerce currency.
	 *
	 * @return the max fraction digits of this commerce currency
	 */
	@Override
	public int getMaxFractionDigits() {
		return model.getMaxFractionDigits();
	}

	/**
	 * Returns the min fraction digits of this commerce currency.
	 *
	 * @return the min fraction digits of this commerce currency
	 */
	@Override
	public int getMinFractionDigits() {
		return model.getMinFractionDigits();
	}

	/**
	 * Returns the modified date of this commerce currency.
	 *
	 * @return the modified date of this commerce currency
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce currency.
	 *
	 * @return the mvcc version of this commerce currency
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce currency.
	 *
	 * @return the name of this commerce currency
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this commerce currency
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce currency. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this commerce currency
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce currency
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
	 * Returns a map of the locales and localized names of this commerce currency.
	 *
	 * @return the locales and localized names of this commerce currency
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the primary of this commerce currency.
	 *
	 * @return the primary of this commerce currency
	 */
	@Override
	public boolean getPrimary() {
		return model.getPrimary();
	}

	/**
	 * Returns the primary key of this commerce currency.
	 *
	 * @return the primary key of this commerce currency
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this commerce currency.
	 *
	 * @return the priority of this commerce currency
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the rate of this commerce currency.
	 *
	 * @return the rate of this commerce currency
	 */
	@Override
	public BigDecimal getRate() {
		return model.getRate();
	}

	/**
	 * Returns the rounding mode of this commerce currency.
	 *
	 * @return the rounding mode of this commerce currency
	 */
	@Override
	public String getRoundingMode() {
		return model.getRoundingMode();
	}

	/**
	 * Returns the status of this commerce currency.
	 *
	 * @return the status of this commerce currency
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the symbol of this commerce currency.
	 *
	 * @return the symbol of this commerce currency
	 */
	@Override
	public String getSymbol() {
		return model.getSymbol();
	}

	/**
	 * Returns the user ID of this commerce currency.
	 *
	 * @return the user ID of this commerce currency
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce currency.
	 *
	 * @return the user name of this commerce currency
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce currency.
	 *
	 * @return the user uuid of this commerce currency
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this commerce currency.
	 *
	 * @return the uuid of this commerce currency
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public CommerceMoney getZero() {
		return model.getZero();
	}

	/**
	 * Returns <code>true</code> if this commerce currency is active.
	 *
	 * @return <code>true</code> if this commerce currency is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	/**
	 * Returns <code>true</code> if this commerce currency is primary.
	 *
	 * @return <code>true</code> if this commerce currency is primary; <code>false</code> otherwise
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

	@Override
	public BigDecimal round(BigDecimal value) {
		return model.round(value);
	}

	/**
	 * Sets whether this commerce currency is active.
	 *
	 * @param active the active of this commerce currency
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the code of this commerce currency.
	 *
	 * @param code the code of this commerce currency
	 */
	@Override
	public void setCode(String code) {
		model.setCode(code);
	}

	/**
	 * Sets the commerce currency ID of this commerce currency.
	 *
	 * @param commerceCurrencyId the commerce currency ID of this commerce currency
	 */
	@Override
	public void setCommerceCurrencyId(long commerceCurrencyId) {
		model.setCommerceCurrencyId(commerceCurrencyId);
	}

	/**
	 * Sets the company ID of this commerce currency.
	 *
	 * @param companyId the company ID of this commerce currency
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this commerce currency.
	 *
	 * @param createDate the create date of this commerce currency
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this commerce currency.
	 *
	 * @param externalReferenceCode the external reference code of this commerce currency
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the format pattern of this commerce currency.
	 *
	 * @param formatPattern the format pattern of this commerce currency
	 */
	@Override
	public void setFormatPattern(String formatPattern) {
		model.setFormatPattern(formatPattern);
	}

	/**
	 * Sets the localized format pattern of this commerce currency in the language.
	 *
	 * @param formatPattern the localized format pattern of this commerce currency
	 * @param locale the locale of the language
	 */
	@Override
	public void setFormatPattern(
		String formatPattern, java.util.Locale locale) {

		model.setFormatPattern(formatPattern, locale);
	}

	/**
	 * Sets the localized format pattern of this commerce currency in the language, and sets the default locale.
	 *
	 * @param formatPattern the localized format pattern of this commerce currency
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setFormatPattern(
		String formatPattern, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setFormatPattern(formatPattern, locale, defaultLocale);
	}

	@Override
	public void setFormatPatternCurrentLanguageId(String languageId) {
		model.setFormatPatternCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized format patterns of this commerce currency from the map of locales and localized format patterns.
	 *
	 * @param formatPatternMap the locales and localized format patterns of this commerce currency
	 */
	@Override
	public void setFormatPatternMap(
		Map<java.util.Locale, String> formatPatternMap) {

		model.setFormatPatternMap(formatPatternMap);
	}

	/**
	 * Sets the localized format patterns of this commerce currency from the map of locales and localized format patterns, and sets the default locale.
	 *
	 * @param formatPatternMap the locales and localized format patterns of this commerce currency
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setFormatPatternMap(
		Map<java.util.Locale, String> formatPatternMap,
		java.util.Locale defaultLocale) {

		model.setFormatPatternMap(formatPatternMap, defaultLocale);
	}

	/**
	 * Sets the last publish date of this commerce currency.
	 *
	 * @param lastPublishDate the last publish date of this commerce currency
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the max fraction digits of this commerce currency.
	 *
	 * @param maxFractionDigits the max fraction digits of this commerce currency
	 */
	@Override
	public void setMaxFractionDigits(int maxFractionDigits) {
		model.setMaxFractionDigits(maxFractionDigits);
	}

	/**
	 * Sets the min fraction digits of this commerce currency.
	 *
	 * @param minFractionDigits the min fraction digits of this commerce currency
	 */
	@Override
	public void setMinFractionDigits(int minFractionDigits) {
		model.setMinFractionDigits(minFractionDigits);
	}

	/**
	 * Sets the modified date of this commerce currency.
	 *
	 * @param modifiedDate the modified date of this commerce currency
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce currency.
	 *
	 * @param mvccVersion the mvcc version of this commerce currency
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce currency.
	 *
	 * @param name the name of this commerce currency
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this commerce currency in the language.
	 *
	 * @param name the localized name of this commerce currency
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this commerce currency in the language, and sets the default locale.
	 *
	 * @param name the localized name of this commerce currency
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
	 * Sets the localized names of this commerce currency from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this commerce currency
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this commerce currency from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this commerce currency
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets whether this commerce currency is primary.
	 *
	 * @param primary the primary of this commerce currency
	 */
	@Override
	public void setPrimary(boolean primary) {
		model.setPrimary(primary);
	}

	/**
	 * Sets the primary key of this commerce currency.
	 *
	 * @param primaryKey the primary key of this commerce currency
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this commerce currency.
	 *
	 * @param priority the priority of this commerce currency
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the rate of this commerce currency.
	 *
	 * @param rate the rate of this commerce currency
	 */
	@Override
	public void setRate(BigDecimal rate) {
		model.setRate(rate);
	}

	/**
	 * Sets the rounding mode of this commerce currency.
	 *
	 * @param roundingMode the rounding mode of this commerce currency
	 */
	@Override
	public void setRoundingMode(String roundingMode) {
		model.setRoundingMode(roundingMode);
	}

	/**
	 * Sets the status of this commerce currency.
	 *
	 * @param status the status of this commerce currency
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the symbol of this commerce currency.
	 *
	 * @param symbol the symbol of this commerce currency
	 */
	@Override
	public void setSymbol(String symbol) {
		model.setSymbol(symbol);
	}

	/**
	 * Sets the user ID of this commerce currency.
	 *
	 * @param userId the user ID of this commerce currency
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce currency.
	 *
	 * @param userName the user name of this commerce currency
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce currency.
	 *
	 * @param userUuid the user uuid of this commerce currency
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this commerce currency.
	 *
	 * @param uuid the uuid of this commerce currency
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
	protected CommerceCurrencyWrapper wrap(CommerceCurrency commerceCurrency) {
		return new CommerceCurrencyWrapper(commerceCurrency);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-38534840