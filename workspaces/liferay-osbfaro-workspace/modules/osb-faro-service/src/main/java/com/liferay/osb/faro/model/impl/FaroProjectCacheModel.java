/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model.impl;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing FaroProject in entity cache.
 *
 * @author Matthew Kong
 * @generated
 */
public class FaroProjectCacheModel
	implements CacheModel<FaroProject>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FaroProjectCacheModel)) {
			return false;
		}

		FaroProjectCacheModel faroProjectCacheModel =
			(FaroProjectCacheModel)object;

		if ((faroProjectId == faroProjectCacheModel.faroProjectId) &&
			(mvccVersion == faroProjectCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, faroProjectId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(53);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", faroProjectId=");
		sb.append(faroProjectId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createTime=");
		sb.append(createTime);
		sb.append(", modifiedTime=");
		sb.append(modifiedTime);
		sb.append(", name=");
		sb.append(name);
		sb.append(", accountKey=");
		sb.append(accountKey);
		sb.append(", accountName=");
		sb.append(accountName);
		sb.append(", corpProjectName=");
		sb.append(corpProjectName);
		sb.append(", corpProjectUuid=");
		sb.append(corpProjectUuid);
		sb.append(", dataSourceConnected=");
		sb.append(dataSourceConnected);
		sb.append(", ipAddresses=");
		sb.append(ipAddresses);
		sb.append(", incidentReportEmailAddresses=");
		sb.append(incidentReportEmailAddresses);
		sb.append(", lastAccessTime=");
		sb.append(lastAccessTime);
		sb.append(", recommendationsEnabled=");
		sb.append(recommendationsEnabled);
		sb.append(", salesforceAccountId=");
		sb.append(salesforceAccountId);
		sb.append(", serverLocation=");
		sb.append(serverLocation);
		sb.append(", services=");
		sb.append(services);
		sb.append(", state=");
		sb.append(state);
		sb.append(", subscription=");
		sb.append(subscription);
		sb.append(", subscriptionModifiedTime=");
		sb.append(subscriptionModifiedTime);
		sb.append(", timeZoneId=");
		sb.append(timeZoneId);
		sb.append(", weDeployKey=");
		sb.append(weDeployKey);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FaroProject toEntityModel() {
		FaroProjectImpl faroProjectImpl = new FaroProjectImpl();

		faroProjectImpl.setMvccVersion(mvccVersion);
		faroProjectImpl.setFaroProjectId(faroProjectId);
		faroProjectImpl.setGroupId(groupId);
		faroProjectImpl.setCompanyId(companyId);
		faroProjectImpl.setUserId(userId);

		if (userName == null) {
			faroProjectImpl.setUserName("");
		}
		else {
			faroProjectImpl.setUserName(userName);
		}

		faroProjectImpl.setCreateTime(createTime);
		faroProjectImpl.setModifiedTime(modifiedTime);

		if (name == null) {
			faroProjectImpl.setName("");
		}
		else {
			faroProjectImpl.setName(name);
		}

		if (accountKey == null) {
			faroProjectImpl.setAccountKey("");
		}
		else {
			faroProjectImpl.setAccountKey(accountKey);
		}

		if (accountName == null) {
			faroProjectImpl.setAccountName("");
		}
		else {
			faroProjectImpl.setAccountName(accountName);
		}

		if (corpProjectName == null) {
			faroProjectImpl.setCorpProjectName("");
		}
		else {
			faroProjectImpl.setCorpProjectName(corpProjectName);
		}

		if (corpProjectUuid == null) {
			faroProjectImpl.setCorpProjectUuid("");
		}
		else {
			faroProjectImpl.setCorpProjectUuid(corpProjectUuid);
		}

		faroProjectImpl.setDataSourceConnected(dataSourceConnected);

		if (ipAddresses == null) {
			faroProjectImpl.setIpAddresses("");
		}
		else {
			faroProjectImpl.setIpAddresses(ipAddresses);
		}

		if (incidentReportEmailAddresses == null) {
			faroProjectImpl.setIncidentReportEmailAddresses("");
		}
		else {
			faroProjectImpl.setIncidentReportEmailAddresses(
				incidentReportEmailAddresses);
		}

		faroProjectImpl.setLastAccessTime(lastAccessTime);
		faroProjectImpl.setRecommendationsEnabled(recommendationsEnabled);

		if (salesforceAccountId == null) {
			faroProjectImpl.setSalesforceAccountId("");
		}
		else {
			faroProjectImpl.setSalesforceAccountId(salesforceAccountId);
		}

		if (serverLocation == null) {
			faroProjectImpl.setServerLocation("");
		}
		else {
			faroProjectImpl.setServerLocation(serverLocation);
		}

		if (services == null) {
			faroProjectImpl.setServices("");
		}
		else {
			faroProjectImpl.setServices(services);
		}

		if (state == null) {
			faroProjectImpl.setState("");
		}
		else {
			faroProjectImpl.setState(state);
		}

		if (subscription == null) {
			faroProjectImpl.setSubscription("");
		}
		else {
			faroProjectImpl.setSubscription(subscription);
		}

		faroProjectImpl.setSubscriptionModifiedTime(subscriptionModifiedTime);

		if (timeZoneId == null) {
			faroProjectImpl.setTimeZoneId("");
		}
		else {
			faroProjectImpl.setTimeZoneId(timeZoneId);
		}

		if (weDeployKey == null) {
			faroProjectImpl.setWeDeployKey("");
		}
		else {
			faroProjectImpl.setWeDeployKey(weDeployKey);
		}

		faroProjectImpl.resetOriginalValues();

		return faroProjectImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		faroProjectId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();

		createTime = objectInput.readLong();

		modifiedTime = objectInput.readLong();
		name = objectInput.readUTF();
		accountKey = objectInput.readUTF();
		accountName = objectInput.readUTF();
		corpProjectName = objectInput.readUTF();
		corpProjectUuid = objectInput.readUTF();

		dataSourceConnected = objectInput.readBoolean();
		ipAddresses = objectInput.readUTF();
		incidentReportEmailAddresses = objectInput.readUTF();

		lastAccessTime = objectInput.readLong();

		recommendationsEnabled = objectInput.readBoolean();
		salesforceAccountId = objectInput.readUTF();
		serverLocation = objectInput.readUTF();
		services = objectInput.readUTF();
		state = objectInput.readUTF();
		subscription = objectInput.readUTF();

		subscriptionModifiedTime = objectInput.readLong();
		timeZoneId = objectInput.readUTF();
		weDeployKey = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(faroProjectId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createTime);

		objectOutput.writeLong(modifiedTime);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (accountKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(accountKey);
		}

		if (accountName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(accountName);
		}

		if (corpProjectName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(corpProjectName);
		}

		if (corpProjectUuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(corpProjectUuid);
		}

		objectOutput.writeBoolean(dataSourceConnected);

		if (ipAddresses == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(ipAddresses);
		}

		if (incidentReportEmailAddresses == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(incidentReportEmailAddresses);
		}

		objectOutput.writeLong(lastAccessTime);

		objectOutput.writeBoolean(recommendationsEnabled);

		if (salesforceAccountId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(salesforceAccountId);
		}

		if (serverLocation == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(serverLocation);
		}

		if (services == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(services);
		}

		if (state == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(state);
		}

		if (subscription == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subscription);
		}

		objectOutput.writeLong(subscriptionModifiedTime);

		if (timeZoneId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(timeZoneId);
		}

		if (weDeployKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(weDeployKey);
		}
	}

	public long mvccVersion;
	public long faroProjectId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createTime;
	public long modifiedTime;
	public String name;
	public String accountKey;
	public String accountName;
	public String corpProjectName;
	public String corpProjectUuid;
	public boolean dataSourceConnected;
	public String ipAddresses;
	public String incidentReportEmailAddresses;
	public long lastAccessTime;
	public boolean recommendationsEnabled;
	public String salesforceAccountId;
	public String serverLocation;
	public String services;
	public String state;
	public String subscription;
	public long subscriptionModifiedTime;
	public String timeZoneId;
	public String weDeployKey;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1220074902