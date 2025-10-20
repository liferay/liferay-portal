/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Layout in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutCacheModel
	implements CacheModel<Layout>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutCacheModel)) {
			return false;
		}

		LayoutCacheModel layoutCacheModel = (LayoutCacheModel)object;

		if ((plid == layoutCacheModel.plid) &&
			(mvccVersion == layoutCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, plid);

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
		StringBundler sb = new StringBundler(89);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", plid=");
		sb.append(plid);
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
		sb.append(", parentPlid=");
		sb.append(parentPlid);
		sb.append(", privateLayout=");
		sb.append(privateLayout);
		sb.append(", layoutId=");
		sb.append(layoutId);
		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);
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
		sb.append(", keywords=");
		sb.append(keywords);
		sb.append(", robots=");
		sb.append(robots);
		sb.append(", type=");
		sb.append(type);
		sb.append(", typeSettings=");
		sb.append(typeSettings);
		sb.append(", hidden=");
		sb.append(hidden);
		sb.append(", system=");
		sb.append(system);
		sb.append(", friendlyURL=");
		sb.append(friendlyURL);
		sb.append(", iconImageId=");
		sb.append(iconImageId);
		sb.append(", themeId=");
		sb.append(themeId);
		sb.append(", colorSchemeId=");
		sb.append(colorSchemeId);
		sb.append(", styleBookEntryERC=");
		sb.append(styleBookEntryERC);
		sb.append(", css=");
		sb.append(css);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", faviconFileEntryId=");
		sb.append(faviconFileEntryId);
		sb.append(", masterLayoutPlid=");
		sb.append(masterLayoutPlid);
		sb.append(", layoutPrototypeUuid=");
		sb.append(layoutPrototypeUuid);
		sb.append(", layoutPrototypeLinkEnabled=");
		sb.append(layoutPrototypeLinkEnabled);
		sb.append(", layoutSetPrototypeLayoutERC=");
		sb.append(layoutSetPrototypeLayoutERC);
		sb.append(", publishDate=");
		sb.append(publishDate);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
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
	public Layout toEntityModel() {
		LayoutImpl layoutImpl = new LayoutImpl();

		layoutImpl.setMvccVersion(mvccVersion);
		layoutImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			layoutImpl.setUuid("");
		}
		else {
			layoutImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			layoutImpl.setExternalReferenceCode("");
		}
		else {
			layoutImpl.setExternalReferenceCode(externalReferenceCode);
		}

		layoutImpl.setPlid(plid);
		layoutImpl.setGroupId(groupId);
		layoutImpl.setCompanyId(companyId);
		layoutImpl.setUserId(userId);

		if (userName == null) {
			layoutImpl.setUserName("");
		}
		else {
			layoutImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			layoutImpl.setCreateDate(null);
		}
		else {
			layoutImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutImpl.setModifiedDate(null);
		}
		else {
			layoutImpl.setModifiedDate(new Date(modifiedDate));
		}

		layoutImpl.setParentPlid(parentPlid);
		layoutImpl.setPrivateLayout(privateLayout);
		layoutImpl.setLayoutId(layoutId);
		layoutImpl.setParentLayoutId(parentLayoutId);
		layoutImpl.setClassNameId(classNameId);
		layoutImpl.setClassPK(classPK);

		if (name == null) {
			layoutImpl.setName("");
		}
		else {
			layoutImpl.setName(name);
		}

		if (title == null) {
			layoutImpl.setTitle("");
		}
		else {
			layoutImpl.setTitle(title);
		}

		if (description == null) {
			layoutImpl.setDescription("");
		}
		else {
			layoutImpl.setDescription(description);
		}

		if (keywords == null) {
			layoutImpl.setKeywords("");
		}
		else {
			layoutImpl.setKeywords(keywords);
		}

		if (robots == null) {
			layoutImpl.setRobots("");
		}
		else {
			layoutImpl.setRobots(robots);
		}

		if (type == null) {
			layoutImpl.setType("");
		}
		else {
			layoutImpl.setType(type);
		}

		if (typeSettings == null) {
			layoutImpl.setTypeSettings("");
		}
		else {
			layoutImpl.setTypeSettings(typeSettings);
		}

		layoutImpl.setHidden(hidden);
		layoutImpl.setSystem(system);

		if (friendlyURL == null) {
			layoutImpl.setFriendlyURL("");
		}
		else {
			layoutImpl.setFriendlyURL(friendlyURL);
		}

		layoutImpl.setIconImageId(iconImageId);

		if (themeId == null) {
			layoutImpl.setThemeId("");
		}
		else {
			layoutImpl.setThemeId(themeId);
		}

		if (colorSchemeId == null) {
			layoutImpl.setColorSchemeId("");
		}
		else {
			layoutImpl.setColorSchemeId(colorSchemeId);
		}

		if (styleBookEntryERC == null) {
			layoutImpl.setStyleBookEntryERC("");
		}
		else {
			layoutImpl.setStyleBookEntryERC(styleBookEntryERC);
		}

		if (css == null) {
			layoutImpl.setCss("");
		}
		else {
			layoutImpl.setCss(css);
		}

		layoutImpl.setPriority(priority);
		layoutImpl.setFaviconFileEntryId(faviconFileEntryId);
		layoutImpl.setMasterLayoutPlid(masterLayoutPlid);

		if (layoutPrototypeUuid == null) {
			layoutImpl.setLayoutPrototypeUuid("");
		}
		else {
			layoutImpl.setLayoutPrototypeUuid(layoutPrototypeUuid);
		}

		layoutImpl.setLayoutPrototypeLinkEnabled(layoutPrototypeLinkEnabled);

		if (layoutSetPrototypeLayoutERC == null) {
			layoutImpl.setLayoutSetPrototypeLayoutERC("");
		}
		else {
			layoutImpl.setLayoutSetPrototypeLayoutERC(
				layoutSetPrototypeLayoutERC);
		}

		if (publishDate == Long.MIN_VALUE) {
			layoutImpl.setPublishDate(null);
		}
		else {
			layoutImpl.setPublishDate(new Date(publishDate));
		}

		if (lastPublishDate == Long.MIN_VALUE) {
			layoutImpl.setLastPublishDate(null);
		}
		else {
			layoutImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		layoutImpl.setStatus(status);
		layoutImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			layoutImpl.setStatusByUserName("");
		}
		else {
			layoutImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			layoutImpl.setStatusDate(null);
		}
		else {
			layoutImpl.setStatusDate(new Date(statusDate));
		}

		layoutImpl.resetOriginalValues();

		return layoutImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		plid = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		parentPlid = objectInput.readLong();

		privateLayout = objectInput.readBoolean();

		layoutId = objectInput.readLong();

		parentLayoutId = objectInput.readLong();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();
		name = objectInput.readUTF();
		title = (String)objectInput.readObject();
		description = (String)objectInput.readObject();
		keywords = objectInput.readUTF();
		robots = objectInput.readUTF();
		type = objectInput.readUTF();
		typeSettings = (String)objectInput.readObject();

		hidden = objectInput.readBoolean();

		system = objectInput.readBoolean();
		friendlyURL = objectInput.readUTF();

		iconImageId = objectInput.readLong();
		themeId = objectInput.readUTF();
		colorSchemeId = objectInput.readUTF();
		styleBookEntryERC = objectInput.readUTF();
		css = (String)objectInput.readObject();

		priority = objectInput.readInt();

		faviconFileEntryId = objectInput.readLong();

		masterLayoutPlid = objectInput.readLong();
		layoutPrototypeUuid = objectInput.readUTF();

		layoutPrototypeLinkEnabled = objectInput.readBoolean();
		layoutSetPrototypeLayoutERC = objectInput.readUTF();
		publishDate = objectInput.readLong();
		lastPublishDate = objectInput.readLong();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
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

		objectOutput.writeLong(plid);

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

		objectOutput.writeLong(parentPlid);

		objectOutput.writeBoolean(privateLayout);

		objectOutput.writeLong(layoutId);

		objectOutput.writeLong(parentLayoutId);

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

		if (keywords == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(keywords);
		}

		if (robots == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(robots);
		}

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}

		if (typeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(typeSettings);
		}

		objectOutput.writeBoolean(hidden);

		objectOutput.writeBoolean(system);

		if (friendlyURL == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(friendlyURL);
		}

		objectOutput.writeLong(iconImageId);

		if (themeId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(themeId);
		}

		if (colorSchemeId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(colorSchemeId);
		}

		if (styleBookEntryERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(styleBookEntryERC);
		}

		if (css == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(css);
		}

		objectOutput.writeInt(priority);

		objectOutput.writeLong(faviconFileEntryId);

		objectOutput.writeLong(masterLayoutPlid);

		if (layoutPrototypeUuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(layoutPrototypeUuid);
		}

		objectOutput.writeBoolean(layoutPrototypeLinkEnabled);

		if (layoutSetPrototypeLayoutERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(layoutSetPrototypeLayoutERC);
		}

		objectOutput.writeLong(publishDate);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long plid;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long parentPlid;
	public boolean privateLayout;
	public long layoutId;
	public long parentLayoutId;
	public long classNameId;
	public long classPK;
	public String name;
	public String title;
	public String description;
	public String keywords;
	public String robots;
	public String type;
	public String typeSettings;
	public boolean hidden;
	public boolean system;
	public String friendlyURL;
	public long iconImageId;
	public String themeId;
	public String colorSchemeId;
	public String styleBookEntryERC;
	public String css;
	public int priority;
	public long faviconFileEntryId;
	public long masterLayoutPlid;
	public String layoutPrototypeUuid;
	public boolean layoutPrototypeLinkEnabled;
	public String layoutSetPrototypeLayoutERC;
	public long publishDate;
	public long lastPublishDate;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}