/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Jürgen Kappler
 */
public abstract class BaseDDMTemplateMVCRenderCommandTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));
	}

	protected User addUser() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		_users.add(user);

		return user;
	}

	protected User addUserWithAddDDMTemplatePermission() throws Exception {
		return _addUser(
			JournalConstants.RESOURCE_NAME, ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()), DDMActionKeys.ADD_TEMPLATE);
	}

	protected User addUserWithUpdatePermission() throws Exception {
		return _addUser(
			ResourceActionsUtil.getCompositeModelName(
				JournalArticle.class.getName(), DDMTemplate.class.getName()),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(ddmTemplate.getTemplateId()), ActionKeys.UPDATE);
	}

	protected void assertDDMTemplateDisplayContext(
		RenderRequest renderRequest) {

		Assert.assertNotNull(
			renderRequest.getAttribute(_RENDER_REQUEST_ATTRIBUTE_NAME));
	}

	protected void assertNoDDMTemplateDisplayContext(
		RenderRequest renderRequest) {

		Assert.assertNull(
			renderRequest.getAttribute(_RENDER_REQUEST_ATTRIBUTE_NAME));
	}

	protected void assertSessionError(
		RenderRequest renderRequest, Class<?> clazz) {

		Assert.assertTrue(SessionErrors.contains(renderRequest, clazz));
	}

	protected MockLiferayPortletRenderRequest
			getMockLiferayPortletRenderRequest(String path, User user)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	protected String render(RenderRequest renderRequest) throws Exception {
		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		mvcPortlet.render(renderRequest, mockLiferayPortletRenderResponse);

		return (String)renderRequest.getAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderResponse.getNamespace(),
				StringPool.PERIOD,
				MVCRenderConstants.MVC_PATH_REQUEST_ATTRIBUTE_NAME));
	}

	protected DDMTemplate ddmTemplate;

	private User _addUser(
			String resourceName, int scope, String primKey, String actionId)
		throws Exception {

		User user = addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roles.add(role);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), resourceName, scope, primKey,
			role.getRoleId(), new String[] {actionId});

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		return user;
	}

	private static final String _RENDER_REQUEST_ATTRIBUTE_NAME =
		"com.liferay.journal.web.internal.display.context." +
			"JournalEditDDMTemplateDisplayContext";

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.portlet.JournalPortlet"
	)
	private Portlet _portlet;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private final List<Role> _roles = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}