/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherProductVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherProductVersionCacheModel
	implements CacheModel<PatcherProductVersion>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherProductVersionCacheModel)) {
			return false;
		}

		PatcherProductVersionCacheModel patcherProductVersionCacheModel =
			(PatcherProductVersionCacheModel)object;

		if (patcherProductVersionId ==
				patcherProductVersionCacheModel.patcherProductVersionId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, patcherProductVersionId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(19);

		sb.append("{patcherProductVersionId=");
		sb.append(patcherProductVersionId);
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
		sb.append(", fixDeliveryMethod=");
		sb.append(fixDeliveryMethod);
		sb.append(", moduleFolderName=");
		sb.append(moduleFolderName);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherProductVersion toEntityModel() {
		PatcherProductVersionImpl patcherProductVersionImpl =
			new PatcherProductVersionImpl();

		patcherProductVersionImpl.setPatcherProductVersionId(
			patcherProductVersionId);
		patcherProductVersionImpl.setCompanyId(companyId);
		patcherProductVersionImpl.setUserId(userId);

		if (userName == null) {
			patcherProductVersionImpl.setUserName("");
		}
		else {
			patcherProductVersionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherProductVersionImpl.setCreateDate(null);
		}
		else {
			patcherProductVersionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherProductVersionImpl.setModifiedDate(null);
		}
		else {
			patcherProductVersionImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			patcherProductVersionImpl.setName("");
		}
		else {
			patcherProductVersionImpl.setName(name);
		}

		patcherProductVersionImpl.setFixDeliveryMethod(fixDeliveryMethod);

		if (moduleFolderName == null) {
			patcherProductVersionImpl.setModuleFolderName("");
		}
		else {
			patcherProductVersionImpl.setModuleFolderName(moduleFolderName);
		}

		patcherProductVersionImpl.resetOriginalValues();

		return patcherProductVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		patcherProductVersionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();

		fixDeliveryMethod = objectInput.readInt();
		moduleFolderName = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(patcherProductVersionId);

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

		objectOutput.writeInt(fixDeliveryMethod);

		if (moduleFolderName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(moduleFolderName);
		}
	}

	public long patcherProductVersionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public int fixDeliveryMethod;
	public String moduleFolderName;

}