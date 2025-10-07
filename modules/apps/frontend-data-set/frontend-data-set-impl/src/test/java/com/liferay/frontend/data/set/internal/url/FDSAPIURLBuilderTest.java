/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.url;

import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Daniel Sanz
 */
public class FDSAPIURLBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_bundleContext = SystemBundleUtil.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, FDSAPIURLResolver.class, "fds.rest.application.key",
			ServiceTrackerCustomizerFactory.<FDSAPIURLResolver>serviceWrapper(
				_bundleContext));

		_fdsAPIURLResolverRegistry = new FDSAPIURLResolverRegistryImpl(
			_serviceTrackerMap);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			12345L
		);

		Mockito.when(
			themeDisplay.getUserId()
		).thenReturn(
			67890L
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	@After
	public void tearDown() {
		_serviceTrackerMap.close();
	}

	@Test
	public void testBuild() throws Exception {

		// No interpolation

		ServiceRegistration<FDSAPIURLResolver> serviceRegistration1 =
			_registerFDSAPIURLResolver(
				"/app", "schema", new String[] {"{foo}"}, new String[] {"bar"});

		Assert.assertEquals(
			"/o/app/{siteId}/{foo}/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{siteId}/{foo}/endpoint", "schema"
			).build(
				false
			));
		Assert.assertEquals(
			"/o/app/{siteId}/{foo}/{bob}/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{siteId}/{foo}/{bob}/endpoint", "schema"
			).setTokenResolutions(
				JSONUtil.put("bob", "alice")
			).build(
				false
			));

		serviceRegistration1.unregister();

		// No resolver

		_testBuild(
			"/o/app/67890/endpoint", "/app", "/{userId}/endpoint", "schema");
		_testBuild("/o/app/endpoint", "/app", "/endpoint", "schema");
		_testBuild("/o/app/endpoint", "/app/v1.0", "/endpoint", "schema");
		_testBuild("/o/app/v1.0/endpoint", "/app", "/v1.0/endpoint", "schema");
		_testBuild(
			"/o/app/v1.0/endpoint", "/app/v1.0", "/v1.0/endpoint", "schema");

		Assert.assertEquals(
			"/o/app/endpoint?param1=value1&param2=value2",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/endpoint", "schema"
			).addParameter(
				"param1", "value1"
			).addParameter(
				"param2", "value2"
			).build());
		Assert.assertEquals(
			"/o/app/endpoint?param1=value1&param2=value2",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/endpoint", "schema"
			).addQueryString(
				"param1=value1&param2=value2"
			).build());
		Assert.assertEquals(
			"/o/app/endpoint?param1=value1&param2=value2&param3=value3&" +
				"param4=value4&param5=value5",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/endpoint", "schema"
			).addParameter(
				"param1", "value1"
			).addQueryString(
				"param2=value2&param3=value3"
			).addParameter(
				"param4", "value4"
			).addQueryString(
				"param5=value5"
			).build());

		// One resolver, one token

		serviceRegistration1 = _registerFDSAPIURLResolver(
			"/app", "schema", new String[] {"{foo}"}, new String[] {"bar"});

		_testBuild(
			"/o/app/12345/bar/endpoint", "/app", "/{siteId}/{foo}/endpoint",
			"schema");
		_testBuild(
			"/o/app/{xyz}/endpoint", "/app", "/{xyz}/endpoint", "schema");

		Assert.assertEquals(
			"/o/app/bar/endpoint?siteId=12345&foo=bar&bar=67890",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{foo}/endpoint", "schema"
			).addParameter(
				"siteId", "{siteId}"
			).addQueryString(
				"foo={foo}"
			).addParameter(
				"{foo}", "{userId}"
			).build());
		Assert.assertEquals(
			"siteId=12345&foo=bar&bar=67890",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/endpoint", "schema"
			).addParameter(
				"siteId", "{siteId}"
			).addQueryString(
				"foo={foo}"
			).addParameter(
				"{foo}", "{userId}"
			).buildQueryString());
		Assert.assertNull(
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/endpoint", "schema"
			).addParameter(
				"", ""
			).addQueryString(
				""
			).addParameter(
				"foo", ""
			).addParameter(
				"", "foo"
			).buildQueryString());

		serviceRegistration1.unregister();

		// One resolver, two tokens

		serviceRegistration1 = _registerFDSAPIURLResolver(
			"/app", "schema", new String[] {"{foo}", "{userId}"},
			new String[] {"bar", "54321"});

		_testBuild(
			"/o/app/bar/54321/endpoint", "/app", "/{foo}/{userId}/endpoint",
			"schema");

		_testBuild(
			"/o/app/12345/bar/54321/endpoint", "/app",
			"/{siteId}/{foo}/{userId}/endpoint", "schema");

		serviceRegistration1.unregister();

		// Resolved tokens, no resolvers

		Assert.assertEquals(
			"/o/app/bar/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{foo}/endpoint", "schema1"
			).setTokenResolutions(
				JSONUtil.put("foo", "bar")
			).build());

		// Resolved tokens, resolver

		serviceRegistration1 = _registerFDSAPIURLResolver(
			"/app", "schema", new String[] {"{foo}"}, new String[] {"bar"});

		Assert.assertEquals(
			"/o/app/baz/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{foo}/endpoint", "schema"
			).setTokenResolutions(
				JSONUtil.put("foo", "baz")
			).build());
		Assert.assertEquals(
			"/o/app/bar/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{foo}/endpoint", "schema"
			).setTokenResolutions(
				JSONUtil.put("other", "baz")
			).build());
		Assert.assertEquals(
			"/o/app/12345/bar/alice/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{siteId}/{foo}/{bob}/endpoint", "schema"
			).setTokenResolutions(
				JSONUtil.put("bob", "alice")
			).build());

		serviceRegistration1.unregister();

		// Resolved tokens, resolver, default tokens

		serviceRegistration1 = _registerFDSAPIURLResolver(
			"/app", "schema", new String[] {"{siteId}"},
			new String[] {"99999"});

		Assert.assertEquals(
			"/o/app/00000/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{siteId}/endpoint", "schema"
			).setTokenResolutions(
				JSONUtil.put("siteId", "00000")
			).build());
		Assert.assertEquals(
			"/o/app/99999/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app",
				"/{siteId}/endpoint", "schema"
			).build());

		serviceRegistration1.unregister();

		// Two resolvers

		serviceRegistration1 = _registerFDSAPIURLResolver(
			"/app1", "schema1", new String[] {"{foo}"}, new String[] {"bar"});
		ServiceRegistration<FDSAPIURLResolver> serviceRegistration2 =
			_registerFDSAPIURLResolver(
				"/app2", "schema2", new String[] {"{foo}"},
				new String[] {"bar"});

		Assert.assertEquals(
			"/o/app1/bar/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app1",
				"/{foo}/endpoint", "schema1"
			).build());
		Assert.assertEquals(
			"/o/app2/bar/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app2",
				"/{foo}/endpoint", "schema2"
			).build());
		Assert.assertEquals(
			"/o/app2/{foo}/endpoint",
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest, "/app2",
				"/{foo}/endpoint", "schema1"
			).build());

		serviceRegistration1.unregister();
		serviceRegistration2.unregister();
	}

	private ServiceRegistration<FDSAPIURLResolver> _registerFDSAPIURLResolver(
		String restApplication, String restSchema, String[] tokens,
		String[] values) {

		return _bundleContext.registerService(
			FDSAPIURLResolver.class,
			new FDSAPIURLResolver() {

				@Override
				public String getSchema() {
					return restSchema;
				}

				@Override
				public String resolve(
						String baseURL, HttpServletRequest httpServletRequest)
					throws PortalException {

					return StringUtil.replace(baseURL, tokens, values);
				}

			},
			MapUtil.singletonDictionary(
				"fds.rest.application.key",
				restApplication + "/" + restSchema));
	}

	private void _testBuild(
		String apiURL, String restApplication, String restEndpoint,
		String restSchema) {

		Assert.assertEquals(
			apiURL,
			new FDSAPIURLBuilder(
				_fdsAPIURLResolverRegistry, _httpServletRequest,
				restApplication, restEndpoint, restSchema
			).build());
	}

	private BundleContext _bundleContext;
	private FDSAPIURLResolverRegistry _fdsAPIURLResolverRegistry;
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private ServiceTrackerMap<String, ServiceWrapper<FDSAPIURLResolver>>
		_serviceTrackerMap;

}