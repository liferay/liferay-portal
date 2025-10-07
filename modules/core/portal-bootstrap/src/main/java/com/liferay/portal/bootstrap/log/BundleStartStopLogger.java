/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bootstrap.log;

import com.liferay.portal.events.ShutdownHelperUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class BundleStartStopLogger implements SynchronousBundleListener {

	public BundleStartStopLogger(BundleContext bundleContext)
		throws InvalidSyntaxException {

		ServiceTracker<Object, Void> serviceTracker =
			new ServiceTracker<Object, Void>(
				bundleContext,
				bundleContext.createFilter(
					"(&(module.service.lifecycle=portal.initialized)(" +
						"objectClass=com.liferay.portal.kernel.module." +
							"framework.ModuleServiceLifecycle))"),
				null) {

				@Override
				public Void addingService(
					ServiceReference<Object> serviceReference) {

					_portalStarted.set(true);

					close();

					return null;
				}

			};

		serviceTracker.open();
	}

	@Override
	public void bundleChanged(BundleEvent bundleEvent) {
		Bundle bundle = bundleEvent.getBundle();

		if (bundle.getSymbolicName() == null) {
			_log.error(bundle.getLocation() + " has a null symbolic name");
		}

		if (_portalStarted.get() && !ShutdownHelperUtil.isShutdown()) {
			if (_log.isInfoEnabled()) {
				if (bundleEvent.getType() == BundleEvent.STARTED) {
					_log.info("STARTED " + bundle);
				}
				else if (bundleEvent.getType() == BundleEvent.STOPPED) {
					_log.info("STOPPED " + bundle);
				}
			}
		}
		else if (_log.isDebugEnabled()) {
			if (bundleEvent.getType() == BundleEvent.STARTED) {
				_log.debug("STARTED " + bundle);
			}
			else if (bundleEvent.getType() == BundleEvent.STOPPED) {
				_log.debug("STOPPED " + bundle);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BundleStartStopLogger.class);

	private final AtomicBoolean _portalStarted = new AtomicBoolean();

}