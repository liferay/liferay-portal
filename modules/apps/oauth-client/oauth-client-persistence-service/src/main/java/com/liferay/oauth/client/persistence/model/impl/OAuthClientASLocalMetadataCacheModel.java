/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model.impl;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
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
 * The cache model class for representing OAuthClientASLocalMetadata in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class OAuthClientASLocalMetadataCacheModel
	implements CacheModel<OAuthClientASLocalMetadata>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OAuthClientASLocalMetadataCacheModel)) {
			return false;
		}

		OAuthClientASLocalMetadataCacheModel
			oAuthClientASLocalMetadataCacheModel =
				(OAuthClientASLocalMetadataCacheModel)object;

		if ((oAuthClientASLocalMetadataId ==
				oAuthClientASLocalMetadataCacheModel.
					oAuthClientASLocalMetadataId) &&
			(mvccVersion == oAuthClientASLocalMetadataCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, oAuthClientASLocalMetadataId);

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
		sb.append(", oAuthClientASLocalMetadataId=");
		sb.append(oAuthClientASLocalMetadataId);
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
		sb.append(", issuer=");
		sb.append(issuer);
		sb.append(", localWellKnownEnabled=");
		sb.append(localWellKnownEnabled);
		sb.append(", localWellKnownURIOAS=");
		sb.append(localWellKnownURIOAS);
		sb.append(", localWellKnownURIOIC=");
		sb.append(localWellKnownURIOIC);
		sb.append(", metadataJSONOAS=");
		sb.append(metadataJSONOAS);
		sb.append(", metadataJSONOIC=");
		sb.append(metadataJSONOIC);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OAuthClientASLocalMetadata toEntityModel() {
		OAuthClientASLocalMetadataImpl oAuthClientASLocalMetadataImpl =
			new OAuthClientASLocalMetadataImpl();

		oAuthClientASLocalMetadataImpl.setMvccVersion(mvccVersion);
		oAuthClientASLocalMetadataImpl.setOAuthClientASLocalMetadataId(
			oAuthClientASLocalMetadataId);
		oAuthClientASLocalMetadataImpl.setCompanyId(companyId);
		oAuthClientASLocalMetadataImpl.setUserId(userId);

		if (userName == null) {
			oAuthClientASLocalMetadataImpl.setUserName("");
		}
		else {
			oAuthClientASLocalMetadataImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			oAuthClientASLocalMetadataImpl.setCreateDate(null);
		}
		else {
			oAuthClientASLocalMetadataImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			oAuthClientASLocalMetadataImpl.setModifiedDate(null);
		}
		else {
			oAuthClientASLocalMetadataImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		if (issuer == null) {
			oAuthClientASLocalMetadataImpl.setIssuer("");
		}
		else {
			oAuthClientASLocalMetadataImpl.setIssuer(issuer);
		}

		oAuthClientASLocalMetadataImpl.setLocalWellKnownEnabled(
			localWellKnownEnabled);

		if (localWellKnownURIOAS == null) {
			oAuthClientASLocalMetadataImpl.setLocalWellKnownURIOAS("");
		}
		else {
			oAuthClientASLocalMetadataImpl.setLocalWellKnownURIOAS(
				localWellKnownURIOAS);
		}

		if (localWellKnownURIOIC == null) {
			oAuthClientASLocalMetadataImpl.setLocalWellKnownURIOIC("");
		}
		else {
			oAuthClientASLocalMetadataImpl.setLocalWellKnownURIOIC(
				localWellKnownURIOIC);
		}

		if (metadataJSONOAS == null) {
			oAuthClientASLocalMetadataImpl.setMetadataJSONOAS("");
		}
		else {
			oAuthClientASLocalMetadataImpl.setMetadataJSONOAS(metadataJSONOAS);
		}

		if (metadataJSONOIC == null) {
			oAuthClientASLocalMetadataImpl.setMetadataJSONOIC("");
		}
		else {
			oAuthClientASLocalMetadataImpl.setMetadataJSONOIC(metadataJSONOIC);
		}

		oAuthClientASLocalMetadataImpl.resetOriginalValues();

		return oAuthClientASLocalMetadataImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		oAuthClientASLocalMetadataId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		issuer = objectInput.readUTF();

		localWellKnownEnabled = objectInput.readBoolean();
		localWellKnownURIOAS = objectInput.readUTF();
		localWellKnownURIOIC = objectInput.readUTF();
		metadataJSONOAS = (String)objectInput.readObject();
		metadataJSONOIC = (String)objectInput.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(oAuthClientASLocalMetadataId);

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

		if (issuer == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(issuer);
		}

		objectOutput.writeBoolean(localWellKnownEnabled);

		if (localWellKnownURIOAS == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(localWellKnownURIOAS);
		}

		if (localWellKnownURIOIC == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(localWellKnownURIOIC);
		}

		if (metadataJSONOAS == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(metadataJSONOAS);
		}

		if (metadataJSONOIC == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(metadataJSONOIC);
		}
	}

	public long mvccVersion;
	public long oAuthClientASLocalMetadataId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String issuer;
	public boolean localWellKnownEnabled;
	public String localWellKnownURIOAS;
	public String localWellKnownURIOIC;
	public String metadataJSONOAS;
	public String metadataJSONOIC;

}