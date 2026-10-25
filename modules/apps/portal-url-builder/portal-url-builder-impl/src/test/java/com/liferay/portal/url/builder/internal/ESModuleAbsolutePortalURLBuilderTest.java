/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.portal.kernel.frontend.hashed.files.CachingStrategy;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
@RunWith(Parameterized.class)
public class ESModuleAbsolutePortalURLBuilderTest
	extends BaseAbsolutePortalURLBuilderTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Parameterized.Parameters(
		name = "{0}: cachingStrategy={1} cdnHost={2}, context={3}, proxy={4}"
	)
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{0, CachingStrategy.DO_NOT_USE_HASHES, false, false, false},
				{1, CachingStrategy.DO_NOT_USE_HASHES, false, false, true},
				{2, CachingStrategy.DO_NOT_USE_HASHES, false, true, false},
				{3, CachingStrategy.DO_NOT_USE_HASHES, false, true, true},
				{4, CachingStrategy.DO_NOT_USE_HASHES, true, false, false},
				{5, CachingStrategy.DO_NOT_USE_HASHES, true, false, true},
				{6, CachingStrategy.DO_NOT_USE_HASHES, true, true, false},
				{7, CachingStrategy.DO_NOT_USE_HASHES, true, true, true},
				{8, CachingStrategy.USE_ONE_HASH_PER_FILE, false, false, false},
				{9, CachingStrategy.USE_ONE_HASH_PER_FILE, false, false, true},
				{10, CachingStrategy.USE_ONE_HASH_PER_FILE, false, true, false},
				{11, CachingStrategy.USE_ONE_HASH_PER_FILE, false, true, true},
				{12, CachingStrategy.USE_ONE_HASH_PER_FILE, true, false, false},
				{13, CachingStrategy.USE_ONE_HASH_PER_FILE, true, false, true},
				{14, CachingStrategy.USE_ONE_HASH_PER_FILE, true, true, false},
				{15, CachingStrategy.USE_ONE_HASH_PER_FILE, true, true, true},
				{
					16, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, false,
					false, false
				},
				{
					17, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, false,
					false, true
				},
				{
					18, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, false,
					true, false
				},
				{
					19, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, false,
					true, true
				},
				{
					20, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, true,
					false, false
				},
				{
					21, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, true,
					false, true
				},
				{
					22, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, true,
					true, false
				},
				{
					23, CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, true,
					true, true
				}
			});
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_absolutePortalURLBuilder = new AbsolutePortalURLBuilderImpl(
			mockCacheHelper(), mockHashedFilesRegistry(cachingStrategy),
			mockPortal(context, proxy, cdnHost), mockHttpServletRequest());
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void test() {
		ESModuleAbsolutePortalURLBuilder esModuleAbsolutePortalURLBuilder =
			_absolutePortalURLBuilder.forESModule(
				"frontend-js-web", "index.js");

		Assert.assertEquals(
			_RESULTS[index], esModuleAbsolutePortalURLBuilder.build());

		esModuleAbsolutePortalURLBuilder =
			_absolutePortalURLBuilder.forESModule(
				"frontend-js-web", "index.js");

		esModuleAbsolutePortalURLBuilder.ignoreCDNHost();

		Assert.assertEquals(
			_RESULTS_IGNORE_CDN[index],
			esModuleAbsolutePortalURLBuilder.build());

		esModuleAbsolutePortalURLBuilder =
			_absolutePortalURLBuilder.forESModule(
				"frontend-js-web", "index.js");

		esModuleAbsolutePortalURLBuilder.ignoreCDNHost();
		esModuleAbsolutePortalURLBuilder.ignorePathProxy();

		Assert.assertEquals(
			_RESULTS_IGNORE_CDN_AND_PROXY[index],
			esModuleAbsolutePortalURLBuilder.build());

		esModuleAbsolutePortalURLBuilder =
			_absolutePortalURLBuilder.forESModule(
				"frontend-js-web", "index.js");

		esModuleAbsolutePortalURLBuilder.ignorePathProxy();

		Assert.assertEquals(
			_RESULTS_IGNORE_PROXY[index],
			esModuleAbsolutePortalURLBuilder.build());
	}

	@Test
	public void testForESModuleComputesRequestValuesOnce() throws Exception {
		HashedFilesRegistry hashedFilesRegistry = mockHashedFilesRegistry(
			cachingStrategy);
		HttpServletRequest httpServletRequest = mockHttpServletRequest();
		Portal portal = mockPortal(context, proxy, cdnHost);

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			new AbsolutePortalURLBuilderImpl(
				mockCacheHelper(), hashedFilesRegistry, portal,
				httpServletRequest);

		ESModuleAbsolutePortalURLBuilder esModuleAbsolutePortalURLBuilder =
			absolutePortalURLBuilder.forESModule("frontend-js-web", "index.js");

		String url = esModuleAbsolutePortalURLBuilder.build();

		Assert.assertEquals(_RESULTS[index], url);

		for (int i = 0; i < 5; i++) {
			esModuleAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forESModule(
					"frontend-js-web", "index.js");

			Assert.assertEquals(url, esModuleAbsolutePortalURLBuilder.build());
		}

		Mockito.verify(
			portal, Mockito.times(1)
		).getCDNHost(
			httpServletRequest
		);

		Mockito.verify(
			hashedFilesRegistry, Mockito.times(1)
		).getCachingStrategy(
			httpServletRequest
		);
	}

	@Parameterized.Parameter(1)
	public CachingStrategy cachingStrategy;

	@Parameterized.Parameter(2)
	public boolean cdnHost;

	@Parameterized.Parameter(3)
	public boolean context;

	@Parameterized.Parameter
	public int index;

	@Parameterized.Parameter(4)
	public boolean proxy;

	private static final String[] _RESULTS = {
		"/o/frontend-js-web/__liferay__/index.js",
		"/proxy/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/proxy/context/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/proxy/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/context/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/proxy/context/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/proxy/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/proxy/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/proxy/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/proxy/context/o/frontend-js-web/__liferay__" +
			"/index.(HASH).js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/proxy/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/proxy/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"http://cdn-host/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"http://cdn-host/proxy/o/js/-/frontend-js-web(HASH)/__liferay__" +
			"/index.js",
		"http://cdn-host/context/o/js/-/frontend-js-web(HASH)/__liferay__" +
			"/index.js",
		"http://cdn-host/proxy/context/o/js/-/frontend-js-web(HASH)" +
			"/__liferay__/index.js"
	};

	private static final String[] _RESULTS_IGNORE_CDN = {
		"/o/frontend-js-web/__liferay__/index.js",
		"/proxy/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/proxy/context/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.js",
		"/proxy/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/proxy/context/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/proxy/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/proxy/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/proxy/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/proxy/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/proxy/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/proxy/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/proxy/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/proxy/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js"
	};

	private static final String[] _RESULTS_IGNORE_CDN_AND_PROXY = {
		"/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js"
	};

	private static final String[] _RESULTS_IGNORE_PROXY = {
		"/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"/context/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/context/o/frontend-js-web/__liferay__/index.js",
		"http://cdn-host/context/o/frontend-js-web/__liferay__/index.js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"http://cdn-host/context/o/frontend-js-web/__liferay__/index.(HASH).js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"/context/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"http://cdn-host/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"http://cdn-host/o/js/-/frontend-js-web(HASH)/__liferay__/index.js",
		"http://cdn-host/context/o/js/-/frontend-js-web(HASH)/__liferay__" +
			"/index.js",
		"http://cdn-host/context/o/js/-/frontend-js-web(HASH)/__liferay__" +
			"/index.js"
	};

	private AbsolutePortalURLBuilder _absolutePortalURLBuilder;

}