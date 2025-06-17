/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.util.Date;

/**
 * The cache model class for representing KaleoDefinition in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class KaleoDefinitionCacheModel
	implements CacheModel<KaleoDefinition>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof KaleoDefinitionCacheModel)) {
			return false;
		}

		KaleoDefinitionCacheModel kaleoDefinitionCacheModel =
			(KaleoDefinitionCacheModel)object;

		if ((kaleoDefinitionId ==
				kaleoDefinitionCacheModel.kaleoDefinitionId) &&
			(mvccVersion == kaleoDefinitionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, kaleoDefinitionId);

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
		sb.append(", kaleoDefinitionId=");
		sb.append(kaleoDefinitionId);
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
		sb.append(", name=");
		sb.append(name);
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", content=");
		sb.append(content);
		sb.append(", scope=");
		sb.append(scope);
		sb.append(", version=");
		sb.append(version);
		sb.append(", active=");
		sb.append(active);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public KaleoDefinition toEntityModel() {
		KaleoDefinitionImpl kaleoDefinitionImpl = new KaleoDefinitionImpl();

		kaleoDefinitionImpl.setMvccVersion(mvccVersion);
		kaleoDefinitionImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			kaleoDefinitionImpl.setUuid("");
		}
		else {
			kaleoDefinitionImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			kaleoDefinitionImpl.setExternalReferenceCode("");
		}
		else {
			kaleoDefinitionImpl.setExternalReferenceCode(externalReferenceCode);
		}

		kaleoDefinitionImpl.setKaleoDefinitionId(kaleoDefinitionId);
		kaleoDefinitionImpl.setGroupId(groupId);
		kaleoDefinitionImpl.setCompanyId(companyId);
		kaleoDefinitionImpl.setUserId(userId);

		if (userName == null) {
			kaleoDefinitionImpl.setUserName("");
		}
		else {
			kaleoDefinitionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			kaleoDefinitionImpl.setCreateDate(null);
		}
		else {
			kaleoDefinitionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			kaleoDefinitionImpl.setModifiedDate(null);
		}
		else {
			kaleoDefinitionImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (name == null) {
			kaleoDefinitionImpl.setName("");
		}
		else {
			kaleoDefinitionImpl.setName(name);
		}

		if (title == null) {
			kaleoDefinitionImpl.setTitle("");
		}
		else {
			kaleoDefinitionImpl.setTitle(title);
		}

		if (description == null) {
			kaleoDefinitionImpl.setDescription("");
		}
		else {
			kaleoDefinitionImpl.setDescription(description);
		}

		if (content == null) {
			kaleoDefinitionImpl.setContent("");
		}
		else {
			kaleoDefinitionImpl.setContent(content);
		}

		if (scope == null) {
			kaleoDefinitionImpl.setScope("");
		}
		else {
			kaleoDefinitionImpl.setScope(scope);
		}

		kaleoDefinitionImpl.setVersion(version);
		kaleoDefinitionImpl.setActive(active);

		kaleoDefinitionImpl.resetOriginalValues();

		try {
			_contentAsXMLMethodHandle.invokeExact(
				kaleoDefinitionImpl, contentAsXML);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return kaleoDefinitionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		kaleoDefinitionId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		name = objectInput.readUTF();
		title = objectInput.readUTF();
		description = objectInput.readUTF();
		content = (String)objectInput.readObject();
		scope = objectInput.readUTF();

		version = objectInput.readInt();

		active = objectInput.readBoolean();

		contentAsXML = (String)objectInput.readObject();
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

		objectOutput.writeLong(kaleoDefinitionId);

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

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (title == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(title);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		if (content == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(content);
		}

		if (scope == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(scope);
		}

		objectOutput.writeInt(version);

		objectOutput.writeBoolean(active);

		objectOutput.writeObject(contentAsXML);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long kaleoDefinitionId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String name;
	public String title;
	public String description;
	public String content;
	public String scope;
	public int version;
	public boolean active;
	public volatile String contentAsXML;

	private static final MethodHandle _contentAsXMLMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_contentAsXMLMethodHandle = lookup.findSetter(
				KaleoDefinitionImpl.class, "_contentAsXML", String.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}