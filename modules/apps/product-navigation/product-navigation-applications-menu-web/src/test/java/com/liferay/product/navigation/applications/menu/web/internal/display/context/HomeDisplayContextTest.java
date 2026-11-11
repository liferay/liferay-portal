/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class HomeDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_homeDisplayContext = Mockito.mock(
			HomeDisplayContext.class, Mockito.CALLS_REAL_METHODS);

		_panelAppRegistry = Mockito.mock(PanelAppRegistry.class);

		ReflectionTestUtil.setFieldValue(
			_homeDisplayContext, "_panelAppRegistry", _panelAppRegistry);

		ReflectionTestUtil.setFieldValue(
			_homeDisplayContext, "_themeDisplay", new ThemeDisplay());
	}

	@Test
	public void testGetPropsItems() throws Exception {
		_testGetPropsItems("/" + RandomTestUtil.randomString(), true);
		_testGetPropsItems("data:image/png;base64,iVBORw0KGgo=", true);
		_testGetPropsItems(
			"data:image/svg+xml,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org" +
				"%2F2000%2Fsvg%22%2F%3E",
			true);
		_testGetPropsItems(
			"data:image/svg+xml;charset=utf-8,%3Csvg%2F%3E", true);
		_testGetPropsItems(
			"https://" + RandomTestUtil.randomString() + ".com/icon.png", true);

		_testGetPropsItems("data:text/html,<script>alert(1)</script>", false);
		_testGetPropsItems(
			"https://" + RandomTestUtil.randomString() +
				".com/\"onerror=\"alert(1)",
			false);
		_testGetPropsItems("javascript:alert(1)", false);
		_testGetPropsItems(null, false);
	}

	private void _testGetPropsItems(String iconURL, boolean valid)
		throws Exception {

		PanelApp panelApp = Mockito.mock(PanelApp.class);

		Mockito.when(
			panelApp.getIconURL()
		).thenReturn(
			iconURL
		);

		Mockito.when(
			panelApp.getPortletURL(Mockito.nullable(HttpServletRequest.class))
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				Mockito.nullable(String.class),
				Mockito.nullable(PermissionChecker.class),
				Mockito.nullable(Group.class))
		).thenReturn(
			List.of(panelApp)
		);

		List<Map<String, Object>> propsItems = ReflectionTestUtil.invoke(
			_homeDisplayContext, "_getPropsItems",
			new Class<?>[] {PanelCategory.class},
			Mockito.mock(PanelCategory.class));

		Map<String, Object> propsItem = propsItems.get(0);

		if (valid) {
			Assert.assertEquals(iconURL, propsItem.get("iconURL"));
		}
		else {
			Assert.assertFalse(propsItem.containsKey("iconURL"));
		}
	}

	private HomeDisplayContext _homeDisplayContext;
	private PanelAppRegistry _panelAppRegistry;

}