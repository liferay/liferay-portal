/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherFixComponent in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherFixComponentCacheModel
	implements CacheModel<PatcherFixComponent>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherFixComponentCacheModel)) {
			return false;
		}

		PatcherFixComponentCacheModel patcherFixComponentCacheModel =
			(PatcherFixComponentCacheModel)object;

		if (patcherFixComponentId ==
				patcherFixComponentCacheModel.patcherFixComponentId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, patcherFixComponentId);
	}

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
		PatcherFixComponentImpl patcherFixComponentImpl =
			new PatcherFixComponentImpl();

		patcherFixComponentImpl.setPatcherFixComponentId(patcherFixComponentId);
		patcherFixComponentImpl.setCompanyId(companyId);
		patcherFixComponentImpl.setUserId(userId);

		if (userName == null) {
			patcherFixComponentImpl.setUserName("");
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
			patcherFixComponentImpl.setName("");
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
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(patcherFixComponentId);

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

		if (name == null) {
			objectOutput.writeUTF("");
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