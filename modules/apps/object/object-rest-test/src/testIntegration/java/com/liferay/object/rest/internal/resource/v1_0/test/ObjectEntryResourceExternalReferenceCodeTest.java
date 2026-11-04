/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceExternalReferenceCodeTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetCreatorExternalReferenceCode() throws Exception {
		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, true, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
						ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
						"Test Field", "testField", true)),
				Collections.emptyList(), new ServiceContext());

		objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		User user = UserTestUtil.addUser(group.getGroupId());

		ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.addObjectEntry(
				0, user.getUserId(), objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"testField", true
				).build(),
				ServiceContextTestUtil.getServiceContext());

		long objectEntryId = serviceBuilderObjectEntry.getObjectEntryId();

		ObjectEntryResource objectEntryResource = getObjectEntryResource(
			objectDefinition, TestPropsValues.getUser());

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			objectEntryResource.getObjectEntry(objectEntryId);

		Creator creator = objectEntry.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testGetObjectEntryByExternalReferenceCodeWithSlash()
		throws Exception {

		ObjectField textObjectField = objectFieldLocalService.getObjectField(
			getObjectDefinition1().getObjectDefinitionId(),
			OBJECT_FIELD_NAME_1);

		getObjectDefinition1().setTitleObjectFieldId(
			textObjectField.getObjectFieldId());

		objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a/",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252F",
				Http.Method.GET));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252F/",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "/a"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/%252Fa",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/b/c"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		try (LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					getObjectDefinition1().getRESTContextPath() +
						"/by-external-reference-code/a/b/c",
					Http.Method.GET));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252Fb%252Fc",
				Http.Method.GET));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252Fb%252Fc/",
				Http.Method.GET));
		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%2Fb%2Fc",
				Http.Method.GET));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a//b"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/a%252F%252Fb",
				Http.Method.GET));
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPatchByExternalReferenceCodeCustomObjectEntry()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		assertPatchCustomObjectEntry(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			objectDefinition1, getObjectDefinition2(), testGroupId);
	}

	@Test
	public void testPatchPutCustomObjectEntryExternalReferenceCode()
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", ERC_VALUE_1
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		_testPatchPutCustomObjectEntryExternalReferenceCode(
			Http.Method.PATCH, jsonObject.getLong("id"));
		_testPatchPutCustomObjectEntryExternalReferenceCode(
			Http.Method.PUT, jsonObject.getLong("id"));
	}

	@Test
	public void testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode()
		throws Exception {

		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			Http.Method.PATCH, getObjectDefinition1(),
			getSiteScopedObjectDefinition1());
		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			Http.Method.PUT, getObjectDefinition2(),
			getSiteScopedObjectDefinition2());
	}

	@Test
	public void testPostCustomObjectEntryWithDuplicateExternalReferenceCode()
		throws Exception {

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).toString(),
				getEndpoint(getSiteScopedObjectDefinition1(), testGroupId),
				Http.Method.POST));
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutByExternalReferenceCodeCustomObjectEntry()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		assertPutCustomObjectEntry(
			jsonObject ->
				objectDefinition1.getRESTContextPath() +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
			objectDefinition1, getObjectDefinition2(), testGroupId);
	}

	private void _testPatchPutCustomObjectEntryExternalReferenceCode(
			Http.Method httpMethod, long objectEntryId)
		throws Exception {

		_testPatchPutCustomObjectEntryExternalReferenceCode(
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				objectEntryId),
			ERC_VALUE_2, httpMethod);
		_testPatchPutCustomObjectEntryExternalReferenceCode(
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_2),
			ERC_VALUE_3, httpMethod);
	}

	private void _testPatchPutCustomObjectEntryExternalReferenceCode(
			String endpoint, String externalReferenceCode,
			Http.Method httpMethod)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).toString(),
			endpoint, httpMethod);

		Assert.assertEquals(
			externalReferenceCode,
			jsonObject.getString("externalReferenceCode"));
	}

	private void
			_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
				Http.Method httpMethod, ObjectDefinition objectDefinition,
				ObjectDefinition siteScopedObjectDefinition)
		throws Exception {

		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			objectDefinition.getRESTContextPath(),
			objectDefinition.getRESTContextPath() +
				"/by-external-reference-code/",
			httpMethod);

		String endpoint = getEndpoint(siteScopedObjectDefinition, testGroupId);

		_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
			endpoint, endpoint + "/by-external-reference-code/", httpMethod);
	}

	private void
			_testPatchPutCustomObjectEntryWithDuplicateExternalReferenceCode(
				String endpoint1, String endpoint2, Http.Method httpMethod)
		throws Exception {

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", externalReferenceCode1
				).toString(),
				endpoint1, Http.Method.POST));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", externalReferenceCode2
				).toString(),
				endpoint1, Http.Method.POST));
		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"externalReferenceCode", externalReferenceCode2
				).toString(),
				endpoint2 + externalReferenceCode1, httpMethod));
	}

}