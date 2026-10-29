/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DepotItemSelectorViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
	}

	@Test
	public void testIsVisible() {
		Assert.assertTrue(
			_assetLibraryDepotItemSelectorView.isVisible(null, null));
		Assert.assertTrue(
			_spacesDepotItemSelectorView.isVisible(null, _themeDisplay));
	}

	@Test
	public void testIsVisibleForASupportedApplication() {
		_assertIsVisible(AssetListPortletKeys.ASSET_LIST, true);
		_assertIsVisible(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, true);
		_assertIsVisible(JournalPortletKeys.JOURNAL, true);
	}

	@Test
	public void testIsVisibleForAnUnsupportedApplication() {
		_assertIsVisible(
			"com_liferay_blogs_web_portlet_BlogsAdminPortlet", false);
		_assertIsVisible(RandomTestUtil.randomString(), false);
	}

	private void _assertIsVisible(String portletId, boolean visible) {
		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setPortletId(portletId);

		Assert.assertEquals(
			visible,
			_assetLibraryDepotItemSelectorView.isVisible(
				groupItemSelectorCriterion, null));
		Assert.assertEquals(
			visible,
			_spacesDepotItemSelectorView.isVisible(
				groupItemSelectorCriterion, _themeDisplay));
	}

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.item.selector.AssetLibraryDepotItemSelectorView"
	)
	private ItemSelectorView<GroupItemSelectorCriterion>
		_assetLibraryDepotItemSelectorView;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.item.selector.SpacesDepotItemSelectorView"
	)
	private ItemSelectorView<GroupItemSelectorCriterion>
		_spacesDepotItemSelectorView;

	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}