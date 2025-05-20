/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFixPack;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherFixPack in entity cache.
 *
 * @author Calvin Keum
 * @see PatcherFixPack
 * @generated
 */
public class PatcherFixPackCacheModel implements CacheModel<PatcherFixPack>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(29);

		sb.append("{patcherFixPackId=");
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
		sb.append(", version=");
		sb.append(version);
		sb.append(", releasedDate=");
		sb.append(releasedDate);
		sb.append(", requirements=");
		sb.append(requirements);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherFixPack toEntityModel() {
		PatcherFixPackImpl patcherFixPackImpl = new PatcherFixPackImpl();

		patcherFixPackImpl.setPatcherFixPackId(patcherFixPackId);
		patcherFixPackImpl.setCompanyId(companyId);
		patcherFixPackImpl.setUserId(userId);

		if (userName == null) {
			patcherFixPackImpl.setUserName(StringPool.BLANK);
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
			patcherFixPackImpl.setName(StringPool.BLANK);
		}
		else {
			patcherFixPackImpl.setName(name);
		}

		patcherFixPackImpl.setVersion(version);

		if (releasedDate == Long.MIN_VALUE) {
			patcherFixPackImpl.setReleasedDate(null);
		}
		else {
			patcherFixPackImpl.setReleasedDate(new Date(releasedDate));
		}

		if (requirements == null) {
			patcherFixPackImpl.setRequirements(StringPool.BLANK);
		}
		else {
			patcherFixPackImpl.setRequirements(requirements);
		}

		patcherFixPackImpl.setStatus(status);

		patcherFixPackImpl.resetOriginalValues();

		return patcherFixPackImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
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
		version = objectInput.readInt();
		releasedDate = objectInput.readLong();
		requirements = objectInput.readUTF();
		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(patcherFixPackId);
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
		objectOutput.writeLong(patcherBuildId);
		objectOutput.writeLong(patcherFixComponentId);
		objectOutput.writeLong(patcherProjectVersionId);

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeInt(version);
		objectOutput.writeLong(releasedDate);

		if (requirements == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(requirements);
		}

		objectOutput.writeInt(status);
	}

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
	public int version;
	public long releasedDate;
	public String requirements;
	public int status;
}