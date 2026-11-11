/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-104086")
@RunWith(Arquillian.class)
public class DeleteContentItemCommentStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		_testExecuteWithCMSObjectDefinition();
		_testExecuteWithCompanyScopedObjectDefinition();
	}

	private Role _addViewRole(ObjectEntry objectEntry, User user)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_userLocalService.addRoleUser(role.getRoleId(), user);

		return role;
	}

	private JSONObject _execute(
			Layout layout, ObjectEntry objectEntry,
			Map<String, String> parameters, StrutsAction strutsAction)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				layout.getGroup(), layout);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		mockHttpServletRequest.setParameter(
			"className", objectDefinition.getClassName());

		mockHttpServletRequest.setParameter(
			"classPK", String.valueOf(objectEntry.getObjectEntryId()));
		mockHttpServletRequest.setParameters(parameters);
		mockHttpServletRequest.setRequestURI(layout.getFriendlyURL());

		User user = TestPropsValues.getUser();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		themeDisplay.setPermissionChecker(permissionChecker);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			strutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	private String _getMentionHTML(User user) {
		return StringBundler.concat(
			"<span class=\"mention mention-user-",
			user.getExternalReferenceCode(), "\">@", user.getFullName(),
			"</span>");
	}

	private void _removeResourcePermissions(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(
			objectEntry.getCompanyId(), roleName);

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntry.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[0]);
	}

	private void _testExecuteWithCMSObjectDefinition() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", depotEntry.getCompanyId());
		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					RandomTestUtil.randomString()
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()), title
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), TestPropsValues.getUserId()));

		_removeResourcePermissions(
			objectDefinition, objectEntry,
			DepotRolesConstants.ASSET_LIBRARY_MEMBER);
		_removeResourcePermissions(
			objectDefinition, objectEntry, RoleConstants.USER);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		User user = UserTestUtil.addUser();

		Role role = _addViewRole(objectEntry, user);

		String text = RandomTestUtil.randomString();

		String commentBody = StringBundler.concat(
			"<p>", text, StringPool.SPACE, _getMentionHTML(user), "</p>");

		JSONObject jsonObject1 = _execute(
			layout, objectEntry,
			HashMapBuilder.put(
				"body", commentBody
			).build(),
			_addContentItemCommentStrutsAction);

		JSONObject jsonObject2 = _execute(
			layout, objectEntry,
			HashMapBuilder.put(
				"body", commentBody
			).put(
				"parentCommentId", jsonObject1.getString("commentId")
			).build(),
			_addContentItemCommentStrutsAction);

		_execute(
			layout, objectEntry,
			HashMapBuilder.put(
				"commentId", jsonObject1.getString("commentId")
			).build(),
			_deleteContentItemCommentStrutsAction);

		Assert.assertNotNull(
			_commentManager.fetchComment(jsonObject2.getLong("commentId")));

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 2,
			userNotificationEvents.size());

		User author = TestPropsValues.getUser();
		Set<String> messageIds = new HashSet<>();

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			UserNotificationFeedEntry userNotificationFeedEntry =
				UserNotificationManagerUtil.interpret(
					StringPool.BLANK, userNotificationEvent,
					ServiceContextTestUtil.getServiceContext());

			String body = userNotificationFeedEntry.getBody();

			Assert.assertTrue(
				body.contains(
					StringBundler.concat(
						author.getFullName(), " mentioned you in a comment on ",
						title, ".")));
			Assert.assertFalse(body.contains(text));

			messageIds.add(
				HttpComponentsUtil.getParameter(
					userNotificationFeedEntry.getLink(), "messageId", false));
		}

		Assert.assertEquals(
			SetUtil.fromArray(
				jsonObject1.getString("commentId"),
				jsonObject2.getString("commentId")),
			messageIds);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			UserNotificationManagerUtil.interpret(
				StringPool.BLANK, userNotificationEvent,
				ServiceContextTestUtil.getServiceContext());
		}

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
		_layoutLocalService.deleteLayout(layout);
		_roleLocalService.deleteRole(role);
		_userLocalService.deleteUser(user);
	}

	private void _testExecuteWithCompanyScopedObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		User user = UserTestUtil.addUser();

		Role role = _addViewRole(objectEntry, user);

		JSONObject jsonObject = _execute(
			layout, objectEntry,
			HashMapBuilder.put(
				"body",
				StringBundler.concat(
					"<p>", RandomTestUtil.randomString(), StringPool.SPACE,
					_getMentionHTML(user), "</p>")
			).build(),
			_addContentItemCommentStrutsAction);

		_execute(
			layout, objectEntry,
			HashMapBuilder.put(
				"commentId", jsonObject.getString("commentId")
			).build(),
			_deleteContentItemCommentStrutsAction);

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationFeedEntry userNotificationFeedEntry =
			UserNotificationManagerUtil.interpret(
				StringPool.BLANK, userNotificationEvents.get(0),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			jsonObject.getString("commentId"),
			HttpComponentsUtil.getParameter(
				userNotificationFeedEntry.getLink(), "messageId", false));

		_layoutLocalService.deleteLayout(layout);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
		_roleLocalService.deleteRole(role);
		_userLocalService.deleteUser(user);
	}

	@Inject(filter = "path=/cms/add_content_item_comment")
	private StrutsAction _addContentItemCommentStrutsAction;

	@Inject
	private CommentManager _commentManager;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "path=/cms/delete_content_item_comment")
	private StrutsAction _deleteContentItemCommentStrutsAction;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}