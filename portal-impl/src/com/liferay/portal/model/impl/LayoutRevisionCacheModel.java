/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing LayoutRevision in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutRevisionCacheModel
	implements CacheModel<LayoutRevision>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutRevisionCacheModel)) {
			return false;
		}

		LayoutRevisionCacheModel layoutRevisionCacheModel =
			(LayoutRevisionCacheModel)object;

		if ((layoutRevisionId == layoutRevisionCacheModel.layoutRevisionId) &&
			(mvccVersion == layoutRevisionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, layoutRevisionId);

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
		sb.append(", layoutRevisionId=");
		sb.append(layoutRevisionId);
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
		sb.append(", layoutSetBranchId=");
		sb.append(layoutSetBranchId);
		sb.append(", layoutBranchId=");
		sb.append(layoutBranchId);
		sb.append(", parentLayoutRevisionId=");
		sb.append(parentLayoutRevisionId);
		sb.append(", head=");
		sb.append(head);
		sb.append(", major=");
		sb.append(major);
		sb.append(", plid=");
		sb.append(plid);
		sb.append(", privateLayout=");
		sb.append(privateLayout);
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
		sb.append(", typeSettings=");
		sb.append(typeSettings);
		sb.append(", iconImageId=");
		sb.append(iconImageId);
		sb.append(", themeId=");
		sb.append(themeId);
		sb.append(", colorSchemeId=");
		sb.append(colorSchemeId);
		sb.append(", css=");
		sb.append(css);
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
	public LayoutRevision toEntityModel() {
		LayoutRevisionImpl layoutRevisionImpl = new LayoutRevisionImpl();

		layoutRevisionImpl.setMvccVersion(mvccVersion);
		layoutRevisionImpl.setLayoutRevisionId(layoutRevisionId);
		layoutRevisionImpl.setGroupId(groupId);
		layoutRevisionImpl.setCompanyId(companyId);
		layoutRevisionImpl.setUserId(userId);

		if (userName == null) {
			layoutRevisionImpl.setUserName("");
		}
		else {
			layoutRevisionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			layoutRevisionImpl.setCreateDate(null);
		}
		else {
			layoutRevisionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutRevisionImpl.setModifiedDate(null);
		}
		else {
			layoutRevisionImpl.setModifiedDate(new Date(modifiedDate));
		}

		layoutRevisionImpl.setLayoutSetBranchId(layoutSetBranchId);
		layoutRevisionImpl.setLayoutBranchId(layoutBranchId);
		layoutRevisionImpl.setParentLayoutRevisionId(parentLayoutRevisionId);
		layoutRevisionImpl.setHead(head);
		layoutRevisionImpl.setMajor(major);
		layoutRevisionImpl.setPlid(plid);
		layoutRevisionImpl.setPrivateLayout(privateLayout);

		if (name == null) {
			layoutRevisionImpl.setName("");
		}
		else {
			layoutRevisionImpl.setName(name);
		}

		if (title == null) {
			layoutRevisionImpl.setTitle("");
		}
		else {
			layoutRevisionImpl.setTitle(title);
		}

		if (description == null) {
			layoutRevisionImpl.setDescription("");
		}
		else {
			layoutRevisionImpl.setDescription(description);
		}

		if (keywords == null) {
			layoutRevisionImpl.setKeywords("");
		}
		else {
			layoutRevisionImpl.setKeywords(keywords);
		}

		if (robots == null) {
			layoutRevisionImpl.setRobots("");
		}
		else {
			layoutRevisionImpl.setRobots(robots);
		}

		if (typeSettings == null) {
			layoutRevisionImpl.setTypeSettings("");
		}
		else {
			layoutRevisionImpl.setTypeSettings(typeSettings);
		}

		layoutRevisionImpl.setIconImageId(iconImageId);

		if (themeId == null) {
			layoutRevisionImpl.setThemeId("");
		}
		else {
			layoutRevisionImpl.setThemeId(themeId);
		}

		if (colorSchemeId == null) {
			layoutRevisionImpl.setColorSchemeId("");
		}
		else {
			layoutRevisionImpl.setColorSchemeId(colorSchemeId);
		}

		if (css == null) {
			layoutRevisionImpl.setCss("");
		}
		else {
			layoutRevisionImpl.setCss(css);
		}

		layoutRevisionImpl.setStatus(status);
		layoutRevisionImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			layoutRevisionImpl.setStatusByUserName("");
		}
		else {
			layoutRevisionImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			layoutRevisionImpl.setStatusDate(null);
		}
		else {
			layoutRevisionImpl.setStatusDate(new Date(statusDate));
		}

		layoutRevisionImpl.resetOriginalValues();

		return layoutRevisionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		layoutRevisionId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		layoutSetBranchId = objectInput.readLong();

		layoutBranchId = objectInput.readLong();

		parentLayoutRevisionId = objectInput.readLong();

		head = objectInput.readBoolean();

		major = objectInput.readBoolean();

		plid = objectInput.readLong();

		privateLayout = objectInput.readBoolean();
		name = objectInput.readUTF();
		title = objectInput.readUTF();
		description = objectInput.readUTF();
		keywords = objectInput.readUTF();
		robots = objectInput.readUTF();
		typeSettings = (String)objectInput.readObject();

		iconImageId = objectInput.readLong();
		themeId = objectInput.readUTF();
		colorSchemeId = objectInput.readUTF();
		css = (String)objectInput.readObject();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(layoutRevisionId);

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

		objectOutput.writeLong(layoutSetBranchId);

		objectOutput.writeLong(layoutBranchId);

		objectOutput.writeLong(parentLayoutRevisionId);

		objectOutput.writeBoolean(head);

		objectOutput.writeBoolean(major);

		objectOutput.writeLong(plid);

		objectOutput.writeBoolean(privateLayout);

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

		if (typeSettings == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(typeSettings);
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

		if (css == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(css);
		}

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
	public long layoutRevisionId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long layoutSetBranchId;
	public long layoutBranchId;
	public long parentLayoutRevisionId;
	public boolean head;
	public boolean major;
	public long plid;
	public boolean privateLayout;
	public String name;
	public String title;
	public String description;
	public String keywords;
	public String robots;
	public String typeSettings;
	public long iconImageId;
	public String themeId;
	public String colorSchemeId;
	public String css;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}
// LIFERAY-SERVICE-BUILDER-HASH:1040580446