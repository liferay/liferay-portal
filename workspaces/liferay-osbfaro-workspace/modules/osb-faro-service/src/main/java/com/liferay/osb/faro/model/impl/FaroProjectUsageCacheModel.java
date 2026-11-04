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
		StringBundler sb = new StringBundler(35);

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
		sb.append(", activeLogicalBytes=");
		sb.append(activeLogicalBytes);
		sb.append(", anonymousIndividualsCount=");
		sb.append(anonymousIndividualsCount);
		sb.append(", eventsCount=");
		sb.append(eventsCount);
		sb.append(", faroProjectId=");
		sb.append(faroProjectId);
		sb.append(", knownIndividualsCount=");
		sb.append(knownIndividualsCount);
		sb.append(", longTermLogicalBytes=");
		sb.append(longTermLogicalBytes);
		sb.append(", monthDateKey=");
		sb.append(monthDateKey);
		sb.append(", onDemandBytesBilled=");
		sb.append(onDemandBytesBilled);
		sb.append(", pageViewsCount=");
		sb.append(pageViewsCount);
		sb.append(", reservationSlotMs=");
		sb.append(reservationSlotMs);
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
		faroProjectUsageImpl.setActiveLogicalBytes(activeLogicalBytes);
		faroProjectUsageImpl.setAnonymousIndividualsCount(
			anonymousIndividualsCount);
		faroProjectUsageImpl.setEventsCount(eventsCount);
		faroProjectUsageImpl.setFaroProjectId(faroProjectId);
		faroProjectUsageImpl.setKnownIndividualsCount(knownIndividualsCount);
		faroProjectUsageImpl.setLongTermLogicalBytes(longTermLogicalBytes);

		if (monthDateKey == null) {
			faroProjectUsageImpl.setMonthDateKey("");
		}
		else {
			faroProjectUsageImpl.setMonthDateKey(monthDateKey);
		}

		faroProjectUsageImpl.setOnDemandBytesBilled(onDemandBytesBilled);
		faroProjectUsageImpl.setPageViewsCount(pageViewsCount);
		faroProjectUsageImpl.setReservationSlotMs(reservationSlotMs);
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

		activeLogicalBytes = objectInput.readLong();

		anonymousIndividualsCount = objectInput.readLong();

		eventsCount = objectInput.readLong();

		faroProjectId = objectInput.readLong();

		knownIndividualsCount = objectInput.readLong();

		longTermLogicalBytes = objectInput.readLong();
		monthDateKey = objectInput.readUTF();

		onDemandBytesBilled = objectInput.readLong();

		pageViewsCount = objectInput.readLong();

		reservationSlotMs = objectInput.readLong();

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

		objectOutput.writeLong(activeLogicalBytes);

		objectOutput.writeLong(anonymousIndividualsCount);

		objectOutput.writeLong(eventsCount);

		objectOutput.writeLong(faroProjectId);

		objectOutput.writeLong(knownIndividualsCount);

		objectOutput.writeLong(longTermLogicalBytes);

		if (monthDateKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(monthDateKey);
		}

		objectOutput.writeLong(onDemandBytesBilled);

		objectOutput.writeLong(pageViewsCount);

		objectOutput.writeLong(reservationSlotMs);

		objectOutput.writeLong(usageTime);
	}

	public long mvccVersion;
	public long faroProjectUsageId;
	public long companyId;
	public long userId;
	public long createTime;
	public long modifiedTime;
	public long activeLogicalBytes;
	public long anonymousIndividualsCount;
	public long eventsCount;
	public long faroProjectId;
	public long knownIndividualsCount;
	public long longTermLogicalBytes;
	public String monthDateKey;
	public long onDemandBytesBilled;
	public long pageViewsCount;
	public long reservationSlotMs;
	public long usageTime;

}
// LIFERAY-SERVICE-BUILDER-HASH:156895086