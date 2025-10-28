/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing OpenIdConnectUser in entity cache.
 *
 * @author Arthur Chan
 * @generated
 */
public class OpenIdConnectUserCacheModel
	implements CacheModel<OpenIdConnectUser>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OpenIdConnectUserCacheModel)) {
			return false;
		}

		OpenIdConnectUserCacheModel openIdConnectUserCacheModel =
			(OpenIdConnectUserCacheModel)object;

		if ((openIdConnectUserId ==
				openIdConnectUserCacheModel.openIdConnectUserId) &&
			(mvccVersion == openIdConnectUserCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, openIdConnectUserId);

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
		StringBundler sb = new StringBundler(15);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", openIdConnectUserId=");
		sb.append(openIdConnectUserId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", issuer=");
		sb.append(issuer);
		sb.append(", subject=");
		sb.append(subject);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OpenIdConnectUser toEntityModel() {
		OpenIdConnectUserImpl openIdConnectUserImpl =
			new OpenIdConnectUserImpl();

		openIdConnectUserImpl.setMvccVersion(mvccVersion);
		openIdConnectUserImpl.setOpenIdConnectUserId(openIdConnectUserId);
		openIdConnectUserImpl.setCompanyId(companyId);
		openIdConnectUserImpl.setUserId(userId);

		if (createDate == Long.MIN_VALUE) {
			openIdConnectUserImpl.setCreateDate(null);
		}
		else {
			openIdConnectUserImpl.setCreateDate(new Date(createDate));
		}

		if (issuer == null) {
			openIdConnectUserImpl.setIssuer("");
		}
		else {
			openIdConnectUserImpl.setIssuer(issuer);
		}

		if (subject == null) {
			openIdConnectUserImpl.setSubject("");
		}
		else {
			openIdConnectUserImpl.setSubject(subject);
		}

		openIdConnectUserImpl.resetOriginalValues();

		return openIdConnectUserImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		openIdConnectUserId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		createDate = objectInput.readLong();
		issuer = objectInput.readUTF();
		subject = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(openIdConnectUserId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);
		objectOutput.writeLong(createDate);

		if (issuer == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(issuer);
		}

		if (subject == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(subject);
		}
	}

	public long mvccVersion;
	public long openIdConnectUserId;
	public long companyId;
	public long userId;
	public long createDate;
	public String issuer;
	public String subject;

}