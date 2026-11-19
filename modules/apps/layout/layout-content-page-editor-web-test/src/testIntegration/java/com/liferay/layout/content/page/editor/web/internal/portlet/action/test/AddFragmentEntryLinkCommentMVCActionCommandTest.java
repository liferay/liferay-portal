/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.NoSuchEntryLinkException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
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
import com.liferay.portal.kernel.test.AssertUtils;
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
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

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
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class AddFragmentEntryLinkCommentMVCActionCommandTest {

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

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout = layout.fetchDraftLayout();

		_fragmentEntryLink = _addFragmentEntryLink(_layout);

		_user = UserTestUtil.addGroupAdminUser(_group);
	}

	@Test
	public void testDoTransactionalCommand() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		String body = RandomTestUtil.randomString();

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getActionRequest(
				body, _fragmentEntryLink.getFragmentEntryLinkId(), 0),
			new MockLiferayPortletActionResponse());

		Assert.assertEquals(body, jsonObject.getString("body"));

		Assert.assertEquals(
			1,
			_commentManager.getCommentsCount(
				FragmentEntryLink.class.getName(),
				_fragmentEntryLink.getFragmentEntryLinkId()));

		_testDoTransactionalCommandFromAnotherLayout();
		_testDoTransactionalCommandReply();
		_testDoTransactionalCommandReplyFromAnotherFragmentEntryLink();
		_testDoTransactionalCommandReplyWithInvalidClassName();
		_testDoTransactionalCommandWithInvalidFragmentEntryLinkId();
	}

	private long _addComment(FragmentEntryLink fragmentEntryLink)
		throws Exception {

		return _commentManager.addComment(
			_user.getUserId(), _group.getGroupId(),
			FragmentEntryLink.class.getName(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			RandomTestUtil.randomString(),
			_getFunction(_getActionRequest(null, 0, 0), _getThemeDisplay()));
	}

	private FragmentEntryLink _addFragmentEntryLink(Layout layout)
		throws Exception {

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private ActionRequest _getActionRequest(
			String body, long fragmentEntryLinkId, long parentCommentId)
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

		mockLiferayPortletActionRequest.setParameter(
			"fragmentEntryLinkId", String.valueOf(fragmentEntryLinkId));
		mockLiferayPortletActionRequest.setParameter(
			"parentCommentId", String.valueOf(parentCommentId));

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
				serviceContext.setAttribute("contentURL", notificationRedirect);
				serviceContext.setAttribute("namespace", StringPool.BLANK);
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);

				return serviceContext;
			});
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

	private void _testDoTransactionalCommandFromAnotherLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			layout.fetchDraftLayout());

		AssertUtils.assertFailure(
			NoSuchEntryLinkException.class,
			"No fragment entry link exists with the primary key " +
				fragmentEntryLink.getFragmentEntryLinkId(),
			() -> ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getActionRequest(
					RandomTestUtil.randomString(),
					fragmentEntryLink.getFragmentEntryLinkId(), 0),
				new MockLiferayPortletActionResponse()));

		Assert.assertEquals(
			0,
			_commentManager.getCommentsCount(
				FragmentEntryLink.class.getName(),
				fragmentEntryLink.getFragmentEntryLinkId()));
	}

	private void _testDoTransactionalCommandReply() throws Exception {
		long parentCommentId = _addComment(_fragmentEntryLink);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getActionRequest(
				RandomTestUtil.randomString(),
				_fragmentEntryLink.getFragmentEntryLinkId(), parentCommentId),
			new MockLiferayPortletActionResponse());

		List<Comment> childComments = _commentManager.getChildComments(
			parentCommentId, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(childComments.toString(), 1, childComments.size());

		Comment childComment = childComments.get(0);

		Assert.assertEquals(
			jsonObject.getLong("commentId"), childComment.getCommentId());
	}

	private void _testDoTransactionalCommandReplyFromAnotherFragmentEntryLink()
		throws Exception {

		long parentCommentId = _addComment(_addFragmentEntryLink(_layout));

		AssertUtils.assertFailure(
			NoSuchModelException.class,
			"No comment exists with the primary key " + parentCommentId,
			() -> ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getActionRequest(
					RandomTestUtil.randomString(),
					_fragmentEntryLink.getFragmentEntryLinkId(),
					parentCommentId),
				new MockLiferayPortletActionResponse()));

		List<Comment> childComments = _commentManager.getChildComments(
			parentCommentId, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertTrue(childComments.isEmpty());
	}

	private void _testDoTransactionalCommandReplyWithInvalidClassName()
		throws Exception {

		long parentCommentId = _commentManager.addComment(
			_user.getUserId(), _group.getGroupId(), Layout.class.getName(),
			_fragmentEntryLink.getFragmentEntryLinkId(),
			RandomTestUtil.randomString(),
			_getFunction(_getActionRequest(null, 0, 0), _getThemeDisplay()));

		AssertUtils.assertFailure(
			NoSuchModelException.class,
			"No comment exists with the primary key " + parentCommentId,
			() -> ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getActionRequest(
					RandomTestUtil.randomString(),
					_fragmentEntryLink.getFragmentEntryLinkId(),
					parentCommentId),
				new MockLiferayPortletActionResponse()));

		List<Comment> childComments = _commentManager.getChildComments(
			parentCommentId, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertTrue(childComments.isEmpty());
	}

	private void _testDoTransactionalCommandWithInvalidFragmentEntryLinkId()
		throws Exception {

		long fragmentEntryLinkId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchEntryLinkException.class,
			"No fragment entry link exists with the primary key " +
				fragmentEntryLinkId,
			() -> ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getActionRequest(
					RandomTestUtil.randomString(), fragmentEntryLinkId, 0),
				new MockLiferayPortletActionResponse()));
	}

	@Inject
	private CommentManager _commentManager;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private FragmentEntryLink _fragmentEntryLink;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/add_fragment_entry_link_comment"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private User _user;

}