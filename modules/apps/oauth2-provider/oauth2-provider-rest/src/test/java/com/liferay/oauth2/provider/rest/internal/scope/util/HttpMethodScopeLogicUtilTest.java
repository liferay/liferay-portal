/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.scope.util;

import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
public class HttpMethodScopeLogicUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCheck() {
		ScopeFinder scopeFinder = () -> {
			_findScopesUseCount = _useCount;

			return Collections.emptyList();
		};

		ServiceReference<?> serviceReference =
			(ServiceReference<?>)ProxyUtil.newProxyInstance(
				ServiceReference.class.getClassLoader(),
				new Class<?>[] {ServiceReference.class},
				(proxy, method, args) -> null);

		Assert.assertTrue(
			HttpMethodScopeLogicUtil.check(
				(BundleContext)ProxyUtil.newProxyInstance(
					BundleContext.class.getClassLoader(),
					new Class<?>[] {BundleContext.class},
					(proxy, method, args) -> {
						String methodName = method.getName();

						if (methodName.equals("getService")) {
							_useCount++;

							return scopeFinder;
						}

						if (methodName.equals("getServiceReferences")) {
							return Collections.singletonList(serviceReference);
						}

						if (methodName.equals("ungetService")) {
							_useCount--;

							return true;
						}

						return null;
					}),
				Collections.singletonMap(
					"osgi.jaxrs.name", RandomTestUtil.randomString())::get,
				scope -> true, RandomTestUtil.randomString()));

		Assert.assertEquals(1, _findScopesUseCount);
		Assert.assertEquals(0, _useCount);
	}

	private int _findScopesUseCount;
	private int _useCount;

}