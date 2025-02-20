/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class DoAsUserThread extends Thread {

	public DoAsUserThread(long userId) {
		this(userId, 1);
	}

	public DoAsUserThread(long userId, int retries) {
		_userId = userId;
		_retries = retries;
	}

	public boolean isSuccess() {
		return _success;
	}

	@Override
	public void run() {
		for (int i = 0; i < _retries; i++) {
			try {
				User user = UserLocalServiceUtil.getUserById(_userId);

				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							user.getCompanyId())) {

					PrincipalThreadLocal.setName(_userId);

					PermissionThreadLocal.setPermissionChecker(
						PermissionCheckerFactoryUtil.create(user));

					doRun();
				}

				_success = true;

				return;
			}
			catch (Exception exception) {
				_log.error(exception);
			}
			finally {
				PrincipalThreadLocal.setName(null);
				PermissionThreadLocal.setPermissionChecker(null);
			}
		}
	}

	protected abstract void doRun() throws Exception;

	private static final Log _log = LogFactoryUtil.getLog(DoAsUserThread.class);

	private final int _retries;
	private boolean _success;
	private final long _userId;

}