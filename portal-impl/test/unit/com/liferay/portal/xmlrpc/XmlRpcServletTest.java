/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xmlrpc;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leon Chi
 */
public class XmlRpcServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testInvokeMethod() throws Exception {
		Response response = XmlRpcUtil.createSuccess(
			RandomTestUtil.randomString());

		Method xmlRpcMethod = (Method)ProxyUtil.newProxyInstance(
			Method.class.getClassLoader(), new Class<?>[] {Method.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "execute")) {
					return response;
				}

				if (Objects.equals(method.getName(), "getMethodName")) {
					return _METHOD_NAME;
				}

				if (Objects.equals(method.getName(), "getToken")) {
					return _TOKEN;
				}

				if (Objects.equals(method.getName(), "setArguments")) {
					return true;
				}

				return null;
			});

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<Method> serviceRegistration =
			bundleContext.registerService(Method.class, xmlRpcMethod, null);

		try {
			XmlRpcServlet xmlRpcServlet = new XmlRpcServlet();

			Assert.assertSame(
				response,
				xmlRpcServlet.invokeMethod(
					RandomTestUtil.randomLong(), _TOKEN, _METHOD_NAME,
					new Object[0]));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final String _METHOD_NAME = "METHOD_NAME";

	private static final String _TOKEN = "TOKEN";

}