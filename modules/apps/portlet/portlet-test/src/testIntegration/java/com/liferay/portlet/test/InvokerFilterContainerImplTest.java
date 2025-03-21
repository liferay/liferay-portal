/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.PortletInstanceFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
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
 * @author Leon Chi
 */
@RunWith(Arquillian.class)
public class InvokerFilterContainerImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortletException {
		Bundle bundle = FrameworkUtil.getBundle(
			InvokerFilterContainerImplTest.class);

		_bundleContext = bundle.getBundleContext();

		String servletContextName =
			ServletContextClassLoaderPool.getServletContextName(
				PortalClassLoaderUtil.getClassLoader());

		PortletAppImpl portletAppImpl = new PortletAppImpl(servletContextName);

		portletAppImpl.setWARFile(false);

		_portlet = new PortletImpl();

		_portlet.setPortletId("InvokerFilterContainerImplTest");
		_portlet.setPortletApp(portletAppImpl);
		_portlet.setPortletClass(MVCPortlet.class.getName());
		_portlet.setInitParams(
			Collections.singletonMap("template-path", "/META-INF/resources/"));

		_invokerFilterContainer =
			(InvokerFilterContainer)_portletInstanceFactory.create(
				_portlet, ServletContextPool.get(servletContextName), true);
	}

	@AfterClass
	public static void tearDownClass() {
		_portletInstanceFactory.destroy(_portlet);
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetEventFilters() {
		EventFilter eventFilter = _registerPortletFilter(EventFilter.class);

		List<EventFilter> eventFilters =
			_invokerFilterContainer.getEventFilters();

		Assert.assertTrue(
			"Target not found in " + eventFilters,
			_hasFilter(eventFilters, eventFilter));
	}

	@Test
	public void testGetRenderFilters() {
		RenderFilter renderFilter = _registerPortletFilter(RenderFilter.class);

		List<RenderFilter> renderFilters =
			_invokerFilterContainer.getRenderFilters();

		Assert.assertTrue(
			"Target not found in " + renderFilters,
			_hasFilter(renderFilters, renderFilter));
	}

	@Test
	public void testGetResourceFilters() {
		ResourceFilter resourceFilter = _registerPortletFilter(
			ResourceFilter.class);

		List<ResourceFilter> resourceFilters =
			_invokerFilterContainer.getResourceFilters();

		Assert.assertTrue(
			"Target not found in " + resourceFilters,
			_hasFilter(resourceFilters, resourceFilter));
	}

	@Test
	public void testInitAndGetActionFilters() {
		boolean[] calledInit = {false};

		ActionFilter actionFilter = (ActionFilter)ProxyUtil.newProxyInstance(
			ActionFilter.class.getClassLoader(),
			new Class<?>[] {ActionFilter.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "equals")) {
					return false;
				}

				if (Objects.equals(method.getName(), "hashcode")) {
					return 0;
				}

				if (Objects.equals(method.getName(), "init")) {
					calledInit[0] = true;
				}

				return null;
			});

		_registerPortletFilter(actionFilter, false);

		Assert.assertTrue(calledInit[0]);

		List<ActionFilter> actionFilters =
			_invokerFilterContainer.getActionFilters();

		Assert.assertTrue(
			"Target not found in " + actionFilters,
			_hasFilter(actionFilters, actionFilter));
	}

	private boolean _hasFilter(
		List<? extends PortletFilter> portletFilters,
		PortletFilter portletFilter) {

		for (PortletFilter currentPortletFilter : portletFilters) {
			if (currentPortletFilter == portletFilter) {
				return true;
			}
		}

		return false;
	}

	private <T> T _registerPortletFilter(Class<T> clazz) {
		T portletFilter = (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "equals")) {
					return false;
				}

				if (Objects.equals(method.getName(), "hashcode")) {
					return 0;
				}

				return null;
			});

		_registerPortletFilter((PortletFilter)portletFilter, true);

		return portletFilter;
	}

	private void _registerPortletFilter(
		PortletFilter portletFilter, boolean preinitialized) {

		_serviceRegistration = _bundleContext.registerService(
			PortletFilter.class, portletFilter,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.name", "InvokerFilterContainerImplTest"
			).put(
				"preinitialized.filter", Boolean.valueOf(preinitialized)
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build());
	}

	private static BundleContext _bundleContext;
	private static InvokerFilterContainer _invokerFilterContainer;
	private static Portlet _portlet;

	@Inject
	private static PortletInstanceFactory _portletInstanceFactory;

	private ServiceRegistration<?> _serviceRegistration;

}