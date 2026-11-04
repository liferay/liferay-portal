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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.io.Serializable;

import java.util.List;

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
public class AddContentItemCommentStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		_testExecuteWithMentionedAuthor();
		_testExecuteWithMentionedUser();
		_testExecuteWithMentionedUserWithoutViewPermission();
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(DepotEntry depotEntry, String title)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", depotEntry.getCompanyId());
		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

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

		return objectEntry;
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
			String body, Layout layout, ObjectEntry objectEntry, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				layout.getGroup(), layout);

		mockHttpServletRequest.setParameter("body", body);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		mockHttpServletRequest.setParameter(
			"className", objectDefinition.getClassName());

		mockHttpServletRequest.setParameter(
			"classPK", String.valueOf(objectEntry.getObjectEntryId()));
		mockHttpServletRequest.setRequestURI(layout.getFriendlyURL());

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

			_addContentItemCommentStrutsAction.execute(
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

	private void _testExecuteWithMentionedAuthor() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry, RandomTestUtil.randomString());

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		int userNotificationEventsCount =
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				TestPropsValues.getUserId());

		_execute(
			_getMentionHTML(TestPropsValues.getUser()), layout, objectEntry,
			TestPropsValues.getUser());

		Assert.assertEquals(
			userNotificationEventsCount,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				TestPropsValues.getUserId()));

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
		_layoutLocalService.deleteLayout(layout);
		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	private void _testExecuteWithMentionedUser() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry, title);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		String screenName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		User user1 = UserTestUtil.addUser(
			RandomTestUtil.randomString(), LocaleUtil.getDefault(), screenName,
			RandomTestUtil.randomString(), null);

		Role role1 = _addViewRole(objectEntry, user1);

		User user2 = UserTestUtil.addUser(screenName);

		Role role2 = _addViewRole(objectEntry, user2);

		JSONObject jsonObject = _execute(
			StringBundler.concat(
				"<p>", RandomTestUtil.randomString(), StringPool.SPACE,
				_getMentionHTML(user1), "</p>"),
			layout, objectEntry, TestPropsValues.getUser());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user1.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		Assert.assertEquals(
			jsonObject.getLong("commentId"),
			payloadJSONObject.getLong("classPK"));

		String entryURL = payloadJSONObject.getString("entryURL");

		Assert.assertEquals(
			jsonObject.getString("commentId"),
			HttpComponentsUtil.getParameter(entryURL, "messageId", false));
		Assert.assertEquals(
			String.valueOf(objectEntry.getObjectEntryId()),
			HttpComponentsUtil.getParameter(entryURL, "objectEntryId", false));

		UserNotificationFeedEntry userNotificationFeedEntry =
			UserNotificationManagerUtil.interpret(
				StringPool.BLANK, userNotificationEvent,
				ServiceContextTestUtil.getServiceContext());

		String body = userNotificationFeedEntry.getBody();

		User author = TestPropsValues.getUser();

		Assert.assertTrue(
			body.contains(
				StringBundler.concat(
					author.getFullName(), " mentioned you in a comment on ",
					title, ".")));

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user2.getUserId()));

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
		_layoutLocalService.deleteLayout(layout);
		_objectEntryLocalService.deleteObjectEntry(objectEntry);
		_roleLocalService.deleteRole(role1);
		_roleLocalService.deleteRole(role2);
		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
	}

	private void _testExecuteWithMentionedUserWithoutViewPermission()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry, RandomTestUtil.randomString());

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		User user = UserTestUtil.addUser();

		String text = RandomTestUtil.randomString();

		JSONObject jsonObject = _execute(
			StringBundler.concat(
				"<p>", text, StringPool.SPACE, _getMentionHTML(user), "</p>"),
			layout, objectEntry, TestPropsValues.getUser());

		Assert.assertEquals(
			StringBundler.concat("<p>", text, " @", user.getFullName(), "</p>"),
			jsonObject.getString("body"));

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
		_layoutLocalService.deleteLayout(layout);
		_objectEntryLocalService.deleteObjectEntry(objectEntry);
		_userLocalService.deleteUser(user);
	}

	@Inject(filter = "path=/cms/add_content_item_comment")
	private StrutsAction _addContentItemCommentStrutsAction;

	@Inject
	private CompanyLocalService _companyLocalService;

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