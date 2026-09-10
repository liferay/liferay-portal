/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.test.util.BasePanelAppNavigationItemsTestCase;
import com.liferay.change.tracking.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class PublicationsPanelAppNavigationItemsTest
	extends BasePanelAppNavigationItemsTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ReflectionTestUtil.setFieldValue(
			_publicationsPanelApp, "_ctSettingsConfigurationHelper",
			_ctSettingsConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_publicationsPanelApp, "_language", language);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		Mockito.when(
			_ctSettingsConfigurationHelper.isEnabled(Mockito.anyLong())
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_publicationsPanelApp.getPanelAppNavigationItems(
				httpServletRequest);

		Assert.assertFalse(
			panelAppNavigationItems.toString(),
			panelAppNavigationItems.isEmpty());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		assertCanonicalName("ongoing", panelAppNavigationItem);
		assertParameterValue(
			"/change_tracking/view_publications", panelAppNavigationItem,
			"mvcRenderCommandName");

		panelAppNavigationItem = panelAppNavigationItems.get(
			panelAppNavigationItems.size() - 1);

		assertCanonicalName("history", panelAppNavigationItem);
		assertParameterValue(
			"/change_tracking/view_history", panelAppNavigationItem,
			"mvcRenderCommandName");
	}

	@Test
	public void testGetPanelAppNavigationItemsIsEmptyWithoutChangeTracking()
		throws Exception {

		Mockito.when(
			_ctSettingsConfigurationHelper.isEnabled(Mockito.anyLong())
		).thenReturn(
			false
		);

		Assert.assertTrue(
			_publicationsPanelApp.getPanelAppNavigationItems(
				httpServletRequest
			).isEmpty());
	}

	private final CTSettingsConfigurationHelper _ctSettingsConfigurationHelper =
		Mockito.mock(CTSettingsConfigurationHelper.class);

	private final PublicationsPanelApp _publicationsPanelApp =
		new PublicationsPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

}