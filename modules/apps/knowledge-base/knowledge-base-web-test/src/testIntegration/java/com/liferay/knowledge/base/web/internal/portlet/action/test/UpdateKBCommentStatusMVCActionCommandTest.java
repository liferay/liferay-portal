/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class UpdateKBCommentStatusMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-115327")
	public void testDoProcessAction() throws Exception {
		KBArticle kbArticle = _addKBArticle();

		_user1 = UserTestUtil.addUser();

		KBComment kbComment = _kbCommentLocalService.addKBComment(
			_user1.getUserId(), PortalUtil.getClassNameId(KBArticle.class),
			kbArticle.getResourcePrimKey(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_user2 = UserTestUtil.addUser(_group.getGroupId());

		PortletException portletException = Assert.assertThrows(
			PortletException.class,
			() -> _processAction(
				kbComment, KBCommentConstants.STATUS_IN_PROGRESS, _user2));

		Throwable throwable = portletException.getCause();

		Assert.assertEquals(
			PrincipalException.MustHavePermission.class, throwable.getClass());

		_assertStatus(kbComment, KBCommentConstants.STATUS_NEW);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		RoleTestUtil.addResourcePermission(
			_role, KBArticle.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()), KBActionKeys.UPDATE);

		_userGroupRoleLocalService.addUserGroupRoles(
			_user2.getUserId(), _group.getGroupId(),
			new long[] {_role.getRoleId()});

		_processAction(
			kbComment, KBCommentConstants.STATUS_IN_PROGRESS, _user2);

		_assertStatus(kbComment, KBCommentConstants.STATUS_IN_PROGRESS);

		_processAction(kbComment, KBCommentConstants.STATUS_COMPLETED, _user2);

		_assertStatus(kbComment, KBCommentConstants.STATUS_COMPLETED);
	}

	private KBArticle _addKBArticle() throws Exception {
		return _kbArticleLocalService.addKBArticle(
			null, TestPropsValues.getUserId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()),
			KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertStatus(KBComment kbComment, int status)
		throws Exception {

		kbComment = _kbCommentLocalService.getKBComment(
			kbComment.getKbCommentId());

		Assert.assertEquals(status, kbComment.getStatus());
	}

	private ThemeDisplay _getThemeDisplay(
			PermissionChecker permissionChecker, User user)
		throws Exception {

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			LayoutTestUtil.addTypeContentLayout(_group));

		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setRealUser(user);
		themeDisplay.setSignedIn(true);
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private void _processAction(KBComment kbComment, int status, User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				new MockLiferayPortletActionRequest();

			mockLiferayPortletActionRequest.setAttribute(
				WebKeys.THEME_DISPLAY,
				_getThemeDisplay(permissionChecker, user));
			mockLiferayPortletActionRequest.setParameter(
				"kbCommentId", String.valueOf(kbComment.getKbCommentId()));
			mockLiferayPortletActionRequest.setParameter(
				"kbCommentStatus", String.valueOf(status));

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private KBCommentLocalService _kbCommentLocalService;

	@Inject(
		filter = "mvc.command.name=/knowledge_base/update_kb_comment_status"
	)
	private MVCActionCommand _mvcActionCommand;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}