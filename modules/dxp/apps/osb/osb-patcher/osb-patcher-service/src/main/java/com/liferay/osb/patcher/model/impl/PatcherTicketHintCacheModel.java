/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherTicketHint;
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
 * The cache model class for representing PatcherTicketHint in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherTicketHintCacheModel
	implements CacheModel<PatcherTicketHint>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherTicketHintCacheModel)) {
			return false;
		}

		PatcherTicketHintCacheModel patcherTicketHintCacheModel =
			(PatcherTicketHintCacheModel)object;

		if ((patcherTicketHintId ==
				patcherTicketHintCacheModel.patcherTicketHintId) &&
			(mvccVersion == patcherTicketHintCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherTicketHintId);

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
		sb.append(", patcherTicketHintId=");
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
		PatcherTicketHintImpl patcherTicketHintImpl =
			new PatcherTicketHintImpl();

		patcherTicketHintImpl.setMvccVersion(mvccVersion);
		patcherTicketHintImpl.setPatcherTicketHintId(patcherTicketHintId);
		patcherTicketHintImpl.setCompanyId(companyId);
		patcherTicketHintImpl.setUserId(userId);

		if (userName == null) {
			patcherTicketHintImpl.setUserName("");
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

		patcherTicketHintImpl.setPatcherProductVersionId(
			patcherProductVersionId);

		if (script == null) {
			patcherTicketHintImpl.setScript("");
		}
		else {
			patcherTicketHintImpl.setScript(script);
		}

		patcherTicketHintImpl.resetOriginalValues();

		return patcherTicketHintImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

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
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherTicketHintId);

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

		objectOutput.writeLong(patcherProductVersionId);

		if (script == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(script);
		}
	}

	public long mvccVersion;
	public long patcherTicketHintId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherProductVersionId;
	public String script;

}