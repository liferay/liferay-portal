/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.CompanyConstants;

import java.util.concurrent.Callable;

/**
 * @author István András Dézsi
 */
public class CompanyInheritableThreadLocalCallable<T> implements Callable<T> {

	public CompanyInheritableThreadLocalCallable(Callable<T> callable) {
		_callable = callable;

		_companyId = CompanyThreadLocal.getCompanyId();
	}

	@Override
	public T call() throws Exception {
		SafeCloseable safeCloseable = null;

		try {
			if (_companyId == CompanyConstants.SYSTEM) {
				safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						_companyId);
			}
			else {
				safeCloseable = CompanyThreadLocal.lock(_companyId);
			}

			return _callable.call();
		}
		finally {
			if (safeCloseable != null) {
				safeCloseable.close();
			}
		}
	}

	private final Callable<T> _callable;
	private final Long _companyId;

}