/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.search.experiences.model.SXPBlueprint;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SXPBlueprint in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SXPBlueprintCacheModel
	implements CacheModel<SXPBlueprint>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SXPBlueprintCacheModel)) {
			return false;
		}

		SXPBlueprintCacheModel sxpBlueprintCacheModel =
			(SXPBlueprintCacheModel)object;

		if ((sxpBlueprintId == sxpBlueprintCacheModel.sxpBlueprintId) &&
			(mvccVersion == sxpBlueprintCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, sxpBlueprintId);

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
		StringBundler sb = new StringBundler(47);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", sxpBlueprintId=");
		sb.append(sxpBlueprintId);
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
		sb.append(", collectionProvider=");
		sb.append(collectionProvider);
		sb.append(", compatibility=");
		sb.append(compatibility);
		sb.append(", configurationJSON=");
		sb.append(configurationJSON);
		sb.append(", description=");
		sb.append(description);
		sb.append(", elementInstancesJSON=");
		sb.append(elementInstancesJSON);
		sb.append(", fallbackDescription=");
		sb.append(fallbackDescription);
		sb.append(", fallbackTitle=");
		sb.append(fallbackTitle);
		sb.append(", schemaVersion=");
		sb.append(schemaVersion);
		sb.append(", title=");
		sb.append(title);
		sb.append(", version=");
		sb.append(version);
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
	public SXPBlueprint toEntityModel() {
		SXPBlueprintImpl sxpBlueprintImpl = new SXPBlueprintImpl();

		sxpBlueprintImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			sxpBlueprintImpl.setUuid("");
		}
		else {
			sxpBlueprintImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			sxpBlueprintImpl.setExternalReferenceCode("");
		}
		else {
			sxpBlueprintImpl.setExternalReferenceCode(externalReferenceCode);
		}

		sxpBlueprintImpl.setSXPBlueprintId(sxpBlueprintId);
		sxpBlueprintImpl.setCompanyId(companyId);
		sxpBlueprintImpl.setUserId(userId);

		if (userName == null) {
			sxpBlueprintImpl.setUserName("");
		}
		else {
			sxpBlueprintImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			sxpBlueprintImpl.setCreateDate(null);
		}
		else {
			sxpBlueprintImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			sxpBlueprintImpl.setModifiedDate(null);
		}
		else {
			sxpBlueprintImpl.setModifiedDate(new Date(modifiedDate));
		}

		sxpBlueprintImpl.setCollectionProvider(collectionProvider);
		sxpBlueprintImpl.setCompatibility(compatibility);

		if (configurationJSON == null) {
			sxpBlueprintImpl.setConfigurationJSON("");
		}
		else {
			sxpBlueprintImpl.setConfigurationJSON(configurationJSON);
		}

		if (description == null) {
			sxpBlueprintImpl.setDescription("");
		}
		else {
			sxpBlueprintImpl.setDescription(description);
		}

		if (elementInstancesJSON == null) {
			sxpBlueprintImpl.setElementInstancesJSON("");
		}
		else {
			sxpBlueprintImpl.setElementInstancesJSON(elementInstancesJSON);
		}

		if (fallbackDescription == null) {
			sxpBlueprintImpl.setFallbackDescription("");
		}
		else {
			sxpBlueprintImpl.setFallbackDescription(fallbackDescription);
		}

		if (fallbackTitle == null) {
			sxpBlueprintImpl.setFallbackTitle("");
		}
		else {
			sxpBlueprintImpl.setFallbackTitle(fallbackTitle);
		}

		if (schemaVersion == null) {
			sxpBlueprintImpl.setSchemaVersion("");
		}
		else {
			sxpBlueprintImpl.setSchemaVersion(schemaVersion);
		}

		if (title == null) {
			sxpBlueprintImpl.setTitle("");
		}
		else {
			sxpBlueprintImpl.setTitle(title);
		}

		if (version == null) {
			sxpBlueprintImpl.setVersion("");
		}
		else {
			sxpBlueprintImpl.setVersion(version);
		}

		sxpBlueprintImpl.setStatus(status);
		sxpBlueprintImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			sxpBlueprintImpl.setStatusByUserName("");
		}
		else {
			sxpBlueprintImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			sxpBlueprintImpl.setStatusDate(null);
		}
		else {
			sxpBlueprintImpl.setStatusDate(new Date(statusDate));
		}

		sxpBlueprintImpl.resetOriginalValues();

		return sxpBlueprintImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		sxpBlueprintId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		collectionProvider = objectInput.readBoolean();

		compatibility = objectInput.readInt();
		configurationJSON = (String)objectInput.readObject();
		description = objectInput.readUTF();
		elementInstancesJSON = (String)objectInput.readObject();
		fallbackDescription = objectInput.readUTF();
		fallbackTitle = objectInput.readUTF();
		schemaVersion = objectInput.readUTF();
		title = objectInput.readUTF();
		version = objectInput.readUTF();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

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

		objectOutput.writeLong(sxpBlueprintId);

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

		objectOutput.writeBoolean(collectionProvider);

		objectOutput.writeInt(compatibility);

		if (configurationJSON == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(configurationJSON);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		if (elementInstancesJSON == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(elementInstancesJSON);
		}

		if (fallbackDescription == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(fallbackDescription);
		}

		if (fallbackTitle == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(fallbackTitle);
		}

		if (schemaVersion == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(schemaVersion);
		}

		if (title == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(title);
		}

		if (version == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(version);
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
	public String uuid;
	public String externalReferenceCode;
	public long sxpBlueprintId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public boolean collectionProvider;
	public int compatibility;
	public String configurationJSON;
	public String description;
	public String elementInstancesJSON;
	public String fallbackDescription;
	public String fallbackTitle;
	public String schemaVersion;
	public String title;
	public String version;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;

}