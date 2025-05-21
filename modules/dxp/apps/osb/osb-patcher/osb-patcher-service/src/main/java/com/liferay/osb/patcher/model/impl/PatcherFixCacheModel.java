/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing PatcherFix in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherFixCacheModel
	implements CacheModel<PatcherFix>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherFixCacheModel)) {
			return false;
		}

		PatcherFixCacheModel patcherFixCacheModel =
			(PatcherFixCacheModel)object;

		if (patcherFixId == patcherFixCacheModel.patcherFixId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, patcherFixId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(59);

		sb.append("{patcherFixId=");
		sb.append(patcherFixId);
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
		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", key=");
		sb.append(key);
		sb.append(", keyVersion=");
		sb.append(keyVersion);
		sb.append(", type=");
		sb.append(type);
		sb.append(", latestFix=");
		sb.append(latestFix);
		sb.append(", obsolete=");
		sb.append(obsolete);
		sb.append(", committish=");
		sb.append(committish);
		sb.append(", gitHash=");
		sb.append(gitHash);
		sb.append(", gitRemoteURL=");
		sb.append(gitRemoteURL);
		sb.append(", dependencies=");
		sb.append(dependencies);
		sb.append(", requirements=");
		sb.append(requirements);
		sb.append(", requestKey=");
		sb.append(requestKey);
		sb.append(", jenkinsResults=");
		sb.append(jenkinsResults);
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", fixPackStatus=");
		sb.append(fixPackStatus);
		sb.append(", notified=");
		sb.append(notified);
		sb.append(", productVersion=");
		sb.append(productVersion);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusByUserName=");
		sb.append(statusByUserName);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherFix toEntityModel() {
		PatcherFixImpl patcherFixImpl = new PatcherFixImpl();

		patcherFixImpl.setPatcherFixId(patcherFixId);
		patcherFixImpl.setCompanyId(companyId);
		patcherFixImpl.setUserId(userId);

		if (userName == null) {
			patcherFixImpl.setUserName("");
		}
		else {
			patcherFixImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherFixImpl.setCreateDate(null);
		}
		else {
			patcherFixImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherFixImpl.setModifiedDate(null);
		}
		else {
			patcherFixImpl.setModifiedDate(new Date(modifiedDate));
		}

		patcherFixImpl.setPatcherProductVersionId(patcherProductVersionId);
		patcherFixImpl.setPatcherProjectVersionId(patcherProjectVersionId);

		if (name == null) {
			patcherFixImpl.setName("");
		}
		else {
			patcherFixImpl.setName(name);
		}

		if (key == null) {
			patcherFixImpl.setKey("");
		}
		else {
			patcherFixImpl.setKey(key);
		}

		patcherFixImpl.setKeyVersion(keyVersion);
		patcherFixImpl.setType(type);
		patcherFixImpl.setLatestFix(latestFix);
		patcherFixImpl.setObsolete(obsolete);

		if (committish == null) {
			patcherFixImpl.setCommittish("");
		}
		else {
			patcherFixImpl.setCommittish(committish);
		}

		if (gitHash == null) {
			patcherFixImpl.setGitHash("");
		}
		else {
			patcherFixImpl.setGitHash(gitHash);
		}

		if (gitRemoteURL == null) {
			patcherFixImpl.setGitRemoteURL("");
		}
		else {
			patcherFixImpl.setGitRemoteURL(gitRemoteURL);
		}

		if (dependencies == null) {
			patcherFixImpl.setDependencies("");
		}
		else {
			patcherFixImpl.setDependencies(dependencies);
		}

		if (requirements == null) {
			patcherFixImpl.setRequirements("");
		}
		else {
			patcherFixImpl.setRequirements(requirements);
		}

		if (requestKey == null) {
			patcherFixImpl.setRequestKey("");
		}
		else {
			patcherFixImpl.setRequestKey(requestKey);
		}

		if (jenkinsResults == null) {
			patcherFixImpl.setJenkinsResults("");
		}
		else {
			patcherFixImpl.setJenkinsResults(jenkinsResults);
		}

		if (comments == null) {
			patcherFixImpl.setComments("");
		}
		else {
			patcherFixImpl.setComments(comments);
		}

		patcherFixImpl.setFixPackStatus(fixPackStatus);
		patcherFixImpl.setNotified(notified);
		patcherFixImpl.setProductVersion(productVersion);
		patcherFixImpl.setStatus(status);
		patcherFixImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			patcherFixImpl.setStatusByUserName("");
		}
		else {
			patcherFixImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			patcherFixImpl.setStatusDate(null);
		}
		else {
			patcherFixImpl.setStatusDate(new Date(statusDate));
		}

		patcherFixImpl.resetOriginalValues();

		return patcherFixImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		patcherFixId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		patcherProjectVersionId = objectInput.readLong();
		name = (String)objectInput.readObject();
		key = objectInput.readUTF();

		keyVersion = objectInput.readDouble();

		type = objectInput.readInt();

		latestFix = objectInput.readBoolean();

		obsolete = objectInput.readBoolean();
		committish = objectInput.readUTF();
		gitHash = objectInput.readUTF();
		gitRemoteURL = objectInput.readUTF();
		dependencies = objectInput.readUTF();
		requirements = objectInput.readUTF();
		requestKey = objectInput.readUTF();
		jenkinsResults = (String)objectInput.readObject();
		comments = (String)objectInput.readObject();

		fixPackStatus = objectInput.readInt();

		notified = objectInput.readBoolean();

		productVersion = objectInput.readInt();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(patcherFixId);

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

		objectOutput.writeLong(patcherProjectVersionId);

		if (name == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(name);
		}

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeDouble(keyVersion);

		objectOutput.writeInt(type);

		objectOutput.writeBoolean(latestFix);

		objectOutput.writeBoolean(obsolete);

		if (committish == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(committish);
		}

		if (gitHash == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(gitHash);
		}

		if (gitRemoteURL == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(gitRemoteURL);
		}

		if (dependencies == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dependencies);
		}

		if (requirements == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requirements);
		}

		if (requestKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requestKey);
		}

		if (jenkinsResults == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(jenkinsResults);
		}

		if (comments == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(comments);
		}

		objectOutput.writeInt(fixPackStatus);

		objectOutput.writeBoolean(notified);

		objectOutput.writeInt(productVersion);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);
	}

	public long patcherFixId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherProductVersionId;
	public long patcherProjectVersionId;
	public String name;
	public String key;
	public double keyVersion;
	public int type;
	public boolean latestFix;
	public boolean obsolete;
	public String committish;
	public String gitHash;
	public String gitRemoteURL;
	public String dependencies;
	public String requirements;
	public String requestKey;
	public String jenkinsResults;
	public String comments;
	public int fixPackStatus;
	public boolean notified;
	public int productVersion;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}