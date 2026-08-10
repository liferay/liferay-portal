/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.instance.lifecycle;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jan Brychta
 */
public class CMSPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-98561")
	public void testPortalInstanceRegisteredDisablesUserLayoutsWhenNotAlreadySet()
		throws Exception {

		PortletPreferences portletPreferences = _getPortletPreferences();

		_portalInstanceRegistered();

		Mockito.verify(
			portletPreferences
		).setValue(
			PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED, "false"
		);

		Mockito.verify(
			portletPreferences
		).setValue(
			PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED, "false"
		);

		Mockito.verify(
			portletPreferences
		).store();
	}

	@Test
	@TestInfo("LPD-98561")
	public void testPortalInstanceRegisteredDoesNotOverrideUserLayoutsWhenAlreadySet()
		throws Exception {

		PortletPreferences portletPreferences = _getPortletPreferences();

		Mockito.when(
			portletPreferences.getValue(
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED, null)
		).thenReturn(
			"true"
		);

		Mockito.when(
			portletPreferences.getValue(
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED, null)
		).thenReturn(
			"true"
		);

		_portalInstanceRegistered();

		Mockito.verify(
			portletPreferences, Mockito.never()
		).setValue(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.verify(
			portletPreferences, Mockito.never()
		).store();
	}

	private PortletPreferences _getPortletPreferences() {
		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		PrefsProps prefsProps = Mockito.mock(PrefsProps.class);

		Mockito.when(
			prefsProps.getPreferences(Mockito.anyLong())
		).thenReturn(
			portletPreferences
		);

		ReflectionTestUtil.setFieldValue(
			PrefsPropsUtil.class, "_prefsProps", prefsProps);

		return portletPreferences;
	}

	private void _portalInstanceRegistered() throws Exception {
		CMSPortalInstanceLifecycleListener cmsPortalInstanceLifecycleListener =
			new CMSPortalInstanceLifecycleListener();

		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		Mockito.when(
			configurationAdmin.createFactoryConfiguration(
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			Mockito.mock(Configuration.class)
		);

		ReflectionTestUtil.setFieldValue(
			cmsPortalInstanceLifecycleListener, "_configurationAdmin",
			configurationAdmin);

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			1L
		);

		cmsPortalInstanceLifecycleListener.portalInstanceRegistered(company);
	}

}