/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.security.permission.SharingEntryAction;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class CollaboratorResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectDefinition = _getObjectDefinition();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDeleteObjectEntryCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _getUser();

		_assertDeleteObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators/by-type/User/",
				user.getUserId()),
			user);
	}

	@Test
	public void testDeleteScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _getUser();

		_assertDeleteObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/User/", user.getUserId()),
			user);
	}

	@Test
	public void testGetObjectEntryCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _getUser();

		_assertGetObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators/by-type/User/",
				user.getUserId()),
			user);
	}

	@Test
	public void testGetObjectEntryCollaboratorsPage() throws Exception {
		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject3 = _getUserCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2,
				collaboratorJSONObject3
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.GET);

		_assertEquals(
			JSONUtil.putAll(
				collaboratorJSONObject3, collaboratorJSONObject2,
				collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));
	}

	@Test
	public void testGetScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _getUser();

		_assertGetObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/User/", user.getUserId()),
			user);
	}

	@Test
	public void testGetScopeScopeKeyByExternalReferenceCodeCollaboratorsPage()
		throws Exception {

		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 =
			_getUserGroupCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(), "/collaborators"),
			Http.Method.GET);

		_assertEquals(
			JSONUtil.putAll(collaboratorJSONObject2, collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));
	}

	@Test
	public void testPostObjectEntryCollaboratorsPage() throws Exception {
		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 =
			_getUserGroupCollaboratorJSONObject();
		JSONObject collaboratorJSONObject3 = _getUserCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2,
				collaboratorJSONObject3
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		_assertEquals(
			JSONUtil.putAll(
				collaboratorJSONObject3, collaboratorJSONObject2,
				collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));
	}

	@Test
	public void testPostScopeScopeKeyByExternalReferenceCodeCollaboratorsPage()
		throws Exception {

		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 =
			_getUserGroupCollaboratorJSONObject();
		JSONObject collaboratorJSONObject3 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject4 =
			_getUserGroupCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2,
				collaboratorJSONObject3, collaboratorJSONObject4
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(), "/collaborators"),
			Http.Method.POST);

		_assertEquals(
			JSONUtil.putAll(
				collaboratorJSONObject4, collaboratorJSONObject3,
				collaboratorJSONObject2, collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));
	}

	@Test
	public void testPutObjectEntryCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		UserGroup userGroup = _getUserGroup();

		_assertPutObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-type/UserGroup/",
				userGroup.getUserGroupId()),
			userGroup);
	}

	@Test
	public void testPutScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		UserGroup userGroup = _getUserGroup();

		_assertPutObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/UserGroup/",
				userGroup.getUserGroupId()),
			userGroup);
	}

	private static ObjectDefinition _getObjectDefinition() throws Exception {
		return ObjectDefinitionTestUtil.publishObjectDefinition(
			true, ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).indexed(
					true
				).indexedAsKeyword(
					true
				).name(
					"title"
				).localized(
					false
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE, TestPropsValues.getUserId());
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertDeleteObjectEntryCollaborator(
			String endpoint, User user)
		throws Exception {

		JSONObject jsonObject1 = _getUserCollaboratorJSONObject(user);

		_assertEquals(
			jsonObject1,
			HTTPTestUtil.invokeToJSONObject(
				jsonObject1.toString(), endpoint, Http.Method.PUT));

		HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.DELETE);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			null, endpoint, Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject2.getString("status"));
	}

	private void _assertEquals(
		JSONArray actualJSONArray, JSONArray expectedJSONArray) {

		Assert.assertEquals(
			expectedJSONArray.length(), actualJSONArray.length());

		for (int i = 0; i < expectedJSONArray.length(); i++) {
			_assertEquals(
				actualJSONArray.getJSONObject(i),
				expectedJSONArray.getJSONObject(i));
		}
	}

	private void _assertEquals(JSONObject jsonObject1, JSONObject jsonObject2) {
		Assert.assertTrue(_equals(jsonObject1, jsonObject2));
	}

	private void _assertGetObjectEntryCollaborator(String endpoint, User user)
		throws Exception {

		JSONObject jsonObject = _getUserCollaboratorJSONObject(user);

		HTTPTestUtil.invokeToJSONObject(
			jsonObject.toString(), endpoint, Http.Method.PUT);

		_assertEquals(
			jsonObject,
			HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.GET));
	}

	private void _assertPutObjectEntryCollaborator(
			String endpoint, UserGroup userGroup)
		throws Exception {

		JSONObject jsonObject = _getUserGroupCollaboratorJSONObject(userGroup);

		HTTPTestUtil.invokeToJSONObject(
			jsonObject.toString(), endpoint, Http.Method.PUT);

		_assertEquals(
			jsonObject,
			HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.GET));
	}

	private boolean _equals(JSONObject jsonObject1, JSONObject jsonObject2) {
		for (String assertFieldName : _ASSERT_FIELD_NAMES) {
			if (Objects.equals(assertFieldName, "actionIds")) {
				if (!JSONUtil.equals(
						jsonObject1.getJSONArray("actionIds"),
						jsonObject2.getJSONArray("actionIds"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "externalReferenceCode")) {
				if (!StringUtil.equals(
						jsonObject1.getString("externalReferenceCode"),
						jsonObject2.getString("externalReferenceCode"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "id")) {
				if (!StringUtil.equals(
						jsonObject1.getString("id"),
						jsonObject2.getString("id"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "name")) {
				if (!StringUtil.equals(
						jsonObject1.getString("name"),
						jsonObject2.getString("name"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "share")) {
				if (!jsonObject1.getBoolean("share") == jsonObject2.getBoolean(
						"share")) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "type") &&
				!StringUtil.equals(
					jsonObject1.getString("type"),
					jsonObject2.getString("type"))) {

				return false;
			}
		}

		return true;
	}

	private User _getUser() throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		return user;
	}

	private JSONObject _getUserCollaboratorJSONObject() throws Exception {
		return _getUserCollaboratorJSONObject(_getUser());
	}

	private JSONObject _getUserCollaboratorJSONObject(User user)
		throws Exception {

		return JSONUtil.put(
			"actionIds", JSONUtil.put(SharingEntryAction.VIEW.getActionId())
		).put(
			"externalReferenceCode", user.getExternalReferenceCode()
		).put(
			"id", user.getUserId()
		).put(
			"name", user.getFullName()
		).put(
			"share", true
		).put(
			"type", "User"
		);
	}

	private UserGroup _getUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroups.add(userGroup);

		return userGroup;
	}

	private JSONObject _getUserGroupCollaboratorJSONObject() throws Exception {
		return _getUserGroupCollaboratorJSONObject(_getUserGroup());
	}

	private JSONObject _getUserGroupCollaboratorJSONObject(UserGroup userGroup)
		throws Exception {

		return JSONUtil.put(
			"actionIds", JSONUtil.put(SharingEntryAction.VIEW.getActionId())
		).put(
			"externalReferenceCode", userGroup.getExternalReferenceCode()
		).put(
			"id", userGroup.getUserGroupId()
		).put(
			"name", userGroup.getName()
		).put(
			"share", true
		).put(
			"type", "UserGroup"
		);
	}

	private static final String[] _ASSERT_FIELD_NAMES = {
		"actionIds", "externalReferenceCode", "name", "share", "type"
	};

	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups = new ArrayList<>();

	@DeleteAfterTestRun
	private List<User> _users = new ArrayList<>();

}