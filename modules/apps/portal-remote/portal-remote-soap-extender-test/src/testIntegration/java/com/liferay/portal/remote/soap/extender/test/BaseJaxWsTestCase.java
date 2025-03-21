/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.soap.extender.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import jakarta.xml.ws.Service;

import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Preston Crary
 */
public abstract class BaseJaxWsTestCase {

	@Before
	public void setUp() throws Exception {
		_bundleActivator = getBundleActivator();

		Bundle bundle = FrameworkUtil.getBundle(BaseJaxWsTestCase.class);

		_bundleContext = bundle.getBundleContext();

		_bundleActivator.start(_bundleContext);
	}

	@After
	public void tearDown() throws Exception {
		_bundleActivator.stop(_bundleContext);
	}

	protected abstract BundleActivator getBundleActivator();

	protected String getGreeting(String spec) throws Exception {
		URL url = new URL(spec);

		QName qName = new QName(
			"http://test.extender.soap.remote.portal.liferay.com/",
			"GreeterImplService");

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			Service service = Service.create(url, qName);

			Greeter greeter = service.getPort(Greeter.class);

			return greeter.greet();
		}
	}

	private BundleActivator _bundleActivator;
	private BundleContext _bundleContext;

}