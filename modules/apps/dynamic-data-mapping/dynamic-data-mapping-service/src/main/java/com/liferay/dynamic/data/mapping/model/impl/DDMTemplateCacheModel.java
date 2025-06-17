/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
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
 * The cache model class for representing DDMTemplate in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDMTemplateCacheModel
	implements CacheModel<DDMTemplate>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMTemplateCacheModel)) {
			return false;
		}

		DDMTemplateCacheModel ddmTemplateCacheModel =
			(DDMTemplateCacheModel)object;

		if ((templateId == ddmTemplateCacheModel.templateId) &&
			(mvccVersion == ddmTemplateCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, templateId);

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
		sb.append(", templateId=");
		sb.append(templateId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", versionUserId=");
		sb.append(versionUserId);
		sb.append(", versionUserName=");
		sb.append(versionUserName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", resourceClassNameId=");
		sb.append(resourceClassNameId);
		sb.append(", templateKey=");
		sb.append(templateKey);
		sb.append(", version=");
		sb.append(version);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", type=");
		sb.append(type);
		sb.append(", mode=");
		sb.append(mode);
		sb.append(", language=");
		sb.append(language);
		sb.append(", script=");
		sb.append(script);
		sb.append(", cacheable=");
		sb.append(cacheable);
		sb.append(", smallImage=");
		sb.append(smallImage);
		sb.append(", smallImageId=");
		sb.append(smallImageId);
		sb.append(", smallImageURL=");
		sb.append(smallImageURL);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DDMTemplate toEntityModel() {
		DDMTemplateImpl ddmTemplateImpl = new DDMTemplateImpl();

		ddmTemplateImpl.setMvccVersion(mvccVersion);
		ddmTemplateImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			ddmTemplateImpl.setUuid("");
		}
		else {
			ddmTemplateImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			ddmTemplateImpl.setExternalReferenceCode("");
		}
		else {
			ddmTemplateImpl.setExternalReferenceCode(externalReferenceCode);
		}

		ddmTemplateImpl.setTemplateId(templateId);
		ddmTemplateImpl.setGroupId(groupId);
		ddmTemplateImpl.setCompanyId(companyId);
		ddmTemplateImpl.setUserId(userId);

		if (userName == null) {
			ddmTemplateImpl.setUserName("");
		}
		else {
			ddmTemplateImpl.setUserName(userName);
		}

		ddmTemplateImpl.setVersionUserId(versionUserId);

		if (versionUserName == null) {
			ddmTemplateImpl.setVersionUserName("");
		}
		else {
			ddmTemplateImpl.setVersionUserName(versionUserName);
		}

		if (createDate == Long.MIN_VALUE) {
			ddmTemplateImpl.setCreateDate(null);
		}
		else {
			ddmTemplateImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			ddmTemplateImpl.setModifiedDate(null);
		}
		else {
			ddmTemplateImpl.setModifiedDate(new Date(modifiedDate));
		}

		ddmTemplateImpl.setClassNameId(classNameId);
		ddmTemplateImpl.setClassPK(classPK);
		ddmTemplateImpl.setResourceClassNameId(resourceClassNameId);

		if (templateKey == null) {
			ddmTemplateImpl.setTemplateKey("");
		}
		else {
			ddmTemplateImpl.setTemplateKey(templateKey);
		}

		if (version == null) {
			ddmTemplateImpl.setVersion("");
		}
		else {
			ddmTemplateImpl.setVersion(version);
		}

		if (name == null) {
			ddmTemplateImpl.setName("");
		}
		else {
			ddmTemplateImpl.setName(name);
		}

		if (description == null) {
			ddmTemplateImpl.setDescription("");
		}
		else {
			ddmTemplateImpl.setDescription(description);
		}

		if (type == null) {
			ddmTemplateImpl.setType("");
		}
		else {
			ddmTemplateImpl.setType(type);
		}

		if (mode == null) {
			ddmTemplateImpl.setMode("");
		}
		else {
			ddmTemplateImpl.setMode(mode);
		}

		if (language == null) {
			ddmTemplateImpl.setLanguage("");
		}
		else {
			ddmTemplateImpl.setLanguage(language);
		}

		if (script == null) {
			ddmTemplateImpl.setScript("");
		}
		else {
			ddmTemplateImpl.setScript(script);
		}

		ddmTemplateImpl.setCacheable(cacheable);
		ddmTemplateImpl.setSmallImage(smallImage);
		ddmTemplateImpl.setSmallImageId(smallImageId);

		if (smallImageURL == null) {
			ddmTemplateImpl.setSmallImageURL("");
		}
		else {
			ddmTemplateImpl.setSmallImageURL(smallImageURL);
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			ddmTemplateImpl.setLastPublishDate(null);
		}
		else {
			ddmTemplateImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		ddmTemplateImpl.resetOriginalValues();

		try {
			_resourceClassNameMethodHandle.invokeExact(
				ddmTemplateImpl, resourceClassName);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return ddmTemplateImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		templateId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();

		versionUserId = objectInput.readLong();
		versionUserName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		resourceClassNameId = objectInput.readLong();
		templateKey = objectInput.readUTF();
		version = objectInput.readUTF();
		name = (String)objectInput.readObject();
		description = (String)objectInput.readObject();
		type = objectInput.readUTF();
		mode = objectInput.readUTF();
		language = objectInput.readUTF();
		script = (String)objectInput.readObject();

		cacheable = objectInput.readBoolean();

		smallImage = objectInput.readBoolean();

		smallImageId = objectInput.readLong();
		smallImageURL = objectInput.readUTF();
		lastPublishDate = objectInput.readLong();

		resourceClassName = (String)objectInput.readObject();
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

		objectOutput.writeLong(templateId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(versionUserId);

		if (versionUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(versionUserName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		objectOutput.writeLong(resourceClassNameId);

		if (templateKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(templateKey);
		}

		if (version == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(version);
		}

		if (name == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(name);
		}

		if (description == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(description);
		}

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}

		if (mode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(mode);
		}

		if (language == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(language);
		}

		if (script == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(script);
		}

		objectOutput.writeBoolean(cacheable);

		objectOutput.writeBoolean(smallImage);

		objectOutput.writeLong(smallImageId);

		if (smallImageURL == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(smallImageURL);
		}

		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeObject(resourceClassName);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long templateId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long versionUserId;
	public String versionUserName;
	public long createDate;
	public long modifiedDate;
	public long classNameId;
	public long classPK;
	public long resourceClassNameId;
	public String templateKey;
	public String version;
	public String name;
	public String description;
	public String type;
	public String mode;
	public String language;
	public String script;
	public boolean cacheable;
	public boolean smallImage;
	public long smallImageId;
	public String smallImageURL;
	public long lastPublishDate;
	public volatile String resourceClassName;

	private static final MethodHandle _resourceClassNameMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_resourceClassNameMethodHandle = lookup.findSetter(
				DDMTemplateImpl.class, "_resourceClassName", String.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}