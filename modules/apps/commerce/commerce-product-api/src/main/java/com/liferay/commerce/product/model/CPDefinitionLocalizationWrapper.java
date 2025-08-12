/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link CPDefinitionLocalization}.
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionLocalization
 * @generated
 */
public class CPDefinitionLocalizationWrapper
	extends BaseModelWrapper<CPDefinitionLocalization>
	implements CPDefinitionLocalization,
			   ModelWrapper<CPDefinitionLocalization> {

	public CPDefinitionLocalizationWrapper(
		CPDefinitionLocalization cpDefinitionLocalization) {

		super(cpDefinitionLocalization);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put(
			"cpDefinitionLocalizationId", getCpDefinitionLocalizationId());
		attributes.put("companyId", getCompanyId());
		attributes.put("CPDefinitionId", getCPDefinitionId());
		attributes.put("languageId", getLanguageId());
		attributes.put("name", getName());
		attributes.put("shortDescription", getShortDescription());
		attributes.put("description", getDescription());
		attributes.put("metaTitle", getMetaTitle());
		attributes.put("metaDescription", getMetaDescription());
		attributes.put("metaKeywords", getMetaKeywords());
		attributes.put("CProductId", getCProductId());

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

		Long cpDefinitionLocalizationId = (Long)attributes.get(
			"cpDefinitionLocalizationId");

		if (cpDefinitionLocalizationId != null) {
			setCpDefinitionLocalizationId(cpDefinitionLocalizationId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long CPDefinitionId = (Long)attributes.get("CPDefinitionId");

		if (CPDefinitionId != null) {
			setCPDefinitionId(CPDefinitionId);
		}

		String languageId = (String)attributes.get("languageId");

		if (languageId != null) {
			setLanguageId(languageId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String shortDescription = (String)attributes.get("shortDescription");

		if (shortDescription != null) {
			setShortDescription(shortDescription);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String metaTitle = (String)attributes.get("metaTitle");

		if (metaTitle != null) {
			setMetaTitle(metaTitle);
		}

		String metaDescription = (String)attributes.get("metaDescription");

		if (metaDescription != null) {
			setMetaDescription(metaDescription);
		}

		String metaKeywords = (String)attributes.get("metaKeywords");

		if (metaKeywords != null) {
			setMetaKeywords(metaKeywords);
		}

		Long CProductId = (Long)attributes.get("CProductId");

		if (CProductId != null) {
			setCProductId(CProductId);
		}
	}

	@Override
	public CPDefinitionLocalization cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this cp definition localization.
	 *
	 * @return the company ID of this cp definition localization
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the cp definition ID of this cp definition localization.
	 *
	 * @return the cp definition ID of this cp definition localization
	 */
	@Override
	public long getCPDefinitionId() {
		return model.getCPDefinitionId();
	}

	/**
	 * Returns the cp definition localization ID of this cp definition localization.
	 *
	 * @return the cp definition localization ID of this cp definition localization
	 */
	@Override
	public long getCpDefinitionLocalizationId() {
		return model.getCpDefinitionLocalizationId();
	}

	/**
	 * Returns the c product ID of this cp definition localization.
	 *
	 * @return the c product ID of this cp definition localization
	 */
	@Override
	public long getCProductId() {
		return model.getCProductId();
	}

	/**
	 * Returns the ct collection ID of this cp definition localization.
	 *
	 * @return the ct collection ID of this cp definition localization
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the description of this cp definition localization.
	 *
	 * @return the description of this cp definition localization
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the language ID of this cp definition localization.
	 *
	 * @return the language ID of this cp definition localization
	 */
	@Override
	public String getLanguageId() {
		return model.getLanguageId();
	}

	/**
	 * Returns the meta description of this cp definition localization.
	 *
	 * @return the meta description of this cp definition localization
	 */
	@Override
	public String getMetaDescription() {
		return model.getMetaDescription();
	}

	/**
	 * Returns the meta keywords of this cp definition localization.
	 *
	 * @return the meta keywords of this cp definition localization
	 */
	@Override
	public String getMetaKeywords() {
		return model.getMetaKeywords();
	}

	/**
	 * Returns the meta title of this cp definition localization.
	 *
	 * @return the meta title of this cp definition localization
	 */
	@Override
	public String getMetaTitle() {
		return model.getMetaTitle();
	}

	/**
	 * Returns the mvcc version of this cp definition localization.
	 *
	 * @return the mvcc version of this cp definition localization
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this cp definition localization.
	 *
	 * @return the name of this cp definition localization
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this cp definition localization.
	 *
	 * @return the primary key of this cp definition localization
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the short description of this cp definition localization.
	 *
	 * @return the short description of this cp definition localization
	 */
	@Override
	public String getShortDescription() {
		return model.getShortDescription();
	}

	/**
	 * Sets the company ID of this cp definition localization.
	 *
	 * @param companyId the company ID of this cp definition localization
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp definition ID of this cp definition localization.
	 *
	 * @param CPDefinitionId the cp definition ID of this cp definition localization
	 */
	@Override
	public void setCPDefinitionId(long CPDefinitionId) {
		model.setCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Sets the cp definition localization ID of this cp definition localization.
	 *
	 * @param cpDefinitionLocalizationId the cp definition localization ID of this cp definition localization
	 */
	@Override
	public void setCpDefinitionLocalizationId(long cpDefinitionLocalizationId) {
		model.setCpDefinitionLocalizationId(cpDefinitionLocalizationId);
	}

	/**
	 * Sets the c product ID of this cp definition localization.
	 *
	 * @param CProductId the c product ID of this cp definition localization
	 */
	@Override
	public void setCProductId(long CProductId) {
		model.setCProductId(CProductId);
	}

	/**
	 * Sets the ct collection ID of this cp definition localization.
	 *
	 * @param ctCollectionId the ct collection ID of this cp definition localization
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this cp definition localization.
	 *
	 * @param description the description of this cp definition localization
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the language ID of this cp definition localization.
	 *
	 * @param languageId the language ID of this cp definition localization
	 */
	@Override
	public void setLanguageId(String languageId) {
		model.setLanguageId(languageId);
	}

	/**
	 * Sets the meta description of this cp definition localization.
	 *
	 * @param metaDescription the meta description of this cp definition localization
	 */
	@Override
	public void setMetaDescription(String metaDescription) {
		model.setMetaDescription(metaDescription);
	}

	/**
	 * Sets the meta keywords of this cp definition localization.
	 *
	 * @param metaKeywords the meta keywords of this cp definition localization
	 */
	@Override
	public void setMetaKeywords(String metaKeywords) {
		model.setMetaKeywords(metaKeywords);
	}

	/**
	 * Sets the meta title of this cp definition localization.
	 *
	 * @param metaTitle the meta title of this cp definition localization
	 */
	@Override
	public void setMetaTitle(String metaTitle) {
		model.setMetaTitle(metaTitle);
	}

	/**
	 * Sets the mvcc version of this cp definition localization.
	 *
	 * @param mvccVersion the mvcc version of this cp definition localization
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this cp definition localization.
	 *
	 * @param name the name of this cp definition localization
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this cp definition localization.
	 *
	 * @param primaryKey the primary key of this cp definition localization
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the short description of this cp definition localization.
	 *
	 * @param shortDescription the short description of this cp definition localization
	 */
	@Override
	public void setShortDescription(String shortDescription) {
		model.setShortDescription(shortDescription);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CPDefinitionLocalization, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPDefinitionLocalization, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected CPDefinitionLocalizationWrapper wrap(
		CPDefinitionLocalization cpDefinitionLocalization) {

		return new CPDefinitionLocalizationWrapper(cpDefinitionLocalization);
	}

}