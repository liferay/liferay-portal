/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFixPack;
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
 * The cache model class for representing PatcherFixPack in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherFixPackCacheModel
	implements CacheModel<PatcherFixPack>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherFixPackCacheModel)) {
			return false;
		}

		PatcherFixPackCacheModel patcherFixPackCacheModel =
			(PatcherFixPackCacheModel)object;

		if ((patcherFixPackId == patcherFixPackCacheModel.patcherFixPackId) &&
			(mvccVersion == patcherFixPackCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherFixPackId);

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
		StringBundler sb = new StringBundler(31);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherFixPackId=");
		sb.append(patcherFixPackId);
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
		sb.append(", patcherBuildId=");
		sb.append(patcherBuildId);
		sb.append(", patcherFixComponentId=");
		sb.append(patcherFixComponentId);
		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", releasedDate=");
		sb.append(releasedDate);
		sb.append(", requirements=");
		sb.append(requirements);
		sb.append(", version=");
		sb.append(version);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherFixPack toEntityModel() {
		PatcherFixPackImpl patcherFixPackImpl = new PatcherFixPackImpl();

		patcherFixPackImpl.setMvccVersion(mvccVersion);
		patcherFixPackImpl.setPatcherFixPackId(patcherFixPackId);
		patcherFixPackImpl.setCompanyId(companyId);
		patcherFixPackImpl.setUserId(userId);

		if (userName == null) {
			patcherFixPackImpl.setUserName("");
		}
		else {
			patcherFixPackImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherFixPackImpl.setCreateDate(null);
		}
		else {
			patcherFixPackImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherFixPackImpl.setModifiedDate(null);
		}
		else {
			patcherFixPackImpl.setModifiedDate(new Date(modifiedDate));
		}

		patcherFixPackImpl.setPatcherBuildId(patcherBuildId);
		patcherFixPackImpl.setPatcherFixComponentId(patcherFixComponentId);
		patcherFixPackImpl.setPatcherProjectVersionId(patcherProjectVersionId);

		if (name == null) {
			patcherFixPackImpl.setName("");
		}
		else {
			patcherFixPackImpl.setName(name);
		}

		if (releasedDate == Long.MIN_VALUE) {
			patcherFixPackImpl.setReleasedDate(null);
		}
		else {
			patcherFixPackImpl.setReleasedDate(new Date(releasedDate));
		}

		if (requirements == null) {
			patcherFixPackImpl.setRequirements("");
		}
		else {
			patcherFixPackImpl.setRequirements(requirements);
		}

		patcherFixPackImpl.setVersion(version);
		patcherFixPackImpl.setStatus(status);

		patcherFixPackImpl.resetOriginalValues();

		return patcherFixPackImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		patcherFixPackId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		patcherBuildId = objectInput.readLong();

		patcherFixComponentId = objectInput.readLong();

		patcherProjectVersionId = objectInput.readLong();
		name = objectInput.readUTF();
		releasedDate = objectInput.readLong();
		requirements = objectInput.readUTF();

		version = objectInput.readInt();

		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherFixPackId);

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

		objectOutput.writeLong(patcherBuildId);

		objectOutput.writeLong(patcherFixComponentId);

		objectOutput.writeLong(patcherProjectVersionId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeLong(releasedDate);

		if (requirements == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requirements);
		}

		objectOutput.writeInt(version);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long patcherFixPackId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherBuildId;
	public long patcherFixComponentId;
	public long patcherProjectVersionId;
	public String name;
	public long releasedDate;
	public String requirements;
	public int version;
	public int status;

}