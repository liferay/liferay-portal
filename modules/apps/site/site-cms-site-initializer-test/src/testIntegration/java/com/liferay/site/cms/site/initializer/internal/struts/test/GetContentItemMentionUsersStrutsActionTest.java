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
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
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
public class GetContentItemMentionUsersStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		_testExecute();
		_testExecuteWithClassNameId();
		_testExecuteWithKeywords();
		_testExecuteWithManyUsers();
		_testExecuteWithoutAddDiscussionPermission();
	}

	@FeatureFlag(enable = false, value = "LPD-104086")
	@Test
	public void testExecuteWithoutFeatureFlag() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> _execute(
				null, layout, objectEntry, TestPropsValues.getUser()));

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _addComment(ObjectEntry objectEntry, User user)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		_commentManager.addComment(
			null, user.getUserId(), objectEntry.getGroupId(),
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			user.getFullName(), null, RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					objectEntry.getGroupId(), user.getUserId())));
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

	private ObjectEntry _addObjectEntry(DepotEntry depotEntry)
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
				"title_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					RandomTestUtil.randomString()
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

	private Role _addViewRole(ObjectEntry objectEntry, User... users)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		for (User user : users) {
			_userLocalService.addRoleUser(role.getRoleId(), user);
		}

		return role;
	}

	private JSONArray _execute(
			MockHttpServletRequest mockHttpServletRequest, User user)
		throws Exception {

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

			_getContentItemMentionUsersStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		return jsonObject.getJSONArray("users");
	}

	private JSONArray _execute(
			String keywords, Layout layout, ObjectEntry objectEntry, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout, objectEntry);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		mockHttpServletRequest.setParameter(
			"className", objectDefinition.getClassName());

		mockHttpServletRequest.setParameter("keywords", keywords);

		return _execute(mockHttpServletRequest, user);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Layout layout, ObjectEntry objectEntry)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				layout.getGroup(), layout);

		mockHttpServletRequest.setParameter(
			"classPK", String.valueOf(objectEntry.getObjectEntryId()));
		mockHttpServletRequest.setRequestURI(layout.getFriendlyURL());

		return mockHttpServletRequest;
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

	private void _testExecute() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		User user1 = UserTestUtil.addUser(depotEntry.getGroupId());
		User user2 = UserTestUtil.addUser(depotEntry.getGroupId());
		User user3 = UserTestUtil.addUser(depotEntry.getGroupId());

		User user4 = UserTestUtil.addUser();

		Role role = _addViewRole(objectEntry, user1, user2, user4);

		_addComment(objectEntry, TestPropsValues.getUser());
		_addComment(objectEntry, user2);
		_addComment(objectEntry, user3);
		_addComment(objectEntry, user4);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		JSONArray usersJSONArray = _execute(
			null, layout, objectEntry, TestPropsValues.getUser());

		Assert.assertEquals(
			usersJSONArray.toString(), 2, usersJSONArray.length());

		JSONObject userJSONObject1 = usersJSONArray.getJSONObject(0);

		Assert.assertEquals(
			user2.getExternalReferenceCode(), userJSONObject1.getString("id"));
		Assert.assertEquals(
			user2.getFullName(), userJSONObject1.getString("name"));
		Assert.assertTrue(userJSONObject1.getBoolean("recent"));
		Assert.assertEquals(
			user2.getScreenName(), userJSONObject1.getString("screenName"));

		JSONObject userJSONObject2 = usersJSONArray.getJSONObject(1);

		Assert.assertEquals(
			user1.getExternalReferenceCode(), userJSONObject2.getString("id"));
		Assert.assertFalse(userJSONObject2.getBoolean("recent"));

		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
		_userLocalService.deleteUser(user3);
		_userLocalService.deleteUser(user4);

		_roleLocalService.deleteRole(role);

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testExecuteWithClassNameId() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		User user = UserTestUtil.addUser(depotEntry.getGroupId());

		Role role = _addViewRole(objectEntry, user);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout, objectEntry);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		mockHttpServletRequest.setParameter(
			"classNameId",
			String.valueOf(
				PortalUtil.getClassNameId(objectDefinition.getClassName())));

		JSONArray usersJSONArray = _execute(
			mockHttpServletRequest, TestPropsValues.getUser());

		Assert.assertEquals(
			usersJSONArray.toString(), 1, usersJSONArray.length());

		JSONObject userJSONObject = usersJSONArray.getJSONObject(0);

		Assert.assertEquals(
			user.getExternalReferenceCode(), userJSONObject.getString("id"));

		_userLocalService.deleteUser(user);

		_roleLocalService.deleteRole(role);

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testExecuteWithKeywords() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		User user1 = UserTestUtil.addUser(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {depotEntry.getGroupId()});
		User user2 = UserTestUtil.addUser(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			depotEntry.getGroupId());
		User user3 = UserTestUtil.addUser(depotEntry.getGroupId());

		Role role = _addViewRole(objectEntry, user1, user2, user3);

		_addComment(objectEntry, user3);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		JSONArray usersJSONArray = _execute(
			user1.getFirstName(), layout, objectEntry,
			TestPropsValues.getUser());

		Assert.assertEquals(
			usersJSONArray.toString(), 1, usersJSONArray.length());

		JSONObject userJSONObject1 = usersJSONArray.getJSONObject(0);

		Assert.assertEquals(
			user1.getExternalReferenceCode(), userJSONObject1.getString("id"));

		usersJSONArray = _execute(
			user2.getScreenName(), layout, objectEntry,
			TestPropsValues.getUser());

		Assert.assertEquals(
			usersJSONArray.toString(), 1, usersJSONArray.length());

		JSONObject userJSONObject2 = usersJSONArray.getJSONObject(0);

		Assert.assertEquals(
			user2.getExternalReferenceCode(), userJSONObject2.getString("id"));

		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
		_userLocalService.deleteUser(user3);

		_roleLocalService.deleteRole(role);

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testExecuteWithManyUsers() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		List<User> users1 = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			users1.add(
				UserTestUtil.addUser(
					StringUtil.toLowerCase(RandomTestUtil.randomString()),
					LocaleUtil.getDefault(),
					"a" + RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					new long[] {depotEntry.getGroupId()}));
		}

		String lastName = RandomTestUtil.randomString();

		List<User> users2 = new ArrayList<>();

		for (int i = 0; i < 21; i++) {
			users2.add(
				UserTestUtil.addUser(
					StringUtil.toLowerCase(RandomTestUtil.randomString()),
					LocaleUtil.getDefault(),
					"z" + RandomTestUtil.randomString(), lastName,
					new long[] {depotEntry.getGroupId()}));
		}

		Role role = _addViewRole(objectEntry, users2.toArray(new User[0]));

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		JSONArray usersJSONArray = _execute(
			null, layout, objectEntry, TestPropsValues.getUser());

		Assert.assertEquals(
			usersJSONArray.toString(), 0, usersJSONArray.length());

		usersJSONArray = _execute(
			lastName, layout, objectEntry, TestPropsValues.getUser());

		Assert.assertEquals(
			usersJSONArray.toString(), 20, usersJSONArray.length());

		for (User user : users1) {
			_userLocalService.deleteUser(user);
		}

		for (User user : users2) {
			_userLocalService.deleteUser(user);
		}

		_roleLocalService.deleteRole(role);

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testExecuteWithoutAddDiscussionPermission() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		User user = UserTestUtil.addUser(depotEntry.getGroupId());

		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _execute(null, layout, objectEntry, user));

		Assert.assertArrayEquals(
			new String[] {ActionKeys.ADD_DISCUSSION},
			mustHavePermission.actionId);
		Assert.assertEquals(
			objectEntry.getObjectEntryId(), mustHavePermission.resourceId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		Assert.assertEquals(
			objectDefinition.getClassName(), mustHavePermission.resourceName);

		_userLocalService.deleteUser(user);

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Inject
	private CommentManager _commentManager;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(filter = "path=/cms/get_content_item_mention_users")
	private StrutsAction _getContentItemMentionUsersStrutsAction;

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

}