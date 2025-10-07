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
 * This class is a wrapper for {@link CPDefinition}.
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinition
 * @generated
 */
public class CPDefinitionWrapper
	extends BaseModelWrapper<CPDefinition>
	implements CPDefinition, ModelWrapper<CPDefinition> {

	public CPDefinitionWrapper(CPDefinition cpDefinition) {
		super(cpDefinition);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("defaultLanguageId", getDefaultLanguageId());
		attributes.put("CPDefinitionId", getCPDefinitionId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("CProductId", getCProductId());
		attributes.put("CPTaxCategoryId", getCPTaxCategoryId());
		attributes.put("productTypeName", getProductTypeName());
		attributes.put("availableIndividually", isAvailableIndividually());
		attributes.put("ignoreSKUCombinations", isIgnoreSKUCombinations());
		attributes.put("shippable", isShippable());
		attributes.put("freeShipping", isFreeShipping());
		attributes.put("shipSeparately", isShipSeparately());
		attributes.put("shippingExtraPrice", getShippingExtraPrice());
		attributes.put("width", getWidth());
		attributes.put("height", getHeight());
		attributes.put("depth", getDepth());
		attributes.put("weight", getWeight());
		attributes.put("taxExempt", isTaxExempt());
		attributes.put("telcoOrElectronics", isTelcoOrElectronics());
		attributes.put("DDMStructureKey", getDDMStructureKey());
		attributes.put("published", isPublished());
		attributes.put("displayDate", getDisplayDate());
		attributes.put("expirationDate", getExpirationDate());
		attributes.put("lastPublishDate", getLastPublishDate());
		attributes.put("subscriptionEnabled", isSubscriptionEnabled());
		attributes.put("subscriptionLength", getSubscriptionLength());
		attributes.put("subscriptionType", getSubscriptionType());
		attributes.put(
			"subscriptionTypeSettings", getSubscriptionTypeSettings());
		attributes.put("maxSubscriptionCycles", getMaxSubscriptionCycles());
		attributes.put(
			"deliverySubscriptionEnabled", isDeliverySubscriptionEnabled());
		attributes.put(
			"deliverySubscriptionLength", getDeliverySubscriptionLength());
		attributes.put(
			"deliverySubscriptionType", getDeliverySubscriptionType());
		attributes.put(
			"deliverySubscriptionTypeSettings",
			getDeliverySubscriptionTypeSettings());
		attributes.put(
			"deliveryMaxSubscriptionCycles",
			getDeliveryMaxSubscriptionCycles());
		attributes.put(
			"accountGroupFilterEnabled", isAccountGroupFilterEnabled());
		attributes.put("channelFilterEnabled", isChannelFilterEnabled());
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

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String defaultLanguageId = (String)attributes.get("defaultLanguageId");

		if (defaultLanguageId != null) {
			setDefaultLanguageId(defaultLanguageId);
		}

		Long CPDefinitionId = (Long)attributes.get("CPDefinitionId");

		if (CPDefinitionId != null) {
			setCPDefinitionId(CPDefinitionId);
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

		Long CProductId = (Long)attributes.get("CProductId");

		if (CProductId != null) {
			setCProductId(CProductId);
		}

		Long CPTaxCategoryId = (Long)attributes.get("CPTaxCategoryId");

		if (CPTaxCategoryId != null) {
			setCPTaxCategoryId(CPTaxCategoryId);
		}

		String productTypeName = (String)attributes.get("productTypeName");

		if (productTypeName != null) {
			setProductTypeName(productTypeName);
		}

		Boolean availableIndividually = (Boolean)attributes.get(
			"availableIndividually");

		if (availableIndividually != null) {
			setAvailableIndividually(availableIndividually);
		}

		Boolean ignoreSKUCombinations = (Boolean)attributes.get(
			"ignoreSKUCombinations");

		if (ignoreSKUCombinations != null) {
			setIgnoreSKUCombinations(ignoreSKUCombinations);
		}

		Boolean shippable = (Boolean)attributes.get("shippable");

		if (shippable != null) {
			setShippable(shippable);
		}

		Boolean freeShipping = (Boolean)attributes.get("freeShipping");

		if (freeShipping != null) {
			setFreeShipping(freeShipping);
		}

		Boolean shipSeparately = (Boolean)attributes.get("shipSeparately");

		if (shipSeparately != null) {
			setShipSeparately(shipSeparately);
		}

		Double shippingExtraPrice = (Double)attributes.get(
			"shippingExtraPrice");

		if (shippingExtraPrice != null) {
			setShippingExtraPrice(shippingExtraPrice);
		}

		Double width = (Double)attributes.get("width");

		if (width != null) {
			setWidth(width);
		}

		Double height = (Double)attributes.get("height");

		if (height != null) {
			setHeight(height);
		}

		Double depth = (Double)attributes.get("depth");

		if (depth != null) {
			setDepth(depth);
		}

		Double weight = (Double)attributes.get("weight");

		if (weight != null) {
			setWeight(weight);
		}

		Boolean taxExempt = (Boolean)attributes.get("taxExempt");

		if (taxExempt != null) {
			setTaxExempt(taxExempt);
		}

		Boolean telcoOrElectronics = (Boolean)attributes.get(
			"telcoOrElectronics");

		if (telcoOrElectronics != null) {
			setTelcoOrElectronics(telcoOrElectronics);
		}

		String DDMStructureKey = (String)attributes.get("DDMStructureKey");

		if (DDMStructureKey != null) {
			setDDMStructureKey(DDMStructureKey);
		}

		Boolean published = (Boolean)attributes.get("published");

		if (published != null) {
			setPublished(published);
		}

		Date displayDate = (Date)attributes.get("displayDate");

		if (displayDate != null) {
			setDisplayDate(displayDate);
		}

		Date expirationDate = (Date)attributes.get("expirationDate");

		if (expirationDate != null) {
			setExpirationDate(expirationDate);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}

		Boolean subscriptionEnabled = (Boolean)attributes.get(
			"subscriptionEnabled");

		if (subscriptionEnabled != null) {
			setSubscriptionEnabled(subscriptionEnabled);
		}

		Integer subscriptionLength = (Integer)attributes.get(
			"subscriptionLength");

		if (subscriptionLength != null) {
			setSubscriptionLength(subscriptionLength);
		}

		String subscriptionType = (String)attributes.get("subscriptionType");

		if (subscriptionType != null) {
			setSubscriptionType(subscriptionType);
		}

		String subscriptionTypeSettings = (String)attributes.get(
			"subscriptionTypeSettings");

		if (subscriptionTypeSettings != null) {
			setSubscriptionTypeSettings(subscriptionTypeSettings);
		}

		Long maxSubscriptionCycles = (Long)attributes.get(
			"maxSubscriptionCycles");

		if (maxSubscriptionCycles != null) {
			setMaxSubscriptionCycles(maxSubscriptionCycles);
		}

		Boolean deliverySubscriptionEnabled = (Boolean)attributes.get(
			"deliverySubscriptionEnabled");

		if (deliverySubscriptionEnabled != null) {
			setDeliverySubscriptionEnabled(deliverySubscriptionEnabled);
		}

		Integer deliverySubscriptionLength = (Integer)attributes.get(
			"deliverySubscriptionLength");

		if (deliverySubscriptionLength != null) {
			setDeliverySubscriptionLength(deliverySubscriptionLength);
		}

		String deliverySubscriptionType = (String)attributes.get(
			"deliverySubscriptionType");

		if (deliverySubscriptionType != null) {
			setDeliverySubscriptionType(deliverySubscriptionType);
		}

		String deliverySubscriptionTypeSettings = (String)attributes.get(
			"deliverySubscriptionTypeSettings");

		if (deliverySubscriptionTypeSettings != null) {
			setDeliverySubscriptionTypeSettings(
				deliverySubscriptionTypeSettings);
		}

		Long deliveryMaxSubscriptionCycles = (Long)attributes.get(
			"deliveryMaxSubscriptionCycles");

		if (deliveryMaxSubscriptionCycles != null) {
			setDeliveryMaxSubscriptionCycles(deliveryMaxSubscriptionCycles);
		}

		Boolean accountGroupFilterEnabled = (Boolean)attributes.get(
			"accountGroupFilterEnabled");

		if (accountGroupFilterEnabled != null) {
			setAccountGroupFilterEnabled(accountGroupFilterEnabled);
		}

		Boolean channelFilterEnabled = (Boolean)attributes.get(
			"channelFilterEnabled");

		if (channelFilterEnabled != null) {
			setChannelFilterEnabled(channelFilterEnabled);
		}

		Integer version = (Integer)attributes.get("version");

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
	public Object clone() {
		return new CPDefinitionWrapper((CPDefinition)model.clone());
	}

	@Override
	public CPDefinition cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public boolean equals(Object object) {
		return model.equals(object);
	}

	@Override
	public CPConfigurationEntry fetchCPConfigurationEntry(
		long cpConfigurationListId) {

		return model.fetchCPConfigurationEntry(cpConfigurationListId);
	}

	@Override
	public CPConfigurationEntry fetchMasterCPConfigurationEntry()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.fetchMasterCPConfigurationEntry();
	}

	/**
	 * Returns the account group filter enabled of this cp definition.
	 *
	 * @return the account group filter enabled of this cp definition
	 */
	@Override
	public boolean getAccountGroupFilterEnabled() {
		return model.getAccountGroupFilterEnabled();
	}

	/**
	 * Returns the available individually of this cp definition.
	 *
	 * @return the available individually of this cp definition
	 */
	@Override
	public boolean getAvailableIndividually() {
		return model.getAvailableIndividually();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the channel filter enabled of this cp definition.
	 *
	 * @return the channel filter enabled of this cp definition
	 */
	@Override
	public boolean getChannelFilterEnabled() {
		return model.getChannelFilterEnabled();
	}

	@Override
	public CommerceCatalog getCommerceCatalog() {
		return model.getCommerceCatalog();
	}

	/**
	 * Returns the company ID of this cp definition.
	 *
	 * @return the company ID of this cp definition
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			int type, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPAttachmentFileEntries(type, status);
	}

	/**
	 * Returns the cp definition ID of this cp definition.
	 *
	 * @return the cp definition ID of this cp definition
	 */
	@Override
	public long getCPDefinitionId() {
		return model.getCPDefinitionId();
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels() {
		return model.getCPDefinitionOptionRels();
	}

	@Override
	public java.util.List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues() {

		return model.getCPDefinitionSpecificationOptionValues();
	}

	@Override
	public java.util.List<CPInstance> getCPInstances() {
		return model.getCPInstances();
	}

	@Override
	public CProduct getCProduct()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCProduct();
	}

	/**
	 * Returns the c product ID of this cp definition.
	 *
	 * @return the c product ID of this cp definition
	 */
	@Override
	public long getCProductId() {
		return model.getCProductId();
	}

	@Override
	public CPTaxCategory getCPTaxCategory()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCPTaxCategory();
	}

	/**
	 * Returns the cp tax category ID of this cp definition.
	 *
	 * @return the cp tax category ID of this cp definition
	 */
	@Override
	public long getCPTaxCategoryId() {
		return model.getCPTaxCategoryId();
	}

	/**
	 * Returns the create date of this cp definition.
	 *
	 * @return the create date of this cp definition
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this cp definition.
	 *
	 * @return the ct collection ID of this cp definition
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the ddm structure key of this cp definition.
	 *
	 * @return the ddm structure key of this cp definition
	 */
	@Override
	public String getDDMStructureKey() {
		return model.getDDMStructureKey();
	}

	@Override
	public String getDefaultImageThumbnailSrc(long commerceAccountId)
		throws Exception {

		return model.getDefaultImageThumbnailSrc(commerceAccountId);
	}

	/**
	 * Returns the default language ID of this cp definition.
	 *
	 * @return the default language ID of this cp definition
	 */
	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the delivery max subscription cycles of this cp definition.
	 *
	 * @return the delivery max subscription cycles of this cp definition
	 */
	@Override
	public long getDeliveryMaxSubscriptionCycles() {
		return model.getDeliveryMaxSubscriptionCycles();
	}

	/**
	 * Returns the delivery subscription enabled of this cp definition.
	 *
	 * @return the delivery subscription enabled of this cp definition
	 */
	@Override
	public boolean getDeliverySubscriptionEnabled() {
		return model.getDeliverySubscriptionEnabled();
	}

	/**
	 * Returns the delivery subscription length of this cp definition.
	 *
	 * @return the delivery subscription length of this cp definition
	 */
	@Override
	public int getDeliverySubscriptionLength() {
		return model.getDeliverySubscriptionLength();
	}

	/**
	 * Returns the delivery subscription type of this cp definition.
	 *
	 * @return the delivery subscription type of this cp definition
	 */
	@Override
	public String getDeliverySubscriptionType() {
		return model.getDeliverySubscriptionType();
	}

	/**
	 * Returns the delivery subscription type settings of this cp definition.
	 *
	 * @return the delivery subscription type settings of this cp definition
	 */
	@Override
	public String getDeliverySubscriptionTypeSettings() {
		return model.getDeliverySubscriptionTypeSettings();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getDeliverySubscriptionTypeSettingsUnicodeProperties() {

		return model.getDeliverySubscriptionTypeSettingsUnicodeProperties();
	}

	/**
	 * Returns the depth of this cp definition.
	 *
	 * @return the depth of this cp definition
	 */
	@Override
	public double getDepth() {
		return model.getDepth();
	}

	@Override
	public String getDescription() {
		return model.getDescription();
	}

	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return model.getDescription(languageId, useDefault);
	}

	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	@Override
	public String getDescriptionMapAsXML() {
		return model.getDescriptionMapAsXML();
	}

	/**
	 * Returns the display date of this cp definition.
	 *
	 * @return the display date of this cp definition
	 */
	@Override
	public Date getDisplayDate() {
		return model.getDisplayDate();
	}

	/**
	 * Returns the expiration date of this cp definition.
	 *
	 * @return the expiration date of this cp definition
	 */
	@Override
	public Date getExpirationDate() {
		return model.getExpirationDate();
	}

	/**
	 * Returns the free shipping of this cp definition.
	 *
	 * @return the free shipping of this cp definition
	 */
	@Override
	public boolean getFreeShipping() {
		return model.getFreeShipping();
	}

	/**
	 * Returns the group ID of this cp definition.
	 *
	 * @return the group ID of this cp definition
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the height of this cp definition.
	 *
	 * @return the height of this cp definition
	 */
	@Override
	public double getHeight() {
		return model.getHeight();
	}

	/**
	 * Returns the ignore sku combinations of this cp definition.
	 *
	 * @return the ignore sku combinations of this cp definition
	 */
	@Override
	public boolean getIgnoreSKUCombinations() {
		return model.getIgnoreSKUCombinations();
	}

	@Override
	public Map<String, String> getLanguageIdToDescriptionMap() {
		return model.getLanguageIdToDescriptionMap();
	}

	@Override
	public Map<String, String> getLanguageIdToMetaDescriptionMap() {
		return model.getLanguageIdToMetaDescriptionMap();
	}

	@Override
	public Map<String, String> getLanguageIdToMetaKeywordsMap() {
		return model.getLanguageIdToMetaKeywordsMap();
	}

	@Override
	public Map<String, String> getLanguageIdToMetaTitleMap() {
		return model.getLanguageIdToMetaTitleMap();
	}

	@Override
	public Map<String, String> getLanguageIdToNameMap() {
		return model.getLanguageIdToNameMap();
	}

	@Override
	public Map<String, String> getLanguageIdToShortDescriptionMap() {
		return model.getLanguageIdToShortDescriptionMap();
	}

	/**
	 * Returns the last publish date of this cp definition.
	 *
	 * @return the last publish date of this cp definition
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	@Override
	public CPConfigurationList getMasterCPConfigurationList()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getMasterCPConfigurationList();
	}

	/**
	 * Returns the max subscription cycles of this cp definition.
	 *
	 * @return the max subscription cycles of this cp definition
	 */
	@Override
	public long getMaxSubscriptionCycles() {
		return model.getMaxSubscriptionCycles();
	}

	@Override
	public String getMetaDescription() {
		return model.getMetaDescription();
	}

	@Override
	public String getMetaDescription(String languageId) {
		return model.getMetaDescription(languageId);
	}

	@Override
	public String getMetaDescription(String languageId, boolean useDefault) {
		return model.getMetaDescription(languageId, useDefault);
	}

	@Override
	public Map<java.util.Locale, String> getMetaDescriptionMap() {
		return model.getMetaDescriptionMap();
	}

	@Override
	public String getMetaDescriptionMapAsXML() {
		return model.getMetaDescriptionMapAsXML();
	}

	@Override
	public String getMetaKeywords() {
		return model.getMetaKeywords();
	}

	@Override
	public String getMetaKeywords(String languageId) {
		return model.getMetaKeywords(languageId);
	}

	@Override
	public String getMetaKeywords(String languageId, boolean useDefault) {
		return model.getMetaKeywords(languageId, useDefault);
	}

	@Override
	public Map<java.util.Locale, String> getMetaKeywordsMap() {
		return model.getMetaKeywordsMap();
	}

	@Override
	public String getMetaKeywordsMapAsXML() {
		return model.getMetaKeywordsMapAsXML();
	}

	@Override
	public String getMetaTitle() {
		return model.getMetaTitle();
	}

	@Override
	public String getMetaTitle(String languageId) {
		return model.getMetaTitle(languageId);
	}

	@Override
	public String getMetaTitle(String languageId, boolean useDefault) {
		return model.getMetaTitle(languageId, useDefault);
	}

	@Override
	public Map<java.util.Locale, String> getMetaTitleMap() {
		return model.getMetaTitleMap();
	}

	@Override
	public String getMetaTitleMapAsXML() {
		return model.getMetaTitleMapAsXML();
	}

	/**
	 * Returns the modified date of this cp definition.
	 *
	 * @return the modified date of this cp definition
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this cp definition.
	 *
	 * @return the mvcc version of this cp definition
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	@Override
	public String getName() {
		return model.getName();
	}

	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	@Override
	public String getName(String languageId, boolean useDefault) {
		return model.getName(languageId, useDefault);
	}

	@Override
	public String getNameCurrentValue() {
		return model.getNameCurrentValue();
	}

	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	@Override
	public String getNameMapAsXML() {
		return model.getNameMapAsXML();
	}

	/**
	 * Returns the primary key of this cp definition.
	 *
	 * @return the primary key of this cp definition
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the product type name of this cp definition.
	 *
	 * @return the product type name of this cp definition
	 */
	@Override
	public String getProductTypeName() {
		return model.getProductTypeName();
	}

	/**
	 * Returns the published of this cp definition.
	 *
	 * @return the published of this cp definition
	 */
	@Override
	public boolean getPublished() {
		return model.getPublished();
	}

	/**
	 * Returns the shippable of this cp definition.
	 *
	 * @return the shippable of this cp definition
	 */
	@Override
	public boolean getShippable() {
		return model.getShippable();
	}

	/**
	 * Returns the shipping extra price of this cp definition.
	 *
	 * @return the shipping extra price of this cp definition
	 */
	@Override
	public double getShippingExtraPrice() {
		return model.getShippingExtraPrice();
	}

	/**
	 * Returns the ship separately of this cp definition.
	 *
	 * @return the ship separately of this cp definition
	 */
	@Override
	public boolean getShipSeparately() {
		return model.getShipSeparately();
	}

	@Override
	public String getShortDescription() {
		return model.getShortDescription();
	}

	@Override
	public String getShortDescription(String languageId) {
		return model.getShortDescription(languageId);
	}

	@Override
	public String getShortDescription(String languageId, boolean useDefault) {
		return model.getShortDescription(languageId, useDefault);
	}

	@Override
	public Map<java.util.Locale, String> getShortDescriptionMap() {
		return model.getShortDescriptionMap();
	}

	@Override
	public String getShortDescriptionMapAsXML() {
		return model.getShortDescriptionMapAsXML();
	}

	/**
	 * Returns the status of this cp definition.
	 *
	 * @return the status of this cp definition
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this cp definition.
	 *
	 * @return the status by user ID of this cp definition
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this cp definition.
	 *
	 * @return the status by user name of this cp definition
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this cp definition.
	 *
	 * @return the status by user uuid of this cp definition
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this cp definition.
	 *
	 * @return the status date of this cp definition
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the subscription enabled of this cp definition.
	 *
	 * @return the subscription enabled of this cp definition
	 */
	@Override
	public boolean getSubscriptionEnabled() {
		return model.getSubscriptionEnabled();
	}

	/**
	 * Returns the subscription length of this cp definition.
	 *
	 * @return the subscription length of this cp definition
	 */
	@Override
	public int getSubscriptionLength() {
		return model.getSubscriptionLength();
	}

	/**
	 * Returns the subscription type of this cp definition.
	 *
	 * @return the subscription type of this cp definition
	 */
	@Override
	public String getSubscriptionType() {
		return model.getSubscriptionType();
	}

	/**
	 * Returns the subscription type settings of this cp definition.
	 *
	 * @return the subscription type settings of this cp definition
	 */
	@Override
	public String getSubscriptionTypeSettings() {
		return model.getSubscriptionTypeSettings();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getSubscriptionTypeSettingsUnicodeProperties() {

		return model.getSubscriptionTypeSettingsUnicodeProperties();
	}

	/**
	 * Returns the tax exempt of this cp definition.
	 *
	 * @return the tax exempt of this cp definition
	 */
	@Override
	public boolean getTaxExempt() {
		return model.getTaxExempt();
	}

	/**
	 * Returns the telco or electronics of this cp definition.
	 *
	 * @return the telco or electronics of this cp definition
	 */
	@Override
	public boolean getTelcoOrElectronics() {
		return model.getTelcoOrElectronics();
	}

	@Override
	public String getURL(String languageId) {
		return model.getURL(languageId);
	}

	@Override
	public Map<java.util.Locale, String> getUrlTitleMap() {
		return model.getUrlTitleMap();
	}

	/**
	 * Returns the user ID of this cp definition.
	 *
	 * @return the user ID of this cp definition
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this cp definition.
	 *
	 * @return the user name of this cp definition
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this cp definition.
	 *
	 * @return the user uuid of this cp definition
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this cp definition.
	 *
	 * @return the uuid of this cp definition
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this cp definition.
	 *
	 * @return the version of this cp definition
	 */
	@Override
	public int getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns the weight of this cp definition.
	 *
	 * @return the weight of this cp definition
	 */
	@Override
	public double getWeight() {
		return model.getWeight();
	}

	/**
	 * Returns the width of this cp definition.
	 *
	 * @return the width of this cp definition
	 */
	@Override
	public double getWidth() {
		return model.getWidth();
	}

	@Override
	public int hashCode() {
		return model.hashCode();
	}

	/**
	 * Returns <code>true</code> if this cp definition is account group filter enabled.
	 *
	 * @return <code>true</code> if this cp definition is account group filter enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isAccountGroupFilterEnabled() {
		return model.isAccountGroupFilterEnabled();
	}

	/**
	 * Returns <code>true</code> if this cp definition is approved.
	 *
	 * @return <code>true</code> if this cp definition is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this cp definition is available individually.
	 *
	 * @return <code>true</code> if this cp definition is available individually; <code>false</code> otherwise
	 */
	@Override
	public boolean isAvailableIndividually() {
		return model.isAvailableIndividually();
	}

	/**
	 * Returns <code>true</code> if this cp definition is channel filter enabled.
	 *
	 * @return <code>true</code> if this cp definition is channel filter enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isChannelFilterEnabled() {
		return model.isChannelFilterEnabled();
	}

	/**
	 * Returns <code>true</code> if this cp definition is delivery subscription enabled.
	 *
	 * @return <code>true</code> if this cp definition is delivery subscription enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isDeliverySubscriptionEnabled() {
		return model.isDeliverySubscriptionEnabled();
	}

	/**
	 * Returns <code>true</code> if this cp definition is denied.
	 *
	 * @return <code>true</code> if this cp definition is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this cp definition is a draft.
	 *
	 * @return <code>true</code> if this cp definition is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this cp definition is expired.
	 *
	 * @return <code>true</code> if this cp definition is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this cp definition is free shipping.
	 *
	 * @return <code>true</code> if this cp definition is free shipping; <code>false</code> otherwise
	 */
	@Override
	public boolean isFreeShipping() {
		return model.isFreeShipping();
	}

	/**
	 * Returns <code>true</code> if this cp definition is ignore sku combinations.
	 *
	 * @return <code>true</code> if this cp definition is ignore sku combinations; <code>false</code> otherwise
	 */
	@Override
	public boolean isIgnoreSKUCombinations() {
		return model.isIgnoreSKUCombinations();
	}

	/**
	 * Returns <code>true</code> if this cp definition is inactive.
	 *
	 * @return <code>true</code> if this cp definition is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this cp definition is incomplete.
	 *
	 * @return <code>true</code> if this cp definition is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this cp definition is pending.
	 *
	 * @return <code>true</code> if this cp definition is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this cp definition is published.
	 *
	 * @return <code>true</code> if this cp definition is published; <code>false</code> otherwise
	 */
	@Override
	public boolean isPublished() {
		return model.isPublished();
	}

	/**
	 * Returns <code>true</code> if this cp definition is scheduled.
	 *
	 * @return <code>true</code> if this cp definition is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	/**
	 * Returns <code>true</code> if this cp definition is shippable.
	 *
	 * @return <code>true</code> if this cp definition is shippable; <code>false</code> otherwise
	 */
	@Override
	public boolean isShippable() {
		return model.isShippable();
	}

	/**
	 * Returns <code>true</code> if this cp definition is ship separately.
	 *
	 * @return <code>true</code> if this cp definition is ship separately; <code>false</code> otherwise
	 */
	@Override
	public boolean isShipSeparately() {
		return model.isShipSeparately();
	}

	/**
	 * Returns <code>true</code> if this cp definition is subscription enabled.
	 *
	 * @return <code>true</code> if this cp definition is subscription enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isSubscriptionEnabled() {
		return model.isSubscriptionEnabled();
	}

	/**
	 * Returns <code>true</code> if this cp definition is tax exempt.
	 *
	 * @return <code>true</code> if this cp definition is tax exempt; <code>false</code> otherwise
	 */
	@Override
	public boolean isTaxExempt() {
		return model.isTaxExempt();
	}

	/**
	 * Returns <code>true</code> if this cp definition is telco or electronics.
	 *
	 * @return <code>true</code> if this cp definition is telco or electronics; <code>false</code> otherwise
	 */
	@Override
	public boolean isTelcoOrElectronics() {
		return model.isTelcoOrElectronics();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets whether this cp definition is account group filter enabled.
	 *
	 * @param accountGroupFilterEnabled the account group filter enabled of this cp definition
	 */
	@Override
	public void setAccountGroupFilterEnabled(
		boolean accountGroupFilterEnabled) {

		model.setAccountGroupFilterEnabled(accountGroupFilterEnabled);
	}

	/**
	 * Sets whether this cp definition is available individually.
	 *
	 * @param availableIndividually the available individually of this cp definition
	 */
	@Override
	public void setAvailableIndividually(boolean availableIndividually) {
		model.setAvailableIndividually(availableIndividually);
	}

	/**
	 * Sets whether this cp definition is channel filter enabled.
	 *
	 * @param channelFilterEnabled the channel filter enabled of this cp definition
	 */
	@Override
	public void setChannelFilterEnabled(boolean channelFilterEnabled) {
		model.setChannelFilterEnabled(channelFilterEnabled);
	}

	/**
	 * Sets the company ID of this cp definition.
	 *
	 * @param companyId the company ID of this cp definition
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the cp definition ID of this cp definition.
	 *
	 * @param CPDefinitionId the cp definition ID of this cp definition
	 */
	@Override
	public void setCPDefinitionId(long CPDefinitionId) {
		model.setCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Sets the c product ID of this cp definition.
	 *
	 * @param CProductId the c product ID of this cp definition
	 */
	@Override
	public void setCProductId(long CProductId) {
		model.setCProductId(CProductId);
	}

	/**
	 * Sets the cp tax category ID of this cp definition.
	 *
	 * @param CPTaxCategoryId the cp tax category ID of this cp definition
	 */
	@Override
	public void setCPTaxCategoryId(long CPTaxCategoryId) {
		model.setCPTaxCategoryId(CPTaxCategoryId);
	}

	/**
	 * Sets the create date of this cp definition.
	 *
	 * @param createDate the create date of this cp definition
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this cp definition.
	 *
	 * @param ctCollectionId the ct collection ID of this cp definition
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the ddm structure key of this cp definition.
	 *
	 * @param DDMStructureKey the ddm structure key of this cp definition
	 */
	@Override
	public void setDDMStructureKey(String DDMStructureKey) {
		model.setDDMStructureKey(DDMStructureKey);
	}

	/**
	 * Sets the default language ID of this cp definition.
	 *
	 * @param defaultLanguageId the default language ID of this cp definition
	 */
	@Override
	public void setDefaultLanguageId(String defaultLanguageId) {
		model.setDefaultLanguageId(defaultLanguageId);
	}

	/**
	 * Sets the delivery max subscription cycles of this cp definition.
	 *
	 * @param deliveryMaxSubscriptionCycles the delivery max subscription cycles of this cp definition
	 */
	@Override
	public void setDeliveryMaxSubscriptionCycles(
		long deliveryMaxSubscriptionCycles) {

		model.setDeliveryMaxSubscriptionCycles(deliveryMaxSubscriptionCycles);
	}

	/**
	 * Sets whether this cp definition is delivery subscription enabled.
	 *
	 * @param deliverySubscriptionEnabled the delivery subscription enabled of this cp definition
	 */
	@Override
	public void setDeliverySubscriptionEnabled(
		boolean deliverySubscriptionEnabled) {

		model.setDeliverySubscriptionEnabled(deliverySubscriptionEnabled);
	}

	/**
	 * Sets the delivery subscription length of this cp definition.
	 *
	 * @param deliverySubscriptionLength the delivery subscription length of this cp definition
	 */
	@Override
	public void setDeliverySubscriptionLength(int deliverySubscriptionLength) {
		model.setDeliverySubscriptionLength(deliverySubscriptionLength);
	}

	/**
	 * Sets the delivery subscription type of this cp definition.
	 *
	 * @param deliverySubscriptionType the delivery subscription type of this cp definition
	 */
	@Override
	public void setDeliverySubscriptionType(String deliverySubscriptionType) {
		model.setDeliverySubscriptionType(deliverySubscriptionType);
	}

	/**
	 * Sets the delivery subscription type settings of this cp definition.
	 *
	 * @param deliverySubscriptionTypeSettings the delivery subscription type settings of this cp definition
	 */
	@Override
	public void setDeliverySubscriptionTypeSettings(
		String deliverySubscriptionTypeSettings) {

		model.setDeliverySubscriptionTypeSettings(
			deliverySubscriptionTypeSettings);
	}

	@Override
	public void setDeliverySubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			deliverySubscriptionTypeSettingsUnicodeProperties) {

		model.setDeliverySubscriptionTypeSettingsUnicodeProperties(
			deliverySubscriptionTypeSettingsUnicodeProperties);
	}

	/**
	 * Sets the depth of this cp definition.
	 *
	 * @param depth the depth of this cp definition
	 */
	@Override
	public void setDepth(double depth) {
		model.setDepth(depth);
	}

	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the display date of this cp definition.
	 *
	 * @param displayDate the display date of this cp definition
	 */
	@Override
	public void setDisplayDate(Date displayDate) {
		model.setDisplayDate(displayDate);
	}

	/**
	 * Sets the expiration date of this cp definition.
	 *
	 * @param expirationDate the expiration date of this cp definition
	 */
	@Override
	public void setExpirationDate(Date expirationDate) {
		model.setExpirationDate(expirationDate);
	}

	/**
	 * Sets whether this cp definition is free shipping.
	 *
	 * @param freeShipping the free shipping of this cp definition
	 */
	@Override
	public void setFreeShipping(boolean freeShipping) {
		model.setFreeShipping(freeShipping);
	}

	/**
	 * Sets the group ID of this cp definition.
	 *
	 * @param groupId the group ID of this cp definition
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the height of this cp definition.
	 *
	 * @param height the height of this cp definition
	 */
	@Override
	public void setHeight(double height) {
		model.setHeight(height);
	}

	/**
	 * Sets whether this cp definition is ignore sku combinations.
	 *
	 * @param ignoreSKUCombinations the ignore sku combinations of this cp definition
	 */
	@Override
	public void setIgnoreSKUCombinations(boolean ignoreSKUCombinations) {
		model.setIgnoreSKUCombinations(ignoreSKUCombinations);
	}

	/**
	 * Sets the last publish date of this cp definition.
	 *
	 * @param lastPublishDate the last publish date of this cp definition
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the max subscription cycles of this cp definition.
	 *
	 * @param maxSubscriptionCycles the max subscription cycles of this cp definition
	 */
	@Override
	public void setMaxSubscriptionCycles(long maxSubscriptionCycles) {
		model.setMaxSubscriptionCycles(maxSubscriptionCycles);
	}

	/**
	 * Sets the modified date of this cp definition.
	 *
	 * @param modifiedDate the modified date of this cp definition
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this cp definition.
	 *
	 * @param mvccVersion the mvcc version of this cp definition
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the primary key of this cp definition.
	 *
	 * @param primaryKey the primary key of this cp definition
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the product type name of this cp definition.
	 *
	 * @param productTypeName the product type name of this cp definition
	 */
	@Override
	public void setProductTypeName(String productTypeName) {
		model.setProductTypeName(productTypeName);
	}

	/**
	 * Sets whether this cp definition is published.
	 *
	 * @param published the published of this cp definition
	 */
	@Override
	public void setPublished(boolean published) {
		model.setPublished(published);
	}

	/**
	 * Sets whether this cp definition is shippable.
	 *
	 * @param shippable the shippable of this cp definition
	 */
	@Override
	public void setShippable(boolean shippable) {
		model.setShippable(shippable);
	}

	/**
	 * Sets the shipping extra price of this cp definition.
	 *
	 * @param shippingExtraPrice the shipping extra price of this cp definition
	 */
	@Override
	public void setShippingExtraPrice(double shippingExtraPrice) {
		model.setShippingExtraPrice(shippingExtraPrice);
	}

	/**
	 * Sets whether this cp definition is ship separately.
	 *
	 * @param shipSeparately the ship separately of this cp definition
	 */
	@Override
	public void setShipSeparately(boolean shipSeparately) {
		model.setShipSeparately(shipSeparately);
	}

	@Override
	public void setShortDescriptionMap(
		Map<java.util.Locale, String> shortDescriptionMap) {

		model.setShortDescriptionMap(shortDescriptionMap);
	}

	/**
	 * Sets the status of this cp definition.
	 *
	 * @param status the status of this cp definition
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this cp definition.
	 *
	 * @param statusByUserId the status by user ID of this cp definition
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this cp definition.
	 *
	 * @param statusByUserName the status by user name of this cp definition
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this cp definition.
	 *
	 * @param statusByUserUuid the status by user uuid of this cp definition
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this cp definition.
	 *
	 * @param statusDate the status date of this cp definition
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets whether this cp definition is subscription enabled.
	 *
	 * @param subscriptionEnabled the subscription enabled of this cp definition
	 */
	@Override
	public void setSubscriptionEnabled(boolean subscriptionEnabled) {
		model.setSubscriptionEnabled(subscriptionEnabled);
	}

	/**
	 * Sets the subscription length of this cp definition.
	 *
	 * @param subscriptionLength the subscription length of this cp definition
	 */
	@Override
	public void setSubscriptionLength(int subscriptionLength) {
		model.setSubscriptionLength(subscriptionLength);
	}

	/**
	 * Sets the subscription type of this cp definition.
	 *
	 * @param subscriptionType the subscription type of this cp definition
	 */
	@Override
	public void setSubscriptionType(String subscriptionType) {
		model.setSubscriptionType(subscriptionType);
	}

	/**
	 * Sets the subscription type settings of this cp definition.
	 *
	 * @param subscriptionTypeSettings the subscription type settings of this cp definition
	 */
	@Override
	public void setSubscriptionTypeSettings(String subscriptionTypeSettings) {
		model.setSubscriptionTypeSettings(subscriptionTypeSettings);
	}

	@Override
	public void setSubscriptionTypeSettingsUnicodeProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			subscriptionTypeSettingsUnicodeProperties) {

		model.setSubscriptionTypeSettingsUnicodeProperties(
			subscriptionTypeSettingsUnicodeProperties);
	}

	/**
	 * Sets whether this cp definition is tax exempt.
	 *
	 * @param taxExempt the tax exempt of this cp definition
	 */
	@Override
	public void setTaxExempt(boolean taxExempt) {
		model.setTaxExempt(taxExempt);
	}

	/**
	 * Sets whether this cp definition is telco or electronics.
	 *
	 * @param telcoOrElectronics the telco or electronics of this cp definition
	 */
	@Override
	public void setTelcoOrElectronics(boolean telcoOrElectronics) {
		model.setTelcoOrElectronics(telcoOrElectronics);
	}

	@Override
	public void setUrlTitleMap(Map<java.util.Locale, String> urlTitleMap) {
		model.setUrlTitleMap(urlTitleMap);
	}

	/**
	 * Sets the user ID of this cp definition.
	 *
	 * @param userId the user ID of this cp definition
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this cp definition.
	 *
	 * @param userName the user name of this cp definition
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this cp definition.
	 *
	 * @param userUuid the user uuid of this cp definition
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this cp definition.
	 *
	 * @param uuid the uuid of this cp definition
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this cp definition.
	 *
	 * @param version the version of this cp definition
	 */
	@Override
	public void setVersion(int version) {
		model.setVersion(version);
	}

	/**
	 * Sets the weight of this cp definition.
	 *
	 * @param weight the weight of this cp definition
	 */
	@Override
	public void setWeight(double weight) {
		model.setWeight(weight);
	}

	/**
	 * Sets the width of this cp definition.
	 *
	 * @param width the width of this cp definition
	 */
	@Override
	public void setWidth(double width) {
		model.setWidth(width);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<CPDefinition, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CPDefinition, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CPDefinitionWrapper wrap(CPDefinition cpDefinition) {
		return new CPDefinitionWrapper(cpDefinition);
	}

}