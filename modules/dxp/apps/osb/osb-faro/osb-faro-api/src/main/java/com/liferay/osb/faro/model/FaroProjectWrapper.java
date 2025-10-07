/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link FaroProject}.
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProject
 * @generated
 */
public class FaroProjectWrapper
	extends BaseModelWrapper<FaroProject>
	implements FaroProject, ModelWrapper<FaroProject> {

	public FaroProjectWrapper(FaroProject faroProject) {
		super(faroProject);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("faroProjectId", getFaroProjectId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createTime", getCreateTime());
		attributes.put("modifiedTime", getModifiedTime());
		attributes.put("name", getName());
		attributes.put("accountKey", getAccountKey());
		attributes.put("accountName", getAccountName());
		attributes.put("corpProjectName", getCorpProjectName());
		attributes.put("corpProjectUuid", getCorpProjectUuid());
		attributes.put("dataSourceConnected", isDataSourceConnected());
		attributes.put("ipAddresses", getIpAddresses());
		attributes.put(
			"incidentReportEmailAddresses", getIncidentReportEmailAddresses());
		attributes.put("lastAccessTime", getLastAccessTime());
		attributes.put("recommendationsEnabled", isRecommendationsEnabled());
		attributes.put("serverLocation", getServerLocation());
		attributes.put("services", getServices());
		attributes.put("state", getState());
		attributes.put("subscription", getSubscription());
		attributes.put(
			"subscriptionModifiedTime", getSubscriptionModifiedTime());
		attributes.put("timeZoneId", getTimeZoneId());
		attributes.put("weDeployKey", getWeDeployKey());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long faroProjectId = (Long)attributes.get("faroProjectId");

		if (faroProjectId != null) {
			setFaroProjectId(faroProjectId);
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

		Long createTime = (Long)attributes.get("createTime");

		if (createTime != null) {
			setCreateTime(createTime);
		}

		Long modifiedTime = (Long)attributes.get("modifiedTime");

		if (modifiedTime != null) {
			setModifiedTime(modifiedTime);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String accountKey = (String)attributes.get("accountKey");

		if (accountKey != null) {
			setAccountKey(accountKey);
		}

		String accountName = (String)attributes.get("accountName");

		if (accountName != null) {
			setAccountName(accountName);
		}

		String corpProjectName = (String)attributes.get("corpProjectName");

		if (corpProjectName != null) {
			setCorpProjectName(corpProjectName);
		}

		String corpProjectUuid = (String)attributes.get("corpProjectUuid");

		if (corpProjectUuid != null) {
			setCorpProjectUuid(corpProjectUuid);
		}

		Boolean dataSourceConnected = (Boolean)attributes.get(
			"dataSourceConnected");

		if (dataSourceConnected != null) {
			setDataSourceConnected(dataSourceConnected);
		}

		String ipAddresses = (String)attributes.get("ipAddresses");

		if (ipAddresses != null) {
			setIpAddresses(ipAddresses);
		}

		String incidentReportEmailAddresses = (String)attributes.get(
			"incidentReportEmailAddresses");

		if (incidentReportEmailAddresses != null) {
			setIncidentReportEmailAddresses(incidentReportEmailAddresses);
		}

		Long lastAccessTime = (Long)attributes.get("lastAccessTime");

		if (lastAccessTime != null) {
			setLastAccessTime(lastAccessTime);
		}

		Boolean recommendationsEnabled = (Boolean)attributes.get(
			"recommendationsEnabled");

		if (recommendationsEnabled != null) {
			setRecommendationsEnabled(recommendationsEnabled);
		}

		String serverLocation = (String)attributes.get("serverLocation");

		if (serverLocation != null) {
			setServerLocation(serverLocation);
		}

		String services = (String)attributes.get("services");

		if (services != null) {
			setServices(services);
		}

		String state = (String)attributes.get("state");

		if (state != null) {
			setState(state);
		}

		String subscription = (String)attributes.get("subscription");

		if (subscription != null) {
			setSubscription(subscription);
		}

		Long subscriptionModifiedTime = (Long)attributes.get(
			"subscriptionModifiedTime");

		if (subscriptionModifiedTime != null) {
			setSubscriptionModifiedTime(subscriptionModifiedTime);
		}

		String timeZoneId = (String)attributes.get("timeZoneId");

		if (timeZoneId != null) {
			setTimeZoneId(timeZoneId);
		}

		String weDeployKey = (String)attributes.get("weDeployKey");

		if (weDeployKey != null) {
			setWeDeployKey(weDeployKey);
		}
	}

	@Override
	public FaroProject cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account key of this faro project.
	 *
	 * @return the account key of this faro project
	 */
	@Override
	public String getAccountKey() {
		return model.getAccountKey();
	}

	/**
	 * Returns the account name of this faro project.
	 *
	 * @return the account name of this faro project
	 */
	@Override
	public String getAccountName() {
		return model.getAccountName();
	}

	/**
	 * Returns the company ID of this faro project.
	 *
	 * @return the company ID of this faro project
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the corp project name of this faro project.
	 *
	 * @return the corp project name of this faro project
	 */
	@Override
	public String getCorpProjectName() {
		return model.getCorpProjectName();
	}

	/**
	 * Returns the corp project uuid of this faro project.
	 *
	 * @return the corp project uuid of this faro project
	 */
	@Override
	public String getCorpProjectUuid() {
		return model.getCorpProjectUuid();
	}

	/**
	 * Returns the create time of this faro project.
	 *
	 * @return the create time of this faro project
	 */
	@Override
	public long getCreateTime() {
		return model.getCreateTime();
	}

	/**
	 * Returns the data source connected of this faro project.
	 *
	 * @return the data source connected of this faro project
	 */
	@Override
	public boolean getDataSourceConnected() {
		return model.getDataSourceConnected();
	}

	/**
	 * Returns the faro project ID of this faro project.
	 *
	 * @return the faro project ID of this faro project
	 */
	@Override
	public long getFaroProjectId() {
		return model.getFaroProjectId();
	}

	/**
	 * Returns the group ID of this faro project.
	 *
	 * @return the group ID of this faro project
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the incident report email addresses of this faro project.
	 *
	 * @return the incident report email addresses of this faro project
	 */
	@Override
	public String getIncidentReportEmailAddresses() {
		return model.getIncidentReportEmailAddresses();
	}

	/**
	 * Returns the ip addresses of this faro project.
	 *
	 * @return the ip addresses of this faro project
	 */
	@Override
	public String getIpAddresses() {
		return model.getIpAddresses();
	}

	/**
	 * Returns the last access time of this faro project.
	 *
	 * @return the last access time of this faro project
	 */
	@Override
	public long getLastAccessTime() {
		return model.getLastAccessTime();
	}

	@Override
	public Date getLastAnniversaryDate() throws Exception {
		return model.getLastAnniversaryDate();
	}

	/**
	 * Returns the modified time of this faro project.
	 *
	 * @return the modified time of this faro project
	 */
	@Override
	public long getModifiedTime() {
		return model.getModifiedTime();
	}

	/**
	 * Returns the mvcc version of this faro project.
	 *
	 * @return the mvcc version of this faro project
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this faro project.
	 *
	 * @return the name of this faro project
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this faro project.
	 *
	 * @return the primary key of this faro project
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public String getProjectId() {
		return model.getProjectId();
	}

	/**
	 * Returns the recommendations enabled of this faro project.
	 *
	 * @return the recommendations enabled of this faro project
	 */
	@Override
	public boolean getRecommendationsEnabled() {
		return model.getRecommendationsEnabled();
	}

	/**
	 * Returns the server location of this faro project.
	 *
	 * @return the server location of this faro project
	 */
	@Override
	public String getServerLocation() {
		return model.getServerLocation();
	}

	/**
	 * Returns the services of this faro project.
	 *
	 * @return the services of this faro project
	 */
	@Override
	public String getServices() {
		return model.getServices();
	}

	@Override
	public Date getStartDate() throws Exception {
		return model.getStartDate();
	}

	/**
	 * Returns the state of this faro project.
	 *
	 * @return the state of this faro project
	 */
	@Override
	public String getState() {
		return model.getState();
	}

	/**
	 * Returns the subscription of this faro project.
	 *
	 * @return the subscription of this faro project
	 */
	@Override
	public String getSubscription() {
		return model.getSubscription();
	}

	/**
	 * Returns the subscription modified time of this faro project.
	 *
	 * @return the subscription modified time of this faro project
	 */
	@Override
	public long getSubscriptionModifiedTime() {
		return model.getSubscriptionModifiedTime();
	}

	@Override
	public String getSubscriptionName() throws Exception {
		return model.getSubscriptionName();
	}

	/**
	 * Returns the time zone ID of this faro project.
	 *
	 * @return the time zone ID of this faro project
	 */
	@Override
	public String getTimeZoneId() {
		return model.getTimeZoneId();
	}

	/**
	 * Returns the user ID of this faro project.
	 *
	 * @return the user ID of this faro project
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this faro project.
	 *
	 * @return the user name of this faro project
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this faro project.
	 *
	 * @return the user uuid of this faro project
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the we deploy key of this faro project.
	 *
	 * @return the we deploy key of this faro project
	 */
	@Override
	public String getWeDeployKey() {
		return model.getWeDeployKey();
	}

	@Override
	public boolean isAllowedIPAddress(String ipAddress) {
		return model.isAllowedIPAddress(ipAddress);
	}

	/**
	 * Returns <code>true</code> if this faro project is data source connected.
	 *
	 * @return <code>true</code> if this faro project is data source connected; <code>false</code> otherwise
	 */
	@Override
	public boolean isDataSourceConnected() {
		return model.isDataSourceConnected();
	}

	/**
	 * Returns <code>true</code> if this faro project is recommendations enabled.
	 *
	 * @return <code>true</code> if this faro project is recommendations enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isRecommendationsEnabled() {
		return model.isRecommendationsEnabled();
	}

	@Override
	public boolean isTrial() {
		return model.isTrial();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the account key of this faro project.
	 *
	 * @param accountKey the account key of this faro project
	 */
	@Override
	public void setAccountKey(String accountKey) {
		model.setAccountKey(accountKey);
	}

	/**
	 * Sets the account name of this faro project.
	 *
	 * @param accountName the account name of this faro project
	 */
	@Override
	public void setAccountName(String accountName) {
		model.setAccountName(accountName);
	}

	/**
	 * Sets the company ID of this faro project.
	 *
	 * @param companyId the company ID of this faro project
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the corp project name of this faro project.
	 *
	 * @param corpProjectName the corp project name of this faro project
	 */
	@Override
	public void setCorpProjectName(String corpProjectName) {
		model.setCorpProjectName(corpProjectName);
	}

	/**
	 * Sets the corp project uuid of this faro project.
	 *
	 * @param corpProjectUuid the corp project uuid of this faro project
	 */
	@Override
	public void setCorpProjectUuid(String corpProjectUuid) {
		model.setCorpProjectUuid(corpProjectUuid);
	}

	/**
	 * Sets the create time of this faro project.
	 *
	 * @param createTime the create time of this faro project
	 */
	@Override
	public void setCreateTime(long createTime) {
		model.setCreateTime(createTime);
	}

	/**
	 * Sets whether this faro project is data source connected.
	 *
	 * @param dataSourceConnected the data source connected of this faro project
	 */
	@Override
	public void setDataSourceConnected(boolean dataSourceConnected) {
		model.setDataSourceConnected(dataSourceConnected);
	}

	/**
	 * Sets the faro project ID of this faro project.
	 *
	 * @param faroProjectId the faro project ID of this faro project
	 */
	@Override
	public void setFaroProjectId(long faroProjectId) {
		model.setFaroProjectId(faroProjectId);
	}

	/**
	 * Sets the group ID of this faro project.
	 *
	 * @param groupId the group ID of this faro project
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the incident report email addresses of this faro project.
	 *
	 * @param incidentReportEmailAddresses the incident report email addresses of this faro project
	 */
	@Override
	public void setIncidentReportEmailAddresses(
		String incidentReportEmailAddresses) {

		model.setIncidentReportEmailAddresses(incidentReportEmailAddresses);
	}

	/**
	 * Sets the ip addresses of this faro project.
	 *
	 * @param ipAddresses the ip addresses of this faro project
	 */
	@Override
	public void setIpAddresses(String ipAddresses) {
		model.setIpAddresses(ipAddresses);
	}

	/**
	 * Sets the last access time of this faro project.
	 *
	 * @param lastAccessTime the last access time of this faro project
	 */
	@Override
	public void setLastAccessTime(long lastAccessTime) {
		model.setLastAccessTime(lastAccessTime);
	}

	/**
	 * Sets the modified time of this faro project.
	 *
	 * @param modifiedTime the modified time of this faro project
	 */
	@Override
	public void setModifiedTime(long modifiedTime) {
		model.setModifiedTime(modifiedTime);
	}

	/**
	 * Sets the mvcc version of this faro project.
	 *
	 * @param mvccVersion the mvcc version of this faro project
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this faro project.
	 *
	 * @param name the name of this faro project
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this faro project.
	 *
	 * @param primaryKey the primary key of this faro project
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this faro project is recommendations enabled.
	 *
	 * @param recommendationsEnabled the recommendations enabled of this faro project
	 */
	@Override
	public void setRecommendationsEnabled(boolean recommendationsEnabled) {
		model.setRecommendationsEnabled(recommendationsEnabled);
	}

	/**
	 * Sets the server location of this faro project.
	 *
	 * @param serverLocation the server location of this faro project
	 */
	@Override
	public void setServerLocation(String serverLocation) {
		model.setServerLocation(serverLocation);
	}

	/**
	 * Sets the services of this faro project.
	 *
	 * @param services the services of this faro project
	 */
	@Override
	public void setServices(String services) {
		model.setServices(services);
	}

	/**
	 * Sets the state of this faro project.
	 *
	 * @param state the state of this faro project
	 */
	@Override
	public void setState(String state) {
		model.setState(state);
	}

	/**
	 * Sets the subscription of this faro project.
	 *
	 * @param subscription the subscription of this faro project
	 */
	@Override
	public void setSubscription(String subscription) {
		model.setSubscription(subscription);
	}

	/**
	 * Sets the subscription modified time of this faro project.
	 *
	 * @param subscriptionModifiedTime the subscription modified time of this faro project
	 */
	@Override
	public void setSubscriptionModifiedTime(long subscriptionModifiedTime) {
		model.setSubscriptionModifiedTime(subscriptionModifiedTime);
	}

	/**
	 * Sets the time zone ID of this faro project.
	 *
	 * @param timeZoneId the time zone ID of this faro project
	 */
	@Override
	public void setTimeZoneId(String timeZoneId) {
		model.setTimeZoneId(timeZoneId);
	}

	/**
	 * Sets the user ID of this faro project.
	 *
	 * @param userId the user ID of this faro project
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this faro project.
	 *
	 * @param userName the user name of this faro project
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this faro project.
	 *
	 * @param userUuid the user uuid of this faro project
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the we deploy key of this faro project.
	 *
	 * @param weDeployKey the we deploy key of this faro project
	 */
	@Override
	public void setWeDeployKey(String weDeployKey) {
		model.setWeDeployKey(weDeployKey);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected FaroProjectWrapper wrap(FaroProject faroProject) {
		return new FaroProjectWrapper(faroProject);
	}

}