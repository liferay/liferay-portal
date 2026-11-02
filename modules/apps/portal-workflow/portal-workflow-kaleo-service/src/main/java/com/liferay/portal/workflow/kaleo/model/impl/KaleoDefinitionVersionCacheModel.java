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
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.util.Date;
import java.util.Map;

/**
 * The cache model class for representing KaleoDefinitionVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class KaleoDefinitionVersionCacheModel
	implements CacheModel<KaleoDefinitionVersion>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof KaleoDefinitionVersionCacheModel)) {
			return false;
		}

		KaleoDefinitionVersionCacheModel kaleoDefinitionVersionCacheModel =
			(KaleoDefinitionVersionCacheModel)object;

		if ((kaleoDefinitionVersionId ==
				kaleoDefinitionVersionCacheModel.kaleoDefinitionVersionId) &&
			(mvccVersion == kaleoDefinitionVersionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, kaleoDefinitionVersionId);

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
		StringBundler sb = new StringBundler(41);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", kaleoDefinitionVersionId=");
		sb.append(kaleoDefinitionVersionId);
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
		sb.append(", kaleoDefinitionId=");
		sb.append(kaleoDefinitionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", title=");
		sb.append(title);
		sb.append(", description=");
		sb.append(description);
		sb.append(", content=");
		sb.append(content);
		sb.append(", version=");
		sb.append(version);
		sb.append(", startKaleoNodeId=");
		sb.append(startKaleoNodeId);
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
	public KaleoDefinitionVersion toEntityModel() {
		KaleoDefinitionVersionImpl kaleoDefinitionVersionImpl =
			new KaleoDefinitionVersionImpl();

		kaleoDefinitionVersionImpl.setMvccVersion(mvccVersion);
		kaleoDefinitionVersionImpl.setCtCollectionId(ctCollectionId);
		kaleoDefinitionVersionImpl.setKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
		kaleoDefinitionVersionImpl.setGroupId(groupId);
		kaleoDefinitionVersionImpl.setCompanyId(companyId);
		kaleoDefinitionVersionImpl.setUserId(userId);

		if (userName == null) {
			kaleoDefinitionVersionImpl.setUserName("");
		}
		else {
			kaleoDefinitionVersionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			kaleoDefinitionVersionImpl.setCreateDate(null);
		}
		else {
			kaleoDefinitionVersionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			kaleoDefinitionVersionImpl.setModifiedDate(null);
		}
		else {
			kaleoDefinitionVersionImpl.setModifiedDate(new Date(modifiedDate));
		}

		kaleoDefinitionVersionImpl.setKaleoDefinitionId(kaleoDefinitionId);

		if (name == null) {
			kaleoDefinitionVersionImpl.setName("");
		}
		else {
			kaleoDefinitionVersionImpl.setName(name);
		}

		if (title == null) {
			kaleoDefinitionVersionImpl.setTitle("");
		}
		else {
			kaleoDefinitionVersionImpl.setTitle(title);
		}

		if (description == null) {
			kaleoDefinitionVersionImpl.setDescription("");
		}
		else {
			kaleoDefinitionVersionImpl.setDescription(description);
		}

		if (content == null) {
			kaleoDefinitionVersionImpl.setContent("");
		}
		else {
			kaleoDefinitionVersionImpl.setContent(content);
		}

		if (version == null) {
			kaleoDefinitionVersionImpl.setVersion("");
		}
		else {
			kaleoDefinitionVersionImpl.setVersion(version);
		}

		kaleoDefinitionVersionImpl.setStartKaleoNodeId(startKaleoNodeId);
		kaleoDefinitionVersionImpl.setStatus(status);
		kaleoDefinitionVersionImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			kaleoDefinitionVersionImpl.setStatusByUserName("");
		}
		else {
			kaleoDefinitionVersionImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			kaleoDefinitionVersionImpl.setStatusDate(null);
		}
		else {
			kaleoDefinitionVersionImpl.setStatusDate(new Date(statusDate));
		}

		kaleoDefinitionVersionImpl.resetOriginalValues();

		try {
			_blockingKaleoTimerExistsMethodHandle.invokeExact(
				kaleoDefinitionVersionImpl, blockingKaleoTimerExists);

			_contentAsXMLMethodHandle.invokeExact(
				kaleoDefinitionVersionImpl, contentAsXML);

			_kaleoNodeKaleoActionsMapMethodHandle.invokeExact(
				kaleoDefinitionVersionImpl, kaleoNodeKaleoActionsMap);

			_kaleoNodeKaleoNotificationsMapMethodHandle.invokeExact(
				kaleoDefinitionVersionImpl, kaleoNodeKaleoNotificationsMap);

			_kaleoNodeKaleoTransitionsMapMethodHandle.invokeExact(
				kaleoDefinitionVersionImpl, kaleoNodeKaleoTransitionsMap);

			_kaleoTimerExistsMethodHandle.invokeExact(
				kaleoDefinitionVersionImpl, kaleoTimerExists);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return kaleoDefinitionVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		kaleoDefinitionVersionId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		kaleoDefinitionId = objectInput.readLong();
		name = objectInput.readUTF();
		title = (String)objectInput.readObject();
		description = objectInput.readUTF();
		content = (String)objectInput.readObject();
		version = objectInput.readUTF();

		startKaleoNodeId = objectInput.readLong();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();

		blockingKaleoTimerExists = (Boolean)objectInput.readObject();

		contentAsXML = (String)objectInput.readObject();

		kaleoNodeKaleoActionsMap = (Map)objectInput.readObject();

		kaleoNodeKaleoNotificationsMap = (Map)objectInput.readObject();

		kaleoNodeKaleoTransitionsMap = (Map)objectInput.readObject();

		kaleoTimerExists = (Boolean)objectInput.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(kaleoDefinitionVersionId);

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

		objectOutput.writeLong(kaleoDefinitionId);

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

		if (version == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(version);
		}

		objectOutput.writeLong(startKaleoNodeId);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);

		objectOutput.writeObject(blockingKaleoTimerExists);

		objectOutput.writeObject(contentAsXML);

		objectOutput.writeObject(kaleoNodeKaleoActionsMap);

		objectOutput.writeObject(kaleoNodeKaleoNotificationsMap);

		objectOutput.writeObject(kaleoNodeKaleoTransitionsMap);

		objectOutput.writeObject(kaleoTimerExists);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long kaleoDefinitionVersionId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long kaleoDefinitionId;
	public String name;
	public String title;
	public String description;
	public String content;
	public String version;
	public long startKaleoNodeId;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;
	public volatile Boolean blockingKaleoTimerExists;
	public volatile String contentAsXML;
	public volatile Map kaleoNodeKaleoActionsMap;
	public volatile Map kaleoNodeKaleoNotificationsMap;
	public volatile Map kaleoNodeKaleoTransitionsMap;
	public volatile Boolean kaleoTimerExists;

	private static final MethodHandle _blockingKaleoTimerExistsMethodHandle;
	private static final MethodHandle _contentAsXMLMethodHandle;
	private static final MethodHandle _kaleoNodeKaleoActionsMapMethodHandle;
	private static final MethodHandle
		_kaleoNodeKaleoNotificationsMapMethodHandle;
	private static final MethodHandle _kaleoNodeKaleoTransitionsMapMethodHandle;
	private static final MethodHandle _kaleoTimerExistsMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_blockingKaleoTimerExistsMethodHandle = lookup.findSetter(
				KaleoDefinitionVersionImpl.class, "_blockingKaleoTimerExists",
				Boolean.class);

			_contentAsXMLMethodHandle = lookup.findSetter(
				KaleoDefinitionVersionImpl.class, "_contentAsXML",
				String.class);

			_kaleoNodeKaleoActionsMapMethodHandle = lookup.findSetter(
				KaleoDefinitionVersionImpl.class, "_kaleoNodeKaleoActionsMap",
				Map.class);

			_kaleoNodeKaleoNotificationsMapMethodHandle = lookup.findSetter(
				KaleoDefinitionVersionImpl.class,
				"_kaleoNodeKaleoNotificationsMap", Map.class);

			_kaleoNodeKaleoTransitionsMapMethodHandle = lookup.findSetter(
				KaleoDefinitionVersionImpl.class,
				"_kaleoNodeKaleoTransitionsMap", Map.class);

			_kaleoTimerExistsMethodHandle = lookup.findSetter(
				KaleoDefinitionVersionImpl.class, "_kaleoTimerExists",
				Boolean.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1759556925