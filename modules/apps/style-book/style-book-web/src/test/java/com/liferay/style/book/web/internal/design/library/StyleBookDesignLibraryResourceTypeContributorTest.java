/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.design.library;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookActionKeys;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class StyleBookDesignLibraryResourceTypeContributorTest {

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
			_styleBookDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@Test
	public void testHasAddPermission() {
		Assert.assertFalse(
			_styleBookDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));

		_setUpManageStyleBookEntriesPermission();

		Assert.assertTrue(
			_styleBookDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));
	}

	@Test
	public void testHasViewPermission() {
		Assert.assertFalse(
			_styleBookDesignLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry));

		_setUpManageStyleBookEntriesPermission();

		Assert.assertTrue(
			_styleBookDesignLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry));
	}

	private void _setUpManageStyleBookEntriesPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES)
		).thenReturn(
			true
		);
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final StyleBookDesignLibraryResourceTypeContributor
		_styleBookDesignLibraryResourceTypeContributor =
			new StyleBookDesignLibraryResourceTypeContributor();

}