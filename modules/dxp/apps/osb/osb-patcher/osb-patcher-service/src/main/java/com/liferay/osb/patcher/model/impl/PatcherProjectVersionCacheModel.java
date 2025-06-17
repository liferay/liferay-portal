/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherProjectVersion;
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
 * The cache model class for representing PatcherProjectVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherProjectVersionCacheModel
	implements CacheModel<PatcherProjectVersion>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherProjectVersionCacheModel)) {
			return false;
		}

		PatcherProjectVersionCacheModel patcherProjectVersionCacheModel =
			(PatcherProjectVersionCacheModel)object;

		if ((patcherProjectVersionId ==
				patcherProjectVersionCacheModel.patcherProjectVersionId) &&
			(mvccVersion == patcherProjectVersionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherProjectVersionId);

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
		StringBundler sb = new StringBundler(33);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);
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
		sb.append(", rootPatcherProjectVersionId=");
		sb.append(rootPatcherProjectVersionId);
		sb.append(", combinedBranch=");
		sb.append(combinedBranch);
		sb.append(", committish=");
		sb.append(committish);
		sb.append(", fixedIssues=");
		sb.append(fixedIssues);
		sb.append(", hide=");
		sb.append(hide);
		sb.append(", name=");
		sb.append(name);
		sb.append(", productVersion=");
		sb.append(productVersion);
		sb.append(", repositoryName=");
		sb.append(repositoryName);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherProjectVersion toEntityModel() {
		PatcherProjectVersionImpl patcherProjectVersionImpl =
			new PatcherProjectVersionImpl();

		patcherProjectVersionImpl.setMvccVersion(mvccVersion);
		patcherProjectVersionImpl.setPatcherProjectVersionId(
			patcherProjectVersionId);
		patcherProjectVersionImpl.setCompanyId(companyId);
		patcherProjectVersionImpl.setUserId(userId);

		if (userName == null) {
			patcherProjectVersionImpl.setUserName("");
		}
		else {
			patcherProjectVersionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherProjectVersionImpl.setCreateDate(null);
		}
		else {
			patcherProjectVersionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherProjectVersionImpl.setModifiedDate(null);
		}
		else {
			patcherProjectVersionImpl.setModifiedDate(new Date(modifiedDate));
		}

		patcherProjectVersionImpl.setPatcherProductVersionId(
			patcherProductVersionId);
		patcherProjectVersionImpl.setRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
		patcherProjectVersionImpl.setCombinedBranch(combinedBranch);

		if (committish == null) {
			patcherProjectVersionImpl.setCommittish("");
		}
		else {
			patcherProjectVersionImpl.setCommittish(committish);
		}

		if (fixedIssues == null) {
			patcherProjectVersionImpl.setFixedIssues("");
		}
		else {
			patcherProjectVersionImpl.setFixedIssues(fixedIssues);
		}

		patcherProjectVersionImpl.setHide(hide);

		if (name == null) {
			patcherProjectVersionImpl.setName("");
		}
		else {
			patcherProjectVersionImpl.setName(name);
		}

		patcherProjectVersionImpl.setProductVersion(productVersion);

		if (repositoryName == null) {
			patcherProjectVersionImpl.setRepositoryName("");
		}
		else {
			patcherProjectVersionImpl.setRepositoryName(repositoryName);
		}

		patcherProjectVersionImpl.resetOriginalValues();

		return patcherProjectVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		patcherProjectVersionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		rootPatcherProjectVersionId = objectInput.readLong();

		combinedBranch = objectInput.readBoolean();
		committish = objectInput.readUTF();
		fixedIssues = (String)objectInput.readObject();

		hide = objectInput.readBoolean();
		name = objectInput.readUTF();

		productVersion = objectInput.readInt();
		repositoryName = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherProjectVersionId);

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

		objectOutput.writeLong(rootPatcherProjectVersionId);

		objectOutput.writeBoolean(combinedBranch);

		if (committish == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(committish);
		}

		if (fixedIssues == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(fixedIssues);
		}

		objectOutput.writeBoolean(hide);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeInt(productVersion);

		if (repositoryName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(repositoryName);
		}
	}

	public long mvccVersion;
	public long patcherProjectVersionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherProductVersionId;
	public long rootPatcherProjectVersionId;
	public boolean combinedBranch;
	public String committish;
	public String fixedIssues;
	public boolean hide;
	public String name;
	public int productVersion;
	public String repositoryName;

}