/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.resource.test;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.headless.delivery.client.dto.v1_0.Document;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentResource;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.AggregationObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectFieldTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.File;
import java.io.Serializable;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlag("LPS-178642")
public class HeadlessBuilderResourceTest extends BaseTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'00:00:00.000'Z'");
		_dateTimeFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_documentResource = DocumentResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();

		_group = GroupTestUtil.addGroup();

		List<ListTypeEntry> listTypeEntries = TransformUtil.transformToList(
			ListTypeValue.values(),
			listTypeValue -> ListTypeEntryUtil.createListTypeEntry(
				listTypeValue.name(),
				Collections.singletonMap(LocaleUtil.US, listTypeValue.name())));

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, listTypeEntries);

		_objectDefinition1 = _addObjectDefinition(
			1, ObjectDefinitionConstants.SCOPE_COMPANY);
		_objectDefinition2 = _addObjectDefinition(
			2, ObjectDefinitionConstants.SCOPE_COMPANY);
		_objectDefinition3 = _addObjectDefinition(
			3, ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectRelationship1 = _addObjectRelationship(
			_objectDefinition1, _objectDefinition2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_objectRelationship2 = _addObjectRelationship(
			_objectDefinition2, _objectDefinition3,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_addAggregationObjectField(
			_objectDefinition1, _objectRelationship1.getName());
		_addAggregationObjectField(
			_objectDefinition2, _objectRelationship2.getName());

		_siteScopedObjectDefinition1 = _addObjectDefinition(
			1, ObjectDefinitionConstants.SCOPE_SITE);
		_siteScopedObjectDefinition2 = _addObjectDefinition(
			2, ObjectDefinitionConstants.SCOPE_SITE);
		_siteScopedObjectDefinition3 = _addObjectDefinition(
			3, ObjectDefinitionConstants.SCOPE_SITE);

		_siteScopedObjectRelationship1 = _addObjectRelationship(
			_siteScopedObjectDefinition1, _siteScopedObjectDefinition2,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_siteScopedObjectRelationship2 = _addObjectRelationship(
			_siteScopedObjectDefinition2, _siteScopedObjectDefinition3,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_addAggregationObjectField(
			_siteScopedObjectDefinition1,
			_siteScopedObjectRelationship1.getName());
		_addAggregationObjectField(
			_siteScopedObjectDefinition2,
			_siteScopedObjectRelationship2.getName());
	}

	@Test
	public void testGetIndividualObjectEntryByUniqueField() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, "textUniqueField",
			APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		_addCustomObjectEntry(
			1, null, _objectDefinition1, "value1", "valueUnique");

		JSONAssert.assertEquals(
			JSONUtil.put(
				"textUniqueProperty", "valueUnique"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1, "/valueUnique"),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetOpenAPIInDifferentCompany() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);
		_publishAPIApplication(_API_APPLICATION_ERC_1);

		assertSuccessfulJSONObject(
			null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
			Http.Method.GET);

		Assert.assertTrue(
			HTTPTestUtil.invokeToJSONObject(
				null, "openapi", Http.Method.GET
			).has(
				"/c/" + _BASE_URL_1
			));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"domain", "able.com"
			).put(
				"portalInstanceId", "able.com"
			).put(
				"virtualHost", "www.able.com"
			).toString(),
			"headless-portal-instances/v1.0/portal-instances",
			Http.Method.POST);

		HTTPTestUtil.customize(
		).withBaseURL(
			"http://www.able.com:8080"
		).withCredentials(
			"test@able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).apply(
			() -> {
				try (LogCapture logCapture =
						LoggerTestUtil.configureLog4JLogger(
							"portal_web.docroot.errors.code_jsp",
							LoggerTestUtil.WARN)) {

					Assert.assertEquals(
						404,
						HTTPTestUtil.invokeToHttpCode(
							null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
							Http.Method.GET));
				}

				Assert.assertFalse(
					HTTPTestUtil.invokeToJSONObject(
						null, "openapi", Http.Method.GET
					).has(
						"/c/" + _BASE_URL_1
					));

				assertSuccessfulJSONObject(
					JSONUtil.put(
						"applicationStatus", "published"
					).put(
						"baseURL", _BASE_URL_1
					).put(
						"externalReferenceCode", _API_APPLICATION_ERC_1
					).put(
						"title", "test-app"
					).toString(),
					"headless-builder/applications", Http.Method.POST);

				Assert.assertTrue(
					HTTPTestUtil.invokeToJSONObject(
						null, "openapi", Http.Method.GET
					).has(
						"/c/" + _BASE_URL_1
					));

				JSONAssert.assertEquals(
					JSONUtil.put(
						"totalCount", 1
					).toString(),
					HTTPTestUtil.invokeToJSONObject(
						null, "headless-builder/applications", Http.Method.GET
					).toString(),
					JSONCompareMode.LENIENT);

				assertSuccessfulJSONObject(
					null,
					"headless-builder/applications/by-external-reference-code" +
						"/" + _API_APPLICATION_ERC_1,
					Http.Method.DELETE);

				Assert.assertFalse(
					HTTPTestUtil.invokeToJSONObject(
						null, "openapi", Http.Method.GET
					).has(
						"/c/" + _BASE_URL_1
					));
			}
		);

		assertSuccessfulJSONObject(
			null,
			"headless-builder/applications/by-external-reference-code/" +
				_API_APPLICATION_ERC_1,
			Http.Method.GET);
	}

	@Test
	public void testGetSingleElementByExternalReferenceCode() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1,
			HeadlessBuilderConstants.PATH_PARAMETER_ERC,
			APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry = _addCustomObjectEntry(
			1, null, _objectDefinition1, "value1",
			RandomTestUtil.randomString());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"textProperty", "value1"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1,
					StringPool.FORWARD_SLASH,
					objectEntry.getExternalReferenceCode()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetSingleElementByObjectEntryId() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, HeadlessBuilderConstants.PATH_PARAMETER_ID,
			APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry = _addCustomObjectEntry(
			1, null, _objectDefinition1, "value1",
			RandomTestUtil.randomString());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"textProperty", "value1"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1,
					StringPool.FORWARD_SLASH, objectEntry.getObjectEntryId()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetWithAPIFilter() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"oDataFilter", "textField eq 'value5' or textField eq 'value7'"
			).put(
				"r_apiEndpointToAPIFilters_l_apiEndpointERC",
				_API_ENDPOINT_ERC_1
			).toString(),
			"headless-builder/filters", Http.Method.POST);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		for (int i = 0; i <= 25; i++) {
			_addCustomObjectEntry(
				i, null, _objectDefinition1, "value" + i,
				RandomTestUtil.randomString());
		}

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put("textProperty", "value5"),
					JSONUtil.put("textProperty", "value7"))
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
				null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items", JSONUtil.putAll(JSONUtil.put("textProperty", "value5"))
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
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1, "?filter=",
					URLCodec.encodeURL(
						"textProperty eq 'value5' or textProperty eq " +
							"'value8'")),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetWithAPISortAsc() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_addAPISort("textField:asc");

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		_addCustomObjectEntry(
			1, null, _objectDefinition1, "value1", "uniqueValue1");
		_addCustomObjectEntry(
			2, null, _objectDefinition1, "value2", "uniqueValue2");
		_addCustomObjectEntry(
			3, null, _objectDefinition1, "value3", "uniqueValue3");

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"integerProperty", 1
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value1"
				).put(
					"textUniqueProperty", "uniqueValue1"
				),
				JSONUtil.put(
					"integerProperty", 2
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value2"
				).put(
					"textUniqueProperty", "uniqueValue2"
				),
				JSONUtil.put(
					"integerProperty", 3
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value3"
				).put(
					"textUniqueProperty", "uniqueValue3"
				)
			).toString(),
			itemsJSONArray.toString(), JSONCompareMode.STRICT);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"c/", _BASE_URL_1, _API_APPLICATION_PATH_1, "?sort=",
				URLCodec.encodeURL("textProperty:desc")),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"integerProperty", 3
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value3"
				).put(
					"textUniqueProperty", "uniqueValue3"
				),
				JSONUtil.put(
					"integerProperty", 2
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value2"
				).put(
					"textUniqueProperty", "uniqueValue2"
				),
				JSONUtil.put(
					"integerProperty", 1
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value1"
				).put(
					"textUniqueProperty", "uniqueValue1"
				)
			).toString(),
			itemsJSONArray.toString(), JSONCompareMode.STRICT);
	}

	@Test
	public void testGetWithAPISortDesc() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_addAPISort("textField:desc");

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		_addCustomObjectEntry(
			1, null, _objectDefinition1, "value1", "uniqueValue1");
		_addCustomObjectEntry(
			2, null, _objectDefinition1, "value2", "uniqueValue2");
		_addCustomObjectEntry(
			3, null, _objectDefinition1, "value3", "uniqueValue3");

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"integerProperty", 3
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value3"
				).put(
					"textUniqueProperty", "uniqueValue3"
				),
				JSONUtil.put(
					"integerProperty", 2
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value2"
				).put(
					"textUniqueProperty", "uniqueValue2"
				),
				JSONUtil.put(
					"integerProperty", 1
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value1"
				).put(
					"textUniqueProperty", "uniqueValue1"
				)
			).toString(),
			itemsJSONArray.toString(), JSONCompareMode.STRICT);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"c/", _BASE_URL_1, _API_APPLICATION_PATH_1, "?sort=",
				URLCodec.encodeURL("textProperty:asc")),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"integerProperty", 1
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value1"
				).put(
					"textUniqueProperty", "uniqueValue1"
				),
				JSONUtil.put(
					"integerProperty", 2
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value2"
				).put(
					"textUniqueProperty", "uniqueValue2"
				),
				JSONUtil.put(
					"integerProperty", 3
				).put(
					"multiselectPicklistProperty", Collections.emptyList()
				).put(
					"relatedIntegerProperty1", Collections.emptyList()
				).put(
					"relatedIntegerProperty2", Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty1",
					Collections.emptyList()
				).put(
					"relatedMultiselectPicklistProperty2",
					Collections.emptyList()
				).put(
					"relatedTextProperty1", Collections.emptyList()
				).put(
					"relatedTextProperty2", Collections.emptyList()
				).put(
					"textProperty", "value3"
				).put(
					"textUniqueProperty", "uniqueValue3"
				)
			).toString(),
			itemsJSONArray.toString(), JSONCompareMode.STRICT);
	}

	@Test
	public void testGetWithCompanyScopedEndpoint() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);
		_addAPIApplication(
			_API_APPLICATION_ERC_2, _API_ENDPOINT_ERC_2, _BASE_URL_2,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_2, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		String endpoint1 = "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, endpoint1, Http.Method.GET));
		}

		String endpoint2 = "c/" + _BASE_URL_2 + _API_APPLICATION_PATH_2;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, endpoint2, Http.Method.GET));
		}

		_publishAPIApplication(_API_APPLICATION_ERC_1);
		_publishAPIApplication(_API_APPLICATION_ERC_2);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(null, endpoint1, Http.Method.GET));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(null, endpoint2, Http.Method.GET));
		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, "/scopes/", TestPropsValues.getGroupId(),
					_API_APPLICATION_PATH_1),
				Http.Method.GET));
		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_2, "/scopes/", TestPropsValues.getGroupId(),
					_API_APPLICATION_PATH_2),
				Http.Method.GET));

		ObjectEntry objectEntry1 = _addCustomObjectEntry(
			1, null, _objectDefinition1, "value1",
			RandomTestUtil.randomString());
		ObjectEntry objectEntry2 = _addCustomObjectEntry(
			2, null, _objectDefinition2, "value2",
			RandomTestUtil.randomString());

		_relateObjectEntries(objectEntry1, objectEntry2, _objectRelationship1);

		ObjectEntry objectEntry3 = _addCustomObjectEntry(
			3, null, _objectDefinition3, "value3",
			RandomTestUtil.randomString());

		_relateObjectEntries(objectEntry2, objectEntry3, _objectRelationship2);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.put(
					JSONUtil.put(
						"relatedTextProperty1", JSONUtil.put("value2")
					).put(
						"relatedTextProperty2", JSONUtil.put("value3")
					).put(
						"textProperty", "value1"
					))
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, endpoint1, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.put(
					JSONUtil.put(
						"relatedTextProperty1", JSONUtil.put("value2")
					).put(
						"relatedTextProperty2", JSONUtil.put("value3")
					).put(
						"textProperty", "value1"
					))
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, endpoint2, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						"c/", _BASE_URL_1, StringPool.SLASH,
						RandomTestUtil.randomString()),
					Http.Method.GET));
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						"c/", _BASE_URL_2, StringPool.SLASH,
						RandomTestUtil.randomString()),
					Http.Method.GET));
		}

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "unpublished"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				_API_APPLICATION_ERC_1,
			Http.Method.PATCH);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, endpoint1, Http.Method.GET));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(null, endpoint2, Http.Method.GET));
	}

	@Test
	public void testGetWithIndirectlyRelatedProperty() throws Exception {
		JSONObject apiApplicationJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		JSONObject apiEndpointJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"description", RandomTestUtil.randomString()
			).put(
				"httpMethod", "get"
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"path",
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).put(
				"responseAPISchemaToAPIEndpoints",
				JSONUtil.put(
					"apiSchemaToAPIProperties",
					JSONUtil.putAll(
						JSONUtil.put(
							"description", RandomTestUtil.randomString()
						).put(
							"name", RandomTestUtil.randomString()
						).put(
							"objectFieldERC", "NAME"
						),
						JSONUtil.put(
							"description", RandomTestUtil.randomString()
						).put(
							"name", RandomTestUtil.randomString()
						).put(
							"objectFieldERC", "NAME"
						).put(
							"objectRelationshipNames",
							"apiApplicationToAPIEndpoints,apiApplicationToAPI" +
								"Schemas"
						))
				).put(
					"description", RandomTestUtil.randomString()
				).put(
					"mainObjectDefinitionERC", "L_API_ENDPOINT"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"r_apiApplicationToAPISchemas_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				)
			).put(
				"retrieveType", "collection"
			).put(
				"scope", "company"
			).toString(),
			"headless-builder/endpoints", Http.Method.POST);

		assertSuccessfulJSONObject(
			null,
			"c/" + apiApplicationJSONObject.getString("baseURL") +
				apiEndpointJSONObject.getString("path"),
			Http.Method.GET);
	}

	@Test
	public void testGetWithIndirectlyUnrelatedObjectEntries() throws Exception {
		ObjectDefinition objectDefinition1 = _addObjectDefinition(
			4, ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition objectDefinition2 = _addObjectDefinition(
			5, ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition objectDefinition3 = _addObjectDefinition(
			6, ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			objectDefinition2, objectDefinition1,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			objectDefinition2, objectDefinition3,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.putAll(
					_createAPIEndpoint(
						_API_ENDPOINT_ERC_1, Http.Method.GET,
						_API_APPLICATION_PATH_1, null,
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue(),
						APIApplication.Endpoint.Scope.COMPANY))
			).put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "integerProperty4"
							).put(
								"objectFieldERC",
								_API_SCHEMA_INTEGER_FIELD_ERC + 4
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedTextProperty6"
							).put(
								"objectFieldERC", _API_SCHEMA_TEXT_FIELD_ERC + 6
							).put(
								"objectRelationshipNames",
								objectRelationship1.getName() + "," +
									objectRelationship2.getName()
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC",
						objectDefinition1.getExternalReferenceCode()
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC_1
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		_relateAPIEndpointWithAPISchemas(
			_API_ENDPOINT_ERC_1, apiSchemaExternalReferenceCode);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry1 = _addCustomObjectEntry(
			1, null, objectDefinition1, "value1",
			RandomTestUtil.randomString());
		ObjectEntry objectEntry2 = _addCustomObjectEntry(
			1, null, objectDefinition2, "value1",
			RandomTestUtil.randomString());
		ObjectEntry objectEntry3 = _addCustomObjectEntry(
			1, null, objectDefinition3, "value1",
			RandomTestUtil.randomString());

		_relateObjectEntries(objectEntry1, objectEntry2, objectRelationship1);
		_relateObjectEntries(objectEntry1, objectEntry3, objectRelationship2);

		assertSuccessfulJSONObject(
			null,
			StringBundler.concat("c/", _BASE_URL_1, _API_APPLICATION_PATH_1),
			Http.Method.GET);
	}

	@Test
	public void testGetWithPagination() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		for (int i = 0; i <= 25; i++) {
			_addCustomObjectEntry(
				i, null, _objectDefinition1, "value" + i,
				RandomTestUtil.randomString());
		}

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put("textProperty", "value5"),
					JSONUtil.put("textProperty", "value6"),
					JSONUtil.put("textProperty", "value7"),
					JSONUtil.put("textProperty", "value8"),
					JSONUtil.put("textProperty", "value9"))
			).put(
				"lastPage", 6
			).put(
				"page", 2
			).put(
				"pageSize", 5
			).put(
				"totalCount", 26
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1,
					"?page=2&pageSize=5"),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetWithPostEndpoint() throws Exception {
		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.putAll(

					// Order is relevant to reproduce the issue. See LPS-202115.

					_createAPIEndpoint(
						_API_ENDPOINT_ERC_2, Http.Method.POST,
						"/testpost", null,
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue(),
						APIApplication.Endpoint.Scope.COMPANY),
					_createAPIEndpoint(
						_API_ENDPOINT_ERC_1, Http.Method.GET,
						"/testget", null,
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue(),
						APIApplication.Endpoint.Scope.COMPANY))
			).put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "attachmentProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_ATTACHMENT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "booleanProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_BOOLEAN_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "dateProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_DATE_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "dateTimeProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_DATE_TIME_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "decimalProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_DECIMAL_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "integerProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_INTEGER_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "longIntegerProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_LONG_INTEGER_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "longTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_LONG_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "multiselectPicklistProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "picklistProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_PICKLIST_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "precisionDecimalProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "richTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_RICH_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "textProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "textUniqueProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_UNIQUE_TEXT_FIELD_ERC + 1
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC",
						_objectDefinition1.getExternalReferenceCode()
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode",
				_API_APPLICATION_ERC_1
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);
		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/responseAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_1),
			Http.Method.PUT);
		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/requestAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_2),
			Http.Method.PUT);
		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/responseAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_2),
			Http.Method.PUT);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		Document document = _addRandomDocument();

		long attachmentPropertyIdValue = document.getId();

		boolean booleanPropertyValue = RandomTestUtil.randomBoolean();
		String datePropertyValue = _dateFormat.format(
			RandomTestUtil.nextDate());
		String dateTimePropertyValue = _dateTimeFormat.format(
			RandomTestUtil.nextDate());
		double decimalPropertyValue = RandomTestUtil.randomDouble();
		int integerPropertyValue = RandomTestUtil.randomInt();
		long longIntegerPropertyValue = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String longTextPropertyValue = RandomTestUtil.randomString();
		Serializable multiselectPicklistPropertyValue =
			(Serializable)TransformUtil.transform(
				Arrays.asList(ListTypeValue.VALUE1, ListTypeValue.VALUE3),
				ListTypeValue::name);
		String picklistPropertyValue = ListTypeValue.VALUE1.name();
		double precisionDecimalPropertyValue = 1.1;
		String richTextPropertyValue = RandomTestUtil.randomString();
		String textPropertyValue = RandomTestUtil.randomString();
		String textUniquePropertyValue = "Unique field value";

		JSONObject expectedJSONObject = JSONUtil.put(
			"attachmentProperty", JSONUtil.put("id", attachmentPropertyIdValue)
		).put(
			"booleanProperty", booleanPropertyValue
		).put(
			"dateProperty", datePropertyValue
		).put(
			"dateTimeProperty", dateTimePropertyValue
		).put(
			"decimalProperty", decimalPropertyValue
		).put(
			"integerProperty", integerPropertyValue
		).put(
			"longIntegerProperty", (Long)longIntegerPropertyValue
		).put(
			"longTextProperty", longTextPropertyValue
		).put(
			"multiselectPicklistProperty",
			JSONUtil.putAll(
				JSONUtil.put(
					"key", ListTypeValue.VALUE1
				).put(
					"name", ListTypeValue.VALUE1
				),
				JSONUtil.put(
					"key", ListTypeValue.VALUE3
				).put(
					"name", ListTypeValue.VALUE3
				))
		).put(
			"picklistProperty",
			JSONUtil.put(
				"key", ListTypeValue.VALUE1
			).put(
				"name", ListTypeValue.VALUE1
			)
		).put(
			"precisionDecimalProperty", precisionDecimalPropertyValue
		).put(
			"richTextProperty", richTextPropertyValue
		).put(
			"textProperty", textPropertyValue
		).put(
			"textUniqueProperty", textUniquePropertyValue
		);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"attachmentProperty", attachmentPropertyIdValue
				).put(
					"booleanProperty", booleanPropertyValue
				).put(
					"dateProperty", datePropertyValue
				).put(
					"dateTimeProperty", dateTimePropertyValue
				).put(
					"decimalProperty", decimalPropertyValue
				).put(
					"integerProperty", integerPropertyValue
				).put(
					"longIntegerProperty", longIntegerPropertyValue
				).put(
					"longTextProperty", longTextPropertyValue
				).put(
					"multiselectPicklistProperty",
					multiselectPicklistPropertyValue
				).put(
					"picklistProperty", picklistPropertyValue
				).put(
					"precisionDecimalProperty", precisionDecimalPropertyValue
				).put(
					"richTextProperty", richTextPropertyValue
				).put(
					"textProperty", textPropertyValue
				).put(
					"textUniqueProperty", textUniquePropertyValue
				).toString(),
				StringBundler.concat("c/", _BASE_URL_1, "/testpost"),
				Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"items", JSONUtil.putAll(expectedJSONObject)
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, StringBundler.concat("c/", _BASE_URL_1, "/testget"),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@FeatureFlag("LPD-10964")
	@Test
	public void testGetWithRecordProperty() throws Exception {
		_addAPIApplicationWithRecordProperty(
			Http.Method.GET, _objectDefinition1.getExternalReferenceCode());

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry1 = _addCustomObjectEntry(
			1, null, _objectDefinition1, "value1",
			RandomTestUtil.randomString());
		ObjectEntry objectEntry2 = _addCustomObjectEntry(
			2, null, _objectDefinition2, "value2",
			RandomTestUtil.randomString());

		_relateObjectEntries(objectEntry1, objectEntry2, _objectRelationship1);

		_testGetWithRecordProperty(
			JSONUtil.putAll(
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
				).put(
					"name", "record1"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
				).put(
					"name", "integerProperty"
				).put(
					"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
				)),
			JSONUtil.put("integerProperty", 1), objectEntry1);
		_testGetWithRecordProperty(
			JSONUtil.putAll(
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
				).put(
					"name", "record1"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_2
				).put(
					"name", "record2"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_3
				).put(
					"name", "record3"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_4
				).put(
					"name", "record4"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_5
				).put(
					"name", "record5"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_4
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
				).put(
					"name", "integerProperty"
				).put(
					"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_2
				).put(
					"name", "relatedMultiselectPicklistProperty"
				).put(
					"objectFieldERC",
					_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 2
				).put(
					"objectRelationshipNames", _objectRelationship1.getName()
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_5
				)),
			JSONUtil.put(
				"record2", JSONUtil.put("integerProperty", 1)
			).put(
				"record4",
				JSONUtil.put(
					"record5",
					JSONUtil.put(
						"relatedMultiselectPicklistProperty",
						Collections.emptyList()))
			),
			objectEntry1);

		_disassociateAPIProperties();

		_testGetWithRecordProperty(
			JSONUtil.putAll(
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
				).put(
					"name", "record1"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_2
				).put(
					"name", "record2"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_1
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_3
				).put(
					"name", "record3"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
				).put(
					"name", "integerProperty"
				).put(
					"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_2
				).put(
					"name", "relatedMultiselectPicklistProperty"
				).put(
					"objectFieldERC",
					_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 2
				).put(
					"objectRelationshipNames", _objectRelationship1.getName()
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_3
				)),
			JSONUtil.put(
				"record1",
				JSONUtil.put(
					"record2",
					JSONUtil.put(
						"integerProperty", 1
					).put(
						"record3",
						JSONUtil.put(
							"relatedMultiselectPicklistProperty",
							Collections.emptyList())
					))),
			objectEntry1);
	}

	@Test
	public void testGetWithRelatedModel() throws Exception {
		JSONObject apiApplicationJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_API_ENDPOINT", TestPropsValues.getCompanyId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "externalReferenceCode");

		JSONObject apiEndpointJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"description", RandomTestUtil.randomString()
			).put(
				"httpMethod", "get"
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"path",
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationId",
				apiApplicationJSONObject.getLong("id")
			).put(
				"responseAPISchemaToAPIEndpoints",
				JSONUtil.put(
					"apiSchemaToAPIProperties",
					JSONUtil.putAll(
						JSONUtil.put(
							"description", RandomTestUtil.randomString()
						).put(
							"name", "APIEndpointsERC"
						).put(
							"objectFieldERC",
							objectField.getExternalReferenceCode()
						).put(
							"objectRelationshipNames",
							"apiApplicationToAPIEndpoints"
						))
				).put(
					"description", RandomTestUtil.randomString()
				).put(
					"mainObjectDefinitionERC", "L_API_APPLICATION"
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"r_apiApplicationToAPISchemas_l_apiApplicationId",
					apiApplicationJSONObject.getLong("id")
				)
			).put(
				"retrieveType", "collection"
			).put(
				"scope", "company"
			).toString(),
			"headless-builder/endpoints", Http.Method.POST);

		JSONObject responseJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			"c/" + apiApplicationJSONObject.getString("baseURL") +
				apiEndpointJSONObject.getString("path"),
			Http.Method.GET);

		JSONArray itemsJSONArray = responseJSONObject.getJSONArray("items");

		JSONObject jsonObject = itemsJSONArray.getJSONObject(0);

		JSONArray apiEndpointsERCJSONArray = jsonObject.getJSONArray(
			"APIEndpointsERC");

		Assert.assertNotEquals("", apiEndpointsERCJSONArray.getString(0));
	}

	@Test
	public void testGetWithRequestFilter() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry1 = _addCustomObjectEntry(
			1, Arrays.asList(ListTypeValue.VALUE1), _objectDefinition1,
			"1value1", RandomTestUtil.randomString());
		ObjectEntry objectEntry2 = _addCustomObjectEntry(
			2, Arrays.asList(ListTypeValue.VALUE2, ListTypeValue.VALUE3),
			_objectDefinition1, "2value2", RandomTestUtil.randomString());

		// Comparison operators

		_assertFilterString("integerProperty", 1, "integerProperty eq 1");
		_assertFilterString("integerProperty", 1, "integerProperty ne 2");
		_assertFilterString("integerProperty", 2, "integerProperty gt 1");
		_assertFilterString("integerProperty", 2, "integerProperty ge 2");
		_assertFilterString("integerProperty", 1, "integerProperty lt 2");
		_assertFilterString("integerProperty", 1, "integerProperty le 1");
		_assertFilterString(
			"integerProperty", 1, "startswith(textProperty,'1value')");
		_assertFilterString(
			"integerProperty", 1, "textProperty in ('1value1','3value3')");

		// Grouping operators

		_assertFilterString(
			"integerProperty", 2,
			"((integerProperty gt 1 or integerProperty lt 1) and " +
				"(textProperty eq '2value2'))");

		// Lambda operators

		_assertFilterString(
			"integerProperty", 1,
			"multiselectPicklistProperty/any(k:contains(k,'LUE1'))");

		// Logical operators

		_assertFilterString(
			"integerProperty", 1,
			"integerProperty ge 1 and integerProperty lt 2");
		_assertFilterString(
			"integerProperty", 2,
			"integerProperty gt 1 or integerProperty lt 1");
		_assertFilterString("integerProperty", 1, "not (integerProperty ge 2)");

		// String functions

		_assertFilterString(
			"integerProperty", 1, "contains(textProperty, 'value1')");

		// Filter using related object entry fields

		ObjectEntry level1RelatedObjectEntry1 = _addCustomObjectEntry(
			3, Arrays.asList(ListTypeValue.VALUE2), _objectDefinition2,
			"3value3", RandomTestUtil.randomString());

		_relateObjectEntries(
			objectEntry1, level1RelatedObjectEntry1, _objectRelationship1);

		ObjectEntry level1RelatedObjectEntry2 = _addCustomObjectEntry(
			4, Arrays.asList(ListTypeValue.VALUE1, ListTypeValue.VALUE3),
			_objectDefinition2, "4value4", RandomTestUtil.randomString());

		_relateObjectEntries(
			objectEntry2, level1RelatedObjectEntry2, _objectRelationship1);

		ObjectEntry level2RelatedObjectEntry1 = _addCustomObjectEntry(
			5, Arrays.asList(ListTypeValue.VALUE3), _objectDefinition3,
			"5value5", RandomTestUtil.randomString());

		_relateObjectEntries(
			level1RelatedObjectEntry1, level2RelatedObjectEntry1,
			_objectRelationship2);

		ObjectEntry level2RelatedObjectEntry2 = _addCustomObjectEntry(
			6, Arrays.asList(ListTypeValue.VALUE1, ListTypeValue.VALUE2),
			_objectDefinition3, "6value6", RandomTestUtil.randomString());

		_relateObjectEntries(
			level1RelatedObjectEntry2, level2RelatedObjectEntry2,
			_objectRelationship2);

		// Comparison operators (using related object entries fields)

		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty1 eq 3");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty2 eq 5");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty1 ne 4");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty2 ne 6");
		_assertFilterString(
			"integerProperty", 2, "relatedIntegerProperty1 gt 3");
		_assertFilterString(
			"integerProperty", 2, "relatedIntegerProperty2 gt 5");
		_assertFilterString(
			"integerProperty", 2, "relatedIntegerProperty1 ge 4");
		_assertFilterString(
			"integerProperty", 2, "relatedIntegerProperty2 ge 6");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty1 lt 4");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty2 lt 6");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty1 le 3");
		_assertFilterString(
			"integerProperty", 1, "relatedIntegerProperty2 le 5");
		_assertFilterString(
			"integerProperty", 1, "startswith(relatedTextProperty1,'3value')");
		_assertFilterString(
			"integerProperty", 1, "startswith(relatedTextProperty2,'5value')");
		_assertFilterString(
			"integerProperty", 1,
			"relatedTextProperty1 in ('1value1','3value3')");
		_assertFilterString(
			"integerProperty", 1,
			"relatedTextProperty2 in ('1value1','5value5')");

		// Grouping operators (using related object entries fields)

		_assertFilterString(
			"integerProperty", 2,
			"((relatedIntegerProperty1 gt 3 or relatedIntegerProperty1 lt 3) " +
				"and (relatedTextProperty1 eq '4value4'))");

		_assertFilterString(
			"integerProperty", 2,
			"((relatedIntegerProperty2 gt 5 or relatedIntegerProperty2 lt 5) " +
				"and (relatedTextProperty2 eq '6value6'))");

		// Lambda operators (using related object entries fields)

		_assertFilterString(
			"integerProperty", 1,
			"relatedMultiselectPicklistProperty1/any(k:contains(k,'LUE2'))");
		_assertFilterString(
			"integerProperty", 1,
			"relatedMultiselectPicklistProperty2/any(k:contains(k,'LUE3'))");

		// Logical operators (using related object entries fields)

		_assertFilterString(
			"integerProperty", 1,
			"relatedIntegerProperty1 ge 3 and relatedIntegerProperty1 lt 4");
		_assertFilterString(
			"integerProperty", 1,
			"relatedIntegerProperty2 ge 5 and relatedIntegerProperty2 lt 6");
		_assertFilterString(
			"integerProperty", 2,
			"relatedIntegerProperty1 gt 3 or relatedIntegerProperty1 lt 3");
		_assertFilterString(
			"integerProperty", 2,
			"relatedIntegerProperty2 gt 5 or relatedIntegerProperty2 lt 5");

		_assertFilterString(
			"integerProperty", 1, "not (relatedIntegerProperty1 ge 4)");
		_assertFilterString(
			"integerProperty", 1, "not (relatedIntegerProperty2 ge 6)");

		// String functions (using related object entries fields)

		_assertFilterString(
			"integerProperty", 1, "contains(relatedTextProperty1, 'value3')");

		_assertFilterString(
			"integerProperty", 1, "contains(relatedTextProperty2, 'value5')");
	}

	@Test
	public void testGetWithServiceAccessPolicy() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_objectDefinition1.getExternalReferenceCode(),
			_objectRelationship1.getName(), _objectRelationship2.getName(),
			_API_APPLICATION_PATH_1, null,
			APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry = _addCustomObjectEntry(
			0, null, _objectDefinition1, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()),
			guestRole.getRoleId(), new String[] {ActionKeys.VIEW});

		_addCustomObjectEntry(
			0, null, _objectDefinition1, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_sapEntry = _sapEntryLocalService.addSAPEntry(
			TestPropsValues.getUserId(),
			"com.liferay.headless.builder.internal.resource." +
				"HeadlessBuilderResourceImpl#get",
			true, true, RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<String, Serializable> values = objectEntry.getValues();

		HTTPTestUtil.customize(
		).withGuest(
		).apply(
			() -> JSONAssert.assertEquals(
				JSONUtil.put(
					"items",
					JSONUtil.putAll(
						JSONUtil.put(
							"textProperty", values.get("textProperty")))
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
					null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
					Http.Method.GET
				).toString(),
				JSONCompareMode.LENIENT)
		);
	}

	@Test
	public void testGetWithSiteScopedEndpoint() throws Exception {
		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_siteScopedObjectDefinition1.getExternalReferenceCode(),
			_siteScopedObjectRelationship1.getName(),
			_siteScopedObjectRelationship2.getName(), _API_APPLICATION_PATH_1,
			null, APIApplication.Endpoint.RetrieveType.COLLECTION.getValue(),
			APIApplication.Endpoint.Scope.SITE);

		String endpointPath = "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, endpointPath, Http.Method.GET));
		}

		String scopedEndpointPath = StringBundler.concat(
			"c/", _BASE_URL_1, "/scopes/", TestPropsValues.getGroupId(),
			_API_APPLICATION_PATH_1);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, scopedEndpointPath, Http.Method.GET));
		}

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, endpointPath, Http.Method.GET));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null, scopedEndpointPath, Http.Method.GET));

		ObjectEntry objectEntry1 = _addCustomObjectEntry(
			_group.getGroupId(), 1, null, _siteScopedObjectDefinition1,
			"value1", RandomTestUtil.randomString());
		ObjectEntry objectEntry2 = _addCustomObjectEntry(
			_group.getGroupId(), 2, null, _siteScopedObjectDefinition2,
			"value2", RandomTestUtil.randomString());

		_relateObjectEntries(
			objectEntry1, objectEntry2, _siteScopedObjectRelationship1);

		ObjectEntry objectEntry3 = _addCustomObjectEntry(
			_group.getGroupId(), 3, null, _siteScopedObjectDefinition3,
			"value3", RandomTestUtil.randomString());

		_relateObjectEntries(
			objectEntry2, objectEntry3, _siteScopedObjectRelationship2);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items", JSONFactoryUtil.createJSONArray()
			).put(
				"totalCount", 0
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, scopedEndpointPath, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		scopedEndpointPath = StringBundler.concat(
			"c/", _BASE_URL_1, "/scopes/", _group.getGroupId(),
			_API_APPLICATION_PATH_1);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.put(
					JSONUtil.put(
						"relatedTextProperty1", JSONUtil.put("value2")
					).put(
						"relatedTextProperty2", JSONUtil.put("value3")
					).put(
						"textProperty", "value1"
					))
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, scopedEndpointPath, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "unpublished"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				_API_APPLICATION_ERC_1,
			Http.Method.PATCH);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, endpointPath, Http.Method.GET));
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, scopedEndpointPath, Http.Method.GET));
		}
	}

	@Test
	public void testGetWithSiteScopedEndpointIndividualObjectEntryByExternalReferenceCode()
		throws Exception {

		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_siteScopedObjectDefinition1.getExternalReferenceCode(),
			_siteScopedObjectRelationship1.getName(),
			_siteScopedObjectRelationship2.getName(), _API_APPLICATION_PATH_1,
			HeadlessBuilderConstants.PATH_PARAMETER_ERC,
			APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.getValue(),
			APIApplication.Endpoint.Scope.SITE);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry = _addCustomObjectEntry(
			_group.getGroupId(), 1, null, _siteScopedObjectDefinition1,
			"value1", RandomTestUtil.randomString());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"textProperty", "value1"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, "/scopes/", _group.getGroupId(),
					_API_APPLICATION_PATH_1, StringPool.FORWARD_SLASH,
					objectEntry.getExternalReferenceCode()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetWithSiteScopedEndpointIndividualObjectEntryByUniqueField()
		throws Exception {

		_addAPIApplication(
			_API_APPLICATION_ERC_1, _API_ENDPOINT_ERC_1, _BASE_URL_1,
			_siteScopedObjectDefinition1.getExternalReferenceCode(),
			_siteScopedObjectRelationship1.getName(),
			_siteScopedObjectRelationship2.getName(), _API_APPLICATION_PATH_1,
			"textUniqueField",
			APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.getValue(),
			APIApplication.Endpoint.Scope.SITE);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		_addCustomObjectEntry(
			_group.getGroupId(), 1, null, _siteScopedObjectDefinition1,
			"value1", "uniqueValue");

		JSONAssert.assertEquals(
			JSONUtil.put(
				"textUniqueProperty", "uniqueValue"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, "/scopes/", _group.getGroupId(),
					_API_APPLICATION_PATH_1, "/uniqueValue"),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetWithSystemObjectFields() throws Exception {
		ObjectField systemObjectFieldCreator =
			_objectFieldLocalService.getObjectField(
				_objectDefinition1.getObjectDefinitionId(), "creator");

		ObjectField systemObjectFieldId =
			_objectFieldLocalService.getObjectField(
				_objectDefinition1.getObjectDefinitionId(), "id");

		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.putAll(
					_createAPIEndpoint(
						_API_ENDPOINT_ERC_1, Http.Method.GET,
						_API_APPLICATION_PATH_1, null,
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue(),
						APIApplication.Endpoint.Scope.COMPANY))
			).put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "id"
							).put(
								"objectFieldERC",
								systemObjectFieldId.getExternalReferenceCode()
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "creator"
							).put(
								"objectFieldERC",
								systemObjectFieldCreator.
									getExternalReferenceCode()
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC",
						_objectDefinition1.getExternalReferenceCode()
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC_1
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		_relateAPIEndpointWithAPISchemas(
			_API_ENDPOINT_ERC_1, apiSchemaExternalReferenceCode);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition1, "externalReferenceCode",
			RandomTestUtil.randomString());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "c/" + _BASE_URL_1 + _API_APPLICATION_PATH_1,
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		jsonObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			objectEntry.getObjectEntryId(), jsonObject.getInt("id"));

		JSONObject creatorJSONObject = jsonObject.getJSONObject("creator");

		Assert.assertEquals(
			objectEntry.getUserName(), creatorJSONObject.getString("name"));
	}

	@Test
	public void testPostWithAllFields() throws Exception {
		_addAPIApplicationWithPostEndpoint(
			true, _objectDefinition1.getExternalReferenceCode(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		Document document = _addRandomDocument();

		long attachmentPropertyIdValue = document.getId();

		boolean booleanPropertyValue = RandomTestUtil.randomBoolean();
		String datePropertyValue = _dateFormat.format(
			RandomTestUtil.nextDate());
		String dateTimePropertyValue = _dateTimeFormat.format(
			RandomTestUtil.nextDate());
		double decimalPropertyValue = RandomTestUtil.randomDouble();
		int integerPropertyValue = RandomTestUtil.randomInt();
		long longIntegerPropertyValue = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String longTextPropertyValue = RandomTestUtil.randomString();
		Serializable multiselectPicklistPropertyValue =
			(Serializable)TransformUtil.transform(
				Arrays.asList(ListTypeValue.VALUE1, ListTypeValue.VALUE3),
				ListTypeValue::name);
		String picklistPropertyValue = ListTypeValue.VALUE1.name();
		double precisionDecimalPropertyValue = 1.1;
		String richTextPropertyValue = RandomTestUtil.randomString();
		String textPropertyValue = RandomTestUtil.randomString();
		String textUniquePropertyValue = "Unique field value";

		JSONAssert.assertEquals(
			JSONUtil.put(
				"attachmentProperty",
				JSONUtil.put("id", attachmentPropertyIdValue)
			).put(
				"booleanProperty", booleanPropertyValue
			).put(
				"dateProperty", datePropertyValue
			).put(
				"dateTimeProperty", dateTimePropertyValue
			).put(
				"decimalProperty", decimalPropertyValue
			).put(
				"integerProperty", integerPropertyValue
			).put(
				"longIntegerProperty", (Long)longIntegerPropertyValue
			).put(
				"longTextProperty", longTextPropertyValue
			).put(
				"multiselectPicklistProperty",
				JSONUtil.putAll(
					JSONUtil.put(
						"key", ListTypeValue.VALUE1
					).put(
						"name", ListTypeValue.VALUE1
					),
					JSONUtil.put(
						"key", ListTypeValue.VALUE3
					).put(
						"name", ListTypeValue.VALUE3
					))
			).put(
				"picklistProperty",
				JSONUtil.put(
					"key", ListTypeValue.VALUE1
				).put(
					"name", ListTypeValue.VALUE1
				)
			).put(
				"precisionDecimalProperty", precisionDecimalPropertyValue
			).put(
				"richTextProperty", richTextPropertyValue
			).put(
				"textProperty", textPropertyValue
			).put(
				"textUniqueProperty", textUniquePropertyValue
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"attachmentProperty", attachmentPropertyIdValue
				).put(
					"booleanProperty", booleanPropertyValue
				).put(
					"dateProperty", datePropertyValue
				).put(
					"dateTimeProperty", dateTimePropertyValue
				).put(
					"decimalProperty", decimalPropertyValue
				).put(
					"integerProperty", integerPropertyValue
				).put(
					"longIntegerProperty", longIntegerPropertyValue
				).put(
					"longTextProperty", longTextPropertyValue
				).put(
					"multiselectPicklistProperty",
					multiselectPicklistPropertyValue
				).put(
					"picklistProperty", picklistPropertyValue
				).put(
					"precisionDecimalProperty", precisionDecimalPropertyValue
				).put(
					"richTextProperty", richTextPropertyValue
				).put(
					"textProperty", textPropertyValue
				).put(
					"textUniqueProperty", textUniquePropertyValue
				).toString(),
				StringBundler.concat("c/", _BASE_URL_1, "/test"),
				Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition1.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());
	}

	@Test
	public void testPostWithDuplicateUniqueField() throws Exception {
		String title = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC_1
			).put(
				"title", title
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"The Base URL is already in use. Please enter a unique Base " +
					"URL."
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"applicationStatus", "unpublished"
				).put(
					"baseURL", _BASE_URL_1
				).put(
					"externalReferenceCode", _API_APPLICATION_ERC_1
				).put(
					"title", title
				).toString(),
				"headless-builder/applications", Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testPostWithMissingRequiredField() throws Exception {
		String objectFieldExternalReferenceCode = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new IntegerObjectFieldBuilder(
					).externalReferenceCode(
						objectFieldExternalReferenceCode
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"integerField"
					).required(
						true
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.putAll(
					_createAPIEndpoint(
						_API_ENDPOINT_ERC_1, Http.Method.POST, "/test", null,
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue(),
						APIApplication.Endpoint.Scope.COMPANY))
			).put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "integerProperty"
							).put(
								"objectFieldERC",
								objectFieldExternalReferenceCode
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC",
						objectDefinition.getExternalReferenceCode()
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC_1
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);
		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/requestAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_1),
			Http.Method.PUT);
		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/responseAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_1),
			Http.Method.PUT);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"No value was provided for required object field " +
					"\"integerField\""
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"booleanProperty", RandomTestUtil.randomBoolean()
				).toString(),
				StringBundler.concat("c/", _BASE_URL_1, "/test"),
				Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testPostWithoutResponseSchema() throws Exception {
		_addAPIApplicationWithPostEndpoint(
			false, _objectDefinition1.getExternalReferenceCode(),
			APIApplication.Endpoint.Scope.COMPANY);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		String textPropertyValue = RandomTestUtil.randomString();

		JSONAssert.assertEquals(
			"{}",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"textProperty", textPropertyValue
				).toString(),
				StringBundler.concat("c/", _BASE_URL_1, "/test"),
				Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition1.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		ObjectEntry objectEntry = objectEntries.get(objectEntries.size() - 1);

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(textPropertyValue, values.get("textField"));
	}

	@FeatureFlag("LPD-10964")
	@Test
	public void testPostWithRecordProperty() throws Exception {
		_addAPIApplicationWithRecordProperty(
			Http.Method.POST, _objectDefinition1.getExternalReferenceCode());

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		_testPostWithRecordProperty(
			JSONUtil.putAll(
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
				).put(
					"name", "record1"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
				).put(
					"name", "integerProperty"
				).put(
					"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
				)),
			JSONUtil.put(
				"integerProperty", 1
			).put(
				"record1", Collections.emptyMap()
			).toString(),
			JSONUtil.put("integerProperty", 1));
		_testPostWithRecordProperty(
			JSONUtil.putAll(
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
				).put(
					"name", "record1"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_2
				).put(
					"name", "record2"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_3
				).put(
					"name", "record3"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_4
				).put(
					"name", "record4"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_5
				).put(
					"name", "record5"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_4
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
				).put(
					"name", "integerProperty"
				).put(
					"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_2
				).put(
					"name", "relatedMultiselectPicklistProperty"
				).put(
					"objectFieldERC",
					_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 2
				).put(
					"objectRelationshipNames", _objectRelationship1.getName()
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_5
				)),
			JSONUtil.put(
				"record1", Collections.emptyMap()
			).put(
				"record2",
				JSONUtil.put(
					"integerProperty", 1
				).put(
					"record3", Collections.emptyMap()
				)
			).put(
				"record4",
				JSONUtil.put(
					"record5",
					JSONUtil.put(
						"relatedMultiselectPicklistProperty",
						Collections.emptyList()))
			).toString(),
			JSONUtil.put(
				"record2", JSONUtil.put("integerProperty", 1)
			).put(
				"record4",
				JSONUtil.put(
					"record5",
					JSONUtil.put(
						"relatedMultiselectPicklistProperty",
						Collections.emptyList()))
			));

		_disassociateAPIProperties();

		_testPostWithRecordProperty(
			JSONUtil.putAll(
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
				).put(
					"name", "record1"
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_2
				).put(
					"name", "record2"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_1
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_RECORD_ERC_3
				).put(
					"name", "record3"
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				).put(
					"type", "record"
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
				).put(
					"name", "integerProperty"
				).put(
					"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_2
				),
				JSONUtil.put(
					"description", "description"
				).put(
					"externalReferenceCode", _API_PROPERTY_VALUE_ERC_2
				).put(
					"name", "relatedMultiselectPicklistProperty"
				).put(
					"objectFieldERC",
					_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 2
				).put(
					"objectRelationshipNames", _objectRelationship1.getName()
				).put(
					"r_apiPropertyToAPIProperties_l_apiPropertyERC",
					_API_PROPERTY_RECORD_ERC_3
				)),
			JSONUtil.put(
				"record1",
				JSONUtil.put(
					"record2",
					JSONUtil.put(
						"integerProperty", 1
					).put(
						"record3",
						JSONUtil.put(
							"relatedMultiselectPicklistProperty",
							Collections.emptyList())
					))
			).toString(),
			JSONUtil.put(
				"record1",
				JSONUtil.put(
					"record2",
					JSONUtil.put(
						"integerProperty", 1
					).put(
						"record3",
						JSONUtil.put(
							"relatedMultiselectPicklistProperty",
							Collections.emptyList())
					))));
	}

	@Test
	public void testPostWithSiteScopedEndpoint() throws Exception {
		_addAPIApplicationWithPostEndpoint(
			true, _siteScopedObjectDefinition1.getExternalReferenceCode(),
			APIApplication.Endpoint.Scope.SITE);

		Document document = _addRandomDocument();

		String body = JSONUtil.put(
			"attachmentProperty", document.getId()
		).put(
			"booleanProperty", RandomTestUtil.randomBoolean()
		).put(
			"dateProperty", _dateFormat.format(RandomTestUtil.nextDate())
		).put(
			"dateTimeProperty",
			_dateTimeFormat.format(RandomTestUtil.nextDate())
		).put(
			"decimalProperty", RandomTestUtil.randomDouble()
		).put(
			"integerProperty", RandomTestUtil.randomInt()
		).put(
			"longIntegerProperty",
			RandomTestUtil.randomLong(
				ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
				ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX)
		).put(
			"longTextProperty", RandomTestUtil.randomString()
		).put(
			"multiselectPicklistProperty",
			TransformUtil.transform(
				Arrays.asList(ListTypeValue.VALUE1, ListTypeValue.VALUE3),
				ListTypeValue::name)
		).put(
			"picklistProperty", ListTypeValue.VALUE1.name()
		).put(
			"precisionDecimalProperty", 1.1
		).put(
			"richTextProperty", RandomTestUtil.randomString()
		).put(
			"textProperty", RandomTestUtil.randomString()
		).put(
			"textUniqueProperty", "Unique field value"
		).toString();

		String endpointPath = "c/" + _BASE_URL_1 + "/test";

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					body, endpointPath, Http.Method.POST));
		}

		long groupId = TestPropsValues.getGroupId();

		String scopedEndpointPath = StringBundler.concat(
			"c/", _BASE_URL_1, "/scopes/", groupId, "/test");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					body, scopedEndpointPath, Http.Method.POST));
		}

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					body, endpointPath, Http.Method.POST));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				body, scopedEndpointPath, Http.Method.POST));

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				groupId, _siteScopedObjectDefinition1.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		scopedEndpointPath = StringBundler.concat(
			"c/", _BASE_URL_1, "/scopes/", _group.getGroupId(), "/test");

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				body, scopedEndpointPath, Http.Method.POST));

		objectEntries = _objectEntryLocalService.getObjectEntries(
			_group.getGroupId(),
			_siteScopedObjectDefinition1.getObjectDefinitionId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());
	}

	private void _addAggregationObjectField(
			ObjectDefinition objectDefinition, String relationshipName)
		throws Exception {

		ObjectField aggregationObjectField = new AggregationObjectFieldBuilder(
		).externalReferenceCode(
			_API_SCHEMA_AGGREGATION_FIELD_ERC
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"aggregationField"
		).objectDefinitionId(
			objectDefinition.getObjectDefinitionId()
		).objectFieldSettings(
			Arrays.asList(
				_createObjectFieldSetting(
					ObjectFieldSettingConstants.NAME_FUNCTION,
					ObjectFieldSettingConstants.VALUE_COUNT),
				_createObjectFieldSetting(
					ObjectFieldSettingConstants.NAME_OBJECT_RELATIONSHIP_NAME,
					relationshipName))
		).build();

		ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(), aggregationObjectField);
	}

	private void _addAPIApplication(
			String apiApplicationExternalReferenceCode,
			String apiEndpointExternalReferenceCode, String baseURL,
			String objectDefinitionExternalReferenceCode,
			String objectRelationshipName1, String objectRelationshipName2,
			String path, String pathParameter, String retrieveType,
			APIApplication.Endpoint.Scope scope)
		throws Exception {

		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "integerProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_INTEGER_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "multiselectPicklistProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "textProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "textUniqueProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_UNIQUE_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedIntegerProperty1"
							).put(
								"objectFieldERC",
								_API_SCHEMA_INTEGER_FIELD_ERC + 2
							).put(
								"objectRelationshipNames",
								objectRelationshipName1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedMultiselectPicklistProperty1"
							).put(
								"objectFieldERC",
								_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 2
							).put(
								"objectRelationshipNames",
								objectRelationshipName1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedTextProperty1"
							).put(
								"objectFieldERC", _API_SCHEMA_TEXT_FIELD_ERC + 2
							).put(
								"objectRelationshipNames",
								objectRelationshipName1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedIntegerProperty2"
							).put(
								"objectFieldERC",
								_API_SCHEMA_INTEGER_FIELD_ERC + 3
							).put(
								"objectRelationshipNames",
								objectRelationshipName1 + "," +
									objectRelationshipName2
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedMultiselectPicklistProperty2"
							).put(
								"objectFieldERC",
								_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 3
							).put(
								"objectRelationshipNames",
								objectRelationshipName1 + "," +
									objectRelationshipName2
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "relatedTextProperty2"
							).put(
								"objectFieldERC", _API_SCHEMA_TEXT_FIELD_ERC + 3
							).put(
								"objectRelationshipNames",
								objectRelationshipName1 + "," +
									objectRelationshipName2
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC",
						objectDefinitionExternalReferenceCode
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", baseURL
			).put(
				"externalReferenceCode", apiApplicationExternalReferenceCode
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);
		assertSuccessfulJSONObject(
			_createAPIEndpoint(
				apiEndpointExternalReferenceCode, Http.Method.GET, path,
				pathParameter, retrieveType, scope
			).put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationERC",
				apiApplicationExternalReferenceCode
			).put(
				"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
				apiSchemaExternalReferenceCode
			).toString(),
			"headless-builder/endpoints", Http.Method.POST);
	}

	private void _addAPIApplicationWithPostEndpoint(
			boolean addResponseSchema,
			String objectDefinitionExternalReferenceCode,
			APIApplication.Endpoint.Scope scope)
		throws Exception {

		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.putAll(
					_createAPIEndpoint(
						_API_ENDPOINT_ERC_1, Http.Method.POST, "/test", null,
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue(),
						scope))
			).put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "attachmentProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_ATTACHMENT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "booleanProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_BOOLEAN_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "dateProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_DATE_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "dateTimeProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_DATE_TIME_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "decimalProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_DECIMAL_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "integerProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_INTEGER_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "longIntegerProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_LONG_INTEGER_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "longTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_LONG_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "multiselectPicklistProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "picklistProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_PICKLIST_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "precisionDecimalProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "richTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_RICH_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "textProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_TEXT_FIELD_ERC + 1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "textUniqueProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_UNIQUE_TEXT_FIELD_ERC + 1
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC",
						objectDefinitionExternalReferenceCode
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC_1
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/requestAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_1),
			Http.Method.PUT);

		if (addResponseSchema) {
			assertSuccessfulJSONObject(
				null,
				StringBundler.concat(
					"headless-builder/schemas/by-external-reference-code/",
					apiSchemaExternalReferenceCode,
					"/responseAPISchemaToAPIEndpoints/", _API_ENDPOINT_ERC_1),
				Http.Method.PUT);
		}
	}

	private void _addAPIApplicationWithRecordProperty(
			Http.Method httpMethod,
			String objectDefinitionExternalReferenceCode)
		throws Exception {

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_SCHEMA_OBJECT_PROPERTY_ERC
					).put(
						"mainObjectDefinitionERC",
						objectDefinitionExternalReferenceCode
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _BASE_URL_1
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC_1
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		if (Objects.equals(Http.Method.GET, httpMethod)) {
			assertSuccessfulJSONObject(
				_createAPIEndpoint(
					_API_ENDPOINT_ERC_1, Http.Method.GET,
					_API_APPLICATION_PATH_1,
					HeadlessBuilderConstants.PATH_PARAMETER_ID,
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue(),
					APIApplication.Endpoint.Scope.COMPANY
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationERC",
					_API_APPLICATION_ERC_1
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
					_API_SCHEMA_OBJECT_PROPERTY_ERC
				).toString(),
				"headless-builder/endpoints", Http.Method.POST);
		}
		else {
			assertSuccessfulJSONObject(
				_createAPIEndpoint(
					_API_ENDPOINT_ERC_1, Http.Method.POST,
					_API_APPLICATION_PATH_1, null,
					APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
						getValue(),
					APIApplication.Endpoint.Scope.COMPANY
				).put(
					"r_apiApplicationToAPIEndpoints_l_apiApplicationERC",
					_API_APPLICATION_ERC_1
				).put(
					"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
					_API_SCHEMA_OBJECT_PROPERTY_ERC
				).put(
					"r_requestAPISchemaToAPIEndpoints_l_apiSchemaERC",
					_API_SCHEMA_OBJECT_PROPERTY_ERC
				).toString(),
				"headless-builder/endpoints", Http.Method.POST);
		}
	}

	private void _addAPISort(String sortString) throws Exception {
		assertSuccessfulJSONObject(
			JSONUtil.put(
				"oDataSort", sortString
			).put(
				"r_apiEndpointToAPISorts_l_apiEndpointERC", _API_ENDPOINT_ERC_1
			).toString(),
			"headless-builder/sorts", Http.Method.POST);
	}

	private ObjectEntry _addCustomObjectEntry(
			int integerFieldValue,
			List<ListTypeValue> multiselectPicklistFieldValue,
			ObjectDefinition objectDefinition, String textFieldValue,
			String textUniqueFieldValue)
		throws Exception {

		return _addCustomObjectEntry(
			0L, integerFieldValue, multiselectPicklistFieldValue,
			objectDefinition, textFieldValue, textUniqueFieldValue);
	}

	private ObjectEntry _addCustomObjectEntry(
			long groupId, int integerFieldValue,
			List<ListTypeValue> multiselectPicklistFieldValue,
			ObjectDefinition objectDefinition, String textFieldValue,
			String textUniqueFieldValue)
		throws Exception {

		ListTypeValue listTypeValue = RandomTestUtil.randomEnum(
			ListTypeValue.class);

		Document document = _addRandomDocument();

		return ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"attachmentField", document.getId()
			).put(
				"booleanField", RandomTestUtil.randomBoolean()
			).put(
				"dateField", _dateFormat.format(RandomTestUtil.nextDate())
			).put(
				"dateTimeField",
				_dateTimeFormat.format(RandomTestUtil.nextDate())
			).put(
				"decimalField", RandomTestUtil.randomDouble()
			).put(
				"integerField", integerFieldValue
			).put(
				"longIntegerField",
				RandomTestUtil.randomLong(
					ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
					ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX)
			).put(
				"longTextField", RandomTestUtil.randomString()
			).put(
				"multiselectPicklistField",
				(Serializable)TransformUtil.transform(
					multiselectPicklistFieldValue, ListTypeValue::name)
			).put(
				"picklistField", listTypeValue.name()
			).put(
				"precisionDecimalField", RandomTestUtil.randomDouble()
			).put(
				"richTextField", RandomTestUtil.randomString()
			).put(
				"textField", textFieldValue
			).put(
				"textUniqueField", textUniqueFieldValue
			).build());
	}

	private ObjectDefinition _addObjectDefinition(int index, String scope)
		throws Exception {

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(
				new AttachmentObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_ATTACHMENT_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"attachmentField"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							"acceptedFileExtensions", "txt"),
						_createObjectFieldSetting(
							"fileSource", "documentsAndMedia"),
						_createObjectFieldSetting("maximumFileSize", "100"))
				).build(),
				new BooleanObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_BOOLEAN_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"booleanField"
				).build(),
				new DateObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_DATE_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateField"
				).build(),
				new DateTimeObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_DATE_TIME_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateTimeField"
				).objectFieldSettings(
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC))
				).build(),
				new DecimalObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_DECIMAL_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"decimalField"
				).build(),
				new IntegerObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_INTEGER_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"integerField"
				).build(),
				new LongIntegerObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_LONG_INTEGER_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longIntegerField"
				).build(),
				new LongTextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_LONG_TEXT_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longTextField"
				).build(),
				new MultiselectPicklistObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					_listTypeDefinition.getListTypeDefinitionId()
				).name(
					"multiselectPicklistField"
				).build(),
				new PicklistObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_PICKLIST_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"picklistField"
				).listTypeDefinitionId(
					_listTypeDefinition.getListTypeDefinitionId()
				).build(),
				new PrecisionDecimalObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"precisionDecimalField"
				).build(),
				new RichTextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_RICH_TEXT_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"richTextField"
				).build(),
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_TEXT_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build(),
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_UNIQUE_TEXT_FIELD_ERC + index
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textUniqueField"
				).objectFieldSettings(
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
							Boolean.TRUE.toString()))
				).build()),
			scope);
	}

	private ObjectRelationship _addObjectRelationship(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, String type)
		throws Exception {

		return ObjectRelationshipTestUtil.addObjectRelationship(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			objectDefinition1, objectDefinition2, TestPropsValues.getUserId(),
			type);
	}

	private Document _addRandomDocument() throws Exception {
		return _documentResource.postSiteDocument(
			TestPropsValues.getGroupId(),
			new Document() {
				{
					description = RandomTestUtil.randomString();
					fileName = RandomTestUtil.randomString() + ".txt";
					title = RandomTestUtil.randomString();
				}
			},
			HashMapBuilder.<String, File>put(
				"file",
				() -> FileUtil.createTempFile(TestDataConstants.TEST_BYTE_ARRAY)
			).build());
	}

	private void _assertFilterString(
			String expectedObjectFieldName,
			Serializable expectedObjectFieldValue, String filterString)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"c/", _BASE_URL_1, _API_APPLICATION_PATH_1, "?filter=",
				URLCodec.encodeURL(filterString)),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			String.valueOf(expectedObjectFieldValue),
			String.valueOf(itemJSONObject.get(expectedObjectFieldName)));
	}

	private JSONObject _createAPIEndpoint(
		String apiEndpointExternalReferenceCode, Http.Method method,
		String path, String pathParameter, String retrieveType,
		APIApplication.Endpoint.Scope scope) {

		if (Objects.equals(
				retrieveType,
				APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
					getValue()) &&
			(pathParameter != null)) {

			return JSONUtil.put(
				"description", "description"
			).put(
				"externalReferenceCode", apiEndpointExternalReferenceCode
			).put(
				"httpMethod", StringUtil.toLowerCase(method.name())
			).put(
				"name", "name"
			).put(
				"path", path + "/{pathId}"
			).put(
				"pathParameter", pathParameter
			).put(
				"retrieveType", retrieveType
			).put(
				"scope", StringUtil.toLowerCase(scope.name())
			);
		}

		return JSONUtil.put(
			"description", "description"
		).put(
			"externalReferenceCode", apiEndpointExternalReferenceCode
		).put(
			"httpMethod", StringUtil.toLowerCase(method.name())
		).put(
			"name", "name"
		).put(
			"path", path
		).put(
			"retrieveType", retrieveType
		).put(
			"scope", StringUtil.toLowerCase(scope.name())
		);
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private void _disassociateAPIProperties() throws Exception {
		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiSchemaToAPIProperties",
				JSONUtil.putAll(
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_RECORD_ERC_1
					).put(
						"name", "record1"
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					).put(
						"type", "record"
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_RECORD_ERC_2
					).put(
						"name", "record2"
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					).put(
						"type", "record"
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_RECORD_ERC_3
					).put(
						"name", "record3"
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					).put(
						"type", "record"
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_RECORD_ERC_4
					).put(
						"name", "record4"
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					).put(
						"type", "record"
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_RECORD_ERC_5
					).put(
						"name", "record5"
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					).put(
						"type", "record"
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_VALUE_ERC_1
					).put(
						"name", "integerProperty"
					).put(
						"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC + 1
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_PROPERTY_VALUE_ERC_2
					).put(
						"name", "relatedMultiselectPicklistProperty"
					).put(
						"objectFieldERC",
						_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC + 2
					).put(
						"objectRelationshipNames",
						_objectRelationship1.getName()
					).put(
						"r_apiPropertyToAPIProperties_l_apiPropertyId",
						StringPool.BLANK
					))
			).put(
				"description", "description"
			).put(
				"externalReferenceCode", _API_SCHEMA_OBJECT_PROPERTY_ERC
			).put(
				"mainObjectDefinitionERC",
				_objectDefinition1.getExternalReferenceCode()
			).put(
				"name", "name"
			).toString(),
			"headless-builder/schemas/by-external-reference-code/" +
				_API_SCHEMA_OBJECT_PROPERTY_ERC,
			Http.Method.PATCH);
	}

	private void _publishAPIApplication(
			String apiApplicationExternalReferenceCode)
		throws Exception {

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				apiApplicationExternalReferenceCode,
			Http.Method.PATCH);
	}

	private void _relateAPIEndpointWithAPISchemas(
			String apiEndpointExternalReferenceCode,
			String apiSchemaExternalReferenceCode)
		throws Exception {

		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/requestAPISchemaToAPIEndpoints/",
				apiEndpointExternalReferenceCode),
			Http.Method.PUT);
		assertSuccessfulJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/responseAPISchemaToAPIEndpoints/",
				apiEndpointExternalReferenceCode),
			Http.Method.PUT);
	}

	private void _relateObjectEntries(
			ObjectEntry objectEntry1, ObjectEntry objectEntry2,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectRelationshipTestUtil.relateObjectEntries(
			objectEntry1.getObjectEntryId(), objectEntry2.getObjectEntryId(),
			objectRelationship, TestPropsValues.getUserId());
	}

	private void _testGetWithRecordProperty(
			JSONArray apiSchemasToAPIPropertiesJSONArray,
			JSONObject expectedJSONObject, ObjectEntry objectEntry)
		throws Exception {

		_updateAPISchemaToAPIPropertiesJSONArray(
			apiSchemasToAPIPropertiesJSONArray);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1,
					StringPool.FORWARD_SLASH, objectEntry.getObjectEntryId()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _testPostWithRecordProperty(
			JSONArray apiSchemasToAPIPropertiesJSONArray, String body,
			JSONObject expectedJSONObject)
		throws Exception {

		_updateAPISchemaToAPIPropertiesJSONArray(
			apiSchemasToAPIPropertiesJSONArray);

		_publishAPIApplication(_API_APPLICATION_ERC_1);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				body,
				StringBundler.concat(
					"c/", _BASE_URL_1, _API_APPLICATION_PATH_1),
				Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _updateAPISchemaToAPIPropertiesJSONArray(
			JSONArray apiSchemaToAPIPropertiesJSONArray)
		throws Exception {

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiSchemaToAPIProperties", apiSchemaToAPIPropertiesJSONArray
			).put(
				"description", "description"
			).put(
				"externalReferenceCode", _API_SCHEMA_OBJECT_PROPERTY_ERC
			).put(
				"mainObjectDefinitionERC",
				_objectDefinition1.getExternalReferenceCode()
			).put(
				"name", "name"
			).toString(),
			"headless-builder/schemas/by-external-reference-code/" +
				_API_SCHEMA_OBJECT_PROPERTY_ERC,
			Http.Method.PATCH);
	}

	private static final String _API_APPLICATION_ERC_1 =
		RandomTestUtil.randomString();

	private static final String _API_APPLICATION_ERC_2 =
		RandomTestUtil.randomString();

	private static final String _API_APPLICATION_PATH_1 =
		StringPool.SLASH +
			StringUtil.toLowerCase(RandomTestUtil.randomString());

	private static final String _API_APPLICATION_PATH_2 =
		StringPool.SLASH +
			StringUtil.toLowerCase(RandomTestUtil.randomString());

	private static final String _API_ENDPOINT_ERC_1 =
		RandomTestUtil.randomString();

	private static final String _API_ENDPOINT_ERC_2 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_1 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_2 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_3 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_4 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_5 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_VALUE_ERC_1 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_VALUE_ERC_2 =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_AGGREGATION_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_ATTACHMENT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_BOOLEAN_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_DATE_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_DATE_TIME_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_DECIMAL_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_INTEGER_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_LONG_INTEGER_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_LONG_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_OBJECT_PROPERTY_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_PICKLIST_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_RICH_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_UNIQUE_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _BASE_URL_1 = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	private static final String _BASE_URL_2 = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	private static DateFormat _dateFormat;
	private static DateFormat _dateTimeFormat;

	private DocumentResource _documentResource;
	private Group _group;
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;
	private ObjectDefinition _objectDefinition3;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	private ObjectRelationship _objectRelationship1;
	private ObjectRelationship _objectRelationship2;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private SAPEntry _sapEntry;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	private ObjectDefinition _siteScopedObjectDefinition1;
	private ObjectDefinition _siteScopedObjectDefinition2;
	private ObjectDefinition _siteScopedObjectDefinition3;
	private ObjectRelationship _siteScopedObjectRelationship1;
	private ObjectRelationship _siteScopedObjectRelationship2;

	private enum ListTypeValue {

		VALUE1, VALUE2, VALUE3

	}

}