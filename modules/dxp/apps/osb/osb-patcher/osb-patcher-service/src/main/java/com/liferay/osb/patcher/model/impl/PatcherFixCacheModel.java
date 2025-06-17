/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFix;
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
 * The cache model class for representing PatcherFix in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherFixCacheModel
	implements CacheModel<PatcherFix>, Externalizable, MVCCModel {

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

		if ((patcherFixId == patcherFixCacheModel.patcherFixId) &&
			(mvccVersion == patcherFixCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherFixId);

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
		StringBundler sb = new StringBundler(61);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherFixId=");
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
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", committish=");
		sb.append(committish);
		sb.append(", dependencies=");
		sb.append(dependencies);
		sb.append(", fixPackStatus=");
		sb.append(fixPackStatus);
		sb.append(", gitHash=");
		sb.append(gitHash);
		sb.append(", gitRemoteURL=");
		sb.append(gitRemoteURL);
		sb.append(", jenkinsResults=");
		sb.append(jenkinsResults);
		sb.append(", key=");
		sb.append(key);
		sb.append(", keyVersion=");
		sb.append(keyVersion);
		sb.append(", latestFix=");
		sb.append(latestFix);
		sb.append(", name=");
		sb.append(name);
		sb.append(", notified=");
		sb.append(notified);
		sb.append(", obsolete=");
		sb.append(obsolete);
		sb.append(", productVersion=");
		sb.append(productVersion);
		sb.append(", requestKey=");
		sb.append(requestKey);
		sb.append(", requirements=");
		sb.append(requirements);
		sb.append(", type=");
		sb.append(type);
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

		patcherFixImpl.setMvccVersion(mvccVersion);
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

		if (comments == null) {
			patcherFixImpl.setComments("");
		}
		else {
			patcherFixImpl.setComments(comments);
		}

		if (committish == null) {
			patcherFixImpl.setCommittish("");
		}
		else {
			patcherFixImpl.setCommittish(committish);
		}

		if (dependencies == null) {
			patcherFixImpl.setDependencies("");
		}
		else {
			patcherFixImpl.setDependencies(dependencies);
		}

		patcherFixImpl.setFixPackStatus(fixPackStatus);

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

		if (jenkinsResults == null) {
			patcherFixImpl.setJenkinsResults("");
		}
		else {
			patcherFixImpl.setJenkinsResults(jenkinsResults);
		}

		if (key == null) {
			patcherFixImpl.setKey("");
		}
		else {
			patcherFixImpl.setKey(key);
		}

		patcherFixImpl.setKeyVersion(keyVersion);
		patcherFixImpl.setLatestFix(latestFix);

		if (name == null) {
			patcherFixImpl.setName("");
		}
		else {
			patcherFixImpl.setName(name);
		}

		patcherFixImpl.setNotified(notified);
		patcherFixImpl.setObsolete(obsolete);
		patcherFixImpl.setProductVersion(productVersion);

		if (requestKey == null) {
			patcherFixImpl.setRequestKey("");
		}
		else {
			patcherFixImpl.setRequestKey(requestKey);
		}

		if (requirements == null) {
			patcherFixImpl.setRequirements("");
		}
		else {
			patcherFixImpl.setRequirements(requirements);
		}

		patcherFixImpl.setType(type);
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

		mvccVersion = objectInput.readLong();

		patcherFixId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		patcherProjectVersionId = objectInput.readLong();
		comments = (String)objectInput.readObject();
		committish = objectInput.readUTF();
		dependencies = objectInput.readUTF();

		fixPackStatus = objectInput.readInt();
		gitHash = objectInput.readUTF();
		gitRemoteURL = objectInput.readUTF();
		jenkinsResults = (String)objectInput.readObject();
		key = objectInput.readUTF();

		keyVersion = objectInput.readDouble();

		latestFix = objectInput.readBoolean();
		name = (String)objectInput.readObject();

		notified = objectInput.readBoolean();

		obsolete = objectInput.readBoolean();

		productVersion = objectInput.readInt();
		requestKey = objectInput.readUTF();
		requirements = objectInput.readUTF();

		type = objectInput.readInt();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

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

		if (comments == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(comments);
		}

		if (committish == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(committish);
		}

		if (dependencies == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(dependencies);
		}

		objectOutput.writeInt(fixPackStatus);

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

		if (jenkinsResults == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(jenkinsResults);
		}

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeDouble(keyVersion);

		objectOutput.writeBoolean(latestFix);

		if (name == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(name);
		}

		objectOutput.writeBoolean(notified);

		objectOutput.writeBoolean(obsolete);

		objectOutput.writeInt(productVersion);

		if (requestKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requestKey);
		}

		if (requirements == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requirements);
		}

		objectOutput.writeInt(type);

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

	public long mvccVersion;
	public long patcherFixId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherProductVersionId;
	public long patcherProjectVersionId;
	public String comments;
	public String committish;
	public String dependencies;
	public int fixPackStatus;
	public String gitHash;
	public String gitRemoteURL;
	public String jenkinsResults;
	public String key;
	public double keyVersion;
	public boolean latestFix;
	public String name;
	public boolean notified;
	public boolean obsolete;
	public int productVersion;
	public String requestKey;
	public String requirements;
	public int type;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}