/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ProductionReadinessIgnore}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnore
 * @generated
 */
public class ProductionReadinessIgnoreWrapper
	extends BaseModelWrapper<ProductionReadinessIgnore>
	implements ModelWrapper<ProductionReadinessIgnore>,
			   ProductionReadinessIgnore {

	public ProductionReadinessIgnoreWrapper(
		ProductionReadinessIgnore productionReadinessIgnore) {

		super(productionReadinessIgnore);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put(
			"productionReadinessIgnoreId", getProductionReadinessIgnoreId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("ruleKey", getRuleKey());
		attributes.put("reason", getReason());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long productionReadinessIgnoreId = (Long)attributes.get(
			"productionReadinessIgnoreId");

		if (productionReadinessIgnoreId != null) {
			setProductionReadinessIgnoreId(productionReadinessIgnoreId);
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

		String ruleKey = (String)attributes.get("ruleKey");

		if (ruleKey != null) {
			setRuleKey(ruleKey);
		}

		String reason = (String)attributes.get("reason");

		if (reason != null) {
			setReason(reason);
		}
	}

	@Override
	public ProductionReadinessIgnore cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this production readiness ignore.
	 *
	 * @return the company ID of this production readiness ignore
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this production readiness ignore.
	 *
	 * @return the create date of this production readiness ignore
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this production readiness ignore.
	 *
	 * @return the modified date of this production readiness ignore
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the primary key of this production readiness ignore.
	 *
	 * @return the primary key of this production readiness ignore
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the production readiness ignore ID of this production readiness ignore.
	 *
	 * @return the production readiness ignore ID of this production readiness ignore
	 */
	@Override
	public long getProductionReadinessIgnoreId() {
		return model.getProductionReadinessIgnoreId();
	}

	/**
	 * Returns the reason of this production readiness ignore.
	 *
	 * @return the reason of this production readiness ignore
	 */
	@Override
	public String getReason() {
		return model.getReason();
	}

	/**
	 * Returns the rule key of this production readiness ignore.
	 *
	 * @return the rule key of this production readiness ignore
	 */
	@Override
	public String getRuleKey() {
		return model.getRuleKey();
	}

	/**
	 * Returns the user ID of this production readiness ignore.
	 *
	 * @return the user ID of this production readiness ignore
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this production readiness ignore.
	 *
	 * @return the user name of this production readiness ignore
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this production readiness ignore.
	 *
	 * @return the user uuid of this production readiness ignore
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
	 * Sets the company ID of this production readiness ignore.
	 *
	 * @param companyId the company ID of this production readiness ignore
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this production readiness ignore.
	 *
	 * @param createDate the create date of this production readiness ignore
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this production readiness ignore.
	 *
	 * @param modifiedDate the modified date of this production readiness ignore
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the primary key of this production readiness ignore.
	 *
	 * @param primaryKey the primary key of this production readiness ignore
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the production readiness ignore ID of this production readiness ignore.
	 *
	 * @param productionReadinessIgnoreId the production readiness ignore ID of this production readiness ignore
	 */
	@Override
	public void setProductionReadinessIgnoreId(
		long productionReadinessIgnoreId) {

		model.setProductionReadinessIgnoreId(productionReadinessIgnoreId);
	}

	/**
	 * Sets the reason of this production readiness ignore.
	 *
	 * @param reason the reason of this production readiness ignore
	 */
	@Override
	public void setReason(String reason) {
		model.setReason(reason);
	}

	/**
	 * Sets the rule key of this production readiness ignore.
	 *
	 * @param ruleKey the rule key of this production readiness ignore
	 */
	@Override
	public void setRuleKey(String ruleKey) {
		model.setRuleKey(ruleKey);
	}

	/**
	 * Sets the user ID of this production readiness ignore.
	 *
	 * @param userId the user ID of this production readiness ignore
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this production readiness ignore.
	 *
	 * @param userName the user name of this production readiness ignore
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this production readiness ignore.
	 *
	 * @param userUuid the user uuid of this production readiness ignore
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
	protected ProductionReadinessIgnoreWrapper wrap(
		ProductionReadinessIgnore productionReadinessIgnore) {

		return new ProductionReadinessIgnoreWrapper(productionReadinessIgnore);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:707049569