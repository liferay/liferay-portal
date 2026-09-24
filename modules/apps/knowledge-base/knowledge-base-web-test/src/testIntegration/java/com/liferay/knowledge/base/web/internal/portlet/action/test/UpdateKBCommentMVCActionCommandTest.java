/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0
 */

package com.liferay.knowledge.base.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class UpdateKBCommentMVCActionCommandTest {

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
	public void testDoProcessAction() throws Exception {
		KBArticle kbArticle1 = _addKBArticle(true);
		KBArticle kbArticle2 = _addKBArticle(false);

		User user1 = UserTestUtil.addUser();

		User user2 = UserTestUtil.addUser();

		_assertMustHavePermission(
			HashMapBuilder.put(
				Constants.CMD, Constants.ADD
			).put(
				"classNameId",
				String.valueOf(PortalUtil.getClassNameId(KBArticle.class))
			).put(
				"classPK", String.valueOf(kbArticle2.getResourcePrimKey())
			).put(
				"content", RandomTestUtil.randomString()
			).build(),
			user2);

		Assert.assertEquals(
			0,
			_kbCommentLocalService.getKBCommentsCount(
				KBArticle.class.getName(), kbArticle2.getResourcePrimKey()));

		_processAction(
			HashMapBuilder.put(
				Constants.CMD, Constants.ADD
			).put(
				"classNameId",
				String.valueOf(PortalUtil.getClassNameId(User.class))
			).put(
				"classPK", String.valueOf(kbArticle1.getResourcePrimKey())
			).put(
				"content", RandomTestUtil.randomString()
			).build(),
			user2);

		Assert.assertEquals(
			1,
			_kbCommentLocalService.getKBCommentsCount(
				user2.getUserId(), KBArticle.class.getName(),
				kbArticle1.getResourcePrimKey()));

		KBComment kbComment = _kbCommentLocalService.addKBComment(
			user1.getUserId(), PortalUtil.getClassNameId(KBArticle.class),
			kbArticle1.getResourcePrimKey(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertProcessActionFails(
			kbComment, KBCommentConstants.STATUS_ANY, user2);
		_assertProcessActionFails(
			kbComment, KBCommentConstants.STATUS_COMPLETED, user2);

		String content = RandomTestUtil.randomString();

		_processAction(
			_getParameters(
				content, kbArticle2, kbComment, KBCommentConstants.STATUS_ANY),
			user1);

		kbComment = _kbCommentLocalService.getKBComment(
			kbComment.getKbCommentId());

		Assert.assertEquals(
			PortalUtil.getClassNameId(KBArticle.class),
			kbComment.getClassNameId());
		Assert.assertEquals(
			kbArticle1.getResourcePrimKey(), kbComment.getClassPK());
		Assert.assertEquals(content, kbComment.getContent());
		Assert.assertEquals(
			KBCommentConstants.STATUS_NEW, kbComment.getStatus());

		content = RandomTestUtil.randomString();

		_processAction(
			_getParameters(
				content, kbArticle2, kbComment,
				KBCommentConstants.STATUS_COMPLETED),
			user1);

		kbComment = _kbCommentLocalService.getKBComment(
			kbComment.getKbCommentId());

		Assert.assertEquals(
			kbArticle1.getResourcePrimKey(), kbComment.getClassPK());
		Assert.assertEquals(content, kbComment.getContent());
		Assert.assertEquals(
			KBCommentConstants.STATUS_COMPLETED, kbComment.getStatus());
	}

	private KBArticle _addKBArticle(boolean addGuestPermissions)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(addGuestPermissions);

		return _kbArticleLocalService.addKBArticle(
			null, TestPropsValues.getUserId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()),
			KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), null, null, null, serviceContext);
	}

	private void _assertMustHavePermission(
			Map<String, String> parameters, User user)
		throws Exception {

		PortletException portletException = Assert.assertThrows(
			PortletException.class, () -> _processAction(parameters, user));

		Throwable throwable = portletException.getCause();

		Assert.assertEquals(
			PrincipalException.MustHavePermission.class, throwable.getClass());
	}

	private void _assertProcessActionFails(
			KBComment kbComment, int status, User user)
		throws Exception {

		_assertMustHavePermission(
			_getParameters(
				RandomTestUtil.randomString(), null, kbComment, status),
			user);

		KBComment currentKBComment = _kbCommentLocalService.getKBComment(
			kbComment.getKbCommentId());

		Assert.assertEquals(
			kbComment.getContent(), currentKBComment.getContent());
		Assert.assertEquals(
			kbComment.getStatus(), currentKBComment.getStatus());
	}

	private Map<String, String> _getParameters(
		String content, KBArticle kbArticle, KBComment kbComment, int status) {

		long classPK = kbComment.getClassPK();

		if (kbArticle != null) {
			classPK = kbArticle.getResourcePrimKey();
		}

		return HashMapBuilder.put(
			Constants.CMD, Constants.UPDATE
		).put(
			"classNameId", String.valueOf(kbComment.getClassNameId())
		).put(
			"classPK", String.valueOf(classPK)
		).put(
			"content", content
		).put(
			"kbCommentId", String.valueOf(kbComment.getKbCommentId())
		).put(
			"status",
			() -> {
				if (status == KBCommentConstants.STATUS_ANY) {
					return null;
				}

				return String.valueOf(status);
			}
		).build();
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

	private void _processAction(Map<String, String> parameters, User user)
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

			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				mockLiferayPortletActionRequest.setParameter(
					entry.getKey(), entry.getValue());
			}

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

	@Inject(filter = "mvc.command.name=/knowledge_base/update_kb_comment")
	private MVCActionCommand _mvcActionCommand;

}