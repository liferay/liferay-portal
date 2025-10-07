/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SamlIdpSpConnection in entity cache.
 *
 * @author Mika Koivisto
 * @generated
 */
public class SamlIdpSpConnectionCacheModel
	implements CacheModel<SamlIdpSpConnection>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlIdpSpConnectionCacheModel)) {
			return false;
		}

		SamlIdpSpConnectionCacheModel samlIdpSpConnectionCacheModel =
			(SamlIdpSpConnectionCacheModel)object;

		if (samlIdpSpConnectionId ==
				samlIdpSpConnectionCacheModel.samlIdpSpConnectionId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, samlIdpSpConnectionId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(39);

		sb.append("{samlIdpSpConnectionId=");
		sb.append(samlIdpSpConnectionId);
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
		sb.append(", samlSpEntityId=");
		sb.append(samlSpEntityId);
		sb.append(", assertionLifetime=");
		sb.append(assertionLifetime);
		sb.append(", attributeNames=");
		sb.append(attributeNames);
		sb.append(", attributesEnabled=");
		sb.append(attributesEnabled);
		sb.append(", attributesNamespaceEnabled=");
		sb.append(attributesNamespaceEnabled);
		sb.append(", enabled=");
		sb.append(enabled);
		sb.append(", encryptionForced=");
		sb.append(encryptionForced);
		sb.append(", metadataUpdatedDate=");
		sb.append(metadataUpdatedDate);
		sb.append(", metadataUrl=");
		sb.append(metadataUrl);
		sb.append(", metadataXml=");
		sb.append(metadataXml);
		sb.append(", name=");
		sb.append(name);
		sb.append(", nameIdAttribute=");
		sb.append(nameIdAttribute);
		sb.append(", nameIdFormat=");
		sb.append(nameIdFormat);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SamlIdpSpConnection toEntityModel() {
		SamlIdpSpConnectionImpl samlIdpSpConnectionImpl =
			new SamlIdpSpConnectionImpl();

		samlIdpSpConnectionImpl.setSamlIdpSpConnectionId(samlIdpSpConnectionId);
		samlIdpSpConnectionImpl.setCompanyId(companyId);
		samlIdpSpConnectionImpl.setUserId(userId);

		if (userName == null) {
			samlIdpSpConnectionImpl.setUserName("");
		}
		else {
			samlIdpSpConnectionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			samlIdpSpConnectionImpl.setCreateDate(null);
		}
		else {
			samlIdpSpConnectionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			samlIdpSpConnectionImpl.setModifiedDate(null);
		}
		else {
			samlIdpSpConnectionImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (samlSpEntityId == null) {
			samlIdpSpConnectionImpl.setSamlSpEntityId("");
		}
		else {
			samlIdpSpConnectionImpl.setSamlSpEntityId(samlSpEntityId);
		}

		samlIdpSpConnectionImpl.setAssertionLifetime(assertionLifetime);

		if (attributeNames == null) {
			samlIdpSpConnectionImpl.setAttributeNames("");
		}
		else {
			samlIdpSpConnectionImpl.setAttributeNames(attributeNames);
		}

		samlIdpSpConnectionImpl.setAttributesEnabled(attributesEnabled);
		samlIdpSpConnectionImpl.setAttributesNamespaceEnabled(
			attributesNamespaceEnabled);
		samlIdpSpConnectionImpl.setEnabled(enabled);
		samlIdpSpConnectionImpl.setEncryptionForced(encryptionForced);

		if (metadataUpdatedDate == Long.MIN_VALUE) {
			samlIdpSpConnectionImpl.setMetadataUpdatedDate(null);
		}
		else {
			samlIdpSpConnectionImpl.setMetadataUpdatedDate(
				new Date(metadataUpdatedDate));
		}

		if (metadataUrl == null) {
			samlIdpSpConnectionImpl.setMetadataUrl("");
		}
		else {
			samlIdpSpConnectionImpl.setMetadataUrl(metadataUrl);
		}

		if (metadataXml == null) {
			samlIdpSpConnectionImpl.setMetadataXml("");
		}
		else {
			samlIdpSpConnectionImpl.setMetadataXml(metadataXml);
		}

		if (name == null) {
			samlIdpSpConnectionImpl.setName("");
		}
		else {
			samlIdpSpConnectionImpl.setName(name);
		}

		if (nameIdAttribute == null) {
			samlIdpSpConnectionImpl.setNameIdAttribute("");
		}
		else {
			samlIdpSpConnectionImpl.setNameIdAttribute(nameIdAttribute);
		}

		if (nameIdFormat == null) {
			samlIdpSpConnectionImpl.setNameIdFormat("");
		}
		else {
			samlIdpSpConnectionImpl.setNameIdFormat(nameIdFormat);
		}

		samlIdpSpConnectionImpl.resetOriginalValues();

		return samlIdpSpConnectionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		samlIdpSpConnectionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		samlSpEntityId = objectInput.readUTF();

		assertionLifetime = objectInput.readInt();
		attributeNames = objectInput.readUTF();

		attributesEnabled = objectInput.readBoolean();

		attributesNamespaceEnabled = objectInput.readBoolean();

		enabled = objectInput.readBoolean();

		encryptionForced = objectInput.readBoolean();
		metadataUpdatedDate = objectInput.readLong();
		metadataUrl = objectInput.readUTF();
		metadataXml = (String)objectInput.readObject();
		name = objectInput.readUTF();
		nameIdAttribute = objectInput.readUTF();
		nameIdFormat = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(samlIdpSpConnectionId);

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

		if (samlSpEntityId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlSpEntityId);
		}

		objectOutput.writeInt(assertionLifetime);

		if (attributeNames == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(attributeNames);
		}

		objectOutput.writeBoolean(attributesEnabled);

		objectOutput.writeBoolean(attributesNamespaceEnabled);

		objectOutput.writeBoolean(enabled);

		objectOutput.writeBoolean(encryptionForced);
		objectOutput.writeLong(metadataUpdatedDate);

		if (metadataUrl == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(metadataUrl);
		}

		if (metadataXml == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(metadataXml);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (nameIdAttribute == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(nameIdAttribute);
		}

		if (nameIdFormat == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(nameIdFormat);
		}
	}

	public long samlIdpSpConnectionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String samlSpEntityId;
	public int assertionLifetime;
	public String attributeNames;
	public boolean attributesEnabled;
	public boolean attributesNamespaceEnabled;
	public boolean enabled;
	public boolean encryptionForced;
	public long metadataUpdatedDate;
	public String metadataUrl;
	public String metadataXml;
	public String name;
	public String nameIdAttribute;
	public String nameIdFormat;

}