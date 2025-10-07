/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.invoker.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.servlet.filters.invoker.Dispatcher;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterHelper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class InvokerFilterHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(InvokerFilterTest.class);

		_bundleContext = bundle.getBundleContext();

		MockServletContext mockServletContext = new MockServletContext();

		mockServletContext.setServletContextName(_SERVLET_CONTEXT_NAME);

		_invokerFilterHelper.init(new MockFilterConfig(mockServletContext));

		_registerFilter(
			new String[] {_TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3}, null,
			null);

		Assert.assertEquals(
			Arrays.asList(_TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testCreateInvokerFilterChainWithAfterDynamicFilter() {
		_registerFilter(
			new String[] {_TEST_FILTER_DYNAMIC}, _TEST_FILTER_1, null);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_DYNAMIC, _TEST_FILTER_2,
				_TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {_TEST_FILTER_C, _TEST_FILTER_A, _TEST_FILTER_B},
			_TEST_FILTER_DYNAMIC, null);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_DYNAMIC, _TEST_FILTER_C,
				_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(_TEST_FILTER_DYNAMIC);

		serviceRegistration.unregister();

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3, _TEST_FILTER_A,
				_TEST_FILTER_B, _TEST_FILTER_C),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {_TEST_FILTER_DYNAMIC}, _TEST_FILTER_1, null);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_DYNAMIC, _TEST_FILTER_C,
				_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	@Test
	public void testCreateInvokerFilterChainWithAfterStaticFilter() {
		_registerFilter(
			new String[] {_TEST_FILTER_C, _TEST_FILTER_A, _TEST_FILTER_B},
			_TEST_FILTER_1, null);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_C, _TEST_FILTER_B, _TEST_FILTER_A,
				_TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	@Test
	public void testCreateInvokerFilterChainWithBeforeAfterDynamicFilter() {
		String dynamicFilterName1 = _TEST_FILTER_DYNAMIC + "_1";
		String dynamicFilterName2 = _TEST_FILTER_DYNAMIC + "_2";

		_registerFilter(
			new String[] {dynamicFilterName1}, _TEST_FILTER_1, null);
		_registerFilter(
			new String[] {dynamicFilterName2}, null, _TEST_FILTER_3);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, dynamicFilterName1, _TEST_FILTER_2,
				dynamicFilterName2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_C},
			dynamicFilterName1, dynamicFilterName2);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, dynamicFilterName1, _TEST_FILTER_C,
				_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_2,
				dynamicFilterName2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		ServiceRegistration<?> serviceRegistration1 =
			_serviceRegistrations.remove(dynamicFilterName1);

		serviceRegistration1.unregister();

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_A, _TEST_FILTER_B,
				_TEST_FILTER_C, dynamicFilterName2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		ServiceRegistration<?> serviceRegistration2 =
			_serviceRegistrations.remove(dynamicFilterName2);

		serviceRegistration2.unregister();

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3, _TEST_FILTER_A,
				_TEST_FILTER_B, _TEST_FILTER_C),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {dynamicFilterName1}, _TEST_FILTER_1, null);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, dynamicFilterName1, _TEST_FILTER_C,
				_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {dynamicFilterName2}, null, _TEST_FILTER_3);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, dynamicFilterName1, _TEST_FILTER_C,
				_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_2,
				dynamicFilterName2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	@Test
	public void testCreateInvokerFilterChainWithBeforeAfterStaticFilter() {
		_registerFilter(
			new String[] {_TEST_FILTER_B, _TEST_FILTER_A, _TEST_FILTER_C},
			_TEST_FILTER_1, _TEST_FILTER_3);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_C, _TEST_FILTER_B, _TEST_FILTER_A,
				_TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	@Test
	public void testCreateInvokerFilterChainWithBeforeDynamicFilter() {
		_registerFilter(
			new String[] {_TEST_FILTER_DYNAMIC}, null, _TEST_FILTER_1);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_DYNAMIC, _TEST_FILTER_1, _TEST_FILTER_2,
				_TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {_TEST_FILTER_C, _TEST_FILTER_A, _TEST_FILTER_B},
			_TEST_FILTER_DYNAMIC, null);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_DYNAMIC, _TEST_FILTER_C, _TEST_FILTER_B,
				_TEST_FILTER_A, _TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());

		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(_TEST_FILTER_DYNAMIC);

		serviceRegistration.unregister();

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3, _TEST_FILTER_A,
				_TEST_FILTER_B, _TEST_FILTER_C),
			_getInvokerFilterChainFilters());

		_registerFilter(
			new String[] {_TEST_FILTER_DYNAMIC}, null, _TEST_FILTER_1);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_DYNAMIC, _TEST_FILTER_C, _TEST_FILTER_B,
				_TEST_FILTER_A, _TEST_FILTER_1, _TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	@Test
	public void testCreateInvokerFilterChainWithBeforeStaticFilter() {
		_registerFilter(
			new String[] {_TEST_FILTER_A, _TEST_FILTER_C, _TEST_FILTER_B}, null,
			_TEST_FILTER_1);

		Assert.assertEquals(
			Arrays.asList(
				_TEST_FILTER_A, _TEST_FILTER_B, _TEST_FILTER_C, _TEST_FILTER_1,
				_TEST_FILTER_2, _TEST_FILTER_3),
			_getInvokerFilterChainFilters());
	}

	private List<String> _getInvokerFilterChainFilters() {
		InvokerFilterChain invokerFilterChain = ReflectionTestUtil.invoke(
			_invokerFilterHelper, "createInvokerFilterChain",
			new Class<?>[] {
				HttpServletRequest.class, Dispatcher.class, String.class,
				FilterChain.class
			},
			new MockHttpServletRequest(), Dispatcher.REQUEST, "/", null);

		return TransformUtil.transform(
			(List<Filter>)ReflectionTestUtil.getFieldValue(
				invokerFilterChain, "_filters"),
			filter -> {
				TestFilter testFilter = (TestFilter)filter;

				return testFilter.getFilterName();
			});
	}

	private void _registerFilter(
		String[] filterNames, String afterFilterName, String beforeFilterName) {

		for (String filterName : filterNames) {
			_serviceRegistrations.put(
				filterName,
				_bundleContext.registerService(
					Filter.class, new TestFilter(filterName),
					HashMapDictionaryBuilder.put(
						"after-filter", afterFilterName
					).put(
						"before-filter", beforeFilterName
					).put(
						"servlet-context-name", _SERVLET_CONTEXT_NAME
					).put(
						"servlet-filter-name", filterName
					).put(
						"url-pattern", "/*"
					).build()));
		}
	}

	private static final String _SERVLET_CONTEXT_NAME =
		RandomTestUtil.randomString();

	private static final String _TEST_FILTER_1 = "TEST_FILTER_1";

	private static final String _TEST_FILTER_2 = "TEST_FILTER_2";

	private static final String _TEST_FILTER_3 = "TEST_FILTER_3";

	private static final String _TEST_FILTER_A = "TEST_FILTER_A";

	private static final String _TEST_FILTER_B = "TEST_FILTER_B";

	private static final String _TEST_FILTER_C = "TEST_FILTER_C";

	private static final String _TEST_FILTER_DYNAMIC = "_TEST_FILTER_DYNAMIC";

	private BundleContext _bundleContext;
	private final InvokerFilterHelper _invokerFilterHelper =
		new InvokerFilterHelper();
	private final Map<String, ServiceRegistration<?>> _serviceRegistrations =
		new HashMap<>();

	private static class TestFilter extends BasePortalFilter {

		public String getFilterName() {
			return _filterName;
		}

		private TestFilter(String filterName) {
			_filterName = filterName;
		}

		private final String _filterName;

	}

}