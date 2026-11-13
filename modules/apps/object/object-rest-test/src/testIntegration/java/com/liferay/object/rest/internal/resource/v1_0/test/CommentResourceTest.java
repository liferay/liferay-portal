/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@RunWith(Arquillian.class)
public class CommentResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_testGroupId = TestPropsValues.getGroupId();
	}

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME, false)),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, _OBJECT_FIELD_NAME, _OBJECT_FIELD_VALUE);

		_siteScopedObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), _OBJECT_FIELD_NAME,
						false)),
				ObjectDefinitionConstants.SCOPE_SITE);

		_siteObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			_siteScopedObjectDefinition, _OBJECT_FIELD_NAME,
			_OBJECT_FIELD_VALUE);
	}

	@After
	public void tearDown() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			_siteScopedObjectDefinition);
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testDeleteByExternalReferenceCodeComment() throws Exception {

		// Company scope

		_testDeleteByExternalReferenceCodeComment(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testDeleteByExternalReferenceCodeComment(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testGetByExternalReferenceCodeComment() throws Exception {

		// Company scope

		_testGetByExternalReferenceCodeComment(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testGetByExternalReferenceCodeComment(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testGetByExternalReferenceCodeCommentChildCommentsPage()
		throws Exception {

		// Company scope

		_testGetByExternalReferenceCodeCommentChildCommentsPage(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testGetByExternalReferenceCodeCommentChildCommentsPage(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testGetByExternalReferenceCodeCommentsPage() throws Exception {

		// Company scope

		_testGetByExternalReferenceCodeCommentsPage(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testGetByExternalReferenceCodeCommentsPage(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-43996"), @FeatureFlag(value = "LPD-72278")
		}
	)
	@Test
	public void testGetByExternalReferenceCodeCommentsPageWithDraftObjectEntry()
		throws Exception {

		_objectDefinition.setEnableComments(true);
		_objectDefinition.setEnableObjectEntryDraft(true);
		_objectDefinition.setEnableObjectEntryVersioning(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			_getEndpoint(
				_objectDefinition,
				_objectEntryLocalService.addDraftObjectEntry(
					TestPropsValues.getUserId(),
					_objectEntry.getObjectEntryId(), RandomTestUtil.randomInt(),
					Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext()),
				0),
			Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostByExternalReferenceCodeComment() throws Exception {

		// Company scope

		_testPostByExternalReferenceCodeComment(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testPostByExternalReferenceCodeComment(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostByExternalReferenceCodeCommentReplyComment()
		throws Exception {

		// Company scope

		_testPostByExternalReferenceCodeCommentReplyComment(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testPostByExternalReferenceCodeCommentReplyComment(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPutByExternalReferenceCodeComment() throws Exception {

		// Company scope

		_testPutByExternalReferenceCodeComment(
			0, _objectDefinition, _objectEntry);

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testPutByExternalReferenceCodeComment(
				_testGroupId, _siteScopedObjectDefinition, _siteObjectEntry);
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	private void _assertCommentsPageWithAggregation(
			JSONObject creatorJSONObject, String endpoint)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, endpoint + "?aggregationTerms=creatorId", Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("facets");

		Assert.assertEquals(1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("creatorId", jsonObject.getString("facetCriteria"));

		jsonArray = jsonObject.getJSONArray("facetValues");

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(2, jsonObject.getInt("numberOfOccurrences"));

		Assert.assertEquals(
			creatorJSONObject.getInt("id"), jsonObject.getInt("term"));
	}

	private void _assertCommentsPageWithSearch(
			String endpoint, String externalReferenceCode, String keywords)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?search=", URLCodec.encodeURL(keywords)),
			Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			externalReferenceCode,
			jsonObject.getString("externalReferenceCode"));
	}

	private void _assertCommentsPageWithSort(
			String endpoint, JSONObject jsonObject1, JSONObject jsonObject2)
		throws Exception {

		JSONObject jsonObject3 = HTTPTestUtil.invokeToJSONObject(
			null, endpoint + "?sort=dateModified:asc", Http.Method.GET);

		JSONArray jsonArray = jsonObject3.getJSONArray("items");

		jsonObject3 = jsonArray.getJSONObject(0);

		JSONObject jsonObject4 = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			jsonObject1.getString("externalReferenceCode"),
			jsonObject3.getString("externalReferenceCode"));

		Assert.assertEquals(
			jsonObject2.getString("externalReferenceCode"),
			jsonObject4.getString("externalReferenceCode"));
	}

	private void _assertSize(String endpoint, int expectedSize)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, endpoint, Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(expectedSize, jsonArray.length());
	}

	private void _enableComments(ObjectDefinition objectDefinition) {
		objectDefinition.setEnableComments(true);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private String _getEndpoint(
		ObjectDefinition objectDefinition, Object scopeKey) {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			return objectDefinition.getRESTContextPath() + "/scopes/" +
				scopeKey;
		}

		return objectDefinition.getRESTContextPath();
	}

	private String _getEndpoint(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			long scopeKey)
		throws Exception {

		return StringBundler.concat(
			_getEndpoint(objectDefinition, scopeKey),
			"/by-external-reference-code/",
			objectEntry.getExternalReferenceCode(), "/comments");
	}

	private JSONObject _postComment(
			String endpoint, String externalReferenceCode, String text)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", text
			).toString(),
			endpoint, Http.Method.POST);
	}

	private void _testDeleteByExternalReferenceCodeComment(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		_enableComments(objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			).toString(),
			_getEndpoint(objectDefinition, objectEntry, groupId),
			Http.Method.POST);

		Assert.assertEquals(
			204,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					_getEndpoint(objectDefinition, objectEntry, groupId),
					StringPool.SLASH,
					jsonObject.getString("externalReferenceCode")),
				Http.Method.DELETE));
	}

	private void _testGetByExternalReferenceCodeComment(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		_enableComments(objectDefinition);

		String endpoint = _getEndpoint(objectDefinition, objectEntry, groupId);
		String externalReferenceCode = RandomTestUtil.randomString();

		JSONObject jsonObject1 = _postComment(
			endpoint, externalReferenceCode, RandomTestUtil.randomString());

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, StringPool.SLASH, externalReferenceCode),
			Http.Method.GET);

		Assert.assertEquals(
			jsonObject1.getString("externalReferenceCode"),
			jsonObject2.getString("externalReferenceCode"));

		Assert.assertEquals(
			jsonObject1.getString("text"), jsonObject2.getString("text"));
	}

	private void _testGetByExternalReferenceCodeCommentChildCommentsPage(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		_enableComments(objectDefinition);

		String endpoint = _getEndpoint(objectDefinition, objectEntry, groupId);

		String externalReferenceCode = RandomTestUtil.randomString();

		_postComment(
			endpoint, externalReferenceCode, RandomTestUtil.randomString());

		endpoint = StringBundler.concat(
			endpoint, StringPool.SLASH, externalReferenceCode,
			"/child-comments");

		_assertSize(endpoint, 0);

		JSONObject jsonObject1 = _postComment(
			endpoint, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		externalReferenceCode = RandomTestUtil.randomString();
		String text = RandomTestUtil.randomString();

		JSONObject jsonObject2 = _postComment(
			endpoint, externalReferenceCode, text);

		_assertSize(endpoint, 2);

		jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"text", text
			).toString(),
			StringBundler.concat(
				_getEndpoint(objectDefinition, objectEntry, groupId),
				StringPool.SLASH,
				jsonObject2.getString("externalReferenceCode")),
			Http.Method.PUT);

		_assertCommentsPageWithAggregation(
			jsonObject1.getJSONObject("creator"), endpoint);
		_assertCommentsPageWithSearch(endpoint, externalReferenceCode, text);
		_assertCommentsPageWithSort(endpoint, jsonObject1, jsonObject2);
	}

	private void _testGetByExternalReferenceCodeCommentsPage(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		_enableComments(objectDefinition);

		String endpoint = _getEndpoint(objectDefinition, objectEntry, groupId);

		_assertSize(endpoint, 0);

		JSONObject jsonObject1 = _postComment(
			endpoint, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		String externalReferenceCode = RandomTestUtil.randomString();
		String text = RandomTestUtil.randomString();

		JSONObject jsonObject2 = _postComment(
			endpoint, externalReferenceCode, text);

		_assertSize(endpoint, 2);

		jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"text", text
			).toString(),
			StringBundler.concat(
				endpoint, StringPool.SLASH,
				jsonObject2.getString("externalReferenceCode")),
			Http.Method.PUT);

		_assertCommentsPageWithAggregation(
			jsonObject1.getJSONObject("creator"), endpoint);
		_assertCommentsPageWithSearch(endpoint, externalReferenceCode, text);
		_assertCommentsPageWithSort(endpoint, jsonObject1, jsonObject2);
	}

	private void _testPostByExternalReferenceCodeComment(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		String text = RandomTestUtil.randomString();

		JSONObject bodyJSONObject = JSONUtil.put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"text", text
		);

		String endpoint = _getEndpoint(objectDefinition, objectEntry, groupId);

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				bodyJSONObject.toString(), endpoint, Http.Method.POST));

		_enableComments(objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			bodyJSONObject.toString(), endpoint, Http.Method.POST);

		Assert.assertEquals(
			externalReferenceCode, jsonObject.get("externalReferenceCode"));
		Assert.assertEquals("<p>" + text + "</p>", jsonObject.get("text"));
	}

	private void _testPostByExternalReferenceCodeCommentReplyComment(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		_enableComments(objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			).toString(),
			_getEndpoint(objectDefinition, objectEntry, groupId),
			Http.Method.POST);

		long parentCommentId = jsonObject.getLong("id");

		String externalReferenceCode = RandomTestUtil.randomString();
		String text = RandomTestUtil.randomString();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", text
			).toString(),
			StringBundler.concat(
				_getEndpoint(objectDefinition, objectEntry, groupId),
				StringPool.SLASH, jsonObject.getString("externalReferenceCode"),
				"/child-comments"),
			Http.Method.POST);

		Assert.assertEquals(
			externalReferenceCode, jsonObject.get("externalReferenceCode"));
		Assert.assertEquals("<p>" + text + "</p>", jsonObject.get("text"));
		Assert.assertEquals(
			parentCommentId, jsonObject.getLong("parentCommentId"));
	}

	private void _testPutByExternalReferenceCodeComment(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		_enableComments(objectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			).toString(),
			_getEndpoint(objectDefinition, objectEntry, groupId),
			Http.Method.POST);

		String text = RandomTestUtil.randomString();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"text", text
			).toString(),
			StringBundler.concat(
				_getEndpoint(objectDefinition, objectEntry, groupId),
				StringPool.SLASH,
				jsonObject.getString("externalReferenceCode")),
			Http.Method.PUT);

		Assert.assertEquals("<p>" + text + "</p>", jsonObject.get("text"));
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

	private static final int _OBJECT_FIELD_VALUE = RandomTestUtil.randomInt();

	private static long _testGroupId;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	private ObjectEntry _siteObjectEntry;
	private ObjectDefinition _siteScopedObjectDefinition;

}