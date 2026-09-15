/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.RecentLayoutBranch;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing RecentLayoutBranch in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutBranchCacheModel
	implements CacheModel<RecentLayoutBranch>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RecentLayoutBranchCacheModel)) {
			return false;
		}

		RecentLayoutBranchCacheModel recentLayoutBranchCacheModel =
			(RecentLayoutBranchCacheModel)object;

		if ((recentLayoutBranchId ==
				recentLayoutBranchCacheModel.recentLayoutBranchId) &&
			(mvccVersion == recentLayoutBranchCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, recentLayoutBranchId);

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
		StringBundler sb = new StringBundler(17);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", recentLayoutBranchId=");
		sb.append(recentLayoutBranchId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", layoutBranchId=");
		sb.append(layoutBranchId);
		sb.append(", layoutSetBranchId=");
		sb.append(layoutSetBranchId);
		sb.append(", plid=");
		sb.append(plid);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public RecentLayoutBranch toEntityModel() {
		RecentLayoutBranchImpl recentLayoutBranchImpl =
			new RecentLayoutBranchImpl();

		recentLayoutBranchImpl.setMvccVersion(mvccVersion);
		recentLayoutBranchImpl.setRecentLayoutBranchId(recentLayoutBranchId);
		recentLayoutBranchImpl.setGroupId(groupId);
		recentLayoutBranchImpl.setCompanyId(companyId);
		recentLayoutBranchImpl.setUserId(userId);
		recentLayoutBranchImpl.setLayoutBranchId(layoutBranchId);
		recentLayoutBranchImpl.setLayoutSetBranchId(layoutSetBranchId);
		recentLayoutBranchImpl.setPlid(plid);

		recentLayoutBranchImpl.resetOriginalValues();

		return recentLayoutBranchImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		recentLayoutBranchId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();

		layoutBranchId = objectInput.readLong();

		layoutSetBranchId = objectInput.readLong();

		plid = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(recentLayoutBranchId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		objectOutput.writeLong(layoutBranchId);

		objectOutput.writeLong(layoutSetBranchId);

		objectOutput.writeLong(plid);
	}

	public long mvccVersion;
	public long recentLayoutBranchId;
	public long groupId;
	public long companyId;
	public long userId;
	public long layoutBranchId;
	public long layoutSetBranchId;
	public long plid;

}
// LIFERAY-SERVICE-BUILDER-HASH:1898308895