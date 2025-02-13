/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.graphql.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalServiceUtil;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionGraphQLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectFieldResource.setContextAcceptLanguage(
			new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return Arrays.asList(LocaleUtil.getDefault());
				}

				@Override
				public String getPreferredLanguageId() {
					return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.getDefault();
				}

			});
		_objectFieldResource.setContextUser(TestPropsValues.getUser());
	}

	@Before
	public void setUp() throws Exception {
		_draftAllowedObjectDefinition = _addObjectDefinition(true);

		_draftAllowedObjectDefinitionName =
			_draftAllowedObjectDefinition.getShortName();

		_draftAllowedObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_draftAllowedObjectDefinition.getObjectDefinitionId());

		ListTypeDefinition listTypeDefinition =
			ListTypeDefinitionLocalServiceUtil.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(_LIST_FIELD_NAME), false,
				Collections.emptyList());

		_addListTypeEntry(listTypeDefinition, StringUtil.randomId());
		_addListTypeEntry(listTypeDefinition, StringUtil.randomId());

		_addListTypeEntry(listTypeDefinition, _LIST_FIELD_VALUE_KEY);

		_parentObjectDefinition = _addObjectDefinition(false);

		ObjectFieldUtil.addCustomObjectField(
			new PicklistObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).listTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).indexedAsKeyword(
				true
			).name(
				_LIST_FIELD_NAME
			).objectDefinitionId(
				_parentObjectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		_parentObjectDefinitionName = _parentObjectDefinition.getShortName();

		ObjectDefinition childObjectDefinition = _addObjectDefinition(false);

		_childObjectDefinitionName = childObjectDefinition.getShortName();

		ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_parentObjectDefinition.getObjectDefinitionId(),
			childObjectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			_RELATIONSHIP_NAME, false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_parentObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_parentObjectDefinition.getObjectDefinitionId());

		childObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				childObjectDefinition.getObjectDefinitionId());

		_parentObjectEntry = ObjectEntryLocalServiceUtil.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			_parentObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				_LIST_FIELD_NAME, _LIST_FIELD_VALUE_KEY
			).put(
				_OBJECT_FIELD_NAME, "peter@liferay.com"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_childObjectEntry = ObjectEntryLocalServiceUtil.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			childObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				StringBundler.concat(
					"r_", _RELATIONSHIP_NAME, "_",
					_parentObjectDefinition.getPKObjectFieldName()),
				_parentObjectEntry.getObjectEntryId()
			).put(
				_OBJECT_FIELD_NAME, "igor@liferay.com"
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@After
	public void tearDown() throws PortalException {
		ObjectEntryLocalServiceUtil.deleteObjectEntry(_childObjectEntry);
		ObjectEntryLocalServiceUtil.deleteObjectEntry(_parentObjectEntry);
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		String value = RandomTestUtil.randomString();

		JSONAssert.assertEquals(
			JSONUtil.put(
				_LIST_FIELD_NAME, JSONUtil.put("key", _LIST_FIELD_VALUE_KEY)
			).put(
				_OBJECT_FIELD_NAME, value
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"create" + _parentObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_parentObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"", value,
										"\", ", _LIST_FIELD_NAME, ": {key: \"",
										_LIST_FIELD_VALUE_KEY, "\"}}")
								).build(),
								new GraphQLField(_LIST_FIELD_NAME + " {key}"),
								new GraphQLField(_OBJECT_FIELD_NAME))))),
				"JSONObject/data", "JSONObject/c",
				"JSONObject/create" + _parentObjectDefinitionName),
			JSONCompareMode.STRICT);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				"Bad Request",
				JSONUtil.getValueAsString(
					_invoke(
						new GraphQLField(
							"mutation",
							new GraphQLField(
								"c",
								new GraphQLField(
									"create" + _parentObjectDefinitionName,
									HashMapBuilder.<String, Object>put(
										_parentObjectDefinitionName,
										StringBundler.concat(
											"{", _OBJECT_FIELD_NAME, ": \"",
											RandomTestUtil.randomString(), "\"",
											", status: draft }")
									).build(),
									new GraphQLField(_OBJECT_FIELD_NAME))))),
					"JSONArray/errors", "Object/0", "JSONObject/extensions",
					"Object/code"));
		}

		JSONAssert.assertEquals(
			JSONUtil.put(
				"extensions",
				JSONUtil.put(
					"classification", "DataFetchingException"
				).put(
					"code", "Bad Request"
				).put(
					"exception", JSONUtil.put("errno", 400)
				)
			).put(
				"message",
				"Exception while fetching data (/c/create" +
					_parentObjectDefinitionName +
						") : Draft status is not allowed"
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"create" + _parentObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_parentObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"",
										RandomTestUtil.randomString(),
										"\", statusCode: ",
										WorkflowConstants.STATUS_DRAFT, "}")
								).build(),
								new GraphQLField(_OBJECT_FIELD_NAME))))),
				"JSONArray/errors", "Object/0"),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				_OBJECT_FIELD_NAME, value
			).put(
				"status", "draft"
			).put(
				"statusCode", WorkflowConstants.STATUS_DRAFT
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"create" + _draftAllowedObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_draftAllowedObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"", value,
										"\", statusCode:",
										WorkflowConstants.STATUS_DRAFT, "}")
								).build(),
								new GraphQLField(_OBJECT_FIELD_NAME),
								new GraphQLField("status"),
								new GraphQLField("statusCode"))))),
				"JSONObject/data", "JSONObject/c",
				"JSONObject/create" + _draftAllowedObjectDefinitionName),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testDeleteObjectEntry() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"mutation",
			new GraphQLField(
				"c",
				new GraphQLField(
					"delete" + _parentObjectDefinitionName,
					HashMapBuilder.<String, Object>put(
						_getPKObjectFieldName(_parentObjectDefinition),
						_parentObjectEntry.getObjectEntryId()
					).build())));

		JSONObject jsonObject = _invoke(graphQLField);

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				jsonObject, "JSONObject/data", "JSONObject/c",
				"Object/delete" + _parentObjectDefinitionName));

		jsonObject = _invoke(graphQLField);

		Assert.assertFalse(
			JSONUtil.getValueAsBoolean(
				jsonObject, "JSONObject/data", "JSONObject/c",
				"Object/delete" + _parentObjectDefinitionName));
	}

	@Test
	public void testGetListObjectEntry() throws Exception {
		String key = TextFormatter.formatPlural(
			StringUtil.lowerCaseFirstLetter(_parentObjectDefinitionName));

		Assert.assertEquals(
			"peter@liferay.com",
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"query",
						new GraphQLField(
							"c",
							new GraphQLField(
								key,
								HashMapBuilder.<String, Object>put(
									"filter",
									"\"" + _OBJECT_FIELD_NAME +
										" eq 'peter@liferay.com'\""
								).build(),
								new GraphQLField(
									"items",
									new GraphQLField(_OBJECT_FIELD_NAME)))))),
				"JSONObject/data", "JSONObject/c", "JSONObject/" + key,
				"Object/items", "Object/0", "Object/" + _OBJECT_FIELD_NAME));
		Assert.assertEquals(
			"peter@liferay.com",
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"query",
						new GraphQLField(
							"c",
							new GraphQLField(
								key,
								HashMapBuilder.<String, Object>put(
									"filter",
									"\"contains(" + _OBJECT_FIELD_NAME +
										",'peter@liferay.com')\""
								).build(),
								new GraphQLField(
									"items",
									new GraphQLField(_OBJECT_FIELD_NAME)))))),
				"JSONObject/data", "JSONObject/c", "JSONObject/" + key,
				"Object/items", "Object/0", "Object/" + _OBJECT_FIELD_NAME));
	}

	@Test
	public void testGetListObjectEntryFilterByObjectFieldUsingNotEquals()
		throws Exception {

		String key = TextFormatter.formatPlural(
			StringUtil.lowerCaseFirstLetter(_parentObjectDefinitionName));

		Assert.assertEquals(
			0,
			JSONUtil.getValueAsInt(
				_invoke(
					new GraphQLField(
						"query",
						new GraphQLField(
							"c",
							new GraphQLField(
								key,
								HashMapBuilder.<String, Object>put(
									"filter",
									"\"" + _OBJECT_FIELD_NAME +
										" ne 'peter@liferay.com'\""
								).build(),
								new GraphQLField("totalCount"))))),
				"JSONObject/data", "JSONObject/c", "JSONObject/" + key,
				"Object/totalCount"));
	}

	@Test
	public void testGetObjectEntry() throws Exception {
		String key = StringUtil.lowerCaseFirstLetter(
			_parentObjectDefinitionName);

		String primaryKeyName = _getPKObjectFieldName(_parentObjectDefinition);

		Assert.assertEquals(
			"peter@liferay.com",
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"query",
						new GraphQLField(
							"c",
							new GraphQLField(
								key,
								HashMapBuilder.<String, Object>put(
									primaryKeyName,
									_parentObjectEntry.getObjectEntryId()
								).build(),
								new GraphQLField(_OBJECT_FIELD_NAME))))),
				"JSONObject/data", "JSONObject/c", "JSONObject/" + key,
				"Object/" + _OBJECT_FIELD_NAME));

		JSONObject jsonObject = _invoke(
			new GraphQLField(
				"query",
				new GraphQLField(
					"c",
					new GraphQLField(
						key,
						HashMapBuilder.<String, Object>put(
							primaryKeyName,
							_parentObjectEntry.getObjectEntryId()
						).build(),
						new GraphQLField(_OBJECT_FIELD_NAME),
						new GraphQLField("dateCreated"),
						new GraphQLField("dateModified"),
						new GraphQLField("status")))));

		Assert.assertNotNull(
			JSONUtil.getValueAsString(
				jsonObject, "JSONObject/data", "JSONObject/c",
				"JSONObject/" + key, "Object/dateCreated"));
		Assert.assertNotNull(
			JSONUtil.getValueAsString(
				jsonObject, "JSONObject/data", "JSONObject/c",
				"JSONObject/" + key, "Object/dateModified"));
		Assert.assertNotNull(
			JSONUtil.getValueAsString(
				jsonObject, "JSONObject/data", "JSONObject/c",
				"JSONObject/" + key, "Object/status"));

		ObjectField objectField =
			_objectFieldResource.postObjectDefinitionObjectField(
				_parentObjectDefinition.getObjectDefinitionId(),
				new ObjectField() {
					{
						businessType = BusinessType.TEXT;
						DBType = ObjectField.DBType.STRING;
						label = Collections.singletonMap(
							LocaleUtil.US.toString(),
							RandomTestUtil.randomString());
						name = StringUtil.randomId();
						required = RandomTestUtil.randomBoolean();
					}
				});

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			_parentObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				_LIST_FIELD_NAME, _LIST_FIELD_VALUE_KEY
			).put(
				objectField.getName(), "matthew@liferay.com"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			"matthew@liferay.com",
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"query",
						new GraphQLField(
							"c",
							new GraphQLField(
								key,
								HashMapBuilder.<String, Object>put(
									primaryKeyName,
									objectEntry.getObjectEntryId()
								).build(),
								new GraphQLField(objectField.getName()))))),
				"JSONObject/data", "JSONObject/c", "JSONObject/" + key,
				"Object/" + objectField.getName()));
	}

	@Test
	public void testGetObjectEntryRelatedParentObjectEntry() throws Exception {
		String key = TextFormatter.formatPlural(
			StringUtil.lowerCaseFirstLetter(_childObjectDefinitionName));

		Assert.assertEquals(
			"peter@liferay.com",
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"query",
						new GraphQLField(
							"c",
							new GraphQLField(
								key,
								new GraphQLField(
									"items",
									new GraphQLField(_RELATIONSHIP_NAME)))))),
				"JSONObject/data", "JSONObject/c", "JSONObject/" + key,
				"Object/items", "Object/0", "Object/" + _RELATIONSHIP_NAME,
				"Object/" + _OBJECT_FIELD_NAME));
	}

	@Test
	public void testGetObjectEntryWithLocalizedObjectField() throws Exception {
		ObjectDefinition objectDefinition = null;

		try {
			objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
				true, ObjectDefinitionTestUtil.getRandomName(),
				Arrays.asList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						_OBJECT_FIELD_NAME_TEXT
					).build(),
					new LongTextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						_OBJECT_FIELD_NAME_LONG_TEXT
					).build(),
					new RichTextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						_OBJECT_FIELD_NAME_RICH_TEXT
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId());

			ObjectEntryTestUtil.addObjectEntry(
				objectDefinition,
				HashMapBuilder.<String, Serializable>put(
					_OBJECT_FIELD_NAME_LONG_TEXT, "name2_text_english"
				).put(
					_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n",
					HashMapBuilder.<String, Serializable>put(
						"en_US", "longTextEng"
					).put(
						"es_ES", "longTextEsp"
					).build()
				).put(
					_OBJECT_FIELD_NAME_RICH_TEXT, "<p>c</p>\\n"
				).put(
					_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n",
					HashMapBuilder.<String, Serializable>put(
						"en_US", "<p>richTextEng</p>"
					).put(
						"es_ES", "<p>richTextEsp</p>"
					).build()
				).put(
					_OBJECT_FIELD_NAME_TEXT, "name1_text_english"
				).put(
					_OBJECT_FIELD_NAME_TEXT + "_i18n",
					HashMapBuilder.<String, Serializable>put(
						"en_US", "textEng"
					).put(
						"es_ES", "textEsp"
					).build()
				).build());

			String pluralName = TextFormatter.formatPlural(
				StringUtil.lowerCaseFirstLetter(
					objectDefinition.getShortName()));

			GraphQLField graphQLField = new GraphQLField(
				"query",
				new GraphQLField(
					"c",
					new GraphQLField(
						pluralName,
						new GraphQLField(
							"items",
							new GraphQLField(_OBJECT_FIELD_NAME_LONG_TEXT),
							new GraphQLField(
								_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n"),
							new GraphQLField(_OBJECT_FIELD_NAME_RICH_TEXT),
							new GraphQLField(
								_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n"),
							new GraphQLField(_OBJECT_FIELD_NAME_TEXT),
							new GraphQLField(
								_OBJECT_FIELD_NAME_TEXT + "_i18n")))));

			// "Accept-Language" header

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_LONG_TEXT, "longTextEsp"
					).put(
						_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "longTextEng"
						).put(
							"es_ES", "longTextEsp"
						)
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT, "<p>richTextEsp</p>"
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "<p>richTextEng</p>"
						).put(
							"es_ES", "<p>richTextEsp</p>"
						)
					).put(
						_OBJECT_FIELD_NAME_TEXT, "textEsp"
					).put(
						_OBJECT_FIELD_NAME_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "textEng"
						).put(
							"es_ES", "textEsp"
						)
					)
				).toString(),
				JSONUtil.getValueAsString(
					_invoke("es-ES", graphQLField), "JSONObject/data",
					"JSONObject/c", "JSONObject/" + pluralName,
					"JSONArray/items"));

			// Empty "Accept-Language" header

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_LONG_TEXT, "longTextEng"
					).put(
						_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "longTextEng"
						).put(
							"es_ES", "longTextEsp"
						)
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT, "<p>richTextEng</p>"
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "<p>richTextEng</p>"
						).put(
							"es_ES", "<p>richTextEsp</p>"
						)
					).put(
						_OBJECT_FIELD_NAME_TEXT, "textEng"
					).put(
						_OBJECT_FIELD_NAME_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "textEng"
						).put(
							"es_ES", "textEsp"
						)
					)
				).toString(),
				JSONUtil.getValueAsString(
					_invoke("", graphQLField), "JSONObject/data",
					"JSONObject/c", "JSONObject/" + pluralName,
					"JSONArray/items"));

			// Nonexistent "Accept-Language" header

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_LONG_TEXT, "longTextEng"
					).put(
						_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "longTextEng"
						).put(
							"es_ES", "longTextEsp"
						)
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT, "<p>richTextEng</p>"
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "<p>richTextEng</p>"
						).put(
							"es_ES", "<p>richTextEsp</p>"
						)
					).put(
						_OBJECT_FIELD_NAME_TEXT, "textEng"
					).put(
						_OBJECT_FIELD_NAME_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "textEng"
						).put(
							"es_ES", "textEsp"
						)
					)
				).toString(),
				JSONUtil.getValueAsString(
					_invoke("de-DE", graphQLField), "JSONObject/data",
					"JSONObject/c", "JSONObject/" + pluralName,
					"JSONArray/items"));

			// Unknown "Accept-Language" header

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_LONG_TEXT, "longTextEng"
					).put(
						_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "longTextEng"
						).put(
							"es_ES", "longTextEsp"
						)
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT, "<p>richTextEng</p>"
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "<p>richTextEng</p>"
						).put(
							"es_ES", "<p>richTextEsp</p>"
						)
					).put(
						_OBJECT_FIELD_NAME_TEXT, "textEng"
					).put(
						_OBJECT_FIELD_NAME_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "textEng"
						).put(
							"es_ES", "textEsp"
						)
					)
				).toString(),
				JSONUtil.getValueAsString(
					_invoke("unknown", graphQLField), "JSONObject/data",
					"JSONObject/c", "JSONObject/" + pluralName,
					"JSONArray/items"));

			// Without "Accept-Language" header

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						_OBJECT_FIELD_NAME_LONG_TEXT, "longTextEng"
					).put(
						_OBJECT_FIELD_NAME_LONG_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "longTextEng"
						).put(
							"es_ES", "longTextEsp"
						)
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT, "<p>richTextEng</p>"
					).put(
						_OBJECT_FIELD_NAME_RICH_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "<p>richTextEng</p>"
						).put(
							"es_ES", "<p>richTextEsp</p>"
						)
					).put(
						_OBJECT_FIELD_NAME_TEXT, "textEng"
					).put(
						_OBJECT_FIELD_NAME_TEXT + "_i18n",
						JSONUtil.put(
							"en_US", "textEng"
						).put(
							"es_ES", "textEsp"
						)
					)
				).toString(),
				JSONUtil.getValueAsString(
					_invoke(graphQLField), "JSONObject/data", "JSONObject/c",
					"JSONObject/" + pluralName, "JSONArray/items"));
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition.getObjectDefinitionId());
			}
		}
	}

	@Test
	public void testUpdateObjectEntry() throws Exception {
		String value = RandomTestUtil.randomString();

		String primaryKeyName = _getPKObjectFieldName(_parentObjectDefinition);

		JSONObject jsonObject = _invoke(
			new GraphQLField(
				"mutation",
				new GraphQLField(
					"c",
					new GraphQLField(
						"create" + _parentObjectDefinitionName,
						HashMapBuilder.<String, Object>put(
							_parentObjectDefinitionName,
							StringBundler.concat(
								"{", _OBJECT_FIELD_NAME, ": \"", value, "\", ",
								_LIST_FIELD_NAME, ": {key: \"",
								_LIST_FIELD_VALUE_KEY, "\"}}")
						).build(),
						new GraphQLField(_LIST_FIELD_NAME + " {key}"),
						new GraphQLField(_OBJECT_FIELD_NAME),
						new GraphQLField(primaryKeyName)))));

		JSONAssert.assertEquals(
			JSONUtil.put(
				_LIST_FIELD_NAME, JSONUtil.put("key", _LIST_FIELD_VALUE_KEY)
			).put(
				_OBJECT_FIELD_NAME, value
			).toString(),
			JSONUtil.getValueAsString(
				jsonObject, "JSONObject/data", "JSONObject/c",
				"JSONObject/create" + _parentObjectDefinitionName),
			JSONCompareMode.STRICT_ORDER);

		Long objectEntryId = JSONUtil.getValueAsLong(
			jsonObject, "JSONObject/data", "JSONObject/c",
			"JSONObject/create" + _parentObjectDefinitionName,
			"Object/" + primaryKeyName);

		value = RandomTestUtil.randomString();

		JSONAssert.assertEquals(
			JSONUtil.put(
				_LIST_FIELD_NAME, JSONUtil.put("key", _LIST_FIELD_VALUE_KEY)
			).put(
				_OBJECT_FIELD_NAME, value
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"update" + _parentObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_parentObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"", value,
										"\", ", _LIST_FIELD_NAME, ": {key: \"",
										_LIST_FIELD_VALUE_KEY, "\"}}")
								).put(
									primaryKeyName,
									String.valueOf(objectEntryId)
								).build(),
								new GraphQLField(_LIST_FIELD_NAME + " {key}"),
								new GraphQLField(_OBJECT_FIELD_NAME))))),
				"JSONObject/data", "JSONObject/c",
				"JSONObject/update" + _parentObjectDefinitionName),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"extensions",
				JSONUtil.put(
					"classification", "DataFetchingException"
				).put(
					"code", "Bad Request"
				).put(
					"exception", JSONUtil.put("errno", 400)
				)
			).put(
				"message",
				"Exception while fetching data (/c/update" +
					_parentObjectDefinitionName +
						") : Draft status is not allowed"
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"update" + _parentObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_parentObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"", value,
										"\", statusCode:",
										WorkflowConstants.STATUS_DRAFT, "}")
								).put(
									primaryKeyName,
									String.valueOf(objectEntryId)
								).build(),
								new GraphQLField(_OBJECT_FIELD_NAME))))),
				"JSONArray/errors", "Object/0"),
			JSONCompareMode.LENIENT);

		primaryKeyName = _getPKObjectFieldName(_draftAllowedObjectDefinition);

		jsonObject = JSONUtil.getValueAsJSONObject(
			_invoke(
				new GraphQLField(
					"mutation",
					new GraphQLField(
						"c",
						new GraphQLField(
							"create" + _draftAllowedObjectDefinitionName,
							HashMapBuilder.<String, Object>put(
								_draftAllowedObjectDefinitionName,
								StringBundler.concat(
									"{", _OBJECT_FIELD_NAME, ": \"", value,
									"\", statusCode:",
									WorkflowConstants.STATUS_DRAFT, "}")
							).build(),
							new GraphQLField(primaryKeyName),
							new GraphQLField("status"),
							new GraphQLField("statusCode"))))),
			"JSONObject/data", "JSONObject/c",
			"JSONObject/create" + _draftAllowedObjectDefinitionName);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "draft"
			).put(
				"statusCode", WorkflowConstants.STATUS_DRAFT
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		objectEntryId = jsonObject.getLong(primaryKeyName);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "approved"
			).put(
				"statusCode", WorkflowConstants.STATUS_APPROVED
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"update" + _draftAllowedObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_draftAllowedObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"", value,
										"\", statusCode: ",
										WorkflowConstants.STATUS_APPROVED, "}")
								).put(
									primaryKeyName,
									String.valueOf(objectEntryId)
								).build(),
								new GraphQLField(primaryKeyName),
								new GraphQLField("status"),
								new GraphQLField("statusCode"))))),
				"JSONObject/data", "JSONObject/c",
				"JSONObject/update" + _draftAllowedObjectDefinitionName),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"extensions",
				JSONUtil.put(
					"classification", "DataFetchingException"
				).put(
					"code", "Bad Request"
				).put(
					"exception", JSONUtil.put("errno", 400)
				)
			).put(
				"message",
				"Exception while fetching data (/c/update" +
					_draftAllowedObjectDefinitionName +
						") : Draft status is not allowed"
			).toString(),
			JSONUtil.getValueAsString(
				_invoke(
					new GraphQLField(
						"mutation",
						new GraphQLField(
							"c",
							new GraphQLField(
								"update" + _draftAllowedObjectDefinitionName,
								HashMapBuilder.<String, Object>put(
									_draftAllowedObjectDefinitionName,
									StringBundler.concat(
										"{", _OBJECT_FIELD_NAME, ": \"", value,
										"\", statusCode: ",
										WorkflowConstants.STATUS_DRAFT, "}")
								).put(
									primaryKeyName,
									String.valueOf(objectEntryId)
								).build(),
								new GraphQLField(primaryKeyName))))),
				"JSONArray/errors", "Object/0"),
			JSONCompareMode.LENIENT);
	}

	private void _addListTypeEntry(
			ListTypeDefinition listTypeDefinition, String key)
		throws Exception {

		ListTypeEntryLocalServiceUtil.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			listTypeDefinition.getListTypeDefinitionId(), key,
			LocalizedMapUtil.getLocalizedMap(key));
	}

	private ObjectDefinition _addObjectDefinition(
			boolean enableObjectEntryDraft)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				enableObjectEntryDraft,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList());

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).indexed(
				true
			).indexedAsKeyword(
				true
			).name(
				_OBJECT_FIELD_NAME
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		return objectDefinition;
	}

	private String _getPKObjectFieldName(ObjectDefinition objectDefinition) {
		return StringUtil.removeFirst(
			objectDefinition.getPKObjectFieldName(), "c_");
	}

	private JSONObject _invoke(GraphQLField queryGraphQLField)
		throws Exception {

		return _invoke(null, queryGraphQLField);
	}

	private JSONObject _invoke(String acceptLanguage, GraphQLField graphQLField)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"query", graphQLField.toString()
			).toString(),
			"graphql",
			HashMapBuilder.put(
				"Accept-Language", () -> acceptLanguage
			).build(),
			Http.Method.POST);
	}

	private static final String _LIST_FIELD_NAME = StringUtil.randomId();

	private static final String _LIST_FIELD_VALUE_KEY = StringUtil.randomId();

	private static final String _OBJECT_FIELD_NAME = StringUtil.randomId();

	private static final String _OBJECT_FIELD_NAME_LONG_TEXT =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_RICH_TEXT =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_TEXT =
		"x" + RandomTestUtil.randomString();

	private static final String _RELATIONSHIP_NAME = "parent";

	@Inject
	private static ObjectFieldResource _objectFieldResource;

	private String _childObjectDefinitionName;
	private ObjectEntry _childObjectEntry;

	@DeleteAfterTestRun
	private ObjectDefinition _draftAllowedObjectDefinition;

	private String _draftAllowedObjectDefinitionName;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _parentObjectDefinition;

	private String _parentObjectDefinitionName;
	private ObjectEntry _parentObjectEntry;

	private static class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

}