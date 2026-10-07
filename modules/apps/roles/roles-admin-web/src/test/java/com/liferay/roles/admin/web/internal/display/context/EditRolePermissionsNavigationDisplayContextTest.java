/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
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
		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-96750")
			).thenReturn(
				false
			);

			_mockRole(RoleConstants.TYPE_DEPOT, null);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_PROJECT,
					ObjectDefinitionConstants.SCOPE_DEPOT));

			_mockRole(
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_PROJECT);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					null, ObjectDefinitionConstants.SCOPE_COMPANY));
			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_PROJECT,
					ObjectDefinitionConstants.SCOPE_DEPOT));
			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_SPACE,
					ObjectDefinitionConstants.SCOPE_DEPOT));

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-96750")
			).thenReturn(
				true
			);

			_mockRole(RoleConstants.TYPE_DEPOT, null);

			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					null, ObjectDefinitionConstants.SCOPE_COMPANY));
			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					null, ObjectDefinitionConstants.SCOPE_DEPOT));
			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_PROJECT,
					ObjectDefinitionConstants.SCOPE_DEPOT));
			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_SPACE,
					ObjectDefinitionConstants.SCOPE_DEPOT));

			_mockRole(
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_PROJECT);

			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					null, ObjectDefinitionConstants.SCOPE_DEPOT));
			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_PROJECT,
					ObjectDefinitionConstants.SCOPE_DEPOT));
			Assert.assertFalse(
				_invokeHasObjectDefinitionValidDomain(
					DepotRolesConstants.SUBTYPE_SPACE,
					ObjectDefinitionConstants.SCOPE_DEPOT));

			_mockRole(
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_SPACE);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					null, ObjectDefinitionConstants.SCOPE_DEPOT));

			_mockRole(RoleConstants.TYPE_REGULAR, null);

			Assert.assertTrue(
				_invokeHasObjectDefinitionValidDomain(
					null, ObjectDefinitionConstants.SCOPE_COMPANY));
		}
	}

	@Test
	public void testIsSiteAndAssetLibraryAdministrationPortlet() {
		String portletId = RandomTestUtil.randomString();

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-96750")
			).thenReturn(
				false
			);

			_mockRole(
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_PROJECT);

			Assert.assertTrue(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(portletId));

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(_COMPANY_ID, "LPD-96750")
			).thenReturn(
				true
			);

			_mockRole(RoleConstants.TYPE_DEPOT, null);

			Assert.assertTrue(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(portletId));

			_mockRole(
				RoleConstants.TYPE_DEPOT,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY);

			Assert.assertTrue(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(
					AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN));
			Assert.assertFalse(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(portletId));

			_mockRole(
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_PROJECT);

			Assert.assertFalse(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(
					AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN));
			Assert.assertTrue(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(
					DepotPortletKeys.DEPOT_SETTINGS));
			Assert.assertFalse(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(portletId));

			_mockRole(
				RoleConstants.TYPE_DEPOT, DepotRolesConstants.SUBTYPE_SPACE);

			Assert.assertTrue(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(portletId));

			_mockRole(
				RoleConstants.TYPE_REGULAR, RandomTestUtil.randomString());

			Assert.assertTrue(
				_invokeIsSiteAndAssetLibraryAdministrationPortlet(portletId));
		}
	}

	private boolean _invokeHasObjectDefinitionValidDomain(
		String domain, String scope) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getScope()
		).thenReturn(
			scope
		);

		try (MockedStatic<ObjectDefinitionSettingUtil>
				objectDefinitionSettingUtilMockedStatic = Mockito.mockStatic(
					ObjectDefinitionSettingUtil.class)) {

			objectDefinitionSettingUtilMockedStatic.when(
				() -> ObjectDefinitionSettingUtil.getValue(
					Mockito.eq(ObjectDefinitionSettingConstants.NAME_DOMAIN),
					Mockito.any())
			).thenReturn(
				domain
			);

			return ReflectionTestUtil.invoke(
				_editRolePermissionsNavigationDisplayContext,
				"_hasObjectDefinitionValidDomain",
				new Class<?>[] {ObjectDefinition.class}, objectDefinition);
		}
	}

	private boolean _invokeIsSiteAndAssetLibraryAdministrationPortlet(
		String portletId) {

		return ReflectionTestUtil.invoke(
			_editRolePermissionsNavigationDisplayContext,
			"_isSiteAndAssetLibraryAdministrationPortlet",
			new Class<?>[] {String.class}, portletId);
	}

	private void _mockRole(int type, String subtype) {
		Mockito.when(
			_role.getSubtype()
		).thenReturn(
			subtype
		);

		Mockito.when(
			_role.getType()
		).thenReturn(
			type
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private EditRolePermissionsNavigationDisplayContext
		_editRolePermissionsNavigationDisplayContext;
	private Role _role;

}