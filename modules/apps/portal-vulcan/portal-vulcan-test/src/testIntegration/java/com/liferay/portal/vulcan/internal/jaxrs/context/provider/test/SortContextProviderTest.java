/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.sort.InvalidSortException;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockFeature;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockMessage;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockResource;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.Arrays;
import java.util.Locale;

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
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SortContextProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		MockFeature mockFeature = new MockFeature(_feature);

		_contextProvider = (ContextProvider<Sort[]>)mockFeature.getObject(
			"com.liferay.portal.vulcan.internal.jaxrs.context.provider." +
				"SortContextProvider");

		Bundle bundle = FrameworkUtil.getBundle(SortContextProviderTest.class);

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
	public void testCreateContext() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest() {
				{
					addParameter("sort", "title:desc");
				}
			};

		Class<? extends MockResource> clazz = _mockResource.getClass();

		Sort[] sorts = _contextProvider.createContext(
			new MockMessage(
				mockHttpServletRequest,
				clazz.getMethod(MockResource.METHOD_NAME, String.class),
				_mockResource));

		Assert.assertEquals(Arrays.toString(sorts), 1, sorts.length);

		Sort sort = sorts[0];

		Assert.assertEquals("internalTitle", sort.getFieldName());
		Assert.assertTrue(sort.isReverse());
	}

	@Test(expected = InvalidSortException.class)
	public void testCreateContextThrowsInvalidSortException() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest() {
				{
					addParameter("sort", "invalid:desc");
				}
			};

		Class<? extends MockResource> clazz = _mockResource.getClass();

		_contextProvider.createContext(
			new MockMessage(
				mockHttpServletRequest,
				clazz.getMethod(MockResource.METHOD_NAME, String.class),
				_mockResource));
	}

	@Test
	public void testCreateContextWithDifferentLocale() throws Exception {

		// GET method

		Locale locale = LocaleUtil.TAIWAN;

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest() {
				{
					addHeader(
						HttpHeaders.ACCEPT_LANGUAGE,
						LocaleUtil.toW3cLanguageId(locale));
					addParameter("sort", "title:desc");
				}
			};

		Class<? extends MockResource> clazz = _mockResource.getClass();

		mockHttpServletRequest.setMethod(HttpMethod.GET);

		Sort[] sorts = _contextProvider.createContext(
			new MockMessage(
				mockHttpServletRequest,
				clazz.getMethod(MockResource.METHOD_NAME, String.class),
				_mockResource));

		Assert.assertEquals(Arrays.toString(sorts), 1, sorts.length);

		Sort sort = sorts[0];

		Assert.assertEquals("internalTitle", sort.getFieldName());
		Assert.assertTrue(sort.isReverse());

		// POST method

		mockHttpServletRequest.setMethod(HttpMethod.POST);

		AssertUtils.assertFailure(
			NotAcceptableException.class,
			"No locales match the accepted languages: " +
				locale.toLanguageTag(),
			() -> _contextProvider.createContext(
				new MockMessage(
					mockHttpServletRequest,
					clazz.getMethod(MockResource.METHOD_NAME, String.class),
					_mockResource)));
	}

	private ContextProvider<Sort[]> _contextProvider;

	@Inject(
		filter = "component.name=com.liferay.portal.vulcan.internal.jaxrs.feature.VulcanFeature"
	)
	private Feature _feature;

	private MockResource _mockResource;
	private ServiceRegistration<EntityModelResource> _serviceRegistration;

}