/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing WorkflowDefinitionLink in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WorkflowDefinitionLinkCacheModel
	implements CacheModel<WorkflowDefinitionLink>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowDefinitionLinkCacheModel)) {
			return false;
		}

		WorkflowDefinitionLinkCacheModel workflowDefinitionLinkCacheModel =
			(WorkflowDefinitionLinkCacheModel)object;

		if ((workflowDefinitionLinkId ==
				workflowDefinitionLinkCacheModel.workflowDefinitionLinkId) &&
			(mvccVersion == workflowDefinitionLinkCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, workflowDefinitionLinkId);

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
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", workflowDefinitionLinkId=");
		sb.append(workflowDefinitionLinkId);
		sb.append(", groupId=");
		sb.append(groupId);
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
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", typePK=");
		sb.append(typePK);
		sb.append(", workflowDefinitionName=");
		sb.append(workflowDefinitionName);
		sb.append(", workflowDefinitionVersion=");
		sb.append(workflowDefinitionVersion);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public WorkflowDefinitionLink toEntityModel() {
		WorkflowDefinitionLinkImpl workflowDefinitionLinkImpl =
			new WorkflowDefinitionLinkImpl();

		workflowDefinitionLinkImpl.setMvccVersion(mvccVersion);
		workflowDefinitionLinkImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			workflowDefinitionLinkImpl.setUuid("");
		}
		else {
			workflowDefinitionLinkImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			workflowDefinitionLinkImpl.setExternalReferenceCode("");
		}
		else {
			workflowDefinitionLinkImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		workflowDefinitionLinkImpl.setWorkflowDefinitionLinkId(
			workflowDefinitionLinkId);
		workflowDefinitionLinkImpl.setGroupId(groupId);
		workflowDefinitionLinkImpl.setCompanyId(companyId);
		workflowDefinitionLinkImpl.setUserId(userId);

		if (userName == null) {
			workflowDefinitionLinkImpl.setUserName("");
		}
		else {
			workflowDefinitionLinkImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			workflowDefinitionLinkImpl.setCreateDate(null);
		}
		else {
			workflowDefinitionLinkImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			workflowDefinitionLinkImpl.setModifiedDate(null);
		}
		else {
			workflowDefinitionLinkImpl.setModifiedDate(new Date(modifiedDate));
		}

		workflowDefinitionLinkImpl.setClassNameId(classNameId);
		workflowDefinitionLinkImpl.setClassPK(classPK);
		workflowDefinitionLinkImpl.setTypePK(typePK);

		if (workflowDefinitionName == null) {
			workflowDefinitionLinkImpl.setWorkflowDefinitionName("");
		}
		else {
			workflowDefinitionLinkImpl.setWorkflowDefinitionName(
				workflowDefinitionName);
		}

		workflowDefinitionLinkImpl.setWorkflowDefinitionVersion(
			workflowDefinitionVersion);

		workflowDefinitionLinkImpl.resetOriginalValues();

		return workflowDefinitionLinkImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		workflowDefinitionLinkId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		typePK = objectInput.readLong();
		workflowDefinitionName = objectInput.readUTF();

		workflowDefinitionVersion = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		if (externalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(externalReferenceCode);
		}

		objectOutput.writeLong(workflowDefinitionLinkId);

		objectOutput.writeLong(groupId);

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

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		objectOutput.writeLong(typePK);

		if (workflowDefinitionName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(workflowDefinitionName);
		}

		objectOutput.writeInt(workflowDefinitionVersion);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long workflowDefinitionLinkId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long classNameId;
	public long classPK;
	public long typePK;
	public String workflowDefinitionName;
	public int workflowDefinitionVersion;

}