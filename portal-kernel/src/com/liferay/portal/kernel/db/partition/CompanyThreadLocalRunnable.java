/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.db.partition;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

/**
 * @author István András Dézsi
 */
public class CompanyThreadLocalRunnable implements Runnable {

	public CompanyThreadLocalRunnable(Runnable runnable) {
		_runnable = runnable;

		_companyId = CompanyThreadLocal.getCompanyId();
	}

	@Override
	public void run() {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(_companyId)) {

			_runnable.run();
		}
	}

	private final Long _companyId;
	private final Runnable _runnable;

}