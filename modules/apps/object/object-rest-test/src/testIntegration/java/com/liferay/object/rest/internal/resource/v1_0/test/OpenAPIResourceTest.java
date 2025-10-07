/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;

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
public class OpenAPIResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			"Object1",
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, true, null,
					"field1", "field1",
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							String.valueOf(1)
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					"field2", "field2", false)),
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	@Test
	public void testGetOpenAPI() throws Exception {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false,
				TransformUtil.transformToList(
					new String[] {"value1", "value2"},
					listTypeValue -> ListTypeEntryUtil.createListTypeEntry(
						listTypeValue,
						Collections.singletonMap(
							LocaleUtil.US, listTypeValue))));

		ObjectDefinition relatedObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object2",
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field1", "field1", false),
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("field2")
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						"multiselectPicklistField"
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition relatedObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object3",
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field1", "field1", false),
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("field2")
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						"multiselectPicklistField"
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectDefinition relatedObjectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object4",
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field1", "field1", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition relatedObjectDefinition4 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object5",
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field1", "field1", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition relatedObjectDefinition5 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object6",
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field1", "field1", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition relatedObjectDefinition6 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object7",
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field1", "field1", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			relatedObjectDefinition1.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship1"), "relationship1",
			false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			relatedObjectDefinition1.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship2"), "relationship2",
			false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			relatedObjectDefinition1.getObjectDefinitionId(),
			relatedObjectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship3"), "relationship3",
			false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			relatedObjectDefinition1.getObjectDefinitionId(),
			relatedObjectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship4"), "relationship4",
			false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			relatedObjectDefinition2.getObjectDefinitionId(),
			relatedObjectDefinition3.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship5"), "relationship5",
			false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			relatedObjectDefinition3.getObjectDefinitionId(),
			relatedObjectDefinition4.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship6"), "relationship6",
			false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			relatedObjectDefinition4.getObjectDefinitionId(),
			relatedObjectDefinition5.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship7"), "relationship7",
			false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			relatedObjectDefinition5.getObjectDefinitionId(),
			relatedObjectDefinition6.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap("relationship8"), "relationship8",
			false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
			"externalReferenceCode", "externalReferenceCode", false);

		_assertOpenAPI("expected_openapi.json", _objectDefinition);
		_assertOpenAPI(
			"expected_openapi_related.json", relatedObjectDefinition1);
		_assertOpenAPI(
			"expected_openapi_site.json",
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object8",
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field", "field", false)),
				ObjectDefinitionConstants.SCOPE_SITE));

		ObjectDefinition categorizationDisabledObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"Object9",
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"field", "field", false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		categorizationDisabledObjectDefinition.setEnableCategorization(false);

		categorizationDisabledObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				categorizationDisabledObjectDefinition);

		_assertOpenAPI(
			"expected_openapi_categorization_disabled.json",
			categorizationDisabledObjectDefinition);
	}

	@Test
	public void testGetOpenAPIInDifferentCompany() throws Exception {
		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"domain", "able.com"
			).put(
				"portalInstanceId", "able.com"
			).put(
				"virtualHost", "www.able.com"
			).toString(),
			"headless-portal-instances/v1.0/portal-instances",
			Http.Method.POST);

		long companyId = jsonObject.getLong("companyId");

		try {
			HTTPTestUtil.customize(
			).withBaseURL(
				"http://www.able.com:8080"
			).withCredentials(
				"test@able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).apply(
				() -> {
					User user = UserTestUtil.addUser(
						_companyLocalService.getCompany(companyId));

					ObjectDefinition companyObjectDefinition =
						ObjectDefinitionTestUtil.publishObjectDefinition(
							"Object1",
							Collections.singletonList(
								ObjectFieldUtil.createObjectField(
									ObjectFieldConstants.BUSINESS_TYPE_TEXT,
									ObjectFieldConstants.DB_TYPE_STRING, true,
									true, null, "field", "field", false)),
							ObjectDefinitionConstants.SCOPE_COMPANY,
							user.getUserId());

					Assert.assertEquals(
						200,
						HTTPTestUtil.invokeToHttpCode(
							null, companyObjectDefinition.getRESTContextPath(),
							Http.Method.GET));

					JSONObject openAPIJSONObject =
						HTTPTestUtil.invokeToJSONObject(
							null, "/openapi", Http.Method.GET);

					JSONArray jsonArray = openAPIJSONObject.getJSONArray(
						companyObjectDefinition.getRESTContextPath());

					Assert.assertEquals(1, jsonArray.length());
					Assert.assertEquals(
						"http://www.able.com:8080/o" +
							companyObjectDefinition.getRESTContextPath() +
								"/openapi.yaml",
						jsonArray.get(0));
				}
			);
		}
		finally {
			if (companyId != 0) {
				_companyLocalService.deleteCompany(companyId);
			}
		}
	}

	@FeatureFlag("LPS-180090")
	@Test
	public void testGetOpenAPIWithActions() throws Exception {
		_assertOpenAPI("expected_openapi_actions.json", _objectDefinition);

		_objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"objectAction", ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);

		_assertOpenAPI(
			"expected_openapi_actions_object_action.json", _objectDefinition);
	}

	@Test
	public void testGetOpenAPIWithSystemObjectRelationship() throws Exception {
		_user = TestPropsValues.getUser();

		SystemObjectDefinitionManager userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		_userSystemObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				userSystemObjectDefinitionManager.getName());

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			userSystemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, _user.getUserId(),
			_userSystemObjectDefinition.getObjectDefinitionId(),
			_objectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"relation1ToM", false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY,
			null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, _user.getUserId(), _objectDefinition.getObjectDefinitionId(),
			_userSystemObjectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"relationMTo1", false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY,
			null);
		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, _user.getUserId(),
			_userSystemObjectDefinition.getObjectDefinitionId(),
			_objectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"relationMToM", false,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		JSONAssert.assertEquals(
			new String(
				FileUtil.getBytes(
					getClass(),
					"dependencies" +
						"/expected_openapi_system_object_relationship.json")),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					jaxRsApplicationDescriptor.getApplicationPath(),
					StringPool.SLASH, jaxRsApplicationDescriptor.getVersion(),
					"/openapi.json"),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private void _assertOpenAPI(
			String fileName, ObjectDefinition objectDefinition)
		throws Exception {

		JSONAssert.assertEquals(
			new String(
				FileUtil.getBytes(getClass(), "dependencies/" + fileName)),
			HTTPTestUtil.invokeToJSONObject(
				null, objectDefinition.getRESTContextPath() + "/openapi.json",
				Http.Method.GET
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private User _user;
	private ObjectDefinition _userSystemObjectDefinition;

	@DeleteAfterTestRun
	private ObjectField _userSystemObjectField;

}