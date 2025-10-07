/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class UpdatePermissionsMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, _company.getCompanyId(), TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		_ownerRole = _roleLocalService.getRole(
			_company.getCompanyId(), RoleConstants.OWNER);
	}

	@Test
	public void testProcessAction1() throws Exception {
		int initialCTEntriesCount =
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				new MockLiferayPortletActionRequest();

			mockLiferayPortletActionRequest.setAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE,
				new MockLiferayPortletActionResponse());
			mockLiferayPortletActionRequest.setAttribute(
				WebKeys.PORTLET_ID, CTPortletKeys.PUBLICATIONS);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(_company);

			mockLiferayPortletActionRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			String[] ownerRoleActionIds = {
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW
			};

			mockLiferayPortletActionRequest.setParameter(
				"permissions",
				JSONUtil.put(
					String.valueOf(_ownerRole.getRoleId()), ownerRoleActionIds
				).toString());

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}

		int finalCTEntriesCount =
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId());

		Assert.assertEquals(0, finalCTEntriesCount - initialCTEntriesCount);
	}

	@Test
	public void testProcessAction2() throws Exception {
		for (String modelResourceOwnerDefaultAction :
				ResourceActionsUtil.getModelResourceOwnerDefaultActions(
					CTCollection.class.getName())) {

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					_company.getCompanyId(), CTCollection.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(_ctCollection.getCtCollectionId()),
					_ownerRole.getRoleId(), modelResourceOwnerDefaultAction));
		}

		Role viewerRole = _roleLocalService.getRole(
			_company.getCompanyId(), RoleConstants.PUBLICATIONS_VIEWER);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_company.getCompanyId()), viewerRole.getRoleId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_company.getCompanyId()), viewerRole.getRoleId(),
				ActionKeys.VIEW));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, CTPortletKeys.PUBLICATIONS);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		String[] ownerRoleActionIds = {
			ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.VIEW
		};

		mockLiferayPortletActionRequest.setParameter(
			"permissions",
			JSONUtil.put(
				String.valueOf(_ownerRole.getRoleId()), ownerRoleActionIds
			).put(
				String.valueOf(viewerRole.getRoleId()),
				new String[] {ActionKeys.UPDATE}
			).toString());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_company.getCompanyId()), viewerRole.getRoleId(),
				ActionKeys.UPDATE));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_company.getCompanyId()), viewerRole.getRoleId(),
				ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_ctCollection.getCtCollectionId()),
				_ownerRole.getRoleId(), CTActionKeys.INVITE_USERS));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_ctCollection.getCtCollectionId()),
				_ownerRole.getRoleId(), CTActionKeys.PUBLISH));

		for (String actionId : ownerRoleActionIds) {
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					_company.getCompanyId(), CTCollection.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(_ctCollection.getCtCollectionId()),
					_ownerRole.getRoleId(), actionId));
		}
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject(filter = "mvc.command.name=/change_tracking/update_permissions")
	private MVCActionCommand _mvcActionCommand;

	private Role _ownerRole;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}