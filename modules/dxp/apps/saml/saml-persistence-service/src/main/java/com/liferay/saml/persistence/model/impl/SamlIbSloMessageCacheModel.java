/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.saml.persistence.model.SamlIbSloMessage;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing SamlIbSloMessage in entity cache.
 *
 * @author Mika Koivisto
 * @generated
 */
public class SamlIbSloMessageCacheModel
	implements CacheModel<SamlIbSloMessage>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlIbSloMessageCacheModel)) {
			return false;
		}

		SamlIbSloMessageCacheModel samlIbSloMessageCacheModel =
			(SamlIbSloMessageCacheModel)object;

		if (samlIbSloMessageId ==
				samlIbSloMessageCacheModel.samlIbSloMessageId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, samlIbSloMessageId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{samlIbSloMessageId=");
		sb.append(samlIbSloMessageId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", samlIdpEntityId=");
		sb.append(samlIdpEntityId);
		sb.append(", logoutRequestXml=");
		sb.append(logoutRequestXml);
		sb.append(", samlIdpSessionIndex=");
		sb.append(samlIdpSessionIndex);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SamlIbSloMessage toEntityModel() {
		SamlIbSloMessageImpl samlIbSloMessageImpl = new SamlIbSloMessageImpl();

		samlIbSloMessageImpl.setSamlIbSloMessageId(samlIbSloMessageId);
		samlIbSloMessageImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			samlIbSloMessageImpl.setCreateDate(null);
		}
		else {
			samlIbSloMessageImpl.setCreateDate(new Date(createDate));
		}

		if (samlIdpEntityId == null) {
			samlIbSloMessageImpl.setSamlIdpEntityId("");
		}
		else {
			samlIbSloMessageImpl.setSamlIdpEntityId(samlIdpEntityId);
		}

		if (logoutRequestXml == null) {
			samlIbSloMessageImpl.setLogoutRequestXml("");
		}
		else {
			samlIbSloMessageImpl.setLogoutRequestXml(logoutRequestXml);
		}

		if (samlIdpSessionIndex == null) {
			samlIbSloMessageImpl.setSamlIdpSessionIndex("");
		}
		else {
			samlIbSloMessageImpl.setSamlIdpSessionIndex(samlIdpSessionIndex);
		}

		samlIbSloMessageImpl.resetOriginalValues();

		return samlIbSloMessageImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		samlIbSloMessageId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		samlIdpEntityId = objectInput.readUTF();
		logoutRequestXml = (String)objectInput.readObject();
		samlIdpSessionIndex = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(samlIbSloMessageId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);

		if (samlIdpEntityId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlIdpEntityId);
		}

		if (logoutRequestXml == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(logoutRequestXml);
		}

		if (samlIdpSessionIndex == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(samlIdpSessionIndex);
		}
	}

	public long samlIbSloMessageId;
	public long companyId;
	public long createDate;
	public String samlIdpEntityId;
	public String logoutRequestXml;
	public String samlIdpSessionIndex;

}