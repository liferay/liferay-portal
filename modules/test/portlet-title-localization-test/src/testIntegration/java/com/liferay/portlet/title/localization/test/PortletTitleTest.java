/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.title.localization.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class PortletTitleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPortletTitles() throws Exception {
		List<String> portletIdsWithMissingTitles = new ArrayList<>();

		for (Portlet portlet : PortletLocalServiceUtil.getPortlets()) {
			String rootPortletId = portlet.getRootPortletId();

			PortletBag portletBag = PortletBagPool.get(rootPortletId);

			PortletConfig portletConfig = PortletConfigFactoryUtil.create(
				portlet, portletBag.getServletContext());

			ResourceBundle resourceBundle = portletConfig.getResourceBundle(
				LocaleUtil.getDefault());

			if ((resourceBundle != null) &&
				!resourceBundle.containsKey(
					"jakarta.portlet.title.".concat(rootPortletId)) &&
				!StringUtil.startsWith(
					rootPortletId,
					"com_liferay_object_web_internal_object_definitions_" +
						"portlet_ObjectDefinitionsPortlet")) {

				portletIdsWithMissingTitles.add(rootPortletId);
			}
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Please update the Language.properties files for the ",
				"following portlets: ", portletIdsWithMissingTitles),
			portletIdsWithMissingTitles.isEmpty());
	}

}