/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CPDefinitionOptionValueRel}.
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRel
 * @generated
 */
public class CPDefinitionOptionValueRelWrapper
	extends BaseModelWrapper<CPDefinitionOptionValueRel>
	implements CPDefinitionOptionValueRel,
			   ModelWrapper<CPDefinitionOptionValueRel> {

	public CPDefinitionOptionValueRelWrapper(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		super(cpDefinitionOptionValueRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"CPDefinitionOptionValueRelId", getCPDefinitionOptionValueRelId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("CPDefinitionOptionRelId", getCPDefinitionOptionRelId());
		attributes.put("CPInstanceUuid", getCPInstanceUuid());
		attributes.put("CProductId", getCProductId());
		attributes.put("key", getKey());
		attributes.put("name", getName());
		attributes.put("preselected", isPreselected());
		attributes.put("price", getPrice());
		attributes.put("priority", getPriority());
		attributes.put("quantity", getQuantity());
		attributes.put("unitOfMeasureKey", getUnitOfMeasureKey());

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

		Long CPDefinitionOptionValueRelId = (Long)attributes.get(
			"CPDefinitionOptionValueRelId");

		if (CPDefinitionOptionValueRelId != null) {
			setCPDefinitionOptionValueRelId(CPDefinitionOptionValueRelId);
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

		Long CPDefinitionOptionRelId = (Long)attributes.get(
			"CPDefinitionOptionRelId");

		if (CPDefinitionOptionRelId != null) {
			setCPDefinitionOptionRelId(CPDefinitionOptionRelId);
		}

		String CPInstanceUuid = (String)attributes.get("CPInstanceUuid");

		if (CPInstanceUuid != null) {
			setCPInstanceUuid(CPInstanceUuid);
		}

		Long CProductId = (Long)attributes.get("CProductId");

		if (CProductId != null) {
			setCProductId(CProductId);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Boolean preselected = (Boolean)attributes.get("preselected");

		if (preselected != null) {
			setPreselected(preselected);
		}

		BigDecimal price = (BigDecimal)attributes.get("price");

		if (price != null) {
			setPrice(price);
		}

		Double priority = (Double)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		BigDecimal quantity = (BigDecimal)attributes.get("quantity");

		if (quantity != null) {
			setQuantity(quantity);
		}

		String unitOfMeasureKey = (String)attributes.get("unitOfMeasureKey");

		if (unitOfMeasureKey != null) {
			setUnitOfMeasureKey(unitOfMeasureKey);
		}
	}

	@Override
	public CPDefinitionOptionValueRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public CPInstance fetchCPInstance() {
		return model.fetchCPInstance();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this cp definition option value rel.
	 *
	 * @return the company ID of this cp definition option value rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public CPDefinitionOptionRel getCPDefinitionOptionRel()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPDefinitionOptionRel();
	}

	/**
	 * Returns the cp definition option rel ID of this cp definition option value rel.
	 *
	 * @return the cp definition option rel ID of this cp definition option value rel
	 */
	@Override
	public long getCPDefinitionOptionRelId() {
		return model.getCPDefinitionOptionRelId();
	}

	/**
	 * Returns the cp definition option value rel ID of this cp definition option value rel.
	 *
	 * @return the cp definition option value rel ID of this cp definition option value rel
	 */
	@Override
	public long getCPDefinitionOptionValueRelId() {
		return model.getCPDefinitionOptionValueRelId();
	}

	/**
	 * Returns the cp instance uuid of this cp definition option value rel.
	 *
	 * @return the cp instance uuid of this cp definition option value rel
	 */
	@Override
	public String getCPInstanceUuid() {
		return model.getCPInstanceUuid();
	}

	/**
	 * Returns the c product ID of this cp definition option value rel.
	 *
	 * @return the c product ID of this cp definition option value rel
	 */
	@Override
	public long getCProductId() {
		return model.getCProductId();
	}

	/**
	 * Returns the create date of this cp definition option value rel.
	 *
	 * @return the create date of this cp definition option value rel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp definition option value rel.
	 *
	 * @return the ct collection ID of this cp definition option value rel
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
	 * Returns the external reference code of this cp definition option value rel.
	 *
	 * @return the external reference code of this cp definition option value rel
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this cp definition option value rel.
	 *
	 * @return the group ID of this cp definition option value rel
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the key of this cp definition option value rel.
	 *
	 * @return the key of this cp definition option value rel
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the modified date of this cp definition option value rel.
	 *
	 * @return the modified date of this cp definition option value rel
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cp definition option value rel.
	 *
	 * @return the mvcc version of this cp definition option value rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cp definition option value rel.
	 *
	 * @return the name of this cp definition option value rel
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this cp definition option value rel in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this cp definition option value rel
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this cp definition option value rel in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp definition option value rel. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this cp definition option value rel in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this cp definition option value rel
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this cp definition option value rel in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this cp definition option value rel
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
	 * Returns a map of the locales and localized names of this cp definition option value rel.
	 *
	 * @return the locales and localized names of this cp definition option value rel
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the preselected of this cp definition option value rel.
	 *
	 * @return the preselected of this cp definition option value rel
	 */
	@Override
	public boolean getPreselected() {
		return model.getPreselected();
	}

	/**
	 * Returns the price of this cp definition option value rel.
	 *
	 * @return the price of this cp definition option value rel
	 */
	@Override
	public BigDecimal getPrice() {
		return model.getPrice();
	}

	/**
	 * Returns the primary key of this cp definition option value rel.
	 *
	 * @return the primary key of this cp definition option value rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this cp definition option value rel.
	 *
	 * @return the priority of this cp definition option value rel
	 */
	@Override
	public double getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the quantity of this cp definition option value rel.
	 *
	 * @return the quantity of this cp definition option value rel
	 */
	@Override
	public BigDecimal getQuantity() {
		return model.getQuantity();
	}

	/**
	 * Returns the unit of measure key of this cp definition option value rel.
	 *
	 * @return the unit of measure key of this cp definition option value rel
	 */
	@Override
	public String getUnitOfMeasureKey() {
		return model.getUnitOfMeasureKey();
	}

	/**
	 * Returns the user ID of this cp definition option value rel.
	 *
	 * @return the user ID of this cp definition option value rel
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp definition option value rel.
	 *
	 * @return the user name of this cp definition option value rel
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp definition option value rel.
	 *
	 * @return the user uuid of this cp definition option value rel
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp definition option value rel.
	 *
	 * @return the uuid of this cp definition option value rel
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this cp definition option value rel is preselected.
	 *
	 * @return <code>true</code> if this cp definition option value rel is preselected; <code>false</code> otherwise
	 */
	@Override
	public boolean isPreselected() {
		return model.isPreselected();
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
	 * Sets the company ID of this cp definition option value rel.
	 *
	 * @param companyId the company ID of this cp definition option value rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp definition option rel ID of this cp definition option value rel.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID of this cp definition option value rel
	 */
	@Override
	public void setCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {
		model.setCPDefinitionOptionRelId(CPDefinitionOptionRelId);
	}

	/**
	 * Sets the cp definition option value rel ID of this cp definition option value rel.
	 *
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID of this cp definition option value rel
	 */
	@Override
	public void setCPDefinitionOptionValueRelId(
		long CPDefinitionOptionValueRelId) {

		model.setCPDefinitionOptionValueRelId(CPDefinitionOptionValueRelId);
	}

	/**
	 * Sets the cp instance uuid of this cp definition option value rel.
	 *
	 * @param CPInstanceUuid the cp instance uuid of this cp definition option value rel
	 */
	@Override
	public void setCPInstanceUuid(String CPInstanceUuid) {
		model.setCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Sets the c product ID of this cp definition option value rel.
	 *
	 * @param CProductId the c product ID of this cp definition option value rel
	 */
	@Override
	public void setCProductId(long CProductId) {
		model.setCProductId(CProductId);
	}

	/**
	 * Sets the create date of this cp definition option value rel.
	 *
	 * @param createDate the create date of this cp definition option value rel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp definition option value rel.
	 *
	 * @param ctCollectionId the ct collection ID of this cp definition option value rel
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the external reference code of this cp definition option value rel.
	 *
	 * @param externalReferenceCode the external reference code of this cp definition option value rel
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this cp definition option value rel.
	 *
	 * @param groupId the group ID of this cp definition option value rel
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the key of this cp definition option value rel.
	 *
	 * @param key the key of this cp definition option value rel
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the modified date of this cp definition option value rel.
	 *
	 * @param modifiedDate the modified date of this cp definition option value rel
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cp definition option value rel.
	 *
	 * @param mvccVersion the mvcc version of this cp definition option value rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cp definition option value rel.
	 *
	 * @param name the name of this cp definition option value rel
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this cp definition option value rel in the language.
	 *
	 * @param name the localized name of this cp definition option value rel
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this cp definition option value rel in the language, and sets the default locale.
	 *
	 * @param name the localized name of this cp definition option value rel
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
	 * Sets the localized names of this cp definition option value rel from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this cp definition option value rel
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this cp definition option value rel from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this cp definition option value rel
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets whether this cp definition option value rel is preselected.
	 *
	 * @param preselected the preselected of this cp definition option value rel
	 */
	@Override
	public void setPreselected(boolean preselected) {
		model.setPreselected(preselected);
	}

	/**
	 * Sets the price of this cp definition option value rel.
	 *
	 * @param price the price of this cp definition option value rel
	 */
	@Override
	public void setPrice(BigDecimal price) {
		model.setPrice(price);
	}

	/**
	 * Sets the primary key of this cp definition option value rel.
	 *
	 * @param primaryKey the primary key of this cp definition option value rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this cp definition option value rel.
	 *
	 * @param priority the priority of this cp definition option value rel
	 */
	@Override
	public void setPriority(double priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets the quantity of this cp definition option value rel.
	 *
	 * @param quantity the quantity of this cp definition option value rel
	 */
	@Override
	public void setQuantity(BigDecimal quantity) {
		model.setQuantity(quantity);
	}

	/**
	 * Sets the unit of measure key of this cp definition option value rel.
	 *
	 * @param unitOfMeasureKey the unit of measure key of this cp definition option value rel
	 */
	@Override
	public void setUnitOfMeasureKey(String unitOfMeasureKey) {
		model.setUnitOfMeasureKey(unitOfMeasureKey);
	}

	/**
	 * Sets the user ID of this cp definition option value rel.
	 *
	 * @param userId the user ID of this cp definition option value rel
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp definition option value rel.
	 *
	 * @param userName the user name of this cp definition option value rel
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp definition option value rel.
	 *
	 * @param userUuid the user uuid of this cp definition option value rel
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp definition option value rel.
	 *
	 * @param uuid the uuid of this cp definition option value rel
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
	public Map<String, Function<CPDefinitionOptionValueRel, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPDefinitionOptionValueRel, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPDefinitionOptionValueRelWrapper wrap(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return new CPDefinitionOptionValueRelWrapper(
			cpDefinitionOptionValueRel);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1916425059