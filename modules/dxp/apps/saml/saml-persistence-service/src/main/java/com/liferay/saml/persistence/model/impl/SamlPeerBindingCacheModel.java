/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.saml.persistence.model.SamlPeerBinding;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SamlPeerBinding in entity cache.
 *
 * @author Mika Koivisto
 * @generated
 */
public class SamlPeerBindingCacheModel
	implements CacheModel<SamlPeerBinding>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlPeerBindingCacheModel)) {
			return false;
		}

		SamlPeerBindingCacheModel samlPeerBindingCacheModel =
			(SamlPeerBindingCacheModel)object;

		if (samlPeerBindingId == samlPeerBindingCacheModel.samlPeerBindingId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, samlPeerBindingId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(25);

		sb.append("{samlPeerBindingId=");
		sb.append(samlPeerBindingId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", samlPeerEntityId=");
		sb.append(samlPeerEntityId);
		sb.append(", deleted=");
		sb.append(deleted);
		sb.append(", samlNameIdFormat=");
		sb.append(samlNameIdFormat);
		sb.append(", samlNameIdNameQualifier=");
		sb.append(samlNameIdNameQualifier);
		sb.append(", samlNameIdSpNameQualifier=");
		sb.append(samlNameIdSpNameQualifier);
		sb.append(", samlNameIdSpProvidedId=");
		sb.append(samlNameIdSpProvidedId);
		sb.append(", samlNameIdValue=");
		sb.append(samlNameIdValue);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SamlPeerBinding toEntityModel() {
		SamlPeerBindingImpl samlPeerBindingImpl = new SamlPeerBindingImpl();

		samlPeerBindingImpl.setSamlPeerBindingId(samlPeerBindingId);
		samlPeerBindingImpl.setCompanyId(companyId);
		samlPeerBindingImpl.setUserId(userId);

		if (userName == null) {
			samlPeerBindingImpl.setUserName("");
		}
		else {
			samlPeerBindingImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			samlPeerBindingImpl.setCreateDate(null);
		}
		else {
			samlPeerBindingImpl.setCreateDate(new Date(createDate));
		}

		if (samlPeerEntityId == null) {
			samlPeerBindingImpl.setSamlPeerEntityId("");
		}
		else {
			samlPeerBindingImpl.setSamlPeerEntityId(samlPeerEntityId);
		}

		samlPeerBindingImpl.setDeleted(deleted);

		if (samlNameIdFormat == null) {
			samlPeerBindingImpl.setSamlNameIdFormat("");
		}
		else {
			samlPeerBindingImpl.setSamlNameIdFormat(samlNameIdFormat);
		}

		if (samlNameIdNameQualifier == null) {
			samlPeerBindingImpl.setSamlNameIdNameQualifier("");
		}
		else {
			samlPeerBindingImpl.setSamlNameIdNameQualifier(
				samlNameIdNameQualifier);
		}

		if (samlNameIdSpNameQualifier == null) {
			samlPeerBindingImpl.setSamlNameIdSpNameQualifier("");
		}
		else {
			samlPeerBindingImpl.setSamlNameIdSpNameQualifier(
				samlNameIdSpNameQualifier);
		}

		if (samlNameIdSpProvidedId == null) {
			samlPeerBindingImpl.setSamlNameIdSpProvidedId("");
		}
		else {
			samlPeerBindingImpl.setSamlNameIdSpProvidedId(
				samlNameIdSpProvidedId);
		}

		if (samlNameIdValue == null) {
			samlPeerBindingImpl.setSamlNameIdValue("");
		}
		else {
			samlPeerBindingImpl.setSamlNameIdValue(samlNameIdValue);
		}

		samlPeerBindingImpl.resetOriginalValues();

		return samlPeerBindingImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		samlPeerBindingId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		samlPeerEntityId = objectInput.readUTF();

		deleted = objectInput.readBoolean();
		samlNameIdFormat = objectInput.readUTF();
		samlNameIdNameQualifier = objectInput.readUTF();
		samlNameIdSpNameQualifier = objectInput.readUTF();
		samlNameIdSpProvidedId = objectInput.readUTF();
		samlNameIdValue = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(samlPeerBindingId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);

		if (samlPeerEntityId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlPeerEntityId);
		}

		objectOutput.writeBoolean(deleted);

		if (samlNameIdFormat == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlNameIdFormat);
		}

		if (samlNameIdNameQualifier == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlNameIdNameQualifier);
		}

		if (samlNameIdSpNameQualifier == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlNameIdSpNameQualifier);
		}

		if (samlNameIdSpProvidedId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlNameIdSpProvidedId);
		}

		if (samlNameIdValue == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlNameIdValue);
		}
	}

	public long samlPeerBindingId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public String samlPeerEntityId;
	public boolean deleted;
	public String samlNameIdFormat;
	public String samlNameIdNameQualifier;
	public String samlNameIdSpNameQualifier;
	public String samlNameIdSpProvidedId;
	public String samlNameIdValue;

}