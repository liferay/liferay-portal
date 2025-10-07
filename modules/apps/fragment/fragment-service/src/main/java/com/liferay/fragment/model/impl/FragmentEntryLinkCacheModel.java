/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.model.impl;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.util.Date;

/**
 * The cache model class for representing FragmentEntryLink in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FragmentEntryLinkCacheModel
	implements CacheModel<FragmentEntryLink>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentEntryLinkCacheModel)) {
			return false;
		}

		FragmentEntryLinkCacheModel fragmentEntryLinkCacheModel =
			(FragmentEntryLinkCacheModel)object;

		if ((fragmentEntryLinkId ==
				fragmentEntryLinkCacheModel.fragmentEntryLinkId) &&
			(mvccVersion == fragmentEntryLinkCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, fragmentEntryLinkId);

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
		StringBundler sb = new StringBundler(59);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", fragmentEntryLinkId=");
		sb.append(fragmentEntryLinkId);
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
		sb.append(", originalFragmentEntryLinkId=");
		sb.append(originalFragmentEntryLinkId);
		sb.append(", fragmentEntryId=");
		sb.append(fragmentEntryId);
		sb.append(", segmentsExperienceId=");
		sb.append(segmentsExperienceId);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", plid=");
		sb.append(plid);
		sb.append(", css=");
		sb.append(css);
		sb.append(", html=");
		sb.append(html);
		sb.append(", js=");
		sb.append(js);
		sb.append(", configuration=");
		sb.append(configuration);
		sb.append(", deleted=");
		sb.append(deleted);
		sb.append(", editableValues=");
		sb.append(editableValues);
		sb.append(", namespace=");
		sb.append(namespace);
		sb.append(", position=");
		sb.append(position);
		sb.append(", rendererKey=");
		sb.append(rendererKey);
		sb.append(", type=");
		sb.append(type);
		sb.append(", lastPropagationDate=");
		sb.append(lastPropagationDate);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FragmentEntryLink toEntityModel() {
		FragmentEntryLinkImpl fragmentEntryLinkImpl =
			new FragmentEntryLinkImpl();

		fragmentEntryLinkImpl.setMvccVersion(mvccVersion);
		fragmentEntryLinkImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			fragmentEntryLinkImpl.setUuid("");
		}
		else {
			fragmentEntryLinkImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			fragmentEntryLinkImpl.setExternalReferenceCode("");
		}
		else {
			fragmentEntryLinkImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		fragmentEntryLinkImpl.setFragmentEntryLinkId(fragmentEntryLinkId);
		fragmentEntryLinkImpl.setGroupId(groupId);
		fragmentEntryLinkImpl.setCompanyId(companyId);
		fragmentEntryLinkImpl.setUserId(userId);

		if (userName == null) {
			fragmentEntryLinkImpl.setUserName("");
		}
		else {
			fragmentEntryLinkImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			fragmentEntryLinkImpl.setCreateDate(null);
		}
		else {
			fragmentEntryLinkImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			fragmentEntryLinkImpl.setModifiedDate(null);
		}
		else {
			fragmentEntryLinkImpl.setModifiedDate(new Date(modifiedDate));
		}

		fragmentEntryLinkImpl.setOriginalFragmentEntryLinkId(
			originalFragmentEntryLinkId);
		fragmentEntryLinkImpl.setFragmentEntryId(fragmentEntryId);
		fragmentEntryLinkImpl.setSegmentsExperienceId(segmentsExperienceId);
		fragmentEntryLinkImpl.setClassNameId(classNameId);
		fragmentEntryLinkImpl.setClassPK(classPK);
		fragmentEntryLinkImpl.setPlid(plid);

		if (css == null) {
			fragmentEntryLinkImpl.setCss("");
		}
		else {
			fragmentEntryLinkImpl.setCss(css);
		}

		if (html == null) {
			fragmentEntryLinkImpl.setHtml("");
		}
		else {
			fragmentEntryLinkImpl.setHtml(html);
		}

		if (js == null) {
			fragmentEntryLinkImpl.setJs("");
		}
		else {
			fragmentEntryLinkImpl.setJs(js);
		}

		if (configuration == null) {
			fragmentEntryLinkImpl.setConfiguration("");
		}
		else {
			fragmentEntryLinkImpl.setConfiguration(configuration);
		}

		fragmentEntryLinkImpl.setDeleted(deleted);

		if (editableValues == null) {
			fragmentEntryLinkImpl.setEditableValues("");
		}
		else {
			fragmentEntryLinkImpl.setEditableValues(editableValues);
		}

		if (namespace == null) {
			fragmentEntryLinkImpl.setNamespace("");
		}
		else {
			fragmentEntryLinkImpl.setNamespace(namespace);
		}

		fragmentEntryLinkImpl.setPosition(position);

		if (rendererKey == null) {
			fragmentEntryLinkImpl.setRendererKey("");
		}
		else {
			fragmentEntryLinkImpl.setRendererKey(rendererKey);
		}

		fragmentEntryLinkImpl.setType(type);

		if (lastPropagationDate == Long.MIN_VALUE) {
			fragmentEntryLinkImpl.setLastPropagationDate(null);
		}
		else {
			fragmentEntryLinkImpl.setLastPropagationDate(
				new Date(lastPropagationDate));
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			fragmentEntryLinkImpl.setLastPublishDate(null);
		}
		else {
			fragmentEntryLinkImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		fragmentEntryLinkImpl.resetOriginalValues();

		try {
			_configurationJSONObjectMethodHandle.invokeExact(
				fragmentEntryLinkImpl, configurationJSONObject);

			_editableValuesJSONObjectMethodHandle.invokeExact(
				fragmentEntryLinkImpl, editableValuesJSONObject);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return fragmentEntryLinkImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		fragmentEntryLinkId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		originalFragmentEntryLinkId = objectInput.readLong();

		fragmentEntryId = objectInput.readLong();

		segmentsExperienceId = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		plid = objectInput.readLong();
		css = (String)objectInput.readObject();
		html = (String)objectInput.readObject();
		js = (String)objectInput.readObject();
		configuration = (String)objectInput.readObject();

		deleted = objectInput.readBoolean();
		editableValues = (String)objectInput.readObject();
		namespace = objectInput.readUTF();

		position = objectInput.readInt();
		rendererKey = objectInput.readUTF();

		type = objectInput.readInt();
		lastPropagationDate = objectInput.readLong();
		lastPublishDate = objectInput.readLong();

		configurationJSONObject =
			(com.liferay.portal.kernel.json.JSONObject)objectInput.readObject();

		editableValuesJSONObject =
			(com.liferay.portal.kernel.json.JSONObject)objectInput.readObject();
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

		objectOutput.writeLong(fragmentEntryLinkId);

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

		objectOutput.writeLong(originalFragmentEntryLinkId);

		objectOutput.writeLong(fragmentEntryId);

		objectOutput.writeLong(segmentsExperienceId);

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		objectOutput.writeLong(plid);

		if (css == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(css);
		}

		if (html == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(html);
		}

		if (js == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(js);
		}

		if (configuration == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(configuration);
		}

		objectOutput.writeBoolean(deleted);

		if (editableValues == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(editableValues);
		}

		if (namespace == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(namespace);
		}

		objectOutput.writeInt(position);

		if (rendererKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(rendererKey);
		}

		objectOutput.writeInt(type);
		objectOutput.writeLong(lastPropagationDate);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeObject(configurationJSONObject);

		objectOutput.writeObject(editableValuesJSONObject);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long fragmentEntryLinkId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long originalFragmentEntryLinkId;
	public long fragmentEntryId;
	public long segmentsExperienceId;
	public long classNameId;
	public long classPK;
	public long plid;
	public String css;
	public String html;
	public String js;
	public String configuration;
	public boolean deleted;
	public String editableValues;
	public String namespace;
	public int position;
	public String rendererKey;
	public int type;
	public long lastPropagationDate;
	public long lastPublishDate;
	public volatile com.liferay.portal.kernel.json.JSONObject
		configurationJSONObject;
	public volatile com.liferay.portal.kernel.json.JSONObject
		editableValuesJSONObject;

	private static final MethodHandle _configurationJSONObjectMethodHandle;
	private static final MethodHandle _editableValuesJSONObjectMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_configurationJSONObjectMethodHandle = lookup.findSetter(
				FragmentEntryLinkImpl.class, "_configurationJSONObject",
				com.liferay.portal.kernel.json.JSONObject.class);

			_editableValuesJSONObjectMethodHandle = lookup.findSetter(
				FragmentEntryLinkImpl.class, "_editableValuesJSONObject",
				com.liferay.portal.kernel.json.JSONObject.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}