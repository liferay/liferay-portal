/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.application.list.test;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsPortletKeys;

import java.util.Collection;
import java.util.Objects;

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
import org.osgi.framework.ServiceReference;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SegmentsPanelAppTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(SegmentsPanelAppTest.class);

		_bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<PanelApp>> serviceReferences =
			_bundleContext.getServiceReferences(
				PanelApp.class,
				"(panel.category.key=site_administration.members)");

		Assert.assertTrue(serviceReferences.size() > 1);

		for (ServiceReference<PanelApp> serviceReference : serviceReferences) {
			PanelApp panelApp = _bundleContext.getService(serviceReference);

			Class<? extends PanelApp> clazz = panelApp.getClass();

			if (!Objects.equals(
					clazz.getCanonicalName(),
					"com.liferay.segments.web.internal.application.list." +
						"SegmentsPanelApp")) {

				continue;
			}

			_serviceReference = serviceReference;

			_panelApp = panelApp;
		}

		Assert.assertNotNull(_panelApp);

		ReflectionTestUtil.setFieldValue(
			_panelApp, "_portletLocalService", _portletLocalService);
	}

	@After
	public void tearDown() throws Exception {
		_bundleContext.ungetService(_serviceReference);
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(
			"com.liferay.segments.web.internal.application.list." +
				"SegmentsPanelApp",
			_panelApp.getKey());
	}

	@Test
	public void testGetLabel() {
		Assert.assertEquals("Segments", _panelApp.getLabel(LocaleUtil.US));
	}

	@Test
	public void testGetPanelCategoryKey() {
		Assert.assertEquals(
			PanelCategoryKeys.SITE_ADMINISTRATION_MEMBERS,
			_serviceReference.getProperty("panel.category.key"));
	}

	@Test
	public void testGetPortletId() {
		Assert.assertEquals(
			SegmentsPortletKeys.SEGMENTS, _panelApp.getPortletId());
	}

	@Test
	public void testIsShow() throws PortalException {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		Assert.assertTrue(
			_panelApp.isShow(
				permissionChecker,
				_groupLocalService.getGroup(TestPropsValues.getGroupId())));
	}

	@Test
	public void testIsShowWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser(TestPropsValues.getGroupId());

		try {
			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			Assert.assertFalse(
				_panelApp.isShow(
					permissionChecker,
					_groupLocalService.getGroup(TestPropsValues.getGroupId())));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private BundleContext _bundleContext;

	@Inject
	private GroupLocalService _groupLocalService;

	private PanelApp _panelApp;

	@Inject
	private PortletLocalService _portletLocalService;

	private ServiceReference<PanelApp> _serviceReference;

	@Inject
	private UserLocalService _userLocalService;

}