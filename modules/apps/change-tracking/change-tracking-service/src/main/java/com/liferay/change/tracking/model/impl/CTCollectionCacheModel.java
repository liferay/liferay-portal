/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.model.impl;

import com.liferay.change.tracking.model.CTCollection;
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
 * The cache model class for representing CTCollection in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CTCollectionCacheModel
	implements CacheModel<CTCollection>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CTCollectionCacheModel)) {
			return false;
		}

		CTCollectionCacheModel ctCollectionCacheModel =
			(CTCollectionCacheModel)object;

		if ((ctCollectionId == ctCollectionCacheModel.ctCollectionId) &&
			(mvccVersion == ctCollectionCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, ctCollectionId);

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
		StringBundler sb = new StringBundler(35);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", ctRemoteId=");
		sb.append(ctRemoteId);
		sb.append(", schemaVersionId=");
		sb.append(schemaVersionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append(", onDemandUserId=");
		sb.append(onDemandUserId);
		sb.append(", shareable=");
		sb.append(shareable);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CTCollection toEntityModel() {
		CTCollectionImpl ctCollectionImpl = new CTCollectionImpl();

		ctCollectionImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			ctCollectionImpl.setUuid("");
		}
		else {
			ctCollectionImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			ctCollectionImpl.setExternalReferenceCode("");
		}
		else {
			ctCollectionImpl.setExternalReferenceCode(externalReferenceCode);
		}

		ctCollectionImpl.setCtCollectionId(ctCollectionId);
		ctCollectionImpl.setCompanyId(companyId);
		ctCollectionImpl.setUserId(userId);

		if (createDate == Long.MIN_VALUE) {
			ctCollectionImpl.setCreateDate(null);
		}
		else {
			ctCollectionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			ctCollectionImpl.setModifiedDate(null);
		}
		else {
			ctCollectionImpl.setModifiedDate(new Date(modifiedDate));
		}

		ctCollectionImpl.setCtRemoteId(ctRemoteId);
		ctCollectionImpl.setSchemaVersionId(schemaVersionId);

		if (name == null) {
			ctCollectionImpl.setName("");
		}
		else {
			ctCollectionImpl.setName(name);
		}

		if (description == null) {
			ctCollectionImpl.setDescription("");
		}
		else {
			ctCollectionImpl.setDescription(description);
		}

		ctCollectionImpl.setOnDemandUserId(onDemandUserId);
		ctCollectionImpl.setShareable(shareable);
		ctCollectionImpl.setStatus(status);
		ctCollectionImpl.setStatusByUserId(statusByUserId);

		if (statusDate == Long.MIN_VALUE) {
			ctCollectionImpl.setStatusDate(null);
		}
		else {
			ctCollectionImpl.setStatusDate(new Date(statusDate));
		}

		ctCollectionImpl.resetOriginalValues();

		return ctCollectionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		ctCollectionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		ctRemoteId = objectInput.readLong();

		schemaVersionId = objectInput.readLong();
		name = objectInput.readUTF();
		description = objectInput.readUTF();

		onDemandUserId = objectInput.readLong();

		shareable = objectInput.readBoolean();

		status = objectInput.readInt();

		statusByUserId = objectInput.readLong();
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

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(ctRemoteId);

		objectOutput.writeLong(schemaVersionId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		objectOutput.writeLong(onDemandUserId);

		objectOutput.writeBoolean(shareable);

		objectOutput.writeInt(status);

		objectOutput.writeLong(statusByUserId);
		objectOutput.writeLong(statusDate);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long ctCollectionId;
	public long companyId;
	public long userId;
	public long createDate;
	public long modifiedDate;
	public long ctRemoteId;
	public long schemaVersionId;
	public String name;
	public String description;
	public long onDemandUserId;
	public boolean shareable;
	public int status;
	public long statusByUserId;
	public long statusDate;

}
// LIFERAY-SERVICE-BUILDER-HASH:956614182