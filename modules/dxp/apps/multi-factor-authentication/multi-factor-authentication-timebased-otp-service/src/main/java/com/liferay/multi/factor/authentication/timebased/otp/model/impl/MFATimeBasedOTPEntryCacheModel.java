/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.model.impl;

import com.liferay.multi.factor.authentication.timebased.otp.model.MFATimeBasedOTPEntry;
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
 * The cache model class for representing MFATimeBasedOTPEntry in entity cache.
 *
 * @author Arthur Chan
 * @generated
 */
public class MFATimeBasedOTPEntryCacheModel
	implements CacheModel<MFATimeBasedOTPEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MFATimeBasedOTPEntryCacheModel)) {
			return false;
		}

		MFATimeBasedOTPEntryCacheModel mfaTimeBasedOTPEntryCacheModel =
			(MFATimeBasedOTPEntryCacheModel)object;

		if ((mfaTimeBasedOTPEntryId ==
				mfaTimeBasedOTPEntryCacheModel.mfaTimeBasedOTPEntryId) &&
			(mvccVersion == mfaTimeBasedOTPEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, mfaTimeBasedOTPEntryId);

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
		StringBundler sb = new StringBundler(31);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", mfaTimeBasedOTPEntryId=");
		sb.append(mfaTimeBasedOTPEntryId);
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
		sb.append(", algorithm=");
		sb.append(algorithm);
		sb.append(", failedAttempts=");
		sb.append(failedAttempts);
		sb.append(", lastFailDate=");
		sb.append(lastFailDate);
		sb.append(", lastFailIP=");
		sb.append(lastFailIP);
		sb.append(", lastSuccessDate=");
		sb.append(lastSuccessDate);
		sb.append(", lastSuccessIP=");
		sb.append(lastSuccessIP);
		sb.append(", lastValidTOTP=");
		sb.append(lastValidTOTP);
		sb.append(", sharedSecret=");
		sb.append(sharedSecret);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public MFATimeBasedOTPEntry toEntityModel() {
		MFATimeBasedOTPEntryImpl mfaTimeBasedOTPEntryImpl =
			new MFATimeBasedOTPEntryImpl();

		mfaTimeBasedOTPEntryImpl.setMvccVersion(mvccVersion);
		mfaTimeBasedOTPEntryImpl.setMfaTimeBasedOTPEntryId(
			mfaTimeBasedOTPEntryId);
		mfaTimeBasedOTPEntryImpl.setCompanyId(companyId);
		mfaTimeBasedOTPEntryImpl.setUserId(userId);

		if (userName == null) {
			mfaTimeBasedOTPEntryImpl.setUserName("");
		}
		else {
			mfaTimeBasedOTPEntryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			mfaTimeBasedOTPEntryImpl.setCreateDate(null);
		}
		else {
			mfaTimeBasedOTPEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			mfaTimeBasedOTPEntryImpl.setModifiedDate(null);
		}
		else {
			mfaTimeBasedOTPEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (algorithm == null) {
			mfaTimeBasedOTPEntryImpl.setAlgorithm("");
		}
		else {
			mfaTimeBasedOTPEntryImpl.setAlgorithm(algorithm);
		}

		mfaTimeBasedOTPEntryImpl.setFailedAttempts(failedAttempts);

		if (lastFailDate == Long.MIN_VALUE) {
			mfaTimeBasedOTPEntryImpl.setLastFailDate(null);
		}
		else {
			mfaTimeBasedOTPEntryImpl.setLastFailDate(new Date(lastFailDate));
		}

		if (lastFailIP == null) {
			mfaTimeBasedOTPEntryImpl.setLastFailIP("");
		}
		else {
			mfaTimeBasedOTPEntryImpl.setLastFailIP(lastFailIP);
		}

		if (lastSuccessDate == Long.MIN_VALUE) {
			mfaTimeBasedOTPEntryImpl.setLastSuccessDate(null);
		}
		else {
			mfaTimeBasedOTPEntryImpl.setLastSuccessDate(
				new Date(lastSuccessDate));
		}

		if (lastSuccessIP == null) {
			mfaTimeBasedOTPEntryImpl.setLastSuccessIP("");
		}
		else {
			mfaTimeBasedOTPEntryImpl.setLastSuccessIP(lastSuccessIP);
		}

		if (lastValidTOTP == null) {
			mfaTimeBasedOTPEntryImpl.setLastValidTOTP("");
		}
		else {
			mfaTimeBasedOTPEntryImpl.setLastValidTOTP(lastValidTOTP);
		}

		if (sharedSecret == null) {
			mfaTimeBasedOTPEntryImpl.setSharedSecret("");
		}
		else {
			mfaTimeBasedOTPEntryImpl.setSharedSecret(sharedSecret);
		}

		mfaTimeBasedOTPEntryImpl.resetOriginalValues();

		return mfaTimeBasedOTPEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		mfaTimeBasedOTPEntryId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		algorithm = objectInput.readUTF();

		failedAttempts = objectInput.readInt();
		lastFailDate = objectInput.readLong();
		lastFailIP = objectInput.readUTF();
		lastSuccessDate = objectInput.readLong();
		lastSuccessIP = objectInput.readUTF();
		lastValidTOTP = objectInput.readUTF();
		sharedSecret = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(mfaTimeBasedOTPEntryId);

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

		if (algorithm == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(algorithm);
		}

		objectOutput.writeInt(failedAttempts);
		objectOutput.writeLong(lastFailDate);

		if (lastFailIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastFailIP);
		}

		objectOutput.writeLong(lastSuccessDate);

		if (lastSuccessIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastSuccessIP);
		}

		if (lastValidTOTP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastValidTOTP);
		}

		if (sharedSecret == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sharedSecret);
		}
	}

	public long mvccVersion;
	public long mfaTimeBasedOTPEntryId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String algorithm;
	public int failedAttempts;
	public long lastFailDate;
	public String lastFailIP;
	public long lastSuccessDate;
	public String lastSuccessIP;
	public String lastValidTOTP;
	public String sharedSecret;

}
// LIFERAY-SERVICE-BUILDER-HASH:408977641