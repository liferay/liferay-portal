/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.dto.v1_0.Comment;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.SystemProperties;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceCommentTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-43996")
	@Test
	public void testDeleteObjectEntryWithComments() throws Exception {

		// Company scope

		_testDeleteObjectEntryWithComments(0, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testDeleteObjectEntryWithComments(
				testGroupId, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testGetObjectEntriesPageWithComments() throws Exception {

		// Company scope

		_testGetObjectEntriesPageWithComments(0, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testGetObjectEntriesPageWithComments(
				testGroupId, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPatchPutObjectEntryWithComments() throws Exception {

		// Company scope

		_enableComments(getObjectDefinition1());

		_testPatchPutObjectEntryWithComments(
			0, Http.Method.PATCH, getObjectDefinition1());
		_testPatchPutObjectEntryWithComments(
			0, Http.Method.PUT, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_enableComments(getSiteScopedObjectDefinition1());

			_testPatchPutObjectEntryWithComments(
				testGroupId, Http.Method.PATCH,
				getSiteScopedObjectDefinition1());
			_testPatchPutObjectEntryWithComments(
				testGroupId, Http.Method.PUT, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPatchPutObjectEntryWithMissingParentCommentReference()
		throws Exception {

		_enableComments(getObjectDefinition1());

		_testPatchPutObjectEntryWithMissingParentCommentReference(
			Http.Method.PATCH);
		_testPatchPutObjectEntryWithMissingParentCommentReference(
			Http.Method.PUT);
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostObjectEntriesWithComments() throws Exception {

		// Company scope

		_testPostObjectEntriesWithComments(0, getObjectDefinition1());

		// Site scope

		PropsUtil.set("feature.flag.LPD-43996", "false");

		try {
			_testPostObjectEntriesWithComments(
				testGroupId, getSiteScopedObjectDefinition1());
		}
		finally {
			PropsUtil.set("feature.flag.LPD-43996", "true");
		}
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostObjectEntriesWithMissingParentCommentReference()
		throws Exception {

		_enableComments(getObjectDefinition1());

		String parentExternalReferenceCode = RandomTestUtil.randomString();

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
				{
					systemProperties = new SystemProperties() {
						{
							comments = new Comment[] {
								new Comment() {
									{
										externalReferenceCode =
											RandomTestUtil.randomString();
										parentCommentExternalReferenceCode =
											parentExternalReferenceCode;
										text = RandomTestUtil.randomString();
									}
								}
							};
						}
					};
				}
			};

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Assert.assertNull(
			_commentManager.fetchComment(
				company.getGroupId(), parentExternalReferenceCode));

		ObjectEntryResource objectEntryResource = getObjectEntryResource(
			getObjectDefinition1(), TestPropsValues.getUser());

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			objectEntryResource.postObjectEntry(objectEntry);
		}

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_commentManager.getComment(
				company.getGroupId(), parentExternalReferenceCode);

		Assert.assertTrue(serviceBuilderComment instanceof WorkflowableComment);

		WorkflowableComment workflowableComment =
			(WorkflowableComment)serviceBuilderComment;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, workflowableComment.getStatus());

		_commentManager.deleteComment(serviceBuilderComment.getCommentId());
	}

	@FeatureFlag("LPD-43996")
	@Test
	public void testPostObjectEntryWithObjectFieldComments() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"comments", "comments", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		_enableComments(objectDefinition);

		String value = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"comments", value
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(value, jsonObject.getString("comments"));

		objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	private void _enableComments(ObjectDefinition objectDefinition) {
		objectDefinition.setEnableComments(true);

		objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private JSONArray _getCommentsJSONArray(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getEndpoint(objectDefinition, groupId) +
				"?nestedFields=systemProperties.comments",
			Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		jsonObject = jsonArray.getJSONObject(0);

		JSONObject systemPropertiesJSONObject = jsonObject.getJSONObject(
			"systemProperties");

		return systemPropertiesJSONObject.getJSONArray("comments");
	}

	private String _getDeletePatchPutEndpoint(
		long groupId, ObjectDefinition objectDefinition,
		JSONObject objectEntryJSONObject) {

		String endpoint = getEndpoint(objectDefinition, groupId);

		ObjectScopeProvider objectScopeProvider =
			objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware() && (groupId != 0)) {
			return StringBundler.concat(
				endpoint, "/by-external-reference-code/",
				objectEntryJSONObject.getString("externalReferenceCode"));
		}

		return StringBundler.concat(
			endpoint, StringPool.SLASH, objectEntryJSONObject.getLong("id"));
	}

	private JSONArray _patchPutObjectEntryWithComments(
			JSONArray commentsJSONArray, long groupId, Http.Method httpMethod,
			ObjectDefinition objectDefinition, JSONObject objectEntryJSONObject)
		throws Exception {

		String endpoint = _getDeletePatchPutEndpoint(
			groupId, objectDefinition, objectEntryJSONObject);

		endpoint = endpoint + "?nestedFields=systemProperties.comments";

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"systemProperties", JSONUtil.put("comments", commentsJSONArray)
			).toString(),
			endpoint, httpMethod);

		JSONObject systemPropertiesJSONObject = jsonObject.getJSONObject(
			"systemProperties");

		return systemPropertiesJSONObject.getJSONArray("comments");
	}

	private JSONArray _postObjectEntryWithComments(
			JSONArray commentsJSONArray, long groupId, boolean nestedFields,
			ObjectDefinition objectDefinition)
		throws Exception {

		JSONArray jsonArray = null;

		String endpoint = getEndpoint(objectDefinition, groupId);

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=systemProperties.comments";
		}

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"systemProperties", JSONUtil.put("comments", commentsJSONArray)
			).toString(),
			endpoint, Http.Method.POST);

		JSONObject systemPropertiesJSONObject = jsonObject.getJSONObject(
			"systemProperties");

		if (systemPropertiesJSONObject != null) {
			jsonArray = systemPropertiesJSONObject.getJSONArray("comments");
		}

		if (jsonArray == null) {
			return JSONFactoryUtil.createJSONArray();
		}

		return jsonArray;
	}

	private void _testDeleteObjectEntryWithComments(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		_enableComments(objectDefinition);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONArray commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			));

		String endpoint = getEndpoint(objectDefinition, groupId);

		JSONObject objectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"systemProperties", JSONUtil.put("comments", commentsJSONArray)
			).toString(),
			endpoint + "?nestedFields=systemProperties.comments",
			Http.Method.POST);

		long objectEntryId = objectEntryJSONObject.getLong("id");

		List<com.liferay.portal.kernel.comment.Comment> serviceBuilderComment =
			_commentManager.getComments(
				objectDefinition.getClassName(), objectEntryId,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertFalse(serviceBuilderComment.isEmpty());

		endpoint = _getDeletePatchPutEndpoint(
			groupId, objectDefinition, objectEntryJSONObject);

		HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.DELETE);

		serviceBuilderComment = _commentManager.getComments(
			objectDefinition.getClassName(), objectEntryId,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertTrue(serviceBuilderComment.isEmpty());
	}

	private void _testGetObjectEntriesPageWithComments(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		_enableComments(objectDefinition);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONArray commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			));

		_postObjectEntryWithComments(
			commentsJSONArray, groupId, false, objectDefinition);

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_getCommentsJSONArray(
				groupId, objectDefinition
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _testPatchPutObjectEntryWithComments(
			long groupId, Http.Method httpMethod,
			ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject objectEntryJSONObject = postObjectEntry(
			groupId, objectDefinition);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONArray commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			));

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_patchPutObjectEntryWithComments(
				commentsJSONArray, groupId, httpMethod, objectDefinition,
				objectEntryJSONObject
			).toString(),
			JSONCompareMode.LENIENT);

		commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			));

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_patchPutObjectEntryWithComments(
				commentsJSONArray, groupId, httpMethod, objectDefinition,
				objectEntryJSONObject
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _testPatchPutObjectEntryWithMissingParentCommentReference(
			Http.Method httpMethod)
		throws Exception {

		ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				getObjectDefinition1().getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		String parentExternalReferenceCode = RandomTestUtil.randomString();

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
					).build();
					systemProperties = new SystemProperties() {
						{
							comments = new Comment[] {
								new Comment() {
									{
										externalReferenceCode =
											RandomTestUtil.randomString();
										parentCommentExternalReferenceCode =
											parentExternalReferenceCode;
										text = RandomTestUtil.randomString();
									}
								}
							};
						}
					};
				}
			};

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Assert.assertNull(
			_commentManager.fetchComment(
				company.getGroupId(), parentExternalReferenceCode));

		ObjectEntryResource objectEntryResource = getObjectEntryResource(
			getObjectDefinition1(), TestPropsValues.getUser());

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			if (httpMethod == Http.Method.PATCH) {
				objectEntryResource.patchObjectEntry(
					serviceBuilderObjectEntry.getObjectEntryId(), objectEntry);
			}
			else if (httpMethod == Http.Method.PUT) {
				objectEntryResource.putObjectEntry(
					serviceBuilderObjectEntry.getObjectEntryId(), objectEntry);
			}
		}

		com.liferay.portal.kernel.comment.Comment serviceBuilderComment =
			_commentManager.getComment(
				company.getGroupId(), parentExternalReferenceCode);

		Assert.assertTrue(serviceBuilderComment instanceof WorkflowableComment);

		WorkflowableComment workflowableComment =
			(WorkflowableComment)serviceBuilderComment;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, workflowableComment.getStatus());

		_commentManager.deleteComment(serviceBuilderComment.getCommentId());
	}

	private void _testPostObjectEntriesWithComments(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		JSONArray commentsJSONArray = JSONUtil.put(
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			));

		Assert.assertEquals(
			0,
			_postObjectEntryWithComments(
				commentsJSONArray, groupId, true, objectDefinition
			).length());

		_enableComments(objectDefinition);

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_postObjectEntryWithComments(
				commentsJSONArray, groupId, true, objectDefinition
			).toString(),
			JSONCompareMode.LENIENT);

		String externalReferenceCode = RandomTestUtil.randomString();

		commentsJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			),
			JSONUtil.put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"parentCommentExternalReferenceCode", externalReferenceCode
			).put(
				"text", RandomTestUtil.randomString()
			));

		JSONAssert.assertEquals(
			_toExpectedCommentsJSONArray(
				commentsJSONArray
			).toString(),
			_postObjectEntryWithComments(
				commentsJSONArray, groupId, true, objectDefinition
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private JSONArray _toExpectedCommentsJSONArray(JSONArray commentsJSONArray)
		throws Exception {

		JSONArray expectedCommentsJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < commentsJSONArray.length(); i++) {
			JSONObject commentJSONObject = commentsJSONArray.getJSONObject(i);

			expectedCommentsJSONArray.put(
				JSONFactoryUtil.createJSONObject(
					commentJSONObject.toString()
				).put(
					"text",
					StringBundler.concat(
						"<p>", commentJSONObject.getString("text"), "</p>")
				));
		}

		return expectedCommentsJSONArray;
	}

	@Inject
	private CommentManager _commentManager;

}