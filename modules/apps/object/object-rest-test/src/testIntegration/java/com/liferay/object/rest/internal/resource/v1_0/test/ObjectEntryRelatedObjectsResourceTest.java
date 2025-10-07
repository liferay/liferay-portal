/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectFieldTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.rest.test.util.UserAccountTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class ObjectEntryRelatedObjectsResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_1,
					false)));

		_objectDefinitions.add(_objectDefinition1);

		_objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_1, _OBJECT_FIELD_VALUE_1);

		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_2,
					false)));

		_objectDefinitions.add(_objectDefinition2);

		_objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition2, _OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_2);
		_objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition2, _OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_2);

		_objectDefinition3 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_2,
					false)));

		_objectDefinitions.add(_objectDefinition3);

		_objectEntry4 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition3, _OBJECT_FIELD_NAME_2,
			RandomTestUtil.randomString());

		_user1 = TestPropsValues.getUser();
		_user2 = UserTestUtil.addUser(TestPropsValues.getGroupId());

		_userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		_userSystemObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				_userSystemObjectDefinitionManager.getName());
	}

	@After
	public void tearDown() throws Exception {
		for (ObjectRelationship objectRelationship : _objectRelationships) {
			_objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship);
		}

		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testDeleteCustomObjectDefinition1WithCustomObjectDefinition2()
		throws Exception {

		Long irrelevantCurrentObjectId = RandomTestUtil.randomLong();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2(
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2(
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2NotFound(
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				irrelevantCurrentObjectId, StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantCurrentObjectId),
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2NotFound(
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				irrelevantCurrentObjectId, StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantCurrentObjectId),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));

		_objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2(
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2NotFound(
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				irrelevantCurrentObjectId, StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantCurrentObjectId),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2NotFound(
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				irrelevantCurrentObjectId, StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey()),
			StringBundler.concat(
				_objectDefinition2.getRESTContextPath(), StringPool.SLASH,
				_objectEntry2.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantCurrentObjectId),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				_objectRelationship.getName()));
	}

	@Test
	public void testDeleteCustomObjectDefinitionWithSystemObjectDefinition()
		throws Exception {

		_testDeleteCustomObjectDefinitionWithSystemObjectDefinition(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testDeleteCustomObjectDefinitionWithSystemObjectDefinition(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testDeleteCustomObjectDefinitionWithSystemObjectDefinitionNotFound(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testDeleteCustomObjectDefinitionWithSystemObjectDefinitionNotFound(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testDeleteCustomObjectEntry() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		user.setEmailAddressVerified(true);

		user = UserLocalServiceUtil.updateUser(user);

		long userId = user.getUserId();

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), userId);

		// Relationship type cascade

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			_objectDefinition1, _objectDefinition2, TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_objectRelationships.add(_objectRelationship);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				Assert.assertEquals(
					403,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.DELETE));
				ResourcePermissionLocalServiceUtil.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					_objectDefinition1.getClassName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					role.getRoleId(),
					new String[] {
						ActionKeys.DELETE, ActionKeys.PERMISSIONS,
						ActionKeys.UPDATE, ActionKeys.VIEW
					});
				JSONAssert.assertEquals(
					JSONUtil.put(
						"status", "BAD_REQUEST"
					).put(
						"title",
						StringBundler.concat(
							"User ", userId,
							" must have DELETE permission for ",
							_objectDefinition2.getClassName(), " ",
							_objectEntry2.getObjectEntryId())
					).toString(),
					HTTPTestUtil.invokeToJSONObject(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.DELETE
					).toString(),
					JSONCompareMode.LENIENT);
				ResourcePermissionLocalServiceUtil.setResourcePermissions(
					TestPropsValues.getCompanyId(),
					_objectDefinition2.getClassName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					role.getRoleId(), new String[] {ActionKeys.VIEW});
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition2.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry2.getExternalReferenceCode()),
						Http.Method.GET));
			}
		);

		// Relationship type disassociate

		_objectRelationship =
			ObjectRelationshipTestUtil.updateObjectRelationship(
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				_objectRelationship.getObjectRelationshipId());

		_objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_1, _OBJECT_FIELD_VALUE_1);

		_objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition2, _OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_2);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				Assert.assertEquals(
					204,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.DELETE));
				Assert.assertEquals(
					404,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.GET));
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition2.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry2.getExternalReferenceCode()),
						Http.Method.GET));
			}
		);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				_objectDefinition1, _objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_objectRelationships.add(objectRelationship);

		_objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_1, _OBJECT_FIELD_VALUE_1);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			objectRelationship, TestPropsValues.getUserId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				Assert.assertEquals(
					204,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.DELETE));
				Assert.assertEquals(
					404,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.GET));
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition2.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry2.getExternalReferenceCode()),
						Http.Method.GET));
			}
		);

		// Relationship type prevent

		_objectRelationship =
			ObjectRelationshipTestUtil.updateObjectRelationship(
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
				_objectRelationship.getObjectRelationshipId());

		_objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_1, _OBJECT_FIELD_VALUE_1);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			_objectRelationship, TestPropsValues.getUserId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONAssert.assertEquals(
					JSONUtil.put(
						"status", "BAD_REQUEST"
					).put(
						"title",
						StringBundler.concat(
							"The prevent deletion type in the object ",
							"relationship ", _objectRelationship.getName(),
							" with object definition ",
							_objectDefinition2.getShortName(),
							" is preventing this object entry from being ",
							"deleted.")
					).toString(),
					HTTPTestUtil.invokeToJSONObject(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.DELETE
					).toString(),
					JSONCompareMode.LENIENT);
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.GET));
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition2.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry2.getExternalReferenceCode()),
						Http.Method.GET));
			}
		);

		objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _objectDefinition2, TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_objectRelationships.add(objectRelationship);

		_objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_1, _OBJECT_FIELD_VALUE_1);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			objectRelationship, TestPropsValues.getUserId());

		String objectRelationshipName = objectRelationship.getName();

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONAssert.assertEquals(
					JSONUtil.put(
						"status", "BAD_REQUEST"
					).put(
						"title",
						StringBundler.concat(
							"The prevent deletion type in the object ",
							"relationship ", objectRelationshipName,
							" with object definition ",
							_objectDefinition2.getShortName(),
							" is preventing this object entry from being ",
							"deleted.")
					).toString(),
					HTTPTestUtil.invokeToJSONObject(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.DELETE
					).toString(),
					JSONCompareMode.LENIENT);
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition1.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry1.getExternalReferenceCode()),
						Http.Method.GET));
				Assert.assertEquals(
					200,
					HTTPTestUtil.invokeToHttpCode(
						null,
						StringBundler.concat(
							_objectDefinition2.getRESTContextPath(),
							"/by-external-reference-code/",
							_objectEntry2.getExternalReferenceCode()),
						Http.Method.GET));
			}
		);
	}

	@Test
	public void testGetRelatedCustomObjectEntriesWhenRelationExists()
		throws Exception {

		// Many to many relationships

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(_objectEntry2, jsonObject.getJSONArray("items"));

		objectRelationship = _addObjectRelationship(
			_objectDefinition2, _objectDefinition1,
			_objectEntry2.getPrimaryKey(), _objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(_objectEntry2, jsonObject.getJSONArray("items"));

		// One to many relationship

		objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(_objectEntry2, jsonObject.getJSONArray("items"));
	}

	@Test
	public void testGetRelatedCustomObjectEntriesWithARegularRole()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		user.setEmailAddressVerified(true);

		user = UserLocalServiceUtil.updateUser(user);

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), user.getUserId());

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectEntry1.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectEntry1.getPrimaryKey()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});
		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectEntry2.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectEntry2.getPrimaryKey()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			this::testGetRelatedCustomObjectEntriesWhenRelationExists
		);
	}

	@Test
	public void testGetRelatedCustomObjectEntriesWithPagination()
		throws Exception {

		// Many to many relationships

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry3.getPrimaryKey(),
			objectRelationship, TestPropsValues.getUserId());

		_assertPagination(_objectEntry2, objectRelationship);

		objectRelationship = _addObjectRelationship(
			_objectDefinition2, _objectDefinition1,
			_objectEntry2.getPrimaryKey(), _objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry3.getPrimaryKey(), _objectEntry1.getPrimaryKey(),
			objectRelationship, TestPropsValues.getUserId());

		_assertPagination(_objectEntry2, objectRelationship);

		// One to many relationship

		objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			_objectEntry1.getPrimaryKey(), _objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _objectEntry3.getPrimaryKey(),
			objectRelationship, TestPropsValues.getUserId());

		_assertPagination(_objectEntry2, objectRelationship);
	}

	@Test
	public void testGetRelatedObjectEntriesWhenRelationDoesNotExist()
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(StringUtil.randomId()), Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	@Test
	public void testGetRelatedSystemObjectsWhenRelationExists()
		throws Exception {

		_userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				_userSystemObjectDefinitionManager.getName());

		// Many to many relationships

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, relatedObjectDefinition,
			_objectEntry1.getPrimaryKey(), _user1.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(_user1, jsonObject.getJSONArray("items"));

		objectRelationship = _addObjectRelationship(
			relatedObjectDefinition, _objectDefinition1, _user1.getUserId(),
			_objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(_user1, jsonObject.getJSONArray("items"));

		ObjectDefinition siteScopedObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"A" + RandomTestUtil.randomString(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), "able", false)),
				ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());

		_objectDefinitions.add(siteScopedObjectDefinition);

		ObjectEntry siteObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			siteScopedObjectDefinition, "able", RandomTestUtil.randomString());

		objectRelationship = _addObjectRelationship(
			siteScopedObjectDefinition, _objectDefinition1,
			siteObjectEntry.getPrimaryKey(), _objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(siteObjectEntry, jsonObject.getJSONArray("items"));

		// One to many relationship

		objectRelationship = _addObjectRelationship(
			_objectDefinition1, relatedObjectDefinition,
			_objectEntry1.getPrimaryKey(), _user1.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		_assertEquals(_user1, jsonObject.getJSONArray("items"));
	}

	@Test
	public void testGetRelatedSystemObjectsWithPagination() throws Exception {

		// Many to many relationships

		_userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				_userSystemObjectDefinitionManager.getName());

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, relatedObjectDefinition,
			_objectEntry1.getPrimaryKey(), _user1.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _user2.getUserId(),
			objectRelationship, TestPropsValues.getUserId());

		_assertPagination(_user1, objectRelationship);

		objectRelationship = _addObjectRelationship(
			relatedObjectDefinition, _objectDefinition1, _user1.getUserId(),
			_objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_user2.getUserId(), _objectEntry1.getPrimaryKey(),
			objectRelationship, TestPropsValues.getUserId());

		_assertPagination(_user1, objectRelationship);

		// One to many relationship

		objectRelationship = _addObjectRelationship(
			_objectDefinition1, relatedObjectDefinition,
			_objectEntry1.getPrimaryKey(), _user1.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry1.getPrimaryKey(), _user2.getUserId(),
			objectRelationship, TestPropsValues.getUserId());

		_assertPagination(_user1, objectRelationship);
	}

	@Test
	public void testPostCustomObjectEntryWithInvalidNestedSystemObjectEntries()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			// Many to many

			_testPostCustomObjectEntryWithInvalidNestedSystemObjectEntries(
				_addObjectRelationship(
					_userSystemObjectDefinition, _objectDefinition1,
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY),
				false);

			// Many to one

			_testPostCustomObjectEntryWithInvalidNestedSystemObjectEntries(
				_addObjectRelationship(
					_userSystemObjectDefinition, _objectDefinition1,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY),
				true);

			// One to many

			_testPostCustomObjectEntryWithInvalidNestedSystemObjectEntries(
				_addObjectRelationship(
					_objectDefinition1, _userSystemObjectDefinition,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY),
				false);
		}
	}

	@Test
	public void testPostCustomObjectEntryWithNestedSystemObjectEntry()
		throws Exception {

		// Many to many

		ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, _userSystemObjectDefinition,
			_SYSTEM_OBJECT_FIELD_NAME_1);

		_testPostCustomObjectEntryWithNestedSystemObjectEntry(
			false,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY));

		// Many to one

		_testPostCustomObjectEntryWithNestedSystemObjectEntry(
			true,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		// One to many

		_testPostCustomObjectEntryWithNestedSystemObjectEntry(
			false,
			_addObjectRelationship(
				_objectDefinition1, _userSystemObjectDefinition,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));
	}

	@Test
	public void testPutCustomObjectEntryUnlinkNestedSystemObjectEntries()
		throws Exception {

		// Many to many

		_testPutCustomObjectEntryUnlinkNestedSystemObjectEntries(
			false,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY));

		// Many to one

		_testPutCustomObjectEntryUnlinkNestedSystemObjectEntries(
			true,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		// One to many

		_testPutCustomObjectEntryUnlinkNestedSystemObjectEntries(
			false,
			_addObjectRelationship(
				_objectDefinition1, _userSystemObjectDefinition,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));
	}

	@Test
	public void testPutCustomObjectEntryUnlinkNestedSystemObjectEntriesByExternalReferenceCode()
		throws Exception {

		// Many to many

		_testPutCustomObjectEntryUnlinkNestedSystemObjectEntriesByExternalReferenceCode(
			false,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY));

		// Many to one

		_testPutCustomObjectEntryUnlinkNestedSystemObjectEntriesByExternalReferenceCode(
			true,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		// One to many

		_testPutCustomObjectEntryUnlinkNestedSystemObjectEntriesByExternalReferenceCode(
			false,
			_addObjectRelationship(
				_objectDefinition1, _userSystemObjectDefinition,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));
	}

	@Test
	public void testPutCustomObjectEntryWithNestedSystemObjectEntry()
		throws Exception {

		// Many to many

		ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, _userSystemObjectDefinition,
			_SYSTEM_OBJECT_FIELD_NAME_2);

		_testPutCustomObjectEntryWithNestedSystemObjectEntry(
			false,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY));

		// Many to one

		_testPutCustomObjectEntryWithNestedSystemObjectEntry(
			true,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		// One to many

		_testPutCustomObjectEntryWithNestedSystemObjectEntry(
			false,
			_addObjectRelationship(
				_objectDefinition1, _userSystemObjectDefinition,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));
	}

	@Test
	public void testPutCustomObjectEntryWithNestedSystemObjectEntryByExternalReferenceCode()
		throws Exception {

		// Many to many

		ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, _userSystemObjectDefinition,
			_SYSTEM_OBJECT_FIELD_NAME_3);

		_testPutCustomObjectEntryWithNestedSystemObjectEntryByExternalReferenceCode(
			false,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY));

		// Many to one

		_testPutCustomObjectEntryWithNestedSystemObjectEntryByExternalReferenceCode(
			true,
			_addObjectRelationship(
				_userSystemObjectDefinition, _objectDefinition1,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		// One to many

		_testPutCustomObjectEntryWithNestedSystemObjectEntryByExternalReferenceCode(
			false,
			_addObjectRelationship(
				_objectDefinition1, _userSystemObjectDefinition,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));
	}

	@Test
	public void testPutObjectEntryRelatedObjectEntry() throws Exception {

		// Many to many relationship

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship1.getName()), Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, jsonArray.length());

		_assertEquals(
			_objectEntry2,
			HTTPTestUtil.invokeToJSONObject(
				null,
				_getEndpoint(
					objectRelationship1.getName(),
					_objectEntry2.getPrimaryKey()),
				Http.Method.PUT));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship1.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		_assertEquals(_objectEntry2, jsonArray);

		// Many to many and one to many relationships

		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			_objectDefinition1, _objectDefinition3,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_assertEquals(
			_objectEntry4,
			HTTPTestUtil.invokeToJSONObject(
				null,
				_getEndpoint(
					objectRelationship2.getName(),
					_objectEntry4.getPrimaryKey()),
				Http.Method.PUT));

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					_OBJECT_FIELD_NAME_2, _NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship1.getName(),
					JSONUtil.putAll(
						JSONUtil.put(
							_OBJECT_FIELD_NAME_2, _NEW_OBJECT_FIELD_VALUE_1
						).put(
							"externalReferenceCode",
							_objectEntry2.getExternalReferenceCode()
						),
						JSONUtil.put(
							_OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_3))
				).put(
					objectRelationship2.getName(),
					JSONUtil.putAll(
						JSONUtil.put(
							_OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_1))
				).toString(),
				_objectDefinition1.getRESTContextPath() + StringPool.SLASH +
					_objectEntry1.getPrimaryKey(),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_2, _NEW_OBJECT_FIELD_VALUE_1
					).put(
						"externalReferenceCode",
						_objectEntry2.getExternalReferenceCode()
					),
					JSONUtil.put(_OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_3))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 2
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, _getEndpoint(objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(_OBJECT_FIELD_NAME_2, _OBJECT_FIELD_VALUE_1))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, _getEndpoint(objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					_OBJECT_FIELD_NAME_2, _NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship1.getName(), JSONUtil.putAll()
				).put(
					objectRelationship2.getName(),
					JSONUtil.putAll(
						JSONUtil.put(
							_OBJECT_FIELD_NAME_2, _NEW_OBJECT_FIELD_VALUE_1))
				).toString(),
				_objectDefinition1.getRESTContextPath() + StringPool.SLASH +
					_objectEntry1.getPrimaryKey(),
				Http.Method.PUT));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship1.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, jsonArray.length());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_2, _NEW_OBJECT_FIELD_VALUE_1))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, _getEndpoint(objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// One to many relationship

		objectRelationship1 = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship1.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, jsonArray.length());

		_assertEquals(
			_objectEntry2,
			HTTPTestUtil.invokeToJSONObject(
				null,
				_getEndpoint(
					objectRelationship1.getName(),
					_objectEntry2.getPrimaryKey()),
				Http.Method.PUT));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship1.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		_assertEquals(_objectEntry2, jsonArray);
	}

	@Test
	public void testPutObjectEntryRelatedObjectEntryDraft() throws Exception {
		_enableObjectEntryDraft(_objectDefinition1);
		_enableObjectEntryDraft(_objectDefinition2);

		long objectEntryId1 = _addObjectEntryDraft(
			_objectDefinition1, _OBJECT_FIELD_NAME_1);
		long objectEntryId2 = _addObjectEntryDraft(
			_objectDefinition2, _OBJECT_FIELD_NAME_2);

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			String.format(
				"%s/%d/%s/%d", _objectDefinition1.getRESTContextPath(),
				objectEntryId1, objectRelationship.getName(), objectEntryId2),
			Http.Method.PUT);

		Assert.assertEquals(objectEntryId2, jsonObject.getLong("id"));

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		Assert.assertEquals(
			objectEntryId1, jsonObject.getLong(objectField.getName()));

		JSONObject statusJSONObject = jsonObject.getJSONObject("status");

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, statusJSONObject.getInt("code"));
	}

	@Test
	public void testPutObjectEntryRelatedSystemObject() throws Exception {
		_userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				_userSystemObjectDefinitionManager.getName());

		// Many to many relationship

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, relatedObjectDefinition,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, jsonArray.length());

		_assertEquals(
			_user1,
			HTTPTestUtil.invokeToJSONObject(
				null,
				_getEndpoint(objectRelationship.getName(), _user1.getUserId()),
				Http.Method.PUT));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		_assertEquals(_user1, jsonArray);

		// One to many relationship

		objectRelationship = _addObjectRelationship(
			_objectDefinition1, relatedObjectDefinition,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, jsonArray.length());

		_assertEquals(
			_user1,
			HTTPTestUtil.invokeToJSONObject(
				"{}",
				_getEndpoint(objectRelationship.getName(), _user1.getUserId()),
				Http.Method.PUT));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		jsonArray = jsonObject.getJSONArray("items");

		_assertEquals(_user1, jsonArray);
	}

	private long _addObjectEntryDraft(
			ObjectDefinition objectDefinition, String objectFieldName)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				objectFieldName, RandomTestUtil.randomString()
			).put(
				"status", JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		return jsonObject.getLong("id");
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, long primaryKey1,
			long primaryKey2, String type)
		throws Exception {

		ObjectRelationship objectRelationship = _addObjectRelationship(
			objectDefinition1, objectDefinition2, type);

		ObjectRelationshipTestUtil.relateObjectEntries(
			primaryKey1, primaryKey2, objectRelationship,
			TestPropsValues.getUserId());

		return objectRelationship;
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(), type);

		_objectRelationships.add(objectRelationship);

		return objectRelationship;
	}

	private <T> void _assertEquals(
		BaseModel<T> baseModel, JSONArray jsonArray) {

		Assert.assertEquals(1, jsonArray.length());

		_assertEquals(baseModel, jsonArray.getJSONObject(0));
	}

	private <T> void _assertEquals(
		BaseModel<T> baseModel, JSONObject jsonObject) {

		Assert.assertEquals(
			baseModel.getPrimaryKeyObj(), jsonObject.getLong("id"));
	}

	private void _assertPagination(
			BaseModel<?> baseModel, ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			_getEndpoint(objectRelationship.getName()) + "?page=1&pageSize=1",
			Http.Method.GET);

		_assertEquals(baseModel, jsonObject.getJSONArray("items"));

		Assert.assertEquals(2, jsonObject.getLong("lastPage"));
		Assert.assertEquals(1, jsonObject.getLong("page"));
		Assert.assertEquals(1, jsonObject.getLong("pageSize"));
		Assert.assertEquals(2, jsonObject.getLong("totalCount"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			_getEndpoint(objectRelationship.getName()) + "?page=0&pageSize=0",
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(2, itemsJSONArray.length());
	}

	private void _assertSystemObjectEntry(
		JSONObject systemObjectEntryJSONObject, String systemObjectFieldName,
		String systemObjectFieldValue, UserAccount userAccount) {

		Assert.assertEquals(
			systemObjectEntryJSONObject.get(systemObjectFieldName),
			systemObjectFieldValue);
		Assert.assertEquals(
			systemObjectEntryJSONObject.get("emailAddress"),
			userAccount.getEmailAddress());
	}

	private JSONObject _createSystemObjectEntryJSONObject(
			String systemObjectFieldName, String systemObjectFieldValue,
			UserAccount userAccount)
		throws Exception {

		JSONObject userAccountJSONObject = JSONFactoryUtil.createJSONObject(
			userAccount.toString());

		return userAccountJSONObject.put(
			systemObjectFieldName, systemObjectFieldValue);
	}

	private void _enableObjectEntryDraft(ObjectDefinition objectDefinition) {
		objectDefinition.setEnableObjectEntryDraft(true);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private String _getEndpoint(String name) {
		return StringBundler.concat(
			_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
			_objectEntry1.getObjectEntryId(), StringPool.SLASH, name);
	}

	private String _getEndpoint(String name, long primaryKey) {
		return StringBundler.concat(
			_getEndpoint(name), StringPool.SLASH, primaryKey);
	}

	private String _getEndpoint(
		String objectEntryId, ObjectRelationship objectRelationship,
		ObjectDefinition objectDefinition) {

		return StringBundler.concat(
			objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntryId, "?nestedFields=", objectRelationship.getName());
	}

	private String _getSystemObjectEntryId(
			String customObjectEntryId, boolean manyToOne,
			ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject systemObjectEntryJSONObject = null;

		JSONObject customObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				null,
				_getEndpoint(
					customObjectEntryId, objectRelationship,
					_objectDefinition1),
				Http.Method.GET);

		if (manyToOne) {
			systemObjectEntryJSONObject =
				customObjectEntryJSONObject.getJSONObject(
					objectRelationship.getName());
		}
		else {
			JSONArray jsonArray = customObjectEntryJSONObject.getJSONArray(
				objectRelationship.getName());

			systemObjectEntryJSONObject = jsonArray.getJSONObject(0);
		}

		return systemObjectEntryJSONObject.getString("id");
	}

	private void _testDeleteCustomObjectDefinition1WithCustomObjectDefinition2(
			String deleteEndpoint, String getEndpoint)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getEndpoint, Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		HTTPTestUtil.invokeToJSONObject(
			null, deleteEndpoint, Http.Method.DELETE);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getEndpoint, Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	private void
			_testDeleteCustomObjectDefinition1WithCustomObjectDefinition2NotFound(
				String deleteEndpoint1, String deleteEndpoint2,
				String getEndpoint)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, deleteEndpoint1, Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, deleteEndpoint2, Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getEndpoint, Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());
	}

	private void _testDeleteCustomObjectDefinitionWithSystemObjectDefinition(
			String type)
		throws Exception {

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, _userSystemObjectDefinition,
			_objectEntry1.getPrimaryKey(), _user1.getUserId(), type);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
				_user1.getUserId()),
			Http.Method.DELETE);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	private void
			_testDeleteCustomObjectDefinitionWithSystemObjectDefinitionNotFound(
				String type)
		throws Exception {

		Long irrelevantPrimaryKey = RandomTestUtil.randomLong();

		Long irrelevantUserId = RandomTestUtil.randomLong();

		ObjectRelationship objectRelationship = _addObjectRelationship(
			_objectDefinition1, _userSystemObjectDefinition,
			_objectEntry1.getPrimaryKey(), _user1.getUserId(), type);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				irrelevantPrimaryKey, StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
				_user1.getUserId()),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				_objectEntry1.getPrimaryKey(), StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
				irrelevantUserId),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getEndpoint(objectRelationship.getName()), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());
	}

	private void _testPostCustomObjectEntryWithInvalidNestedSystemObjectEntries(
			ObjectRelationship objectRelationship, boolean manyToOne)
		throws Exception {

		// Flip manyToOne to ensure invalid nested system object entries

		manyToOne = !manyToOne;

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_toBody(
				manyToOne, objectRelationship,
				JSONFactoryUtil.createJSONObject(
					String.valueOf(UserAccountTestUtil.randomUserAccount()))),
			_objectDefinition1.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.get("status"));
	}

	private void _testPostCustomObjectEntryWithNestedSystemObjectEntry(
			boolean manyToOne, ObjectRelationship objectRelationship)
		throws Exception {

		UserAccount userAccount = UserAccountTestUtil.randomUserAccount();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_toBody(
				manyToOne, objectRelationship,
				_createSystemObjectEntryJSONObject(
					_SYSTEM_OBJECT_FIELD_NAME_1, _SYSTEM_OBJECT_FIELD_VALUE,
					userAccount)),
			_objectDefinition1.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		if (manyToOne) {
			JSONObject systemObjectEntryJSONObject = jsonObject.getJSONObject(
				objectRelationship.getName());

			Assert.assertEquals(
				systemObjectEntryJSONObject.get("emailAddress"),
				userAccount.getEmailAddress());
		}
		else {
			JSONArray relatedSystemObjectEntriesJSONArray =
				jsonObject.getJSONArray(objectRelationship.getName());

			_assertSystemObjectEntry(
				relatedSystemObjectEntriesJSONArray.getJSONObject(0),
				_SYSTEM_OBJECT_FIELD_NAME_1, _SYSTEM_OBJECT_FIELD_VALUE,
				userAccount);
		}

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_assertSystemObjectEntry(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					jaxRsApplicationDescriptor.getRESTContextPath(),
					StringPool.SLASH,
					_getSystemObjectEntryId(
						jsonObject.getString("id"), manyToOne,
						objectRelationship)),
				Http.Method.GET),
			_SYSTEM_OBJECT_FIELD_NAME_1, _SYSTEM_OBJECT_FIELD_VALUE,
			userAccount);
	}

	private void _testPutCustomObjectEntryUnlinkNestedSystemObjectEntries(
			boolean manyToOne, ObjectRelationship objectRelationship)
		throws Exception {

		UserAccount postUserAccount = UserAccountTestUtil.randomUserAccount();

		JSONObject customObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				_toBody(
					manyToOne, objectRelationship,
					JSONFactoryUtil.createJSONObject(
						postUserAccount.toString())),
				_objectDefinition1.getRESTContextPath(), Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_toBody(manyToOne, objectRelationship, null),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				customObjectEntryJSONObject.getString("id")),
			Http.Method.PUT);

		if (manyToOne) {
			JSONObject systemObjectEntryJSONObject = jsonObject.getJSONObject(
				objectRelationship.getName());

			Assert.assertNull(systemObjectEntryJSONObject);
		}
		else {
			JSONArray relatedSystemObjectEntriesJSONArray =
				jsonObject.getJSONArray(objectRelationship.getName());

			Assert.assertEquals(
				0, relatedSystemObjectEntriesJSONArray.length());
		}
	}

	private void
			_testPutCustomObjectEntryUnlinkNestedSystemObjectEntriesByExternalReferenceCode(
				boolean manyToOne, ObjectRelationship objectRelationship)
		throws Exception {

		UserAccount postUserAccount = UserAccountTestUtil.randomUserAccount();

		JSONObject customObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				_toBody(
					manyToOne, objectRelationship,
					JSONFactoryUtil.createJSONObject(
						postUserAccount.toString())),
				_objectDefinition1.getRESTContextPath(), Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_toBody(manyToOne, objectRelationship, null),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(),
				"/by-external-reference-code/",
				customObjectEntryJSONObject.getString("externalReferenceCode")),
			Http.Method.PUT);

		if (manyToOne) {
			JSONObject systemObjectEntryJSONObject = jsonObject.getJSONObject(
				objectRelationship.getName());

			Assert.assertNull(systemObjectEntryJSONObject);
		}
		else {
			JSONArray relatedSystemObjectEntriesJSONArray =
				jsonObject.getJSONArray(objectRelationship.getName());

			Assert.assertEquals(
				0, relatedSystemObjectEntriesJSONArray.length());
		}
	}

	private void _testPutCustomObjectEntryWithNestedSystemObjectEntry(
			boolean manyToOne, ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject customObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				_toBody(
					manyToOne, objectRelationship,
					_createSystemObjectEntryJSONObject(
						_SYSTEM_OBJECT_FIELD_NAME_2, _SYSTEM_OBJECT_FIELD_VALUE,
						UserAccountTestUtil.randomUserAccount())),
				_objectDefinition1.getRESTContextPath(), Http.Method.POST);

		String customObjectEntryId = customObjectEntryJSONObject.getString(
			"id");

		UserAccount putUserAccount = UserAccountTestUtil.randomUserAccount();

		putUserAccount.setEmailAddress(
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com");
		putUserAccount.setExternalReferenceCode(
			() -> {
				JSONObject systemObjectEntryJSONObject =
					HTTPTestUtil.invokeToJSONObject(
						null,
						_getEndpoint(
							customObjectEntryId, objectRelationship,
							_objectDefinition1),
						Http.Method.GET);

				if (manyToOne) {
					return systemObjectEntryJSONObject.getString(
						StringBundler.concat(
							"r_", objectRelationship.getName(), "_",
							StringUtil.replaceLast(
								_userSystemObjectDefinition.
									getPKObjectFieldName(),
								"Id", "ERC")));
				}

				JSONArray jsonArray = systemObjectEntryJSONObject.getJSONArray(
					objectRelationship.getName());

				systemObjectEntryJSONObject = jsonArray.getJSONObject(0);

				return systemObjectEntryJSONObject.getString(
					"externalReferenceCode");
			});

		String systemObjectFieldValue = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_toBody(
				manyToOne, objectRelationship,
				_createSystemObjectEntryJSONObject(
					_SYSTEM_OBJECT_FIELD_NAME_2, systemObjectFieldValue,
					putUserAccount)),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(), StringPool.SLASH,
				customObjectEntryId),
			Http.Method.PUT);

		if (manyToOne) {
			JSONObject systemObjectEntryJSONObject = jsonObject.getJSONObject(
				objectRelationship.getName());

			Assert.assertEquals(
				systemObjectEntryJSONObject.get("emailAddress"),
				putUserAccount.getEmailAddress());
		}
		else {
			JSONArray relatedSystemObjectEntriesJSONArray =
				jsonObject.getJSONArray(objectRelationship.getName());

			Assert.assertEquals(
				1, relatedSystemObjectEntriesJSONArray.length());

			_assertSystemObjectEntry(
				relatedSystemObjectEntriesJSONArray.getJSONObject(0),
				_SYSTEM_OBJECT_FIELD_NAME_2, systemObjectFieldValue,
				putUserAccount);
		}

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_assertSystemObjectEntry(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					jaxRsApplicationDescriptor.getRESTContextPath(),
					StringPool.SLASH,
					_getSystemObjectEntryId(
						customObjectEntryId, manyToOne, objectRelationship)),
				Http.Method.GET),
			_SYSTEM_OBJECT_FIELD_NAME_2, systemObjectFieldValue,
			putUserAccount);
	}

	private void
			_testPutCustomObjectEntryWithNestedSystemObjectEntryByExternalReferenceCode(
				boolean manyToOne, ObjectRelationship objectRelationship)
		throws Exception {

		JSONObject customObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				_toBody(
					manyToOne, objectRelationship,
					_createSystemObjectEntryJSONObject(
						_SYSTEM_OBJECT_FIELD_NAME_3, _SYSTEM_OBJECT_FIELD_VALUE,
						UserAccountTestUtil.randomUserAccount())),
				_objectDefinition1.getRESTContextPath(), Http.Method.POST);

		UserAccount putUserAccount = UserAccountTestUtil.randomUserAccount();

		putUserAccount.setEmailAddress(
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com");
		putUserAccount.setExternalReferenceCode(
			() -> {
				JSONObject systemObjectEntryJSONObject =
					HTTPTestUtil.invokeToJSONObject(
						null,
						_getEndpoint(
							customObjectEntryJSONObject.getString("id"),
							objectRelationship, _objectDefinition1),
						Http.Method.GET);

				if (manyToOne) {
					return systemObjectEntryJSONObject.getString(
						StringBundler.concat(
							"r_", objectRelationship.getName(), "_",
							StringUtil.replaceLast(
								_userSystemObjectDefinition.
									getPKObjectFieldName(),
								"Id", "ERC")));
				}

				JSONArray jsonArray = systemObjectEntryJSONObject.getJSONArray(
					objectRelationship.getName());

				systemObjectEntryJSONObject = jsonArray.getJSONObject(0);

				return systemObjectEntryJSONObject.getString(
					"externalReferenceCode");
			});

		String systemObjectFieldValue = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_toBody(
				manyToOne, objectRelationship,
				_createSystemObjectEntryJSONObject(
					_SYSTEM_OBJECT_FIELD_NAME_3, systemObjectFieldValue,
					putUserAccount)),
			StringBundler.concat(
				_objectDefinition1.getRESTContextPath(),
				"/by-external-reference-code/",
				customObjectEntryJSONObject.getString("externalReferenceCode")),
			Http.Method.PUT);

		if (manyToOne) {
			JSONObject systemObjectEntryJSONObject = jsonObject.getJSONObject(
				objectRelationship.getName());

			Assert.assertEquals(
				systemObjectEntryJSONObject.get("emailAddress"),
				putUserAccount.getEmailAddress());
		}
		else {
			JSONArray relatedSystemObjectEntriesJSONArray =
				jsonObject.getJSONArray(objectRelationship.getName());

			Assert.assertEquals(
				1, relatedSystemObjectEntriesJSONArray.length());

			_assertSystemObjectEntry(
				relatedSystemObjectEntriesJSONArray.getJSONObject(0),
				_SYSTEM_OBJECT_FIELD_NAME_3, systemObjectFieldValue,
				putUserAccount);
		}

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_assertSystemObjectEntry(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					jaxRsApplicationDescriptor.getRESTContextPath(),
					StringPool.SLASH,
					_getSystemObjectEntryId(
						customObjectEntryJSONObject.getString("id"), manyToOne,
						objectRelationship)),
				Http.Method.GET),
			_SYSTEM_OBJECT_FIELD_NAME_3, systemObjectFieldValue,
			putUserAccount);
	}

	private String _toBody(
		boolean manyToOne, ObjectRelationship objectRelationship,
		JSONObject userAccountJSONObject) {

		if (userAccountJSONObject != null) {
			return JSONUtil.put(
				objectRelationship.getName(),
				manyToOne ? userAccountJSONObject :
					JSONUtil.put(userAccountJSONObject)
			).toString();
		}

		return JSONUtil.put(
			objectRelationship.getName(),
			manyToOne ? JSONFactoryUtil.createJSONObject() :
				JSONFactoryUtil.createJSONArray()
		).toString();
	}

	private static final String _NEW_OBJECT_FIELD_VALUE_1 =
		RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_1 =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_2 =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE_1 =
		RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE_2 =
		RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE_3 =
		RandomTestUtil.randomString();

	private static final String _SYSTEM_OBJECT_FIELD_NAME_1 =
		"x" + RandomTestUtil.randomString();

	private static final String _SYSTEM_OBJECT_FIELD_NAME_2 =
		"x" + RandomTestUtil.randomString();

	private static final String _SYSTEM_OBJECT_FIELD_NAME_3 =
		"x" + RandomTestUtil.randomString();

	private static final String _SYSTEM_OBJECT_FIELD_VALUE =
		RandomTestUtil.randomString();

	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;
	private ObjectDefinition _objectDefinition3;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();
	private ObjectEntry _objectEntry1;
	private ObjectEntry _objectEntry2;
	private ObjectEntry _objectEntry3;
	private ObjectEntry _objectEntry4;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private final List<ObjectRelationship> _objectRelationships =
		new ArrayList<>();

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private User _user1;
	private User _user2;
	private ObjectDefinition _userSystemObjectDefinition;
	private SystemObjectDefinitionManager _userSystemObjectDefinitionManager;

}