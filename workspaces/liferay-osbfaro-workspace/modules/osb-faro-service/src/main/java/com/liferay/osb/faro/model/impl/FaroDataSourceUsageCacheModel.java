/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model.impl;

import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing FaroDataSourceUsage in entity cache.
 *
 * @author Matthew Kong
 * @generated
 */
public class FaroDataSourceUsageCacheModel
	implements CacheModel<FaroDataSourceUsage>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FaroDataSourceUsageCacheModel)) {
			return false;
		}

		FaroDataSourceUsageCacheModel faroDataSourceUsageCacheModel =
			(FaroDataSourceUsageCacheModel)object;

		if ((faroDataSourceUsageId ==
				faroDataSourceUsageCacheModel.faroDataSourceUsageId) &&
			(mvccVersion == faroDataSourceUsageCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, faroDataSourceUsageId);

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
		StringBundler sb = new StringBundler(27);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", faroDataSourceUsageId=");
		sb.append(faroDataSourceUsageId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", createTime=");
		sb.append(createTime);
		sb.append(", modifiedTime=");
		sb.append(modifiedTime);
		sb.append(", billableEventsCount=");
		sb.append(billableEventsCount);
		sb.append(", dataSourceId=");
		sb.append(dataSourceId);
		sb.append(", dataSourceName=");
		sb.append(dataSourceName);
		sb.append(", dataSourceStatus=");
		sb.append(dataSourceStatus);
		sb.append(", faroProjectId=");
		sb.append(faroProjectId);
		sb.append(", knownIndividualsCount=");
		sb.append(knownIndividualsCount);
		sb.append(", usageTime=");
		sb.append(usageTime);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FaroDataSourceUsage toEntityModel() {
		FaroDataSourceUsageImpl faroDataSourceUsageImpl =
			new FaroDataSourceUsageImpl();

		faroDataSourceUsageImpl.setMvccVersion(mvccVersion);
		faroDataSourceUsageImpl.setFaroDataSourceUsageId(faroDataSourceUsageId);
		faroDataSourceUsageImpl.setCompanyId(companyId);
		faroDataSourceUsageImpl.setUserId(userId);
		faroDataSourceUsageImpl.setCreateTime(createTime);
		faroDataSourceUsageImpl.setModifiedTime(modifiedTime);
		faroDataSourceUsageImpl.setBillableEventsCount(billableEventsCount);
		faroDataSourceUsageImpl.setDataSourceId(dataSourceId);

		if (dataSourceName == null) {
			faroDataSourceUsageImpl.setDataSourceName("");
		}
		else {
			faroDataSourceUsageImpl.setDataSourceName(dataSourceName);
		}

		if (dataSourceStatus == null) {
			faroDataSourceUsageImpl.setDataSourceStatus("");
		}
		else {
			faroDataSourceUsageImpl.setDataSourceStatus(dataSourceStatus);
		}

		faroDataSourceUsageImpl.setFaroProjectId(faroProjectId);
		faroDataSourceUsageImpl.setKnownIndividualsCount(knownIndividualsCount);
		faroDataSourceUsageImpl.setUsageTime(usageTime);

		faroDataSourceUsageImpl.resetOriginalValues();

		return faroDataSourceUsageImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		faroDataSourceUsageId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();

		createTime = objectInput.readLong();

		modifiedTime = objectInput.readLong();

		billableEventsCount = objectInput.readLong();

		dataSourceId = objectInput.readLong();
		dataSourceName = objectInput.readUTF();
		dataSourceStatus = objectInput.readUTF();

		faroProjectId = objectInput.readLong();

		knownIndividualsCount = objectInput.readLong();

		usageTime = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(faroDataSourceUsageId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		objectOutput.writeLong(createTime);

		objectOutput.writeLong(modifiedTime);

		objectOutput.writeLong(billableEventsCount);

		objectOutput.writeLong(dataSourceId);

		if (dataSourceName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dataSourceName);
		}

		if (dataSourceStatus == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dataSourceStatus);
		}

		objectOutput.writeLong(faroProjectId);

		objectOutput.writeLong(knownIndividualsCount);

		objectOutput.writeLong(usageTime);
	}

	public long mvccVersion;
	public long faroDataSourceUsageId;
	public long companyId;
	public long userId;
	public long createTime;
	public long modifiedTime;
	public long billableEventsCount;
	public long dataSourceId;
	public String dataSourceName;
	public String dataSourceStatus;
	public long faroProjectId;
	public long knownIndividualsCount;
	public long usageTime;

}
// LIFERAY-SERVICE-BUILDER-HASH:-425949060