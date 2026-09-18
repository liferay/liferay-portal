/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CommerceDiscountRule}.
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountRule
 * @generated
 */
public class CommerceDiscountRuleWrapper
	extends BaseModelWrapper<CommerceDiscountRule>
	implements CommerceDiscountRule, ModelWrapper<CommerceDiscountRule> {

	public CommerceDiscountRuleWrapper(
		CommerceDiscountRule commerceDiscountRule) {

		super(commerceDiscountRule);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("commerceDiscountRuleId", getCommerceDiscountRuleId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("commerceDiscountId", getCommerceDiscountId());
		attributes.put("type", getType());
		attributes.put("typeSettings", getTypeSettings());

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

		Long commerceDiscountRuleId = (Long)attributes.get(
			"commerceDiscountRuleId");

		if (commerceDiscountRuleId != null) {
			setCommerceDiscountRuleId(commerceDiscountRuleId);
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

		Long commerceDiscountId = (Long)attributes.get("commerceDiscountId");

		if (commerceDiscountId != null) {
			setCommerceDiscountId(commerceDiscountId);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String typeSettings = (String)attributes.get("typeSettings");

		if (typeSettings != null) {
			setTypeSettings(typeSettings);
		}
	}

	@Override
	public CommerceDiscountRule cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the commerce discount ID of this commerce discount rule.
	 *
	 * @return the commerce discount ID of this commerce discount rule
	 */
	@Override
	public long getCommerceDiscountId() {
		return model.getCommerceDiscountId();
	}

	/**
	 * Returns the commerce discount rule ID of this commerce discount rule.
	 *
	 * @return the commerce discount rule ID of this commerce discount rule
	 */
	@Override
	public long getCommerceDiscountRuleId() {
		return model.getCommerceDiscountRuleId();
	}

	/**
	 * Returns the company ID of this commerce discount rule.
	 *
	 * @return the company ID of this commerce discount rule
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this commerce discount rule.
	 *
	 * @return the create date of this commerce discount rule
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this commerce discount rule.
	 *
	 * @return the external reference code of this commerce discount rule
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this commerce discount rule.
	 *
	 * @return the modified date of this commerce discount rule
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce discount rule.
	 *
	 * @return the mvcc version of this commerce discount rule
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce discount rule.
	 *
	 * @return the name of this commerce discount rule
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this commerce discount rule.
	 *
	 * @return the primary key of this commerce discount rule
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getSettingsProperties() {

		return model.getSettingsProperties();
	}

	@Override
	public String getSettingsProperty(String key) {
		return model.getSettingsProperty(key);
	}

	/**
	 * Returns the type of this commerce discount rule.
	 *
	 * @return the type of this commerce discount rule
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the type settings of this commerce discount rule.
	 *
	 * @return the type settings of this commerce discount rule
	 */
	@Override
	public String getTypeSettings() {
		return model.getTypeSettings();
	}

	/**
	 * Returns the user ID of this commerce discount rule.
	 *
	 * @return the user ID of this commerce discount rule
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce discount rule.
	 *
	 * @return the user name of this commerce discount rule
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce discount rule.
	 *
	 * @return the user uuid of this commerce discount rule
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the commerce discount ID of this commerce discount rule.
	 *
	 * @param commerceDiscountId the commerce discount ID of this commerce discount rule
	 */
	@Override
	public void setCommerceDiscountId(long commerceDiscountId) {
		model.setCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Sets the commerce discount rule ID of this commerce discount rule.
	 *
	 * @param commerceDiscountRuleId the commerce discount rule ID of this commerce discount rule
	 */
	@Override
	public void setCommerceDiscountRuleId(long commerceDiscountRuleId) {
		model.setCommerceDiscountRuleId(commerceDiscountRuleId);
	}

	/**
	 * Sets the company ID of this commerce discount rule.
	 *
	 * @param companyId the company ID of this commerce discount rule
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this commerce discount rule.
	 *
	 * @param createDate the create date of this commerce discount rule
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this commerce discount rule.
	 *
	 * @param externalReferenceCode the external reference code of this commerce discount rule
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this commerce discount rule.
	 *
	 * @param modifiedDate the modified date of this commerce discount rule
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce discount rule.
	 *
	 * @param mvccVersion the mvcc version of this commerce discount rule
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce discount rule.
	 *
	 * @param name the name of this commerce discount rule
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this commerce discount rule.
	 *
	 * @param primaryKey the primary key of this commerce discount rule
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	public void setSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties unicodeProperties) {

		model.setSettingsProperties(unicodeProperties);
	}

	/**
	 * Sets the type of this commerce discount rule.
	 *
	 * @param type the type of this commerce discount rule
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the type settings of this commerce discount rule.
	 *
	 * @param typeSettings the type settings of this commerce discount rule
	 */
	@Override
	public void setTypeSettings(String typeSettings) {
		model.setTypeSettings(typeSettings);
	}

	/**
	 * Sets the user ID of this commerce discount rule.
	 *
	 * @param userId the user ID of this commerce discount rule
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce discount rule.
	 *
	 * @param userName the user name of this commerce discount rule
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce discount rule.
	 *
	 * @param userUuid the user uuid of this commerce discount rule
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
	protected CommerceDiscountRuleWrapper wrap(
		CommerceDiscountRule commerceDiscountRule) {

		return new CommerceDiscountRuleWrapper(commerceDiscountRule);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1564444546