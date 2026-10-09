/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.preferences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;

import java.util.Dictionary;

import org.junit.Assert;
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
 * @author Jorge Ferrer
 */
@RunWith(Arquillian.class)
public class PortletPreferencesFactoryImplTest
	extends BasePortletPreferencesTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetLayoutPortletSetup() throws Exception {
		String name = RandomTestUtil.randomString(20);
		String[] values = {RandomTestUtil.randomString(20)};

		addLayoutPortletPreferences(
			testLayout, testPortlet, getPortletPreferencesXML(name, values));

		PortletPreferences portletPreferences =
			portletPreferencesFactory.getLayoutPortletSetup(
				testLayout, _PORTLET_ID);

		Assert.assertArrayEquals(
			portletPreferences.getValues(name, null), values);
	}

	@Test
	public void testGetLayoutPortletSetupCustomizableColumn() throws Exception {
		long userId = RandomTestUtil.randomLong();

		long ownerId = userId;

		int ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
		String customizableColumnPortletId = PortletIdCodec.encode(
			_PORTLET_ID, userId, null);

		String name = RandomTestUtil.randomString(20);
		String[] values = {RandomTestUtil.randomString(20)};

		portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), ownerId, ownerType,
			testLayout.getPlid(), customizableColumnPortletId, testPortlet,
			getPortletPreferencesXML(name, values));

		PortletPreferences portletPreferences =
			portletPreferencesFactory.getLayoutPortletSetup(
				testLayout, customizableColumnPortletId);

		Assert.assertArrayEquals(
			portletPreferences.getValues(name, null), values);
	}

	@Test
	public void testGetPortalPreferences() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setSignedIn(true);
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Assert.assertSame(
			portletPreferencesFactory.getPortalPreferences(
				mockHttpServletRequest),
			portletPreferencesFactory.getPortalPreferences(
				mockHttpServletRequest));
	}

	@Test
	public void testGetStrictPortletSetupWithGroupEmbeddedPortlet()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			PortletPreferencesFactoryImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, String> portletProperties =
			MapUtil.singletonDictionary("jakarta.portlet.name", _PORTLET_ID);

		ServiceRegistration<Portlet> portletServiceRegistration =
			bundleContext.registerService(
				Portlet.class, new MVCPortlet(), portletProperties);

		ServiceRegistration<PortletDataHandler>
			portletDataHandlerServiceRegistration =
				bundleContext.registerService(
					PortletDataHandler.class,
					new BasePortletDataHandler() {
					},
					portletProperties);

		try {
			portletPreferencesFactory.getStrictPortletSetup(
				TestPropsValues.getCompanyId(), 0L, _PORTLET_ID);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}
		}
		finally {
			portletServiceRegistration.unregister();
			portletDataHandlerServiceRegistration.unregister();
		}
	}

	@Override
	protected String getPortletId() {
		return _PORTLET_ID;
	}

	private static final String _PORTLET_ID = RandomTestUtil.randomString(10);

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesFactoryImplTest.class);

}