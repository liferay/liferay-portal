/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

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
	implements CacheModel<PatcherProjectVersion>, Externalizable {

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

		if (patcherProjectVersionId ==
				patcherProjectVersionCacheModel.patcherProjectVersionId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, patcherProjectVersionId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(31);

		sb.append("{patcherProjectVersionId=");
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
		sb.append(", name=");
		sb.append(name);
		sb.append(", combinedBranch=");
		sb.append(combinedBranch);
		sb.append(", hide=");
		sb.append(hide);
		sb.append(", committish=");
		sb.append(committish);
		sb.append(", repositoryName=");
		sb.append(repositoryName);
		sb.append(", fixedIssues=");
		sb.append(fixedIssues);
		sb.append(", productVersion=");
		sb.append(productVersion);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherProjectVersion toEntityModel() {
		PatcherProjectVersionImpl patcherProjectVersionImpl =
			new PatcherProjectVersionImpl();

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

		if (name == null) {
			patcherProjectVersionImpl.setName("");
		}
		else {
			patcherProjectVersionImpl.setName(name);
		}

		patcherProjectVersionImpl.setCombinedBranch(combinedBranch);
		patcherProjectVersionImpl.setHide(hide);

		if (committish == null) {
			patcherProjectVersionImpl.setCommittish("");
		}
		else {
			patcherProjectVersionImpl.setCommittish(committish);
		}

		if (repositoryName == null) {
			patcherProjectVersionImpl.setRepositoryName("");
		}
		else {
			patcherProjectVersionImpl.setRepositoryName(repositoryName);
		}

		if (fixedIssues == null) {
			patcherProjectVersionImpl.setFixedIssues("");
		}
		else {
			patcherProjectVersionImpl.setFixedIssues(fixedIssues);
		}

		patcherProjectVersionImpl.setProductVersion(productVersion);

		patcherProjectVersionImpl.resetOriginalValues();

		return patcherProjectVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		patcherProjectVersionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		rootPatcherProjectVersionId = objectInput.readLong();
		name = objectInput.readUTF();

		combinedBranch = objectInput.readBoolean();

		hide = objectInput.readBoolean();
		committish = objectInput.readUTF();
		repositoryName = objectInput.readUTF();
		fixedIssues = (String)objectInput.readObject();

		productVersion = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
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

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeBoolean(combinedBranch);

		objectOutput.writeBoolean(hide);

		if (committish == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(committish);
		}

		if (repositoryName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(repositoryName);
		}

		if (fixedIssues == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(fixedIssues);
		}

		objectOutput.writeInt(productVersion);
	}

	public long patcherProjectVersionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherProductVersionId;
	public long rootPatcherProjectVersionId;
	public String name;
	public boolean combinedBranch;
	public boolean hide;
	public String committish;
	public String repositoryName;
	public String fixedIssues;
	public int productVersion;

}