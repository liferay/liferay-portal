/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model.impl;

import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing FaroProjectUsage in entity cache.
 *
 * @author Matthew Kong
 * @generated
 */
public class FaroProjectUsageCacheModel
	implements CacheModel<FaroProjectUsage>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FaroProjectUsageCacheModel)) {
			return false;
		}

		FaroProjectUsageCacheModel faroProjectUsageCacheModel =
			(FaroProjectUsageCacheModel)object;

		if ((faroProjectUsageId ==
				faroProjectUsageCacheModel.faroProjectUsageId) &&
			(mvccVersion == faroProjectUsageCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, faroProjectUsageId);

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
		StringBundler sb = new StringBundler(23);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", faroProjectUsageId=");
		sb.append(faroProjectUsageId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", createTime=");
		sb.append(createTime);
		sb.append(", modifiedTime=");
		sb.append(modifiedTime);
		sb.append(", faroProjectId=");
		sb.append(faroProjectId);
		sb.append(", knownIndividualsCount=");
		sb.append(knownIndividualsCount);
		sb.append(", monthDateKey=");
		sb.append(monthDateKey);
		sb.append(", pageViewsCount=");
		sb.append(pageViewsCount);
		sb.append(", usageTime=");
		sb.append(usageTime);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FaroProjectUsage toEntityModel() {
		FaroProjectUsageImpl faroProjectUsageImpl = new FaroProjectUsageImpl();

		faroProjectUsageImpl.setMvccVersion(mvccVersion);
		faroProjectUsageImpl.setFaroProjectUsageId(faroProjectUsageId);
		faroProjectUsageImpl.setCompanyId(companyId);
		faroProjectUsageImpl.setUserId(userId);
		faroProjectUsageImpl.setCreateTime(createTime);
		faroProjectUsageImpl.setModifiedTime(modifiedTime);
		faroProjectUsageImpl.setFaroProjectId(faroProjectId);
		faroProjectUsageImpl.setKnownIndividualsCount(knownIndividualsCount);

		if (monthDateKey == null) {
			faroProjectUsageImpl.setMonthDateKey("");
		}
		else {
			faroProjectUsageImpl.setMonthDateKey(monthDateKey);
		}

		faroProjectUsageImpl.setPageViewsCount(pageViewsCount);
		faroProjectUsageImpl.setUsageTime(usageTime);

		faroProjectUsageImpl.resetOriginalValues();

		return faroProjectUsageImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		faroProjectUsageId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();

		createTime = objectInput.readLong();

		modifiedTime = objectInput.readLong();

		faroProjectId = objectInput.readLong();

		knownIndividualsCount = objectInput.readLong();
		monthDateKey = objectInput.readUTF();

		pageViewsCount = objectInput.readLong();

		usageTime = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(faroProjectUsageId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		objectOutput.writeLong(createTime);

		objectOutput.writeLong(modifiedTime);

		objectOutput.writeLong(faroProjectId);

		objectOutput.writeLong(knownIndividualsCount);

		if (monthDateKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(monthDateKey);
		}

		objectOutput.writeLong(pageViewsCount);

		objectOutput.writeLong(usageTime);
	}

	public long mvccVersion;
	public long faroProjectUsageId;
	public long companyId;
	public long userId;
	public long createTime;
	public long modifiedTime;
	public long faroProjectId;
	public long knownIndividualsCount;
	public String monthDateKey;
	public long pageViewsCount;
	public long usageTime;

}