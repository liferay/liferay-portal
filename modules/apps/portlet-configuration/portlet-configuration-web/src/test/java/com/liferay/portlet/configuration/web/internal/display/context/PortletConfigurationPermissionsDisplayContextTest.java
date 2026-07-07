/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.display.context;

import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PortletConfigurationPermissionsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_portletConfigurationPermissionsDisplayContext = Mockito.mock(
			PortletConfigurationPermissionsDisplayContext.class,
			Mockito.CALLS_REAL_METHODS);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		ReflectionTestUtil.setFieldValue(
			_portletConfigurationPermissionsDisplayContext, "_themeDisplay",
			themeDisplay);
	}

	@Test
	public void testGetSubtype() {
		ReflectionTestUtil.setFieldValue(
			_portletConfigurationPermissionsDisplayContext, "_group", null);

		Assert.assertNull(_invokeGetSubtype());

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			false
		);

		ReflectionTestUtil.setFieldValue(
			_portletConfigurationPermissionsDisplayContext, "_group", group);

		Assert.assertNull(_invokeGetSubtype());

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			true
		);

		try (MockedStatic<DepotRoleUtil> depotRoleUtilMockedStatic =
				Mockito.mockStatic(DepotRoleUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.anyString())
			).thenReturn(
				false
			);

			Assert.assertNull(_invokeGetSubtype());

			depotRoleUtilMockedStatic.when(
				() -> DepotRoleUtil.getSubtype(_GROUP_ID)
			).thenReturn(
				"space"
			);

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-17564")
			).thenReturn(
				true
			);

			Assert.assertNull(_invokeGetSubtype());

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-96750")
			).thenReturn(
				true
			);

			Assert.assertEquals("space", _invokeGetSubtype());
		}
	}

	private String _invokeGetSubtype() {
		return ReflectionTestUtil.invoke(
			_portletConfigurationPermissionsDisplayContext, "_getSubtype",
			new Class<?>[0]);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private PortletConfigurationPermissionsDisplayContext
		_portletConfigurationPermissionsDisplayContext;

}