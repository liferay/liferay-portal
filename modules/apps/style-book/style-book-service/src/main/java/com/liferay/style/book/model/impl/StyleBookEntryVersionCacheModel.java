/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.style.book.model.StyleBookEntryVersion;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing StyleBookEntryVersion in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class StyleBookEntryVersionCacheModel
	implements CacheModel<StyleBookEntryVersion>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof StyleBookEntryVersionCacheModel)) {
			return false;
		}

		StyleBookEntryVersionCacheModel styleBookEntryVersionCacheModel =
			(StyleBookEntryVersionCacheModel)object;

		if ((styleBookEntryVersionId ==
				styleBookEntryVersionCacheModel.styleBookEntryVersionId) &&
			(mvccVersion == styleBookEntryVersionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, styleBookEntryVersionId);

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
		sb.append(", styleBookEntryVersionId=");
		sb.append(styleBookEntryVersionId);
		sb.append(", version=");
		sb.append(version);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", styleBookEntryId=");
		sb.append(styleBookEntryId);
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
		sb.append(", defaultStyleBookEntry=");
		sb.append(defaultStyleBookEntry);
		sb.append(", frontendTokenDefinition=");
		sb.append(frontendTokenDefinition);
		sb.append(", frontendTokensValues=");
		sb.append(frontendTokensValues);
		sb.append(", name=");
		sb.append(name);
		sb.append(", previewFileEntryId=");
		sb.append(previewFileEntryId);
		sb.append(", styleBookEntryKey=");
		sb.append(styleBookEntryKey);
		sb.append(", themeId=");
		sb.append(themeId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public StyleBookEntryVersion toEntityModel() {
		StyleBookEntryVersionImpl styleBookEntryVersionImpl =
			new StyleBookEntryVersionImpl();

		styleBookEntryVersionImpl.setMvccVersion(mvccVersion);
		styleBookEntryVersionImpl.setCtCollectionId(ctCollectionId);
		styleBookEntryVersionImpl.setStyleBookEntryVersionId(
			styleBookEntryVersionId);
		styleBookEntryVersionImpl.setVersion(version);

		if (uuid == null) {
			styleBookEntryVersionImpl.setUuid("");
		}
		else {
			styleBookEntryVersionImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			styleBookEntryVersionImpl.setExternalReferenceCode("");
		}
		else {
			styleBookEntryVersionImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		styleBookEntryVersionImpl.setStyleBookEntryId(styleBookEntryId);
		styleBookEntryVersionImpl.setGroupId(groupId);
		styleBookEntryVersionImpl.setCompanyId(companyId);
		styleBookEntryVersionImpl.setUserId(userId);

		if (userName == null) {
			styleBookEntryVersionImpl.setUserName("");
		}
		else {
			styleBookEntryVersionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			styleBookEntryVersionImpl.setCreateDate(null);
		}
		else {
			styleBookEntryVersionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			styleBookEntryVersionImpl.setModifiedDate(null);
		}
		else {
			styleBookEntryVersionImpl.setModifiedDate(new Date(modifiedDate));
		}

		styleBookEntryVersionImpl.setDefaultStyleBookEntry(
			defaultStyleBookEntry);

		if (frontendTokenDefinition == null) {
			styleBookEntryVersionImpl.setFrontendTokenDefinition("");
		}
		else {
			styleBookEntryVersionImpl.setFrontendTokenDefinition(
				frontendTokenDefinition);
		}

		if (frontendTokensValues == null) {
			styleBookEntryVersionImpl.setFrontendTokensValues("");
		}
		else {
			styleBookEntryVersionImpl.setFrontendTokensValues(
				frontendTokensValues);
		}

		if (name == null) {
			styleBookEntryVersionImpl.setName("");
		}
		else {
			styleBookEntryVersionImpl.setName(name);
		}

		styleBookEntryVersionImpl.setPreviewFileEntryId(previewFileEntryId);

		if (styleBookEntryKey == null) {
			styleBookEntryVersionImpl.setStyleBookEntryKey("");
		}
		else {
			styleBookEntryVersionImpl.setStyleBookEntryKey(styleBookEntryKey);
		}

		if (themeId == null) {
			styleBookEntryVersionImpl.setThemeId("");
		}
		else {
			styleBookEntryVersionImpl.setThemeId(themeId);
		}

		styleBookEntryVersionImpl.resetOriginalValues();

		return styleBookEntryVersionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		styleBookEntryVersionId = objectInput.readLong();

		version = objectInput.readInt();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		styleBookEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		defaultStyleBookEntry = objectInput.readBoolean();
		frontendTokenDefinition = (String)objectInput.readObject();
		frontendTokensValues = (String)objectInput.readObject();
		name = objectInput.readUTF();

		previewFileEntryId = objectInput.readLong();
		styleBookEntryKey = objectInput.readUTF();
		themeId = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(styleBookEntryVersionId);

		objectOutput.writeInt(version);

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

		objectOutput.writeLong(styleBookEntryId);

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

		objectOutput.writeBoolean(defaultStyleBookEntry);

		if (frontendTokenDefinition == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(frontendTokenDefinition);
		}

		if (frontendTokensValues == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(frontendTokensValues);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeLong(previewFileEntryId);

		if (styleBookEntryKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(styleBookEntryKey);
		}

		if (themeId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(themeId);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long styleBookEntryVersionId;
	public int version;
	public String uuid;
	public String externalReferenceCode;
	public long styleBookEntryId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public boolean defaultStyleBookEntry;
	public String frontendTokenDefinition;
	public String frontendTokensValues;
	public String name;
	public long previewFileEntryId;
	public String styleBookEntryKey;
	public String themeId;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1386151244