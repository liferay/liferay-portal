/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Dante Wang
 */
public class ServiceHolder<S> implements Comparable<ServiceHolder<?>> {

	public ServiceHolder(
		Bundle bundle, S service, long serviceId, int serviceRanking) {

		_bundle = bundle;
		_service = service;
		_serviceId = serviceId;
		_serviceRanking = serviceRanking;

		_serviceObjects = null;
	}

	public ServiceHolder(ServiceObjects<S> serviceObjects) {
		_serviceObjects = serviceObjects;

		ServiceReference<S> serviceReference =
			serviceObjects.getServiceReference();

		_bundle = serviceReference.getBundle();

		_service = serviceObjects.getService();
		_serviceId = (long)serviceReference.getProperty("service.id");
		_serviceRanking = GetterUtil.getInteger(
			serviceReference.getProperty("service.ranking"));
	}

	@Override
	public int compareTo(ServiceHolder<?> serviceHolder) {
		if (_serviceRanking == serviceHolder._serviceRanking) {
			return Long.compare(_serviceId, serviceHolder._serviceId);
		}
		else if (_serviceRanking < serviceHolder._serviceRanking) {
			return 1;
		}

		return -1;
	}

	public S get() {
		return _service;
	}

	public Bundle getBundle() {
		return _bundle;
	}

	public ClassLoader getBundleClassLoader() {
		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		return bundleWiring.getClassLoader();
	}

	public ServiceReference<S> getServiceReference() {
		if (_serviceObjects == null) {
			return null;
		}

		return _serviceObjects.getServiceReference();
	}

	public void release() {
		if ((_serviceObjects != null) && (_service != null)) {
			try {
				_serviceObjects.ungetService(_service);
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(ServiceHolder.class);

	private final Bundle _bundle;
	private final S _service;
	private final long _serviceId;
	private final ServiceObjects<S> _serviceObjects;
	private final int _serviceRanking;

}