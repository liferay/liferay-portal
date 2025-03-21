/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.test;

import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import jakarta.portlet.Portlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Matthew Tambara
 */
public class JspPrecompileBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			Portlet.class, new JspPrecompilePortlet(),
			HashMapDictionaryBuilder.put(
				"jakarta.portlet.display-name", "Jsp Precompile Portlet"
			).put(
				"jakarta.portlet.name", JspPrecompilePortlet.PORTLET_NAME
			).build());
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_serviceRegistration.unregister();
	}

	private ServiceRegistration<Portlet> _serviceRegistration;

}