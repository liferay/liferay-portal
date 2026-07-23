/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

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
public class EditRolePermissionsNavigationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		_role = Mockito.mock(Role.class);

		_editRolePermissionsNavigationDisplayContext =
			new EditRolePermissionsNavigationDisplayContext(
				httpServletRequest, Mockito.mock(RenderResponse.class), _role,
				false);
	}

	@Test
	public void testHasObjectDefinitionValidDomain() {
		Mockito.when(
			_role.getSubtype()
		).thenReturn(
			null
		);

		Mockito.when(
			_role.getType()
		).thenReturn(
			RoleConstants.TYPE_DEPOT
		);

		Assert.assertTrue(
			_invokeHasObjectDefinitionValidDomain(
				Mockito.mock(ObjectDefinition.class)));

		Mockito.when(
			_role.getSubtype()
		).thenReturn(
			"space"
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<ObjectDefinitionSettingUtil>
				objectDefinitionSettingUtilMockedStatic = Mockito.mockStatic(
					ObjectDefinitionSettingUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.anyString())
			).thenReturn(
				false
			);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					Mockito.mock(ObjectDefinition.class)));

			objectDefinitionSettingUtilMockedStatic.when(
				() -> ObjectDefinitionSettingUtil.getValue(
					Mockito.eq(ObjectDefinitionSettingConstants.NAME_DOMAIN),
					Mockito.any())
			).thenReturn(
				null
			);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					Mockito.mock(ObjectDefinition.class)));

			objectDefinitionSettingUtilMockedStatic.when(
				() -> ObjectDefinitionSettingUtil.getValue(
					Mockito.eq(ObjectDefinitionSettingConstants.NAME_DOMAIN),
					Mockito.any())
			).thenReturn(
				"project"
			);

			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					Mockito.mock(ObjectDefinition.class)));

			objectDefinitionSettingUtilMockedStatic.when(
				() -> ObjectDefinitionSettingUtil.getValue(
					Mockito.eq(ObjectDefinitionSettingConstants.NAME_DOMAIN),
					Mockito.any())
			).thenReturn(
				"space"
			);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					Mockito.mock(ObjectDefinition.class)));
		}

		Mockito.when(
			_role.getSubtype()
		).thenReturn(
			"design-library"
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<ObjectDefinitionSettingUtil>
				objectDefinitionSettingUtilMockedStatic = Mockito.mockStatic(
					ObjectDefinitionSettingUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-57283")
			).thenReturn(
				true
			);

			objectDefinitionSettingUtilMockedStatic.when(
				() -> ObjectDefinitionSettingUtil.getValue(
					Mockito.eq(ObjectDefinitionSettingConstants.NAME_DOMAIN),
					Mockito.any())
			).thenReturn(
				"project"
			);

			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					Mockito.mock(ObjectDefinition.class)));

			objectDefinitionSettingUtilMockedStatic.when(
				() -> ObjectDefinitionSettingUtil.getValue(
					Mockito.eq(ObjectDefinitionSettingConstants.NAME_DOMAIN),
					Mockito.any())
			).thenReturn(
				"design-library"
			);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					Mockito.mock(ObjectDefinition.class)));
		}

		Mockito.when(
			_role.getSubtype()
		).thenReturn(
			null
		);

		Mockito.when(
			_role.getType()
		).thenReturn(
			RoleConstants.TYPE_REGULAR
		);

		Assert.assertTrue(
			_invokeHasObjectDefinitionValidDomain(
				Mockito.mock(ObjectDefinition.class)));
	}

	private boolean _invokeHasObjectDefinitionValidDomain(
		ObjectDefinition objectDefinition) {

		return ReflectionTestUtil.invoke(
			_editRolePermissionsNavigationDisplayContext,
			"_hasObjectDefinitionValidDomain",
			new Class<?>[] {ObjectDefinition.class}, objectDefinition);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private EditRolePermissionsNavigationDisplayContext
		_editRolePermissionsNavigationDisplayContext;
	private Role _role;

}