/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.saml.persistence.model.SamlSpMessage;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SamlSpMessage in entity cache.
 *
 * @author Mika Koivisto
 * @generated
 */
public class SamlSpMessageCacheModel
	implements CacheModel<SamlSpMessage>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlSpMessageCacheModel)) {
			return false;
		}

		SamlSpMessageCacheModel samlSpMessageCacheModel =
			(SamlSpMessageCacheModel)object;

		if (samlSpMessageId == samlSpMessageCacheModel.samlSpMessageId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, samlSpMessageId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{samlSpMessageId=");
		sb.append(samlSpMessageId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", samlIdpEntityId=");
		sb.append(samlIdpEntityId);
		sb.append(", expirationDate=");
		sb.append(expirationDate);
		sb.append(", samlIdpResponseKey=");
		sb.append(samlIdpResponseKey);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SamlSpMessage toEntityModel() {
		SamlSpMessageImpl samlSpMessageImpl = new SamlSpMessageImpl();

		samlSpMessageImpl.setSamlSpMessageId(samlSpMessageId);
		samlSpMessageImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			samlSpMessageImpl.setCreateDate(null);
		}
		else {
			samlSpMessageImpl.setCreateDate(new Date(createDate));
		}

		if (samlIdpEntityId == null) {
			samlSpMessageImpl.setSamlIdpEntityId("");
		}
		else {
			samlSpMessageImpl.setSamlIdpEntityId(samlIdpEntityId);
		}

		if (expirationDate == Long.MIN_VALUE) {
			samlSpMessageImpl.setExpirationDate(null);
		}
		else {
			samlSpMessageImpl.setExpirationDate(new Date(expirationDate));
		}

		if (samlIdpResponseKey == null) {
			samlSpMessageImpl.setSamlIdpResponseKey("");
		}
		else {
			samlSpMessageImpl.setSamlIdpResponseKey(samlIdpResponseKey);
		}

		samlSpMessageImpl.resetOriginalValues();

		return samlSpMessageImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		samlSpMessageId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		samlIdpEntityId = objectInput.readUTF();
		expirationDate = objectInput.readLong();
		samlIdpResponseKey = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(samlSpMessageId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);

		if (samlIdpEntityId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlIdpEntityId);
		}

		objectOutput.writeLong(expirationDate);

		if (samlIdpResponseKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlIdpResponseKey);
		}
	}

	public long samlSpMessageId;
	public long companyId;
	public long createDate;
	public String samlIdpEntityId;
	public long expirationDate;
	public String samlIdpResponseKey;

}