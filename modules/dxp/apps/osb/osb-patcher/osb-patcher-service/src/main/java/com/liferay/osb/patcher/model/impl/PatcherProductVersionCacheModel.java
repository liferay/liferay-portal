/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherProductVersion;
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
 * The cache model class for representing PatcherProductVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherProductVersionCacheModel
	implements CacheModel<PatcherProductVersion>, Externalizable, MVCCModel {

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

		if ((patcherProductVersionId ==
				patcherProductVersionCacheModel.patcherProductVersionId) &&
			(mvccVersion == patcherProductVersionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherProductVersionId);

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
		StringBundler sb = new StringBundler(21);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherProductVersionId=");
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
		sb.append(", fixDeliveryMethod=");
		sb.append(fixDeliveryMethod);
		sb.append(", moduleFolderName=");
		sb.append(moduleFolderName);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherProductVersion toEntityModel() {
		PatcherProductVersionImpl patcherProductVersionImpl =
			new PatcherProductVersionImpl();

		patcherProductVersionImpl.setMvccVersion(mvccVersion);
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

		patcherProductVersionImpl.setFixDeliveryMethod(fixDeliveryMethod);

		if (moduleFolderName == null) {
			patcherProductVersionImpl.setModuleFolderName("");
		}
		else {
			patcherProductVersionImpl.setModuleFolderName(moduleFolderName);
		}

		if (name == null) {
			patcherProductVersionImpl.setName("");
		}
		else {
			patcherProductVersionImpl.setName(name);
		}

		patcherProductVersionImpl.resetOriginalValues();

		return patcherProductVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		fixDeliveryMethod = objectInput.readInt();
		moduleFolderName = objectInput.readUTF();
		name = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

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

		objectOutput.writeInt(fixDeliveryMethod);

		if (moduleFolderName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(moduleFolderName);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long mvccVersion;
	public long patcherProductVersionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public int fixDeliveryMethod;
	public String moduleFolderName;
	public String name;

}