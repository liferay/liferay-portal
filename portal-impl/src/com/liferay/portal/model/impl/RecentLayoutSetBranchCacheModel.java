/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.RecentLayoutSetBranch;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing RecentLayoutSetBranch in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutSetBranchCacheModel
	implements CacheModel<RecentLayoutSetBranch>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RecentLayoutSetBranchCacheModel)) {
			return false;
		}

		RecentLayoutSetBranchCacheModel recentLayoutSetBranchCacheModel =
			(RecentLayoutSetBranchCacheModel)object;

		if ((recentLayoutSetBranchId ==
				recentLayoutSetBranchCacheModel.recentLayoutSetBranchId) &&
			(mvccVersion == recentLayoutSetBranchCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, recentLayoutSetBranchId);

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
		StringBundler sb = new StringBundler(15);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", recentLayoutSetBranchId=");
		sb.append(recentLayoutSetBranchId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", layoutSetBranchId=");
		sb.append(layoutSetBranchId);
		sb.append(", layoutSetId=");
		sb.append(layoutSetId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public RecentLayoutSetBranch toEntityModel() {
		RecentLayoutSetBranchImpl recentLayoutSetBranchImpl =
			new RecentLayoutSetBranchImpl();

		recentLayoutSetBranchImpl.setMvccVersion(mvccVersion);
		recentLayoutSetBranchImpl.setRecentLayoutSetBranchId(
			recentLayoutSetBranchId);
		recentLayoutSetBranchImpl.setGroupId(groupId);
		recentLayoutSetBranchImpl.setCompanyId(companyId);
		recentLayoutSetBranchImpl.setUserId(userId);
		recentLayoutSetBranchImpl.setLayoutSetBranchId(layoutSetBranchId);
		recentLayoutSetBranchImpl.setLayoutSetId(layoutSetId);

		recentLayoutSetBranchImpl.resetOriginalValues();

		return recentLayoutSetBranchImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		recentLayoutSetBranchId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();

		layoutSetBranchId = objectInput.readLong();

		layoutSetId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(recentLayoutSetBranchId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		objectOutput.writeLong(layoutSetBranchId);

		objectOutput.writeLong(layoutSetId);
	}

	public long mvccVersion;
	public long recentLayoutSetBranchId;
	public long groupId;
	public long companyId;
	public long userId;
	public long layoutSetBranchId;
	public long layoutSetId;

}
// LIFERAY-SERVICE-BUILDER-HASH:936098779