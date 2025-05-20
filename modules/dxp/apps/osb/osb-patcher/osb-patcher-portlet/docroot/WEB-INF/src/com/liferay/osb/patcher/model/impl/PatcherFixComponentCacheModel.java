/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFixComponent;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherFixComponent in entity cache.
 *
 * @author Calvin Keum
 * @see PatcherFixComponent
 * @generated
 */
public class PatcherFixComponentCacheModel implements CacheModel<PatcherFixComponent>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(15);

		sb.append("{patcherFixComponentId=");
		sb.append(patcherFixComponentId);
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
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherFixComponent toEntityModel() {
		PatcherFixComponentImpl patcherFixComponentImpl = new PatcherFixComponentImpl();

		patcherFixComponentImpl.setPatcherFixComponentId(patcherFixComponentId);
		patcherFixComponentImpl.setCompanyId(companyId);
		patcherFixComponentImpl.setUserId(userId);

		if (userName == null) {
			patcherFixComponentImpl.setUserName(StringPool.BLANK);
		}
		else {
			patcherFixComponentImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherFixComponentImpl.setCreateDate(null);
		}
		else {
			patcherFixComponentImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherFixComponentImpl.setModifiedDate(null);
		}
		else {
			patcherFixComponentImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			patcherFixComponentImpl.setName(StringPool.BLANK);
		}
		else {
			patcherFixComponentImpl.setName(name);
		}

		patcherFixComponentImpl.resetOriginalValues();

		return patcherFixComponentImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		patcherFixComponentId = objectInput.readLong();
		companyId = objectInput.readLong();
		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(patcherFixComponentId);
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

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long patcherFixComponentId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
}