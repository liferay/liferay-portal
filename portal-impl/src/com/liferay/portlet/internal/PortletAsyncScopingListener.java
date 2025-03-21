/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManager;
import com.liferay.portlet.PortletAsyncListenerAdapter;

import jakarta.portlet.PortletAsyncContext;

import jakarta.servlet.AsyncEvent;

import java.io.IOException;

/**
 * @author Neil Griffin
 */
public class PortletAsyncScopingListener extends PortletAsyncListenerAdapter {

	public PortletAsyncScopingListener(
		PortletAsyncContext portletAsyncContext,
		PortletAsyncScopeManager portletAsyncScopeManager) {

		super(portletAsyncContext);

		_portletAsyncScopeManager = portletAsyncScopeManager;
	}

	@Override
	public void onComplete(AsyncEvent asyncEvent) throws IOException {
		_invokeCallback(() -> super.onComplete(asyncEvent), true);
	}

	@Override
	public void onError(AsyncEvent asyncEvent) throws IOException {
		_invokeCallback(() -> super.onError(asyncEvent), false);
	}

	@Override
	public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
		_invokeCallback(() -> super.onStartAsync(asyncEvent), false);
	}

	@Override
	public void onTimeout(AsyncEvent asyncEvent) throws IOException {
		_invokeCallback(() -> super.onTimeout(asyncEvent), false);
	}

	private void _invokeCallback(
			UnsafeRunnable<IOException> unsafeRunnable, boolean close)
		throws IOException {

		_portletAsyncScopeManager.activateScopeContexts();

		try {
			unsafeRunnable.run();
		}
		finally {
			_portletAsyncScopeManager.deactivateScopeContexts(close);
		}
	}

	private final PortletAsyncScopeManager _portletAsyncScopeManager;

}