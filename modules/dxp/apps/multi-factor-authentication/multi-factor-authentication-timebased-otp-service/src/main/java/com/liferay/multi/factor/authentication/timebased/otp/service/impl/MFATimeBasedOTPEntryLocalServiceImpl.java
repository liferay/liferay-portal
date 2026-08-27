/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.service.impl;

import com.liferay.multi.factor.authentication.timebased.otp.exception.DuplicateMFATimeBasedOTPEntryException;
import com.liferay.multi.factor.authentication.timebased.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.timebased.otp.model.MFATimeBasedOTPEntry;
import com.liferay.multi.factor.authentication.timebased.otp.service.base.MFATimeBasedOTPEntryLocalServiceBaseImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.exception.SecretException;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = "model.class.name=com.liferay.multi.factor.authentication.timebased.otp.model.MFATimeBasedOTPEntry",
	service = AopService.class
)
public class MFATimeBasedOTPEntryLocalServiceImpl
	extends MFATimeBasedOTPEntryLocalServiceBaseImpl {

	@Override
	public MFATimeBasedOTPEntry addTimeBasedOTPEntry(
			long userId, String sharedSecret)
		throws PortalException {

		if (KeyReferenceUtil.isKeyReference(sharedSecret)) {
			throw new PortalException(
				"Shared secret cannot begin with a reserved key reference " +
					"prefix");
		}

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			mfaTimeBasedOTPEntryPersistence.fetchByUserId(userId);

		if (mfaTimeBasedOTPEntry != null) {
			throw new DuplicateMFATimeBasedOTPEntryException(
				"User ID  " + userId);
		}

		mfaTimeBasedOTPEntry = mfaTimeBasedOTPEntryPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUserById(userId);

		mfaTimeBasedOTPEntry.setCompanyId(user.getCompanyId());

		mfaTimeBasedOTPEntry.setUserId(userId);
		mfaTimeBasedOTPEntry.setUserName(user.getFullName());
		mfaTimeBasedOTPEntry.setCreateDate(new Date());

		_setSharedSecret(mfaTimeBasedOTPEntry, sharedSecret);

		return mfaTimeBasedOTPEntryPersistence.update(mfaTimeBasedOTPEntry);
	}

	@Override
	public MFATimeBasedOTPEntry deleteMFATimeBasedOTPEntry(
			long mfaTimeBasedOTPEntryId)
		throws PortalException {

		return deleteMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntryPersistence.findByPrimaryKey(
				mfaTimeBasedOTPEntryId));
	}

	@Override
	public MFATimeBasedOTPEntry deleteMFATimeBasedOTPEntry(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		String sharedSecret = mfaTimeBasedOTPEntry.getSharedSecret();

		if (KeyReferenceUtil.isKeyReference(sharedSecret)) {
			try {
				_secretManager.deleteSecret(
					mfaTimeBasedOTPEntry.getCompanyId(),
					KeyReferenceUtil.toKeyReference(sharedSecret));
			}
			catch (SecretException secretException) {
				_log.error(
					"Unable to delete the stored value for MFA time-based " +
						"OTP entry " +
							mfaTimeBasedOTPEntry.getMfaTimeBasedOTPEntryId(),
					secretException);
			}
		}

		return mfaTimeBasedOTPEntryPersistence.remove(mfaTimeBasedOTPEntry);
	}

	@Override
	public MFATimeBasedOTPEntry fetchMFATimeBasedOTPEntryByUserId(long userId) {
		return mfaTimeBasedOTPEntryPersistence.fetchByUserId(userId);
	}

	@Override
	public String getPlaintextSharedSecret(
			MFATimeBasedOTPEntry mfaTimeBasedOTPEntry)
		throws PortalException {

		String sharedSecret = mfaTimeBasedOTPEntry.getSharedSecret();

		if (!KeyReferenceUtil.isKeyReference(sharedSecret)) {
			return sharedSecret;
		}

		try (Secret secret = _secretManager.getSecret(
				mfaTimeBasedOTPEntry.getCompanyId(),
				KeyReferenceUtil.toKeyReference(sharedSecret))) {

			return new String(secret.getChars());
		}
	}

	@Override
	public MFATimeBasedOTPEntry resetFailedAttempts(long userId)
		throws PortalException {

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			mfaTimeBasedOTPEntryPersistence.fetchByUserId(userId);

		if (mfaTimeBasedOTPEntry == null) {
			throw new NoSuchEntryException("User ID " + userId);
		}

		mfaTimeBasedOTPEntry.setFailedAttempts(0);
		mfaTimeBasedOTPEntry.setLastFailDate(null);
		mfaTimeBasedOTPEntry.setLastFailIP(null);

		return mfaTimeBasedOTPEntryPersistence.update(mfaTimeBasedOTPEntry);
	}

	@Override
	public MFATimeBasedOTPEntry updateAttempts(
			long userId, String ipAddress, boolean success)
		throws PortalException {

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			mfaTimeBasedOTPEntryPersistence.fetchByUserId(userId);

		if (mfaTimeBasedOTPEntry == null) {
			throw new NoSuchEntryException("User ID " + userId);
		}

		if (success) {
			mfaTimeBasedOTPEntry.setFailedAttempts(0);
			mfaTimeBasedOTPEntry.setLastFailDate(null);
			mfaTimeBasedOTPEntry.setLastFailIP(null);
			mfaTimeBasedOTPEntry.setLastSuccessDate(new Date());
			mfaTimeBasedOTPEntry.setLastSuccessIP(ipAddress);
		}
		else {
			mfaTimeBasedOTPEntry.setFailedAttempts(
				mfaTimeBasedOTPEntry.getFailedAttempts() + 1);
			mfaTimeBasedOTPEntry.setLastFailDate(new Date());
			mfaTimeBasedOTPEntry.setLastFailIP(ipAddress);
		}

		return mfaTimeBasedOTPEntryPersistence.update(mfaTimeBasedOTPEntry);
	}

	@Override
	public MFATimeBasedOTPEntry updateLastTOTP(
			long userId, String lastValidTOTP)
		throws PortalException {

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			mfaTimeBasedOTPEntryPersistence.fetchByUserId(userId);

		if (mfaTimeBasedOTPEntry == null) {
			throw new NoSuchEntryException("User ID " + userId);
		}

		mfaTimeBasedOTPEntry.setLastValidTOTP(lastValidTOTP);

		return mfaTimeBasedOTPEntryPersistence.update(mfaTimeBasedOTPEntry);
	}

	private String _getSecretIdentifier(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		return StringBundler.concat(
			MFATimeBasedOTPEntry.class.getSimpleName(), StringPool.POUND,
			mfaTimeBasedOTPEntry.getMfaTimeBasedOTPEntryId());
	}

	private void _setSharedSecret(
			MFATimeBasedOTPEntry mfaTimeBasedOTPEntry, String sharedSecret)
		throws PortalException {

		if (Validator.isNull(sharedSecret) || !PropsValues.FIPS_ENABLED) {
			mfaTimeBasedOTPEntry.setSharedSecret(sharedSecret);

			return;
		}

		KeyReference keyReference = new KeyReference(
			_getSecretIdentifier(mfaTimeBasedOTPEntry), StringPool.STAR,
			KeyReference.Type.SECRET);

		mfaTimeBasedOTPEntry.setSharedSecret(
			KeyReferenceUtil.toKeyReferenceString(keyReference));

		try (Secret secret = new Secret(keyReference, sharedSecret)) {
			_secretManager.putSecret(
				mfaTimeBasedOTPEntry.getCompanyId(), secret);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFATimeBasedOTPEntryLocalServiceImpl.class);

	@Reference
	private SecretManager _secretManager;

	@Reference
	private UserLocalService _userLocalService;

}