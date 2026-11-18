/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link MFATimeBasedOTPEntry}.
 * </p>
 *
 * @author Arthur Chan
 * @see MFATimeBasedOTPEntry
 * @generated
 */
public class MFATimeBasedOTPEntryWrapper
	extends BaseModelWrapper<MFATimeBasedOTPEntry>
	implements MFATimeBasedOTPEntry, ModelWrapper<MFATimeBasedOTPEntry> {

	public MFATimeBasedOTPEntryWrapper(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		super(mfaTimeBasedOTPEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("mfaTimeBasedOTPEntryId", getMfaTimeBasedOTPEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("algorithm", getAlgorithm());
		attributes.put("failedAttempts", getFailedAttempts());
		attributes.put("lastFailDate", getLastFailDate());
		attributes.put("lastFailIP", getLastFailIP());
		attributes.put("lastSuccessDate", getLastSuccessDate());
		attributes.put("lastSuccessIP", getLastSuccessIP());
		attributes.put("lastValidTOTP", getLastValidTOTP());
		attributes.put("sharedSecret", getSharedSecret());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long mfaTimeBasedOTPEntryId = (Long)attributes.get(
			"mfaTimeBasedOTPEntryId");

		if (mfaTimeBasedOTPEntryId != null) {
			setMfaTimeBasedOTPEntryId(mfaTimeBasedOTPEntryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String algorithm = (String)attributes.get("algorithm");

		if (algorithm != null) {
			setAlgorithm(algorithm);
		}

		Integer failedAttempts = (Integer)attributes.get("failedAttempts");

		if (failedAttempts != null) {
			setFailedAttempts(failedAttempts);
		}

		Date lastFailDate = (Date)attributes.get("lastFailDate");

		if (lastFailDate != null) {
			setLastFailDate(lastFailDate);
		}

		String lastFailIP = (String)attributes.get("lastFailIP");

		if (lastFailIP != null) {
			setLastFailIP(lastFailIP);
		}

		Date lastSuccessDate = (Date)attributes.get("lastSuccessDate");

		if (lastSuccessDate != null) {
			setLastSuccessDate(lastSuccessDate);
		}

		String lastSuccessIP = (String)attributes.get("lastSuccessIP");

		if (lastSuccessIP != null) {
			setLastSuccessIP(lastSuccessIP);
		}

		String lastValidTOTP = (String)attributes.get("lastValidTOTP");

		if (lastValidTOTP != null) {
			setLastValidTOTP(lastValidTOTP);
		}

		String sharedSecret = (String)attributes.get("sharedSecret");

		if (sharedSecret != null) {
			setSharedSecret(sharedSecret);
		}
	}

	@Override
	public MFATimeBasedOTPEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the algorithm of this mfa time based otp entry.
	 *
	 * @return the algorithm of this mfa time based otp entry
	 */
	@Override
	public String getAlgorithm() {
		return model.getAlgorithm();
	}

	/**
	 * Returns the company ID of this mfa time based otp entry.
	 *
	 * @return the company ID of this mfa time based otp entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this mfa time based otp entry.
	 *
	 * @return the create date of this mfa time based otp entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the failed attempts of this mfa time based otp entry.
	 *
	 * @return the failed attempts of this mfa time based otp entry
	 */
	@Override
	public int getFailedAttempts() {
		return model.getFailedAttempts();
	}

	/**
	 * Returns the last fail date of this mfa time based otp entry.
	 *
	 * @return the last fail date of this mfa time based otp entry
	 */
	@Override
	public Date getLastFailDate() {
		return model.getLastFailDate();
	}

	/**
	 * Returns the last fail ip of this mfa time based otp entry.
	 *
	 * @return the last fail ip of this mfa time based otp entry
	 */
	@Override
	public String getLastFailIP() {
		return model.getLastFailIP();
	}

	/**
	 * Returns the last success date of this mfa time based otp entry.
	 *
	 * @return the last success date of this mfa time based otp entry
	 */
	@Override
	public Date getLastSuccessDate() {
		return model.getLastSuccessDate();
	}

	/**
	 * Returns the last success ip of this mfa time based otp entry.
	 *
	 * @return the last success ip of this mfa time based otp entry
	 */
	@Override
	public String getLastSuccessIP() {
		return model.getLastSuccessIP();
	}

	/**
	 * Returns the last valid totp of this mfa time based otp entry.
	 *
	 * @return the last valid totp of this mfa time based otp entry
	 */
	@Override
	public String getLastValidTOTP() {
		return model.getLastValidTOTP();
	}

	/**
	 * Returns the mfa time based otp entry ID of this mfa time based otp entry.
	 *
	 * @return the mfa time based otp entry ID of this mfa time based otp entry
	 */
	@Override
	public long getMfaTimeBasedOTPEntryId() {
		return model.getMfaTimeBasedOTPEntryId();
	}

	/**
	 * Returns the modified date of this mfa time based otp entry.
	 *
	 * @return the modified date of this mfa time based otp entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this mfa time based otp entry.
	 *
	 * @return the mvcc version of this mfa time based otp entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this mfa time based otp entry.
	 *
	 * @return the primary key of this mfa time based otp entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the shared secret of this mfa time based otp entry.
	 *
	 * @return the shared secret of this mfa time based otp entry
	 */
	@Override
	public String getSharedSecret() {
		return model.getSharedSecret();
	}

	/**
	 * Returns the user ID of this mfa time based otp entry.
	 *
	 * @return the user ID of this mfa time based otp entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this mfa time based otp entry.
	 *
	 * @return the user name of this mfa time based otp entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this mfa time based otp entry.
	 *
	 * @return the user uuid of this mfa time based otp entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the algorithm of this mfa time based otp entry.
	 *
	 * @param algorithm the algorithm of this mfa time based otp entry
	 */
	@Override
	public void setAlgorithm(String algorithm) {
		model.setAlgorithm(algorithm);
	}

	/**
	 * Sets the company ID of this mfa time based otp entry.
	 *
	 * @param companyId the company ID of this mfa time based otp entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this mfa time based otp entry.
	 *
	 * @param createDate the create date of this mfa time based otp entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the failed attempts of this mfa time based otp entry.
	 *
	 * @param failedAttempts the failed attempts of this mfa time based otp entry
	 */
	@Override
	public void setFailedAttempts(int failedAttempts) {
		model.setFailedAttempts(failedAttempts);
	}

	/**
	 * Sets the last fail date of this mfa time based otp entry.
	 *
	 * @param lastFailDate the last fail date of this mfa time based otp entry
	 */
	@Override
	public void setLastFailDate(Date lastFailDate) {
		model.setLastFailDate(lastFailDate);
	}

	/**
	 * Sets the last fail ip of this mfa time based otp entry.
	 *
	 * @param lastFailIP the last fail ip of this mfa time based otp entry
	 */
	@Override
	public void setLastFailIP(String lastFailIP) {
		model.setLastFailIP(lastFailIP);
	}

	/**
	 * Sets the last success date of this mfa time based otp entry.
	 *
	 * @param lastSuccessDate the last success date of this mfa time based otp entry
	 */
	@Override
	public void setLastSuccessDate(Date lastSuccessDate) {
		model.setLastSuccessDate(lastSuccessDate);
	}

	/**
	 * Sets the last success ip of this mfa time based otp entry.
	 *
	 * @param lastSuccessIP the last success ip of this mfa time based otp entry
	 */
	@Override
	public void setLastSuccessIP(String lastSuccessIP) {
		model.setLastSuccessIP(lastSuccessIP);
	}

	/**
	 * Sets the last valid totp of this mfa time based otp entry.
	 *
	 * @param lastValidTOTP the last valid totp of this mfa time based otp entry
	 */
	@Override
	public void setLastValidTOTP(String lastValidTOTP) {
		model.setLastValidTOTP(lastValidTOTP);
	}

	/**
	 * Sets the mfa time based otp entry ID of this mfa time based otp entry.
	 *
	 * @param mfaTimeBasedOTPEntryId the mfa time based otp entry ID of this mfa time based otp entry
	 */
	@Override
	public void setMfaTimeBasedOTPEntryId(long mfaTimeBasedOTPEntryId) {
		model.setMfaTimeBasedOTPEntryId(mfaTimeBasedOTPEntryId);
	}

	/**
	 * Sets the modified date of this mfa time based otp entry.
	 *
	 * @param modifiedDate the modified date of this mfa time based otp entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this mfa time based otp entry.
	 *
	 * @param mvccVersion the mvcc version of this mfa time based otp entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this mfa time based otp entry.
	 *
	 * @param primaryKey the primary key of this mfa time based otp entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the shared secret of this mfa time based otp entry.
	 *
	 * @param sharedSecret the shared secret of this mfa time based otp entry
	 */
	@Override
	public void setSharedSecret(String sharedSecret) {
		model.setSharedSecret(sharedSecret);
	}

	/**
	 * Sets the user ID of this mfa time based otp entry.
	 *
	 * @param userId the user ID of this mfa time based otp entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this mfa time based otp entry.
	 *
	 * @param userName the user name of this mfa time based otp entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this mfa time based otp entry.
	 *
	 * @param userUuid the user uuid of this mfa time based otp entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected MFATimeBasedOTPEntryWrapper wrap(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		return new MFATimeBasedOTPEntryWrapper(mfaTimeBasedOTPEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1525849646