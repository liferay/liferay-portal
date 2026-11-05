/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.friendly.url.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class FriendlyURLResolverRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			FriendlyURLResolverRegistryUtilTest.class);

		_bundleContext = bundle.getBundleContext();

		_friendlyURLResolver = _createFriendlyURLResolver();

		_serviceRegistration = _bundleContext.registerService(
			FriendlyURLResolver.class, _friendlyURLResolver, null);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetFriendlyURLResolver() {
		_assertGetFriendlyURLResolvers();

		Assert.assertSame(
			_friendlyURLResolver,
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_COMPANY_ID, _SEPARATOR));
	}

	@Test
	public void testGetFriendlyURLResolverByDefaultURLSeparator() {
		_assertGetFriendlyURLResolvers();

		Assert.assertSame(
			_friendlyURLResolver,
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolverByDefaultURLSeparator(_SEPARATOR));
	}

	@Test
	public void testGetFriendlyURLResolverWithHigherServiceRanking() {
		FriendlyURLResolver sampleFriendlyURLResolver =
			new SampleFriendlyURLResolver();

		FriendlyURLResolver defaultCanonicalURLSeparatorFriendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_COMPANY_ID, _CANONICAL_URL_SEPARATOR);

		ServiceRegistration<FriendlyURLResolver> serviceRegistration =
			_bundleContext.registerService(
				FriendlyURLResolver.class, sampleFriendlyURLResolver,
				MapUtil.singletonDictionary("service.ranking", 1000));

		try {
			_assertFriendlyURLResolver(
				sampleFriendlyURLResolver,
				defaultCanonicalURLSeparatorFriendlyURLResolver);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetFriendlyURLResolverWithLowerServiceRanking() {
		FriendlyURLResolver defaultCanonicalURLSeparatorFriendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_COMPANY_ID, _CANONICAL_URL_SEPARATOR);

		List<ServiceRegistration<FriendlyURLResolver>> list = new ArrayList<>();

		FriendlyURLResolver sampleFriendlyURLResolver1 =
			new SampleFriendlyURLResolver();

		list.add(
			_bundleContext.registerService(
				FriendlyURLResolver.class, sampleFriendlyURLResolver1,
				MapUtil.singletonDictionary("service.ranking", 1000)));

		try {
			_assertFriendlyURLResolver(
				sampleFriendlyURLResolver1,
				defaultCanonicalURLSeparatorFriendlyURLResolver);

			FriendlyURLResolver sampleFriendlyURLResolver2 =
				new SampleFriendlyURLResolver();

			list.add(
				_bundleContext.registerService(
					FriendlyURLResolver.class, sampleFriendlyURLResolver2,
					MapUtil.singletonDictionary("service.ranking", 500)));

			_assertFriendlyURLResolver(
				sampleFriendlyURLResolver1, sampleFriendlyURLResolver2);
		}
		finally {
			for (ServiceRegistration<FriendlyURLResolver> serviceRegistration :
					list) {

				serviceRegistration.unregister();
			}
		}
	}

	@Test
	public void testGetFriendlyURLResolverWithNegativeServiceRanking() {
		FriendlyURLResolver sampleFriendlyURLResolver =
			new SampleFriendlyURLResolver();

		FriendlyURLResolver defaultCanonicalURLSeparatorFriendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_COMPANY_ID, _CANONICAL_URL_SEPARATOR);

		ServiceRegistration<FriendlyURLResolver> serviceRegistration =
			_bundleContext.registerService(
				FriendlyURLResolver.class, sampleFriendlyURLResolver,
				MapUtil.singletonDictionary("service.ranking", -1000));

		try {
			_assertFriendlyURLResolver(
				defaultCanonicalURLSeparatorFriendlyURLResolver,
				sampleFriendlyURLResolver);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetURLSeparators() {
		long companyId = RandomTestUtil.randomLong();
		String urlSeparator = StringPool.SLASH + RandomTestUtil.randomString();

		ServiceRegistration<FriendlyURLResolver> serviceRegistration =
			_bundleContext.registerService(
				FriendlyURLResolver.class,
				_createFriendlyURLResolver(companyId, urlSeparator), null);

		try {
			_assertURLSeparators(_COMPANY_ID, _SEPARATOR, urlSeparator);
			_assertURLSeparators(companyId, urlSeparator, _SEPARATOR);
		}
		finally {
			serviceRegistration.unregister();

			FriendlyURLResolverRegistryUtil.removeURLSeparators();
		}
	}

	@Test
	public void testOverride() {
		Bundle bundle = FrameworkUtil.getBundle(
			FriendlyURLResolverRegistryUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		FriendlyURLResolver friendlyURLResolver = _createFriendlyURLResolver();

		ServiceRegistration<FriendlyURLResolver> serviceRegistration1 =
			bundleContext.registerService(
				FriendlyURLResolver.class, friendlyURLResolver,
				MapUtil.singletonDictionary("service.ranking", 25));

		ServiceRegistration<FriendlyURLResolver> serviceRegistration2 = null;

		try {
			Collection<FriendlyURLResolver> friendlyURLResolvers =
				FriendlyURLResolverRegistryUtil.
					getFriendlyURLResolversAsCollection(_COMPANY_ID);

			Assert.assertFalse(
				friendlyURLResolvers.toString(),
				friendlyURLResolvers.isEmpty());

			Assert.assertSame(
				friendlyURLResolver,
				FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
					_COMPANY_ID, _SEPARATOR));

			serviceRegistration2 = bundleContext.registerService(
				FriendlyURLResolver.class, _createFriendlyURLResolver(),
				MapUtil.singletonDictionary("service.ranking", 12));

			Assert.assertSame(
				friendlyURLResolver,
				FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
					_COMPANY_ID, _SEPARATOR));
		}
		finally {
			serviceRegistration1.unregister();

			if (serviceRegistration2 != null) {
				serviceRegistration2.unregister();
			}

			Assert.assertSame(
				_friendlyURLResolver,
				FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
					_COMPANY_ID, _SEPARATOR));
		}
	}

	private static FriendlyURLResolver _createFriendlyURLResolver() {
		return _createFriendlyURLResolver(_COMPANY_ID, _SEPARATOR);
	}

	private static FriendlyURLResolver _createFriendlyURLResolver(
		long companyId, String urlSeparator) {

		return (FriendlyURLResolver)ProxyUtil.newProxyInstance(
			FriendlyURLResolver.class.getClassLoader(),
			new Class<?>[] {FriendlyURLResolver.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getCompanyId")) {
					return companyId;
				}

				if (Objects.equals(
						method.getName(), "getDefaultURLSeparator") ||
					Objects.equals(method.getName(), "getURLSeparator")) {

					return urlSeparator;
				}

				return null;
			});
	}

	private void _assertFriendlyURLResolver(
		FriendlyURLResolver expectedFriendlyURLResolver,
		FriendlyURLResolver notExpectedFriendlyURLResolver) {

		FriendlyURLResolver curFriendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_COMPANY_ID, _CANONICAL_URL_SEPARATOR);

		Assert.assertEquals(
			expectedFriendlyURLResolver, curFriendlyURLResolver);
		Assert.assertNotEquals(
			notExpectedFriendlyURLResolver, curFriendlyURLResolver);
	}

	private void _assertGetFriendlyURLResolvers() {
		Collection<FriendlyURLResolver> friendlyURLResolvers =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolversAsCollection(
				_COMPANY_ID);

		Assert.assertFalse(
			friendlyURLResolvers.toString(), friendlyURLResolvers.isEmpty());
	}

	private void _assertURLSeparators(
		long companyId, String expectedURLSeparator,
		String notExpectedURLSeparator) {

		String[] urlSeparators =
			FriendlyURLResolverRegistryUtil.getURLSeparators(companyId);

		Assert.assertTrue(
			Arrays.toString(urlSeparators),
			ArrayUtil.contains(urlSeparators, expectedURLSeparator));
		Assert.assertFalse(
			Arrays.toString(urlSeparators),
			ArrayUtil.contains(urlSeparators, notExpectedURLSeparator));

		for (String urlSeparator : urlSeparators) {
			Assert.assertNotNull(
				urlSeparator,
				FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
					companyId, urlSeparator));
		}
	}

	private static final String _CANONICAL_URL_SEPARATOR = "/-/";

	private static final Long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _SEPARATOR = "/-foo-";

	private static BundleContext _bundleContext;
	private static FriendlyURLResolver _friendlyURLResolver;
	private static ServiceRegistration<FriendlyURLResolver>
		_serviceRegistration;

	private static class SampleFriendlyURLResolver
		implements FriendlyURLResolver {

		@Override
		public String getActualURL(
				long companyId, long groupId, boolean privateLayout,
				String mainPath, String friendlyURL,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
			throws PortalException {

			return StringPool.BLANK;
		}

		@Override
		public LayoutFriendlyURLComposite getLayoutFriendlyURLComposite(
				long companyId, long groupId, boolean privateLayout,
				String friendlyURL, Map<String, String[]> params,
				Map<String, Object> requestContext)
			throws PortalException {

			return null;
		}

		@Override
		public String getURLSeparator() {
			return _CANONICAL_URL_SEPARATOR;
		}

	}

}