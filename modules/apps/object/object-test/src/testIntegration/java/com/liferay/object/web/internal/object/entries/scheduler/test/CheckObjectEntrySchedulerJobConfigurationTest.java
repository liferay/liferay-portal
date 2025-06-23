/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.ObjectEntryExpirationDateException;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

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
				).build()));
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
	}

	@Test
	public void testCheckObjectEntryReviewDate() throws Exception {
		Date date = new Date();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"reviewDate", date
			).build());

		ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				"reviewDate",
				new Date(date.getTime() + TimeUnit.MINUTE.toMillis(1))
			).build());

		_jobExecutorUnsafeRunnable.run();

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				objectEntry.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			userNotificationEvent.getPayload());

		Assert.assertEquals(
			StringBundler.concat(
				"The object entry ", objectEntry.getTitleValue(),
				" has reached its review date."),
			jsonObject.get("notificationMessage"));
	}

	private void _updateExpirationDate(
		Date expirationDate, ObjectEntry objectEntry) {

		objectEntry.setExpirationDate(expirationDate);

		_objectEntryLocalService.updateObjectEntry(objectEntry);
	}

	private static final String _OBJECT_FIELD_NAME =
		"a" + RandomTestUtil.randomString();

	private static UnsafeRunnable<Exception> _jobExecutorUnsafeRunnable;
	private static ObjectDefinition _objectDefinition;

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.scheduler.CheckObjectEntrySchedulerJobConfiguration"
	)
	private static SchedulerJobConfiguration _schedulerJobConfiguration;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}