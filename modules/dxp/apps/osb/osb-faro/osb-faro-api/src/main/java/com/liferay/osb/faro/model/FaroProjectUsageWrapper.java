/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link FaroProjectUsage}.
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectUsage
 * @generated
 */
public class FaroProjectUsageWrapper
	extends BaseModelWrapper<FaroProjectUsage>
	implements FaroProjectUsage, ModelWrapper<FaroProjectUsage> {

	public FaroProjectUsageWrapper(FaroProjectUsage faroProjectUsage) {
		super(faroProjectUsage);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("faroProjectUsageId", getFaroProjectUsageId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("createTime", getCreateTime());
		attributes.put("modifiedTime", getModifiedTime());
		attributes.put("faroProjectId", getFaroProjectId());
		attributes.put("knownIndividualsCount", getKnownIndividualsCount());
		attributes.put("monthDateKey", getMonthDateKey());
		attributes.put("pageViewsCount", getPageViewsCount());
		attributes.put("usageTime", getUsageTime());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long faroProjectUsageId = (Long)attributes.get("faroProjectUsageId");

		if (faroProjectUsageId != null) {
			setFaroProjectUsageId(faroProjectUsageId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Long createTime = (Long)attributes.get("createTime");

		if (createTime != null) {
			setCreateTime(createTime);
		}

		Long modifiedTime = (Long)attributes.get("modifiedTime");

		if (modifiedTime != null) {
			setModifiedTime(modifiedTime);
		}

		Long faroProjectId = (Long)attributes.get("faroProjectId");

		if (faroProjectId != null) {
			setFaroProjectId(faroProjectId);
		}

		Long knownIndividualsCount = (Long)attributes.get(
			"knownIndividualsCount");

		if (knownIndividualsCount != null) {
			setKnownIndividualsCount(knownIndividualsCount);
		}

		String monthDateKey = (String)attributes.get("monthDateKey");

		if (monthDateKey != null) {
			setMonthDateKey(monthDateKey);
		}

		Long pageViewsCount = (Long)attributes.get("pageViewsCount");

		if (pageViewsCount != null) {
			setPageViewsCount(pageViewsCount);
		}

		Long usageTime = (Long)attributes.get("usageTime");

		if (usageTime != null) {
			setUsageTime(usageTime);
		}
	}

	@Override
	public FaroProjectUsage cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this faro project usage.
	 *
	 * @return the company ID of this faro project usage
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create time of this faro project usage.
	 *
	 * @return the create time of this faro project usage
	 */
	@Override
	public long getCreateTime() {
		return model.getCreateTime();
	}

	/**
	 * Returns the faro project ID of this faro project usage.
	 *
	 * @return the faro project ID of this faro project usage
	 */
	@Override
	public long getFaroProjectId() {
		return model.getFaroProjectId();
	}

	/**
	 * Returns the faro project usage ID of this faro project usage.
	 *
	 * @return the faro project usage ID of this faro project usage
	 */
	@Override
	public long getFaroProjectUsageId() {
		return model.getFaroProjectUsageId();
	}

	/**
	 * Returns the known individuals count of this faro project usage.
	 *
	 * @return the known individuals count of this faro project usage
	 */
	@Override
	public long getKnownIndividualsCount() {
		return model.getKnownIndividualsCount();
	}

	/**
	 * Returns the modified time of this faro project usage.
	 *
	 * @return the modified time of this faro project usage
	 */
	@Override
	public long getModifiedTime() {
		return model.getModifiedTime();
	}

	/**
	 * Returns the month date key of this faro project usage.
	 *
	 * @return the month date key of this faro project usage
	 */
	@Override
	public String getMonthDateKey() {
		return model.getMonthDateKey();
	}

	/**
	 * Returns the mvcc version of this faro project usage.
	 *
	 * @return the mvcc version of this faro project usage
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the page views count of this faro project usage.
	 *
	 * @return the page views count of this faro project usage
	 */
	@Override
	public long getPageViewsCount() {
		return model.getPageViewsCount();
	}

	/**
	 * Returns the primary key of this faro project usage.
	 *
	 * @return the primary key of this faro project usage
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the usage time of this faro project usage.
	 *
	 * @return the usage time of this faro project usage
	 */
	@Override
	public long getUsageTime() {
		return model.getUsageTime();
	}

	/**
	 * Returns the user ID of this faro project usage.
	 *
	 * @return the user ID of this faro project usage
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this faro project usage.
	 *
	 * @return the user uuid of this faro project usage
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
	 * Sets the company ID of this faro project usage.
	 *
	 * @param companyId the company ID of this faro project usage
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create time of this faro project usage.
	 *
	 * @param createTime the create time of this faro project usage
	 */
	@Override
	public void setCreateTime(long createTime) {
		model.setCreateTime(createTime);
	}

	/**
	 * Sets the faro project ID of this faro project usage.
	 *
	 * @param faroProjectId the faro project ID of this faro project usage
	 */
	@Override
	public void setFaroProjectId(long faroProjectId) {
		model.setFaroProjectId(faroProjectId);
	}

	/**
	 * Sets the faro project usage ID of this faro project usage.
	 *
	 * @param faroProjectUsageId the faro project usage ID of this faro project usage
	 */
	@Override
	public void setFaroProjectUsageId(long faroProjectUsageId) {
		model.setFaroProjectUsageId(faroProjectUsageId);
	}

	/**
	 * Sets the known individuals count of this faro project usage.
	 *
	 * @param knownIndividualsCount the known individuals count of this faro project usage
	 */
	@Override
	public void setKnownIndividualsCount(long knownIndividualsCount) {
		model.setKnownIndividualsCount(knownIndividualsCount);
	}

	/**
	 * Sets the modified time of this faro project usage.
	 *
	 * @param modifiedTime the modified time of this faro project usage
	 */
	@Override
	public void setModifiedTime(long modifiedTime) {
		model.setModifiedTime(modifiedTime);
	}

	/**
	 * Sets the month date key of this faro project usage.
	 *
	 * @param monthDateKey the month date key of this faro project usage
	 */
	@Override
	public void setMonthDateKey(String monthDateKey) {
		model.setMonthDateKey(monthDateKey);
	}

	/**
	 * Sets the mvcc version of this faro project usage.
	 *
	 * @param mvccVersion the mvcc version of this faro project usage
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the page views count of this faro project usage.
	 *
	 * @param pageViewsCount the page views count of this faro project usage
	 */
	@Override
	public void setPageViewsCount(long pageViewsCount) {
		model.setPageViewsCount(pageViewsCount);
	}

	/**
	 * Sets the primary key of this faro project usage.
	 *
	 * @param primaryKey the primary key of this faro project usage
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the usage time of this faro project usage.
	 *
	 * @param usageTime the usage time of this faro project usage
	 */
	@Override
	public void setUsageTime(long usageTime) {
		model.setUsageTime(usageTime);
	}

	/**
	 * Sets the user ID of this faro project usage.
	 *
	 * @param userId the user ID of this faro project usage
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this faro project usage.
	 *
	 * @param userUuid the user uuid of this faro project usage
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
	protected FaroProjectUsageWrapper wrap(FaroProjectUsage faroProjectUsage) {
		return new FaroProjectUsageWrapper(faroProjectUsage);
	}

}