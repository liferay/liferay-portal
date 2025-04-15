/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import jakarta.portlet.Portlet;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Raymond Augé
 */
public abstract class BasePortletContainerTestCase {

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		layout = LayoutTestUtil.addTypePortletLayout(group);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		testPortlet = new TestPortlet();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(null);

		for (ServiceRegistration<?> serviceRegistration :
				serviceRegistrations) {

			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();
	}

	protected void registerService(
		Class<?> clazz, Object object, Dictionary<String, Object> properties) {

		Assert.assertNotNull(properties);

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				new String[] {Object.class.getName(), clazz.getName()}, object,
				properties);

		serviceRegistrations.add(serviceRegistration);
	}

	protected void setUpPortlet(
			Portlet portlet, Dictionary<String, Object> properties,
			String portletName)
		throws Exception {

		setUpPortlet(portlet, properties, portletName, true);
	}

	protected void setUpPortlet(
			Portlet portlet, Dictionary<String, Object> properties,
			String portletName, boolean addToLayout)
		throws Exception {

		properties.put("jakarta.portlet.name", portletName);

		registerService(Portlet.class, portlet, properties);

		if (addToLayout) {
			LayoutTestUtil.addPortletToLayout(
				TestPropsValues.getUserId(), layout, portletName, "column-1",
				new HashMap<String, String[]>());
		}
	}

	protected static final String TEST_PORTLET_ID = "TEST_PORTLET_ID";

	@DeleteAfterTestRun
	protected Group group;

	protected Layout layout;
	protected List<ServiceRegistration<?>> serviceRegistrations =
		new CopyOnWriteArrayList<>();
	protected TestPortlet testPortlet;

}