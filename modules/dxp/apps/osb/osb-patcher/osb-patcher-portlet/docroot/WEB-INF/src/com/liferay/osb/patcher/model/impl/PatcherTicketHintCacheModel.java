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

import com.liferay.osb.patcher.model.PatcherTicketHint;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherTicketHint in entity cache.
 *
 * @author Calvin Keum
 * @see PatcherTicketHint
 * @generated
 */
public class PatcherTicketHintCacheModel implements CacheModel<PatcherTicketHint>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(17);

		sb.append("{patcherTicketHintId=");
		sb.append(patcherTicketHintId);
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
		sb.append(", patcherProductVersionId=");
		sb.append(patcherProductVersionId);
		sb.append(", script=");
		sb.append(script);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherTicketHint toEntityModel() {
		PatcherTicketHintImpl patcherTicketHintImpl = new PatcherTicketHintImpl();

		patcherTicketHintImpl.setPatcherTicketHintId(patcherTicketHintId);
		patcherTicketHintImpl.setCompanyId(companyId);
		patcherTicketHintImpl.setUserId(userId);

		if (userName == null) {
			patcherTicketHintImpl.setUserName(StringPool.BLANK);
		}
		else {
			patcherTicketHintImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherTicketHintImpl.setCreateDate(null);
		}
		else {
			patcherTicketHintImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherTicketHintImpl.setModifiedDate(null);
		}
		else {
			patcherTicketHintImpl.setModifiedDate(new Date(modifiedDate));
		}

		patcherTicketHintImpl.setPatcherProductVersionId(patcherProductVersionId);

		if (script == null) {
			patcherTicketHintImpl.setScript(StringPool.BLANK);
		}
		else {
			patcherTicketHintImpl.setScript(script);
		}

		patcherTicketHintImpl.resetOriginalValues();

		return patcherTicketHintImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		patcherTicketHintId = objectInput.readLong();
		companyId = objectInput.readLong();
		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		patcherProductVersionId = objectInput.readLong();
		script = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(patcherTicketHintId);
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
		objectOutput.writeLong(patcherProductVersionId);

		if (script == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(script);
		}
	}

	public long patcherTicketHintId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherProductVersionId;
	public String script;
}