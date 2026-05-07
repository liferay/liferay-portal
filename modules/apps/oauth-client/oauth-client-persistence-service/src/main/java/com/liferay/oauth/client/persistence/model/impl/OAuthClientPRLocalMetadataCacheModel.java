/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model.impl;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
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
 * The cache model class for representing OAuthClientPRLocalMetadata in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class OAuthClientPRLocalMetadataCacheModel
	implements CacheModel<OAuthClientPRLocalMetadata>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientPRLocalMetadataCacheModel)) {
			return false;
		}

		OAuthClientPRLocalMetadataCacheModel
			oAuthClientPRLocalMetadataCacheModel =
				(OAuthClientPRLocalMetadataCacheModel)object;

		if ((oAuthClientPRLocalMetadataId ==
				oAuthClientPRLocalMetadataCacheModel.
					oAuthClientPRLocalMetadataId) &&
			(mvccVersion == oAuthClientPRLocalMetadataCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, oAuthClientPRLocalMetadataId);

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
		StringBundler sb = new StringBundler(27);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", oAuthClientPRLocalMetadataId=");
		sb.append(oAuthClientPRLocalMetadataId);
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
		sb.append(", localWellKnownEnabled=");
		sb.append(localWellKnownEnabled);
		sb.append(", localWellKnownURI=");
		sb.append(localWellKnownURI);
		sb.append(", metadataJSON=");
		sb.append(metadataJSON);
		sb.append(", resource=");
		sb.append(resource);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OAuthClientPRLocalMetadata toEntityModel() {
		OAuthClientPRLocalMetadataImpl oAuthClientPRLocalMetadataImpl =
			new OAuthClientPRLocalMetadataImpl();

		oAuthClientPRLocalMetadataImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			oAuthClientPRLocalMetadataImpl.setUuid("");
		}
		else {
			oAuthClientPRLocalMetadataImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			oAuthClientPRLocalMetadataImpl.setExternalReferenceCode("");
		}
		else {
			oAuthClientPRLocalMetadataImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		oAuthClientPRLocalMetadataImpl.setOAuthClientPRLocalMetadataId(
			oAuthClientPRLocalMetadataId);
		oAuthClientPRLocalMetadataImpl.setCompanyId(companyId);
		oAuthClientPRLocalMetadataImpl.setUserId(userId);

		if (userName == null) {
			oAuthClientPRLocalMetadataImpl.setUserName("");
		}
		else {
			oAuthClientPRLocalMetadataImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			oAuthClientPRLocalMetadataImpl.setCreateDate(null);
		}
		else {
			oAuthClientPRLocalMetadataImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			oAuthClientPRLocalMetadataImpl.setModifiedDate(null);
		}
		else {
			oAuthClientPRLocalMetadataImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		oAuthClientPRLocalMetadataImpl.setLocalWellKnownEnabled(
			localWellKnownEnabled);

		if (localWellKnownURI == null) {
			oAuthClientPRLocalMetadataImpl.setLocalWellKnownURI("");
		}
		else {
			oAuthClientPRLocalMetadataImpl.setLocalWellKnownURI(
				localWellKnownURI);
		}

		if (metadataJSON == null) {
			oAuthClientPRLocalMetadataImpl.setMetadataJSON("");
		}
		else {
			oAuthClientPRLocalMetadataImpl.setMetadataJSON(metadataJSON);
		}

		if (resource == null) {
			oAuthClientPRLocalMetadataImpl.setResource("");
		}
		else {
			oAuthClientPRLocalMetadataImpl.setResource(resource);
		}

		oAuthClientPRLocalMetadataImpl.resetOriginalValues();

		return oAuthClientPRLocalMetadataImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		oAuthClientPRLocalMetadataId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		localWellKnownEnabled = objectInput.readBoolean();
		localWellKnownURI = objectInput.readUTF();
		metadataJSON = (String)objectInput.readObject();
		resource = objectInput.readUTF();
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

		objectOutput.writeLong(oAuthClientPRLocalMetadataId);

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

		objectOutput.writeBoolean(localWellKnownEnabled);

		if (localWellKnownURI == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(localWellKnownURI);
		}

		if (metadataJSON == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(metadataJSON);
		}

		if (resource == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(resource);
		}
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long oAuthClientPRLocalMetadataId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public boolean localWellKnownEnabled;
	public String localWellKnownURI;
	public String metadataJSON;
	public String resource;

}
// LIFERAY-SERVICE-BUILDER-HASH:-271475707