/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.internal.rest.manager.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.exception.ObjectEntryManagerHttpException;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.test.util.BaseObjectEntryManagerImplTestCase;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.storage.salesforce.configuration.SalesforceConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.text.DateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@FeatureFlag("LPS-135430")
@RunWith(Arquillian.class)
public class SalesforceObjectEntryManagerImplTest
	extends BaseObjectEntryManagerImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		adminUser = TestPropsValues.getUser();

		companyId = TestPropsValues.getCompanyId();

		_configurationProvider.saveCompanyConfiguration(
			SalesforceConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"consumerKey",
				TestPropsUtil.get("object.storage.salesforce.consumer.key")
			).put(
				"consumerSecret",
				TestPropsUtil.get("object.storage.salesforce.consumer.secret")
			).put(
				"loginURL",
				TestPropsUtil.get("object.storage.salesforce.login.url")
			).put(
				"password",
				TestPropsUtil.get("object.storage.salesforce.password")
			).put(
				"username",
				TestPropsUtil.get("object.storage.salesforce.username")
			).build());

		_simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			SalesforceConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"consumerKey", ""
			).put(
				"consumerSecret", ""
			).put(
				"loginURL", ""
			).put(
				"password", ""
			).put(
				"username", ""
			).build());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				"Status", TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				false,
				Arrays.asList(
					ListTypeEntryUtil.createListTypeEntry(
						"Completed", "completed",
						Collections.singletonMap(LocaleUtil.US, "Completed")),
					ListTypeEntryUtil.createListTypeEntry(
						"Not Completed", "notCompleted",
						Collections.singletonMap(
							LocaleUtil.US, "Not Completed")),
					ListTypeEntryUtil.createListTypeEntry(
						"Queued", "queued",
						Collections.singletonMap(LocaleUtil.US, "Queued")),
					ListTypeEntryUtil.createListTypeEntry(
						"Started", "started",
						Collections.singletonMap(LocaleUtil.US, "Started"))),
				new ServiceContext());

		_objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				null, adminUser.getUserId(), 0, null, null, false, false, true,
				false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Ticket"), "Ticket", null,
				null, LocalizedMapUtil.getLocalizedMap("Tickets"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(),
				ListUtil.fromArray(
					new RichTextObjectFieldBuilder(
					).externalReferenceCode(
						"Description__c"
					).userId(
						adminUser.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Description")
					).name(
						"description"
					).build(),
					new DateObjectFieldBuilder(
					).externalReferenceCode(
						"Due_date__c"
					).userId(
						adminUser.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Due Date")
					).name(
						"dueDate"
					).build(),
					new BooleanObjectFieldBuilder(
					).externalReferenceCode(
						"Flagged__c"
					).userId(
						adminUser.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Flagged")
					).name(
						"flagged"
					).build(),
					new LongIntegerObjectFieldBuilder(
					).externalReferenceCode(
						"Object_Definition_id__c"
					).userId(
						adminUser.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Run ID")
					).name(
						"runId"
					).build(),
					new DateTimeObjectFieldBuilder(
					).externalReferenceCode(
						"Start_date__c"
					).userId(
						adminUser.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Start Date")
					).name(
						"startDate"
					).objectFieldSettings(
						Collections.singletonList(
							_createObjectFieldSetting(
								ObjectFieldSettingConstants.NAME_TIME_STORAGE,
								ObjectFieldSettingConstants.
									VALUE_USE_INPUT_AS_ENTERED))
					).build(),
					new PicklistObjectFieldBuilder(
					).externalReferenceCode(
						"Status__c"
					).userId(
						adminUser.getUserId()
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("Status")
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						"customStatus"
					).build()),
				Collections.emptyList(), new ServiceContext());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).externalReferenceCode(
				"Title__c"
			).userId(
				adminUser.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Title")
			).name(
				"title"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		_objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		_objectDefinition.setExternalReferenceCode("Ticket__c");

		_objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			_objectDefinition);

		_objectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				adminUser.getUserId(),
				_objectDefinition.getObjectDefinitionId());

		try {
			getObjectEntries(Collections.<String, String>emptyMap(), null);
		}
		catch (ObjectEntryManagerHttpException
					objectEntryManagerHttpException) {

			_uanbleToAuthenticateWithSalesforce = true;

			Assume.assumeNoException(
				"Unable to authenticate with Salesforce",
				objectEntryManagerHttpException);
		}
	}

	@After
	public void tearDown() throws Exception {
		try {
			_deleteObjectEntries();
		}
		finally {
			if (_objectDefinition != null) {
				objectDefinitionLocalService.deleteObjectDefinition(
					_objectDefinition.getObjectDefinitionId());
			}

			if (listTypeDefinition != null) {
				listTypeDefinitionLocalService.deleteListTypeDefinition(
					listTypeDefinition.getListTypeDefinitionId());
			}
		}
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			null, null, false, null, RandomTestUtil.randomString());

		Assert.assertNotNull(objectEntry.getExternalReferenceCode());
	}

	@Test
	public void testAddOrUpdateObjectEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			null, null, false, null, RandomTestUtil.randomString());

		String title = RandomTestUtil.randomString();

		objectEntry.setProperties(
			HashMapBuilder.putAll(
				objectEntry.getProperties()
			).put(
				"title", title
			).build());

		objectEntry = _objectEntryManager.updateObjectEntry(
			companyId, dtoConverterContext,
			objectEntry.getExternalReferenceCode(), _objectDefinition,
			objectEntry, ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertEquals(
			title, MapUtil.getString(objectEntry.getProperties(), "title"));
	}

	@Test
	public void testGetObjectEntries() throws Exception {
		String title1 = null;
		String title2 = "a" + RandomTestUtil.randomString();
		String title3 = "b" + RandomTestUtil.randomString();
		String title4 = "c" + RandomTestUtil.randomString();
		String title5 = "d" + RandomTestUtil.randomString();

		Date date = RandomTestUtil.nextDate();

		ObjectEntry objectEntry1 = _addObjectEntry(
			"queued", date, false, null, title1);

		LocalDateTime localDateTime1 = LocalDateTime.now();

		localDateTime1 = localDateTime1.truncatedTo(ChronoUnit.MILLIS);

		ObjectEntry objectEntry2 = _addObjectEntry(
			"started", new Date(date.getTime() - Time.DAY), true,
			localDateTime1, title2);

		LocalDateTime localDateTime2 = localDateTime1.plusHours(1);

		ObjectEntry objectEntry3 = _addObjectEntry(
			"completed", new Date(date.getTime() + Time.DAY), false,
			localDateTime2, title3);

		ObjectEntry objectEntry4 = _addObjectEntry(
			"queued", date, true, null, title4);
		ObjectEntry objectEntry5 = _addObjectEntry(
			"queued", date, false, null, title5);

		// And/or with equals/not equals expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", buildEqualsExpressionFilterString("title", null)
			).build(),
			objectEntry1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					buildEqualsExpressionFilterString("customStatus", "queued"),
					" and ", buildEqualsExpressionFilterString("dueDate", date),
					" and ", buildEqualsExpressionFilterString("title", title1))
			).build(),
			objectEntry1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					_buildNotEqualsExpressionFilterString(
						"customStatus", "queued"),
					" and ",
					_buildNotEqualsExpressionFilterString("dueDate", date),
					" and ",
					_buildNotEqualsExpressionFilterString("title", title1))
			).build(),
			objectEntry2, objectEntry3);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					buildEqualsExpressionFilterString("customStatus", "queued"),
					" or ", buildEqualsExpressionFilterString("dueDate", date),
					" or ", buildEqualsExpressionFilterString("title", title1))
			).build(),
			objectEntry1, objectEntry4, objectEntry5);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					_buildNotEqualsExpressionFilterString(
						"customStatus", "queued"),
					" or ",
					_buildNotEqualsExpressionFilterString("dueDate", date),
					" or ",
					_buildNotEqualsExpressionFilterString("title", title1))
			).build(),
			objectEntry2, objectEntry3, objectEntry4, objectEntry5);

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		String dateTimeString1 = dateTimeFormatter.format(
			localDateTime1.withNano(0));
		String dateTimeString2 = dateTimeFormatter.format(
			localDateTime2.withNano(0));

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					"startDate ne ", dateTimeString1, " or startDate eq ",
					dateTimeString2)
			).build(),
			objectEntry1, objectEntry3, objectEntry4, objectEntry5);

		// Equals/not equals expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString("customStatus", "queued")
			).build(),
			objectEntry1, objectEntry4, objectEntry5);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildNotEqualsExpressionFilterString("customStatus", "queued")
			).build(),
			objectEntry2, objectEntry3);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", buildEqualsExpressionFilterString("dueDate", date)
			).build(),
			objectEntry1, objectEntry4, objectEntry5);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", _buildNotEqualsExpressionFilterString("dueDate", date)
			).build(),
			objectEntry2, objectEntry3);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", buildEqualsExpressionFilterString("flagged", true)
			).build(),
			objectEntry2, objectEntry4);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString("startDate", localDateTime1)
			).build(),
			objectEntry2);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildNotEqualsExpressionFilterString(
					"startDate", localDateTime2)
			).build(),
			objectEntry1, objectEntry2, objectEntry4, objectEntry5);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", buildEqualsExpressionFilterString("title", title1)
			).build(),
			objectEntry1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", _buildNotEqualsExpressionFilterString("title", title1)
			).build(),
			objectEntry2, objectEntry3, objectEntry4, objectEntry5);

		// Range expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildRangeExpression(
					_simpleDateFormat.parse(
						MapUtil.getString(
							objectEntry1.getProperties(), "dueDate")),
					new Date(), "dueDate", "yyyy-MM-dd")
			).build(),
			objectEntry1, objectEntry4, objectEntry5);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", "startDate ge " + dateTimeString1
			).build(),
			objectEntry2, objectEntry3);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", "startDate gt " + dateTimeString1
			).build(),
			objectEntry3);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", "startDate le " + dateTimeString2
			).build(),
			objectEntry2, objectEntry3);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", "startDate lt " + dateTimeString2
			).build(),
			objectEntry2);
	}

	@Test
	public void testGetObjectEntry() throws Exception {
		String description = "<p>" + RandomTestUtil.randomString() + "</p>";
		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(
			null, description, null, false, null, title);

		_assertObjectEntry(
			description, objectEntry.getExternalReferenceCode(), title);
	}

	@Test
	public void testPartialUpdateObjectEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			null, RandomTestUtil.randomString(), null, false, null,
			RandomTestUtil.randomString());

		String description = "<p>" + RandomTestUtil.randomString() + "</p>";
		String title = RandomTestUtil.randomString();

		_objectEntryManager.partialUpdateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry.getExternalReferenceCode(), _objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"description", description
					).put(
						"title", title
					).build();
				}
			},
			null);

		_assertObjectEntry(
			description, objectEntry.getExternalReferenceCode(), title);
	}

	@Override
	protected Page<ObjectEntry> getObjectEntries(
			Map<String, String> context, Sort[] sorts)
		throws Exception {

		if ((sorts == null) || !context.containsKey("sort")) {
			sorts = new Sort[] {SortFactoryUtil.create("title", false)};
		}

		return _objectEntryManager.getObjectEntries(
			companyId, _objectDefinition, null, null, dtoConverterContext,
			_buildRunFilterString(context.get("filter")), Pagination.of(1, 4),
			context.get("search"), sorts);
	}

	private ObjectEntry _addObjectEntry(
			String customStatus, Date dueDate, boolean flagged,
			LocalDateTime startDate, String title)
		throws Exception {

		return _addObjectEntry(
			customStatus, RandomTestUtil.randomString(), dueDate, flagged,
			startDate, title);
	}

	private ObjectEntry _addObjectEntry(
			String customStatus, String description, Date dueDate,
			boolean flagged, LocalDateTime startDate, String title)
		throws Exception {

		return _objectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"customStatus", customStatus
					).put(
						"description", description
					).put(
						"dueDate",
						(dueDate != null) ? _simpleDateFormat.format(dueDate) :
							null
					).put(
						"flagged", flagged
					).put(
						"runId", _RUN_ID
					).put(
						"startDate", startDate
					).put(
						"title", title
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private void _assertObjectEntry(
			String description, String externalReferenceCode, String title)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			_objectDefinition, ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertEquals(
			description,
			MapUtil.getString(objectEntry.getProperties(), "description"));
		Assert.assertEquals(
			HtmlParserUtil.extractText(description),
			MapUtil.getString(
				objectEntry.getProperties(), "descriptionRawText"));
		Assert.assertEquals(
			title, MapUtil.getString(objectEntry.getProperties(), "title"));
	}

	private String _buildNotEqualsExpressionFilterString(
		String fieldName, Object value) {

		return StringBundler.concat(fieldName, " ne ", getValue(value));
	}

	private String _buildRunFilterString(String filterString) {
		if (filterString == null) {
			return "runId eq " + _RUN_ID;
		}

		return StringBundler.concat(
			"(runId eq ", _RUN_ID, ") and (", filterString, ")");
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private void _deleteObjectEntries() throws Exception {
		if (_uanbleToAuthenticateWithSalesforce) {
			return;
		}

		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			companyId, _objectDefinition, null, null, dtoConverterContext,
			_buildRunFilterString(null), Pagination.of(1, 200), null, null);

		for (ObjectEntry objectEntry : page.getItems()) {
			_objectEntryManager.deleteObjectEntry(
				companyId, dtoConverterContext,
				objectEntry.getExternalReferenceCode(), _objectDefinition,
				ObjectDefinitionConstants.SCOPE_COMPANY);
		}
	}

	private static final long _RUN_ID = RandomTestUtil.randomInt();

	@Inject
	private static ConfigurationProvider _configurationProvider;

	private static DateFormat _simpleDateFormat;

	private ObjectDefinition _objectDefinition;

	@Inject(
		filter = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE
	)
	private ObjectEntryManager _objectEntryManager;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	private boolean _uanbleToAuthenticateWithSalesforce;

}