/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.threadlocal;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class ThreadLocalFilter
	extends BasePortalFilter implements TryFinallyFilter {

	@Override
	public void doFilterFinally(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Object object) {

		ThreadLocalCacheManager.clearAll(Lifecycle.REQUEST);

		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

	@Override
	public Object doFilterTry(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		ThreadLocalFilterThreadLocal.setFilterInvoked();

		return null;
	}

}