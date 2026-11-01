/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Miguel Barcos
 */
@RunWith(Arquillian.class)
public class RelatedObjectEntryResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					"Text", "String", true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME, false)),
			false);

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, _OBJECT_FIELD_NAME, _OBJECT_FIELD_VALUE);

		_user = TestPropsValues.getUser();

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
		_objectRelationshipLocalService.deleteObjectRelationship(
			_objectRelationship);
	}

	@Test
	public void testDeleteManyToManySystemObjectNotFoundRelatedObjectEntry()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();
		Long irrelevantUserId = RandomTestUtil.randomLong();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, irrelevantUserId, StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry.getPrimaryKey()),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());
	}

	@Test
	public void testDeleteManyToManySystemObjectRelatedObjectEntry()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry.getPrimaryKey()),
			Http.Method.DELETE);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	@Test
	public void testDeleteManyToManySystemObjectRelatedObjectEntryNotFound()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		Long irrelevantPrimaryKey = RandomTestUtil.randomLong();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantPrimaryKey),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		// Inactive object definition

		_objectDefinition.setActive(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry.getPrimaryKey()),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	@Test
	public void testDeleteOneToManySystemObjectNotFoundRelatedObjectEntry()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		Long irrelevantUserId = RandomTestUtil.randomLong();

		_objectRelationship = _addObjectRelationship(
			_userSystemObjectDefinition, _objectDefinition, _user.getUserId(),
			_objectEntry.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, irrelevantUserId, StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry.getPrimaryKey()),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		// Inactive object definition

		_objectDefinition.setActive(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry.getPrimaryKey()),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	@Test
	public void testDeleteOneToManySystemObjectRelatedObjectEntry()
		throws Exception {

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_userSystemObjectDefinition, _objectDefinition, _user.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testDeleteOneToManySystemObjectRelatedObjectEntry();

		_objectRelationshipLocalService.deleteObjectRelationship(
			_objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);

		_objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					"Text", "String", true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME, false)));

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_userSystemObjectDefinition, _objectDefinition, _user.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				_user.getUserId(), _objectDefinition.getObjectDefinitionId());

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, _OBJECT_FIELD_NAME, _OBJECT_FIELD_VALUE);

		_testDeleteOneToManySystemObjectRelatedObjectEntry();
	}

	@Test
	public void testDeleteOneToManySystemObjectRelatedObjectEntryNotFound()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_objectRelationship = _addObjectRelationship(
			_userSystemObjectDefinition, _objectDefinition, _user.getUserId(),
			_objectEntry.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Long irrelevantPrimaryKey = RandomTestUtil.randomLong();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantPrimaryKey),
			Http.Method.DELETE);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		// Inactive object definition

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_objectDefinition.setActive(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	@Test
	public void testGetNotFoundSystemObjectRelatedObjectEntries()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();
		Long irrelevantUserId = RandomTestUtil.randomLong();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, irrelevantUserId, StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	@Test
	public void testGetSystemObjectRelatedObjectEntries() throws Exception {
		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			_OBJECT_FIELD_VALUE, itemJSONObject.getString(_OBJECT_FIELD_NAME));
	}

	@Test
	public void testGetSystemObjectRelatedObjectEntriesWithPagination()
		throws Exception {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, _OBJECT_FIELD_NAME, _OBJECT_FIELD_VALUE);

		_objectRelationshipLocalService.addObjectRelationshipMappingTableValues(
			_user.getUserId(), _objectRelationship.getObjectRelationshipId(),
			objectEntry.getPrimaryKey(), _user.getUserId(),
			ServiceContextTestUtil.getServiceContext());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), "?page=1&pageSize=1"),
			Http.Method.GET);

		Assert.assertEquals(2, jsonObject.getLong("lastPage"));
		Assert.assertEquals(1, jsonObject.getLong("page"));
		Assert.assertEquals(1, jsonObject.getLong("pageSize"));
		Assert.assertEquals(2, jsonObject.getLong("totalCount"));
	}

	@Test
	public void testPutSystemObjectRelatedObjectEntry() throws Exception {
		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testPutSystemObjectRelatedObjectEntry(_objectRelationship);

		_objectRelationshipLocalService.deleteObjectRelationship(
			_objectRelationship);

		_objectRelationship = _addObjectRelationship(
			_userSystemObjectDefinition, _objectDefinition, _user.getUserId(),
			_objectEntry.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testPutSystemObjectRelatedObjectEntry(_objectRelationship);
	}

	@Test
	public void testPutSystemObjectRelatedObjectNotFound() throws Exception {
		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		_objectRelationship = _addObjectRelationship(
			_objectDefinition, _userSystemObjectDefinition,
			_objectEntry.getPrimaryKey(), _user.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		Long irrelevantPrimaryKey = RandomTestUtil.randomLong();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				irrelevantPrimaryKey),
			Http.Method.PUT);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		// Inactive object definition

		String objectFieldValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, _OBJECT_FIELD_NAME, objectFieldValue);

		_objectDefinition.setActive(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				objectEntry.getPrimaryKey()),
			Http.Method.PUT);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, long primaryKey1,
			long primaryKey2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2, _user.getUserId(), type);

		ObjectRelationshipTestUtil.relateObjectEntries(
			primaryKey1, primaryKey2, objectRelationship, _user.getUserId());

		return objectRelationship;
	}

	private void _testDeleteOneToManySystemObjectRelatedObjectEntry()
		throws Exception {

		ObjectRelationshipTestUtil.relateObjectEntries(
			_user.getUserId(), _objectEntry.getPrimaryKey(),
			_objectRelationship, _user.getUserId());

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName(), StringPool.SLASH,
				_objectEntry.getPrimaryKey()),
			Http.Method.DELETE);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				_objectRelationship.getName()),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	private void _testPutSystemObjectRelatedObjectEntry(
			ObjectRelationship objectRelationship)
		throws Exception {

		String objectFieldValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, _OBJECT_FIELD_NAME, objectFieldValue);

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			_userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
				objectEntry.getPrimaryKey()),
			Http.Method.PUT);

		Assert.assertEquals(
			objectFieldValue, jsonObject.getString(_OBJECT_FIELD_NAME));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(),
				StringPool.SLASH, _user.getUserId(), StringPool.SLASH,
				objectRelationship.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(2, itemsJSONArray.length());
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE =
		RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;
	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private User _user;
	private ObjectDefinition _userSystemObjectDefinition;
	private SystemObjectDefinitionManager _userSystemObjectDefinitionManager;

}