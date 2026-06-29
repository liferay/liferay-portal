/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.configuration.ObjectEntryVersionConfiguration;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.exception.ObjectEntryExpirationDateException;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
@Sync
public class CheckObjectEntrySchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			List.of(
				new TextObjectFieldBuilder(
				).userId(
					TestPropsValues.getUserId()
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					_OBJECT_FIELD_NAME
				).build()),
			false);

		_configurationProvider.saveCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"maximumRetentionPeriod", 1
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationProvider.deleteCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId());
	}

	@Test
	public void testCheckObjectEntryDisplayDate() throws Exception {
		Date date = new Date();

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"displayDate",
				new Date(date.getTime() + TimeUnit.MILLISECOND.toMillis(1000))
			).build());

		Assert.assertTrue(objectEntry1.isScheduled());

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"displayDate",
				new Date(date.getTime() + TimeUnit.DAY.toMillis(1))
			).build());

		Assert.assertTrue(objectEntry2.isScheduled());

		Thread.sleep(1000);

		_jobExecutorUnsafeRunnable.run();

		objectEntry1 = _objectEntryLocalService.getObjectEntry(
			objectEntry1.getObjectEntryId());
		objectEntry2 = _objectEntryLocalService.getObjectEntry(
			objectEntry2.getObjectEntryId());

		Assert.assertTrue(objectEntry1.isApproved());
		Assert.assertTrue(objectEntry2.isScheduled());

		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"displayDate",
				new Date(date.getTime() + TimeUnit.DAY.toMillis(1))
			).build());

		Assert.assertTrue(objectEntry3.isScheduled());

		_updateDisplayDate(
			new Date(date.getTime() - TimeUnit.DAY.toMillis(1)), objectEntry3);

		_jobExecutorUnsafeRunnable.run();

		objectEntry3 = _objectEntryLocalService.getObjectEntry(
			objectEntry3.getObjectEntryId());

		Assert.assertTrue(objectEntry3.isApproved());
	}

	@Test
	public void testCheckObjectEntryExpirationDate() throws Exception {
		Date date = new Date();

		AssertUtils.assertFailure(
			ObjectEntryExpirationDateException.class,
			"Expiration date must be a future date",
			() -> ObjectEntryTestUtil.addObjectEntry(
				0, _objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
				).put(
					"expirationDate",
					new Date(date.getTime() - TimeUnit.MINUTE.toMillis(1))
				).build()));

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build());

		_updateExpirationDate(date, objectEntry1);

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build());

		_updateExpirationDate(
			new Date(date.getTime() + TimeUnit.MINUTE.toMillis(5)),
			objectEntry2);

		_jobExecutorUnsafeRunnable.run();

		objectEntry1 = _objectEntryLocalService.getObjectEntry(
			objectEntry1.getObjectEntryId());
		objectEntry2 = _objectEntryLocalService.getObjectEntry(
			objectEntry2.getObjectEntryId());

		Assert.assertTrue(objectEntry1.isExpired());
		Assert.assertTrue(objectEntry2.isApproved());

		_updateExpirationDate(new Date(), objectEntry2);

		_jobExecutorUnsafeRunnable.run();

		objectEntry2 = _objectEntryLocalService.getObjectEntry(
			objectEntry2.getObjectEntryId());

		Assert.assertTrue(objectEntry2.isExpired());

		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build());

		objectEntry3 = _objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry3,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(objectEntry3.isInTrash());

		_updateExpirationDate(new Date(), objectEntry3);

		_jobExecutorUnsafeRunnable.run();

		objectEntry3 = _objectEntryLocalService.getObjectEntry(
			objectEntry3.getObjectEntryId());

		Assert.assertFalse(objectEntry3.isExpired());
		Assert.assertTrue(objectEntry3.isInTrash());

		ObjectEntry objectEntry4 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"expirationDate",
				new Date(date.getTime() + TimeUnit.DAY.toMillis(1))
			).build());

		Assert.assertTrue(objectEntry4.isApproved());

		_updateExpirationDate(
			new Date(date.getTime() - TimeUnit.DAY.toMillis(1)), objectEntry4);

		_jobExecutorUnsafeRunnable.run();

		objectEntry4 = _objectEntryLocalService.getObjectEntry(
			objectEntry4.getObjectEntryId());

		Assert.assertTrue(objectEntry4.isExpired());
	}

	@Test
	public void testCheckObjectEntryReviewDate() throws Exception {
		Date date = new Date();

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"reviewDate", date
			).build());

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"reviewDate",
				new Date(date.getTime() + TimeUnit.MINUTE.toMillis(1))
			).build());

		_jobExecutorUnsafeRunnable.run();

		JSONObject payloadJSONObject = null;
		List<UserNotificationEvent> testUserNotificationEvents =
			new ArrayList<>();

		for (UserNotificationEvent userNotificationEvent :
				_userNotificationEventLocalService.getUserNotificationEvents(
					objectEntry1.getUserId())) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				userNotificationEvent.getPayload());

			long classPK = jsonObject.getLong("classPK");

			if ((classPK == objectEntry1.getObjectEntryId()) ||
				(classPK == objectEntry2.getObjectEntryId())) {

				payloadJSONObject = jsonObject;
				testUserNotificationEvents.add(userNotificationEvent);
			}
		}

		Assert.assertEquals(
			testUserNotificationEvents.toString(), 1,
			testUserNotificationEvents.size());

		Assert.assertEquals(
			objectEntry1.getObjectEntryId(),
			payloadJSONObject.getLong("classPK"));
		Assert.assertEquals(
			objectEntry1.getTitleValue(),
			payloadJSONObject.getString("notificationMessageArg"));
		Assert.assertEquals(
			"x-has-reached-its-review-date",
			payloadJSONObject.getString("notificationMessageKey"));
	}

	@Test
	public void testCheckObjectEntryVersionsWithMaximumRetentionPeriod()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, true, false, true,
				false, true, true, false, false, true, "_",
				RandomTestUtil.randomLocaleStringMap(),
				"A" + StringUtil.randomString(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"textObjectFieldName"
					).build()),
				Collections.emptyList(), new ServiceContext());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		ObjectEntryVersionConfiguration objectEntryVersionConfiguration =
			_configurationProvider.getCompanyConfiguration(
				ObjectEntryVersionConfiguration.class,
				CompanyThreadLocal.getCompanyId());

		Assert.assertEquals(
			1, objectEntryVersionConfiguration.maximumRetentionPeriod());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		_updateLatestObjectEntryVersion(_getPastDate(3), objectEntry);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_updateLatestObjectEntryVersion(_getPastDate(2), objectEntry);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_updateLatestObjectEntryVersion(_getPastDate(2), objectEntry);

		Assert.assertEquals(
			3,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();

		Assert.assertEquals(
			1,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private java.sql.Date _getPastDate(int months) {
		return java.sql.Date.valueOf(
			LocalDate.now(
			).minusMonths(
				months
			));
	}

	private void _updateDisplayDate(Date displayDate, ObjectEntry objectEntry) {
		objectEntry.setDisplayDate(displayDate);

		_objectEntryLocalService.updateObjectEntry(objectEntry);
	}

	private void _updateExpirationDate(
		Date expirationDate, ObjectEntry objectEntry) {

		objectEntry.setExpirationDate(expirationDate);

		_objectEntryLocalService.updateObjectEntry(objectEntry);
	}

	private void _updateLatestObjectEntryVersion(
			java.sql.Date createDate, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.getObjectEntryVersion(
				objectEntry.getObjectEntryId(), objectEntry.getVersion());

		objectEntryVersion.setCreateDate(createDate);

		_objectEntryVersionLocalService.updateObjectEntryVersion(
			objectEntryVersion);
	}

	private static final String _OBJECT_FIELD_NAME =
		"a" + RandomTestUtil.randomString();

	@Inject
	private static ConfigurationProvider _configurationProvider;

	private static UnsafeRunnable<Exception> _jobExecutorUnsafeRunnable;
	private static ObjectDefinition _objectDefinition;

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.scheduler.CheckObjectEntrySchedulerJobConfiguration"
	)
	private static SchedulerJobConfiguration _schedulerJobConfiguration;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}