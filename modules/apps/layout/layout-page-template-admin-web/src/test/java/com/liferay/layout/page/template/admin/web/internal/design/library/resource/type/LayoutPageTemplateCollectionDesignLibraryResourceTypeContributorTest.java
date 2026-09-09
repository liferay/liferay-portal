/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class
	LayoutPageTemplateCollectionDesignLibraryResourceTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_depotEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		ReflectionTestUtil.setFieldValue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@Test
	@TestInfo("LPD-104840")
	public void testHasAddPermission() {
		Assert.assertFalse(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateCollectionPermission();

		Assert.assertTrue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));
	}

	@Test
	@TestInfo("LPD-104840")
	public void testHasViewPermission() {
		Assert.assertFalse(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateCollectionPermission();

		Assert.assertTrue(
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));
	}

	private void _setUpAddLayoutPageTemplateCollectionPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION)
		).thenReturn(
			true
		);
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final
		LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor
			_layoutPageTemplateCollectionDesignLibraryResourceTypeContributor =
				new LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}