/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherBuild;
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
 * The cache model class for representing PatcherBuild in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherBuildCacheModel
	implements CacheModel<PatcherBuild>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherBuildCacheModel)) {
			return false;
		}

		PatcherBuildCacheModel patcherBuildCacheModel =
			(PatcherBuildCacheModel)object;

		if ((patcherBuildId == patcherBuildCacheModel.patcherBuildId) &&
			(mvccVersion == patcherBuildCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherBuildId);

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
		StringBundler sb = new StringBundler(81);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherBuildId=");
		sb.append(patcherBuildId);
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
		sb.append(", hotfixId=");
		sb.append(hotfixId);
		sb.append(", patcherAccountId=");
		sb.append(patcherAccountId);
		sb.append(", patcherFixId=");
		sb.append(patcherFixId);
		sb.append(", patcherProductVersionId=");
		sb.append(patcherProductVersionId);
		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);
		sb.append(", ticketEntryId=");
		sb.append(ticketEntryId);
		sb.append(", accountEntryCode=");
		sb.append(accountEntryCode);
		sb.append(", childBuild=");
		sb.append(childBuild);
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", fileName=");
		sb.append(fileName);
		sb.append(", initialName=");
		sb.append(initialName);
		sb.append(", key=");
		sb.append(key);
		sb.append(", keyVersion=");
		sb.append(keyVersion);
		sb.append(", latestBuild=");
		sb.append(latestBuild);
		sb.append(", latestKeyBuild=");
		sb.append(latestKeyBuild);
		sb.append(", latestLESATicketBuild=");
		sb.append(latestLESATicketBuild);
		sb.append(", latestSupportTicketBuild=");
		sb.append(latestSupportTicketBuild);
		sb.append(", lesaTicket=");
		sb.append(lesaTicket);
		sb.append(", lesaTicketVersion=");
		sb.append(lesaTicketVersion);
		sb.append(", name=");
		sb.append(name);
		sb.append(", notified=");
		sb.append(notified);
		sb.append(", productVersion=");
		sb.append(productVersion);
		sb.append(", qaComments=");
		sb.append(qaComments);
		sb.append(", qaStatus=");
		sb.append(qaStatus);
		sb.append(", requestKey=");
		sb.append(requestKey);
		sb.append(", sourceName=");
		sb.append(sourceName);
		sb.append(", supportTicket=");
		sb.append(supportTicket);
		sb.append(", supportTicketVersion=");
		sb.append(supportTicketVersion);
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
	public PatcherBuild toEntityModel() {
		PatcherBuildImpl patcherBuildImpl = new PatcherBuildImpl();

		patcherBuildImpl.setMvccVersion(mvccVersion);
		patcherBuildImpl.setPatcherBuildId(patcherBuildId);
		patcherBuildImpl.setCompanyId(companyId);
		patcherBuildImpl.setUserId(userId);

		if (userName == null) {
			patcherBuildImpl.setUserName("");
		}
		else {
			patcherBuildImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			patcherBuildImpl.setCreateDate(null);
		}
		else {
			patcherBuildImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			patcherBuildImpl.setModifiedDate(null);
		}
		else {
			patcherBuildImpl.setModifiedDate(new Date(modifiedDate));
		}

		patcherBuildImpl.setHotfixId(hotfixId);
		patcherBuildImpl.setPatcherAccountId(patcherAccountId);
		patcherBuildImpl.setPatcherFixId(patcherFixId);
		patcherBuildImpl.setPatcherProductVersionId(patcherProductVersionId);
		patcherBuildImpl.setPatcherProjectVersionId(patcherProjectVersionId);
		patcherBuildImpl.setTicketEntryId(ticketEntryId);

		if (accountEntryCode == null) {
			patcherBuildImpl.setAccountEntryCode("");
		}
		else {
			patcherBuildImpl.setAccountEntryCode(accountEntryCode);
		}

		patcherBuildImpl.setChildBuild(childBuild);

		if (comments == null) {
			patcherBuildImpl.setComments("");
		}
		else {
			patcherBuildImpl.setComments(comments);
		}

		if (fileName == null) {
			patcherBuildImpl.setFileName("");
		}
		else {
			patcherBuildImpl.setFileName(fileName);
		}

		if (initialName == null) {
			patcherBuildImpl.setInitialName("");
		}
		else {
			patcherBuildImpl.setInitialName(initialName);
		}

		if (key == null) {
			patcherBuildImpl.setKey("");
		}
		else {
			patcherBuildImpl.setKey(key);
		}

		patcherBuildImpl.setKeyVersion(keyVersion);
		patcherBuildImpl.setLatestBuild(latestBuild);
		patcherBuildImpl.setLatestKeyBuild(latestKeyBuild);
		patcherBuildImpl.setLatestLESATicketBuild(latestLESATicketBuild);
		patcherBuildImpl.setLatestSupportTicketBuild(latestSupportTicketBuild);

		if (lesaTicket == null) {
			patcherBuildImpl.setLesaTicket("");
		}
		else {
			patcherBuildImpl.setLesaTicket(lesaTicket);
		}

		patcherBuildImpl.setLesaTicketVersion(lesaTicketVersion);

		if (name == null) {
			patcherBuildImpl.setName("");
		}
		else {
			patcherBuildImpl.setName(name);
		}

		patcherBuildImpl.setNotified(notified);
		patcherBuildImpl.setProductVersion(productVersion);

		if (qaComments == null) {
			patcherBuildImpl.setQaComments("");
		}
		else {
			patcherBuildImpl.setQaComments(qaComments);
		}

		patcherBuildImpl.setQaStatus(qaStatus);

		if (requestKey == null) {
			patcherBuildImpl.setRequestKey("");
		}
		else {
			patcherBuildImpl.setRequestKey(requestKey);
		}

		if (sourceName == null) {
			patcherBuildImpl.setSourceName("");
		}
		else {
			patcherBuildImpl.setSourceName(sourceName);
		}

		if (supportTicket == null) {
			patcherBuildImpl.setSupportTicket("");
		}
		else {
			patcherBuildImpl.setSupportTicket(supportTicket);
		}

		patcherBuildImpl.setSupportTicketVersion(supportTicketVersion);
		patcherBuildImpl.setType(type);
		patcherBuildImpl.setStatus(status);
		patcherBuildImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			patcherBuildImpl.setStatusByUserName("");
		}
		else {
			patcherBuildImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			patcherBuildImpl.setStatusDate(null);
		}
		else {
			patcherBuildImpl.setStatusDate(new Date(statusDate));
		}

		patcherBuildImpl.resetOriginalValues();

		return patcherBuildImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		patcherBuildId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		hotfixId = objectInput.readLong();

		patcherAccountId = objectInput.readLong();

		patcherFixId = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		patcherProjectVersionId = objectInput.readLong();

		ticketEntryId = objectInput.readLong();
		accountEntryCode = objectInput.readUTF();

		childBuild = objectInput.readBoolean();
		comments = (String)objectInput.readObject();
		fileName = objectInput.readUTF();
		initialName = objectInput.readUTF();
		key = objectInput.readUTF();

		keyVersion = objectInput.readDouble();

		latestBuild = objectInput.readBoolean();

		latestKeyBuild = objectInput.readBoolean();

		latestLESATicketBuild = objectInput.readBoolean();

		latestSupportTicketBuild = objectInput.readBoolean();
		lesaTicket = objectInput.readUTF();

		lesaTicketVersion = objectInput.readDouble();
		name = (String)objectInput.readObject();

		notified = objectInput.readBoolean();

		productVersion = objectInput.readInt();
		qaComments = (String)objectInput.readObject();

		qaStatus = objectInput.readInt();
		requestKey = objectInput.readUTF();
		sourceName = objectInput.readUTF();
		supportTicket = objectInput.readUTF();

		supportTicketVersion = objectInput.readDouble();

		type = objectInput.readInt();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherBuildId);

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

		objectOutput.writeLong(hotfixId);

		objectOutput.writeLong(patcherAccountId);

		objectOutput.writeLong(patcherFixId);

		objectOutput.writeLong(patcherProductVersionId);

		objectOutput.writeLong(patcherProjectVersionId);

		objectOutput.writeLong(ticketEntryId);

		if (accountEntryCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(accountEntryCode);
		}

		objectOutput.writeBoolean(childBuild);

		if (comments == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(comments);
		}

		if (fileName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(fileName);
		}

		if (initialName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(initialName);
		}

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeDouble(keyVersion);

		objectOutput.writeBoolean(latestBuild);

		objectOutput.writeBoolean(latestKeyBuild);

		objectOutput.writeBoolean(latestLESATicketBuild);

		objectOutput.writeBoolean(latestSupportTicketBuild);

		if (lesaTicket == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lesaTicket);
		}

		objectOutput.writeDouble(lesaTicketVersion);

		if (name == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(name);
		}

		objectOutput.writeBoolean(notified);

		objectOutput.writeInt(productVersion);

		if (qaComments == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(qaComments);
		}

		objectOutput.writeInt(qaStatus);

		if (requestKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requestKey);
		}

		if (sourceName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sourceName);
		}

		if (supportTicket == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(supportTicket);
		}

		objectOutput.writeDouble(supportTicketVersion);

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
	public long patcherBuildId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long hotfixId;
	public long patcherAccountId;
	public long patcherFixId;
	public long patcherProductVersionId;
	public long patcherProjectVersionId;
	public long ticketEntryId;
	public String accountEntryCode;
	public boolean childBuild;
	public String comments;
	public String fileName;
	public String initialName;
	public String key;
	public double keyVersion;
	public boolean latestBuild;
	public boolean latestKeyBuild;
	public boolean latestLESATicketBuild;
	public boolean latestSupportTicketBuild;
	public String lesaTicket;
	public double lesaTicketVersion;
	public String name;
	public boolean notified;
	public int productVersion;
	public String qaComments;
	public int qaStatus;
	public String requestKey;
	public String sourceName;
	public String supportTicket;
	public double supportTicketVersion;
	public int type;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}