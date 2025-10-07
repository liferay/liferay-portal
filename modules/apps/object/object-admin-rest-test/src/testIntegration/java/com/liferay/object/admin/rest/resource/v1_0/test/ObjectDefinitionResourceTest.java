/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinitionSetting;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFieldSetting;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutBox;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutColumn;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutRow;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutTab;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRuleSetting;
import com.liferay.object.admin.rest.client.dto.v1_0.Status;
import com.liferay.object.admin.rest.client.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.problem.Problem;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectDefinitionSerDes;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlag("LPD-34594")
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

	@FeatureFlag("LPD-17564")
	@Override
	@Test
	public void testGetObjectDefinition() throws Exception {
		_testGetObjectDefinition();
		_testGetObjectDefinitionWithRootObjectDefinitionExternalReferenceCodes();
		_testGetObjectDefinitionWithWorkflowDefinitionLink();
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
			testGraphQLObjectDefinition_addObjectDefinition(
				randomObjectDefinition());
		ObjectDefinition objectDefinition2 =
			testGraphQLObjectDefinition_addObjectDefinition(
				randomObjectDefinition());

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

	@FeatureFlag("LPD-17564")
	@Override
	@Test
	@TestInfo("LPD-49994")
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
		Assert.assertEquals(
			postObjectDefinition.getClassName(),
			randomModifiableSystemObjectDefinition.getClassName());

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

		ObjectRelationship objectRelationship = _createObjectRelationship(
			randomObjectDefinition, randomObjectDefinition,
			ObjectRelationship.Type.ONE_TO_MANY);

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

		_testPostObjectDefinitionBatch();
		_testPostObjectDefinitionWithSystemAggregationObjectField();
		_testPostObjectDefinitionWithWorkflowDefinitionLinks();
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-17564"), @FeatureFlag(value = "LPD-32050")
		}
	)
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
						businessType = BusinessType.AUTO_INCREMENT;
						DBType = ObjectField.DBType.create("String");
						label = Collections.singletonMap(
							"en_US", RandomTestUtil.randomString());
						name = StringUtil.randomId();
						objectFieldSettings = new ObjectFieldSetting[] {
							new ObjectFieldSetting() {
								{
									name =
										ObjectFieldSettingConstants.
											NAME_INITIAL_VALUE;
									value = RandomTestUtil.randomInt();
								}
							}
						};
					}
				},
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						label = Collections.singletonMap(
							"en_US", RandomTestUtil.randomString());
						localized = true;
						name = StringUtil.randomId();
					}
				},
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.create("String");
						label = Collections.singletonMap(
							"en_US", RandomTestUtil.randomString());
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

		// Enable object entry subscription

		randomObjectDefinition = randomObjectDefinition();

		randomObjectDefinition.setEnableObjectEntrySubscription(true);

		postObjectDefinition = objectDefinitionResource.postObjectDefinition(
			randomObjectDefinition);

		ObjectAction[] objectActions = postObjectDefinition.getObjectActions();

		Assert.assertEquals(objectActions.toString(), 3, objectActions.length);

		ObjectAction objectAction1 = objectActions[0];

		Assert.assertTrue(objectAction1.getActive());

		ObjectAction objectAction2 = objectActions[1];

		Assert.assertTrue(objectAction2.getActive());

		ObjectAction objectAction3 = objectActions[2];

		Assert.assertTrue(objectAction3.getActive());

		postObjectDefinition.setEnableObjectEntrySubscription(false);

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		objectActions = postObjectDefinition.getObjectActions();

		objectAction1 = objectActions[0];

		Assert.assertFalse(objectAction1.getActive());

		objectAction2 = objectActions[1];

		Assert.assertFalse(objectAction2.getActive());

		objectAction3 = objectActions[2];

		Assert.assertFalse(objectAction3.getActive());

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
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

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
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

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

		// Workflow definition link with company scope

		WorkflowDefinition workflowDefinition1 =
			_workflowDefinitionManager.getWorkflowDefinition(
				WorkflowDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_SINGLE_APPROVER,
				TestPropsValues.getCompanyId());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode = StringPool.BLANK;
					workflowDefinitionName = workflowDefinition1.getName();
				}
			};

		WorkflowDefinitionLink[] workflowDefinitionLinks = {
			workflowDefinitionLink1
		};

		postObjectDefinition.setWorkflowDefinitionLinks(
			workflowDefinitionLinks);

		_assertWorkflowDefinitionLinks(
			objectDefinitionResource.putObjectDefinition(
				postObjectDefinition.getId(), postObjectDefinition),
			workflowDefinitionLinks);

		_objectDefinitionLocalService.deleteObjectDefinition(
			postObjectDefinition.getId());

		// Workflow definition link with site scope

		postObjectDefinition = randomObjectDefinition();

		postObjectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		postObjectDefinition = _addObjectDefinition(postObjectDefinition);

		String content = workflowDefinition1.getContentAsXML();

		WorkflowDefinition workflowDefinition2 =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), content.getBytes());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode = RandomTestUtil.randomString();
					workflowDefinitionName = workflowDefinition2.getName();
				}
			};

		workflowDefinitionLinks = new WorkflowDefinitionLink[] {
			workflowDefinitionLink1, workflowDefinitionLink2
		};

		postObjectDefinition.setWorkflowDefinitionLinks(
			workflowDefinitionLinks);

		try {
			objectDefinitionResource.putObjectDefinition(
				postObjectDefinition.getId(), postObjectDefinition);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"An object definition can only be linked to a workflow " +
					"definition with an existing group",
				problem.getTitle());
			Assert.assertEquals(
				"ObjectDefinitionScopeException", problem.getType());
		}

		Group group3 = GroupTestUtil.addGroup();

		workflowDefinitionLink2.setGroupExternalReferenceCode(
			group3.getExternalReferenceCode());

		Group group4 = GroupTestUtil.addGroup();

		WorkflowDefinition workflowDefinition3 =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), content.getBytes());

		WorkflowDefinitionLink workflowDefinitionLink3 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode =
						group4.getExternalReferenceCode();
					workflowDefinitionName = workflowDefinition3.getName();
				}
			};

		workflowDefinitionLinks = new WorkflowDefinitionLink[] {
			workflowDefinitionLink1, workflowDefinitionLink2,
			workflowDefinitionLink3
		};

		postObjectDefinition.setWorkflowDefinitionLinks(
			workflowDefinitionLinks);

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		_assertWorkflowDefinitionLinks(
			postObjectDefinition, workflowDefinitionLinks);

		workflowDefinitionLink1.setWorkflowDefinitionName(
			workflowDefinition2.getName());
		workflowDefinitionLink2.setWorkflowDefinitionName(
			workflowDefinition1.getName());

		workflowDefinitionLinks = new WorkflowDefinitionLink[] {
			workflowDefinitionLink1, workflowDefinitionLink2,
			workflowDefinitionLink3
		};

		postObjectDefinition.setWorkflowDefinitionLinks(
			workflowDefinitionLinks);

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		_assertWorkflowDefinitionLinks(
			postObjectDefinition, workflowDefinitionLinks);

		workflowDefinitionLinks = new WorkflowDefinitionLink[] {
			workflowDefinitionLink2
		};

		postObjectDefinition.setWorkflowDefinitionLinks(
			workflowDefinitionLinks);

		postObjectDefinition = objectDefinitionResource.putObjectDefinition(
			postObjectDefinition.getId(), postObjectDefinition);

		_assertWorkflowDefinitionLinks(
			postObjectDefinition, workflowDefinitionLinks);

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
						name = "r_relationshipName_c_objectDefinition1Id";
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
								objectFieldName =
									"r_relationshipName_c_objectDefinition1Id";
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
			"r_relationshipName_c_objectDefinition1Id", objectField.getName());
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
						name = "a" + RandomTestUtil.randomString();
						objectDefinitionExternalReferenceCode1 =
							"TESTOBJECTDEFINITION1";
						objectDefinitionExternalReferenceCode2 =
							"TESTOBJECTDEFINITION2";
						type = ObjectRelationship.Type.ONE_TO_MANY;
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

		_objectDefinitions.add(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition.getId()));

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

	private void _assertWorkflowDefinitionLinks(
		ObjectDefinition objectDefinition,
		WorkflowDefinitionLink[] workflowDefinitionLinks) {

		Assert.assertEquals(
			new HashSet<>(
				Arrays.asList(objectDefinition.getWorkflowDefinitionLinks())),
			new HashSet<>(Arrays.asList(workflowDefinitionLinks)));
	}

	private ObjectRelationship _createObjectRelationship(
		ObjectDefinition objectDefinition1, ObjectDefinition objectDefinition2,
		ObjectRelationship.Type type) {

		ObjectRelationship objectRelationship = new ObjectRelationship();

		objectRelationship.setDeletionType(
			ObjectRelationship.DeletionType.CASCADE);
		objectRelationship.setExternalReferenceCode(
			RandomTestUtil.randomString());
		objectRelationship.setName("a" + RandomTestUtil.randomString());
		objectRelationship.setObjectDefinitionExternalReferenceCode1(
			objectDefinition1.getExternalReferenceCode());
		objectRelationship.setObjectDefinitionExternalReferenceCode2(
			objectDefinition2.getExternalReferenceCode());
		objectRelationship.setObjectDefinitionId1(objectDefinition1.getId());
		objectRelationship.setObjectDefinitionId2(objectDefinition2.getId());
		objectRelationship.setType(type);

		return objectRelationship;
	}

	private ObjectDefinition _randomModifiableSystemObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition = randomObjectDefinition();

		objectDefinition.setActive(true);

		String randomObjectDefinitionExternalReferenceCode =
			"L_" + objectDefinition.getExternalReferenceCode();

		objectDefinition.setExternalReferenceCode(
			randomObjectDefinitionExternalReferenceCode);

		objectDefinition.setName("Test" + RandomTestUtil.randomString());
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

	private void _testGetObjectDefinition() throws Exception {
		super.testGetObjectDefinition();

		ObjectDefinition objectDefinition =
			objectDefinitionResource.postObjectDefinition(
				randomObjectDefinition());

		String objectDefinitionPluralName = StringUtil.lowerCaseFirstLetter(
			TextFormatter.formatPlural(objectDefinition.getName()));

		Assert.assertEquals(
			"/o/c/" + objectDefinitionPluralName,
			objectDefinition.getRestContextPath());
	}

	private void _testGetObjectDefinitionWithRootObjectDefinitionExternalReferenceCodes()
		throws Exception {

		ObjectDefinition objectDefinitionA =
			objectDefinitionResource.postObjectDefinition(
				randomObjectDefinition());
		ObjectDefinition objectDefinitionAA =
			objectDefinitionResource.postObjectDefinition(
				randomObjectDefinition());

		TreeTestUtil.bind(
			objectDefinitionA.getId(), objectDefinitionAA.getId(),
			_objectRelationshipLocalService);

		ObjectDefinition objectDefinitionB =
			objectDefinitionResource.postObjectDefinition(
				randomObjectDefinition());

		TreeTestUtil.bind(
			objectDefinitionB.getId(), objectDefinitionAA.getId(),
			_objectRelationshipLocalService);

		objectDefinitionAA = objectDefinitionResource.getObjectDefinition(
			objectDefinitionAA.getId());

		Assert.assertArrayEquals(
			new ObjectDefinitionSetting[] {
				new ObjectDefinitionSetting() {
					{
						name =
							ObjectDefinitionSettingConstants.
								NAME_ROOT_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODES;
						value =
							objectDefinitionA.getExternalReferenceCode() + "," +
								objectDefinitionB.getExternalReferenceCode();
					}
				}
			},
			objectDefinitionAA.getObjectDefinitionSettings());

		TreeTestUtil.unbind(
			objectDefinitionA.getId(), _objectRelationshipLocalService);
		TreeTestUtil.unbind(
			objectDefinitionB.getId(), _objectRelationshipLocalService);

		objectDefinitionAA = objectDefinitionResource.getObjectDefinition(
			objectDefinitionAA.getId());

		Assert.assertTrue(
			ArrayUtil.isEmpty(
				objectDefinitionAA.getObjectDefinitionSettings()));
	}

	@TestInfo("LPD-63538")
	private void _testGetObjectDefinitionWithWorkflowDefinitionLink()
		throws Exception {

		// Company scope

		ObjectDefinition objectDefinition = _addObjectDefinition(
			randomObjectDefinition());

		WorkflowDefinition workflowDefinition1 =
			_workflowDefinitionManager.getWorkflowDefinition(
				WorkflowDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_SINGLE_APPROVER,
				TestPropsValues.getCompanyId());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode = StringPool.BLANK;
					workflowDefinitionName = workflowDefinition1.getName();
				}
			};

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			0, objectDefinition.getClassName(), objectDefinition.getId(), 0,
			workflowDefinitionLink1.getWorkflowDefinitionName(), 0);

		objectDefinition = objectDefinitionResource.getObjectDefinition(
			objectDefinition.getId());

		Assert.assertEquals(
			new WorkflowDefinitionLink[] {workflowDefinitionLink1},
			objectDefinition.getWorkflowDefinitionLinks());

		// Site scope

		Group group1 = GroupTestUtil.addGroup();

		objectDefinition = randomObjectDefinition();

		objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		objectDefinition = _addObjectDefinition(objectDefinition);

		workflowDefinitionLink1.setGroupExternalReferenceCode(
			group1.getExternalReferenceCode());

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			group1.getGroupId(), objectDefinition.getClassName(),
			objectDefinition.getId(), 0,
			workflowDefinitionLink1.getWorkflowDefinitionName(), 0);

		Group group2 = GroupTestUtil.addGroup();

		String content = workflowDefinition1.getContentAsXML();

		WorkflowDefinition workflowDefinition2 =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), content.getBytes());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode =
						group2.getExternalReferenceCode();
					workflowDefinitionName = workflowDefinition2.getName();
				}
			};

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			group2.getGroupId(), objectDefinition.getClassName(),
			objectDefinition.getId(), 0,
			workflowDefinitionLink2.getWorkflowDefinitionName(), 0);

		objectDefinition = objectDefinitionResource.getObjectDefinition(
			objectDefinition.getId());

		Assert.assertEquals(
			new HashSet<>(
				Arrays.asList(
					workflowDefinitionLink1, workflowDefinitionLink2)),
			new HashSet<>(
				Arrays.asList(objectDefinition.getWorkflowDefinitionLinks())));
	}

	private void _testPostObjectDefinitionBatch() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition1 = randomObjectDefinition();

		objectDefinition1.setExternalReferenceCode(externalReferenceCode1);
		objectDefinition1.setObjectFolderExternalReferenceCode(
			_objectFolder1.getExternalReferenceCode());
		objectDefinition1.setObjectRelationships(
			new ObjectRelationship[] {
				new ObjectRelationship() {
					{
						deletionType = ObjectRelationship.DeletionType.CASCADE;
						externalReferenceCode = RandomTestUtil.randomString();
						name = "a" + RandomTestUtil.randomString();
						objectDefinitionExternalReferenceCode1 =
							externalReferenceCode1;
						objectDefinitionExternalReferenceCode2 =
							externalReferenceCode2;
						type = ObjectRelationship.Type.ONE_TO_MANY;
					}
				}
			});
		objectDefinition1.setStatus(
			new Status() {
				{
					code = WorkflowConstants.STATUS_APPROVED;
				}
			});

		ObjectDefinition objectDefinition2 = randomObjectDefinition();

		objectDefinition2.setExternalReferenceCode(externalReferenceCode2);
		objectDefinition2.setObjectFolderExternalReferenceCode(
			_objectFolder1.getExternalReferenceCode());
		objectDefinition2.setStatus(
			new Status() {
				{
					code = WorkflowConstants.STATUS_APPROVED;
				}
			});

		User user = TestPropsValues.getUser();

		ObjectDefinitionResource batchObjectDefinitionResource =
			ObjectDefinitionResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).parameter(
				"createStrategy", "UPSERT"
			).locale(
				LocaleUtil.getDefault()
			).build();

		JSONObject jsonObject = _waitForFinish(
			"COMPLETED", true,
			JSONFactoryUtil.createJSONObject(
				batchObjectDefinitionResource.
					postObjectDefinitionBatchHttpResponse(
						null,
						JSONUtil.putAll(
							JSONFactoryUtil.createJSONObject(
								String.valueOf(objectDefinition1)),
							JSONFactoryUtil.createJSONObject(
								String.valueOf(objectDefinition2)))
					).getContent()));

		Assert.assertEquals(2, jsonObject.getLong("processedItemsCount"));
		Assert.assertEquals(2, jsonObject.getLong("totalItemsCount"));

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null,
				"objectFolderExternalReferenceCode eq '" +
					_objectFolder1.getExternalReferenceCode() + "'",
				null, null);

		Assert.assertEquals(2, page.getTotalCount());
	}

	private void _testPostObjectDefinitionWithSystemAggregationObjectField()
		throws Exception {

		ObjectDefinition randomModifiableSystemObjectDefinition =
			_randomModifiableSystemObjectDefinition();

		ObjectDefinition objectDefinition = randomObjectDefinition();

		ObjectRelationship objectRelationship = _createObjectRelationship(
			randomModifiableSystemObjectDefinition, objectDefinition,
			ObjectRelationship.Type.MANY_TO_MANY);

		randomModifiableSystemObjectDefinition.setObjectRelationships(
			new ObjectRelationship[] {objectRelationship});

		String aggregationObjectFieldName = "c" + RandomTestUtil.randomString();

		randomModifiableSystemObjectDefinition.setObjectFields(
			new ObjectField[] {
				new ObjectField() {
					{
						businessType = BusinessType.AGGREGATION;
						DBType = ObjectField.DBType.create("String");
						defaultValue = null;
						externalReferenceCode = RandomTestUtil.randomString();
						indexed = false;
						indexedAsKeyword = false;
						indexedLanguageId = StringPool.BLANK;
						label = Collections.singletonMap(
							"en-US", RandomTestUtil.randomString());
						localized = false;
						name = aggregationObjectFieldName;
						objectFieldSettings = new ObjectFieldSetting[] {
							new ObjectFieldSetting() {
								{
									name =
										ObjectFieldSettingConstants.
											NAME_FUNCTION;
									value =
										ObjectFieldSettingConstants.VALUE_COUNT;
								}
							},
							new ObjectFieldSetting() {
								{
									name =
										ObjectFieldSettingConstants.
											NAME_OBJECT_RELATIONSHIP_NAME;
									value = objectRelationship.getName();
								}
							}
						};
						readOnly = ReadOnly.TRUE;
						readOnlyConditionExpression = StringPool.BLANK;
						required = false;
						state = false;
						system = true;
						unique = false;
					}
				},
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

		_addObjectDefinition(randomModifiableSystemObjectDefinition);

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition1 =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						randomModifiableSystemObjectDefinition.
							getExternalReferenceCode(),
						TestPropsValues.getCompanyId());

		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				serviceBuilderObjectDefinition1.getObjectDefinitionId(),
				aggregationObjectFieldName));
	}

	@TestInfo("LPD-63539")
	private void _testPostObjectDefinitionWithWorkflowDefinitionLinks()
		throws Exception {

		// Company scope

		ObjectDefinition objectDefinition = randomObjectDefinition();

		WorkflowDefinition workflowDefinition1 =
			_workflowDefinitionManager.getWorkflowDefinition(
				WorkflowDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_SINGLE_APPROVER,
				TestPropsValues.getCompanyId());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode = StringPool.BLANK;
					workflowDefinitionName = workflowDefinition1.getName();
				}
			};

		WorkflowDefinitionLink[] workflowDefinitionLinks = {
			workflowDefinitionLink1
		};

		objectDefinition.setWorkflowDefinitionLinks(workflowDefinitionLinks);

		_assertWorkflowDefinitionLinks(
			_addObjectDefinition(objectDefinition), workflowDefinitionLinks);

		// Site scope

		objectDefinition = randomObjectDefinition();

		objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		String content = workflowDefinition1.getContentAsXML();

		WorkflowDefinition workflowDefinition2 =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), content.getBytes());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode = RandomTestUtil.randomString();
					workflowDefinitionName = workflowDefinition2.getName();
				}
			};

		workflowDefinitionLinks = new WorkflowDefinitionLink[] {
			workflowDefinitionLink1, workflowDefinitionLink2
		};

		objectDefinition.setWorkflowDefinitionLinks(workflowDefinitionLinks);

		try {
			_addObjectDefinition(objectDefinition);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"An object definition can only be linked to a workflow " +
					"definition with an existing group",
				problem.getTitle());
			Assert.assertEquals(
				"ObjectDefinitionScopeException", problem.getType());
		}

		Group group1 = GroupTestUtil.addGroup();

		workflowDefinitionLink2.setGroupExternalReferenceCode(
			group1.getExternalReferenceCode());

		Group group2 = GroupTestUtil.addGroup();

		WorkflowDefinition workflowDefinition3 =
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), content.getBytes());

		WorkflowDefinitionLink workflowDefinitionLink3 =
			new WorkflowDefinitionLink() {
				{
					groupExternalReferenceCode =
						group2.getExternalReferenceCode();
					workflowDefinitionName = workflowDefinition3.getName();
				}
			};

		workflowDefinitionLinks = new WorkflowDefinitionLink[] {
			workflowDefinitionLink1, workflowDefinitionLink2,
			workflowDefinitionLink3
		};

		objectDefinition.setWorkflowDefinitionLinks(workflowDefinitionLinks);

		_assertWorkflowDefinitionLinks(
			_addObjectDefinition(objectDefinition), workflowDefinitionLinks);
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<com.liferay.object.model.ObjectDefinition>
		_objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolder1;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolder2;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}