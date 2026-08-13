/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.design.library;

import com.liferay.depot.model.DepotEntry;
import com.liferay.fragment.constants.FragmentActionKeys;
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
 * @author Thiago Buarque
 */
public class FragmentDesignLibraryResourceTypeContributorTest {

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
			_fragmentDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@Test
	public void testHasAddPermission() {
		Assert.assertFalse(
			_fragmentDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));

		_setUpManageFragmentEntriesPermission();

		Assert.assertTrue(
			_fragmentDesignLibraryResourceTypeContributor.hasAddPermission(
				_permissionChecker, _depotEntry));
	}

	@Test
	public void testHasViewPermission() {
		Assert.assertFalse(
			_fragmentDesignLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry));

		_setUpManageFragmentEntriesPermission();

		Assert.assertTrue(
			_fragmentDesignLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry));
	}

	private void _setUpManageFragmentEntriesPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)
		).thenReturn(
			true
		);
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final FragmentDesignLibraryResourceTypeContributor
		_fragmentDesignLibraryResourceTypeContributor =
			new FragmentDesignLibraryResourceTypeContributor();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}