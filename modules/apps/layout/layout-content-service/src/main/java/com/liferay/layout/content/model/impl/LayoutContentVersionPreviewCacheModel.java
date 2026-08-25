/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.model.impl;

import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing LayoutContentVersionPreview in entity cache.
 *
 * @author Lourdes Fernández Besada
 * @generated
 */
public class LayoutContentVersionPreviewCacheModel
	implements CacheModel<LayoutContentVersionPreview>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutContentVersionPreviewCacheModel)) {
			return false;
		}

		LayoutContentVersionPreviewCacheModel
			layoutContentVersionPreviewCacheModel =
				(LayoutContentVersionPreviewCacheModel)object;

		if ((layoutContentVersionPreviewId ==
				layoutContentVersionPreviewCacheModel.
					layoutContentVersionPreviewId) &&
			(mvccVersion ==
				layoutContentVersionPreviewCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, layoutContentVersionPreviewId);

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
		StringBundler sb = new StringBundler(25);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", layoutContentVersionPreviewId=");
		sb.append(layoutContentVersionPreviewId);
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
		sb.append(", layoutContentVersionId=");
		sb.append(layoutContentVersionId);
		sb.append(", html=");
		sb.append(html);
		sb.append(", languageId=");
		sb.append(languageId);
		sb.append(", segmentsExperienceERC=");
		sb.append(segmentsExperienceERC);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public LayoutContentVersionPreview toEntityModel() {
		LayoutContentVersionPreviewImpl layoutContentVersionPreviewImpl =
			new LayoutContentVersionPreviewImpl();

		layoutContentVersionPreviewImpl.setMvccVersion(mvccVersion);
		layoutContentVersionPreviewImpl.setLayoutContentVersionPreviewId(
			layoutContentVersionPreviewId);
		layoutContentVersionPreviewImpl.setGroupId(groupId);
		layoutContentVersionPreviewImpl.setCompanyId(companyId);
		layoutContentVersionPreviewImpl.setUserId(userId);

		if (userName == null) {
			layoutContentVersionPreviewImpl.setUserName("");
		}
		else {
			layoutContentVersionPreviewImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			layoutContentVersionPreviewImpl.setCreateDate(null);
		}
		else {
			layoutContentVersionPreviewImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutContentVersionPreviewImpl.setModifiedDate(null);
		}
		else {
			layoutContentVersionPreviewImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		layoutContentVersionPreviewImpl.setLayoutContentVersionId(
			layoutContentVersionId);

		if (html == null) {
			layoutContentVersionPreviewImpl.setHtml("");
		}
		else {
			layoutContentVersionPreviewImpl.setHtml(html);
		}

		if (languageId == null) {
			layoutContentVersionPreviewImpl.setLanguageId("");
		}
		else {
			layoutContentVersionPreviewImpl.setLanguageId(languageId);
		}

		if (segmentsExperienceERC == null) {
			layoutContentVersionPreviewImpl.setSegmentsExperienceERC("");
		}
		else {
			layoutContentVersionPreviewImpl.setSegmentsExperienceERC(
				segmentsExperienceERC);
		}

		layoutContentVersionPreviewImpl.resetOriginalValues();

		return layoutContentVersionPreviewImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		layoutContentVersionPreviewId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		layoutContentVersionId = objectInput.readLong();
		html = (String)objectInput.readObject();
		languageId = objectInput.readUTF();
		segmentsExperienceERC = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(layoutContentVersionPreviewId);

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

		objectOutput.writeLong(layoutContentVersionId);

		if (html == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(html);
		}

		if (languageId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(languageId);
		}

		if (segmentsExperienceERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(segmentsExperienceERC);
		}
	}

	public long mvccVersion;
	public long layoutContentVersionPreviewId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long layoutContentVersionId;
	public String html;
	public String languageId;
	public String segmentsExperienceERC;

}
// LIFERAY-SERVICE-BUILDER-HASH:1884126044