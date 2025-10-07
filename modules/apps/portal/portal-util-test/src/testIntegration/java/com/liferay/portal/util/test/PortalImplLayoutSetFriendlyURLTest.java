/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Sierra
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
public class PortalImplLayoutSetFriendlyURLTest
	extends BasePortalImplURLTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAccessFromVirtualHost() throws Exception {
		Field field = ReflectionUtil.getDeclaredField(
			PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME");

		Object value = field.get(null);

		Group defaultGroup = GroupTestUtil.addGroup();

		try {
			field.set(null, defaultGroup.getName());

			Layout layout = LayoutTestUtil.addTypePortletLayout(defaultGroup);

			String friendlyURL = portal.getLayoutSetFriendlyURL(
				layout.getLayoutSet(),
				initThemeDisplay(
					company, group, publicLayout, LOCALHOST, VIRTUAL_HOSTNAME));

			Assert.assertFalse(friendlyURL, friendlyURL.contains(LOCALHOST));
		}
		finally {
			field.set(null, value);

			_groupLocalService.deleteGroup(defaultGroup);
		}
	}

	@Test
	public void testPreserveParameters() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, controlPanelLayout, VIRTUAL_HOSTNAME);

		themeDisplay.setDoAsUserId("impersonated");

		Assert.assertEquals(
			"impersonated",
			HttpComponentsUtil.getParameter(
				portal.getLayoutSetFriendlyURL(
					_layoutSetLocalService.getLayoutSet(
						group.getGroupId(), false),
					themeDisplay),
				"doAsUserId"));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

}