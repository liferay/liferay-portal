/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PasswordTracker;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.service.base.PasswordTrackerLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 */
public class PasswordTrackerLocalServiceImpl
	extends PasswordTrackerLocalServiceBaseImpl {

	@Override
	public void deletePasswordTrackers(long userId) {
		passwordTrackerPersistence.removeByUserId(userId);
	}

	@Override
	public boolean isSameAsCurrentPassword(long userId, String newClearTextPwd)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		String currentPwd = user.getPassword();

		if (user.isPasswordEncrypted()) {
			String newEncPwd = PasswordEncryptorUtil.encrypt(
				newClearTextPwd, user.getPassword());

			return currentPwd.equals(newEncPwd);
		}

		return currentPwd.equals(newClearTextPwd);
	}

	@Override
	public boolean isValidPassword(long userId, String newClearTextPwd)
		throws PortalException {

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicyByUserId(userId);

		if ((passwordPolicy == null) || !passwordPolicy.isHistory()) {
			return true;
		}

		// Check password history

		int historyCount = 1;

		List<PasswordTracker> passwordTrackers =
			passwordTrackerPersistence.findByUserId(userId);

		for (PasswordTracker passwordTracker : passwordTrackers) {
			if (historyCount > passwordPolicy.getHistoryCount()) {
				break;
			}

			String oldEncPwd = passwordTracker.getPassword();

			String newEncPwd = PasswordEncryptorUtil.encrypt(
				newClearTextPwd, oldEncPwd);

			if (oldEncPwd.equals(newEncPwd)) {
				return false;
			}

			historyCount++;
		}

		return true;
	}

	@Override
	public void trackPassword(long userId, String encPassword)
		throws PortalException {

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicyByUserId(userId);

		if ((passwordPolicy != null) && passwordPolicy.isHistory()) {
			long passwordTrackerId = counterLocalService.increment();

			PasswordTracker passwordTracker = passwordTrackerPersistence.create(
				passwordTrackerId);

			passwordTracker.setUserId(userId);
			passwordTracker.setCreateDate(new Date());
			passwordTracker.setPassword(encPassword);

			passwordTrackerPersistence.update(passwordTracker);
		}
	}

	@BeanReference(type = PasswordPolicyLocalService.class)
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}