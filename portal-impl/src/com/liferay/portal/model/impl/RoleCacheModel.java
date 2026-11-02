/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.Role;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Role in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RoleCacheModel
	implements CacheModel<Role>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RoleCacheModel)) {
			return false;
		}

		RoleCacheModel roleCacheModel = (RoleCacheModel)object;

		if ((roleId == roleCacheModel.roleId) &&
			(mvccVersion == roleCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, roleId);

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
		StringBundler sb = new StringBundler(37);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", roleId=");
		sb.append(roleId);
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
		sb.append(", name=");
		sb.append(name);
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", type=");
		sb.append(type);
		sb.append(", subtype=");
		sb.append(subtype);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Role toEntityModel() {
		RoleImpl roleImpl = new RoleImpl();

		roleImpl.setMvccVersion(mvccVersion);
		roleImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			roleImpl.setUuid("");
		}
		else {
			roleImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			roleImpl.setExternalReferenceCode("");
		}
		else {
			roleImpl.setExternalReferenceCode(externalReferenceCode);
		}

		roleImpl.setRoleId(roleId);
		roleImpl.setCompanyId(companyId);
		roleImpl.setUserId(userId);

		if (userName == null) {
			roleImpl.setUserName("");
		}
		else {
			roleImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			roleImpl.setCreateDate(null);
		}
		else {
			roleImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			roleImpl.setModifiedDate(null);
		}
		else {
			roleImpl.setModifiedDate(new Date(modifiedDate));
		}

		roleImpl.setClassNameId(classNameId);
		roleImpl.setClassPK(classPK);

		if (name == null) {
			roleImpl.setName("");
		}
		else {
			roleImpl.setName(name);
		}

		if (title == null) {
			roleImpl.setTitle("");
		}
		else {
			roleImpl.setTitle(title);
		}

		if (description == null) {
			roleImpl.setDescription("");
		}
		else {
			roleImpl.setDescription(description);
		}

		roleImpl.setType(type);

		if (subtype == null) {
			roleImpl.setSubtype("");
		}
		else {
			roleImpl.setSubtype(subtype);
		}

		roleImpl.setStatus(status);

		roleImpl.resetOriginalValues();

		return roleImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		roleId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();
		name = objectInput.readUTF();
		title = (String)objectInput.readObject();
		description = (String)objectInput.readObject();

		type = objectInput.readInt();
		subtype = objectInput.readUTF();

		status = objectInput.readInt();
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

		objectOutput.writeLong(roleId);

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

		if (description == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(description);
		}

		objectOutput.writeInt(type);

		if (subtype == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subtype);
		}

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long roleId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long classNameId;
	public long classPK;
	public String name;
	public String title;
	public String description;
	public int type;
	public String subtype;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1042202261