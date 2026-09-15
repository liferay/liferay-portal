/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.RoleSubtypeException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class DepotRoleUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFilter() {
		Role role1 = _mockRole(DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY);
		Role role2 = _mockRole(DepotRolesConstants.SUBTYPE_PROJECT);
		Role role3 = _mockRole(DepotRolesConstants.SUBTYPE_SPACE);
		Role role4 = _mockRole(null);
		Role role5 = _mockRole("");

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				false
			);

			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					_mockDepotEntry(DepotConstants.TYPE_PROJECT),
					Arrays.asList(role1, role2, role3, role4, role5)));
			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5),
					DepotRolesConstants.SUBTYPE_SPACE));

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					(DepotEntry)null,
					Arrays.asList(role1, role2, role3, role4, role5)));
			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					_mockDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY),
					Arrays.asList(role1, role2, role3, role4, role5)));
			Assert.assertEquals(
				Arrays.asList(role1),
				DepotRoleUtil.filter(
					_mockDepotEntry(DepotConstants.TYPE_DESIGN_LIBRARY),
					Arrays.asList(role1, role2, role3, role4, role5)));
			Assert.assertEquals(
				Arrays.asList(role2),
				DepotRoleUtil.filter(
					_mockDepotEntry(DepotConstants.TYPE_PROJECT),
					Arrays.asList(role1, role2, role3, role4, role5)));
			Assert.assertEquals(
				Arrays.asList(role3, role4, role5),
				DepotRoleUtil.filter(
					_mockDepotEntry(DepotConstants.TYPE_SPACE),
					Arrays.asList(role1, role2, role3, role4, role5)));
		}
	}

	@Test
	public void testFilterBySubtype() {
		Role role1 = _mockRole(DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY);
		Role role2 = _mockRole(DepotRolesConstants.SUBTYPE_PROJECT);
		Role role3 = _mockRole(DepotRolesConstants.SUBTYPE_SPACE);
		Role role4 = _mockRole(null);
		Role role5 = _mockRole("");

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				false
			);

			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5),
					DepotRolesConstants.SUBTYPE_PROJECT));
			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5),
					DepotRolesConstants.SUBTYPE_SPACE));

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5), null));
			Assert.assertEquals(
				Arrays.asList(role1, role2, role3, role4, role5),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5), ""));
			Assert.assertEquals(
				Arrays.asList(role1),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5),
					DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY));
			Assert.assertEquals(
				Arrays.asList(role2),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5),
					DepotRolesConstants.SUBTYPE_PROJECT));
			Assert.assertEquals(
				Arrays.asList(role3, role4, role5),
				DepotRoleUtil.filter(
					Arrays.asList(role1, role2, role3, role4, role5),
					DepotRolesConstants.SUBTYPE_SPACE));
		}
	}

	@Test
	public void testValidate() throws Exception {
		_testValidateWithAssetLibraryDepotEntry();
		_testValidateWithDesignLibraryDepotEntry();
		_testValidateWithDisabledFeatureFlag();
		_testValidateWithProjectDepotEntry();
		_testValidateWithSiteGroup();
		_testValidateWithSiteRole();
		_testValidateWithSpaceDepotEntry();
	}

	private DepotEntry _mockDepotEntry(int depotType) {
		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			depotType
		);

		return depotEntry;
	}

	private void _mockDepotEntry(
		MockedStatic<DepotEntryLocalServiceUtil>
			depotEntryLocalServiceUtilMockedStatic,
		int depotType, long groupId) {

		DepotEntry depotEntry = _mockDepotEntry(depotType);

		depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(groupId)
		).thenReturn(
			depotEntry
		);
	}

	private long _mockRole(
		MockedStatic<RoleLocalServiceUtil> roleLocalServiceUtilMockedStatic,
		String subtype, int type) {

		Role role = _mockRole(subtype);
		long roleId = RandomTestUtil.randomLong();

		Mockito.when(
			role.getRoleId()
		).thenReturn(
			roleId
		);

		Mockito.when(
			role.getType()
		).thenReturn(
			type
		);

		roleLocalServiceUtilMockedStatic.when(
			() -> RoleLocalServiceUtil.getRole(roleId)
		).thenReturn(
			role
		);

		return roleId;
	}

	private Role _mockRole(String subtype) {
		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getSubtype()
		).thenReturn(
			subtype
		);

		return role;
	}

	private void _testValidateWithAssetLibraryDepotEntry() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<RoleLocalServiceUtil>
				roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RoleLocalServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic,
				DepotConstants.TYPE_ASSET_LIBRARY, _GROUP_ID_ASSET_LIBRARY);

			DepotRoleUtil.validate(
				_GROUP_ID_ASSET_LIBRARY,
				new long[] {
					_mockRole(
						roleLocalServiceUtilMockedStatic,
						DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
						RoleConstants.TYPE_DEPOT),
					_mockRole(
						roleLocalServiceUtilMockedStatic,
						DepotRolesConstants.SUBTYPE_PROJECT,
						RoleConstants.TYPE_DEPOT),
					_mockRole(
						roleLocalServiceUtilMockedStatic,
						DepotRolesConstants.SUBTYPE_SPACE,
						RoleConstants.TYPE_DEPOT),
					_mockRole(
						roleLocalServiceUtilMockedStatic, null,
						RoleConstants.TYPE_DEPOT)
				});
		}
	}

	private void _testValidateWithDesignLibraryDepotEntry() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<RoleLocalServiceUtil>
				roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RoleLocalServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic,
				DepotConstants.TYPE_DESIGN_LIBRARY, _GROUP_ID_DESIGN_LIBRARY);

			DepotRoleUtil.validate(
				_GROUP_ID_DESIGN_LIBRARY,
				new long[] {
					_mockRole(
						roleLocalServiceUtilMockedStatic,
						DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
						RoleConstants.TYPE_DEPOT)
				});

			Assert.assertThrows(
				RoleSubtypeException.class,
				() -> DepotRoleUtil.validate(
					_GROUP_ID_DESIGN_LIBRARY,
					new long[] {
						_mockRole(
							roleLocalServiceUtilMockedStatic,
							DepotRolesConstants.SUBTYPE_PROJECT,
							RoleConstants.TYPE_DEPOT)
					}));

			Assert.assertThrows(
				RoleSubtypeException.class,
				() -> DepotRoleUtil.validate(
					_GROUP_ID_DESIGN_LIBRARY,
					new long[] {
						_mockRole(
							roleLocalServiceUtilMockedStatic, null,
							RoleConstants.TYPE_DEPOT)
					}));
		}
	}

	private void _testValidateWithDisabledFeatureFlag() throws Exception {
		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				false
			);

			DepotRoleUtil.validate(
				RandomTestUtil.randomLong(),
				new long[] {RandomTestUtil.randomLong()});
		}
	}

	private void _testValidateWithProjectDepotEntry() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<RoleLocalServiceUtil>
				roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RoleLocalServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic,
				DepotConstants.TYPE_PROJECT, _GROUP_ID_PROJECT);

			long projectRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic,
				DepotRolesConstants.SUBTYPE_PROJECT, RoleConstants.TYPE_DEPOT);

			DepotRoleUtil.validate(
				_GROUP_ID_PROJECT, new long[] {projectRoleId});

			long designLibraryRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
				RoleConstants.TYPE_DEPOT);

			Assert.assertThrows(
				RoleSubtypeException.class,
				() -> DepotRoleUtil.validate(
					_GROUP_ID_PROJECT, new long[] {designLibraryRoleId}));

			Assert.assertThrows(
				RoleSubtypeException.class,
				() -> DepotRoleUtil.validate(
					_GROUP_ID_PROJECT,
					new long[] {designLibraryRoleId, projectRoleId}));
		}
	}

	private void _testValidateWithSiteGroup() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			DepotRoleUtil.validate(
				RandomTestUtil.randomLong(),
				new long[] {RandomTestUtil.randomLong()});
		}
	}

	private void _testValidateWithSiteRole() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<RoleLocalServiceUtil>
				roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RoleLocalServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic,
				DepotConstants.TYPE_PROJECT, _GROUP_ID_PROJECT);

			DepotRoleUtil.validate(
				_GROUP_ID_PROJECT,
				new long[] {
					_mockRole(
						roleLocalServiceUtilMockedStatic,
						DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
						RoleConstants.TYPE_SITE)
				});
		}
	}

	private void _testValidateWithSpaceDepotEntry() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<RoleLocalServiceUtil>
				roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RoleLocalServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic,
				DepotConstants.TYPE_SPACE, _GROUP_ID_SPACE);

			DepotRoleUtil.validate(
				_GROUP_ID_SPACE,
				new long[] {
					_mockRole(
						roleLocalServiceUtilMockedStatic,
						DepotRolesConstants.SUBTYPE_SPACE,
						RoleConstants.TYPE_DEPOT),
					_mockRole(
						roleLocalServiceUtilMockedStatic, null,
						RoleConstants.TYPE_DEPOT)
				});

			Assert.assertThrows(
				RoleSubtypeException.class,
				() -> DepotRoleUtil.validate(
					_GROUP_ID_SPACE,
					new long[] {
						_mockRole(
							roleLocalServiceUtilMockedStatic,
							DepotRolesConstants.SUBTYPE_PROJECT,
							RoleConstants.TYPE_DEPOT)
					}));
		}
	}

	private static final long _GROUP_ID_ASSET_LIBRARY =
		RandomTestUtil.randomLong();

	private static final long _GROUP_ID_DESIGN_LIBRARY =
		RandomTestUtil.randomLong();

	private static final long _GROUP_ID_PROJECT = RandomTestUtil.randomLong();

	private static final long _GROUP_ID_SPACE = RandomTestUtil.randomLong();

}