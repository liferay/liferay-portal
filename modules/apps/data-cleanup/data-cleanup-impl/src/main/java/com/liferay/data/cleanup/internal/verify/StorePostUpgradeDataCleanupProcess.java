/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

/**
 * @author Jorge Avalos
 */
public class StorePostUpgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public StorePostUpgradeDataCleanupProcess(Store store) {
		_store = store;
	}

	public void cleanUp() throws Exception {
		if (!CompanyThreadLocal.isDefaultCompany()) {
			return;
		}

		_store.verifyCompanyStores();
	}

	private final Store _store;

}