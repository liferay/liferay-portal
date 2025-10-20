/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing PermissionCheckFinderEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PermissionCheckFinderEntryCacheModel
	implements CacheModel<PermissionCheckFinderEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PermissionCheckFinderEntryCacheModel)) {
			return false;
		}

		PermissionCheckFinderEntryCacheModel
			permissionCheckFinderEntryCacheModel =
				(PermissionCheckFinderEntryCacheModel)object;

		if (permissionCheckFinderEntryId ==
				permissionCheckFinderEntryCacheModel.
					permissionCheckFinderEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, permissionCheckFinderEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(15);

		sb.append("{permissionCheckFinderEntryId=");
		sb.append(permissionCheckFinderEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", integer=");
		sb.append(integer);
		sb.append(", name=");
		sb.append(name);
		sb.append(", type=");
		sb.append(type);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PermissionCheckFinderEntry toEntityModel() {
		PermissionCheckFinderEntryImpl permissionCheckFinderEntryImpl =
			new PermissionCheckFinderEntryImpl();

		permissionCheckFinderEntryImpl.setPermissionCheckFinderEntryId(
			permissionCheckFinderEntryId);
		permissionCheckFinderEntryImpl.setGroupId(groupId);
		permissionCheckFinderEntryImpl.setCompanyId(companyId);
		permissionCheckFinderEntryImpl.setUserId(userId);
		permissionCheckFinderEntryImpl.setInteger(integer);

		if (name == null) {
			permissionCheckFinderEntryImpl.setName("");
		}
		else {
			permissionCheckFinderEntryImpl.setName(name);
		}

		if (type == null) {
			permissionCheckFinderEntryImpl.setType("");
		}
		else {
			permissionCheckFinderEntryImpl.setType(type);
		}

		permissionCheckFinderEntryImpl.resetOriginalValues();

		return permissionCheckFinderEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		permissionCheckFinderEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();

		integer = objectInput.readInt();
		name = objectInput.readUTF();
		type = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(permissionCheckFinderEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		objectOutput.writeInt(integer);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}
	}

	public long permissionCheckFinderEntryId;
	public long groupId;
	public long companyId;
	public long userId;
	public int integer;
	public String name;
	public String type;

}