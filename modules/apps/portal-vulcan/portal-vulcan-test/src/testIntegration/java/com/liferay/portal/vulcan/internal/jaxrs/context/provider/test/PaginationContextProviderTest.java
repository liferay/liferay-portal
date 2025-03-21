/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockFeature;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockMessage;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockResource;
import com.liferay.portal.vulcan.internal.test.util.PaginationConfigurationTestUtil;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.ws.rs.core.Feature;

import org.apache.cxf.jaxrs.ext.ContextProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alberto Javier Moreno lage
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class PaginationContextProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		MockFeature mockFeature = new MockFeature(_feature);

		_contextProvider = (ContextProvider<Pagination>)mockFeature.getObject(
			"com.liferay.portal.vulcan.internal.jaxrs.context.provider." +
				"PaginationContextProvider");

		Bundle bundle = FrameworkUtil.getBundle(
			PaginationContextProviderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_mockResource = new MockResource();

		_serviceRegistration = bundleContext.registerService(
			EntityModelResource.class, _mockResource,
			HashMapDictionaryBuilder.<String, Object>put(
				"component.name", MockResource.class.getCanonicalName()
			).put(
				"osgi.jaxrs.resource", "true"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void test() throws Exception {

		// Default limited page size and limited page size requested

		_test(1, 20, null, null);
		_test(1, 5, null, 5);
		_test(1, 30, null, 30);
		_test(1, 20, null, null);
		_test(1, 15, null, 15);
		_test(1, 30, null, 30);
		_test(1, 40, null, 40);
		_test(2, 20, 2, null);
		_test(3, 20, 3, null);

		// Default limited page size and unlimited page size requested

		_test(1, 500, null, -1);
		_test(1, 500, null, 0);
		_test(1, 500, -1, null);
		_test(1, 500, 0, null);

		// Limited page size configured and limited page size requested

		PaginationConfigurationTestUtil.withPageSizeLimit(
			10, () -> _test(1, 10, null, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			10, () -> _test(1, 5, null, 5));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			10, () -> _test(1, 10, null, 30));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			30, () -> _test(1, 20, null, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			30, () -> _test(1, 15, null, 15));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			30, () -> _test(1, 30, null, 30));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			30, () -> _test(1, 30, null, 40));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			50, () -> _test(2, 20, 2, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			50, () -> _test(3, 20, 3, null));

		// Limited page size configured and unlimited page size requested

		PaginationConfigurationTestUtil.withPageSizeLimit(
			50, () -> _test(1, 50, null, -1));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			50, () -> _test(1, 50, null, 0));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			50, () -> _test(1, 50, -1, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			50, () -> _test(1, 50, 0, null));

		// Unlimited page size configured and limited page size requested

		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(1, 20, null, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(1, 25, null, 25));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(2, 20, 2, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(2, 25, 2, 25));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(1, 20, null, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(1, 25, null, 25));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(2, 20, 2, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(2, 25, 2, 25));

		// Unlimited page size configured and unlimited page size requested

		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(-1, -1, -1, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(-1, -1, 0, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(-1, -1, null, -1));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			-1, () -> _test(-1, -1, null, 0));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(-1, -1, -1, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(-1, -1, 0, null));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(-1, -1, null, -1));
		PaginationConfigurationTestUtil.withPageSizeLimit(
			0, () -> _test(-1, -1, null, 0));
	}

	private void _test(
			int expectedPage, int expectedPageSize, Integer requestPage,
			Integer requestPageSize)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (requestPage != null) {
			mockHttpServletRequest.addParameter("page", requestPage.toString());
		}

		if (requestPageSize != null) {
			mockHttpServletRequest.addParameter(
				"pageSize", requestPageSize.toString());
		}

		Pagination pagination = _contextProvider.createContext(
			new MockMessage(
				mockHttpServletRequest,
				MockResource.class.getMethod(
					MockResource.METHOD_NAME, String.class),
				_mockResource));

		Assert.assertEquals(expectedPage, pagination.getPage());
		Assert.assertEquals(expectedPageSize, pagination.getPageSize());
	}

	private ContextProvider<Pagination> _contextProvider;

	@Inject(
		filter = "component.name=com.liferay.portal.vulcan.internal.jaxrs.feature.VulcanFeature"
	)
	private Feature _feature;

	private MockResource _mockResource;
	private ServiceRegistration<EntityModelResource> _serviceRegistration;

}