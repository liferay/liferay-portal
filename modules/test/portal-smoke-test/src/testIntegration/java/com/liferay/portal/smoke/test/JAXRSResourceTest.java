/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.smoke.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class JAXRSResourceTest {

	@Test
	public void testInterfaces() throws InvalidSyntaxException {
		Bundle bundle = FrameworkUtil.getBundle(JAXRSResourceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		StringBundler sb = new StringBundler();

		for (ServiceReference<?> serviceReference :
				bundleContext.getServiceReferences(
					(String)null, "(osgi.jaxrs.resource=true)")) {

			Object service = bundleContext.getService(serviceReference);

			_scanInterfaces(sb, service.getClass());

			bundleContext.ungetService(serviceReference);
		}

		Assert.assertEquals(sb.toString(), 0, sb.index());
	}

	private void _scanInterfaces(StringBundler sb, Class<?> serviceClass) {
		Class<?> superClass = serviceClass.getSuperclass();

		for (Class<?> interfaceClass : serviceClass.getInterfaces()) {
			if (interfaceClass.isAssignableFrom(superClass)) {
				sb.append(serviceClass.getName());
				sb.append(" should not directly implement interface ");
				sb.append(interfaceClass.getName());
				sb.append(
					" because it is already implemented by the super class ");
				sb.append(superClass.getName());
			}
		}
	}

}