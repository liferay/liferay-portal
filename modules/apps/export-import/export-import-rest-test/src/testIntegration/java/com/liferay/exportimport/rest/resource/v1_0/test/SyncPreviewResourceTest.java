/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.rest.client.dto.v1_0.Choice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.SyncPreview;
import com.liferay.exportimport.rest.client.resource.v1_0.SyncPreviewResource;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 * @author Jaime Leon
 * @author Daniel Raposo
 */
@FeatureFlag("LPD-82107")
@RunWith(Arquillian.class)
public class SyncPreviewResourceTest extends BaseSyncPreviewResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_syncPreviewResource = SyncPreviewResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_userLocalService.deleteUser(_user);
	}

	@Override
	@Test
	public void testGetSiteTemplateSyncPreview() throws Exception {
		_testGetSiteTemplateSyncPreviewWithFeatureFlagDisabled();
		_testGetSiteTemplateSyncPreviewWithLookAndFeel();
		_testGetSiteTemplateSyncPreviewWithoutPermission();
		_testGetSiteTemplateSyncPreviewWithPermission();
		_testGetSiteTemplateSyncPreviewWithPrivateLayouts();
	}

	private Choice _getChoice(
		PortletDataHandler portletDataHandler, String name) {

		for (PortletDataHandlerControl portletDataHandlerControl :
				portletDataHandler.getPortletDataHandlerControls()) {

			if (!(portletDataHandlerControl instanceof
					PortletDataHandlerChoice)) {

				continue;
			}

			PortletDataHandlerChoice portletDataHandlerChoice =
				(PortletDataHandlerChoice)portletDataHandlerControl;

			for (Choice choice : portletDataHandlerChoice.getChoices()) {
				if (name.equals(choice.getName())) {
					return choice;
				}
			}
		}

		return null;
	}

	private PortletDataHandler _getPortletDataHandler(
		SyncPreview syncPreview, String name) {

		for (PortletDataHandler portletDataHandler :
				syncPreview.getPortletDataHandlers()) {

			if (name.equals(portletDataHandler.getName())) {
				return portletDataHandler;
			}
		}

		return null;
	}

	private void _testGetSiteTemplateSyncPreviewWithFeatureFlagDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-82107")) {

			assertHttpResponseStatusCode(
				404,
				syncPreviewResource.getSiteTemplateSyncPreviewHttpResponse(
					_layoutSetPrototype.getLayoutSetPrototypeId()));
		}
	}

	private void _testGetSiteTemplateSyncPreviewWithLookAndFeel()
		throws Exception {

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			syncPreviewResource.getSiteTemplateSyncPreview(
				_layoutSetPrototype.getLayoutSetPrototypeId()),
			"LOOK_AND_FEEL");

		Assert.assertEquals(
			"[THEME_REFERENCE, LOGO, LAYOUT_SET_SETTINGS]",
			Arrays.toString(
				TransformUtil.transform(
					portletDataHandler.getPortletDataHandlerControls(),
					PortletDataHandlerControl::getName, String.class)));
	}

	private void _testGetSiteTemplateSyncPreviewWithPermission()
		throws Exception {

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), LayoutSetPrototype.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_layoutSetPrototype.getLayoutSetPrototypeId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		assertHttpResponseStatusCode(
			200,
			_syncPreviewResource.getSiteTemplateSyncPreviewHttpResponse(
				_layoutSetPrototype.getLayoutSetPrototypeId()));
	}

	private void _testGetSiteTemplateSyncPreviewWithPrivateLayouts()
		throws Exception {

		Group group = _layoutSetPrototype.getGroup();

		LayoutTestUtil.addTypePortletLayout(group, true);
		LayoutTestUtil.addTypePortletLayout(group, true);

		PortletDataHandler portletDataHandler = _getPortletDataHandler(
			syncPreviewResource.getSiteTemplateSyncPreview(
				_layoutSetPrototype.getLayoutSetPrototypeId()),
			"PORTLET_DATA_" + LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS);

		Assert.assertNull(_getChoice(portletDataHandler, "public-pages"));

		Choice choice = _getChoice(portletDataHandler, "private-pages");

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, true),
			GetterUtil.getLong(choice.getAdditionCount()));
	}

	private void _testGetSiteTemplateSyncPreviewWithoutPermission()
		throws Exception {

		assertHttpResponseStatusCode(
			404,
			_syncPreviewResource.getSiteTemplateSyncPreviewHttpResponse(
				_layoutSetPrototype.getLayoutSetPrototypeId()));
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	private SyncPreviewResource _syncPreviewResource;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}