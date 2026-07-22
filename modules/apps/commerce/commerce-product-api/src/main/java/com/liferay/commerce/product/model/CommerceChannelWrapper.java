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
 * This class is a wrapper for {@link CommerceChannel}.
 * </p>
 *
 * @author Marco Leo
 * @see CommerceChannel
 * @generated
 */
public class CommerceChannelWrapper
	extends BaseModelWrapper<CommerceChannel>
	implements CommerceChannel, ModelWrapper<CommerceChannel> {

	public CommerceChannelWrapper(CommerceChannel commerceChannel) {
		super(commerceChannel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("commerceChannelId", getCommerceChannelId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("accountEntryId", getAccountEntryId());
		attributes.put("siteGroupId", getSiteGroupId());
		attributes.put("name", getName());
		attributes.put("type", getType());
		attributes.put("typeSettings", getTypeSettings());
		attributes.put("commerceCurrencyCode", getCommerceCurrencyCode());
		attributes.put("priceDisplayType", getPriceDisplayType());
		attributes.put("discountsTargetNetPrice", isDiscountsTargetNetPrice());
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

		Long commerceChannelId = (Long)attributes.get("commerceChannelId");

		if (commerceChannelId != null) {
			setCommerceChannelId(commerceChannelId);
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

		Long accountEntryId = (Long)attributes.get("accountEntryId");

		if (accountEntryId != null) {
			setAccountEntryId(accountEntryId);
		}

		Long siteGroupId = (Long)attributes.get("siteGroupId");

		if (siteGroupId != null) {
			setSiteGroupId(siteGroupId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String typeSettings = (String)attributes.get("typeSettings");

		if (typeSettings != null) {
			setTypeSettings(typeSettings);
		}

		String commerceCurrencyCode = (String)attributes.get(
			"commerceCurrencyCode");

		if (commerceCurrencyCode != null) {
			setCommerceCurrencyCode(commerceCurrencyCode);
		}

		String priceDisplayType = (String)attributes.get("priceDisplayType");

		if (priceDisplayType != null) {
			setPriceDisplayType(priceDisplayType);
		}

		Boolean discountsTargetNetPrice = (Boolean)attributes.get(
			"discountsTargetNetPrice");

		if (discountsTargetNetPrice != null) {
			setDiscountsTargetNetPrice(discountsTargetNetPrice);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public CommerceChannel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account entry ID of this commerce channel.
	 *
	 * @return the account entry ID of this commerce channel
	 */
	@Override
	public long getAccountEntryId() {
		return model.getAccountEntryId();
	}

	/**
	 * Returns the commerce channel ID of this commerce channel.
	 *
	 * @return the commerce channel ID of this commerce channel
	 */
	@Override
	public long getCommerceChannelId() {
		return model.getCommerceChannelId();
	}

	/**
	 * Returns the commerce currency code of this commerce channel.
	 *
	 * @return the commerce currency code of this commerce channel
	 */
	@Override
	public String getCommerceCurrencyCode() {
		return model.getCommerceCurrencyCode();
	}

	/**
	 * Returns the company ID of this commerce channel.
	 *
	 * @return the company ID of this commerce channel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this commerce channel.
	 *
	 * @return the create date of this commerce channel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this commerce channel.
	 *
	 * @return the ct collection ID of this commerce channel
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the discounts target net price of this commerce channel.
	 *
	 * @return the discounts target net price of this commerce channel
	 */
	@Override
	public boolean getDiscountsTargetNetPrice() {
		return model.getDiscountsTargetNetPrice();
	}

	/**
	 * Returns the external reference code of this commerce channel.
	 *
	 * @return the external reference code of this commerce channel
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	@Override
	public com.liferay.portal.kernel.model.Group getGroup() {
		return model.getGroup();
	}

	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the modified date of this commerce channel.
	 *
	 * @return the modified date of this commerce channel
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce channel.
	 *
	 * @return the mvcc version of this commerce channel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce channel.
	 *
	 * @return the name of this commerce channel
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the price display type of this commerce channel.
	 *
	 * @return the price display type of this commerce channel
	 */
	@Override
	public String getPriceDisplayType() {
		return model.getPriceDisplayType();
	}

	/**
	 * Returns the primary key of this commerce channel.
	 *
	 * @return the primary key of this commerce channel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the site group ID of this commerce channel.
	 *
	 * @return the site group ID of this commerce channel
	 */
	@Override
	public long getSiteGroupId() {
		return model.getSiteGroupId();
	}

	/**
	 * Returns the status of this commerce channel.
	 *
	 * @return the status of this commerce channel
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the type of this commerce channel.
	 *
	 * @return the type of this commerce channel
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the type settings of this commerce channel.
	 *
	 * @return the type settings of this commerce channel
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
	 * Returns the user ID of this commerce channel.
	 *
	 * @return the user ID of this commerce channel
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce channel.
	 *
	 * @return the user name of this commerce channel
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce channel.
	 *
	 * @return the user uuid of this commerce channel
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this commerce channel.
	 *
	 * @return the uuid of this commerce channel
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this commerce channel is discounts target net price.
	 *
	 * @return <code>true</code> if this commerce channel is discounts target net price; <code>false</code> otherwise
	 */
	@Override
	public boolean isDiscountsTargetNetPrice() {
		return model.isDiscountsTargetNetPrice();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the account entry ID of this commerce channel.
	 *
	 * @param accountEntryId the account entry ID of this commerce channel
	 */
	@Override
	public void setAccountEntryId(long accountEntryId) {
		model.setAccountEntryId(accountEntryId);
	}

	/**
	 * Sets the commerce channel ID of this commerce channel.
	 *
	 * @param commerceChannelId the commerce channel ID of this commerce channel
	 */
	@Override
	public void setCommerceChannelId(long commerceChannelId) {
		model.setCommerceChannelId(commerceChannelId);
	}

	/**
	 * Sets the commerce currency code of this commerce channel.
	 *
	 * @param commerceCurrencyCode the commerce currency code of this commerce channel
	 */
	@Override
	public void setCommerceCurrencyCode(String commerceCurrencyCode) {
		model.setCommerceCurrencyCode(commerceCurrencyCode);
	}

	/**
	 * Sets the company ID of this commerce channel.
	 *
	 * @param companyId the company ID of this commerce channel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this commerce channel.
	 *
	 * @param createDate the create date of this commerce channel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this commerce channel.
	 *
	 * @param ctCollectionId the ct collection ID of this commerce channel
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets whether this commerce channel is discounts target net price.
	 *
	 * @param discountsTargetNetPrice the discounts target net price of this commerce channel
	 */
	@Override
	public void setDiscountsTargetNetPrice(boolean discountsTargetNetPrice) {
		model.setDiscountsTargetNetPrice(discountsTargetNetPrice);
	}

	/**
	 * Sets the external reference code of this commerce channel.
	 *
	 * @param externalReferenceCode the external reference code of this commerce channel
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this commerce channel.
	 *
	 * @param modifiedDate the modified date of this commerce channel
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce channel.
	 *
	 * @param mvccVersion the mvcc version of this commerce channel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce channel.
	 *
	 * @param name the name of this commerce channel
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the price display type of this commerce channel.
	 *
	 * @param priceDisplayType the price display type of this commerce channel
	 */
	@Override
	public void setPriceDisplayType(String priceDisplayType) {
		model.setPriceDisplayType(priceDisplayType);
	}

	/**
	 * Sets the primary key of this commerce channel.
	 *
	 * @param primaryKey the primary key of this commerce channel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the site group ID of this commerce channel.
	 *
	 * @param siteGroupId the site group ID of this commerce channel
	 */
	@Override
	public void setSiteGroupId(long siteGroupId) {
		model.setSiteGroupId(siteGroupId);
	}

	/**
	 * Sets the status of this commerce channel.
	 *
	 * @param status the status of this commerce channel
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the type of this commerce channel.
	 *
	 * @param type the type of this commerce channel
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the type settings of this commerce channel.
	 *
	 * @param typeSettings the type settings of this commerce channel
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
	 * Sets the user ID of this commerce channel.
	 *
	 * @param userId the user ID of this commerce channel
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce channel.
	 *
	 * @param userName the user name of this commerce channel
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce channel.
	 *
	 * @param userUuid the user uuid of this commerce channel
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this commerce channel.
	 *
	 * @param uuid the uuid of this commerce channel
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
	public Map<String, Function<CommerceChannel, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<CommerceChannel, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CommerceChannelWrapper wrap(CommerceChannel commerceChannel) {
		return new CommerceChannelWrapper(commerceChannel);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-344525756