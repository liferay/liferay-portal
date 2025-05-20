/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherAccount;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherAccount in entity cache.
 *
 * @author Calvin Keum
 * @see PatcherAccount
 * @generated
 */
public class PatcherAccountCacheModel implements CacheModel<PatcherAccount>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(17);

		sb.append("{patcherAccountId=");
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

		patcherAccountImpl.setPatcherAccountId(patcherAccountId);
		patcherAccountImpl.setCompanyId(companyId);
		patcherAccountImpl.setUserId(userId);

		if (userName == null) {
			patcherAccountImpl.setUserName(StringPool.BLANK);
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
			patcherAccountImpl.setAccountEntryCode(StringPool.BLANK);
		}
		else {
			patcherAccountImpl.setAccountEntryCode(accountEntryCode);
		}

		patcherAccountImpl.resetOriginalValues();

		return patcherAccountImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
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
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(patcherAccountId);
		objectOutput.writeLong(companyId);
		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);
		objectOutput.writeLong(accountEntryId);

		if (accountEntryCode == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(accountEntryCode);
		}
	}

	public long patcherAccountId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long accountEntryId;
	public String accountEntryCode;
}