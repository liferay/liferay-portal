/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.session.timeout;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera Avellón
 */
public class SessionTimeoutUtil {

	public static final boolean AUTO_EXTEND = false;

	public static final int AUTO_EXTEND_OFFSET = 70;

	public static int getAutoExtendOffset(
		HttpServletRequest httpServletRequest) {

		if (_sessionTimeout == null) {
			return AUTO_EXTEND_OFFSET;
		}

		return _sessionTimeout.getAutoExtendOffset(httpServletRequest);
	}

	public static boolean isAutoExtend(HttpServletRequest httpServletRequest) {
		if (_sessionTimeout == null) {
			return AUTO_EXTEND;
		}

		return _sessionTimeout.isAutoExtend(httpServletRequest);
	}

	private static final ServiceTracker<SessionTimeout, SessionTimeout>
		_serviceTracker;
	private static volatile SessionTimeout _sessionTimeout;

	static {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceTracker = new ServiceTracker<>(
			bundleContext, SessionTimeout.class,
			new ServiceTrackerCustomizer<SessionTimeout, SessionTimeout>() {

				@Override
				public SessionTimeout addingService(
					ServiceReference<SessionTimeout> serviceReference) {

					_sessionTimeout = bundleContext.getService(
						serviceReference);

					return _sessionTimeout;
				}

				@Override
				public void modifiedService(
					ServiceReference<SessionTimeout> serviceReference,
					SessionTimeout sessionTimeout) {
				}

				@Override
				public void removedService(
					ServiceReference<SessionTimeout> serviceReference,
					SessionTimeout sessionTimeout) {

					_sessionTimeout = null;

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

}