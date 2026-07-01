/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
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
 * The cache model class for representing LayoutPageTemplateStructureRelElementVariation in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationCacheModel
	implements CacheModel<LayoutPageTemplateStructureRelElementVariation>,
			   Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				LayoutPageTemplateStructureRelElementVariationCacheModel)) {

			return false;
		}

		LayoutPageTemplateStructureRelElementVariationCacheModel
			layoutPageTemplateStructureRelElementVariationCacheModel =
				(LayoutPageTemplateStructureRelElementVariationCacheModel)
					object;

		if ((layoutPageTemplateStructureRelElementVariationId ==
				layoutPageTemplateStructureRelElementVariationCacheModel.
					layoutPageTemplateStructureRelElementVariationId) &&
			(mvccVersion ==
				layoutPageTemplateStructureRelElementVariationCacheModel.
					mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(
			0, layoutPageTemplateStructureRelElementVariationId);

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
		sb.append(", layoutPageTemplateStructureRelElementVariationId=");
		sb.append(layoutPageTemplateStructureRelElementVariationId);
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
		sb.append(", hide=");
		sb.append(hide);
		sb.append(", html=");
		sb.append(html);
		sb.append(", js=");
		sb.append(js);
		sb.append(", name=");
		sb.append(name);
		sb.append(", plid=");
		sb.append(plid);
		sb.append(", segmentsExperienceERC=");
		sb.append(segmentsExperienceERC);
		sb.append(", targetElement=");
		sb.append(targetElement);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation toEntityModel() {
		LayoutPageTemplateStructureRelElementVariationImpl
			layoutPageTemplateStructureRelElementVariationImpl =
				new LayoutPageTemplateStructureRelElementVariationImpl();

		layoutPageTemplateStructureRelElementVariationImpl.setMvccVersion(
			mvccVersion);
		layoutPageTemplateStructureRelElementVariationImpl.setCtCollectionId(
			ctCollectionId);

		if (uuid == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setUuid("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			layoutPageTemplateStructureRelElementVariationImpl.
				setExternalReferenceCode("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.
				setExternalReferenceCode(externalReferenceCode);
		}

		layoutPageTemplateStructureRelElementVariationImpl.
			setLayoutPageTemplateStructureRelElementVariationId(
				layoutPageTemplateStructureRelElementVariationId);
		layoutPageTemplateStructureRelElementVariationImpl.setGroupId(groupId);
		layoutPageTemplateStructureRelElementVariationImpl.setCompanyId(
			companyId);
		layoutPageTemplateStructureRelElementVariationImpl.setUserId(userId);

		if (userName == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setUserName("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setUserName(
				userName);
		}

		if (createDate == Long.MIN_VALUE) {
			layoutPageTemplateStructureRelElementVariationImpl.setCreateDate(
				null);
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setCreateDate(
				new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			layoutPageTemplateStructureRelElementVariationImpl.setModifiedDate(
				null);
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		if (hide == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setHide("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setHide(hide);
		}

		if (html == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setHtml("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setHtml(html);
		}

		if (js == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setJs("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setJs(js);
		}

		if (name == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setName("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setName(name);
		}

		layoutPageTemplateStructureRelElementVariationImpl.setPlid(plid);

		if (segmentsExperienceERC == null) {
			layoutPageTemplateStructureRelElementVariationImpl.
				setSegmentsExperienceERC("");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.
				setSegmentsExperienceERC(segmentsExperienceERC);
		}

		if (targetElement == null) {
			layoutPageTemplateStructureRelElementVariationImpl.setTargetElement(
				"");
		}
		else {
			layoutPageTemplateStructureRelElementVariationImpl.setTargetElement(
				targetElement);
		}

		layoutPageTemplateStructureRelElementVariationImpl.
			resetOriginalValues();

		return layoutPageTemplateStructureRelElementVariationImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		layoutPageTemplateStructureRelElementVariationId =
			objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		hide = objectInput.readUTF();
		html = objectInput.readUTF();
		js = objectInput.readUTF();
		name = objectInput.readUTF();

		plid = objectInput.readLong();
		segmentsExperienceERC = objectInput.readUTF();
		targetElement = objectInput.readUTF();
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

		objectOutput.writeLong(
			layoutPageTemplateStructureRelElementVariationId);

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

		if (hide == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(hide);
		}

		if (html == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(html);
		}

		if (js == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(js);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeLong(plid);

		if (segmentsExperienceERC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(segmentsExperienceERC);
		}

		if (targetElement == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(targetElement);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long layoutPageTemplateStructureRelElementVariationId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String hide;
	public String html;
	public String js;
	public String name;
	public long plid;
	public String segmentsExperienceERC;
	public String targetElement;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2003123989