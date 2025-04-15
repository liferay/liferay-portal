/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.init.servlet.filter.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import jakarta.servlet.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Matthew Tambara
 */
@Component(service = {})
public class InitFilterTracker {

	@Activate
	protected void activate(BundleContext bundleContext) {
		InitFilter initFilter = new InitFilter();

		_serviceRegistration = bundleContext.registerService(
			Filter.class, initFilter,
			HashMapDictionaryBuilder.<String, Object>put(
				"dispatcher", new String[] {"FORWARD", "REQUEST"}
			).put(
				"servlet-context-name", ""
			).put(
				"servlet-filter-name", "Init Filter"
			).put(
				"url-pattern", "/*"
			).build());

		initFilter.setServiceRegistration(_serviceRegistration);
	}

	@Deactivate
	protected void deactivate() {
		try {
			_serviceRegistration.unregister();
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InitFilterTracker.class);

	private ServiceRegistration<Filter> _serviceRegistration;

}