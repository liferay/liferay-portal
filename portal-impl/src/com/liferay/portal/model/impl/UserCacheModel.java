/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.User;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.util.Date;

/**
 * The cache model class for representing User in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserCacheModel
	implements CacheModel<User>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserCacheModel)) {
			return false;
		}

		UserCacheModel userCacheModel = (UserCacheModel)object;

		if ((userId == userCacheModel.userId) &&
			(mvccVersion == userCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, userId);

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
		StringBundler sb = new StringBundler(89);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", contactId=");
		sb.append(contactId);
		sb.append(", password=");
		sb.append(password);
		sb.append(", passwordEncrypted=");
		sb.append(passwordEncrypted);
		sb.append(", passwordReset=");
		sb.append(passwordReset);
		sb.append(", passwordModifiedDate=");
		sb.append(passwordModifiedDate);
		sb.append(", digest=");
		sb.append(digest);
		sb.append(", reminderQueryQuestion=");
		sb.append(reminderQueryQuestion);
		sb.append(", reminderQueryAnswer=");
		sb.append(reminderQueryAnswer);
		sb.append(", graceLoginCount=");
		sb.append(graceLoginCount);
		sb.append(", screenName=");
		sb.append(screenName);
		sb.append(", emailAddress=");
		sb.append(emailAddress);
		sb.append(", facebookId=");
		sb.append(facebookId);
		sb.append(", googleUserId=");
		sb.append(googleUserId);
		sb.append(", ldapServerId=");
		sb.append(ldapServerId);
		sb.append(", openId=");
		sb.append(openId);
		sb.append(", portraitId=");
		sb.append(portraitId);
		sb.append(", languageId=");
		sb.append(languageId);
		sb.append(", timeZoneId=");
		sb.append(timeZoneId);
		sb.append(", greeting=");
		sb.append(greeting);
		sb.append(", comments=");
		sb.append(comments);
		sb.append(", firstName=");
		sb.append(firstName);
		sb.append(", middleName=");
		sb.append(middleName);
		sb.append(", lastName=");
		sb.append(lastName);
		sb.append(", jobTitle=");
		sb.append(jobTitle);
		sb.append(", loginDate=");
		sb.append(loginDate);
		sb.append(", loginIP=");
		sb.append(loginIP);
		sb.append(", lastLoginDate=");
		sb.append(lastLoginDate);
		sb.append(", lastLoginIP=");
		sb.append(lastLoginIP);
		sb.append(", lastFailedLoginDate=");
		sb.append(lastFailedLoginDate);
		sb.append(", failedLoginAttempts=");
		sb.append(failedLoginAttempts);
		sb.append(", lockout=");
		sb.append(lockout);
		sb.append(", lockoutDate=");
		sb.append(lockoutDate);
		sb.append(", agreedToTermsOfUse=");
		sb.append(agreedToTermsOfUse);
		sb.append(", emailAddressVerified=");
		sb.append(emailAddressVerified);
		sb.append(", type=");
		sb.append(type);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public User toEntityModel() {
		UserImpl userImpl = new UserImpl();

		userImpl.setMvccVersion(mvccVersion);
		userImpl.setCtCollectionId(ctCollectionId);

		if (uuid == null) {
			userImpl.setUuid("");
		}
		else {
			userImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			userImpl.setExternalReferenceCode("");
		}
		else {
			userImpl.setExternalReferenceCode(externalReferenceCode);
		}

		userImpl.setUserId(userId);
		userImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			userImpl.setCreateDate(null);
		}
		else {
			userImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			userImpl.setModifiedDate(null);
		}
		else {
			userImpl.setModifiedDate(new Date(modifiedDate));
		}

		userImpl.setContactId(contactId);

		if (password == null) {
			userImpl.setPassword("");
		}
		else {
			userImpl.setPassword(password);
		}

		userImpl.setPasswordEncrypted(passwordEncrypted);
		userImpl.setPasswordReset(passwordReset);

		if (passwordModifiedDate == Long.MIN_VALUE) {
			userImpl.setPasswordModifiedDate(null);
		}
		else {
			userImpl.setPasswordModifiedDate(new Date(passwordModifiedDate));
		}

		if (digest == null) {
			userImpl.setDigest("");
		}
		else {
			userImpl.setDigest(digest);
		}

		if (reminderQueryQuestion == null) {
			userImpl.setReminderQueryQuestion("");
		}
		else {
			userImpl.setReminderQueryQuestion(reminderQueryQuestion);
		}

		if (reminderQueryAnswer == null) {
			userImpl.setReminderQueryAnswer("");
		}
		else {
			userImpl.setReminderQueryAnswer(reminderQueryAnswer);
		}

		userImpl.setGraceLoginCount(graceLoginCount);

		if (screenName == null) {
			userImpl.setScreenName("");
		}
		else {
			userImpl.setScreenName(screenName);
		}

		if (emailAddress == null) {
			userImpl.setEmailAddress("");
		}
		else {
			userImpl.setEmailAddress(emailAddress);
		}

		userImpl.setFacebookId(facebookId);

		if (googleUserId == null) {
			userImpl.setGoogleUserId("");
		}
		else {
			userImpl.setGoogleUserId(googleUserId);
		}

		userImpl.setLdapServerId(ldapServerId);

		if (openId == null) {
			userImpl.setOpenId("");
		}
		else {
			userImpl.setOpenId(openId);
		}

		userImpl.setPortraitId(portraitId);

		if (languageId == null) {
			userImpl.setLanguageId("");
		}
		else {
			userImpl.setLanguageId(languageId);
		}

		if (timeZoneId == null) {
			userImpl.setTimeZoneId("");
		}
		else {
			userImpl.setTimeZoneId(timeZoneId);
		}

		if (greeting == null) {
			userImpl.setGreeting("");
		}
		else {
			userImpl.setGreeting(greeting);
		}

		if (comments == null) {
			userImpl.setComments("");
		}
		else {
			userImpl.setComments(comments);
		}

		if (firstName == null) {
			userImpl.setFirstName("");
		}
		else {
			userImpl.setFirstName(firstName);
		}

		if (middleName == null) {
			userImpl.setMiddleName("");
		}
		else {
			userImpl.setMiddleName(middleName);
		}

		if (lastName == null) {
			userImpl.setLastName("");
		}
		else {
			userImpl.setLastName(lastName);
		}

		if (jobTitle == null) {
			userImpl.setJobTitle("");
		}
		else {
			userImpl.setJobTitle(jobTitle);
		}

		if (loginDate == Long.MIN_VALUE) {
			userImpl.setLoginDate(null);
		}
		else {
			userImpl.setLoginDate(new Date(loginDate));
		}

		if (loginIP == null) {
			userImpl.setLoginIP("");
		}
		else {
			userImpl.setLoginIP(loginIP);
		}

		if (lastLoginDate == Long.MIN_VALUE) {
			userImpl.setLastLoginDate(null);
		}
		else {
			userImpl.setLastLoginDate(new Date(lastLoginDate));
		}

		if (lastLoginIP == null) {
			userImpl.setLastLoginIP("");
		}
		else {
			userImpl.setLastLoginIP(lastLoginIP);
		}

		if (lastFailedLoginDate == Long.MIN_VALUE) {
			userImpl.setLastFailedLoginDate(null);
		}
		else {
			userImpl.setLastFailedLoginDate(new Date(lastFailedLoginDate));
		}

		userImpl.setFailedLoginAttempts(failedLoginAttempts);
		userImpl.setLockout(lockout);

		if (lockoutDate == Long.MIN_VALUE) {
			userImpl.setLockoutDate(null);
		}
		else {
			userImpl.setLockoutDate(new Date(lockoutDate));
		}

		userImpl.setAgreedToTermsOfUse(agreedToTermsOfUse);
		userImpl.setEmailAddressVerified(emailAddressVerified);
		userImpl.setType(type);
		userImpl.setStatus(status);

		userImpl.resetOriginalValues();

		try {
			_groupIdMethodHandle.invokeExact(userImpl, groupId);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		return userImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		userId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		contactId = objectInput.readLong();
		password = objectInput.readUTF();

		passwordEncrypted = objectInput.readBoolean();

		passwordReset = objectInput.readBoolean();
		passwordModifiedDate = objectInput.readLong();
		digest = objectInput.readUTF();
		reminderQueryQuestion = objectInput.readUTF();
		reminderQueryAnswer = objectInput.readUTF();

		graceLoginCount = objectInput.readInt();
		screenName = objectInput.readUTF();
		emailAddress = objectInput.readUTF();

		facebookId = objectInput.readLong();
		googleUserId = objectInput.readUTF();

		ldapServerId = objectInput.readLong();
		openId = objectInput.readUTF();

		portraitId = objectInput.readLong();
		languageId = objectInput.readUTF();
		timeZoneId = objectInput.readUTF();
		greeting = objectInput.readUTF();
		comments = objectInput.readUTF();
		firstName = objectInput.readUTF();
		middleName = objectInput.readUTF();
		lastName = objectInput.readUTF();
		jobTitle = objectInput.readUTF();
		loginDate = objectInput.readLong();
		loginIP = objectInput.readUTF();
		lastLoginDate = objectInput.readLong();
		lastLoginIP = objectInput.readUTF();
		lastFailedLoginDate = objectInput.readLong();

		failedLoginAttempts = objectInput.readInt();

		lockout = objectInput.readBoolean();
		lockoutDate = objectInput.readLong();

		agreedToTermsOfUse = objectInput.readBoolean();

		emailAddressVerified = objectInput.readBoolean();

		type = objectInput.readInt();

		status = objectInput.readInt();

		groupId = (long)objectInput.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

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

		objectOutput.writeLong(userId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(contactId);

		if (password == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(password);
		}

		objectOutput.writeBoolean(passwordEncrypted);

		objectOutput.writeBoolean(passwordReset);
		objectOutput.writeLong(passwordModifiedDate);

		if (digest == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(digest);
		}

		if (reminderQueryQuestion == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(reminderQueryQuestion);
		}

		if (reminderQueryAnswer == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(reminderQueryAnswer);
		}

		objectOutput.writeInt(graceLoginCount);

		if (screenName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(screenName);
		}

		if (emailAddress == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(emailAddress);
		}

		objectOutput.writeLong(facebookId);

		if (googleUserId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(googleUserId);
		}

		objectOutput.writeLong(ldapServerId);

		if (openId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(openId);
		}

		objectOutput.writeLong(portraitId);

		if (languageId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(languageId);
		}

		if (timeZoneId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(timeZoneId);
		}

		if (greeting == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(greeting);
		}

		if (comments == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(comments);
		}

		if (firstName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(firstName);
		}

		if (middleName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(middleName);
		}

		if (lastName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastName);
		}

		if (jobTitle == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(jobTitle);
		}

		objectOutput.writeLong(loginDate);

		if (loginIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(loginIP);
		}

		objectOutput.writeLong(lastLoginDate);

		if (lastLoginIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastLoginIP);
		}

		objectOutput.writeLong(lastFailedLoginDate);

		objectOutput.writeInt(failedLoginAttempts);

		objectOutput.writeBoolean(lockout);
		objectOutput.writeLong(lockoutDate);

		objectOutput.writeBoolean(agreedToTermsOfUse);

		objectOutput.writeBoolean(emailAddressVerified);

		objectOutput.writeInt(type);

		objectOutput.writeInt(status);

		objectOutput.writeObject(groupId);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public String uuid;
	public String externalReferenceCode;
	public long userId;
	public long companyId;
	public long createDate;
	public long modifiedDate;
	public long contactId;
	public String password;
	public boolean passwordEncrypted;
	public boolean passwordReset;
	public long passwordModifiedDate;
	public String digest;
	public String reminderQueryQuestion;
	public String reminderQueryAnswer;
	public int graceLoginCount;
	public String screenName;
	public String emailAddress;
	public long facebookId;
	public String googleUserId;
	public long ldapServerId;
	public String openId;
	public long portraitId;
	public String languageId;
	public String timeZoneId;
	public String greeting;
	public String comments;
	public String firstName;
	public String middleName;
	public String lastName;
	public String jobTitle;
	public long loginDate;
	public String loginIP;
	public long lastLoginDate;
	public String lastLoginIP;
	public long lastFailedLoginDate;
	public int failedLoginAttempts;
	public boolean lockout;
	public long lockoutDate;
	public boolean agreedToTermsOfUse;
	public boolean emailAddressVerified;
	public int type;
	public int status;
	public volatile long groupId;

	private static final MethodHandle _groupIdMethodHandle;

	static {
		MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

		try {
			_groupIdMethodHandle = lookup.findSetter(
				UserImpl.class, "_groupId", long.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}