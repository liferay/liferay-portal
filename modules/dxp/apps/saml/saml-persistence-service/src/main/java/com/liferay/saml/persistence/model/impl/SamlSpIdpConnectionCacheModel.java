/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SamlSpIdpConnection in entity cache.
 *
 * @author Mika Koivisto
 * @generated
 */
public class SamlSpIdpConnectionCacheModel
	implements CacheModel<SamlSpIdpConnection>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlSpIdpConnectionCacheModel)) {
			return false;
		}

		SamlSpIdpConnectionCacheModel samlSpIdpConnectionCacheModel =
			(SamlSpIdpConnectionCacheModel)object;

		if (samlSpIdpConnectionId ==
				samlSpIdpConnectionCacheModel.samlSpIdpConnectionId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, samlSpIdpConnectionId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(43);

		sb.append("{samlSpIdpConnectionId=");
		sb.append(samlSpIdpConnectionId);
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
		sb.append(", samlIdpEntityId=");
		sb.append(samlIdpEntityId);
		sb.append(", assertionSignatureRequired=");
		sb.append(assertionSignatureRequired);
		sb.append(", clockSkew=");
		sb.append(clockSkew);
		sb.append(", enabled=");
		sb.append(enabled);
		sb.append(", forceAuthn=");
		sb.append(forceAuthn);
		sb.append(", ldapImportEnabled=");
		sb.append(ldapImportEnabled);
		sb.append(", metadataUpdatedDate=");
		sb.append(metadataUpdatedDate);
		sb.append(", metadataUrl=");
		sb.append(metadataUrl);
		sb.append(", metadataXml=");
		sb.append(metadataXml);
		sb.append(", name=");
		sb.append(name);
		sb.append(", nameIdFormat=");
		sb.append(nameIdFormat);
		sb.append(", signAuthnRequest=");
		sb.append(signAuthnRequest);
		sb.append(", unknownUsersAreStrangers=");
		sb.append(unknownUsersAreStrangers);
		sb.append(", userAttributeMappings=");
		sb.append(userAttributeMappings);
		sb.append(", userIdentifierExpression=");
		sb.append(userIdentifierExpression);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SamlSpIdpConnection toEntityModel() {
		SamlSpIdpConnectionImpl samlSpIdpConnectionImpl =
			new SamlSpIdpConnectionImpl();

		samlSpIdpConnectionImpl.setSamlSpIdpConnectionId(samlSpIdpConnectionId);
		samlSpIdpConnectionImpl.setCompanyId(companyId);
		samlSpIdpConnectionImpl.setUserId(userId);

		if (userName == null) {
			samlSpIdpConnectionImpl.setUserName("");
		}
		else {
			samlSpIdpConnectionImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			samlSpIdpConnectionImpl.setCreateDate(null);
		}
		else {
			samlSpIdpConnectionImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			samlSpIdpConnectionImpl.setModifiedDate(null);
		}
		else {
			samlSpIdpConnectionImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (samlIdpEntityId == null) {
			samlSpIdpConnectionImpl.setSamlIdpEntityId("");
		}
		else {
			samlSpIdpConnectionImpl.setSamlIdpEntityId(samlIdpEntityId);
		}

		samlSpIdpConnectionImpl.setAssertionSignatureRequired(
			assertionSignatureRequired);
		samlSpIdpConnectionImpl.setClockSkew(clockSkew);
		samlSpIdpConnectionImpl.setEnabled(enabled);
		samlSpIdpConnectionImpl.setForceAuthn(forceAuthn);
		samlSpIdpConnectionImpl.setLdapImportEnabled(ldapImportEnabled);

		if (metadataUpdatedDate == Long.MIN_VALUE) {
			samlSpIdpConnectionImpl.setMetadataUpdatedDate(null);
		}
		else {
			samlSpIdpConnectionImpl.setMetadataUpdatedDate(
				new Date(metadataUpdatedDate));
		}

		if (metadataUrl == null) {
			samlSpIdpConnectionImpl.setMetadataUrl("");
		}
		else {
			samlSpIdpConnectionImpl.setMetadataUrl(metadataUrl);
		}

		if (metadataXml == null) {
			samlSpIdpConnectionImpl.setMetadataXml("");
		}
		else {
			samlSpIdpConnectionImpl.setMetadataXml(metadataXml);
		}

		if (name == null) {
			samlSpIdpConnectionImpl.setName("");
		}
		else {
			samlSpIdpConnectionImpl.setName(name);
		}

		if (nameIdFormat == null) {
			samlSpIdpConnectionImpl.setNameIdFormat("");
		}
		else {
			samlSpIdpConnectionImpl.setNameIdFormat(nameIdFormat);
		}

		samlSpIdpConnectionImpl.setSignAuthnRequest(signAuthnRequest);
		samlSpIdpConnectionImpl.setUnknownUsersAreStrangers(
			unknownUsersAreStrangers);

		if (userAttributeMappings == null) {
			samlSpIdpConnectionImpl.setUserAttributeMappings("");
		}
		else {
			samlSpIdpConnectionImpl.setUserAttributeMappings(
				userAttributeMappings);
		}

		if (userIdentifierExpression == null) {
			samlSpIdpConnectionImpl.setUserIdentifierExpression("");
		}
		else {
			samlSpIdpConnectionImpl.setUserIdentifierExpression(
				userIdentifierExpression);
		}

		samlSpIdpConnectionImpl.resetOriginalValues();

		return samlSpIdpConnectionImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		samlSpIdpConnectionId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		samlIdpEntityId = objectInput.readUTF();

		assertionSignatureRequired = objectInput.readBoolean();

		clockSkew = objectInput.readLong();

		enabled = objectInput.readBoolean();

		forceAuthn = objectInput.readBoolean();

		ldapImportEnabled = objectInput.readBoolean();
		metadataUpdatedDate = objectInput.readLong();
		metadataUrl = objectInput.readUTF();
		metadataXml = (String)objectInput.readObject();
		name = objectInput.readUTF();
		nameIdFormat = objectInput.readUTF();

		signAuthnRequest = objectInput.readBoolean();

		unknownUsersAreStrangers = objectInput.readBoolean();
		userAttributeMappings = objectInput.readUTF();
		userIdentifierExpression = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(samlSpIdpConnectionId);

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

		if (samlIdpEntityId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlIdpEntityId);
		}

		objectOutput.writeBoolean(assertionSignatureRequired);

		objectOutput.writeLong(clockSkew);

		objectOutput.writeBoolean(enabled);

		objectOutput.writeBoolean(forceAuthn);

		objectOutput.writeBoolean(ldapImportEnabled);
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

		if (nameIdFormat == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(nameIdFormat);
		}

		objectOutput.writeBoolean(signAuthnRequest);

		objectOutput.writeBoolean(unknownUsersAreStrangers);

		if (userAttributeMappings == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userAttributeMappings);
		}

		if (userIdentifierExpression == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userIdentifierExpression);
		}
	}

	public long samlSpIdpConnectionId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String samlIdpEntityId;
	public boolean assertionSignatureRequired;
	public long clockSkew;
	public boolean enabled;
	public boolean forceAuthn;
	public boolean ldapImportEnabled;
	public long metadataUpdatedDate;
	public String metadataUrl;
	public String metadataXml;
	public String name;
	public String nameIdFormat;
	public boolean signAuthnRequest;
	public boolean unknownUsersAreStrangers;
	public String userAttributeMappings;
	public String userIdentifierExpression;

}