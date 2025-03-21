/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManager;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;

import java.io.IOException;

/**
 * @author Neil Griffin
 */
public class PortletAsyncScopingRunnable implements Runnable {

	public PortletAsyncScopingRunnable(
		Runnable runnable, AsyncListener asyncListener,
		PortletAsyncScopeManager portletAsyncScopeManager) {

		_runnable = runnable;
		_asyncListener = asyncListener;
		_portletAsyncScopeManager = portletAsyncScopeManager;
	}

	@Override
	public void run() {
		_portletAsyncScopeManager.activateScopeContexts();

		try {
			_runnable.run();
		}
		catch (Throwable throwable) {
			try {
				_asyncListener.onError(new AsyncEvent(null, throwable));
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}
		finally {
			_portletAsyncScopeManager.deactivateScopeContexts(false);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletAsyncScopingRunnable.class);

	private final AsyncListener _asyncListener;
	private final PortletAsyncScopeManager _portletAsyncScopeManager;
	private final Runnable _runnable;

}