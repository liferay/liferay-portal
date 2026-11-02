/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SAPEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SAPEntryCacheModel
	implements CacheModel<SAPEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SAPEntryCacheModel)) {
			return false;
		}

		SAPEntryCacheModel sapEntryCacheModel = (SAPEntryCacheModel)object;

		if (sapEntryId == sapEntryCacheModel.sapEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, sapEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(25);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", sapEntryId=");
		sb.append(sapEntryId);
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
		sb.append(", allowedServiceSignatures=");
		sb.append(allowedServiceSignatures);
		sb.append(", defaultSAPEntry=");
		sb.append(defaultSAPEntry);
		sb.append(", enabled=");
		sb.append(enabled);
		sb.append(", name=");
		sb.append(name);
		sb.append(", title=");
		sb.append(title);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SAPEntry toEntityModel() {
		SAPEntryImpl sapEntryImpl = new SAPEntryImpl();

		if (uuid == null) {
			sapEntryImpl.setUuid("");
		}
		else {
			sapEntryImpl.setUuid(uuid);
		}

		sapEntryImpl.setSapEntryId(sapEntryId);
		sapEntryImpl.setCompanyId(companyId);
		sapEntryImpl.setUserId(userId);

		if (userName == null) {
			sapEntryImpl.setUserName("");
		}
		else {
			sapEntryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			sapEntryImpl.setCreateDate(null);
		}
		else {
			sapEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			sapEntryImpl.setModifiedDate(null);
		}
		else {
			sapEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (allowedServiceSignatures == null) {
			sapEntryImpl.setAllowedServiceSignatures("");
		}
		else {
			sapEntryImpl.setAllowedServiceSignatures(allowedServiceSignatures);
		}

		sapEntryImpl.setDefaultSAPEntry(defaultSAPEntry);
		sapEntryImpl.setEnabled(enabled);

		if (name == null) {
			sapEntryImpl.setName("");
		}
		else {
			sapEntryImpl.setName(name);
		}

		if (title == null) {
			sapEntryImpl.setTitle("");
		}
		else {
			sapEntryImpl.setTitle(title);
		}

		sapEntryImpl.resetOriginalValues();

		return sapEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		uuid = objectInput.readUTF();

		sapEntryId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		allowedServiceSignatures = objectInput.readUTF();

		defaultSAPEntry = objectInput.readBoolean();

		enabled = objectInput.readBoolean();
		name = objectInput.readUTF();
		title = (String)objectInput.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(sapEntryId);

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

		if (allowedServiceSignatures == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(allowedServiceSignatures);
		}

		objectOutput.writeBoolean(defaultSAPEntry);

		objectOutput.writeBoolean(enabled);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (title == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(title);
		}
	}

	public String uuid;
	public long sapEntryId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String allowedServiceSignatures;
	public boolean defaultSAPEntry;
	public boolean enabled;
	public String name;
	public String title;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1855152747