/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link FaroDataSourceUsage}.
 * </p>
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsage
 * @generated
 */
public class FaroDataSourceUsageWrapper
	extends BaseModelWrapper<FaroDataSourceUsage>
	implements FaroDataSourceUsage, ModelWrapper<FaroDataSourceUsage> {

	public FaroDataSourceUsageWrapper(FaroDataSourceUsage faroDataSourceUsage) {
		super(faroDataSourceUsage);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("faroDataSourceUsageId", getFaroDataSourceUsageId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("createTime", getCreateTime());
		attributes.put("modifiedTime", getModifiedTime());
		attributes.put("billableEventsCount", getBillableEventsCount());
		attributes.put("dataSourceId", getDataSourceId());
		attributes.put("dataSourceName", getDataSourceName());
		attributes.put("dataSourceStatus", getDataSourceStatus());
		attributes.put("faroProjectId", getFaroProjectId());
		attributes.put("knownIndividualsCount", getKnownIndividualsCount());
		attributes.put("usageTime", getUsageTime());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long faroDataSourceUsageId = (Long)attributes.get(
			"faroDataSourceUsageId");

		if (faroDataSourceUsageId != null) {
			setFaroDataSourceUsageId(faroDataSourceUsageId);
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

		Long billableEventsCount = (Long)attributes.get("billableEventsCount");

		if (billableEventsCount != null) {
			setBillableEventsCount(billableEventsCount);
		}

		Long dataSourceId = (Long)attributes.get("dataSourceId");

		if (dataSourceId != null) {
			setDataSourceId(dataSourceId);
		}

		String dataSourceName = (String)attributes.get("dataSourceName");

		if (dataSourceName != null) {
			setDataSourceName(dataSourceName);
		}

		String dataSourceStatus = (String)attributes.get("dataSourceStatus");

		if (dataSourceStatus != null) {
			setDataSourceStatus(dataSourceStatus);
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

		Long usageTime = (Long)attributes.get("usageTime");

		if (usageTime != null) {
			setUsageTime(usageTime);
		}
	}

	@Override
	public FaroDataSourceUsage cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the billable events count of this faro data source usage.
	 *
	 * @return the billable events count of this faro data source usage
	 */
	@Override
	public long getBillableEventsCount() {
		return model.getBillableEventsCount();
	}

	/**
	 * Returns the company ID of this faro data source usage.
	 *
	 * @return the company ID of this faro data source usage
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create time of this faro data source usage.
	 *
	 * @return the create time of this faro data source usage
	 */
	@Override
	public long getCreateTime() {
		return model.getCreateTime();
	}

	/**
	 * Returns the data source ID of this faro data source usage.
	 *
	 * @return the data source ID of this faro data source usage
	 */
	@Override
	public long getDataSourceId() {
		return model.getDataSourceId();
	}

	/**
	 * Returns the data source name of this faro data source usage.
	 *
	 * @return the data source name of this faro data source usage
	 */
	@Override
	public String getDataSourceName() {
		return model.getDataSourceName();
	}

	/**
	 * Returns the data source status of this faro data source usage.
	 *
	 * @return the data source status of this faro data source usage
	 */
	@Override
	public String getDataSourceStatus() {
		return model.getDataSourceStatus();
	}

	/**
	 * Returns the faro data source usage ID of this faro data source usage.
	 *
	 * @return the faro data source usage ID of this faro data source usage
	 */
	@Override
	public long getFaroDataSourceUsageId() {
		return model.getFaroDataSourceUsageId();
	}

	/**
	 * Returns the faro project ID of this faro data source usage.
	 *
	 * @return the faro project ID of this faro data source usage
	 */
	@Override
	public long getFaroProjectId() {
		return model.getFaroProjectId();
	}

	/**
	 * Returns the known individuals count of this faro data source usage.
	 *
	 * @return the known individuals count of this faro data source usage
	 */
	@Override
	public long getKnownIndividualsCount() {
		return model.getKnownIndividualsCount();
	}

	/**
	 * Returns the modified time of this faro data source usage.
	 *
	 * @return the modified time of this faro data source usage
	 */
	@Override
	public long getModifiedTime() {
		return model.getModifiedTime();
	}

	/**
	 * Returns the mvcc version of this faro data source usage.
	 *
	 * @return the mvcc version of this faro data source usage
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this faro data source usage.
	 *
	 * @return the primary key of this faro data source usage
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the usage time of this faro data source usage.
	 *
	 * @return the usage time of this faro data source usage
	 */
	@Override
	public long getUsageTime() {
		return model.getUsageTime();
	}

	/**
	 * Returns the user ID of this faro data source usage.
	 *
	 * @return the user ID of this faro data source usage
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this faro data source usage.
	 *
	 * @return the user uuid of this faro data source usage
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
	 * Sets the billable events count of this faro data source usage.
	 *
	 * @param billableEventsCount the billable events count of this faro data source usage
	 */
	@Override
	public void setBillableEventsCount(long billableEventsCount) {
		model.setBillableEventsCount(billableEventsCount);
	}

	/**
	 * Sets the company ID of this faro data source usage.
	 *
	 * @param companyId the company ID of this faro data source usage
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create time of this faro data source usage.
	 *
	 * @param createTime the create time of this faro data source usage
	 */
	@Override
	public void setCreateTime(long createTime) {
		model.setCreateTime(createTime);
	}

	/**
	 * Sets the data source ID of this faro data source usage.
	 *
	 * @param dataSourceId the data source ID of this faro data source usage
	 */
	@Override
	public void setDataSourceId(long dataSourceId) {
		model.setDataSourceId(dataSourceId);
	}

	/**
	 * Sets the data source name of this faro data source usage.
	 *
	 * @param dataSourceName the data source name of this faro data source usage
	 */
	@Override
	public void setDataSourceName(String dataSourceName) {
		model.setDataSourceName(dataSourceName);
	}

	/**
	 * Sets the data source status of this faro data source usage.
	 *
	 * @param dataSourceStatus the data source status of this faro data source usage
	 */
	@Override
	public void setDataSourceStatus(String dataSourceStatus) {
		model.setDataSourceStatus(dataSourceStatus);
	}

	/**
	 * Sets the faro data source usage ID of this faro data source usage.
	 *
	 * @param faroDataSourceUsageId the faro data source usage ID of this faro data source usage
	 */
	@Override
	public void setFaroDataSourceUsageId(long faroDataSourceUsageId) {
		model.setFaroDataSourceUsageId(faroDataSourceUsageId);
	}

	/**
	 * Sets the faro project ID of this faro data source usage.
	 *
	 * @param faroProjectId the faro project ID of this faro data source usage
	 */
	@Override
	public void setFaroProjectId(long faroProjectId) {
		model.setFaroProjectId(faroProjectId);
	}

	/**
	 * Sets the known individuals count of this faro data source usage.
	 *
	 * @param knownIndividualsCount the known individuals count of this faro data source usage
	 */
	@Override
	public void setKnownIndividualsCount(long knownIndividualsCount) {
		model.setKnownIndividualsCount(knownIndividualsCount);
	}

	/**
	 * Sets the modified time of this faro data source usage.
	 *
	 * @param modifiedTime the modified time of this faro data source usage
	 */
	@Override
	public void setModifiedTime(long modifiedTime) {
		model.setModifiedTime(modifiedTime);
	}

	/**
	 * Sets the mvcc version of this faro data source usage.
	 *
	 * @param mvccVersion the mvcc version of this faro data source usage
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this faro data source usage.
	 *
	 * @param primaryKey the primary key of this faro data source usage
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the usage time of this faro data source usage.
	 *
	 * @param usageTime the usage time of this faro data source usage
	 */
	@Override
	public void setUsageTime(long usageTime) {
		model.setUsageTime(usageTime);
	}

	/**
	 * Sets the user ID of this faro data source usage.
	 *
	 * @param userId the user ID of this faro data source usage
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this faro data source usage.
	 *
	 * @param userUuid the user uuid of this faro data source usage
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
	protected FaroDataSourceUsageWrapper wrap(
		FaroDataSourceUsage faroDataSourceUsage) {

		return new FaroDataSourceUsageWrapper(faroDataSourceUsage);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:419811222