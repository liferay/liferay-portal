/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.resource.locator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.locator.ResourceLocator;
import com.liferay.portal.vulcan.resource.locator.ResourceLocatorFactory;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ResourceLocatorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testResourceLocator() {
		Bundle bundle = FrameworkUtil.getBundle(
			ResourceLocatorFactoryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				TestResource.Factory.class, new TestResourceFactoryImpl(),
				MapUtil.singletonDictionary(
					"resource.locator.key", "/test/v1.0/Test"));

		try {
			HttpServletRequest httpServletRequest =
				ProxyFactory.newDummyInstance(HttpServletRequest.class);
			User user = ProxyFactory.newDummyInstance(User.class);

			ResourceLocator resourceLocator = _resourceLocatorFactory.create(
				httpServletRequest, user);

			Object testResource = resourceLocator.locate("/test/v1.0", "Test");

			Assert.assertSame(TestResourceImpl.class, testResource.getClass());

			TestResourceImpl testResourceImpl = (TestResourceImpl)testResource;

			Assert.assertSame(
				httpServletRequest, testResourceImpl._httpServletRequest);
			Assert.assertSame(user, testResourceImpl._user);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Inject
	private ResourceLocatorFactory _resourceLocatorFactory;

	private static class TestResourceFactoryImpl
		implements TestResource.Factory {

		@Override
		public TestResource.Builder create() {
			return new TestResource.Builder() {

				@Override
				public TestResource build() {
					return new TestResourceImpl(_httpServletRequest, _user);
				}

				@Override
				public TestResource.Builder httpServletRequest(
					HttpServletRequest httpServletRequest) {

					_httpServletRequest = httpServletRequest;

					return this;
				}

				@Override
				public TestResource.Builder user(User user) {
					_user = user;

					return this;
				}

				private HttpServletRequest _httpServletRequest;
				private User _user;

			};
		}

	}

	private static class TestResourceImpl implements TestResource {

		private TestResourceImpl(
			HttpServletRequest httpServletRequest, User user) {

			_httpServletRequest = httpServletRequest;
			_user = user;
		}

		private final HttpServletRequest _httpServletRequest;
		private final User _user;

	}

	private interface TestResource {

		public interface Builder {

			public TestResource build();

			public Builder httpServletRequest(
				HttpServletRequest httpServletRequest);

			public Builder user(User user);

		}

		public interface Factory {

			public Builder create();

		}

	}

}