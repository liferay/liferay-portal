/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

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
	implements CacheModel<PatcherBuild>, Externalizable {

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

		if (patcherBuildId == patcherBuildCacheModel.patcherBuildId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, patcherBuildId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(79);

		sb.append("{patcherBuildId=");
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
		sb.append(", hotfixId=");
		sb.append(hotfixId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", originalName=");
		sb.append(originalName);
		sb.append(", key=");
		sb.append(key);
		sb.append(", keyVersion=");
		sb.append(keyVersion);
		sb.append(", type=");
		sb.append(type);
		sb.append(", latestBuild=");
		sb.append(latestBuild);
		sb.append(", latestKeyBuild=");
		sb.append(latestKeyBuild);
		sb.append(", latestLESATicketBuild=");
		sb.append(latestLESATicketBuild);
		sb.append(", latestSupportTicketBuild=");
		sb.append(latestSupportTicketBuild);
		sb.append(", accountEntryCode=");
		sb.append(accountEntryCode);
		sb.append(", lesaTicket=");
		sb.append(lesaTicket);
		sb.append(", lesaTicketVersion=");
		sb.append(lesaTicketVersion);
		sb.append(", supportTicket=");
		sb.append(supportTicket);
		sb.append(", supportTicketVersion=");
		sb.append(supportTicketVersion);
		sb.append(", fileName=");
		sb.append(fileName);
		sb.append(", sourceName=");
		sb.append(sourceName);
		sb.append(", childBuild=");
		sb.append(childBuild);
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", qaComments=");
		sb.append(qaComments);
		sb.append(", qaStatus=");
		sb.append(qaStatus);
		sb.append(", requestKey=");
		sb.append(requestKey);
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
	public PatcherBuild toEntityModel() {
		PatcherBuildImpl patcherBuildImpl = new PatcherBuildImpl();

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

		patcherBuildImpl.setPatcherAccountId(patcherAccountId);
		patcherBuildImpl.setPatcherFixId(patcherFixId);
		patcherBuildImpl.setPatcherProductVersionId(patcherProductVersionId);
		patcherBuildImpl.setPatcherProjectVersionId(patcherProjectVersionId);
		patcherBuildImpl.setTicketEntryId(ticketEntryId);
		patcherBuildImpl.setHotfixId(hotfixId);

		if (name == null) {
			patcherBuildImpl.setName("");
		}
		else {
			patcherBuildImpl.setName(name);
		}

		if (originalName == null) {
			patcherBuildImpl.setOriginalName("");
		}
		else {
			patcherBuildImpl.setOriginalName(originalName);
		}

		if (key == null) {
			patcherBuildImpl.setKey("");
		}
		else {
			patcherBuildImpl.setKey(key);
		}

		patcherBuildImpl.setKeyVersion(keyVersion);
		patcherBuildImpl.setType(type);
		patcherBuildImpl.setLatestBuild(latestBuild);
		patcherBuildImpl.setLatestKeyBuild(latestKeyBuild);
		patcherBuildImpl.setLatestLESATicketBuild(latestLESATicketBuild);
		patcherBuildImpl.setLatestSupportTicketBuild(latestSupportTicketBuild);

		if (accountEntryCode == null) {
			patcherBuildImpl.setAccountEntryCode("");
		}
		else {
			patcherBuildImpl.setAccountEntryCode(accountEntryCode);
		}

		if (lesaTicket == null) {
			patcherBuildImpl.setLesaTicket("");
		}
		else {
			patcherBuildImpl.setLesaTicket(lesaTicket);
		}

		patcherBuildImpl.setLesaTicketVersion(lesaTicketVersion);

		if (supportTicket == null) {
			patcherBuildImpl.setSupportTicket("");
		}
		else {
			patcherBuildImpl.setSupportTicket(supportTicket);
		}

		patcherBuildImpl.setSupportTicketVersion(supportTicketVersion);

		if (fileName == null) {
			patcherBuildImpl.setFileName("");
		}
		else {
			patcherBuildImpl.setFileName(fileName);
		}

		if (sourceName == null) {
			patcherBuildImpl.setSourceName("");
		}
		else {
			patcherBuildImpl.setSourceName(sourceName);
		}

		patcherBuildImpl.setChildBuild(childBuild);

		if (comments == null) {
			patcherBuildImpl.setComments("");
		}
		else {
			patcherBuildImpl.setComments(comments);
		}

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

		patcherBuildImpl.setNotified(notified);
		patcherBuildImpl.setProductVersion(productVersion);
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

		patcherBuildId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		patcherAccountId = objectInput.readLong();

		patcherFixId = objectInput.readLong();

		patcherProductVersionId = objectInput.readLong();

		patcherProjectVersionId = objectInput.readLong();

		ticketEntryId = objectInput.readLong();

		hotfixId = objectInput.readLong();
		name = (String)objectInput.readObject();
		originalName = (String)objectInput.readObject();
		key = objectInput.readUTF();

		keyVersion = objectInput.readDouble();

		type = objectInput.readInt();

		latestBuild = objectInput.readBoolean();

		latestKeyBuild = objectInput.readBoolean();

		latestLESATicketBuild = objectInput.readBoolean();

		latestSupportTicketBuild = objectInput.readBoolean();
		accountEntryCode = objectInput.readUTF();
		lesaTicket = objectInput.readUTF();

		lesaTicketVersion = objectInput.readDouble();
		supportTicket = objectInput.readUTF();

		supportTicketVersion = objectInput.readDouble();
		fileName = objectInput.readUTF();
		sourceName = objectInput.readUTF();

		childBuild = objectInput.readBoolean();
		comments = (String)objectInput.readObject();
		qaComments = (String)objectInput.readObject();

		qaStatus = objectInput.readInt();
		requestKey = objectInput.readUTF();

		notified = objectInput.readBoolean();

		productVersion = objectInput.readInt();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
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

		objectOutput.writeLong(patcherAccountId);

		objectOutput.writeLong(patcherFixId);

		objectOutput.writeLong(patcherProductVersionId);

		objectOutput.writeLong(patcherProjectVersionId);

		objectOutput.writeLong(ticketEntryId);

		objectOutput.writeLong(hotfixId);

		if (name == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(name);
		}

		if (originalName == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(originalName);
		}

		if (key == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(key);
		}

		objectOutput.writeDouble(keyVersion);

		objectOutput.writeInt(type);

		objectOutput.writeBoolean(latestBuild);

		objectOutput.writeBoolean(latestKeyBuild);

		objectOutput.writeBoolean(latestLESATicketBuild);

		objectOutput.writeBoolean(latestSupportTicketBuild);

		if (accountEntryCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(accountEntryCode);
		}

		if (lesaTicket == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lesaTicket);
		}

		objectOutput.writeDouble(lesaTicketVersion);

		if (supportTicket == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(supportTicket);
		}

		objectOutput.writeDouble(supportTicketVersion);

		if (fileName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(fileName);
		}

		if (sourceName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sourceName);
		}

		objectOutput.writeBoolean(childBuild);

		if (comments == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(comments);
		}

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

	public long patcherBuildId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long patcherAccountId;
	public long patcherFixId;
	public long patcherProductVersionId;
	public long patcherProjectVersionId;
	public long ticketEntryId;
	public long hotfixId;
	public String name;
	public String originalName;
	public String key;
	public double keyVersion;
	public int type;
	public boolean latestBuild;
	public boolean latestKeyBuild;
	public boolean latestLESATicketBuild;
	public boolean latestSupportTicketBuild;
	public String accountEntryCode;
	public String lesaTicket;
	public double lesaTicketVersion;
	public String supportTicket;
	public double supportTicketVersion;
	public String fileName;
	public String sourceName;
	public boolean childBuild;
	public String comments;
	public String qaComments;
	public int qaStatus;
	public String requestKey;
	public boolean notified;
	public int productVersion;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}