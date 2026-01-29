/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.display.context.logic.test;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.display.context.logic.test.constants.ApplicationsMenuTestPortletKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class PanelCategoryHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);
	}

	@Test
	public void testGetPanelCategory() {
		PanelCategory panelCategory = _panelCategoryHelper.getPanelCategory(
			ApplicationsMenuTestPortletKeys.APPLICATIONS_MENU_TEST_PORTLET);

		Assert.assertEquals(
			PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS,
			panelCategory.getKey());
	}

	@Test
	public void testIsApplicationsMenuApp() {
		Assert.assertTrue(
			_panelCategoryHelper.isApplicationsMenuApp(
				ApplicationsMenuTestPortletKeys.
					APPLICATIONS_MENU_TEST_PORTLET));
	}

	@Inject
	private PanelAppRegistry _panelAppRegistry;

	private PanelCategoryHelper _panelCategoryHelper;

}