/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherAccount in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherAccountCacheModel
	implements CacheModel<PatcherAccount>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherAccountCacheModel)) {
			return false;
		}

		PatcherAccountCacheModel patcherAccountCacheModel =
			(PatcherAccountCacheModel)object;

		if ((patcherAccountId == patcherAccountCacheModel.patcherAccountId) &&
			(mvccVersion == patcherAccountCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherAccountId);

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
		StringBundler sb = new StringBundler(19);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherAccountId=");
		sb.append(patcherAccountId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", accountEntryId=");
		sb.append(accountEntryId);
		sb.append(", accountEntryCode=");
		sb.append(accountEntryCode);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherAccount toEntityModel() {
		PatcherAccountImpl patcherAccountImpl = new PatcherAccountImpl();

		patcherAccountImpl.setMvccVersion(mvccVersion);
		patcherAccountImpl.setPatcherAccountId(patcherAccountId);
		patcherAccountImpl.setCompanyId(companyId);
		patcherAccountImpl.setUserId(userId);

		if (userName == null) {
			patcherAccountImpl.setUserName("");
		}
		else {
			patcherAccountImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherAccountImpl.setCreateDate(null);
		}
		else {
			patcherAccountImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherAccountImpl.setModifiedDate(null);
		}
		else {
			patcherAccountImpl.setModifiedDate(new Date(modifiedDate));
		}

		patcherAccountImpl.setAccountEntryId(accountEntryId);

		if (accountEntryCode == null) {
			patcherAccountImpl.setAccountEntryCode("");
		}
		else {
			patcherAccountImpl.setAccountEntryCode(accountEntryCode);
		}

		patcherAccountImpl.resetOriginalValues();

		return patcherAccountImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		patcherAccountId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		accountEntryId = objectInput.readLong();
		accountEntryCode = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherAccountId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(accountEntryId);

		if (accountEntryCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(accountEntryCode);
		}
	}

	public long mvccVersion;
	public long patcherAccountId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long accountEntryId;
	public String accountEntryCode;

}