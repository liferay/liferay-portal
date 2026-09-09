/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class DisplayPageTemplateDesignLibraryResourceTypeContributorTest {

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
			_displayPageTemplateDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@Test
	public void testGetType() {
		Assert.assertEquals(
			String.valueOf(LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE),
			_displayPageTemplateDesignLibraryResourceTypeContributor.getType());
	}

	@Test
	public void testHasAddPermission() {
		Assert.assertFalse(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateEntryPermission();

		Assert.assertTrue(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));
	}

	@Test
	public void testHasViewPermission() {
		Assert.assertFalse(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));

		_setUpViewPermission();

		Assert.assertTrue(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));
	}

	private void _setUpAddLayoutPageTemplateEntryPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY)
		).thenReturn(
			true
		);
	}

	private void _setUpViewPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID, ActionKeys.VIEW)
		).thenReturn(
			true
		);
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final DisplayPageTemplateDesignLibraryResourceTypeContributor
		_displayPageTemplateDesignLibraryResourceTypeContributor =
			new DisplayPageTemplateDesignLibraryResourceTypeContributor();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}