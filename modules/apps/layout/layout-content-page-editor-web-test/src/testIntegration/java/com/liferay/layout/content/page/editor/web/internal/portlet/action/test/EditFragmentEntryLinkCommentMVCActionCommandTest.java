/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Attila Bakay
 */
@RunWith(Arquillian.class)
public class EditFragmentEntryLinkCommentMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_user = UserTestUtil.addGroupAdminUser(_group);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout = layout.fetchDraftLayout();

		long parentCommentId = _commentManager.addComment(
			_user.getUserId(), _group.getGroupId(),
			FragmentEntryLink.class.getName(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(),
			_getFunction(
				_getActionRequest(null, null, "false"), _getThemeDisplay()));

		_parentComment = _commentManager.fetchComment(parentCommentId);

		long childCommentId = _commentManager.addComment(
			null, _user.getUserId(), FragmentEntryLink.class.getName(),
			RandomTestUtil.randomLong(), _user.getFullName(), parentCommentId,
			null, RandomTestUtil.randomString(),
			_getFunction(
				_getActionRequest(null, null, "false"), _getThemeDisplay()));

		_childComment = _commentManager.fetchComment(childCommentId);
	}

	@Test
	public void testEditFragmentEntryLinkCommentResolveComment()
		throws Exception {

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getActionRequest(
				null, String.valueOf(_parentComment.getCommentId()), "true"),
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(jsonObject.getBoolean("resolved"));

		List<Comment> childComments = _commentManager.getChildComments(
			jsonObject.getLong("commentId"), WorkflowConstants.STATUS_DRAFT, -1,
			-1);

		Assert.assertFalse(childComments.isEmpty());
	}

	@Test
	public void testEditFragmentEntryLinkCommentUnresolveComment()
		throws Exception {

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getActionRequest(
				null, String.valueOf(_parentComment.getCommentId()), "true"),
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(jsonObject.getBoolean("resolved"));

		List<Comment> childComments = _commentManager.getChildComments(
			jsonObject.getLong("commentId"), WorkflowConstants.STATUS_DRAFT, -1,
			-1);

		Assert.assertFalse(childComments.isEmpty());

		jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getActionRequest(
				null, String.valueOf(_parentComment.getCommentId()), "false"),
			new MockLiferayPortletActionResponse());

		Assert.assertFalse(jsonObject.getBoolean("resolved"));

		childComments = _commentManager.getChildComments(
			jsonObject.getLong("commentId"), WorkflowConstants.STATUS_APPROVED,
			-1, -1);

		Assert.assertFalse(childComments.isEmpty());
	}

	private ActionRequest _getActionRequest(
			String body, String commentId, String resolved)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		if (Validator.isNotNull(body)) {
			mockLiferayPortletActionRequest.setParameter("body", body);
		}

		if (Validator.isNotNull(commentId)) {
			mockLiferayPortletActionRequest.setParameter(
				"commentId", commentId);
		}

		if (Validator.isNotNull(resolved)) {
			mockLiferayPortletActionRequest.setParameter("resolved", resolved);
		}

		return mockLiferayPortletActionRequest;
	}

	private Function<String, ServiceContext> _getFunction(
			ActionRequest actionRequest, ThemeDisplay themeDisplay)
		throws Exception {

		Function<String, ServiceContext> function = new ServiceContextFunction(
			actionRequest);

		String notificationRedirect = HttpComponentsUtil.setParameter(
			PortalUtil.getLayoutFullURL(themeDisplay), "p_l_mode",
			Constants.EDIT);

		return function.andThen(
			serviceContext -> {
				boolean resolved = ParamUtil.getBoolean(
					actionRequest, "resolved");

				int workflowAction = WorkflowConstants.ACTION_PUBLISH;

				if (resolved) {
					workflowAction = WorkflowConstants.ACTION_SAVE_DRAFT;
				}

				serviceContext.setWorkflowAction(workflowAction);

				return serviceContext;
			}
		).andThen(
			serviceContext -> {
				serviceContext.setAttribute("contentURL", notificationRedirect);
				serviceContext.setAttribute("namespace", StringPool.BLANK);

				return serviceContext;
			}
		);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(PortalUtil.getSiteDefaultLocale(_group));

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setRealUser(_user);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		return themeDisplay;
	}

	private Comment _childComment;

	@Inject
	private CommentManager _commentManager;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/edit_fragment_entry_link_comment"
	)
	private MVCActionCommand _mvcActionCommand;

	private Comment _parentComment;
	private User _user;

}