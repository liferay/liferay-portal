/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManager;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManagerThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManager;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Neil Griffin
 */
public class SpringPortletAsyncScopeManager
	implements PortletAsyncScopeManager {

	public SpringPortletAsyncScopeManager() {
		_scopedBeanManagersInstallRunnable =
			SpringScopedBeanManagerThreadLocal.captureScopedBeanManagers();
	}

	@Override
	public void activateScopeContexts() {
		_scopedBeanManagersInstallRunnable.run();
	}

	@Override
	public void activateScopeContexts(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig) {

		if (_closeable != null) {
			throw new IllegalStateException(
				"Allready called activateScopeContexts");
		}

		_closeable = SpringScopedBeanManagerThreadLocal.install(
			new SpringScopedBeanManager(
				portletConfig, resourceRequest, resourceResponse));
	}

	@Override
	public void deactivateScopeContexts(boolean close) {
		if (!close) {
			SpringScopedBeanManagerThreadLocal.remove();

			return;
		}

		try {
			_closeable.close();
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringPortletAsyncScopeManager.class);

	private Closeable _closeable;
	private final Runnable _scopedBeanManagersInstallRunnable;

}