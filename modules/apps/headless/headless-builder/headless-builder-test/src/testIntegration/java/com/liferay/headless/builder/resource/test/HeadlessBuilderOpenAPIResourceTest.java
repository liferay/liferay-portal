/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
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
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carlos Correa
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlags({"LPD-10964", "LPS-178642"})
@RunWith(Arquillian.class)
public class HeadlessBuilderOpenAPIResourceTest extends BaseTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ListTypeEntry listTypeEntry = ListTypeEntryUtil.createListTypeEntry(
			RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()));

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Collections.singletonList(listTypeEntry));

		_objectDefinition1 = _publishObjectDefinition(
			Arrays.asList(
				new AttachmentObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_ATTACHMENT_FIELD_ERC
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
					_API_SCHEMA_BOOLEAN_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"booleanField"
				).build(),
				new DateObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_DATE_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateField"
				).build(),
				new DateTimeObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_DATE_TIME_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateTimeField"
				).objectFieldSettings(
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							ObjectFieldSettingConstants.
								VALUE_USE_INPUT_AS_ENTERED))
				).build(),
				new DecimalObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_DECIMAL_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"decimalField"
				).build(),
				new IntegerObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_INTEGER_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"integerField"
				).build(),
				new LongIntegerObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_LONG_INTEGER_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longIntegerField"
				).build(),
				new LongTextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_LONG_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longTextField"
				).build(),
				new MultiselectPicklistObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC
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
					_API_SCHEMA_PICKLIST_FIELD_ERC
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
					_API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"precisionDecimalField"
				).build(),
				new RichTextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_RICH_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"richTextField"
				).build(),
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectDefinition2 = _publishObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_objectDefinition3 = _publishObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_OBJECT_PROPERTY_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build(),
				new LongTextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SCHEMA_OBJECT_PROPERTY_LONG_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longTextField"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_singleElementObjectDefinition = _publishObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SINGLE_ELEMENT_SCHEMA_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_singleElementSiteScopedObjectDefinition = _publishObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SINGLE_ELEMENT_SITE_SCOPED_SCHEMA_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);
		_siteScopedObjectDefinition = _publishObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).externalReferenceCode(
					_API_SITE_SCOPED_SCHEMA_TEXT_FIELD_ERC
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textField"
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);

		String relationshipName = "a" + RandomTestUtil.randomString();

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_objectDefinition1.getObjectDefinitionId(),
			_objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			relationshipName, false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectField aggregationObjectField = new AggregationObjectFieldBuilder(
		).externalReferenceCode(
			_API_SCHEMA_AGGREGATION_FIELD_ERC
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"aggregationField"
		).objectDefinitionId(
			_objectDefinition1.getObjectDefinitionId()
		).objectFieldSettings(
			Arrays.asList(
				_createObjectFieldSetting(
					ObjectFieldSettingConstants.NAME_FUNCTION,
					ObjectFieldSettingConstants.VALUE_COUNT),
				_createObjectFieldSetting(
					ObjectFieldSettingConstants.NAME_OBJECT_RELATIONSHIP_NAME,
					relationshipName))
		).build();

		_objectFieldLocalService.addCustomObjectField(
			aggregationObjectField.getExternalReferenceCode(),
			TestPropsValues.getUserId(),
			aggregationObjectField.getListTypeDefinitionId(),
			aggregationObjectField.getObjectDefinitionId(),
			aggregationObjectField.getBusinessType(),
			aggregationObjectField.getDBType(),
			aggregationObjectField.isIndexed(),
			aggregationObjectField.isIndexedAsKeyword(),
			aggregationObjectField.getIndexedLanguageId(),
			aggregationObjectField.getLabelMap(),
			aggregationObjectField.isLocalized(),
			aggregationObjectField.getName(),
			aggregationObjectField.getReadOnly(),
			aggregationObjectField.getReadOnlyConditionExpression(),
			aggregationObjectField.isRequired(),
			aggregationObjectField.isState(),
			aggregationObjectField.getObjectFieldSettings());
	}

	@After
	public void tearDown() throws Exception {
		assertSuccessfulJSONObject(
			JSONUtil.put(
				"requestAPISchemaToAPIEndpoints",
				JSONFactoryUtil.createJSONArray()
			).put(
				"responseAPISchemaToAPIEndpoints",
				JSONFactoryUtil.createJSONArray()
			).toString(),
			"headless-builder/schemas/by-external-reference-code/" +
				_API_SCHEMA_ERC,
			Http.Method.PATCH);
		assertSuccessfulJSONObject(
			JSONUtil.put(
				"requestAPISchemaToAPIEndpoints",
				JSONFactoryUtil.createJSONArray()
			).put(
				"responseAPISchemaToAPIEndpoints",
				JSONFactoryUtil.createJSONArray()
			).toString(),
			"headless-builder/schemas/by-external-reference-code/" +
				_API_SITE_SCOPED_SCHEMA_ERC,
			Http.Method.PATCH);
	}

	@Test
	public void test() throws Exception {
		_addAPIApplication();
		_addUnrelatedAPIApplication();

		String apiApplicationURL = "/c/" + _API_BASE_URL;

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "/openapi", Http.Method.GET);

		Assert.assertFalse(jsonObject.has("/" + _API_BASE_URL));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null, apiApplicationURL + "/openapi.json",
					Http.Method.GET));
		}

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				_API_APPLICATION_ERC,
			Http.Method.PATCH);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "/openapi", Http.Method.GET);

		JSONAssert.assertEquals(
			JSONUtil.put(
				apiApplicationURL,
				JSONUtil.put(
					"http://localhost:8080/o/c/" + _API_BASE_URL +
						"/openapi.yaml")
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, apiApplicationURL + "/openapi.json", Http.Method.GET);

		JSONAssert.assertEquals(
			StringUtil.replace(
				new String(
					FileUtil.getBytes(
						getClass(), "dependencies/expected_openapi.json")),
				"${BASE_URL}", "c/" + _API_BASE_URL),
			jsonObject.toString(), JSONCompareMode.STRICT);
	}

	private void _addAPIApplication() throws Exception {
		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPISchemas",
				JSONUtil.putAll(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "aggregationProperty description"
							).put(
								"name", "aggregationProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_AGGREGATION_FIELD_ERC
							),
							JSONUtil.put(
								"description", "attachmentProperty description"
							).put(
								"name", "attachmentProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_ATTACHMENT_FIELD_ERC
							),
							JSONUtil.put(
								"description", "booleanProperty description"
							).put(
								"name", "booleanProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_BOOLEAN_FIELD_ERC
							),
							JSONUtil.put(
								"description", "dateProperty description"
							).put(
								"name", "dateProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_DATE_FIELD_ERC
							),
							JSONUtil.put(
								"description", "dateTimeProperty description"
							).put(
								"name", "dateTimeProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_DATE_TIME_FIELD_ERC
							),
							JSONUtil.put(
								"description", "decimalProperty description"
							).put(
								"name", "decimalProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_DECIMAL_FIELD_ERC
							),
							JSONUtil.put(
								"description", "integerProperty description"
							).put(
								"name", "integerProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_INTEGER_FIELD_ERC
							),
							JSONUtil.put(
								"description", "longIntegerProperty description"
							).put(
								"name", "longIntegerProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_LONG_INTEGER_FIELD_ERC
							),
							JSONUtil.put(
								"description", "longTextProperty description"
							).put(
								"name", "longTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_LONG_TEXT_FIELD_ERC
							),
							JSONUtil.put(
								"description",
								"multiselectPicklistProperty description"
							).put(
								"name", "multiselectPicklistProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC
							),
							JSONUtil.put(
								"description", "picklistProperty description"
							).put(
								"name", "picklistProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_PICKLIST_FIELD_ERC
							),
							JSONUtil.put(
								"description",
								"precisionDecimalProperty description"
							).put(
								"name", "precisionDecimalProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC
							),
							JSONUtil.put(
								"description", "richTextProperty description"
							).put(
								"name", "richTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_RICH_TEXT_FIELD_ERC
							),
							JSONUtil.put(
								"description", "textProperty description"
							).put(
								"name", "textProperty"
							).put(
								"objectFieldERC", _API_SCHEMA_TEXT_FIELD_ERC
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_SCHEMA_ERC
					).put(
						"mainObjectDefinitionERC",
						_objectDefinition1.getExternalReferenceCode()
					).put(
						"name", "SchemaName"
					),
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"name", "recordProperty1"
							).put(
								"type", "record"
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								_API_PROPERTY_RECORD_ERC_1
							).put(
								"name", "recordProperty2"
							).put(
								"type", "record"
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"name", "recordProperty3"
							).put(
								"r_apiPropertyToAPIProperties_l_apiPropertyERC",
								_API_PROPERTY_RECORD_ERC_1
							).put(
								"type", "record"
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								_API_PROPERTY_RECORD_ERC_2
							).put(
								"name", "recordProperty4"
							).put(
								"type", "record"
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								_API_PROPERTY_RECORD_ERC_3
							).put(
								"name", "recordProperty5"
							).put(
								"r_apiPropertyToAPIProperties_l_apiPropertyERC",
								_API_PROPERTY_RECORD_ERC_2
							).put(
								"type", "record"
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"name", "textProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_OBJECT_PROPERTY_TEXT_FIELD_ERC
							).put(
								"r_apiPropertyToAPIProperties_l_apiPropertyERC",
								_API_PROPERTY_RECORD_ERC_1
							),
							JSONUtil.put(
								"description", "description"
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"name", "longTextProperty"
							).put(
								"objectFieldERC",
								_API_SCHEMA_OBJECT_PROPERTY_LONG_TEXT_FIELD_ERC
							).put(
								"r_apiPropertyToAPIProperties_l_apiPropertyERC",
								_API_PROPERTY_RECORD_ERC_3
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", RandomTestUtil.randomString()
					).put(
						"mainObjectDefinitionERC",
						_objectDefinition3.getExternalReferenceCode()
					).put(
						"name", "RecordSchemaName"
					),
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "singleElementTextProperty"
							).put(
								"objectFieldERC",
								_API_SINGLE_ELEMENT_SCHEMA_TEXT_FIELD_ERC
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_SINGLE_ELEMENT_SCHEMA_ERC
					).put(
						"mainObjectDefinitionERC",
						_singleElementObjectDefinition.
							getExternalReferenceCode()
					).put(
						"name", "SingleElementSchemaName"
					),
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description",
								"singleElementSiteScopedTextProperty " +
									"description"
							).put(
								"name", "singleElementSiteScopedTextProperty"
							).put(
								"objectFieldERC",
								_API_SINGLE_ELEMENT_SITE_SCOPED_SCHEMA_TEXT_FIELD_ERC
							))
					).put(
						"description", "site scoped description"
					).put(
						"externalReferenceCode",
						_API_SINGLE_ELEMENT_SITE_SCOPED_SCHEMA_ERC
					).put(
						"mainObjectDefinitionERC",
						_singleElementSiteScopedObjectDefinition.
							getExternalReferenceCode()
					).put(
						"name", "SingleElementSiteScopedSchemaName"
					),
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.putAll(
							JSONUtil.put(
								"description",
								"siteScopedTextProperty description"
							).put(
								"name", "siteScopedTextProperty"
							).put(
								"objectFieldERC",
								_API_SITE_SCOPED_SCHEMA_TEXT_FIELD_ERC
							))
					).put(
						"description", "site scoped description"
					).put(
						"externalReferenceCode", _API_SITE_SCOPED_SCHEMA_ERC
					).put(
						"mainObjectDefinitionERC",
						_siteScopedObjectDefinition.getExternalReferenceCode()
					).put(
						"name", "SiteScopedSchemaName"
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", _API_BASE_URL
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC
			).put(
				"title", "title"
			).toString(),
			"headless-builder/applications", Http.Method.POST);
		assertSuccessfulJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.putAll(
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode",
						_API_SINGLE_ELEMENT_ENDPOINT_ERC
					).put(
						"httpMethod", "get"
					).put(
						"name", " single element name"
					).put(
						"path", "/single-element-path/{singleElementPathId}"
					).put(
						"pathParameter",
						HeadlessBuilderConstants.PATH_PARAMETER_ID
					).put(
						"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SINGLE_ELEMENT_SCHEMA_ERC
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue()
					).put(
						"scope",
						APIApplication.Endpoint.Scope.COMPANY.getValue()
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode",
						_API_SINGLE_ELEMENT_SITE_SCOPED_ENDPOINT_ERC
					).put(
						"httpMethod", "get"
					).put(
						"name", " single element site scoped name"
					).put(
						"path",
						"/single-element-path/by-external-reference-code" +
							"/{singleElementPathERC}"
					).put(
						"pathParameter",
						HeadlessBuilderConstants.PATH_PARAMETER_ERC
					).put(
						"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SINGLE_ELEMENT_SITE_SCOPED_SCHEMA_ERC
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue()
					).put(
						"scope", APIApplication.Endpoint.Scope.SITE.getValue()
					),
					JSONUtil.put(
						"description",
						"post endpoint no request schema description"
					).put(
						"externalReferenceCode",
						_API_POST_COMPANY_SCOPED_NO_SCHEMA_ENDPOINT_ERC
					).put(
						"httpMethod", "post"
					).put(
						"name", "company scoped post no schema"
					).put(
						"path", "/no-schema"
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue()
					).put(
						"scope",
						APIApplication.Endpoint.Scope.COMPANY.getValue()
					),
					JSONUtil.put(
						"description", "site scoped description"
					).put(
						"externalReferenceCode", _API_SITE_SCOPED_ENDPOINT_ERC
					).put(
						"httpMethod", "get"
					).put(
						"name", "site scoped name"
					).put(
						"path", "/site-scoped-path"
					).put(
						"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SITE_SCOPED_SCHEMA_ERC
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue()
					).put(
						"scope", APIApplication.Endpoint.Scope.SITE.getValue()
					),
					JSONUtil.put(
						"description", "site scoped no schema description"
					).put(
						"externalReferenceCode",
						_API_SITE_SCOPED_NO_SCHEMA_ENDPOINT_ERC
					).put(
						"httpMethod", "get"
					).put(
						"name", "site scoped no schema name"
					).put(
						"path", "/no-schema"
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue()
					).put(
						"scope", APIApplication.Endpoint.Scope.SITE.getValue()
					),
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode", _API_GET_ENDPOINT_ERC
					).put(
						"httpMethod", "get"
					).put(
						"name", "name"
					).put(
						"path", "/path"
					).put(
						"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SCHEMA_ERC
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue()
					).put(
						"scope",
						APIApplication.Endpoint.Scope.COMPANY.getValue()
					),
					JSONUtil.put(
						"description", "post description"
					).put(
						"externalReferenceCode", _API_POST_ENDPOINT_ERC
					).put(
						"httpMethod", "post"
					).put(
						"name", "post endpoint"
					).put(
						"path", "/path"
					).put(
						"r_requestAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SCHEMA_ERC
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue()
					).put(
						"scope",
						APIApplication.Endpoint.Scope.COMPANY.getValue()
					),
					JSONUtil.put(
						"description", "site scoped post description"
					).put(
						"externalReferenceCode",
						_API_SITE_SCOPED_POST_ENDPOINT_ERC
					).put(
						"httpMethod", "post"
					).put(
						"name", "site scoped post endpoint"
					).put(
						"path", "/site-scoped-path"
					).put(
						"r_requestAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SITE_SCOPED_SCHEMA_ERC
					).put(
						"r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC",
						_API_SITE_SCOPED_SCHEMA_ERC
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT.
							getValue()
					).put(
						"scope", APIApplication.Endpoint.Scope.SITE.getValue()
					))
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				_API_APPLICATION_ERC,
			Http.Method.PATCH);
	}

	private void _addUnrelatedAPIApplication() throws Exception {
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.put(
							JSONUtil.put(
								"description", RandomTestUtil.randomString()
							).put(
								"name", RandomTestUtil.randomString()
							).put(
								"objectFieldERC", "APPLICATION_STATUS"
							))
					).put(
						"description", RandomTestUtil.randomString()
					).put(
						"mainObjectDefinitionERC", "L_API_APPLICATION"
					).put(
						"name", RandomTestUtil.randomString()
					))
			).put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private ObjectDefinition _publishObjectDefinition(
			List<ObjectField> objectFields, String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private static final String _API_APPLICATION_ERC =
		RandomTestUtil.randomString();

	private static final String _API_BASE_URL = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	private static final String _API_GET_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String
		_API_POST_COMPANY_SCOPED_NO_SCHEMA_ENDPOINT_ERC =
			RandomTestUtil.randomString();

	private static final String _API_POST_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_1 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_2 =
		RandomTestUtil.randomString();

	private static final String _API_PROPERTY_RECORD_ERC_3 =
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

	private static final String _API_SCHEMA_ERC = RandomTestUtil.randomString();

	private static final String _API_SCHEMA_INTEGER_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_LONG_INTEGER_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_LONG_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_MULTISELECT_PICKLIST_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String
		_API_SCHEMA_OBJECT_PROPERTY_LONG_TEXT_FIELD_ERC =
			RandomTestUtil.randomString();

	private static final String _API_SCHEMA_OBJECT_PROPERTY_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_PICKLIST_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_PRECISION_DECIMAL_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_RICH_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SCHEMA_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SINGLE_ELEMENT_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SINGLE_ELEMENT_SCHEMA_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SINGLE_ELEMENT_SCHEMA_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SINGLE_ELEMENT_SITE_SCOPED_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SINGLE_ELEMENT_SITE_SCOPED_SCHEMA_ERC =
		RandomTestUtil.randomString();

	private static final String
		_API_SINGLE_ELEMENT_SITE_SCOPED_SCHEMA_TEXT_FIELD_ERC =
			RandomTestUtil.randomString();

	private static final String _API_SITE_SCOPED_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SITE_SCOPED_NO_SCHEMA_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SITE_SCOPED_POST_ENDPOINT_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SITE_SCOPED_SCHEMA_ERC =
		RandomTestUtil.randomString();

	private static final String _API_SITE_SCOPED_SCHEMA_TEXT_FIELD_ERC =
		RandomTestUtil.randomString();

	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition3;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _singleElementObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _singleElementSiteScopedObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _siteScopedObjectDefinition;

}