/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.exception.RoleSubtypeException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class DepotRoleLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddRole() throws Exception {
		DepotRoleLocalServiceWrapper depotRoleLocalServiceWrapper =
			new DepotRoleLocalServiceWrapper();

		depotRoleLocalServiceWrapper.setWrappedService(
			Mockito.mock(RoleLocalService.class));

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			_assertAddRole(
				depotRoleLocalServiceWrapper, DepotRolesConstants.SUBTYPE_SPACE,
				RoleConstants.TYPE_DEPOT, true);
			_assertAddRole(
				depotRoleLocalServiceWrapper,
				DepotRolesConstants.SUBTYPE_PROJECT, RoleConstants.TYPE_DEPOT,
				true);
			_assertAddRole(
				depotRoleLocalServiceWrapper,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
				RoleConstants.TYPE_DEPOT, false);
			_assertAddRole(
				depotRoleLocalServiceWrapper, RandomTestUtil.randomString(),
				RoleConstants.TYPE_DEPOT, false);

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-57283"))
			).thenReturn(
				true
			);

			_assertAddRole(
				depotRoleLocalServiceWrapper,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
				RoleConstants.TYPE_DEPOT, true);
			_assertAddRole(
				depotRoleLocalServiceWrapper, RandomTestUtil.randomString(),
				RoleConstants.TYPE_DEPOT, false);
		}
	}

	private void _assertAddRole(
			DepotRoleLocalServiceWrapper depotRoleLocalServiceWrapper,
			String subtype, int type, boolean valid)
		throws Exception {

		try {
			depotRoleLocalServiceWrapper.addRole(
				null, 0, null, 0, RandomTestUtil.randomString(), null, null,
				type, subtype, null);

			if (!valid) {
				Assert.fail();
			}
		}
		catch (RoleSubtypeException roleSubtypeException) {
			if (valid) {
				throw roleSubtypeException;
			}
		}
	}

}