/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.util.Date;

/**
 * The cache model class for representing Group in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class GroupCacheModel
	implements CacheModel<Group>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GroupCacheModel)) {
			return false;
		}

		GroupCacheModel groupCacheModel = (GroupCacheModel)object;

		if ((groupId == groupCacheModel.groupId) &&
			(mvccVersion == groupCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, groupId);

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
		StringBundler sb = new StringBundler(51);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", creatorUserId=");
		sb.append(creatorUserId);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", parentGroupId=");
		sb.append(parentGroupId);
		sb.append(", liveGroupId=");
		sb.append(liveGroupId);
		sb.append(", treePath=");
		sb.append(treePath);
		sb.append(", groupKey=");
		sb.append(groupKey);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", type=");
		sb.append(type);
		sb.append(", typeSettings=");
		sb.append(typeSettings);
		sb.append(", manualMembership=");
		sb.append(manualMembership);
		sb.append(", membershipRestriction=");
		sb.append(membershipRestriction);
		sb.append(", friendlyURL=");
		sb.append(friendlyURL);
		sb.append(", site=");
		sb.append(site);
		sb.append(", remoteStagingGroupCount=");
		sb.append(remoteStagingGroupCount);
		sb.append(", inheritContent=");
		sb.append(inheritContent);
		sb.append(", active=");
		sb.append(active);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Group toEntityModel() {
		GroupImpl groupImpl = new GroupImpl();

		groupImpl.setMvccVersion(mvccVersion);
		groupImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			groupImpl.setUuid("");
		}
		else {
			groupImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			groupImpl.setExternalReferenceCode("");
		}
		else {
			groupImpl.setExternalReferenceCode(externalReferenceCode);
		}

		groupImpl.setGroupId(groupId);
		groupImpl.setCompanyId(companyId);
		groupImpl.setCreatorUserId(creatorUserId);

		if (modifiedDate == Long.MIN_VALUE) {
			groupImpl.setModifiedDate(null);
		}
		else {
			groupImpl.setModifiedDate(new Date(modifiedDate));
		}

		groupImpl.setClassNameId(classNameId);
		groupImpl.setClassPK(classPK);
		groupImpl.setParentGroupId(parentGroupId);
		groupImpl.setLiveGroupId(liveGroupId);

		if (treePath == null) {
			groupImpl.setTreePath("");
		}
		else {
			groupImpl.setTreePath(treePath);
		}

		if (groupKey == null) {
			groupImpl.setGroupKey("");
		}
		else {
			groupImpl.setGroupKey(groupKey);
		}

		if (name == null) {
			groupImpl.setName("");
		}
		else {
			groupImpl.setName(name);
		}

		if (description == null) {
			groupImpl.setDescription("");
		}
		else {
			groupImpl.setDescription(description);
		}

		groupImpl.setType(type);

		if (typeSettings == null) {
			groupImpl.setTypeSettings("");
		}
		else {
			groupImpl.setTypeSettings(typeSettings);
		}

		groupImpl.setManualMembership(manualMembership);
		groupImpl.setMembershipRestriction(membershipRestriction);

		if (friendlyURL == null) {
			groupImpl.setFriendlyURL("");
		}
		else {
			groupImpl.setFriendlyURL(friendlyURL);
		}

		groupImpl.setSite(site);
		groupImpl.setRemoteStagingGroupCount(remoteStagingGroupCount);
		groupImpl.setInheritContent(inheritContent);
		groupImpl.setActive(active);

		groupImpl.resetOriginalValues();

		try {
			_classNameMethodHandle.invokeExact(groupImpl, className);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return groupImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		creatorUserId = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		parentGroupId = objectInput.readLong();

		liveGroupId = objectInput.readLong();
		treePath = objectInput.readUTF();
		groupKey = objectInput.readUTF();
		name = objectInput.readUTF();
		description = objectInput.readUTF();

		type = objectInput.readInt();
		typeSettings = (String)objectInput.readObject();

		manualMembership = objectInput.readBoolean();

		membershipRestriction = objectInput.readInt();
		friendlyURL = objectInput.readUTF();

		site = objectInput.readBoolean();

		remoteStagingGroupCount = objectInput.readInt();

		inheritContent = objectInput.readBoolean();

		active = objectInput.readBoolean();

		className = (String)objectInput.readObject();
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

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(creatorUserId);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		objectOutput.writeLong(parentGroupId);

		objectOutput.writeLong(liveGroupId);

		if (treePath == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(treePath);
		}

		if (groupKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(groupKey);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeInt(type);

		if (typeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(typeSettings);
		}

		objectOutput.writeBoolean(manualMembership);

		objectOutput.writeInt(membershipRestriction);

		if (friendlyURL == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(friendlyURL);
		}

		objectOutput.writeBoolean(site);

		objectOutput.writeInt(remoteStagingGroupCount);

		objectOutput.writeBoolean(inheritContent);

		objectOutput.writeBoolean(active);

		objectOutput.writeObject(className);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long groupId;
	public long companyId;
	public long creatorUserId;
	public long modifiedDate;
	public long classNameId;
	public long classPK;
	public long parentGroupId;
	public long liveGroupId;
	public String treePath;
	public String groupKey;
	public String name;
	public String description;
	public int type;
	public String typeSettings;
	public boolean manualMembership;
	public int membershipRestriction;
	public String friendlyURL;
	public boolean site;
	public int remoteStagingGroupCount;
	public boolean inheritContent;
	public boolean active;
	public volatile String className;

	private static final MethodHandle _classNameMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_classNameMethodHandle = lookup.findSetter(
				GroupImpl.class, "_className", String.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}