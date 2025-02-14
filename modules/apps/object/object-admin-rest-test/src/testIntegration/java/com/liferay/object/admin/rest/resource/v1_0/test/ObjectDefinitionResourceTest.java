/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinitionSetting;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutBox;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutColumn;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutRow;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutTab;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRuleSetting;
import com.liferay.object.admin.rest.client.dto.v1_0.Status;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.problem.Problem;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectDefinitionSerDes;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlags("LPD-34594")
@RunWith(Arquillian.class)
public class ObjectDefinitionResourceTest
	extends BaseObjectDefinitionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectFolder1 = _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString());

		_objectFolder2 = _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			try {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition.getId());
			}
			catch (NoSuchObjectDefinitionException
						noSuchObjectDefinitionException) {

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchObjectDefinitionException);
				}
			}
		}
	}

	@Override
	@Test
	public void testGetObjectDefinition() throws Exception {
		super.testGetObjectDefinition();

		ObjectDefinition objectDefinition =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		String objectDefinitionPluralName = StringUtil.lowerCaseFirstLetter(
			TextFormatter.formatPlural(objectDefinition.getName()));

		Assert.assertEquals(
			"/o/c/" + objectDefinitionPluralName,
			objectDefinition.getRestContextPath());
	}

	@Override
	@Test
	public void testGetObjectDefinitionByExternalReferenceCode()
		throws Exception {

		super.testGetObjectDefinitionByExternalReferenceCode();

		ObjectDefinition objectDefinition =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"object-admin/v1.0/object-definitions",
				"/by-external-reference-code/",
				objectDefinition.getExternalReferenceCode(),
				"?nestedFields=objectFields"),
			Http.Method.GET);

		JSONArray jsonArray = jsonObject.getJSONArray("objectFields");

		Assert.assertEquals(jsonArray.toString(), 7, jsonArray.length());
	}

	@Override
	@Test
	public void testGetObjectDefinitionsPage() throws Exception {
		ObjectDefinitionResource.Builder builder =
			ReflectionTestUtil.getFieldValue(
				objectDefinitionResource, "_builder");

		ReflectionTestUtil.setFieldValue(
			this, "objectDefinitionResource",
			ProxyUtil.newProxyInstance(
				ObjectDefinitionResourceTest.class.getClassLoader(),
				new Class<?>[] {ObjectDefinitionResource.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getObjectDefinitionsPage")) {

						args[3] = Pagination.of(1, 20);
					}

					return method.invoke(builder.build(), args);
				}));

		try {
			super.testGetObjectDefinitionsPage();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				this, "objectDefinitionResource", builder.build());
		}

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, "status/any(k:k eq 2)", Pagination.of(1, 20), null);

		long totalCount = page.getTotalCount();

		ObjectDefinition objectDefinition =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		Status status = new Status() {
			{
				code = WorkflowConstants.STATUS_APPROVED;
				label = WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_APPROVED);
				label_i18n = _language.get(
					LanguageResources.getResourceBundle(
						LocaleUtil.getDefault()),
					WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_APPROVED));
			}
		};

		randomObjectDefinition.setStatus(status);

		testGetObjectDefinitionsPage_addObjectDefinition(
			randomObjectDefinition);

		page = objectDefinitionResource.getObjectDefinitionsPage(
			null, null, "status/any(k:k eq 2)", Pagination.of(1, 20), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(
			objectDefinition, (List<ObjectDefinition>)page.getItems());
	}

	@Override
	@Test
	public void testGetObjectDefinitionsPageWithSortString() throws Exception {
		ObjectDefinition objectDefinition1 = randomObjectDefinition();

		objectDefinition1.setName("A" + objectDefinition1.getName());

		objectDefinition1 = testGetObjectDefinitionsPage_addObjectDefinition(
			objectDefinition1);

		ObjectDefinition objectDefinition2 = randomObjectDefinition();

		objectDefinition2.setName("B" + objectDefinition2.getName());

		objectDefinition2 = testGetObjectDefinitionsPage_addObjectDefinition(
			objectDefinition2);

		Page<ObjectDefinition> ascPage =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, null, null, "name:asc");

		List<ObjectDefinition> objectDefinitions =
			(List<ObjectDefinition>)ascPage.getItems();

		assertEquals(
			Arrays.asList(objectDefinition1, objectDefinition2),
			objectDefinitions.subList(2, 4));

		Page<ObjectDefinition> descPage =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, null, null, "name:desc");

		objectDefinitions = (List<ObjectDefinition>)descPage.getItems();

		assertEquals(
			Arrays.asList(objectDefinition2, objectDefinition1),
			objectDefinitions.subList(
				objectDefinitions.size() - 4, objectDefinitions.size() - 2));

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition1.getId());
		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition2.getId());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectDefinitionNotFound() {
	}

	@Override
	@Test
	public void testGraphQLGetObjectDefinitionsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitions",
			HashMapBuilder.<String, Object>put(
				"page", 1
			).put(
				"pageSize",
				() -> {
					int objectDefinitionsCount =
						_objectDefinitionLocalService.getObjectDefinitionsCount(
							TestPropsValues.getCompanyId());

					return objectDefinitionsCount + 10;
				}
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject objectDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitions");

		long totalCount = objectDefinitionsJSONObject.getLong("totalCount");

		ObjectDefinition objectDefinition1 =
			testGraphQLGetObjectDefinitionsPage_addObjectDefinition();
		ObjectDefinition objectDefinition2 =
			testGraphQLGetObjectDefinitionsPage_addObjectDefinition();

		objectDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitions");

		Assert.assertEquals(
			totalCount + 2, objectDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			objectDefinition1,
			Arrays.asList(
				ObjectDefinitionSerDes.toDTOs(
					objectDefinitionsJSONObject.getString("items"))));
		assertContains(
			objectDefinition2,
			Arrays.asList(
				ObjectDefinitionSerDes.toDTOs(
					objectDefinitionsJSONObject.getString("items"))));
	}

	@Override
	@Test
	public void testPostObjectDefinition() throws Exception {
		super.testPostObjectDefinition();

		// Enable index search

		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setEnableIndexSearch((Boolean)null);
		randomObjectDefinition.setObjectFields((ObjectField[])null);

		ObjectDefinition postObjectDefinition =
			testPostObjectDefinition_addObjectDefinition(
				randomObjectDefinition);

		Assert.assertTrue(postObjectDefinition.getEnableIndexSearch());
		Assert.assertTrue(
			ArrayUtil.isEmpty(
				ArrayUtil.filter(
					postObjectDefinition.getObjectFields(),
					objectField -> !objectField.getSystem())));

		// Modifiable system object definition

		String randomListTypeDefinitionExternalReferenceCode =
			RandomTestUtil.randomString();

		ObjectDefinition randomModifiableSystemObjectDefinition =
			_randomModifiableSystemObjectDefinition();

		randomModifiableSystemObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.PICKLIST;
						DBType = ObjectField.DBType.create("String");
						externalReferenceCode = RandomTestUtil.randomString();
						indexed = false;
						indexedAsKeyword = false;
						label = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						listTypeDefinitionExternalReferenceCode =
							randomListTypeDefinitionExternalReferenceCode;
						localized = false;
						name = "a" + RandomTestUtil.randomString();
						readOnly = ReadOnly.FALSE;
						required = false;
						state = false;
						system = true;
					}
				}
			});

		ObjectValidationRule systemObjectValidationRule =
			(ObjectValidationRule)ArrayUtil.getValue(
				randomModifiableSystemObjectDefinition.
					getObjectValidationRules(),
				1);

		systemObjectValidationRule.setObjectValidationRuleSettings(
			new ObjectValidationRuleSetting[] {
				new ObjectValidationRuleSetting() {
					{
						name =
							ObjectValidationRuleSettingConstants.
								NAME_ALLOW_ACTIVE_STATUS_UPDATE;
						value = "true";
					}
				}
			});

		postObjectDefinition = testPostObjectDefinition_addObjectDefinition(
			randomModifiableSystemObjectDefinition);

		assertEquals(
			postObjectDefinition, randomModifiableSystemObjectDefinition);
		assertValid(postObjectDefinition);

		ListTypeDefinition serviceBuilderlistTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					randomListTypeDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		Assert.assertNotNull(serviceBuilderlistTypeDefinition);
		Assert.assertTrue(serviceBuilderlistTypeDefinition.isSystem());

		// Object action

		ObjectAction objectAction = new ObjectAction();

		objectAction.setExternalReferenceCode(RandomTestUtil.randomString());
		objectAction.setActive(true);
		objectAction.setConditionExpression(StringPool.BLANK);
		objectAction.setDescription(RandomTestUtil.randomString());
		objectAction.setErrorMessage(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));
		objectAction.setLabel(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));
		objectAction.setName("a" + RandomTestUtil.randomString());
		objectAction.setObjectActionExecutorKey(
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY);
		objectAction.setObjectActionTriggerKey(
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD);
		objectAction.setParameters(
			HashMapBuilder.put(
				"objectDefinitionExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", RandomTestUtil.randomString()
					).put(
						"value", RandomTestUtil.randomString()
					)
				).toString()
			).build());

		randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setObjectActions(
			new ObjectAction[] {objectAction});

		postObjectDefinition = testPostObjectDefinition_addObjectDefinition(
			randomObjectDefinition);

		assertEquals(postObjectDefinition, randomObjectDefinition);
		assertValid(postObjectDefinition);

		// Object relationship

		randomObjectDefinition = randomObjectDefinition();

		ObjectRelationship objectRelationship = new ObjectRelationship();

		objectRelationship.setDeletionType(
			ObjectRelationship.DeletionType.CASCADE);
		objectRelationship.setExternalReferenceCode(
			RandomTestUtil.randomString());
		objectRelationship.setName("a" + RandomTestUtil.randomString());
		objectRelationship.setObjectDefinitionExternalReferenceCode1(
			randomObjectDefinition.getExternalReferenceCode());
		objectRelationship.setObjectDefinitionExternalReferenceCode2(
			randomObjectDefinition.getExternalReferenceCode());
		objectRelationship.setObjectDefinitionId1(RandomTestUtil.randomLong());
		objectRelationship.setObjectDefinitionId2(RandomTestUtil.randomLong());
		objectRelationship.setType(ObjectRelationship.Type.ONE_TO_MANY);

		randomObjectDefinition.setObjectRelationships(
			new ObjectRelationship[] {objectRelationship});

		postObjectDefinition = testPostObjectDefinition_addObjectDefinition(
			randomObjectDefinition);

		assertEquals(postObjectDefinition, randomObjectDefinition);
		assertValid(postObjectDefinition);

		randomObjectDefinition = randomObjectDefinition();

		ObjectField relationshipObjectField = new ObjectField();

		relationshipObjectField.setBusinessType(
			ObjectField.BusinessType.RELATIONSHIP);
		relationshipObjectField.setLabel(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));
		relationshipObjectField.setLocalized(false);
		relationshipObjectField.setName("r_" + RandomTestUtil.randomString());

		randomObjectDefinition.setObjectFields(
			ArrayUtil.append(
				randomObjectDefinition.getObjectFields(),
				relationshipObjectField));

		postObjectDefinition = testPostObjectDefinition_addObjectDefinition(
			randomObjectDefinition);

		assertEquals(postObjectDefinition, randomObjectDefinition);
		assertValid(postObjectDefinition);

		// Status

		randomObjectDefinition = randomObjectDefinition();

		Status status = new Status() {
			{
				code = WorkflowConstants.STATUS_APPROVED;
				label = WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_APPROVED);
				label_i18n = _language.get(
					LanguageResources.getResourceBundle(
						LocaleUtil.getDefault()),
					WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_APPROVED));
			}
		};

		randomObjectDefinition.setStatus(status);

		postObjectDefinition = testPostObjectDefinition_addObjectDefinition(
			randomObjectDefinition);

		assertEquals(postObjectDefinition, randomObjectDefinition);
		assertValid(postObjectDefinition);
	}

	@FeatureFlags("LPD-32050")
	@Override
	@Test
	public void testPutObjectDefinition() throws Exception {
		super.testPutObjectDefinition();

		// Account entry restricted

		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setSystem(false);

		ObjectDefinition postObjectDefinition =
			objectDefinitionResource.postObjectDefinition(
				randomObjectDefinition);

		com.liferay.object.model.ObjectDefinition
			serviceBuilderAccountEntryObjectDefinition =
				_objectDefinitionLocalService.fetchSystemObjectDefinition(
					TestPropsValues.getCompanyId(),
					AccountEntry.class.getSimpleName());

		_objectDefinitionLocalService.enableAccountEntryRestricted(
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				serviceBuilderAccountEntryObjectDefinition.
					getObjectDefinitionId(),
				postObjectDefinition.getId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"a" + RandomTestUtil.randomString(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		postObjectDefinition = objectDefinitionResource.getObjectDefinition(
			postObjectDefinition.getId());

		Assert.assertTrue(postObjectDefinition.getAccountEntryRestricted());

		String accountEntryRestrictedObjectFieldName =
			postObjectDefinition.getAccountEntryRestrictedObjectFieldName();

		ObjectDefinition accountEntryObjectDefinition =
			objectDefinitionResource.getObjectDefinition(
				serviceBuilderAccountEntryObjectDefinition.
					getObjectDefinitionId());

		accountEntryObjectDefinition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectDefinitionResource.putObjectDefinition(
			accountEntryObjectDefinition.getId(), accountEntryObjectDefinition);

		postObjectDefinition = objectDefinitionResource.getObjectDefinition(
			postObjectDefinition.getId());

		Assert.assertTrue(postObjectDefinition.getAccountEntryRestricted());
		Assert.assertEquals(
			accountEntryRestrictedObjectFieldName,
			postObjectDefinition.getAccountEntryRestrictedObjectFieldName());

		// Account entry update with null object definition ID 2

		accountEntryObjectDefinition.setExternalReferenceCode(
			RandomTestUtil.randomString());
		accountEntryObjectDefinition.setTitleObjectFieldName("type");

		ObjectRelationship objectRelationship =
			accountEntryObjectDefinition.getObjectRelationships()[0];

		objectRelationship.setObjectDefinitionId2((Long)null);

		ObjectDefinition putAccountEntryObjectDefinition =
			objectDefinitionResource.putObjectDefinition(
				accountEntryObjectDefinition.getId(),
				accountEntryObjectDefinition);

		Assert.assertEquals(
			accountEntryObjectDefinition.getExternalReferenceCode(),
			putAccountEntryObjectDefinition.getExternalReferenceCode());
		Assert.assertEquals(
			accountEntryObjectDefinition.getTitleObjectFieldName(),
			putAccountEntryObjectDefinition.getTitleObjectFieldName());

		objectRelationship =
			putAccountEntryObjectDefinition.getObjectRelationships()[0];

		Assert.assertNotNull(objectRelationship.getObjectDefinitionId2());

		_objectDefinitionLocalService.deleteObjectDefinition(
			postObjectDefinition.getId());

		// Default language ID

		postObjectDefinition = objectDefinitionResource.postObjectDefinition(
			randomObjectDefinition());

		String objectDefinitionDefaultLanguageId = "pt_BR";

		String siteDefaultLanguageId = LanguageUtil.getLanguageId(
			LocaleUtil.getSiteDefault());

		Assert.assertNotEquals(
			objectDefinitionDefaultLanguageId, siteDefaultLanguageId);

		postObjectDefinition.setDefaultLanguageId(
			objectDefinitionDefaultLanguageId);
		postObjectDefinition.setLabel(
			MapUtil.fromArray(
				objectDefinitionDefaultLanguageId,
				RandomTestUtil.randomString()));

		objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		postObjectDefinition = objectDefinitionResource.getObjectDefinition(
			postObjectDefinition.getId());

		Map<String, String> labelMap = postObjectDefinition.getLabel();

		Assert.assertEquals(
			labelMap.get(siteDefaultLanguageId),
			labelMap.get(objectDefinitionDefaultLanguageId));

		// Draft custom object definition

		postObjectDefinition = objectDefinitionResource.postObjectDefinition(
			randomObjectDefinition());

		Assert.assertEquals(
			postObjectDefinition.getStatus(),
			new Status() {
				{
					code = WorkflowConstants.STATUS_DRAFT;
					label = WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_DRAFT);
					label_i18n = _language.get(
						LanguageResources.getResourceBundle(
							LocaleUtil.getDefault()),
						WorkflowConstants.getStatusLabel(
							WorkflowConstants.STATUS_DRAFT));
				}
			});

		postObjectDefinition.setStatus(
			new Status() {
				{
					code = WorkflowConstants.STATUS_APPROVED;
				}
			});

		ObjectDefinition randomPersistedPublishedObjectDefinition =
			objectDefinitionResource.putObjectDefinition(
				postObjectDefinition.getId(), postObjectDefinition);

		Assert.assertEquals(
			randomPersistedPublishedObjectDefinition.getStatus(),
			new Status() {
				{
					code = WorkflowConstants.STATUS_APPROVED;
					label = WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_APPROVED);
					label_i18n = _language.get(
						LanguageResources.getResourceBundle(
							LocaleUtil.getDefault()),
						WorkflowConstants.getStatusLabel(
							WorkflowConstants.STATUS_APPROVED));
				}
			});

		// Enable localization

		randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						label = Collections.singletonMap("en_US", "Column");
						localized = true;
						name = StringUtil.randomId();
					}
				}
			});
		randomObjectDefinition.setStatus(
			() -> new Status() {
				{
					code = WorkflowConstants.STATUS_APPROVED;
					label = WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_APPROVED);
					label_i18n = _language.get(
						LanguageResources.getResourceBundle(
							LocaleUtil.getDefault()),
						WorkflowConstants.getStatusLabel(
							WorkflowConstants.STATUS_APPROVED));
				}
			});

		postObjectDefinition = testPostObjectDefinition_addObjectDefinition(
			randomObjectDefinition);

		Assert.assertTrue(postObjectDefinition.getEnableLocalization());

		ObjectField[] localizedObjectFields = ArrayUtil.filter(
			postObjectDefinition.getObjectFields(), ObjectField::getLocalized);

		Assert.assertEquals(
			Arrays.toString(localizedObjectFields), 1,
			localizedObjectFields.length);

		postObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						label = Collections.singletonMap("en_US", "Column");
						localized = false;
						name = StringUtil.randomId();
					}
				}
			});

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		Assert.assertTrue(postObjectDefinition.getEnableLocalization());

		localizedObjectFields = ArrayUtil.filter(
			postObjectDefinition.getObjectFields(), ObjectField::getLocalized);

		Assert.assertEquals(
			Arrays.toString(localizedObjectFields), 0,
			localizedObjectFields.length);

		postObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						label = Collections.singletonMap("en_US", "Column");
						localized = true;
						name = StringUtil.randomId();
					}
				}
			});

		localizedObjectFields = ArrayUtil.filter(
			postObjectDefinition.getObjectFields(), ObjectField::getLocalized);

		Assert.assertEquals(
			Arrays.toString(localizedObjectFields), 1,
			localizedObjectFields.length);

		// Modifiable system object definition

		ObjectDefinition randomModifiableSystemObjectDefinition =
			_addObjectDefinition(_randomModifiableSystemObjectDefinition());

		ObjectValidationRule customObjectValidationRule =
			(ObjectValidationRule)ArrayUtil.getValue(
				randomModifiableSystemObjectDefinition.
					getObjectValidationRules(),
				0);
		ObjectValidationRule systemObjectValidationRule =
			(ObjectValidationRule)ArrayUtil.getValue(
				randomModifiableSystemObjectDefinition.
					getObjectValidationRules(),
				1);

		randomModifiableSystemObjectDefinition.setEnableObjectEntryDraft(
			(Boolean)null);
		randomModifiableSystemObjectDefinition.
			setObjectFolderExternalReferenceCode(StringPool.BLANK);

		ObjectValidationRule updatedCustomObjectValidationRule =
			new ObjectValidationRule() {
				{
					active = false;
					engine = ObjectValidationRuleConstants.ENGINE_TYPE_DDM;
					errorLabel = Collections.singletonMap(
						"en_US", RandomTestUtil.randomString());
					externalReferenceCode =
						customObjectValidationRule.getExternalReferenceCode();
					name = Collections.singletonMap(
						"en_US", RandomTestUtil.randomString());
					objectDefinitionExternalReferenceCode =
						randomModifiableSystemObjectDefinition.
							getExternalReferenceCode();
					objectValidationRuleSettings =
						new ObjectValidationRuleSetting[] {
							new ObjectValidationRuleSetting() {
								{
									name =
										ObjectValidationRuleSettingConstants.
											NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE;
									value = "customObjectFieldERC";
								}
							}
						};
					outputType = OutputType.create("partialValidation");
					script = "isEmailAddress(customObjectField)";
					system = false;
				}
			};
		ObjectValidationRule updatedSystemObjectValidationRule =
			new ObjectValidationRule() {
				{
					active = false;
					engine = ObjectValidationRuleConstants.ENGINE_TYPE_DDM;
					errorLabel = Collections.singletonMap(
						"en_US", RandomTestUtil.randomString());
					externalReferenceCode =
						systemObjectValidationRule.getExternalReferenceCode();
					name = Collections.singletonMap(
						"en_US", RandomTestUtil.randomString());
					objectDefinitionExternalReferenceCode =
						randomModifiableSystemObjectDefinition.
							getExternalReferenceCode();
					objectValidationRuleSettings =
						new ObjectValidationRuleSetting[] {
							new ObjectValidationRuleSetting() {
								{
									name =
										ObjectValidationRuleSettingConstants.
											NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE;
									value = "customObjectFieldERC";
								}
							},
							new ObjectValidationRuleSetting() {
								{
									name =
										ObjectValidationRuleSettingConstants.
											NAME_ALLOW_ACTIVE_STATUS_UPDATE;
									value = "true";
								}
							}
						};
					outputType = OutputType.create("partialValidation");
					script = "isEmailAddress(systemObjectField)";
					system = true;
				}
			};

		randomModifiableSystemObjectDefinition.setObjectValidationRules(
			new ObjectValidationRule[] {
				updatedCustomObjectValidationRule,
				updatedSystemObjectValidationRule
			});

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			objectDefinitionResource.putObjectDefinition(
				randomModifiableSystemObjectDefinition.getId(),
				randomModifiableSystemObjectDefinition);
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}

		ObjectDefinition getObjectDefinition =
			objectDefinitionResource.getObjectDefinition(
				randomModifiableSystemObjectDefinition.getId());

		_assertObjectValidationRule(
			null, "customObjectFieldERC", updatedCustomObjectValidationRule,
			(ObjectValidationRule)ArrayUtil.getValue(
				getObjectDefinition.getObjectValidationRules(), 0));
		_assertObjectValidationRule(
			null, null, systemObjectValidationRule,
			(ObjectValidationRule)ArrayUtil.getValue(
				getObjectDefinition.getObjectValidationRules(), 1));

		randomModifiableSystemObjectDefinition.setObjectValidationRules(
			new ObjectValidationRule[] {updatedSystemObjectValidationRule});

		objectDefinitionResource.putObjectDefinition(
			randomModifiableSystemObjectDefinition.getId(),
			randomModifiableSystemObjectDefinition);

		getObjectDefinition = objectDefinitionResource.getObjectDefinition(
			randomModifiableSystemObjectDefinition.getId());

		_assertObjectValidationRule(
			null, "customObjectFieldERC", updatedCustomObjectValidationRule,
			(ObjectValidationRule)ArrayUtil.getValue(
				getObjectDefinition.getObjectValidationRules(), 0));
		_assertObjectValidationRule(
			"true", "customObjectFieldERC", updatedSystemObjectValidationRule,
			(ObjectValidationRule)ArrayUtil.getValue(
				getObjectDefinition.getObjectValidationRules(), 1));

		_objectDefinitionLocalService.deleteObjectDefinition(
			randomModifiableSystemObjectDefinition.getId());

		// Object definition settings

		randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setScope(ObjectDefinitionConstants.SCOPE_DEPOT);

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());

		Group group1 = depotEntry1.getGroup();

		randomObjectDefinition.setObjectDefinitionSettings(
			new ObjectDefinitionSetting[] {
				new ObjectDefinitionSetting() {
					{
						setName(
							ObjectDefinitionSettingConstants.
								NAME_ACCEPTED_GROUP_IDS);
						setValue(group1.getGroupId());
					}
				}
			});

		postObjectDefinition = objectDefinitionResource.postObjectDefinition(
			randomObjectDefinition);

		Assert.assertArrayEquals(
			new ObjectDefinitionSetting[] {
				new ObjectDefinitionSetting() {
					{
						setName(
							ObjectDefinitionSettingConstants.
								NAME_ACCEPTED_GROUP_EXTERNAL_REFERENCE_CODES);
						setValue(group1.getExternalReferenceCode());
					}
				}
			},
			postObjectDefinition.getObjectDefinitionSettings());

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());

		Group group2 = depotEntry2.getGroup();

		postObjectDefinition.setObjectDefinitionSettings(
			new ObjectDefinitionSetting[] {
				new ObjectDefinitionSetting() {
					{
						setName(
							ObjectDefinitionSettingConstants.
								NAME_ACCEPTED_GROUP_IDS);
						setValue(
							StringBundler.concat(
								group1.getGroupId(), StringPool.COMMA,
								group2.getGroupId()));
					}
				}
			});

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		Assert.assertArrayEquals(
			new ObjectDefinitionSetting[] {
				new ObjectDefinitionSetting() {
					{
						setName(
							ObjectDefinitionSettingConstants.
								NAME_ACCEPTED_GROUP_EXTERNAL_REFERENCE_CODES);
						setValue(
							StringBundler.concat(
								group1.getExternalReferenceCode(),
								StringPool.COMMA,
								group2.getExternalReferenceCode()));
					}
				}
			},
			postObjectDefinition.getObjectDefinitionSettings());

		ObjectDefinitionSetting[] expectedObjectDefinitionSettings = {
			new ObjectDefinitionSetting() {
				{
					setName(
						ObjectDefinitionSettingConstants.
							NAME_ACCEPTED_GROUP_EXTERNAL_REFERENCE_CODES);
					setValue(String.valueOf(group2.getExternalReferenceCode()));
				}
			}
		};

		postObjectDefinition.setObjectDefinitionSettings(
			expectedObjectDefinitionSettings);

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		Assert.assertArrayEquals(
			expectedObjectDefinitionSettings,
			postObjectDefinition.getObjectDefinitionSettings());

		// Storage type

		postObjectDefinition = testPutObjectDefinition_addObjectDefinition();

		randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setStorageType(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);

		try {
			objectDefinitionResource.putObjectDefinition(
				postObjectDefinition.getId(), randomObjectDefinition);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}

		_objectDefinitionLocalService.deleteObjectDefinition(
			postObjectDefinition.getId());
	}

	@Override
	@Test
	public void testPutObjectDefinitionByExternalReferenceCode()
		throws Exception {

		super.testPutObjectDefinitionByExternalReferenceCode();

		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setExternalReferenceCode(
			"TESTOBJECTDEFINITION2");
		randomObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.RELATIONSHIP;
						DBType = ObjectField.DBType.LONG;
						indexed = true;
						label = Collections.singletonMap(
							"en_US", RandomTestUtil.randomString());
						name = "relationshipObjectFieldName";
						objectDefinitionExternalReferenceCode1 =
							"TESTOBJECTDEFINITION1";
						objectRelationshipExternalReferenceCode =
							"TESTOBJECTRELATIONSHIP";
					}
				}
			});

		ObjectLayoutRow[] finalObjectLayoutRows = {
			new ObjectLayoutRow() {
				{
					objectLayoutColumns = new ObjectLayoutColumn[] {
						new ObjectLayoutColumn() {
							{
								objectFieldName = "relationshipObjectFieldName";
								priority = 0;
								size = 6;
							}
						}
					};
					priority = 0;
				}
			}
		};

		randomObjectDefinition.setObjectLayouts(
			new ObjectLayout[] {
				new ObjectLayout() {
					{
						defaultObjectLayout = true;
						objectLayoutTabs = new ObjectLayoutTab[] {
							new ObjectLayoutTab() {
								{
									objectLayoutBoxes = new ObjectLayoutBox[] {
										new ObjectLayoutBox() {
											{
												collapsable = true;
												objectLayoutRows =
													finalObjectLayoutRows;
												priority = 0;
												type = Type.REGULAR;
											}
										}
									};
									priority = 0;
								}
							}
						};
					}
				}
			});

		ObjectDefinition putObjectDefinition =
			objectDefinitionResource.putObjectDefinitionByExternalReferenceCode(
				randomObjectDefinition.getExternalReferenceCode(),
				randomObjectDefinition);

		ObjectField[] objectFields = ArrayUtil.filter(
			putObjectDefinition.getObjectFields(),
			objectField -> !objectField.getSystem());

		Assert.assertEquals(
			Arrays.toString(objectFields), 1, objectFields.length);

		ObjectField objectField = objectFields[0];

		Assert.assertEquals(
			"relationshipObjectFieldName", objectField.getName());
		Assert.assertEquals(
			"TESTOBJECTDEFINITION1",
			objectField.getObjectDefinitionExternalReferenceCode1());
		Assert.assertEquals(
			"TESTOBJECTRELATIONSHIP",
			objectField.getObjectRelationshipExternalReferenceCode());

		ObjectLayout[] objectLayouts = putObjectDefinition.getObjectLayouts();

		Assert.assertEquals(
			Arrays.toString(objectLayouts), 1, objectLayouts.length);

		Assert.assertNotNull(
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				"TESTOBJECTDEFINITION1"));

		randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setExternalReferenceCode(
			"TESTOBJECTDEFINITION1");
		randomObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.STRING;
						indexed = true;
						label = Collections.singletonMap(
							"en_US", RandomTestUtil.randomString());
						name = "titleObjectFieldName";
					}
				}
			});

		Map<String, String> objectRelationshipLabelMap =
			Collections.singletonMap("en_US", RandomTestUtil.randomString());

		randomObjectDefinition.setObjectRelationships(
			new ObjectRelationship[] {
				new ObjectRelationship() {
					{
						deletionType = ObjectRelationship.DeletionType.CASCADE;
						externalReferenceCode = "TESTOBJECTRELATIONSHIP";
						label = objectRelationshipLabelMap;
						name = RandomTestUtil.randomString();
						objectDefinitionExternalReferenceCode1 =
							"TESTOBJECTDEFINITION1";
						objectDefinitionExternalReferenceCode2 =
							"TESTOBJECTDEFINITION2";
					}
				}
			});

		randomObjectDefinition.setTitleObjectFieldName("titleObjectFieldName");

		putObjectDefinition =
			objectDefinitionResource.putObjectDefinitionByExternalReferenceCode(
				randomObjectDefinition.getExternalReferenceCode(),
				randomObjectDefinition);

		ObjectRelationship[] objectRelationships =
			putObjectDefinition.getObjectRelationships();

		Assert.assertEquals(
			Arrays.toString(objectRelationships), 1,
			objectRelationships.length);

		ObjectRelationship objectRelationship = objectRelationships[0];

		Assert.assertEquals(
			ObjectRelationship.DeletionType.CASCADE,
			objectRelationship.getDeletionType());
		Assert.assertEquals(
			"TESTOBJECTRELATIONSHIP",
			objectRelationship.getExternalReferenceCode());
		Assert.assertEquals(
			objectRelationshipLabelMap, objectRelationship.getLabel());
		Assert.assertEquals(
			"TESTOBJECTDEFINITION1",
			objectRelationship.getObjectDefinitionExternalReferenceCode1());
		Assert.assertEquals(
			"TESTOBJECTDEFINITION2",
			objectRelationship.getObjectDefinitionExternalReferenceCode2());

		Assert.assertEquals(
			"titleObjectFieldName",
			randomObjectDefinition.getTitleObjectFieldName());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "status"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"dateCreated", "dateModified", "label", "userId"};
	}

	@Override
	protected ObjectDefinition randomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition = super.randomObjectDefinition();

		objectDefinition.setAccountEntryRestricted(false);
		objectDefinition.setAccountEntryRestrictedObjectFieldName("");
		objectDefinition.setActive(false);
		objectDefinition.setClassName(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomString());
		objectDefinition.setEnableLocalization(true);
		objectDefinition.setLabel(
			Collections.singletonMap(
				"en_US", "O" + objectDefinition.getName()));
		objectDefinition.setModifiable(true);
		objectDefinition.setName("O" + objectDefinition.getName());
		objectDefinition.setObjectFolderExternalReferenceCode(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_DEFAULT);
		objectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						indexed = false;
						indexedAsKeyword = false;
						label = Collections.singletonMap("en_US", "Column");
						localized = !objectDefinition.getSystem();
						name = StringUtil.randomId();
						readOnly = ReadOnly.FALSE;
						required = false;
						system = false;
					}
				}
			});
		objectDefinition.setPluralLabel(
			Collections.singletonMap(
				"en_US", "O" + objectDefinition.getName()));
		objectDefinition.setRootObjectDefinitionExternalReferenceCode(
			StringPool.BLANK);
		objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_COMPANY);
		objectDefinition.setStatus(
			new Status() {
				{
					code = WorkflowConstants.STATUS_DRAFT;
					label = WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_DRAFT);
					label_i18n = _language.get(
						LanguageResources.getResourceBundle(
							LocaleUtil.getDefault()),
						WorkflowConstants.getStatusLabel(
							WorkflowConstants.STATUS_DRAFT));
				}
			});
		objectDefinition.setSystem(false);

		if (!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {
			objectDefinition.setStorageType(StringPool.BLANK);
		}

		return objectDefinition;
	}

	@Override
	protected ObjectDefinition testDeleteObjectDefinition_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	@Override
	protected ObjectDefinition testGetObjectDefinition_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	@Override
	protected ObjectDefinition
			testGetObjectDefinitionByExternalReferenceCode_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	@Override
	protected ObjectDefinition testGetObjectDefinitionsPage_addObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		return _addObjectDefinition(objectDefinition);
	}

	@Override
	protected void testGetObjectDefinitionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ObjectDefinition objectDefinition1 = randomObjectDefinition();

		objectDefinition1.setObjectFolderExternalReferenceCode(
			_objectFolder1.getExternalReferenceCode());

		objectDefinition1 = testGetObjectDefinitionsPage_addObjectDefinition(
			objectDefinition1);

		ObjectDefinition objectDefinition2 = randomObjectDefinition();

		objectDefinition2.setObjectFolderExternalReferenceCode(
			_objectFolder2.getExternalReferenceCode());

		testGetObjectDefinitionsPage_addObjectDefinition(objectDefinition2);

		for (EntityField entityField : entityFields) {
			if (StringUtil.equals(
					entityField.getName(),
					"rootObjectDefinitionExternalReferenceCode")) {

				continue;
			}

			_assertGetObjectDefinitionsPageWithFilter(
				Collections.singletonList(objectDefinition1),
				getFilterString(entityField, operator, objectDefinition1));

			_objectFolder1 = _objectFolderLocalService.updateObjectFolder(
				RandomTestUtil.randomString(),
				_objectFolder1.getObjectFolderId(),
				_objectFolder1.getLabelMap());

			objectDefinition1 = objectDefinitionResource.getObjectDefinition(
				objectDefinition1.getId());

			_assertGetObjectDefinitionsPageWithFilter(
				Collections.singletonList(objectDefinition1),
				getFilterString(entityField, operator, objectDefinition1));
		}
	}

	@Override
	protected ObjectDefinition testGraphQLObjectDefinition_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	@Override
	protected ObjectDefinition testPatchObjectDefinition_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	@Override
	protected ObjectDefinition testPostObjectDefinition_addObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		return _addObjectDefinition(objectDefinition);
	}

	@Override
	protected ObjectDefinition
			testPostObjectDefinitionPublish_addObjectDefinition(
				ObjectDefinition objectDefinition)
		throws Exception {

		return _addObjectDefinition(objectDefinition);
	}

	@Override
	protected ObjectDefinition testPutObjectDefinition_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	@Override
	protected ObjectDefinition
			testPutObjectDefinitionByExternalReferenceCode_addObjectDefinition()
		throws Exception {

		return _addObjectDefinition(randomObjectDefinition());
	}

	private ObjectDefinition _addObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		objectDefinition = objectDefinitionResource.postObjectDefinition(
			objectDefinition);

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private void _assertGetObjectDefinitionsPageWithFilter(
			List<ObjectDefinition> expectedObjectDefinitions,
			String filterString)
		throws Exception {

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, filterString, Pagination.of(1, 2), null);

		assertEquals(
			expectedObjectDefinitions, (List<ObjectDefinition>)page.getItems());
	}

	private void _assertObjectValidationRule(
			String expectedAllowActiveStatusUpdate,
			String expectedObjectFieldExternalReferenceCode,
			ObjectValidationRule expectedObjectValidationRule,
			ObjectValidationRule actualObjectValidationRule)
		throws Exception {

		Assert.assertEquals(
			expectedObjectValidationRule.getActive(),
			actualObjectValidationRule.getActive());
		Assert.assertEquals(
			expectedObjectValidationRule.getEngine(),
			actualObjectValidationRule.getEngine());
		Assert.assertEquals(
			expectedObjectValidationRule.getErrorLabel(),
			actualObjectValidationRule.getErrorLabel());
		Assert.assertEquals(
			expectedObjectValidationRule.getExternalReferenceCode(),
			expectedObjectValidationRule.getExternalReferenceCode());
		Assert.assertEquals(
			expectedObjectValidationRule.getName(),
			actualObjectValidationRule.getName());
		Assert.assertEquals(
			expectedObjectValidationRule.getOutputType(),
			actualObjectValidationRule.getOutputType());
		Assert.assertEquals(
			expectedObjectValidationRule.getScript(),
			actualObjectValidationRule.getScript());

		Map<String, Object> objectValidationRuleSettings = new HashMap<>();

		for (ObjectValidationRuleSetting objectValidationRuleSetting :
				actualObjectValidationRule.getObjectValidationRuleSettings()) {

			objectValidationRuleSettings.put(
				objectValidationRuleSetting.getName(),
				objectValidationRuleSetting.getValue());
		}

		Assert.assertEquals(
			expectedAllowActiveStatusUpdate,
			objectValidationRuleSettings.getOrDefault(
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE,
				null));

		if (StringUtil.equals(
				actualObjectValidationRule.getOutputTypeAsString(),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION)) {

			Assert.assertNull(
				objectValidationRuleSettings.get(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE));
		}
		else if (StringUtil.equals(
					actualObjectValidationRule.getOutputTypeAsString(),
					ObjectValidationRuleConstants.
						OUTPUT_TYPE_PARTIAL_VALIDATION)) {

			Assert.assertEquals(
				expectedObjectFieldExternalReferenceCode,
				objectValidationRuleSettings.get(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE));
		}
	}

	private ObjectDefinition _randomModifiableSystemObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition = randomObjectDefinition();

		objectDefinition.setActive(true);

		String randomObjectDefinitionExternalReferenceCode =
			"L_" + objectDefinition.getExternalReferenceCode();

		objectDefinition.setExternalReferenceCode(
			randomObjectDefinitionExternalReferenceCode);

		objectDefinition.setName("Test");
		objectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						externalReferenceCode = "customObjectFieldERC";
						indexed = false;
						indexedAsKeyword = false;
						label = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						localized = false;
						name = "customObjectField";
						readOnly = ReadOnly.FALSE;
						required = false;
						system = false;
					}
				},
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						externalReferenceCode = RandomTestUtil.randomString();
						indexed = false;
						indexedAsKeyword = false;
						label = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						localized = false;
						name = "systemObjectField";
						readOnly = ReadOnly.FALSE;
						required = false;
						system = true;
					}
				}
			});
		objectDefinition.setObjectValidationRules(
			new ObjectValidationRule[] {
				new ObjectValidationRule() {
					{
						active = true;
						engine = ObjectValidationRuleConstants.ENGINE_TYPE_DDM;
						errorLabel = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						externalReferenceCode = RandomTestUtil.randomString();
						name = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						objectDefinitionExternalReferenceCode =
							randomObjectDefinitionExternalReferenceCode;
						outputType = OutputType.create("fullValidation");
						script = "isEmailAddress(customObjectField)";
						system = false;
					}
				},
				new ObjectValidationRule() {
					{
						active = true;
						engine = ObjectValidationRuleConstants.ENGINE_TYPE_DDM;
						errorLabel = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						externalReferenceCode = RandomTestUtil.randomString();
						name = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						objectDefinitionExternalReferenceCode =
							randomObjectDefinitionExternalReferenceCode;
						outputType = OutputType.create("fullValidation");
						script = "isEmailAddress(systemObjectField)";
						system = true;
					}
				}
			});
		objectDefinition.setStatus(
			new Status() {
				{
					code = WorkflowConstants.STATUS_APPROVED;
					label = WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_APPROVED);
					label_i18n = _language.get(
						LanguageResources.getResourceBundle(
							LocaleUtil.getDefault()),
						WorkflowConstants.getStatusLabel(
							WorkflowConstants.STATUS_APPROVED));
				}
			});
		objectDefinition.setSystem(true);

		return objectDefinition;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionResourceTest.class);

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Language _language;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@DeleteAfterTestRun
	private ObjectFolder _objectFolder1;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolder2;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}