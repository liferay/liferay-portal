/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

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
		Configuration configuration = _getConfiguration();

		if (configuration != null) {
			_originalProperties = configuration.getProperties();
		}

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_ownerRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.OWNER);
		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR,
			CTCollection.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);
	}

	@After
	public void tearDown() throws Exception {
		Configuration configuration = _getConfiguration();

		if (_originalProperties != null) {
			ConfigurationTestUtil.saveConfiguration(
				configuration, _originalProperties);
		}
		else if (configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testProcessAction() throws Exception {
		for (String modelResourceOwnerDefaultAction :
				ResourceActionsUtil.getModelResourceOwnerDefaultActions(
					CTCollection.class.getName())) {

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(),
					CTCollection.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(_ctCollection.getCtCollectionId()),
					_ownerRole.getRoleId(), modelResourceOwnerDefaultAction));
		}

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ActionKeys.VIEW));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(TestPropsValues.getUser());

		String[] ownerRoleActionIds = {
			ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.VIEW
		};

		mockLiferayPortletActionRequest.setParameter(
			"permissions",
			JSONUtil.put(
				String.valueOf(_ownerRole.getRoleId()), ownerRoleActionIds
			).put(
				String.valueOf(_role.getRoleId()),
				new String[] {ActionKeys.UPDATE}
			).toString());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_ctCollection.getCtCollectionId()),
				_ownerRole.getRoleId(), CTActionKeys.INVITE_USERS));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_ctCollection.getCtCollectionId()),
				_ownerRole.getRoleId(), CTActionKeys.PUBLISH));

		for (String actionId : ownerRoleActionIds) {
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(),
					CTCollection.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(_ctCollection.getCtCollectionId()),
					_ownerRole.getRoleId(), actionId));
		}
	}

	@Test
	public void testProcessActionWithCTCollection() throws Exception {
		int initialCTEntriesCount =
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest(TestPropsValues.getUser());

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
	public void testProcessActionWithoutConfigurationPermission()
		throws Exception {

		_user = UserTestUtil.addUser();

		Role publicationsViewerRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.PUBLICATIONS_VIEWER);

		_roleLocalService.addUserRole(
			_user.getUserId(), publicationsViewerRole.getRoleId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(_user);

		mockLiferayPortletActionRequest.setParameter(
			"permissions",
			JSONUtil.put(
				String.valueOf(_ownerRole.getRoleId()),
				new String[] {ActionKeys.VIEW}
			).put(
				String.valueOf(_role.getRoleId()),
				new String[] {ActionKeys.UPDATE}
			).toString());

		try {
			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			Assert.fail();
		}
		catch (PortletException portletException) {
			Throwable throwable = portletException.getCause();

			Assert.assertTrue(
				throwable instanceof PrincipalException.MustHavePermission);
		}

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ActionKeys.VIEW));

		for (String modelResourceOwnerDefaultAction :
				ResourceActionsUtil.getModelResourceOwnerDefaultActions(
					CTCollection.class.getName())) {

			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(),
					CTCollection.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(_ctCollection.getCtCollectionId()),
					_ownerRole.getRoleId(), modelResourceOwnerDefaultAction));
		}
	}

	private Configuration _getConfiguration() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(&(", ConfigurationAdmin.SERVICE_FACTORYPID, StringPool.EQUAL,
				CTSettingsConfiguration.class.getName(), ".scoped)(companyId=",
				TestPropsValues.getCompanyId(), "))"));

		if (ArrayUtil.isEmpty(configurations)) {
			return null;
		}

		return configurations[0];
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, CTPortletKeys.PUBLICATIONS);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject(filter = "mvc.command.name=/change_tracking/update_permissions")
	private MVCActionCommand _mvcActionCommand;

	private Dictionary<String, Object> _originalProperties;
	private Role _ownerRole;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

}