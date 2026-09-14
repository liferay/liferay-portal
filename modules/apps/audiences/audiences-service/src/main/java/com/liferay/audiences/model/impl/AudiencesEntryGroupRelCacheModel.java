/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.model.impl;

import com.liferay.audiences.model.AudiencesEntryGroupRel;
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
 * The cache model class for representing AudiencesEntryGroupRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AudiencesEntryGroupRelCacheModel
	implements CacheModel<AudiencesEntryGroupRel>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AudiencesEntryGroupRelCacheModel)) {
			return false;
		}

		AudiencesEntryGroupRelCacheModel audiencesEntryGroupRelCacheModel =
			(AudiencesEntryGroupRelCacheModel)object;

		if ((audiencesEntryGroupRelId ==
				audiencesEntryGroupRelCacheModel.audiencesEntryGroupRelId) &&
			(mvccVersion == audiencesEntryGroupRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, audiencesEntryGroupRelId);

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
		sb.append(", audiencesEntryGroupRelId=");
		sb.append(audiencesEntryGroupRelId);
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
		sb.append(", audienceEntryERC=");
		sb.append(audienceEntryERC);
		sb.append(", groupERC=");
		sb.append(groupERC);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public AudiencesEntryGroupRel toEntityModel() {
		AudiencesEntryGroupRelImpl audiencesEntryGroupRelImpl =
			new AudiencesEntryGroupRelImpl();

		audiencesEntryGroupRelImpl.setMvccVersion(mvccVersion);
		audiencesEntryGroupRelImpl.setAudiencesEntryGroupRelId(
			audiencesEntryGroupRelId);
		audiencesEntryGroupRelImpl.setCompanyId(companyId);
		audiencesEntryGroupRelImpl.setUserId(userId);

		if (userName == null) {
			audiencesEntryGroupRelImpl.setUserName("");
		}
		else {
			audiencesEntryGroupRelImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			audiencesEntryGroupRelImpl.setCreateDate(null);
		}
		else {
			audiencesEntryGroupRelImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			audiencesEntryGroupRelImpl.setModifiedDate(null);
		}
		else {
			audiencesEntryGroupRelImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (audienceEntryERC == null) {
			audiencesEntryGroupRelImpl.setAudienceEntryERC("");
		}
		else {
			audiencesEntryGroupRelImpl.setAudienceEntryERC(audienceEntryERC);
		}

		if (groupERC == null) {
			audiencesEntryGroupRelImpl.setGroupERC("");
		}
		else {
			audiencesEntryGroupRelImpl.setGroupERC(groupERC);
		}

		audiencesEntryGroupRelImpl.resetOriginalValues();

		return audiencesEntryGroupRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		audiencesEntryGroupRelId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		audienceEntryERC = objectInput.readUTF();
		groupERC = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(audiencesEntryGroupRelId);

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

		if (audienceEntryERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(audienceEntryERC);
		}

		if (groupERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(groupERC);
		}
	}

	public long mvccVersion;
	public long audiencesEntryGroupRelId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String audienceEntryERC;
	public String groupERC;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1377428438